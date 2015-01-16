package com.lodge;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.lodge.scene.Scene;
import com.lodge.ui.UserInput;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class ExtRenderer implements GLSurfaceView.Renderer {


	
	

	/*************************************************************/
	// FRUSTUM
	/*************************************************************/
	public final static float TOP 	 = 1;
	public final static float BOTTOM = -1;
	public final static float FAR = 40;
	public final static float NEAR = 1;
	
	private Context context;
	float[] mProjMatrix = new float[16];
	Scene mGameHandler = new Scene();
	
	public ExtRenderer (Context context) {
		this.context = context; 		
	}
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		GLES30.glDisable(GLES30.GL_CULL_FACE);
		GLES30.glEnable(GLES30.GL_DEPTH_TEST);		
		GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
	}


	@Override
	public void onDrawFrame(GL10 unused) {

		GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
		mGameHandler.update(mProjMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES30.glViewport(0, 0, width, height);
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 40.0f;
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
		float[] frustum = new float[] {near,far,ratio};
		mGameHandler.init(context,width,height,frustum);
	}



	public static void checkGlError(String TAG, String op) {
		int error;
		while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
	
	public void onPause(){		
	}
	public void onResume(){		
	}

	public void newUserInput(UserInput input){
		mGameHandler.setUserInput(input);
	}






}

