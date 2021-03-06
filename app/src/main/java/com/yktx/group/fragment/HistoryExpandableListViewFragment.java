package com.yktx.group.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.yktx.bean.GroupListBean;
import com.yktx.group.ChatActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.adapter.HistoryExpandableListAdapter;
import com.yktx.group.adapter.HistoryExpandableListAdapter.OnItemClickChild;
import com.yktx.group.adapter.HistoryExpandableListAdapter.OnItemLongClickChild;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.mylistview.XExpandableListView;
import com.yktx.mylistview.XExpandableListView.IXListViewListener;
import com.yktx.sqlite.DBHelper;
import com.yktx.util.Contanst;
import com.yktx.util.TimeUtil;

/**
 * 最新Fragment的界面
 * 
 * @author wudi
 */
public class HistoryExpandableListViewFragment extends BaseFragment implements
		ServiceListener {

	XExpandableListView xExpandableListView;
	HistoryExpandableListAdapter adapter;

	boolean isConn, isReflush;
	String userID;
	long send_time;
	ArrayList<GroupListBean> historyList = new ArrayList<GroupListBean>(10);

	GroupListBean toChatBean;

	// 添加历史
	ArrayList<GroupListBean> historygroupList = new ArrayList<GroupListBean>(10);
	DBHelper dbHelper;
	String groupIDs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		SharedPreferences settings = HistoryExpandableListViewFragment.this
				.getActivity()
				.getBaseContext()
				.getSharedPreferences(
						((GroupApplication) (this.getActivity().getApplicationContext()))
								.getCurSP(), 0);
		userID = settings.getString("userID", "-1");

		SharedPreferences setting = HistoryExpandableListViewFragment.this
				.getActivity().getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		send_time = 0;

		dbHelper = new DBHelper(
				HistoryExpandableListViewFragment.this.getActivity());

		isReflush = true;

		View view = inflater.inflate(
				R.layout.history_expandablelistview_fragment, container, false);
		imageListNull = (ImageView) view.findViewById(R.id.imageListNull);
		imageListNull.setImageResource(R.drawable.history_list_null);
		xExpandableListView = (XExpandableListView) view
				.findViewById(R.id.xExpandableListView);
		xExpandableListView.setXListViewListener(listener);
		// adapter = new HistoryFragmentListViewAdapter(
		// HistoryExpandableListViewFragment.this.getActivity());
		// xExpandableListView.setAdapter(adapter);
		// xListView.setIXListViewOnGoHome(iXListViewOnGoHome);
		// xListView.setPullGoHome(true);
		xExpandableListView.setFooterDividersEnabled(false);
		xExpandableListView.setPullLoadEnable(true);
		xExpandableListView.setGroupIndicator(null);

		xExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				// TODO Auto-generated method stub return true;
				return true;
			}

		});

		//
		// xExpandableListView.setOnItemLongClickListener(new
		// OnItemLongClickListener(){
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		//
		//
		// return true;
		// }
		// });

		// xExpandableListView.setOnItemLongClickListener(new
		// OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		// });
		

		adapter = new HistoryExpandableListAdapter(
				HistoryExpandableListViewFragment.this.getActivity());
		adapter.setOnItemLongClickChild(onItemLongClickChild);
		adapter.setOnItemClickChild(onItemClickChild);
		xExpandableListView.setAdapter(adapter);
		for (int i = 0; i < 3; i++) {
			xExpandableListView.expandGroup(i);
		}

		// xExpandableListView.setOnChildClickListener(new
		// OnChildClickListener() {
		//
		// @Override
		// public boolean onChildClick(ExpandableListView arg0, View arg1,
		// int arg2, int arg3, long arg4) {
		// // TODO Auto-generated method stub
		// Log.i(  "bbb", "arg2 ================= "+arg2);
		// Log.i(  "bbb", "arg3 ================= "+arg3);
		// toChatBean = child.get(arg2).get(arg3);
		//
		// if (GroupApplication.getInstance().getCurSP()
		// .equals(Contanst.TouristStone)) {
		// // 游客 直接进入
		// intoGroup(toChatBean.getGroupID(),
		// toChatBean.getGroupName());
		// } else {
		// opencamera(toChatBean.getGroupID(),
		// toChatBean.getGroupName(),
		// toChatBean.getDistance(),
		// toChatBean.getGroupPeopleCount(),
		// toChatBean.getAtNum());
		// }
		//
		// return false;
		// }
		// });

		return view;
	}

	private void showdialogDelete(final String id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this.getActivity(),
						R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("删除此群？");
		builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				for (int i = 0; i < historyList.size(); i++) {
					if (historyList.get(i).getGroupID().equals(id)) {
						historyList.remove(i);
					}
				}
				dbHelper.insertMainHistoryList(historyList);

				initialData(historyList);
				adapter.setList(child);
				adapter.setGroup(GroupName);
				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	OnItemLongClickChild onItemLongClickChild = new OnItemLongClickChild() {

		@Override
		public void getClick(String id) {
			// TODO Auto-generated method stub
			showdialogDelete(id);
		}
	};

	OnItemClickChild onItemClickChild = new OnItemClickChild() {

		@Override
		public void getClick(GroupListBean groupListBean) {
			// TODO Auto-generated method stub
			toChatBean = groupListBean;
//			if (GroupApplication.getInstance().getCurSP()
//					.equals(Contanst.TouristStone)) {
				// 游客 直接进入
				intoGroup(groupListBean.getGroupID(),
						groupListBean.getGroupName());
//			} else {
//				opencamera(groupListBean.getGroupID(),
//						groupListBean.getGroupName(),
//						groupListBean.getDistance(),
//						groupListBean.getGroupPeopleCount(),
//						groupListBean.getAtNum());
//			}

		}
	};

	private String getGroupIDs() {
		historygroupList.clear();
		historygroupList = dbHelper.getMainHistoryList(historygroupList);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < historygroupList.size(); i++) {
			sb.append(historygroupList.get(i).getGroupID());
			Log.i(  "aaa",
					"historygroupList.size()  ============= "
							+ historygroupList.size());
			if (i != historygroupList.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	private void conn(String ids) {
		Log.i(  "aaa", "ids ================ " + ids);

		if (ids == null || ids.equals("")) {
			onLoad();
			imageListNull.setVisibility(View.VISIBLE);
			xExpandableListView.setVisibility(View.GONE);
			return;
		}

		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		try {
			params1.add(new BasicNameValuePair("longitude", longitude));
			params1.add(new BasicNameValuePair("latitude", latitude));
			params1.add(new BasicNameValuePair("group_ids", ids));
			params1.add(new BasicNameValuePair("user_id", userID));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETHISTORYGROUPS, null, null,
				HistoryExpandableListViewFragment.this).addList(params1)
				.request(UrlParams.POST);
	}

	IXListViewListener listener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			if (isConn) {
				return;
			}
			conn(getGroupIDs());
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
			conn(getGroupIDs());
			isConn = true;
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
				HistoryExpandableListViewFragment.this).addList(params)
				.request(UrlParams.POST);
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
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.GETHISTORYGROUPS) {

					if (isReflush) {
						currentPage = 1;
						historyList.clear();

						ArrayList<GroupListBean> list = (ArrayList<GroupListBean>) msg.obj;
						historyList = list;
						if (list.size() == 0) {
							onLoad();
							imageListNull.setVisibility(View.VISIBLE);
							xExpandableListView.setVisibility(View.GONE);
							// historyLeftLineView.setVisibility(View.GONE);
							return;
						} else {
							if (imageListNull.getVisibility() == View.VISIBLE) {
								imageListNull.setVisibility(View.GONE);
								xExpandableListView.setVisibility(View.VISIBLE);
							}
						}
						initialData(list);
						adapter.setList(child);
						adapter.setGroup(GroupName);
						adapter.notifyDataSetChanged();
						// } else {
						// currentPage++;
						// ArrayList<GroupListBean> list =
						// (ArrayList<GroupListBean>) msg.obj;
						// historyList.addAll(historyList);
						// adapter.notifyDataSetChanged();
					}

					onLoad();

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					String group_id = map.get("group_id");

					Intent in = new Intent(
							HistoryExpandableListViewFragment.this
									.getActivity(),
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName,
								toChatBean.getGroupName());
						in.putExtra(ChatActivity.IsNewGroup, false);
						in.putExtra(ChatActivity.IsFirst, false);
						in.putExtra(ChatActivity.IsJump, false);
						in.putExtra(ChatActivity.AtMyNum, toChatBean.getAtNum());
					}

					// if (isSearch) {
					// // 添加搜索界面历史
					//
					// insertSearchHistory(group_id, group_name);
					// }
					// 添加本地历史记录
					insertSearchHistory(group_id, toChatBean.getGroupName());
					HistoryExpandableListViewFragment.this.getActivity()
							.startActivity(in);
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				if (msg.arg1 == Contanst.GETHISTORYGROUPS) {
					// xListView.setAdapter(adapter);
					// xListView.setPullLoadEnable(false);
					// adapter.notifyDataSetChanged();

					Log.i(  "aaa", "message = " + message);
					onLoad();
				} else if (msg.arg1 == Contanst.INTOGROUP) {
					Toast.makeText(
							HistoryExpandableListViewFragment.this
									.getActivity(),
							message, Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}

	};

	private ArrayList<ArrayList<GroupListBean>> child;
	ArrayList<String> GroupName = new ArrayList<String>(3);

	public void initialData(ArrayList<GroupListBean> historygroupList) {
		ArrayList<GroupListBean> todayList = new ArrayList<GroupListBean>();
		ArrayList<GroupListBean> sevenList = new ArrayList<GroupListBean>();
		ArrayList<GroupListBean> agoList = new ArrayList<GroupListBean>();

		child = new ArrayList<ArrayList<GroupListBean>>();
		for (int i = 0; i < historygroupList.size(); i++) {
			long time = historygroupList.get(i).getGroupTime();
			if (TimeUtil.isToday(time)) {
				todayList.add(historygroupList.get(i));
			} else if (TimeUtil.isSevenday(time)) {
				sevenList.add(historygroupList.get(i));
			} else {
				agoList.add(historygroupList.get(i));
			}
		}
		GroupName.clear();
		if (todayList.size() != 0) {
			child.add(todayList);
			if (!isListHas("今天"))
				GroupName.add("今天");
		}
		if (sevenList.size() != 0) {
			child.add(sevenList);
			if (!isListHas("7天内"))
				GroupName.add("7天内");
		}
		if (agoList.size() != 0) {
			child.add(agoList);
			if (!isListHas("较早"))
				GroupName.add("较早");
		}

	}

	private boolean isListHas(String str) {
		for (int i = 0; i < GroupName.size(); i++) {
			if (GroupName.get(i).equals(str)) {
				return true;
			}
		}
		return false;
	}

	private void onLoad() {
		xExpandableListView.stopRefresh();
		xExpandableListView.stopLoadMore();
		isConn = false;
		isReflush = false;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (historyList.size() == 0) {

				isReflush = true;

				conn(getGroupIDs());

			}

		}
	}

}
