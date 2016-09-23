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

import com.yktx.bean.HotMainBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.bean.RecommendMainBean;
import com.yktx.group.ChatActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.adapter.RecommendFragmentListViewAdapter;
import com.yktx.group.adapter.RecommendFragmentListViewAdapter.OnClickViewPagerButton;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.mylistview.XListView;
import com.yktx.mylistview.XListView.IXListViewListener;
import com.yktx.util.Contanst;

/**
 * 推荐 Fragment的界面
 * 
 * @author wudi
 */
public class RecommendFragment extends BaseFragment implements ServiceListener {

	XListView xListView;
	boolean isConn, isReflush;
	String userID;
	long send_time;
	ArrayList<HotMainBean> hotList = new ArrayList<HotMainBean>(10);
	ArrayList<RecommendMainBean> recommendList = new ArrayList<RecommendMainBean>(
			6);
	RecommendFragmentListViewAdapter adapter;
	int recommendListSize = 0;
	HotMainBean toChatBean;
	RelativeLayout loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		SharedPreferences settings = RecommendFragment.this
				.getActivity()
				.getBaseContext()
				.getSharedPreferences(
						((GroupApplication) (RecommendFragment.this.getActivity().getApplicationContext()))
								.getCurSP(), 0);
		userID = settings.getString("userID", "-1");
		SharedPreferences setting = RecommendFragment.this.getActivity()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
	
