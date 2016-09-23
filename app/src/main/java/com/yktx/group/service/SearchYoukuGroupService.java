package com.yktx.group.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.bean.GroupListBean;
import com.yktx.group.conn.HttpPostListener;

/**
 * 
 * @项目名称：Group
 * @类名称：SearchYoukuGroupService
 * @类描述：优酷搜索接口
 * @创建人：chenyongxian
 * @创建时间：2014年10月30日上午11:43:21
 * @修改人：
 * @修改时间：2014年10月30日上午11:43:21
 * @修改备注：
 * @version
 *
 */
public class SearchYoukuGroupService{
	
	private static int time = 0;
	static ArrayList<GroupListBean> searchList = new ArrayList<GroupListBean>(
			10);
	public static ArrayList<GroupListBean> doHttpPostJSON(List<NameValuePair> param,HttpPostListener httpPostListener) {
		HttpPost post = null;
		String uri = "http://tip.soku.com/search_keys";
		HttpResponse httpresponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		post = new HttpPost(uri);
		try {
			post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
			httpresponse = httpclient.execute(post);
			String strResult = EntityUtils.toString(httpresponse.getEntity());
			strResult = strResult.substring(17, strResult.length() - 1);
			JSONObject result = new JSONObject(strResult);
			if(result == null || result.length() <= 0) return null;
			if (result != null) {// 成功获取数据
				if(searchList != null && searchList.size() > 0) searchList.clear();
				
				String searchName = result.getString("q");
				String searchCode = result.getString("vs");
				Log.i(  "aaa", "searchName = " + searchName);
				JSONArray searchArray = result.getJSONArray("r");
				Log.i(  "aaa", "searchArray = " + searchArray);
				for (int j = 0; j < searchArray.length(); j++) {
					JSONObject jsonSearch = searchArray.getJSONObject(j);
					GroupListBean searchBean = new GroupListBean();
					if (jsonSearch.has("c"))
						searchBean.setGroupName(jsonSearch.getString("c"));
					searchList.add(searchBean);
				}
			}
		}catch (SocketTimeoutException e) {
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(param, httpPostListener);
		} catch (ParseException e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			if (++time >= 2)
				httpPostListener.connFail(e.toString());
			else
				doHttpPostJSON(param, httpPostListener);
		} catch (Exception e) {
			e.printStackTrace();
			httpPostListener.connFail(e.toString());
		}
		
		return searchList;
	}
}