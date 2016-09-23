package com.yktx.group;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.tendcloud.tenddata.TCAgent;
import com.yktx.bean.UserBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.fragment.GroupMainFragmentActivity;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.FileURl;

public class MainActivity extends BaseActivity implements ServiceListener {

	// TextView ok;
	// String Imei;
	boolean isNet;
	boolean isTimer;
	public static boolean isNewUser;

	private static final int OPNE_WEATHERACT = 1;

	UserBean userBean;
	boolean isAutoLogin;
	String userNickName;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TCAgent.init(this);
		SharedPreferences systemSP = MainActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.SystemStone, 0);
		String longitude = systemSP.getString("longitude", "-1");
		String latitude = systemSP.getString("latitude", "-1");
		isAutoLogin = systemSP.getBoolean("isAutoLogin", false);
		Contanst.isLogin = false;
		Log.i(  "aaa", "isAotuLogin ============ " + isAutoLogin);

		// 如果不是登录使用游客身份
		// 之前登录了
		if (isAutoLogin) {
			(GroupApplication.getInstance()).setCurSP(Contanst.UserStone);
			Contanst.isLogin = true;

			SharedPreferences settings = MainActivity.this.getBaseContext()
					.getSharedPreferences(
							(GroupApplication.getInstance()).getCurSP(), 0);
			String alias = settings.getString("alias", null);
			userNickName = settings.getString("userName", null);

			File file = new File(FileURl.GoodsVoiceURL);
			if (!file.exists()) {
				file.mkdirs();
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			try {

				params.add(new BasicNameValuePair("source", "0"));
				if (alias != null) {
					params.add(new BasicNameValuePair("pushid", alias));
				}
				params.add(new BasicNameValuePair("longitude", longitude));
				params.add(new BasicNameValuePair("latitude", latitude));
			} catch (Exception e) {

			}
			Service.getService(Contanst.HTTP_REGISTER_VERIFCATION, null, null,
					MainActivity.this).addList(params).request(UrlParams.POST);
		} else {
			// (GroupApplication.getInstance()).setCurSP(Contanst.TouristStone);
		}
		h.sendEmptyMessageDelayed(OPNE_WEATHERACT, 1000);
	}

	private Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPNE_WEATHERACT:
				if (isNet || !isAutoLogin) {
					openAct();
				}
				isTimer = true;
				break;
			default:
				break;
			}
		}
	};

	private void openAct() {
		// TODO Auto-generated method stub
		if (isAutoLogin) {
			SharedPreferences settings = MainActivity.this.getBaseContext()
					.getSharedPreferences(
							(GroupApplication.getInstance()).getCurSP(), 0);
			Editor edit = settings.edit();
			// 需要判断是否是新用户
			userNickName = userBean.getName();
			edit.putString("userID", userBean.getId());
			edit.putString("userName", userBean.getName());
			edit.putString("userPhoto", userBean.getPhoto());
			edit.putString("alias", userBean.getPushid());
			edit.putString("userStatus", userBean.getStatus());
			edit.putString("userSex", userBean.getSex());
			edit.putLong("birthday", userBean.getBirthday());
			edit.putString("integral", userBean.getIntegral());
			edit.putString("tnum", userBean.getTnum());
			edit.putString("pnum", userBean.getPnum());
			edit.commit();
			
			loginEasemob(userBean.getId());
			
		
		} else {
			Intent in = new Intent(MainActivity.this, LoginChoiceActivity.class);
			MainActivity.this.startActivity(in);
			MainActivity.this.finish();
		}
		// }

		// commitshare();

	}
	
	boolean isLoginFail;
	public void loginEasemob(final String userName){
		
		EMChatManager.getInstance().login(userName,"123",new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						
						EMChatManager.getInstance().updateCurrentUserNick(userNickName);
						
						EMChatManager.getInstance().loadAllConversations();
						Log.i(  "bbb", "登陆聊天服务器成功！");	
						Intent in = new Intent(MainActivity.this, GroupMainFragmentActivity.class);
						MainActivity.this.startActivity(in);
						MainActivity.this.finish();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
				
			}

			@Override
			public void onError(int code, String message) {
				Log.i(  "bbb", "登陆聊天服务器失败！");
				registerEasemob(userName);
				isLoginFail = true;
			}
		});
	}
	
	
	public void registerEasemob(final String userName) {
		new Thread(new Runnable() {
	
	    public void run() {
	        try {
	           // 调用sdk注册方法
	           EMChatManager.getInstance().createAccountOnServer(userName, "123");
	           if(isLoginFail){
	        	   loginEasemob(userName);
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


	public boolean getshare() {
		SharedPreferences preferences = getSharedPreferences("HELP",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("isfirst", true);
	}

	public void commitshare() {
		SharedPreferences preferences = getSharedPreferences("HELP",
				Context.MODE_PRIVATE);
		preferences.edit().putBoolean("isfirst", false).commit();
	}

	public boolean getPhone() {
		SharedPreferences preferences = getSharedPreferences(
				(GroupApplication.getInstance()).getCurSP(),
				Context.MODE_PRIVATE);
		if (preferences.getString("phone", "-1").equals("-1")) {
			return false;
		} else {
			return true;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataSuccess(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "bbb", "getJOSNdataSuccessgetJOSNdataSuccess");
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
		Log.i(  "bbb", "getJOSNdataFailgetJOSNdataFail");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i(  "bbb", " Handler mHandler = new Handler()");
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.REGISTER_VERIFCATION) {
					userBean = (UserBean) msg.obj;

					if (isTimer) {
						openAct();
					}
					isNet = true;
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}

	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TCAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TCAgent.onPause(this);
	}

}
