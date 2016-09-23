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
import com.yktx.bean.ChatPhotoBean;
import com.yktx.group.R;

public class ChatPhotoAdapter extends BaseAdapter {
	/**
	 * 用来存储图片的选中情况
	 */
	private ArrayList<ChatPhotoBean> curList = new ArrayList<ChatPhotoBean>(3);
	protected LayoutInflater mInflater;
	Activity mContext;

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(null).showImageForEmptyUri(null)
			.cacheInMemory(false).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(false)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public ChatPhotoAdapter(Activity context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void setList(ArrayList<ChatPhotoBean> list){
		curList.clear();
		curList.addAll(list);
		ChatPhotoBean bean = new ChatPhotoBean();
		ChatPhotoBean bean1 = new ChatPhotoBean();
		curList.add(0,bean);
		curList.add(bean1);
	};

	@Override
	public int getCount() {
		return curList.size();
	}

	@Override
	public Object getItem(int position) {
		return curList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HoldView viewHolder;
		final ChatPhotoBean bean = curList.get(position);
		String path = bean.getImagePath();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_photo_item, null);
			viewHolder = new HoldView(convertView, position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HoldView) convertView.getTag();
		}

		if (position == 0 || position == 4) {
			if(position == 0 ){
				viewHolder.chat_select_image.setImageResource(R.drawable.liaotian_paizhao);
			} else {
				viewHolder.chat_select_image.setImageResource(R.drawable.liaotian_xiangce);
			}
			
		} else {
			if (bean.isSelect()) {
				viewHolder.chat_select_image
						.setImageResource(R.drawable.liaotian_xuanzhongtu);
			} else {
				viewHolder.chat_select_image
						.setImageResource(R.drawable.kongtu);
			}

			ImageLoader.getInstance().displayImage(path, viewHolder.mImageView,
					options);
		}
		return convertView;
	}

	public static class HoldView {
		public ImageView mImageView, chat_select_image;
		int position;

		public HoldView(View convertView, int position) {
			mImageView = (ImageView) convertView.findViewById(R.id.chat_image);
			chat_select_image = (ImageView) convertView
					.findViewById(R.id.chat_select_image);
			this.position = position;
		}

		public int getPosition() {
			return position;
		}
	}


}
