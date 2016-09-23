package com.yktx.group.service;

import java.lang.reflect.Type;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yktx.bean.GroupMemberListBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator 进群
 *
 */
public class GetGroupMembersService extends Service{

	public GetGroupMembersService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_GETGROUPMEMBERS;
		Log.i(  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETGROUPMEMBERS);
		Log.i(  "aaa", "httpFailhttpFail");
	}
	@Override
	void httpSuccess(String reponse) {
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode =  result.getString("statusCode");
			
			Log.i(  "aaa", "retcode = "+retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {//成功获取数据
				/**
				 * 获取群成员信息
				 */
				String pager = result.getString("groupPager");
				Gson gson = new Gson();
				// 泛型
				Type objectType = new TypeToken<MainHomePageBean<GroupMemberListBean>>() {
				}.getType();
				MainHomePageBean<GroupMemberListBean> mainHomePageBean = gson
						.fromJson(pager, objectType);
				serviceListener.getJOSNdataSuccess(mainHomePageBean, retcode , Contanst.GETGROUPMEMBERS);
				
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.GETGROUPMEMBERS);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.GETGROUPMEMBERS);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
