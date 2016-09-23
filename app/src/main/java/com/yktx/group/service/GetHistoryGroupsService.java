package com.yktx.group.service;

import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.bean.GroupListBean;
import com.yktx.bean.HistoryMemberBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * 
 * @项目名称：Group
 * @类名称：GetHistoryGroupsService.java
 * @类描述：获取历史搜索群最新信息
 * @创建人：chenyongxian
 * @创建时间：2014年10月30日下午4:12:34
 * @修改人：
 * @修改时间：2014年10月30日下午4:12:34
 * @修改备注：
 * @version
 * 
 */
public class GetHistoryGroupsService extends Service {

	public GetHistoryGroupsService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_GETHISTORYGROUPS;
		Log.i(  "aaa", "url ===== " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.GETHISTORYGROUPS);
		Log.i(  "aaa", "httpFailhttpFail");
	}

	@Override
	void httpSuccess(String reponse) {
		try {
			// ======================根据协议本地测试数据================================
			// reponse =
			// ========================联调接口时注释就可以============================
			JSONObject result = new JSONObject(reponse);
			String retcode = result.getString("statusCode");
			Log.i(  "aaa", "retcode = " + retcode);

			if (Contanst.HTTP_SUCCESS.equals(retcode)) {// 成功获取数据

				JSONArray searchArray = result.getJSONArray("dataList");
				ArrayList<GroupListBean> groupHistoryList = new ArrayList<GroupListBean>(
						10);
				for (int j = 0; j < searchArray.length(); j++) {
					JSONObject jsonChat = searchArray.getJSONObject(j);
					GroupListBean groupHistoryBean = new GroupListBean();

					if (jsonChat.has("id"))
						groupHistoryBean.setGroupID(jsonChat.getString("id"));
					if (jsonChat.has("name"))
						groupHistoryBean.setGroupName(jsonChat
								.getString("name"));
					if (jsonChat.has("groupManCount"))
						groupHistoryBean.setGroupPeopleCount(jsonChat
								.getString("groupManCount"));
					if (jsonChat.has("offTime"))
						groupHistoryBean.setGroupTime(jsonChat
								.getLong("offTime"));
					if (jsonChat.has("photo"))
						groupHistoryBean.setGroupPhoto(jsonChat
								.getString("photo"));
					if (jsonChat.has("msgnum"))
						groupHistoryBean.setAtNum(jsonChat.getString("msgnum"));
					if (jsonChat.has("distance"))
						groupHistoryBean.setDistance(jsonChat
								.getString("distance"));
					if (jsonChat.has("user_name"))
						groupHistoryBean.setUser_name(jsonChat
								.getString("user_name"));

					if (jsonChat.has("members")) {
						JSONArray array = jsonChat.getJSONArray("members");
						ArrayList<HistoryMemberBean> menberList = new ArrayList<HistoryMemberBean>(
								4);
						for (int i = 0; i < array.length(); i++) {
							HistoryMemberBean historyMemberBean = new HistoryMemberBean();
							JSONObject jsonMember = array.getJSONObject(i);
							if (jsonMember.has("id"))
								historyMemberBean.setId(jsonMember
										.getString("id"));
							if (jsonMember.has("photo"))
								historyMemberBean.setPhoto(jsonMember
										.getString("photo"));
							menberList.add(historyMemberBean);
						}
						groupHistoryBean.setMembers(menberList);
					}

					groupHistoryList.add(groupHistoryBean);
				}
				serviceListener.getJOSNdataSuccess(groupHistoryList, retcode,
						Contanst.GETHISTORYGROUPS);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.GETHISTORYGROUPS);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常",
					Contanst.GETHISTORYGROUPS);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
