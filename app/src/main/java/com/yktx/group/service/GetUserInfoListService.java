package com.yktx.group.service;

import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.bean.UserInfoBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator 根据id获取用户头像名字数组
 *
 */
public class GetUserInfoListService extends Service{

	public GetUserInfoListService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_GETUSERINFOLIST
				;
		Log.i(  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETUSERINFOLIST);
		Log.i(  "aaa", "httpFailhttpFail");
//		LodingActivity.isJion = false;
	}
	@Override
	void httpSuccess(String reponse) {
//		LodingActivity.isJion = false;
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.getString("statusCode");
			
//			reponse = {"dataList":[{"id":2370,"name":"艾尔沙维","photo":"/image/headphoto/83d59c08-d036-434d-b694-2e2ac3108d39.jpg"},
//			{"id":11959,"name":"客服妹纸","photo":"/image/headphoto/935a5003-86c6-4b1b-995b-8bde11a4b648.jpg"}],"statusCode":200}
			
			Log.i(  "aaa", "retcode = "+retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
				HashMap<String, UserInfoBean> map = new HashMap<String, UserInfoBean>(10);
				JSONArray array = result.getJSONArray("dataList");
				for(int i = 0; i < array.length(); i++){
					JSONObject item = array.getJSONObject(i);
					UserInfoBean userInfoBean = new UserInfoBean();
					String id = "";
					if (item.has("id")){
						id = item.getString("id");
						userInfoBean.setId(id);
					}
					if(item.has("name"))
						userInfoBean.setName(item.getString("name"));
					if(item.has("photo"))
						userInfoBean.setPhoto(item.getString("photo"));
					map.put(id, userInfoBean);
				}
				
				serviceListener.getJOSNdataSuccess(map, retcode , Contanst.GETUSERINFOLIST);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETUSERINFOLIST);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETUSERINFOLIST);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
