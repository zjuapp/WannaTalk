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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Constants;

public class RegisterActivity extends Activity implements OnClickListener {
	public static final String TAG = "RegisterActivity";
	private static final int GALLERY = 20;
	private static final int CAMERA = 21;
	protected Button register;
	protected Button cancel;
	protected EditText name;
	protected EditText pwd;
	protected EditText confirm;
	protected boolean b = false;
	protected int sex = 0; 
	private ImageView mHeadView = null;
	private PopupWindow mPopupWindow = null;
	private View mMenuView = null;
	private boolean isPopupWindowShowing = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		name = (EditText) findViewById(R.id.name);
		pwd = (EditText) findViewById(R.id.pwd);
		confirm = (EditText) findViewById(R.id.confirm);
		register = (Button) findViewById(R.id.register);
		cancel = (Button) findViewById(R.id.cancel);
		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mMenuView = mLayoutInflater.inflate(R.layout.update_head_menu_view, null);
		mPopupWindow = new PopupWindow(mMenuView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopupWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_popmenu));
        mPopupWindow.update();
        mPopupWindow.setTouchable(true);
        /*设置点击menu以外其他地方以及返回键退出*/
        mPopupWindow.setFocusable(true);
        View[] menu = new View[3];
        menu[0] = mMenuView.findViewById(R.id.btn_head_camera);
        menu[1] = mMenuView.findViewById(R.id.btn_head_photo);
        menu[2] = mMenuView.findViewById(R.id.btn_cancel);
        menu[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				
			}
        	
        });
        menu[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
        	
        });
      	mHeadView = (ImageView) findViewById(R.id.iv_head);
		mHeadView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				if(!isPopupWindowShowing){
					mPopupWindow.showAtLocation(RegisterActivity.this.findViewById(R.id.register_layout), Gravity.BOTTOM, 0, 0);
				}
			}
			
		});
	}

	private void takeFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("return-data", true);
		this.startActivityForResult(intent, GALLERY);
	}
	
	private void takeFromCamera() {
		
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
				Toast.makeText(RegisterActivity.this, "2次输入密码不一致", Toast.LENGTH_SHORT).show();
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
            	Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
        		startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                break;  
            case 0:
    			Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }  
            
        }  
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
}

