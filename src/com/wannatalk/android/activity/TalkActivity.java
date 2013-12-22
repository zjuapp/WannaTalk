package com.wannatalk.android.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.ChatAdapter;
import com.wannatalk.android.comm.ChatEntity;
import com.wannatalk.android.comm.Config;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.comm.RefreshableView;
import com.wannatalk.android.comm.RefreshableView.PullToRefreshListener;
import com.wannatalk.android.data.DatabaseService;
import com.wannatalk.android.model.MessageModle;
import com.wannatalk.android.utils.HttpHelper;
import com.wannatalk.android.utils.StringUtils;

public class TalkActivity extends Activity{
	public static final String TAG = "TalkActivity";
	public static boolean isForeground = false;
	public static int fid   = 0;
	private String      mesg = null;
	private Handler mHandler = null;
	private DatabaseService ds = new DatabaseService(this);
	boolean isNotify = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字)
		//此设定必须要写在setContentView之前，否则会有异常）
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		isForeground = true;
		Intent intent = this.getIntent();
		fid = intent.getExtras().getInt("friendId");
		isNotify = intent.getExtras().getBoolean("notification", false);
		if(isNotify){
			mesg = intent.getExtras().getString("content"); 
		}
		initView();
		init();
		registerMessageReceiver();
	}
	
	private TextView mTextView = null;
	private EditText mEditText = null;
	private ListView mListView = null;
	private RefreshableView refreshableView = null;
	private ArrayList<ChatEntity> chatList = new ArrayList<ChatEntity>();
	private ChatAdapter adapter = null;
	
	/**
	 * init view
	 */
	private void initView() {
		setContentView(R.layout.talk_view);
		Button sendButton = (Button)findViewById(R.id.btn_send);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		mListView = (ListView) findViewById(R.id.mesgList);
		mEditText = (EditText) findViewById(R.id.et_mesg);
		if(refreshableView != null) {
			refreshableView.setOnRefreshListener(new PullToRefreshListener(){
				@Override
				public void onRefresh() {
					Log.d(TAG, "refreshing");
					try {  
	                    Thread.sleep(3000);  
	                } catch (InterruptedException e) {  
	                    e.printStackTrace();  
	                }  
	                refreshableView.finishRefreshing();  
	            }  
	        }, 0);
		} else {
			Log.e(TAG, "refreshable view is null");
		}
		sendButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mesg = mEditText.getText().toString(); 
				if(mesg.equals("")){
					Log.e(TAG, "message is empty");
					Toast toast = Toast.makeText(TalkActivity.this, "消息不能为空", Toast.LENGTH_SHORT);
					toast.show();
					return;
				} else {
					Log.d(TAG, "send message " + mesg);
					ChatEntity entity = new ChatEntity();
					entity.setChatTime(StringUtils.getCurTime());
					entity.setCome(false);
					entity.setMessage(mEditText.getText().toString());
					chatList.add(entity);
					adapter.notifyDataSetChanged();	
					mEditText.setText("");
					new Thread(){
						@Override
						public void run() {
							pushMessage(mesg, "" + fid);
							//pushMessage(mesg, "" + Config.uid);
						}
					}.start();
				}
			}
		});
		adapter = new ChatAdapter(this, chatList);
		if(isNotify){
			ChatEntity entity = new ChatEntity();
			entity.setChatTime(StringUtils.getCurTime());
			entity.setCome(true);
			entity.setMessage(mesg);
			chatList.add(entity);
		}
		mListView.setAdapter(adapter);
	}
	
	/**
	 * init handler
	 */
	private void init(){
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case Constants.GET_MESSAGE:
					Bundle bundle = msg.getData();
					String content = bundle.getString("content");
					ChatEntity entity = new ChatEntity();
					entity.setChatTime(StringUtils.getCurTime());
					entity.setCome(true);
					entity.setMessage(content);
					chatList.add(entity);
					adapter.notifyDataSetChanged();
					break;
				}
			}
		};
	}
	
	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	protected void onStop() {
		isForeground = false;
		try{
			this.unregisterReceiver(mMessageReceiver);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onStop();
	}
	
	private MessageReceiver mMessageReceiver;
	
	private void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}
	public static final String MESSAGE_RECEIVED_ACTION = "com.wannatalk.android.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	private class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "message receiver receive a message and action is " + intent.getAction());
			if(MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String mesg = intent.getStringExtra(KEY_MESSAGE);
				Log.d(TAG, "message is " + mesg);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				showMsg(mesg);
			}	
		}
	}
	
	private void showMsg(String content) {
		Message msg = new Message();
		Bundle data = new Bundle();
		msg.what = Constants.GET_MESSAGE;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = format.format(new Date());
		data.putString("content", content);
		data.putString("time",time);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
	
	
	private void pushMessage(String mesg, String target) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("message", mesg);
		params.put("friend", target);
		params.put("uid", ""+Config.uid);
		if(ds.insertMessage(new MessageModle(mesg, StringUtils.getCurTime(),Config.uid,Config.uid ))) {
			Log.d(TAG, "insert message ok");
		}else {
			Log.d(TAG, "insert message failed");
		}
		try {
			HttpHelper.post(Constants.PUSH_MESG, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

