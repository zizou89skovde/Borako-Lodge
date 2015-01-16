package com.lodge.scene;

import android.content.Context;

import com.lodge.gl.camera.Camera;
import com.lodge.misc.FPS;
import com.lodge.object.derived.TemplateObject;
import com.lodge.ui.UserInput;

public class Scene {
/*
	TextureSet mTextures;

	ModelHandler mModelHandler;
	
	SceneHandler mSceneHandler;

	ArrayList<GameObject> mGameObjects;
	CameraObject mCameraObject;
	LightHandler mLightHandler;
	FpsCounter mFpsCounter;

	FrameBuffer mDepthBuffer;
	FrameBuffer mColorBuffer;
	FrameBuffer mPhotoBuffer;
	FrameBuffer mLightEfftectsBuffer;
	PostEffects mPostEffects;
	Blurfilter mBlurFilter;

*/
	float time = 0.0f;
	int mScreenWitdh,mScreenHeight;
	Camera mCamera;
	TemplateObject mTemplateObject;
	FPS mFPS;
	
	public Scene() {


	}

	public void init(Context context,int w, int h, float[] frustum){
		
		

		mScreenHeight=h;
		mScreenWitdh=w;
		
		mTemplateObject = new TemplateObject(context.getResources());
		mCamera = new Camera();
		mFPS = new FPS();


	}
	private void updateCamera(){
		mCamera.update();
	}

	private void updateTime(){
		time+=.1f;
	}

	private void updateObjects(){
		
	}

	private void renderObjects(float[] projection,float[] view){
		mTemplateObject.render(projection, view);
	}

	private void render(float[] projection){
	
		float[] view =mCamera.getViewMatrix();
		renderObjects(projection, view);

	}



	public void update(float[] ProjectionMatrix){
		
		updateTime();
		updateCamera();
		updateObjects();
		
		
		render(ProjectionMatrix);
		mFPS.printFps();
		
	}

	public void setUserInput(UserInput touch) {
		mCamera.onUserInput(touch);

	}

}
