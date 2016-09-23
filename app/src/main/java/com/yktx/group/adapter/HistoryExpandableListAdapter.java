package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.GroupListBean;
import com.yktx.group.BaseActivity;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.TimeUtil;

public class HistoryExpandableListAdapter extends BaseExpandableListAdapter {
	Activity activity;
	private ArrayList<ArrayList<GroupListBean>> child = new ArrayList<ArrayList<GroupListBean>>(2);

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheInMemory(true).displayer(new RoundedBitmapDisplayer(100))
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
//	String GroupName[];
	ArrayList<String> GroupName = new ArrayList<String>(3);
	// 添加历史
	OnItemLongClickChild onItemLongClickChild;
	public void setOnItemLongClickChild(OnItemLongClickChild onItemLongClickChild){
		this.onItemLongClickChild = onItemLongClickChild;
	}
	
	OnItemClickChild onItemClickChild;
	public void setOnItemClickChild(OnItemClickChild onItemClickChild){
		this.onItemClickChild = onItemClickChild;
	}
	
	public HistoryExpandableListAdapter(Activity a) {
		activity = a;
	}

	public void setList(ArrayList<ArrayList<GroupListBean>> child) {
		this.child = child;
	}

	public void setGroup(ArrayList<String> GroupName ){
		this.GroupName = GroupName;
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return child.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	// 设置子选项样式
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HoldView holdView;
		GroupListBean groupListBean = child.get(groupPosition).get(
				childPosition);
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.history_fragment_child_item, null);
			holdView = new HistoryExpandableListAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
			show(holdView, groupListBean,isLastChild);
		
		// String string = child.get(groupPosition).get(childPosition);
		return convertView;
	}

	private void show(final HoldView holdView, final GroupListBean groupListBean ,boolean isLast) {
		if (isLast){
			holdView.bottomHLine.setVisibility(View.VISIBLE);
			holdView.bottomLine.setVisibility(View.GONE);
			
		} else {
			holdView.bottomHLine.setVisibility(View.GONE);
			holdView.bottomLine.setVisibility(View.VISIBLE);
		}
		
		if (groupListBean.getAtNum().equals("0")) {
			holdView.historyGroupMsgCount.setVisibility(View.GONE);
			holdView.atImage.setVisibility(View.GONE);
		} else {
			holdView.atImage.setVisibility(View.VISIBLE);
			holdView.historyGroupMsgCount.setVisibility(View.VISIBLE);
			holdView.historyGroupMsgCount.setText(groupListBean.getAtNum()
					+ "条未读");
		}
		holdView.historyGroupName.setText(groupListBean.getGroupName());

		int number = Integer.parseInt(groupListBean.getGroupPeopleCount());
		if (number <= 0) {
			RelativeLayout.LayoutParams hotLeftImageParams;
			hotLeftImageParams = new RelativeLayout.LayoutParams(
					(int) (RelativeLayout.LayoutParams.MATCH_PARENT),
					(int) (75 * BaseActivity.DENSITY));
			holdView.chidLayout.setLayoutParams(hotLeftImageParams);
			holdView.layout3.setVisibility(View.GONE);
		} else {
			RelativeLayout.LayoutParams hotLeftImageParams;
			hotLeftImageParams = new RelativeLayout.LayoutParams(
					(int) (RelativeLayout.LayoutParams.MATCH_PARENT),
					(int) (119 * BaseActivity.DENSITY));
			holdView.chidLayout.setLayoutParams(hotLeftImageParams);
			holdView.layout3.setVisibility(View.VISIBLE);
			setImageHead(holdView, groupListBean, number);
		}
		
		holdView.chidLayout.setOnClickListener (new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(onItemClickChild != null){
					onItemClickChild.getClick(groupListBean);
				}
			}
		});
		
		
		holdView.chidLayout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if(onItemLongClickChild != null){
					onItemLongClickChild.getClick(groupListBean.getGroupID());
				}
				return true;
			}
		});
		
		holdView.historyLastTime.setText(TimeUtil.getTimes(groupListBean
				.getGroupTime()));
	}

	private void setImageHead(HoldView holdView, GroupListBean groupListBean,
			int number) {
		switch (number) {
		case 1:
			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP + groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.GONE);
			holdView.historyChildOnLine3.setVisibility(View.GONE);
			holdView.historyChildOnLine4.setVisibility(View.GONE);
			holdView.historyChildOnLine5.setVisibility(View.GONE);
			holdView.historyChildOnLine6.setVisibility(View.GONE);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);

			break;
		case 2:

			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.GONE);
			holdView.historyChildOnLine4.setVisibility(View.GONE);
			holdView.historyChildOnLine5.setVisibility(View.GONE);
			holdView.historyChildOnLine6.setVisibility(View.GONE);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);
			break;
		case 3:
			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(2).getPhoto(),
					holdView.historyChildOnLine3, options);
			holdView.historyChildOnLine4.setVisibility(View.GONE);
			holdView.historyChildOnLine5.setVisibility(View.GONE);
			holdView.historyChildOnLine6.setVisibility(View.GONE);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);

			break;
		case 4:
			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(2).getPhoto(),
					holdView.historyChildOnLine3, options);
			holdView.historyChildOnLine4.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(3).getPhoto(),
					holdView.historyChildOnLine4, options);
			holdView.historyChildOnLine5.setVisibility(View.GONE);
			holdView.historyChildOnLine6.setVisibility(View.GONE);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);

			break;
		case 5:

			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(2).getPhoto(),
					holdView.historyChildOnLine3, options);
			holdView.historyChildOnLine4.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(3).getPhoto(),
					holdView.historyChildOnLine4, options);
			holdView.historyChildOnLine5.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(4).getPhoto(),
					holdView.historyChildOnLine5, options);
			holdView.historyChildOnLine6.setVisibility(View.GONE);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);
			break;
		case 6:

			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(2).getPhoto(),
					holdView.historyChildOnLine3, options);
			holdView.historyChildOnLine4.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(3).getPhoto(),
					holdView.historyChildOnLine4, options);
			holdView.historyChildOnLine5.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(4).getPhoto(),
					holdView.historyChildOnLine5, options);
			holdView.historyChildOnLine6.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(5).getPhoto(),
					holdView.historyChildOnLine6, options);
			holdView.historyGroupPeopleNum.setVisibility(View.GONE);
			break;
		default:

			holdView.historyChildOnLine1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(0).getPhoto(),
					holdView.historyChildOnLine1, options);
			holdView.historyChildOnLine2.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(1).getPhoto(),
					holdView.historyChildOnLine2, options);
			holdView.historyChildOnLine3.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(2).getPhoto(),
					holdView.historyChildOnLine3, options);
			holdView.historyChildOnLine4.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(3).getPhoto(),
					holdView.historyChildOnLine4, options);
			holdView.historyChildOnLine5.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(4).getPhoto(),
					holdView.historyChildOnLine5, options);
			holdView.historyChildOnLine6.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					UrlParams.IP +groupListBean.getMembers().get(5).getPhoto(),
					holdView.historyChildOnLine6, options);
			holdView.historyGroupPeopleNum.setVisibility(View.VISIBLE);
			holdView.historyGroupPeopleNum.setText(number+"");
			break;
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(child.size() == 0)
			return 0;
		return child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return GroupName.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return GroupName.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	// 使用LayoutInflater解析定制的XML文件,然后设置相应的样式
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// LayoutInflater inflater =
		// (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		// View layout = inflater.inflate(R.layout.main_listview,null);
		LayoutInflater inflater = LayoutInflater.from(activity);
		View layout = inflater.inflate(R.layout.history_fragment_group_item,
				null);
		TextView my_text = (TextView) layout
				.findViewById(R.id.historyGroupGroupName);
		my_text.setText(GroupName.get(groupPosition));
		// my_text.set
		return layout;
		// return getGenericView(string);
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

