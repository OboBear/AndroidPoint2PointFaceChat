package com.obo.camera.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

public abstract class OBBaseSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {
    private static final String TAG = "BaseSurfaceView";

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private byte[] mFrame;
    private boolean mThreadRun;
    private Size mSize;

    public OBBaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    private OBCameraListener mAgent = null;
    private Handler handler = null;

    public void setImgAgent(OBCameraListener agent, Handler handler) {
        this.mAgent = agent;
        this.handler = handler;
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

    public void surfaceCreated(SurfaceHolder holder) {

        mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
        mCamera.getParameters().setPictureSize(100, 120);
        mCamera.getParameters().setPreviewSize(100, 120);
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewCallback(new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (OBBaseSurfaceView.this) {
                    mFrame = data;
                    mSize = camera.getParameters().getPreviewSize();

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

    private Bitmap piture = null;

    @Override
    public void run() {
        mCamera.getParameters().setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
        mThreadRun = true;
        Log.i(TAG, "Starting processing thread");
        while (mThreadRun) {
            synchronized (this) {
                try {
                    this.wait();
                    int width = mSize.width;
                    int height = mSize.height;

                    final YuvImage image = new YuvImage(mFrame,
                            ImageFormat.NV21, width, height, null);

                    ByteArrayOutputStream os = new ByteArrayOutputStream(
                            mFrame.length);
                    if (image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
                        byte[] tmp = os.toByteArray();
                        piture = BitmapFactory.decodeByteArray(tmp, 0,
                                tmp.length);
                        Log.i(TAG, "base");
                        if (mAgent != null) {
                            new Thread() {
                                public void run() {
                                    mAgent.onCameraImgGet(piture);
                                }
                            }.start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
