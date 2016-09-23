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
 * @author Administrator 发布新的合体
 * 
 */
public class SearchGroupService extends Service {

	public SearchGroupService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_SEARCHGROUP;
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.SEARCHGROUP);
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

			// HomeBestView.listCount =
			// Integer.parseInt((String)result.get("tot_count"));
			Log.i(  "aaa", "retcode = " + retcode);
			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据
				JSONObject groupPager = result.getJSONObject("groupPager");

				JSONArray searchArray = groupPager.getJSONArray("listData");
				ArrayList<GroupListBean> searchList = new ArrayList<GroupListBean>(
						10);
				for (int j = 0; j < searchArray.length(); j++) {
					JSONObject jsonSearch = searchArray.getJSONObject(j);
					GroupListBean searchBean = new GroupListBean();
					if (jsonSearch.has("id"))
						searchBean.setGroupID(jsonSearch.getString("id"));
					if (jsonSearch.has("name"))
						searchBean.setGroupName(jsonSearch.getString("name"));
					if (jsonSearch.has("distance"))
						searchBean
								.setDistance(jsonSearch.getString("distance"));
					if (jsonSearch.has("groupManCount"))
						searchBean.setGroupPeopleCount(jsonSearch
								.getString("groupManCount"));
					// if (jsonSearch.has("photo"))
					// searchBean.setGroupPhoto(jsonSearch.getString("photo"));
					searchList.add(searchBean);
				}
				Log.i(  "aaa",
						"searchListsearchListsearchList === "
								+ searchList.size());
				serviceListener.getJOSNdataSuccess(searchList, retcode,
						Contanst.SEARCHGROUP);

			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.SEARCHGROUP);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.SEARCHGROUP);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
