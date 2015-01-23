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

	final static String TEXTURE_TYPE = "uniform sampler2D";


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
			s[0] =  Attributes.LABEL_ATTR_FS_TCOORD;
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
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_TCOORD + " = " + "(" + VBO.LABEL_POSITION + ".xy+1.0)*0.5";
			break;
		case TEXTURED_VPOS_REPEATED:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + Attributes.LABEL_ATTR_FS_TCOORD + " = " + VBO.LABEL_POSITION + " * vec2("  + String.valueOf(repeat[0])+ "," + String.valueOf(repeat[1]) + ")";
			break;
		case NONE:
			shading = new String[]{""};
			break;
		default:
			GLError.exit("Texturing : Invalid Type");
			break;
		}
		return ShaderComposer.FORMAT_LINE(shading);
	}

	public static String FS_MAIN(Type t,String str){
		
		
		String[] s = new String[1];
		if(t == Type.NONE )
			s[0] = ShaderComposer.TAB +  "vec4 color = vec4(" + str + ")";
		else
			s[0] = ShaderComposer.TAB +  "vec4 color = texture(" + str + "," + Attributes.LABEL_ATTR_FS_TCOORD + ");";
		

		return ShaderComposer.FORMAT_LINE(s);
	}

	public static String FS_TEXTURE_DECLARE(Texture texture) {
		String[] s = new String[1];
		s[0] = TEXTURE_TYPE + " " + texture.getLabel();
		return ShaderComposer.FORMAT_LINE(s);
	}
}
