/**
 * 
 */
package com.yktx.util;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yktx.group.R;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年7月29日 下午4:11:18  
 * 类说明  */
/**
 * @author Administrator
 *
 */
public class LoginOrRegisterDialog extends AlertDialog{

//	public RelativeLayout weixin, circle, jubao;
	public TextView loginBotton, registerBotton, back;
//	public ImageView back;
	private RelativeLayout share;
	
	
	public LoginOrRegisterDialog(Context context, int theme) {
	    super(context, theme);
	}
	
	public LoginOrRegisterDialog(Context context) {
	    super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.login_or_register_dialog);
	    
	    share = (RelativeLayout)findViewById(R.id.share);
	    loginBotton = (TextView)findViewById(R.id.loginBotton);
	    registerBotton = (TextView)findViewById(R.id.registerBotton);
	    back = (TextView)findViewById(R.id.share_back);
	    
	    share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginOrRegisterDialog.this.dismiss();
			}
		});
	    
	 
	}
}
