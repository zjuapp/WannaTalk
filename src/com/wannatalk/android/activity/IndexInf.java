package com.wannatalk.android.activity;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.NodeList;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.Symbol.Stroke;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wannatalk.android.activity.SigModify;
import com.wannatalk.android.R;
import com.wannatalk.android.model.*;
import com.wannatalk.android.utils.HttpHelper;
import com.wannatalk.android.comm.Config;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.comm.WannaTalkApplication;

public class IndexInf extends Activity{


	/***************index_search_inf************************/
	private GeoPoint centerpoint = null;
	private MapController mapController = null;
	private MKSearch mSearch = null;
	private MKMapViewListener mMapListener = null;
	private MKMapTouchListener mapTouchLister = null;
	private android.app.AlertDialog.Builder search_people = null;
	private MapView mapView = null;
 	private LocationClient local = null;
 	private PeopleItem User = null;
	Dialog dlg;
	/***************index_person_inf************************/
	protected ImageButton modifyInfo;
	protected RatingBar rt_mood;
	protected TextView info_username;
	protected TextView info_sig;
	protected TextView info_mood;
	public static float mood_rating;
	public static int moodid;
	public static String sig;
	
	private int dis(GeoPoint a, GeoPoint b){
		return (int)Math.sqrt(((long)(a.getLatitudeE6() - b.getLatitudeE6())) * (a.getLatitudeE6() - b.getLatitudeE6()) + 
				((long)(a.getLongitudeE6() - b.getLongitudeE6())) * (a.getLongitudeE6() - b.getLongitudeE6()));
	}
	public Graphic drawCircle(GeoPoint p) {
			//����Բ*/
			int dis = dis(p, mapView.getMapCenter());
	  		Geometry circleGeometry = new Geometry();
	  		//����Բ���ĵ�����Ͱ뾶
	  		circleGeometry.setCircle(p, dis / 10);
	  		//������ʽ
	  		Symbol circleSymbol = new Symbol();
	 		Symbol.Color circleColor = circleSymbol.new Color();
	 		circleColor.red = 0;
	 		circleColor.green = 255;
	 		circleColor.blue = 0;
	 		circleColor.alpha = 126;
	  		circleSymbol.setSurface(circleColor,1,3, new Stroke(3, circleSymbol.new Color(0xFFFF0000)));
	  		//����Graphic����
	  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
	  		return circleGraphic;
	   }
	private void init_index_person_inf(){
		modifyInfo = (ImageButton) findViewById(R.id.info_button);
		modifyInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(IndexInf.this, SigModify.class);
				IndexInf.this.startActivity(intent);
			}
		});
		rt_mood = (RatingBar) findViewById(R.id.rb_mood);
		info_sig =(TextView) findViewById(R.id.text_sig);
		info_mood =(TextView) findViewById(R.id.text_mood);
		moodid = Config.motion;
		mood_rating = (float) (Config.motionlevel * 1.0 / 2);
		sig = Config.happen;
	}
	private void init_index_search(){
		mapView = (MapView)findViewById(R.id.search_map);
		CharSequence textCharSequence = "�����������";
		setTitle(textCharSequence);
		centerpoint = new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6));
		User = new PeopleItem();
		User.id = 106;
		Button search_button = (Button)findViewById(R.id.search_button);
		final EditText edit = (EditText)findViewById(R.id.search_edit);
		initmap();
		local = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption(); 
        option.setCoorType("gcj02"); 
        option.setPriority(LocationClientOption.NetWorkFirst); // ������������ 
        option.setProdName("demo"); // ���ò�Ʒ������ 
        local.setLocOption(option);
        local.registerLocationListener(new BDLocationListener() { 
			@Override
			public void onReceiveLocation(BDLocation location) { 
				if(location == null) return;
				centerpoint.setLatitudeE6((int) (location.getLatitude() * 1e6));
				centerpoint.setLongitudeE6((int) (location.getLongitude() * 1e6));
				String strInfo = String.format("γ�ȣ�%f ���ȣ�%f", location.getLatitude(), location.getLongitude());
				Toast.makeText(IndexInf.this, strInfo.toString(), Toast.LENGTH_LONG).show();
			}
			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        local.start();//���������ȡλ�÷ֿ����Ϳ��Ծ������ں����ʹ����
		final Geocoder geo = new Geocoder(this, Locale.CHINA);
		final MapView mapView = (MapView)findViewById(R.id.search_map);
		Button	syn_button = (Button)findViewById(R.id.setpos_button);
		 mSearch = new MKSearch();
	        mSearch.init(((WannaTalkApplication)this.getApplication()).mBMapManager, new MKSearchListener() {
	            @Override
	            public void onGetPoiDetailSearchResult(int type, int error) {
	            }
				public void onGetAddrResult(MKAddrInfo res, int error) {
					if (error != 0) {
						String str = String.format("����ţ�%d", error);
						Toast.makeText(IndexInf.this, str, Toast.LENGTH_LONG).show();
						return;
					}
					//��ͼ�ƶ����õ�
				
					mapView.getController().animateTo(res.geoPt);
					centerpoint.setLatitudeE6((int)(res.geoPt.getLatitudeE6()));
					centerpoint.setLongitudeE6((int)(res.geoPt.getLongitudeE6()));
					if (res.type == MKAddrInfo.MK_GEOCODE){
						//�������룺ͨ����ַ���������
						String strInfo = String.format("γ�ȣ�%f ���ȣ�%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
						Toast.makeText(IndexInf.this, strInfo, Toast.LENGTH_LONG).show();
					}
					if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
						//���������룺ͨ������������ϸ��ַ���ܱ�poi
						String strInfo = res.strAddr;
						Toast.makeText(IndexInf.this, strInfo, Toast.LENGTH_LONG).show();
						
					}
					//����ItemizedOverlayͼ��������ע�����
					ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mapView);
					//����Item
					OverlayItem item = new OverlayItem(res.geoPt, "", null);
					//�õ���Ҫ���ڵ�ͼ�ϵ���Դ
					Drawable marker = getResources().getDrawable(R.drawable.icon_markf);  
					//Ϊmaker����λ�úͱ߽�
					marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
					//��item����marker
					item.setMarker(marker);
					//��ͼ��������item
					itemOverlay.addItem(item);
					
					//�����ͼ����ͼ��
					mapView.getOverlays().clear();
					//����һ����עItemizedOverlayͼ��
					mapView.getOverlays().add(itemOverlay);
					//ִ��ˢ��ʹ��Ч
					mapView.refresh();
				}
				public void onGetPoiResult(MKPoiResult res, int type, int error) {
					
				}
				public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
				}
				public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
				}
				public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
				}
				public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				}
				@Override
				public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
					
				}
				@Override
				public void onGetShareUrlResult(MKShareUrlResult result, int type,
						int error) {
					// TODO Auto-generated method stub
					
				}

	        });
	        
		search_button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editplace = (EditText)findViewById(R.id.search_edit);
				mSearch.geocode(editplace.getText().toString(), "");
			}
		});
		syn_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (local != null && local.isStarted()){
					local.requestLocation();
					mSearch.reverseGeocode(centerpoint);
					new Thread(){
						public void run(){	
							Looper.prepare();
							int res = synchronize(User.id, centerpoint.getLatitudeE6(), centerpoint.getLongitudeE6());
							Message message = new Message();
							message.what = res;
							handle_syn.sendMessage(message);
						}
					}.start();
				}
			}
		});
	}
	private void initmap(){
		final WannaTalkApplication app = (WannaTalkApplication)this.getApplication(); 
		search_people = new AlertDialog.Builder(this);
		if (app.mBMapManager == null) { //init
            app.mBMapManager = new BMapManager(this);
            app.mBMapManager.init(WannaTalkApplication.strKey,new WannaTalkApplication.MyGeneralListener());
        }
		mapController = mapView.getController();
        mapController.enableClick(true);
        mapController.setZoom(16);
   	 	mapController.setCenter(centerpoint);
   	 	mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * �ڴ˴�����ͼ�ƶ���ɻص�
				 * ���ţ�ƽ�ƵȲ�����ɺ󣬴˻ص�������
				 */
				
			}
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * �ڴ˴�����ͼpoi����¼�
				 * ��ʾ��ͼpoi���Ʋ��ƶ����õ�
				 * ���ù��� mMapController.enableClick(true); ʱ���˻ص����ܱ�����
				 * 
				 */
				}
			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  �����ù� mMapView.getCurrentMap()�󣬴˻ص��ᱻ����
				 *  ���ڴ˱����ͼ���洢�豸
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  ��ͼ��ɴ������Ĳ�������: animationTo()���󣬴˻ص�������
				 */
			}
         /**
          * �ڴ˴�����ͼ������¼� 
          */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(IndexInf.this, 
						       "��ͼ�������", 
						       Toast.LENGTH_SHORT).show();
			}
		};
		mapTouchLister  = new MKMapTouchListener(){
			@Override
			public void onMapClick(final GeoPoint point) {
				mapView.getOverlays().clear();
				GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mapView);
		        mapView.getOverlays().add(graphicsOverlay);
		    	//���ӵ�
		        graphicsOverlay.setData(drawCircle(point));
		        mapView.refresh();
		        search_people.setPositiveButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						final Bundle data = new Bundle();
						final Intent intent = new Intent(IndexInf.this, ShowPeople.class);
						dlg = ProgressDialog.show(IndexInf.this, null,  "���ڻ�ȡ��,����", true, true);
						new Thread(){
							public void run(){	
							Looper.prepare();
			    			PostMethod method = new PostMethod("/api/search");
			    			NameValuePair []user = {
			    					new NameValuePair("lat", "0"),//Integer.toString(point.getLatitudeE6())), 
			    					new NameValuePair("lon", "0"),//Integer.toString(point.getLongitudeE6())),
			    					new NameValuePair("r", "1")//Integer.toString(dis(mapView.getMapCenter(), point)))
			    			};	
			    			method.setRequestBody(user);
			    			try{
			    				Config.client.executeMethod(method);
			    				String response = method.getResponseBodyAsString();
			    				List <PeopleItem> arr = HttpHelper.getpeople(response);
			    				data.putInt("numpeople", arr.size());
			    				for(int i = 0; i < arr.size(); ++i){
			    					data.putSerializable("people" + i, arr.get(i));
			    				}
			    				Log.d("SearchMap", Integer.toString(arr.size()));
			    				dlg.dismiss();
			    				intent.putExtras(data);
			    				startActivity(intent);
			    			} 
			    			catch(Exception e){
			    				Log.v("SearchMap", "begin call search error", e);
			    				e.printStackTrace();
			    				dlg.dismiss();
			    			}
			    			}
			    		}.start();	
					}
		        }
				);
		        search_people.setOnCancelListener(new OnCancelListener(){
					@Override
					public void onCancel(DialogInterface arg0) {
						// TODO Auto-generated method stub
						mapView.getOverlays().clear();
						mapView.refresh();
					}
				});
		        search_people.create().show();
			}
			@Override
			public void onMapDoubleClick(GeoPoint point) {
			
			}
			
			@Override
			public void onMapLongClick(GeoPoint point) {
			
			}
        };
        mapView.regMapTouchListner(mapTouchLister);
		mapView.regMapViewListener(WannaTalkApplication.getInstance().mBMapManager, mMapListener);
	}
	/*ͬ�� �ص� ȷ�� index*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchmap);
		init_index_person_inf();
		init_index_search();
	}
	private int synchronize(int id, int lat, int lon){
		PostMethod method = new PostMethod("/api/update");
		NameValuePair []user = {
			new NameValuePair("lat", Integer.toString(lat)),//Integer.toString(centerpoint.getLatitudeE6())), 
			new NameValuePair("lon", Integer.toString(lon)),//Integer.toString(centerpoint.getLongitudeE6())),
			new NameValuePair("id", Integer.toString(id))//Integer.toString(dis(centerpoint, point)))
		};	
		method.setRequestBody(user);
		try{
			Config.client.executeMethod(method);
			String response = method.getResponseBodyAsString();
			if("true".equals(response))
				return 1;
			else 
				return 0;
			
		}
		catch(Exception e){
			Log.v("network", "update error");
			return 0;
		}
	}
	private Handler handle_syn = new Handler(){
		public void handleMessage(Message msg){  
       	   switch(msg.what) {  
           case 1:  
           	Toast.makeText(IndexInf.this, "ͬ���ɹ�", Toast.LENGTH_SHORT).show();
            break;  
           case 0:
   			Toast.makeText(IndexInf.this, "ͬ��ʧ��", Toast.LENGTH_SHORT).show();
           }  
       }  
	};
	protected void onResume(){
		  super.onResume();
		  rt_mood.setRating(Config.motionlevel * 2);
		  info_mood.setText(Constants.ModState[Config.motion]);
		  info_sig.setText(Config.happen);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.addCategory(Intent.CATEGORY_HOME);
			startActivity(MyIntent);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.clear();
		menu.add("�˳�");
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(featureId) {
			case 0:
				this.finish();
				
				break;
		}
		return true;

	}
	
}