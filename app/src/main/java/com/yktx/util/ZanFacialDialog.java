/**
 * 
 */
package com.yktx.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.RegisterActivity;
import com.yktx.group.adapter.ChatZanGridViewAdapter;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年7月29日 下午4:11:18  
 * 类说明 选择点赞表情弹出界面 */
/**
 * @author Administrator
 * 
 */
public class ZanFacialDialog extends AlertDialog {

	// public RelativeLayout weixin, circle, jubao;
	// public ImageView back;
	private Activity mContext;
	GridView zanGridView;
	ChatZanGridViewAdapter adapter;

	public ZanFacialDialog(Activity context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public ZanFacialDialog(Activity context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.zan_facial_dialog);

		WindowManager windowManager = mContext.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = ZanFacialDialog.this.getWindow()
				.getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.height = (int) (display.getHeight()); // 设置宽度
		ZanFacialDialog.this.getWindow().setAttributes(lp);

		zanGridView = (GridView) findViewById(R.id.zanGridView);
		adapter = new ChatZanGridViewAdapter(mContext);
		zanGridView.setAdapter(adapter);

		zanGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if (GroupApplication.getInstance().getCurSP()
						.equals(Contanst.UserStone)) {
					if (onClickZanFacialButton != null) {
						onClickZanFacialButton.getClick(arg2 + 1);
					}
				} else {
					showdialogFinish();
				}
				ZanFacialDialog.this.dismiss();
			}
		});
		
		RelativeLayout share = (RelativeLayout)findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ZanFacialDialog.this.dismiss();
			}
		});
		
	}
	
	private void showdialogFinish() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(mContext, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("游客身份禁止此项操作");
		builder.setPositiveButton("去注册", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GroupApplication.getInstance().backHomeActivity();
				Intent intentdata = new Intent(GroupApplication.getInstance().activityList.get(0), RegisterActivity.class);
				GroupApplication.getInstance().activityList.get(0).startActivity(intentdata);
				
			}
		});
		builder.setNegativeButton("我知道了", null);
		builder.show();
	}


	OnClickZanFacialButton onClickZanFacialButton;

	public void setOnClickZanFacialButton(
			OnClickZanFacialButton onClickZanFacialButton) {
		this.onClickZanFacialButton = onClickZanFacialButton;
	}

	public interface OnClickZanFacialButton {
		public void getClick(int photoChatID);
	}

}
