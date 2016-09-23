package com.yktx.view;

/**
 * 
 */

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yktx.bean.ChatListBean;
import com.yktx.bean.GroupMemberListBean;
import com.yktx.bean.ZanBean;
import com.yktx.group.ChatActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.R;
import com.yktx.group.adapter.ChatOnLineAdapter;
import com.yktx.group.conn.ServiceListener;
import com.yktx.util.Contanst;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年8月13日 下午4:59:34  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class ChatPopOnLineNumberView extends LinearLayout implements
		ServiceListener {

	Activity mContext;
	OnOnLineClickBack onClickBack;
	OnOnLineClickHeadCenter onClickHeadCenter;
	// GoodsListView goodsListView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null).showImageOnFail(null)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.displayer(new RoundedBitmapDisplayer(100)).cacheInMemory(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	public DisplayImageOptions options2 = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_null)
			.showImageForEmptyUri(null)
			.showImageOnFail(null)
			// .displayer(new RoundedBitmapDisplayer(120))

			// .displayer(new RoundedBitmapDisplayer(20))
			.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	/**
	 * @param context
	 */
	LinearLayout homeSale;
	ArrayList<GroupMemberListBean> groupMembersList;

	String userid;
	String userName = null;
	public ImageView gatback;
	public TextView onLineName, onLineNum;

	private GridView onLineGridView;
	ChatOnLineAdapter chatOnLineAdapter;

	public ChatPopOnLineNumberView(ChatActivity mContext) {
		super(mContext);
		// getBestView(mContext, chatID);
	}


	public void setOnLineOnClickBack(OnOnLineClickBack onClickBack) {
		this.onClickBack = onClickBack;
	}
	public void updateGridView(){
		chatOnLineAdapter.notifyDataSetChanged();
	}
	
	public void setOnLineOnClickHeadCenter(OnOnLineClickHeadCenter onClickHeadCenter) {
		this.onClickHeadCenter = onClickHeadCenter;
	}

	public LinearLayout getBestView(Activity mContext,
			ArrayList<GroupMemberListBean> groupMembersList, String GroupName, String onLineNumStr) {
		this.groupMembersList = groupMembersList;
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		homeSale = (LinearLayout) mInflater.inflate(
				R.layout.chat_online_activity, null);
		SharedPreferences settings = mContext.getBaseContext()
				.getSharedPreferences(((GroupApplication)(mContext.getApplicationContext())).getCurSP(), 0);
		userid = settings.getString("userID", "");
		// mContext.setContentView(R.layout.sequence);
		this.mContext = mContext;

		onLineName = (TextView) homeSale.findViewById(R.id.onLineName);
		onLineNum = (TextView) homeSale.findViewById(R.id.onLineNum);
		
		onLineName.setText(GroupName);
		onLineNum.setText(onLineNumStr);

		onLineGridView = (GridView) homeSale.findViewById(R.id.onLineGridView);
		gatback = (ImageView) homeSale.findViewById(R.id.onLineFinish);
		init(mContext);

		return homeSale;
	}

	private void init(final Activity mContext) {
		chatOnLineAdapter = new ChatOnLineAdapter(mContext,
				 groupMembersList);
		onLineGridView.setAdapter(chatOnLineAdapter);
	
		LinearLayout popLayout = (LinearLayout) homeSale
				.findViewById(R.id.popBaseLayout);

		gatback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (onClickBack != null) {
					onClickBack.getClick();
				}
			}
		});

		// initPhotoZan(chatBean, holdView);

		onLineGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (onClickHeadCenter != null) {
					onClickHeadCenter.getClick(groupMembersList.get(arg2).getId(), arg2);
				}
			}
		});

	

		// RelativeLayout userLayout = (RelativeLayout) homeSale
		// .findViewById(R.id.userLayout);

	}

	private boolean isCanZan(ChatListBean chatBean, String content) {
		ArrayList<ZanBean> zanList = chatBean.getZanList();
		int size = zanList.size();
		for (int i = 0; i < size; i++) {
			ZanBean bean = zanList.get(i);

			if (bean.getZanUserID().equals(userid)) {
				// 如果赞的list里面有自己的id 已经赞过，不能在点击
				return false;
			}

		}
		return true;
	}


	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataFail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataFail(String errcode, String errmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		if (errcode.equals("10011"))
			msg.obj = errcode;
		else
			msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				if (msg.arg1 == Contanst.GETGATLIST) {
					ChatListBean chatBean = (ChatListBean) msg.obj;
//					init(mContext, chatBean);
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	public interface OnOnLineClickBack {
		public void getClick();
	}

	public interface OnOnLineClickHeadCenter {
		public void getClick(String userID, int position);
	}

}
