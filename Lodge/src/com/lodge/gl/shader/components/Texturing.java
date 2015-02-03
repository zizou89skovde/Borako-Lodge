package com.lodge.gl.shader.components;

import java.util.ArrayList;
import java.util.Vector;

import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
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
	final static String COLOR_NAME 	 = "color";
	final static String NORMAL_NAME 	 = "normal";

	public static boolean HAS_TCOORDS(String[] attr){
		int len = attr.length;
		for(int i = 0; i < len; i++){
			if(attr[i].equals(VBO.LABEL_TEXCOORD))
				return true;

		}

		return false;

	}

	public static String[] FS_ATTR_DECLARE(Renderable rend){
		String[] s = null;
		switch(rend.getTexturingType()){

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
	
	public static String FS_ATTR_DECLARE_(Renderable rend){
		String[] s = null;
		switch(rend.texturing()){

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
		if(s != null)
			return s[0];
		
		return "";
	}

	public static String VS_MAIN(Renderable r){
		String[] shading = null;
		ShaderVariables sv = r.getShaderVariables();
		String outTCoord = Attributes.LABEL_ATTR_FS_TCOORD;
		switch(r.getTexturingType())
		{
		case TEXTURED_TCOORDS:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + outTCoord + " = " + sv.texcoord();
			sv.texcoord(outTCoord);
			break;
		case TEXTURED_VPOS:
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + outTCoord + " = " + "(" + sv.position() + ".xy+1.0)*0.5";
			sv.texcoord(outTCoord);
			break;
		case TEXTURED_VPOS_REPEATED:
			Texture  t = r.getTexture(0);
			float repeat[] = t.getRepeated();
			shading = new String[1];
			shading[0] = ShaderComposer.TAB + outTCoord + " = (" + sv.position() + ".xy+1.0) * vec2("  + String.valueOf(repeat[0])+ "," + String.valueOf(repeat[1]) + ")";
			sv.texcoord(outTCoord);
			break;
		case NONE:
			shading = null;
			break;
		default:
			GLError.exit("Texturing : Invalid Type");
			break;
		}
		
		
		
		if(shading!= null)
			return ShaderComposer.FORMAT_LINE(shading);
		
		return "";
	}

	public static String FS_MAIN(Renderable renderable){
		String[] s = null;
		Vector<Texture> textures = renderable.getTextures();
		ShaderVariables sv = renderable.getShaderVariables();
		if(textures.size() == 0){
			s = new String[1];
			String color = renderable.colorString();
			if(color == null)
				GLError.exit("NO COLOR NO TEXTURE");
			s[0] = ShaderComposer.TAB +  "vec4 " + COLOR_NAME + " = vec4(" + color  + ")";
		} else{
			
			ArrayList<String> colorList = new ArrayList<String>();
			//TODO: Enable multitexturing (NOT HIGH PRIO) : COLOR_i , only usage when have a texturemap, which is mucho worko.
			for (Texture texture : textures) {
				colorList.add(ShaderComposer.TAB +  "vec4 " + COLOR_NAME + " = texture(" + texture.getLabel() + "," + Attributes.LABEL_ATTR_FS_TCOORD + ");");
				if(texture.hasNormalMap()){
					Texture normalmap = texture.getNormalMap();
					colorList.add(ShaderComposer.TAB +  "vec3 "+NORMAL_NAME + " = texture(" + normalmap.getLabel() + "," + Attributes.LABEL_ATTR_FS_TCOORD + ");");
				}
				
			}
			s = colorList.toArray(new String[colorList.size()]);
		}
		
		//Set color variable name
		sv.color(COLOR_NAME);
		sv.normalMap(NORMAL_NAME);
		if(s != null)
			return ShaderComposer.FORMAT_LINE(s);
		return "";
	}

	public static String FS_TEXTURE_DECLARE(Renderable rend) {
		Vector<Texture> textures = rend.getTextures();
		String[] s = new String[textures.size()];
		int i = 0;
		for (Texture t : textures) {
			s[i++] = TEXTURE_TYPE + " " + t.getLabel();
		}
		
		return ShaderComposer.FORMAT_LINE(s);
	}
}
