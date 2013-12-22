package com.wannatalk.android.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wannatalk.android.R;
import com.wannatalk.android.comm.Constants;
import com.wannatalk.android.model.PeopleItem;

public class ShowPeople extends Activity implements OnScrollListener{
	List<String> data;
    // view
    ListView lv;
    int lastitem;
    int totalcount = 100;
    // controller
    private LinearLayout footer;
    ListViewAdapt adapt;
    Handler handledata = null;
    List <PeopleItem> arr = new ArrayList<PeopleItem>();
    int size = 1;
    // 初始化组件
    
    private void initWidget() {
        lv = (ListView) findViewById(R.id.list);
        footer = new LinearLayout(this);  
        footer.setMinimumHeight(60);  
        footer.setGravity(Gravity.CENTER);  
        footer.setOrientation(LinearLayout.HORIZONTAL);  
        ProgressBar mProgressBar = new ProgressBar(this); 
        android.widget.LinearLayout.LayoutParams mProgressBarLayoutParams = new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.WRAP_CONTENT,  
                LinearLayout.LayoutParams.WRAP_CONTENT); 
        mProgressBar.setPadding(0, 0, 15, 0);  
        footer.addView(mProgressBar, mProgressBarLayoutParams);  
        TextView mTipContent = new TextView(this);  
        mTipContent.setText("加载中...");  
        android.widget.LinearLayout.LayoutParams mTipContentLayoutParams = new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.WRAP_CONTENT,  
                LinearLayout.LayoutParams.WRAP_CONTENT);  
        footer.addView(mTipContent, mTipContentLayoutParams);  
    }
    class ListViewAdapt extends BaseAdapter{
    	List <PeopleItem> arr = new ArrayList<PeopleItem>();
    	LayoutInflater inflater;
    	int count;
    	ListViewAdapt(List <PeopleItem> arr){
    		super();
    		this.arr = arr;
    		totalcount = arr.size();
    		count = Math.min(totalcount, 6);
    	}	
    	public int count(){
    		return count;
    	}
    	void add(int num){
    		count += num;
    		if(count >= totalcount)
    			count = totalcount;
    	}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public Object getItem(int arg0) {
			return arr.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		class Viewholder {
			TextView id;
			TextView status;
		}
		@Override
		public View getView(int arg0, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Viewholder viewholder = null;
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.peopleitem, null);
				viewholder = new Viewholder();
				viewholder.id = (TextView)convertView.findViewById(R.id.people_id);
				viewholder.status = (TextView)convertView.findViewById(R.id.people_status);
				convertView.setTag(viewholder);
			}
			else
				viewholder = (Viewholder)convertView.getTag();
			viewholder.id.setText(Integer.toString(arr.get(arg0).id));
			viewholder.id.setTextColor(arr.get(arg0).sex ? Color.BLUE: Color.RED);
			viewholder.status.setText(Constants.ModState[arr.get(arg0).motion]);
			return convertView;
		}
    }
    // 初始化绑定数据
    private void initData() {
        if (lv == null)
            return;
        Bundle data = getIntent().getExtras();
        int centerlat = data.getInt("centerlat");
        int centerlon = data.getInt("centerlon");
        int r = data.getInt("radius");
        int size = data.getInt("numpeople");
        Log.v("numpeople",Integer.toString(size));
        
        for(int i = 0; i < size; ++i){
        	arr.add((PeopleItem)data.getSerializable("people" + i));
        }
        
        Log.v("numpeople",Integer.toString(arr.size()));
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent intent = new Intent(ShowPeople.this,TalkActivity.class);
				Bundle data = new Bundle();
				int fid = arr.get(pos).id;
				data.putInt("friendId", fid);
				intent.putExtras(data);
				ShowPeople.this.startActivity(intent);
			}
        	
        });
        lv.addFooterView(footer, null, false);
        lv.setFooterDividersEnabled(false);
        adapt = new ListViewAdapt(arr);
        lv.setAdapter(adapt);
    }
    private void initlisten(){
    	lv.setOnScrollListener(this);
    }
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpeople);
		setTitle("找到的小伙伴");
		initWidget();
		initData();
		initlisten();
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		lastitem = firstVisibleItem + visibleItemCount - 1;   
		if(adapt.count() >= totalcount)
			lv.removeFooterView(footer);
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		Log.v("fuck", Integer.toString(lastitem));
		if (lastitem == adapt.count()  
                && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {  
	       if (adapt.count() < totalcount) {  
            	if(lv.getFooterViewsCount() == 0)
    				lv.addFooterView(footer);
            	if(handledata == null)
            		handledata = new Handler();
                handledata.postDelayed(new Runnable() {  
                    @Override  
                    public void run() {  
                    	adapt.add(10);
                        adapt.notifyDataSetChanged();  
                        lv.setSelection(lastitem);  
                    }  
                }, 1000);  
            }  
        }  
	}
}
