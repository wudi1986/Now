/**
 * 
 */
package com.yktx.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
import com.yktx.bean.UserBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.fragment.GroupMainFragmentActivity;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.MyUMSDK;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年5月21日 下午6:56:13  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class LoginChoiceActivity extends BaseActivity implements
		OnClickListener,ServiceListener {

	TextView registerBotton, loginOld;
	RelativeLayout loginSina;
	LinearLayout xieyiLayout;

	// =========授权登录============
	MyUMSDK msShareSDK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_choice_activity);
		registerBotton = (TextView) findViewById(R.id.registerBotton);
		loginOld = (TextView) findViewById(R.id.loginOld);
		loginSina = (RelativeLayout) findViewById(R.id.loginSina);
		xieyiLayout = (LinearLayout) findViewById(R.id.xieyiLayout);
		registerBotton.setOnClickListener(this);
		loginOld.setOnClickListener(this);
		loginSina.setOnClickListener(this);
		xieyiLayout.setOnClickListener(this);
		msShareSDK = new MyUMSDK(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {

			case R.id.registerBotton:
				Intent register = new Intent(LoginChoiceActivity.this,
						RegisterActivity.class);
				LoginChoiceActivity.this.startActivityForResult(register, 111);
				break;
				
			case R.id.loginSina:
				login(SHARE_MEDIA.SINA);
				break;
				
			case R.id.loginOld:
				Intent oldIntent = new Intent(LoginChoiceActivity.this,
						LoginActivity.class);
				LoginChoiceActivity.this.startActivityForResult(oldIntent, 111);
				break;
			
			case R.id.xieyiLayout:
				Intent in = new Intent(LoginChoiceActivity.this,
						XieYiActivity.class);
				LoginChoiceActivity.this.startActivity(in);
				break;

		}

	}

	private String ssoUserID;
	boolean isConn;

	/**
	 * 授权。如果授权成功，则获取用户信息</br>
	 */
	private void login(final SHARE_MEDIA platform) {
		msShareSDK.mController.doOauthVerify(this, platform,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA platform) {
					}

					@Override
					public void onError(SocializeException e,
							SHARE_MEDIA platform) {
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						ssoUserID = value.getString("uid");
						if (!TextUtils.isEmpty(ssoUserID)) {
							getUserInfo(platform);
						} else {
							Toast.makeText(LoginChoiceActivity.this, "授权失败...",
									Toast.LENGTH_SHORT).show();
							isConn = false;
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {

					}
				});
	}

	/**
	 * 获取授权平台的用户信息</br>
	 */
	private void getUserInfo(SHARE_MEDIA platform) {
		msShareSDK.mController.getPlatformInfo(LoginChoiceActivity.this,
				platform, new UMDataListener() {

					@Override
					public void onStart() {
						
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						showProgressDialog("授权成功，正在加载数据。请稍后...");
						if (info != null) {
							 conn(info);
						}
					}
				});
	}
	
	boolean isLoginFail;
	public void loginEasemob(final String userName , final String userNickName){
		
		EMChatManager.getInstance().login(userName,"123",new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						
						EMChatManager.getInstance().updateCurrentUserNick(userNickName);
						
						EMChatManager.getInstance().loadAllConversations();
						Log.i(  "bbb", "登陆聊天服务器成功！");	
						Intent in = new Intent(LoginChoiceActivity.this, GroupMainFragmentActivity.class);
						LoginChoiceActivity.this.startActivity(in);
						LoginChoiceActivity.this.finish(); 
						overridePendingTransition(R.anim.activity_come_in,
								R.anim.activity_come_out_bottom);
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
				
			}

			@Override
			public void onError(int code, String message) {
				Log.i(  "bbb", "登陆聊天服务器失败！");
				isLoginFail = true;
				registerEasemob(userName, userNickName);
			}
		});
	}
	
	
	public void registerEasemob(final String userName, final String userNickName) {
		new Thread(new Runnable() {
	
	    public void run() {
	        try {
	           // 调用sdk注册方法
	           EMChatManager.getInstance().createAccountOnServer(userName, "123");
	           if(isLoginFail){
	        	   loginEasemob(userName, userNickName);
	           }
	        } catch (final EaseMobException e) {
	        //注册失败
		  		String errorMsg = e.getMessage();
		  		int errorCode=e.getErrorCode();
		  		if(errorCode==EMError.NONETWORK_ERROR){
		  		    Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
		  		}else if(errorCode==EMError.USER_ALREADY_EXISTS){
		  		    Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
		  		}else if(errorCode==EMError.UNAUTHORIZED){
		  			Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
		  		}else{
		  			Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		        }
	        }
	        }}
	    ).start();
	}

	private void conn(Map<String, Object> info) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			// if (userStatus.equals("0")) {
			// 注册03-25 16:28:02.984: I/aaa(11089): info ===================== {uid=1703927717, favourites_count=3, location=北京 朝阳区, description=专注无线互联网！, verified=false, friends_count=156, gender=1, screen_name=狂野放歌, statuses_count=683, followers_count=193, profile_image_url=http://tp2.sinaimg.cn/1703927717/180/5642899489/1, access_token=2.00H8V_rBPrNzLCef2f81c59cikCRoD}

			params.add(new BasicNameValuePair("source", "2"));
			params.add(new BasicNameValuePair("user_name", info.get("uid").toString()));
			params.add(new BasicNameValuePair("password", "123456"));
			params.add(new BasicNameValuePair("name", info.get("screen_name").toString()));
			params.add(new BasicNameValuePair("photo", info.get("profile_image_url").toString()));
			SharedPreferences settings = LoginChoiceActivity.this.getBaseContext()
					.getSharedPreferences(Contanst.SystemStone, 0);
			params.add(new BasicNameValuePair("longitude", settings.getString("longitude", "-1")));
			params.add(new BasicNameValuePair("latitude", settings.getString("latitude", "-1")));
			params.add(new BasicNameValuePair("sex",  info.get("gender").toString()));
			params.add(new BasicNameValuePair("birthday", "631123200000"));
		} catch (Exception e) {

		}
		// if (userStatus.equals("0")) {
		// 注册
		Service.getService(Contanst.HTTP_REGISTER, null, null,
				LoginChoiceActivity.this).addList(params)
				.request(UrlParams.POST);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.group.conn.ServiceListener#getJOSNdataSuccess(java.lang.Object,
	 * java.lang.String, int)
	 */
	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataFail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataFail(String errcode, String errmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				
				if (msg.arg1 == Contanst.REGISTER) {
					
					((GroupApplication)(LoginChoiceActivity.this.getApplicationContext())).setCurSP(Contanst.UserStone); 
					
					SharedPreferences settings = LoginChoiceActivity.this
							.getBaseContext().getSharedPreferences(Contanst.UserStone, 0);
					Contanst.isLogin = true;
					UserBean userBean = (UserBean) msg.obj;
					Editor edit = settings.edit();
					edit.putString("userID", userBean.getId());
					edit.putString("userName", userBean.getName());
					edit.putString("userPhoto", userBean.getPhoto());
					edit.putString("alias", userBean.getPushid());
					edit.putString("userSex", userBean.getSex());
					edit.putLong("birthday", userBean.getBirthday());
					edit.putString("isSee", userBean.getIsSee());
					edit.putString("token", userBean.getToken());
					edit.putString("integral", userBean.getIntegral());
					edit.putString("tnum", userBean.getTnum());
					edit.putString("pnum", userBean.getPnum());
					edit.commit();
					
					// 注册成功。
					SharedPreferences setting = LoginChoiceActivity.this
							.getBaseContext().getSharedPreferences(
									Contanst.SystemStone, 0);
					Editor edit1 = setting.edit();
					edit1.putBoolean("isAutoLogin", true);
					edit1.commit();
					
					loginEasemob(userBean.getId(), userBean.getName());
					
					
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.REGISTER) {
					
				}
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				// Toast.makeText(LoginChoiceActivity.this, message,
				// Toast.LENGTH_LONG).show();
				break;
			}
		}

	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	

		if (requestCode == 111 && resultCode == 444) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				return;
			}
			
			Intent in = new Intent(LoginChoiceActivity.this, GroupMainFragmentActivity.class);
			LoginChoiceActivity.this.startActivity(in);
			LoginChoiceActivity.this.finish(); 
			overridePendingTransition(R.anim.activity_come_in,
					R.anim.activity_come_out_bottom);
		}
		/** 使用SSO授权必须添加如下代码 */
		// UMSsoHandler ssoHandler = MyUMSDK.mController.getConfig()
		// .getSsoHandler(requestCode);
		// if (ssoHandler != null) {
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }

		UMSsoHandler ssoHandler = msShareSDK.mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
