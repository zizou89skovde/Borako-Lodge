package com.lodge.gl.shader;


import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Attributes;
import com.lodge.gl.shader.components.Lightning;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.shader.components.VSTransform;

public class VertexShader {



	public final static String 	VS_GL_POSITION 	 = "gl_Position";


	public static String create(Renderable renderable){


		String vertexShader = new String();

		vertexShader += ShaderComposer.VERSION(310);

		vertexShader += VSTransform.VS_DECLARE(renderable);
		
		vertexShader += Lightning.DECLARE(renderable, true);

		vertexShader += Attributes.VS_IN_DECLARE(renderable);



		vertexShader += Attributes.VS_OUT_DECLARE(renderable);

		///////////////////// MAIN BODY STARTS HERE /////////////////////////

		vertexShader += ShaderComposer.MAIN_START;

		vertexShader += Texturing.VS_MAIN(renderable);

		vertexShader += VSTransform.VS_MAIN(renderable);
		
		vertexShader += Lightning.VS_MAIN(renderable);

		vertexShader += ShaderComposer.MAIN_END;
		///////////////////// MAIN BODY STARTS HERE /////////////////////////

		return vertexShader;

	}
}
