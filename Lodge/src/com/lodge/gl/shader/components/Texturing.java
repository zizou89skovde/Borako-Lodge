package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
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
	
	public static String[] FS_ATTR_DECLARE(Type t){
		String[] s = null;
		switch(t){

		case TEXTURED_TCOORDS:
		case TEXTURED_VPOS:
		case TEXTURED_VPOS_REPEATED:
			s = new String[1];
			s[0] = Attributes.LABEL_ATTR_FS_TCOORD;
		case NONE:
			break;

		default:
			GLError.exit("Texturing: invalid type");
			break;

		}
		return s;
		
	}
}
