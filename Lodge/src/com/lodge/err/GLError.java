package com.lodge.err;

import android.opengl.GLES30;
import android.util.Log;

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
		Log.i(TAG,str);
	}
	
	public static void checkLocation(int l,String s){
		if(l < 0){
			exit(s);
		}
	}
	
	static public void checkError(String op) {
		int error;
		while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
			Log.e(TAG,op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
