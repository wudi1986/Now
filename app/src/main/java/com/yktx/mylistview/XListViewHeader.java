/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.yktx.mylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yktx.group.R;

public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	// private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private ImageView xlistview_header_stop;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
	public final static int STATE_GOHOME = 3;
	boolean isMyProgressBar;

	public XListViewHeader(Context context, boolean isMyProgressBar) {
		super(context);
		this.isMyProgressBar = isMyProgressBar;
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public void setBackGround(int color) {
		mContainer.setBackgroundColor(color);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		if (isMyProgressBar) {
			mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.xlistview_fragment_header, null);
		} else {
			mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.xlistview_header, null);
		}
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		// mArrowImageView =
		// (ImageView)findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);
		xlistview_header_stop = (ImageView) findViewById(R.id.xlistview_header_stop);
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState)
			return;

		// if (state == STATE_REFRESHING) { // 显示进度
		// mArrowImageView.clearAnimation();
		// mArrowImageView.setVisibility(View.INVISIBLE);
		if (isMyProgressBar) {
			xlistview_header_stop.setVisibility(View.VISIBLE);

		}
		// } else { // 显示箭头图片
		// // mArrowImageView.setVisibility(View.VISIBLE);
		// mProgressBar.setVisibility(View.INVISIBLE);
		// }

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				// mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				// mArrowImageView.clearAnimation();
			}

			mHintTextView.setText(R.string.xlistview_header_hint_normal);
			if (isMyProgressBar) {
				xlistview_header_stop.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				// mArrowImageView.clearAnimation();
				// mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(R.string.xlistview_header_hint_ready);
				if (isMyProgressBar) {
					xlistview_header_stop.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
				}
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(R.string.xlistview_header_hint_loading);
			if (isMyProgressBar) {
				xlistview_header_stop.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
			break;

		case STATE_GOHOME:
			mHintTextView.setText(R.string.xlistview_header_hint_gohome);
			if (isMyProgressBar) {
				xlistview_header_stop.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
