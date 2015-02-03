package com.lodge.math;

import com.lodge.err.GLError;

public class Vec3 extends LVec {

	public Vec3(float s) {
		mData = new float[]{s,s,s};
		mSize = 3;
	}

	public Vec3(float[] d, int size) {
		mData = d.clone();
		mSize = size;
	}

	public Vec3(LVec v) {
		if(v.size() < 3)
			GLError.exit("Vec3 constr. : invalid size ");
		mData = new float[3];
		for (int i = 0; i < 3; i++) {
			mData[i] = v.at(i);
		}
	
		mSize = 3;
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
