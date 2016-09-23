package com.yktx.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.yktx.bean.GetAttentionListBean;
import com.yktx.bean.UserInfoBean;
import com.yktx.group.adapter.ChatAllHistoryAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.fragment.HistoryActivity;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;

/**
 * 
 * 关注
 * 
 * @author Administrator
 * 
 */
public class MessageActivity extends BaseActivity implements ServiceListener {

	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	RelativeLayout message_upLayout;
	ListView listView;
	boolean isConn, isReflush;
	String longitude, latitude;
	ArrayList<GetAttentionListBean> getAttentionList = new ArrayList<GetAttentionListBean>(
			10);
	ChatAllHistoryAdapter adapter;
	int pageLimit, totalCount, currentPage, totalPage;
	int type;
	public static MessageActivity messageActivity;
	String userID;
	public boolean isFirstTalk = true;
	TextView xListViewNull;
	SharedPreferences setting;
	private ImageView message_leftImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		messageActivity = this;
		setContentView(R.layout.message_activity);
		final SharedPreferences settings = MessageActivity.this
				.getBaseContext().getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		userID = settings.getString("userID", "-1");
		setting = GroupApplication.getInstance().getSharedPreferences(
				Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		isFirstTalk = setting.getBoolean("isFirstTalk", true);
		message_leftImage = (ImageView) findViewById(R.id.message_leftImage);
		message_leftImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		if (isFirstTalk) {
			sendKF();
		}
		listView = (ListView) findViewById(R.id.xListView);
		// 注册上下文菜单
		registerForContextMenu(listView);

		adapter = new ChatAllHistoryAdapter(this, 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);
		xListViewNull = (TextView) findViewById(R.id.xListViewNull);
		xListViewNull.setVisibility(View.GONE);
		isReflush = true;

		// initTitile();
		final String st2 = getResources().getString(
				R.string.Cant_chat_with_yourself);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(GroupApplication.getInstance()
						.getUserName()))
					Toast.makeText(MessageActivity.this, st2, 0).show();
				else {
					// 进入聊天页面
					Intent intent = new Intent(MessageActivity.this,
							ChatEasemobActivity.class);
					// it is single chat
					intent.putExtra("userId", username);
					intent.putExtra("myNickName",
							settings.getString("userName", ""));
					intent.putExtra("myHead",
							UrlParams.IP + settings.getString("userPhoto", ""));
					intent.putExtra("myUserID",
							settings.getString("userID", ""));
					intent.putExtra("chatType",
							ChatEasemobActivity.CHATTYPE_SINGLE);
					startActivity(intent);
				}
			}
		});

		message_upLayout = (RelativeLayout) findViewById(R.id.message_upLayout);
		message_upLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(MessageActivity.this,
						HistoryActivity.class);
				MessageActivity.this.startActivity(in);
			}
		});
	}

	private void connGetUserInfoList() {
		
		if(ids != null && ids.length() > 0){

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {
				params.add(new BasicNameValuePair("user_ids", ids));

			} catch (Exception e) {

			}
			Service.getService(Contanst.HTTP_GETUSERINFOLIST, null, null,
					MessageActivity.this).addList(params).request(UrlParams.POST);
		} else {
			xListViewNull.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		// if (adapter != null)
		// adapter.notifyDataSetChanged();

		if (ids != null) {
			connGetUserInfoList();
		}
	}

	String ids;

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();

		StringBuffer sb = new StringBuffer();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0) {
				list.add(conversation);
				sb.append(conversation.getUserName());
				sb.append(",");
			}

		}
		ids = sb.toString();
		if (ids.length() > 0) {
			ids = ids.substring(0, ids.length() - 1);
		}
		// 排序
		sortConversationByLastChatTime(list);
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1,
					final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage
						.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage
						.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
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
				if (msg.arg1 == Contanst.GETUSERINFOLIST) {
					HashMap<String, UserInfoBean> map = (HashMap<String, UserInfoBean>) msg.obj;
					if (map.size() > 0) {
						adapter.setMap(map);
						adapter.notifyDataSetChanged();
						xListViewNull.setVisibility(View.GONE);
					} else {
						xListViewNull.setVisibility(View.VISIBLE);
					}
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				break;
			}
		}

	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMConversation tobeDeleteCons = adapter
					.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(
					tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(this);
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			adapter.remove(tobeDeleteCons);
			adapter.notifyDataSetChanged();
			if(adapter.getCount() == 0){
				xListViewNull.setVisibility(View.VISIBLE);
			}
			
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void sendKF() {
		// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation("11959");
		// 创建一条文本消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		// 设置消息body
		TextMessageBody txtBody = new TextMessageBody("无聊空虚寂寞冷，我来没事找事儿咯~~");
		message.addBody(txtBody);
		// 设置接收人
		message.setReceipt("11959");
		// 把消息加入到此会话对象中
		conversation.addMessage(message);
		// 发送消息
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}
		});
		isFirstTalk = false;
		Editor edit = setting.edit();
		// 需要判断是否是新用户
		edit.putBoolean("isFirstTalk", isFirstTalk);
		edit.commit();
	}

}
