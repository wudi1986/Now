/**
 * 
 */
package com.yktx.group;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gallety.PhotoActivity;
import com.android.gallety.ShowImageActivity;
import com.clien.ClientMessageHandlerAdapter.Callback;
import com.clien.MinaClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.sso.UMSsoHandler;
import com.yktx.bean.BigImageBean;
import com.yktx.bean.ChatListBean;
import com.yktx.bean.ChatPhotoBean;
import com.yktx.bean.GroupMemberListBean;
import com.yktx.bean.MainHomePageBean;
import com.yktx.bean.UserBean;
import com.yktx.bean.ZanBean;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.facial.Expressions;
import com.yktx.group.adapter.ChatAdapter;
import com.yktx.group.adapter.ChatAdapter.OnClickAtButton;
import com.yktx.group.adapter.ChatAdapter.OnClickJuBaoButton;
import com.yktx.group.adapter.ChatAdapter.OnClickSendErrorButton;
import com.yktx.group.adapter.ChatAdapter.OnClickUserCenterButton;
import com.yktx.group.adapter.ChatAdapter.OnClickZanButton;
import com.yktx.group.adapter.ChatAdapter.OnClickZanCenterButton;
import com.yktx.group.adapter.ChatAdapter.ShowLastItem;
import com.yktx.group.adapter.ChatMemberAdapter;
import com.yktx.group.adapter.ChatPhotoAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.mylistview.XListView;
import com.yktx.mylistview.XListView.IXListViewListener;
import com.yktx.mylistview.XListView.IXListViewOnScroll;
import com.yktx.util.Contanst;
import com.yktx.util.FileURl;
import com.yktx.util.Geohash;
import com.yktx.util.HorizontalListView;
import com.yktx.util.ImageTool;
import com.yktx.util.MyUMSDK;
import com.yktx.util.PictureUtil;
import com.yktx.util.PopShareDialog;
import com.yktx.util.SoundMeter;
import com.yktx.util.TimeUtil;
import com.yktx.util.Tools;
import com.yktx.util.ZanFacialDialog;
import com.yktx.util.ZanFacialDialog.OnClickZanFacialButton;
import com.yktx.view.ChatErrorPopView;
import com.yktx.view.ChatErrorPopView.OnClickQuit;
import com.yktx.view.ChatErrorPopView.OnClickSendAgain;
import com.yktx.view.ChatPopOnLineNumberView;
import com.yktx.view.ChatPopOnLineNumberView.OnOnLineClickBack;
import com.yktx.view.ChatPopOnLineNumberView.OnOnLineClickHeadCenter;
import com.yktx.view.ChatPopView;
import com.yktx.view.ChatPopView.OnClickBack;
import com.yktx.view.ChatPopView.OnClickHeadCenter;
import com.yktx.view.KeyboardLayout;
import com.yktx.view.KeyboardLayout.onKybdsChangeListener;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月7日 下午2:08:07  
 * 类说明 聊天 */
/**
 * @author Administrator
 * 
 */
