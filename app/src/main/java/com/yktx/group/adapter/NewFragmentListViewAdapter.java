package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.NewMainBean;
import com.yktx.facial.ExpressionUtil;
import com.yktx.facial.Expressions;
import com.yktx.group.ChatActivity;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.util.Geohash;
import com.yktx.util.Player;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
public class NewFragmentListViewAdapter extends BaseAdapter {
	Activity context;
	String latitude = "-1";
	String longitude = "-1";
	IntoUserCenterListener intoUserCenter;
	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
			.displayer(new RoundedBitmapDisplayer(100))
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<NewMainBean> list = new ArrayList<NewMainBean>(10);

	// Player myPlayer;

	int lastPlayerIndex = -1;

	public void setLastPlayerIndex(int lastPlayerIndex) {
		this.lastPlayerIndex = lastPlayerIndex;
	}

	public HashMap<String, Object> chatFacialMap = new HashMap<String, Object>();

	private void initFacialMap() {
		for (int i = 0; i < Expressions.expressionImgNames.length; i++) {
			chatFacialMap.put(Expressions.expressionImgNames[i],
					Expressions.expressionImgs[i]);
		}

	}

	OnClickPicture onClickPicture;
	OnClickGroupName onClickGroupName;

	public void setOnClickPicture(OnClickPicture onClickPicture) {
		this.onClickPicture = onClickPicture;
	}

	public void setOnClickGroupName(OnClickGroupName onClickGroupName) {
		this.onClickGroupName = onClickGroupName;
	}

	String userID;

	public NewFragmentListViewAdapter(Activity context, String userID) {
		this.context = context;
		if (Player.myPlayer == null) {
			Player.getPlayer();
		}
		this.userID = userID;
		initFacialMap();
	}

	public void setList(ArrayList<NewMainBean> list) {
		this.list = list;
	}

	public void setIntoUserCenter(IntoUserCenterListener intoUserCenter) {
		this.intoUserCenter = intoUserCenter;
	}

	public void setDistance(String latitude, String longitude) {
		// TODO Auto-generated method stub
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		NewMainBean newMainBean = (NewMainBean) list.get(position);
		if (newMainBean.getFlag().equals(
				ChatActivity.CHAT_FLAG_POP_MESSAGE + "")) {
			if (newMainBean.getStatus().equals("0")) {// 文字
				return 1;
			} else if (newMainBean.getStatus().equals("1")) {
				return 2;
			} else {
				return 4;
			}
		} else if (newMainBean.getFlag().equals(
				ChatActivity.CHAT_FLAG_FIRST_IMAGE + "")) {
			return 2;
		} else if (newMainBean.getFlag().equals(
				ChatActivity.CHAT_FLAG_ZAN_MESSAGE + "")) {
			return 3;
		} else if (newMainBean.getFlag().equals(
				ChatActivity.CHAT_FLAG_AT_MESSATE + "")) {
			return 1;
		} else if (newMainBean.getFlag().equals(
				ChatActivity.CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE + "")) {
			return 5;
		}
		return 1;
	}

