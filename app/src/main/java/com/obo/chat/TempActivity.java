package com.obo.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TempActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activitiy);

        new Thread() {
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent intent = new Intent(TempActivity.this, MainActivity.class);

//				TempActivity.this.startActivity(intent);
//				TempActivity.this.finish();
            }
        }.start();


        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(TempActivity.this, MainActivity.class);

                TempActivity.this.startActivity(intent);
                TempActivity.this.finish();
            }

        });
    }
}
