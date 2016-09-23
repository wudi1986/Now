package com.yktx.group.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yktx.group.R;

public class ChatZanGridViewAdapter extends BaseAdapter {
	/**
	 * 用来存储图片的选中情况
	 */
	protected LayoutInflater mInflater;
	Activity mContext;
	int[] zanFacial = { R.drawable.chat_zan_facial_01,
			R.drawable.chat_zan_facial_02, R.drawable.chat_zan_facial_03,
			R.drawable.chat_zan_facial_04, R.drawable.chat_zan_facial_05,
			R.drawable.chat_zan_facial_06, R.drawable.chat_zan_facial_07,
			R.drawable.chat_zan_facial_08, R.drawable.chat_zan_facial_09 };

	public ChatZanGridViewAdapter(Activity context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return zanFacial.length;
	}

	@Override
	public Object getItem(int position) {
		return zanFacial[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HoldView viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grid_chat_facial_item, null);
			viewHolder = new HoldView(convertView, position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HoldView) convertView.getTag();
		}
		viewHolder.zanItem.setImageResource(zanFacial[position]);
		
		return convertView;
	}

	public static class HoldView {
		public ImageView zanItem;

		public HoldView(View convertView, int position) {
			zanItem = (ImageView) convertView.findViewById(R.id.zanItem);
		}
		
	}

}