	private SpannableString ChatTextFacial(String chatContent) {

		SpannableString spannableString = null;
		String zhengze = "\\[#[0-9][0-9]?\\]";
		try {
			spannableString = ExpressionUtil.getExpressionString(context,
					chatContent, zhengze, chatFacialMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spannableString;
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
	@Override
	public int getViewTypeCount() {
		return 6;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView;
		int type = 1;
		type = getItemViewType(position);
		NewMainBean newMainBean = (NewMainBean) list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.new_fragment_item, null);
			holdView = new NewFragmentListViewAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, newMainBean, position, type);

		return convertView;
	}

	private void show(final HoldView holdView, final NewMainBean newMainBean,
			final int position, final int type) {

		ImageLoader.getInstance().displayImage(
				UrlParams.IP + newMainBean.getPhoto(), holdView.newUserHead,
				options);
		holdView.newUserHead.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intoUserCenter.getIntoUserCenter(newMainBean.getUser_id());
			}
		});

		switch (type) {
		case 1:
			holdView.new_chat_text.setText(ChatTextFacial(newMainBean
					.getContent()));
			holdView.imageLayout.setVisibility(View.GONE);
			holdView.newZanImage.setVisibility(View.GONE);
			holdView.newZanNum.setVisibility(View.GONE);
			break;
		case 2:
			holdView.new_chat_text.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + newMainBean.getContent(),
					holdView.new_chat_photo, options2);
			holdView.new_chat_photo2.setVisibility(View.GONE);
			holdView.new_chat_photo3.setVisibility(View.GONE);
			holdView.new_chat_photo
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickPicture != null) {
								onClickPicture.getPicture(
										newMainBean.getMessage_id(), 0, newMainBean.getContent());
							}
						}
					});

			holdView.newZanImage.setVisibility(View.VISIBLE);
			holdView.newZanNum.setVisibility(View.VISIBLE);
			holdView.newZanImage
					.setBackgroundResource(R.drawable.chat_image_zan_unselect);
			holdView.newZanNum.setText(newMainBean.getZan_num() + "");

			break;
		case 3:
			final String array[] = newMainBean.getContent_title().split(",");
			holdView.new_chat_text.setText("赞  " + array[1] + "！");
			final String array2[] = newMainBean.getContent().split(",");
			ImageLoader.getInstance().displayImage(UrlParams.IP + array2[0],
					holdView.new_chat_photo, options2);
			holdView.new_chat_photo
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (onClickPicture != null) {
								String arrayZan[] = newMainBean.getContent()
										.split(",");
								onClickPicture.getPicture(arrayZan[1], 0, array2[0]);
							}
						}
					});

			holdView.newZanImage.setVisibility(View.GONE);
			holdView.newZanNum.setVisibility(View.GONE);
