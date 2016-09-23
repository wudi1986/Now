package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.HotMainBean;
import com.yktx.bean.RecommendMainBean;
import com.yktx.group.BaseActivity;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;

/**
 * Created by Administrator on 2014/4/8.
 */
public class RecommendFragmentListViewAdapter extends BaseAdapter {
	Activity context;

	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private ViewPager advPager = null;
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private boolean isContinue = true;
	private boolean isRun = false;
	OnClickViewPagerButton onClickViewPagerButton;
	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(null)
			.showImageOnFail(null).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheInMemory(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(null)
			.showImageOnFail(null).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheInMemory(true) // 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	private ArrayList<HotMainBean> hotList = new ArrayList<HotMainBean>(3);
	private ArrayList<RecommendMainBean> RecommendList = new ArrayList<RecommendMainBean>(
			10);

	public RecommendFragmentListViewAdapter(Activity context) {
		this.context = context;
	}

	public void setHotList(ArrayList<HotMainBean> hotList) {
		this.hotList = hotList;
		if (hotList != null)
			hotListSize = hotList.size();
	}

	public void setOnClickViewPagerButton(
			OnClickViewPagerButton onClickViewPagerButton) {
		this.onClickViewPagerButton = onClickViewPagerButton;
	}

//	public void setRecommendList(ArrayList<RecommendMainBean> RecommendList) {
//		this.RecommendList = RecommendList;
//		if (RecommendList != null) {
//			if (RecommendList.size() > 3) {
//				recommendListSize = 3;
//			} else
//				recommendListSize = RecommendList.size();
//		}
//	}

//	int recommendListSize = 0;
	int hotListSize = 0;

	@Override
	public int getCount() {
//		int size = 0;
//
//		if (hotList != null) {
//
//			if (RecommendList != null && recommendListSize > 0) {
//				if (recommendListSize > 0 && recommendListSize <= 3) {
//					if (hotListSize == 0) {
//						size = 1;
//					} else {
//						size = hotListSize + 1;
//					}
//				} else if (recommendListSize > 3) {
//					if (hotListSize == 0) {
//						size = 2;
//					} else {
//						size = hotListSize + 2;
//					}
//				}
//			} else {
//				size = 0;
//			}
//
//		} else if (RecommendList != null && recommendListSize > 0
//				&& recommendListSize <= 3) {
//			size = 1;
//		} else if (RecommendList != null && recommendListSize > 3) {
//			size = 2;
//		} else {
//			size = 0;
//		}
		return hotList.size();
	}

	@Override
	public Object getItem(int position) {
		return hotList.get(position);
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
//	@Override
//	public int getItemViewType(int position) {
//		int type = 0;
//
//		if (recommendListSize > 0 && recommendListSize <= 3) {
//			if (position == 0) {
//				type = 0;
//			} else {
//				type = 2;
//			}
//		} else if (recommendListSize > 3) {
//			if (position == 0) {
//				type = 0;
//			} else if (position == 1) {
//				type = 1;
//			} else {
//				type = 2;
//			}
//		}
//
//		return type;
//	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
//	@Override
//	public int getViewTypeCount() {
//		return 3;
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RecommendFragmentListViewAdapter.HoldView holdView;
		int type = 0;
		type = getItemViewType(position);
		if (convertView == null) {
//			switch (type) {
//			case 0:
//				convertView = LayoutInflater.from(context).inflate(
//						R.layout.recommend_pager, null);
//				initViewPager(convertView, context);
//				break;
//
//			case 1:
//				convertView = LayoutInflater.from(context).inflate(
//						R.layout.recommend_hlistview, null);
//				initHListView(convertView, context);
//
//				break;
//
//			case 2:
//				if (recommendListSize <= 3) {
//					position = position - 1;
//				} else if (recommendListSize > 3) {
//					position = position - 2;
//				}

				HotMainBean HotMainBean = (HotMainBean) hotList.get(position);
				// if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.hot_fragment_item, null);
				holdView = new RecommendFragmentListViewAdapter.HoldView(
						convertView);
				convertView.setTag(holdView);
				// } else {
				// holdView = (HoldView) convertView.getTag();
				// }
				show(holdView, HotMainBean, position);
//				break;
//
//			}
		} else {

//			switch (type) {
//			case 0:
//				convertView = LayoutInflater.from(context).inflate(
//						R.layout.recommend_pager, null);
//				initViewPager(convertView, context);
//				break;
//
//			case 1:
//				holdView = (HoldView) convertView.getTag();
//				initHListView(convertView, context);
//				break;
//			case 2:
//				if (recommendListSize <= 3) {
//					position = position - 1;
//				} else if (recommendListSize > 3) {
//					position = position - 2;
//				}
				holdView = (HoldView) convertView.getTag();
				HotMainBean HotMainBean = (HotMainBean) hotList.get(position);
				show(holdView, HotMainBean, position);
//				break;
//			}

			holdView = (HoldView) convertView.getTag();
		}
		return convertView;
	}

	private void initHListView(View view, Activity context) {
		ViewPager hListView = (ViewPager) view
				.findViewById(R.id.recommendHListView);
		ArrayList<RecommendMainBean> newList = new ArrayList<RecommendMainBean>(
				10);
		for (int i = 3; i < RecommendList.size(); i++) {
			newList.add(RecommendList.get(i));
		}
		RecommendHListViewAdapter adapter = new RecommendHListViewAdapter(
				context, newList, onClickViewPagerButton);
		hListView.setClipChildren(false);
		hListView.setPageMargin((int) (-110 * BaseActivity.DENSITY));
		hListView.setOffscreenPageLimit(2);
		hListView.setAdapter(adapter);

		// hListView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent arg1) {
		// // TODO Auto-generated method stub
		// return true;
		// }
		// });
	}

	private void initViewPager(View view, Context context) {
		advPager = (ViewPager) view.findViewById(R.id.adv_pager);
		ViewGroup group = (ViewGroup) view.findViewById(R.id.viewGroup);

		RelativeLayout.LayoutParams imageViewParams;
		imageViewParams = new RelativeLayout.LayoutParams(
				BaseActivity.ScreenWidth, BaseActivity.ScreenWidth * 9 / 16);
		advPager.setLayoutParams(imageViewParams);

		// 对imageviews进行填充
		if (RecommendList.size() >= 3) {
			imageViews = new ImageView[3];
		} else {
			imageViews = new ImageView[RecommendList.size()];
		}

		ArrayList<RecommendMainBean> newList = new ArrayList<RecommendMainBean>(
				3);
		// 小图标
		for (int i = 0; i < imageViews.length; i++) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new LayoutParams(40, 40));
			imageView.setPadding(10, 5, 10, 5);
			imageViews[i] = imageView;
			newList.add(RecommendList.get(i));
			if (i == 0) {
				imageViews[i].setImageResource(R.drawable.detail_dot_focus);
			} else {
				imageViews[i].setImageResource(R.drawable.detail_dot_normal);
			}
			group.addView(imageViews[i]);
		}

