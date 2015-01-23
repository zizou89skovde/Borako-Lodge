package com.lodge.gl.shader;


import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Attributes;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Shading;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.shader.components.VSTransform;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Transform;
import com.lodge.gl.utils.VAO;

public class VertexShader {

	

	
	
	public static String create(Renderable renderable){
		
		Transform transform = renderable.getTransform();
		VAO 	  vao		= renderable.getVAO();
		
		Shading.Type shadingType = Shading.Type.PHONG;
		
		
		Texturing.Type texturingType = renderable.texturingType();
		String[] transforms = Transform.getShaderNames(transform.type());
		
		Light.Type lightningType = renderable.lightType();
		String[] lightLabal = new String[]{Light.LABEL_LIGHT_DIR};
		
	
		
		String vertexShader = new String();
		
		vertexShader += ShaderComposer.VERSION(310);
		
		vertexShader += VSTransform.VS_TRANSFORM(transform.type());
		
		vertexShader += Lightning.VS_IN_DECLARE(lightningType, lightLabal);
		
		vertexShader += Attributes.VS_IN_DECLARE(vao);
		
		vertexShader += Attributes.VS_OUT_DECLARE(shadingType,texturingType);
		
		vertexShader += Lightning.FS_DECLARE(lightningType, "out");
		
		
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
