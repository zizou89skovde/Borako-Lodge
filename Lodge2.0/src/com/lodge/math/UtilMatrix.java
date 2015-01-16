package com.lodge.math;

import android.util.Log;

public class UtilMatrix {

	
static public float[] InverseTranspose(float[] in)
	{
		float a11, a12, a13, a21, a22, a23, a31, a32, a33;
		float[] out = new float[16];
		
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
}
