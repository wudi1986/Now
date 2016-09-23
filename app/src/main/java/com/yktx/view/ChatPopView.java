package com.yktx.view;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yktx.bean.BigImageBean;
import com.yktx.bean.ChatListBean;
import com.yktx.bean.GroupMemberListBean;
import com.yktx.bean.ZanBean;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.adapter.ChatAdapter.OnClickZanButton;
import com.yktx.group.adapter.GatGridViewAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.util.Contanst;
import com.yktx.util.ImageTool;
import com.yktx.util.PopShareDialog;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年8月13日 下午4:59:34  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class ChatPopView extends LinearLayout implements ServiceListener {

	Activity mContext;
	OnClickBack onClickBack;
	OnClickZanButton onClickZanButton;
	OnClickHeadCenter onClickHeadCenter;
	ViewPager viewpager;
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

	String userid;
	String userName = null;
	public ImageView zanbutton
	// , gatback
	;
	// public TextView chat_name_item, chat_distance_item, chat_time_item;

	private GridView gat_gridView;
	boolean isFragmentTab;
	GatGridViewAdapter gatGridViewAdapter;

	/**
	 * 查看点赞图片只一张。不轮播
	 */
	boolean isZanClick;

	public ChatPopView(Activity mContext, boolean isFragmentTab,
			boolean isZanClick) {
		super(mContext);
		// getBestView(mContext, chatID);
		this.isFragmentTab = isFragmentTab;
		this.isZanClick = isZanClick;
	}

	ChatListBean chatBean;

	public void setPopZanBean(ZanBean zanBean) {
		ArrayList<ZanBean> list = chatBean.getZanList();
		list.add(0, zanBean);
		chatBean.setZanList(list);
		gatGridViewAdapter.notifyDataSetInvalidated();
	}

	public void setOnClickBack(OnClickBack onClickBack) {
		this.onClickBack = onClickBack;
	}

	public void setOnClickZanButton(OnClickZanButton onClickZanButton) {
		this.onClickZanButton = onClickZanButton;
	}

	public void setOnClickHeadCenter(OnClickHeadCenter onClickHeadCenter) {
		this.onClickHeadCenter = onClickHeadCenter;
	}

	/**
	 * 点击图片下标
	 */
	int photoIndex;
	int curListIndex;

	/**
	 * 大图list
	 */

	public LinearLayout getBestView(Activity mContext,
			final ArrayList<BigImageBean> list, int index) {

		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		homeSale = (LinearLayout) mInflater.inflate(R.layout.zan_info_activity,
				null);
		SharedPreferences settings = mContext
				.getBaseContext()
				.getSharedPreferences(
						((GroupApplication) (mContext.getApplicationContext()))
								.getCurSP(),
						0);
		userid = settings.getString("userID", "");
		homeSale.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// mContext.setContentView(R.layout.sequence);
		curListIndex = index;
		connGetGatList(list.get(index).getChatID(), curListIndex);
		this.mContext = mContext;

		viewpager = (ViewPager) homeSale.findViewById(R.id.viewpager);
		ShowPageAdepter pagerAdapter = new ShowPageAdepter(list, mContext);
		viewpager.setAdapter(pagerAdapter);
		viewpager.setCurrentItem(index);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				photoIndex = list.get(arg0).getIndex();
				curListIndex = arg0;
				connGetGatList(list.get(arg0).getChatID(), curListIndex);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		zanbutton = (ImageView) homeSale.findViewById(R.id.zanbutton);
		if (isFragmentTab) {
			zanbutton.setVisibility(View.GONE);
		}
		gat_gridView = (GridView) homeSale.findViewById(R.id.gat_gridView);
		return homeSale;
	}

	boolean isZan;

	private void init(final Activity mContext, final ChatListBean chatBean2) {
		this.chatBean = chatBean2;
		gatGridViewAdapter = new GatGridViewAdapter(mContext,
				chatBean.getZanList());
		gat_gridView.setAdapter(gatGridViewAdapter);

		LinearLayout popLayout = (LinearLayout) homeSale
				.findViewById(R.id.chatBottom);
		final String array[] = chatBean.getContent().split(",");
		popLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		isZan = isCanZan(chatBean, chatBean.getContent());
		if (isZan) {
			zanbutton.setImageResource(R.drawable.chat_zan_unselect);
		} else {
			zanbutton.setImageResource(R.drawable.chat_zan_select);
		}

		zanbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isZan) {
					zanbutton
							.setImageResource(R.drawable.chat_zan_select);

					if (onClickZanButton != null)
						onClickZanButton.getClick(0, chatBean.getUserId(),
								chatBean.getUserName(), array[photoIndex],
								chatBean.getPhotoChatID());
					isZan = false;
				}
			}
		});

		gat_gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (onClickHeadCenter != null) {
					ZanBean zanBean = chatBean.getZanList().get(arg2);
					GroupMemberListBean bean = new GroupMemberListBean();
					bean.setDistance(chatBean.getDistance());
					bean.setId(zanBean.getZanUserID());
					bean.setName(zanBean.getZanUserName());
					bean.setPhoto(zanBean.getZanUserHead());
					onClickHeadCenter.getClick(bean);
				}
			}
		});

	}

	PopShareDialog selectDialog = null;

	public void showShare(Bitmap loadedImage) {
		// mController.setShareContent("请“正确”使用合体拍照 ———");
		// yktx2013
		selectDialog = new PopShareDialog(mContext,
				R.style.CustomDiaLog_by_SongHang, null, loadedImage);// 创建Dialog并设置样式主题
		Window win = selectDialog.getWindow();
		android.view.WindowManager.LayoutParams params = new android.view.WindowManager.LayoutParams();
		// params.x = -80;//设置x坐标
		// params.y = -60;//设置y坐标
		win.setAttributes((android.view.WindowManager.LayoutParams) params);
		selectDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		selectDialog.show();
	}

	// *显示dialog*/
	private void showsetheaddialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(mContext,
						R.style.CustomDiaLog_by_SongHang));
		builder.setItems(new String[] { "分享图片", "保存图片", "取消" },
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							ImageLoader.getInstance().loadImage(
									UrlParams.IP + chatBean.getContent(),
									new ImageLoadingListener() {

										@Override
										public void onLoadingStarted(
												String imageUri, View view) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onLoadingFailed(
												String imageUri, View view,
												FailReason failReason) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onLoadingComplete(
												String imageUri, View view,
												Bitmap loadedImage) {
											// TODO Auto-generated method stub
											showShare(loadedImage);
										}

										@Override
										public void onLoadingCancelled(
												String imageUri, View view) {
											// TODO Auto-generated method stub

										}
									});

							break;
						case 1:
							ImageLoader.getInstance().loadImage(
									UrlParams.IP + chatBean.getContent(),
									new ImageLoadingListener() {

										@Override
										public void onLoadingStarted(
												String imageUri, View view) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onLoadingFailed(
												String imageUri, View view,
												FailReason failReason) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onLoadingComplete(
												String imageUri, View view,
												Bitmap loadedImage) {
											// TODO Auto-generated method stub
											ImageTool.saveBitmapToAlbum(
													mContext, loadedImage);
										}

										@Override
										public void onLoadingCancelled(
												String imageUri, View view) {
											// TODO Auto-generated method stub

										}
									});

							break;
						default:
							break;
						}
					}

				});
		builder.show();
	}

	private boolean isCanZan(ChatListBean chatBean, String content) {
		ArrayList<ZanBean> zanList = chatBean.getZanList();
		int size = zanList.size();
		for (int i = 0; i < size; i++) {
			ZanBean bean = zanList.get(i);

			if (bean.getZanUserID().equals(userid)) {
				// 如果赞的list里面有自己的id 已经赞过，不能在点击
				return false;
			}

		}
		return true;
	}

	private void connGetGatList(String chatID, int curListIndex) {
		Log.i(  "aaa", "chatID = ================ " + chatID);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("message_id", chatID));
		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_GETGATLIST, null, curListIndex + "",
				ChatPopView.this).addList(params).request(UrlParams.POST);
	}

	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		int index = Integer.parseInt(sccmsg);
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		msg.arg2 = index;
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
		if (errcode.equals("10011"))
			msg.obj = errcode;
		else
			msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				if (msg.arg1 == Contanst.GETGATLIST) {
					if (curListIndex == msg.arg2) {		//快速翻页的时候只解析最后一页
						ChatListBean chatBean = (ChatListBean) msg.obj;
						init(mContext, chatBean);
					}
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	public interface OnClickBack {
		public void getClick();
	}

	public interface OnClickHeadCenter {
		public void getClick(GroupMemberListBean bean);
	}

	private class ShowPageAdepter extends PagerAdapter {
		ArrayList<BigImageBean> imageArray;
		private LayoutInflater inflater;

		ShowPageAdepter(ArrayList<BigImageBean> imageArray, Activity context) {
			this.imageArray = imageArray;
			inflater = context.getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return imageArray.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			RelativeLayout bgLayout = (RelativeLayout) imageLayout
					.findViewById(R.id.bgLayout);
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + imageArray.get(position).getImageUrl(),
					imageView);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickBack != null)
						onClickBack.getClick();
				}
			});

			bgLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickBack != null)
						onClickBack.getClick();
				}
			});

			imageView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					showsetheaddialog();
					return true;
				}
			});

			((ViewPager) view).addView(imageLayout, 0); // 将图片增加到ViewPager
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

}
