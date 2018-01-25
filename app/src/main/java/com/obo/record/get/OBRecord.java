package com.obo.record.get;

import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Audio recorder
 * @author obo
 */
public class OBRecord implements Runnable {

    private AudioRecord mAudioRecord;
    private int mInBufSize;
    private byte[] mInBytes;
    private boolean mKeepRunning;
    private LinkedList<byte[]> mLinkedList;

    private OBRecordFlowAgent agent;

    public OBRecord(OBRecordFlowAgent agent) {
        this.agent = agent;
        init();

        new Thread(this).start();
    }

    public void close() {
        mKeepRunning = false;

        try {
            mAudioRecord.stop();
            mAudioRecord.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioRecord = null;
    }


    public void init() {

        mInBufSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
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
            mAudioRecord.startRecording();
            while (mKeepRunning) {

                Log.i("", "");
                mAudioRecord.read(mInBytes, 0, mInBufSize);
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
