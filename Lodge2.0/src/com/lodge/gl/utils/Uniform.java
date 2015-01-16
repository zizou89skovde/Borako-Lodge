package com.lodge.gl.utils;

import android.opengl.GLES30;
import android.util.Log;

public class Uniform {
	String mLabel;
	float[] mData;
	int mSizeData;
	boolean hasBeenUploaded;



	public Uniform(String label, float[] data, int sizeData) {
		mLabel = label;
		mData = data;
		mSizeData = sizeData;
		hasBeenUploaded = false;
	}

	public void upload(int program){
		
		if(!hasBeenUploaded){
			
			int location = GLES30.glGetUniformLocation(program, mLabel);
			if(location < 0)
				Log.e("GL_ERROR","Uniform - Could not find shader location");
			switch (mSizeData) {
			case 1:
				GLES30.glUniform1f(location,mData[0]);
				break;
			case 2:
				GLES30.glUniform2f(location,mData[0],mData[1]);
				break;
			case 3:
				GLES30.glUniform3f(location,mData[0],mData[1],mData[2]);
				break;
			case 4:
				GLES30.glUniform4f(location,mData[0],mData[1],mData[2],mData[3]);

			default:
				break;
			}
			hasBeenUploaded = true;
		}
	}
	
	public void set(float[] data, int size){
		if(mData != null)
			mData = null;
		
		mData = data;
		mSizeData = size;
		hasBeenUploaded = false;
	}
}
