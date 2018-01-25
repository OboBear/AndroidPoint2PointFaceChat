package com.obo.socket.flowsend;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author obo
 */
public class OBSendFlow {
    private final static String TAG = "OBSendFlow";
    private String url;
    private int port;

    public OBSendFlow(String url, int port) {
        this.url = url;
        this.port = port;

        initSocket();
    }

    public void sendFlow(byte[] baos) {
        processFrame(baos);
    }

    private boolean sendingFree = true;

    protected void processFrame(final byte[] imgData) {
        // TODO Auto-generated method stub
        new Thread() {
            @Override
            public void run() {
                if (sendingFree) {
                    sendingFree = false;
                    send(imgData);
                    Log.i(TAG, "send success");
                    sendingFree = true;
                }
            }
        }.start();
    }

    private Socket mSocket;
    private BufferedReader mBufferedReader;
    private PrintWriter mPrintWriter;
    private String line = "";
    private DataOutputStream out;

    private void initSocket() {
        closeSocket();

        try {
            mSocket = new Socket(url, port);
            mSocket.setReuseAddress(true);
            mSocket.setKeepAlive(true);
            mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            out = new DataOutputStream(mSocket.getOutputStream());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
            Log.i(TAG, "�˿ڻ��ַ����");

        }
    }


    public void close() {
        closeSocket();
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

        Log.i("", "send length:" + imgData.length);
        try {

            mPrintWriter.println("" + imgData.length);
            line = mBufferedReader.readLine();
            if (!line.equals("SUCCESS")) {
                Log.i("", "connect fail 1 ");
                closeSocket();
                Log.i("", "connect fail 2");
                return;
            }
            Log.i("", "connect SUCCESS");

            //
            int start = 0;
            int maxLength = 1024;

            while (start < imgData.length) {
                int sendLength = imgData.length - start < maxLength ? imgData.length - start : maxLength;
                out.write(imgData, start, sendLength);
                out.flush();
                start += sendLength;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            //创建socket
            initSocket();
        }

    }

}
