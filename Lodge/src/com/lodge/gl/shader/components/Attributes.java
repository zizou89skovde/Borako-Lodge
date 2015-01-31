package com.lodge.gl.shader.components;

import java.util.ArrayList;

import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.shader.components.Lightning.Type;
import com.lodge.gl.utils.VAO;
import com.lodge.gl.utils.VBO;
import com.lodge.misc.StringUtils;

public class Attributes {


	final static String FS_IN_POSITION    = "f_Position";
	final static String FS_IN_NORMAL = "f_Normal";
	final static String LABEL_ATTR_FS_TCOORD = "f_Texcoord";

	public final static String LABEL_INTERNAL_POS    = "position";
	public final static String LABEL_INTERNAL_NORM   = "normal";
	public final static String LABEL_INTERNAL_TCOORD = "texcoord";

	final static String[] LABEL_INTERNAL = new String[]{LABEL_INTERNAL_POS,
		LABEL_INTERNAL_NORM,
		LABEL_INTERNAL_TCOORD
	};

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

	static String DECLARE(Renderable rend,String inout){
		//Resulting String
		ArrayList<String> temp = new ArrayList<String>();

		//Attributes - Position & Normal
		String[] vsAttr = ATTR_FS_DECLARE(rend);
		if(vsAttr != null){
			String[] vsAttrType = StringUtils.STR_SET(vsAttr.length, inout +" vec3 ");
			String[] attrRows 	= StringUtils.CONCAT(vsAttrType,vsAttr);
			StringUtils.APPEND(temp, attrRows);
		}
		//Attributes - Texture Coordinates		
		String[] vsTCoord = Texturing.FS_ATTR_DECLARE(rend);
		if(vsTCoord != null){
			String[] vsTCoordType = StringUtils.STR_SET(vsTCoord.length, inout +" vec2 ");
			String[] tCoordRows = StringUtils.CONCAT(vsTCoordType,vsTCoord);
			StringUtils.APPEND(temp, tCoordRows);
		}

		String[] res = temp.toArray(new String[temp.size()]);

		return ShaderComposer.FORMAT_LINE(res);
	}

	public static String FS_IN_DECLARE(Renderable renderable){
		return DECLARE(renderable, "in");
	}

	public static String VS_OUT_DECLARE(Renderable renderable){
		return DECLARE(renderable, "out");	
	}

	public static String VS_IN_DECLARE(Renderable rend){
		VAO vao = rend.getVAO();
		String[]  vsIn = vao.getAttributesString();
		boolean hasNormals = false;
		
		
		//Store names of the active variables 
		ShaderVariables shadVar = rend.getShaderVariables();
		for (int i = 0; i < vsIn.length; i++) {
			if(vsIn[i].equals(VBO.LABEL_POSITION)){
				shadVar.position(vsIn[i]);
			}
			if(vsIn[i].equals(VBO.LABEL_NORMAL)){
				shadVar.normal(vsIn[i]);
				hasNormals = true;
					
			}
			if(vsIn[i].equals(VBO.LABEL_TEXCOORD)){
				shadVar.texcoord(vsIn[i]);
			}
		}
		if(rend.lightning() != Type.NONE && !hasNormals)
			GLError.exit("Lightning has been enables but no normals available");
		
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
	
	static String[] ATTR_FS_DECLARE(Renderable r){
		String[] attr = null;
		switch(r.lightning()){

		case PHONG:
		case DIFFUSE:
			attr = new String[2]; 
			attr[0] = Attributes.FS_IN_POSITION;
			attr[1] = Attributes.FS_IN_NORMAL;
			break;
		case NONE:
			break;
		default:
			GLError.exit("Shading: Invalid shading type");
		}
		return attr;
	}




}
