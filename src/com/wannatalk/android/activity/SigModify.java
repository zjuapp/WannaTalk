package com.wannatalk.android.activity;


import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Config;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class SigModify extends Activity implements OnClickListener, OnRatingBarChangeListener {
	protected EditText sigText;
	//public static String sig;
	protected Button confirm_sig;
	protected Button cancel_sig;
	protected int moodid = 0;
	protected RadioGroup moodGroup;
	protected RadioButton happy_radio, annoyed_radio, sorrow_radio, soso_radio;
	
	protected float tt_mood = IndexInf.mood_rating;
	protected RatingBar modifyRb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sig_modify);
		sigText = (EditText) findViewById(R.id.sig_textView1);
		sigText.setText(IndexInf.sig);
		confirm_sig = (Button) findViewById(R.id.sig_button1);
		cancel_sig = (Button) findViewById(R.id.sig_button2);
		confirm_sig.setOnClickListener(this);
		cancel_sig.setOnClickListener(this);
		 modifyRb = (RatingBar) findViewById(R.id.ratingBar1);
		 modifyRb.setRating(IndexInf.mood_rating);
		 modifyRb.setOnRatingBarChangeListener(this);
		moodGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		happy_radio = (RadioButton) findViewById(R.id.radio0);
		annoyed_radio = (RadioButton) findViewById(R.id.radio1);
		sorrow_radio = (RadioButton) findViewById(R.id.radio2);
		soso_radio = (RadioButton) findViewById(R.id.radio3);
		moodid = IndexInf.moodid;
		switch(IndexInf.moodid){
		case 0:
			happy_radio.setChecked(true);break;
		case 1:
			annoyed_radio.setChecked(true);break;
		case 2:
			sorrow_radio.setChecked(true);break;
		case 3:
			soso_radio.setChecked(true);break;
		}
		moodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId==happy_radio.getId()){
					moodid = 0;
				}
				else if(checkedId==annoyed_radio.getId()){
					moodid = 1;
				}
				else if(checkedId==sorrow_radio.getId()){
					moodid = 2;
				}
				else if(checkedId==soso_radio.getId()){
					moodid = 3;
				}
				
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==confirm_sig){
			new Thread(){
				@Override
				public void run(){
					PostMethod method = new PostMethod("/api/updatemotionandsig");
					NameValuePair []user = {
							new NameValuePair("id", Integer.toString(Config.uid)),
	    					new NameValuePair("motion", Integer.toString(moodid)),//Integer.toString(point.getLatitudeE6())), 
	    					new NameValuePair("motionlevel", Integer.toString((int)(tt_mood * 2))),//Integer.toString(point.getLongitudeE6())),
	    					new NameValuePair("signature", sigText.getText().toString())//Integer.toString(dis(mapView.getMapCenter(), point)))
	    			};	
	    			method.setRequestBody(user);
	    			Boolean flag = false;
	    			try{
	    				Config.client.executeMethod(method);
	    				String response = method.getResponseBodyAsString();
	    				if("true".equals(response)){
	    					flag = true;
	    					Config.motion = moodid;
	    					Config.motionlevel = (int)(tt_mood * 2);
	    					Config.happen = sigText.getText().toString();
	    				}
	    			}
	    			catch(Exception e){
	    				
	    			}
	    			Message message = new Message();
	    			message.what = flag ? 1:0;
	    			handler.sendMessage(message);
				}
			}.start();
		}
		else if(v==cancel_sig){
			finish();
		}
	}
	
	public void onRatingChanged(RatingBar rb, float rating, boolean arg2) {
		// TODO Auto-generated method stub
		rb.setRating(rating);
		if(rb==modifyRb)
			tt_mood=rb.getRating();
	}
	private Handler handler = new Handler(){
		 public void handleMessage(Message msg){  
           switch(msg.what) {  
           case 1:  
           	Toast.makeText(SigModify.this, "同步信息成功", Toast.LENGTH_SHORT).show();
           	SigModify.this.finish();
               break;  
           case 0:
   			Toast.makeText(SigModify.this, "同步信息失败", Toast.LENGTH_SHORT).show();
   			SigModify.this.finish();
           }  
           
       }  
	};
}
