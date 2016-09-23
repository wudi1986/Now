package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yktx.bean.AttentionMainBean;
import com.yktx.group.R;
import com.yktx.group.conn.UrlParams;
import com.yktx.listener.IntoUserCenterListener;
import com.yktx.util.ImageTool;
import com.yktx.util.TimeUtil;

/**
 * Created by Administrator on 2014/4/8.
 */
public class AttentionFragmentListViewAdapter extends BaseAdapter {
	Activity context;
	String latitude;
	String longitude;
	IntoUserCenterListener intoUserCenterListener;
	 public DisplayImageOptions options = new DisplayImageOptions.Builder()
	 .showImageOnLoading(R.drawable.head_null)
	 .showImageForEmptyUri(null).showImageOnFail(null)
	 .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
//	 .cacheInMemory(true).displayer(new RoundedBitmapDisplayer(100))
	 // 启用内存缓存
	 .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	ArrayList<AttentionMainBean> list = new ArrayList<AttentionMainBean>(10);

	public AttentionFragmentListViewAdapter(Activity context) {
		this.context = context;
	}

	public void setList(ArrayList<AttentionMainBean> list) {
		this.list = list;
	}

	public void setDistance(String latitude, String longitude) {
		// TODO Auto-generated method stub
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void setIntoUserCenterListener(
			IntoUserCenterListener intoUserCenterListener) {
		this.intoUserCenterListener = intoUserCenterListener;
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
		AttentionMainBean attentionMainBean = (AttentionMainBean) list
				.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.attention_fragment_item, null);
			holdView = new AttentionFragmentListViewAdapter.HoldView(
					convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, attentionMainBean);

		return convertView;
	}

	private void show(final HoldView holdView,
			final AttentionMainBean attentionMainBean) {
		
		
		ImageLoader.getInstance().displayImage(UrlParams.IP + attentionMainBean.getPhoto(), holdView.attentionUserHead,options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				if (!attentionMainBean.getIs_line().equals("1")) {
					holdView.attentionUserHead.setImageBitmap(ImageTool
							.toGrayscale(loadedImage));
					holdView.attentionTime.setText("最后进入：");
				}else{
					holdView.attentionTime.setText("正在：");
				}
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
		

		holdView.attentionUserHead
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (intoUserCenterListener != null) {
							intoUserCenterListener
									.getIntoUserCenter(attentionMainBean
											.getUser_id());
						}
					}
				});

		// }

		holdView.attentionUserName.setText(attentionMainBean.getUser_name());
		try {
			holdView.attentionUserAge.setText(TimeUtil.getAge(new Date(
					attentionMainBean.getBirthday())) + "岁");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holdView.attentionUserLevel.setText("lv"
				+ attentionMainBean.getIntegral());
		if (attentionMainBean.getIs_line().equals("1")) {
			holdView.attentionGroupName.setTextColor(context.getResources().getColor(R.color.nearfragment_textother_color));
		} else {
			holdView.attentionGroupName.setTextColor(context.getResources().getColor(R.color.meibao_color_6));
		}
		holdView.attentionGroupName.setText(attentionMainBean.getGroup_name());
		holdView.attentionGroupPeopleNum.setText(attentionMainBean.getLinenum()
				+ "人在线");
	}

	public class HoldView {
		public com.yktx.util.RoundImageView attentionUserHead;
		public TextView attentionUserName, attentionUserAge,
				attentionUserLevel, attentionGroupName,
				attentionGroupPeopleNum,attentionTime;

		public HoldView(View convertView) {
			attentionUserHead = (com.yktx.util.RoundImageView) convertView
					.findViewById(R.id.newUserHead);
			attentionUserName = (TextView) convertView
					.findViewById(R.id.newUserName);
			attentionUserAge = (TextView) convertView
					.findViewById(R.id.newUserAge);
			attentionUserLevel = (TextView) convertView
					.findViewById(R.id.newUserLevel);
			attentionGroupName = (TextView) convertView
					.findViewById(R.id.newGroupName);
			attentionGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.newGroupPeopleNum);
			attentionTime = (TextView) convertView
					.findViewById(R.id.attentionTime);
		}
	}
}
