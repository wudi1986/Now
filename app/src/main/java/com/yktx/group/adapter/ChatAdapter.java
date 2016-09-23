package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.ChatListBean;
import com.yktx.bean.ZanBean;
import com.yktx.facial.ExpressionUtil;
import com.yktx.group.ChatActivity;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;
import com.yktx.util.Player;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
@SuppressLint("NewApi")
public class ChatAdapter extends BaseAdapter {
	Activity context;
	// protected ImageLoader imageLoader = ImageLoader.getInstance();

	/** 其他人 文字 */
	private static final int CHAT_ORTHER_TEXT = 0;
	/** 其他人 图片 */
	private static final int CHAT_ORTHER_PHOTO = 1;
	/** 自己说话 文字 */
	private static final int CHAT_MY_TEXT = 2;
	/** 自己说话 图片 */
	private static final int CHAT_MY_PHOTO = 3;
	/** 其他人@自己 */
	private static final int CHAT_ORTHER_AT_MY = 4;
	/** 其他人点自己赞 */
	private static final int CHAT_ORTHER_ZAN_MY = 5;
	/** 其他人给别人点赞 */
	private static final int CHAT_ORTHER_ZAN_OTHER = 6;
	/** 系统消息 */
	private static final int CHAT_SYSTEM = 7;
	/** 语音聊天 */
	private static final int CHAT_ORTHER_VOICE = 8;
	/** 多图或者图片和文字 */
	private static final int CHAT_IMAGE_AND_TEXT = 9;

	private OnClickZanButton onClickZanButton;

	private ShowLastItem showLastItem;

	private OnClickZanCenterButton onClickZanCenterButton;

	private OnClickAtButton onClickAtButton;

	private OnClickSendErrorButton onClickSendErrorButton;

	private OnClickUserCenterButton onClickUserCenterButton;
	
	private OnClickJuBaoButton onClickJuBaoButton;
	

