package com.obo.camera.send;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

public class OBSocketImgSend {
    private final static String TAG = "OBSendImg";

    private static final String STATUS_SUCCESS = "SUCCESS";

    private String url;
    private int port;

    public OBSocketImgSend(String url, int port) {
        this.url = url;
        this.port = port;

        initSocket();
    }

    public void sendImg(Bitmap img, float quality) {
        Matrix matrix = new Matrix();
        matrix.postScale(quality, quality);
        img = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
        processFrame(byteArrayOutputStream.toByteArray());
    }

    private boolean sendingFree = true;

    protected void processFrame(final byte[] imgData) {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                if (sendingFree) {
                    sendingFree = false;
                    send(imgData);
                    sendingFree = true;
                }
            }
        }.start();
    }

    private Socket mSocket = null;
    private BufferedReader mBufferedReader = null;
    private PrintWriter mPrintWriter = null;
    private DataOutputStream mDataOutputStream = null;

    private void initSocket() {
        closeSocket();
        // socket
        try {
            mSocket = new Socket(url, port);
//			mSocket.setReuseAddress(true);
//			mSocket.setKeepAlive(true);

            mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mDataOutputStream = new DataOutputStream(mSocket.getOutputStream());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void close() {
        closeSocket();

        Log.i(TAG, "" + port);
    }


    private void closeSocket() {
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mBufferedReader = null;
        }
        if (mPrintWriter != null) {
            mPrintWriter.close();
            mPrintWriter = null;
        }

        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket = null;
        }

        if (mDataOutputStream != null) {
            try {
                mDataOutputStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            mDataOutputStream = null;
        }
    }

    void send(byte[] imgData) {

        Log.i(TAG, "send length:" + imgData.length);
        try {
            mPrintWriter.println("" + imgData.length);
            String lineData = mBufferedReader.readLine();
            if (!lineData.equals(STATUS_SUCCESS)) {
                Log.i(TAG, "connect fail");
                closeSocket();
                return;
            }
            Log.i(TAG, "connect SUCCESS");

            int start = 0;
            int maxLength = 1024;

            while (start < imgData.length) {
                int sendLength = imgData.length - start < maxLength ? imgData.length
                        - start
                        : maxLength;
                mDataOutputStream.write(imgData, start, sendLength);
                mDataOutputStream.flush();
                start += sendLength;
            }
        } catch (IOException ie) {
            // socket
            initSocket();
            ie.printStackTrace();
        } catch (Exception e) {
            // socket
            initSocket();
        }
    }

}
