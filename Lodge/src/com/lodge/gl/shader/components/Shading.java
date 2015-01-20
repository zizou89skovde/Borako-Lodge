package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.VBO;

public class Shading {
	
	final static String FS_DECLARE_COLOR = "ut vec4 out_value";
	public final static String FS_COLOR = "out_value";
	public final static String VS_GL_POSITION = "gl_Position";
	public enum Type{
		PHONG,
		DIFFUSE,
		AMBIENT,
		NONE

	}
	
	static String[] ATTR_FS_DECLARE(Type t){
		String[] attr = null;
		switch(t){

		case PHONG:
		case DIFFUSE:
			attr = new String[2]; 
			attr[0] = Attributes.LABEL_ATTR_FS_POS;
			attr[1] = Attributes.LABEL_ATTR_FS_NORMAL;
			break;
		case NONE:
			break;
			default:
				GLError.exit("Shading: Invalid shading type");
		}
		return attr;
	}

	static public boolean CHECK_DEP(Type shading,String[] vsAttr){

		int nAttr = vsAttr.length;

		switch(shading){

		case PHONG:
		case DIFFUSE:

			if(nAttr < 2)
				GLError.exit("Shader config Insufficient Attributes");

			if(vsAttr[0].equals("v_Postion") && vsAttr[1].equals("v_Normal"));
			return true;

		case AMBIENT:

			if(nAttr < 1)
				GLError.exit("Shader config Insufficient Attributes");

			if(vsAttr[0].equals("v_Postion"))
				return true;
			break;

		default:
			GLError.exit("Shader config: Invalid config");
			break;

		}

		return false;

	}


	public static String VS_MAIN(Type t ,String[] sTransforms){
		
		String[] shading  = null;
		switch(t){
			
		case PHONG:
			shading = new String[3]; 
			shading[0] = ShaderComposer.TAB + "f_Position" + " = " + sTransforms[1] + "*vec4(" + VBO.LABEL_POSITION+",1.0)";
			shading[1] = ShaderComposer.TAB + "f_Normal" + " = " + sTransforms[1] + "*vec4(" + VBO.LABEL_POSITION+",1.0)";
			shading[2] = ShaderComposer.TAB + VS_GL_POSITION + " = " + sTransforms[0] + " *vec4(" + VBO.LABEL_POSITION+",1.0)";
			break;
		case DIFFUSE:
			shading = new String[2];
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_NORMAL + "= " + sTransforms[1] + "*vec4(" + VBO.LABEL_POSITION+",1.0)";
			shading[1] = ShaderComposer.TAB + VS_GL_POSITION + " = " + sTransforms[0] + " *vec4(" + VBO.LABEL_POSITION+",1.0)";
			break;
			
		case NONE:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + VS_GL_POSITION + " = " + "vec4(" + VBO.LABEL_POSITION+",1.0)";
			break;
			
		default:
			GLError.exit("Shading: Invalid type");
		}

		return ShaderComposer.FORMAT_LINE(shading);
	}
	
	public static String FS_DECLARE(){
		return ShaderComposer.FORMAT_LINE( new String[]{FS_DECLARE_COLOR});
	}
	
	public static String FS_MAIN(Type t ,String[] sTransforms){

		String[] shading  = null;
		int i = 0;
		switch(t){

		case PHONG:
			shading = new String[10];
			
			shading[i++] = "vec3 l = normalize("  + Lightning.FS_DECLARE_DIRECTION + ")";
			shading[i++] = "vec3 e = normalize(-" + Attributes.LABEL_ATTR_FS_POS + ")";
			shading[i++] = "vec3 n = normalize("  + Attributes.LABEL_ATTR_FS_NORMAL + ")";
			shading[i++] = "vec3 r = normalize(-l,n)";
			shading[i++] = "";
			shading[i++] = "float specular = max(dot(r,e),0.0)";
			shading[i++] = "specular = pow(specular,20.0)";
			shading[i++] = "";
			shading[i++] = "float diffuse = max(dot(n,l),0.0)";
			shading[i++] = "float ambient = 0.1";
			shading[i++] = "out_Color = (specular+diffuse+ambient)*color";
			break;
		case DIFFUSE:
			shading = new String[2]; 
			shading[i++] = "vec3 l = normalize("  + Lightning.FS_DECLARE_DIRECTION + ")";
			shading[i++] = "vec3 n = normalize("  + Attributes.LABEL_ATTR_FS_NORMAL + ")";
			shading[i++] = "float diffuse = max(dot(n,l),0.0)";
			shading[i++] = "float ambient = 0.1";
			shading[i++] = "out_Color = (diffuse+ambient)*color";
			break;
		case NONE:
			shading = new String[1];
			shading[i++] = "out_Color = color";
			break;
			
		default:
			GLError.exit("Shading: Invalid type");
			break;

		}
		
		return ShaderComposer.FORMAT_LINE(shading);


		
	}



}
