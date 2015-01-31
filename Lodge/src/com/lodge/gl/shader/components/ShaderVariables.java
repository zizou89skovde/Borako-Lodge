package com.lodge.gl.shader.components;

public class ShaderVariables {
	
		private String mAttributePosition 	  	= null;
		private String mAttributeNormal 	  	= null;
		private String mAttributeTextureCoord 	= null;
		private String[] mAttributeSpotLight    = null;
		private String mAttributeGlobalLight    = null;
		private String mOutColor 				= null;
		private String mTBN 					= null;
		
		public static int LIGHT_DIR = 0;
		public static int LIGHT_POS = 1;
		
		public String position(){
			return mAttributePosition;
		}
		public void position(String s){
			mAttributePosition = s;
		}
		
		public String normal(){
			return mAttributeNormal;
		}
		public void normal(String s){
			mAttributeNormal = s;
		}
		
		public String texcoord(){
			return mAttributeTextureCoord;
		}
		
		public void texcoord(String s){
			mAttributeTextureCoord = s;
		}
	
		public void color(String s){
			mOutColor = s;
		}
		
		public String color(){
			return mOutColor;
		}
		
		public String[] spotLight(){
			return mAttributeSpotLight;
		}
		
		public String spotLight(int type){
			return mAttributeSpotLight[type];
		}
		
		
		public void spotLight(String s, int type){
			if(mAttributeSpotLight == null)
				mAttributeSpotLight = new String[2];
			mAttributeSpotLight[type] = s;
		}
		
		public String globalLight(){
			return mAttributeGlobalLight;
		}
		
		public void globalLight(String s){
			mAttributeGlobalLight = s;
		}
		public void TBN(String s) {
			mTBN = s;
		}
		public String TBN(){
			return mTBN;
		}
		
		

}
