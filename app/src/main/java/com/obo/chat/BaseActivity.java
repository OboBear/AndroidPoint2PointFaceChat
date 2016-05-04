package com.obo.chat;


import com.obo.util.MobileIpV4;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
/**
 * @author obo
 *
 */
public abstract class BaseActivity extends Activity{
	/**
	 * �첽�߳�תͬ���߳�
	 */
	public SharedPreferences share;
	public String myIp;
	public String IP="12345";
	
	
	public Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		initDatas();
	}
	
	private void initDatas()
	{
		share  = this.getPreferences(MODE_PRIVATE);
		IP = share.getString("IP", "183.247.162.196:8888");
	}
	
	////////////////////////////////
	//���setting
	
	public abstract void changeIp();
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0,1,1,"����IP");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == 1)
		{
//			Toast.makeText(this, "������������", Toast.LENGTH_LONG).show();
			showBuilder("���õ�ַ");
		}
		return true;
	}
	
	private void showBuilder(String title)
	{
		Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(title);
		View layout = this.getLayoutInflater().inflate(R.layout.dialog_edit, null);
		builder.setView(layout);
		
		TextView textMyId = (TextView) layout.findViewById(R.id.text_my_ip);
		myIp = MobileIpV4.getLocalIpAddress();
		textMyId.setText(myIp);
		
		final EditText eText = (EditText) layout.findViewById(R.id.edit_your_ip);
		
		eText.setText(IP);
		
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				IP = eText.getText().toString();
				share.edit().putString("IP", IP).commit();
				
				changeIp();
				
			}
		});
		
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
		});
		
		builder.show();
	}

}
