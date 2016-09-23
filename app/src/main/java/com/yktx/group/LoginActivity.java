/**
 * 
 */
package com.yktx.group;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.yktx.bean.UserBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.RegisterDialog;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年5月21日 下午6:56:13  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity implements ServiceListener {
	// 初始化数字按键

	/**
	 * 
	 */
	// public com.way.locus.MyScrollView sl;

	/** 输入计数 */
	EditText login_user, password;
	String userName = ""; // 输入的手机号
	String userEmail,userPassword;
	// KeyboardView keyboardView;
	// CheckBox checkBox;
	TextView registerFinish,forgetPassword,login_himt;
	LinearLayout autoLoginLayout, remenberPasswordLayout;
	ImageView autoLoginCheck, remenberPasswordCheck;
	boolean isAuto = true, isRemenber = true;
	SharedPreferences systemSP ;
	RegisterDialog mRegisterDialog;
	boolean  isUserFocus,isPasswordFocus;//为了防止重复获得焦点

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_newactivity);
		// mContent = this;
		// sl = (com.way.locus.MyScrollView) findViewById(R.id.sl);
		
		initTitle();
		systemSP = LoginActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.SystemStone, 0);

		//		isRemenber = systemSP.getBoolean("RemenberPassword", false);
		isAuto = true;

		registerFinish = (TextView) findViewById(R.id.registerFinish);

		login_user = (EditText) findViewById(R.id.login_user);
		password = (EditText) findViewById(R.id.login_password);
		//刚进入此页面两个输入框都取消焦点
		login_user.setFocusable(false);   
		login_user.setFocusableInTouchMode(false);   
		login_user.requestFocus(); 
		password.setFocusable(false);   
		password.setFocusableInTouchMode(false);   
		password.requestFocus();
		String strName = getIntent().getStringExtra("register_user");
		if(strName != null && strName.length() != 0){
			login_user.setText(strName);
		}

		SharedPreferences userSP = LoginActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.UserStone, 0);
		userEmail = userSP.getString("userEmail", "");
		Log.i(  "aaa", "userEmail ================ "+userSP.getString("password", ""));
		//		login_user.setText(userEmail);
		login_himt = (TextView) findViewById(R.id.login_himt);

		autoLoginCheck = (ImageView) findViewById(R.id.autoLoginCheck);
		remenberPasswordCheck = (ImageView) findViewById(R.id.remenberPasswordCheck);
		autoLoginLayout = (LinearLayout) findViewById(R.id.autoLoginLayout);
		remenberPasswordLayout = (LinearLayout) findViewById(R.id.remenberPasswordLayout);
		//		if(isRemenber){
		//			password.setText(userSP.getString("password", ""));
		//			remenberPasswordCheck.setImageResource(R.drawable.zhuce_icon1);
		//		} else {
		//			remenberPasswordCheck.setImageResource(R.drawable.zhuce_icon2);
		//		}
		if(isAuto){
			autoLoginCheck.setImageResource(R.drawable.zhuce_icon1);
		} else {
			autoLoginCheck.setImageResource(R.drawable.zhuce_icon2);
		}

		autoLoginLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isAuto) {
					autoLoginCheck
					.setImageResource(R.drawable.zhuce_icon2);
					isAuto = false;
				} else {
					autoLoginCheck
					.setImageResource(R.drawable.zhuce_icon1);
					remenberPasswordCheck
					.setImageResource(R.drawable.zhuce_icon1);
					isAuto = true;
					isRemenber = true;
				}
			}
		});
		remenberPasswordLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (isRemenber) {
					remenberPasswordCheck
					.setImageResource(R.drawable.zhuce_icon2);
					isRemenber = false;
				} else {
					remenberPasswordCheck
					.setImageResource(R.drawable.zhuce_icon1);
					isRemenber = true;
				}
			}
		});
		login_user.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isUserFocus){
					login_user.setFocusable(true);   
					login_user.setFocusableInTouchMode(true);   
					login_user.requestFocus();
					isUserFocus = true;
				}
				if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
					GroupApplication.openKeybord(login_user, mContext);
				}
				
			}
		});
		password.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isPasswordFocus){
					password.setFocusable(true);   
					password.setFocusableInTouchMode(true);   
					password.requestFocus(); 
					isPasswordFocus = true;
				}
				if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
					GroupApplication.openKeybord(password, mContext);
				}
			}
		});
		
		login_user.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					login_user.setHint("");
				}else{
					String userStr = login_user.getText().toString();
					if(userStr.length() == 0){
						login_user.setHint("请输入邮箱地址");
					}
				}
				
			}
		});
		password.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					password.setHint("");
				}else{
					String passwordStr = password.getText().toString();
					if(passwordStr.length() == 0){
						password.setHint("输入密码");
					}
				}
			}
		});

		registerFinish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// final String password = pwd.getText().toString();
				GroupApplication.getInstance().closeKeybord(password, mContext);
				userName = login_user.getText().toString();
				userPassword = password.getText().toString();
				if (login_user.length() == 0) {
					login_himt.setText("请输入用户名");
					return;

				} else if (userPassword.length() == 0) {
					login_himt.setText("请输入密码");
					return;
				} else {
					mRegisterDialog = new RegisterDialog(mContext,"正在登录，请稍等");
					mRegisterDialog.show();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					try {
						params.add(new BasicNameValuePair("user_name", userName));
						params.add(new BasicNameValuePair("password", userPassword));
					} catch (Exception e) {

					}
					Service.getService(Contanst.HTTP_APPLOGIN, null, null,
							LoginActivity.this).addList(params)
							.request(UrlParams.POST);
				}
			}
		});
		login_user.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String userStr = login_user.getText().toString();
				if(userStr.length()!= 0){
					forgetPassword.setVisibility(View.VISIBLE);
				}else{
					forgetPassword.setVisibility(View.GONE);
				}
			}
		});
		password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String userStr = login_user.getText().toString();
				String passwordStr = password.getText().toString();

				if(userStr.length()!= 0 &&passwordStr.length()!= 0){
					registerFinish.setBackgroundResource(R.drawable.shape_color1);
					registerFinish.setTextColor(getResources().getColor(R.color.white));
				}else{
					registerFinish.setBackgroundResource(R.drawable.shape_member);
					registerFinish.setTextColor(getResources().getColor(R.color.meibao_color_6));
				}
			}
		});
		forgetPassword = (TextView)findViewById(R.id.forgetPassword);
		forgetPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String strEmail = login_user.getText().toString();
				if(isEmail(strEmail)){
					userEmail = strEmail;
					forgetPassword.setText("新密码正在发送");
					conn();
				}else{
					openOneButtonDialog("请输入正确格式的邮箱地址");
				}
				
			}
		});
	}

	ImageView titleLeftImage, titleRightImage;

	private void initTitle() {

		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleLeftImage.setImageResource(R.drawable.zhuce_back);
		// rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);
		titleRightImage.setVisibility(View.GONE);
		TextView text = (TextView) findViewById(R.id.title);
		text.setText("登录");
		TextView rightText = (TextView) findViewById(R.id.right);
		rightText.setVisibility(View.GONE);
		titleLeftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GroupApplication.closeKeybord(login_user, mContext);
				// 返回
				LoginActivity.this.finish();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
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
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
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
			if(mRegisterDialog != null && mRegisterDialog.isShowing()){
				mRegisterDialog.dismiss();
			}
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.APPLOGIN) {

					UserBean userBean = (UserBean) msg.obj;
					((GroupApplication)(LoginActivity.this.getApplicationContext())).setCurSP(Contanst.UserStone);
					Contanst.isLogin = true;
					SharedPreferences settings = LoginActivity.this
							.getBaseContext().getSharedPreferences(
									((GroupApplication)(LoginActivity.this.getApplicationContext())).getCurSP(), 0);
					Editor edit = settings.edit();
					// 需要判断是否是新用户
					edit.putString("userID", userBean.getId());
					edit.putString("userName", userBean.getName());
					edit.putString("password", userPassword);

					edit.putString("userPhoto", userBean.getPhoto());
					edit.putString("alias", userBean.getPushid());
					edit.putString("userStatus", userBean.getStatus());
					edit.putString("userSex", userBean.getSex());
					edit.putLong("birthday", userBean.getBirthday());
					edit.putString("integral", userBean.getIntegral());
					edit.putString("tnum", userBean.getTnum());
					edit.putString("pnum", userBean.getPnum());
					edit.putString("userEmail", userBean.getUser_name());
					edit.commit();
					

					SharedPreferences setting = LoginActivity.this
							.getBaseContext().getSharedPreferences(
									Contanst.SystemStone, 0);
					Editor edit1 = setting.edit();
					edit1.putBoolean("isAutoLogin", isAuto);
					edit1.putBoolean("RemenberPassword", isRemenber);
					edit1.commit();
					loginEasemob(userBean.getId(), userBean.getName());
					
				} else if (msg.arg1 == Contanst.UPDATEPASSWORD){
					login_himt.setText("新密码已发送您的注册邮箱，请查收。");
				}else if(msg.arg1 == Contanst.FORGETPASSWORD){
					forgetPassword.setText("新密码已经发送到邮箱");
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.APPLOGIN) {
					login_himt.setText("用户名或密码错误，请从新填写");

				} else if (msg.arg1 == Contanst.UPDATEPASSWORD) {

				}
				Animation shake = AnimationUtils.loadAnimation(
						LoginActivity.this, R.anim.shake);
				login_user.startAnimation(shake);
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				login_himt.setText(message);
				break;
			}
		}

	};
	
	private void conn() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {

			params.add(new BasicNameValuePair("user_name", userEmail));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_FORGETPASSWORD, null, null,
				LoginActivity.this).addList(params)
				.request(UrlParams.POST);

	}
	
	
	
	public void openOneButtonDialog(String message){
		new AlertDialog.Builder(mContext)
		.setTitle("提示")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage(message)
		.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	}
	public static boolean isEmail(String strEmail) {
		String isStrEmail = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(isStrEmail);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}
	
	public void loginEasemob(final String userName ,final String userNickName){
		
		EMChatManager.getInstance().login(userName,"123",new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						
						
						EMChatManager.getInstance().loadAllConversations();
						Log.i(  "bbb", "登陆聊天服务器成功！");	

						EMChatManager.getInstance().updateCurrentUserNick(userNickName);
						Intent intentdata = new Intent();
						setResult(444, intentdata);
						LoginActivity.this.finish();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {
				
			}

			@Override
			public void onError(int code, String message) {
				Log.i(  "bbb", "登陆聊天服务器失败！");
			}
		});
	}
	
}
