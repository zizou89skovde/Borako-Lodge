package com.lodge.gl;

import java.util.ArrayList;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.lodge.err.GLError;
import com.lodge.gl.model.ModelData;
import com.lodge.gl.model.ModelLoader;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Lightning.Type;
import com.lodge.gl.shader.components.ShaderVariables;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.utils.DrawMethod;
import com.lodge.gl.utils.DrawMethod.Method;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Settings;
import com.lodge.gl.utils.Shader;
import com.lodge.gl.utils.Texture;
import com.lodge.gl.utils.Transform;
import com.lodge.gl.utils.Uniform;
import com.lodge.gl.utils.VAO;
import com.lodge.gl.utils.VBO;
import com.lodge.scene.LodgeScene;
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
abstract public class Renderable {

	Resources mResources;
	private LodgeScene			mScene;
	private VAO 				mVAO;
	private Shader 				mShader;
	protected Transform 		mTransform;
	protected Settings			mSettings;

	private Vector<VBO>			mVBOs;
	private Vector<Uniform>  	mUniforms;
	private Vector<Texture> 	mTextures;
	private Vector<Light> 		mLight;
	private float[]				mColor;

	private float[]				mOrigin = new float[3];
	ShaderVariables 			mShaderVariables = new ShaderVariables();


	private boolean mInitialized;

	/*
	 * Shader Related items
	 */
	private Type mLightning = Type.PHONG;
	private Texturing.Type mTexturing = Texturing.Type.NONE;



	boolean mRenderDepth;


	public Renderable(Resources res,LodgeScene scene){
		// Store reference to resources 
		mResources = res;

		mScene = scene;

		// Create lists
		mVBOs 	  = new Vector<VBO>();
		mUniforms = new Vector<Uniform>();
		mTextures = new Vector<Texture>();

		//Setup up light sources
		mLight 	  = new Vector<Light>();
		setLights();

		//Set some default settings
		mSettings = new Settings();
		mSettings.mDrawMethod = DrawMethod.Method.ELEMENTS;

		// To be used for light depth rendering
		mRenderDepth = false;
		mInitialized = false;

		// Default transform is MVP
		setTransformType(Transform.Type.MVP);

		//Default positions in 0 
		mTransform.translate(0, 0, 0);

	}

	/**
	 * Run initially and thereafter every fram. Checks which light that are affecting this renderable.
	 */
	private void setLights(){
		mScene.setLights(mLight,mOrigin);
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
			GLError.exit("Set VBOs before compile shaders");
			return;
		}
		mShader = new Shader(mResources,vs,fs,mVAO);
	}



	/**
	 * Load model from resources. Create VAO & VBOs.
	 * @param resId
	 */
	protected void setModel(int resId){
		setModel(resId,true,true);
	}
	/**
	 * 
	 * @param resId
	 * @param useNormal Possible to discard normal buffer
	 * @param useTextureCoords Possible to discard texcoord buffer
	 */
	protected void setModel(int resId, boolean useNormal, boolean useTextureCoords){
		if(mVAO != null){
			GLError.warn("VAO already exist. Previous VAO and its content will be deallocated");


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

	protected void addTexture(Texture texture, float[] repeat){
		Texture t = new Texture(texture);
		t.setRepeat(repeat);
		mTextures.add(t);
	}


	public void setColor(float r, float g, float b, float a){
		if(mTextures.size() != 0){
			GLError.exit("Trying to set color when textures has been attached");
		}
		mColor = new float[] {r,g,b,a};
	}

	public String colorString() {
		if(mColor == null)
			GLError.exit("Renderable does not contain texture or color ");
		String color = new String();
		for (int i = 0; i < 3; i++) {
			color+= String.valueOf(mColor[i]);
			color+= ",";
		}
		color+= String.valueOf(mColor[3]);
		return color;
	}
	/**
	 * Add an existing texture.
	 */
	protected void addTexture(Texture texture){
		mTextures.add(texture);
	}

	protected void addTexture(Bitmap bitmap,String label){
		if(mColor != null){
			GLError.exit("Trying to attach texture when color has been set");
		}
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


	/**
	 * Runs only first frame.
	 */
	private void init(){
		if(mInitialized)
			return;
		// Auto generate shader 
		if(mShader == null){
			mShader = ShaderComposer.create(this);
		}
		mInitialized = true;

	}



	public void render(float[] projection,float[] view){

		init();

		int program  = mShader.get();

		mShader.use();

		mVAO.bind();

		Texture.Bind(mTextures,program);

		mTransform.upload(program, projection, view);
		if(lightning() != Lightning.Type.NONE)
			Light.Upload(mLight,program,mTransform); // TODO: do this once for every new light, this should not be done no light model has been selected

		DrawMethod.Draw(mSettings, mVAO);

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

	public Texture getTexture(String key) {
		return mScene.getTexture(key);
	}

	public Vector<Texture> getTextures(){
		return mTextures;
	}

	public Light getLight() {
		if(mLight.size() > 0 )
			return mLight.get(0);
		return null;
	}

	public Type lightning() {
		return mLightning;
	}
	
	public void lightning(Type shading) {
		mLightning = shading;
	}

	public Texturing.Type texturing() {
		if(mTextures.size() < 1)
			return Texturing.Type.NONE;

		if(mTextures.get(0).hasNormalMap()){

		}
		return  ShaderComposer.CHECK_TEXTURING(this);
	}

	public boolean hasTexture() {
		return mTextures.size() > 0;
	}

	public boolean hasNormalMap() {
		if(mTextures.size() < 1)
			return false;

		return mTextures.get(0).hasNormalMap();

	}



	public Light.Type lightType() {
		if( mLight.size() < 1)
			return Light.Type.NONE;

		return mLight.get(0).type();
	}

	public void setTexturingType(Texturing.Type t) {
		mTexturing = t;
	}

	public Texturing.Type getTexturingType() {
		return mTexturing;
	}

	public Texture getTexture(int i) {
		return mTextures.get(i);
	}


	public ShaderVariables getShaderVariables(){
		return mShaderVariables;
	}


}