		advPager.setAdapter(new AdvAdapter(newList));
		advPager.setOnPageChangeListener(new GuidePageChangeListener());
		advPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				default:
					isContinue = true;
					break;
				}
				return false;
			}
		});
		if (!isRun)
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						isRun = true;
						if (isContinue) {
							viewHandler.sendEmptyMessage(atomicInteger.get());
							whatOption();
						}
					}
				}

			}).start();
	}

	private void whatOption() {
		atomicInteger.incrementAndGet();
		if (atomicInteger.get() > imageViews.length - 1) {
			atomicInteger.getAndAdd(-4);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
	}

	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			advPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			atomicInteger.getAndSet(arg0);
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0].setImageResource(R.drawable.detail_dot_focus);
				if (arg0 != i) {
					imageViews[i]
							.setImageResource(R.drawable.detail_dot_normal);
				}
			}

		}

	}

	private final class AdvAdapter extends PagerAdapter {
		private LayoutInflater inflater;
		ArrayList<RecommendMainBean> image;

		public AdvAdapter(ArrayList<RecommendMainBean> views) {
			image = views;
			inflater = context.getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup arg0, int arg1, Object object) {
			((ViewPager) arg0).removeView((View) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return image.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_list, view,
					false);
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + image.get(position).getPhoto(), imageView,
					options2);
			imageView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (onClickViewPagerButton != null) {
						onClickViewPagerButton.getClick(position);
					}
				}

			});
//			TextView number = (TextView) imageLayout.findViewById(R.id.miaoshu);
//			RelativeLayout.LayoutParams numberParams;

//			numberParams = new RelativeLayout.LayoutParams(
//					RelativeLayout.LayoutParams.WRAP_CONTENT,
//					RelativeLayout.LayoutParams.WRAP_CONTENT);
//			numberParams
//					.setMargins(
//							(int) (image.get(position).getOffx() * BaseActivity.ScreenWidth),
//							(int) (image.get(position).getOffy()
//									* BaseActivity.ScreenWidth * 9 / 16), 0, 0);
//
//			number.setLayoutParams(numberParams);
//			number.setText(image.get(position).getGroupManCount() + "人在线");
//
//			long numberColor = Long.parseLong(image.get(position).getColor());
//			number.setTextColor((int) numberColor);
			
			((ViewPager) view).addView(imageLayout, 0); // 将图片增加到ViewPager
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

//	int leftLayoutHeightArray[] = { 106, 100, 96, 92, 88, 84, 80 };
//	int hotLeftImagewidthArray[] = { 100, 98, 96, 94, 92, 90, 88 };
//	int hotLeftImageHeightArray[] = { 84, 80, 76, 72, 68, 64, 60 };
//	int hotLeftTextSizeArray[] = { 50, 45, 40, 35, 30, 25, 20 };
	

	int leftLayoutHeightArray[] = { 72, 72, 72, 72, 72, 72, 72 };
	int hotLeftImagewidthArray[] = { 64, 64, 64, 64, 64, 64, 64 };
	int hotLeftImageHeightArray[] = { 48, 48, 48, 48, 48, 48, 48 };
