package com.lodge.gl.shader.components;

import java.util.ArrayList;

import com.lodge.err.GLError;
import com.lodge.gl.Renderable;
import com.lodge.gl.shader.ShaderComposer;
import com.lodge.gl.utils.Light;
import com.lodge.misc.StringUtils;

public class Lightning {


	static final String FS_DEFINE_MAX_SPOT_LIGHT		= "#define MAX_SPOT_LIGHTS " + String.valueOf(Light.MAX_SPOT_LIGHTS);
	static final String FS_DEFINE_MAX_GLOBAL_LIGHT		= "#define MAX_GLOBAL_LIGHTS " + String.valueOf(Light.MAX_GLOBAL_LIGHTS);

	static final String FS_DIRECTION_TYPE = " vec3 ";
	static final String FS_POSITION_TYPE  = " vec3 ";


	static final String MAX_GL_LIGHTS = "[MAX_GLOBAL_LIGHTS]";
	static final String MAX_SL_LIGHTS  = "[MAX_SPOT_LIGHTS]";


	public static final String LIGHT_STRUCT 		   = "Light";

	static final String FS_IN_SIZE_TYPE = "uniform int";

	static final String UNIFORM_GLOBAL_TYPE    = "uniform "+LIGHT_STRUCT+"[MAX_GLOBAL_LIGHTS]";
	static final String UNIFORM_SPOTLIGHT_TYPE = "uniform "+LIGHT_STRUCT+"[MAX_SPOT_LIGHTS]";


	public final static String FS_GLOBAL_LIGHT = "f_GlobalLight";	
	public final static String FS_SPOT_LIGHT_POS   = "f_slPos";
	public final static String FS_SPOT_LIGHT_DIR = "f_slDir";

	public final static boolean LIGHT_FS = false;

	public enum Type{
		PHONG,
		DIFFUSE,
		AMBIENT,
		NONE
	}

	public static String DECLARE(Renderable renderable, boolean isVS){
		String shader = "";
		
		if(renderable.lightning() == Type.NONE)
			return "";
		
		if(LIGHT_FS){
			if(isVS){

			}else {	
				shader += Lightning.DEFINE(renderable);

				shader += Lightning.NUM_LIGHTS(renderable);

				shader += Lightning.STRUCT_DECLARE(renderable);

				shader += Lightning.UNIFORM_DECLARE(renderable);
			}
		}else{
			if(isVS){
				shader += Lightning.DEFINE(renderable);

				shader += Lightning.NUM_LIGHTS(renderable);

				shader += Lightning.STRUCT_DECLARE(renderable);

				shader += Lightning.UNIFORM_DECLARE(renderable);

				shader += Lightning.ATTRIBUTE_DECLARE(renderable,"out");
			}else {

				shader += Lightning.DEFINE(renderable);

				shader += Lightning.NUM_LIGHTS(renderable);

				shader += Lightning.ATTRIBUTE_DECLARE(renderable,"in");
			}
		}
		return shader;
	}


	public static String STRUCT_DECLARE(Renderable rend){
		if(rend.lightning() != Type.NONE){
			return		"struct "+LIGHT_STRUCT+"{ \n"+
					ShaderComposer.TAB + "float "+Light.LIGHT_STRUCT_INTENSITY+";\n"+
					ShaderComposer.TAB + "vec3 "+Light.LIGHT_STRUCT_POS+";\n"+
					ShaderComposer.TAB + "float "+Light.LIGHT_STRUCT_SL_WIDTH+";\n"+
					ShaderComposer.TAB + "vec3 "+Light.LIGHT_STRUCT_DIR+";\n"+
					"};\n";
		}
		return "";

	}

	static String ATTRIBUTE_DECLARE(Renderable rend,String inout){
		if(rend.lightning() != Type.NONE){

			ArrayList<String> s = new ArrayList<String>();
			s.add(inout + " vec3 " + FS_GLOBAL_LIGHT+MAX_GL_LIGHTS);
			s.add(inout + " vec3 " + FS_SPOT_LIGHT_DIR+MAX_SL_LIGHTS);
			s.add(inout + " vec3 " + FS_SPOT_LIGHT_POS+MAX_SL_LIGHTS);
			return ShaderComposer.FORMAT_LINE(StringUtils.TO_ARR(s));

		}
		return "";
	}

