package com.lodge.math;

import com.lodge.err.GLError;

public abstract class LVec {

		final static float eps = 0.0001f;
		float[] mData;
		int 	mSize;
	
		
		static LVec create(float[] d, int size){
			LVec v = null;
			if(size == 3)
				v = new Vec3(d,size);
			else if(size == 4)
				v = new Vec4(d,size);
			else 
				GLError.exit("Vector : unsupported lengts");
			
			return v;
		}
		
		public LVec clone(){
			return create(mData,size());
		}
		
		private void checkErrors(){
			if(mData == null)
				GLError.exit(this.getClass().getSimpleName() + ": Data is null");
		}
		
		abstract public void mult(LMat m);
		/**
		 * Vector element-wise multiplication. Will throws error if size are inconsistent.
		 * @param v
		 */
		abstract public void mult(LVec v);
		
		/**
		 * Vector element-wise addition. Will throws error if size are inconsistent.
		 * @param v
		 */
		abstract public void add(LVec v);
		
		/**
		 * Vector element-wise subtraction. Will throws error if size are inconsistent.
		 * @param v
		 */
		abstract public void sub(LVec v);
		
		/**
		 * Normalize vector
		 */
		public void normalize(){
			checkErrors();
			
			float len = length();
			if(len < eps)
				GLError.exit("Vector normalize: lenght ~0");
			
			for (int i = 0; i < mSize; i++) {
				mData[i] /= len;
			}
			
		}
		
		/**
		 * Return length of vector
		 * @return
		 */
		public float length(){
			return (float) Math.sqrt(dot(this));
		}
		
		/**
		 * Dot product
		 * @param v
		 * @return
		 */
		public float dot(LVec v){
			checkErrors();
			
			if(mSize != v.size())
				GLError.exit("Vector: inconsistent sizes");
			
			float prod = 0.0f;
			for (int i = 0; i < mSize; i++) {
				prod += mData[i] * v.at(i);
			}
			return prod;
		}
		/**
		 * Return size/length of the vector
		 * @return
		 */
		public int size(){
			return mSize;
		}
		/**
		 * Return value at index 
		 * @param idx index
		 * @return
		 */
		public float at(int idx){
			checkErrors();
			if(idx >= mSize)
				GLError.exit("Vector value: invalid index");
			return mData[idx];
		}
		
		/**
		 * 
		 * @param v
		 * @return
		 */
		final public LVec cross(LVec v2){
			LVec v1 = this;
			float[] temp = new float[mSize];
			temp[0] = v1.at(1)*v2.at(2) - v1.at(2)*v2.at(1);
			temp[1] = v1.at(2)*v2.at(0) - v1.at(0)*v2.at(2);
			temp[2] = v1.at(0)*v2.at(1) - v1.at(1)*v2.at(0);

			return LVec.create(temp, mSize);
		}
		
		public float[] data(){
			return mData.clone();
		}
		
		void set(float[] d){
			if(d.length != mSize)
				GLError.exit("Vector set: inconsistent lengths");
			for (int i = 0; i < d.length; i++) {
				mData[i] = d[i];
			}
		}
		
		public float x(){
			return mData[0];
		}
		
		public float y(){
			return mData[1];
		}
		
		public float z(){
			return mData[2];
		}
		
		public static Vec3 CrossProduct(LVec v1,LVec v2){
			LVec vres = v1.clone();
			vres.cross(v2);
			Vec3 out = ToVec3(vres);
			return out;
		}
		
		public static Vec3 Normalize(LVec v1){
			LVec vres = v1.clone();
			vres.normalize();
			return ToVec3(vres);
		}
		
		static Vec3 ToVec3(LVec v){
			if(v.getClass() != Vec3.class){
				return new Vec3(v);
			}
			return (Vec3) v;
		}
	
}
