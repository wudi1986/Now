package com.yktx.group.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.yktx.group.conn.HttpConnectinWrapper;
import com.yktx.group.conn.HttpPostListener;
import com.yktx.group.conn.ServiceListener;
import com.yktx.util.Contanst;


/**
 * 
 * @author wudi
 * @version 2011-02-25
 */
public abstract class Service {

	protected String url = "";
	static Service service = null;
	List<NameValuePair> listRequest = new ArrayList<NameValuePair>();
	ServiceListener serviceListener = null;
	
	
	Map<String, String> params = new HashMap<String, String>();
	Map<String, File> files = new HashMap<String, File>();
	
	public Service(String requestType, Hashtable<String, String> params,
			String urlParams, ServiceListener serviceListener) {
		// ����post�������
		this.addHashtable(params);
		// ����������������
		this.serviceListener = serviceListener;
	}

	public static Service getService(String requestType,
			Hashtable<String, String> params, String urlParams,
			ServiceListener serviceListener) {
		if (Contanst.HTTP_GETGROUPMEMBERS.equals(requestType)){
			service = new GetGroupMembersService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_REGISTER_VERIFCATION.equals(requestType)){
			service = new RegisterVerificationService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_UPLOADMESSAGEARM.equals(requestType)){
			service = new UploadMessageArmService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_UPDATEUSERINFO.equals(requestType)){
			service = new UpdateUserInfoService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_SENDVERIFY.equals(requestType)){
			service = new SendVerifyService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_VERIFYCODE.equals(requestType)){
			service = new VerifyCodeService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_UPLOADCONTACT.equals(requestType)){
			service = new UploadContactService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_SEARCHGROUP.equals(requestType)){
			service = new SearchGroupService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_NEARORHOTGROUP.equals(requestType)){
			service = new NearorHotGroupService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_GETHISTORYMESSAGE.equals(requestType)){
			service = new GetHistoryMessageService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_HOMEPAGE.equals(requestType)){
			service = new HomePageService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_INTOGROUP.equals(requestType)){
			service = new IntoGroupService(requestType, params, urlParams,
					serviceListener);	
		} else if (Contanst.HTTP_UPLOADMESSAGEIMG.equals(requestType)){
			service = new UploadMessageImgService(requestType, params, urlParams,
					serviceListener);	
		} else if (Contanst.HTTP_GETGATLIST.equals(requestType)){
			service = new GetGatListService(requestType, params, urlParams,
					serviceListener);	
		} else if (Contanst.HTTP_GETHISTORYGROUPS.equals(requestType)){
			service = new GetHistoryGroupsService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_GETUSERINFO.equals(requestType)){
			service = new GetUserInfoService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_GETUSERINFOLIST.equals(requestType)){
			service = new GetUserInfoListService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_GETATTENTIONLIST.equals(requestType)){
			service = new GetAttentionListService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_ATTENTION.equals(requestType)){
			service = new AttentionService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_JPUSHGROUP.equals(requestType)){
			service = new JPushGroupService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_REGISTER.equals(requestType)){
			service = new RegisterService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_APPLOGIN.equals(requestType)){
			service = new AppLoginService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_UPDATEPASSWORD.equals(requestType)){
			service = new UpdatePasswordService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_FORGETPASSWORD.equals(requestType)){
			service = new ForgetPasswordService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_VALIDATEUSERNAME.equals(requestType)){
			service = new ValidateUserNameService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_ADDERRORLOG.equals(requestType)){
			service = new AddErrorLogService(requestType, params, urlParams,
					serviceListener);

		} else if (Contanst.HTTP_BAIDU_LOCATION.equals(requestType)){
			service = new BaiduLocationService(requestType, params, urlParams,
					serviceListener);
		} else if (Contanst.HTTP_HOTSEARCHGROUP.equals(requestType)){
			service = new HotSearchGroupService(requestType, params, urlParams,
					serviceListener);
			
//		} else if (Contanst.HTTP_PATSBYID.equals(requestType)){
//			service = new PatsByIDService(requestType, params, urlParams,
//					serviceListener);
		} 
		
		return service;

	}
	
//	// add by justin
//	public static Service getService(String requestType,
//			Hashtable<String, String> params, String urlParams,
//			ServiceListener serviceListener, String _type) {
//		if ("sns".equals(requestType)) {
//			service = new SendVerifyService(requestType, params, urlParams,
//					serviceListener);
//		}
//		
//		return service;
//	}

	// public Service addJSONObject(JSONObject jsonRequest) {
	// this.listRequest = jsonRequest;
	// return service;
	// }
	/**
	 * �ⲿֱ�������Ҫ�ύ��JSON���
	 * 
	 * @param jsonRequest
	 *            JSON�������
	 * @return ��ǰ����ӿ�
	 */
	public Service addList(List<NameValuePair> params) {
		this.listRequest = params;
		return service;
	}
	
	
	public Service addPart(Map<String, String> params, Map<String, File> files){
		this.params = params;
		this.files = files;
		return service;
	}
	/**
	 * ��Ӽ������
	 * 
	 * @param params
	 * @return
	 */
	List<NameValuePair> addHashtable(Hashtable<String, String> params) {
		if (params == null)
			return listRequest;
		Enumeration<String> perpertyKeys = params.keys();
		while (perpertyKeys.hasMoreElements()) {
			String key = (String) perpertyKeys.nextElement();
			try {
				listRequest.add(new BasicNameValuePair(key, (String) params
						.get(key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listRequest;
	}

	/**
	 * ��ӵ������
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	List<NameValuePair> addParam(String key, String value) {
		try {
			listRequest.add(new BasicNameValuePair(key, value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listRequest;
	}

	/** ���������,����ص�����״̬������� */
	protected HttpPostListener httpPostListener = new HttpPostListener() {

		public void connFail(String erro) {
			Log.i(  "aaa", "connFail");
			Log.i(  "aaa", "erro = " + erro);
			httpFail(erro);
		}

		public void connSuccess(String reponse) {
			Log.i(  "aaa", "connSuccess");
			Log.i(  "aaa", "reponse = " + reponse);
			httpSuccess(reponse);
		}
	};

	/**
	 * ������������ʼ�������
	 * 
	 * @param httpMethod
	 *            http����ʽ��GET/POST/PUT/DELETE��
	 */
	public void request(String httpMethod) {
		Log.i(  "aaa", "httpMethod = " + httpMethod);
		new HttpConnectinWrapper().request(httpMethod, url, listRequest,
				params,files,httpPostListener);
	}

	
	/**
	 * ����ɹ�
	 * 
	 * @param reponse
	 *            ����ɹ����ص��ַ������
	 */
	abstract void httpSuccess(String reponse);

	/**
	 * ����ʧ��
	 * 
	 * @param erro
	 *            ����ʧ�ܷ��صĴ�����Ϣ
	 */
	abstract void httpFail(String erro);

	/**
	 * ������ݣ���ΪĿǰJSON��ʽ�򵥣�û���ڴ˷����н�����ݣ�</br> ֱ����
	 * <code>httpSuccess(String reponse)</code>����ɣ�.
	 * 
	 * @param reponse
	 *            ����ɹ����ص��ַ������
	 */
	abstract void parse(String reponse);

	protected String erroCodeParse(String erroCode) {
		return erroCode;
	}

}
