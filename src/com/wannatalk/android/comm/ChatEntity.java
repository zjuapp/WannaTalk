package com.wannatalk.android.comm;

public class ChatEntity {
	private int userImage;
	private String message;
	private String chatTime;
	private boolean isCome;
	private int uid;
	public int getUserImage() {
		return userImage;
	}
	public void setUserImage(int userImage) {
		this.userImage = userImage;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChatTime() {
		return chatTime;
	}
	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}
	public boolean isCome() {
		return isCome;
	}
	public void setCome(boolean isCome) {
		this.isCome = isCome;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	
}
