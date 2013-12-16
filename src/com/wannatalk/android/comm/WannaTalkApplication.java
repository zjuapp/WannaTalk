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
         JPushInterface.setDebugMode(true); 	//���ÿ�����־,����ʱ��ر���־
         JPushInterface.init(this);     		// ��ʼ�� JPush
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
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
 // �����¼���������������ͨ�������������Ȩ��֤�����
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//����ֵ��ʾkey��֤δͨ��
            if (iError != 0) {
                //��ȨKey����
                Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), 
                        "���� WannaTalkApplication.java�ļ�������ȷ����ȨKey,������������������Ƿ�������error: "+iError, Toast.LENGTH_LONG).show();
                WannaTalkApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	WannaTalkApplication.getInstance().m_bKeyRight = true;
            	Toast.makeText(WannaTalkApplication.getInstance().getApplicationContext(), 
                        "key��֤�ɹ�", Toast.LENGTH_LONG).show();
            }
        }
    }
}
