package com.yktx.group;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.fb.FeedbackAgent;
import com.yktx.bean.UserBean;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.BlurUtil;
import com.yktx.util.Contanst;
import com.yktx.util.FileURl;
import com.yktx.util.NotificationUtil;
import com.yktx.util.PictureUtil;
import com.yktx.util.TimeUtil;
import com.yktx.util.Tools;

/**
 * 个人中心 Created by songhang on 2014/4/12.
 */
@SuppressLint({ "ResourceAsColor", "NewApi" })
public class UserCenterActivity extends BaseActivity implements ServiceListener {

	private RelativeLayout registerinfo_backgroud;
	ImageView registerinfo_headimage, registerinfo_headimageline,
			iv_registerinfo_name;
	EditText registerinfo_edit_name;
	RelativeLayout EdittextLayout;
	private RelativeLayout rl_leftImage, rl_registerinfo_takeheadimage;

	LinearLayout fenOrAttentionLayout;

	public static final int STATE_BAOBAO = 2, STATE_HUAIYUN = 1,
			STATE_DAIYUN = 0;
	private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;

	ImageView titleLeftImage, titleRightImage, usercenter_opinion,
			userCenterAttentionButton, gerenzhuye_bg;
	TextView centerUserAttention, centerUserFans, updatePassword;
	private SharedPreferences settings;
	private String userName, userPhoto, attentionNum, fansNum;
	TextView centerUserAge, centerUserLevel, userCenterFinish;
	View userCenterLine, userCenterVerticalLine;
	LinearLayout fansLayout, attentionLayout;

	boolean isUserLogin;
	/**
	 * 是否关注
	 */
	String isAttention;

	boolean isUpdatePhoto = false;

