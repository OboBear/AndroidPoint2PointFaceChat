package com.obo.track.play;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class OBTrack {

    private AudioTrack audioTrack;
    private int mOutBufSize;
    private boolean mIsAlive;

    public OBTrack() {
        init();
    }

    private void init() {

        try {
            mIsAlive = true;

            mOutBufSize = AudioTrack.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, mOutBufSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {

        mIsAlive = false;

        try {
            audioTrack.stop();
            audioTrack.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioTrack = null;

    }

    public void record(byte[] flow) {
        audioTrack.write(flow, 0, flow.length);
    }

}
