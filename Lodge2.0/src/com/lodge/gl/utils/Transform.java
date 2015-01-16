package com.lodge.gl.utils;

import com.lodge.err.GLError;
import com.lodge.math.UtilMatrix;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

public class Transform {


	public enum TransformType{
		MVP,
		VP,
		P,
		NONE,
	}

	private float[] mMatrix;
	private float[] mTranslate;
	private float[] mRotate;
	private float[] mScale;
	private float[] mArbRotate;

	private TransformType mType;

	public Transform(TransformType type) {
		mType = type;
		mMatrix = null;

		if(mType == TransformType.MVP){
			mMatrix = new float[16];
			mTranslate = new float[3];
			mRotate    = new float[3];
			mScale    = new float[]{1,1,1};
			Matrix.setIdentityM(mMatrix, 0);
		}


	}

	public void translate(float x, float y, float z){
		mTranslate = new float[]{x,y,z};
	}

	public void rotate(float x, float y, float z){
		mTranslate = new float[]{x,y,z};
	}

	public void scale(float s){
		mScale = new float[]{s,s,s};
	}

	public void scale(float x,float y, float z){
		mScale = new float[]{x,y,z};
	}

	private void computeMatrix(){

		Matrix.setIdentityM(mMatrix, 0);

		Matrix.scaleM(mMatrix, 0, mScale[0], mScale[1], mScale[2]);

		Matrix.rotateM(mMatrix, 0, mRotate[0], 1, 0, 0);
		Matrix.rotateM(mMatrix, 0, mRotate[1], 0, 1, 0);
		Matrix.rotateM(mMatrix, 0, mRotate[2], 0, 0, 1);

		Matrix.translateM(mMatrix, 0, mTranslate[0], mTranslate[1], mTranslate[2]);


	}



	public void upload(int program, float[] projection, float[] view){
		int location;
		float[] normalMatrix;
		switch(mType){

		case MVP: 

			computeMatrix();

			float[] MV_Matrix  =  new float[16];
			float[] MVPMatrix  =  new float[16];		

			Matrix.multiplyMM(MV_Matrix,0, view, 0, mMatrix, 0);
			Matrix.multiplyMM(MVPMatrix,0, projection, 0, MV_Matrix, 0); 


			location = GLES30.glGetUniformLocation(program, "u_MVPMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, MVPMatrix, 0);
			
			location   = GLES30.glGetUniformLocation(program, "u_MVMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, MV_Matrix, 0);
			
			
			location = GLES30.glGetUniformLocation(program, "u_NormalMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			
			normalMatrix = UtilMatrix.InverseTranspose(MV_Matrix);
			GLES30.glUniformMatrix3fv(location, 1, false, normalMatrix, 0);
			
			break;

		case VP:

			float[] VPMatrix  =  new float[16];		

			Matrix.multiplyMM(VPMatrix,0, projection, 0, view, 0);
			location = GLES30.glGetUniformLocation(program, "u_VPMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, VPMatrix, 0);

			location   = GLES30.glGetUniformLocation(program, "u_MVMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			GLES30.glUniformMatrix4fv(location, 1, false, view, 0);

			location = GLES30.glGetUniformLocation(program, "u_NormalMatrix");
			if(location < 0){
				GLError.exit("Transform - Invalid shader location");
			}
			normalMatrix = UtilMatrix.InverseTranspose(view);
			GLES30.glUniformMatrix3fv(location, 1, false, normalMatrix, 0);
			break;
		case P: 

			location = GLES30.glGetUniformLocation(program, "u_PMatrix");
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

}
