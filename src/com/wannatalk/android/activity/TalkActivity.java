package com.wannatalk.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.wannatalk.android.R;
public class TalkActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		initView();
	}
	private void initView() {
		Button sendButton = (Button)findViewById(R.id.sendButton);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	

}
