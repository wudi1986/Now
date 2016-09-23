package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yktx.bean.GroupListBean;
import com.yktx.group.R;

/**
 * Created by Administrator on 2014/4/8.
 */
public class SearchListAdapter extends BaseAdapter {
	Activity context;
	// protected ImageLoader imageLoader = ImageLoader.getInstance();
	//
	// public DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.head_null)
	// .showImageForEmptyUri(null).showImageOnFail(null)
	// .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
	// .cacheInMemory(true)
	// // 启用内存缓存
	// .displayer(new RoundedBitmapDisplayer(100))
	// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<GroupListBean> list;
	String searchStr;

	public SearchListAdapter(Activity context, ArrayList<GroupListBean> list,
			String searchStr) {
		this.context = context;
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		this.list = list;
		this.searchStr = searchStr;
	}

	public void setList(ArrayList<GroupListBean> list){
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
		GroupListBean groupBean = (GroupListBean) list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.search_result_item, null);
			holdView = new SearchListAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, groupBean);

		return convertView;
	}

	private void show(final HoldView holdView, GroupListBean groupBean) {

//		Log.i(  "aaa",
//				"groupBean.getGroupName() ======== " + groupBean.getGroupTime());
		// =========高亮显示
		if (searchStr != null) {
			SpannableString s = new SpannableString(groupBean.getGroupName());

			Pattern p = Pattern.compile(searchStr);

			Matcher m = p.matcher(s);

			while (m.find()) {
				int start = m.start();
				int end = m.end();
				s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.meibao_color_1)), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			holdView.near_groupName.setText(s);
		} else {
			holdView.near_groupName.setText(groupBean.getGroupName());
		}
		
		if(TextUtils.isEmpty(groupBean.getGroupPeopleCount()) || groupBean.getGroupPeopleCount().length() <= 0 || "0".equals(groupBean.getGroupPeopleCount())){
//			holdView.near_num.setVisibility(View.INVISIBLE);
//			holdView.near_distance.setVisibility(View.INVISIBLE);
//			holdView.near_distance_icon.setVisibility(View.INVISIBLE);
		}else{
//			holdView.near_num.setVisibility(View.VISIBLE);
//			holdView.near_distance.setVisibility(View.VISIBLE);
//			holdView.near_distance_icon.setVisibility(View.VISIBLE);
//			holdView.near_distance.setText(groupBean.getDistance());
//			holdView.near_num.setText(groupBean.getGroupPeopleCount() + "人");
		}
	}

	public class HoldView {
		public TextView near_groupName, near_num, near_distance;
		public ImageView near_distance_icon;

		public HoldView(View convertView) {

			near_groupName = (TextView) convertView
					.findViewById(R.id.near_groupName);
//			near_num = (TextView) convertView.findViewById(R.id.near_num);
//			near_distance = (TextView) convertView
//					.findViewById(R.id.near_distance);
//			
//			near_distance_icon = (ImageView) convertView
//					.findViewById(R.id.near_distance_icon);

		}
	}
}
