package com.yktx.group.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yktx.bean.GroupListBean;
import com.yktx.group.R;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
public class HistoryFragmentListViewAdapter extends BaseAdapter {
	Activity context;

	ArrayList<GroupListBean> list = new ArrayList<GroupListBean>(10);

	public HistoryFragmentListViewAdapter(Activity context) {
		this.context = context;
	}

	public void setList(ArrayList<GroupListBean> list) {
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
		GroupListBean groupListBean = (GroupListBean) list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.history_fragment_item, null);
			holdView = new HistoryFragmentListViewAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, groupListBean, position);

		return convertView;
	}

	private void show(final HoldView holdView, GroupListBean groupListBean,
			int position) {
		// ImageLoader.getInstance().displayImage(
		// UrlParams.IP + groupListBean.getGroupPhoto(),
		// holdView.historyLastPhoto, options2);

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

		if (groupListBean.getGroupPeopleCount().equals("0")) {
			holdView.historyGroupPeopleNum.setText(groupListBean
					.getGroupPeopleCount() + "人在线");
		} else if (groupListBean.getGroupPeopleCount().equals("1")) {
			holdView.historyGroupPeopleNum.setText(groupListBean.getUser_name()
					+ groupListBean.getGroupPeopleCount() + "人在线");
		} else {
			holdView.historyGroupPeopleNum.setText(groupListBean.getUser_name()
					+ "等" + groupListBean.getGroupPeopleCount() + "人在线");
		}
		holdView.historyLastTime.setText(TimeUtil.getTimes(groupListBean
				.getGroupTime()) + "进入");
	}

	public class HoldView {
		public TextView historyGroupMsgCount, historyGroupName,
				historyGroupPeopleNum, historyLastTime;
		private ImageView atImage;
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
			atImage = (ImageView) convertView
			 .findViewById(R.id.atImage);
		}
	}
}