		send_time = 0;
		if (!isConn) {
			isConn = true;
			isReflush = true;
			connHot(1, 0);
		}
//		connRecommendList();
		View view = inflater.inflate(R.layout.hot_or_near_activity, container,
				false);
		loadingView = (RelativeLayout) view.findViewById(R.id.loadingView);
		imageListNull = (ImageView) view.findViewById(R.id.imageListNull);
		imageListNull.setImageResource(R.drawable.conn_null);
		xListView = (XListView) view.findViewById(R.id.xListView);
		xListView.setXListViewListener(listener);
		adapter = new RecommendFragmentListViewAdapter(
				RecommendFragment.this.getActivity());
		adapter.setOnClickViewPagerButton(onClickViewPagerButton);
		xListView.setAdapter(adapter);
		// xListView.setIXListViewOnGoHome(iXListViewOnGoHome);
		xListView.setPullGoHome(false);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(true);
		xListView.setIsShow(false);
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int index = 0;
				if (recommendListSize == 0) {
					index = arg2 - 1;
				} else if (recommendListSize <= 3) {
					index = arg2 - 2;
				} else if (recommendListSize > 3) {
					index = arg2 - 2;
				}
				toChatBean = hotList.get(index);
//				if (GroupApplication.getInstance().getCurSP()
//						.equals(Contanst.TouristStone)) {
					// 游客 直接进入
					intoGroup(toChatBean.getGroup_id(),
							toChatBean.getGroup_name());
//				} else {
//					opencamera(toChatBean.getGroup_id(),
//							toChatBean.getGroup_name(), null,
//							toChatBean.getGroupManCount(), "0");
//				}
			}
		});

		return view;
	}

	OnClickViewPagerButton onClickViewPagerButton = new OnClickViewPagerButton() {

		@Override
		public void getClick(int option) {
			// TODO Auto-generated method stub

			RecommendMainBean bean = recommendList.get(option);
			 toChatBean = new HotMainBean();
			 toChatBean.setGroup_id(bean.getGroup_id());
			 toChatBean.setGroup_name(bean.getGroup_name());
			 toChatBean.setGroupManCount(bean.getGroupManCount());
//			if (GroupApplication.getInstance().getCurSP()
//					.equals(Contanst.TouristStone)) {
				// 游客 直接进入
				intoGroup(toChatBean.getGroup_id(),
						toChatBean.getGroup_name());
//			} else {
//				opencamera(bean.getGroup_id(), bean.getGroup_name(), null,
//						bean.getGroupManCount(), "0");
//			}
			
		}
	};

	private void connHot(int currentPage, long send_time) {
		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		try {
			params1.add(new BasicNameValuePair("currentPage", currentPage + ""));
			params1.add(new BasicNameValuePair("pageLimit", pageLimit + ""));
			params1.add(new BasicNameValuePair("longitude", longitude));
			params1.add(new BasicNameValuePair("latitude", latitude));
			params1.add(new BasicNameValuePair("type", "2"));
			params1.add(new BasicNameValuePair("user_id", userID));
			params1.add(new BasicNameValuePair("send_time", send_time + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_HOMEPAGE, null,
				GroupMainFragmentActivity.MAIN_HOT_HTTP, RecommendFragment.this)
				.addList(params1).request(UrlParams.POST);
	}

//	private void connRecommendList() {
//
//		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
//		try {
//			params1.add(new BasicNameValuePair("currentPage", "1"));
//			params1.add(new BasicNameValuePair("pageLimit", pageLimit + ""));
//			params1.add(new BasicNameValuePair("longitude", longitude));
//			params1.add(new BasicNameValuePair("latitude", latitude));
//			params1.add(new BasicNameValuePair("type", "3"));
//			params1.add(new BasicNameValuePair("user_id", userID));
//			params1.add(new BasicNameValuePair("send_time", "0"));
//
//		} catch (Exception e) {
//
//		}
//		Service.getService(Contanst.HTTP_HOMEPAGE, null,
//				GroupMainFragmentActivity.MAIN_RECOMMEND_HTTP,
//				RecommendFragment.this).addList(params1)
//				.request(UrlParams.POST);
//	}

	IXListViewListener listener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			if (isConn) {
				return;
			}
//			connRecommendList();
			connHot(1, 0);
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
				onLoad();
				return;
			}
			connHot(currentPage + 1, send_time);
			isConn = true;
		}
	};

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
				RecommendFragment.this).addList(params).request(UrlParams.POST);
	}

	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		if (sccmsg != null
				&& sccmsg.equals(GroupMainFragmentActivity.MAIN_RECOMMEND_HTTP)) {
			msg.arg2 = 1;
		} else if (sccmsg != null
				&& sccmsg.equals(GroupMainFragmentActivity.MAIN_HOT_HTTP)) {
			msg.arg2 = 2;
		}
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
					if (msg.arg2 == 1) { // MAIN_RECOMMEND_HTTP
//						MainHomePageBean<RecommendMainBean> recommendMainBean = (MainHomePageBean<RecommendMainBean>) msg.obj;
//						recommendList = recommendMainBean.getListData();
//						adapter.setRecommendList(recommendList);
//						recommendListSize = recommendMainBean.getListData()
//								.size();
//						adapter.notifyDataSetChanged();
					} else if (msg.arg2 == 2) { // MAIN_HOT_HTTP
						// 刷新附近列表
						if (isReflush) {
							currentPage = 1;
							hotList.clear();
							MainHomePageBean<HotMainBean> bean = (MainHomePageBean<HotMainBean>) msg.obj;
							currentPage = bean.getCurrentPage();
							totalCount = bean.getTotalCount();
							totalPage = bean.getTotalPage();

							hotList = bean.getListData();
							// if (hotList.size() == 0) {
							// onLoad();
							// return;
							// }
							if (hotList.size() == 0) {
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
							adapter.setHotList(hotList);
							adapter.notifyDataSetChanged();
						} else {
							currentPage++;
							MainHomePageBean<HotMainBean> bean = (MainHomePageBean<HotMainBean>) msg.obj;
							hotList.addAll(bean.getListData());
							adapter.setHotList(hotList);
							adapter.notifyDataSetChanged();
						}

						onLoad();
						if (totalCount <= 10 || currentPage * 10 >= totalCount) {
							xListView.setIsShow(false);
						} else {
							xListView.setIsShow(true);
						}
					}

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					String group_id = map.get("group_id");

					Intent in = new Intent(
							RecommendFragment.this.getActivity(),
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
					RecommendFragment.this.getActivity().startActivity(in);
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				if (msg.arg1 == Contanst.HOMEPAGE) {
					xListView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					
					Log.i(  "aaa", "message = " + message);
					onLoad();
				} else if (msg.arg1 == Contanst.INTOGROUP) {
					Toast.makeText(RecommendFragment.this.getActivity(), message, Toast.LENGTH_SHORT).show();
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
