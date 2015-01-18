package com.lodge.gl.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.Resources;



public class ModelLoader {



	
	public static ArrayList<ModelData> getModelDataSingleThread(int modelId, Resources res) {
		InputStream ins = res.openRawResource(modelId);
		final ArrayList<ModelData> mModelDataList = new ArrayList<ModelData>();
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader buf;
		ModelData newModelData;
		int size = -1;
		try {

			buf = new BufferedReader(isr);
			String line;
			while ((line = buf.readLine()) != null) {
				if(line.equals(new String("p"))){
					newModelData  = new ModelData();
					line = buf.readLine();
					newModelData.label = line;
					mModelDataList.add(newModelData);
					newModelData.hasTexture = false;
					size++;
				}
				if(line.equals(new String("v"))){
					final String tempLine = buf.readLine();				
					float[] temp = getFloatArrays(tempLine);
					mModelDataList.get(size).positionArray  = temp;	 
				} 	else if(line.equals(new String("n"))){
					final String tempLine = buf.readLine();				
					float[] temp = getFloatArrays(tempLine);
					mModelDataList.get(size).normalArray = temp;
				} else if(line.equals(new String("i"))){
					final String tempLine = buf.readLine();
					short[] temp = getShortArrays(tempLine);
					mModelDataList.get(size).indexArray = temp;
				}else if(line.equals(new String("t"))){
					final String tempLine = buf.readLine();
					mModelDataList.get(size).hasTexture = true;
					float[] temp = getTextureArrays(tempLine);
					mModelDataList.get(size).textureCoordArray = temp;
				}
			}
			buf.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		return mModelDataList;
	}


	static float[]  getFloatArrays(String ins){
		int size = 0;
		size = ins.length();
		float[] temp = new float[(int)(size/6)];
		float sign = 1;
		int ptr = 0;
		int currentVal;
		int currentdecimalHigh = 0;
		int currentdecimalLow = 0;

		int[] valueBufferLow = new int[20];
		int[] valueBufferHigh = new int[20];

		for (int p= 0; p < size; p++) {

			currentVal=(int)ins.charAt(p);
			if(currentVal == 45  ){
				sign = -1;
				p++;
				currentVal=(int)ins.charAt(p);
			}

			while(currentVal != 46  && p < size) {
				valueBufferHigh[currentdecimalHigh] = currentVal;
				currentdecimalHigh++;
				p++;
				currentVal=(int)ins.charAt(p);
			}
			p++;
			currentVal=(int)ins.charAt(p);

			while(currentVal != 44 && p < size-1){

				valueBufferLow[currentdecimalLow] = currentVal;
				currentdecimalLow++;
				p++;
				currentVal=(int)ins.charAt(p);

			}

			currentdecimalHigh--;
			currentdecimalLow--;

			for (int i = 0; i <= currentdecimalHigh; i++) {
				temp[ptr]  += Math.pow(10,currentdecimalHigh-i)*(float)Character.getNumericValue(valueBufferHigh[i]);
			}
			for (int i = 0; i <= currentdecimalLow; i++) {
				temp[ptr]  += Math.pow(10,-1-i)*(float) Character.getNumericValue(valueBufferLow[i]);
			}
			temp[ptr] *= sign;
			sign = 1;
			currentdecimalHigh = 0;
			currentdecimalLow  = 0;
			ptr++;
		}

		float[] finalFloat= new float[ptr];
		System.arraycopy(temp, 0, finalFloat, 0, ptr);
		return finalFloat;

	}

	static short[] getShortArrays(String ins){

		int size = 0;
		size = ins.length();


		short[] temp = new short[size];

		int ptr = 0;
		int currentdecimal = 0;
		int currentVal;
		int[] valueBuffer = new int[15];

		for (int p= 0; p < size; p++) {

			currentVal=(int)ins.charAt(p);

			while(currentVal != 44 && p < (size - 1) ){

				valueBuffer[currentdecimal] = currentVal;
				currentdecimal++;
				p++;
				currentVal=(int)ins.charAt(p);

			}

			if(p >= (size - 1)){
				valueBuffer[currentdecimal] = currentVal;
				currentdecimal++;
			}

			currentdecimal--;
			for (int i = 0; i <= currentdecimal; i++) {
				temp[ptr]  += Math.pow(10,currentdecimal-i)*(short) Character.getNumericValue(valueBuffer[i]);
			}
			currentdecimal = 0;
			ptr++;
		}

		short[] finalShort = new short[ptr];
		System.arraycopy(temp, 0, finalShort, 0, ptr);
		return finalShort;
	}

	static float[] getTextureArrays(String ins){
		int size = 0;

		size = ins.length();


		float[] temp = new float[(int)(size/4)];
		int ptr = 0;
		int currentVal;
		int currentdecimalHigh = 0;
		int currentdecimalLow = 0;

		int[] valueBufferLow = new int[20];
		int[] valueBufferHigh = new int[20];

		for (int p= 0; p < size; p++) {

			currentVal=(int)ins.charAt(p);

			while(currentVal != 46  && p < size-1) {
				valueBufferHigh[currentdecimalHigh] = currentVal;
				currentdecimalHigh++;
				p++;
				currentVal=(int)ins.charAt(p);

			}
			currentdecimalHigh--;
			p++;
			currentVal=(int)ins.charAt(p);
			while(currentVal != 44 && p < size-1){

				valueBufferLow[currentdecimalLow] = currentVal;
				currentdecimalLow++;
				p++;
				currentVal=(int)ins.charAt(p);

			}
			currentdecimalLow--;
			for (int i = 0; i <= currentdecimalHigh; i++) {
				temp[ptr]  += Math.pow(10,currentdecimalHigh-i)*(float)Character.getNumericValue(valueBufferHigh[i]);
			}
			for (int i = 0; i <= currentdecimalLow; i++) {
				temp[ptr]  += Math.pow(10,-1-i)*(float) Character.getNumericValue(valueBufferLow[i]);
			}

			currentdecimalHigh = 0;
			currentdecimalLow  = 0;
			ptr++;
		}


		float[] finalFloat= new float[ptr];
		System.arraycopy(temp, 0, finalFloat, 0, ptr);
		return finalFloat;

	}
}


