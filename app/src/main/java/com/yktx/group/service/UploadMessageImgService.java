package com.yktx.group.service;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * 
 * @项目名称：Group
 * @类名称：UploadMessageImgService.java
 * @类描述：聊天图片上传
 * @创建人：chenyongxian
 * @创建时间：2014年10月31日下午2:20:50
 * @修改人：
 * @修改时间：2014年10月31日下午2:20:50
 * @修改备注：
 * @version
 *
 */
public class UploadMessageImgService extends Service {
	String urlParams;
	public UploadMessageImgService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		super(requestType, params, urlParams, serviceListener);
		this.url = UrlParams.IP + Contanst.HTTP_UPLOADMESSAGEIMG;
		this.urlParams = urlParams;
		Log.i(  "aaa", "url ======= " + url);
	}

	@Override
	void httpFail(String erro) {
		serviceListener.getJOSNdataFail("", "网络异常", Contanst.UPLOADMESSAGEIMG);
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
//			{"statusCode":200,"imgname":"/image/messagephoto/07e0ca81-3093-403a-b040-60c71b34de0f.jpg"}

				String imgname = result.getString("imgname");
				
				serviceListener.getJOSNdataSuccess(imgname+","+urlParams, retcode,
						Contanst.UPLOADMESSAGEIMG);
			} else {
				String errmsg = (String) result.get("mess");
				serviceListener.getJOSNdataFail(erroCodeParse(retcode), errmsg,
						Contanst.UPLOADMESSAGEIMG);
			}
		} catch (JSONException e) {
			serviceListener.getJOSNdataFail("", "服务器异常", Contanst.UPLOADMESSAGEIMG);
			e.printStackTrace();
		}
	}

	@Override
	void parse(String reponse) {
		// TODO Auto-generated method stub

	}

}
