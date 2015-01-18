package com.lodge.gl.utils;

import android.opengl.GLES30;

import com.lodge.gl.utils.Draw.Method;

public class Settings {

	
	public Method mDrawMethod;
	public int mPrimitivType = GLES30.GL_TRIANGLES;
	public int mSizeIndex 	  = GLES30.GL_UNSIGNED_SHORT;
	
	public boolean mMipMapEnabled = true;
	
	public Settings() {
	}
}
