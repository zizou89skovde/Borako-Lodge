package com.lodge.object.derived;

import android.content.res.Resources;

import com.lodge.R;
import com.lodge.gl.Renderable;
import com.lodge.gl.utils.Transform.TransformType;
import com.lodge.object.SceneObject;

public class TemplateObject extends SceneObject{
	
	
	public TemplateObject(Resources res) {
		super(res);
		mRenderable = new TemplateRenderable(res);
		
	}

	class TemplateRenderable extends Renderable{

		public TemplateRenderable(Resources res) {
			super(res);
			
			loadModel(R.drawable.model_sphere_norm);
			
			setShader(R.drawable.well_vert, R.drawable.well_frag);
			
			setTransformType(TransformType.MVP);
			
			mTransform.translate(0, 0, 0);
			
		}
		
		
		
	}
	
	


}
