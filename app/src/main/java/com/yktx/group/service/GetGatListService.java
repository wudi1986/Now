package com.yktx.group.service;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import com.yktx.bean.ChatListBean;
import com.yktx.bean.ZanBean;
import com.yktx.group.GroupApplication;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;
import com.yktx.util.Geohash;

/**
 * @author Administrator 上传注册信息
 * 
 */
public class GetGatListService extends Service {

	String urlParams;
	
	public GetGatListService(String requestType, Hashtable<String, String> params, String urlParams, ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_GETGATLIST;
		this.urlParams = urlParams;
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETGATLIST);
		Log.i(  "aaa", "httpFailhttpFail");
		// LodingActivity.isJion = false;
	}

	@Override
	void httpSuccess(String reponse) {
		// LodingActivity.isJion = false;
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.getString("statusCode");

			// reponse =
			// {"statusCode":"1","mess":"验证验证码:验证成功","data":{"pushallas":"b1e00bfbb53d4ea58e268683035b589d"}}
			
			
			JSONObject jsonChat = result.getJSONObject("msgs");

			Log.i(  "aaa", "retcode = " + retcode);
			Log.i(  "bbb", "getJOSNdataSuccessgetJOSNdataSuccess");
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据
				// serviceListener.getJOSNdataSuccess("ok", retcode ,
				// Contanst.REGISTER);

				ChatListBean chatBean = new ChatListBean();
				
				if (jsonChat.has("name"))
					chatBean.setUserName(jsonChat.getString("name"));
				String latitude = "-1", longitude = "-1";
				if (jsonChat.has("longitude"))
					longitude = jsonChat.getString("longitude");
				if (jsonChat.has("latitude"))
					latitude = jsonChat.getString("latitude");

				SharedPreferences settings = GroupApplication.getInstance().getSharedPreferences(Contanst.SystemStone, 0);
				String curlongitude = settings.getString("longitude", "-1");
				String curlatitude = settings.getString("latitude", "-1");
				

				String distance = null;
				try {
					distance = Geohash.GetDistance(Double.parseDouble(curlatitude), Double.parseDouble(curlongitude),
							Double.parseDouble(latitude), Double.parseDouble(longitude));
				} catch (Exception e) {
					distance = "火星";
				}
				

				chatBean.setDistance(distance);

				if (jsonChat.has("user_id"))
					chatBean.setUserId(jsonChat.getString("user_id"));
				if (jsonChat.has("photo"))
					chatBean.setUserHead(jsonChat.getString("photo"));
				if (jsonChat.has("content")) {
					chatBean.setContent(jsonChat.getString("content"));
				}
				if (jsonChat.has("send_time"))
					chatBean.setSendTime(jsonChat.getLong("send_time"));
				if (jsonChat.has("message_id"))
					chatBean.setPhotoChatID(jsonChat.getString("message_id"));
				
				
				ArrayList<ZanBean> zanList = new ArrayList<ZanBean>(10);
				JSONArray zanArray = result.getJSONArray("gats");
				for (int i = 0; i < zanArray.length(); i++) {
					JSONObject jsonZan = zanArray.getJSONObject(i);
					ZanBean zanBean = new ZanBean();
					if (jsonZan.has("id"))
						zanBean.setZanUserID(jsonZan.getString("id"));
					if (jsonZan.has("name"))
						zanBean.setZanUserName(jsonZan.getString("name"));
					if (jsonZan.has("photo"))
						zanBean.setZanUserHead(jsonZan.getString("photo"));
					if (jsonZan.has("brow")){
						zanBean.setBrow(jsonZan.getInt("brow"));
						}
					zanList.add(zanBean);
				}
				
				
//				for (int i = 0; i < 20; i++) {
//					JSONObject jsonZan = zanArray.getJSONObject(0);
//					ZanBean zanBean = new ZanBean();
//					if (jsonZan.has("id"))
//						zanBean.setZanUserID(jsonZan.getString("id"));
//					if (jsonZan.has("name"))
//						zanBean.setZanUserName(jsonZan.getString("name"));
//					if (jsonZan.has("photo"))
//						zanBean.setZanUserHead(jsonZan.getString("photo"));
//					zanList.add(zanBean);
//				}
				
				
				
				chatBean.setZanList(zanList);

				// Log.i(  "aaa", "searchList ============ " + searchList.size());

				serviceListener.getJOSNdataSuccess(chatBean, urlParams, Contanst.GETGATLIST);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETGATLIST);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETGATLIST);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
