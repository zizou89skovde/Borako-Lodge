package com.lodge.math;

public class Vec4 extends LVec{

	public Vec4(float s) {
		mData = new float[]{s,s,s,s};
		mSize = 4;
	}

	public Vec4(float[] d, int size) {
		mData = d.clone();
		mSize = size;
	}

	@Override
	public void mult(LMat m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mult(LVec v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(LVec v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sub(LVec v) {
		// TODO Auto-generated method stub
		
	}

}
