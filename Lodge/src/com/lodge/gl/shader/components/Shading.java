package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.VBO;

public class Shading {

	public enum Type{
		PHONG,
		DIFFUSE,
		AMBIENT,
		NONE

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

	static String[] ATTR_FS_DECLARE(Type t){
		String[] attr = null;
		switch(t){

		case PHONG:
		case DIFFUSE:
			attr = new String[2]; 
			attr[0] = Attributes.LABEL_ATTR_FS_POS;
			attr[1] = Attributes.LABEL_ATTR_FS_NORMAL;
			break;
			default:
				GLError.exit("Shading: Invalid shading type");
		}
		return attr;
	}


	static String[] VS_MAIN(Type t,Lightning.Type tLightning,Texturing.Type tTexturing ,String[] sTransforms){

		String[] vsOut = null;

		String[] light = Lightning.VS_MAIN(tLightning,sTransforms);
		String[] tcoord = null;

		String[] attr  = null;
		switch(t){

		case PHONG:
		case DIFFUSE:
			attr = new String[2]; 
			attr[0] = ShaderComposer.TAB + "f_Position" + ShaderComposer.TAB+ "= " + sTransforms[1] + "*vec4(" + VBO.LABEL_POSITION+",1.0)";
			attr[1] = ShaderComposer.TAB + "f_Normal" + ShaderComposer.TAB+ "= " + sTransforms[1] + "*vec4(" + VBO.LABEL_POSITION+",1.0)";

		}



		return null;
	}



}
