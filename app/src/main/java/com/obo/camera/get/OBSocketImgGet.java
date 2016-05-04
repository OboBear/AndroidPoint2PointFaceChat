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
 * socke
 * @author obo
 *
 */
public class OBSocketImgGet {
	
	private final static String TAG = "OBSocketImgGet";
	
	Handler 			handler = null;
	OBSocketImgGetAgent agent 	= null;
	ServerSocket 		ss		= null;
	Socket 				s		= null;
	BufferedReader 		br		= null;
	PrintWriter 		pw		= null;
	
	int port = 0;
	
	public OBSocketImgGet(OBSocketImgGetAgent agent,Handler handler ,int port)
	{
		this.handler 	= handler;
		this.agent 		= agent;
		this.port		= port;
		
		aliveFlag = true;
		
		new Thread() {
			public void run() {
				startSocket();
			}
		}.start();
	}
	
	boolean aliveFlag = true;
	public void close()
	{
		aliveFlag = false;
		
		closeSocket();
		
	}
	
	private void startSocket() {

		while (aliveFlag) {
			
			try {
				// ��ʼ��socket
				initSocket();
			} catch (IOException ie) {
				ie.printStackTrace();
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			
			while (aliveFlag) {
				try {
					final String line = new String(br.readLine().getBytes(
							"UTF-8"));
					
					System.out.println(line);
					int length = Integer.parseInt(line);
					
					pw.println(new String("SUCCESS"));
					
					Log.i("", "length:" + length);
					// ��ȡ����
					DataInputStream in = new DataInputStream(s.getInputStream());
					final byte[] testR = new byte[length];

					int start = 0;
					int readLength;
					int maxLength = 4096;
					while ((readLength = in.read(testR, start, testR.length
							- start < maxLength ? testR.length - start
							: maxLength)) > 0) {
						Log.i("", "readLength:"+readLength);
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
				}catch(Exception e)
				{
					e.printStackTrace();
//					������������ǰ����
					break;
				}
			}
			
			Log.i("", "���Թرյ�ǰsocket");
			closeSocket();
		}
		
		closeSocket();
		
		Log.i(TAG,TAG+"��  �������");
		Log.i(TAG,TAG+"��  �ͷŶ˿ڣ�"+ port);
	}
	
	private void closeSocket()
	{
		if(br!=null)
		{
			try {
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			br = null;
		}
		if(pw!=null)
		{
			pw.close();
			pw = null;
		}
		if(s!=null)
		{
			try {
				s.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = null;
		}
		if(ss!=null)
		{
			try {
				ss.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ss = null;
		}
	}
	
	private void initSocket() throws IOException,Exception
	{

		Log.i("", "�󶨵��˿ڼ���");
		ss = new ServerSocket(port);

		
		System.out.println("Server is starting...");
		//�����ⲿ����
		Log.i(TAG, "����socket��������");
		s = ss.accept();
		System.out.println("����Ŀͻ���ַ��"+s.getInetAddress());
		br = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		pw = new PrintWriter(s.getOutputStream(), true);
	}

}
