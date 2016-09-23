package com.yktx.group.service;

import java.lang.reflect.Type;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yktx.bean.AttentionMainBean;
import com.yktx.bean.HotMainBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.bean.NearMainBean;
import com.yktx.bean.NewMainBean;
import com.yktx.bean.RecommendMainBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.fragment.GroupMainFragmentActivity;
import com.yktx.util.Contanst;

/**
 * @author Administrator 首页数据
 * 
 */
public class HomePageService extends Service {

	String urlParams;

	public HomePageService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_HOMEPAGE;
		this.urlParams = urlParams;
		Log.i(  "aaa", "url ======= " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.HOMEPAGE);
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

				if (urlParams.equals(GroupMainFragmentActivity.MAIN_NEW_HTTP)) {
					String pager = result.getString("pager");
					Gson gson = new Gson();
					// 泛型
					Type objectType = new TypeToken<MainHomePageBean<NewMainBean>>() {
					}.getType();
					MainHomePageBean<NewMainBean> mainHomePageBean = gson
							.fromJson(pager, objectType);

					serviceListener.getJOSNdataSuccess(mainHomePageBean,
							retcode, Contanst.HOMEPAGE);
				} else if (urlParams
						.equals(GroupMainFragmentActivity.MAIN_NEAR_HTTP)) {
					String pager = result.getString("pager");
					Gson gson = new Gson();
					// 泛型
					Type objectType = new TypeToken<MainHomePageBean<NearMainBean>>() {
					}.getType();
					MainHomePageBean<NewMainBean> mainHomePageBean = gson
							.fromJson(pager, objectType);

					serviceListener.getJOSNdataSuccess(mainHomePageBean,
							retcode, Contanst.HOMEPAGE);
				} else if (urlParams
						.equals(GroupMainFragmentActivity.MAIN_ATTENTION_HTTP)) {
					String pager = result.getString("pager");
					Gson gson = new Gson();
					// 泛型
					Type objectType = new TypeToken<MainHomePageBean<AttentionMainBean>>() {
					}.getType();
					MainHomePageBean<AttentionMainBean> mainHomePageBean = gson
							.fromJson(pager, objectType);

					serviceListener.getJOSNdataSuccess(mainHomePageBean,
							retcode, Contanst.HOMEPAGE);
				} else if (urlParams
						.equals(GroupMainFragmentActivity.MAIN_HOT_HTTP)) {
					String pager = result.getString("pager");
					Gson gson = new Gson();
					// 泛型
					Type objectType = new TypeToken<MainHomePageBean<HotMainBean>>() {
					}.getType();
					MainHomePageBean<HotMainBean> mainHomePageBean = gson
							.fromJson(pager, objectType);

					serviceListener.getJOSNdataSuccess(mainHomePageBean,
							GroupMainFragmentActivity.MAIN_HOT_HTTP,
							Contanst.HOMEPAGE);
				} else if (urlParams
						.equals(GroupMainFragmentActivity.MAIN_RECOMMEND_HTTP)) {
					String pager = result.getString("pager");
					Gson gson = new Gson();
					// 泛型
					Type objectType = new TypeToken<MainHomePageBean<RecommendMainBean>>() {
					}.getType();
					MainHomePageBean<RecommendMainBean> mainHomePageBean = gson
							.fromJson(pager, objectType);

					serviceListener.getJOSNdataSuccess(mainHomePageBean,
							GroupMainFragmentActivity.MAIN_RECOMMEND_HTTP,
							Contanst.HOMEPAGE);
				}

			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.HOMEPAGE);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.HOMEPAGE);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
