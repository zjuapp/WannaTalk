package com.wannatalk.android.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.wannatalk.android.R;
import com.wannatalk.android.activity.TalkActivity;
import com.wannatalk.android.utils.StringUtils;

public class TalkReceiver extends BroadcastReceiver{
	private static final String TAG = "TalkRecevier";
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        mContext = context;
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "����Registration Id : " + regId);
            //send the Registration Id to your server...
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "����UnRegistration Id : " + regId);
          //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "���յ������������Զ�����Ϣ: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	try {
				processCustomMessage(context, bundle);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "���յ�����������֪ͨ");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "���յ�����������֪ͨ��ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "�û��������֪ͨ");
            
        	//���Զ����Activity
        	Intent i = new Intent(context, TalkActivity.class);
        	i.putExtras(bundle);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "�û��յ���RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //��������� JPushInterface.EXTRA_EXTRA �����ݴ�����룬������µ�Activity�� ��һ����ҳ��..
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}

	// ��ӡ���е� intent extra ����
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to TalkActivity
	private void processCustomMessage(Context context, Bundle bundle) throws JSONException {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Toast.makeText(context, extras, Toast.LENGTH_SHORT).show();
		JSONObject json = new JSONObject(extras);
		String fid = json.optString("fid");
		Toast.makeText(context, "fid is " + fid, Toast.LENGTH_SHORT).show();
		if (TalkActivity.isForeground && Integer.parseInt(fid) == TalkActivity.fid) {
			Log.d(TAG, "TalkActivity is foreground");
			Intent msgIntent = new Intent(TalkActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(TalkActivity.KEY_MESSAGE, message);
			if (!StringUtils.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(TalkActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			context.sendBroadcast(msgIntent);
			return;
		}
		String tikerText = "����Ϣ";
		String title = "����Ϣ";
		String content = message;
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(mContext)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle(title)
			    .setContentText(content);
		Intent intent = new Intent(mContext, TalkActivity.class);
		intent.putExtra("notification", true);
		intent.putExtra("friendId", Integer.parseInt(fid));
		intent.putExtra("content", message);
		PendingIntent pIntent = PendingIntent.getActivity(mContext,0,intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pIntent);
		NotificationManager mNotifyMgr = 
		        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotifyMgr.notify(10, notification);
	}
}
