package com.obo.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.obo.camera.surface.OBBaseSurfaceView2;

/**
 * Created by obo on 2017/8/13.
 * Email:obo1993@gmail.com
 */

public class Camera2Activity extends Activity {

    OBBaseSurfaceView2 obBaseSurfaceView2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        obBaseSurfaceView2 = findViewById(R.id.sv_camera2);
        obBaseSurfaceView2.getHolder();
    }
}
