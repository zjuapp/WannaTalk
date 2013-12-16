package com.wannatalk.android.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.wannatalk.android.model.MessageModle.MessageEntry;

public class SQLiteHelper extends SQLiteOpenHelper {
	public static final String TAG = "SQLiteHelper";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "talk.db";
	private static SQLiteHelper helper = null;
	private SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static SQLiteHelper getHelper(Context context){
		if(helper == null) {
			helper = new SQLiteHelper(context);
		}
		return helper;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		ArrayList<String> tableList = new ArrayList<String>(); 
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		while(cursor.moveToNext()){
			String name = cursor.getString(0);
			if(name.equals("android_metadata")){
				continue;
			}else {
				tableList.add(name);
			}
		}
		if(!tableList.contains("message")){
			Log.d(TAG, this.DB_SCRIPT_CREATE_TABLE_MESSAGE);
			SQLiteStatement statement = db.compileStatement(this.DB_SCRIPT_CREATE_TABLE_MESSAGE);
			statement.execute();
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(this.DB_SCRIPT_DROP_TABLE_MESSAGE);
        onCreate(db);
	}	
	
	public boolean insertMessage(String content, String date ,int recvId, int sendId){
		Log.d(TAG, "insert message is invoked");
		if(helper == null){
			Log.d(TAG, "error : helper is null");
			return false;
		}
		Log.d(TAG, "content: " + content + " date: " + date + " recvId: " + recvId + " sendId: " + sendId);
		SQLiteDatabase db = helper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(MessageEntry.COLUMN_NAME_CONTENT, content);
		values.put(MessageEntry.COLUMN_NAME_DATE, date);
		values.put(MessageEntry.COLUMN_NAME_RECEIVER_ID,recvId);
		values.put(MessageEntry.COLUMN_NAME_SENDER_ID,sendId);
		long rowId = db.insert(MessageEntry.TABLE_NAME, null, values);
		Log.d(TAG, "rowId is " + rowId);
		return rowId != -1;
	}
	
	public Cursor readMessage(int recvId, int sendId){
		if(helper == null){
			Log.d(TAG, "error : helper is null");
			return null;
		}
		SQLiteDatabase db = helper.getReadableDatabase();
		String []projection = {
			MessageEntry._ID,
			MessageEntry.COLUMN_NAME_CONTENT,
			MessageEntry.COLUMN_NAME_DATE
		};
		String sortOrder = MessageEntry._ID + "DESC";
		String selection = 
				"where " + MessageEntry.COLUMN_NAME_RECEIVER_ID +" = ? " + " and " +  MessageEntry.COLUMN_NAME_SENDER_ID + " = ?";
		String []selectionArgs = {""+recvId,""+sendId };
		Cursor c = db.query(MessageEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);  
		return c;
	}
	
	private static final String DB_SCRIPT_CREATE_TABLE_MESSAGE = 
			"create table " + MessageEntry.TABLE_NAME + " ( "+
			MessageEntry._ID + "integer primary key, " + 
			MessageEntry.COLUMN_NAME_RECEIVER_ID + " integer ,"+
			MessageEntry.COLUMN_NAME_SENDER_ID + " integer ," + 
			MessageEntry.COLUMN_NAME_CONTENT + " text, " +
			MessageEntry.COLUMN_NAME_DATE + " varchar(128) " +
			" )";
	private static final String DB_SCRIPT_DROP_TABLE_MESSAGE = 
			"drop table if exists" + MessageEntry.TABLE_NAME;
			
}
