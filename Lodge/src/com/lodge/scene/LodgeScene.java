package com.lodge.scene;

import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;

import com.lodge.err.GLError;
import com.lodge.gl.camera.Camera;
import com.lodge.gl.utils.Texture;
import com.lodge.misc.FPS;

import com.lodge.ui.UserInput;

abstract public class LodgeScene {


	
	Resources mResources;
	
	float time = 0.0f;
	
	int mScreenWitdh,mScreenHeight;
	Camera mCamera;

	
	public HashMap<String,Texture> mTextures = new HashMap<String, Texture>();
	
	FPS mFPS;
	
	public LodgeScene() {


	}

	public void init(Context context,int w, int h, float[] frustum){
		
		mResources = context.getResources();
	

		mScreenHeight=h;
		mScreenWitdh=w;
		
		mCamera = new Camera();
		mFPS = new FPS();


	}
	
	public Texture getTexture(String key){
		Texture t = mTextures.get(key);
		if(t == null)
			GLError.exit("Texture not found");
		return t;
	}
	
	private void updateCamera(){
		mCamera.update();
	}

	private void updateTime(){
		time+=.1f;
	}


	private void render(float[] projection){
	

	}



	public void update(float[] ProjectionMatrix){
		
		updateTime();
		updateCamera();
		
		
		render(ProjectionMatrix);
		mFPS.printFps();
		
	}

	public void setUserInput(UserInput touch) {
		mCamera.onUserInput(touch);

	}

}

