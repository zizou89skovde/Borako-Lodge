package com.lodge.math;

import android.opengl.Matrix;

import com.lodge.err.GLError;

public abstract class LMat {


	float[][] mData;
	int 	mRows;
	int     mCols;
	int 	mSize;

	static boolean mTranposed = false;

	/**
	 * Clone LMat instance
	 */
	public LMat clone(){
		LMat c = create(mRows);
		c.mCols = this.mCols;
		c.mRows = this.mRows;
		c.mData = new float[mRows][mCols];
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < mCols; j++) {
				c.mData[i][j] = this.mData[i][j];
			}

		}
		return c;
	}


	final static LMat create(int r){
		if(r == 3)
			return new Mat4();
		else
			return new Mat3();
	}

	final public void identity(){

		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < mCols; j++) {
				if(i == j)
					mData[i][j] = 1;
				else
					mData[i][j] = 0;
			}

		}
	}

	/**
	 * Matrix vector multiplication. Will throw error if dimensions are not consistent.
	 * @param v - Vector 
	 */
	abstract public void mult(LVec v);

	/**
	 *Matrix matrix multiplication. Will throw error if dimensions are not consistent. 
	 * @param m Matrix
	 */
	final public void mult(LMat m){
		if(mRows == 4 && mCols == 4 && m.rows() == 4 && m.cols()==4){
			float[] res  = new float[16];  
			Matrix.multiplyMM(res, 0, data(), 0, m.data(),0);
			setData(res, mRows, mCols);
		}else{
			float[][] res = new float[mRows][mCols];
			for (int i = 0; i < mRows; i++) {
				for (int j = 0; j < mCols; j++) {
					for (int n = 0; n < mCols; n++) 
						if(mTranposed)
							res[i][j] +=  at(i,n) * m.at(n, j);
						else
							res[i][j] +=  at(n,i) * m.at(j, n); 
				}
			}
		}
	}

	/**
	 *Matrix matrix element-wise addition. Will throw error if dimensions are not consistent. 
	 * @param m Matrix
	 */
	abstract public void add(LMat m);

	/**
	 *Matrix matrix element-wise subtraction. Will throw error if dimensions are not consistent. 
	 * @param m Matrix
	 */
	abstract public void sub(LMat m);

	/**
	 * Inverse transpose of matrix. Will throw error if matrix are not inverterbar----
	 */
	abstract public void inverseTranspose();

	/**
	 * Transpose of matrix. 
	 */
	final public void transpose(){
		float[] d = data();
		float[] r = new float[d.length];
		if(mRows == 3 && mCols == 3){

			r[0] = d[0]; r[3] = d[1]; r[6] = d[2];
			r[1] = d[3]; r[4] = d[4]; r[7] = d[5];
			r[2] = d[6]; r[5] = d[7]; r[8] = d[8];

			setData(r, mRows, mCols);

		}else if(mRows == 4 && mCols == 4){

			r[0] = d[0];  r[4] = d[1];  r[8]  = d[2];  r[12] = d[3];
			r[1] = d[4];  r[5] = d[5];  r[9]  = d[6];  r[13] = d[7];
			r[2] = d[8];  r[6] = d[9];  r[10] = d[10]; r[14] = d[11];
			r[3] = d[12]; r[7] = d[13]; r[11] = d[14]; r[15] = d[15];

			setData(r, mRows, mCols);
		}else{
			GLError.exit("Matrix transpose: Unsupported format");
		}
	}

	/**
	 * Scale matrix.
	 * @param V
	 */
	public void scale(LVec v){
		mData[0][0] = v.at(0);
		mData[1][1] = v.at(1);
		mData[2][2] = v.at(2);

		if(mRows > 3){
			if(v.size() < 4)
				GLError.exit("Matrix scale. Scaling vector too small");
			mData[3][3] = v.at(3);
		}
	}

	/**
	 * Uniform scaling of matrix
	 * @param s scale
	 */
	public void scale(float s){
		LVec v = null;
		if(mRows == 3)
			v = new Vec3(s);
		else if(mRows == 4)
			v = new Vec4(s);
		scale(v);
	}


	final public void rotate(LVec V){
		rotateX(V.at(0));
		rotateY(V.at(1));
		rotateZ(V.at(2));
	}



	final public void arbRotate(Vec3 axis, float a){
		Vec3 x = (Vec3) axis.clone();
		Vec3 y = (Vec3) axis.clone();
		Vec3 z = (Vec3) axis.clone();

		Mat4 R     =  new Mat4();
		Mat4 Rt    = new Mat4();
		Mat4 Raxel = new Mat4();
		Mat4 m 	   = new Mat4();

		// Check if parallel to Z
		if (axis.x() < 0.0000001) // Below some small value
			if (axis.x() > -0.0000001)
				if (axis.y() < 0.0000001)
					if (axis.y() > -0.0000001)
					{
						if (axis.z() > 0)
						{
							rotateZ(a);
							return;
						}
						else
						{
							rotateZ(-a);
							return;
						}
					}

		x.normalize();
		z.set(new float[]{0,0,1});
		y = LVec.Normalize(LVec.CrossProduct(z, x)); // y' = z^ x x'
		z = LVec.CrossProduct(x, y); // z' = x x y


		R.mData[0][0] = x.x(); R.mData[1][0] = x.y(); R.mData[2][0] = x.z();  R.mData[3][0] = 0.0f;
		R.mData[0][1] = y.x(); R.mData[1][1] = y.y(); R.mData[2][1] = y.z();  R.mData[3][1] = 0.0f;
		R.mData[0][2] = z.x(); R.mData[1][2] = z.y(); R.mData[2][2]  =z.z();  R.mData[3][2] = 0.0f;
		R.mData[0][3] = 0;     R.mData[1][3] = 0;     R.mData[2][3]  =0;      R.mData[3][3] = 0.0f;
		
		if (!mTranposed)
			R.transpose();


		Rt = (Mat4) Transpose(R); 

		Raxel = (Mat4) RotateX(a); // Rotate around x axis

		// m := Rt * Rx * R
		m = (Mat4) Mult(Mult(Rt, Raxel), R);
		setData(m.data(),m.rows(),m.cols());
		
	}

	final LMat Mult(LMat m1, LMat m2){
		LMat out = m1.clone();
		out.mult(m2);
		return out;
	}

	final public LMat RotateX(float a){
		LMat m = this.clone();
		m.rotateX(a);
		return m;
	}

	final public LMat RotateY(float a){
		LMat m = this.clone();
		m.rotateY(a);
		return m;
	}

	final public LMat RotateZ(float a){
		LMat m = this.clone();
		m.rotateZ(a);
		return m;
	}



	final public void rotateX(float a){

		LMat m = this.clone();
		m.identity();
		m.mData[2][2]  = (float)Math.cos(a);
		if (mTranposed)
			mData[3][2] = (float)-Math.sin(a);
		else
			mData[3][2] = (float)Math.sin(a);

		mData[2][3] = -mData[3][2];
		mData[3][3] = m.mData[2][2]; 

		mult(m);
	}

	final public void rotateY(float a){
		LMat m = this.clone();
		m.identity();
		m.mData[0][0]  = (float)Math.cos(a);
		if (mTranposed)
			mData[3][1] = (float)-Math.sin(a);
		else
			mData[3][1] = (float)Math.sin(a);

		mData[1][3] = -mData[3][1];
		mData[3][3] = m.mData[0][0]; 

		mult(m);
	}

	final public void rotateZ(float a){
		LMat m = this.clone();
		m.identity();
		m.mData[0][0]  = (float)Math.cos(a);
		if (mTranposed)
			mData[2][1] = (float)-Math.sin(a);
		else
			mData[2][1] = (float)Math.sin(a);

		mData[1][2] = -mData[3][1];
		mData[2][2] = m.mData[0][0]; 

		mult(m);
	}

	public void translate(Vec3 v){
		if(mCols < 4)
			GLError.exit("Invalid Matrix translation");


		mData[0][4] = v.at(0);
		mData[1][4] = v.at(1);
		mData[2][4] = v.at(2);

	}

	public int rows(){
		return mRows;
	}

	public int cols(){
		return mRows;
	}

	public float[] data(){
		float f[] = new float[mRows*mCols];
		int n = 0;
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < mCols; j++) {
				f[n++] = mData[i][j];
			}
		}
		return f;
	}

	private void setData(float[] d, int r,int c){
		if(r != mRows || c != mCols)
			GLError.exit("Matrix set data: inconsistent dim");
		int n = 0;
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				mData[i][j] = d[n++];
			}

		}
	}

	static LMat Transpose(LMat m){
		LMat out = m.clone();
		out.transpose();
		return out;
	}
		

	float at(int ir, int ic){
		if(ir >= mRows || ic >= mCols)
			GLError.exit("Matrix at : invalid indices");

		return mData[ir][ic];
	}
	/*
	private void set(double val, int i, int j) {
		if(i >= mRows || i >=  mCols)
			GLError.exit("Matrix set: invalid indices");
		mData[i][j] = (float)val;
	}
	private void set(float val, int i, int j) {
		if(i >= mRows || i >=  mCols)
			GLError.exit("Matrix set: invalid indices");
		mData[i][j] = val;
	}

	 */
}
