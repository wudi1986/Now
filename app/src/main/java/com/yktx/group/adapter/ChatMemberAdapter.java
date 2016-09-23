package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.GroupMemberListBean;
import com.yktx.group.R;
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
public class ChatMemberAdapter extends BaseAdapter {
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
	private ArrayList<GroupMemberListBean> list;

	public ChatMemberAdapter(Activity context,
			ArrayList<GroupMemberListBean> list) {
		this.context = context;
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list.size() > 5) {
			return list.size() + 1;
		} else
			return list.size() ;
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
		ChatMemberAdapter.HoldView holdView;
		GroupMemberListBean groupMemberBean = null;
		if (convertView == null) {
			if (position != list.size()) {
				groupMemberBean = (GroupMemberListBean) list.get(position);
			}
			convertView = LayoutInflater.from(context).inflate(
					R.layout.chat_group_member, null);
			holdView = new ChatMemberAdapter.HoldView(convertView);
			convertView.setTag(holdView);
			show(holdView, groupMemberBean, position);
		} else {
			holdView = (HoldView) convertView.getTag();
			
			if (position != list.size()) {
				groupMemberBean = (GroupMemberListBean) list.get(position);
			}
			
			show(holdView, groupMemberBean, position);
		}
		return convertView;
	}

	private void show(final HoldView holdView,
			GroupMemberListBean groupMemberBean, int position) {
		if (0 == position) {
			holdView.chat_headphoto
					.setImageResource(R.drawable.invitation_chat_head);
			// holdView.chat_head_distance.setText("邀请");
			holdView.chatShadow.setVisibility(View.GONE);
			holdView.chat_head_distance.setVisibility(View.GONE);
			holdView.chat_headphoto.setVisibility(View.VISIBLE);
			holdView.bgLayout.setBackgroundResource(R.drawable.toumingimg);
			return;
		}
		if (list.size() == position) {
			holdView.chat_headphoto.setVisibility(View.GONE);
			holdView.bgLayout.setBackgroundResource(R.drawable.chat_head_last);
			holdView.chatShadow.setVisibility(View.GONE);
			holdView.chat_head_distance.setVisibility(View.GONE);
			return;
		} else {
			holdView.bgLayout.setBackgroundResource(R.drawable.toumingimg);
			holdView.chat_headphoto.setVisibility(View.VISIBLE);
			holdView.chatShadow.setVisibility(View.VISIBLE);
			holdView.chat_head_distance.setVisibility(View.VISIBLE);
		}
		if (groupMemberBean.getPhoto() == null)
			groupMemberBean.setPhoto("");
			ImageLoader.getInstance().displayImage(
				UrlParams.IP + groupMemberBean.getPhoto(),
				holdView.chat_headphoto, options);
		if (1 == position) {
			holdView.chat_head_distance.setText("我");

		} else {
			if(groupMemberBean.getDistance().equals("火星")){
				holdView.chat_head_distance.setText(groupMemberBean.getDistance());
			} else {
				holdView.chat_head_distance.setText(groupMemberBean.getDistance() + "km");
			}
		}

	}

	public class HoldView {
		public ImageView chat_headphoto, chatShadow;
		public TextView chat_head_distance;
		private RelativeLayout bgLayout;

		public HoldView(View convertView) {
			chat_headphoto = (ImageView) convertView
					.findViewById(R.id.chat_headphoto);
			chatShadow = (ImageView) convertView.findViewById(R.id.chatShadow);
			chat_head_distance = (TextView) convertView
					.findViewById(R.id.chat_head_distance);
			bgLayout = (RelativeLayout)convertView.findViewById(R.id.bgLayout);

		}
	}
}
