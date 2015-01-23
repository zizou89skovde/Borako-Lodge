package com.lodge.gl.shader;

import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Attributes;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Shading;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.shader.components.Texturing.Type;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Texture;
public class FragmentShader {

	
	public static String create(Renderable renderable){
	
		Shading.Type shadingType 	 = renderable.shading();
	
		Texturing.Type texturingType = renderable.texturingType();
		Texture text = null;
		String  colorLabel;
		if(texturingType == Type.NONE){
			colorLabel = renderable.colorString();
		}else{
			text = renderable.getTexture();
			colorLabel  = text.getLabel();
		}
		
		Light.Type lightningType 	 = renderable.lightType();
			
		
		String fragmengtShader = new String();
		
		fragmengtShader += ShaderComposer.VERSION(310);
		
		fragmengtShader += Shading.FS_DECLARE_COLOR();
		
		if(text != null)
			fragmengtShader += Texturing.FS_TEXTURE_DECLARE(text);
	
		fragmengtShader += Lightning.FS_DECLARE(lightningType,"in");
	
		fragmengtShader += Attributes.FS_IN_DECLARE(shadingType,texturingType);
		
		///fragmengtShader += Texturing.FS_ATTR_DECLARE(texturingType,"in");
		
	
		
		
		
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		fragmengtShader += ShaderComposer.MAIN_START;
		
		fragmengtShader += Texturing.FS_MAIN(texturingType, colorLabel);
		
		fragmengtShader += Shading.FS_MAIN(shadingType);

		fragmengtShader += ShaderComposer.MAIN_END;
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		

		
		return fragmengtShader;
	}
}
