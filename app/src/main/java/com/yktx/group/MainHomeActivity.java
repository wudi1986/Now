package com.yktx.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.update.UmengUpdateAgent;
import com.yktx.bean.GroupListBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.sqlite.DBHelper;
import com.yktx.util.Contanst;
import com.yktx.wheel.widget.ArrayWheelAdapter;
import com.yktx.wheel.widget.OnWheelChangedListener;
import com.yktx.wheel.widget.OnWheelScrollListener;
import com.yktx.wheel.widget.WheelView;
import com.yktx.wheel.widget.WheelView.WheelOnClick;
/**
 * 
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class MainHomeActivity extends BaseActivity implements ServiceListener {

	// private RelativeLayout rightHead;
	// private ImageView userHead;
	String userHeadUrl;
	SharedPreferences settings;
	boolean isFirstUp, isFirstDown;
	// ImageView userHead_first;
	// TextView firstTextLeft, firstTextRight;
	// View goMainGroup;
	TextView mainSearchEdit;

	int curWheelIndex;

	DBHelper dbHelper;
	ArrayList<GroupListBean> historyList = new ArrayList<GroupListBean>(10);
	/** 搜索进来的 */
	GroupListBean searchGroupBean;
	String longitude, latitude, userID;
	SharedPreferences setting;
	WheelView wheelView;

	ArrayList<GroupListBean> wheelViewList = new ArrayList<GroupListBean>(5);
	RelativeLayout searchLayout;
//	private RelativeLayout rl_activity;
	private ImageView mTitleLeft,mTitleRight;
	private TextView mTitleContent;
	private float y;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setting = MainHomeActivity.this.getSharedPreferences(
				Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		settings = MainHomeActivity.this.getSharedPreferences(
				(GroupApplication.getInstance()).getCurSP(), 0);
		userID = settings.getString("userID", "-1");
		UmengUpdateAgent.update(this);
		dbHelper = new DBHelper(this);
		historyList = dbHelper.getMainHistoryList(historyList);
		options = new DisplayImageOptions.Builder().showImageOnLoading(null)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// 启用内存缓存
				.displayer(new RoundedBitmapDisplayer(100))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		setting = MainHomeActivity.this.getSharedPreferences(
				Contanst.UserStone, 0);
		userHeadUrl = setting.getString("userPhoto", "");

		setContentView(R.layout.activity_main_home);

		wheelView = (com.yktx.wheel.widget.WheelView) findViewById(R.id.picker);

		connHotSearchGroup();
		// userHead_first = (ImageView) findViewById(R.id.userHead_first);
//		rl_activity = (RelativeLayout) findViewById(R.id.rl_activitya);// 全局布局文件

		searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
		
		//修改title
		initTitle();
		
		searchLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				y = searchLayout.getY() - 10;
				TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
						-y);
				animation.setDuration(400);
				animation.setFillAfter(true);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Intent intent = new Intent(MainHomeActivity.this,
								SearchActivity.class);
						startActivityForResult(intent, 100);
						// overridePendingTransition(R.anim.animation_2,R.anim.animation_1);
					}
				});
				searchLayout.startAnimation(animation);

				// Intent in = new Intent(MainHomeActivity.this,
				// SearchActivity.class);
				// MainHomeActivity.this.startActivity(in);
			}
		});

	}
	private void initTitle() {
		mTitleLeft = (ImageView) findViewById(R.id.leftImage);
		mTitleRight = (ImageView) findViewById(R.id.rightImage);
		mTitleContent = (TextView) findViewById(R.id.title);
		mTitleLeft = (ImageView) findViewById(R.id.leftImage);
		mTitleLeft.setImageResource(R.drawable.zhuce_back);
		mTitleRight = (ImageView) findViewById(R.id.rightImage);
		mTitleRight.setVisibility(View.GONE);
		mTitleContent.setText("搜索");
		mTitleLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 返回
				MainHomeActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (socektTimer != null)
			socektTimer.cancel();
	}

	private void initWheel(ArrayList<GroupListBean> list) {

		wheelView.setAdapter(new ArrayWheelAdapter(list));
		wheelView.addChangingListener(changedListener);
		wheelView.setCyclic(true);
		wheelView.setVisibleItems(2);
		wheelView.setCurrentItem(1);
		wheelView.addScrollingListener(scrolledListener);
		wheelView.onClick(onClick);

		// wheelView.setInterpolator(new AnticipateOvershootInterpolator());
	}
	String group_name = "";
	WheelOnClick onClick = new WheelOnClick() {

		@Override
		public void onClick() {
			// TODO Auto-generated method stub
			group_name = wheelViewList.get(curWheelIndex).getGroupName();
			intoGroup(wheelViewList.get(curWheelIndex).getGroupID(),
					group_name);
		}
	};
	// Wheel changed listener
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {

		}
	};

	OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
		}

		public void onScrollingFinished(WheelView wheel) {
			curWheelIndex = wheelView.getCurrentItem();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, -y, 0);
		animation.setDuration(400);
		animation.setFillAfter(true);
		searchLayout.startAnimation(animation);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void connHotSearchGroup() {

		if (userID == null || userID.length() == 0) {
			SharedPreferences setting = GroupApplication.getInstance()
					.getSharedPreferences(
							(GroupApplication.getInstance()).getCurSP(), 0);
			userID = setting.getString("userID", null);

		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Service.getService(Contanst.HTTP_HOTSEARCHGROUP, null, null,
				MainHomeActivity.this).addList(params).request(UrlParams.POST);
	}

	/**
	 * 进群接口
	 */
	private void intoGroup(String group_id, String group_name) {

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
				MainHomeActivity.this).addList(params).request(UrlParams.POST);
	}

	/* 打开相机 */
	// private void opencamera() {
	//
	// Intent cameraIntent = new Intent(MainHomeActivity.this,
	// CameraActivity.class);
	// cameraIntent.putExtra(CameraActivity.IsRegister, "0");
	// cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
	// cameraIntent.putExtra("longitude", longitude);
	// cameraIntent.putExtra("latitude", latitude);
	// // cameraIntent.putExtra("group_id", group_id);
	// cameraIntent
	// .putExtra("group_name", mainSearchEdit.getText().toString());
	// // cameraIntent.putExtra("insertGroupBean", insertGroupBean);
	// cameraIntent.putExtra("isSearch", true);
	// startActivityForResult(cameraIntent, 444);
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

