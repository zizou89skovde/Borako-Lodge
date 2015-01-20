package com.lodge.gl.shader;

import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Attributes;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Shading;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.shader.components.VSTransform;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Texture;
import com.lodge.gl.utils.Transform;
import com.lodge.gl.utils.VAO;

public class VertexShader {

	
	
	static Texturing.Type CHECK_TEXTURING(VAO vao,Texture texture){
		boolean hasTexture = false;
		if(texture != null)
			hasTexture = true;
		
		boolean hasTextureCoords = Texturing.HAS_TCOORDS(vao.getAttributesString());
		
		if(!hasTextureCoords && hasTexture){
			GLError.warn("Vertex shader: Has texture coords but no attached texture");
			
			return Texturing.Type.TEXTURED_VPOS;
		}
		
		if(hasTextureCoords && !hasTexture){
			GLError.exit("Vertex shader: Has attached texture but no texture coords");
		}
		
		return Texturing.Type.TEXTURED_TCOORDS;
	}
	
	static Light.Type CHECK_LIGHT(Light light, Shading.Type shading){
		
		if(light == null)
			return Light.Type.NONE;
		
		if(shading == Shading.Type.NONE)
			return Light.Type.NONE;
		
		return light.type();
	}
	
	
	public static String create(Renderable renderable){
		
		Transform transform = renderable.getTransform();
		VAO 	  vao		= renderable.getVAO();
		
		Shading.Type shadingType = Shading.Type.PHONG;
		
		Texture	  texture	= renderable.getTexture();
		Texturing.Type texturingType = CHECK_TEXTURING(vao,texture);
		String[] transforms = Transform.getShaderNames(transform.type());
		
		Light light = renderable.getLight();
		Light.Type lightningType = CHECK_LIGHT(light, shadingType);
		String[] lightLabal = new String[]{Light.LABEL_LIGHT_DIR};
		
	
		
		String vertexShader = new String();
		
		vertexShader += ShaderComposer.VERSION(310);
		
		vertexShader += VSTransform.VS_TRANSFORM(transform.type());
		
		vertexShader += Lightning.VS_IN_DECLARE(lightningType, lightLabal);
		
		vertexShader += Attributes.VS_IN_DECLARE(vao);
		
		vertexShader += Attributes.VS_OUT_DECLARE(shadingType,texturingType);
		
		vertexShader += Lightning.FS_DECLARE(lightningType, lightLabal, "out");
		
		
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		vertexShader += ShaderComposer.MAIN_START;
		
		vertexShader += Texturing.VS_MAIN(texturingType, null);
		
		vertexShader += Lightning.VS_MAIN(lightningType, transforms);
		
		vertexShader += Shading.VS_MAIN(shadingType, transforms);

		vertexShader += ShaderComposer.MAIN_END;
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		
		return vertexShader;
		
	}
}
