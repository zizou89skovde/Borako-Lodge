package com.lodge.gl.shader;

import java.util.Vector;
import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
import com.lodge.gl.shader.components.Lightning.Type;
import com.lodge.gl.shader.components.Texturing;
import com.lodge.gl.utils.Light;
import com.lodge.gl.utils.Shader;
import com.lodge.gl.utils.Texture;
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

	public static final String VERION_3_0 = "#version 300 es" + "\n" +
			"precision mediump float;\n";       
	public static final String VERION_3_1 =  "#version 300 es" + "\n" +
			"precision mediump float;\n";

	public static final String MAIN_START = "//START OF SHADER \nvoid main(void)" + "\n" + "{\n";
	public static final String MAIN_END = "} // END OF SHADER \n";

	
	
	
	public static Shader create(Renderable renderable) {
		
		CHECK_SHADING(renderable);
		renderable.setTexturingType(CHECK_TEXTURING(renderable));
		String vs = VertexShader.create(renderable);
		String fs = FragmentShader.create(renderable);
		GLError.warn(vs);
		GLError.warn(fs);

		return  new Shader(vs, fs, renderable.getVAO());
		
	}
	
	

	

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

	class Dependencies {
		boolean hasTexture = false;
		boolean hasNormalMap = false;
		boolean hasLightShading = false;
	}
	
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


	public static Texturing.Type CHECK_TEXTURING(Renderable renderable){
		Vector<Texture> textures = renderable.getTextures();
		VAO vao = renderable.getVAO();
		boolean hasTexture = false;
		if(textures.size() != 0)
			hasTexture = true;
		
		boolean hasTextureCoords = Texturing.HAS_TCOORDS(vao.getAttributesString());
		
		
		if(!hasTextureCoords && hasTexture){
			GLError.warn("Vertex shader: Has texture coords but no attached texture");
			if(textures.get(0).getRepeated() == null)
				return Texturing.Type.TEXTURED_VPOS;
			else
				return Texturing.Type.TEXTURED_VPOS_REPEATED;
		}
		
		if(hasTextureCoords && !hasTexture){
			GLError.exit("Vertex shader: Has attached texture but no texture coords");
		}
		
		if(!hasTexture)
			return Texturing.Type.NONE;
		
		return Texturing.Type.TEXTURED_TCOORDS;
	}
	
	public static void CHECK_SHADING(Renderable r){
		Type s = r.lightning();
		Light.Type l   = r.lightType();
		
		if(s == Type.PHONG && l == Light.Type.NONE)
			GLError.exit("CHECK SHADING: Cant create phong shader without light source");
		
	
	}






}
