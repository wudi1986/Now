/**
 * 
 */
package com.yktx.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yktx.bean.GroupListBean;
import com.yktx.group.adapter.SearchListAdapter;
import com.yktx.group.conn.HttpPostListener;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.SearchYoukuGroupService;
import com.yktx.group.service.Service;
import com.yktx.sqlite.DBHelper;
import com.yktx.util.Contanst;
import com.yktx.util.Tools;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年8月14日 下午3:10:41  
 * 类说明  搜索主页*/
/**
 * @author Administrator
 * 
 */
public class SearchActivity extends BaseActivity implements ServiceListener {

	SharedPreferences settings;
	String searchInfo;
	int sign = -1;
	String number;
	String longitude;
	String latitude;
	String userID;
	String group_id, group_name, newGroupId;
	TextView search_history;
	ImageView noHistoryImage;
	ListView searchListView;
	private EditText mEtSearch = null;
	private Button mBtnClearSearchText = null;
	SearchListAdapter searchListAdapter;
	DBHelper dbHelper;
	/** 搜索进来的 */
	GroupListBean searchGroupBean;
	Timer timer;
	ArrayList<GroupListBean> historyList = new ArrayList<GroupListBean>(10);
	ArrayList<GroupListBean> searchList = new ArrayList<GroupListBean>(10);

	// String historyGroupIds;
	/** 搜索文字 */
	String searchStr;
	private final int SearchTime = 1000;

	private static final int SEARCH_YOUKU = 101;

	private static final int TIMER_SEARCH = 102;

	private GroupListBean insertGroupBean;

	String lastGroupID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yktx.yingtao.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		timer = new Timer();
		settings = this.getBaseContext().getSharedPreferences(
				(GroupApplication.getInstance()).getCurSP(), 0);
		number = settings.getString("phone", "-1");
		userID = settings.getString("userID", "-1");

		SharedPreferences setting = this.getSharedPreferences(
				Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		lastGroupID = settings.getString("lastGroupID", "-1");
		noHistoryImage = (ImageView) findViewById(R.id.noHistoryImage);
		searchListView = (ListView) findViewById(R.id.searchListView);
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				group_id = searchList.get(position).getGroupID();
				group_name = searchList.get(position).getGroupName();
				insertGroupBean = searchList.get(position);
				searchStr = group_name;
				if (!isFirstCamera()) {
					// intoGroup(group_id, group_name);
				}
			}
		});

		searchListView.setOnItemLongClickListener(onItemLongClickListener);
		initSearch();
		dbHelper = new DBHelper(this);
		// historyGroupIds = dbHelper.getGroupIdList().toString();
		// /**
		// * 获取历史搜索群最新消息
		// */
		// getHistoryGroupList(historyGroupIds.substring(1,
		// historyGroupIds.length() - 1));

		historyList = dbHelper.getNameList(historyList);
		searchList.clear();
		Log.i(  "aaa", "historyList =============== "
				+ historyList.size());
		searchList = historyList;
		if (historyList.size() == 0) {
			// 无历史显示图片
			search_history.setVisibility(View.GONE);
			noHistoryImage.setVisibility(View.VISIBLE);
		} else {
			Log.i(  "aaa", "setHistoryList------------------");
			searchListAdapter = new SearchListAdapter(SearchActivity.this,
					historyList, null);
			searchListView.setAdapter(searchListAdapter);
			searchListAdapter.notifyDataSetChanged();
		}

		TextView right = (TextView) findViewById(R.id.right);
		right.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SearchActivity.this.finish();
			}
		});

	}

	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			showdialogDelete(arg2);
			return true;
		}
	};

	private void showdialogDelete(final int index) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("确认删除？");
		builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				historyList.remove(index);
				dbHelper.insertSearchList(historyList);
				searchListAdapter.notifyDataSetInvalidated();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	long inGroupTime;

	/**
	 * 判断是否先拍照
	 */
	private boolean isFirstCamera() {
		inGroupTime = settings.getLong("inGroupTime", 0);
		// if (!lastGroupID.equals(group_id)
		// || System.currentTimeMillis() - inGroupTime > Contanst.LOGIN_TIME *
		// 60 * 1000) {

//		if (GroupApplication.getInstance().getCurSP()
//				.equals(Contanst.TouristStone)) {
			// 游客 直接进入
			intoGroup(null, group_name);
//		} else {
//			opencamera();
//			// opencamera(toChatBean.getGroup_id(), toChatBean.getGroup_name(),
//			// toChatBean.getDistance(), toChatBean.getGroupManCount(), "0");
//		}
		insertSearchHistory("", group_name);
		return true;
		// }
		// return false;
	}

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
				SearchActivity.this).addList(params).request(UrlParams.POST);
	}

	/* 打开相机 */
