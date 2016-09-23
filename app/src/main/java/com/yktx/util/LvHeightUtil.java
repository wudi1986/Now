/**
 * 
 */
package com.yktx.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年5月29日 下午2:47:58  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class LvHeightUtil {
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		Adapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
//			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		Log.i(  "aaa", "totalHeight ==== "+totalHeight);
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		
	}
}
