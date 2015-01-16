package com.lodge.gl.utils;

import java.util.ArrayList;
import java.util.Vector;

import android.opengl.GLES30;

public class VAO {


	final int VAO_SIZE = 1;
	private int mVAO[];

	Vector<VBO> mVBOs;
	int mNumIndices;
	
	boolean isInstanced;
	int mNumInstances = 0;
	
	public VAO() {
		mVBOs = new Vector<VBO>();
		mVAO = new int[VAO_SIZE];
		GLES30.glGenVertexArrays(VAO_SIZE, mVAO, 0);
	}
	/**
	 * Bind vertex array. This should be done before rendering VBO attached 
	 * to this Vertex array object.
	 */
	public void bind(){
		GLES30.glBindVertexArray(mVAO[0]);
	}
	/**
	 * Unbind the vertex array
	 */
	public void unbind(){
		GLES30.glBindVertexArray(0);
	}
	
	
	/**
	 * Free memory 
	 */
	public void release(){
		GLES30.glDeleteVertexArrays(1, mVAO,0);
	}
	/**
	 * Attach VBO instance to the VAO. This VAO should be the only hold of these references. 
	 * 
	 * @param vbo
	 */
	public void addVBO(VBO vbo){
		mVBOs.add(vbo);
	}
	
	public void addVBO(Vector<VBO> vboList){
		mVBOs.addAll(vboList);
	}
	/**
	 * Make all VBO-items ready for instanced drawing. 
	 * @param divisor
	 */
	public void makeInstanced(int divisor,int numInstances){
		isInstanced = true;
		mNumInstances = numInstances;
		for (VBO vbo : mVBOs) {
			vbo.makeInstanced(divisor);
		}
	}
	
	public int numInstances(){
		return mNumInstances;
	}
	
	public boolean isInstanced(){
		return isInstanced;
	}
	
	public void enableAttributes(int program){
		for (VBO vbo : mVBOs) {
			vbo.enableAttribute(program);
		}
	}
	
	public String[] getAttributesString() {
		ArrayList<String> mLabels = new ArrayList<String>();
		for (VBO vbo : mVBOs) {
			String label = vbo.getLabel();
			if(label != null)
				mLabels.add(label);
		}
		return mLabels.toArray(new String[mLabels.size()]);
	}
	public int indexCount() {
		return mNumIndices;
	}
	
	public void setIndexCount(int count) {
		mNumIndices = count;
	}

}
