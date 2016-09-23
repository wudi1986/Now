package com.yktx.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.fb.FeedbackAgent;
import com.yktx.bean.GroupListBean;
import com.yktx.bean.HomeBean;
import com.yktx.group.adapter.HomeHotGridViewAdapter;
import com.yktx.group.adapter.HomeNearAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.HomeGridView;
import com.yktx.util.HomeListView;
import com.yktx.util.LvHeightUtil;

public class GroupMainActivity extends BaseActivity implements ServiceListener {

	ArrayList<GroupListBean> hotGroupList = new ArrayList<GroupListBean>(6);
	ArrayList<GroupListBean> nearGroupList = new ArrayList<GroupListBean>(4);
	HomeNearAdapter nearAdapter;
	HomeHotGridViewAdapter hotAdapter;
	RelativeLayout mainSearch, nearButton, hotButton;
	RelativeLayout rl_leftImage, rl_rightImage;
	HomeListView home_near_listView;
	HomeGridView home_hot_GridView;
	ImageView titleLeftImage, titleRightImage, titleImage;
	TextView title;
	FeedbackAgent agent;
	SharedPreferences settings;
	String longitude, latitude, group_id, group_name, lastGroupID, userPhoto,
			group_distance, group_peopleCount, userStatus;

	public DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		agent = new FeedbackAgent(GroupMainActivity.this);
		mContext = this;
		settings = GroupMainActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		SharedPreferences setting = GroupMainActivity.this
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		userPhoto = settings.getString("userPhoto", "-1");
		userStatus = settings.getString("userStatus", "0");

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.head_null)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				// 启用内存缓存
				.displayer(new RoundedBitmapDisplayer(100))
				.imageScaleType(ImageScaleType.EXACTLY).build();

		if (!userPhoto.equals("-1"))
			userPhoto = UrlParams.IP + userPhoto;
		home_near_listView = (HomeListView) findViewById(R.id.home_near_listView);
		home_hot_GridView = (HomeGridView) findViewById(R.id.home_hot_gridView);
		nearButton = (RelativeLayout) findViewById(R.id.nearButton);
		hotButton = (RelativeLayout) findViewById(R.id.hotButton);

		nearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(GroupMainActivity.this,
						HotOrNearActivity.class);
				in.putExtra("status", "0");
				GroupMainActivity.this.startActivity(in);
			}
		});
		hotButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(GroupMainActivity.this,
						HotOrNearActivity.class);
				in.putExtra("status", "2");
				GroupMainActivity.this.startActivity(in);
			}
		});
		mainSearch = (RelativeLayout) findViewById(R.id.main_search);
		mainSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(GroupMainActivity.this,
						SearchActivity.class);
				GroupMainActivity.this.startActivity(in);
			}
		});
		initTitle();

		home_near_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lastGroupID = settings.getString("lastGroupID", "-1");
				group_id = nearGroupList.get(position).getGroupID();
				group_name = nearGroupList.get(position).getGroupName();
				group_distance = nearGroupList.get(position).getDistance();
				group_peopleCount = nearGroupList.get(position)
						.getGroupPeopleCount();
//				if (!isFirstCamera()) {
					intoGroup(group_id);
//				}
				Log.i(  "bbb", "onItemClick group_id ======] "
						+ group_id);
			}
		});
		home_hot_GridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lastGroupID = settings.getString("lastGroupID", "-1");
				group_id = hotGroupList.get(position).getGroupID();
				group_name = hotGroupList.get(position).getGroupName();
				group_distance = hotGroupList.get(position).getDistance();
				group_peopleCount = hotGroupList.get(position)
						.getGroupPeopleCount();
//				if (!isFirstCamera()) {
					intoGroup(group_id);
