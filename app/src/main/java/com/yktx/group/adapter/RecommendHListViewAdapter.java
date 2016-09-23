package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yktx.bean.RecommendMainBean;
import com.yktx.group.BaseActivity;
import com.yktx.group.R;
import com.yktx.group.adapter.RecommendFragmentListViewAdapter.OnClickViewPagerButton;
import com.yktx.group.conn.UrlParams;

/**
 * 
 * @项目名称：Group
 * @类名称：ChatMemberAdapter.java
 * @类描述：群组成员头像
 * @创建人：chenyongxian
 * @创建时间：2014年10月29日下午10:29:09
 * @修改人：
 * @修改时间：2014年10月31日下午3:49:33
 * @修改备注：
 * @version
 * 
 */
public class RecommendHListViewAdapter extends PagerAdapter {
	private LayoutInflater inflater;
	ArrayList<RecommendMainBean> image;
	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(null)
			.showImageOnFail(null).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).cacheInMemory(true) // 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	OnClickViewPagerButton onClickViewPagerButton;

	public RecommendHListViewAdapter(Activity context,
			ArrayList<RecommendMainBean> views,	OnClickViewPagerButton onClickViewPagerButton) {
		image = views;
		inflater = context.getLayoutInflater();
		this.onClickViewPagerButton = onClickViewPagerButton;
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
		View imageLayout = inflater.inflate(R.layout.recommend_hlistview_adapter, view,
				false);
		ImageView imageView = (ImageView) imageLayout.findViewById(R.id.recomend_image);
		ImageLoader.getInstance().displayImage(
				UrlParams.IP + image.get(position).getPhoto(), imageView,
				options2);
		
		TextView number = (TextView) imageLayout.findViewById(R.id.recomend_num);
		RelativeLayout.LayoutParams numberParams;

		numberParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		float imageWidth = 135*BaseActivity.DENSITY*16/9; 
		numberParams.setMargins((int) (image.get(position).getOffx() * imageWidth), (int) (image.get(position).getOffy() * 9/16), 0, 0);
		
		number.setLayoutParams(numberParams);
		number.setText(image.get(position).getGroupManCount()+"人在线");
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onClickViewPagerButton!= null ){
					onClickViewPagerButton.getClick(position+3);
				}
			}
		});

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
