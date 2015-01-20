package com.lodge.gl;

import java.util.ArrayList;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.lodge.gl.model.ModelData;
import com.lodge.gl.model.ModelLoader;
import com.lodge.gl.utils.Draw.Method;
import com.lodge.gl.utils.Draw;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Settings;
import com.lodge.gl.utils.Shader;
import com.lodge.gl.utils.Texture;
import com.lodge.gl.utils.Transform;
import com.lodge.gl.utils.Uniform;
import com.lodge.gl.utils.VAO;
import com.lodge.gl.utils.VBO;
/**
 * When subclassing Renderable:
 * </br> 
 * </br>
 *  Musts: </br>
 *  1. Load Model(VBO,VAO) </br>
 *  2. Set shader</br>
 *  3. Set Transform Type </br>
 *  4. Set Transform (A must, if MVP is set)</br>
 *  5. Select Draw Method </br>
 *  </br>
 *  Optional: </br>
 *   </br>
 *  6. Set Texture </br>
 *  7. Set Uniforms </br>
 *  8. Enable for depth rendering </br>
 * @author Datorn
 *
 */
public class Renderable {
	
	Resources mResources;
	
	private VAO 				mVAO;
	private Shader 				mShader;
	protected Transform 		mTransform;
	protected Settings			mSettings;
	
	private Vector<VBO>			mVBOs;
	private Vector<Uniform>  	mUniforms;
	private Vector<Texture> 	mTextures;
	private Vector<Light> 	mLight;
	
	boolean mRenderDepth;
	
	
	public Renderable(Resources res){
		// Store reference to resources 
		mResources = res;
		
		// Create lists
		mVBOs 	  = new Vector<VBO>();
		mUniforms = new Vector<Uniform>();
		mTextures = new Vector<Texture>();
		mLight 	  = new Vector<Light>();

		//Set some default settings
		mSettings = new Settings();
		mSettings.mDrawMethod = Draw.Method.ELEMENTS;
		// To be used for light depth rendering
		mRenderDepth = false;
		
	}
	
	/**
	 * 
	 * Set shader. The VBOs should be set before shader is set.
	 * 
	 * @param vs Handle to the resources text file for the vertex shader
	 * @param fs Handle to the resources text file for the fragment shader
	 */
	protected void setShader(int vs,int fs){
		if(mVBOs.size() < 1){
			Log.e("GL_ERROR","Set VBOs before compile shaders");
			return;
		}
		mShader = new Shader(mResources,vs,fs,mVAO);
	}
	
	/**
	 * Load model from resources. Create VAO & VBOs.
	 * @param resId
	 */
	protected void loadModel(int resId){
		loadModel(resId,true,true);
	}
	/**
	 * 
	 * @param resId
	 * @param useNormal Possible to discard normal buffer
	 * @param useTextureCoords Possible to discard texcoord buffer
	 */
	protected void loadModel(int resId, boolean useNormal, boolean useTextureCoords){
		if(mVAO != null){
			Log.e("GL_ERROR", "VAO already exist. Previous VAO and its content will be deallocated");
			
			
			for (VBO vbo : mVBOs) {
				vbo.release();
			}	
			mVBOs.clear();
			mVAO.release();
			mVAO = null;
		}
		
		mVAO = new VAO();
		mVAO.bind();
		
		
		ArrayList<ModelData> modelData = ModelLoader.getModelDataSingleThread(resId, mResources);
		mVBOs.add(new VBO(3, modelData.get(0).positionArray,VBO.LABEL_POSITION));
		
		mVBOs.add(new VBO(-1,modelData.get(0).indexArray,null));
		
		if(useNormal && modelData.get(0).normalArray!= null){
			mVBOs.add(new VBO(3, modelData.get(0).normalArray,VBO.LABEL_NORMAL));
		}
		
		if(useTextureCoords && modelData.get(0).textureCoordArray != null){
			mVBOs.add(new VBO(3, modelData.get(0).textureCoordArray,VBO.LABEL_TEXCOORD));
		}
		
		mVAO.setIndexCount(modelData.get(0).indexArray.length);
		mVAO.addVBO(mVBOs);
		mVAO.unbind();
	}
	