//			holdView.newZanImage
//					.setBackgroundResource(R.drawable.chat_image_zan_unselect);
//			holdView.newZanNum.setText(newMainBean.getZan_num() + "");

			break;
		case 4:
			final String array3[] = newMainBean.getContent().split(",");
			holdView.new_chat_text.setVisibility(View.GONE);
			holdView.imageLayout.setVisibility(View.GONE);
			holdView.voice_text.setVisibility(View.VISIBLE);
			if (array3.length > 1) {
				holdView.voice_text.setText(array3[1] + "''");
			}
			holdView.voice_photo.setVisibility(View.VISIBLE);
			final AnimationDrawable animationDrawable = (AnimationDrawable) holdView.voice_photo
					.getBackground();

			if (lastPlayerIndex != position && animationDrawable.isRunning()) {
				animationDrawable.stop();
				animationDrawable.selectDrawable(0);
			}
			final String arrayContent[] = newMainBean.getContent().split(",");
			holdView.voice_photo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// if(onClickVoiceButton != null){
					// onClickVoiceButton.getClick(arrayContent[0]);

					// 暂停
					if (lastPlayerIndex == position && Player.myPlayer != null
							&& Player.myPlayer.getMediaPlayer() != null
							&& Player.myPlayer.getMediaPlayer().isPlaying()) {
						Player.myPlayer.stop();
						animationDrawable.stop();
						Player.myPlayer = null;
						animationDrawable.selectDrawable(0);
						return;
					}

					animationDrawable.start();
					lastPlayerIndex = position;
					if (Player.myPlayer == null) {
						// myPlayer = new Player();
						Player.getPlayer();
					}
					if (arrayContent[0].indexOf("groupvoice") != -1) {
						Player.myPlayer.playUrl(arrayContent[0]);
					} else {
						Player.myPlayer.playUrl(UrlParams.IP + arrayContent[0]);
					}
					Player.myPlayer.play();
					NewFragmentListViewAdapter.this.notifyDataSetChanged();
					Player.myPlayer.getMediaPlayer().setOnCompletionListener(
							new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									// TODO Auto-generated method stub

									animationDrawable.stop();
									Player.myPlayer = null;
									animationDrawable.selectDrawable(0);
								}
							});
					// }
				}
			});
			holdView.newZanImage.setVisibility(View.GONE);
			holdView.newZanNum.setVisibility(View.GONE);
			break;

		case 5:

			final String contentString[] = newMainBean.getContent().split(",");
			ImageView imageArray[] = { holdView.new_chat_photo,
					holdView.new_chat_photo2, holdView.new_chat_photo3 };
			String info = newMainBean.getContent_title();
			if (info != null && info.length() > 0) {
				holdView.new_chat_text.setVisibility(View.VISIBLE);
				holdView.new_chat_text.setText(info);
			} else {
				holdView.new_chat_text.setVisibility(View.GONE);
			}
			for (int i = 0; i < imageArray.length; i++) {
				final int index = i;
				if (i < contentString.length) {
					imageArray[i].setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(
							UrlParams.IP + contentString[i], imageArray[i],
							options2);
					imageArray[i]
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									if (onClickPicture != null) {
										onClickPicture.getPicture(
												newMainBean.getMessage_id(),
												index, newMainBean.getContent());
									}
								}
							});
				} else {
					imageArray[i].setVisibility(View.GONE);
				}
			}

			holdView.newZanImage.setVisibility(View.VISIBLE);
			holdView.newZanNum.setVisibility(View.VISIBLE);
			holdView.newZanImage
					.setBackgroundResource(R.drawable.chat_image_zan_unselect);
			holdView.newZanNum.setText(newMainBean.getZan_num() + "");

			break;
		}

		holdView.newUserName.setText(newMainBean.getUser_name());
		holdView.newUserAge.setVisibility(View.GONE);
		// try {
		// holdView.newUserAge.setText(TimeUtil.getAge(new Date(newMainBean
		// .getBirthday())) + "岁");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		holdView.newUserLevel.setVisibility(View.GONE);
		// holdView.newUserLevel.setText("Lv" + newMainBean.getIntegral());
		//

		if (!userID.equals(newMainBean.getUser_id())) {
			String distance = null;
			try {
				distance = Geohash.GetDistance(
						Double.parseDouble(newMainBean.getLatitude()),
						Double.parseDouble(newMainBean.getLongitude()),
						Double.parseDouble(latitude),
						Double.parseDouble(longitude));
			} catch (Exception e) {
				distance = "火星";
			}

			if (distance.equals("火星")) {
				holdView.newUserDistance.setText(distance);
			} else {
				holdView.newUserDistance.setText(distance + "km");
			}
		} else {
			// holdView.new_distansImage.setVisibility(View.GONE);
			holdView.newUserDistance.setVisibility(View.GONE);
		}
		holdView.newSendTime.setText(TimeUtil.getTimes(newMainBean
				.getSend_time()));
		holdView.newGroupName.setText("#"+newMainBean.getGroup_name());
		holdView.newGroupName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (onClickGroupName != null) {
					onClickGroupName.getGroupName(position);
				}
			}
		});
		holdView.newGroupPeopleNum.setText(newMainBean.getLinmun() + "人在线");
	}

	public class HoldView {
		public ImageView newUserHead, new_chat_photo, new_chat_photo2,
				new_chat_photo3, voice_photo, newZanImage
				// ,new_distansImage
				;
		public TextView newUserName, newUserAge, newUserLevel, newUserDistance,
				new_chat_text, newGroupName, newGroupPeopleNum, voice_text,
				newSendTime, newZanNum;

		LinearLayout imageLayout;

		public HoldView(View convertView) {
			newUserHead = (ImageView) convertView
					.findViewById(R.id.newUserHead);
			new_chat_photo = (ImageView) convertView
					.findViewById(R.id.new_chat_photo);
			new_chat_photo2 = (ImageView) convertView
					.findViewById(R.id.new_chat_photo2);
			new_chat_photo3 = (ImageView) convertView
					.findViewById(R.id.new_chat_photo3);
			newZanImage = (ImageView) convertView
					.findViewById(R.id.newZanImage);
			imageLayout = (LinearLayout) convertView
					.findViewById(R.id.imageLayout);

			// new_distansImage = (ImageView) convertView
			// .findViewById(R.id.new_distansImage);

			newUserName = (TextView) convertView.findViewById(R.id.newUserName);
			newUserAge = (TextView) convertView.findViewById(R.id.newUserAge);
			newUserLevel = (TextView) convertView
					.findViewById(R.id.newUserLevel);
			newUserDistance = (TextView) convertView
					.findViewById(R.id.newUserDistance);
			new_chat_text = (TextView) convertView
					.findViewById(R.id.new_chat_text);
			newZanNum = (TextView) convertView.findViewById(R.id.newZanNum);
			newGroupName = (TextView) convertView
					.findViewById(R.id.newGroupName);
			newGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.newGroupPeopleNum);
			voice_photo = (ImageView) convertView
					.findViewById(R.id.voice_photo);
			voice_text = (TextView) convertView.findViewById(R.id.voice_text);
			newSendTime = (TextView) convertView.findViewById(R.id.newSendTime);
		}
	}

	public interface OnClickPicture {
		public void getPicture(String userID, int photoIndex, String imageUrl);
	}

	public interface OnClickGroupName {
		public void getGroupName(int position);
	}
}
