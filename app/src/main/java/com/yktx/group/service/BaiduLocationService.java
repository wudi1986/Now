package com.yktx.group.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.group.conn.ServiceListener;
import com.yktx.util.Contanst;

/**
 * @author Administrator 验证验证码
 * 
 */
public class BaiduLocationService extends Service {

	public static String pushalias = "";

	public BaiduLocationService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = Contanst.HTTP_BAIDU_LOCATION + urlParams;
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.BAIDU_LOCATION);
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
			String retcode = (String) result.get("status");

			// reponse =
			// {"data":{"pushalias":"b0c61e693afd494fbdcb80d5d137a1b1"},"statusCode":"1","mess":"验证验证码:验证成功"}


			Log.i(  "aaa", "retcode = " + retcode);
			if ("OK".equals(retcode)) {// 成功获取数据
				
				JSONObject data = (JSONObject) result.get("result");
				JSONObject data2 = (JSONObject) data.get("addressComponent");
				String province = data2.getString("province");
				String district = data2.getString("district");
				
				Log.i(  "bbb", "province === " + province+district);
				serviceListener.getJOSNdataSuccess(province+district, retcode,
						Contanst.BAIDU_LOCATION);
			} else if ("111".equals(retcode)) {
				serviceListener.getJOSNdataSuccess("ok", retcode,
						Contanst.BAIDU_LOCATION);
				// JSONObject data = (JSONObject) result.get("data");
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.BAIDU_LOCATION);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.VERIFYCODE);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
