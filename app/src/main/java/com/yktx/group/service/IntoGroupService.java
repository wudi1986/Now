package com.yktx.group.service;

import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * @author Administrator 进群
 * 
 */
public class IntoGroupService extends Service {

	public IntoGroupService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_INTOGROUP;
		Log.i(  "aaa", "url ======= " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.INTOGROUP);
		Log.i(  "aaa", "httpFailhttpFail");
		// LodingActivity.isJion = false;
	}

	@Override
	void httpSuccess(String reponse) {

		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.getString("statusCode").toString();
			Log.i(  "aaa", "retcode = " + retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据
				String first = result.getString("first");
				String group_id = result.getString("group_id");
				
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("isFirst", first);
				if(group_id != null && group_id.length() > 0){
					map.put("group_id", group_id);
				}
				
				serviceListener.getJOSNdataSuccess(map, retcode,
						Contanst.INTOGROUP);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.INTOGROUP);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.INTOGROUP);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
