package com.lodge.err;

import android.opengl.GLES30;
import android.util.Log;

import com.lodge.R;

public class GLError {

	public enum ErrorType{
		GL
	}
	
	static void printErr(ErrorType type,String str){
		switch(type){
		case GL: 
		//	String e = Context.getResources().getString(R.string.GL_ERROR);
			//Log.e(e,str);
			break;
		
		
		
		}
	}
	
	public static void exit(String str){
		throw new RuntimeException(str);
	}
	
	static public void checkError(String op) {
		int error;
		while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
			throw new RuntimeException(op + ": glError " + error);
		}
	}
}
