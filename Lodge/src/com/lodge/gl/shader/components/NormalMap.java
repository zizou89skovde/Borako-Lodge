package com.lodge.gl.shader.components;

import java.util.ArrayList;

import com.lodge.gl.Renderable;

public class NormalMap {

	static String TRANSFORM_MATRIX = "TBN";
	
	static String VS_MAIN(Renderable r){
		String shader = "";
		if(Lightning.LIGHT_FS)
			return shader;
		
		ShaderVariables sv = r.getShaderVariables();
		if(r.hasNormalMap()){
			ArrayList<String> s = new ArrayList<String>();
			s.add("vec3 n	= " + sv.normal()+ ")"); 
			s.add("vec3 b	= cross(" + sv.normal() +" vec3(0.0,0.0,1.0))");
			s.add("vec3 t	= cross(b,n)"); 
			s.add("b		= cross(n,t)");
			s.add("mat3 TBN = transpose(mat3(t,b,n))");
			
			sv.TBN(TRANSFORM_MATRIX);
		}
		return shader;
		
	}
	
	static String FS_MAIN(Renderable r){
		String shader = "";
		if(Lightning.LIGHT_FS)
			return shader;
		
		ShaderVariables sv = r.getShaderVariables();
		if(r.hasNormalMap()){
			ArrayList<String> s = new ArrayList<String>();
			s.add("vec3 n	= " + sv.normal()+ ")"); 
			s.add("vec3 b	= cross(" + sv.normal() +" vec3(0.0,0.0,1.0))");
			s.add("vec3 t	= cross(b,n)"); 
			s.add("b		= cross(n,t)");
			s.add("mat3 TBN = transpose(mat3(t,b,n))");
			
			sv.TBN(TRANSFORM_MATRIX);
		}
		return shader;
		
	}
}
