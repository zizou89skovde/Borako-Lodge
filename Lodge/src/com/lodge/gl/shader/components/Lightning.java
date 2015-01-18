package com.lodge.gl.shader.components;

import com.lodge.err.GLError;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.Light;

public class Lightning {


	static final String FS_DECLARE_DIRECTION = "f_LightDirection";
	static final String FS_DECLARE_POSITION  = "f_LightPosition";

	static final String VS_IN_DIRECTION_TYPE = "uniform vec3 ";
	static final String VS_IN_POSITION_TYPE  = "uniform vec3 ";
	
	static final String FS_DIRECTION_TYPE = " vec3 ";
	static final String FS_POSITION_TYPE  = " vec3 ";

	public enum Type{
		POSITIONAL,
		DIRECTIONAL,
		NONE
	}


	public static String VS_IN_DECLARE(Type t,String[] strLight){
		String[] s = null;
		switch(t){

		case POSITIONAL:
			s = new String[2];
			s[0] = VS_IN_POSITION_TYPE  + strLight[0];
			s[1] = VS_IN_DIRECTION_TYPE + strLight[1];
			break;

		case DIRECTIONAL:
			s = new String[1];
			s[0] = VS_IN_DIRECTION_TYPE + strLight[0];
			break;

		case NONE:
			break;
		default:
			GLError.exit("Lightning: Invalid type");
			break;

		}
		if(s != null)
			return ShaderComposer.FORMAT_LINE(s);

		return null;
	}

	public static String FS_DECLARE(Type t,String[] strLight, String inout){
		String[] s = null;
		switch(t){

		case POSITIONAL:
			s = new String[2];
			s[0] = inout + FS_POSITION_TYPE  + FS_DECLARE_POSITION;
			s[1] = inout + FS_DIRECTION_TYPE + FS_DECLARE_DIRECTION;
			break;

		case DIRECTIONAL:
			s = new String[1];
			s[0] = inout + FS_DIRECTION_TYPE + FS_DECLARE_DIRECTION;
			break;

		case NONE:
			break;
		default:
			GLError.exit("Lightning: Invalid type");
			break;

		}
		if(s != null)
			return ShaderComposer.FORMAT_LINE(s);
		
		return null;
	}

	static String COMPUTE_POS(String transform){
		return "= vec3(" + transform + "*vec4(" + Light.LABEL_LIGHT_POS + ",1.0))";
	}

	static String COMPUTE_DIR(String transform){
		return "= " + transform  + Light.LABEL_LIGHT_DIR;
	}


	public static String[] VS_MAIN(Type t,String[] transforms) {

		String[] s = null;
		switch(t){

		case POSITIONAL:
			s = new String[2];
			s[0] = ShaderComposer.TAB + FS_DECLARE_POSITION + ShaderComposer.TAB + COMPUTE_POS(transforms[1]);
			s[1] = ShaderComposer.TAB + FS_DECLARE_DIRECTION + ShaderComposer.TAB + COMPUTE_DIR(transforms[2]);
			break;

		case DIRECTIONAL:
			s = new String[1];
			s[1] = ShaderComposer.TAB + FS_DECLARE_DIRECTION + ShaderComposer.TAB + COMPUTE_DIR(transforms[2]);;

			break;

		case NONE:
			break;
		default:
			GLError.exit("Lightning: Invalid type");
			break;

		}


		return s;
	}
}
