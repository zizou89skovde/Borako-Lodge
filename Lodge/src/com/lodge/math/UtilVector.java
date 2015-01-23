package com.lodge.math;

import com.lodge.err.GLError;

import android.opengl.Matrix;
public class UtilVector {


	public static int XY 	= 1;
	public static int XZ 	= 2;
	public static int YZ 	= 3;
	public static int XYZ 	= 4;


	public static void copyArray(float[] src, float[] dst){
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i];
		}
	}
	
	public static float[] mat42Mat3(float[] Matrix4){
		float[] Mat3 =  new float[]{ Matrix4[0],Matrix4[1],Matrix4[2],
				Matrix4[4],Matrix4[5],Matrix4[6],
				Matrix4[8],Matrix4[9],Matrix4[10],
		};
		return Mat3;
	}
	
	public static float getLength(float[] vec, int dim){
		if (dim == 1)
			return  (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[1],2));
		if (dim == 2)
			return  (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[2],2));
		if (dim == 3)
			return  (float)Math.sqrt(Math.pow(vec[1],2)+Math.pow(vec[2],2));
		if (dim == 4)
			return  (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[1],2)+Math.pow(vec[2],2));

		return -1;
	}

	public static float getLength(float[] endvec,float[] startvec, int dim) {
		float[] temp = endvec.clone();
		for (int i = 0; i < temp.length; i++) {
			temp[i] -= startvec[i];
		}
		return getLength(temp, dim);
	}

	public static float getLength(double[] vec, int dim){
		float length = 0;
		if (dim == 1)
			length =   (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[1],2));
		if (dim == 2)
			length =  (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[2],2));
		if (dim == 3)
			length = (float)Math.sqrt(Math.pow(vec[1],2)+Math.pow(vec[2],2));
		if (dim == 4)
			length =  (float)Math.sqrt(Math.pow(vec[0],2)+Math.pow(vec[1],2)+Math.pow(vec[2],2));

		return length;
	}

	public static void vectorAdd(float[] dstvec, float[] termvec){
		for (int i = 0; i < dstvec.length; i++) {
			dstvec[i] += termvec[i];
		}
	}

	public static float[] vectorAddCpy(float[] vec1, float[] vec2){
		float[] res =  new float[vec1.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = vec1[i] + vec2[i];
		}
		return res;
	}

	public static double[] vectorAdd(double[] termvec1, double[] termvec2){
		double[] res = new double[termvec1.length]; 
		for (int i = 0; i < termvec1.length; i++) {
			res[i] =  termvec1[i] + termvec2[i];
		}
		return res;
	}
	public static void vectorAdd(float[] dstvec, float term){
		for (int i = 0; i < dstvec.length; i++) {
			dstvec[i] += term;
		}
	}

	public static void vectorSub(float[] dstvec,float[] termvec){
		for (int i = 0; i < dstvec.length; i++) {
			dstvec[i] -= termvec[i];
		}
	}

	public static float[] vectorSubCpy(float[] vec1,float[] vec2){
		float[] res = new float[vec1.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = vec1[i] - vec2[i];
		}
		return res;
	}

	public static double[] vectorSub2(double[] vec1,double[] vec2){
		double[] dst = new double[vec1.length];
		for (int i = 0; i < dst.length; i++) {
			dst[i] = vec1[i] - vec2[i];
		}
		return dst;
	}


	public static void vectorMult(float[] dstvec, float factor){
		for (int i = 0; i < dstvec.length; i++) {
			dstvec[i] *= factor;
		}
	}

	public static float[] vectorMultCpy(float[] dstvec, float factor){
		float[] temp = new float[3];
		for (int i = 0; i < dstvec.length; i++) {
			temp[i] = dstvec[i] * factor;
		}
		return temp;
	}
	public static double[] vectorMultCpy(double[] vector,double factor) {
		double[] temp = new double[3];
		for (int i = 0; i < vector.length; i++) {
			temp[i] = (float)vector[i]*(float)factor;
		}
		return temp;
	}

	public static float[] vectorMultConversion(double[] vector,double factor) {
		float[] temp = new float[3];
		for (int i = 0; i < vector.length; i++) {
			temp[i] = (float)vector[i]*(float)factor;
		}
		return temp;
	}

	public static float[] vectorMultCpy(float[] vector, float[] factor){
		float[] temp = new float[vector.length];
		for (int i = 0; i < vector.length; i++) {
			temp[i] = vector[i]*factor[i];
		}
		return temp;
	}

	public static void vectorMult1(float[] vector, float[] factor){
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i]*factor[i];
		}
	}

	public static void normalize(float[] vector){

		int l = vector.length;
		float sum = 0;
		for (int i = 0; i < l; i++) {
			sum += vector[i]*vector[i];
		}
		float length = (float) Math.sqrt(sum);
		if(length == 0){
			vector =  new float[3];
			return;
		}
		
		for (int i = 0; i < l; i++) {
			vector[i] /= length;
		}

		/*float length =  Matrix.length(vector[0], vector[1], vector[2]);
		vector[0] = vector[0]/length;
		vector[1] = vector[1]/length;
		vector[2] = vector[2]/length;*/

	}

	public static void normalize2(float[] vector){

		float length =  (float) Math.sqrt(Math.pow(vector[0],2) + Math.pow(vector[1],2));
		vector[0] = vector[0]/length;
		vector[1] = vector[1]/length;

	}
	public static void normalize(double[] vector){

		float length =  Matrix.length((float)vector[0], (float)vector[1], (float)vector[2]);
		vector[0] = vector[0]/length;
		vector[1] = vector[1]/length;
		vector[2] = vector[2]/length;

	}

	public static float[] normalize(float[] vector,float length){
		float[] temp = new float[3];
		temp[0] = vector[0]/length;
		temp[1] = vector[1]/length;
		temp[2] = vector[2]/length;
		return temp;
	}
	public static float[] normalize(double[] vector,float length){
		float[] temp = new float[3];
		temp[0] = (float)vector[0]/length;
		temp[1] = (float)vector[1]/length;
		temp[2] = (float)vector[2]/length;
		return temp;
	}


	public static  float[] getDirection(float[] startvec, float[] endvec, boolean normalized){
		float[] temp = new float[3];
		temp[0] = endvec[0] - startvec[0];
		temp[1] = endvec[1] - startvec[1];
		temp[2] = endvec[2] - startvec[2];
		if(normalized){
			normalize(temp);
		}
		return temp;
	}

	public static  float[] getDirection(float[] startvec, float[] endvec){
		float[] temp = new float[3];

		temp = getDirection(startvec, endvec, false);

		float lengthXYZ   =  getLength(temp, XYZ);
		float lengthXY	  =  getLength(temp, XY);

		float XY_angle = (float)Math.atan2(temp[1], temp[0]);
		float Z_angle  = (float)Math.atan2(temp[2], lengthXY);

		temp[0] = XY_angle;
		temp[1] = Z_angle;
		temp[2] = lengthXYZ;

		return temp;
	}

	public static  float[] getDirection(double[] vec){
		float[] temp = new float[2];
		float lengthXY	  =  getLength(vec, XY);

		float XY_angle = (float)Math.atan2(vec[1], vec[0]);
		float Z_angle  = (float)Math.atan2(vec[2], lengthXY);

		temp[0] = XY_angle;
		temp[1] = Z_angle;

		return temp;
	}

	public static  float[] getDirection(float[] vec){
		float[] temp = new float[2];
		float lengthXY	  =  getLength(vec, XY);

		float XY_angle = (float)Math.atan2(vec[1], vec[0]);
		float Z_angle  = (float)Math.atan2(vec[2], lengthXY);

		temp[0] = XY_angle;
		temp[1] = Z_angle;

		return temp;
	}

	public static float[] cross(float[] vec1, float[] vec2) {
		float[] temp = new float[3];
		temp[0] = vec1[1]*vec2[2] - vec1[2]*vec2[1];
		temp[1] = vec1[2]*vec2[0] - vec1[0]*vec2[2];
		temp[2] = vec1[0]*vec2[1] - vec1[1]*vec2[0];

		return temp;

	}

	public static double dotProduct(double[] normalizedVelocity, double[] groundNormals) {
		double res = 0;
		for (int i = 0; i < groundNormals.length; i++) {
			res += normalizedVelocity[i]*groundNormals[i];
		}
		return res;
	}
	public static float dotProduct(float[] vec1, float[] vec2) {
		float res = 0;
		for (int i = 0; i < vec1.length; i++) {
			res += vec1[i]*vec2[i];
		}
		return res;
	}

	public static float[] reflect(float[] I , float[] N ){
		float factor = 2*UtilVector.dotProduct(I,N);
		float[] R = UtilVector.vectorSubCpy(UtilVector.vectorMultCpy( N , factor),I );
		return R;
	}
	public static double[] reflect(double[] I , double[] N ){
		double[] L =  UtilVector.vectorMultCpy(I,-1);
		double factor = 2*UtilVector.dotProduct(L,N);
		double[] R = UtilVector.vectorSub2(L,UtilVector.vectorMultCpy( N , factor) );
		return R;
	}

	public static int append(float[] dst, float src[], int off){
		for (int i = 0; i < src.length; i++) {
			dst[off+i] = src[i];
		}
		return off+src.length;
	}

	public static float[] expand(float[] v, int exp, float val) {
		if(v.length > exp)
			GLError.exit("Vector: expand error size " );
		
		float[] res = new float[exp];
		
		for (int i = 0; i < v.length; i++) {
			res[i] = v[i];
		}
		for (int i = 0; i < exp; i++) {
			res[v.length + i] = val;
		}
		
		return res;
		
		
	}







}

