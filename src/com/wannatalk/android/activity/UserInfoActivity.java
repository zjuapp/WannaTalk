package com.wannatalk.android.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.utils.HttpHelper;

public class UserInfoActivity extends Activity{
	private String uid;
	private Handler mHandler;
	private ImageView img = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		uid = bundle.getString("uid");
		initView();
		init();
	}
	private void initView(){
		this.setContentView(R.layout.user_info_layout);
		img = (ImageView) findViewById(R.id.iv_head);
	}
	private void init() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
					case Constants.GET_USER_IMG_OK:
						
						break;
				}
			}
			
		};
		new Thread(){
			@Override
			public void run() {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("uid", uid);
				try {
					String result = HttpHelper.post("request_user_info", params);
					JSONObject json = new JSONObject(result);
					String img = json.optString("img");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
}
