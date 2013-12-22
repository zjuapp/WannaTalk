package com.wannatalk.android.activity;


import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Config;
import com.wannatalk.android.model.PeopleItem;
import com.wannatalk.android.utils.HttpHelper;


public class LoginActivity extends Activity {
	public static final String TAG = "LoginActivity";
	public static String userInfo;
	private String username;
	private int uid;
	protected Button btnLogin;
	protected Button register;
	protected EditText accountEt,passwordEt;
	Dialog dialog;
	boolean returnval = false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_login);
	    // init jpush
	    JPushInterface.init(getApplicationContext());
	    accountEt=(EditText) findViewById(R.id.et_account); //account entered.
	    passwordEt=(EditText) findViewById(R.id.et_password);//password entered.
	    btnLogin=(Button) findViewById(R.id.btn_login);
	    btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				
				if(accountEt.getText().equals("") || passwordEt.getText().equals("")){
					Toast.makeText(LoginActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
				}else{
					dialog = ProgressDialog.show(LoginActivity.this, null,  "登录中", true, true);
					new Thread(){
						public void run(){
							Looper.prepare();
							username = accountEt.getText().toString();
							PeopleItem people = login(accountEt.getText().toString(), passwordEt.getText().toString());
							int b = 0;
							if(people == null) b = 0;
							else{
								Config.makeconfig(people);//login information
								b = 1;
							}
							Message m=new Message();  
	                        m.what= b;
	                        handler.sendMessage(m);
						}
					}.start();
				}
			}
	    });
	    findViewById(R.id.btn_register).setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
			}
	    });
	}
	
	PeopleItem login(final String a, final String p){
				PostMethod method = new PostMethod("/api/login");
				NameValuePair []user = {
						new NameValuePair("username", a), 
						new NameValuePair("password", p)
					};
				method.setRequestBody(user);
			try{
				Config.client.executeMethod(method);
				String response = method.getResponseBodyAsString();
				if("false".equals(response)) return null;
				return HttpHelper.getpeople(response).get(0);
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
	}
	
	private void resetAliasAndTags() {
		JPushInterface.setAliasAndTags(this, ""+ Config.uid, null);
	}
	private Handler handler=new Handler(){  
        public void handleMessage(Message msg){  
        	 dialog.dismiss();
            switch(msg.what) {  
            case 1:  
            	Toast.makeText(LoginActivity.this, "登录成功 -- id is " + Config.uid , Toast.LENGTH_SHORT).show();
            	resetAliasAndTags();
            	startActivity(new Intent(LoginActivity.this, IndexInf.class));
        		finish();
                break;  
            case 0:
    			Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }   
        }  
    };

}

