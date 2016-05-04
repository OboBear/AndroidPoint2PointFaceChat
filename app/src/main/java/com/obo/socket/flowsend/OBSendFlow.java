package com.obo.socket.flowsend;

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

public class OBSendFlow 
{
	private final static String TAG = "OBSendFlow";
	String url;
	int port;
	
	public OBSendFlow(String url,int port )
	{
		this.url 	= url;
		this.port 	= port;
		
		initSocket();
	}
	
	public void sendFlow(byte[] baos)
	{
		processFrame(baos);
	}
	int dif = 0;
	boolean sendingFree = true;
	protected void processFrame(final byte[]imgData) {
		// TODO Auto-generated method stub
		new Thread()
		{
			public void run()
			{
				Log.i(TAG, "��������"+(dif++));
				if(sendingFree)
				{
					sendingFree = false;
					send(imgData);
					Log.i(TAG, "���ͽ���");
					dif = 0;
					sendingFree = true;
				}
			}
		}.start();
	}

	int number = 0;
	Socket s;
	BufferedReader br;
	PrintWriter pw;
	String line = "";
	DataOutputStream out;
	
	private void initSocket() 
	{
		closeSocket();
		
		try {
			s = new Socket(url, port);
			s.setReuseAddress(true);
			s.setKeepAlive(true);
			pw = new PrintWriter(s.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new DataOutputStream(s.getOutputStream());
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			
			e.printStackTrace();
			Log.i(TAG, "�˿ڻ��ַ����");
			
		}
	}
	
	
	
	public void close()
	{
		
		closeSocket();
		
	}
	private void closeSocket() 
	{
		if (br != null)
		{
			try {
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			br = null;
		}
		if (pw != null)
			pw.close();
		if (s != null)
		{
			try {
				s.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = null;
		}
		if (out != null)
		{
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
		
		Log.i("", "send length:"+imgData.length);
		try {
			
			pw.println("" + imgData.length);
			line = br.readLine();
			if (!line.equals("SUCCESS"))
			{
				Log.i("", "connect fail 1 ");
				closeSocket();
				Log.i("", "connect fail 2");
				return;
			}
			Log.i("", "connect SUCCESS");

			// ��������
			int start = 0;
			int maxLength = 1024;
			
			while(start<imgData.length)
			{
				int sendLength =  imgData.length - start < maxLength?imgData.length - start : maxLength;
				out.write(imgData,start,sendLength);
				out.flush();
				start+= sendLength;
			}
			
			Log.i(TAG, "���ͳɹ�");
			
		} catch (IOException ie) {
			ie.printStackTrace();
			//���³�ʼ��
			initSocket();
			
		} catch (Exception e) {
			//���³�ʼ��
			initSocket();
		}

	}
	
}
