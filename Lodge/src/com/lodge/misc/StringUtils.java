package com.lodge.misc;

import java.util.ArrayList;

import com.lodge.err.GLError;

public class StringUtils {


	static public String[] STR_SET(int len,String s){
		String[] sa = new String[len];
		STR_SET(sa,s);
		return sa;
	}

	static public void STR_SET(String[] sa, String s){
		for (int i = 0; i < sa.length; i++) {
			sa[i] = s; 
		}
	}
	
	static public void APPEND(ArrayList<String> list, String[] s){
		int l = s.length;
		
		for (int i = 0; i < l; i++) {
			list.add(s[i]);
		}
		
	}
	static public void APPEND(ArrayList<String> list, String s){
			list.add(s);
	}
	
	
	static public String [] APPEND(String[] s1, String[] s2){
		int l1 = s1.length;
		int l2 = s2.length;
		int l = l1 + l2;
		String[] out = new String[l];
		
		int index = 0;
		for(int i = 0; i < l1; i ++){
			out[index++] = s1[i]; 
		}
		

		for(int i = 0; i < l1; i ++){
			out[index++] = s2[i]; 
		}
		
		return out;
		
	}
	
	static public String ARR2STR(String[] sa){
		
		String s="";
		for (int i = 0; i < sa.length; i++) {
			s+=sa[i];
		}
		return s;
	}


	static public String[] CONCAT(String[] s1, String[] s2){
		int len1 = s1.length;
		int len2 = s2.length;

		if(len1 != len2)
			GLError.exit("CONCAT: Inconsistent lengths s1: " + String.valueOf(len1) + " s2: " + String.valueOf(len2));

		String[] out = new String[len1];

		for (int i = 0; i < len1; i++) {
			out[i] = s1[i] + s2[i];
		}
		return out;
	}

}