	static String NUM_LIGHTS(Renderable rend){
		String[] s = null;
		switch(rend.lightning()){

		case PHONG:
		case DIFFUSE:
			ArrayList<String> declare = new ArrayList<String>();
			declare.add(FS_IN_SIZE_TYPE + " " + Light.LABEL_NUM_GLOBAL_LIGHTS);
			declare.add(FS_IN_SIZE_TYPE + " " + Light.LABEL_NUM_SPOTLIGHTS);
			s = declare.toArray(new String[declare.size()]);
		case NONE:
			break;
		default:
			GLError.exit("Lightning: Invalid type");
			break;
		}
		if(s != null)
			return ShaderComposer.FORMAT_LINE(s);

		return "";

	}

	static String UNIFORM_DECLARE(Renderable rend){
		String[] s = null;
		ShaderVariables sv = rend.getShaderVariables();
		switch(rend.lightning()){

		case PHONG:
		case DIFFUSE:
			ArrayList<String> declare = new ArrayList<String>();
			declare.add(UNIFORM_SPOTLIGHT_TYPE 	 + " " + Light.LABEL_SPOT_LIGHT);
			declare.add(UNIFORM_GLOBAL_TYPE 	 + " " + Light.LABEL_GLOBAL_LIGHT);

			//Shader variables declared 
			sv.spotLight(Light.LABEL_SPOT_LIGHT,ShaderVariables.LIGHT_DIR);
			sv.spotLight(Light.LABEL_SPOT_LIGHT,ShaderVariables.LIGHT_POS);
			sv.globalLight(Light.LABEL_GLOBAL_LIGHT);


			s = declare.toArray(new String[declare.size()]);
		case NONE:
			break;
		default:
			GLError.exit("Lightning: Invalid type");
			break;
		}
		if(s != null)
			return ShaderComposer.FORMAT_LINE(s);

		return "";
	}

	static void COPY_GLOBAL(ArrayList<String> s, String[] ll, String[] rl,String[] transform){
		String dir = "[i]."+Light.LIGHT_STRUCT_DIR;
		if(LIGHT_FS){


			String intensity = "[i]."+Light.LIGHT_STRUCT_INTENSITY;

			s.add(ShaderComposer.TAB + ll + dir + 		" = " + transform[2] + "*" +  rl + dir +";");
			s.add(ShaderComposer.TAB + ll + intensity + " = " + rl + intensity +";");
		}else{

			s.add(ShaderComposer.TAB + ll[0]+"[i] = " + transform[2] + "*" +  rl[0] + dir +";");
		}
	}

	static void COPY_SPOT(ArrayList<String> s, String[] ll, String[] rl,String[] transform){
		COPY_GLOBAL(s, ll, rl, transform);
		String pos = "[i]."+Light.LIGHT_STRUCT_POS;
		if(LIGHT_FS){


			String slWidth = "[i]."+Light.LIGHT_STRUCT_SL_WIDTH;

			s.add(ShaderComposer.TAB + ll + pos + 	  " = vec3("+ transform[1]+ "* vec4(" +  rl + pos +"));");
			s.add(ShaderComposer.TAB + ll + slWidth + " = " + rl + slWidth +";");
		}
		else{
			s.add(ShaderComposer.TAB + ll[1] + "[i] = vec3("+ transform[1]+ "* vec4(" +  rl[1] + pos +",1.0));");
		}

	}

	public static String VS_MAIN(Renderable rend){
		//If light is passing through main 
		if(LIGHT_FS){
			return "";
		}
		ArrayList<String> temp = new ArrayList<String>();
		ShaderVariables sv = rend.getShaderVariables();
		String[] transform = rend.getTransform().getShaderNames();

		switch(rend.lightning()){

		case PHONG:
		case DIFFUSE:
			//Copy global data to varying variable
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + " && i < MAX_GLOBAL_LIGHTS;i++){");
			COPY_GLOBAL(temp, new String[]{FS_GLOBAL_LIGHT}, new String[]{sv.globalLight()}, transform);
			temp.add("}");

			//Copy spotlight data to varying variable
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + " && i < MAX_SPOT_LIGHTS;i++){");
			COPY_SPOT(temp, new String[]{FS_SPOT_LIGHT_DIR,FS_SPOT_LIGHT_POS}, sv.spotLight(), transform);
			temp.add("}");

			sv.globalLight(FS_GLOBAL_LIGHT);
			sv.spotLight(FS_SPOT_LIGHT_DIR,ShaderVariables.LIGHT_DIR);
			sv.spotLight(FS_SPOT_LIGHT_POS,ShaderVariables.LIGHT_POS);
			break;
		case NONE:
			temp.add("");
			break;

