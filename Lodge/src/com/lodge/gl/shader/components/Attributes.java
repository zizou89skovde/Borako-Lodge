package com.lodge.gl.shader.components;

import java.util.ArrayList;
import com.lodge.err.GLError;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.VAO;
import com.lodge.misc.StringUtils;

public class Attributes {


	public static String LABEL_ATTR_FS_POS    = "f_Position";
	public static String LABEL_ATTR_FS_NORMAL = "f_Normal";
	public static String LABEL_ATTR_FS_TCOORD = "f_Texcoord";

	static String[] ATTR_TYPE(Integer[] sizeStr){

		int len = sizeStr.length;
		String types[] = new String[len];

		for (int i = 0; i < len; i++) {
			int size = sizeStr[i];
			switch(size){

			case 1:
				types[i] = "in float ";
				break;
			case 2:
				types[i] = "in vec2 ";
				break;
			case 3:
				types[i] = "in vec3 ";
				break;
			case 4:
				types[i] = "in vec4 ";
				break;
			case 9:
				types[i] = "in mat3 ";
			case 16:
				types[i] = "in mat4 ";
			default:
				GLError.exit("ATTR TYPE: Unsupported size/format");
				break;

			}
		}

		return types;

	}

	static String DECLARE(Shading.Type sShading,Texturing.Type sTexturing,String inout){
		//Resulting String
		ArrayList<String> temp = new ArrayList<String>();

		//Attributes - Position & Normal
		String[] vsAttr 	= Shading.ATTR_FS_DECLARE(sShading);
		if(vsAttr != null){
			String[] vsAttrType = StringUtils.STR_SET(vsAttr.length, inout +" vec3 ");
			String[] attrRows 	= StringUtils.CONCAT(vsAttrType,vsAttr);
			StringUtils.APPEND(temp, attrRows);
		}
		//Attributes - Texture Coordinates		
		String[] vsTCoord = Texturing.FS_ATTR_DECLARE(sTexturing,inout);
		if(vsTCoord != null){
			String[] vsTCoordType = StringUtils.STR_SET(vsTCoord.length, inout +" vec2 ");
			String[] tCoordRows = StringUtils.CONCAT(vsTCoordType,vsTCoord);
			StringUtils.APPEND(temp, tCoordRows);
		}

		String[] res = temp.toArray(new String[temp.size()]);

		return ShaderComposer.FORMAT_LINE(res);
	}

	public static String FS_IN_DECLARE(Shading.Type sShading,Texturing.Type sTexturing){
		return DECLARE(sShading, sTexturing, "in");
	}

	public static String VS_OUT_DECLARE(Shading.Type sShading,Texturing.Type sTexturing){
		return DECLARE(sShading, sTexturing, "out");	
	}

	public static String VS_IN_DECLARE(VAO vao){
		String[]  vsIn = vao.getAttributesString();
		Integer[] attrSize = vao.getAttributesSize();



		if(vsIn.length != attrSize.length)
			GLError.exit("VS_INPUT: inconstistent lengths");

		//Determine attribute type
		String vsType[] = ATTR_TYPE(attrSize);

		//Concatenate type with variable names
		String[] vsRows = StringUtils.CONCAT(vsType, vsIn);

		//Format attribute rows to a compilable string  
		return ShaderComposer.FORMAT_LINE(vsRows);

	}


}
