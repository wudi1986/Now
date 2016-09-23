package com.yktx.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yktx.bean.GetAttentionListBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.group.adapter.AttentionActivityListViewAdapter;
import com.yktx.group.adapter.AttentionActivityListViewAdapter.AttentionButtonListener;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.mylistview.XListView;
import com.yktx.mylistview.XListView.IXListViewListener;
import com.yktx.util.Contanst;

/**
 * 
 * 关注
 * 
 * @author Administrator
 * 
 */
public class AttentionActivity extends BaseActivity implements ServiceListener {

	XListView xListView;
	boolean isConn, isReflush;
	long send_time;
	String longitude, latitude;
	ArrayList<GetAttentionListBean> getAttentionList = new ArrayList<GetAttentionListBean>(
			10);
	AttentionActivityListViewAdapter adapter;
	int pageLimit, totalCount, currentPage, totalPage;
	boolean isAttention = false;
	String userID, myUserID;
	ImageView imageListNull;
	int type;
	RelativeLayout loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_or_near_activity);
		SharedPreferences settings = AttentionActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		// userID = settings.getString("userID", "-1");
		SharedPreferences setting = GroupApplication.getInstance()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");

		loadingView = (RelativeLayout) findViewById(R.id.loadingView);
		myUserID = settings.getString("userID", null);
		imageListNull = (ImageView) findViewById(R.id.imageListNull);
		send_time = 0;
		isAttention = getIntent().getBooleanExtra("isAttention", false);
		userID = getIntent().getStringExtra("otherID");

		if (isAttention) {
			type = 0;
			if(userID.equals(myUserID)){
				imageListNull.setImageResource(R.drawable.attention_list_null);
			} else {
				imageListNull.setImageResource(R.drawable.other_attention_list_null);
			}
		} else {
			type = 1;
			if(userID.equals(myUserID)){
				imageListNull.setImageResource(R.drawable.fans_list_null);
			} else {
				imageListNull.setImageResource(R.drawable.other_fans_list_null);
			}
		}
		connGetAttList(currentPage, type);

		isReflush = true;

		initTitile();
		xListView = (XListView) findViewById(R.id.xListView);
		xListView.setXListViewListener(listener);
		adapter = new AttentionActivityListViewAdapter(AttentionActivity.this);
		adapter.setIntoUserCenter(intoUserCenter);
		adapter.setAttentionButtonListener(attentionButtonListener);
		xListView.setAdapter(adapter);
		xListView.setIsShow(true);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(false);
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				GetAttentionListBean bean = getAttentionList.get(arg2 - 1);
				// opencamera(bean.getGroup_id(), bean.getGroup_name(), null,
				// bean.getLinenum());
				Intent in = new Intent(AttentionActivity.this,
						UserCenterActivity.class);
				in.putExtra("userID", bean.getUser_id());
				AttentionActivity.this.startActivity(in);
			}
		});
	}

	AttentionButtonListener attentionButtonListener = new AttentionButtonListener() {

		@Override
		public void getAttentionButtonListener(String userID, String isatt,
				int position) {
			// TODO Auto-generated method stub
			connGetAttention(userID, isatt, position);
		}
	};

	IntoUserCenterListener intoUserCenter = new IntoUserCenterListener() {

		@Override
		public void getIntoUserCenter(String userID) {
			// TODO Auto-generated method stub
			Intent in = new Intent(AttentionActivity.this,
					UserCenterActivity.class);
			in.putExtra("userID", userID);
			AttentionActivity.this.startActivity(in);
		}
	};

	private void initTitile() {
		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.activitytop);
		titleLayout.setVisibility(View.VISIBLE);
		View topLine = (View) findViewById(R.id.line);
		topLine.setVisibility(View.VISIBLE);
		TextView title = (TextView) findViewById(R.id.title);
		if (type == 0) {
			if (userID.equals(myUserID)) {
				title.setText("我的关注");
			} else
				title.setText("关注");
		} else {
			if (userID.equals(myUserID)) {
				title.setText("我的粉儿");
			} else
				title.setText("粉儿");
		}
		ImageView right = (ImageView) findViewById(R.id.rightImage);
		right.setVisibility(View.GONE);
		ImageView left = (ImageView) findViewById(R.id.leftImage);
		left.setImageResource(R.drawable.zhuce_back);
		left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AttentionActivity.this.finish();
			}
		});
	}

	/* 打开相机 */
	// public void opencamera(String group_id, String group_name,
	// String group_distance, String group_peopleCount) {
	//
	// Intent cameraIntent = new Intent(AttentionActivity.this,
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

	private void connGetAttList(int currentPage, int type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("user_id", userID));
			params.add(new BasicNameValuePair("myuser_id", myUserID));
			params.add(new BasicNameValuePair("currentPage", currentPage + ""));
			params.add(new BasicNameValuePair("pageLimit", "10"));
			params.add(new BasicNameValuePair("type", type + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETATTENTIONLIST, null, null,
				AttentionActivity.this).addList(params).request(UrlParams.POST);

	}

	private void connGetAttention(String userID, String type, int position) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("tuser_id", myUserID));
			params.add(new BasicNameValuePair("puser_id", userID));
			params.add(new BasicNameValuePair("is_delete", type + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_ATTENTION, null, position + "",
				AttentionActivity.this).addList(params).request(UrlParams.POST);

	}

	IXListViewListener listener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			if (isConn) {
				return;
			}
			connGetAttList(1, type);
			isReflush = true;
			isConn = true;

		}

		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub
			if (isConn) {
				return;
			}
			isReflush = false;

			if (currentPage * 10 >= totalCount) {
				// Toast.makeText(NewFragment.this.getActivity(), "亲，没有更多信息了",
				// Toast.LENGTH_SHORT).show();

				onLoad();
				return;
			}
			connGetAttList(currentPage + 1, type);
			isConn = true;
		}
	};

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
		if (connType == Contanst.ATTENTION) {
			String position = errcode;
			int index = 0;
			if (position != null) {
				index = Integer.parseInt(position);
				msg.arg2 = index;
			}
		}
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.GETATTENTIONLIST) {

					// 刷新附近列表
					if (isReflush) {
						currentPage = 1;
						getAttentionList.clear();
						MainHomePageBean<GetAttentionListBean> bean = (MainHomePageBean<GetAttentionListBean>) msg.obj;
						currentPage = bean.getCurrentPage();
						totalCount = bean.getTotalCount();
						totalPage = bean.getTotalPage();

						getAttentionList = bean.getListData();
						if (getAttentionList.size() == 0) {
							onLoad();
							imageListNull.setVisibility(View.VISIBLE);
							xListView.setVisibility(View.GONE);
							return;
						} else {
							if (imageListNull.getVisibility() == View.VISIBLE) {
								imageListNull.setVisibility(View.GONE);
								xListView.setVisibility(View.VISIBLE);
							}
						}

						adapter.setList(getAttentionList);
						adapter.setDistance(latitude, longitude);
						xListView.setPullLoadEnable(true);
						adapter.notifyDataSetChanged();
					} else {
						currentPage++;
						MainHomePageBean<GetAttentionListBean> bean = (MainHomePageBean<GetAttentionListBean>) msg.obj;
						getAttentionList.addAll(bean.getListData());
						adapter.setList(getAttentionList);
						adapter.setDistance(latitude, longitude);
						adapter.notifyDataSetChanged();
					}

					onLoad();
					if (totalCount <= 10 || currentPage * 10 >= totalCount) {
						xListView.setIsShow(false);
					} else {
						xListView.setIsShow(true);
					}

				} else if (msg.arg1 == Contanst.ATTENTION) {
					String position = (String) msg.obj;
					int index = 0;
					if (position != null) {
						index = Integer.parseInt(position);
					}
					if (getAttentionList.get(index).getIs_att().equals("0")) {
						getAttentionList.get(index).setIs_att("1");
					} else {
						getAttentionList.get(index).setIs_att("0");
					}

					getAttentionList.get(index).setLoading(false);
					adapter.notifyDataSetChanged();
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.GETATTENTIONLIST) {
					xListView.setAdapter(adapter);
					xListView.setPullLoadEnable(false);
					adapter.notifyDataSetChanged();
					String message = (String) msg.obj;
					Log.i(  "aaa", "message = " + message);
					onLoad();
				} else if (msg.arg1 == Contanst.ATTENTION) {
					int index = msg.arg2;
					getAttentionList.get(index).setLoading(false);
					adapter.notifyDataSetChanged();
					Toast.makeText(AttentionActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}

	};

	private void onLoad() {
		if(loadingView.getVisibility() != View.GONE){
			loadingView.setVisibility(View.GONE);
		}
		xListView.stopRefresh();
		xListView.stopLoadMore();
		isConn = false;
		isReflush = false;
	}

}
