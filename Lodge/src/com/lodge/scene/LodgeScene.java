package com.lodge.scene;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;

import com.lodge.R;
import com.lodge.err.GLError;
import com.lodge.gl.camera.Camera;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Texture;
import com.lodge.misc.FPS;

import com.lodge.ui.UserInput;

abstract public class LodgeScene {


	
	Resources mResources;
	
	float time = 0.0f;
	
	int mScreenWitdh,mScreenHeight;
	protected Camera mCamera;

	
	HashMap<String,Texture> mTextures = new HashMap<String, Texture>();
	
	/**
	 * Light
	 */
	Vector<Light> mLights = new Vector<Light>();
	int mLightId = 0;
	
	/**
	 * FPS counter
	 */
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
	
	protected void storeTexture(String key,int Rid){
		mTextures.put(key,new Texture(mResources,R.drawable.ic_launcher,key));
	}
	
	protected void storeTexture(String key,Texture texture){
		mTextures.put(key,new Texture(texture,key));
	}
	
	protected int[] screenSize(){
		return new int[]{mScreenWitdh,mScreenHeight};
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
	/**
	 * Add an light source to the common list. Light in the common list will be distributed to all the renderables
	 * that are affected by the light source.
	 * @param l - Light instance
	 * @param position 
	 * @param direction
	 * @return
	 */
	protected int addLight(Light l,float[] position,float[] direction){
		mLights.add(l);
		
		if(position != null)
			l.position(position);
		if(direction != null)
			l.direction(direction);
		
		l.id(mLightId);
		return mLightId++;
	}
	
	protected void removeLight(int id){
		Light tbr = null;
		for (Light l : mLights) {
			if(l.id() == id)
				tbr = l;
		}
		if(tbr != null)
			mLights.remove(tbr);
		else
			GLError.exit("Remove Light: Light do not exist id:" + id);
	}
	

	protected void printFPS(){
		mFPS.printFps();
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
/**
 * Fill Renderable light vector with light sources. All directional (global) lights sources 
 * will be added. Positional (point) sources will be added if they inside the light frustum.  
 * @param rLights
 * @param rOrigin
 */
	public void setLights(Vector<Light> rLights,float[] rOrigin) {
		rLights.clear();
		for (Light l : mLights) {
			if(l.type() == Light.Type.DIRECTIONAL)
				rLights.add(l);
			
		}
		
	}

}

