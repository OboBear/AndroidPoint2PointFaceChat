package com.obo.socket.flowget;

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
 * @author obo
 *
 */
public class OBSocketFlowGet {
	
	private final static String TAG = "OBSocketFlowGet";
	
	Handler handler;
	OBSocketFlowGetAgent agent;
	ServerSocket ss;
	Socket s;
	BufferedReader br;
	PrintWriter pw;
	int port;
	
	public OBSocketFlowGet(OBSocketFlowGetAgent agent,Handler handler ,int port)
	{
		this.handler 	= handler;
		this.agent 		= agent;
		this.port		= port;
		
		new Thread() {
			public void run() {
				startSocket();
			}
		}.start();
	}
	
	
	boolean closeFlag = false;
	public void close()
	{
		closeFlag = true;
		
		closeSocket();
		Log.i(TAG,TAG+"��  ������� �ͷŶ˿ڣ�"+port);
	}
	
	private void startSocket() {
		
		while (!closeFlag) {
			try {
				initSocket();
			} catch (IOException ie) {
				ie.printStackTrace();
			}  catch(Exception e)
			{
				e.printStackTrace();
			}
			while (!closeFlag) {
				
				try {
					
					final String line = new String(br.readLine().getBytes(
							"UTF-8"));
					System.out.println(line);
					
					int length = Integer.parseInt(line);
					pw.println(new String("SUCCESS"));
					

					DataInputStream in = new DataInputStream(s.getInputStream());
					final byte[] testR = new byte[length];
					in.read(testR);
					
					handler.post(new Runnable() {
						public void run() {
							agent.getFlow(testR);
						}
					});
					
				} catch (IOException ie) {
					ie.printStackTrace();
					break;
				}catch(Exception e)
				{
					break;
				}
			}
			
			try {
				initSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		closeSocket();
		Log.i(TAG,TAG+"��  ��������ͷŶ˿ڣ�"+port);
	}
	
	private void closeSocket()
	{
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = null;
		}
		if (ss != null) {
			try {
				ss.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ss = null;
		}
	}
	
	
	private void initSocket() throws IOException ,Exception
	{
		Log.i("", "�󶨵��˿ڼ���:"+port);
		ss = new ServerSocket(port);
		Log.i("", "�󶨵��˿ڼ���ɹ���");
		System.out.println("Server is starting...");
		s = ss.accept();
		System.out.println("�ͻ���1ip��"+s.getInetAddress());
		br = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		pw = new PrintWriter(s.getOutputStream(), true);
	}

}
