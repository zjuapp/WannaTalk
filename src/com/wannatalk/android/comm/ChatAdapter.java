package com.wannatalk.android.comm;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wannatalk.android.R;
import com.wannatalk.android.activity.UserInfoActivity;
import com.wannatalk.android.comm.*;

public class ChatAdapter extends BaseAdapter{
	public static final String TAG = "ChatAdapter";
	private Context mContext = null;
	private ArrayList<ChatEntity> mChatList = null;
	private LayoutInflater mInflater = null;
	public ChatAdapter(Context context, ArrayList<ChatEntity> chatList) {
		mContext = context;
		mChatList = chatList;
		Log.d(TAG ,"list has " + chatList.size() + " items");
		mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return mChatList.size();
	}

	@Override
	public Object getItem(int position) {
		return mChatList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return mChatList.get(position).isCome() ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatHolder chatHolder = null;
		final int pos = position; 
		if(convertView == null) {
			chatHolder = new ChatHolder();
			if(mChatList.get(position).isCome()) {
				convertView = mInflater.inflate(R.layout.chat_from_item, null);
			} else {
				convertView = mInflater.inflate(R.layout.chat_to_item, null);
			}
			chatHolder.timeTextView = (TextView) convertView.findViewById(R.id.time_text);
			chatHolder.mesgTextView = (TextView) convertView.findViewById(R.id.mesg_text);
			chatHolder.userImageView = (ImageView) convertView.findViewById(R.id.head_img);
			chatHolder.userImageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext,UserInfoActivity.class);
					intent.putExtra("uid", mChatList.get(pos).getUid());
					//mContext.startActivity(intent);
				}
				
			});
			convertView.setTag(chatHolder);
		} else {
			chatHolder = (ChatHolder) convertView.getTag();
		}
		if(chatHolder.timeTextView == null) { 
			Log.e(TAG, "chatHolder.timeTextView is null");
		}
		chatHolder.timeTextView.setText(mChatList.get(position).getChatTime());
		chatHolder.mesgTextView.setText(mChatList.get(position).getMessage());
		chatHolder.userImageView.setImageResource(mChatList.get(position).getUserImage());
		return convertView;

	}
	private class ChatHolder {
		private TextView timeTextView;
		private ImageView userImageView;
		private TextView mesgTextView;
		
	}


}
