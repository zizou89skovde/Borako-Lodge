package com.lodge.gl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.lodge.err.GLError;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

public class Shader {

	int mProgram;
	/**
	 * Read shader as text files from the resources. Compiling each shader. 
	 * Links and creates Program. Also enable vertex attributes.
	 * 
	 * @param res
	 * @param vsId
	 * @param fsId
	 * @param vao
	 */
	public Shader(Resources res, int vsId,int fsId, VAO vao) {
		String vsString = read(res,vsId);
		String fsString = read(res,fsId);
		
		int vs  = compile(GLES30.GL_VERTEX_SHADER, vsString);
		int fs  = compile(GLES30.GL_FRAGMENT_SHADER, fsString);
		
		String[] attributes = vao.getAttributesString();
		
		mProgram = createProgram(vs, fs, attributes);
		vao.bind();
		vao.enableAttributes(mProgram);
		vao.unbind();
	}
	
	public Shader (String vsString, String fsString, VAO vao){
		int vs  = compile(GLES30.GL_VERTEX_SHADER, vsString);
		int fs  = compile(GLES30.GL_FRAGMENT_SHADER, fsString);
		
		String[] attributes = vao.getAttributesString();
		
		mProgram = createProgram(vs, fs, attributes);
		vao.bind();
		vao.enableAttributes(mProgram);
		vao.unbind();
	}
	
	public void use(){
		GLES30.glUseProgram(mProgram);
	}
	
	public void unuse(){
		GLES30.glUseProgram(0);
	}
	
	public int get(){
		return mProgram;
	}
	
	public void release(){
		GLES30.glDeleteProgram(mProgram);
	}
	
	private String read(final Resources res,
			final int resourceId)
	{
		final InputStream inputStream = res.openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try
		{
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return body.toString();
	}
	
	private int compile(final int type, final String sString) 
	{
		int shaderHandle = GLES30.glCreateShader(type);
		if (shaderHandle != 0) {
			GLES30.glShaderSource(shaderHandle, sString);
			GLES30.glCompileShader(shaderHandle);
			final int[] status = new int[1];
			GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, status, 0);
			if (status[0] == 0){
				GLError.exit("Compilation error : " + GLES30.glGetShaderInfoLog(shaderHandle));
				GLES30.glDeleteShader(shaderHandle);
				
				shaderHandle = 0;
			}
		}
		if (shaderHandle == 0){			
			throw new RuntimeException("Error compile shader.");
		}	
		return shaderHandle;
	}
	
	private int createProgram(final int vs, final int fs, final String[] attributes) 
	{
		int programHandle = GLES30.glCreateProgram();
		if (programHandle != 0) {
			GLES30.glAttachShader(programHandle, vs);			
			GLES30.glAttachShader(programHandle, fs);
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES30.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			GLES30.glLinkProgram(programHandle);
			final int[] status = new int[1];
			GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, status, 0);
			if (status[0] == 0) {				
				Log.e("GL_ERROR", "Error compiling program: " + GLES30.glGetProgramInfoLog(programHandle));
				GLES30.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		if (programHandle == 0)	{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
	
}
