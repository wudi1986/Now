package com.yktx.bottombar;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yktx.group.R;

/**
 * 
 * @see LinearLayout
 * @author wudi
 * @since JDK1.6,Android SDK1.6
 * @Comapny MobileSolu
 * @Copyright Copyright 2011 MobileSolu. All rights reserved.
 */
public class BottomBar extends LinearLayout {

	private ImageView lastFocusTab;
	private TextView lastFocusTextView;
	private ImageView lastFocusImageView;
	private LinearLayout lastFocusLayout ;
	
	private BottomBarOnClickListener bottomBarOnClickListener;
	private boolean isOnlayout = true;

	public BottomBar(Context context) {
		super(context);
	}

	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (!isOnlayout) {
			super.onLayout(changed, left, top, right, bottom);
			return;
		}
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure((right - left) / childCount, bottom - top);
			child.layout(0 + i * (getWidth() / childCount), 0, (i + 1)
					* getWidth() / childCount, getHeight());
			child.setFocusable(true);
			child.setClickable(true);
		}
	}

	/**
	 */
	public void setBottomBarOnClickListener(
			BottomBarOnClickListener bottomBarOnClickListener) {
		this.bottomBarOnClickListener = bottomBarOnClickListener;
	}


	/** ��������main Tab */
	public void setFocusPosition(int position, int idup[], int iddown[]) {
		if (position == (Integer) lastFocusTab.getTag())
			return;
		int index = (Integer) lastFocusTab.getTag();
		if (lastFocusTab != null)
			lastFocusTab.setBackgroundResource(idup[index]);
		getChildAt(position).setBackgroundResource(iddown[position]);
		lastFocusTab = (ImageView) getChildAt(position);
	}

	/** ��������main Tab */
	public void setFocusPosition(int position) {
		if (position == (Integer) lastFocusTextView.getTag())
			return;
		TextView tv = (TextView) getChildAt(position).findViewById(
				R.id.bottombar_text);
		ImageView imageView = (ImageView) getChildAt(position).findViewById(
				R.id.bottombar_image);
		if (lastFocusTextView != null){
			lastFocusTextView.setTextColor(color2);
		}
		if (lastFocusLayout!= null){
			lastFocusLayout.setBackgroundColor(backGroundUnSelectColor);
		}
		
		if (lastFocusImageView != null){
			lastFocusImageView.setImageResource(tabImageUnSelect[(Integer) lastFocusImageView.getTag()]);
		}
		lastFocusTextView.setTextColor(color3);
		lastFocusTextView = tv;
		lastFocusLayout =  (LinearLayout) getChildAt(position);
		lastFocusImageView.setImageResource(tabImageSelect[position]);
		lastFocusImageView = imageView;
		lastFocusLayout.setBackgroundColor(backGroundSelectColor);
	}
	
	int color3 = 0xffffffff;
	int color2 = 0xff727171;

	int backGroundSelectColor = 0xff000000;
	int backGroundUnSelectColor = 0xcc000000;
	
	
	int[] tabImageSelect = {R.drawable.tab_doing_liang,R.drawable.tab_fanxian_liang,R.drawable.tab_xiaoxi_liang,R.drawable.tab_wo_liang};
	int[] tabImageUnSelect = {R.drawable.tab_doing_an,R.drawable.tab_fanxian_an,R.drawable.tab_xiaoxi_an,R.drawable.tab_wo_an};
	// Group botton
	@SuppressLint("ResourceAsColor")
	public BottomBar addItems(String[] tabText, int defaultFocus ) {
		if (tabText == null)
			return this;

		for (int i = 0; i < tabText.length; i++) {
			final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
					this.getContext(), R.layout.bottombar_group_text, null);

			final TextView tv = (TextView) barItem
					.findViewById(R.id.bottombar_text);
			tv.setTag(i);
			tv.setText(tabText[i]);
			final ImageView imageView = (ImageView) barItem
					.findViewById(R.id.bottombar_image);
			imageView.setTag(i);
			if (defaultFocus == i) {
				tv.setTextColor(color3);
				lastFocusTextView = tv;
				lastFocusImageView = imageView;
				lastFocusLayout = barItem;
				imageView.setImageResource(tabImageSelect[i]);
				barItem.setBackgroundColor(backGroundSelectColor);
			} else {
				tv.setTextColor(color2);
				imageView.setImageResource(tabImageUnSelect[i]);
				barItem.setBackgroundColor(backGroundUnSelectColor);
			}
			barItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int curPosition = (Integer) tv.getTag();
					if (curPosition == (Integer) lastFocusTextView.getTag())
						return;
					if (lastFocusTextView != null){
						lastFocusTextView.setTextColor(color2);
					} 
					if (lastFocusImageView != null){
						lastFocusImageView.setImageResource(tabImageUnSelect[(Integer) lastFocusImageView.getTag()]);
					}

					if (lastFocusLayout != null){
						lastFocusLayout.setBackgroundColor(backGroundUnSelectColor);
					}
					tv.setTextColor(color3);
					imageView.setImageResource(tabImageSelect[curPosition]);
					barItem.setBackgroundColor(backGroundSelectColor);
					lastFocusTextView = tv;
					lastFocusImageView= imageView;
					lastFocusLayout = barItem;
					if (bottomBarOnClickListener != null)
						bottomBarOnClickListener.onClick(curPosition);
				}
			});

			this.addView(barItem);
		}
		return this;
	}

	// Group botton
	// public BottomBar addItems(int[] iconResIds, int defaultFocus) {
	// if (iconResIds == null)
	// return this;
	// for (int i = 0; i < iconResIds.length; i++) {
	// final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
	// this.getContext(), R.layout.bottombar_group, null);
	// barItem.setTag(i);
	// ImageView itemIcon = (ImageView) barItem
	// .findViewById(R.id.bottombar_item_icon);
	// itemIcon.setImageResource(iconResIds[i]);
	// if (defaultFocus == i) {
	// barItem.setBackgroundResource(R.drawable.tab_bg_d);
	// lastFocusTab = barItem;
	// } else {
	// barItem.setBackgroundResource(R.drawable.tab_bg_u);
	// }
	//
	// barItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// int curPosition = (Integer) barItem.getTag();
	// if (curPosition == (Integer) lastFocusTab.getTag())
	// return;
	// if (lastFocusTab != null)
	// lastFocusTab.setBackgroundResource(R.drawable.tab_bg_u);
	// barItem.setBackgroundResource(R.drawable.tab_bg_d);
	// lastFocusTab = barItem;
	// if (bottomBarOnClickListener != null)
	// bottomBarOnClickListener.onClick(curPosition);
	// }
	// });
	//
	// this.addView(barItem);
	// }
	// return this;
	// }

	@SuppressLint("ResourceAsColor")
	// public BottomBar addItems( final int[] iconResIdsUp,final int[]
	// iconResIdDown, final String [] title,int defaultFocus) {
	// if (iconResIdsUp == null)
	// return this;
	// for (int i = 0; i < iconResIdsUp.length; i++) {
	// final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
	// this.getContext(), R.layout.bottombar_group, null);
	// // barItem.setTag(i);
	// final ImageView itemIcon = (ImageView) barItem
	// .findViewById(R.id.bottombar_item_icon);
	// final TextView itemText = (TextView)
	// barItem.findViewById(R.id.bottombar_item_text);
	// itemText.setText(title[i]);
	// itemIcon.setTag(i);
	// itemIcon.setImageResource(iconResIdsUp[i]);
	// if (defaultFocus == i) {
	// itemIcon.setImageResource(iconResIdDown[i]);
	// itemText.setTextColor(getResources().getColor(R.color.meibao_color3));
	// // itemIcon.setImageResource(iconResIdDown[i]);
	// lastFocusTab = itemIcon;
	// lastFocusTextView = itemText;
	// } else {
	// // itemIcon.setImageResource(iconResIdsUp[i]);
	// itemIcon.setImageResource(iconResIdsUp[i]);
	// itemText.setTextColor(getResources().getColor(R.color.meibao_color_7));
	//
	// }
	//
	// barItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// int curPosition = (Integer) itemIcon.getTag();
	// if (curPosition == (Integer) lastFocusTab.getTag() && curPosition!= 0)
	// return;
	// if (lastFocusTab != null){
	// lastFocusTab
	// .setImageResource(iconResIdsUp[(Integer) lastFocusTab.getTag()]);
	// lastFocusTextView.setTextColor(getResources().getColor(R.color.meibao_color_7));
	//
	// }
	// itemIcon.setImageResource(iconResIdDown[curPosition]);
	// itemText.setTextColor(getResources().getColor(R.color.meibao_color3));
	// lastFocusTab = itemIcon;
	// lastFocusTextView = itemText;
	// if (bottomBarOnClickListener != null)
	// bottomBarOnClickListener.onClick(curPosition);
	// }
	// });
	//
	// this.addView(barItem);
	// }
	// return this;
	// }
	// public BottomBar addItems2( final int[] iconResIdsUp,final int[]
	// iconResIdDown, int defaultFocus) {
	// if (iconResIdsUp == null)
	// return this;
	// for (int i = 0; i < iconResIdsUp.length; i++) {
	// final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
	// this.getContext(), R.layout.bottombar_group2, null);
	// // barItem.setTag(i);
	// final ImageView itemIcon = (ImageView) barItem
	// .findViewById(R.id.bottombar_item_icon);
	// itemIcon.setTag(i);
	// itemIcon.setImageResource(iconResIdsUp[i]);
	// if (defaultFocus == i) {
	// itemIcon.setImageResource(iconResIdDown[i]);
	// // itemIcon.setImageResource(iconResIdDown[i]);
	// lastFocusTab = itemIcon;
	// } else {
	// // itemIcon.setImageResource(iconResIdsUp[i]);
	// itemIcon.setImageResource(iconResIdsUp[i]);
	// }
	//
	// barItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// int curPosition = (Integer) itemIcon.getTag();
	// if (curPosition == (Integer) lastFocusTab.getTag() && curPosition!= 0)
	// return;
	// if (lastFocusTab != null)
	// lastFocusTab
	// .setImageResource(iconResIdsUp[(Integer) lastFocusTab.getTag()]);
	// itemIcon.setImageResource(iconResIdDown[curPosition]);
	// lastFocusTab = itemIcon;
	// if (bottomBarOnClickListener != null)
	// bottomBarOnClickListener.onClick(curPosition);
	// }
	// });
	//
	// this.addView(barItem);
	// }
	// return this;
	// }
	private boolean isChecked;

	/**
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * 
	 * @param isOnLayout
	 * @param strRes
	 * @param icons
	 * @return
	 */
	// public BottomBar addItems(boolean isOnLayout, int strRes, int[] icons) {
	// this.isOnlayout = isOnLayout;
	// final LinearLayout leftItem = (LinearLayout) LinearLayout.inflate(
	// getContext(), R.layout.bottombar_item3, null);
	// TextView leftText = (TextView) leftItem
	// .findViewById(R.id.bottombar_item3_des);
	// leftText.setText(strRes);
	// final ImageView leftCheck = (ImageView) leftItem
	// .findViewById(R.id.bottombar_item3_check);
	// leftItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// isChecked = !isChecked;
	// if (isChecked)
	// leftCheck
	// .setImageResource(R.drawable.btn_comment_sametime_select);
	// else
	// leftCheck
	// .setImageResource(R.drawable.btn_comment_sametime_unselect);
	// }
	// });
	// this.addView(leftItem, new LinearLayout.LayoutParams(
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
	//
	// for (int i = 0; i < icons.length; i++) {
	// final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
	// getContext(), R.layout.bottombar_item2, null);
	// barItem.setTag(i);
	// ImageView itemIcon = (ImageView) barItem
	// .findViewById(R.id.bottombar_item_icon);
	// itemIcon.setImageResource(icons[i]);
	//
	// barItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// int curPosition = (Integer) barItem.getTag();
	// // 位置回调
	// if (bottomBarOnClickListener != null)
	// bottomBarOnClickListener.onClick(curPosition);
	// }
	// });
	//
	// this.addView(barItem, new LinearLayout.LayoutParams(
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
	// android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
	// }
	// return this;
	// }

	/**
	 * @return BottomBar
	 */
	// public BottomBar addItems(String[] itemNames, final int[] iconResIds,
	// final int[] iconFocusResIds) {
	// if (itemNames == null || iconResIds == null)
	// return this;
	// for (int i = 0; i < itemNames.length; i++) {
	// // 解析当前Item组件
	// final LinearLayout barItem = (LinearLayout) LinearLayout.inflate(
	// this.getContext(), R.layout.bottombar_item1, null);
	// barItem.setTag(i);
	// TextView itemText = (TextView) barItem
	// .findViewById(R.id.bottombar_item_text);
	// itemText.setText(itemNames[i]);
	// final ImageView itemIcon = (ImageView) barItem
	// .findViewById(R.id.bottombar_item_icon);
	// itemIcon.setTag(i);
	// itemIcon.setImageResource(iconResIds[i]);
	//
	// barItem.setOnTouchListener(new OnTouchListener() {
	// public boolean onTouch(View v, MotionEvent event) {
	// if (iconFocusResIds != null)
	// itemIcon
	// .setImageResource(iconFocusResIds[(Integer) itemIcon
	// .getTag()]);
	// return false;
	// }
	// });
	//
	// barItem.setOnClickListener(new OnClickListener() {
	// public void onClick(View v) {
	// itemIcon.setImageResource(iconResIds[(Integer) itemIcon
	// .getTag()]);
	// // 位置回调
	// if (bottomBarOnClickListener != null)
	// bottomBarOnClickListener.onClick((Integer) barItem
	// .getTag());
	// }
	// });
	//
	// this.addView(barItem);
	// }
	// return this;
	// }

}
