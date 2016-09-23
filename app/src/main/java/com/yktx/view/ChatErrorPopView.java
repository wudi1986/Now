package com.yktx.view;

/**
 * 
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.ChatListBean;
import com.yktx.group.R;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年8月13日 下午4:59:34  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class ChatErrorPopView extends LinearLayout {

	Activity mContext;
	OnClickSendAgain onClickSendAgain;
	OnClickQuit onClickQuit;
	// GoodsListView goodsListView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.displayer(new RoundedBitmapDisplayer(100)).cacheInMemory(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null)
			.showImageOnFail(null)
			// .displayer(new RoundedBitmapDisplayer(120))

			// .displayer(new RoundedBitmapDisplayer(20))
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	/**
	 * @param context
	 */
	LinearLayout homeSale;

	public ImageView chat_error_send_again, chat_error_quit;
	RelativeLayout chatErrorLayout;

	public ChatErrorPopView(Activity mContext) {
		super(mContext);
		// getBestView(mContext, chatID);
	}

	public void setOnClickSendAgain(OnClickSendAgain onClickSendAgain) {
		this.onClickSendAgain = onClickSendAgain;
	}

	public void setOnClickQuit(OnClickQuit onClickQuit) {
		this.onClickQuit = onClickQuit;
	}

	public static final int ANCHOR_BOTTOM = 0;
	public static final int ANCHOR_TOP = 1;
	public static final int ANCHOR_LEFT = 2;

	/**
	 * @param mContext
	 * @param anchor
	 *            锚点
	 * @return
	 */
	public LinearLayout getBestView(Activity mContext, int anchor,
			final ChatListBean bean) {
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// mContext.setContentView(R.layout.sequence);
		this.mContext = mContext;

		switch (anchor) {
		case ANCHOR_BOTTOM:
			homeSale = (LinearLayout) mInflater.inflate(
					R.layout.chat_error_pop_bottom, null);
			break;
		case ANCHOR_TOP:
			homeSale = (LinearLayout) mInflater.inflate(
					R.layout.chat_error_pop_top, null);
			break;
		case ANCHOR_LEFT:
			homeSale = (LinearLayout) mInflater.inflate(
					R.layout.chat_error_pop_left, null);
			break;
		}

		chatErrorLayout = (RelativeLayout) homeSale
				.findViewById(R.id.chatErrorLayout);
		chat_error_send_again = (ImageView) homeSale
				.findViewById(R.id.chat_error_send_again);
		chat_error_quit = (ImageView) homeSale
				.findViewById(R.id.chat_error_quit);

		chat_error_send_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onClickSendAgain.getClick(bean);
			}
		});

		chat_error_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (onClickQuit != null)
					onClickQuit.getClick(bean.getChatId());
			}
		});
		return homeSale;
	}

	public interface OnClickQuit {
		public void getClick(String chatID);
	}

	public interface OnClickSendAgain {
		public void getClick(ChatListBean bean);
	}

}
