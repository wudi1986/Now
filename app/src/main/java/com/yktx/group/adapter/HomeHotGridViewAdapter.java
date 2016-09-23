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
import com.yktx.bean.GroupListBean;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;

/**
 * Created by Administrator on 2014/4/8.
 */
public class HomeHotGridViewAdapter extends BaseAdapter {
	Activity context;
//	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true)
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<GroupListBean> list;

	public HomeHotGridViewAdapter(Activity context, ArrayList<GroupListBean> list) {
		this.context = context;
//		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list.size() > 9){
			return 9;
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
					R.layout.home_hot_item, null);
			holdView = new HomeHotGridViewAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, groupBean);

		return convertView;
	}

	private void show(final HoldView holdView, GroupListBean groupBean) {

		
		ImageLoader.getInstance().displayImage(UrlParams.IP + groupBean.getGroupPhoto(), holdView.hot_head,
				options);
		holdView.hot_groupName.setText(groupBean.getGroupName());
		holdView.hot_num.setText(groupBean.getGroupPeopleCount()+"人");
		
	}

	public class HoldView {
		public ImageView hot_head;
		public TextView hot_groupName, hot_num;

		public HoldView(View convertView) {
			hot_head = (ImageView) convertView.findViewById(R.id.hot_head);
			
			hot_groupName = (TextView) convertView
					.findViewById(R.id.hot_groupName);
			hot_num = (TextView) convertView.findViewById(R.id.hot_num);
			
		}
	}
}