//	private void opencamera() {
//		Intent cameraIntent = new Intent(SearchActivity.this,
//				CameraActivity.class);
//		cameraIntent.putExtra(CameraActivity.IsRegister, "0");
//		cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
//		cameraIntent.putExtra("longitude", longitude);
//		cameraIntent.putExtra("latitude", latitude);
//		// cameraIntent.putExtra("group_id", group_id);
//		cameraIntent.putExtra("group_name", group_name);
//		// cameraIntent.putExtra("insertGroupBean", insertGroupBean);
//		cameraIntent.putExtra("isSearch", true);
//		startActivityForResult(cameraIntent, 444);
//	}

	private void insertSearchHistory(String group_id, String group_Name) {
		// 添加历史
		ArrayList<GroupListBean> historygroupList = dbHelper
				.getNameList(historyList);

		if (historygroupList.size() > 0 && historygroupList != null) {
			for (int i = 0; i < historygroupList.size(); i++) {
				GroupListBean historyListBean = historygroupList.get(i);
				if (group_Name.equals(historyListBean.getGroupName())) {
					Log.i(  "aaa", "重复数据" + group_Name);
					// 如果重复不添加
					historygroupList.get(i).setGroupTime(
							System.currentTimeMillis());
					dbHelper.insertSearchList(historygroupList);
					return;
				}
			}

			if (insertGroupBean.getGroupName() != null
					&& insertGroupBean.getGroupName().length() > 0) {
				insertGroupBean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(insertGroupBean);
				dbHelper.insertSearchList(historygroupList);
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				bean.setGroupPeopleCount("1");
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				dbHelper.insertSearchList(historygroupList);
			}
		} else {
			if (insertGroupBean.getGroupName() != null
					&& insertGroupBean.getGroupName().length() > 0) {
				insertGroupBean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(insertGroupBean);
				dbHelper.insertSearchList(historygroupList);
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				bean.setGroupPeopleCount("1");
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				dbHelper.insertSearchList(historygroupList);
			}
		}
	}

	private void initSearch() {

		mEtSearch = (EditText) findViewById(R.id.et_search);
		mEtSearch.setFocusable(true);
		search_history = (TextView) findViewById(R.id.search_history);

		search_history.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 清除历史
				dbHelper.deleteSearchList();
				historyList.clear();
				searchListAdapter.setList(historyList);
				searchListAdapter.notifyDataSetChanged();
				search_history.setVisibility(View.GONE);
				noHistoryImage.setVisibility(View.VISIBLE);
			}
		});
		mBtnClearSearchText = (Button) findViewById(R.id.btn_clear_search_text);
		mEtSearch.addTextChangedListener(new TextWatcher() {

			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				searchStr = mEtSearch.getText().toString();
				selectionStart = mEtSearch.getSelectionStart();
				selectionEnd = mEtSearch.getSelectionEnd();
				final int textLength = mEtSearch.getText().length();
				if (Tools.getLineSize(searchStr) > 20) {
					Toast.makeText(SearchActivity.this, "字数超限",
							Toast.LENGTH_SHORT).show();
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					mEtSearch.setText(s);
					mEtSearch.setSelection(tempSelection);
				}

				timer.cancel();
				timer = new Timer();
				/**
				 * 延时一秒执行
				 */
				timer.schedule(new TimerTask() {
					public void run() {
						Message msg = new Message();
						msg.what = TIMER_SEARCH;
						msg.arg1 = textLength;
						mHandler.sendMessage(msg);
					}
				}, SearchTime);

			}
		});

		mBtnClearSearchText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEtSearch.setText("");
				mBtnClearSearchText.setVisibility(View.GONE);
			}
		});
		mEtSearch.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					String query = mEtSearch.getText().toString().trim();
					Log.i(  "aaa", "query === " + query);
					conn(0, query);
					if (query != null && query.length() == 0) {
						// Toast.makeText(SearchActivity.this, "请输入搜索的关键字",
						// Toast.LENGTH_LONG).show();
					} else {

					}
					searchInfo = query;
					return true;
				}
				return false;
			}
		});
	}

	public void searchConn(String keywords) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("keywords", keywords));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_SEARCHGROUP, null, null,
				SearchActivity.this).addList(params).request(UrlParams.POST);

	}

	boolean isNewGroup = false;

	/**
	 * 进群接口
	 */
	// private void intoGroup(String group_id, String group_name) {
	// Log.i(  "aaa",
	// "intoGroup  group_name ============ "+group_name);
	// TelephonyManager tm = (TelephonyManager) SearchActivity.this
	// .getSystemService(TELEPHONY_SERVICE);
	// final String Imei = tm.getDeviceId();
	//
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// try {
	// params.add(new BasicNameValuePair("longitude", longitude));
	// params.add(new BasicNameValuePair("latitude", latitude));
	// if (group_id != null && group_id.length() > 0) {
	// params.add(new BasicNameValuePair("group_id", group_id));
	// }
	// if (group_name != null && group_name.length() > 0) {
	// params.add(new BasicNameValuePair("group_name", group_name));
	// isNewGroup = true;
	// }
	// params.add(new BasicNameValuePair("token", Imei));
	// } catch (Exception e) {
	//
	// }
	// Service.getService(Contanst.HTTP_INTOGROUP, null, null,
	// SearchActivity.this).addList(params).request(UrlParams.POST);
	// }

	/**
	 * 搜索历史搜索群最新消息
	 */
	// private void getHistoryGroupList(String historyGroupIds) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// try {
	// params.add(new BasicNameValuePair("longitude", longitude));
	// params.add(new BasicNameValuePair("latitude", latitude));
	// params.add(new BasicNameValuePair("group_ids", historyGroupIds));
	// Log.i(  "aaa", params + "");
	// } catch (Exception e) {
	//
	// }
	// Service.getService(Contanst.HTTP_GETHISTORYGROUPS, null, null,
	// SearchActivity.this).addList(params).request(UrlParams.POST);
	// }

	class MyGridView extends GridView {
		public MyGridView(android.content.Context context,
				android.util.AttributeSet attrs) {
			super(context, attrs);
		}

		/**
		 * 设置不滚动
		 */
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}

	int typtID;
	String choice;

	/**
	 * typeID 1 为按照类型筛选 0按照标题
	 */
	private void conn(int typeID, String choice) {
		this.typtID = typeID;
		this.choice = choice;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("phone", number));
			params.add(new BasicNameValuePair("chooice", choice));
			params.add(new BasicNameValuePair("istypeId", typeID + ""));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_SEARCHGROUP, null, null,
				SearchActivity.this).addList(params).request(UrlParams.POST);
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
				/**
				 * 搜索用户输入 关键字的群
				 */
				if (msg.arg1 == Contanst.SEARCHGROUP) {
					ArrayList<GroupListBean> list = (ArrayList<GroupListBean>) msg.obj;
					searchList = list;
					if (searchList.size() > 0) {
						if (searchListAdapter != null) {
							searchListAdapter = null;
						}
						boolean isSame = false;
						for (int i = 0; i < searchList.size(); i++) {
							String curName = searchList.get(i).getGroupName();
							if (curName.equals(searchStr)) {
								isSame = true;
								break;
							}
						}
						if (!isSame) {
							// 没有搜索到数据添加新建群组，
							addNewGroup();
						}
						noHistoryImage.setVisibility(View.GONE);

						searchListAdapter = new SearchListAdapter(
								SearchActivity.this, searchList, searchStr);
						searchListView.setAdapter(searchListAdapter);
						searchListView.setOnItemLongClickListener(null);
						searchListAdapter.notifyDataSetChanged();
					} else {
						searchGroup();
					}
					/**
					 * 进群接口
					 */
				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					String group_id = map.get("group_id");

					Intent in = new Intent(SearchActivity.this,
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName, group_name);
						in.putExtra(ChatActivity.IsNewGroup, false);
//						in.putExtra(ChatActivity.IsFirst, false);
//						in.putExtra(ChatActivity.IsJump, false);
						// in.putExtra(ChatActivity.AtMyNum,
						// toChatBean.getMsgnum());
					}

					insertGroupSearchHistory(group_id, group_name);
					SearchActivity.this.startActivity(in);
				}
				break;
			case Contanst.BEST_INFO_FAIL:

				// if (msg.arg1 == Contanst.SEARCHGROUP) {
				//
				// }
				//
				// if (msg.arg1 == Contanst.INTOGROUP) {
				//
				// }
				//
				// if (msg.arg1 == Contanst.GETHISTORYGROUPS) {
				//
				// }
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
//				Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG)
//						.show();
				break;

			/**
			 * 优酷链接搜索
			 */
			case SEARCH_YOUKU:
				ArrayList<GroupListBean> searchYoukuList = (ArrayList<GroupListBean>) msg.obj;
				searchList = searchYoukuList;

				boolean isHaveSameName = false;
				for (int i = 0; i < searchYoukuList.size(); i++) {
					String curName = searchYoukuList.get(i).getGroupName();
					if (curName.equals(searchStr)) {
						isHaveSameName = true;
						break;
					}
				}
				if (!isHaveSameName) {
					// 没有搜索到数据添加新建群组，
					addNewGroup();
				}
				if (searchList.size() > 0) {
					if (searchListAdapter != null) {
						searchListAdapter = null;
					}
					noHistoryImage.setVisibility(View.GONE);
				}
				searchListAdapter = new SearchListAdapter(SearchActivity.this,
						searchList, searchStr);
				searchListView.setAdapter(searchListAdapter);
				searchListAdapter.notifyDataSetChanged();
				break;

			case TIMER_SEARCH:

				if (msg.arg1 > 0) {
					search_history.setVisibility(View.GONE);
					mBtnClearSearchText.setVisibility(View.VISIBLE);
					searchConn(searchStr);

				} else {
					if (searchListAdapter != null) {
						searchListAdapter = null;
					}
					searchListAdapter = new SearchListAdapter(
							SearchActivity.this, historyList, searchStr);
					searchListView.setAdapter(searchListAdapter);
					searchListAdapter.notifyDataSetChanged();
					if (historyList.size() > 0) {

						noHistoryImage.setVisibility(View.VISIBLE);
						noHistoryImage.setVisibility(View.GONE);
					} else {
						// 搜索出来的消息，不是历史，所以隐藏历史记录
						search_history.setVisibility(View.GONE);
						noHistoryImage.setVisibility(View.VISIBLE);

					}
					mBtnClearSearchText.setVisibility(View.GONE);
				}
				break;

			}
		}
	};

	/**
	 * 搜索字段不完全相等。 显示新的群
	 */
	private void addNewGroup() {
		// 没有搜索到数据添加新建群组，
		GroupListBean newGroup = new GroupListBean();
		newGroup.setDistance("0.0km");
		newGroup.setGroupID("");
		newGroup.setGroupName(searchStr);
		newGroup.setGroupPeopleCount("0");
		searchList.add(0, newGroup);
		// 搜索出来的消息，不是历史，所以隐藏历史记录
		search_history.setVisibility(View.GONE);
		noHistoryImage.setVisibility(View.VISIBLE);
	}

	protected HttpPostListener httpPostListener = new HttpPostListener() {

		public void connFail(String erro) {
			Log.i(  "aaa", "connFail");
			Log.i(  "aaa", "erro = " + erro);
		}

		public void connSuccess(String reponse) {
			Log.i(  "aaa", "connSuccess");
			Log.i(  "aaa", "reponse = " + reponse);
		}
	};

	/**
	 * 若服务端无检索数据 则去优酷开放链接获取联想词提示用户
	 */
	private void searchGroup() {

		new Thread() {
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					params.add(new BasicNameValuePair("query", searchStr));
				} catch (Exception e) {

				}
				ArrayList<GroupListBean> searchYoukuList = SearchYoukuGroupService
						.doHttpPostJSON(params, httpPostListener);
				Message msg = new Message();
				msg.obj = searchYoukuList;
				msg.what = SEARCH_YOUKU;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.i(  "aaa",
				"onActivityResult  requestCode ============ " + requestCode);
		Log.i(  "aaa",
				"onActivityResult  resultCode ============ " + resultCode);

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 111 && resultCode == 222) {
			searchConn(searchStr);
		}
		if (requestCode == 444 && resultCode == 111) {
			// intoGroup(group_id, group_name);
			Log.i(  "aaa",
					"onActivityResult  group_name ============ " + group_name);
		}
		if (requestCode == 444 && resultCode == 0) {
			SearchActivity.this.finish();
			// 从聊天返回
		}

	}

	public void insertGroupSearchHistory(String group_id, String group_Name) {
		// 添加历史
		ArrayList<GroupListBean> historygroupList = new ArrayList<GroupListBean>(
				10);
		// if (isMain) {
		historygroupList = dbHelper.getMainHistoryList(historygroupList);
		// } else {
		// historygroupList = dbHelper.getNameList(historygroupList);
		// }

		if (historygroupList.size() > 0 && historygroupList != null) {
			for (int i = 0; i < historygroupList.size(); i++) {
				GroupListBean historyListBean = historygroupList.get(i);
				if (group_id.equals(historyListBean.getGroupID())) {
					Log.i(  "aaa", "重复数据" + group_Name);
					// 如果重复不添加
					historygroupList.get(i).setGroupTime(
							System.currentTimeMillis());
					// dbHelper.insertSearchList(historygroupList);

					// if (isMain) {
					dbHelper.insertMainHistoryList(historygroupList);
					// } else {
					// dbHelper.insertSearchList(historygroupList);
					// }
					return;
				}
			}

			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				// if (group_distance != null) {
				// bean.setDistance(group_distance);
				// } else {
				bean.setDistance("0.04km");
				// }
				// if (group_peopleCount != null) {
				// bean.setGroupPeopleCount(group_peopleCount);
				// } else {
				bean.setGroupPeopleCount("1");
				// }
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		} else {
			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				dbHelper.insertMainHistoryList(historygroupList);
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				bean.setGroupPeopleCount("1");
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		}
	}

}
