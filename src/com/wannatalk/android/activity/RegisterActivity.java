package com.wannatalk.android.activity;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
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
import com.wannatalk.android.utils.CompressImage;
import com.wannatalk.android.utils.HttpHelper;

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
	private String photoPath;
	private Context mContext;
	private Bitmap headImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
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
				Log.d(TAG, "btn_head_camera is clicked");
				takeFromCamera();
			}
        	
        });
        menu[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				takeFromGallery();
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
		Log.d(TAG, "take from gallery");
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("return-data", true);
		this.startActivityForResult(intent, GALLERY);
	}
	
	private void takeFromCamera() {
		Log.d(TAG, "take from camera");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			File dir = new File(Environment.getExternalStorageDirectory()+"");
			if(!dir.exists()){
				Log.d(TAG, "make dir " + dir.getAbsolutePath());
				dir.mkdir();
			}
			photoPath = dir + "/" + "image.jpg";
			Log.d(TAG, "photo path is " + photoPath);
			File file = new File(photoPath);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			this.startActivityForResult(intent, CAMERA);
		} else {
			Toast.makeText(this, "SD卡不可用", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == GALLERY) {
			Log.d(TAG, "request code is GALLERY");
			String filePath = "";
			ContentResolver resolver = getContentResolver();
			try {
				Uri originUri = data.getData();
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = resolver.query(originUri, proj, null, null, null);
				if(cursor.moveToFirst()){
					int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					filePath = cursor.getString(columnIndex);
				}
				cursor.close();
				Log.d(TAG, "file path is " + filePath);
				uploadImg(filePath);
				
			}catch(Exception e) {
				Log.d(TAG, "Exception : ", e );
				e.printStackTrace();
			}
		}else if(requestCode == CAMERA){
			uploadImg(photoPath);
			
		}
	}
	
	public void uploadImg(String filePath) {
		Log.d(TAG, "upload img in " + filePath);
		new UploadImage().execute(filePath);
	}
	class UploadImage extends AsyncTask<String , Integer, Boolean> {
		ProgressDialog onUploadDialog;
		int headWidth;
		int headHeight;
		public UploadImage(){
			mPopupWindow.dismiss();
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.activity_register, null);
			ImageView headView = (ImageView) view.findViewById(R.id.iv_head);
			headView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
							 MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			headWidth = headView.getMeasuredWidth();
			headHeight = headView.getMeasuredHeight();
			Log.d(TAG, "head width and height are " + headWidth + " ," + headHeight);
			onUploadDialog = new ProgressDialog(RegisterActivity.this); 
			onUploadDialog.setTitle("上传头像");
			onUploadDialog.setMessage("正在上传");
			onUploadDialog.show();
		}
		
	    @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
	    
		@Override
		protected Boolean doInBackground(String... params) {
			Log.d(TAG, "upload img asynctask is invoked");
			String path = params[0];
			CompressImage ci = new CompressImage(mContext, path, headWidth, headHeight);
			try {
				headImg = ci.getBitmap();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return HttpHelper.postImg(mContext, path, headWidth, headHeight);
		}
		@Override
        protected void onPostExecute(Boolean result) {
			onUploadDialog.dismiss();
			if(result) {
				Toast.makeText(mContext, "上传图片成功", Toast.LENGTH_SHORT).show();
				mHeadView.setImageBitmap(headImg);
				SharedPreferences sp = mContext.getSharedPreferences("user_info", 0);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("head_img", photoPath);
				editor.commit();
			}else {
				Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
			}	
		}
		
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
            	RegisterActivity.this.finish();
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