//				}

			}
		});

		// Set up the pager

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		conn();
	}

	long inGroupTime;

	/**
	 * 判断是否先拍照
	 */
	// private boolean isFirstCamera() {
	// inGroupTime = settings.getLong("inGroupTime", 0);
	// // if (!lastGroupID.equals(group_id)
	// // || System.currentTimeMillis() - inGroupTime > Contanst.LOGIN_TIME *
	// // 60 * 1000) {
	// opencamera();
	// return true;
	// // }
	// // return false;
	// }

	/* 打开相机 */
	// private void opencamera() {
	//
	// Intent cameraIntent = new Intent(GroupMainActivity.this,
	// CameraActivity.class);
	// cameraIntent.putExtra(CameraActivity.IsRegister, "0");
	// cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
	// cameraIntent.putExtra("longitude", longitude);
	// cameraIntent.putExtra("latitude", latitude);
	// cameraIntent.putExtra("group_id", group_id);
	// cameraIntent.putExtra("group_name", group_name);
	// cameraIntent.putExtra("group_distance", group_distance);
	// cameraIntent.putExtra("group_peopleCount", group_peopleCount);
	// startActivityForResult(cameraIntent, 444);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Log.i(  "bbb", "resultCode ========== " + resultCode);
		// Log.i(  "bbb", "requestCode ========== " +
		// requestCode);
		// if (requestCode == 444 && resultCode == 111) {
		// intoGroup(group_id);
		// }
	}

	private void initTitle() {

		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);
		titleImage = (ImageView) findViewById(R.id.titleImage);
		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.GONE);
		titleImage.setVisibility(View.VISIBLE);
		rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
		rl_rightImage = (RelativeLayout) findViewById(R.id.rl_rightImage);
		Log.i(  "aaa", "userPhoto =========== " + userPhoto);
		if ("1".equals(userStatus)) {
			ImageLoader.getInstance().displayImage(userPhoto, titleLeftImage,
					options);
			titleLeftImage.setBackgroundResource(R.drawable.user_title_headbg);
		} else {
			titleLeftImage.setBackgroundColor(0x00000000);
		}

		rl_leftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 注册或修改信息
				Intent in = new Intent(GroupMainActivity.this,
						RegisterHeadActivity.class);
				GroupMainActivity.this.startActivity(in);
			}
		});
		titleRightImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟信息反馈

				agent.sync();
				agent.startFeedbackActivity();
			}
		});
	}

	int connIndex;

	private void conn() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_HOMEPAGE, null, null,
				GroupMainActivity.this).addList(params).request(UrlParams.POST);
	}

	/**
	 * 进群接口
	 */
	private void intoGroup(String group_id) {
		TelephonyManager tm = (TelephonyManager) GroupMainActivity.this
				.getSystemService(TELEPHONY_SERVICE);
		final String Imei = tm.getDeviceId();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
			Log.i(  "aaa", "group_id ========] " + group_id);
			params.add(new BasicNameValuePair("group_id", group_id));
			params.add(new BasicNameValuePair("token", Imei));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_INTOGROUP, null, null,
				GroupMainActivity.this).addList(params).request(UrlParams.POST);
	}

	// public ProgressDialog mDialog;

	// public void showProgressDialog(String message) {
	// mDialog = new ProgressDialog(this);
	// mDialog.setMessage(message);
	// mDialog.setIndeterminate(true);
	// mDialog.setCancelable(true);
	// mDialog.show();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.group.conn.ServiceListener#getJOSNdataSuccess(java.lang.Object,
	 * java.lang.String, int)
	 */
	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
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
		public void handleMessage(Message msg) {
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.HOMEPAGE) {

					HomeBean bean = (HomeBean) msg.obj;
					if (hotGroupList != null)
						hotGroupList.clear();
					hotGroupList = bean.getHotList();
					hotAdapter = new HomeHotGridViewAdapter(
							GroupMainActivity.this, hotGroupList);
					home_hot_GridView.setAdapter(hotAdapter);
					Log.i(  "aaa", "hotGroupList.size === "
							+ hotGroupList.size());
					hotAdapter.notifyDataSetChanged();

					if (nearGroupList != null)
						nearGroupList.clear();
					nearGroupList = bean.getNearList();
					nearAdapter = new HomeNearAdapter(GroupMainActivity.this,
							nearGroupList, true);
					home_near_listView.setAdapter(nearAdapter);
					LvHeightUtil
							.setListViewHeightBasedOnChildren(home_near_listView);

					Log.i(  "aaa", "nearGroupList.size === "
							+ nearGroupList.size());
					nearAdapter.notifyDataSetChanged();
				} else if (msg.arg1 == Contanst.INTOGROUP) {
					// HashMap<String, String> map = (HashMap<String, String>)
					// msg.obj;
					// String isFirst = map.get("isFirst");
					Intent in = new Intent(GroupMainActivity.this,
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName, group_name);
						// in.putExtra(ChatActivity.IsFirst, isFirst);
					}
					GroupMainActivity.this.startActivity(in);
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.HOMEPAGE) {

				}
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(GroupMainActivity.this, message,
						Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

}
