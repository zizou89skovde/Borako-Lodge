package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.shader.VertexShader;
import com.lodge.gl.utils.Transform;
import com.lodge.misc.StringUtils;

public class VSTransform {
	static String[] TRANSFORM_TYPE(String[] tString){
		int len = tString.length;
		String[] type = new String[len];
		for (int i = 0; i < len; i++) {

			if(tString[i].equals(Transform.NORMAL_MATRIX))
				type[i] = "uniform mat3 ";
			else
				type[i] = "uniform mat4 ";
		}


		return type;
	}

	public static String VS_DECLARE(Renderable renderable){

		String[] vsTran = renderable.getTransform().getShaderNames();
		String[] vsType = TRANSFORM_TYPE(vsTran);
		String[] vsRows = StringUtils.CONCAT(vsType, vsTran);
		return ShaderComposer.FORMAT_LINE(vsRows);
	}

	static String[] EVAL_LIGHTMODEL(Renderable renderable, String[] transforms){
		String[] shading = null;
		ShaderVariables shaderVar = renderable.getShaderVariables();
		switch (renderable.lightning()) {
		case PHONG:
			shading = new String[3];
			shading[0] = ShaderComposer.TAB + Attributes.FS_IN_POSITION + "= vec3(" + transforms[1] + "*vec4(" + shaderVar.position() +",1.0))";
			shading[1] = ShaderComposer.TAB + Attributes.FS_IN_NORMAL + " = " + transforms[2] + "*" + shaderVar.normal() ;  
			shading[2] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + transforms[0] + " *vec4(" + shaderVar.position() +",1.0)";
			break;

		case DIFFUSE:
			shading = new String[2];
			shading[0] = ShaderComposer.TAB + Attributes.FS_IN_NORMAL + " = " + transforms[2] + "*" + shaderVar.normal() ;
			shading[1] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + transforms[0] + " *vec4(" + shaderVar.position() +",1.0)";
			break;

		case AMBIENT:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + transforms[0] + " *vec4(" + shaderVar.position() +",1.0)";
			break;
		case NONE:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + transforms[0] + " *vec4(" + shaderVar.position() +",1.0)";
			break;
		default:
			break;
		}

		return shading;
	}

	public static String VS_MAIN(Renderable renderable){
		String[] shading  = null;
		Transform transform = renderable.getTransform();
		String[] transforms = transform.getShaderNames();
		ShaderVariables shaderVar = renderable.getShaderVariables();
		switch(transform.type()){

		case MVP:
			//Apply transformations required for Phong shading
			shading = EVAL_LIGHTMODEL(renderable, transforms); 
			//Store active variable names
			shaderVar.position(Attributes.FS_IN_POSITION);
			shaderVar.normal(Attributes.FS_IN_NORMAL);

			break;
		case VP:
			shading = EVAL_LIGHTMODEL(renderable, transforms); 
			shaderVar.normal(Attributes.FS_IN_NORMAL);
			break;
		case P:
			shading = new String[2];
			shading[0] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + transforms[0] + " *vec4(" + shaderVar.position()+",1.0)";
			shaderVar.normal(Attributes.FS_IN_NORMAL);
			break;

		case NONE:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + VertexShader.VS_GL_POSITION + " = " + "vec4(" + shaderVar.position() +",1.0)";
			break;

		default:
			GLError.exit("Shading: Invalid type");
		}
		return ShaderComposer.FORMAT_LINE(shading);
	}
}
