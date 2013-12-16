package com.wannatalk.android.model;

import android.provider.BaseColumns;

public class MessageModle {
	
	private int sendid;
	private int recvid;
	private String content;
	private String date; // yyyy-MM-dd hh:mm:ss
	
	public MessageModle(String content, String date, int sendid, int recvid){
		this.sendid = sendid;
		this.recvid = recvid;
		this.content = content;
		this.date = date;
	}
	public int getSendid() {
		return sendid;
	}
	public void setSendid(int sendid) {
		this.sendid = sendid;
	}
	public int getRecvid() {
		return recvid;
	}
	public void setRecvid(int recvid) {
		this.recvid = recvid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public static abstract class MessageEntry implements BaseColumns {
		public static final String TABLE_NAME              = "message";
		public static final String COLUMN_NAME_SENDER_ID   = "senderid";
		public static final String COLUMN_NAME_RECEIVER_ID = "recvid";
		public static final String COLUMN_NAME_CONTENT     = "content";
		public static final String COLUMN_NAME_DATE        = "date";
	}
}
