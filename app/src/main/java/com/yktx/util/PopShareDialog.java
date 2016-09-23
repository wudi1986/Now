/**
 * 
 */
package com.yktx.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.yktx.group.InvitationActivity;
import com.yktx.group.R;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年7月29日 下午4:11:18  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class PopShareDialog extends AlertDialog {

	// public RelativeLayout weixin, circle, jubao;
	public TextView weixin, circle, sinaShare, back, qqShare, qqRoomShare,
			shareInvitation;
	// public ImageView back;
	private RelativeLayout share;
	private Activity mContext;
	private String groupID;
	final String appID = "wxdb36e60780803b38";
	final String weixinappSecret = "6007e3653019c32cba53da63266eab80";
	final String QQID = "1103582580";
	final String QQappSecret = "UcZHX5axTNhgM19K ";
	final String contentUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.yktx.group";
	Bitmap imageBitmap;
	final String title = "Doing-没事找事";
	final String content = "无聊时，用Doing找点“事儿”做。没事找事！";
	boolean isShareImage;

	public PopShareDialog(Activity context, int theme, String groupID,
			Bitmap imageBitmap) {
		super(context, theme);
		mContext = context;
		this.groupID = groupID;
		this.imageBitmap = imageBitmap;
		if (imageBitmap != null) {
			isShareImage = true;
		}
	}

	public PopShareDialog(Activity context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pop_native_share_dialog);
		WindowManager windowManager = mContext.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = PopShareDialog.this.getWindow()
				.getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		PopShareDialog.this.getWindow().setAttributes(lp);
		final MyUMSDK msShareSDK = new MyUMSDK(mContext);
		final UMSocialService mController = UMServiceFactory
				.getUMSocialService("com.umeng.share");

		share = (RelativeLayout) findViewById(R.id.share);
		weixin = (TextView) findViewById(R.id.weixin);
		circle = (TextView) findViewById(R.id.circle);
		sinaShare = (TextView) findViewById(R.id.sinaShare);
		qqShare = (TextView) findViewById(R.id.qqShare);
		qqRoomShare = (TextView) findViewById(R.id.qqRoomShare);
		shareInvitation = (TextView) findViewById(R.id.share_invitation);
		back = (TextView) findViewById(R.id.share_back);

		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopShareDialog.this.dismiss();
			}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub.
				dismiss();
				// LiveCameraActivity.this.finish();
			}
		});

		if (groupID == null) {
			shareInvitation.setVisibility(View.GONE);
		}
		shareInvitation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// connInvitation("1361");
				Intent in = new Intent(mContext, InvitationActivity.class);
				in.putExtra("groupID", groupID);
				mContext.startActivity(in);
				dismiss();
			}

		});

		weixin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int i = msShareSDK.weixinUMShared(content, title, contentUrl,
						imageBitmap,isShareImage);
				dismiss();
			}

		});

		circle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

					int i = msShareSDK.friendsterUMShared(content, title,
							contentUrl, imageBitmap,isShareImage);
				dismiss();
			}
		});

		// qq
		qqShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				msShareSDK.qqUMShared(mContext, content, title, contentUrl,
						imageBitmap,isShareImage);
				dismiss();
			}
		});

		// qq空间
		qqRoomShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				msShareSDK.qzeroUMShared(mContext, content, title, contentUrl,
						imageBitmap);
				dismiss();
			}
		});

		// 新浪
		sinaShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				msShareSDK.sinaUMShared(content, contentUrl, imageBitmap);
				dismiss();
			}
		});
		setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// QhtMainActivity.mContext.share
				// .setVisibility(View.VISIBLE);
			}
		});

	}
}
