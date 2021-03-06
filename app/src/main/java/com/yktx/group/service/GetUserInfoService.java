package com.yktx.group.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.yktx.bean.UserBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator 上传注册信息
 *
 */
public class GetUserInfoService extends Service{

	public GetUserInfoService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_GETUSERINFO
				;
		Log.i(  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETUSERINFO);
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
			
//	 		reponse = {"statusCode":"1","mess":"验证验证码:验证成功","data":{"pushallas":"b1e00bfbb53d4ea58e268683035b589d"}}

			Log.i(  "aaa", "retcode = "+retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
				String userStr = result.getString("user");
				Gson gson = new Gson();
				UserBean userBean = gson
						.fromJson(userStr, UserBean.class);
				serviceListener.getJOSNdataSuccess(userBean, retcode , Contanst.GETUSERINFO);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETUSERINFO);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETUSERINFO);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
