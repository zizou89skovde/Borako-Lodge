package com.lodge.object;

import android.content.res.Resources;

import com.lodge.gl.Renderable;
/**
 * How to subclass:  </br>
 * </br>
 *  - Derive Renderable and create and instance assigned to mRenderable</br>
 *  - 
 *   
 *   
 * 
 * 
 * @author Datorn
 *
 */
public class SceneObject {

	public SceneObject(Resources res) {

	}
	
	protected Renderable mRenderable;
	
	public void render(float[] projection, float[] view){
		mRenderable.render(projection, view);
	}
	
	public boolean renderDepth(){
		return mRenderable.renderDepth();
	}
}
