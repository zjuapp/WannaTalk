package com.wannatalk.android.comm;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


public class WannaTalkApplication extends Application{

    private static final String TAG = "WannaTalkApplication";
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    private static WannaTalkApplication mInstance = null;
    public static final String strKey = "Akk6PX9SwTEUspMVAHQWGzU6";
    @Override
    public void onCreate() {    	     
    	 Log.d(TAG, "onCreate");
         super.onCreate();
         mInstance = this;
         JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
         initEngineManager(this);
    }
    
	public static WannaTalkApplication getInstance() {
		return mInstance;
	}
	
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
 // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), 
                        "请在 WannaTalkApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                WannaTalkApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	WannaTalkApplication.getInstance().m_bKeyRight = true;
            	Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), 
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
