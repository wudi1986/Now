package com.yktx.group.service;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.bean.GroupListBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;


/**
 * @author Administrator hot/near groupList
 *
 */
public class HotSearchGroupService extends Service{

	public HotSearchGroupService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url=UrlParams.IP+Contanst.HTTP_HOTSEARCHGROUP;
		Log.i(  "aaa", "url ===== "+url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.HOTSEARCHGROUP);
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
//	{"statusCode":200,"listData":[{"group_name":"斡","online_num":0,"group_id":113532},
//			{"group_name":"男朋友镜头中的我","online_num":0,"group_id":113531},
//			{"group_name":"清明扫墓，约起","online_num":0,"group_id":113530},
//			{"group_name":"明明是谁","online_num":0,"group_id":113529},
//			{"group_name":"我","online_num":0,"group_id":113528}]}

			// HomeBestView.listCount =
			// Integer.parseInt((String)result.get("tot_count"));
			Log.i(  "aaa", "retcode = " + retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据

//				Log.i(  "aaa", "groupPager === " + groupPager);
				JSONArray searchArray = result.getJSONArray("listData");
				ArrayList<GroupListBean> searchList = new ArrayList<GroupListBean>(
						10);
				for (int j = 0; j < searchArray.length(); j++) {
					JSONObject jsonSearch = searchArray.getJSONObject(j);
					GroupListBean searchBean = new GroupListBean();
					if (jsonSearch.has("group_id"))
						searchBean.setGroupID(jsonSearch.getString("group_id"));
					if (jsonSearch.has("group_name"))
						searchBean.setGroupName(jsonSearch.getString("group_name"));
//					if (jsonSearch.has("online_num"))
//						searchBean
//								.setDistance(jsonSearch.getString("online_num"));
					searchList.add(searchBean);
				}
				Log.i(  "aaa",
						"searchListsearchListsearchList === "
								+ searchList.size());
				serviceListener.getJOSNdataSuccess(searchList, retcode,
						Contanst.HOTSEARCHGROUP);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg, Contanst.HOTSEARCHGROUP);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.HOTSEARCHGROUP);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub
		
	}

}
