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
 * socket
 *
 * @author obo
 */
public class OBSocketImgGet {

    private final static String TAG = "OBSocketImgGet";

    private Handler handler = null;
    private OBSocketImgGetAgent agent = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private BufferedReader br = null;
    private PrintWriter pw = null;

    private int port = 0;

    public OBSocketImgGet(OBSocketImgGetAgent agent, Handler handler, int port) {
        this.handler = handler;
        this.agent = agent;
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
                    final String line = new String(br.readLine().getBytes("UTF-8"));

                    System.out.println(line);
                    int length = Integer.parseInt(line);

                    pw.println("SUCCESS");

                    Log.i(TAG, "length:" + length);
                    DataInputStream in = new DataInputStream(socket.getInputStream());
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

                    handler.post(new Runnable() {
                        public void run() {
                            final Bitmap piture = BitmapFactory
                                    .decodeByteArray(testR, 0, testR.length);
                            agent.getImg(piture);
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

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            socket = null;
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            serverSocket = null;
        }
    }

    private void initSocket() throws Exception {

        Log.i(TAG, "");
        serverSocket = new ServerSocket(port);

        System.out.println("Server is starting...");
        Log.i(TAG, "");
        socket = serverSocket.accept();
        System.out.println("" + socket.getInetAddress());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pw = new PrintWriter(socket.getOutputStream(), true);
    }

}
