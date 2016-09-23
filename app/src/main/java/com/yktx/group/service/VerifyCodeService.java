package com.yktx.group.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * @author Administrator 验证验证码
 * 
 */
public class VerifyCodeService extends Service {

	public static String pushalias = "";

	public VerifyCodeService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_VERIFYCODE + ".html";
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.VERIFYCODE);
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
			String retcode = result.get("statusCode").toString();

			// reponse =
			// {"data":{"pushalias":"b0c61e693afd494fbdcb80d5d137a1b1"},"statusCode":"1","mess":"验证验证码:验证成功"}


			Log.i(  "aaa", "retcode = " + retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据
				
				JSONObject data = (JSONObject) result.get("data");
				pushalias = data.getString("pushalias");
				Log.i(  "bbb", "pushallas === " + pushalias);
				serviceListener.getJOSNdataSuccess("ok", retcode,
						Contanst.VERIFYCODE);
			} else if ("111".equals(retcode)) {
				serviceListener.getJOSNdataSuccess("ok", retcode,
						Contanst.VERIFYCODE);
				// JSONObject data = (JSONObject) result.get("data");
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.VERIFYCODE);
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
