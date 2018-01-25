package com.obo.chat;

import com.obo.camera.get.OBSocketImgGet;
import com.obo.camera.get.OBSocketImgGetAgent;
import com.obo.camera.send.OBSocketImgSend;
import com.obo.camera.surface.OBCameraListener;
import com.obo.camera.surface.OBCameraView;
import com.obo.record.get.OBRecord;
import com.obo.record.get.OBRecordFlowAgent;
import com.obo.socket.flowget.OBSocketFlowGet;
import com.obo.socket.flowget.OBSocketFlowGetAgent;
import com.obo.socket.flowsend.OBSendFlow;
import com.obo.track.play.OBTrack;
import com.obo.util.ThreadPool;

import android.os.Bundle;
import android.os.Handler;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends BaseActivity implements OBSocketImgGetAgent, OBCameraListener, OBRecordFlowAgent, OBSocketFlowGetAgent {

    private final static String TAG = "MainActivity";

    private static final int SIZE_MAX_GET = 3;
    private static final int SIZE_MAX_SEND = 3;

    private OBSocketImgGet imgGet[] = new OBSocketImgGet[SIZE_MAX_GET];
    private OBSocketImgSend imgSend[] = new OBSocketImgSend[SIZE_MAX_SEND];

    private OBSendFlow mSendFlow = null;
    private OBSocketFlowGet mSocketFlowGet = null;

    private ImageView imgOther = null;
    private OBCameraView oboCamera = null;

    private OBRecord oboRecord = null;
    private OBTrack oboTrack = null;

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        imgOther = findViewById(R.id.img_friend);
    }

    private void initActions() {
        for (int i = 0; i < imgGet.length; i++) {
            // Init image get socket
            if (imgGet[i] == null) {
                imgGet[i] = new OBSocketImgGet(this, handler, 10000 + i);
            }

            // Init image send socket
            if (imgSend[i] == null) {
                imgSend[i] = new OBSocketImgSend(mIP, 10000 + i);
            }
        }

        // Init send socket
        if (mSendFlow == null) {
            mSendFlow = new OBSendFlow(mIP, 10005);
        }

        // Init get socket
        if (mSocketFlowGet == null) {
            mSocketFlowGet = new OBSocketFlowGet(this, handler, 10005);
        }


        // Init Audio recorder
        if (oboRecord == null) {
            oboRecord = new OBRecord(this);
        }

        // Init Audio player
        if (oboTrack == null) {
            oboTrack = new OBTrack();
        }
    }

    @Override
    public void getImg(Bitmap bmp) {
        // TODO Auto-generated method stub
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        imgOther.setImageBitmap(bmp);
    }


    @Override
    public void changeIp() {
        ThreadPool.getInstance().exec(new Runnable() {
            @Override
            public void run() {
                closeAll();
                initActions();
            }
        });
    }

    private int currentSend = 0;

    @Override
    public void onCameraImgGet(Bitmap image) {
        // TODO Auto-generated method stub
        currentSend %= SIZE_MAX_SEND;
        if (imgSend[currentSend] != null) {
            imgSend[currentSend].sendImg(image, 0.2f);
            currentSend++;
        }
    }

    @Override
    public void sendFlow(byte[] arg0) {
        // TODO Auto-generated method stub
        if (mSendFlow != null)
            mSendFlow.sendFlow(arg0);
    }

    @Override
    public void getFlow(byte[] arg0) {
        // TODO Auto-generated method stub
        if (oboTrack != null)
            oboTrack.record(arg0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        new Thread() {
            @Override
            public void run() {
                super.run();
                initActions();
            }
        }.start();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        new Thread(){
            @Override
            public void run() {
                super.run();
                initActions();
            }
        }.start();

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");

        closeAll();

        super.onPause();
    }


    private void closeAll() {

        if (oboRecord != null) {
            try {
                oboRecord.close();
            } catch (Exception e) {

            }
            oboRecord = null;
        }
        if (oboTrack != null) {
            try {
                oboTrack.close();
            } catch (Exception e) {

            }
            oboTrack = null;
        }
        if (mSocketFlowGet != null) {
            try {
                mSocketFlowGet.close();
            } catch (Exception e) {

            }
            mSocketFlowGet = null;
        }

        if (mSendFlow != null) {
            try {
                mSendFlow.close();
            } catch (Exception e) {

            }
            mSendFlow = null;
        }

        for (int i = 0; i < imgGet.length; i++) {
            if (imgGet[i] != null) {
                try {
                    imgGet[i].close();
                } catch (Exception e) {

                }
                imgGet[i] = null;
            }

            if (imgSend[i] != null) {
                try {
                    imgSend[i].close();
                } catch (Exception e) {

                }
                imgSend[i] = null;
            }
        }
    }


    @Override
    public void onStop() {
        Log.i("", "onStop");
        closeAll();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("", "onDestroy");
        closeAll();

        super.onDestroy();
    }

}