//	int hotLeftTextSizeArray[] = { 20, 20, 20, 20, 20, 20, 20 };
	int leftImageRes[] = { R.drawable.recommend_item_1,
			R.drawable.recommend_item_2, R.drawable.recommend_item_3,
			R.drawable.recommend_item_4, R.drawable.recommend_item_5,
			R.drawable.recommend_item_6, R.drawable.recommend_item_6, };

	private void show(final HoldView holdView, HotMainBean hotMainBean,
			int position) {
		AbsListView.LayoutParams leftParams;
		RelativeLayout.LayoutParams hotLeftImageParams;
		RelativeLayout.LayoutParams hotLeftTextParams;
		if (position < 3) {
//			leftParams = new AbsListView.LayoutParams(
//					LayoutParams.MATCH_PARENT,
//					(int) (leftLayoutHeightArray[position] * BaseActivity.DENSITY));
//			holdView.hotLayout.setLayoutParams(leftParams);
//			hotLeftImageParams = new RelativeLayout.LayoutParams(
//					(int) (hotLeftImagewidthArray[position] * BaseActivity.DENSITY),
//					(int) (hotLeftImageHeightArray[position] * BaseActivity.DENSITY));
//			holdView.hotLeftImage.setLayoutParams(hotLeftImageParams);
//			holdView.hotLeftImage.setImageResource(leftImageRes[position]);
//			hotLeftTextParams = new RelativeLayout.LayoutParams(
//					(int) (hotLeftImageHeightArray[position] * BaseActivity.DENSITY),
//					(int) (hotLeftImageHeightArray[position] * BaseActivity.DENSITY));
//			holdView.hotMsgnum.setTextSize(hotLeftTextSizeArray[position]);
			holdView.hotMsgnum.setBackgroundResource(R.drawable.recommend_item_1);
//			hotLeftTextParams
//					.setMargins(
//							(int) ((hotLeftImagewidthArray[position] - hotLeftImageHeightArray[position]) * BaseActivity.DENSITY),
//							0, 0, 0);
//			holdView.hotMsgnum.setLayoutParams(hotLeftTextParams);

		} else {
//			leftParams = new AbsListView.LayoutParams(
//					LayoutParams.MATCH_PARENT,
//					(int) (leftLayoutHeightArray[6] * BaseActivity.DENSITY));
//			holdView.hotLayout.setLayoutParams(leftParams);
//			hotLeftImageParams = new RelativeLayout.LayoutParams(
//					(int) (hotLeftImagewidthArray[6] * BaseActivity.DENSITY),
//					(int) (hotLeftImageHeightArray[6] * BaseActivity.DENSITY));
//			holdView.hotLeftImage.setLayoutParams(hotLeftImageParams);
//			holdView.hotLeftImage.setImageResource(leftImageRes[6]);
//			hotLeftTextParams = new RelativeLayout.LayoutParams(
//					(int) (hotLeftImageHeightArray[6] * BaseActivity.DENSITY),
//					(int) (hotLeftImageHeightArray[6] * BaseActivity.DENSITY));
//			holdView.hotMsgnum.setTextSize(hotLeftTextSizeArray[6]);
			holdView.hotMsgnum.setTextColor(context.getResources().getColor(R.color.meibao_color_4));
			holdView.hotMsgnum.setBackgroundResource(R.drawable.kongtu);
//			hotLeftTextParams
//					.setMargins(
//							(int) ((hotLeftImagewidthArray[6] - hotLeftImageHeightArray[6]) * BaseActivity.DENSITY),
//							0, 0, 0);
//			holdView.hotMsgnum.setLayoutParams(hotLeftTextParams);

		}
//
//		if (position < 5) {
//			holdView.hotGroupName.setTextColor(context.getResources().getColor(
//					R.color.meibao_item_name_color));
//		} else {
//			holdView.hotGroupName.setTextColor(context.getResources().getColor(
//					R.color.meibao_item_name_color));
//		}

		holdView.hotMsgnum.setText((position + 1) + "");
		holdView.hotGroupName.setText("#"+hotMainBean.getGroup_name());
		holdView.hotGroupPeopleNum.setText(hotMainBean.getGroupManCount());
		holdView.hotGroupMsgCount.setText("累计" + hotMainBean.getMsgnum()
				+ "条内容");
	}

	public class HoldView {
		public TextView hotMsgnum, hotGroupName, hotGroupPeopleNum,
				hotGroupMsgCount;
		private RelativeLayout hotLayout;
		private ImageView hotLeftImage;

		public HoldView(View convertView) {

			hotLayout = (RelativeLayout) convertView
					.findViewById(R.id.hotLayout);
			hotGroupMsgCount = (TextView) convertView
					.findViewById(R.id.hotGroupMsgCount);
			hotMsgnum = (TextView) convertView.findViewById(R.id.hotMsgnum);
			hotGroupName = (TextView) convertView
					.findViewById(R.id.hotGroupName);
			hotGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.hotGroupPeopleNum);
			hotLeftImage = (ImageView) convertView
					.findViewById(R.id.hotLeftImage);

		}
	}

	/**
	 * @author Administrator 跳转个人主页的监听
	 */
	public interface OnClickViewPagerButton {
		public void getClick(int option);
	}
}
