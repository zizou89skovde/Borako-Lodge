package com.lodge.gl.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

public class Texture {


	private int mTexture[];
	private String mLabel;
	private boolean isShared;
	private boolean isMipmaped;
/**
 * 
 * 
 * @param bitmap Bitmap instance
 * @param mipmapEnable mipmaping
 * @param label Name of the 2DSample unit in the shader
 */
	public Texture(Bitmap bitmap,boolean mipmap,String label) {
		defaultSetting(label, mipmap);
		generateTexture(bitmap);
	}
/**
 * 
 * @param res Resource instance
 * @param id  Resource id
 * @param mipmap Enable mipmaping
 * @param label  Name of the 2DSample unit in the shader
 */
	public Texture(Resources res, int id,boolean mipmap,String label) {
		defaultSetting(label, mipmap);

		Bitmap bitmap = BitmapFactory.decodeResource(res, id);
		Bitmap.Config bitmapConfig =  bitmap.getConfig();
		bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		bitmap = bitmap.copy(bitmapConfig, true);

		generateTexture(bitmap);
	}

	/**
	 * Apply some default settings
	 * 
	 * @param label
	 * @param mipmap
	 */
	private void defaultSetting(String label, boolean mipmap){
		isShared = false;
		isMipmaped = mipmap;
		mLabel = label;
	}

	/**
	 * Creating an Texture instance with a shared texture. This Texture instance will not be 
	 * responsible of deallocation of the texture memory
	 * @param texture
	 */
	public Texture(int texture,String label){
		mTexture = new int[1];
		mTexture[0] = texture;
		isShared = true;
		mLabel = label; 
	}

	/**
	 * Generate texture on the device. Default settings:  linear interpolation and repeat. 
	 * 
	 * @param bitmap
	 */
	private void generateTexture(Bitmap bitmap){
		mTexture = new int[1];
		GLES20.glGenTextures(1, mTexture, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP,mTexture[0]);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		if(isMipmaped){
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);	
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);	
		}else{
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);		
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);	
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);	
		}

	}
	
	


	/**
	 * Generate a selected number of empty textures
	 * @param nof
	 * @return
	 */
	static Texture genTexture(String label){
		int[] t = new int[1];
		GLES30.glGenTextures(1, t, 0);

		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, t[0]);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
		
		return new Texture(t[0],label);

	}
	
	public boolean equals(String label){
		return mLabel.hashCode() == label.hashCode();
	}
	
	
	/**
	 * 
	 * @param program
	 * @param count If shader has multiple texture count should be increment for each
	 * texture that is binded during current pass. 
	 */
	public void bind(int program,int count){
		int handle;
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0+count);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
		handle = GLES20.glGetUniformLocation(program, mLabel);
		GLES20.glUniform1i(handle , count);
	}

	
	/**
	 * Unbind texture
	 */
	static void unbind(){
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	/**
	 *  If this texture is created outside this instance. It will not be deallocated here.
	 * 
	 */
	public void release(){
		if(!isShared)
			GLES30.glDeleteTextures(1, mTexture, 0);
	}

	/**
	 * Return value of the handle to the texture on the device 
	 * @return
	 */
	public int get() {
		return mTexture[0];
	}
}