	// private OnClickVoiceButton onClickVoiceButton;

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			// 启用内存缓存
			.displayer(new RoundedBitmapDisplayer(100)).build();

	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(null)
			.showImageOnFail(null).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).cacheInMemory(true)
			// 启用内存缓存
			// .displayer(new RoundedBitmapDisplayer(30))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions options3 = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			// 启用内存缓存
			// .displayer(new RoundedBitmapDisplayer(10))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	private ArrayList<ChatListBean> list;
	String userID, userName;
	HashMap<String, Object> chatFacial;

	/**
	 * 锁定用户id 如果id为null取消锁定
	 */
	private String lockUserID;

	Player myPlayer;

	int lastPlayerIndex;

	public ChatAdapter(Activity context) {
		this.context = context;
		if (myPlayer == null) {
			myPlayer = Player.getPlayer();
		}
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setChatFacialMap(HashMap<String, Object> chatFacial) {
		this.chatFacial = chatFacial;
	}

	public void setLockUserID(String lockUserID) {
		this.lockUserID = lockUserID;
	}

	public String getLockUserID() {
		return lockUserID;
	}

	public void setOnClickZanButton(OnClickZanButton onClickZanButton) {
		this.onClickZanButton = onClickZanButton;
	}

	public void setShowLastItem(ShowLastItem showLastItem) {
		this.showLastItem = showLastItem;
	}

	public void setOnClickZanCenterButton(
			OnClickZanCenterButton onClickZanCenterButton) {
		this.onClickZanCenterButton = onClickZanCenterButton;
	}

	public void setOnClickAtButton(OnClickAtButton onClickAtButton) {
		this.onClickAtButton = onClickAtButton;
	}

	public void setOnClickSendErrorButton(
			OnClickSendErrorButton onClickSendErrorButton) {
		this.onClickSendErrorButton = onClickSendErrorButton;
	}

	public void setOnClickUserCenterButton(
			OnClickUserCenterButton onClickUserCenterButton) {
		this.onClickUserCenterButton = onClickUserCenterButton;
	}

	public void setOnClickJuBaoButton(
			OnClickJuBaoButton onClickJuBaoButton) {
		this.onClickJuBaoButton = onClickJuBaoButton;
	}

	
	// public void setOnClickVoiceButton(
	// OnClickVoiceButton onClickVoiceButton) {
	// this.onClickVoiceButton = onClickVoiceButton;
	// }

	@Override
	public int getCount() {
		if (list != null)
			return list.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 根据数据源的position返回需要显示的的layout的type
	 * 
	 * type的值必须从0开始
	 * 
	 * */
	@Override
	public int getItemViewType(int position) {
		if (list == null) {
			return CHAT_SYSTEM;
		}
		ChatListBean chatBean = list.get(position);
		if (chatBean.getUserId() == null || chatBean.getUserId().equals("")) { // 系统消息
			return CHAT_SYSTEM;
		}

		String flag = chatBean.getFlag();

		if (flag.equals(ChatActivity.CHAT_FLAG_POP_MESSAGE + "")) { // 普通消息

			if (userID.equals(chatBean.getUserId())) {
				// 如果userid是自己
				if (chatBean.getChatStatus().equals("0")) { // 文本
					return CHAT_MY_TEXT;
				} else if (chatBean.getChatStatus().equals("1")) { // 图片
					return CHAT_MY_PHOTO;
				} else if (chatBean.getChatStatus().equals("2")) { // 语音
					return CHAT_ORTHER_VOICE;
				}
			} else {
				// userid不是自己
				if (chatBean.getChatStatus().equals("0")) { // 文本
					return CHAT_ORTHER_TEXT;
				} else if (chatBean.getChatStatus().equals("1")) { // 图片
					//
					return CHAT_ORTHER_PHOTO;
				} else if (chatBean.getChatStatus().equals("2")) { // 语音
					return CHAT_ORTHER_VOICE;
				}

			}

		} else if (flag.equals(ChatActivity.CHAT_FLAG_AT_MESSATE + "")) { // @
			// user_list
			if (userID.equals(chatBean.getUserId())) {
				// 自己@别人
				return CHAT_MY_TEXT;
			} else {
				String[] strArray = chatBean.getAtIDList();

				for (int i = 0; i < strArray.length; i++) {
					if (userID.equals(strArray[i])) { // 有@自己
						return CHAT_ORTHER_AT_MY;
					}
				}
				return CHAT_ORTHER_TEXT;
			}
		} else if (flag.equals(ChatActivity.CHAT_FLAG_FIRST_IMAGE + "")) { // 3第一次图片

			if (userID.equals(chatBean.getUserId())) {
				return CHAT_MY_PHOTO;
			} else {
				return CHAT_ORTHER_PHOTO;
			}

		} else if (flag.equals(ChatActivity.CHAT_FLAG_ZAN_MESSAGE + "")) { // @4
																			// 赞
			String arrayZan[] = chatBean.getContent_title().split(",");
			// Log.i(  "aaa",
			// "userID.equ "
			// + userID.equals(chatBean.getUserId())
			// +" userid === ||"+userID+"||  chatBean.getUserId() ====  ||"+chatBean.getUserId()+"||     arrayZan[0] ========= ||"+arrayZan[0]+"||");
			if (userID.equals(chatBean.getUserId())) { // 给别人点赞
														// content_title解析
				// 别人给你点赞
				return CHAT_ORTHER_ZAN_MY;
			} else {
				return CHAT_ORTHER_ZAN_OTHER;
			}
		} else if (flag.equals(ChatActivity.CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE
				+ "")) { // 多图或者文字加图片
			return CHAT_IMAGE_AND_TEXT;
		}
		return CHAT_SYSTEM;
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
	@Override
	public int getViewTypeCount() {
		return 10;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatAdapter.HoldView holdView;

		if (position >= list.size() - 1) {
			showLastItem.getClick();
		}
		int type = 0;
		type = getItemViewType(position);
		if (convertView == null) {
			ChatListBean chatBean = (ChatListBean) list.get(position);
			switch (type) {
			case CHAT_ORTHER_TEXT:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_item, null);
				break;
			case CHAT_ORTHER_PHOTO:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_photo_item, null);
				break;
			case CHAT_ORTHER_VOICE:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_voice_item, null);
				break;
			case CHAT_MY_TEXT:
				// convertView = LayoutInflater.from(context).inflate(
				// R.layout.chat_right_text_item, null);
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_item, null);
				break;
			case CHAT_MY_PHOTO:
				// convertView = LayoutInflater.from(context).inflate(
				// R.layout.chat_right_photo_item, null);
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_photo_item, null);
				break;
			case CHAT_ORTHER_AT_MY:
				// convertView = LayoutInflater.from(context).inflate(
				// R.layout.chat_right_text_at_item, null);
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_item, null);
				break;

			case CHAT_ORTHER_ZAN_MY:
				// convertView = LayoutInflater.from(context).inflate(
				// R.layout.chat_right_text_zan_item, null);
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_zan_item, null);
				break;
			case CHAT_ORTHER_ZAN_OTHER:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_zan_item, null);
				break;
			case CHAT_SYSTEM:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_center_text, null);
				break;
			case CHAT_IMAGE_AND_TEXT:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_left_text_and_image_item, null);
				break;
			}
			holdView = new ChatAdapter.HoldView(convertView);
			convertView.setTag(holdView);
			show(holdView, chatBean, type, position);
		} else {
			holdView = (HoldView) convertView.getTag();
			ChatListBean chatBean = (ChatListBean) list.get(position);
			show(holdView, chatBean, type, position);
		}

		return convertView;
	}

	private void show(final HoldView holdView, final ChatListBean chatBean,
			final int type, final int position) {
		// 0附近 1最新 2好有人脉3.获取在售的商品
		Log.i(  "aaa", "type ========= " + type);
		String flag = chatBean.getFlag();

		switch (type) {
		case CHAT_ORTHER_TEXT:
			drawChatStates(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			drawChatAgeAndLevel(chatBean, holdView);
			holdView.chat_text.setText(ChatTextFacial(chatBean.getContent()));
			holdView.chat_name_item.setText(chatBean.getUserName());
			if (chatBean.getDistance() != null
					&& chatBean.getDistance().equals("火星")) {
				holdView.chat_distance_item.setText(chatBean.getDistance());
			} else {
				holdView.chat_distance_item.setText(chatBean.getDistance()
						+ "km");
			}

			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			// holdView.chat_text.setOnClickListener(new View.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if (onClickAtButton != null) {
			// ZanBean bean = new ZanBean();
			// bean.setZanUserID(chatBean.getUserId());
			// bean.setZanUserName(chatBean.getUserName());
			// onClickAtButton.getClick(bean);
			// }
			// }
			// });

			break;
		case CHAT_IMAGE_AND_TEXT:
			drawChatStates(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			holdView.chat_text.setText(ChatTextFacial(chatBean
					.getContent_title()));
			// holdView.chat_name_item.setText(chatBean.getUserName());
			if (userID.equals(chatBean.getUserId())) {
				// 自己的消息
				holdView.chat_name_item.setText("我");
				holdView.chat_distance_item.setVisibility(View.GONE);
				holdView.imageView1.setVisibility(View.GONE);
			} else {
				holdView.chat_name_item.setText(chatBean.getUserName());
				if (chatBean.getDistance() != null
						&& chatBean.getDistance().equals("火星")) {
					holdView.chat_distance_item.setText(chatBean.getDistance());
				} else {
					holdView.chat_distance_item.setText(chatBean.getDistance()
							+ "km");
				}
			}
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);

			final String[] arrayImage = chatBean.getContent().split(",");
//			ImageView[] imageArray = { holdView.chatImage1,
//					holdView.chatImage2, holdView.chatImage3 };
			
			switch(arrayImage.length){
			case 1:
				holdView.chatImage1.setVisibility(View.GONE);
				holdView.chatImage2.setVisibility(View.GONE);
				holdView.chatImage3.setVisibility(View.GONE);
				holdView.chatImage2big.setVisibility(View.GONE);
				holdView.chatImage1big.setVisibility(View.VISIBLE);
				
				
				if (arrayImage[0].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[0], holdView.chatImage1big,
							options2);
				}
				holdView.chatImage1big.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 0, arrayImage[0]);
					}
				});

				break;
			case 2:
				
				holdView.chatImage1.setVisibility(View.VISIBLE);
				holdView.chatImage2.setVisibility(View.GONE);
				holdView.chatImage3.setVisibility(View.GONE);
				holdView.chatImage2big.setVisibility(View.VISIBLE);
				holdView.chatImage1big.setVisibility(View.GONE);
				
				if (arrayImage[0].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[0], holdView.chatImage1,
							options2);
				}
				if (arrayImage[1].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[1], holdView.chatImage2big,
							options2);
				}
				holdView.chatImage1.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 0, arrayImage[0]);
					}
				});
				holdView.chatImage2big.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 1, arrayImage[1]);
					}
				});
				break;
			case 3:

				holdView.chatImage1.setVisibility(View.VISIBLE);
				holdView.chatImage2.setVisibility(View.VISIBLE);
				holdView.chatImage3.setVisibility(View.VISIBLE);
				holdView.chatImage2big.setVisibility(View.GONE);
				holdView.chatImage1big.setVisibility(View.GONE);
				
				if (arrayImage[0].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[0], holdView.chatImage1,
							options2);
				}
				if (arrayImage[1].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[1], holdView.chatImage2,
							options2);
				}
				if (arrayImage[2].indexOf("file") == -1){
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + arrayImage[2], holdView.chatImage3,
							options2);
				}
				holdView.chatImage1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 0, arrayImage[0]);
					}
				});
				holdView.chatImage2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 1, arrayImage[1]);
					}
				});
				holdView.chatImage3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (onClickZanCenterButton != null)
							onClickZanCenterButton.getClick(chatBean.getChatId(),
									false, 2, arrayImage[2]);
					}
				});
				break;
			}
