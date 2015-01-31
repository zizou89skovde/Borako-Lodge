package com.lodge.gl.shader;

import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Attributes;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Texturing;

public class FragmentShader {

	final static String 		FS_DECLARE_COLOR = "out vec4 out_value;\n";
	public final static String 	FS_COLOR 		 = "out_value";
	
	public static String create(Renderable renderable){
	
		

		String fragmengtShader = new String();
		
		fragmengtShader += ShaderComposer.VERSION(310);
		
		fragmengtShader += Lightning.DECLARE(renderable, false);
		
		fragmengtShader += FS_DECLARE_COLOR;
	
		fragmengtShader += Texturing.FS_TEXTURE_DECLARE(renderable);
		
		fragmengtShader += Attributes.FS_IN_DECLARE(renderable);
		
	
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		fragmengtShader += ShaderComposer.MAIN_START;
		
		fragmengtShader += Texturing.FS_MAIN(renderable);
		
		fragmengtShader += Lightning.FS_MAIN(renderable);

		fragmengtShader +=  OUTPUT(renderable); 
		
		fragmengtShader += ShaderComposer.MAIN_END;
		///////////////////// MAIN BODY STARTS HERE /////////////////////////
		

		
		return fragmengtShader;
	}
	
	static String OUTPUT(Renderable r){
		return ShaderComposer.TAB + FS_COLOR + " = " + r.getShaderVariables().color() + ";\n";
	}
}
