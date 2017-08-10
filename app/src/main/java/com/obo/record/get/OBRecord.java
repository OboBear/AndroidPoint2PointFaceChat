package com.obo.record.get;

import java.io.DataOutputStream;
import java.util.LinkedList;

import com.obo.socket.flowsend.OBSendFlow;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author obo
 */
public class OBRecord implements Runnable {

    protected AudioRecord audioRecord;
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;
    protected boolean m_keep_running;
    protected LinkedList<byte[]> m_in_q;

    private OBRecordFlowAgent agent;

    public OBRecord(OBRecordFlowAgent agent) {
        this.agent = agent;
        init();

        new Thread(this).start();
    }

    public void close() {
        m_keep_running = false;

        try {
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        audioRecord = null;
    }


    public void init() {

        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);

        m_in_bytes = new byte[m_in_buf_size];

        m_keep_running = true;

        m_in_q = new LinkedList<byte[]>();

    }

    public void run() {

        try {
            byte[] bytes_pkg;
            audioRecord.startRecording();
            while (m_keep_running) {

                Log.i("", "��ȡ����");
                audioRecord.read(m_in_bytes, 0, m_in_buf_size);

                bytes_pkg = m_in_bytes.clone();

                if (m_in_q.size() >= 2) {

                    agent.sendFlow(m_in_bytes);

                }
                m_in_q.add(bytes_pkg);
            }

//			audioRecord.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
