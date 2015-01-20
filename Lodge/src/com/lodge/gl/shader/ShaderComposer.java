package com.lodge.gl.shader;

import com.lodge.err.GLError;
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

	public static final String VERION_3_0 = "#version 300 es" + "\n" +
			"precision mediump float;\n";       
	public static final String VERION_3_1 =  "#version 300 es" + "\n" +
			"precision mediump float;\n";

	public static final String MAIN_START = "//START OF SHADER \nvoid main(void)" + "\n" + "{\n";
	public static final String MAIN_END = "} // END OF SHADER \n";

	

	public static String VERSION(int version){
		String s = null;
		switch(version){
		case 300:
			s = VERION_3_0;
			break;
		case 310:
			s = VERION_3_1;
			break;
		default:
			GLError.exit("Invalid Shader Version : " + version);
			break;

		}
		return s;

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






}
