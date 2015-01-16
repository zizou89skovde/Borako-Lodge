package com.lodge.gl.camera;

import android.opengl.Matrix;

import com.lodge.math.UtilVector;
import com.lodge.ui.UserInput;

public class Camera {

	public static  final float CAM_UP_X = 0;
	public static  final float CAM_UP_Y = 1;
	public static  final float CAM_UP_Z = 0;
	//To be removed
	public final static float LOOK_LENGTH = 7.0f; 

	//Camera data instance, holding camera oriention etc.
	CameraInstance mCamera;

	// Static camera height
	static float camHeight = 6;

	// Lazy solution of moving camera, from user input. 
	boolean mMoving;

	public Camera() {
		mCamera = new CameraInstance();
		mCamera.setCamEye(new float[]{-4,0,0});
		mCamera.setCamCenter(new float[]{0,0,0});
	}

	public void update(){
		mCamera.calculateCameraVectors();

		if(mMoving){

			float deltaPos  = 0.4f;

			float[] eye 	= mCamera.getCamEye().clone();
			float[] center 	= mCamera.getCamCenter().clone();
			float[] dir 	= mCamera.getLookDir();

			eye[0] += dir[0]*deltaPos;
			eye[1] += dir[1]*deltaPos;

			center[0] += dir[0]*deltaPos;
			center[1] += dir[1]*deltaPos;

//			if(!mSceneHandler.isCameraCollision(eye)){
				mCamera.setCamEye(eye);
				mCamera.setCamCenter(center);
//			}
		}
	}

	public float[] getViewMatrix(){
		return mCamera.getViewMatrix();
	}
	public CameraInstance getCamInstance() {
		return mCamera;
	}
	public void onUserInput(UserInput touch) {
		synchronized (mCamera) {


			float[] deltaRot = touch.getDeltaCamRotation();
			if(deltaRot != null){

				float speed = 2; 

				float[] right 	= mCamera.getCamRight();
				float[] up 		= mCamera.getCamUp();
				float[] look 	= mCamera.getLookDir();
				float[] eye 	= mCamera.getCamEye();

				float[] dRight 	= UtilVector.vectorMultCpy(right, deltaRot[0]*speed);
				float[] dUp 	= UtilVector.vectorMultCpy(up, deltaRot[1]*speed);

				float[] newLook = UtilVector.vectorAddCpy(look, dRight);
				UtilVector.vectorAdd(newLook, dUp);

				float[] newCenter = UtilVector.vectorAddCpy(eye,newLook);
				mCamera.setCamCenter(newCenter);
			}

			mMoving = touch.isMoving();


		}
	}
	
	public class CameraInstance{

		// Varying positions 
		float[] camEye, camCenter; 
		float[] camUp ,camRight ;

		public CameraInstance() {
		
		}


		public float[] getCamCenter(){
			synchronized (camCenter) {
				return camCenter;
			}

		}
		public float[] getCamEye(){
			synchronized (camEye) {
				return camEye;
			}

		}

		public void setCamEye(float[] camEye){	
			this.camEye = camEye;
		}

		public void setCamCenter(float[] camCenter){
			this.camCenter = camCenter;
		}

		void calculateCameraVectors(){

			float[] look = UtilVector.vectorSubCpy(
					new float[]{camPos[3],camPos[4],camPos[5]},
					new float[]{camPos[0],camPos[1],camPos[2]} 
					);
			
			camRight = UtilVector.cross(look, new float[]{CAM_UP_X,CAM_UP_Y,CAM_UP_Z});
			camUp = UtilVector.cross(camRight,look);

			UtilVector.normalize(camRight);
			UtilVector.normalize(camUp);

		}

		public float[] getCamUp(){
			return camUp;
		}

		public float[] getCamRight(){
			return camRight;
		}


		float[] camPos = new float[9];
		public float[] getViewMatrix(){
				System.arraycopy(camEye, 0, camPos, 0, 3);
				System.arraycopy(camCenter, 0, camPos, 3, 3);
				camPos[6] = CAM_UP_X;
				camPos[7] = CAM_UP_Y;
				camPos[8] = CAM_UP_Z;
				float[] mViewMatrix = new float[16];
				Matrix.setLookAtM(mViewMatrix, 0, camPos[0],camPos[1], camPos[2], camPos[3], camPos[4], camPos[5],camPos[6], camPos[7], camPos[8]);	
				return mViewMatrix;

		}


		public float[] getLookDir() {
			float[] look = UtilVector.vectorSubCpy(camCenter, camEye);
			UtilVector.normalize(look);
			return look;
		}



	}
}

