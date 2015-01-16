package com.lodge.ui;

import com.lodge.math.UtilVector;
import android.view.MotionEvent;


public class UserInput {
	int mNumFingers = -1; 
	float[] mScreenCoords =null;
	float[] mPreviousScreenCoords = null;
	int mTouchEvent = -1; 
	float[] mDelta = null;
	long mPreviousTime;
	long mDeltaTime;
	boolean mMove = false;
	boolean mTwoFingerLock = false;

	private void resetData(){
		mNumFingers = -1; 
		mScreenCoords =null;
		mPreviousScreenCoords = null;
		mTouchEvent = -1;
		mDelta = null;
		mPreviousTime = 0;
		mTwoFingerLock = false;
		mMove= false;;

	}

	public void setNewData(int fingers,float[] coords,int event, long downTime){

		if(mScreenCoords !=null){
			mPreviousScreenCoords = mScreenCoords;
			mDelta = UtilVector.vectorSubCpy(coords, mPreviousScreenCoords);
			mDeltaTime = System.currentTimeMillis() - mPreviousTime;
		
		}
		mPreviousTime = System.currentTimeMillis();
		mScreenCoords = coords;
		mTouchEvent = event;
		mNumFingers = fingers;
		if(mNumFingers == 2)
			mTwoFingerLock = true;

		handleEvent();

	}

	private void handleEvent(){
		if(mTouchEvent == MotionEvent.ACTION_UP && mNumFingers == 1){
			resetData();
		}
		if(mTouchEvent == MotionEvent.ACTION_POINTER_DOWN){
			mMove = true;
		}

	}

	public boolean isMoving(){
		return mNumFingers == 2;
	}
	public float[] getDeltaCamRotation(){
		if(!mTwoFingerLock)
			return mDelta;
		else
			return null;
	}



}
