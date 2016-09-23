package com.yktx.group.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yktx.bean.AttentionMainBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.group.ChatActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.UserCenterActivity;
import com.yktx.group.adapter.AttentionFragmentListViewAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.mylistview.XListView;
import com.yktx.mylistview.XListView.IXListViewListener;
import com.yktx.util.Contanst;

/**
 * 关注 Fragment的界面
 * 
 * @author wudi
 */
public class AttentionFragment extends BaseFragment implements ServiceListener {

	XListView xListView;
	boolean isConn, isReflush;
	String userID;
	long send_time;
	RelativeLayout loadingView;
	ArrayList<AttentionMainBean> attentionList = new ArrayList<AttentionMainBean>(
			10);
	AttentionFragmentListViewAdapter adapter;
	AttentionMainBean toChatBean;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		SharedPreferences settings = AttentionFragment.this
				.getActivity()
				.getBaseContext()
				.getSharedPreferences(
						((GroupApplication) (this.getActivity().getApplicationContext()))
								.getCurSP(), 0);
		userID = settings.getString("userID", "-1");

		SharedPreferences setting = AttentionFragment.this.getActivity()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");

		send_time = 0;
		isReflush = true;
		conn(1, 0);
		View view = inflater.inflate(R.layout.hot_or_near_activity, container,
				false);
		loadingView = (RelativeLayout) view.findViewById(R.id.loadingView);
		imageListNull = (ImageView) view.findViewById(R.id.imageListNull);

//		if (GroupApplication.getInstance().getCurSP()
//				.equals(Contanst.TouristStone)) {
//			imageListNull
//					.setImageResource(R.drawable.attention_list_null_unlogin);
//			imageListNull.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					GroupApplication.getInstance().backHomeActivity();
//					Intent intentdata = new Intent(GroupApplication
//							.getInstance().activityList.get(0),
//							RegisterActivity.class);
//					GroupApplication.getInstance().activityList.get(0)
//							.startActivity(intentdata);
//
//					// TODO Auto-generated method stub
//
//				}
//			});
//		} else {
			imageListNull.setImageResource(R.drawable.attention_list_null);
//
//		}

