package com.yktx.group.service;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.bean.ChatListBean;
import com.yktx.bean.ZanBean;
import com.yktx.group.ChatActivity;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * @author Administrator hot/near groupList
 * 
 */
public class GetHistoryMessageService extends Service {

	public GetHistoryMessageService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_GETHISTORYMESSAGE;
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETHISTORYMESSAGE);
		Log.i(  "aaa", "httpFailhttpFail");
		// LodingActivity.isJion = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yktx.group.service.Service#httpSuccess(java.lang.String)
	 */
	@Override
	void httpSuccess(String reponse) {
		// LodingActivity.isJion = false;
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.getString("statusCode");

			// HomeBestView.listCount =
			// Integer.parseInt((String)result.get("tot_count"));
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据

				// JSONObject groupPager = result.getJSONObject("groupPager");
				// Log.i(  "aaa", "groupPager === " + groupPager);
				JSONObject pager =result.getJSONObject("pager");
				
				String chatMessageNum = pager.getString("totalCount");
				ChatActivity.chatTotalCount = chatMessageNum;
				JSONArray searchArray = pager.getJSONArray("listData");
				ArrayList<ChatListBean> searchList = new ArrayList<ChatListBean>(10);
				long sendLastTime = ChatActivity.lastDate;
				for (int j = 0; j < searchArray.length(); j++) {
					JSONObject jsonChat = searchArray.getJSONObject(j);
					ChatListBean chatBean = new ChatListBean();

					if (jsonChat.has("send_time")) {
						long chatTime = jsonChat.getLong("send_time");
						if (ChatActivity.lastDate == 0) { // 第一条数据，直接接收
							chatBean.setSendTime(chatTime);

						} else { // 获取消息记录添加系统消息
							
//							if (TimeUtil.isSameDate(sendLastTime, chatTime)) { // 同一天消息
								chatBean.setSendTime(chatTime);
//							} else {
//								ChatListBean chatBean1 = new ChatListBean();
//								chatBean1.setContent(TimeUtil
//										.getMMDD(sendLastTime / 1000));
//								searchList.add(chatBean1);
//								chatBean.setSendTime(chatTime);
//							}
							sendLastTime = chatTime;
						}
						if (j == searchArray.length() - 1) {
							ChatActivity.lastDate = chatTime;
						} else if (j == 0 && ChatActivity.curDate == 0) {
							ChatActivity.curDate = chatTime;
						}
					}
					String flag = "";
					if (jsonChat.has("flag")) {
						flag = jsonChat.getString("flag");
						chatBean.setFlag(flag);
					}
					if (jsonChat.has("id"))
						chatBean.setChatId(jsonChat.getString("id"));
					if (jsonChat.has("name"))
						chatBean.setUserName(jsonChat.getString("name"));
					if (jsonChat.has("distance"))
						chatBean.setDistance(jsonChat.getString("distance"));
					if (jsonChat.has("status"))
						chatBean.setChatStatus(jsonChat.getString("status"));
					if (jsonChat.has("user_id"))
						chatBean.setUserId(jsonChat.getString("user_id"));
					if (jsonChat.has("photo"))
						chatBean.setUserHead(jsonChat.getString("photo"));
					if (jsonChat.has("sex"))
						chatBean.setSex(jsonChat.getString("sex"));
					if (jsonChat.has("integral"))
						chatBean.setLevel(jsonChat.getString("integral"));
					if (jsonChat.has("birthday"))
						chatBean.setBirthday(jsonChat.getLong("birthday"));
					if (jsonChat.has("content")) {

						if (flag.equals(ChatActivity.CHAT_FLAG_ZAN_MESSAGE + "")) {
							String content = jsonChat.getString("content");
							String[] array = content.split(",");
							chatBean.setContent(array[0]);
							chatBean.setPhotoChatID(array[1]);
						} else {
							chatBean.setContent(jsonChat.getString("content"));
						}

					}
					if (jsonChat.has("send_time"))
						chatBean.setSendTime(jsonChat.getLong("send_time"));

					if (jsonChat.has("content_title"))
						chatBean.setContent_title(jsonChat
								.getString("content_title"));
					if (jsonChat.has("user_list")) {
						String str = jsonChat.getString("user_list");
						String[] strArray = str.split(",");
						chatBean.setAtIDList(strArray);
					}
					if (jsonChat.has("gatuser")) {
						ArrayList<ZanBean> zanList = new ArrayList<ZanBean>(10);
						JSONArray zanArray = jsonChat.getJSONArray("gatuser");
						for (int i = 0; i < zanArray.length(); i++) {
							JSONObject jsonZan = zanArray.getJSONObject(i);
							ZanBean zanBean = new ZanBean();
							if (jsonZan.has("id"))
								zanBean.setZanUserID(jsonZan.getString("id"));
							if (jsonZan.has("name"))
								zanBean.setZanUserName(jsonZan
										.getString("name"));
							if (jsonZan.has("photo"))
								zanBean.setZanUserHead(jsonZan
										.getString("photo"));
							if (jsonZan.has("brow"))
								zanBean.setBrow(jsonZan.getInt("brow"));
							zanList.add(zanBean);
						}
						chatBean.setZanList(zanList);
					}

					searchList.add(chatBean);

				}
				ArrayList<ChatListBean> searchListDao = new ArrayList<ChatListBean>(
						10);
				for (int j = searchList.size() - 1; j >= 0; j--) {
					searchListDao.add(searchList.get(j));
				}

				serviceListener.getJOSNdataSuccess(searchListDao, retcode,
						Contanst.GETHISTORYMESSAGE);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.GETHISTORYMESSAGE);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常",
					Contanst.GETHISTORYMESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
