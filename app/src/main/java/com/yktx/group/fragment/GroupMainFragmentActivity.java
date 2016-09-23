package com.yktx.group.fragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

import com.adcocoa.sdk.AdcocoaPopupAd;
import com.adcocoa.sdk.AdcocoaPopupAdListener;
import com.adcocoa.sdk.offerwalllibrary.AdcocoaOfferWall;
import com.adcocoa.sdk.offerwalllibrary.AdcocoaOfferWallListener;
import com.adcocoa.sdk.smartbannerlibrary.AdcocoaSmartBanner;
import com.adcocoa.sdk.smartbannerlibrary.AdcocoaSmartBannerPosition;
import com.astuetz.PagerSlidingTabStrip;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.umeng.fb.FeedbackAgent;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.group.ChatEasemobActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.MainActivity;
import com.yktx.group.MainHomeActivity;
import com.yktx.group.MessageActivity;
import com.yktx.group.R;
import com.yktx.group.SearchActivity;
import com.yktx.group.UserCenterActivity;
import com.yktx.group.fragment.BaseFragment.OnGoHomeListener;
import com.yktx.util.StickyLayout.OnGiveUpTouchEventListener;

public class GroupMainFragmentActivity extends FragmentActivity implements
OnGiveUpTouchEventListener {

	private NewFragment newFragment;

	private NearFragment nearFragment;

	private RecommendFragment recommendFragment;

	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	// private StickyLayout stickyLayout;

	public static final String MAIN_NEW_HTTP = "MAIN_NEW_HTTP";
	public static final String MAIN_NEAR_HTTP = "MAIN_NEAR_HTTP";
	public static final String MAIN_RECOMMEND_HTTP = "MAIN_RECOMMEND_HTTP";
	public static final String MAIN_HOT_HTTP = "MAIN_HOT_HTTP";
	public static final String MAIN_ATTENTION_HTTP = "MAIN_ATTENTION_HTTP";
	public static final String MAIN_HISTORY_HTTP = "MAIN_HISTORY_HTTP";

	private static final int notifiId = 11;
	protected NotificationManager notificationManager;
	private NewMessageBroadcastReceiver msgReceiver;

	FeedbackAgent fb;//友盟反馈

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GroupApplication.getInstance().addActivity(this);
		initChat();
		setContentView(R.layout.activity_main_group_fragment);
		setOverflowShowingAlways();
		dm = getResources().getDisplayMetrics();
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		pager.setOffscreenPageLimit(4);
		// pager.setCurrentItem(2);
		tabs.setViewPager(pager);
		initAdcoco();//初始化广告

		setTabsValue();
//		setUpUmengFeedback();
		fb = new FeedbackAgent(GroupMainFragmentActivity.this);
	}

	public void initAdcoco(){
		// 代码设置平台ID
		AdcocoaPopupAd.setPlatformId("2bc5f44bb73527647bdef54e797790a0");
		AdcocoaPopupAd.init(this);// 这里需要先调用平台的init方法！推荐写在onCreate的第一句。
		AdcocoaOfferWall.init(this);//初始化积分墙

		// 设置插屏广告关闭监听，每次关闭插屏广告都会调用此监听。此监听将在destroy时候销毁。
		AdcocoaPopupAd.setOnCloseListener(new AdcocoaPopupAdListener() {
			@Override
			public void onSucceed() {
//				Toast.makeText(GroupMainFragmentActivity.this, "插屏广告关闭了", Toast.LENGTH_LONG)
//				.show();
				AdcocoaSmartBanner.setPosition(GroupMainFragmentActivity.this, AdcocoaSmartBannerPosition.BOTTOM);
				AdcocoaSmartBanner.resume(GroupMainFragmentActivity.this);
			}

			@Override
			public void onError(int errorCode, String errorMsg) {
//				Toast.makeText(GroupMainFragmentActivity.this, "插屏广告关闭失败！你遇到过吗？反正我是没遇到过！",
//						Toast.LENGTH_SHORT).show();
			}
		});
		//打卡插屏广告的监听
		AdcocoaPopupAd.open(this, new AdcocoaPopupAdListener() {
			@Override
			public void onSucceed() {
//				Toast.makeText(GroupMainFragmentActivity.this, "插屏广告展示成功。",
//						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(int errorCode, String errorMsg) {
//				Toast.makeText(GroupMainFragmentActivity.this,
//						"插屏广告打开失败:" + errorCode + "; " + errorMsg,
//						Toast.LENGTH_SHORT).show();
			}
		});
		AdcocoaPopupAd.setCloseableOnBackPressd(true);
	}

	public void initChat(){
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		setContentView(R.layout.main_tab_activity);

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
	}
	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatEasemobActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(
							ChatEasemobActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatEasemobActivity.activityInstance
							.getToChatUsername()))
						return;
				}
			}

			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();


			notifyNewMessage(message);

			// 刷新bottom bar消息未读数
			//			updateUnreadLabel();
			//			if (currentTabIndex == 0) {
			//				// 当前页面如果为聊天历史页面，刷新此页面
			//				if (chatHistoryFragment != null) {
			MessageActivity.messageActivity.refresh();
			//				}
			//			}

		}
	}
	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
	 * 如果不需要，注释掉即可
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		//如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		//以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if(!EasyUtils.isAppRunningForeground(this)){
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(getApplicationInfo().icon)
		.setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = CommonUtils.getMessageDigest(message, this);
		String st = getResources().getString(R.string.expression);
		if(message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
		//设置状态栏提示
		mBuilder.setTicker(message.getFrom()+": " + ticker);

		//必须设置pendingintent，否则在2.3的机器上会有bug
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notificationManager.notify(notifiId, notification);
		notificationManager.cancel(notifiId);

	}
	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(getResources().getColor(R.color.meibao_color_3));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(getResources()
				.getColor(R.color.meibao_color_3));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);

		}
		//
		private final String[] titles = { "附近", "动态", "排行" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 1:
				if (newFragment == null) {
					newFragment = new NewFragment();
					newFragment.setOnGoHomeListener(goHomeListener);
				}
				return newFragment;

			case 2:
				if (recommendFragment == null) {
					recommendFragment = new RecommendFragment();
					recommendFragment.setOnGoHomeListener(goHomeListener);
				}
				return recommendFragment;

			case 0:
				if (nearFragment == null) {
					nearFragment = new NearFragment();
					nearFragment.setOnGoHomeListener(goHomeListener);
				}
				return nearFragment;

				//			case 3:
				//				if (attentionFragment == null) {
				//					attentionFragment = new AttentionFragment();
				//					attentionFragment.setOnGoHomeListener(goHomeListener);
				//				}
				//				return attentionFragment;
				//			case 4:
				//				
				//				if (historyExpandableListViewFragment == null) {
				//					historyExpandableListViewFragment = new HistoryExpandableListViewFragment();
				//					historyExpandableListViewFragment.setOnGoHomeListener(goHomeListener);
				//				}
				//				return historyExpandableListViewFragment;
				//				if (historyFragment == null) {
				//					historyFragment = new HistoryFragment();
				//					historyFragment.setOnGoHomeListener(goHomeListener);
				//				}
				//				return historyFragment;
			default:
				return null;
			}
		}

	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean giveUpTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 需要每个fragment 判断
		return false;
	}

	OnGoHomeListener goHomeListener = new OnGoHomeListener() {

		@Override
		public void goHome() {
			// TODO Auto-generated method stub
			// stickyLayout.goHome();
			GroupMainFragmentActivity.this.finish();
			overridePendingTransition(R.anim.activity_bottom_up,
					R.anim.activity_come_out_bottom);
		}
	};
	public static MenuItem mType,mSearch,mMore;
	//系统的Actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.group_main_fragment_activity, menu);
		mType = menu.findItem(R.id.action_message);
		mSearch = menu.findItem(R.id.action_search);
		mMore = menu.findItem(R.id.action_my);
		return true;
	}
	/**
	 * 显示 actionbar 前面的小图标和文字
	 * onMenuOpened 在显示选项菜单之前被调用。该方法在onPrepareOptionsMenu方法之后调用。
	 * onPrepareOptionsMenu:在显示选项菜单之前调用。一般用来修改即将显示的选项菜单。
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					//					TCAgent.onEvent(mContext, "gengduo");
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent in = null;
		switch (item.getItemId()) {
		case R.id.action_message:
			in = new Intent(this, MessageActivity.class);
			startActivity(in);
			break;
		case R.id.action_search:
			Intent intent = new Intent(GroupMainFragmentActivity.this,
					SearchActivity.class);
			startActivity(intent);
			break;
		case R.id.action_my:
			in = new Intent(this, UserCenterActivity.class);
			startActivity(in);
			break;
		case R.id.action_feedback:
			AdcocoaOfferWall.open(this, new AdcocoaOfferWallListener<Void>() {
				@Override
				public void onSucceed(Void result) {
					//					mResultView.setText("打开积分墙成功！");
//					Toast.makeText(GroupMainFragmentActivity.this, "打开积分墙成功！", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onError(int errorCode, String errorMsg) {
					//					mResultView.setText("打开积分墙失败 --> " + errorCode + " --> "
					//							+ errorMsg);
//					Toast.makeText(GroupMainFragmentActivity.this, "打开积分墙失败 --> " + errorCode + " --> "
//							+ errorMsg, Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case R.id.action_invite:
//			FeedbackAgent agent = new FeedbackAgent(GroupMainFragmentActivity.this);
//			agent.startFeedbackActivity();
			fb.sync();
			fb.startFeedbackActivity();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 双击返回键退出系统 第一次点击时间
	 */
	private long firstClickTime = 0;
	/**
	 * 双击返回键退出程序 第二次点击时间
	 */
	private long secondClickTime;
	/**
	 * 键盘按钮监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**
		 * 双击返回键退出程序
		 */
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (!isDoubleClickQuit) {
			// MainHomeActivity.this.finish();
			// } else {
			secondClickTime = System.currentTimeMillis();

			if ((secondClickTime - firstClickTime) < (1000 * 2)) {
				/*
				 * 杀死程序
				 */
				System.exit(0);
			} else {
				Toast.makeText(this, "再按一次返回键退出应用", 0).show();
				firstClickTime = secondClickTime;
			}
			return true;

			// }
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		AdcocoaOfferWall.destroy(this);//积分墙的销毁
		AdcocoaPopupAd.destroy(this);//插屏广告的销毁// 这里需要调用平台的destroy方法！释放相关资源。
		super.onDestroy();
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
	}
	//	//初始化和显示智能banner
	//	@Override
	//	protected void onResume() {
	//		super.onResume();
	//		AdcocoaSmartBanner.resume(this);
	//	}
	//暂停智能banner
	@Override
	protected void onPause() {
		AdcocoaSmartBanner.pause(this);
		super.onPause();
	}
//	private void setUpUmengFeedback() {
//		fb = new FeedbackAgent(this);
//		// check if the app developer has replied to the feedback or not.
//		fb.sync();
//		fb.openAudioFeedback();
//		fb.openFeedbackPush();
////		PushAgent.getInstance(this).enable();
//
//		//fb.setWelcomeInfo();
//		//  fb.setWelcomeInfo("Welcome to use umeng feedback app");
//		//        FeedbackPush.getInstance(this).init(true);
//		//        PushAgent.getInstance(this).setPushIntentServiceClass(MyPushIntentService.class);
//
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				boolean result = fb.updateUserInfo();
//			}
//		}).start();
//	}

}
