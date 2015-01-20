package com.lodge.gl.shader.components;

import com.lodge.gl.shader.ShaderComposer;
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




	public static String VS_TRANSFORM(Transform.Type tType){
		String[] vsTran = Transform.getShaderNames(tType);
		String[] vsType = TRANSFORM_TYPE(vsTran);
		String[] vsRows = StringUtils.CONCAT(vsType, vsTran);
		return ShaderComposer.FORMAT_LINE(vsRows);
	}
}
