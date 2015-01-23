package com.lodge;

import com.lodge.scene.LodgeScene;
import com.lodge.ui.UserInput;

import android.content.Context;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class LodgeSurfaceView extends GLSurfaceView {
	//Graphics 

	protected final LodgeRenderer mRenderer;
	UserInput mUserInput;
	float mDensity;
	public LodgeSurfaceView(final Context context,LodgeScene scene) {

		super(context);
		setEGLContextClientVersion(3);
		mUserInput = new UserInput();
		mRenderer = new LodgeRenderer(context, scene);
		setRenderer(mRenderer);	

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

