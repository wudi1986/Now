package com.yktx.group;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.yktx.bottombar.BottomBarOnClickListener;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.group.fragment.GroupMainFragmentActivity;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements BottomBarOnClickListener {

	private TabHost mHost;
	private Intent mFramentActivity;
	private Intent mSearchIntent;
	private Intent mUserCenterIntent;
	private Intent mMessageIntent;

	private NewMessageBroadcastReceiver msgReceiver;
	

    private static final int notifiId = 11;
    protected NotificationManager notificationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		setContentView(R.layout.main_tab_activity);
		
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);


		this.mSearchIntent = new Intent(this, MainHomeActivity.class);
		this.mFramentActivity = new Intent(this, GroupMainFragmentActivity.class);
		this.mMessageIntent = new Intent(this, MessageActivity.class);
		this.mUserCenterIntent = new Intent(this, UserCenterActivity.class);


		this.mHost = getTabHost();	
		TabHost localTabHost = this.mHost;

		localTabHost.addTab(buildTabSpec("Doing", R.string.app_name,
				R.drawable.icon, this.mSearchIntent));

		localTabHost.addTab(buildTabSpec("distory", R.string.app_name,
				R.drawable.icon, this.mFramentActivity));

		localTabHost.addTab(buildTabSpec("message", R.string.app_name,
				R.drawable.icon, this.mMessageIntent));

		localTabHost.addTab(buildTabSpec("my", R.string.app_name,
				R.drawable.icon, this.mUserCenterIntent));
		initBottomBar();

	}

	public com.yktx.bottombar.BottomBar bottomBar;
	String[] bottomTitle = { "Doing", "发现", "消息", "我" };
	private void initBottomBar() {
		Log.i(  "kkk", "initBottomBar");
		this.bottomBar = (com.yktx.bottombar.BottomBar) findViewById(R.id.titleTab);
		bottomBar.setVisibility(View.VISIBLE);
		this.bottomBar.setBottomBarOnClickListener(this);
		bottomBar.addItems(bottomTitle, 0);
	}

	
	

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}



	@Override
	public void onClick(int position) {
		// TODO Auto-generated method stub
		switch(position){
		case 0:
			this.mHost.setCurrentTabByTag("Doing");
			break;
		case 1:
			this.mHost.setCurrentTabByTag("distory");
			break;
		case 2:
			this.mHost.setCurrentTabByTag("message");
			break;
		case 3:
			this.mHost.setCurrentTabByTag("my");
			break;
		}
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
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
    }
}