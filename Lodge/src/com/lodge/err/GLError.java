package com.lodge.err;

import android.opengl.GLES30;
import android.util.Log;

import com.lodge.R;

public class GLError {

	static final String TAG = "GL_Error";
	
	public enum ErrorType{
		GL
	}
	
	public static void exit(String str){
		Log.e(TAG,str);
		throw new RuntimeException(str);
	}
	
	public static void warn(String str){
		Log.e(TAG,"MV/V transforms are not used");
	}
	
	
	static public void checkError(String op) {
		int error;
		while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
			Log.e(TAG,op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
