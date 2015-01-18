package com.lodge.err;

import android.util.Log;

import com.lodge.R;

public class Error {

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
}
