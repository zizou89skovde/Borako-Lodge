package com.lodge.gl.utils;

import java.nio.Buffer;

import android.R;
import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

public class FBO {
	
	public enum Content{
		UNDEFINED,
		COLOR,
		DEPTH, 
		COLOR_DEPTH
	}
	
	private final String DepthLabel = "u_DepthTexture";
	private final String ColorLabel = "u_ColorTexture";
	
	final int UNUSED = -1;
	final int NUM_FBO = 1; 
	
	private int[] mFBO;
	private Texture mTexture;
	private Texture mRenderBuffer;
	private Meta mMeta;
	private int mRenderTargets;
	
	final private String ERROR_LABEL = "FBO ERROR "; 
	
	
	public FBO(Meta meta, Buffer data) {
	
		mRenderTargets = 0;
		
		mFBO = null;
		mTexture = null;
		mRenderBuffer = null;
	
		mMeta = meta;
		
		
		int attachment = -1;
		switch(meta.content){
		
		case COLOR:
			attachment = GLES30.GL_COLOR_ATTACHMENT0;
			break;
		case DEPTH:
			attachment = GLES30.GL_DEPTH_ATTACHMENT;
			break;
			
		case COLOR_DEPTH: 
			attachment = GLES30.GL_COLOR_ATTACHMENT0;
			mMeta.hasRenderBuffer = true;
			break;
		
		default:
			Log.e("GL_ERROR", ERROR_LABEL+"invalid content");
			break;
		
		}
		
		create(data,attachment);
		
		
	}
	
	/**
	 * Enable this frame buffer
	 */
	public void enable(){
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFBO[0]);
		GLES30.glViewport(0, 0,mMeta.width, mMeta.width);
		
		if(mMeta.content == Content.DEPTH)
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);
		else if(mMeta.content== Content.COLOR)
			GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
		else
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
		
	}
	
	public void disable(){
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
	}
	
	/**
	 * Add render target to the fbo. Note that this instance is not responsible of free memory of the additional 
	 * attachment.
	 * @param textureHandle
	 */
	public void addRenderTarget(int textureHandle){
		GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0+mRenderTargets, GLES30.GL_TEXTURE_2D, mTexture.get(), 0);
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
	private void create(Buffer data,int attachment){
			
		// Create frame buffer
		GLES30.glGenFramebuffers(NUM_FBO,mFBO,0);
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mFBO[0]);	
		GLES30.glClearColor(.0f, .0f, .0f, 1.0f);
		
		// Create texture 
		if(mMeta.content == Content.DEPTH)
			mTexture = Texture.genTexture(DepthLabel);
		else
			mTexture = Texture.genTexture(ColorLabel);
		
		// Set initial data
		if(data != null)
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mMeta.internalFormat, mMeta.width, mMeta.height, 0, GLES30.GL_RGBA, mMeta.format,data);
		else
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, mMeta.internalFormat, mMeta.width, mMeta.height, 0, GLES30.GL_RGBA, mMeta.format,null);
		
		// Attach texture to frame buffer as color
		GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, attachment, GLES30.GL_TEXTURE_2D, mTexture.get(), 0);
		mRenderTargets++;
		
		// If depth and color are to be stored in same FBO, use a render buffer 
		if(mMeta.hasRenderBuffer){
			int rbuf[] = new int[1]; 
			GLES30.glGenRenderbuffers(NUM_FBO, rbuf, 0);
			mRenderBuffer = new Texture(rbuf[0],"u_Depth");
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, mRenderBuffer.get());
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT32F, mMeta.width, mMeta.height);
			GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, mRenderBuffer.get());
			
		}
		
		// Status check
		int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
		if (status != GLES30.GL_FRAMEBUFFER_COMPLETE)
			Log.e("GL_ERROR"," FB STATUS: " + status);
		
		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);	
		
	}
	/**
	 * Return handle to the color texture that this frame buffer is rendering to.
	 * 
	 * @return handle to texture 
	 */
	public Texture getColorAttachment(){
		if(mMeta.content == Content.DEPTH && mMeta.content== Content.COLOR_DEPTH)
			return mTexture;
		
		Log.e("GL_ERROR","No color attachment to this Frame Buffer");
		return null;
	}
	/**
	 * Return handle to the depth texture that this frame buffer is rendering to.
	 * 
	 * @return handle to texture 
	 */
	public Texture getDepthAttachment(){
		if(mMeta.content == Content.DEPTH)
			return mTexture;
		else if(mMeta.content == Content.COLOR_DEPTH)
			return mRenderBuffer;
		
		Log.e("GL_ERROR","No depth attachment to this Frame Buffer");
		return null;
	}
	

	public class Meta{
	
		public Context context;
		
		public int width;
		public int height;
		
		public Content content;
		
		public int internalFormat;
		public int format;
		
		public boolean hasRenderBuffer = false;
	
	
	}
	
	/**
	 * Deallocates memory for the textures and fbo on the GPU.
	 */
	public void release(){
		if(mTexture != null)
			GLES30.glDeleteTextures(1, new int[]{mTexture.get()}, 1);
		
		if(mRenderBuffer != null)
			GLES30.glDeleteRenderbuffers(1, new int[]{mRenderBuffer.get()}, 0);
		
		if(mFBO != null)
			GLES30.glDeleteFramebuffers(1, mFBO, 0);
		
	}


}
