package com.obo.chat;


import com.obo.util.MobileIpV4;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author obo
 */
public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    private static final String SP_IP = "sp_ip";

    public SharedPreferences mSharedPreferences;
    public String myIp;
    public String mIP = "12345";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
    }

    private void initDatas() {
        mSharedPreferences = getSharedPreferences(SP_IP, MODE_PRIVATE);
        mIP = mSharedPreferences.getString("mIP", "183.247.162.196:8888");
    }

    public abstract void changeIp();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, 1, 1, "Reset mIP");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            showBuilder("Reset mIP");
        }
        return true;
    }

    private void showBuilder(String title) {
        Builder builder = new Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(title);
        View layout = this.getLayoutInflater().inflate(R.layout.dialog_edit, null);
        builder.setView(layout);

        TextView tvMyId = layout.findViewById(R.id.tv_my_ip);
        myIp = MobileIpV4.getLocalIpAddress();
        tvMyId.setText(myIp);

        final EditText eText = layout.findViewById(R.id.edit_your_ip);

        eText.setText(mIP);

        builder.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIP = eText.getText().toString();
                mSharedPreferences.edit().putString("mIP", mIP).commit();
                changeIp();
            }
        });

        builder.setNegativeButton("CANCEL", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}
