package com.wannatalk.android.activity;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Constants;

public class RegisterActivity extends Activity implements OnClickListener {
	public static final String TAG = "RegisterActivity";
	protected Button register;
	protected Button cancel;
	protected EditText name;
	protected EditText pwd;
	protected EditText confirm;
	protected boolean b = false;
	protected int sex = 0; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		name = (EditText) findViewById(R.id.name);
		pwd = (EditText) findViewById(R.id.pwd);
		confirm = (EditText) findViewById(R.id.confirm);
		register = (Button) findViewById(R.id.register);
		cancel = (Button) findViewById(R.id.cancel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == cancel) {
			finish();
		} else if (v == register) {
			final String NAME = name.getText().toString();
			final String PWD = pwd.getText().toString();
			String CONFIRM = confirm.getText().toString();
			RadioGroup group = (RadioGroup)findViewById(R.id.sex);
			group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup arg0,int id) {
					if(id==R.id.male){
						sex=1;
					}
				}
			});
			if (!PWD.equals(CONFIRM)) {
				Toast.makeText(RegisterActivity.this, "2¥Œ ‰»Î√‹¬Î≤ª“ª÷¬", Toast.LENGTH_SHORT).show();
			} else {
				new Thread(){
					public void run(){
						Looper.prepare();
						Log.d(TAG, "begin register " + "name is " + NAME + " PWD is " + PWD);
						boolean b  = register(NAME, PWD, sex +"");
						Message message = new Message();
						message.what = b == true ? 1:0;
						handler.sendMessage(message);
					}
				}.start();
			}
		}
	}
	public boolean register(String username, String password, String sex){
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost(Constants.REQUEST_HOST,8081,"http");
		PostMethod method = new PostMethod("/api/register");
		NameValuePair []user = {
				new NameValuePair("username", username), 
				new NameValuePair("password", password),
				new NameValuePair("sex", sex)
			};
			method.setRequestBody(user);
		try{
			client.executeMethod(method);
			String response = method.getResponseBodyAsString();
			Log.v("response", response);
			return "0".equals(response) ? false:true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	private Handler handler = new Handler(){
		 public void handleMessage(Message msg){  
            switch(msg.what) {  
            case 1:  
            	Toast.makeText(RegisterActivity.this, "◊¢≤·≥…π¶", Toast.LENGTH_SHORT).show();
        		startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;  
            case 0:
    			Toast.makeText(RegisterActivity.this, "◊¢≤· ß∞‹", Toast.LENGTH_SHORT).show();
            }  
            
        }  
	};
}

