package com.yktx.group;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yktx.bean.UserBean;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.FileURl;
import com.yktx.util.NotificationUtil;
import com.yktx.util.PictureUtil;
import com.yktx.util.TimeUtil;
import com.yktx.util.Tools;

/**
 * Created by songhang on 2014/4/12.
 */
@SuppressLint({ "ResourceAsColor", "NewApi" })
public class RegisterHeadActivity extends BaseActivity implements
		ServiceListener {

	ImageView registerinfo_headimage, registerinfo_headimageline;
	TextView manCheck, womanCheck;
	EditText registerinfo_edit_name;
	// CheckBox checkBox;
	// TextView unline;
	private RelativeLayout rl_leftImage, rl_registerinfo_takeheadimage;

	public static final int STATE_BAOBAO = 2, STATE_HUAIYUN = 1,
			STATE_DAIYUN = 0;
	private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;

	TextView birthdayText, registerFinish;
	LinearLayout birthdayLayout;

	ImageView titleLeftImage, titleRightImage;
	boolean isHead, isUserName;
	LinearLayout manCheckLayout, womanCheckLayout;
	// ImageView xieyiImage;
	private String userStatus, userName, userPhoto, latitude, longitude,
			userEmail, passWord, userID;

	/**
	 * 是否为注册 true 用户第一次进入是注册 false 用户再次进入修改注册信息
	 */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		isHead = false;
		isUserName = false;
		options = new DisplayImageOptions.Builder().showImageOnLoading(null)
				.showImageOnFail(null).showImageForEmptyUri(R.drawable.addhead)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(450))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		userEmail = getIntent().getStringExtra("userName");
		passWord = getIntent().getStringExtra("passWord");
		userID = getIntent().getStringExtra("userID");
		SharedPreferences settings = RegisterHeadActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		// 判断是否是注册状态
		userStatus = "0";
		userName = settings.getString("userName", "");
		userPhoto = settings.getString("userPhoto", "0");

		SharedPreferences setting = RegisterHeadActivity.this.getBaseContext()
				.getSharedPreferences(Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		if ("null".equals(userPhoto))
			userPhoto = "";

		setContentView(R.layout.register_head_layout);
		initview();
		initTitle();
	}

	private void initTitle() {
		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleLeftImage.setImageResource(R.drawable.zhuce_back);
		// rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);
		titleRightImage.setVisibility(View.GONE);
		TextView text = (TextView) findViewById(R.id.title);
		text.setText("填写个人资料");
		TextView rightText = (TextView) findViewById(R.id.right);
		rightText.setVisibility(View.GONE);
		titleLeftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 返回
				RegisterHeadActivity.this.finish();
			}
		});
		rightText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 完成
				/* if (isRegister) { */// 注册时必须填写

				if (!isHead) {
					initToast("请添加头像");
					Animation shake = AnimationUtils.loadAnimation(
							RegisterHeadActivity.this, R.anim.shake);
					rl_registerinfo_takeheadimage.startAnimation(shake);
					// Toast.makeText(RegisterHeadActivity.this, "请选择头像",
					// Toast.LENGTH_SHORT).show();
					// ToastUtil.showtextshort("请选择头像");
					return;
				}
				if (registerinfo_edit_name.getText().toString().equals("")) {
					initToast("请输入用户名");
					Animation shake = AnimationUtils.loadAnimation(
							RegisterHeadActivity.this, R.anim.shake);
					registerinfo_edit_name.startAnimation(shake);
					// Toast.makeText(RegisterHeadActivity.this, "请填写名字",
					// Toast.LENGTH_SHORT).show();
					// ToastUtil.showtextshort("请填写名字");
					return;
				}

				TelephonyManager tm = (TelephonyManager) RegisterHeadActivity.this
						.getSystemService(TELEPHONY_SERVICE);
				final String Imei = tm.getDeviceId();

				NotificationUtil
						.calltop(RegisterHeadActivity.this, "照片发送中....");
				// communityid = intent.getStringExtra("communityid");
				String path = FileURl.LOAD_FILE + FileURl.GoodsIamgeURL
						+ FileURl.IMAGE_NAME;
				ImageLoader.getInstance().loadImage(path,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub

								String str = PictureUtil.save(loadedImage);
								loadedImage.recycle();

								List<NameValuePair> params = new ArrayList<NameValuePair>();
								try {

									params.add(new BasicNameValuePair("source",
											"0"));
									params.add(new BasicNameValuePair(
											"user_name", userEmail));
									params.add(new BasicNameValuePair(
											"password", passWord));
									params.add(new BasicNameValuePair("name",
											registerinfo_edit_name.getText()
													.toString()));
									params.add(new BasicNameValuePair("sex",
											sex));
									params.add(new BasicNameValuePair(
											"longitude", longitude));
									params.add(new BasicNameValuePair(
											"longitude", latitude));

									params.add(new BasicNameValuePair("photo",
											str));
								} catch (Exception e) {

								}
								Service.getService(
										Contanst.HTTP_UPDATEUSERINFO, null,
										null, RegisterHeadActivity.this)
										.addList(params)
										.request(UrlParams.POST);
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}
						});
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(msgReceiver);
		GroupApplication.closeKeybord(registerinfo_edit_name, mContext);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	String sex = "0";

	private void initview() {
		// checkBox = (CheckBox) findViewById(R.id.checkBox);
		// xieyiImage = (ImageView) findViewById(R.id.xieyiImage);
		womanCheckLayout = (LinearLayout) findViewById(R.id.womanCheckLayout);
		manCheckLayout = (LinearLayout) findViewById(R.id.manCheckLayout);

		manCheck = (TextView) findViewById(R.id.manCheck);
		womanCheck = (TextView) findViewById(R.id.womanCheck);
		birthdayText = (TextView) findViewById(R.id.birthdayText);
		birthdayLayout = (LinearLayout) findViewById(R.id.birthdayLayout);
		registerFinish = (TextView) findViewById(R.id.registerFinish);
		registerFinish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 完成
				/* if (isRegister) { */// 注册时必须填写

				if (!isUserName && !isHead) {
					return;
				}

				if (!isHead) {
					initToast("请添加头像");
					Animation shake = AnimationUtils.loadAnimation(
							RegisterHeadActivity.this, R.anim.shake);
					rl_registerinfo_takeheadimage.startAnimation(shake);
					// Toast.makeText(RegisterHeadActivity.this, "请选择头像",
					// Toast.LENGTH_SHORT).show();
					// ToastUtil.showtextshort("请选择头像");
					return;
				}
				if (registerinfo_edit_name.getText().toString().equals("")) {
					initToast("请输入用户名");
					Animation shake = AnimationUtils.loadAnimation(
							RegisterHeadActivity.this, R.anim.shake);
					registerinfo_edit_name.startAnimation(shake);
					// Toast.makeText(RegisterHeadActivity.this, "请填写名字",
					// Toast.LENGTH_SHORT).show();
					// ToastUtil.showtextshort("请填写名字");
					return;
				}

				// if (birthday == 0) {
				// initToast("请选择生日");
				// return;
				// }

				NotificationUtil
						.calltop(RegisterHeadActivity.this, "照片发送中....");
				String path = FileURl.LOAD_FILE + FileURl.GoodsIamgeURL
						+ FileURl.IMAGE_NAME;
				ImageLoader.getInstance().loadImage(path,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub

								String str = PictureUtil.save(loadedImage);
								loadedImage.recycle();

								List<NameValuePair> params = new ArrayList<NameValuePair>();
								try {
									// if (userStatus.equals("0")) {
									// 注册
									params.add(new BasicNameValuePair("source",
											"0"));
									params.add(new BasicNameValuePair(
											"user_name", userEmail));
									params.add(new BasicNameValuePair(
											"password", passWord));
									// } else {
									// // 修改
									// params.add(new BasicNameValuePair(
									// "user_id", userID));
									//
									// }

									params.add(new BasicNameValuePair("name",
											registerinfo_edit_name.getText()
													.toString()));
									params.add(new BasicNameValuePair("photo",
											str));
									params.add(new BasicNameValuePair(
											"longitude", longitude));
									params.add(new BasicNameValuePair(
											"latitude", latitude));
									params.add(new BasicNameValuePair("sex",
											sex));
									params.add(new BasicNameValuePair(
											"birthday", birthday + ""));
								} catch (Exception e) {

								}
								// if (userStatus.equals("0")) {
								// 注册
								Service.getService(Contanst.HTTP_REGISTER,
										null, null, RegisterHeadActivity.this)
										.addList(params)
										.request(UrlParams.POST);
								// } else {
								// // 修改
								// Service.getService(
								// Contanst.HTTP_UPDATEUSERINFO, null,
								// null, RegisterHeadActivity.this)
								// .addList(params)
								// .request(UrlParams.POST);
								// }

								Log.i(  "aaa", "params ======= "
										+ params);
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}
						});
			}
		});

		birthdayLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DatePickerDialog dpd = new DatePickerDialog(
						new ContextThemeWrapper(RegisterHeadActivity.this,
								R.style.CustomDiaLog_by_SongHang),
						Datelistener, 1990, 0, 1);
				dpd.show();
			}
		});

		manCheckLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				manCheck.setBackgroundResource(R.drawable.zhuce_icon1);
				manCheck.setTextColor(getResources().getColor(R.color.meibao_color_3));