//	public void initialData() {
//		ArrayList<GroupListBean> todayList = new ArrayList<GroupListBean>();
//		ArrayList<GroupListBean> sevenList = new ArrayList<GroupListBean>();
//		ArrayList<GroupListBean> agoList = new ArrayList<GroupListBean>();
//
//		child = new ArrayList<List<GroupListBean>>();
//		for (int i = 0; i < historygroupList.size(); i++) {
//			long time = historygroupList.get(i).getGroupTime();
//			if (TimeUtil.isToday(time)) {
//				todayList.add(historygroupList.get(i));
//			} else if (TimeUtil.isSevenday(time)) {
//				sevenList.add(historygroupList.get(i));
//			} else {
//				agoList.add(historygroupList.get(i));
//			}
//		}
//		child.add(todayList);
//		child.add(sevenList);
//		child.add(agoList);
//		this.notifyDataSetInvalidated();
//	}

	// View stub to create Group/Children 's View
	public TextView getGenericView(String s) {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);
		TextView text = new TextView(activity);
		text.setLayoutParams(lp);
		// Center the text vertically
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		text.setPadding(36, 0, 0, 0);

		text.setText(s);
		return text;
	}

	public class HoldView {
		public TextView historyGroupMsgCount, historyGroupName,
				historyGroupPeopleNum, historyLastTime;
		private ImageView atImage;
		private ImageView historyChildOnLine1, historyChildOnLine2,
				historyChildOnLine3, historyChildOnLine4, historyChildOnLine5,
				historyChildOnLine6;
		LinearLayout layout3;
		RelativeLayout chidLayout;
		View bottomHLine;
		ImageView bottomLine;
		// private ImageView historyLastPhoto;

		public HoldView(View convertView) {

			historyGroupMsgCount = (TextView) convertView
					.findViewById(R.id.historyGroupMsgCount);
			historyGroupName = (TextView) convertView
					.findViewById(R.id.historyGroupName);
			historyGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.historyGroupPeopleNum);
			historyLastTime = (TextView) convertView
					.findViewById(R.id.historyLastTime);
			atImage = (ImageView) convertView.findViewById(R.id.atImage);
			historyChildOnLine1 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine1);
			historyChildOnLine2 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine2);
			historyChildOnLine3 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine3);
			historyChildOnLine4 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine4);
			historyChildOnLine5 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine5);
			historyChildOnLine6 = (ImageView) convertView
					.findViewById(R.id.historyChildOnLine6);
			layout3 = (LinearLayout) convertView.findViewById(R.id.layout3);
			bottomLine = (ImageView) convertView.findViewById(R.id.bottomLine);
			bottomHLine = (View) convertView.findViewById(R.id.bottomHLine);
			chidLayout = (RelativeLayout) convertView.findViewById(R.id.chidLayout);
		}
	}

	/**
	 * @author 长按按钮
	 */
	public interface OnItemLongClickChild {
		public void getClick(String id);
	}

	/**
	 * @author 点击按钮
	 */
	public interface OnItemClickChild {
		public void getClick(GroupListBean toChatBean);
	}

}
