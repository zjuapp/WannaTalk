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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.wannatalk.android.activity.ShowPeople.PeopleItem;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.comm.WannaTalkApplication;

public class SearchMap extends Activity {
	//private final MapView mapView = (MapView)findViewById(R.id.search_map);
	//private final GeoPoint centerpoint = new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6));
	private GeoPoint centerpoint = null;
	private MapController mapController = null;
	private MKSearch mSearch = null;
	private MKMapViewListener mMapListener = null;
	private MKMapTouchListener mapTouchLister = null;
	private android.app.AlertDialog.Builder search_people = null;
	Dialog dlg;
	private int dis(GeoPoint a, GeoPoint b){
		return (int)Math.sqrt(((long)(a.getLatitudeE6() - b.getLatitudeE6())) * (a.getLatitudeE6() - b.getLatitudeE6()) + 
				((long)(a.getLongitudeE6() - b.getLongitudeE6())) * (a.getLongitudeE6() - b.getLongitudeE6()));
	}
	public Graphic drawCircle(GeoPoint p) {
		 
		   	int distance = dis(p, centerpoint);
		   	Log.v("dis", Integer.toString(distance));
		   	//����Բ
	  		Geometry circleGeometry = new Geometry();
	  		//����Բ���ĵ�����Ͱ뾶
	  		circleGeometry.setCircle(centerpoint, distance / 10);
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
	private void initmap(){
		final MapView mapView = (MapView)findViewById(R.id.search_map);
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
				 * �ڴ˴����ͼ�ƶ���ɻص�
				 * ���ţ�ƽ�ƵȲ�����ɺ󣬴˻ص�������
				 */
				
			}
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * �ڴ˴����ͼpoi����¼�
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
          * �ڴ˴����ͼ������¼� 
          */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(SearchMap.this, 
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
		    	//��ӵ�
		        graphicsOverlay.setData(drawCircle(point));
		        mapView.refresh();
		        search_people.setPositiveButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						final Bundle data = new Bundle();
						final Intent intent = new Intent(SearchMap.this, ShowPeople.class);
						dlg = ProgressDialog.show(SearchMap.this, null,  "���ڻ�ȡ��,��", true, true);
						new Thread(){
							public void run(){	
								Looper.prepare();
			    			HttpClient client = new HttpClient();
			    			client.getHostConfiguration().setHost(Constants.REQUEST_HOST,8081,"http");
			    			PostMethod method = new PostMethod("/api/search");
			    			NameValuePair []user = {
			    					new NameValuePair("lat", "0"),//Integer.toString(centerpoint.getLatitudeE6())), 
			    					new NameValuePair("lon", "0"),//Integer.toString(centerpoint.getLongitudeE6())),
			    					new NameValuePair("r", "1")//Integer.toString(dis(centerpoint, point)))
			    			};	
			    			method.setRequestBody(user);
			    			try{
			    				List <PeopleItem> arr = new ArrayList<PeopleItem>();
			    				client.executeMethod(method);
			    				Log.d("SearchMap", "begin call search");
			    				String response = method.getResponseBodyAsString();
			    				response = response.substring(response.indexOf("<"));
			    				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			    				org.w3c.dom.Document document = dBuilder.parse(new ByteArrayInputStream(response.getBytes()));
			    				NodeList userlist= document.getElementsByTagName("user");
			    				for(int i = 0; i < userlist.getLength(); ++i){
			    					PeopleItem tmpItem = new PeopleItem();
			    					org.w3c.dom.Element item = (org.w3c.dom.Element)userlist.item(i);
			    					tmpItem.id = Integer.valueOf(item.getElementsByTagName("uid").item(0).getFirstChild().getNodeValue());
			    					tmpItem.sex = Boolean.valueOf(item.getElementsByTagName("sex").item(0).getFirstChild().getNodeValue());
			    					tmpItem.happen = item.getElementsByTagName("signature").item(0).getFirstChild().getNodeValue();
			    					tmpItem.status = PeopleItem.STATUS[Integer.valueOf(item.getElementsByTagName("motion").
			    						item(0).getFirstChild().getNodeValue())];
				    				arr.add(tmpItem);
			        			}
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
			    				Log.v("SearchMap", "error", e);
			    				e.printStackTrace();
			    				dlg.dismiss();
			    			}
			    			}
			    		}.start();	
					}
		        }
				);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchmap);
		CharSequence textCharSequence = "�����������";
		setTitle(textCharSequence);
		centerpoint = new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6));
		Button search_button = (Button)findViewById(R.id.search_button);
		final EditText edit = (EditText)findViewById(R.id.search_edit);
		initmap();
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
						Toast.makeText(SearchMap.this, str, Toast.LENGTH_LONG).show();
						return;
					}
					//��ͼ�ƶ����õ�
					mapView.getController().animateTo(res.geoPt);
					centerpoint.setLatitudeE6(res.geoPt.getLatitudeE6());
					centerpoint.setLongitudeE6(res.geoPt.getLongitudeE6());
					if (res.type == MKAddrInfo.MK_GEOCODE){
						//������룺ͨ����ַ���������
						String strInfo = String.format("γ�ȣ�%f ���ȣ�%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
						Toast.makeText(SearchMap.this, strInfo, Toast.LENGTH_LONG).show();
					}
					if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
						//��������룺ͨ������������ϸ��ַ���ܱ�poi
						String strInfo = res.strAddr;
						Toast.makeText(SearchMap.this, strInfo, Toast.LENGTH_LONG).show();
						
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
					//��ͼ�������item
					itemOverlay.addItem(item);
					
					//�����ͼ����ͼ��
					mapView.getOverlays().clear();
					//���һ����עItemizedOverlayͼ��
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
				EditText editCity = (EditText)findViewById(R.id.search_edit);
				EditText editGeoCodeKey = (EditText)findViewById(R.id.search_edit);
				//Geo����
				mSearch.geocode("�㽭��ѧ", "����");
			}
		});
		syn_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
