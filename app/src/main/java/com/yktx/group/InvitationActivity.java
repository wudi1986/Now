package com.yktx.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yktx.bean.GetAttentionListBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.group.adapter.InvitationActivityListViewAdapter;
import com.yktx.group.adapter.InvitationActivityListViewAdapter.InvitationButtonListener;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.mylistview.XListView;
import com.yktx.mylistview.XListView.IXListViewListener;
import com.yktx.util.Contanst;

public class InvitationActivity extends BaseActivity implements ServiceListener {
	XListView xListView;
	boolean isConn, isReflush;
	long send_time;
	String longitude, latitude;
	ArrayList<GetAttentionListBean> getAttentionList = new ArrayList<GetAttentionListBean>(
			10);
	InvitationActivityListViewAdapter adapter;
	int pageLimit, totalCount, currentPage, totalPage;
	boolean isAttention = false;
	String groupID, myUserID;
	int type = 1;
	RelativeLayout loadingView;
	ImageView imageListNull;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences settings = InvitationActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		SharedPreferences setting = GroupApplication.getInstance()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");

		myUserID = settings.getString("userID", null);
		send_time = 0;
		groupID = getIntent().getStringExtra("groupID");
		connGetAttList(currentPage, type);
		isReflush = true;
		setContentView(R.layout.hot_or_near_activity);
		imageListNull = (ImageView) findViewById(R.id.imageListNull);
		loadingView = (RelativeLayout) findViewById(R.id.loadingView);
		initTitile();
		xListView = (XListView) findViewById(R.id.xListView);
		xListView.setXListViewListener(listener);
		adapter = new InvitationActivityListViewAdapter(InvitationActivity.this);
		adapter.setInvitationButtonListener(invitationButtonListener);
		xListView.setAdapter(adapter);
		xListView.setIsShow(true);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(true);
	}

	private void initTitile() {
		RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.activitytop);
		titleLayout.setVisibility(View.VISIBLE);
		View topLine = (View) findViewById(R.id.line);
		topLine.setVisibility(View.VISIBLE);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("邀请");
		ImageView right = (ImageView) findViewById(R.id.rightImage);
		right.setVisibility(View.GONE);
		ImageView left = (ImageView) findViewById(R.id.leftImage);
		left.setImageResource(R.drawable.zhuce_back);
		left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InvitationActivity.this.finish();
			}
		});
	}

	InvitationButtonListener invitationButtonListener = new InvitationButtonListener() {

		@Override
		public void getInvitationButtonListener(String userID) {
			// TODO Auto-generated method stub

			connInvitation(userID);
		}
	};

	private void connGetAttList(int currentPage, int type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("user_id", myUserID));
			params.add(new BasicNameValuePair("myuser_id", myUserID));
			params.add(new BasicNameValuePair("currentPage", currentPage + ""));
			params.add(new BasicNameValuePair("pageLimit", "10"));
			params.add(new BasicNameValuePair("type", type + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETATTENTIONLIST, null, null,
				InvitationActivity.this).addList(params)
				.request(UrlParams.POST);

	}

	// 邀请推送信息
	/**
	 * @param curLastDate
	 *            记录消息时间
	 * @param historyFlat
	 *            0-此时间之前的20条消息记录， 1-此时间后的所有记录
	 */
	private void connInvitation(String otherID) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("myuser_id", myUserID));
			params.add(new BasicNameValuePair("group_id", groupID));
			params.add(new BasicNameValuePair("fruser_id", otherID));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_JPUSHGROUP, null, null,
				InvitationActivity.this).addList(params)
				.request(UrlParams.POST);

		Log.i(  "aaa", "params ===== " + params);
		isConn = true;

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
							imageListNull.setVisibility(View.VISIBLE);
							onLoad();
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

				} else if (msg.arg1 == Contanst.JPUSHGROUP) {
					String result = (String) msg.obj;
					if (result.equals("201")) {
						Toast.makeText(InvitationActivity.this, "已经在群里啦。",
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(InvitationActivity.this, "邀请发送成功。",
								Toast.LENGTH_SHORT).show();
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
				} else {
					Toast.makeText(InvitationActivity.this, "邀请发送成功。",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}

	};

	private void onLoad() {
		if (loadingView.getVisibility() != View.GONE) {
			loadingView.setVisibility(View.GONE);
		}
		xListView.stopRefresh();
		xListView.stopLoadMore();
		isConn = false;
		isReflush = false;
	}

}