		xListView = (XListView) view.findViewById(R.id.xListView);
		xListView.setXListViewListener(listener);
		adapter = new AttentionFragmentListViewAdapter(
				AttentionFragment.this.getActivity());
		adapter.setIntoUserCenterListener(intoUserCenterListener);
		xListView.setAdapter(adapter);
		// xListView.setIXListViewOnGoHome(iXListViewOnGoHome);
		// xListView.setPullGoHome(true);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(true);
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				toChatBean = attentionList.get(arg2 - 1);
//				if (GroupApplication.getInstance().getCurSP()
//						.equals(Contanst.TouristStone)) {
					// 游客 直接进入
					intoGroup(toChatBean.getGroup_id(),
							toChatBean.getGroup_name());
//				} else {
//					opencamera(toChatBean.getGroup_id(),
//							toChatBean.getGroup_name(), null,
//							toChatBean.getLinenum(), "0");
//				}

			}
		});

		return view;
	}

	private void conn(int currentPage, long send_time) {
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		try {
			params1.add(new BasicNameValuePair("currentPage", currentPage + ""));
			params1.add(new BasicNameValuePair("pageLimit", pageLimit + ""));
			params1.add(new BasicNameValuePair("longitude", longitude));
			params1.add(new BasicNameValuePair("latitude", latitude));
			params1.add(new BasicNameValuePair("type", "4"));
			params1.add(new BasicNameValuePair("user_id", userID));
			params1.add(new BasicNameValuePair("send_time", send_time + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_HOMEPAGE, null,
				GroupMainFragmentActivity.MAIN_ATTENTION_HTTP,
				AttentionFragment.this).addList(params1)
				.request(UrlParams.POST);
	}

	IXListViewListener listener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			if (isConn) {
				return;
			}
			conn(1, 0);
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
				// Toast.makeText(NearFragment.this.getActivity(), "亲，没有更多信息了",
				// Toast.LENGTH_SHORT).show();
				onLoad();
				return;
			}
			conn(currentPage + 1, send_time);
			isConn = true;
		}
	};

	IntoUserCenterListener intoUserCenterListener = new IntoUserCenterListener() {

		@Override
		public void getIntoUserCenter(String userID) {
			// TODO Auto-generated method stub
			Intent in = new Intent(AttentionFragment.this.getActivity(),
					UserCenterActivity.class);
			in.putExtra("userID", userID);
			AttentionFragment.this.startActivity(in);
		}
	};

	/**
	 * 进群接口
	 */
	private void intoGroup(String group_id, String group_name) {
		Log.i(  "aaa", "intoGroup  group_name ============ "
				+ group_name);
		Log.i(  "aaa",
				"(GroupApplication.getInstance()).getCurSP() ============ "
						+ (GroupApplication.getInstance()).getCurSP());
		// TelephonyManager tm = (TelephonyManager) CameraActivity.this
		// .getSystemService(TELEPHONY_SERVICE);
		// final String Imei = tm.getDeviceId();

		if (userID == null || userID.length() == 0) {
			SharedPreferences setting = GroupApplication.getInstance()
					.getSharedPreferences(
							(GroupApplication.getInstance()).getCurSP(), 0);
			userID = setting.getString("userID", null);

		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
			params.add(new BasicNameValuePair("user_id", userID));
			if (group_id != null && group_id.length() > 0) {

				params.add(new BasicNameValuePair("group_id", group_id));
			}
			if (group_name != null && group_name.length() > 0) {
				params.add(new BasicNameValuePair("group_name", group_name));
			}
			// params.add(new BasicNameValuePair("token", Imei));

			Log.i(  "aaa", "params ============ " + params);

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_INTOGROUP, null, null,
				AttentionFragment.this).addList(params).request(UrlParams.POST);
	}

	// IXListViewOnGoHome iXListViewOnGoHome = new IXListViewOnGoHome() {
	//
	// @Override
	// public void onGoHome() {
	// // TODO Auto-generated method stub
	// if (onGoHomeListener != null) {
	// onGoHomeListener.goHome();
	// }
	// onLoad();
	//
	// }
	// };

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

				if (msg.arg1 == Contanst.HOMEPAGE) {
					// 刷新附近列表
					if (isReflush) {

						currentPage = 1;
						attentionList.clear();
						MainHomePageBean<AttentionMainBean> bean = (MainHomePageBean<AttentionMainBean>) msg.obj;
						currentPage = bean.getCurrentPage();
						totalCount = bean.getTotalCount();
						totalPage = bean.getTotalPage();
						
						attentionList = bean.getListData();
						if (attentionList.size() == 0) {
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
						adapter.setList(attentionList);
						adapter.setDistance(latitude, longitude);
						xListView.setPullLoadEnable(true);
						adapter.notifyDataSetChanged();
					} else {
						currentPage++;
						MainHomePageBean<AttentionMainBean> bean = (MainHomePageBean<AttentionMainBean>) msg.obj;
						attentionList.addAll(bean.getListData());
						adapter.setDistance(latitude, longitude);
						adapter.notifyDataSetChanged();
					}

					onLoad();
					if (totalCount <= 10 || currentPage * 10 >= totalCount) {
						xListView.setIsShow(false);
					} else {
						xListView.setIsShow(true);
					}

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					String group_id = map.get("group_id");

					Intent in = new Intent(
							AttentionFragment.this.getActivity(),
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName,
								toChatBean.getGroup_name());
						in.putExtra(ChatActivity.IsNewGroup, false);
						in.putExtra(ChatActivity.IsFirst, false);
						in.putExtra(ChatActivity.IsJump, false);
						// in.putExtra(ChatActivity.AtMyNum,
						// toChatBean.getMsgnum());
					}

					// if (isSearch) {
					// // 添加搜索界面历史
					//
					// insertSearchHistory(group_id, group_name);
					// }
					// 添加本地历史记录
					insertSearchHistory(group_id, toChatBean.getGroup_name());
					AttentionFragment.this.getActivity().startActivity(in);
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				if (msg.arg1 == Contanst.HOMEPAGE) {
					xListView.setAdapter(adapter);
					xListView.setPullLoadEnable(false);
					adapter.notifyDataSetChanged();
					Log.i(  "aaa", "message = " + message);
					onLoad();
				} else if (msg.arg1 == Contanst.INTOGROUP) {
					Toast.makeText(AttentionFragment.this.getActivity(),
							message, Toast.LENGTH_SHORT).show();
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
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (attentionList.size() == 0) {
				isReflush = true;
				conn(1, 0);
			}
		}
	}

}
