package com.lodge.math;

import android.util.Log;

public class UtilMatrix {


	static public float[] InverseTranspose(float[] in)
	{
		float a11, a12, a13, a21, a22, a23, a31, a32, a33;
		float[] out = new float[9];

		// Copying to internal variables
		a11 = in[0];
		a12 = in[1];
		a13 = in[2];
		a21 = in[4];
		a22 = in[5];
		a23 = in[6];
		a31 = in[8];
		a32 = in[9];
		a33 = in[10];
		float DET = a11*(a33*a22-a32*a23)-a21*(a33*a12-a32*a13)+a31*(a23*a12-a22*a13);
		if (DET != 0)
		{
			out[0] = (a33*a22-a32*a23)/DET;
			out[3] = -(a33*a12-a32*a13)/DET;
			out[6] = (a23*a12-a22*a13)/DET;
			out[1] = -(a33*a21-a31*a23)/DET;
			out[4] = (a33*a11-a31*a13)/DET;
			out[7] = -(a23*a11-a21*a13)/DET;
			out[2] = (a32*a21-a31*a22)/DET;
			out[5] = -(a32*a11-a31*a12)/DET;
			out[8] = (a22*a11-a21*a12)/DET;
		}
		else
		{
			Log.e("GL_ERROR","Failed inverse transpose");
		}

		return out;
	}

	public static float[] M3V3(float[] m, float[] v) {
		float[] res = new float[3];
		int o = 0;
		for(int i = 0; i < 3; i ++){
			res[i] = m[o]*v[0] + m[o+1]*v[1] + m[o+2]*v[2];
			o+=3;
//			res[i] = m[o]*v[0] + m[o+3]*v[1] + m[o+6]*v[2];
//			o+=1;
		}
		return res;
	}

	public static float[] M4V4(float[] m, float[] v) {
		float[] res = new float[4];
		int o = 0;
		for(int i = 0; i < 4; i ++){
			res[i] = m[o]*v[0] + m[o+1]*v[1] + m[o+2]*v[2] + + m[o+3]*v[3];
			o+=4;
			
//			res[i] = m[o]*v[0] + m[o+4]*v[1] + m[o+8]*v[2] + + m[o+12]*v[3];
//			o+=1;
		}
		return res;
	}
	
	public static float[] M4ToM3(float[] m4){
		float[] m3 = new float[9];
		int j = 0;
		for (int i = 0; i < 12; i+=4) {
			m3[j++] = m4[i];
			m3[j++] = m4[i+1];
			m3[j++] = m4[i+2];
		}
		return m3;
	}
}
