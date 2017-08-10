package com.obo.record.get;

import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author obo
 */
public class OBRecord implements Runnable {

    protected AudioRecord audioRecord;
    protected int mInBufSize;
    protected byte[] mInBytes;
    protected boolean mKeepRunning;
    protected LinkedList<byte[]> mLinkedList;

    private OBRecordFlowAgent agent;

    public OBRecord(OBRecordFlowAgent agent) {
        this.agent = agent;
        init();

        new Thread(this).start();
    }

    public void close() {
        mKeepRunning = false;

        try {
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        audioRecord = null;
    }


    public void init() {

        mInBufSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, mInBufSize);

        mInBytes = new byte[mInBufSize];

        mKeepRunning = true;

        mLinkedList = new LinkedList<>();

    }

    @Override
    public void run() {

        try {
            byte[] bytes_pkg;
            audioRecord.startRecording();
            while (mKeepRunning) {

                Log.i("", "");
                audioRecord.read(mInBytes, 0, mInBufSize);
                bytes_pkg = mInBytes.clone();
                if (mLinkedList.size() >= 2) {
                    agent.sendFlow(mInBytes);
                }
                mLinkedList.add(bytes_pkg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
