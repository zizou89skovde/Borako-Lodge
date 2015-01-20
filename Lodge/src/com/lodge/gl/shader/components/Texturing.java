package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.Texture;
import com.lodge.gl.utils.VBO;

public class Texturing {


	public enum Type{
		TEXTURED_TCOORDS,
		TEXTURED_VPOS,
		TEXTURED_VPOS_REPEATED,
		NONE
	}



	public static boolean HAS_TCOORDS(String[] attr){
		int len = attr.length;
		for(int i = 0; i < len; i++){
			if(attr[i].equals(VBO.LABEL_TEXCOORD))
				return true;

		}

		return false;

	}

	public static String[] FS_ATTR_DECLARE(Type t, String inout){
		String[] s = null;
		switch(t){

		case TEXTURED_TCOORDS:
		case TEXTURED_VPOS:
		case TEXTURED_VPOS_REPEATED:
			s = new String[1];
			s[0] = inout + " " + Attributes.LABEL_ATTR_FS_TCOORD;
		case NONE:
			break;

		default:
			GLError.exit("Texturing: invalid type");
			break;

		}
		return s;

	}

	public static String VS_MAIN(Type t, float[] repeat){
		String[] shading = null;
		switch(t)
		{
		case TEXTURED_TCOORDS:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_TCOORD + " = " + VBO.LABEL_TEXCOORD;
			break;
		case TEXTURED_VPOS:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_TCOORD + " = " + "(" + VBO.LABEL_POSITION + "+1.0)*0.5";
			break;
		case TEXTURED_VPOS_REPEATED:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_TCOORD + " = " + VBO.LABEL_POSITION + " * vec2("  + String.valueOf(repeat[0])+ "," + String.valueOf(repeat[1]) + ")";

			break;
		default:
			GLError.exit("Texturing : Invalid Type");
			break;
		}
		return ShaderComposer.FORMAT_LINE(shading);
	}

	public static String FS_MAIN(Type t,Texture texture){
		
		String label = texture.getLabel();
		
		String[] s = new String[1];
		s[0] = "vec4 color = texture(" + label + "," + Attributes.LABEL_ATTR_FS_TCOORD + ");";

		return ShaderComposer.FORMAT_LINE(s);
	}
}
