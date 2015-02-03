package com.lodge.gl.model;

import com.lodge.math.Vec3;

public class Tangent {

	
	static Vec3 read(float[] arr ,short[] index){
		
	}
	
	static float[] genTangets(ModelData m){
		
		
		int numIndices = m.indexArray.length;
		int numVertices = m.positionArray.length/3;
	
		
		Vec3 tan1[] = new Vec3[numVertices];
		Vec3 tan2[] = new Vec3[numVertices];
		
		short indices[] = m.indexArray;
		float position[] = m.positionArray;
		float position[] = m.normalArray;
		//incidces
		short i0,i1,i2;
		short idx[] = new short[3];
		
		for (int i = 0; i < numIndices; i+=3) {
			
			i0 = indices[i];
			i1 = indices[i+1];
			i2 = indices[i+2];
			idx[0] = i0;
			idx[0] = i1;
			idx[0] = i2;
			Vec3
			
		}
		
		return null;
	}
	
}

