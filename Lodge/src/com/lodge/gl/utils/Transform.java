package com.lodge.gl.utils;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.lodge.err.GLError;
import com.lodge.math.UtilMatrix;


public class Transform {


	public enum Type{
		MVP,
		VP,
		P,
		NONE,
	}


	public static String NORMAL_MATRIX = "u_NormalMatrix";



	private float[] mMatrix;
	private float[] mTranslate;
	private float[] mRotate;
	private float[] mScale;
	//private float[] mArbRotate; // TODO: ArbRotate

	private float[] mNormalMatrix = new float[9];
	private float[] mModelViewMatrix = new float[16];

	private Type mType;

	private boolean hasBeenModified = true;
	private boolean hasWarnedLocation = false;
	public Transform(Type type) {
		mType = type;
		mMatrix = null;

		if(mType == Type.MVP){
			mMatrix = new float[16];
			mTranslate = new float[3];
			mRotate    = new float[3];
			mScale    = new float[]{1,1,1};
			Matrix.setIdentityM(mMatrix, 0);
		}


	}

	public static String[] getShaderNames(Type t){
		String[] s = null;
		switch(t){

		case MVP:
			s = new String[3];
			s[0] = "u_MVPMatrix";
			s[1] = "u_MVMatrix";
			s[2] = "u_NormalMatrix";
			break;
		case VP:
			s = new String[3];
			s[0] = "u_VPMatrix";
			s[1] = "u_VMatrix";
			s[2] = "u_NormalMatrix";
			break;

		case P:
			s = new String[1];
			s[0] = "u_PMatrix";

		default:
			GLError.exit("Transform: Invalid type");


		}
		return s;
	}

	public void translate(float x, float y, float z){
		mTranslate = new float[]{x,y,z};
		hasBeenModified = true;
	}

	public void rotate(float x, float y, float z){
		mTranslate = new float[]{x,y,z};
		hasBeenModified = true;
	}

	public void scale(float s){
		mScale = new float[]{s,s,s};
		hasBeenModified = true;
	}

	public void scale(float x,float y, float z){
		mScale = new float[]{x,y,z};
		hasBeenModified = true;
	}

	private void computeMatrix(){
		if(hasBeenModified){
			Matrix.setIdentityM(mMatrix, 0);

			Matrix.scaleM(mMatrix, 0, mScale[0], mScale[1], mScale[2]);

			Matrix.rotateM(mMatrix, 0, mRotate[0], 1, 0, 0);
			Matrix.rotateM(mMatrix, 0, mRotate[1], 0, 1, 0);
			Matrix.rotateM(mMatrix, 0, mRotate[2], 0, 0, 1);

			Matrix.translateM(mMatrix, 0, mTranslate[0], mTranslate[1], mTranslate[2]);

		}
		hasBeenModified = false;

	}

	private void checkLocation(int location){
		if(location < 0 && !hasWarnedLocation){
			GLError.warn("Transform - Invalid shader location");
			hasWarnedLocation = true;
		}
	}



	public void upload(int program, float[] projection, float[] view){
		int location;


		String[] name = getShaderNames(mType);

		switch(mType){

		case MVP: 

			computeMatrix();


			float[] MVPMatrix  =  new float[16];		

			Matrix.multiplyMM(mModelViewMatrix,0, view, 0, mMatrix, 0);
			Matrix.multiplyMM(MVPMatrix,0, projection, 0, mModelViewMatrix, 0); 


			location = GLES30.glGetUniformLocation(program, name[0]);
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, MVPMatrix, 0);

			location   = GLES30.glGetUniformLocation(program, name[1]);
			checkLocation(location);

			GLES30.glUniformMatrix4fv(location, 1, false, mModelViewMatrix, 0);


			location = GLES30.glGetUniformLocation(program, name[2]);
			checkLocation(location);

			mNormalMatrix = UtilMatrix.InverseTranspose(mModelViewMatrix);
			GLES30.glUniformMatrix3fv(location, 1, false, mNormalMatrix, 0);

			break;

		case VP:

			float[] VPMatrix  =  new float[16];		
			mModelViewMatrix = view.clone();

			Matrix.multiplyMM(VPMatrix,0, projection, 0, view, 0);
			location = GLES30.glGetUniformLocation(program, name[0]);
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, VPMatrix, 0);

			location   = GLES30.glGetUniformLocation(program, name[1]);
			checkLocation(location);

			GLES30.glUniformMatrix4fv(location, 1, false, view, 0);

			location = GLES30.glGetUniformLocation(program, name[2]);
			checkLocation(location);

			mNormalMatrix = UtilMatrix.InverseTranspose(view);
			GLES30.glUniformMatrix3fv(location, 1, false, mNormalMatrix, 0);
			break;
		case P: 

			location = GLES30.glGetUniformLocation(program, name[0]);
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, projection, 0);
			break;
		case NONE:
			break;

		default: 
			GLError.exit("Transform - Invalid transform type selected");


		}
	}

	public Type type() {
		return mType;
	}

	public float[] normalixMatrix() {
		if(mType == Type.MVP || mType == Type.VP)
			return mNormalMatrix;
		return null;
	}

	public float[] toViewMatrix() {
		if(mType == Type.MVP || mType == Type.VP )
			return mModelViewMatrix;
		return null;
	}

}
