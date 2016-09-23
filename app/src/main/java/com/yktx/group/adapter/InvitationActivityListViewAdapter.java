package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.Date;

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
import com.yktx.bean.GetAttentionListBean;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
public class InvitationActivityListViewAdapter extends BaseAdapter {
	Activity context;
	String latitude;
	String longitude;
	InvitationButtonListener invitationButtonListener;
	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.head_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.cacheInMemory(true).displayer(new RoundedBitmapDisplayer(100))
			// 启用内存缓存
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<GetAttentionListBean> list = new ArrayList<GetAttentionListBean>(
			10);
	IntoUserCenterListener intoUserCenter;

	public void setIntoUserCenter(IntoUserCenterListener intoUserCenter) {
		this.intoUserCenter = intoUserCenter;
	}

	public void setInvitationButtonListener(
			InvitationButtonListener invitationButtonListener) {
		this.invitationButtonListener = invitationButtonListener;
	}

	public InvitationActivityListViewAdapter(Activity context) {
		this.context = context;
	}

	public void setList(ArrayList<GetAttentionListBean> list) {
		this.list = list;
	}

	public void setDistance(String latitude, String longitude) {
		// TODO Auto-generated method stub
		this.latitude = latitude;
		this.longitude = longitude;
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
		GetAttentionListBean getAttentionListBean = (GetAttentionListBean) list
				.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.invitation_activity_item, null);
			holdView = new InvitationActivityListViewAdapter.HoldView(
					convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, getAttentionListBean, position);

		return convertView;
	}

	private void show(final HoldView holdView,
			final GetAttentionListBean getAttentionListBean, final int position) {
		ImageLoader.getInstance().displayImage(
				UrlParams.IP + getAttentionListBean.getPhoto(),
				holdView.invitationUserHead, options);
		holdView.invitationUserHead
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (intoUserCenter != null) {
							intoUserCenter
									.getIntoUserCenter(getAttentionListBean
											.getUser_id());
						}
					}
				});

		holdView.invitationUserName.setText(getAttentionListBean.getUser_name());
		try {
			holdView.invitationUserAge.setText(TimeUtil.getAge(new Date(
					getAttentionListBean.getBirthday())) + "岁");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holdView.invitationUserLevel.setText("lv"
				+ getAttentionListBean.getIntegral());
		holdView.invitationGroupName.setText(getAttentionListBean
				.getGroup_name());
		holdView.invitationGroupPeopleNum.setText(getAttentionListBean
				.getLinenum() + "人在线");
		holdView.invitationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (invitationButtonListener != null) {
					invitationButtonListener.getInvitationButtonListener(
							getAttentionListBean.getUser_id());

				}
			}
		});
	}

	public class HoldView {
		public ImageView invitationUserHead;
		public TextView invitationUserName, invitationUserAge,
				invitationUserLevel, invitationGroupName,
				invitationGroupPeopleNum, invitationButton;
		

		public HoldView(View convertView) {
			invitationUserHead = (ImageView) convertView
					.findViewById(R.id.invitationUserHead);
			invitationUserName = (TextView) convertView
					.findViewById(R.id.invitationUserName);
			invitationUserAge = (TextView) convertView
					.findViewById(R.id.invitationUserAge);
			invitationUserLevel = (TextView) convertView
					.findViewById(R.id.invitationUserLevel);
			invitationGroupName = (TextView) convertView
					.findViewById(R.id.invitationGroupName);
			invitationGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.invitationGroupPeopleNum);
			invitationButton = (TextView) convertView
					.findViewById(R.id.invitationButton);
		
		}
	}

	public interface InvitationButtonListener {

		public void getInvitationButtonListener(String userID);
	}
}
