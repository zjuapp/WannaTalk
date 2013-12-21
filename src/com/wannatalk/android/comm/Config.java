package com.wannatalk.android.comm;

import org.apache.commons.httpclient.HttpClient;

import com.wannatalk.android.model.PeopleItem;

public class Config {
	public static Boolean sex = false;
	public static int uid = 1;
	public static String imgtext;
	public static int motion;
	public static String happen;
	public static int motionlevel;
	public static String username = null;
	public static HttpClient client = new HttpClient();
	static{
		client.getHostConfiguration().setHost(Constants.REQUEST_HOST,8081,"http");
	}
	public static void makeconfig(PeopleItem a){
		sex = a.sex;
		uid = a.id;
		imgtext = a.imgtext;
		motion = a.motion;
		motionlevel  = a.motionlevel;
		happen = a.happen;
	}
	public static void reset(){
		uid = -1;
	}
}