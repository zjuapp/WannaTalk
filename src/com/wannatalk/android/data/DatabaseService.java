package com.wannatalk.android.data;

import java.util.ArrayList;

import android.content.Context;

import com.wannatalk.android.model.MessageModle;

public class DatabaseService {
	SQLiteHelper helper = null;
	public DatabaseService(Context context) {
		helper = SQLiteHelper.getHelper(context);
	}
	public boolean insertMessage(MessageModle msg) {
		return helper.insertMessage(msg.getContent(), msg.getDate(), msg.getRecvid(), msg.getSendid());
	}
	public ArrayList<MessageModle> get_message_list(int sendId, int recvId, int begin, int num) {
		
		return null;
	}

}
