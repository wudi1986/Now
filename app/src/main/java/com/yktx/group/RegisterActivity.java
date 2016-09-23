package com.yktx.group;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.RegisterDialog;
import com.yktx.util.Tools;

public class RegisterActivity extends BaseActivity implements ServiceListener {
	private TextView mNext,register_himt;
	private Context mContext;
	private EditText mUserName, mUserPassword
//	,mConfirmPassword //确认密码取消
	;
	ImageView titleLeftImage, titleRightImage
//	,xieyiImage
	;
	String userEmail, passWord;
	ImageView userName_sign, password_sign, password_confirm_sign;
	boolean isUserName, isPassword, isNext;
	private RegisterDialog mRegisterDialog;
	boolean  isUserFocus,isPasswordFocus;//为了防止重复获得焦点

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		mContext = this;
		initView();
		initTitle();
	}

	private void initView() {

		userName_sign = (ImageView) findViewById(R.id.userName_sign);
		password_sign = (ImageView) findViewById(R.id.password_sign);
		password_confirm_sign = (ImageView) findViewById(R.id.password_confirm_sign);
		mNext = (TextView) findViewById(R.id.registerFinish);
//		xieyiImage = (ImageView) findViewById(R.id.xieyiImage);
		mUserName = (EditText) findViewById(R.id.register_user);
		mUserPassword = (EditText) findViewById(R.id.register_password);
		register_himt = (TextView) findViewById(R.id.register_himt);
		mUserName.setFocusable(false);   
		mUserName.setFocusableInTouchMode(false);   
		mUserName.requestFocus(); 
		mUserPassword.setFocusable(false);   
		mUserPassword.setFocusableInTouchMode(false);   
		mUserPassword.requestFocus(); 
//		mConfirmPassword = (EditText) findViewById(R.id.register_confirmpassword);//确认密码取消
		mUserName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isUserFocus){
					mUserName.setFocusable(true);   
					mUserName.setFocusableInTouchMode(true);   
					mUserName.requestFocus();
					isUserFocus = true;
				}
				if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
					GroupApplication.openKeybord(mUserName, mContext);
				}
			}
		});
		mUserPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isPasswordFocus){
					mUserPassword.setFocusable(true);   
					mUserPassword.setFocusableInTouchMode(true);   
					mUserPassword.requestFocus(); 
					isPasswordFocus = true;
				}
				if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
					GroupApplication.openKeybord(mUserPassword, mContext);
				}
			}
		});
		
		mUserName
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus) {
							userEmail = mUserName.getText().toString();
							int userLength = userEmail.length();
							if (userLength == 0) {
								userName_sign
										.setImageResource(R.drawable.login_wrong);
//								toast.setText("邮箱不能为空！");
//								toast.show();
								mNext.setBackgroundResource(R.drawable.shape_member);
								mNext.setTextColor(getResources().getColor(
										R.color.meibao_color_3));
								mUserName.setHint("输入邮箱地址");
								return;
							}
							isUserName = true;
//							if (!isEmail(userEmail)) {
//								userName_sign
//										.setImageResource(R.drawable.login_wrong);
////								toast.setText("邮箱格式不正确！");
////								toast.show();
//								register_himt.setText("邮箱格式不正确！");
//								register_himt.setText("");
//								mNext.setBackgroundResource(R.drawable.shape_member);
//								mNext.setTextColor(getResources().getColor(
//										R.color.gray_6));
//								return;
//							}
//							connValidateUserName(userEmail);

						}else{
							mUserName.setHint("");
						}
					}
				});
		mUserPassword.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					mUserPassword.setHint("");
				}else{
					
					String passwordStr = mUserPassword.getText().toString();
					if(passwordStr.length() == 0){
						mUserPassword.setHint("6-12数字或英文字母");
						return ; 
					}
				}
			}
		});
		mUserName.addTextChangedListener(new TextWatcher() {
			
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
				userEmail = mUserName.getText().toString();
				int userLength = userEmail.length();
				if (userLength == 0) {
					
					return;
				}
				isUserName = true;
				if (isUserName && isPassword) {
					mNext.setBackgroundResource(R.drawable.shape_color1);
					mNext.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					mNext.setBackgroundResource(R.drawable.shape_member);
					mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
				}
			}
		});
		mUserPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				passWord = mUserPassword.getText().toString();
				int passwordLength = passWord.length();
				if (passwordLength == 0) {
					password_sign.setImageResource(R.drawable.login_wrong);
					isPassword = false;
					mNext.setBackgroundResource(R.drawable.shape_member);
					mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
					return;
				}
				register_himt.setText("");
				isPassword = true;
				password_sign.setImageResource(R.drawable.login_right);
				if (isUserName && isPassword) {
					mNext.setBackgroundResource(R.drawable.shape_color1);
					mNext.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					mNext.setBackgroundResource(R.drawable.shape_member);
					mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
				}

				password_confirm_sign.setVisibility(View.VISIBLE);
				password_confirm_sign.setImageResource(R.drawable.login_right);
				Tools.getLog(Tools.d, "aaa", "isUserName"+isUserName+",isPassword"+isUserName);
				if (isUserName && isPassword) {
					mNext.setBackgroundResource(R.drawable.shape_color1);
					mNext.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					mNext.setBackgroundResource(R.drawable.shape_member);
					mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
				}

			}
		});


		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isUserName && isPassword) {
					passWord = mUserPassword.getText().toString();
					int passwordLength = passWord.length();
					userEmail = mUserName.getText().toString();
					int userLength = userEmail.length();
					if (userLength == 0) {
						userName_sign.setImageResource(R.drawable.login_wrong);
						register_himt.setText("邮箱不能为空！");
						mNext.setBackgroundResource(R.drawable.shape_member);
						mNext.setTextColor(getResources().getColor(
								R.color.meibao_color_3));
						return;
					}
					if (!isEmail(userEmail)) {
						userName_sign.setImageResource(R.drawable.login_wrong);
//						register_himt.setText("邮箱格式不正确！");
						openOneButtonDialog("请输入正确格式的邮箱地址");
						mNext.setBackgroundResource(R.drawable.shape_member);
						mNext.setTextColor(getResources().getColor(
								R.color.meibao_color_3));
						return;
					}
					
					if (passwordLength < 6 || passwordLength > 12) {
						password_sign.setImageResource(R.drawable.login_wrong);
						isPassword = false;
						mNext.setBackgroundResource(R.drawable.shape_member);
						mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
//						if(passwordLength > 12){
						openOneButtonDialog("密码需要为6-12位数字或英文字母");
//						}
						return;
					}
					mRegisterDialog = new RegisterDialog(mContext);
					mRegisterDialog.show();
					mRegisterDialog.mExit.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							mRegisterDialog.dismiss();
						}
					});
					connValidateUserName(userEmail);

					isNext = true;

				} else {
					isNext = false;
				}
			}
		});

