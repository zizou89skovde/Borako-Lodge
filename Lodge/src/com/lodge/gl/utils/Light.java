package com.lodge.gl.utils;

import com.lodge.gl.camera.Camera;
import android.opengl.Matrix;

public class Light {

	
	public final static String LABEL_LIGHT_DIR= "u_LightDirection";
	public final static String LABEL_LIGHT_POS= "u_LightPosition";
	

	public enum Type{
		POSITIONAL,
		DIRECTIONAL,
		NONE
	}
	
	final float UNASSIGNED = -999;


	float[] mDirection;
	float[] mPosition;
	float[] mLookAt;

	float[] mLightMatrix;
	float[] mTextureMatrix;

	Type mType;

	boolean mUseLightMatrix;
	boolean mUseTextureMatrix;
	boolean mIsDynamic;
	
	public Light(Type type){
		setup(type,false,false);
	}

	public Light(Type type, boolean useTextureMatrix, boolean useLightMatrix) {
		setup(type,useTextureMatrix,useLightMatrix);

	}

	private void setup(Type type, boolean useTextureMatrix, boolean useLightMatrix){
		mType = type;

		mDirection = new float[]{UNASSIGNED,UNASSIGNED,UNASSIGNED};
		mPosition  = new float[]{UNASSIGNED,UNASSIGNED,UNASSIGNED};
		mLookAt    = new float[]{UNASSIGNED,UNASSIGNED,UNASSIGNED};

		mUseLightMatrix   = useLightMatrix;
		mUseTextureMatrix = useTextureMatrix;
		mIsDynamic = false;
	}
	
	
	public void addVolumetricLight(){
		
	}
	
	public void removeVolumetricLight(){
		
	}
	
	/**
	 * If this light source is suppose to move. Set this to true.
	 */
	public void enableDynamic(){
		mIsDynamic = false;
	}
	/***
	 * If the light is going to move set this to false in order 
	 * to save continuous uploads of light data. Reduce bus overhead 
	 */
	public void disableDynamic(){
		mIsDynamic = false;
	}

	public void position(float[] position){
		for (int i = 0; i < position.length; i++) {
			mPosition[i] = position[i];
		}
	}

	public void position(float x, float y, float z){
		position(new float[]{x,y,z});
	}

	public void direction(float[] direction){
		for (int i = 0; i < direction.length; i++) {
			mDirection[i] = direction[i];
		}
	}

	public void direction(float x, float y, float z){
		direction(new float[]{x,y,z});
	}


	public void look(float[] look){
		for (int i = 0; i < look.length; i++) {
			mLookAt[i] = look[i];
		}
	}

	public void look(float x, float y, float z){
		look(new float[]{x,y,z});
	}

	public void computeLightMatrix(float[] projection){
		float[] lightView = new float[16];
		Matrix.setLookAtM(
				lightView, 0, 
				mPosition[0], mPosition[1], mPosition[2], 
				mLookAt[0], mLookAt[1], mLookAt[2],
				Camera.CAM_UP_X, Camera.CAM_UP_Y, Camera.CAM_UP_Z);

		Matrix.multiplyMM(mLightMatrix,0, projection, 0, lightView, 0);
	}

	public void computeTextureMatrix(float[] projection){

	}

	public void upload(int program,int count){
		
		if(mIsDynamic && mPosition[0] != UNASSIGNED){
			
			
		}
		
		
	}

	public Type type() {
			return mType;
	}



}
