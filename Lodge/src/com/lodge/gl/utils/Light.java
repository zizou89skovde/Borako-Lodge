package com.lodge.gl.utils;

import java.util.Vector;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.lodge.err.GLError;
import com.lodge.gl.camera.Camera;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.math.UtilMatrix;
import com.lodge.math.UtilVector;

public class Light {


	public final static String LABEL_NUM_GLOBAL_LIGHTS 	= "u_NumGlobalLights";
	public final static String LABEL_NUM_SPOTLIGHTS 	= "u_NumSpotLights";

	public final static String LABEL_SPOT_LIGHT   = "u_SpotLight";
	public final static String LABEL_GLOBAL_LIGHT = "u_GlobalLight";


	public static final String LIGHT_STRUCT_POS       = "pos";
	public static final String LIGHT_STRUCT_DIR       = "dir";
	public static final String LIGHT_STRUCT_SL_WIDTH  = "sl_width";
	public static final String LIGHT_STRUCT_INTENSITY = "intensity";

	public static final float MAX_DISTANCE 				= 10;
	public static final int MAX_SPOT_LIGHTS 				= 2;
	public static final int MAX_GLOBAL_LIGHTS 				= 1;


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

	float mIntensity = 1;
	float mSpotLightWidth = 0.2f;


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
	int ctr = 0;
	void computeSCDirection(Transform transform){
		mCSDirection = UtilMatrix.M3V3(transform.normalixMatrix(),mDirection);
		UtilVector.normalize(mCSDirection);
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
			return;
		GLES30.glUniform1i(location,numSpotLights);

		if(numSpotLights > 0){
			int i = 0;
			for (Light l : lights) 
				if(l.type() == Type.POSITIONAL){
					String index = "["+String.valueOf(i)+"].";
					//Upload direction data for light i
					location = GLES30.glGetUniformLocation(program, LABEL_SPOT_LIGHT+index+LIGHT_STRUCT_DIR);
					if(location < 0)
						GLError.exit("Light upload - cant find spotlight uniform");
					if(Lightning.LIGHT_FS)
						GLES30.glUniform3f(location,l.mCSDirection[0],l.mCSDirection[1],l.mCSDirection[2]);
					else
						GLES30.glUniform3f(location,l.mDirection[0],l.mDirection[1],l.mDirection[2]);

					//Upload position data for light i
					location = GLES30.glGetUniformLocation(program, LABEL_SPOT_LIGHT+index+LIGHT_STRUCT_POS);
					if(location < 0)
						GLError.exit("Light upload - cant find spotlight uniform");
					if(Lightning.LIGHT_FS)
						GLES30.glUniform3f(location,l.mCSPosition[0],l.mCSPosition[1],l.mCSPosition[2]);
					else
						GLES30.glUniform3f(location,l.mPosition[0],l.mPosition[1],l.mPosition[2]);

					//Upload light intensity
					location = GLES30.glGetUniformLocation(program, LABEL_SPOT_LIGHT+index+LIGHT_STRUCT_INTENSITY);
					if(location < 0)
						GLError.exit("Light upload - cant find spotlight uniform");
					GLES30.glUniform1f(location,l.mIntensity);

					//Upload spotlight width
					location = GLES30.glGetUniformLocation(program, LABEL_SPOT_LIGHT+index+LIGHT_STRUCT_SL_WIDTH);
					if(location < 0)
						GLError.exit("Light upload - cant find spotlight uniform");
					GLES30.glUniform1f(location,l.mSpotLightWidth);

				}
		}	
	}

	static void UploadGlobalLight(Vector<Light> lights,int program){
		int numGlobalLights = NumItems(lights, Type.DIRECTIONAL);
		int location = GLES30.glGetUniformLocation(program, LABEL_NUM_GLOBAL_LIGHTS);
		if(location < 0)
			return;
		GLES30.glUniform1i(location,numGlobalLights);
		if(numGlobalLights > 0){
			int i = 0;
			for (Light l : lights) {
				if(l.type() == Type.DIRECTIONAL){
					String index = "["+String.valueOf(i)+"].";
					location = GLES30.glGetUniformLocation(program, LABEL_GLOBAL_LIGHT +index+LIGHT_STRUCT_DIR);
					if(location < 0)
						GLError.exit("Light upload - cant find global light uniform");
					
					if(Lightning.LIGHT_FS)
						GLES30.glUniform3f(location,l.mCSDirection[0],l.mCSDirection[1],l.mCSDirection[2]);
					else
						GLES30.glUniform3f(location,l.mDirection[0],l.mDirection[1],l.mDirection[2]);
					
					//Upload light intensity
					location = GLES30.glGetUniformLocation(program, LABEL_GLOBAL_LIGHT+index+LIGHT_STRUCT_INTENSITY);
					if(location < 0)
						GLError.exit("Light upload - cant find global light uniform");
					GLES30.glUniform1f(location,l.mIntensity);

					//Upload spotlight width
					location = GLES30.glGetUniformLocation(program, LABEL_GLOBAL_LIGHT+index+LIGHT_STRUCT_SL_WIDTH);
					if(location < 0)
						GLError.exit("Light upload - cant find global light uniform");
					GLES30.glUniform1f(location,l.mSpotLightWidth);
					
				
				}
			}

		}
	}
	
//	static void UploadLightProperties

	public static void Upload(Vector<Light> lights,int program,Transform transform){

		//Transform into Camera space coordinates
		ComputeCS(lights, transform);

		UploadSpotLight(lights, program);

		UploadGlobalLight(lights, program);

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

	public float distance(float[] p) {
		if(mType != Type.POSITIONAL)
			GLError.exit("Distance function not valid for directional light");


		return UtilVector.getLength3(p, mPosition);
	}





}

