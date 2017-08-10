package com.obo.camera.get;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

/**
 * mSocket
 *
 * @author obo
 */
public class OBSocketImgGet {

    private final static String TAG = "OBSocketImgGet";

    private Handler mHandler = null;
    private OBSocketImgGetAgent mAgent = null;
    private ServerSocket mServerSocket = null;
    private Socket mSocket = null;
    private BufferedReader mBufferedReader = null;
    private PrintWriter mPrintWriter = null;

    private int port = 0;

    public OBSocketImgGet(OBSocketImgGetAgent agent, Handler mHandler, int port) {
        this.mHandler = mHandler;
        this.mAgent = agent;
        this.port = port;

        aliveFlag = true;

        new Thread() {
            public void run() {
                startSocket();
            }
        }.start();
    }

    boolean aliveFlag = true;

    public void close() {

        aliveFlag = false;
        closeSocket();
    }

    private void startSocket() {

        while (aliveFlag) {
            try {
                initSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (aliveFlag) {
                try {
                    final String line = new String(mBufferedReader.readLine().getBytes("UTF-8"));

                    System.out.println(line);
                    int length = Integer.parseInt(line);

                    mPrintWriter.println("SUCCESS");

                    Log.i(TAG, "length:" + length);
                    DataInputStream in = new DataInputStream(mSocket.getInputStream());
                    final byte[] testR = new byte[length];

                    int start = 0;
                    int readLength;
                    int maxLength = 4096;
                    while ((readLength = in.read(testR, start, testR.length
                            - start < maxLength ? testR.length - start
                            : maxLength)) > 0) {
                        Log.i("", "readLength:" + readLength);
                        start += readLength;
                        if (start == length)
                            break;
                    }

                    mHandler.post(new Runnable() {
                        public void run() {
                            final Bitmap piture = BitmapFactory
                                    .decodeByteArray(testR, 0, testR.length);
                            mAgent.getImg(piture);
                        }
                    });
                } catch (IOException ie) {
                    ie.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            Log.i("", "");
            closeSocket();
        }

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mSocket = null;
        }
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mServerSocket = null;
        }
    }

    private void initSocket() throws Exception {

        Log.i(TAG, "initSocket");
        mServerSocket = new ServerSocket(port);

        System.out.println("Server is starting...");
        Log.i(TAG, "initSocket");
        mSocket = mServerSocket.accept();
        System.out.println("" + mSocket.getInetAddress());
        mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
    }

}
