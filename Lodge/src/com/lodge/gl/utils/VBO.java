package com.lodge.gl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.lodge.err.GLError;

import android.opengl.GLES30;
import android.util.Log;

public class VBO {

	
	public final static String LABEL_POSITION 	= "v_Position";
	public final static String LABEL_NORMAL 	= "v_Normal";
	public final static String LABEL_TEXCOORD 	= "v_TexCoord";
	public final static String LABEL_INSTANCED 	= "v_Offset";
	
	private final static int BYTES_PER_FLOAT = 4;
	protected static int BYTES_PER_SHORT = 2;
	private final int NUM_VBO = 1;
	
	private int mVBO[];
	private int mStride;
	private String mLabel;
	
	private boolean isInstanced;
	private int mInstanceDivisor;
	
	private boolean isIndexBuffer;
	
	public VBO(int stride, float[] data,String label) {
		
		defaultSetup(stride, label);
		
		int numBytes = data.length * BYTES_PER_FLOAT;
		FloatBuffer buffer =  ByteBuffer.allocateDirect(numBytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
		buffer.put(data).position(0);
		
		GLES30.glGenBuffers(NUM_VBO, mVBO, 0);
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[0]);
		GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_FLOAT, buffer, GLES30.GL_STATIC_DRAW);
		
	}
	
	public VBO(int stride, short[] data,String label) {
		
		defaultSetup(stride, label);
		isIndexBuffer = true;
		
		int numBytes = data.length * BYTES_PER_SHORT;
		ShortBuffer buffer =  ByteBuffer.allocateDirect(numBytes).order(ByteOrder.nativeOrder()).asShortBuffer();
		buffer.put(data).position(0);
		
		if(buffer.capacity() != data.length)
			GLError.exit("VBO - Inconsistent length");
		
		GLES30.glGenBuffers(NUM_VBO, mVBO, 0);
		GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBO[0]);
		GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,numBytes , buffer, GLES30.GL_STATIC_DRAW);
		

	}
	
	private void defaultSetup(int stride,String label){
		
		mVBO = new int[1];
		
		mLabel = label;
		mStride = stride;
		
		isInstanced = false;
		mInstanceDivisor = 0;
		
	}
	
	public boolean equals(String label){
		 return mLabel.hashCode() == label.hashCode();
	}
	

	/**
	 * Setup Vertex attributes
	 * 
	 * @param program Handle to the gpu program (Vertex Shader and Fragment shader)
	 */
	public void enableAttribute(int program){
		if(!isIndexBuffer){
			int location = GLES30.glGetAttribLocation(program, mLabel);
			if(location < 0){
				GLError.exit("Attribut does not exist in shader");
			}
			GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[0]);
			GLES30.glEnableVertexAttribArray(location);
			GLES30.glVertexAttribPointer(location, mStride, GLES30.GL_FLOAT, false, 0, 0);
			if(isInstanced){
				GLES30.glVertexAttribDivisor(location,mInstanceDivisor);
			}
		}
	}
	/**
	 * Make this vbo ready for be instanced drawing.
	 * This function can only be called once. 
	 * 
	 * @param divisor
	 */
	public void makeInstanced(int divisor){
		//This function does not apply on the buffer holding indices ( draw order).
		if(!isInstanced && !isIndexBuffer){
			isInstanced = true;
			mInstanceDivisor = divisor;
		}
	}
	
	/**
	 * Deallocate memory
	 */
	public void release(){
		GLES30.glDeleteBuffers(1, mVBO,0);
		
	}

	public String getLabel() {
		if(isIndexBuffer)
			return null;
		return mLabel;
	}

	public Integer getStride() {
		return mStride;
	}
}
