package com.lodge;

import com.lodge.ui.UserInput;

import android.content.Context;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GL_SurfaceView extends GLSurfaceView {
	//Graphics 

	private final ExtRenderer mRenderer;
	UserInput mUserInput;
	float mDensity;
	public GL_SurfaceView(final Context context) {

		super(context);
		setEGLContextClientVersion(3);

		/* Graphics */
		mRenderer = new ExtRenderer(context);
		setRenderer(mRenderer);	
		
		mUserInput = new UserInput();

	}

	@Override
	public void onPause() {
		mRenderer.onPause();
	}

	@Override
	public void onResume() {
		mRenderer.onResume();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {


		float xNorm = event.getX()/getWidth();
		float yNorm = event.getY()/getHeight();
		mUserInput.setNewData(
				event.getPointerCount(),
				new float[]{xNorm,yNorm},
				event.getAction(),
				 event.getDownTime()
				);
		mRenderer.newUserInput(mUserInput);
		return true;
	}






}