//		/**
//		 * 协议监听
//		 */
//		xieyiImage.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent in = new Intent(RegisterActivity.this,
//						XieYiActivity.class);
//				RegisterActivity.this.startActivity(in);
//			}
//		});
	}

	private void connValidateUserName(String user_name) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("user_name", user_name));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_VALIDATEUSERNAME, null, null,
				RegisterActivity.this).addList(params).request(UrlParams.POST);

	}

	private void initTitle() {
		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleLeftImage.setImageResource(R.drawable.zhuce_back);
		// rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);
		titleRightImage.setVisibility(View.GONE);
		TextView text = (TextView) findViewById(R.id.title);
		text.setText("注册");
		TextView rightText = (TextView) findViewById(R.id.right);
		rightText.setVisibility(View.GONE);
		titleLeftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GroupApplication.closeKeybord(mUserName, mContext);
				// 返回
				RegisterActivity.this.finish();
			}
		});
	}
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
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.VALIDATEUSERNAME) {

					if (isNext) {
						Intent in = new Intent(RegisterActivity.this,
								RegisterHeadActivity.class);
						isNext = false;
						in.putExtra("userName", userEmail);
						in.putExtra("passWord", passWord);
						startActivityForResult(in, 222);
					} else {
						userName_sign.setImageResource(R.drawable.login_right);
						isUserName = true;
						if (isUserName && isPassword) {
							mNext.setBackgroundResource(R.drawable.shape_color1);
							mNext.setTextColor(getResources().getColor(
									R.color.white));
						} else {
							mNext.setBackgroundResource(R.drawable.shape_member);
							mNext.setTextColor(getResources().getColor(
									R.color.meibao_color_3));
						}
					}
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.VALIDATEUSERNAME) {

					userName_sign.setImageResource(R.drawable.login_wrong);
					isUserName = false;
					isNext = false;
				}
				OpenDialog();
				if (isUserName && isPassword) {
					mNext.setBackgroundResource(R.drawable.shape_color1);
					mNext.setTextColor(getResources().getColor(
							R.color.white));
				} else {
					mNext.setBackgroundResource(R.drawable.shape_member);
					mNext.setTextColor(getResources().getColor(R.color.meibao_color_3));
				}
				break;
			}
		}

	};

	public static boolean isEmail(String strEmail) {
		String isStrEmail = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(isStrEmail);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(  "aaa", "resultCode ============== " + resultCode);
		Log.i(  "aaa", "requestCode ============== "
				+ requestCode);
		if (resultCode == 222) {
			Intent intentdata = new Intent();
			setResult(444, intentdata);
			RegisterActivity.this.finish();
			return;
		}
	}
	public void OpenDialog(){
		new AlertDialog.Builder(mContext)
		.setTitle("提示")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setMessage("邮箱已被注册")
		.setPositiveButton("登录", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String strName = mUserName.getText().toString(); 
				Intent intent = new Intent(mContext, LoginActivity.class);
				intent.putExtra("register_user", strName);
				finish();
				startActivity(intent);
			}
		})
		.setNegativeButton("换一个邮箱", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mUserName.setText("");
				mUserPassword.setText("");
			}
		})
		.show();
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
	
}
