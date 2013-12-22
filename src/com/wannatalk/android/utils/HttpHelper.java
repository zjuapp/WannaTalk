package com.wannatalk.android.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.wannatalk.android.comm.Config;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.model.PeopleItem;


public class HttpHelper {
	public final static String TAG = "HttpHelper";
	private final static int READ_TIMEOUT    = 1000 * 100;
	private final static int CONNECT_TIMEOUT = 1000 * 100;
	private final static String CHARSET = "UTF-8";
	
	
	public static String post(String path, Map<String, String> params) throws Exception {
		Log.d(TAG, "http helper is invoked");
		Log.d(TAG, "http helper reveived : " + StringUtils.parseParams(params) );
		BufferedReader in = null;
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost request = new HttpPost(Constants.REQUEST_PATH + path);
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				Log.d(TAG, "key is " + entry.getKey() + " and value is " + entry.getValue());
				postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParams, "UTF-8");
			request.setEntity(formEntity);
			HttpResponse response = httpClient.execute(request);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.i(TAG, "invoke " + path );
				in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder sb = new StringBuilder("");
				String line;
				while((line = in.readLine()) != null ) {
					sb.append(line);
				}
				return sb.toString();
			}else {
				Log.d(TAG, "http helper post error!");
				return null;
			}
		} catch (Exception e) {
			Log.e(TAG, "post exception", e);
		} finally {
			if(in != null) {
				in.close();
			}
		}
		return null;


	}
	
	public static boolean postImg(Context context, String filePath, int width, int height){
		Log.d(TAG, "postImg is invoked");
		HttpClient httpClient = new DefaultHttpClient();
		File fileTemp = null;
		try{
			HttpPost request = new HttpPost(Constants.REQUEST_PATH + Constants.PUSH_IMG + "?uid="+Config.uid + "&filename="+filePath);
			MultipartEntityBuilder  entityBuilder =  MultipartEntityBuilder.create();
			CompressImage compress = new CompressImage(context, filePath, width, height);
			Bitmap bm = compress.getBitmap();
			String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/"+System.currentTimeMillis()+".jpg";
			FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
			bm.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
			fileTemp = new File(path);
			entityBuilder.addPart("file", new FileBody(fileTemp));
			request.setEntity(entityBuilder.build());
			HttpResponse response = httpClient.execute(request);	
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.d(TAG, "upload image ok!");
				return true;
			}
			
		}catch(Exception e){
			Log.d(TAG, "Exception : ", e);
			e.printStackTrace();
		} finally{
			if(fileTemp != null && fileTemp.exists()){
				fileTemp.delete();
			}			
		}
		return false;
	}
	
	
	public static List <PeopleItem> getpeople(String response) throws ParserConfigurationException, SAXException, IOException{
		response = response.substring(response.indexOf("<"));
		List <PeopleItem> arr = new ArrayList<PeopleItem>();
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document document = dBuilder.parse(new ByteArrayInputStream(response.getBytes()));
		NodeList userlist= document.getElementsByTagName("user");
		for(int i = 0; i < userlist.getLength(); ++i){
			PeopleItem tmpItem = new PeopleItem();
			org.w3c.dom.Element item = (org.w3c.dom.Element)userlist.item(i);
			tmpItem.id = Integer.valueOf(item.getElementsByTagName("uid").item(0).getFirstChild().getNodeValue());
			tmpItem.sex = Boolean.valueOf(item.getElementsByTagName("sex").item(0).getFirstChild().getNodeValue());
			if(item.getElementsByTagName("signature").item(0).getFirstChild() == null){
				tmpItem.happen = "";
			}
			else tmpItem.happen = item.getElementsByTagName("signature").item(0).getFirstChild().getNodeValue();
			tmpItem.motion = Integer.valueOf(item.getElementsByTagName("motion").
				item(0).getFirstChild().getNodeValue());	
			tmpItem.motionlevel = Integer.valueOf(item.getElementsByTagName("motionlevel").
					item(0).getFirstChild().getNodeValue());
			arr.add(tmpItem);
		}
		return arr;
	}
}
