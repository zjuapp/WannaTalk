package com.wannatalk.android.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Config;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.utils.HttpHelper;

public class MainActivity extends InstrumentedActivity{
	public static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button talkButton = (Button) findViewById(R.id.talkButton);
		Button synchButton = (Button) findViewById(R.id.synch);
		synchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				newThreadToReset();
			}
			
		});
		talkButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, TalkActivity.class);
				startActivity(intent);
			}
			
		});
		JPushInterface.init(getApplicationContext());
		newThreadToReset();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void newThreadToReset() {
		Log.d(TAG, "newThreadReset is invoked");
		new Thread() {
			public void run() {
				resetAliasAndTags();
			}
		}.start();
	}
	private void resetAliasAndTags() {
		// download from wannatalk server
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", "" + Config.uid);
		
		String userInfo = null;
		try {
			userInfo = HttpHelper.post(Constants.REQUEST_USER_INFO, params);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Call wannatalk api to get user info error",e);
			Log.e(TAG, "return result : " + userInfo);
			return;
		}

		Log.d(TAG, "Original user info - " + userInfo);
		if (null == userInfo) {
			Log.w("TAG", "Unexpected: failed to get user info from server.");
			return;
		}
		
		String myChattingName = null;
		try {
			JSONObject json = new JSONObject(userInfo);
			String username = json.optString("username");
			if (null != username) {
			    myChattingName = username;
			    Config.username = username;
			}
		} catch (JSONException e) {
			Log.e(TAG, "Parse user info json error", e);
			return;
		}
		Log.d(TAG, "reset alias : chattingname is " + myChattingName);
		// reset to jpush
		JPushInterface.setAliasAndTags(MainActivity.this, myChattingName, null);
	}

}
