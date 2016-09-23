package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.GetAttentionListBean;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.RegisterActivity;
import com.yktx.group.conn.UrlParams;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.util.Contanst;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
public class AttentionActivityListViewAdapter extends BaseAdapter {
	Activity context;
	String latitude;
	String longitude;
	AttentionButtonListener attentionButtonListener;
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

	public void setAttentionButtonListener(
			AttentionButtonListener attentionButtonListener) {
		this.attentionButtonListener = attentionButtonListener;
	}

	public AttentionActivityListViewAdapter(Activity context) {
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
					R.layout.attention_activity_item, null);
			holdView = new AttentionActivityListViewAdapter.HoldView(
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
				holdView.attentionUserHead, options);
		holdView.attentionUserHead
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

		holdView.attentionUserName.setText(getAttentionListBean.getUser_name());
		if (getAttentionListBean.getSex().equals("-1")) {
			holdView.attentionUserAge.setVisibility(View.GONE);
			holdView.attentionUserLevel.setVisibility(View.GONE);
		} else {
			holdView.attentionUserAge.setVisibility(View.VISIBLE);
			holdView.attentionUserLevel.setVisibility(View.VISIBLE);
			if (getAttentionListBean.getSex().equals("1")) {
				holdView.attentionUserAge
						.setBackgroundResource(R.drawable.shape_age);
				holdView.attentionUserAge.setTextColor(context.getResources()
						.getColor(R.color.meibao_color_9));
			} else {
				holdView.attentionUserAge
						.setBackgroundResource(R.drawable.shape_age_girl);
				holdView.attentionUserAge.setTextColor(context.getResources()
						.getColor(R.color.meibao_color_10));
			}

			try {

				holdView.attentionUserAge.setText(TimeUtil.getAge(new Date(
						getAttentionListBean.getBirthday())) + "岁");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			holdView.attentionUserLevel.setText("lv"
					+ getAttentionListBean.getIntegral());
		}
		holdView.attentionGroupName.setText("#"+getAttentionListBean
				.getGroup_name());
		if (getAttentionListBean.getIs_line().equals("1")) {
			holdView.onlineState.setText("当前：");
		} else {
			holdView.onlineState.setText("最后在：");
		}
		holdView.attentionGroupPeopleNum.setText(getAttentionListBean
				.getLinenum() + "人在线");
		if (getAttentionListBean.getIs_att().equals("1")) {
			holdView.attentionButton
					.setImageResource(R.drawable.unattention_button);
//			holdView.attentionBottomLayout
//					.setBackgroundResource(R.drawable.shape_member);
		} else {
			holdView.attentionButton
					.setImageResource(R.drawable.attention_button);
//			holdView.attentionBottomLayout
//					.setBackgroundResource(R.drawable.shape_color3);
		}
		if (getAttentionListBean.isLoading()) {
			holdView.attentionButton.setVisibility(View.GONE);
			holdView.attentionProgress.setVisibility(View.VISIBLE);
		} else {
			holdView.attentionButton.setVisibility(View.VISIBLE);
			holdView.attentionProgress.setVisibility(View.GONE);
		}

		holdView.attentionButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!GroupApplication.getInstance().curSP
						.equals(Contanst.UserStone)) {
					showdialogGoRegister();
				} else {
					holdView.attentionProgress.setVisibility(View.VISIBLE);
					holdView.attentionButton.setVisibility(View.GONE);
					list.get(position).setLoading(true);
					if (attentionButtonListener != null) {
						attentionButtonListener.getAttentionButtonListener(
								getAttentionListBean.getUser_id(),
								getAttentionListBean.getIs_att(), position);
					}
				}
			}
		});
	}

	private void showdialogGoRegister() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						R.style.CustomDiaLog_by_SongHang));
		builder.setTitle("提示");
		builder.setMessage("游客身份禁止此项操作");
		builder.setPositiveButton("去注册", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GroupApplication.getInstance().backHomeActivity();
				Intent intentdata = new Intent(
						GroupApplication.getInstance().activityList.get(0),
						RegisterActivity.class);
				GroupApplication.getInstance().activityList.get(0)
						.startActivity(intentdata);

			}
		});
		builder.setNegativeButton("我知道了", null);
		builder.show();
	}

	public class HoldView {
		public ImageView attentionUserHead, attentionButton;
		public TextView attentionUserName, attentionUserAge,
				attentionUserLevel, attentionGroupName,
				attentionGroupPeopleNum, onlineState;
		private ProgressBar attentionProgress;
//		private RelativeLayout attentionBottomLayout;

		public HoldView(View convertView) {
			attentionUserHead = (ImageView) convertView
					.findViewById(R.id.attentionUserHead);
			attentionUserName = (TextView) convertView
					.findViewById(R.id.attentionUserName);
			attentionUserAge = (TextView) convertView
					.findViewById(R.id.attentionUserAge);
			attentionUserLevel = (TextView) convertView
					.findViewById(R.id.attentionUserLevel);
			attentionGroupName = (TextView) convertView
					.findViewById(R.id.attentionGroupName);
			attentionGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.attentionGroupPeopleNum);
			attentionButton = (ImageView) convertView
					.findViewById(R.id.attentionButton);
			onlineState = (TextView) convertView.findViewById(R.id.onlineState);
			attentionProgress = (ProgressBar) convertView
					.findViewById(R.id.attentionProgress);
//			attentionBottomLayout = (RelativeLayout) convertView
//					.findViewById(R.id.attentionBottomLayout);

		}
	}

	public interface AttentionButtonListener {

		public void getAttentionButtonListener(String userID, String isAtt,
				int position);
	}
}