	void addVBO(float[] data,int stride,String label){
		
		// Bind mVAO 
		mVAO.bind();
		
		// Create VBO 
		VBO vbo = new VBO(stride, data, label);
		
		//If previous VBO items are instance make this VBO instances as well
		// with 0 as divisor.
		if(mVAO.isInstanced())
			vbo.makeInstanced(0);
		mVBOs.add(vbo);
		
		// Re-enable vertex attributes
		mVAO.enableAttributes(mShader.get());
	}
	/**
	 * Create an VBO instance holding the indices ( draw order).
	 * 
	 * @param data
	 * @param stride
	 * @param label
	 */
	void setIBO(short[] data,int stride,String label){
		
		// Bind mVAO 
		mVAO.bind();
		
		// Create VBO 
		VBO vbo = new VBO(stride, data, label);
		
		//If previous VBO items are instance make this VBO instances as well
		// with 0 as divisor.
		if(mVAO.isInstanced())
			vbo.makeInstanced(0);
		mVBOs.add(vbo);
		
		// Re-enable vertex attributes
		mVAO.enableAttributes(mShader.get());
	}
	
	void setInstancedVBO(float[] data,int stride){
		
	
		// Bind mVAO 
		mVAO.bind();
		
		// Make all previous VBO's divided by 0
		for (VBO vbo : mVBOs) {
			vbo.makeInstanced(0);
		}
		
		VBO vbo = new VBO(stride, data, VBO.LABEL_INSTANCED);
		
		// Make new vertex attribute divided by 1. Means that #stride of floats will 
		// be passed to the vertex shader for each instanced draw.
		vbo.makeInstanced(1);
		mVBOs.add(vbo);
		
		// Re-enable vertex attributes
		mVAO.enableAttributes(mShader.get());
		
		mVAO.unbind();
	}
	
	protected void addTexture(int id,String label){
		mTextures.add(new Texture(mResources,id,mSettings.mMipMapEnabled, label));
	}
	/**
	 * Add an existing texture.
	 */
	protected void addTexture(Texture texture){
		mTextures.add(texture);
	}
	
	protected void addTexture(Bitmap bitmap,String label){
		mTextures.add(new Texture(bitmap,mSettings.mMipMapEnabled, label));
	}
	
	protected void addLight(Light l){
		mLight.add(l);
	}
	
	protected void setTransformType(Transform.Type type){
		mTransform = new Transform(type);
	}
	/**
	 * Set value to uniform unit of the shader
	 * 
	 * @param data  Should be a float array. Size 1-4 or 9 (3x3 matrix) or 16 (4x4 matrix).
	 * @param label Name of uniform variable in shader.
	 */
	protected void setUniform(float[] data,String label){
			Uniform uniform = null;
			for (Uniform u : mUniforms) {
				if(u.equals(label)){
					uniform  = u;
				}
			}
			
			if(uniform != null){
				uniform.set(data, data.length);
				uniform.upload(mShader.get());
			}else{
				uniform = new Uniform(label, data, data.length);
				uniform.upload(mShader.get());
				mUniforms.add(uniform);
			}
	}
	
	private void bindTextures(){
		int count = 0;
		int program = mShader.get();
		for (Texture texture : mTextures) {
			texture.select(program, count);
			count++;
		}
	}
	

	public void render(float[] projection,float[] view){
		
		
		
		int program  = mShader.get();
		
		mShader.use();
		
		mVAO.bind();
		
		bindTextures();
		
		mTransform.upload(program, projection, view);
		
		for(Light l : mLight)
				l.upload(program, 0);
		
		Draw.draw(mSettings, mVAO);
		
		mVAO.unbind();
		
		mShader.unuse();
		
		
	}
	
	
	protected void setDrawMethod(Method method){
		mSettings.mDrawMethod = method;
	}

	public boolean renderDepth() {
		return mRenderDepth;
	}
	
	protected void setDepthRendering(boolean bool){
		mRenderDepth = bool;
	}

	public Transform getTransform() {
		return mTransform;
	}

	public VAO getVAO() {
		return mVAO;
	}

	public Texture getTexture() {
		if(mTextures.size() > 0)
			return mTextures.get(0);
		return null;
	}

	public Light getLight() {
		if(mLight.size() > 0 )
			return mLight.get(0);
		return null;
	}
	
	
	
	
	
	

}