public class ChatActivity extends BaseActivity implements ServiceListener,
		OnClickListener, Callback {

	/** 筛选条件 */
	int chooseState;
	private final static int LOCAL_ALL = 1;
	private final static int LOCAL_10 = 2;
	private final static int LOCAL_1 = 3;
	private final static int WITH_ME = 4;
	private final static int UNREAD = 5;
	private final static int ONLY_IMAGE = 6;
	private final static int LOCK_PEOPLE = 7;
	/** 筛选菜单偏移量 */

	public final static int CHAT_FLAG_POP_MESSAGE = 1; // 普通消息
	public final static int CHAT_FLAG_AT_MESSATE = 2; // @
	public final static int CHAT_FLAG_FIRST_IMAGE = 3; // 3第一次图片
	public final static int CHAT_FLAG_ZAN_MESSAGE = 4; // @4 赞
	public final static int CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE = 5; // 5图文

	int offX;

	/** 上条消息的时间, 第一次默认最新 */
	public static long lastDate = 0;

	/** 距离发送成功最近的一条消息 */
	public static long curDate = 0;

	/**
	 * 消息总数
	 */
	public static String chatTotalCount;
	RelativeLayout chat_back;
	RelativeLayout chatChooseLayout;
	TextView chat_choose_only_image;
	TextView chat_choose_local_all;
	TextView chat_choose_local_10;
	TextView chat_choose_local_1;
	TextView chat_choose_withme;
	TextView chatChooseButton;
	TextView chat_choose_lock;
	// ImageView chatCamera;
	ImageView chatSmile;
	ImageView chat_up_more_message;
	private Handler mVoiceHandler = new Handler();

	int offY = 0;

	/**
	 * 表情展示
	 */
	GridView facial_gridView;

	String longitude, latitude, userID, userName, userHead, userSex, userLevel;
	long userAge;

	String groupID, groupName;// 是否第一次进群标示 0：是 1：否
	XListView xListView;
	ChatAdapter chatAdapter;
	ArrayList<ChatListBean> chatList = new ArrayList<ChatListBean>(20);
	ArrayList<GroupMemberListBean> groupMembersList = new ArrayList<GroupMemberListBean>();

	ArrayList<ZanBean> zanList = new ArrayList<ZanBean>(2);

	boolean isReflash; // 如果筛选需要刷新list

	/**
	 * 是否为断线重连的状态
	 */
	boolean isReconnect = false;
	boolean isGoCameraOrPicture = false;

	boolean isGetUserInfoConn;

	TextView tv_chatGroupName;
	TextView chatGroupNumber, chatMessageNum;
	LinearLayout chatOnLineLayout;
	HorizontalListView chatHeadList, chatPhotoList;
	/** 显示发送图片list */
	LinearLayout chatPhotoListLayout;

	ChatPhotoAdapter mChatPhotoAdatper;
	Button btn_chatsend;
	EditText ed_chatEdit;
	public static final String GroupName = "GroupName";
	public static final String GroupID = "GroupID";
	public static final String GroupBean = "GroupBean";
	public static final String IsFirst = "IsFirst";
	public static final String IsJump = "IsJump";
	public static final String IsNewGroup = "isNewGroup";
	public static final String AtMyNum = "AtMyNum";
	/**
	 * 在线人数
	 */
	String onlineNum;
	/**
	 * 弹出详情页
	 */
	ChatPopView chatPopView;

	/**
	 * 弹出在线人数
	 */
	ChatPopOnLineNumberView chatPopOnLineNumberView;

	/**
	 * 详情页
	 */
	RelativeLayout popLayout;

	/**
	 * 消息浮层
	 */
	RelativeLayout chatErrorPop;

	SharedPreferences settings;

	boolean isNewGroup = false;
	/** 新消息提示 */
	ImageView newMessage;

	ImageView atListNullImage;
	/**
	 * 聊天按钮
	 */
	ImageView chatVoice;
	TextView chat_talk_button;
	LinearLayout rcChat_popup, voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;

	private SoundMeter mSensor;
	// 是否显示在聊天的状态
	boolean btn_vocie;

	// boolean isFirst;
	// boolean isJump;

	/** 服务器是否登录成功 */
	boolean isMinaConn = false;

	/** 可以选择几张相册图片 */
	int canshownum;

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			// 启用内存缓存
			.displayer(new RoundedBitmapDisplayer(20))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions optionMember = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			// 启用内存缓存
			.displayer(new RoundedBitmapDisplayer(200))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	String atMyNum;
	TextView chatAtTitle;

	public ArrayList<ChatPhotoBean> chatPhotoSendList = new ArrayList<ChatPhotoBean>(
			3);

	private static final int GET_PHOTO_LIST_DESC = 1000;

	/**
	 * 监听弹出框
	 */
	com.yktx.view.KeyboardLayout mainLayout;
	/**
	 * 软键盘上一个状态
	 */
	private int LastState;

	// /**
	// * 第一次进入聊天页面
	// */
	// boolean isFirstInChat = true;

	// Player myPlayer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yktx.group.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		isReflash = true;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		settings = ChatActivity.this.getSharedPreferences(
				(GroupApplication.getInstance()).getCurSP(), 0);
		SharedPreferences setting = ChatActivity.this.getSharedPreferences(
				Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		userID = settings.getString("userID", "-1");
		userName = settings.getString("userName", "-1");
		userHead = settings.getString("userPhoto", "");
		userSex = settings.getString("userSex", "");
		userAge = settings.getLong("birthday", 0);
		userLevel = settings.getString("integral", "");
		mSensor = new SoundMeter();
		isNewGroup = settings.getBoolean(IsNewGroup, false);
		initFacialMap();
		if (isNewGroup) {
			lastDate = 0;
		} else {
			lastDate = System.currentTimeMillis();
		}

		chooseState = LOCAL_ALL;

		groupID = getIntent().getStringExtra(GroupID);
		Log.i(  "bbb", "groupID============= " + groupID);
		groupName = getIntent().getStringExtra(GroupName);
		// isFirst = !getIntent().getBooleanExtra(IsFirst, false);
		// isJump = getIntent().getBooleanExtra(IsJump, false);
		// isFirst = true;
		atMyNum = getIntent().getStringExtra(AtMyNum);
		mainLayout = (com.yktx.view.KeyboardLayout) findViewById(R.id.mainLayout);
		mainLayout.setOnkbdStateListener(new onKybdsChangeListener() {

			@Override
			public void onKeyBoardStateChange(int state) {
				// TODO Auto-generated method stub
				switch (state) {
				case KeyboardLayout.KEYBOARD_STATE_HIDE:
					if (LastState != KeyboardLayout.KEYBOARD_STATE_HIDE) {
						LastState = KeyboardLayout.KEYBOARD_STATE_HIDE;
						if (isGoCameraOrPicture) {
							GroupApplication.getInstance().openKeybord(
									ed_chatEdit, mContext);
							isGoCameraOrPicture = false;
							return;
						}
						if (!isFacialShow) {
							chatPhotoListLayout.setVisibility(View.GONE);
							isGoCameraOrPicture = false;
						}
					}
					break;
				case KeyboardLayout.KEYBOARD_STATE_SHOW:
					if (LastState != KeyboardLayout.KEYBOARD_STATE_SHOW) {
						LastState = KeyboardLayout.KEYBOARD_STATE_SHOW;
						if (chatPhotoListLayout.getVisibility() == View.GONE) {
							if (!isGoCameraOrPicture)
								getChatImageList();
							chatPhotoListLayout.setVisibility(View.VISIBLE);
						}
					}
					break;
				}
			}
		});
		initView();
		initMina(null);
		startSokectTimer();
	}

	public HashMap<String, Object> chatFacialMap = new HashMap<String, Object>();

	private void initFacialMap() {
		for (int i = 0; i < Expressions.expressionImgNames.length; i++) {
			chatFacialMap.put(Expressions.expressionImgNames[i],
					Expressions.expressionImgs[i]);
		}
	}

	/**
	 * @param map
	 */
	private void initMina(final HashMap<String, String> map) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (MinaClient.getMinaClient().connect(ChatActivity.this,
						groupID, userID)) {

					// if (map != null) {
					Message msg = new Message();
					msg.what = Contanst.INIT_MINA_SEND_MESSAGE;
					msg.obj = map;
					mHandler.sendMessage(msg);
					// }
				} else {
					Message msg = new Message();
					msg.what = Contanst.MINA_CONN_FAIL;
					msg.obj = "服务器连接失败";
					mHandler.sendMessage(msg);
				}

			}
		}).start();
		// 建立连接
	}

	private void initView() {
		atListNullImage = (ImageView) findViewById(R.id.atListNullImage);
		xListView = (XListView) findViewById(R.id.listView);
		xListView.setXListViewListener(listener);
		xListView.setIXListViewOnScroll(iXListViewOnScroll);

		xListView.setPullLoadEnable(false);
		chatAdapter = new ChatAdapter(ChatActivity.this);
		chatAdapter.setShowLastItem(showLastItem);
		chatAdapter.setOnClickZanButton(onClickZanButton);
		chatAdapter.setOnClickZanCenterButton(onClickZanCenterButton);
		// chatAdapter.setOnClickVoiceButton(onClickVoiceButton);
		chatAdapter.setOnClickAtButton(onClickAtButton);
		chatAdapter.setOnClickSendErrorButton(onClickSendErrorButton);
		chatAdapter.setOnClickUserCenterButton(onClickUserCenterButton);
		chatAdapter.setOnClickJuBaoButton(onClickJuBaoButton);
		xListView.setAdapter(chatAdapter);

		xListView.setPullLoadEnable(false);
		xListView.setSelection(xListView.getCount() - 1);

		newMessage = (ImageView) findViewById(R.id.new_message);
		chatHeadList = (HorizontalListView) findViewById(R.id.chatHeadList);
		chatPhotoList = (HorizontalListView) findViewById(R.id.chatPhotoList);
		mChatPhotoAdatper = new ChatPhotoAdapter(ChatActivity.this);
		chatPhotoList.setAdapter(mChatPhotoAdatper);

		chatPhotoListLayout = (LinearLayout) findViewById(R.id.chatPhotoListLayout);

		facial_gridView = (GridView) findViewById(R.id.facial_gridView);

		chat_back = (RelativeLayout) findViewById(R.id.chatBack);
		offY = getOffYKeyWord();
		chatChooseLayout = (RelativeLayout) findViewById(R.id.chatChooseLayout);
		chatChooseButton = (TextView) findViewById(R.id.chatChooseButton);
		chat_choose_local_all = (TextView) findViewById(R.id.chat_choose_local_all);
		chat_choose_local_10 = (TextView) findViewById(R.id.chat_choose_local_10);
		chat_choose_local_1 = (TextView) findViewById(R.id.chat_choose_local_1);
		chat_choose_withme = (TextView) findViewById(R.id.chat_choose_withme);
		chat_choose_lock = (TextView) findViewById(R.id.chat_choose_lock);
		chat_choose_only_image = (TextView) findViewById(R.id.chat_choose_only_image);
		tv_chatGroupName = (TextView) findViewById(R.id.chatGroupName);
		chatGroupNumber = (TextView) findViewById(R.id.chatGroupNumber);
		chatMessageNum = (TextView) findViewById(R.id.chatMessageNum);
		chatOnLineLayout = (LinearLayout) findViewById(R.id.chatOnLineLayout);

		// chatCamera = (ImageView) findViewById(R.id.chatCamera);
		chatAtTitle = (TextView) findViewById(R.id.chatAtTitle);
		if (atMyNum == null || atMyNum.equals("0")) {
			chatAtTitle.setVisibility(View.GONE);
		} else {
			chatAtTitle.setVisibility(View.VISIBLE);
			chatAtTitle.setText(atMyNum + "条@信息");
		}
		chat_up_more_message = (ImageView) findViewById(R.id.chat_up_more_message);
		chatSmile = (ImageView) findViewById(R.id.chatSmile);
		tv_chatGroupName.setText(groupName);
		btn_chatsend = (Button) findViewById(R.id.chatSend);
		ed_chatEdit = (EditText) findViewById(R.id.chatEdit);
		popLayout = (RelativeLayout) findViewById(R.id.popLayout);
		chatErrorPop = (RelativeLayout) findViewById(R.id.chatErrorPop);
		chatVoice = (ImageView) findViewById(R.id.chatVoice);
		chat_talk_button = (TextView) findViewById(R.id.chat_talk_button);
		// del_re = (LinearLayout) findViewById(R.id.del_re);
		rcChat_popup = (LinearLayout) findViewById(R.id.rcChat_popup);
		voice_rcd_hint_loading = (LinearLayout) findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_rcding = (LinearLayout) findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_tooshort = (LinearLayout) findViewById(R.id.voice_rcd_hint_tooshort);
		// img1 = (ImageView) this.findViewById(R.id.img1);
		// sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		volume = (ImageView) this.findViewById(R.id.volume);
		/**
		 * 返回
		 */
		chat_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent intentdata = new Intent();
				// setResult(222, intentdata);
				// finish();
				showdialogFinish();
			}
		});

		chatChooseButton.setOnClickListener(this);
		chat_choose_local_all.setOnClickListener(this);
		chat_choose_local_10.setOnClickListener(this);
		chat_choose_local_1.setOnClickListener(this);
		chat_choose_withme.setOnClickListener(this);
		chat_choose_lock.setOnClickListener(this);
		btn_chatsend.setOnClickListener(this);
		chat_choose_only_image.setOnClickListener(this);

		// chatCamera.setOnClickListener(this);
		chatSmile.setOnClickListener(this);
		ed_chatEdit.setOnClickListener(this);
		chatVoice.setOnClickListener(this);
		chatOnLineLayout.setOnClickListener(this);
		chatAtTitle.setOnClickListener(this);
		newMessage.setOnClickListener(this);

		chat_talk_button.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});

		ed_chatEdit.addTextChangedListener(new TextWatcher() {

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
				String str = ed_chatEdit.getText().toString();
				// if (str != null && str.length() > 0) {
				// btn_chatsend.setVisibility(View.VISIBLE);
				// } else {
				// btn_chatsend.setVisibility(View.INVISIBLE);
				// }
			}
		});

		ViewTreeObserver vto = chatChooseLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				offX = chatChooseLayout.getMeasuredWidth();
				return true;
			}
		});

		chatHeadList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (position == 0) {
					showShare();
				} else if (position == 1) {

					if (menberdialog == null || !menberdialog.isShowing())
						if (!isGetUserInfoConn)
							connGetUserInfo(userID);
				} else if (position == groupMembersList.size()) {
					return;
				} else {
					GroupMemberListBean memberBean = groupMembersList
							.get(position);
					// showMemberDialog(memberBean);
					if (menberdialog == null || !menberdialog.isShowing())
						if (!isGetUserInfoConn)
							connGetUserInfo(memberBean.getId());
				}
			}
		});

		chatPhotoList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (position == 0) {
					// opencamera();
					selectPicFromCamera();
					isGoCameraOrPicture = true;
				} else if (position == 4) {
					Intent intent_gralley = new Intent(ChatActivity.this,
							PhotoActivity.class);

					getCanShowNum();
					intent_gralley.putExtra("canshownum", canshownum);
					startActivityForResult(intent_gralley,
							ChatEasemobActivity.REQUEST_CODE_PICTURE);
					isGoCameraOrPicture = true;
				} else {

					if (chatPhotoSendList.get(position - 1).isSelect()) {
						chatPhotoSendList.get(position - 1).setSelect(false);
					} else {
						chatPhotoSendList.get(position - 1).setSelect(true);
					}
					mChatPhotoAdatper.setList(chatPhotoSendList);
					mChatPhotoAdatper.notifyDataSetChanged();
				}
			}

		});
	}

	private void getCanShowNum() {
		int index = 0;
		for (int i = 0; i < chatPhotoSendList.size(); i++) {
			if (chatPhotoSendList.get(i).isSelect()) {
				index++;
			}
		}
		canshownum = index;
	}

	/** 获取聊天发送图片最后三张图 */
	private String getChatImageLastName() {
		// 获取SDcard卡的路径
		String sdcardPath = Environment.getExternalStorageDirectory()
				.toString();

		ContentResolver mContentResolver = mContext.getContentResolver();
		Cursor mCursor = mContentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.Images.Media._ID,
						MediaStore.Images.Media.DATA },
				MediaStore.Images.Media.MIME_TYPE + "=? OR "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg", "image/png" },
				MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列
		// ArrayList<ChatPhotoBean> list = new ArrayList<ChatPhotoBean>();
		int index = 0;
		while (mCursor.moveToNext() && index < 1) {
//			ChatPhotoBean bean = new ChatPhotoBean();
			// 打印LOG查看照片ID的值
			long id = mCursor.getLong(mCursor
					.getColumnIndex(MediaStore.Images.Media._ID));
			Log.i(  "MediaStore.Images.Media_ID=", id + "");
			// 获取照片路径
			String path = mCursor.getString(mCursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			// bean.setImagePath(FileURl.LOAD_FILE + path);
			// list.add(bean);
			// index++;
			return FileURl.LOAD_FILE + path;
		}
		mCursor.close();
		return null;
		// Message msg = new Message();
		// msg.obj = list;
		// msg.what = GET_PHOTO_LIST_DESC;
		// mHandler.sendMessage(msg);
	}

	/** 获取聊天发送图片最后三张图 */
	private void getChatImageList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 获取SDcard卡的路径
				String sdcardPath = Environment.getExternalStorageDirectory()
						.toString();

				ContentResolver mContentResolver = mContext
						.getContentResolver();
				Cursor mCursor = mContentResolver.query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media._ID,
								MediaStore.Images.Media.DATA },
						MediaStore.Images.Media.MIME_TYPE + "=? OR "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列
				ArrayList<ChatPhotoBean> list = new ArrayList<ChatPhotoBean>();
				int index = 0;
				while (mCursor.moveToNext() && index < 3) {
					ChatPhotoBean bean = new ChatPhotoBean();
					// 打印LOG查看照片ID的值
					long id = mCursor.getLong(mCursor
							.getColumnIndex(MediaStore.Images.Media._ID));
					Log.i(  "MediaStore.Images.Media_ID=", id + "");
					// 获取照片路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					bean.setImagePath(FileURl.LOAD_FILE + path);
					list.add(bean);
					index++;
				}
				mCursor.close();
				Message msg = new Message();
				msg.obj = list;
				msg.what = GET_PHOTO_LIST_DESC;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		// 删除
		Editable editable = ed_chatEdit.getText();
		String edittext_content = editable.toString();
		int num = 1;
		String str_5 = null;
		int index = ed_chatEdit.getSelectionStart();

		// 上一个是\t
		if (edittext_content.length() > 0 && index != 0
				&& edittext_content.charAt(index - 1) == '\t') {

			String regExpAt = "\\@(.*?)\\\t";
			str_5 = edittext_content.substring(0, index);
			num = deleteString(regExpAt, str_5);
			if (num == 0) {
				return super.dispatchKeyEvent(event);
			}
			if (event.getAction() == KeyEvent.ACTION_UP) {
				try {
					editable.delete(index - num, index);
					ed_chatEdit.setSelection(index);

				} catch (Exception e) {

				}
			} else {
				return false;
			}
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		removeChatErrorPop();
		switch (view.getId()) {
		case R.id.chatChooseButton:
			chatChooseLayout.setVisibility(View.VISIBLE);
			chatChooseButton.setVisibility(View.GONE);
			offAnimStart(chatChooseLayout);
			break;

		case R.id.chat_choose_only_image:
			chooseState = ONLY_IMAGE;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							chatChooseButton.setText("筛选（只看图）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;

		case R.id.chat_choose_local_all:
			chooseState = LOCAL_ALL;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							chatChooseButton.setText("筛选（全部）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;
		case R.id.chat_choose_local_10:

			chooseState = LOCAL_10;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							chatChooseButton.setText("筛选（10km）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;
		case R.id.chat_choose_local_1:

			chooseState = LOCAL_1;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							chatChooseButton.setText("筛选（1km）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;
		case R.id.chat_choose_withme:

			chooseState = WITH_ME;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							chatChooseButton.setText("筛选（@我）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;
		case R.id.chat_choose_lock:

			if (chatAdapter.getLockUserID() == null) {
				Toast.makeText(ChatActivity.this, "请选择锁定的人", Toast.LENGTH_SHORT)
						.show();
				offAnimOver(chatChooseLayout).setAnimationListener(
						new AnimationListener() {

							@Override
							public void onAnimationStart(Animation arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation arg0) {
								// TODO Auto-generated method
								chatChooseButton.setVisibility(View.VISIBLE);
							}
						});
				return;
			}

			chooseState = LOCK_PEOPLE;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatChooseLayout.setVisibility(View.INVISIBLE);
			offAnimOver(chatChooseLayout).setAnimationListener(
					new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method
							chatChooseButton.setText("筛选（锁定的）");
							chatChooseButton.setVisibility(View.VISIBLE);
						}
					});
			break;
		case R.id.chatSend:
			final String message = ed_chatEdit.getText().toString().trim();
			GroupApplication.getInstance().closeKeybord(ed_chatEdit, mContext);
			if (isFacialShow) {
				// 如果显示表情， 隐藏表情，打开输入法
				removeGridView();
				chatPhotoListLayout.setVisibility(View.GONE);
			}
			/**
			 * 如果消息为空 不发送
			 */
			String uuid = UUID.randomUUID().toString();

			getCanShowNum();
			if (message != null && message.length() > 0 && canshownum > 0
					|| canshownum > 1) {
				// 发送图文 或者 多图
				StringBuffer sb = new StringBuffer();
				for (int i = 0, j = 0; i < chatPhotoSendList.size(); i++) {
					if (chatPhotoSendList.get(i).isSelect()) {
						postImage(uuid, j, chatPhotoSendList.get(i)
								.getImagePath());
						sb.append(chatPhotoSendList.get(i).getImagePath());
						if (i != chatPhotoSendList.size() - 1) {
							sb.append(",");
						}
						j++;
					}
				}

				// sendChatTextAndImage(message, sb.toString(), uuid);
				addChatListForImageAndText(message, sb.toString(), uuid);
				chatPhotoSendList.clear();

			} else if (message != null && message.length() > 0) {
				// 发送文字
				// 判断session是否链接成功 isClosing和isConnected必须同时使用
				String getAtStr = getAtList(message);
				Log.i(  "aaa", "getAtStr ==== " + getAtStr);

				if (getAtStr != null && getAtStr.length() > 0) {

					sendChatOrImage(CHAT_FLAG_AT_MESSATE + "", message, "0",
							getAtStr, null, null, uuid, 0);
				} else {
					sendChatOrImage(CHAT_FLAG_POP_MESSAGE + "", message, "0",
							null, null, null, uuid, 0);
				}

			} else if (canshownum == 1) {
				// 发送图片
				String filePath = null;
				for (int i = 0; i < chatPhotoSendList.size(); i++) {
					if (chatPhotoSendList.get(i).isSelect()) {
						filePath = chatPhotoSendList.get(i).getImagePath();
						break;
					}
				}

				addChatListForYourself("1", filePath, CHAT_FLAG_POP_MESSAGE
						+ "", uuid, null);
				postImage(uuid, -1, filePath);
			} else {
				Toast.makeText(ChatActivity.this, "请您输入要发送的消息",
						Toast.LENGTH_SHORT).show();
			}

			chatPhotoSendList.clear();

			break;
		// case R.id.chatCamera:
		// // 点击照相
		// showsetheaddialog();
		//
		// break;

		case R.id.chatVoice:

			if (!btn_vocie) {
				chatVoice
						.setBackgroundResource(R.drawable.chatting_keybord_btn);
				chat_talk_button.setVisibility(View.VISIBLE);
				ed_chatEdit.setVisibility(View.GONE);
				chatSmile.setVisibility(View.GONE);
				// btn_chatsend.setVisibility(View.GONE);
				GroupApplication.closeKeybord(ed_chatEdit, mContext);
				btn_vocie = true;
			} else {
				chatVoice.setBackgroundResource(R.drawable.chat_voice_button);
				chat_talk_button.setVisibility(View.GONE);
				ed_chatEdit.setVisibility(View.VISIBLE);
				chatSmile.setVisibility(View.VISIBLE);
				// btn_chatsend.setVisibility(View.VISIBLE);
				btn_vocie = false;
			}
			break;
		case R.id.chatSmile:
			// 点击表情按钮
			if (isFacialShow) {
				removeGridView();
				GroupApplication.getInstance().openKeybord(ed_chatEdit,
						mContext);
			} else {
				if (chatPhotoListLayout.getVisibility() == View.GONE)
					getChatImageList();
				initGridView();
				chatSmile
						.setBackgroundResource(R.drawable.chatting_keybord_btn);
			}
			break;
		case R.id.chatEdit:
			// 获得焦点
			if (chatPhotoListLayout.getVisibility() == View.GONE)
				getChatImageList();
			if (isFacialShow) {
				// 如果显示表情， 隐藏表情，打开输入法
				removeGridView();

			}
			break;
		case R.id.chatOnLineLayout:

			chatPopOnLineNumberView = new ChatPopOnLineNumberView(
					ChatActivity.this);
			chatPopOnLineNumberView.setOnLineOnClickBack(onOnLineClickBack);
			chatPopOnLineNumberView
					.setOnLineOnClickHeadCenter(onOnLineClickHeadCenter);
			popLayout.addView(chatPopOnLineNumberView.getBestView(
					ChatActivity.this, groupMembersList, groupName, onlineNum));
			popLayout.setVisibility(View.VISIBLE);
			popLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					return;
				}
			});
			break;
		case R.id.chatAtTitle:
			chooseState = UNREAD;
			lastDate = System.currentTimeMillis();
			conn(0, 0);
			isReflash = true;
			chatAtTitle.setVisibility(View.GONE);
			chatChooseButton.setText("筛选（@我）");
			break;
		case R.id.new_message:
			newMessage.setVisibility(View.GONE);
			chatAdapter.notifyDataSetChanged();
		}
	}

	OnOnLineClickBack onOnLineClickBack = new OnOnLineClickBack() {

		@Override
		public void getClick() {
			// TODO Auto-generated method stub
			removePopLayout();
		}
	};
	OnOnLineClickHeadCenter onOnLineClickHeadCenter = new OnOnLineClickHeadCenter() {

		@Override
		public void getClick(String userID, int position) {
			// TODO Auto-generated method stub
			// showMemberDialog(bean);

			if (position == 0) {
				showShare();
			} else {
				if (menberdialog == null || !menberdialog.isShowing())
					if (!isGetUserInfoConn)
						connGetUserInfo(userID);
			}
		}
	};

	private long startVoiceT, endVoiceT;
	private String voiceName;
	private int voiceFlag = 1;
	private boolean isShosrt = false;

	private ImageView volume;
	boolean isDown = false;
	boolean isUp = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}
		if (!btn_vocie) {
			return false;
		}
		int[] location = new int[2];
		chat_talk_button.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
		int btn_rc_Y = location[1];
		int btn_rc_X = location[0];
		int[] del_location = new int[2];
		// del_re.getLocationInWindow(del_location);
		int del_Y = del_location[1];
		int del_x = del_location[0];

		if (event.getAction() == MotionEvent.ACTION_UP) {
			isUp = true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN && voiceFlag == 1) {
			isUp = false;
			if (!Environment.getExternalStorageDirectory().exists()) {
				Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
				return false;
			}
			if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X
					&& event.getX() < 302 * BaseActivity.DENSITY) {// 判断手势按下的位置是否是语音录制按钮的范围内
				chat_talk_button
						.setBackgroundResource(R.drawable.shape_talk_vedio_edittext_select);
				rcChat_popup.setVisibility(View.VISIBLE);
				voice_rcd_hint_loading.setVisibility(View.VISIBLE);
				voice_rcd_hint_rcding.setVisibility(View.GONE);
				voice_rcd_hint_tooshort.setVisibility(View.GONE);
				mVoiceHandler.postDelayed(new Runnable() {
					public void run() {
						if (!isShosrt && !isUp) {
							voice_rcd_hint_loading.setVisibility(View.GONE);
							voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
							startVoiceT = System.currentTimeMillis();
							voiceName = startVoiceT + ".amr";

							File folderFile = new File(FileURl.VOICE_PATH
									+ voiceName);
							if (!folderFile.exists()) {
								folderFile.mkdir();
							}
							start(FileURl.VOICE_PATH + voiceName);
							voiceFlag = 2;
							// } else {
							// rcChat_popup.setVisibility(View.GONE);
							// voice_rcd_hint_loading.setVisibility(View.GONE);
							// initToast("按住，发送语音");
						}
					}
				}, 300);
				// img1.setVisibility(View.VISIBLE);
				// del_re.setVisibility(View.GONE);
			}
		} else if ((event.getAction() == MotionEvent.ACTION_UP || event
				.getAction() == MotionEvent.ACTION_CANCEL) && voiceFlag == 2) {// 松开手势时执行录制完成
			chat_talk_button
					.setBackgroundResource(R.drawable.shape_talk_vedio_edittext_unselect);
			if (event.getY() < btn_rc_Y || event.getX() < btn_rc_X) {
				rcChat_popup.setVisibility(View.GONE);
				// img1.setVisibility(View.VISIBLE);
				// del_re.setVisibility(View.GONE);
				stop();
				voiceFlag = 1;
				File file = new File(FileURl.GoodsVoiceURL + voiceName);
				if (file.exists()) {
					file.delete();
				}
			} else {

				voice_rcd_hint_rcding.setVisibility(View.GONE);
				stop();
				endVoiceT = System.currentTimeMillis();
				voiceFlag = 1;
				int time = (int) ((endVoiceT - startVoiceT) / 1000);
				if (time < 1) {
					isShosrt = true;
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
					mVoiceHandler.postDelayed(new Runnable() {
						public void run() {
							voice_rcd_hint_tooshort.setVisibility(View.GONE);
							rcChat_popup.setVisibility(View.GONE);
							isShosrt = false;
						}
					}, 500);
					return false;
				} else {

					MediaPlayer player = MediaPlayer.create(
							ChatActivity.this,
							Uri.fromFile(new File(FileURl.GoodsVoiceURL
									+ voiceName)));
					int time1 = -1;
					try {
						time1 = player.getDuration();
					} catch (Exception e) {
						// TODO: handle exception
						return false;
					}
					if (time1 > 0) {
						postVioce(CHAT_FLAG_POP_MESSAGE + "", false, null,
								voiceName, time + "");
					} else {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mVoiceHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
				}
				rcChat_popup.setVisibility(View.GONE);

			}
		}
		if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
			// Animation mLitteAnimation =
			// AnimationUtils.loadAnimation(this,
			// R.anim.cancel_rc);
			// Animation mBigAnimation = AnimationUtils.loadAnimation(this,
			// R.anim.cancel_rc2);
			// img1.setVisibility(View.GONE);
			volume.setImageResource(R.drawable.chat_amp_delete);

			// del_re.setVisibility(View.VISIBLE);
			// del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
			if (event.getY() >= del_Y
					&& event.getY() <= del_Y + volume.getHeight()
					&& event.getX() >= del_x
					&& event.getX() <= del_x + volume.getWidth()) {
				// del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
				// sc_img1.startAnimation(mLitteAnimation);
				// sc_img1.startAnimation(mBigAnimation);
			}
		} else {
			volume.setImageResource(R.drawable.chat_amp1);
			// img1.setVisibility(View.VISIBLE);
			// del_re.setVisibility(View.GONE);
			// del_re.setBackgroundResource(0);
		}
		// }
		return super.onTouchEvent(event);
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mVoiceHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mVoiceHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mVoiceHandler.removeCallbacks(mSleepTask);
		mVoiceHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.chat_amp1);
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.chat_amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.chat_amp1);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.chat_amp2);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.chat_amp2);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.chat_amp3);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.chat_amp3);
			break;
		default:
			volume.setImageResource(R.drawable.chat_amp3);
			break;
		}
	}

	/**
	 * @param chatText
	 *            输入框的文字x
	 * @return
	 */
	private String getAtList(String chatText) {
		String str = null;
		// 加特殊字符，如果不加查找不到最后一个@结尾的人
		chatText = chatText + "\t nn";
		Pattern pAt = Pattern.compile("\\@(.*?)\\\t");
		Matcher mmAt = pAt.matcher(chatText);
		// String key = "";
		StringBuffer sb = new StringBuffer();
		String key = "";
		ArrayList<String> list = new ArrayList<String>(2);
		while (mmAt.find()) {
			key = mmAt.group();
			key = key.substring(1, key.length() - 1);
			for (int i = 0; i < zanList.size(); i++) {
				String zanUserID = zanList.get(i).getZanUserID();
				if (key.equals(zanList.get(i).getZanUserName())) {
					boolean isAdd = true;
					for (int j = 0; j < list.size(); j++) {
						if (zanUserID.equals(list.get(j))) {
							isAdd = false;
						}
					}
					if (isAdd) {
						list.add(zanUserID);
						sb.append(zanUserID);
						sb.append(",");
					}
					break;
				}
			}

		}
		str = sb.toString();
		if (str.length() > 0) {
			str = str.substring(0, str.length() - 1);
		}

		zanList.clear();
		return str;
	}

	/**
	 * 是否显示表情
	 */
	boolean isFacialShow;

	// private int[] expressionImages
	// , expressionNameNums
	// ;
	// private String[] expressionImageNames;

	private void initGridView() {
		// if (isSmile) {
		isFacialShow = true;
		facial_gridView.setVisibility(View.VISIBLE);
		// if(imm.isActive()){ //若返回true，则表示输入法打开
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// }
		// ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
		// .hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus()
		// .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		GroupApplication.closeKeybord(ed_chatEdit, mContext);
		// isSmile = false;
		// } else {
		// facial_gridView.setVisibility(View.GONE);
		// isSmile = true;
		// }

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Expressions.expressionImgs.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", Expressions.expressionImgs[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.image });
		facial_gridView.setAdapter(simpleAdapter);
		facial_gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						Expressions.expressionImgs[arg2]);
				ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);

				SpannableString spannableString = new SpannableString(
						Expressions.expressionImgNames[arg2]);
				spannableString.setSpan(imageSpan, 0,
						Expressions.expressionImgNames[arg2].length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				if (arg2 == (Expressions.expressionImgNames.length - 1)) { // 删除按钮

					Editable editable = ed_chatEdit.getText();
					int num = 1;
					String str_5 = null;
					int index = ed_chatEdit.getSelectionStart();
					// 取锟斤拷锟角�位锟叫讹拷 锟角凤拷锟斤拷锟斤拷锟斤拷锟斤拷式
					String edittext_content = editable.toString();
					if (index >= 5) {
						str_5 = edittext_content.substring(index - 5, index);
					} else {
						str_5 = edittext_content.substring(0, index);
					}
					/*
					 * String regExp = "[(\u4E00-\u9FA5)|(a-zA-Z)]"; Pattern p =
					 * Pattern.compile(regExp); Matcher mm = p.matcher(str_5);
					 */
					// 表情正则表达式
					String regExpFacial = "\\[#[0-9][0-9]?\\]";

					Pattern pFacial = Pattern.compile(regExpFacial);
					Matcher mmFacial = pFacial.matcher(str_5);
					if (mmFacial.find()) {
						num = 1;
						for (int i = 0; i < Expressions.expressionImgNames.length; i++) {
							if (str_5.equals(Expressions.expressionImgNames[i])) {
								num = 5;
							}
						}
						try {

							editable.delete(index - num, index);
							ed_chatEdit.setSelection(index - num);
						} catch (Exception e) {

						}
					}
					String regExpAt = "\\@(.*?)\\\t";
					str_5 = edittext_content.substring(0, index);
					num = deleteString(regExpAt, str_5);
					if (num == 0) {
						num = 1;
					}
					try {
						editable.delete(index - num, index);
						ed_chatEdit.setSelection(index - num);
					} catch (Exception e) {

					}

				} else {
					//
					ed_chatEdit.append(spannableString);
				}
			}
		});
	}

	private void removeGridView() {
		isFacialShow = false;
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
				R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.image });
		facial_gridView.setAdapter(simpleAdapter);

		chatSmile.setBackgroundResource(R.drawable.chat_smile_btn_bg);
	}

	private int deleteString(String regExpAt, String str_5) {
		// @正则
		// String regExpAt = "\\@([\\w\\-]+\\\t";
		// regExpAt = "\\@(.*?)\\\t";
		// Pattern.compile("@(.*?)/t").matcher(urls[i])
		// str_5 = edittext_content.substring(0, index);
		Pattern pAt = Pattern.compile(regExpAt);
		Matcher mmAt = pAt.matcher(str_5);
		String key = "";

		while (mmAt.find()) {
			key = mmAt.group();
		}
		return key.length();
		// try {
		// editable.delete(index - num, index);
		// ed_chatEdit.setSelection(index);
		// } catch (Exception e) {
		//
		// }
	}

	/**
	 * @return 判断长链接是否在连接
	 */
	private boolean isSocketConn() {

		if (isMinaConn && MinaClient.getMinaClient().isClient()) {
			return true;
		}
		return false;
	}

	/**
	 * @param flag
	 *            5 图文
	 * @param message
	 *            发送内容
	 * @param status
	 *            0普通文字、1图片、2语言
	 * @param user_list
	 *            @ list ,赞 对方Id
	 * 
	 */
	private void sendChatTextAndImage(String content_title, String list,
			String chatID) {
		// String chatstatus, String message,
		// ArrayList<String> list, String uuid

		addChatListForImageAndText(content_title, list, chatID);
	}

	/**
	 * @param flag
	 *            1 普通，2 @，3 第一次图片，4 赞
	 * @param message
	 *            发送内容
	 * @param status
	 *            0普通文字、1图片、2语言
	 * @param user_list
	 *            @ list ,赞 对方Id
	 * 
	 */
	private void sendChatOrImage(String flag, String message,
			String chatstatus, String atuserList_zanOtherUserID,
			String userName, String zanChatId, String chatID, int brow) {

		if (flag.equals(CHAT_FLAG_ZAN_MESSAGE + "")) {
			addChatListForZan(chatstatus, message, userName,
					atuserList_zanOtherUserID, zanChatId, chatID, brow);
		} else if ("0".equals(chatstatus)) { // 文字聊天插入list
			addChatListForYourself(chatstatus, message, CHAT_FLAG_POP_MESSAGE
					+ "", chatID, zanChatId);
		}
		sendChatMina(flag, message, chatstatus, atuserList_zanOtherUserID,
				userName, zanChatId, chatID, brow);
	}

	private void sendChatMina(String flag, String message, String chatstatus,
			String atuserList_zanOtherUserID, String userName,
			String zanChatId, String chatID, int brow) {
		if (MinaClient.getMinaClient().isClient()) {
			// String msg = groupID + userID;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("flag", flag);
			if (flag.equals(CHAT_FLAG_ZAN_MESSAGE + "")
					|| flag.equals(CHAT_FLAG_POP_MESSAGE + "")
					&& chatstatus.equals("2")) {
				message = message + "," + zanChatId;
			}
			map.put("id", chatID);
			map.put("content", message);
			map.put("user_id", userID);
			map.put("group_id", groupID);
			map.put("type", "0"); // 固定，服务器使用
			map.put("status", chatstatus);// 0:文字 1：图片
			map.put("longitude", longitude);
			map.put("latitude", latitude);
			if (brow != 0)
				map.put("brow", brow + "");
			if (atuserList_zanOtherUserID != null)
				map.put("user_list", atuserList_zanOtherUserID);// @list 赞 对方Id

			MinaClient.getMinaClient().send(map);
		} else {
			initMina(null);
		}
	}

	private void sendChatImageAndTextMina(ChatListBean bean) {
		if (MinaClient.getMinaClient().isClient()) {
			// String msg = groupID + userID;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("flag", CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "");
			map.put("id", bean.getChatId());
			map.put("content", bean.getContent());
			map.put("content_title", bean.getContent_title());
			map.put("user_id", bean.getUserId());
			map.put("group_id", bean.getGroupId());
			map.put("type", "0"); // 固定，服务器使用
			map.put("status", "3");// 0:文字 1：图片
			map.put("longitude", longitude);
			map.put("latitude", latitude);
			MinaClient.getMinaClient().send(map);
		} else {
			initMina(null);
		}
	}

	private final String CHAT_MAP_FLAG = "flag";
	private final String CHAT_MAP_MESSAGE = "message";
	private final String CHAT_MAP_CHATSTATUS = "chatstatus";
	private final String CHAT_MAP_USERLIST = "userList";

	private HashMap<String, String> initSendChatOrImageMap(String flag,
			String message, String chatstatus, String userList) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(CHAT_MAP_FLAG, flag);
		map.put(CHAT_MAP_MESSAGE, message);
		map.put(CHAT_MAP_CHATSTATUS, chatstatus);
		map.put(CHAT_MAP_USERLIST, userList);

		return map;

	}

	/**
	 * 插入自己发送的消息
	 */
	private void addChatListForYourself(String chatstatus, String message,
			String flag, String uuid, String zanChatId) {

		ChatListBean bean = new ChatListBean();
		bean.setUserName(userName);
		bean.setFlag(flag);
		if (flag.equals(CHAT_FLAG_FIRST_IMAGE + "")) {
			bean.setContent_title(Contanst.firstInGroupStr);
		} else if (flag.equals(CHAT_FLAG_POP_MESSAGE + "")
				&& chatstatus.equals("2")) {
			message = message + "," + zanChatId;
		} else {
			bean.setContent_title(zanChatId);
		}
		bean.setChatStatus(chatstatus);
		bean.setUserId(userID);
		bean.setChatId(uuid);
		bean.setGroupId(groupID);
		bean.setSendTime(System.currentTimeMillis());
		bean.setUserHead(userHead);
		bean.setSex(userSex);
		bean.setBirthday(userAge);
		bean.setLevel(userLevel);
		bean.setPhotoChatID(zanChatId);
		bean.setChatSendStates(Contanst.CHAT_SEND_ING);
		bean.setContent(message);
		chatList.add(bean);
		chatAdapter.notifyDataSetInvalidated();
		// 显示最后一行
		xListView.setSelection(chatList.size() - 1);
		ed_chatEdit.setText("");

	}

	/**
	 * 插入自己发送的图文消息
	 */
	private ChatListBean addChatListForImageAndText(String message,
			String imageList, String uuid) {
		ChatListBean bean = new ChatListBean();
		bean.setUserName(userName);
		bean.setFlag(CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "");
		bean.setChatStatus(3 + "");
		bean.setChatId(uuid);
		bean.setUserId(userID);
		bean.setGroupId(groupID);
		bean.setSendTime(System.currentTimeMillis());
		bean.setUserHead(userHead);
		bean.setSex(userSex);
		bean.setBirthday(userAge);
		bean.setLevel(userLevel);
		bean.setChatSendStates(Contanst.CHAT_SEND_ING);
		String array[] = imageList.split(",");
		HashMap<String, ChatPhotoBean> map = new HashMap<String, ChatPhotoBean>();
		for (int i = 0; i < array.length; i++) {
			ChatPhotoBean curBean = new ChatPhotoBean();
			curBean.setSelect(false);
			map.put(i + "", curBean);
		}
		bean.setPostSuccessMap(map);
		bean.setContent(imageList);
		bean.setContent_title(message);
		chatList.add(bean);

		chatAdapter.notifyDataSetInvalidated();
		// 显示最后一行
		// xListView.setSelection(chatList.size() - 1);
		ed_chatEdit.setText("");
		return bean;
	}

	/**
	 * 插入自己点赞发送的消息
	 */
	private void addChatListForZan(String chatstatus, String message,
			String chatUserName, String otherUserId, String zanChatId,
			String uuid, int brow) {
		ChatListBean bean = new ChatListBean();
		bean.setUserName(userName);
		bean.setFlag(CHAT_FLAG_ZAN_MESSAGE + "");
		bean.setChatStatus(chatstatus);
		bean.setChatId(uuid);
		bean.setUserId(userID);
		bean.setGroupId(groupID);
		bean.setSendTime(System.currentTimeMillis());
		bean.setUserHead(userHead);
		bean.setBrow(brow);
		bean.setContent(message);
		bean.setSex(userSex);
		bean.setBirthday(userAge);
		bean.setLevel(userLevel);
		bean.setPhotoChatID(zanChatId);
		bean.setChatSendStates(Contanst.CHAT_SEND_ING);
		bean.setContent_title(otherUserId + "," + chatUserName);
		chatList.add(bean);

		if (popLayout.getVisibility() == View.VISIBLE) {
			ZanBean zanBean = new ZanBean();
			zanBean.setZanUserHead(userHead);
			zanBean.setZanUserID(userID);
			zanBean.setZanUserName(userName);
			zanBean.setBrow(brow);
			chatPopView.setPopZanBean(zanBean);
		}

		chatAdapter.notifyDataSetInvalidated();
		// 显示最后一行
		// xListView.setSelection(chatList.size() - 1);
		ed_chatEdit.setText("");
	}

	// 获取聊天信息
	/**
	 * @param curLastDate
	 *            记录消息时间
	 * @param historyFlat
	 *            0-此时间之前的20条消息记录， 1-此时间后的所有记录
	 */
	private void conn(long curLastDate, int historyFlag) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			if (chooseState == LOCK_PEOPLE) {
				params.add(new BasicNameValuePair("user_id", chatAdapter
						.getLockUserID()));
			} else {
				params.add(new BasicNameValuePair("user_id", userID));
			}
			params.add(new BasicNameValuePair("type", chooseState + ""));
			params.add(new BasicNameValuePair("send_time", curLastDate + ""));
			params.add(new BasicNameValuePair("group_id", groupID));
			params.add(new BasicNameValuePair("flag", historyFlag + ""));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETHISTORYMESSAGE, null, null,
				ChatActivity.this).addList(params).request(UrlParams.POST);

		Log.i(  "aaa", "params ===== " + params);
		isConn = true;

	}

	// 邀请推送信息
	/**
	 * @param curLastDate
	 *            记录消息时间
	 * @param historyFlat
	 *            0-此时间之前的20条消息记录， 1-此时间后的所有记录
	 */
	// private void connInvitation(String otherID) {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// try {
	// params.add(new BasicNameValuePair("myuser_id", userID));
	// params.add(new BasicNameValuePair("group_id", groupID));
	// params.add(new BasicNameValuePair("fruser_id", otherID));
	// } catch (Exception e) {
	//
	// }
	// Service.getService(Contanst.HTTP_JPUSHGROUP, null, null,
	// ChatActivity.this).addList(params).request(UrlParams.POST);
	//
	// Log.i(  "aaa", "params ===== " + params);
	// isConn = true;
	//
	// }

	// 获取群成员信息
	private void getGroupMember() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("currentPage", "1"));
			params.add(new BasicNameValuePair("pageLimit", "100"));
			params.add(new BasicNameValuePair("user_id", userID));
			params.add(new BasicNameValuePair("group_id", groupID));
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETGROUPMEMBERS, null, null,
				ChatActivity.this).addList(params).request(UrlParams.POST);
	}

	boolean isConn;
	IXListViewListener listener = new IXListViewListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			Log.i(  "aaa", "onRefresh");
			if (isConn) {
				return;
			}
			conn(lastDate, 0);
			isConn = true;
		}

		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub

		}

	};

	private void onLoad() {
		xListView.stopRefresh();
		isConn = false;
	}

	private Animation offAnimStart(View view) {
		// mTextureView.setVisibility(View.INVISIBLE);
		Animation a = new TranslateAnimation(offX, 0.0f, 0.0f, 0.0f);
		a.setDuration(300);
		a.setStartOffset(300);
		a.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.decelerate_interpolator));
		a.setFillAfter(true);
		view.startAnimation(a);

		return a;
	}

	ShowLastItem showLastItem = new ShowLastItem() {

		@Override
		public void getClick() {
			// TODO Auto-generated method stub
			// 返回最后一个显示
			if (newMessage.getVisibility() != View.GONE)
				newMessage.setVisibility(View.GONE);

		}
	};

	OnClickZanFacialButton onClickZanFacialButton = new OnClickZanFacialButton() {

		@Override
		public void getClick(int photoChatID) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 点赞表情
	 */
	int curBrow;
	OnClickZanButton onClickZanButton = new OnClickZanButton() {
		@Override
		public void getClick(final int position, final String otherUserId,
				final String otherUserName, final String photoUrl,
				final String chatID) {
			ZanFacialDialog zanFacialDialog = new ZanFacialDialog(
					ChatActivity.this, R.style.dialog);// 创建Dialog并设置样式主题
			zanFacialDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
			zanFacialDialog
					.setOnClickZanFacialButton(new OnClickZanFacialButton() {

						@Override
						public void getClick(int brow) {
							// TODO Auto-generated method stub
							curBrow = brow;
							String uuid = UUID.randomUUID().toString();
							// 点赞
							sendChatOrImage(CHAT_FLAG_ZAN_MESSAGE + "",
									photoUrl, "0", otherUserId, otherUserName,
									chatID, uuid, brow);
							chatList.get(position).setCilckZan(true);

						}
					});
			zanFacialDialog.show();
		}
	};

	OnClickJuBaoButton onClickJuBaoButton = new OnClickJuBaoButton() {

		@Override
		public void getClick(String userName, String userID) {
			// TODO Auto-generated method stub
			if (userID.equals(ChatActivity.this.userID)) {
				showdialogJuBaoYourSelf();
			} else {
				showdialogJuBao();
			}
		}
	};

	OnClickUserCenterButton onClickUserCenterButton = new OnClickUserCenterButton() {

		@Override
		public void getClick(String userName, String userID, String userHead,
				long birthday, String sex, String level) {
			// TODO Auto-generated method stub

			if (menberdialog == null || !menberdialog.isShowing())
				if (!isGetUserInfoConn)
					connGetUserInfo(userID);
		}
	};

	OnClickHeadCenter onClickHeadCenter = new OnClickHeadCenter() {

		@Override
		public void getClick(GroupMemberListBean bean) {
			// TODO Auto-generated method stub

			if (menberdialog == null || !menberdialog.isShowing())
				if (!isGetUserInfoConn)
					connGetUserInfo(bean.getId());
		}
	};

	private void showdialogJuBao() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("确定要举报当前用户？");
		builder.setPositiveButton("举报", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ChatActivity.this, "举报成功", Toast.LENGTH_SHORT)
						.show();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void showdialogJuBaoYourSelf() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("无法举报自己。");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	/**
	 * 详情页返回
	 */
	OnClickBack onClickBack = new OnClickBack() {

		@Override
		public void getClick() {
			// TODO Auto-generated method stub
			removePopLayout();
		}
	};

	/**
	 * 软键盘高度
	 */
	private int getOffYKeyWord() {
		int[] location = new int[2];
		chat_back.getLocationOnScreen(location);
		int x2 = location[0];
		int y2 = location[1];
		return y2;
	}

	private void removePopLayout() {
		if (popLayout.getVisibility() == View.VISIBLE) {
			popLayout.removeAllViews();
			popLayout.setVisibility(View.GONE);
			chatPopView = null;
		}
	}

	OnClickZanCenterButton onClickZanCenterButton = new OnClickZanCenterButton() {

		@Override
		public void getClick(String photoChatID, boolean isZan, int index,
				String url) {
			// TODO Auto-generated method stub
			ArrayList<BigImageBean> list = new ArrayList<BigImageBean>(1);
			int bigIndex = 0;
			if (isZan) {
				BigImageBean bean = new BigImageBean();
				bean.setChatID(photoChatID);
				bean.setIndex(index);
				bean.setImageUrl(url);
				list.add(bean);
			} else {
				list = getBigImageList(chatList);
				bigIndex = getBigImageListIndex(url, list);
			}
			GroupApplication.closeKeybord(ed_chatEdit, mContext);
			chatPopView = new ChatPopView(ChatActivity.this, false, isZan);
			chatPopView.setOnClickBack(onClickBack);
			chatPopView.setOnClickHeadCenter(onClickHeadCenter);
			chatPopView.setOnClickZanButton(onClickZanButton);
			popLayout.addView(chatPopView.getBestView(ChatActivity.this, list,
					bigIndex));
			popLayout.setVisibility(View.VISIBLE);
			popLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_UP)
						removePopLayout();
					return true;
				}
			});

		}
	};
	OnClickAtButton onClickAtButton = new OnClickAtButton() {

		@Override
		public void getClick(ZanBean bean) {
			// TODO Auto-generated method stub
			clickAtConn(bean);
		}
	};

	OnClickQuit onClickQuit = new OnClickQuit() {

		@Override
		public void getClick(String chatID) {
			// TODO Auto-generated method stub
			removeListItem(chatID);
			chatAdapter.notifyDataSetInvalidated();
			chatErrorPop.removeAllViews();
			chatErrorPop.setVisibility(View.GONE);
		}
	};

	OnClickSendAgain onClickSendAgain = new OnClickSendAgain() {

		@Override
		public void getClick(ChatListBean bean) {
			// TODO Auto-generated method stub

			chatErrorPop.removeAllViews();
			chatErrorPop.setVisibility(View.GONE);
			if (bean.getFlag().equals(CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {
				// 图片post没成功
				sendChatImageAndTextMina(bean);
				// postImage(bean.getFlag(), true, bean.getChatId());
			} else {
				sendChatMina(bean.getFlag(), bean.getContent(),
						bean.getChatStatus(), bean.getPhotoChatID(), userName,
						bean.getPhotoChatID(), bean.getChatId(), bean.getBrow());
			}
		}
	};

	OnClickSendErrorButton onClickSendErrorButton = new OnClickSendErrorButton() {

		@Override
		public void getClick(ChatListBean bean, int x, int y) {
			// TODO Auto-generated method stub
			ChatErrorPopView chatErrorPopView = new ChatErrorPopView(
					ChatActivity.this);
			chatErrorPopView.setOnClickSendAgain(onClickSendAgain);
			chatErrorPopView.setOnClickQuit(onClickQuit);
			chatErrorPop.removeAllViews();

			int keyWordOffY = offY - getOffYKeyWord();

			chatErrorPop.addView(chatErrorPopView.getBestView(
					ChatActivity.this, ChatErrorPopView.ANCHOR_LEFT, bean));
			RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mLayoutParams.setMargins((int) (x + 36 * BaseActivity.DENSITY),
					(int) (y + keyWordOffY), 0, 0);
			chatErrorPop.setLayoutParams(mLayoutParams);
			// } else {
			// chatErrorPop.addView(chatErrorPopView
			// .getBestView(ChatActivity.this,
			// ChatErrorPopView.ANCHOR_BOTTOM, bean));
			// RelativeLayout.LayoutParams mLayoutParams = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// mLayoutParams.setMargins(
			// (int) (x - 200 * BaseActivity.DENSITY),
			// (int) (y - 88 * BaseActivity.DENSITY), 0, 0);
			// chatErrorPop.setLayoutParams(mLayoutParams);
			// }

			chatErrorPop.setVisibility(View.VISIBLE);
		}
	};

	private void removeChatErrorPop() {
		if (chatErrorPop != null
				&& chatErrorPop.getVisibility() == View.VISIBLE) {
			chatErrorPop.removeAllViews();
			chatErrorPop.setVisibility(View.GONE);
		}
	}

	IXListViewOnScroll iXListViewOnScroll = new IXListViewOnScroll() {

		@Override
		public void onScroll() {
			// TODO Auto-generated method stub
			removeChatErrorPop();
			if (chat_up_more_message.getVisibility() == View.VISIBLE) {
				chat_up_more_message.setVisibility(View.GONE);
			}
		}
	};

	private Animation offAnimOver(View view) {
		// mTextureView.setVisibility(View.INVISIBLE);
		Animation a = new TranslateAnimation(0.0f, offX, 0.0f, 0.0f);
		a.setDuration(300);
		a.setStartOffset(300);
		a.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.decelerate_interpolator));
		a.setFillAfter(true);
		view.startAnimation(a);
		return a;
	}

	/**
	 * 判断图片是否全部发送成功
	 */
	private boolean isPostMapSuccessed(HashMap<String, ChatPhotoBean> map) {
		if (map.size() == 0)
			return false;

		for (int i = 0; i < map.size(); i++) {
			ChatPhotoBean bean = map.get(i + "");
			if (!bean.isSelect()) {
				return false;
			}
		}
		return true;
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
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:

				if (msg.arg1 == Contanst.GETHISTORYMESSAGE) {
					ArrayList<ChatListBean> curlist = (ArrayList<ChatListBean>) msg.obj;
					int size = curlist.size();

					if (isReflash) {
						chatList = curlist;
						isReflash = false;
						if (chatList.size() == 0 && chooseState == WITH_ME) {
							atListNullImage.setVisibility(View.VISIBLE);
						} else {
							atListNullImage.setVisibility(View.GONE);
						}

					} else if (isReconnect) { // 断线重连添加到最后
						chatList.addAll(curlist);
						newMessage.setVisibility(View.VISIBLE);
					} else {
						// chatList = curlist;
						curlist.addAll(chatList);
						chatList = curlist;
					}
					// chatList = curlist;

					chatAdapter.setItemList(chatList); // 重新设置数据
					chatAdapter.setUserID(userID);
					chatAdapter.setUserName(userName);
					chatAdapter.setChatFacialMap(chatFacialMap);
					chatAdapter.notifyDataSetInvalidated();

					chatMessageNum.setText("累计" + chatTotalCount + "条消息");

					if (isReconnect) {
						isReconnect = false;
					} else {
						xListView.setSelection(size);
					}
					onLoad();
					// 初始化list后再创建聊天服务。
					String uuid = UUID.randomUUID().toString();
					if (!isSocketConn()) {

						initMina(null);
						// if (isFirst) {
						// if (GroupApplication.getInstance().getCurSP()
						// .equals(Contanst.TouristStone)) {
						// sendChatOrImage(CHAT_FLAG_POP_MESSAGE + "",
						// Contanst.firstTouristInGroupStr, "0",
						// null, null, null, uuid, 0);
						// isFirst = false;
						// } else {
						// postImage(CHAT_FLAG_FIRST_IMAGE + "", false,
						// null);
						// }
						// }
					} else {
						// if (isFirst) {
						// if (GroupApplication.getInstance().getCurSP()
						// .equals(Contanst.TouristStone)) {
						// sendChatOrImage(CHAT_FLAG_POP_MESSAGE + "",
						// Contanst.firstTouristInGroupStr, "0",
						// null, null, null, uuid, 0);
						// isFirst = false;
						// } else {
						// postImage(CHAT_FLAG_FIRST_IMAGE + "", false,
						// null);
						// }
						// }
					}
				} else if (msg.arg1 == Contanst.GETGROUPMEMBERS) {
					groupMembersList.clear();
					GroupMemberListBean invitation = new GroupMemberListBean();
					invitation.setDistance("邀请");
					groupMembersList.add(invitation);
					GroupMemberListBean bean = new GroupMemberListBean();
					bean.setDistance("我");
					bean.setId(userID);
					bean.setName(userName);
					bean.setPhoto(userHead);
					groupMembersList.add(bean);
					// ArrayList<GroupMemberListBean> memberList =
					// (ArrayList<GroupMemberListBean>) msg.obj;
					MainHomePageBean<GroupMemberListBean> mainHomePageBean = (MainHomePageBean<GroupMemberListBean>) msg.obj;
					ArrayList<GroupMemberListBean> memberList = mainHomePageBean
							.getListData();
					groupMembersList.addAll(memberList);
					onlineNum = (mainHomePageBean.getTotalCount() + 1)
							+ "人   Doing";
					chatGroupNumber.setText(onlineNum);

					/**
					 * 群组成员listview绑定数据
					 */
					ChatMemberAdapter chatMemberAdapter = new ChatMemberAdapter(
							ChatActivity.this, groupMembersList);
					chatHeadList.setAdapter(chatMemberAdapter);
					chatMemberAdapter.notifyDataSetChanged();
					if (chatPopOnLineNumberView != null) {
						chatPopOnLineNumberView.updateGridView();
					}
					// conn(0);
				} else if (msg.arg1 == Contanst.UPLOADMESSAGEIMG) {

					String[] array = ((String) msg.obj).split(",");
					String imageUrl = array[0];
					String uuid = array[1];
					String index = array[2];

					ChatListBean bean = null;
					int chatListIndex = 0;
					for (int i = 0; i < chatList.size(); i++) {
						if (chatList.get(i).getChatId().equals(uuid)) {
							bean = chatList.get(i);
							chatListIndex = i;
							break;
						}
					}

					ChatPhotoBean chatPhotoBean = new ChatPhotoBean();
					chatPhotoBean.setImagePath(imageUrl);
					chatPhotoBean.setSelect(true);
					chatList.get(chatListIndex).getPostSuccessMap()
							.remove(index);
					chatList.get(chatListIndex).getPostSuccessMap()
							.put(index, chatPhotoBean);
					if (index.equals("-1")) { // 单图
						if (isSocketConn()) {
							sendChatMina(CHAT_FLAG_POP_MESSAGE + "", imageUrl,
									"1", null, userName, null,
									bean.getChatId(), 0);
							// }
						} else {
							// 连接失败
							initMina(null);
							sendChatMina(CHAT_FLAG_POP_MESSAGE + "", imageUrl,
									"1", null, userName, null,
									bean.getChatId(), bean.getBrow());

						}
						setListImageUrl(CHAT_FLAG_POP_MESSAGE + "", imageUrl,
								"1", uuid);
					} else {
						if (isPostMapSuccessed(chatList.get(chatListIndex)
								.getPostSuccessMap())) {
							StringBuffer sb = new StringBuffer();
							HashMap<String, ChatPhotoBean> map = chatList.get(
									chatListIndex).getPostSuccessMap();
							for (int i = 0; i < chatList.get(chatListIndex)
									.getPostSuccessMap().size(); i++) {
								sb.append(map.get(i + "").getImagePath());
								if (i < map.size() - 1) {
									sb.append(",");
								}
							}
							chatList.get(chatListIndex).setContent(
									sb.toString());

							// 图片全部发送完成
							if (isSocketConn()) {
								sendChatImageAndTextMina(chatList
										.get(chatListIndex));
								// }
							} else {
								// 连接失败
								initMina(null);
								sendChatImageAndTextMina(chatList
										.get(chatListIndex));

							}

							setListImageUrl(CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE
									+ "", sb.toString(), "3", uuid);
						} else {
							return;
						}
					}

				} else if (msg.arg1 == Contanst.UPLOADMESSAGEARM) {

					String[] array = ((String) msg.obj).split(",");
					String voiceUrl = array[0];
					String uuid = array[1];
					String time = array[2];
					sendChatOrImage(CHAT_FLAG_POP_MESSAGE + "", voiceUrl, "2",
							null, null, time, uuid, 0);

				} else if (msg.arg1 == Contanst.ATTENTION) {
					String array[] = ((String) msg.obj).split(",");
					if (array[1].equals("0")) {
						Toast.makeText(ChatActivity.this, "关注成功",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(ChatActivity.this, "取消成功",
								Toast.LENGTH_SHORT).show();
					}

				} else if (msg.arg1 == Contanst.GETUSERINFO) {
					UserBean userBean = (UserBean) msg.obj;
					showMemberDialog(userBean);
					isGetUserInfoConn = false;
				}

				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.GETHISTORYMESSAGE) {

				} else if (msg.arg1 == Contanst.UPLOADMESSAGEIMG) {
					setListSendFail();
					chatAdapter.notifyDataSetInvalidated();
				} else if (msg.arg1 == Contanst.GETUSERINFO) {
					isGetUserInfoConn = false;
				} else if (msg.arg1 == Contanst.GETGROUPMEMBERS) {
					// conn(0);
				}

				onLoad();
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG)
						.show();
				break;

			case Contanst.MINA_CONN_FAIL:

				setListSendFail();
				if (chatAdapter != null)
					chatAdapter.notifyDataSetInvalidated();
				Toast.makeText(GroupApplication.getInstance(), "网络异常，服务器连接失败",
						Toast.LENGTH_SHORT).show();

				break;

			case Contanst.MINA_ERROR:
				// 链接中断
				initMina(null);
				setListSendFail();
				chatAdapter.notifyDataSetInvalidated();

				break;

			case Contanst.MINA_MESSAGE_RECEIVED:

				removeChatErrorPop();
				String chatBack = (String) msg.obj;

				Log.i(  "aaa", "message = " + chatBack);
				try {
					JSONObject result = new JSONObject(chatBack);

					String msgCode = "";
					if (result.has("msgCode")) {
						msgCode = result.getString("msgCode");
					}
					ChatListBean bean = null;
					if (msgCode.equals("101")) { // 系统消息
						isMinaConn = true;
						return;
					} else if (msgCode.equals("102")) { // 上下线系统通知
						getGroupMember();
						return;
					} else if (msgCode.equals("201")) { // socket 接收消息的代码
						bean = chatJsonToBean(result);
						if (bean == null) {
							return;
						}
					} else if (msgCode.equals("202")) { // socket 发送成功代码
						chatPostBack(result);
						return;
					}
					if (isLastVisble()) {
						chatList.add(bean);
						chatAdapter.notifyDataSetInvalidated();
						xListView.setSelection(chatList.size() - 1);
					} else {
						newMessage.setVisibility(View.VISIBLE);
						chatList.add(bean);
						chatAdapter.notifyDataSetInvalidated();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case Contanst.INIT_MINA_SEND_MESSAGE:

				HashMap<String, String> map = (HashMap<String, String>) msg.obj;
				if (map != null) {
					String flag = map.get(CHAT_MAP_FLAG);
					String content = map.get(CHAT_MAP_MESSAGE);
					String chatStatus = map.get(CHAT_MAP_CHATSTATUS);
					String userList = map.get(CHAT_MAP_USERLIST);
					// if(chatStatus.equals("0")){
					String uuid = UUID.randomUUID().toString();
					sendChatOrImage(flag, content, chatStatus, userList, null,
							null, uuid, 0);
				}
				if (groupID != null && groupID.length() > 0) {
					// conn();
					getGroupMember();// 获取群成员信息
					Log.i(  "aaa", "msg.arg1 == " + msg.arg1);
					if (isReconnect) {
						conn(curDate, 1);
					} else {
						conn(0, 0);
						isReflash = true;
					}

				}

				// } else if(chatStatus.equals("1")){
				// postImage(flag);
				// }
				// sendChatOrImage("1", message, "0", null);
				break;

			case Contanst.MINA_TIMER_TASK:
				if (!isSocketConn()) {
					// soket链接断了
					initMina(null);
					isReconnect = true;
				}
				break;

			case GET_PHOTO_LIST_DESC:
				Tools.getLog(Tools.d, "aaa", "查看相册！");
				chatPhotoSendList = (ArrayList<ChatPhotoBean>) msg.obj;
				mChatPhotoAdatper.setList(chatPhotoSendList);
				// mChatPhotoAdatper.notifyDataSetChanged();
				mChatPhotoAdatper.notifyDataSetInvalidated();
				chatPhotoListLayout.setVisibility(View.VISIBLE);
				break;
			}
		}

	};

	/**
	 * 聊天计时器 40s检测
	 */
	Timer socektTimer;

	private void startSokectTimer() {
		socektTimer = new Timer(true);
		socektTimer.schedule(task, 1000, 10000);
	}

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = Contanst.MINA_TIMER_TASK;
			mHandler.sendMessage(message);
		}
	};

	ArrayList<BigImageBean> bigImageList = new ArrayList<BigImageBean>(10);

	/**
	 * 得到消息list，获取所有图片的消息，拼成数组
	 */
	private ArrayList<BigImageBean> getBigImageList(
			ArrayList<ChatListBean> curChatList) {
		ArrayList<BigImageBean> list = new ArrayList<BigImageBean>();
		for (int i = 0; i < curChatList.size(); i++) {
			ChatListBean bean = curChatList.get(i);
			if (bean.getFlag().equals(CHAT_FLAG_POP_MESSAGE + "")
					&& bean.getChatStatus().equals("1")) {
				// 单图
				BigImageBean bigBean = new BigImageBean();
				bigBean.setChatID(bean.getChatId());
				bigBean.setIndex(0);
				bigBean.setImageUrl(bean.getContent());
				list.add(bigBean);
			} else if (bean.getFlag().equals(
					CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {
				String array[] = bean.getContent().split(",");
				for (int j = 0; j < array.length; j++) {
					BigImageBean bigBean = new BigImageBean();
					bigBean.setChatID(bean.getChatId());
					bigBean.setIndex(j);
					bigBean.setImageUrl(array[j]);
					list.add(bigBean);
				}
			}
		}
		return list;
	}

	/**
	 * 获取点击图片在list中的下标
	 * 
	 * @return
	 */
	private int getBigImageListIndex(String imageUrl,
			ArrayList<BigImageBean> list) {

		for (int i = 0; i < list.size(); i++) {
			if (imageUrl.equals(list.get(i).getImageUrl())) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 判断当前显示的list item 是否是有最后一个。 如果有最后一个接收到的消息要弹出显示，如果没有，不刷新页面
	 * 
	 * @return
	 */
	private boolean isLastVisble() {
		if (xListView.getLastVisiblePosition() >= chatList.size()) {
			return true;
		}
		return false;
	}

	/**
	 * socket 返回数据
	 */
	private void chatPostBack(JSONObject jsonStr) {
		try {
			if (jsonStr.has("msgCode")) {
				String flag = "";
				String content = "";
				String status = "";
				String id = "";
				if (jsonStr.has("flag"))
					flag = jsonStr.getString("flag");

				if (jsonStr.has("id")) {
					id = jsonStr.getString("id");
					setListSendSuccess(id);
					chatAdapter.notifyDataSetInvalidated();
				}

				Log.i(  "aaa", "isReconnect ============= "
						+ isReconnect);
				if (!isReconnect && jsonStr.has("send_time")) {
					curDate = jsonStr.getLong("send_time");
				}

				if (jsonStr.has("status"))
					status = jsonStr.getString("status");
				if (flag.equals(CHAT_FLAG_FIRST_IMAGE + "")
						|| flag.equals(CHAT_FLAG_POP_MESSAGE + "")
						&& status.equals("1")) {
					if (jsonStr.has("content"))
						content = jsonStr.getString("content");
					if (jsonStr.has("id"))
						id = jsonStr.getString("id");
					// setListImageChat(content, id, status);
					return;
				} else if (flag.equals(CHAT_FLAG_ZAN_MESSAGE + "")) {
					// 自己主动点赞成功后添加到list里面
					ZanBean zanBean = new ZanBean();
					zanBean.setZanUserHead(userHead);
					zanBean.setZanUserID(userID);
					zanBean.setZanUserName(userName);
					zanBean.setBrow(curBrow);
					String photoImageID = "";
					if (jsonStr.has("content")) {
						String[] array = jsonStr.getString("content")
								.split(",");
						content = array[0];
						photoImageID = array[1];
					}
					setListZanImageChat(content, zanBean, photoImageID);
					// 添加后刷新
					chatAdapter.notifyDataSetInvalidated();
					newMessage.setVisibility(View.VISIBLE);
					// 显示最后一行
					// xListView.setSelection(chatList.size() - 1);
				} else if (flag.equals(CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {

				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 201
	 * 
	 * @return
	 */
	private ChatListBean chatJsonToBean(JSONObject jsonStr) {

		ChatListBean bean = new ChatListBean();

		try {

			String flag = "";
			String name = "";
			String user_id = "";
			String photo = "";
			String content = "";
			String status = "";
			String id = "";
			int brow = 0;

			if (jsonStr.has("flag")) {
				flag = jsonStr.getString("flag");
				bean.setFlag(flag);
			}
			if (jsonStr.has("status")) {
				status = jsonStr.getString("status");
				bean.setChatStatus(status);
			}

			double curlatitude = 0.0f;
			double curlongitude = 0.0f;
			if (jsonStr.has("latitude")) {
				if (jsonStr.getString("latitude") == null
						|| jsonStr.getString("latitude").equals("null")) {
					curlatitude = 0.0;
				} else {
					curlatitude = jsonStr.getDouble("latitude");
				}
			}
			if (jsonStr.has("longitude")) {
				if (jsonStr.getString("longitude") == null
						|| jsonStr.getString("longitude").equals("null")) {
					curlongitude = 0.0;
				} else {
					curlongitude = jsonStr.getDouble("longitude");
				}
			}
			String distance = null;
			try {
				distance = Geohash.GetDistance(curlatitude, curlatitude,
						Double.parseDouble(latitude),
						Double.parseDouble(longitude));
			} catch (Exception e) {
				distance = "火星";
			}
			bean.setDistance(distance);

			if (jsonStr.has("name")) {
				name = jsonStr.getString("name");
				bean.setUserName(name);
			}
			if (jsonStr.has("brow")) {
				brow = jsonStr.getInt("brow");
				bean.setBrow(brow);
			}
			Log.i(  "aaa", "brow ============= " + brow);

			if (jsonStr.has("send_time")) {
				long chatTime = jsonStr.getLong("send_time");
				bean.setSendTime(chatTime);
				if (!isReconnect)
					curDate = chatTime;
				Log.i(  "aaa", "isReconnect ============= "
						+ isReconnect);
			}
			// bean.setSex(userSex);
			// bean.setBirthday(userAge);
			// bean.setLevel(userLevel);
			if (jsonStr.has("sex"))
				bean.setSex(jsonStr.getString("sex"));
			if (jsonStr.has("integral"))
				bean.setLevel(jsonStr.getString("integral"));
			if (jsonStr.has("birthday")) {

				bean.setBirthday(jsonStr.getLong("birthday"));
			}

			if (jsonStr.has("photo")) {
				photo = jsonStr.getString("photo");
				bean.setUserHead(photo);
			}
			if (jsonStr.has("user_id")) {
				user_id = jsonStr.getString("user_id");
				bean.setUserId(user_id);
			}
			String photoImageID = "";
			if (jsonStr.has("content")) {
				if (flag.equals(ChatActivity.CHAT_FLAG_ZAN_MESSAGE + "")) {
					String[] array = jsonStr.getString("content").split(",");
					content = array[0];
					bean.setContent(content);
					bean.setPhotoChatID(array[1]);
					photoImageID = array[1];

					if (popLayout.getVisibility() == View.VISIBLE) {
						ZanBean zanBean = new ZanBean();
						zanBean.setZanUserHead(photo);
						zanBean.setZanUserID(user_id);
						zanBean.setZanUserName(name);
						zanBean.setBrow(brow);
						chatPopView.setPopZanBean(zanBean);
					}
				} else {
					content = jsonStr.getString("content");
					bean.setContent(content);
				}
			}
			if (jsonStr.has("content_title"))
				bean.setContent_title(jsonStr.getString("content_title"));
			if (jsonStr.has("id"))
				bean.setChatId(jsonStr.getString("id"));
			if (jsonStr.has("user_list"))
				bean.setAtIDList(jsonStr.getString("user_list").split(","));

			bean.setGroupId(groupID);
			if (flag.equals(CHAT_FLAG_ZAN_MESSAGE + "")) {
				ZanBean zanBean = new ZanBean();
				zanBean.setZanUserHead(photo);
				zanBean.setZanUserID(user_id);
				zanBean.setZanUserName(name);
				zanBean.setBrow(brow);
				setListZanImageChat(content, zanBean, photoImageID);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 发送修改消息成功改变对应消息的状态
	 */
	private void setListSendSuccess(String chatID) {

		for (int i = 0; i < chatList.size(); i++) {
			ChatListBean bean = chatList.get(i);

			if (bean.getChatId() != null && bean.getChatId().equals(chatID)) {
				// 如果在消息列表里面发送的是图片
				// url 相等
				chatList.get(i).setChatSendStates(Contanst.CHAT_SEND_SUCCESS);
			}
		}
	}

	/**
	 * 发送修改消息失败
	 */
	private void setListSendFail() {

		for (int i = 0; i < chatList.size(); i++) {
			ChatListBean bean = chatList.get(i);

			if (bean.getChatSendStates() == Contanst.CHAT_SEND_ING) {
				// 如果在消息列表里面发送的是图片
				// url 相等
				chatList.get(i).setChatSendStates(Contanst.CHAT_SEND_FAIL);
			}
		}
	}

	// 11-20 17:27:12.764: I/aaa(30088): path ========
	// file:///storage/emulated/0/groupimage/1416475631group.jpg
	/**
	 * 发送图片成功后修改Url
	 */
	private void setListImageUrl(String flag, String imageUrl, String status,
			String content) {

		for (int i = 0; i < chatList.size(); i++) {
			ChatListBean bean = chatList.get(i);
			if (bean.getFlag() != null
					&& bean.getFlag().equals(CHAT_FLAG_FIRST_IMAGE + "")
					|| bean.getFlag() != null
					&& bean.getFlag().equals(CHAT_FLAG_POP_MESSAGE + "")
					&& bean.getChatStatus().equals("1")
					|| bean.getFlag() != null
					&& bean.getFlag().equals(
							CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {
				// 如果在消息列表里面发送的是图片
				if (bean.getChatId().equals(content)) {
					// url 相等
					chatList.get(i).setContent(imageUrl);
					return;
				}
			}
		}
	}

	/**
	 * 收到zan的消息 修改zan数据
	 */
	private void setListZanImageChat(String content, ZanBean zanBean,
			String photoImageID) {

		for (int i = 0; i < chatList.size(); i++) {
			ChatListBean bean = chatList.get(i);
			if (bean.getFlag() != null
					&& bean.getFlag().equals(CHAT_FLAG_FIRST_IMAGE + "")
					|| bean.getFlag() != null
					&& bean.getFlag().equals(CHAT_FLAG_POP_MESSAGE + "")
					&& bean.getChatStatus().equals("1")) {
				// 如果在消息列表里面发送的是图片
				if (bean.getContent() != null
						&& bean.getContent().equals(content)) {
					// url 相等
					ArrayList<ZanBean> list = new ArrayList<ZanBean>();
					list = chatList.get(i).getZanList();
					list.add(zanBean);
					chatList.get(i).setZanList(list);
					return;
				}

			} else if (bean.getFlag() != null
					&& bean.getFlag().equals(
							CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {

				if (bean.getContent() != null) {
					String curContent = bean.getContent().split(",")[0];

					if (curContent.equals(content)) {
						// url 相等
						ArrayList<ZanBean> list = new ArrayList<ZanBean>();
						list = chatList.get(i).getZanList();
						list.add(zanBean);
						chatList.get(i).setZanList(list);
						return;
					}
				}
			}
		}
	}

	/**
	 * 取消删除消息
	 */
	private void removeListItem(String chatID) {

		for (int i = 0; i < chatList.size(); i++) {
			ChatListBean bean = chatList.get(i);
			if (bean.getChatId() != null && bean.getChatId().equals(chatID)) {
				// 如果在消息列表里面发送的是图片
				// url 相等
				chatList.remove(i);
				return;
			}
		}
	}

	// *显示dialog*/
	// private void showsetheaddialog() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// new ContextThemeWrapper(ChatActivity.this,
	// R.style.CustomDiaLog_by_SongHang));
	// builder.setItems(new String[] { "拍照", "从相册选择" },
	// new AlertDialog.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// switch (which) {
	// case 0:
	// opencamera();
	// break;
	// case 1:
	// Intent intent_gralley = new Intent(
	// ChatActivity.this, PhotoActivity.class);
	// intent_gralley.putExtra(CameraActivity.IsRegister,
	// "0");
	// startActivityForResult(intent_gralley, 444);
	// break;
	// default:
	// break;
	// }
	// }
	//
	// });
	// builder.show();
	// }

	/* 打开相机 */
	private void opencamera() {

		Intent cameraIntent = new Intent(ChatActivity.this,
				CameraActivity.class);
		cameraIntent.putExtra(CameraActivity.IsRegister, "2");
		cameraIntent.putExtra(CameraActivity.IsFile, true);
		startActivityForResult(cameraIntent,
				ChatEasemobActivity.REQUEST_CODE_CAMERA);
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
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camera, ChatEasemobActivity.REQUEST_CODE_CAMERA);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ChatEasemobActivity.REQUEST_CODE_PICTURE) {
				// 相册
				List<String> filenames = (List<String>) data
						.getSerializableExtra(ShowImageActivity.SELECTIAMGE);
				setChatPhotoListPictrue(filenames);
				mChatPhotoAdatper.setList(chatPhotoSendList);
				mChatPhotoAdatper.notifyDataSetInvalidated();

			} else if (requestCode == ChatEasemobActivity.REQUEST_CODE_CAMERA) {
				// 照相

				if (data != null) {
					Uri selectedImage = data.getData();
					ContentResolver resolver = getContentResolver();
					Bitmap photo = null;
					try {
						if (selectedImage != null) {
							// // sendPicByUri(selectedImage);
							photo = MediaStore.Images.Media.getBitmap(resolver,
									selectedImage);

						} else {
							Bundle extras = data.getExtras();
							if (extras != null) {
								// 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
								photo = extras.getParcelable("data");

							}
							
						}
						if (photo != null) {
							// String str =
							
							if (photo.getWidth() > photo.getHeight()) {
								photo = ImageTool.rotateBitMap(photo, 1);
							}
							
							ImageTool.saveBitmapToAlbum(ChatActivity.this,
									photo);
							
							setChatPhotoListCamera(getChatImageLastName());
							mChatPhotoAdatper.setList(chatPhotoSendList);
							mChatPhotoAdatper.notifyDataSetInvalidated();
							return;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		if (selectDialog != null) {
			/** 使用SSO授权必须添加如下代码 */
			UMSsoHandler ssoHandler = MyUMSDK.mController.getConfig()
					.getSsoHandler(requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
				selectDialog = null;
			}
		}

	}

	private void setChatPhotoListPictrue(List<String> fileList) {
		for (int j = 0; j < fileList.size(); j++) {
			ChatPhotoBean bean = new ChatPhotoBean();
			bean.setImagePath(fileList.get(j));
			bean.setSelect(true);
			for (int i = chatPhotoSendList.size() - 1; i >= 0; i--) {
				if (!chatPhotoSendList.get(i).isSelect()) {
					chatPhotoSendList.remove(i);
					chatPhotoSendList.add(0, bean);
					break;
				}
			}
		}
	}

	private void setChatPhotoListCamera(String filePath) {
		ChatPhotoBean bean = new ChatPhotoBean();
		bean.setImagePath(filePath);
		bean.setSelect(true);
		for (int i = chatPhotoSendList.size() - 1; i >= 0; i--) {
			if (!chatPhotoSendList.get(i).isSelect()) {
				chatPhotoSendList.remove(i);
				chatPhotoSendList.add(0, bean);
				return;
			}
		}
	}

	private void postVioce(final String flag, final boolean isSendAgain,
			final String chatID, String name, String timeSecond) {

		// TODO Auto-generated method stub

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			String fileURl = FileURl.fileToString(FileURl.GoodsVoiceURL + name);
			if (fileURl == null) {
				return;
			}
			params.add(new BasicNameValuePair("arm", fileURl));
		} catch (Exception e) {

		}
		if (!isSendAgain) {
			String uuid = UUID.randomUUID().toString();

			addChatListForYourself("2", FileURl.GoodsVoiceURL + name, flag,
					uuid, timeSecond);

			Service.getService(Contanst.HTTP_UPLOADMESSAGEARM, null,
					uuid + "," + timeSecond, ChatActivity.this).addList(params)
					.request(UrlParams.POST);
		} else {
			Service.getService(Contanst.HTTP_UPLOADMESSAGEARM, null,
					chatID + "," + timeSecond, ChatActivity.this)
					.addList(params).request(UrlParams.POST);
		}

	}

	private void postImage(final String chatID, final int index,
			final String imagePath) {

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			return;
		}
		ImageLoader.getInstance().loadImage(imagePath,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub

						String str = PictureUtil.save(loadedImage);
						loadedImage.recycle();
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {
							params.add(new BasicNameValuePair("photo", str));
						} catch (Exception e) {

						}
						Service.getService(Contanst.HTTP_UPLOADMESSAGEIMG,
								null, (chatID + "," + index), ChatActivity.this)
								.addList(params).request(UrlParams.POST);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});

	}

	Dialog menberdialog;

	/**
	 * 成员详细信息 dialog
	 */
	/**
	 * @param userBean
	 */
	private void showMemberDialog(final UserBean userBean) {
		menberdialog = new Dialog(ChatActivity.this, R.style.MemberInfoDialog);
		menberdialog.setContentView(R.layout.member_dialog);
		menberdialog.setCanceledOnTouchOutside(true); // 点击外围消失

		RelativeLayout rl_group_member_info, menber_dialog_bg;
		TextView tv_group_member_name, tv_group_member_age, tv_group_member_level, tv_group_member_distance;
		ImageView iv_group_member_head, iv_group_member_aite, iv_group_member_jubao, iv_group_member_lock, iv_group_member_sixin, iv_group_member_attention;

		rl_group_member_info = (RelativeLayout) menberdialog
				.findViewById(R.id.rl_group_member_info);
		menber_dialog_bg = (RelativeLayout) menberdialog
				.findViewById(R.id.menber_dialog_bg);
		tv_group_member_name = (TextView) menberdialog
				.findViewById(R.id.tv_group_member_name);
		tv_group_member_age = (TextView) menberdialog
				.findViewById(R.id.tv_group_member_age);
		tv_group_member_level = (TextView) menberdialog
				.findViewById(R.id.tv_group_member_level);
		iv_group_member_head = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_head);
		iv_group_member_aite = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_aite);
		iv_group_member_jubao = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_jubao);
		iv_group_member_lock = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_lock);
		iv_group_member_attention = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_attention);
		iv_group_member_sixin = (ImageView) menberdialog
				.findViewById(R.id.iv_group_member_sixin);

		tv_group_member_distance = (TextView) menberdialog
				.findViewById(R.id.tv_group_member_distance);

		// if (distance != null && distance.length() > 0) {
		// tv_group_member_distance.setText(distance);
		// }

		if (!userID.equals(userBean.getId())) {
			String distance = null;
			try {
				distance = Geohash.GetDistance(
						Double.parseDouble(userBean.getLatitude()),
						Double.parseDouble(userBean.getLongitude()),
						Double.parseDouble(latitude),
						Double.parseDouble(longitude));
			} catch (Exception e) {
				distance = "火星";
			}

			if (distance.equals("火星")) {
				tv_group_member_distance.setText(distance);
			} else {
				tv_group_member_distance.setText(distance + "km");
			}
		} else {
			tv_group_member_distance.setVisibility(View.GONE);
		}

		rl_group_member_info.getBackground().setAlpha(229);

		tv_group_member_name.setText(userBean.getName());

		if (userBean.getStatus().equals("0")) {
			tv_group_member_age.setVisibility(View.GONE);
			tv_group_member_level.setVisibility(View.GONE);
		} else {

			try {
				tv_group_member_age.setText(TimeUtil.getAge(new Date(userBean
						.getBirthday())) + "岁");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tv_group_member_level.setText("lv" + userBean.getIntegral());

		}
		if (chatAdapter.getLockUserID() == null
				|| !userBean.getId().equals(chatAdapter.getLockUserID())) {
			iv_group_member_lock.setImageResource(R.drawable.chat_member_lock);

		} else {
			iv_group_member_lock
					.setImageResource(R.drawable.chat_member_unlock);
		}

		Log.i(  "aaa", "userBean.getIs_att() =========== "
				+ userBean.getIs_att());

		if (userBean.getSex().equals("1")) {
			tv_group_member_age.setBackgroundResource(R.drawable.shape_age);
			tv_group_member_age.setTextColor(getResources().getColor(
					R.color.meibao_color_9));
		} else {
			tv_group_member_age
					.setBackgroundResource(R.drawable.shape_age_girl);
			tv_group_member_age.setTextColor(getResources().getColor(
					R.color.meibao_color_10));
		}

		ImageLoader.getInstance().displayImage(
				UrlParams.IP + userBean.getPhoto(), iv_group_member_head,
				optionMember);
		menberdialog.show();

		if (userBean.getIs_att().equals("0")) {
			iv_group_member_attention
					.setImageResource(R.drawable.chat_member_cancel_attention);
		} else {
			iv_group_member_attention
					.setImageResource(R.drawable.chat_member_attention);
		}

		if (userBean.getId() != null && userBean.getId().equals(userID)) { // 自己不能@和举报自己，改变按钮颜色
			iv_group_member_aite
					.setImageResource(R.drawable.chat_member_aite_unclick);
			iv_group_member_jubao
					.setImageResource(R.drawable.chat_member_jubao_unclick);
			iv_group_member_attention
					.setImageResource(R.drawable.chat_member_unattention);
		}
		if (userBean.getStatus().equals("0")) {
			iv_group_member_attention
					.setImageResource(R.drawable.chat_member_unattention);
		}

		menber_dialog_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menberdialog.dismiss();
			}
		});

		// 关注
		iv_group_member_attention
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (!GroupApplication.getInstance().curSP
								.equals(Contanst.UserStone)) {
							showdialogGoRegister();
							return;
						} else if (userID.equals(userBean.getId())) {
							Toast.makeText(ChatActivity.this, "不能关注自己哦。",
									Toast.LENGTH_SHORT).show();
						} else if (userBean.getStatus().equals("0")) {

						} else {
							if (userBean.getIs_att().equals("1")) {
								connGetAttention(userBean.getId(), "0");
							} else {
								connGetAttention(userBean.getId(), "1");
							}
						}
						menberdialog.dismiss();
					}
				});

		if (userBean.getId().equals(userID)) {
			iv_group_member_sixin
					.setImageResource(R.drawable.chat_member_sixi2);
		} else {
			iv_group_member_sixin.setImageResource(R.drawable.chat_member_sixi);
		}
		/**
		 * 私信
		 */
		iv_group_member_sixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (userBean.getId().equals(userID)) { // 自己不能私信自己
					return;
				}
				Intent intent = new Intent(ChatActivity.this,
						ChatEasemobActivity.class);
				intent.putExtra("userId", userBean.getId());
				intent.putExtra("myNickName", userBean.getUser_name());
				intent.putExtra("myHead",
						UrlParams.IP + settings.getString("userPhoto", ""));
				intent.putExtra("myUserID", userID);
				intent.putExtra("chatType", ChatEasemobActivity.CHATTYPE_SINGLE);
				startActivity(intent);
				menberdialog.dismiss();
			}
		});

		/**
		 * 锁定好友
		 */
		iv_group_member_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (chatAdapter.getLockUserID() == null
						|| !userBean.getId()
								.equals(chatAdapter.getLockUserID())) {
					chatAdapter.setLockUserID(userBean.getId());
					chatAdapter.notifyDataSetInvalidated();
				} else {
					chatAdapter.setLockUserID(null);
					chatAdapter.notifyDataSetInvalidated();
				}
				menberdialog.dismiss();

			}
		});

		/**
		 * 发消息@当前好友
		 */
		iv_group_member_aite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!userBean.getId().equals(userID)) { // 自己不能@自己
					String id = userBean.getId();
					String name = userBean.getName();
					ZanBean bean = new ZanBean();
					bean.setZanUserID(id);
					bean.setZanUserName(name);
					clickAtConn(bean);
					// 发送@信息
					// sendChatOrImage(CHAT_FLAG_AT_MESSATE + "", photoUrl, "0",
					// otherUserId, otherUserName, chatID);
				}
				menberdialog.dismiss();
			}
		});
		/**
		 * 举报当前好友
		 */
		iv_group_member_jubao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!userBean.getId().equals(userID)) { // 自己不能举报自己
					Toast.makeText(ChatActivity.this, "举报成功",
							Toast.LENGTH_SHORT).show();
					removePopLayout();
					menberdialog.dismiss();
				}
			}
		});
	}

	/**
	 * 发送@的按钮
	 */
	private void clickAtConn(ZanBean bean) {
		ed_chatEdit.append("@" + bean.getZanUserName() + "\t ");
		zanList.add(bean);
		removePopLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clien.ClientMessageHandlerAdapter.Callback#connected()
	 */
	@Override
	public void connected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clien.ClientMessageHandlerAdapter.Callback#loggedIn()
	 */
	@Override
	public void loggedIn() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clien.ClientMessageHandlerAdapter.Callback#loggedOut()
	 */
	@Override
	public void loggedOut() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.clien.ClientMessageHandlerAdapter.Callback#disconnected()
	 */
	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clien.ClientMessageHandlerAdapter.Callback#messageReceived(java.lang
	 * .String)
	 */
	@Override
	public void messageReceived(String message) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.MINA_MESSAGE_RECEIVED;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clien.ClientMessageHandlerAdapter.Callback#error(java.lang.String)
	 */
	@Override
	public void error(String message) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.MINA_ERROR;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

	@Override
	public void messageSentReport(String message) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (MinaClient.getMinaClient().isClient())
			MinaClient.getMinaClient().session.close(true);
		if (socektTimer != null)
			socektTimer.cancel();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			removeChatErrorPop();
			if (isFacialShow) {
				removeGridView();
				chatPhotoListLayout.setVisibility(View.GONE);
				return false;
			} else if (popLayout.getVisibility() == View.VISIBLE) {
				removePopLayout();

				return false;
			} else {
				showdialogFinish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showdialogFinish() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("退出聊天？");
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intentdata = new Intent();
				setResult(222, intentdata);
				finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yktx.group.BaseActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 记录离开时间
		Editor edit = settings.edit();
		edit.putString("lastGroupID", groupID);
		edit.putLong("inGroupTime", System.currentTimeMillis());
		edit.commit();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		long times = settings
				.getLong("inGroupTime", System.currentTimeMillis());
		//
		// if (!isFirstInChat
		// && System.currentTimeMillis() - times > Contanst.LOGIN_TIME * 1000) {
		// Intent intentdata = new Intent();
		// setResult(222, intentdata);
		// ChatActivity.this.finish();
		// }
		// isFirstInChat = false;

	}

	private void showdialogGoRegister() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(mContext,
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

	PopShareDialog selectDialog = null;

	public void showShare() {
		// mController.setShareContent("请“正确”使用合体拍照 ———");
		// yktx2013
		selectDialog = new PopShareDialog(ChatActivity.this, R.style.dialog,
				groupID, null);// 创建Dialog并设置样式主题
		Window win = selectDialog.getWindow();
		android.view.WindowManager.LayoutParams params = new android.view.WindowManager.LayoutParams();
		// params.x = -80;//设置x坐标
		// params.y = -60;//设置y坐标
		win.setAttributes((android.view.WindowManager.LayoutParams) params);
		selectDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		selectDialog.show();
	}

	// 关注
	private void connGetAttention(String userID, String type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("tuser_id", this.userID));
			params.add(new BasicNameValuePair("puser_id", userID));
			params.add(new BasicNameValuePair("is_delete", type + ""));

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_ATTENTION, null, userID + "," + type,
				ChatActivity.this).addList(params).request(UrlParams.POST);

	}

	private void connGetUserInfo(String otherID) {

		isGetUserInfoConn = true;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {

			params.add(new BasicNameValuePair("myuser_id", userID));
			if (!userID.equals(otherID))
				params.add(new BasicNameValuePair("bruser_id", otherID));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETUSERINFO, null, null,
				ChatActivity.this).addList(params).request(UrlParams.POST);
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
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

}
