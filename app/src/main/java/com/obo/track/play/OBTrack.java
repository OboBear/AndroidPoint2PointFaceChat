package com.obo.track.play;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

public class OBTrack {

	protected AudioTrack audioTrack;
	protected int m_out_buf_size;
	protected byte[] m_out_bytes;
	protected boolean m_keep_running;

	public OBTrack() {
		init();
	}
	
	public void init() {

		try {
			m_keep_running = true;

			m_out_buf_size = AudioTrack.getMinBufferSize(8000,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
					AudioTrack.MODE_STREAM);
			audioTrack.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void close() {
		
		m_keep_running = false;
		
		try{
			audioTrack.stop();
			audioTrack.release();
		}catch(Exception e)
		{
			e.printStackTrace();
			
			
			
		}
		audioTrack = null; 
		
	}
	
	public void record(byte[] flow)
	{
		audioTrack.write(flow, 0, flow.length);
	}

}
