package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.GroupListBean;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;

/**
 * Created by Administrator on 2014/4/8.
 */
public class HomeNearAdapter extends BaseAdapter {
	Activity context;
	// protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			// 启用内存缓存
			.displayer(new RoundedBitmapDisplayer(100))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<GroupListBean> list;
	boolean isMain;

	public HomeNearAdapter(Activity context, ArrayList<GroupListBean> list,
			boolean isMain) {
		this.context = context;
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.list = list;
		this.isMain = isMain;
	}

	@Override
	public int getCount() {
		if (isMain) {
			if (list.size() > 4) {
				return 4;
			} else {
				return list.size();
			}
		} else {
			return list.size();
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView;
		GroupListBean groupBean = (GroupListBean) list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.home_near_item, null);
			holdView = new HomeNearAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		// boolean isLast = false;
		// if((list.size() - 1) == position) {isLast = true;}else{isLast =
		// false;}
		show(holdView, groupBean);

		return convertView;
	}

	private void show(final HoldView holdView, GroupListBean groupBean) {

		ImageLoader.getInstance().displayImage(
				UrlParams.IP + groupBean.getGroupPhoto(), holdView.near_head,
				options);
		holdView.near_groupName.setText(groupBean.getGroupName());

		if(groupBean.getDistance().equals("火星")){
			holdView.near_distance.setText(groupBean.getDistance());
		} else {
			holdView.near_distance.setText(groupBean.getDistance() + "km");
		}
		
		holdView.near_num.setText(groupBean.getGroupPeopleCount() + "人");

		/*
		 * if(isLast){ RelativeLayout.LayoutParams rl = new
		 * RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
		 * 2); rl.setMargins(0, 0, 0, 0);
		 * rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 * holdView.near_line.setLayoutParams(rl); }
		 */
	}

	public class HoldView {
		public ImageView near_head;
		public TextView near_groupName, near_num, near_distance;

		// public View near_line;

		public HoldView(View convertView) {
			near_head = (ImageView) convertView.findViewById(R.id.near_head);

			near_groupName = (TextView) convertView
					.findViewById(R.id.near_groupName);
			near_num = (TextView) convertView.findViewById(R.id.near_num);
			near_distance = (TextView) convertView
					.findViewById(R.id.near_distance);

			// near_line = (View)convertView.findViewById(R.id.near_line);

		}
	}
}
