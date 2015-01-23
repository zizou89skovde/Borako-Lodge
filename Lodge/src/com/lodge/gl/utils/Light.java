package com.lodge.gl.utils;

import java.util.Vector;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.lodge.err.GLError;
import com.lodge.gl.camera.Camera;
import com.lodge.math.UtilMatrix;
import com.lodge.math.UtilVector;

public class Light {


	public final static String LABEL_LIGHT_DIR  = "u_LightDirection";
	public final static String LABEL_LIGHT_POS  = "u_LightPosition";

	public final static String LABEL_NUM_GLOBAL_LIGHTS 	= "u_NumGlobalLights";
	public final static String LABEL_NUM_SPOTLIGHTS 	= "u_NumSpotLights";

	public final static String LABEL_SPOTLIGHT_POS	 	= "u_SpotLightPosition";
	public final static String LABEL_SPOTLIGHT_DIR	 	= "u_SpotLightDirection";
	
	public final static String LABEL_GLOBAL_DIR	 	= "u_GlobalLightDirection";


	public enum Type{
		POSITIONAL(0),
		DIRECTIONAL(1),
		NONE(2);

		private int numVal;

		Type(int numVal) {
			this.numVal = numVal;
		}

		public int getNumVal() {
			return numVal;
		}
	}

	final float UNASSIGNED = -999;


	float[] mCSDirection;
	float[] mCSPosition;

	float[] mDirection;
	float[] mPosition;


	float[] mLookAt;

	float[] mLightMatrix;
	float[] mTextureMatrix;

	Type mType;
	int mId;

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
	/**
	 * Compute Camera Space direction 
	 */
	void computeSCDirection(Transform transform){
		mCSDirection = UtilVector.expand(UtilMatrix.M3V3(transform.normalixMatrix(),mDirection),4,0);
	}

	/**
	 * Compute Camera space position
	 */
	void computeSCPosition(Transform transform){
		mCSPosition = UtilMatrix.M4V4(transform.toViewMatrix(),UtilVector.expand(mPosition,4,1));
	}
	/**
	 * Compute Camera space position and direction
	 */
	public void computeCS(Transform t){
		computeSCDirection(t);
		computeSCPosition(t);
	}

	static void ComputeCS(Vector<Light> lights,Transform transform){
		for (Light l : lights) {
			l.computeCS(transform);
		}
	}


	static int NumItems(Vector<Light> lights,Type type){
		int n = 0;
		for (Light l : lights) {
			if(l.type() == type)
				n++;
		}
		return n;
	}


	static void UploadSpotLight(Vector<Light> lights,int program){
		int numSpotLights = NumItems(lights, Type.POSITIONAL);

		// Upload #spotlights
		int location = GLES30.glGetUniformLocation(program, LABEL_NUM_SPOTLIGHTS);
		if(location < 0)
			GLError.exit("Light upload - cant find NUM SPOTLIGHT location");
		GLES30.glUniform1i(location,numSpotLights);

		if(numSpotLights > 0){

			float[] spotDir = new float[4*numSpotLights];
			int offDir = 0;
			float[] spotPos = new float[4*numSpotLights];
			int offPos = 0;

			for (Light l : lights) {
				if(l.type() == Type.POSITIONAL){
					offDir = UtilVector.append(spotDir,l.mCSDirection,offDir);
					offPos = UtilVector.append(spotPos,l.mCSPosition,offPos);
				}
			}
			location = GLES30.glGetUniformLocation(program, LABEL_SPOTLIGHT_DIR);
			if(location < 0)
				GLError.exit("Light upload - cant find spotlight DIRECTION location");
			GLES30.glUniform4fv(location,numSpotLights,spotDir,0);

			location = GLES30.glGetUniformLocation(program, LABEL_SPOTLIGHT_POS);
			if(location < 0)
				GLError.exit("Light upload - cant find spotlight POSITION location");
			GLES30.glUniform4fv(location,numSpotLights,spotPos,0);
		}
	}
	
	static void UploadGlobalLight(Vector<Light> lights,int program){
		int numGlobalLights = NumItems(lights, Type.POSITIONAL);
		int location = GLES30.glGetUniformLocation(program, LABEL_NUM_GLOBAL_LIGHTS);
		if(location < 0)
			return;
		GLES30.glUniform1i(location,numGlobalLights);
		if(numGlobalLights > 0){
			float[] dir = new float[4*numGlobalLights];
			int off = 0;
			for (Light l : lights) {
				if(l.type() == Type.DIRECTIONAL){
					off = UtilVector.append(dir,l.mCSDirection,off);
				}
			}
			location = GLES30.glGetUniformLocation(program, LABEL_GLOBAL_DIR);
			if(location < 0)
				GLError.exit("Light upload - cant find spotlight DIRECTION location");
			GLES30.glUniform4fv(location,numGlobalLights,dir,0);

		}
	}

	public static void Upload(Vector<Light> lights,int program,Transform transform){

		//Transform into Camera space coordinates
		ComputeCS(lights, transform);

		UploadSpotLight(lights, program);
		
		UploadGlobalLight(lights, program);

	}

	public void upload(int program,int count){

		int location = GLES30.glGetUniformLocation(program, LABEL_LIGHT_DIR);
		if(location < 0)
			GLError.warn("Uniform - Could not find shader location");
		else
			GLES30.glUniform3f(location,mDirection[0],mDirection[1],mDirection[2]);

		if(mType == Type.POSITIONAL){


		}else{


		}



	}

	public Type type() {
		return mType;
	}

	public void id(int id) {
		mId = id;

	}
	public int id(){
		return mId;
	}





}