//			for (int i = 0; i < 3; i++) {
//				if (i < arrayImage.length) {
//					imageArray[i].setVisibility(View.VISIBLE);
//					if (arrayImage[i].indexOf("file") == -1) {
//						ImageLoader.getInstance().displayImage(
//								UrlParams.IP + arrayImage[i], imageArray[i],
//								options2);
//					} else {
//						ImageLoader.getInstance().displayImage(arrayImage[i],
//								imageArray[i], options2);
//					}
//				} else {
//					imageArray[i].setVisibility(View.GONE);
//				}
//
//			}

			drawChatStates(chatBean, holdView);
			drawChatAgeAndLevel(chatBean, holdView);
			initPhotoZan(chatBean, holdView, position);
			final boolean isZan = isCanZan(chatBean, arrayImage[0]);
			if (isZan) {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_unselect);
			} else {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_select);
			}

			holdView.zanbutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!chatBean.isCilckZan()
							&& chatBean.getContent().indexOf("file") == -1) {
						if (chatBean.getChatId() != null
								&& chatBean.getChatId().length() > 0) {
							if (isZan) {
								onClickZanButton.getClick(position,
										chatBean.getUserId(),
										chatBean.getUserName(), arrayImage[0],
										chatBean.getChatId());
							}
						}
					}
				}
			});

			break;

		case CHAT_MY_TEXT:
			drawChatStates(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			holdView.chat_text.setText(ChatTextFacial(chatBean.getContent()));
			// holdView.chat_name_item.setText(chatBean.getUserName());
			holdView.chat_name_item.setText("我");
			holdView.chat_distance_item.setVisibility(View.GONE);
			holdView.imageView1.setVisibility(View.GONE);
			holdView.chat_age_item.setVisibility(View.GONE);
			holdView.chat_level_item.setVisibility(View.GONE);
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			break;
		case CHAT_ORTHER_PHOTO:
			drawChatStates(chatBean, holdView);
			drawChatAgeAndLevel(chatBean, holdView);
			if (flag.equals(ChatActivity.CHAT_FLAG_FIRST_IMAGE + "")) { // 首次进群
				holdView.contentTitle.setVisibility(View.VISIBLE);
				holdView.contentTitle.setText(chatBean.getContent_title());
			} else {
				holdView.contentTitle.setVisibility(View.GONE);
			}
			initPhotoZan(chatBean, holdView, position);
			final boolean isZan1 = isCanZan(chatBean, chatBean.getContent());
			if (isZan1) {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_unselect);
			} else {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_select);
			}

			holdView.zanbutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!chatBean.isCilckZan()
							&& chatBean.getContent().indexOf("file") == -1) {
						if (chatBean.getChatId() != null
								&& chatBean.getChatId().length() > 0) {
							if (isZan1) {
								onClickZanButton.getClick(position,
										chatBean.getUserId(),
										chatBean.getUserName(),
										chatBean.getContent(),
										chatBean.getChatId());
							}
						}
					}
				}
			});

			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);

			if (chatBean.getContent().indexOf("file") == -1) {
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + chatBean.getContent(),
						holdView.chat_photo, options2);
			} else {
				ImageLoader.getInstance().displayImage(chatBean.getContent(),
						holdView.chat_photo, options2);
			}

			holdView.chat_photo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickZanCenterButton != null)
						onClickZanCenterButton.getClick(chatBean.getChatId(),
								false,0, chatBean.getContent());
				}
			});

			holdView.chat_name_item.setText(chatBean.getUserName());
			if (chatBean.getDistance() != null
					&& chatBean.getDistance().equals("火星")) {
				holdView.chat_distance_item.setText(chatBean.getDistance());
			} else {
				holdView.chat_distance_item.setText(chatBean.getDistance()
						+ "km");
			}
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			break;

		case CHAT_ORTHER_VOICE:
			drawChatStates(chatBean, holdView);
			drawChatAgeAndLevel(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			final String arrayContent[] = chatBean.getContent().split(",");
			final AnimationDrawable animationDrawable = (AnimationDrawable) holdView.chat_photo
					.getBackground();

			if (lastPlayerIndex != position && animationDrawable.isRunning()) {
				animationDrawable.stop();
				animationDrawable.selectDrawable(0);
			}
			holdView.chat_photo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// if(onClickVoiceButton != null){
					// onClickVoiceButton.getClick(arrayContent[0]);
					// 暂停
					if (lastPlayerIndex == position && myPlayer != null
							&& myPlayer.getMediaPlayer() != null
							&& myPlayer.getMediaPlayer().isPlaying()) {
						myPlayer.stop();
						animationDrawable.stop();
						myPlayer = null;
						animationDrawable.selectDrawable(0);
						return;
					}

					animationDrawable.start();
					lastPlayerIndex = position;
					ChatAdapter.this.notifyDataSetInvalidated();
					if (myPlayer == null) {
						myPlayer = Player.getPlayer();
					}
					if (arrayContent[0].indexOf("groupvoice") != -1) {
						myPlayer.playUrl(arrayContent[0]);
					} else {
						myPlayer.playUrl(UrlParams.IP + arrayContent[0]);
					}
					myPlayer.play();
					myPlayer.getMediaPlayer().setOnCompletionListener(
							new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									// TODO Auto-generated method stub

									animationDrawable.stop();
									myPlayer = null;
									animationDrawable.selectDrawable(0);
								}
							});
					// }
				}
			});
			if (userID.equals(chatBean.getUserId())) {
				holdView.chat_distance_item.setVisibility(View.GONE);
				holdView.imageView1.setVisibility(View.GONE);
				holdView.chat_age_item.setVisibility(View.GONE);
				holdView.chat_level_item.setVisibility(View.GONE);
			}

			if (arrayContent.length > 1) {
				holdView.chat_text.setText(arrayContent[1] + "''");
			}
			if (chatBean.getUserId().equals(userID)) {
				holdView.chat_name_item.setText("我");
			} else {
				holdView.chat_name_item.setText(chatBean.getUserName());
			}
			if (chatBean.getDistance() != null
					&& chatBean.getDistance().equals("火星")) {
				holdView.chat_distance_item.setText(chatBean.getDistance());
			} else {
				holdView.chat_distance_item.setText(chatBean.getDistance()
						+ "km");
			}
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));
			break;
		case CHAT_MY_PHOTO:
			drawChatStates(chatBean, holdView);
			if (flag.equals(ChatActivity.CHAT_FLAG_FIRST_IMAGE + "")) { // 首次进群
				holdView.contentTitle.setVisibility(View.VISIBLE);
				holdView.contentTitle.setText(chatBean.getContent_title());
			} else {
				holdView.contentTitle.setVisibility(View.GONE);
			}

			initPhotoZan(chatBean, holdView, position);

			final boolean isZanMy = isCanZan(chatBean, chatBean.getContent());
			if (isZanMy) {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_unselect);
			} else {
				holdView.zanbutton
						.setImageResource(R.drawable.chat_zan_select);
			}

			holdView.zanbutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!chatBean.isCilckZan()
							&& chatBean.getContent().indexOf("file") == -1) {
						if (chatBean.getChatId() != null
								&& chatBean.getChatId().length() > 0) {
							if (isZanMy) {
								// chatBean.setCilckZan(true);
								// holdView.zanbutton
								// .setImageResource(R.drawable.chat_zan_select);
								onClickZanButton.getClick(position,
										chatBean.getUserId(),
										chatBean.getUserName(),
										chatBean.getContent(),
										chatBean.getChatId());
							}
						}
					}
				}

			});

			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			String imageUrl = chatBean.getContent();
			Log.i(  "aaa", "imageUrl =============== " + imageUrl);
			if (imageUrl.indexOf("file") == -1
					&& imageUrl.indexOf("assets") == -1) {
				imageUrl = UrlParams.IP + imageUrl;
			}

			ImageLoader.getInstance().displayImage(imageUrl,
					holdView.chat_photo, options2);
			holdView.chat_photo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickZanCenterButton != null)
						onClickZanCenterButton.getClick(chatBean.getChatId(),
								false,0, chatBean.getContent());
				}
			});

			// holdView.chat_name_item.setText(chatBean.getUserName());
			holdView.chat_name_item.setText("我");
			holdView.chat_distance_item.setVisibility(View.GONE);
			holdView.imageView1.setVisibility(View.GONE);
			holdView.chat_age_item.setVisibility(View.GONE);
			holdView.chat_level_item.setVisibility(View.GONE);
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			break;
		case CHAT_ORTHER_AT_MY:
			drawChatAgeAndLevel(chatBean, holdView);
			drawChatStates(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			// holdView.chat_text.setText(chatBean.getContent());
			if (userName != null) {

				// String curContent =
				// ChatTextFacial(chatBean.getContent()).toString();

				SpannableString s = ChatTextFacial(chatBean.getContent());

				Pattern p = Pattern.compile("@" + userName);

				Matcher m = p.matcher(s);

				while (m.find()) {
					int start = m.start();
					int end = m.end();
					s.setSpan(new ForegroundColorSpan(context.getResources()
							.getColor(R.color.meibao_color_1)), start, end,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				holdView.chat_text.setText(s);
			} else {
				holdView.chat_text
						.setText(ChatTextFacial(chatBean.getContent()));
			}
			holdView.chat_name_item.setText(chatBean.getUserName());
			if (chatBean.getDistance() != null
					&& chatBean.getDistance().equals("火星")) {
				holdView.chat_distance_item.setText(chatBean.getDistance());
			} else {
				holdView.chat_distance_item.setText(chatBean.getDistance()
						+ "km");
			}
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));

			// holdView.chat_text.setOnClickListener(new View.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(View v) {
			//
			// // TODO Auto-generated method stub
			// if (onClickAtButton != null) {
			// ZanBean bean = new ZanBean();
			// bean.setZanUserID(chatBean.getUserId());
			// bean.setZanUserName(chatBean.getUserName());
			// onClickAtButton.getClick(bean);
			// }
			//
			// }
			// });

			break;

		case CHAT_ORTHER_ZAN_MY:
			drawChatAgeAndLevel(chatBean, holdView);
			drawChatStates(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);

			String array[] = chatBean.getContent_title().split(",");
			holdView.chat_text.setText("赞  " + array[1] + "！");
			holdView.chat_distance_item.setVisibility(View.GONE);
			holdView.imageView1.setVisibility(View.GONE);
			// holdView.chat_name_item.setText(chatBean.getUserName());
			holdView.chat_name_item.setText("我");
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getContent(), holdView.chat_photo,
					options3);

			holdView.chat_photo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickZanCenterButton != null)
						onClickZanCenterButton.getClick(
								chatBean.getPhotoChatID(), true,0, chatBean.getContent());
				}
			});

			// holdView.chat_text.setOnClickListener(new View.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// if (!chatBean.getUserId().equals(userID)) { // 自己不能@自己
			// // TODO Auto-generated method stub
			// if (onClickAtButton != null) {
			// ZanBean bean = new ZanBean();
			// bean.setZanUserID(chatBean.getUserId());
			// bean.setZanUserName(chatBean.getUserName());
			// onClickAtButton.getClick(bean);
			// }
			// }
			//
			// }
			// });
			holdView.chat_distance_item.setVisibility(View.GONE);
			holdView.imageView1.setVisibility(View.GONE);
			holdView.chat_age_item.setVisibility(View.GONE);
			holdView.chat_level_item.setVisibility(View.GONE);
			break;
		case CHAT_ORTHER_ZAN_OTHER:
			drawChatStates(chatBean, holdView);
			drawChatAgeAndLevel(chatBean, holdView);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getUserHead(), holdView.chat_head,
					options);
			String arrayZan[] = chatBean.getContent_title().split(",");
			holdView.chat_text.setText("赞  " + arrayZan[1] + "！");
			holdView.chat_name_item.setText(chatBean.getUserName());
			if (chatBean.getDistance() != null
					&& chatBean.getDistance().equals("火星")) {
				holdView.chat_distance_item.setText(chatBean.getDistance());
			} else {
				holdView.chat_distance_item.setText(chatBean.getDistance()
						+ "km");
			}
			holdView.chat_time_item.setText(TimeUtil.getTimes(chatBean
					.getSendTime()));
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + chatBean.getContent(), holdView.chat_photo,
					options3);
			holdView.chat_photo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickZanCenterButton != null)
						onClickZanCenterButton.getClick(
								chatBean.getPhotoChatID(), true,0, chatBean.getContent());

				}
			});

			// holdView.chat_text.setOnClickListener(new View.OnClickListener()
			// {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			//
			// // TODO Auto-generated method stub
			// if (onClickAtButton != null) {
			// ZanBean bean = new ZanBean();
			// bean.setZanUserID(chatBean.getUserId());
			// bean.setZanUserName(chatBean.getUserName());
			// onClickAtButton.getClick(bean);
			// }
			//
			// }
			// });

			break;
		case CHAT_SYSTEM:
			holdView.chat_text.setText(chatBean.getContent());
			break;
		}
		if (type != CHAT_SYSTEM) {
			holdView.chat_jubao.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickJuBaoButton != null)
						onClickJuBaoButton.getClick(
								chatBean.getUserName(), chatBean.getUserId());
					
				}
			});
			
			holdView.chat_head
					.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View arg0) {
							// TODO Auto-generated method stub
							if (!chatBean.getUserId().equals(userID)) { // 自己不能@自己
								if (onClickAtButton != null) {
									ZanBean bean = new ZanBean();
									bean.setZanUserID(chatBean.getUserId());
									bean.setZanUserName(chatBean.getUserName());
									onClickAtButton.getClick(bean);
								}
							}
							return true;
						}
					});

			holdView.chat_head.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickUserCenterButton != null)
						onClickUserCenterButton.getClick(
								chatBean.getUserName(), chatBean.getUserId(),
								chatBean.getUserHead(), chatBean.getBirthday(),
								chatBean.getSex(), chatBean.getLevel());
				}
			});

		}
	}

	private void drawChatAgeAndLevel(final ChatListBean chatBean,
			final HoldView holdView) {

		// try {
		// holdView.chat_age_item.setText(TimeUtil.getAge(new Date(chatBean
		// .getBirthday())) + "岁");
		holdView.chat_age_item.setVisibility(View.GONE);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (chatBean.getSex().equals("1")) {
		// holdView.chat_age_item.setTextColor(context.getResources()
		// .getColor(R.color.meibao_age_color));
		// holdView.chat_age_item.setBackgroundResource(R.drawable.shape_age);
		// } else {
		// holdView.chat_age_item.setTextColor(context.getResources()
		// .getColor(R.color.meibao_age_girl_color));
		// holdView.chat_age_item
		// .setBackgroundResource(R.drawable.shape_age_girl);
		// }
		// holdView.chat_level_item.setText("lv" + chatBean.getLevel());
		holdView.chat_level_item.setVisibility(View.GONE);
	}

	private void drawChatStates(final ChatListBean chatBean,
			final HoldView holdView) {

		if (lockUserID != null && lockUserID.equals(chatBean.getUserId())) {
			holdView.chat_head_lock.setVisibility(View.VISIBLE);
		} else {
			holdView.chat_head_lock.setVisibility(View.GONE);
		}
		// TODO Auto-generated method stub
		switch (chatBean.getChatSendStates()) {
		case Contanst.CHAT_SEND_SUCCESS:
			holdView.chatProgressBar.setVisibility(View.GONE);
			holdView.chat_error_image.setVisibility(View.GONE);
			break;
		case Contanst.CHAT_SEND_ING:
			holdView.chatProgressBar.setVisibility(View.VISIBLE);
			holdView.chat_error_image.setVisibility(View.GONE);
			break;
		case Contanst.CHAT_SEND_FAIL:
			holdView.chatProgressBar.setVisibility(View.GONE);
			holdView.chat_error_image.setVisibility(View.VISIBLE);
			holdView.chat_error_image
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							int[] location = new int[2];
							holdView.chat_error_image
									.getLocationOnScreen(location);
							int x = location[0];
							int y = location[1];
							onClickSendErrorButton.getClick(chatBean, x, y);
						}
					});

			break;
		}
	}

	private SpannableString ChatTextFacial(String chatContent) {

		SpannableString spannableString = null;
		String zhengze = "\\[#[0-9][0-9]?\\]";
		try {
			spannableString = ExpressionUtil.getExpressionString(context,
					chatContent, zhengze, chatFacial);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spannableString;
	}

	private boolean isCanZan(ChatListBean chatBean, String content) {
		ArrayList<ZanBean> zanList = chatBean.getZanList();
		int size = zanList.size();
		for (int i = 0; i < size; i++) {
			ZanBean bean = zanList.get(i);
			if (bean.getZanUserID().equals(userID)) {
				// 如果赞的list里面有自己的id 已经赞过，不能在点击
				if (content.indexOf("file:") == -1) {
					return false;
				}
			}
		}

		return true;
	}

	private void initPhotoZan(final ChatListBean chatBean, HoldView holdView,
			final int position) {
		final ArrayList<ZanBean> zanList = chatBean.getZanList();
		int size = zanList.size();
		if (size < 4) {
			switch (size) {
			case 0:
				holdView.chat_zan_more.setVisibility(View.GONE);
				holdView.chat_zan_layout3.setVisibility(View.GONE);
				holdView.chat_zan_layout2.setVisibility(View.GONE);
				holdView.chat_zan_layout1.setVisibility(View.GONE);
				// holdView.chat_zan_head3.setVisibility(View.GONE);
				// holdView.chat_zan_head2.setVisibility(View.GONE);
				// holdView.chat_zan_head1.setVisibility(View.GONE);
				// holdView.chat_zan_facial3.setVisibility(View.GONE);
				// holdView.chat_zan_facial2.setVisibility(View.GONE);
				// holdView.chat_zan_facial1.setVisibility(View.GONE);
				break;
			case 1:
				holdView.chat_zan_more.setVisibility(View.GONE);
				// holdView.chat_zan_head3.setVisibility(View.GONE);
				// holdView.chat_zan_head2.setVisibility(View.GONE);
				// holdView.chat_zan_head1.setVisibility(View.VISIBLE);
				holdView.chat_zan_layout3.setVisibility(View.GONE);
				holdView.chat_zan_layout2.setVisibility(View.GONE);
				holdView.chat_zan_layout1.setVisibility(View.VISIBLE);

				// holdView.chat_zan_facial3.setVisibility(View.GONE);
				// holdView.chat_zan_facial2.setVisibility(View.GONE);
				// holdView.chat_zan_facial1.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(0).getZanUserHead(),
						holdView.chat_zan_head1, options);
				holdView.chat_zan_facial1
						.setImageResource(getZanFacialNum(zanList.get(0)
								.getBrow()));
				holdView.chat_zan_head1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {
									ZanBean bean = zanList.get(0);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());
								}
							}
						});
				break;
			case 2:
				holdView.chat_zan_more.setVisibility(View.GONE);
				holdView.chat_zan_layout3.setVisibility(View.GONE);
				holdView.chat_zan_layout2.setVisibility(View.VISIBLE);
				holdView.chat_zan_layout1.setVisibility(View.VISIBLE);

				// holdView.chat_zan_head3.setVisibility(View.GONE);
				// holdView.chat_zan_head2.setVisibility(View.VISIBLE);
				// holdView.chat_zan_head1.setVisibility(View.VISIBLE);
				// holdView.chat_zan_facial3.setVisibility(View.GONE);
				// holdView.chat_zan_facial2.setVisibility(View.VISIBLE);
				// holdView.chat_zan_facial1.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(0).getZanUserHead(),
						holdView.chat_zan_head2, options);
				holdView.chat_zan_facial2
						.setImageResource(getZanFacialNum(zanList.get(0)
								.getBrow()));
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(1).getZanUserHead(),
						holdView.chat_zan_head1, options);
				holdView.chat_zan_facial1
						.setImageResource(getZanFacialNum(zanList.get(1)
								.getBrow()));
				holdView.chat_zan_head2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {

									ZanBean bean = zanList.get(0);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());

								}

							}
						});
				holdView.chat_zan_head1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {

									ZanBean bean = zanList.get(1);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());

								}

							}
						});

				break;
			case 3:
				holdView.chat_zan_more.setVisibility(View.GONE);
				holdView.chat_zan_layout3.setVisibility(View.VISIBLE);
				holdView.chat_zan_layout2.setVisibility(View.VISIBLE);
				holdView.chat_zan_layout1.setVisibility(View.VISIBLE);
				// holdView.chat_zan_head3.setVisibility(View.VISIBLE);
				// holdView.chat_zan_head2.setVisibility(View.VISIBLE);
				// holdView.chat_zan_head1.setVisibility(View.VISIBLE);
				// holdView.chat_zan_facial3.setVisibility(View.VISIBLE);
				// holdView.chat_zan_facial2.setVisibility(View.VISIBLE);
				// holdView.chat_zan_facial1.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(2).getZanUserHead(),
						holdView.chat_zan_head3, options);
				holdView.chat_zan_facial3
						.setImageResource(getZanFacialNum(zanList.get(2)
								.getBrow()));
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(1).getZanUserHead(),
						holdView.chat_zan_head2, options);
				holdView.chat_zan_facial2
						.setImageResource(getZanFacialNum(zanList.get(1)
								.getBrow()));
				ImageLoader.getInstance().displayImage(
						UrlParams.IP + zanList.get(0).getZanUserHead(),
						holdView.chat_zan_head1, options);
				holdView.chat_zan_facial1
						.setImageResource(getZanFacialNum(zanList.get(0)
								.getBrow()));
				holdView.chat_zan_head3
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {

									ZanBean bean = zanList.get(2);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());

								}
							}
						});
				holdView.chat_zan_head2
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {

									ZanBean bean = zanList.get(1);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());

								}

							}
						});
				holdView.chat_zan_head1
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								if (onClickUserCenterButton != null) {

									ZanBean bean = zanList.get(0);

									onClickUserCenterButton.getClick(
											bean.getZanUserName(),
											bean.getZanUserID(),
											bean.getZanUserHead(),
											bean.getBirthday(), bean.getSex(),
											bean.getLevel());

								}

							}
						});
				break;
			}
		} else {
			final String array[] = chatBean.getContent().split(",");
			holdView.chat_zan_more.setVisibility(View.VISIBLE);
			holdView.chat_zan_more.setBackgroundResource(R.drawable.kongtu);
			holdView.chat_zan_more
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickZanCenterButton != null)
								onClickZanCenterButton.getClick(
										chatBean.getChatId(), false,0, array[0]);
						}
					});

			holdView.chat_zan_layout3.setVisibility(View.VISIBLE);
			holdView.chat_zan_layout2.setVisibility(View.VISIBLE);
			holdView.chat_zan_layout1.setVisibility(View.VISIBLE);
			// holdView.chat_zan_head3.setVisibility(View.VISIBLE);
			// holdView.chat_zan_head2.setVisibility(View.VISIBLE);
			// holdView.chat_zan_head1.setVisibility(View.VISIBLE);
			// holdView.chat_zan_facial3.setVisibility(View.VISIBLE);
			// holdView.chat_zan_facial2.setVisibility(View.VISIBLE);
			// holdView.chat_zan_facial1.setVisibility(View.VISIBLE);
			holdView.chat_zan_more
					.setImageResource(R.drawable.chat_image_zan_more);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + zanList.get(0).getZanUserHead(),
					holdView.chat_zan_head3, options);
			holdView.chat_zan_facial3.setImageResource(getZanFacialNum(zanList
					.get(0).getBrow()));
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + zanList.get(1).getZanUserHead(),
					holdView.chat_zan_head2, options);
			holdView.chat_zan_facial2.setImageResource(getZanFacialNum(zanList
					.get(1).getBrow()));
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + zanList.get(2).getZanUserHead(),
					holdView.chat_zan_head1, options);
			holdView.chat_zan_facial1.setImageResource(getZanFacialNum(zanList
					.get(2).getBrow()));
			holdView.chat_zan_head3
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickUserCenterButton != null) {

								ZanBean bean = zanList.get(0);

								onClickUserCenterButton.getClick(
										bean.getZanUserName(),
										bean.getZanUserID(),
										bean.getZanUserHead(),
										bean.getBirthday(), bean.getSex(),
										bean.getLevel());

							}

						}
					});
			holdView.chat_zan_head2
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickUserCenterButton != null) {

								ZanBean bean = zanList.get(1);

								onClickUserCenterButton.getClick(
										bean.getZanUserName(),
										bean.getZanUserID(),
										bean.getZanUserHead(),
										bean.getBirthday(), bean.getSex(),
										bean.getLevel());

							}

						}
					});
			holdView.chat_zan_head1
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickUserCenterButton != null) {

								ZanBean bean = zanList.get(2);

								onClickUserCenterButton.getClick(
										bean.getZanUserName(),
										bean.getZanUserID(),
										bean.getZanUserHead(),
										bean.getBirthday(), bean.getSex(),
										bean.getLevel());

							}
						}
					});

		}

	}

	private int getZanFacialNum(int facialIndex) {
		if (facialIndex == 0) {
			return Contanst.CHAT_FACIAL_SMALL[4];
		} else {
			return Contanst.CHAT_FACIAL_SMALL[facialIndex - 1];
		}
	}

	private boolean isHaveUser(ArrayList<ZanBean> list, String userId) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getZanUserID().equals(userId)) {
				return true;
			}
		}

		return false;
	}

	private class HoldView {
		private ImageView chat_head, chat_photo, imageView1, chat_head_lock,
				chatImage1, chatImage2, chatImage3,chatImage1big;
		private TextView chat_text, chat_name_item, chat_distance_item,
				chat_time_item, contentTitle, chat_level_item, chat_age_item
				// loca_item_age, sellLvl, buyLvl,
				// loca_item_lvl,
				// jiaobiao
				;
		private ImageView chat_zan_head1, chat_zan_more, chat_zan_head2,
				chat_zan_head3, zanbutton, chat_error_image, chat_zan_facial1,
				chat_zan_facial2, chat_zan_facial3,chat_jubao;
		private ProgressBar chatProgressBar;

		RelativeLayout chat_zan_layout1, chat_zan_layout2, chat_zan_layout3;
		ImageView chatImage2big;
		// ImageView guanxi;
		// LinearLayout buyLayout, sellLayout;

		public HoldView(View convertView) {
			chat_jubao = (ImageView) convertView.findViewById(R.id.chat_jubao);
			chat_head = (ImageView) convertView.findViewById(R.id.chat_head);
			chat_photo = (ImageView) convertView.findViewById(R.id.chat_photo);
			imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			chatImage1 = (ImageView) convertView.findViewById(R.id.chatImage1);
			chatImage2 = (ImageView) convertView.findViewById(R.id.chatImage2);
			chatImage3 = (ImageView) convertView.findViewById(R.id.chatImage3);
			chatImage1big = (ImageView) convertView.findViewById(R.id.chatImage1big);
			
			chat_text = (TextView) convertView.findViewById(R.id.chat_text);
			chatImage2big = (ImageView) convertView.findViewById(R.id.chatImage2big);

			chat_head_lock = (ImageView) convertView
					.findViewById(R.id.chat_head_lock);

			chat_name_item = (TextView) convertView
					.findViewById(R.id.chat_name_item);
			chat_distance_item = (TextView) convertView
					.findViewById(R.id.chat_distance_item);
			chat_time_item = (TextView) convertView
					.findViewById(R.id.chat_time_item);
			contentTitle = (TextView) convertView
					.findViewById(R.id.contentTitle);
			chat_age_item = (TextView) convertView
					.findViewById(R.id.chat_age_item);
			chat_level_item = (TextView) convertView
					.findViewById(R.id.chat_level_item);

			chat_zan_head1 = (ImageView) convertView
					.findViewById(R.id.chat_zan_head1);
			chat_zan_head2 = (ImageView) convertView
					.findViewById(R.id.chat_zan_head2);
			chat_zan_head3 = (ImageView) convertView
					.findViewById(R.id.chat_zan_head3);
			chat_zan_more = (ImageView) convertView
					.findViewById(R.id.chat_zan_headmore);
			zanbutton = (ImageView) convertView.findViewById(R.id.zanbutton);
			chat_error_image = (ImageView) convertView
					.findViewById(R.id.chat_error_image);
			chat_zan_facial1 = (ImageView) convertView
					.findViewById(R.id.chat_zan_facial1);
			chat_zan_facial2 = (ImageView) convertView
					.findViewById(R.id.chat_zan_facial2);
			chat_zan_facial3 = (ImageView) convertView
					.findViewById(R.id.chat_zan_facial3);

			chat_zan_layout1 = (RelativeLayout) convertView
					.findViewById(R.id.chat_zan_layout1);
			chat_zan_layout2 = (RelativeLayout) convertView
					.findViewById(R.id.chat_zan_layout2);
			chat_zan_layout3 = (RelativeLayout) convertView
					.findViewById(R.id.chat_zan_layout3);

			chatProgressBar = (ProgressBar) convertView
					.findViewById(R.id.chatProgressBar);

		}
	}

	public void setItemList(ArrayList<ChatListBean> chatList) {
		// TODO Auto-generated method stub
		this.list = chatList;
	}

	public interface OnClickZanButton {
		public void getClick(int ooption, String otherUserId,
				String otherUserName, String photoUrl, String chatID);
	}

	public interface ShowLastItem {
		public void getClick();
	}

	public interface OnClickZanCenterButton {
		public void getClick(String photoChatID, boolean isZan , int index, String imageUrl);
	}

	/**
	 * @author Administrator at按钮
	 */
	public interface OnClickAtButton {
		public void getClick(ZanBean bean);
	}

	/**
	 * @author Administrator at按钮
	 */
	public interface OnClickSendErrorButton {
		public void getClick(ChatListBean bean, int x, int y);
	}

	/**
	 * @author Administrator 跳转个人主页的监听
	 */
	public interface OnClickUserCenterButton {
		public void getClick(String userName, String userID, String userHead,
				long birthday, String sex, String level);
	}
	/**
	 * @author Administrator 举报监听
	 */
	public interface OnClickJuBaoButton {
		public void getClick(String userName, String userID);
	}

	// /**
	// * @author Administrator 跳转个人主页的监听
	// */
	// public interface OnClickVoiceButton {
	// public void getClick(String voiceUrl);
	// }

}
