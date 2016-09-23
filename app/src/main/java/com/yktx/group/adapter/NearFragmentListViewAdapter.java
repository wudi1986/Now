package com.yktx.group.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yktx.bean.NearMainBean;
import com.yktx.bean.NearMainBean.MsgListBean;
import com.yktx.facial.ExpressionUtil;
import com.yktx.facial.Expressions;
import com.yktx.group.ChatActivity;
import com.yktx.group.R;

/**
 * Created by Administrator on 2014/4/8.
 */
public class NearFragmentListViewAdapter extends BaseAdapter {
	Activity context;
	String latitude;
	String longitude;

	ArrayList<NearMainBean> list = new ArrayList<NearMainBean>(10);

	public NearFragmentListViewAdapter(Activity context) {
		initFacialMap();
		this.context = context;
	}

	public void setList(ArrayList<NearMainBean> list) {
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
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		// NearMainBean nearMainBean = (NearMainBean) list.get(position);
		// if (position == 0)
		// return 2;
		return 1;
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView;

		int type = 0;
		type = getItemViewType(position);

		NearMainBean nearMainBean = (NearMainBean) list.get(position);
		if (convertView == null) {
			switch (type) {
			case 1:
				convertView = LayoutInflater.from(context).inflate(
						R.layout.near_fragment_item, null);
				break;
			// case 2:
			// convertView = LayoutInflater.from(context).inflate(
			// R.layout.near_fragment_item_first, null);
			// break;
			}

			holdView = new NearFragmentListViewAdapter.HoldView(convertView);

			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		show(holdView, nearMainBean, position, type);

		return convertView;
	}

	private void show(final HoldView holdView, NearMainBean nearMainBean,
			int position, int type) {

		switch (type) {
		case 1:
			// if(position < 5){
			// holdView.nearGroupName.setTextColor(context.getResources().getColor(R.color.meibao_color3));
			// } else {
			// holdView.nearGroupName.setTextColor(context.getResources().getColor(R.color.meibao_item_name_color));
			// }
			if (nearMainBean.getDistance().equals("火星")) {
				holdView.nearUserDistance.setText(nearMainBean.getDistance());
				holdView.imageView2.setImageResource(R.drawable.near_50);
			} else {
				float distance = Float.parseFloat(nearMainBean.getDistance());
//				if (distance < 1) {
//					holdView.imageView2.setImageResource(R.drawable.near_0to1);
//				} else if (distance < 10) {
//
//					holdView.imageView2.setImageResource(R.drawable.near_1to10);
//				} else if (distance < 50) {
//
//					holdView.imageView2
//							.setImageResource(R.drawable.near_10to50);
//				} else {
//					holdView.imageView2.setImageResource(R.drawable.near_50);
//				}
				if (distance < 100) {

					holdView.nearUserDistance.setText(nearMainBean.getDistance());
				} else{
					int i_distance = (int) distance;
					holdView.nearUserDistance.setText(i_distance+"");
				}
			}
			holdView.nearGroupName.setText("#" + nearMainBean.getGroup_name());

			if (nearMainBean.getMsgList().size() > 0) {

				holdView.nearMessageNum.setText(chatText(nearMainBean
						.getMsgList().get(0)));
				if (nearMainBean.getMsgList().size() > 1) {

					holdView.nearGroupPeopleNum.setText(chatText(nearMainBean
							.getMsgList().get(1)));
				} else {
					holdView.nearGroupPeopleNum.setVisibility(View.INVISIBLE);
				}
			} else {
				holdView.nearMessageNum.setText("暂无消息");
				holdView.nearGroupPeopleNum.setVisibility(View.INVISIBLE);
			}
			
			break;
		case 2:
			// holdView.nearGroupName.setText(nearMainBean.getGroup_name());
			// holdView.nearGroupPeopleNum.setText(nearMainBean.getGroupManCount()+"人在线");
			// holdView.nearMessageNum.setText("累计"+nearMainBean.getMsgnum()+"条消息");

			break;
		}

	}

	public HashMap<String, Object> chatFacialMap = new HashMap<String, Object>();

	private void initFacialMap() {
		for (int i = 0; i < Expressions.expressionImgNames.length; i++) {
			chatFacialMap.put(Expressions.expressionImgNames[i],
					Expressions.expressionImgs[i]);
		}

	}

	private boolean ChatTextFacial(String chatContent) {

		boolean spannableString = false;
		String zhengze = "\\[#[0-9][0-9]?\\]";
		try {
			spannableString = ExpressionUtil.isHave(context, chatContent,
					zhengze, chatFacialMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spannableString;
	}

	private String chatText(MsgListBean bean) {
		StringBuffer sb = new StringBuffer();
		switch (bean.getFlag()) {
		case ChatActivity.CHAT_FLAG_POP_MESSAGE:
			switch (bean.getStatus()) {
			case 0: // 文字
				sb.append(bean.getUser_name());
				sb.append("：");
				if (ChatTextFacial(bean.getContent())) {
					sb.append("发了一个表情");
				} else
					sb.append(bean.getContent());
				break;
			case 1: // 图片

				sb.append(bean.getUser_name());
				sb.append("：发布一张图片");
				break;
			case 2: // 语音
				sb.append(bean.getUser_name());
				sb.append("：发布一段语音");
				break;
			}
			break;
		case ChatActivity.CHAT_FLAG_AT_MESSATE:
			sb.append(bean.getUser_name());
			sb.append("：");
			sb.append(bean.getContent());
			break;
		case ChatActivity.CHAT_FLAG_IMAGE_AND_TEXT_MESSAGE:
			sb.append(bean.getUser_name());
			sb.append("：发布图片");
			break;
		case ChatActivity.CHAT_FLAG_ZAN_MESSAGE:
			String zanName = bean.getContent_title().split(",")[1];
			sb.append(zanName);
			sb.append("：赞  ");
			sb.append(bean.getUser_name());
			break;
		}

		return sb.toString();
	}

	public class HoldView {
		public TextView nearUserDistance, nearGroupName, nearGroupPeopleNum,
				nearMessageNum;
		public ImageView imageView2;

		public HoldView(View convertView) {

			nearUserDistance = (TextView) convertView
					.findViewById(R.id.newUserDistance);
			nearGroupName = (TextView) convertView
					.findViewById(R.id.newGroupName);
			nearGroupPeopleNum = (TextView) convertView
					.findViewById(R.id.newGroupPeopleNum);
			nearMessageNum = (TextView) convertView
					.findViewById(R.id.nearMessageNum);
			imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);

		}
	}
}
