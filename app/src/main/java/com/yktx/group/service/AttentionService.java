package com.yktx.group.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator 获取验证码
 *
 */
public class AttentionService extends Service{

	
	String urlParams;
	public AttentionService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_ATTENTION
				+".html";
		Log.i(  "aaa", "url ===== "+url);
		this.urlParams = urlParams;
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail(urlParams, "网络异常", Contanst.ATTENTION);
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
			String retcode =  result.get("statusCode").toString();
			
//			HomeBestView.listCount = Integer.parseInt((String)result.get("tot_count")); 
			Log.i(  "aaa", "retcode = "+retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
				serviceListener.getJOSNdataSuccess(urlParams, retcode , Contanst.ATTENTION);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(urlParams, errmsg, Contanst.ATTENTION);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail(urlParams, "服务器异常", Contanst.ATTENTION);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
