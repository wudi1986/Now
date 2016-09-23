package com.yktx.group.service;

import java.lang.reflect.Type;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yktx.bean.GetAttentionListBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator 上传注册信息
 *
 */
public class GetAttentionListService extends Service{

	public GetAttentionListService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_GETATTENTIONLIST
				;
		Log.i(  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETATTENTIONLIST);
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
				
				
				String pager = result.getString("pager");
				Log.i(  "aaa", "pager = " + pager);
				Gson gson = new Gson();
				// 泛型
				Type objectType = new TypeToken<MainHomePageBean<GetAttentionListBean>>() {
				}.getType();
				MainHomePageBean<GetAttentionListBean> mainHomePageBean = gson
						.fromJson(pager, objectType);
				serviceListener.getJOSNdataSuccess(mainHomePageBean, retcode , Contanst.GETATTENTIONLIST);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETATTENTIONLIST);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETATTENTIONLIST);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
