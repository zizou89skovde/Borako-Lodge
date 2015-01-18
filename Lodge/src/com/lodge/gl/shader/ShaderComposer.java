package com.lodge.gl.shader;

import java.util.Vector;

import com.lodge.err.GLError;
import com.lodge.gl.shader.components.Shading;
import com.lodge.gl.utils.Transform;
import com.lodge.gl.utils.VAO;
import com.lodge.misc.StringUtils;

public class ShaderComposer {

	int VPOS    = 0;
	int VNORM   = 0;
	int VTCOORD = 0;
	int VINST   = 0;
	
	int FPOS    = 0;
	int FNORM   = 0;
	int FTCOORD = 0;
	
	public final static String TAB = "    ";

	final String VERION_3_0 = "#version 300 es" + "\n" +
			"precision mediump float;\n";       
	final String VERION_3_1 =  "#version 300 es" + "\n" +
			"precision mediump float;\n";
	
	final String MAIN_START = "void main(void)" + "\n" + "{\n";

	Shading.Type SHADING;
	int VERSION;
	
	void SHADING(Shading.Type shading){
		SHADING = shading;
	}
	
	void VERSION(int version){
		VERSION = version;
	}
	/********************************************************************************
	 * Utility functions
	 *********************************************************************************/
	
	/***
	 * 
	 * @param strArr
	 * @return
	 */
	public static String FORMAT_LINE(String[] strArr){
		
		int len = strArr.length;
		String[] eofLine = StringUtils.STR_SET(len,";\n");
		
		String[] formatStrArr = StringUtils.CONCAT(strArr, eofLine);
		
		String resStr = StringUtils.ARR2STR(formatStrArr); 
		
		return resStr;
	}
	
	
	/********************************************************************************
	 * Attribute function. (in/out in GLSL)
	 * 
	 * 
		//Check dependencies. Is sufficient attributes supported to perform selected shading
		Shading.CHECK_DEP(SHADING, vsIn);
	 *********************************************************************************/

	/********************************************************************************
	 * Transform functions. (Product of Model to world- , view and projection matrices etc. )
	 *********************************************************************************/

	
	
	
	String[] TRANSFORM_TYPE(String[] tString){
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
	
	

	
	String VS_TRANSFORM(Transform.Type tType){
		String[] vsTran = Transform.getShaderNames(tType);
		String[] vsType = TRANSFORM_TYPE(vsTran);
		String[] vsRows = StringUtils.CONCAT(vsType, vsTran);
		return FORMAT_LINE(vsRows);
	}

}
