package com.lodge.misc;

import android.util.Log;

public class FPS {
	/*************************************************************/
	// FPS stuff
	/*************************************************************/
	long beginTime = 0;		
	long timeDiff;		
	int sleepTime;
	int counter =  0;
	int fpsRate = 200;
	float FPS;
	public void printFps(){
		timeDiff = System.currentTimeMillis() - beginTime;
		float fps = 1000/(timeDiff+1);
		FPS += fps;
		if(counter % fpsRate == 0){
			Log.d("FPS","FPS: " + (int)(FPS/fpsRate));
			counter = 0;
			FPS = 0;
		}
		counter++;
		beginTime = System.currentTimeMillis();
	}
}
