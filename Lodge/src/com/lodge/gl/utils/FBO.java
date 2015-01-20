package com.lodge.gl.utils;

import java.nio.Buffer;
import com.lodge.err.GLError;
import android.opengl.GLES30;

public class FBO {

	public enum Content{
		UNDEFINED,
		COLOR,
		DEPTH, 
		COLOR_DEPTH
	}

	private final String DepthLabel = "u_DepthTexture";
	private final String ColorLabel = "u_ColorTexture";
	
	private final int DEPTH_INTERNAL_FORMAT = GLES30.GL_DEPTH_COMPONENT;
	private final int DEPTH_FORMAT 			= GLES30.GL_DEPTH_COMPONENT;
	private final int DEPTH_TYPE 			= GLES30.GL_UNSIGNED_INT; 

	
	final int UNUSED = -1;
	final int NUM_FBO = 1; 

	private int[] mFBO;
	private Texture mColorTexture;
	private Texture mDepthTexture;
	private Texture mRenderBuffer;
	private int mRenderTargets;

	private int mWidth;
	private int mHeight;

	private int mInternalFormat;
	private int mFormat;
	private int mType;

	private int mAttachment;

	Content mContent;


	public FBO(int width, int height){
		setup(width, height,GLES30.GL_UNSIGNED_BYTE,GLES30.GL_RGBA,null,Content.COLOR);
	}
	public FBO(int width, int height,Content content){
		setup(width, height,GLES30.GL_UNSIGNED_BYTE,GLES30.GL_RGBA,null,content);
	}

	public FBO(int width, int height, int type, int internalFormat, Buffer data,Content content) {
		setup(width, height, type, internalFormat, data,content);
	}

	private void setup(int width, int height, int type, int internalFormat, Buffer data,Content content){

		mFBO = new int[1];

		mRenderTargets = 0;

		mColorTexture = null;
		mRenderBuffer = null;

		mContent = content;
		mWidth = width;
		mHeight = height;

		mInternalFormat = internalFormat;
		mFormat = GLES30.GL_RGBA;
		mType	= type;
	

		switch(content){

		case COLOR:
			mAttachment = GLES30.GL_COLOR_ATTACHMENT0;
			mColorTexture = Texture.genTexture(ColorLabel);

			break;
		case DEPTH:
			mAttachment = GLES30.GL_DEPTH_ATTACHMENT;
			mDepthTexture = Texture.genTexture(DepthLabel);
			mInternalFormat = DEPTH_INTERNAL_FORMAT;
			mFormat 		= DEPTH_FORMAT;
			mType 			= DEPTH_TYPE;
			break;

		case COLOR_DEPTH: 
			mAttachment = GLES30.GL_COLOR_ATTACHMENT0;
			mColorTexture = Texture.genTexture(ColorLabel);
			mDepthTexture = Texture.genTexture(DepthLabel);
			break;

		default:
			GLError.exit("FBO invalid content");
			break;

		}

		create(data);
	}

	/**
	 * Enable this frame buffer
	 */
	public void use(){
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFBO[0]);
		GLES30.glViewport(0, 0,mWidth, mHeight);
		/*

		if(mContent == Content.DEPTH)
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);
		else if(mContent == Content.COLOR)
			GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
		else*/
		GLES30.glClearColor(0.0f, .0f, .0f, 1.0f);
		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
		/*
		int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
		if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
			GLError.exit(" FB STATUS: " + status);
		 */
	}

	public void unuse(){

		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);	

	}

	public static void useScreen(int w, int h){
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
		GLES30.glViewport(0, 0, w, h);
	}

	/**
	 * Add render target to the fbo. Note that this instance is not responsible of free memory of the additional 
	 * attachment.
	 * @param textureHandle
	 */
	public void addRenderTarget(int textureHandle){
		GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0+mRenderTargets, GLES30.GL_TEXTURE_2D, mColorTexture.get(), 0);
		mRenderTargets++;
	}



	/**
	 * 
	 * Create a frame buffer and its attached textures/render buffers 
	 * 
	 * @param context
	 * @param data
	 * @param internalFormat Eg. GLES30.FLOAT or GLES30.BYTE
	 * @param format  	 Eg. GLES30.FLOAT or GLES30.BYTE 
	 * @param attachment Eg. GLES30.GL_COLOR_ATTACHMENT0  or GLES30.GL_DEPTH_ATTACHMENT
	 * @param hasRenderBuffer
	 */
	private void create(Buffer data){

		// Create frame buffer
		GLES30.glGenFramebuffers(NUM_FBO,mFBO,0);
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFBO[0]);	
		GLES30.glClearColor(.0f, .0f, .0f, 1.0f);

		//Create render buffer
		int rBuf[] = new int[1];
		GLES30.glGenRenderbuffers(NUM_FBO, rBuf, 0);

		//Bind target texture
		Texture target = null;
		if(mContent == Content.DEPTH)
			target = mDepthTexture;
		else
			target = mColorTexture;

		target.bind();
		// Set initial data
		if(data != null)
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mInternalFormat, mWidth, mHeight, 0, mFormat, mType,data);
		else
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mInternalFormat, mWidth, mHeight, 0, mFormat, mType,null);

		// Attach texture to frame buffer as color
		GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, mAttachment, GLES30.GL_TEXTURE_2D, target.get(), 0);
		mRenderTargets++;
		target.unbind();

		//  If FBO contains both Color and Depth add an depth attachment
		if(mContent == Content.COLOR_DEPTH){

			mDepthTexture.bind();
			//GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT, mWidth, mHeight, 0, GLES30.GL_DEPTH_COMPONENT, GLES30.GL_UNSIGNED_INT,null);
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, DEPTH_INTERNAL_FORMAT, mWidth, mHeight, 0,DEPTH_FORMAT, DEPTH_TYPE,null);
			GLError.checkError("1");
			//Attach texture to frame buffer as color
			
			GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, mDepthTexture.get(), 0);
			mDepthTexture.unbind();

		}else if(mContent == Content.COLOR){
			mRenderBuffer = new Texture(rBuf[0],"RBUF");
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, mRenderBuffer.get());
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT32F, mWidth, mHeight);
			GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, mRenderBuffer.get());
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER,0);
		}
		// FBO status 
		int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
		if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
			GLError.exit(" FB STATUS: " + status);


		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);	

	}
	/**
	 * Return handle to the color texture that this frame buffer is rendering to.
	 * 
	 * @return handle to texture 
	 */
	public Texture getColorTexture(){
		if(mContent == Content.COLOR || mContent == Content.COLOR_DEPTH)
			return mColorTexture;

		GLError.exit("No color attachment to this Frame Buffer");
		return null;
	}
	/**
	 * Return handle to the depth texture that this frame buffer is rendering to.
	 * 
	 * @return handle to texture 
	 */
	public Texture getDepthTexture(){
		if(mContent == Content.DEPTH || mContent == Content.COLOR_DEPTH)
			return mDepthTexture;

		GLError.exit("No depth attachment to this Frame Buffer");
		return null;
	}



	/**
	 * Deallocates memory for the textures and fbo on the GPU.
	 */
	public void release(){
		if(mColorTexture != null)
			GLES30.glDeleteTextures(1, new int[]{mColorTexture.get()}, 1);

		if(mRenderBuffer != null)
			GLES30.glDeleteRenderbuffers(1, new int[]{mRenderBuffer.get()}, 0);

		if(mFBO != null)
			GLES30.glDeleteFramebuffers(1, mFBO, 0);

	}


}
