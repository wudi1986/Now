/**
 * 
 */
package com.yktx.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yktx.bean.GroupListBean;
import com.yktx.group.adapter.HomeNearAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月11日 下午5:02:49  
 * 类说明 最热/最近 详情 */
/**
 * @author Administrator
 * 
 */
public class HotOrNearActivity extends BaseActivity implements ServiceListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yktx.group.BaseActivity#onCreate(android.os.Bundle)
	 */

	ListView myListView;
	HomeNearAdapter myAdapter;
	ArrayList<GroupListBean> groupList = new ArrayList<GroupListBean>(10);

	String status; // 0附近，2 hot
	String longitude, latitude, group_id, group_name, lastGroupID, userPhoto,group_distance, group_peopleCount,
			userStatus;
	SharedPreferences settings;
	RelativeLayout loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_or_near_activity);
		loadingView = (RelativeLayout) findViewById(R.id.loadingView);
		settings = this.getBaseContext()
				.getSharedPreferences((GroupApplication.getInstance()).getCurSP(), 0);
		SharedPreferences setting = HotOrNearActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");

		userPhoto = settings.getString("userPhoto", "-1");
		userStatus = settings.getString("userStatus", "0");
		
		
		status = getIntent().getStringExtra("status");
//		myListView = (ListView) findViewById(R.id.myListView);
		conn();
		initTitile();
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				lastGroupID = settings.getString("lastGroupID", "-1");
				group_id = groupList.get(position).getGroupID();
				group_name = groupList.get(position).getGroupName();
				group_distance = groupList.get(position).getDistance();
				group_peopleCount = groupList.get(position).getGroupPeopleCount();
				
//				if (!isFirstCamera()) {
					intoGroup(group_id);
//				}
			}
		});
	}

	private void initTitile() {
		TextView title = (TextView) findViewById(R.id.title);
		if (status.equals("0")) {
			title.setText("附近的Now");
		} else {
			title.setText("最热的Now");
		}
		ImageView right = (ImageView) findViewById(R.id.rightImage);
		right.setVisibility(View.GONE);
		ImageView left = (ImageView) findViewById(R.id.leftImage);
		left.setImageResource(R.drawable.zhuce_back);
		left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				HotOrNearActivity.this.finish();
			}
		});
	}

	/* 打开相机 */
//	private void opencamera() {
//
//		Intent cameraIntent = new Intent(HotOrNearActivity.this,
//				CameraActivity.class);
//		cameraIntent.putExtra(CameraActivity.IsRegister, "0");
//		cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
//		cameraIntent.putExtra("longitude", longitude);
//		cameraIntent.putExtra("latitude", latitude);
//		cameraIntent.putExtra("group_id", group_id);
//		cameraIntent.putExtra("group_name", group_name);
//		cameraIntent.putExtra("group_distance", group_distance);
//		cameraIntent.putExtra("group_peopleCount", group_peopleCount);
//		startActivityForResult(cameraIntent, 444);
//	}
	
	long inGroupTime;

	/**
	 * 判断是否先拍照
	 */
//	private boolean isFirstCamera() {
//		inGroupTime = settings.getLong("inGroupTime", 0);
////		if (!lastGroupID.equals(group_id)
////				|| System.currentTimeMillis() - inGroupTime > Contanst.LOGIN_TIME * 60 * 1000) {
//			opencamera();
//			return true;
////		}
////		return false;
//	}

	/**
	 * 进群接口
	 */
	private void intoGroup(String group_id) {
		TelephonyManager tm = (TelephonyManager) HotOrNearActivity.this
				.getSystemService(TELEPHONY_SERVICE);
		final String Imei = tm.getDeviceId();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
			params.add(new BasicNameValuePair("group_id", group_id));
			params.add(new BasicNameValuePair("token", Imei));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_INTOGROUP, null, null,
				HotOrNearActivity.this).addList(params).request(UrlParams.POST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 444 && resultCode == 111) {
			intoGroup(group_id);
		}
	}

	private void conn() {

		SharedPreferences settings = HotOrNearActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.SystemStone, 0);
		String longitude = settings.getString("longitude", "-1");
		String latitude = settings.getString("latitude", "-1");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("status", status));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_NEARORHOTGROUP, null, null,
				HotOrNearActivity.this).addList(params).request(UrlParams.POST);
	}

	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataFail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataFail(String errcode, String errmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.NEARORHOTGROUP) {
					if(loadingView.getVisibility() != View.GONE){
						loadingView.setVisibility(View.GONE);
					}
					groupList = (ArrayList<GroupListBean>) msg.obj;
					if (myAdapter != null) {
						myAdapter = null;
					}
					myAdapter = new HomeNearAdapter(HotOrNearActivity.this,
							groupList, false);
					myListView.setAdapter(myAdapter);
					myAdapter.notifyDataSetChanged();

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					// String isFirst = map.get("isFirst");
					Intent in = new Intent(HotOrNearActivity.this,
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName, group_name);
						// in.putExtra(ChatActivity.IsFirst, isFirst);
					}
					HotOrNearActivity.this.startActivity(in);
				}

				break;
			case Contanst.BEST_INFO_FAIL:

				if (msg.arg1 == Contanst.NEARORHOTGROUP) {
					if(loadingView.getVisibility() != View.GONE){
						loadingView.setVisibility(View.GONE);
					}
				}
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(HotOrNearActivity.this, message,
						Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

}
