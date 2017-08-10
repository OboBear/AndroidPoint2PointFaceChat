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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 10, baos);
        processFrame(baos.toByteArray());
    }

    private boolean sendingFree = true;
    private boolean initFlag = false;

    protected void processFrame(final byte[] imgData) {
        // TODO Auto-generated method stub
        new Thread() {
            public void run() {
                if (initFlag) return;

                if (sendingFree) {
                    sendingFree = false;

                    send(imgData);
                    sendingFree = true;
                }
            }
        }.start();
    }

    Socket s = null;
    BufferedReader br = null;
    PrintWriter pw = null;
    String line = "";
    DataOutputStream out = null;

    private void initSocket() {
        closeSocket();
        // socket
        try {
            s = new Socket(url, port);
//			s.setReuseAddress(true);
//			s.setKeepAlive(true);

            pw = new PrintWriter(s.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new DataOutputStream(s.getOutputStream());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

            Log.i(TAG, "");
        }
    }

    public void close() {
        closeSocket();

        Log.i(TAG, "" + port);
    }


    private void closeSocket() {
        if (br != null) {
            try {
                br.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            br = null;
        }
        if (pw != null) {
            pw.close();
            pw = null;
        }

        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            s = null;
        }

        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            out = null;
        }
    }

    void send(byte[] imgData) {

        Log.i(TAG, "send length:" + imgData.length);
        try {
            pw.println("" + imgData.length);
            line = br.readLine();
            if (!line.equals(STATUS_SUCCESS)) {
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
                out.write(imgData, start, sendLength);
                out.flush();
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
