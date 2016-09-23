package com.yktx.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.yktx.group.R;

public class RegisterDialog extends AlertDialog{
	private String message;
	public RegisterDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public RegisterDialog(Context context,String message) {
		super(context);
		// TODO Auto-generated constructor stub
		this.message = message;
	}
	public Button mExit;
	public TextView mContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_dialog);
		mExit = (Button) findViewById(R.id.register_exit);
		mContent = (TextView) findViewById(R.id.register_content);
		if(message != null){
			mContent.setText(message);
		}
	}
}
