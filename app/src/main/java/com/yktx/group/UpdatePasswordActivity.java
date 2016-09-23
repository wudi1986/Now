package com.yktx.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;

/**
 * 
 * 修改密码
 * 
 * @author Administrator
 * 
 */
public class UpdatePasswordActivity extends BaseActivity implements
		ServiceListener {

	EditText oldPassword, newPassword, confirmpassword;
	TextView finish;
	String userEmail, newStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_password_activity);
		SharedPreferences settings = UpdatePasswordActivity.this
				.getBaseContext().getSharedPreferences((GroupApplication.getInstance()).getCurSP(), 0);
		userEmail = settings.getString("userEmail", "");
		Log.i(  "aaa", "userEmail =========== "+userEmail);
		initTitle();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
		finish = (TextView) findViewById(R.id.finish);

		finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				newStr = newPassword.getText().toString();
				String oldStr = oldPassword.getText().toString(), confirmStr = confirmpassword
						.getText().toString();
				if (oldStr == null || oldStr.length() == 0) {
					Toast.makeText(UpdatePasswordActivity.this, "请输入旧密码",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (newStr == null || newStr.length() == 0) {
					Toast.makeText(UpdatePasswordActivity.this, "请输入新密码",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (confirmStr == null || confirmStr.length() == 0) {
					Toast.makeText(UpdatePasswordActivity.this, "请输入确认密码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (newStr.length() < 6 || newStr.length()  > 12) {
					Toast.makeText(UpdatePasswordActivity.this, "密码长度不在范围内！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!newStr.equals(confirmStr)) {
					Toast.makeText(UpdatePasswordActivity.this, "输入的两次密码不一样！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				conn(oldStr, newStr);
			}
		});
	}

	private void initTitle() {
		ImageView titleLeftImage, titleRightImage;
		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleLeftImage.setImageResource(R.drawable.zhuce_back);
		// rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);
		titleRightImage.setVisibility(View.GONE);
		TextView text = (TextView) findViewById(R.id.title);
		text.setText("修改密码");
		TextView rightText = (TextView) findViewById(R.id.right);
		rightText.setVisibility(View.GONE);
		titleLeftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 返回
				UpdatePasswordActivity.this.finish();
			}
		});
	}

	private void conn(String oldPassword, String newPassword) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			
			params.add(new BasicNameValuePair("user_name", userEmail));
			params.add(new BasicNameValuePair("oldpassword", oldPassword));
			params.add(new BasicNameValuePair("newpassword", newPassword));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_UPDATEPASSWORD, null, null,
				UpdatePasswordActivity.this).addList(params)
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
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.UPDATEPASSWORD) {
//					UserBean userBean = (UserBean) msg.obj;
					SharedPreferences settings = UpdatePasswordActivity.this
							.getBaseContext().getSharedPreferences(
									((GroupApplication)(UpdatePasswordActivity.this.getApplicationContext())).getCurSP(), 0);
					Editor edit = settings.edit();
					edit.putString("password", newStr);
					edit.commit();
					Toast.makeText(UpdatePasswordActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
					UpdatePasswordActivity.this.finish();
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.UPDATEPASSWORD) {
					
				}
				String message = (String) msg.obj;
				Toast.makeText(UpdatePasswordActivity.this, message,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

}
