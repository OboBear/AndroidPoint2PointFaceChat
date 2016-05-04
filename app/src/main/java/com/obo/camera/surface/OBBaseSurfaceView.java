package com.obo.camera.surface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;

public abstract class OBBaseSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {
	private static final String TAG = "BaseSurfaceView";
	
	public  Camera mCamera;
	private SurfaceHolder mHolder;
	private byte[] mFrame;
	private boolean mThreadRun;
	private Size size;
	Context context;
	LayoutInflater layoutInflater;
	AlertDialog alert;

	public OBBaseSurfaceView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub

		super(context,attrs);
		this.context = context;
		
		layoutInflater = LayoutInflater.from(context);
		
		mHolder = getHolder();
		
		mHolder.addCallback(this);
		
	}
	
	OBCameraAgent agent = null;
	Handler handler =null;
	public void setImgAgent(OBCameraAgent agent,Handler handler)
	{
		this.agent 		= agent;
		this.handler 	= handler;
	}

	public void surfaceChanged(SurfaceHolder _holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceCreated");
		if (mCamera != null) {
			
			this.layout(0, 0, width, height);

			try {
				mCamera.setPreviewDisplay(this.getHolder());
			} catch (Exception e) {
				Log.e(TAG, "mCamera.setPreviewDisplay fails: " + e);
			}

			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				Log.d(TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}

	@SuppressLint("NewApi")
	public void surfaceCreated(SurfaceHolder holder) {
		
		mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		mCamera.getParameters().setPictureSize(100, 120);
		mCamera.getParameters().setPreviewSize(100, 120);
//		mCamera.getParameters().setJpegQuality(30);
//		mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);

		mCamera.setDisplayOrientation(90);

		mCamera.setPreviewCallback(new PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				synchronized (OBBaseSurfaceView.this) {
					mFrame = data;
					// //////////////////////////////////////////
					size = camera.getParameters().getPreviewSize();
//					BaseSurfaceView.this.layout(0, 0,size.height,size.width);
					// /////////////////////////////////
					OBBaseSurfaceView.this.notify();
				}
			}
		});
		
		(new Thread(this)).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		mThreadRun = false;
		if (mCamera != null) {
			synchronized (this) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}
		}
	}

	float myEyesDistance;
	int numberOfFaceDetected;
	Bitmap piture = null;
	public void run() {
		mCamera.getParameters().setColorEffect(
				Camera.Parameters.EFFECT_SOLARIZE);
		
		mThreadRun = true;
		Log.i(TAG, "Starting processing thread");
		while (mThreadRun) {
			synchronized (this) {
				try {
					this.wait();
					final int w = size.width; // ���
					final int h = size.height;
					
					final YuvImage image = new YuvImage(mFrame,
							ImageFormat.NV21, w, h, null);
					
					ByteArrayOutputStream os = new ByteArrayOutputStream(
							mFrame.length);
					if (image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {

						byte[] tmp = os.toByteArray();
						
						piture = BitmapFactory.decodeByteArray(tmp, 0,
								tmp.length);
						Log.i(TAG, "���� base");
						if(agent!=null)
						{
							new Thread(){
								public void run()
								{
									//�ص�����ͼƬ���
									agent.getCameraImg(piture);
								}
							}.start();
						}
						
						
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void close()
	{
		mThreadRun = false;
	}

}