//				womanCheck.setBackgroundResource(R.drawable.zhuce_icon2);
				womanCheck.setTextColor(getResources().getColor(R.color.meibao_color_6));
				sex = "1";
			}
		});
		womanCheckLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				manCheck.setBackgroundResource(R.drawable.zhuce_icon2);
				manCheck.setTextColor(getResources().getColor(R.color.meibao_color_6));
//				womanCheck.setBackgroundResource(R.drawable.zhuce_icon1);
				womanCheck.setTextColor(getResources().getColor(R.color.meibao_color_3));
				sex = "0";
			}
		});

		/**
		 * 协议监听
		 */
		// xieyiImage.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent in = new Intent(RegisterHeadActivity.this,
		// XieYiActivity.class);
		// RegisterHeadActivity.this.startActivity(in);
		// }
		// });
		registerinfo_headimage = (ImageView) findViewById(R.id.registerinfo_headimage);
		registerinfo_headimageline = (ImageView) findViewById(R.id.registerinfo_headimageline);
		rl_registerinfo_takeheadimage = (RelativeLayout) findViewById(R.id.rl_registerinfo_takeheadimage);
		rl_registerinfo_takeheadimage
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showsetheaddialog();
					}
				});

		registerinfo_edit_name = (EditText) findViewById(R.id.registerinfo_edit_name);
		registerinfo_edit_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				registerinfo_edit_name.setCursorVisible(true);
				GroupApplication.openKeybord(registerinfo_edit_name, mContext);
				CharSequence text = registerinfo_edit_name.getText();
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
			}
		});
		/*
		 * if(registerinfo_edit_name.getText() != null &&
		 * registerinfo_edit_name.getText().toString().length() > 0)
		 * registerinfo_edit_name.setCursorVisible(false); int nameDp =
		 * DisplayUtil.sp2dp(RegisterHeadActivity.this, (float)(20
		 * *registerinfo_edit_name.getText().toString().length()));
		 * RelativeLayout.LayoutParams tl_name = new
		 * RelativeLayout.LayoutParams(
		 * RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout
		 * .LayoutParams.WRAP_CONTENT);
		 * tl_name.addRule(RelativeLayout.CENTER_IN_PARENT);
		 * registerinfo_edit_name.setLayoutParams(tl_name);
		 * 
		 * RelativeLayout.LayoutParams tl_iv_name = new
		 * RelativeLayout.LayoutParams
		 * (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout
		 * .LayoutParams.WRAP_CONTENT);
		 * tl_iv_name.addRule(RelativeLayout.CENTER_VERTICAL);
		 * tl_iv_name.addRule(RelativeLayout.RIGHT_OF,
		 * R.id.registerinfo_edit_name);
		 * iv_registerinfo_name.setLayoutParams(tl_iv_name);
		 */
		/**
		 * 根据是否注册标示展示界面
		 */
		// if (!isRegister) {
		// xieyiLayout.setVisibility(View.GONE);
		// iv_registerinfo_name.setVisibility(View.VISIBLE);
		// registerinfo_edit_name.setText(userName);
		// registerinfo_edit_name.setCursorVisible(false);
		// registerinfo_headimageline
		// .setBackgroundResource(R.drawable.takehead_finish);
		// ImageLoader.getInstance().displayImage(UrlParams.IP + userPhoto,
		// registerinfo_headimage, options);
		// } else {
		registerinfo_headimageline.setBackgroundResource(R.drawable.takehead);
		// }

		registerinfo_edit_name.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private boolean isEdit = true;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				temp = s;
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				selectionStart = registerinfo_edit_name.getSelectionStart();
				selectionEnd = registerinfo_edit_name.getSelectionEnd();
				Log.i(  "gongbiao1", "" + selectionStart);

				// registerinfo_edit_name.getText().toString();

				if (Tools.getLineSize(registerinfo_edit_name.getText()
						.toString()) > 16) {
					// initToast("字数超限");
					// Toast.makeText(RegisterHeadActivity.this, "字数超限",
					// Toast.LENGTH_SHORT).show();
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					registerinfo_edit_name.setText(s);
					registerinfo_edit_name.setSelection(tempSelection);
				}
				String curName = registerinfo_edit_name.getText().toString();
				if (curName != null && curName.length() > 0) {
					isUserName = true;
					if (isHead) {
						registerFinish
								.setBackgroundResource(R.drawable.shape_color1);
						registerFinish.setTextColor(getResources().getColor(
								R.color.white));
					}
				} else {
					isUserName = false;
					registerFinish
							.setBackgroundResource(R.drawable.shape_member);
					registerFinish.setTextColor(getResources().getColor(
							R.color.meibao_color_3));
				}
			}

		});

	}

	long birthday = 631123200;// 默认时间1990-01-01
	private DatePickerDialog.OnDateSetListener Datelistener;
	{
		Datelistener = new DatePickerDialog.OnDateSetListener() {
			/**
			 * params：view：该事件关联的组件 params：myyear：当前选择的年
			 * params：monthOfYear：当前选择的月 params：dayOfMonth：当前选择的日
			 */
			@Override
			public void onDateSet(DatePicker view, int myyear, int monthOfYear,
					int dayOfMonth) {

				birthdayText.setText(myyear
						+ "-"
						+ ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1)
								: monthOfYear) + "-"
						+ (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth));
				try {
					birthday = new SimpleDateFormat("yyyy-MM-dd")
							.parse(myyear + "-" + (monthOfYear + 1) + "-"
									+ dayOfMonth).getTime();
					if (birthday > System.currentTimeMillis()) {
						// 选择今天之后的日期
						birthday = System.currentTimeMillis();
						birthdayText.setText(TimeUtil.getDateTime(new Date(
								birthday)));
					}

					view.setMaxDate(System.currentTimeMillis());

				} catch (ParseException e) {
					e.printStackTrace();
				}
				updateDate();

			}

			// 当DatePickerDialog关闭时，更新日期显示
			private void updateDate() {
				// 在TextView上显示日期
				// showdate.setText("当前日期："+year+"-"+(month+1)+"-"+day);
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i(  "aaa", "Activity.RESULT_OK === "
				+ Activity.RESULT_OK);
		Log.i(  "aaa", "requestCode === " + resultCode);

		if (resultCode == RESULT_OK) {
			if (requestCode == ChatEasemobActivity.REQUEST_CODE_LOCAL) {
				if (data != null) {
					startPicCut(data.getData());
					// Uri selectedImage = data.getData();
					// if (selectedImage != null) {
					// // sendPicByUri(selectedImage);
					// }
				}
			} else if (requestCode == ChatEasemobActivity.REQUEST_CODE_CAMERA) {
				startPicCut(data.getData());

			} else if (requestCode == ChatEasemobActivity.REQUEST_CLIP_PHOTO) {
				if (data != null) {
					Uri selectedImage = data.getData();
					ContentResolver resolver = getContentResolver();
					Bitmap photo = null;
					try {
						if (selectedImage != null) {
							// // sendPicByUri(selectedImage);
							photo = MediaStore.Images.Media.getBitmap(resolver,
									selectedImage);
							registerinfo_headimage.setImageBitmap(photo);

						} else {
							Bundle extras = data.getExtras();
							if (extras != null) {
								// 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
								photo = extras.getParcelable("data");
								if (photo != null) {
									registerinfo_headimage
											.setImageBitmap(photo);
								}
							}

						}

						String filepath = FileURl.GoodsIamgeURL
								+ FileURl.IMAGE_NAME;
						File file = new File(filepath);
						if (file.exists()) {
							file.mkdirs();
						}
						BufferedOutputStream bos;
						bos = new BufferedOutputStream(new FileOutputStream(
								file));
						photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩的流里面
						bos.flush();// 刷新此缓冲区的输出流
						bos.close();// 关闭此输出流并释放与此流有关的所有系统资源

						ImageLoader.getInstance().clearMemoryCache();
						String path = FileURl.LOAD_FILE + FileURl.GoodsIamgeURL
								+ FileURl.IMAGE_NAME;
						ImageLoader.getInstance().displayImage(path,
								registerinfo_headimage, options);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				isHead = true;
				if (isUserName) {
					registerFinish
							.setBackgroundResource(R.drawable.shape_color1);
					registerFinish.setTextColor(getResources().getColor(
							R.color.white));
				}

			}
		}
//		if (resultCode == 444 || resultCode == 111) {
//			Log.i(  "aaa", "resultCoderesultCoderesultCode === "
//					+ resultCode);
//			isHead = true;
//			String sdStatus = Environment.getExternalStorageState();
//			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//				return;
//			}
//
//			String path = FileURl.LOAD_FILE + FileURl.GoodsIamgeURL
//					+ FileURl.IMAGE_NAME;
//			Log.i(  "aaa", "path ======== " + path);
//			ImageLoader.getInstance().displayImage(path,
//					registerinfo_headimage, options);
//			// registerinfo_headimageline
//			// .setBackgroundResource(R.drawable.takehead_finish);
//
//			if (isUserName) {
//				registerFinish
//						.setBackgroundResource(R.drawable.shape_finish_select_btn);
//				registerFinish.setTextColor(getResources().getColor(
//						R.color.meibao_color_1));
//			}
//		}

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

				if (msg.arg1 == Contanst.UPDATEUSERINFO) {
					UserBean userBean = (UserBean) msg.obj;
					SharedPreferences settings = RegisterHeadActivity.this
							.getBaseContext()
							.getSharedPreferences(
									((GroupApplication) (RegisterHeadActivity.this
											.getApplicationContext()))
											.getCurSP(), 0);
					Editor edit = settings.edit();
					edit.putString("userID", userBean.getId());
					edit.putString("userName", userBean.getName());
					edit.putString("userSex", userBean.getSex());
					edit.putLong("birthday", userBean.getBirthday());
					edit.putString("userPhoto", userBean.getPhoto());
					edit.putString("isSee", userBean.getIsSee());
					edit.putString("token", userBean.getToken());
					edit.putString("integral", userBean.getIntegral());
					edit.putString("tnum", userBean.getTnum());
					edit.putString("pnum", userBean.getPnum());
					edit.putString("userEmail", userEmail);
					edit.putString("password", passWord);
					edit.commit();

					RegisterHeadActivity.this.finish();
					overridePendingTransition(R.anim.activity_come_in,
							R.anim.activity_come_out_bottom);
				} else if (msg.arg1 == Contanst.REGISTER) {

					((GroupApplication) (RegisterHeadActivity.this
							.getApplicationContext()))
							.setCurSP(Contanst.UserStone);

					SharedPreferences settings = RegisterHeadActivity.this
							.getBaseContext().getSharedPreferences(
									Contanst.UserStone, 0);
					Contanst.isLogin = true;
					UserBean userBean = (UserBean) msg.obj;
					Editor edit = settings.edit();
					edit.putString("userID", userBean.getId());
					edit.putString("userName", userBean.getName());
					edit.putString("password", passWord);
					edit.putString("userPhoto", userBean.getPhoto());
					edit.putString("alias", userBean.getPushid());
					edit.putString("userSex", userBean.getSex());
					edit.putLong("birthday", userBean.getBirthday());
					edit.putString("isSee", userBean.getIsSee());
					edit.putString("token", userBean.getToken());
					edit.putString("integral", userBean.getIntegral());
					edit.putString("tnum", userBean.getTnum());
					edit.putString("pnum", userBean.getPnum());
					edit.putString("userEmail", userEmail);

					edit.commit();

					// 注册成功。
					SharedPreferences setting = RegisterHeadActivity.this
							.getBaseContext().getSharedPreferences(
									Contanst.SystemStone, 0);
					Editor edit1 = setting.edit();
					edit1.putBoolean("isAutoLogin", true);
					edit1.commit();

					SharedPreferences settings1;
					settings1 = RegisterHeadActivity.this.getBaseContext()
							.getSharedPreferences(Contanst.SystemStone, 0);
					Editor editor = settings1.edit();
					editor.putBoolean("isFirstUp", true);
					editor.commit();
					registerEasemob(userBean.getId(), userBean.getName());

				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.UPDATEUSERINFO) {

				}
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				initToast(message);
				// Toast.makeText(RegisterHeadActivity.this, message,
				// Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

	public void loginEasemob(final String userName, final String userNickName) {

		EMChatManager.getInstance().login(userName, "123", new EMCallBack() {// 回调
					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {
							public void run() {

								EMChatManager.getInstance()
										.updateCurrentUserNick(userNickName);

								EMChatManager.getInstance()
										.loadAllConversations();
								Log.i(  "bbb", "登陆聊天服务器成功！");

								Intent intentdata = new Intent();
								setResult(222, intentdata);
								RegisterHeadActivity.this.finish();
								overridePendingTransition(
										R.anim.activity_come_in,
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
					}
				});
	}

	public void registerEasemob(final String userName, final String userNickName) {
		new Thread(new Runnable() {

			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(userName,
							"123");
					loginEasemob(userName, userNickName);
				} catch (final EaseMobException e) {
					// 注册失败
					String errorMsg = e.getMessage();
					int errorCode = e.getErrorCode();
					if (errorCode == EMError.NONETWORK_ERROR) {
						Toast.makeText(getApplicationContext(), "网络异常，请检查网络！",
								Toast.LENGTH_SHORT).show();
					} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
						Toast.makeText(getApplicationContext(), "用户已存在！",
								Toast.LENGTH_SHORT).show();
					} else if (errorCode == EMError.UNAUTHORIZED) {
						Toast.makeText(getApplicationContext(), "注册失败，无权限！",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"注册失败: " + e.getMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		}).start();
	}

	/**
	 * 容错提示框
	 * 
	 * @param sToast
	 */
	private void initToast(String sToast) {
		View toastRoot = getLayoutInflater().inflate(R.layout.toast, null);
		toastRoot.getBackground().setAlpha(209);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(sToast);

		Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	// *显示dialog*/
	private void showsetheaddialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(RegisterHeadActivity.this,
						R.style.CustomDiaLog_by_SongHang));
		builder.setItems(new String[] { "拍照", "从相册选择", "取消" },
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
//							opencamera();
							selectPicFromCamera();
							break;
						case 1:
							selectPicFromLocal();
							break;
						default:
							break;
						}
					}

					/* 打开相机 */
					private void opencamera() {
						Intent cameraIntent = new Intent(
								RegisterHeadActivity.this, CameraActivity.class);
						cameraIntent.putExtra(CameraActivity.IsRegister, "1");
						startActivityForResult(cameraIntent,
								CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					}
				});
		builder.show();
	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(
					R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}
		//
		// cameraFile = new File(PathUtil.getInstance().getImagePath(),
		// GroupApplication.getInstance().getUserName()
		// + System.currentTimeMillis() + ".jpg");
		// cameraFile.getParentFile().mkdirs();
		// startActivityForResult(new
		// Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,
		// Uri.fromFile(cameraFile)),
		// ChatEasemobActivity.REQUEST_CODE_CAMERA);

		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camera, ChatEasemobActivity.REQUEST_CODE_CAMERA);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ChatEasemobActivity.REQUEST_CODE_LOCAL);
	}

	/**
	 * 裁剪图片的方法
	 * 
	 * @param uri
	 */
	public void startPicCut(Uri uri) {
		Intent intentCarema = new Intent("com.android.camera.action.CROP");
		intentCarema.setDataAndType(uri, "image/*");
		intentCarema.putExtra("crop", true);
		intentCarema.putExtra("scale", false);
		intentCarema.putExtra("noFaceDetection", true);// 不需要人脸识别功能
		// intentCarema.putExtra("circleCrop", "");//设定此方法选定区域会是圆形区域
		// aspectX aspectY是宽高比例
		intentCarema.putExtra("aspectX", 1);
		intentCarema.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片的宽高
		intentCarema.putExtra("outputX", 150);
		intentCarema.putExtra("outputY", 150);
		intentCarema.putExtra("return-data", true);
		startActivityForResult(intentCarema,
				ChatEasemobActivity.REQUEST_CLIP_PHOTO);
	}

}