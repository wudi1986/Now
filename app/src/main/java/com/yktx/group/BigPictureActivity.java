package com.yktx.group;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.yktx.bean.BigImageBean;
import com.yktx.view.ChatPopView;
import com.yktx.view.ChatPopView.OnClickBack;

public class BigPictureActivity extends BaseActivity {
	/**
	 * 弹出详情页
	 */
	ChatPopView chatPopView;

	String photoChatID, imageUrl;
	LinearLayout popView;
	int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_picture_activity);
		popView = (LinearLayout) findViewById(R.id.popView);
		photoChatID = getIntent().getStringExtra("photoChatID");
		index = getIntent().getIntExtra("photoIndex", 0);
		imageUrl = getIntent().getStringExtra("photoUrl");
		initPopView();
	}

	private void initPopView() {
		// TODO Auto-generated method stub
		chatPopView = new ChatPopView(BigPictureActivity.this, true, true);
		chatPopView.setOnClickBack(onClickBack);
		ArrayList<BigImageBean> list = new ArrayList<BigImageBean>(1);
		String array[] = imageUrl.split(",");
		for (int i = 0; i < array.length; i++) {
			BigImageBean bean = new BigImageBean();
			bean.setChatID(photoChatID);
			bean.setIndex(i);
			bean.setImageUrl(array[i]);
			list.add(bean);
		}
		
		popView.addView(chatPopView.getBestView(BigPictureActivity.this, list,
				index));
		popView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				BigPictureActivity.this.finish();
				return true;
			}
		});

	}

	/**
	 * 详情页返回
	 */
	OnClickBack onClickBack = new OnClickBack() {

		@Override
		public void getClick() {
			// TODO Auto-generated method stub
			BigPictureActivity.this.finish();
		}
	};

}