//	/**
//	 * 双击返回键退出系统 第一次点击时间
//	 */
//	private long firstClickTime = 0;
//	/**
//	 * 双击返回键退出程序 第二次点击时间
//	 */
//	private long secondClickTime;
//
//	/**
//	 * 键盘按钮监听
//	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		/**
//		 * 双击返回键退出程序
//		 */
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			// if (!isDoubleClickQuit) {
//			// MainHomeActivity.this.finish();
//			// } else {
//			secondClickTime = System.currentTimeMillis();
//
//			if ((secondClickTime - firstClickTime) < (1000 * 2)) {
//				/*
//				 * 杀死程序
//				 */
//				System.exit(0);
//			} else {
//				Toast.makeText(this, "再按一次返回键退出应用", 0).show();
//				firstClickTime = secondClickTime;
//			}
//			return true;
//
//			// }
//		}
//		return super.onKeyDown(keyCode, event);
//	}

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

				if (msg.arg1 == Contanst.HOTSEARCHGROUP) {

					wheelViewList = (ArrayList<GroupListBean>) msg.obj;
					initWheel(wheelViewList);
					startSokectTimer();

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					String group_id = map.get("group_id");
					
					Intent in = new Intent(MainHomeActivity.this,
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName, group_name);
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
					insertSearchHistory(group_id, group_name);
					MainHomeActivity.this.startActivity(in);
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				if (msg.arg1 == Contanst.HOTSEARCHGROUP) {

				} else if (msg.arg1 == Contanst.INTOGROUP) {
					Toast.makeText(MainHomeActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				break;

			case Contanst.MINA_TIMER_TASK:
				if (wheelView != null) {
					curWheelIndex = curWheelIndex + 1 >= wheelViewList.size() ? 0
							: curWheelIndex + 1;
					wheelView.setCurrentItem(curWheelIndex,true);
				}
				break;
			}
		}

	};
	/**
	 * 滚动计时器2s
	 */
	Timer socektTimer;

	private void startSokectTimer() {
		socektTimer = new Timer(true);
		socektTimer.schedule(task, 1000, 2000);
	}

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = Contanst.MINA_TIMER_TASK;
			mHandler.sendMessage(message);
		}
	};

	public void insertSearchHistory(String group_id, String group_Name) {
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
