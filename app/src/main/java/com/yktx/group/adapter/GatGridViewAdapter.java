package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.ZanBean;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;

/**
 * Created by Administrator on 2014/4/8.
 */
public class GatGridViewAdapter extends BaseAdapter {
	Activity context;
	// protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.head_null).showImageForEmptyUri(null)
			.showImageOnFail(null).bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true).cacheInMemory(true)
			.displayer(new RoundedBitmapDisplayer(100))
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<ZanBean> list;

	public GatGridViewAdapter(Activity context, ArrayList<ZanBean> list) {
		this.context = context;
		this.list = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView;
		ZanBean groupBean = (ZanBean) list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.gat_item, null);
			holdView = new GatGridViewAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, groupBean);

		return convertView;
	}

	private void show(final HoldView holdView, ZanBean groupBean) {
		ImageLoader.getInstance().displayImage(UrlParams.IP + groupBean.getZanUserHead(), holdView.hot_head, options);
		
		if(groupBean.getBrow() == 0){
			holdView.zanFacial.setImageResource(Contanst.CHAT_FACIAL_SMALL[4]);
		} else {
			holdView.zanFacial.setImageResource(Contanst.CHAT_FACIAL_SMALL[groupBean.getBrow()-1]);
		}
		
	}

	public class HoldView {
		public ImageView hot_head, zanFacial;

		public HoldView(View convertView) {
			hot_head = (ImageView) convertView.findViewById(R.id.hot_head);
			zanFacial = (ImageView) convertView.findViewById(R.id.zanFacial);
			
		}
	}
}
