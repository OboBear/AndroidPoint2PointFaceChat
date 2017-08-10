package com.obo.camera.surface;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class OBCameraView extends OBBaseSurfaceView {
	public OBCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public void surfaceChanged(SurfaceHolder _holder, int format, int width,
			int height) {
		super.surfaceChanged(_holder, format, width, height);
	}
}
