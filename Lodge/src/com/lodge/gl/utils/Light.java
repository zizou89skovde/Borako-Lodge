package com.lodge.gl.utils;

import java.util.Vector;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.lodge.err.GLError;
import com.lodge.gl.camera.Camera;
import com.lodge.math.UtilMatrix;
import com.lodge.math.UtilVector;

public class Light {


	public final static String LABEL_NUM_GLOBAL_LIGHTS 	= "u_NumGlobalLights";
	public final static String LABEL_NUM_SPOTLIGHTS 	= "u_NumSpotLights";

	public final static String LABEL_SPOT_LIGHT   = "u_SpotLight";
	public final static String LABEL_GLOBAL_LIGHT = "u_GlobalLight";


	public final static String LABEL_SPOT_LIGHT_DIR   = "u_sLightDir";
	public final static String LABEL_SPOT_LIGHT_POS   = "u_sLightPos";
	public final static String LABEL_GLOBAL_LIGHT_DIR = "u_gLightDir";

	
	public final static String LABEL_PROP_GLOBAL_LIGHT = "u_gLightProp";
	public final static String LABEL_PROP_SPOT_LIGHT   = "u_sLightProp";
	
	public static final String LIGHT_PROP_STRUCT_SL_WIDTH  = "sl_width";
	public static final String LIGHT_PROP_STRUCT_INTENSITY = "intensity";
	public static final String LIGHT_PROP_STRUCT_COLOR  	  = "color";
	
	

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

	float[] mOutDirection;
	float[] mOutPosition;

	float mIntensity = 1;
	float mSpotLightWidth = 10f;
	float[] mColor = new float[]{1,1,1,1};


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

		mDirection = new float[]{UNASSIGNED,UNASSIGNED,UNASSIGNED,0};
		mPosition  = new float[]{UNASSIGNED,UNASSIGNED,UNASSIGNED,1};
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
		mCSDirection = new float[4];
		mCSDirection = UtilMatrix.M3V3(UtilMatrix.M4ToM3(transform.getView()),mDirection); //Matrix.multiplyMV(mCSDirection, 0, transform.getView(), 0, mDirection, 0); //
//		UtilVector.normalize(mCSDirection);
	}

	/**
	 * Compute Camera space position
	 */
	void computeSCPosition(Transform transform){
		if(mPosition != null)
			mCSPosition = UtilMatrix.M4V4(transform.getView(),mPosition);
	}
	/**
	 * Compute Camera space position and direction
	 */
	public void computeCS(Transform t){

		computeSCDirection(t);
		if(mPosition != null)
			computeSCPosition(t);
	}

	static void ComputeCS(Vector<Light> lights,Transform transform){
		for (Light l : lights) {
				l.computeCS(transform);
				l.mOutDirection = l.mCSDirection.clone();// l.mDirection; //
				l.mOutPosition  = l.mCSPosition.clone(); //l.mPosition; //
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

	static void UploadProperties(Light l,String label, int program){
		String errStr = "Upload light props";
		
		//Upload light color
		String color = label + "." + LIGHT_PROP_STRUCT_COLOR;
		int location = GLES30.glGetUniformLocation(program, color);
		GLError.checkLocation(location, errStr);
		GLES30.glUniform3f(location,l.mColor[0],l.mColor[1],l.mColor[2]);

		//Upload light intensity
		location = GLES30.glGetUniformLocation(program, label + "." + LIGHT_PROP_STRUCT_INTENSITY);
		GLError.checkLocation(location, errStr);
		GLES30.glUniform1f(location,l.mIntensity);
		
		//Upload spot light width
		location = GLES30.glGetUniformLocation(program, label + "." + LIGHT_PROP_STRUCT_SL_WIDTH);
		GLError.checkLocation(location, errStr);
		GLES30.glUniform1f(location,l.mSpotLightWidth);
	}


	static void Upload(Vector<Light> lights,int program){
		String errString = "Light upload";

		// Upload #light of each kind
		int numGlobalLights = NumItems(lights, Type.DIRECTIONAL);
		int numSpotLights = NumItems(lights, Type.POSITIONAL);

		int location = GLES30.glGetUniformLocation(program, LABEL_NUM_GLOBAL_LIGHTS);
		GLError.checkLocation(location,errString);
		GLES30.glUniform1i(location,numGlobalLights);

		location = GLES30.glGetUniformLocation(program, LABEL_NUM_SPOTLIGHTS);
		GLError.checkLocation(location,errString);
		GLES30.glUniform1i(location,numSpotLights);

		int ig = 0;
		int is = 0;
		for (Light l : lights) {
			
			

			if(l.type() == Type.POSITIONAL){
				String index = "["+String.valueOf(is)+"]";
				is++;
				
				//Light props
				UploadProperties(l, LABEL_PROP_SPOT_LIGHT+index, program);
				
				//Position
				String pos 	 = LABEL_SPOT_LIGHT_POS + index;
				location = GLES30.glGetUniformLocation(program, pos);
				GLError.checkLocation(location,errString);
				GLES30.glUniform3f(location,l.mOutPosition[0],l.mOutPosition[1],l.mOutPosition[2]);

				//Direction
				String dir 	= LABEL_SPOT_LIGHT_DIR + index;
				location = GLES30.glGetUniformLocation(program,dir);
				GLError.checkLocation(location,errString);
				GLES30.glUniform3f(location,l.mOutDirection[0],l.mOutDirection[1],l.mOutDirection[2]);


			}else if(l.type() == Type.DIRECTIONAL){
				String index = "["+String.valueOf(ig)+"]";
				ig++;
				
				//Light props
				UploadProperties(l, LABEL_PROP_GLOBAL_LIGHT+index, program);
				
				//Direction
				String dir 	 = LABEL_GLOBAL_LIGHT_DIR + index;
				location = GLES30.glGetUniformLocation(program,dir);
				GLError.checkLocation(location,errString);
				GLES30.glUniform3f(location,l.mOutDirection[0],l.mOutDirection[1],l.mOutDirection[2]);
			}
		}
	}



	public static void Upload(Vector<Light> lights,int program,Transform transform){

		//Transform into Camera space coordinates
		ComputeCS(lights, transform);

		//Upload lights to shader
		Upload(lights,program);

	}
	
	public void color(float[] color){
		mColor = color;
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

