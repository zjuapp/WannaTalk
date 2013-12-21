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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.wannatalk.android.R;
import com.wannatalk.android.model.*;
import com.wannatalk.android.comm.Config;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.comm.WannaTalkApplication;

public class SearchMap extends Activity{
	//private final MapView mapView = (MapView)findViewById(R.id.search_map);
	//private final GeoPoint centerpoint = new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6));
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
	private int dis(GeoPoint a, GeoPoint b){
		return (int)Math.sqrt(((long)(a.getLatitudeE6() - b.getLatitudeE6())) * (a.getLatitudeE6() - b.getLatitudeE6()) + 
				((long)(a.getLongitudeE6() - b.getLongitudeE6())) * (a.getLongitudeE6() - b.getLongitudeE6()));
	}
	public Graphic drawCircle(GeoPoint p) {
			//构建圆*/
			int dis = dis(p, mapView.getMapCenter());
	  		Geometry circleGeometry = new Geometry();
	  		//设置圆中心点坐标和半径
	  		circleGeometry.setCircle(p, dis / 10);
	  		//设置样式
	  		Symbol circleSymbol = new Symbol();
	 		Symbol.Color circleColor = circleSymbol.new Color();
	 		circleColor.red = 0;
	 		circleColor.green = 255;
	 		circleColor.blue = 0;
	 		circleColor.alpha = 126;
	  		circleSymbol.setSurface(circleColor,1,3, new Stroke(3, circleSymbol.new Color(0xFFFF0000)));
	  		//生成Graphic对象
	  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
	  		return circleGraphic;
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
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
				
			}
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				}
			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}
         /**
          * 在此处理地图载完成事件 
          */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(SearchMap.this, 
						       "地图加载完成", 
						       Toast.LENGTH_SHORT).show();
			}
		};
		mapTouchLister  = new MKMapTouchListener(){
			@Override
			public void onMapClick(final GeoPoint point) {
				mapView.getOverlays().clear();
				GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mapView);
		        mapView.getOverlays().add(graphicsOverlay);
		    	//添加点
		        graphicsOverlay.setData(drawCircle(point));
		        mapView.refresh();
		        search_people.setPositiveButton("查找", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						final Bundle data = new Bundle();
						final Intent intent = new Intent(SearchMap.this, ShowPeople.class);
						dlg = ProgressDialog.show(SearchMap.this, null,  "正在获取中,表急", true, true);
						new Thread(){
							public void run(){	
								Looper.prepare();
			    			HttpClient client = new HttpClient();
			    			client.getHostConfiguration().setHost(Constants.REQUEST_HOST,8081,"http");
			    			PostMethod method = new PostMethod("/api/search");
			    			NameValuePair []user = {
			    					new NameValuePair("lat", "0"),//Integer.toString(point.getLatitudeE6())), 
			    					new NameValuePair("lon", "0"),//Integer.toString(point.getLongitudeE6())),
			    					new NameValuePair("r", "1")//Integer.toString(dis(mapView.getMapCenter(), point)))
			    			};	
			    			method.setRequestBody(user);
			    			try{
			    				List <PeopleItem> arr = new ArrayList<PeopleItem>();
			    				client.executeMethod(method);
			    				Log.d("SearchMapp", "begin call search");
			    				String response = method.getResponseBodyAsString();
			    				Log.d("SearchMapp", "begin call search" + response);
			    				Log.d("Searchsizep", "begin call search" + Integer.toString(response.length()));
			    				response = response.substring(response.indexOf("<"));
			    				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			    				org.w3c.dom.Document document = dBuilder.parse(new ByteArrayInputStream(response.getBytes()));
			    				NodeList userlist= document.getElementsByTagName("user");
			    				Log.d("SearchMapp", "begin call search fuck" + response);
			    				for(int i = 0; i < userlist.getLength(); ++i){
			    					PeopleItem tmpItem = new PeopleItem();
			    					org.w3c.dom.Element item = (org.w3c.dom.Element)userlist.item(i);
			    					tmpItem.id = Integer.valueOf(item.getElementsByTagName("uid").item(0).getFirstChild().getNodeValue());
			    					Log.d("id", "begin call search" + tmpItem.id);
			    					
			    					tmpItem.sex = Boolean.valueOf(item.getElementsByTagName("sex").item(0).getFirstChild().getNodeValue());
			    					Log.d("sex", "begin call search" + tmpItem.sex);
					    			
			    					if(item.getElementsByTagName("signature").item(0).getFirstChild() == null){
			    						tmpItem.happen = "";
			    					}
			    					else tmpItem.happen = item.getElementsByTagName("signature").item(0).getFirstChild().getNodeValue();
			    					Log.d("happen", "begin call search" + tmpItem.happen);
				    				
			    					
			    					tmpItem.status = PeopleItem.STATUS[Integer.valueOf(item.getElementsByTagName("motion").
			    						item(0).getFirstChild().getNodeValue())];
			    					Log.d("status", "begin call search" + tmpItem.status);
				    				
			    					arr.add(tmpItem);
			        			}
			    				Log.d("Searchsize", "begin call search " + Integer.toString(userlist.getLength()));
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
	/*同步 地点 确定 index*/
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchmap);
		mapView = (MapView)findViewById(R.id.search_map);
		CharSequence textCharSequence = "查找聊天对象";
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
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先 
        option.setProdName("demo"); // 设置产品线名称 
        local.setLocOption(option);
        local.registerLocationListener(new BDLocationListener() { 
			@Override
			public void onReceiveLocation(BDLocation location) { 
				if(location == null) return;
				Log.v("d","begin f" + Double.toString(location.getLatitude()));
				Log.v("d","begin f" + Double.toString(location.getLongitude()));
				centerpoint.setLatitudeE6((int) (location.getLatitude() * 1e6));
				centerpoint.setLongitudeE6((int) (location.getLongitude() * 1e6));
				String strInfo = String.format("纬度：%f 经度：%f", location.getLatitude(), location.getLongitude());
				Toast.makeText(SearchMap.this, strInfo.toString(), Toast.LENGTH_LONG).show();
			}
			@Override
			public void onReceivePoi(BDLocation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        local.start();//将开启与获取位置分开，就可以尽量的在后面的使用中
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
						String str = String.format("错误号：%d", error);
						Toast.makeText(SearchMap.this, str, Toast.LENGTH_LONG).show();
						return;
					}
					//地图移动到该点
				
					mapView.getController().animateTo(res.geoPt);
					centerpoint.setLatitudeE6((int)(res.geoPt.getLatitudeE6()));
					centerpoint.setLongitudeE6((int)(res.geoPt.getLongitudeE6()));
					if (res.type == MKAddrInfo.MK_GEOCODE){
						//地理编码：通过地址检索坐标点
						String strInfo = String.format("纬度：%f 经度：%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
						Toast.makeText(SearchMap.this, strInfo, Toast.LENGTH_LONG).show();
					}
					if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
						//反地理编码：通过坐标点检索详细地址及周边poi
						String strInfo = res.strAddr;
						Toast.makeText(SearchMap.this, strInfo, Toast.LENGTH_LONG).show();
						
					}
					//生成ItemizedOverlay图层用来标注结果点
					ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mapView);
					//生成Item
					OverlayItem item = new OverlayItem(res.geoPt, "", null);
					//得到需要标在地图上的资源
					Drawable marker = getResources().getDrawable(R.drawable.icon_markf);  
					//为maker定义位置和边界
					marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
					//给item设置marker
					item.setMarker(marker);
					//在图层上添加item
					itemOverlay.addItem(item);
					
					//清除地图其他图层
					mapView.getOverlays().clear();
					//添加一个标注ItemizedOverlay图层
					mapView.getOverlays().add(itemOverlay);
					//执行刷新使生效
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
	private int synchronize(int id, int lat, int lon){
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost(Constants.REQUEST_HOST,8081,"http");
		PostMethod method = new PostMethod("/api/update");
		NameValuePair []user = {
			new NameValuePair("lat", Integer.toString(lat)),//Integer.toString(centerpoint.getLatitudeE6())), 
			new NameValuePair("lon", Integer.toString(lon)),//Integer.toString(centerpoint.getLongitudeE6())),
			new NameValuePair("id", Integer.toString(id))//Integer.toString(dis(centerpoint, point)))
		};	
		method.setRequestBody(user);
		try{
			client.executeMethod(method);
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
           	Toast.makeText(SearchMap.this, "同步成功", Toast.LENGTH_SHORT).show();
            break;  
           case 0:
   			Toast.makeText(SearchMap.this, "同步失败", Toast.LENGTH_SHORT).show();
           }  
           
       }  
	};
}