	boolean isUpdateUserName = false;
	/**
	 * 是否是自己
	 */
	boolean isSelf;
	String userID = "";
	String myUserID;
	FeedbackAgent agent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isUpdatePhoto = false;
		isUpdateUserName = false;
		agent = new FeedbackAgent(UserCenterActivity.this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		options = new DisplayImageOptions.Builder().showImageOnLoading(null)
				.showImageOnFail(null).showImageForEmptyUri(R.drawable.addhead)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(450))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		settings = UserCenterActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);
		myUserID = settings.getString("userID", "null");
		userName = settings.getString("userName", "null");
		userID = getIntent().getStringExtra("userID");

		if (userID == null
				|| GroupApplication.getInstance().curSP
						.equals(Contanst.UserStone) && userID.equals(myUserID)) {
			// 自己
			isSelf = true;
			userID = myUserID;
		} else {
			isSelf = false;
		}
		connGetUserInfo();
		userPhoto = settings.getString("userPhoto", "0");
		if ("null".equals(userPhoto))
			userPhoto = "";

		setContentView(R.layout.user_center_layout);
		initview();
		initTitle();
	}

	private void connGetUserInfo() {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {

			params.add(new BasicNameValuePair("myuser_id", myUserID));
			if (!isSelf)
				params.add(new BasicNameValuePair("bruser_id", userID));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETUSERINFO, null, null,
				UserCenterActivity.this).addList(params)
				.request(UrlParams.POST);
		Log.i(  "aaa", "myUserID =========== " + myUserID);
	}

	private void connAttention(String type) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {

			params.add(new BasicNameValuePair("tuser_id", myUserID));
			params.add(new BasicNameValuePair("puser_id", userID));
			params.add(new BasicNameValuePair("is_delete", type));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_ATTENTION, null, null,
				UserCenterActivity.this).addList(params)
				.request(UrlParams.POST);
		Log.i(  "aaa", "myUserID =========== " + myUserID);
	}

	private void initTitle() {

		titleLeftImage = (ImageView) findViewById(R.id.leftImage);
		titleLeftImage.setImageResource(R.drawable.zhuce_back);
		TextView leftText = (TextView) findViewById(R.id.left);
		TextView rightText = (TextView) findViewById(R.id.right);
		titleRightImage = (ImageView) findViewById(R.id.rightImage);

		if (isSelf) {
			leftText.setVisibility(View.VISIBLE);
			leftText.setText("注销");
			titleLeftImage.setVisibility(View.GONE);
			titleRightImage.setVisibility(View.GONE);
			rightText.setVisibility(View.VISIBLE);
			rightText.setText("联系客服小妹");
		} else {
			leftText.setVisibility(View.GONE);
			titleLeftImage.setVisibility(View.VISIBLE);
			titleRightImage.setVisibility(View.VISIBLE);
			rightText.setVisibility(View.GONE);
			titleRightImage.setImageResource(R.drawable.wode_sixin);
		}
		// rl_leftImage = (RelativeLayout) findViewById(R.id.rl_leftImage);
	
	
		TextView text = (TextView) findViewById(R.id.title);
		text.setVisibility(View.GONE);

		centerUserAttention = (TextView) findViewById(R.id.centerUserAttention);
		centerUserFans = (TextView) findViewById(R.id.centerUserFans);

		
		leftText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 返回
				// GroupApplication.closeKeybord(registerinfo_edit_name,
				// mContext);
				// UserCenterActivity.this.finish();
				showdialogCancelLogin();
			}
		});

		titleLeftImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 返回
				GroupApplication.closeKeybord(registerinfo_edit_name, mContext);
				UserCenterActivity.this.finish();
			}
		});

		
		titleRightImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					// "+私信"
					Intent intent = new Intent(UserCenterActivity.this,
							ChatEasemobActivity.class);
					intent.putExtra("userId", userID);
					intent.putExtra("myNickName",
							settings.getString("userName", ""));
					intent.putExtra("myHead",
							UrlParams.IP + settings.getString("userPhoto", ""));
					intent.putExtra("myUserID", myUserID);
					intent.putExtra("chatType",
							ChatEasemobActivity.CHATTYPE_SINGLE);
					startActivity(intent);
			}
		});
		
		rightText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					// 联系客服小妹
					Intent intent = new Intent(UserCenterActivity.this,
							ChatEasemobActivity.class);
					intent.putExtra("userId", "11959");
					intent.putExtra("myNickName",
							settings.getString("userName", ""));
					intent.putExtra("myHead",
							UrlParams.IP + settings.getString("userPhoto", ""));
					intent.putExtra("myUserID", myUserID);
					intent.putExtra("chatType",
							ChatEasemobActivity.CHATTYPE_SINGLE);
					startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(msgReceiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// connGetUserInfo();
	}

	private void initview() {

		centerUserAge = (TextView) findViewById(R.id.centerUserAge);
		centerUserLevel = (TextView) findViewById(R.id.centerUserLevel);
		usercenter_opinion = (ImageView) findViewById(R.id.usercenter_opinion);
		updatePassword = (TextView) findViewById(R.id.updatePassword);
		gerenzhuye_bg = (ImageView) findViewById(R.id.gerenzhuye_bg);
		userCenterLine = (View) findViewById(R.id.userCenterLine);
		userCenterVerticalLine = findViewById(R.id.userCenterVerticalLine);
		fenOrAttentionLayout = (LinearLayout) findViewById(R.id.fenOrAttentionLayout);
		userCenterLine.setVisibility(View.GONE);
		if (!isSelf) {
			usercenter_opinion.setVisibility(View.GONE);
			updatePassword.setVisibility(View.GONE);
		}
		usercenter_opinion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 意见反馈
				agent.sync();
				agent.startFeedbackActivity();
			}
		});
		// 修改成退出登录
		updatePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent in = new Intent(UserCenterActivity.this,
				// UpdatePasswordActivity.class);
				// UserCenterActivity.this.startActivity(in);
				showdialogCancelLogin();
			}
		});

		registerinfo_headimage = (ImageView) findViewById(R.id.registerinfo_headimage);
		registerinfo_headimageline = (ImageView) findViewById(R.id.registerinfo_headimageline);
		rl_registerinfo_takeheadimage = (RelativeLayout) findViewById(R.id.rl_registerinfo_takeheadimage);
		rl_registerinfo_takeheadimage
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (isSelf) {
							showsetheaddialog();
						}
					}
				});

		registerinfo_backgroud = (RelativeLayout) findViewById(R.id.registerinfo_backgroud);
		registerinfo_edit_name = (EditText) findViewById(R.id.registerinfo_edit_name);
		EdittextLayout = (RelativeLayout) findViewById(R.id.EdittextLayout);
		if (!isSelf) {
			registerinfo_edit_name.setEnabled(false);
		}
		iv_registerinfo_name = (ImageView) findViewById(R.id.iv_registerinfo_name);
		userCenterAttentionButton = (ImageView) findViewById(R.id.userCenterAttentionButton);
		userCenterFinish = (TextView) findViewById(R.id.userCenterFinish);

		userCenterFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showdialogCancelLogin();
			}

		});

		iv_registerinfo_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (!isUpdateUserName && !isUpdatePhoto) {
					if (!isUpdateUserName) {
						EdittextLayout.clearFocus();
						EdittextLayout.setFocusable(false);
						EdittextLayout.setFocusableInTouchMode(false);
						GroupApplication.openKeybord(registerinfo_edit_name,
								UserCenterActivity.this);
						registerinfo_edit_name.setFocusable(true);
						registerinfo_edit_name.setFocusableInTouchMode(true);
						registerinfo_edit_name.requestFocus();

						iv_registerinfo_name
								.setBackgroundResource(R.drawable.user_center_finish);
						userCenterVerticalLine.setVisibility(View.VISIBLE);
						userCenterLine.setVisibility(View.VISIBLE);
					}
					return;
				}

				if (registerinfo_edit_name.getText().toString().equals("")) {
					initToast("请输入用户名");
					Animation shake = AnimationUtils.loadAnimation(
							UserCenterActivity.this, R.anim.shake);
					registerinfo_edit_name.startAnimation(shake);
					// Toast.makeText(RegisterHeadActivity.this, "请填写名字",
					// Toast.LENGTH_SHORT).show();
					// ToastUtil.showtextshort("请填写名字");
					return;
				}

				if (isUpdatePhoto) {

					NotificationUtil.calltop(UserCenterActivity.this,
							"照片发送中....");
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

										params.add(new BasicNameValuePair(
												"user_id", myUserID));
										params.add(new BasicNameValuePair(
												"name", registerinfo_edit_name
														.getText().toString()));
										params.add(new BasicNameValuePair(
												"photo", str));
									} catch (Exception e) {

									}
									Service.getService(
											Contanst.HTTP_UPDATEUSERINFO, null,
											null, UserCenterActivity.this)
											.addList(params)
											.request(UrlParams.POST);

									Log.i(  "aaa",
											"params ======= " + params);
								}

								@Override
								public void onLoadingCancelled(String imageUri,
										View view) {
									// TODO Auto-generated method stub

								}
							});
				} else {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					try {

						params.add(new BasicNameValuePair("user_id", myUserID));
						params.add(new BasicNameValuePair("name",
								registerinfo_edit_name.getText().toString()));
					} catch (Exception e) {

					}
					Service.getService(Contanst.HTTP_UPDATEUSERINFO, null,
							null, UserCenterActivity.this).addList(params)
							.request(UrlParams.POST);
				}
			}
		});

		userCenterAttentionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!GroupApplication.getInstance().curSP
						.equals(Contanst.UserStone)) {
					showdialogGoRegister();
				} else if (!isUserLogin) {
					Toast.makeText(UserCenterActivity.this, "游客不能关注",
							Toast.LENGTH_SHORT).show();
				} else {
					if (isAttention.equals("0")) {
						connAttention("1");
					} else {
						connAttention("0");
					}
				}
			}
		});

		/*
		 * /** 根据是否注册标示展示界面
		 */
		if (!isSelf) {
			// registerinfo_edit_name.setText(userName);
			registerinfo_edit_name.setCursorVisible(false);
			registerinfo_headimageline.setVisibility(View.GONE);
			userCenterAttentionButton.setVisibility(View.VISIBLE);
			iv_registerinfo_name.setVisibility(View.GONE);
			userCenterFinish.setVisibility(View.GONE);
		} else {
			iv_registerinfo_name.setVisibility(View.VISIBLE);
			// iv_registerinfo_name.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// if(!isUpdateUserName && !isUpdatePhoto){
			// //
			// GroupApplication.getInstance().openKeybord(registerinfo_edit_name,
			// mContext);
			// registerinfo_edit_name.setFocusable(true);
			// registerinfo_edit_name.setFocusableInTouchMode(true);
			// GroupApplication.getInstance().openKeybord(registerinfo_edit_name,
			// mContext);
			// }
			// }
			// });
			registerinfo_headimageline
					.setBackgroundResource(R.drawable.takehead_finish);
			userCenterAttentionButton.setVisibility(View.GONE);
			userCenterFinish.setVisibility(View.GONE);// 是自己的时候隐藏关注下的Textview
		}

		fansLayout = (LinearLayout) findViewById(R.id.fansLayout);
		attentionLayout = (LinearLayout) findViewById(R.id.attentionLayout);
		attentionLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(UserCenterActivity.this,
						AttentionActivity.class);
				in.putExtra("isAttention", true);
				in.putExtra("otherID", userID);
				startActivity(in);
			}
		});
		fansLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(UserCenterActivity.this,
						AttentionActivity.class);
				in.putExtra("isAttention", false);
				in.putExtra("otherID", userID);
				startActivity(in);
			}
		});

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
				// registerinfo_edit_name.getText().toString();
				if (isSelf) {
					if (Tools.getLineSize(registerinfo_edit_name.getText()
							.toString()) > 16) {
						initToast("字数超限");
						// Toast.makeText(RegisterHeadActivity.this, "字数超限",
						// Toast.LENGTH_SHORT).show();
						s.delete(selectionStart - 1, selectionEnd);
						int tempSelection = selectionStart;
						registerinfo_edit_name.setText(s);
						registerinfo_edit_name.setSelection(tempSelection);
					}

					if (!userName.equals(s + "")) {
						iv_registerinfo_name
								.setBackgroundResource(R.drawable.user_center_finish);
						userCenterVerticalLine.setVisibility(View.VISIBLE);
						userCenterLine.setVisibility(View.VISIBLE);
						isUpdateUserName = true;
					} else {
						isUpdateUserName = false;
						if (!isUpdatePhoto)
							iv_registerinfo_name
									.setBackgroundResource(R.drawable.update_userinfo);
						userCenterVerticalLine.setVisibility(View.GONE);
						userCenterLine.setVisibility(View.GONE);
					}
				}
			}

		});

		registerinfo_edit_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// iv_registerinfo_name.setVisibility(View.GONE);
				registerinfo_edit_name.setCursorVisible(true);
				CharSequence text = registerinfo_edit_name.getText();
				if (text instanceof Spannable) {
					Spannable spanText = (Spannable) text;
					Selection.setSelection(spanText, text.length());
				}
			}
		});
	}

	private void showdialogGoRegister() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(UserCenterActivity.this,
						R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("游客身份禁止此项操作");
		builder.setPositiveButton("去注册", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GroupApplication.getInstance().backHomeActivity();
				Intent intentdata = new Intent(
						GroupApplication.getInstance().activityList.get(0),
						RegisterActivity.class);
				GroupApplication.getInstance().activityList.get(0)
						.startActivity(intentdata);

			}
		});
		builder.setNegativeButton("我知道了", null);
		builder.show();
	}

	private void showdialogCancelLogin() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("确定要注销当前用户？");
		builder.setPositiveButton("注销", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Contanst.isLogin = false;
				((GroupApplication) (UserCenterActivity.this
						.getApplicationContext()))
						.setCurSP(Contanst.TouristStone);
				SharedPreferences setting = UserCenterActivity.this
						.getBaseContext().getSharedPreferences(
								Contanst.SystemStone, 0);
				Editor edit = setting.edit();
				// 需要判断是否是新用户isAutoLogin
				edit.putBoolean("isAutoLogin", false);
				edit.commit();

				// 极光注销用户，只能收到游客消息
				SharedPreferences settings = getBaseContext()
						.getSharedPreferences(Contanst.TouristStone, 0);
				String alias = settings.getString("alias", "");
				Log.i(  "aaa", "BaseActivity alias == " + alias);
				JPushInterface.setAliasAndTags(getApplicationContext(), alias,
						null, mAliasCallback);

				EMChatManager.getInstance().logout();// 此方法为同步方法
				Intent in = new Intent(UserCenterActivity.this,
						LoginChoiceActivity.class);
				UserCenterActivity.this.startActivity(in);
				UserCenterActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

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
							setBg(photo);
						} else {
							Bundle extras = data.getExtras();
							if (extras != null) {
								// 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
								photo = extras.getParcelable("data");
								if (photo != null) {
									registerinfo_headimage
											.setImageBitmap(photo);

									setBg(photo);
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
						ImageLoader.getInstance().loadImage(path,
								new ImageLoadingListener() {

									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onLoadingFailed(String arg0,
											View arg1, FailReason arg2) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onLoadingComplete(String arg0,
											View arg1, Bitmap arg2) {
										// TODO Auto-generated method stub

										setBg(arg2);
									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {
										// TODO Auto-generated method stub

									}
								});

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				isUpdatePhoto = true;
				iv_registerinfo_name
						.setBackgroundResource(R.drawable.user_center_finish);

			}
		}

		// if (resultCode == 444 || resultCode == 111) {
		// ImageLoader.getInstance().clearMemoryCache();
		// String sdStatus = Environment.getExternalStorageState();
		// if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
		// return;
		// }
		//
		// String path = FileURl.LOAD_FILE + FileURl.GoodsIamgeURL
		// + FileURl.IMAGE_NAME;
		// ImageLoader.getInstance().displayImage(path,
		// registerinfo_headimage, options);
		// // registerinfo_headimageline
		// // .setBackgroundResource(R.drawable.takehead_finish);
		// // registerinfo_headimage.setImageResource(R.drawable.image_null);
		// //
		// registerinfo_headimage.setImageBitmap(BitmapFactory.decodeFile(path));
		// isUpdatePhoto = true;
		// iv_registerinfo_name
		// .setBackgroundResource(R.drawable.user_center_finish);
		// }

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

				if (msg.arg1 == Contanst.GETUSERINFO) {

					UserBean userBean = (UserBean) msg.obj;

					if (userBean.getStatus().equals("0")) {
						// 游客
						fenOrAttentionLayout.setVisibility(View.GONE);
						userCenterAttentionButton.setVisibility(View.GONE);
					}

					centerUserAttention.setText(userBean.getTnum());
					centerUserFans.setText(userBean.getPnum());
					registerinfo_edit_name.setText(userBean.getName());
					if (userBean.getStatus().equals("0")) {
						isUserLogin = false;
						centerUserAge.setVisibility(View.GONE);
						centerUserLevel.setVisibility(View.GONE);
					} else {
						isUserLogin = true;
						if (userBean.getSex().equals("1")) {
							centerUserAge
									.setBackgroundResource(R.drawable.shape_age_full_man);
							// centerUserAge.setTextColor(getResources().getColor(
							// R.color.meibao_color_9));
						} else {
							centerUserAge
									.setBackgroundResource(R.drawable.shape_age_full_woman);
							// centerUserAge.setTextColor(getResources().getColor(
							// R.color.meibao_age_girl_color));
						}
						try {
							centerUserAge.setText(TimeUtil.getAge(new Date(
									userBean.getBirthday())) + "岁");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						centerUserLevel.setText("Lv" + userBean.getIntegral());

						isAttention = userBean.getIs_att();
						if (isAttention.equals("0")) {
							userCenterAttentionButton
									.setImageResource(R.drawable.wode_quxiaoguanzhu);
						} else {
							userCenterAttentionButton
									.setImageResource(R.drawable.wode_guanzhu);
						}
					}
					if (!isUpdatePhoto) {
						ImageLoader.getInstance().displayImage(
								UrlParams.IP + userBean.getPhoto(),
								registerinfo_headimage, options);
						// ImageLoader.getInstance().displayImage(
						// UrlParams.IP + userBean.getPhoto(),
						// gerenzhuye_bg);

						ImageLoader.getInstance().loadImage(
								UrlParams.IP + userBean.getPhoto(),
								new ImageLoadingListener() {

									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onLoadingFailed(String arg0,
											View arg1, FailReason arg2) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onLoadingComplete(String arg0,
											View arg1, Bitmap arg2) {
										// TODO Auto-generated method stub

										setBg(arg2);
									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {
										// TODO Auto-generated method stub

									}
								});

					}
					if (isSelf) {
						Editor edit = settings.edit();
						edit.putString("userName", userBean.getName());
						edit.putString("userSex", userBean.getSex());
						edit.putLong("birthday", userBean.getBirthday());
						edit.putString("userPhoto", userBean.getPhoto());
						edit.putString("isSee", userBean.getIsSee());
						edit.putString("token", userBean.getToken());
						edit.putString("integral", userBean.getIntegral());
						edit.putString("tnum", userBean.getTnum());
						edit.putString("pnum", userBean.getPnum());
						edit.commit();
					}
					registerinfo_edit_name.clearFocus();
					registerinfo_edit_name.setFocusable(false);
					GroupApplication.closeKeybord(registerinfo_edit_name,
							UserCenterActivity.this);
					EdittextLayout.setFocusable(true);
					EdittextLayout.setFocusableInTouchMode(true);
					iv_registerinfo_name
							.setBackgroundResource(R.drawable.update_userinfo);
					userCenterVerticalLine.setVisibility(View.GONE);
					userCenterLine.setVisibility(View.GONE);
					isUpdateUserName = false;
					isUpdatePhoto = false;
				} else if (msg.arg1 == Contanst.ATTENTION) {
					if (isAttention.equals("0")) {
						isAttention = "1";
						int fansNum = Integer.parseInt(centerUserFans.getText()
								.toString());
						centerUserFans.setText(fansNum - 1 + "");
						userCenterAttentionButton
								.setImageResource(R.drawable.wode_guanzhu);
					} else {
						int fansNum = Integer.parseInt(centerUserFans.getText()
								.toString());
						centerUserFans.setText(fansNum + 1 + "");
						isAttention = "0";
						userCenterAttentionButton
								.setImageResource(R.drawable.wode_quxiaoguanzhu);
					}
				} else if (msg.arg1 == Contanst.UPDATEUSERINFO) {

					UserBean userBean = (UserBean) msg.obj;
					Editor edit = settings.edit();
					edit.putString("userName", userBean.getName());
					edit.putString("userSex", userBean.getSex());
					edit.putLong("birthday", userBean.getBirthday());
					edit.putString("userPhoto", userBean.getPhoto());
					edit.putString("isSee", userBean.getIsSee());
					edit.putString("token", userBean.getToken());
					edit.putString("integral", userBean.getIntegral());
					edit.putString("tnum", userBean.getTnum());
					edit.putString("pnum", userBean.getPnum());
					edit.commit();
					Toast.makeText(UserCenterActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();

					registerinfo_edit_name.clearFocus();
					registerinfo_edit_name.setFocusable(false);
					registerinfo_edit_name.setFocusableInTouchMode(false);
					GroupApplication.closeKeybord(registerinfo_edit_name,
							UserCenterActivity.this);
					EdittextLayout.setFocusable(true);
					EdittextLayout.setFocusableInTouchMode(true);
					iv_registerinfo_name
							.setBackgroundResource(R.drawable.update_userinfo);
					userCenterVerticalLine.setVisibility(View.GONE);
					userCenterLine.setVisibility(View.GONE);
					isUpdateUserName = false;
					isUpdatePhoto = false;

					// 开始是不回主页，又要回主页。
					// 现在TMD又不回去了。
					// UserCenterActivity.this.finish();
					// overridePendingTransition(R.anim.activity_come_in,
					// R.anim.activity_come_out_bottom);

				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.GETUSERINFO) {

				} else if (msg.arg1 == Contanst.ATTENTION) {

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

	private void setBg(Bitmap photo) {
		try {
			final Bitmap blurBmp = BlurUtil.fastblur(UserCenterActivity.this,
					photo, 25);
			if (blurBmp != null)
				gerenzhuye_bg.setImageBitmap(blurBmp);
		} catch (Exception e) {

		}
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
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastRoot);
		toast.show();
	}

	// *显示dialog*/
	private void showsetheaddialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(UserCenterActivity.this,
						R.style.CustomDiaLog_by_SongHang));
		builder.setItems(new String[] { "拍照", "从相册选择", "取消" },
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							// opencamera();
							selectPicFromCamera();
							break;
						case 1:
							selectPicFromLocal();
							// Intent intent_gralley = new Intent(
							// UserCenterActivity.this,
							// PhotoActivity.class);
							// intent_gralley.putExtra(CameraActivity.IsRegister,
							// "1");
							// startActivityForResult(intent_gralley,
							// CameraActivity.GRALLEY);

							// Intent intent = new Intent();
							// intent.addCategory(Intent.CATEGORY_OPENABLE);
							// intent.setType("image/*");
							// // 根据版本号不同使用不同的Action
							// if (Build.VERSION.SDK_INT < 19) {
							// intent.setAction(Intent.ACTION_GET_CONTENT);
							// } else {
							// intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
							// }
							//
							// File out = new File(FileURl.ImageFilePath,
							// FileURl.IMAGE_NAME);
							// // out.mkdirs();
							// Uri uri = Uri.fromFile(out);
							// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							// startActivityForResult(intent,
							// GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
							break;
						default:
							break;
						}
					}

					/* 打开相机 */
					private void opencamera() {
						Intent cameraIntent = new Intent(
								UserCenterActivity.this, CameraActivity.class);
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