		default:
			GLError.exit("Shading: Invalid type");
			break;

		}
		return FINALIZE(temp);

	}

	// Spotlights
	static void diffuseGlobal(ArrayList<String> s,String light){
		String dir = null; 
		if(LIGHT_FS)
			dir = "[i]."+Light.LIGHT_STRUCT_DIR+");";
		else
			dir = "[i]);";
		
		s.add(ShaderComposer.TAB + "l = normalize(" + light + dir);
		s.add(ShaderComposer.TAB + "diffuse += 0.0*max(dot(n,l),0.0);");
	}
	static void specularGlobal(ArrayList<String> s){
		s.add(ShaderComposer.TAB + "r = reflect(l,n);");
		s.add(ShaderComposer.TAB + "float temp_s = max(dot(r,e),0.0);");
		s.add(ShaderComposer.TAB + "specular += pow(temp_s,20.0);");
	}

	// Global lights
	static void diffuseSpotLight(ArrayList<String> s,String[] light,String position){

		
		String pos = null;
		if(LIGHT_FS)
			pos = "[i]."+Light.LIGHT_STRUCT_POS+");";
		else
			pos = "[i]);";
		
		String dir = null;
		if(LIGHT_FS)
			dir = "[i]."+Light.LIGHT_STRUCT_DIR+"))*0.5;";
		else
			dir = "[i]))*0.5;";
		
		s.add(ShaderComposer.TAB + "float d =  (1.0+dot(l,"+ light[1]+dir);
		s.add(ShaderComposer.TAB + "l = normalize("+position +" - " + light[0]+ pos);
		
		s.add(ShaderComposer.TAB + "diffuse += d*max(dot(n,-l),0.0);");
	}
	static void specularSpotLight(ArrayList<String> s){
		s.add(ShaderComposer.TAB + "r = -reflect(l,n);");
		s.add(ShaderComposer.TAB + "float temp_s = max(dot(r,e),0.0);");
		s.add(ShaderComposer.TAB + "specular += d*pow(temp_s,20.0);");
	}


	public static String FS_MAIN(Renderable rend){

		ShaderVariables sv = rend.getShaderVariables();

		ArrayList<String> temp = new ArrayList<String>();
		temp.add("vec3 n = normalize("  + sv.normal() + ");");
		temp.add("vec3 l;");
		temp.add("vec3 r;");
		temp.add("float diffuse = 0.0;");
		temp.add("float ambient = 0.1;");


		switch(rend.lightning()){

		case PHONG:

			temp.add("vec3 e = normalize(-" + sv.position() + ");");
			temp.add("float specular = 0.0;");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + " && i < MAX_GLOBAL_LIGHTS;i++){");
			diffuseGlobal(temp, sv.globalLight());
			specularGlobal(temp);
			temp.add("}");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + " && i < MAX_SPOT_LIGHTS;i++){");
			diffuseSpotLight(temp, sv.spotLight(),sv.position());
			specularSpotLight(temp);
			temp.add("}");

			temp.add( sv.color() + "= (specular+diffuse+ambient) *" + sv.color()  + ";");

			break;
		case DIFFUSE:
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + " && i < MAX_GLOBAL_LIGHTS;i++){");
			diffuseGlobal(temp, sv.globalLight());
			temp.add("}");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + " && i < MAX_SPOT_LIGHTS;i++){");
			diffuseSpotLight(temp, sv.spotLight(),sv.position());
			temp.add("}");

			temp.add( sv.color() + "= (diffuse+ambient)*" + sv.color()  + ";");

			break;
		case NONE:		
			temp.clear();	
			break;

		default:
			GLError.exit("Shading: Invalid type");
			break;

		}

		return FINALIZE(temp);

	}

	static String FINALIZE(ArrayList<String> s) {
		String[] shading = StringUtils.TO_ARR(s);
		String[] TAB = StringUtils.STR_SET(shading.length, ShaderComposer.TAB);
		String[] tmp = StringUtils.CONCAT(TAB, shading);
		int len = tmp.length;
		String[] eofLine = StringUtils.STR_SET(len,"\n");
		String[] formatStrArr = StringUtils.CONCAT(tmp, eofLine);
		String resStr = StringUtils.ARR2STR(formatStrArr); 
		return resStr;
	}

	static String DEFINE(Renderable renderable) {
		String[] s = new String[]{FS_DEFINE_MAX_SPOT_LIGHT+"\n",FS_DEFINE_MAX_GLOBAL_LIGHT+"\n"};
		return StringUtils.ARR2STR(s);
	}

}
