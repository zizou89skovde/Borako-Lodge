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


	public static final String LIGHT_STRUCT  = "Light";

	static final String FS_IN_SIZE_TYPE = "uniform int";

	static final String UNIFORM_GLOBAL_TYPE    = "uniform "+LIGHT_STRUCT+"[MAX_GLOBAL_LIGHTS]";
	static final String UNIFORM_SPOTLIGHT_TYPE = "uniform "+LIGHT_STRUCT+"[MAX_SPOT_LIGHTS]";


	public final static String FS_GLOBAL_LIGHT 	 = "f_glDir";	
	public final static String FS_SPOT_LIGHT_POS = "f_slPos";
	public final static String FS_SPOT_LIGHT_DIR = "f_slDir";


	public static boolean LIGHT_FS = true;

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

		if(!isVS){
			shader += DEFINE(renderable);
			shader += NUM_LIGHTS(renderable);
			shader += STRUCT_PROPERTIES_DEFINE();
			shader += PROPERTIES_DECLARE();
			
			if(LIGHT_FS)
				shader += UNIFORM_DECLARE(renderable); 			//Light solely is computed in fragment shader
			else
				shader += ATTRIBUTE_DECLARE(renderable,"in"); 	//Light is passed through vertex shader
		}

		//Light is passed through vertex shader
		if(isVS && !LIGHT_FS){
			shader += DEFINE(renderable);
			shader += NUM_LIGHTS(renderable);
			shader += UNIFORM_DECLARE(renderable);
			shader += ATTRIBUTE_DECLARE(renderable,"out");
		}

		return shader;
	}

	public static String STRUCT_PROPERTIES_DEFINE(){
		ArrayList<String> s = new ArrayList<String>();
		s.add("struct "+LIGHT_STRUCT+"{ \n");
		s.add(ShaderComposer.TAB + "vec3 " +Light.LIGHT_PROP_STRUCT_COLOR + ";\n");
		s.add(ShaderComposer.TAB + "float "+Light.LIGHT_PROP_STRUCT_INTENSITY+ ";\n");
		s.add(ShaderComposer.TAB + "float "+Light.LIGHT_PROP_STRUCT_SL_WIDTH+ ";\n");
		s.add("};\n");
		return FINALIZE(s);
	}

	public static String PROPERTIES_DECLARE(){
		ArrayList<String> s = new ArrayList<String>();
		s.add("uniform " + LIGHT_STRUCT + " " + Light.LABEL_PROP_GLOBAL_LIGHT+MAX_GL_LIGHTS+"");
		s.add("uniform " + LIGHT_STRUCT + " " + Light.LABEL_PROP_SPOT_LIGHT+MAX_SL_LIGHTS+"");
		return ShaderComposer.FORMAT_LINE(StringUtils.TO_ARR(s));
	}


	static String ATTRIBUTE_DECLARE(Renderable rend,String inout){
		if(rend.lightning() != Type.NONE){
			ArrayList<String> s = new ArrayList<String>();

			s.add(inout + " vec3 " + FS_GLOBAL_LIGHT   + MAX_GL_LIGHTS);
			s.add(inout + " vec4 " + FS_SPOT_LIGHT_DIR + MAX_SL_LIGHTS);

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

			declare.add("uniform vec3 " + Light.LABEL_GLOBAL_LIGHT_DIR + MAX_GL_LIGHTS);
			declare.add("uniform vec3 " + Light.LABEL_SPOT_LIGHT_DIR + MAX_SL_LIGHTS);
			declare.add("uniform vec3 " + Light.LABEL_SPOT_LIGHT_POS + MAX_SL_LIGHTS);

			//Shader variables declared 
			sv.spotLight(Light.LABEL_SPOT_LIGHT_POS,ShaderVariables.LIGHT_POS);
			sv.spotLight(Light.LABEL_SPOT_LIGHT_DIR,ShaderVariables.LIGHT_DIR);
			sv.globalLight(Light.LABEL_GLOBAL_LIGHT_DIR);

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
	/**
	 * 
	 * @param sv
	 * @param t
	 * @return
	 */
	static String GetSpotLight(ShaderVariables sv,int t){

		if(t == ShaderVariables.LIGHT_POS){
			return sv.spotLight(ShaderVariables.LIGHT_POS)+"[i]";
		}else if(t == ShaderVariables.LIGHT_DIR){
			return sv.spotLight(ShaderVariables.LIGHT_DIR)+"[i]";			
		}else{
			GLError.exit("Invalid spotlight type");	
		}
		return "";
	}

	static void COPY_GLOBAL(ArrayList<String> s, Renderable rend){

		String dir = "[i]";
		ShaderVariables sv = rend.getShaderVariables();
		String TBN = "";
		if(sv.TBN() != null)
			TBN = sv.TBN()+"*";

		s.add(ShaderComposer.TAB + FS_GLOBAL_LIGHT+"[i] = " +TBN +  sv.globalLight() + dir +";");
	}



	static void COPY_SPOT(ArrayList<String> s,Renderable rend){

		ShaderVariables sv = rend.getShaderVariables();

		String lightPos  = GetSpotLight(sv,ShaderVariables.LIGHT_POS);
		String lightDir  = GetSpotLight(sv,ShaderVariables.LIGHT_DIR);
		String vertexPos = sv.position();

		String TBN = "";
		if(sv.TBN() != null)
			TBN = sv.TBN()+"*";

		String outDir    = FS_SPOT_LIGHT_DIR+"[i]";
		s.add(ShaderComposer.TAB + "vec3 temp = " + lightPos + "-" + vertexPos + ";");
		s.add(ShaderComposer.TAB + outDir + ".xyz ="+TBN + "temp;"); 
		s.add(ShaderComposer.TAB + outDir + ".w = dot(normalize(temp)" + ",-" + lightDir+");"); 
	}

	public static String VS_MAIN(Renderable rend){
		//If light is passing through main 
		if(LIGHT_FS){
			return "";
		}
		ArrayList<String> temp = new ArrayList<String>();
		ShaderVariables sv = rend.getShaderVariables();

		switch(rend.lightning()){

		case PHONG:
		case DIFFUSE:
			//Copy global data to varying variable
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + ";i++){");
			COPY_GLOBAL(temp, rend);
			temp.add("}");

			//Copy spotlight data to varying variable
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + ";i++){");
			COPY_SPOT(temp, rend);
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

	//  Global lights
	static void GlobalDiffuse(ArrayList<String> s,String light){
		String dir = "[i]);";
		s.add(ShaderComposer.TAB + "l = -normalize(" + light + dir);
		s.add(ShaderComposer.TAB + "diffuse += max(dot(n,l),0.0);");
	}
	static void GlobalSpecular(ArrayList<String> s){
		s.add(ShaderComposer.TAB + "r = -reflect(l,n);");
		s.add(ShaderComposer.TAB + "float temp_s = max(dot(r,e),0.0);");
		String color = Light.LABEL_PROP_GLOBAL_LIGHT+"[i]."+Light.LIGHT_PROP_STRUCT_COLOR;
		s.add(ShaderComposer.TAB + "specular +=" + color + "* pow(temp_s,20.0);");
	}

	//Spotlights
	static void SpotLightDiffuse(ArrayList<String> s,ShaderVariables sv){
		String att =  "1.0/(1.0+0.2*length(dir))";

		if(LIGHT_FS){
			String lightPos = GetSpotLight(sv, ShaderVariables.LIGHT_POS);
			String lightDir = GetSpotLight(sv, ShaderVariables.LIGHT_DIR);
			String pos 		= sv.position();
			s.add(ShaderComposer.TAB + "vec3 dir  = " + lightPos +"-" + pos + ";");
			s.add(ShaderComposer.TAB + "l = normalize(dir);");
			s.add(ShaderComposer.TAB + "float att = "+ att+"*dot(-l,"+lightDir+");");
		}else{

			String lightDir = sv.spotLight(ShaderVariables.LIGHT_DIR)+"[i]";
			s.add(ShaderComposer.TAB + "vec3 dir ="+lightDir+".xyz;");
			s.add(ShaderComposer.TAB + "l = normalize("+lightDir+".xyz);");
			s.add(ShaderComposer.TAB + "float att ="+ att+"*"+lightDir+".w;");

		}
		s.add(ShaderComposer.TAB + "att = max(att,0.0);");
		s.add(ShaderComposer.TAB + "diffuse += att*max(dot(n,l),0.0);");
	}

	static void SpotLightSpecular(ArrayList<String> s){
		s.add(ShaderComposer.TAB + "r = -reflect(l,n);");
		s.add(ShaderComposer.TAB + "float temp_s = clamp(dot(r,e),0.0,1.0);");
		String color = Light.LABEL_PROP_SPOT_LIGHT+"[i]."+Light.LIGHT_PROP_STRUCT_COLOR;
		s.add(ShaderComposer.TAB + "specular +=" + color + "*att*pow(temp_s,20.0);");
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
			temp.add("vec4 specular = vec4(0.0);");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + ";i++){");
			GlobalDiffuse(temp, sv.globalLight());
			GlobalSpecular(temp);
			temp.add("}");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + ";i++){");
			SpotLightDiffuse(temp, sv);
			SpotLightSpecular(temp);
			temp.add("}");

			temp.add( sv.color() + "= specular + (diffuse+ambient) *" + sv.color()  + ";");

			break;
		case DIFFUSE:
			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_GLOBAL_LIGHTS + ";i++){");
			GlobalDiffuse(temp, sv.globalLight());
			temp.add("}");

			temp.add("for(int i = 0; i < "  + Light.LABEL_NUM_SPOTLIGHTS + ";i++){");
			SpotLightDiffuse(temp, sv);
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
