/**
 * 
 */
package com.yktx.group.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.yktx.bean.GroupListBean;
import com.yktx.sqlite.DBHelper;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年5月23日 下午1:59:20  
 * 类说明  */
/**
 * @author Administrator
 *
 */
public class BaseFragment extends Fragment{
	
	public ProgressDialog mDialog;
	
	/** 第几页 */
	public int currentPage;
	/** 总数 */
	public int totalCount;
	/** 总页数 */	
	public int totalPage;
	/** 一页多少条数据 */
	public int pageLimit = 10;
	/** 数据集合 */
	public String listData;
	/** 当前时间 */
	public long reflashTime;
	
	String longitude = "-1", latitude = "-1";
		
	OnGoHomeListener onGoHomeListener;
	
	ImageView imageListNull;
	
	public boolean  isReflushFragment = true;
	
	DBHelper dbHelper;
	ArrayList<GroupListBean> historyList = new ArrayList<GroupListBean>(10);
	/** 搜索进来的 */
	GroupListBean searchGroupBean;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(this.getActivity());
		historyList = dbHelper.getMainHistoryList(historyList);
	}
	
	public interface OnGoHomeListener {
		public void goHome();
	}
	
	public void setOnGoHomeListener(OnGoHomeListener onGoHomeListener){
		this.onGoHomeListener = onGoHomeListener;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }

		
		
	}
	
	/* 打开相机 */
//	public void opencamera(String group_id,String group_name,String group_distance,String group_peopleCount,String atMyNum) {
//
//		Intent cameraIntent = new Intent(BaseFragment.this.getActivity(),
//				CameraActivity.class);
//		cameraIntent.putExtra(CameraActivity.IsRegister, "0");
//		cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
//		cameraIntent.putExtra("longitude", longitude);
//		cameraIntent.putExtra("latitude", latitude);
//		cameraIntent.putExtra("group_id", group_id);
//		cameraIntent.putExtra("group_name", group_name);
//		cameraIntent.putExtra("group_distance", group_distance);
//		cameraIntent.putExtra("group_peopleCount", group_peopleCount);
//		cameraIntent.putExtra("atMyNum", atMyNum);
//		startActivityForResult(cameraIntent, 444);
//	}
	
	
	
	public void showProgressDialog(String message) {
		mDialog = new ProgressDialog(BaseFragment.this.getActivity());
		mDialog.setMessage(message);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
		mDialog.show();
		
		
	}
	
	public void insertSearchHistory(String group_id, String group_Name) {
		// 添加历史
		ArrayList<GroupListBean> historygroupList = new ArrayList<GroupListBean>(
				10);
		// if (isMain) {
		historygroupList = dbHelper.getMainHistoryList(historygroupList);
		// } else {
		// historygroupList = dbHelper.getNameList(historygroupList);
		// }

		if (historygroupList.size() > 0 && historygroupList != null) {
			for (int i = 0; i < historygroupList.size(); i++) {
				GroupListBean historyListBean = historygroupList.get(i);
				if (group_id.equals(historyListBean.getGroupID())) {
					Log.i(  "aaa", "重复数据" + group_Name);
					// 如果重复不添加
					historygroupList.get(i).setGroupTime(
							System.currentTimeMillis());
					// dbHelper.insertSearchList(historygroupList);

					// if (isMain) {
					dbHelper.insertMainHistoryList(historygroupList);
					// } else {
					// dbHelper.insertSearchList(historygroupList);
					// }
					return;
				}
			}

			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
//				if (group_distance != null) {
//					bean.setDistance(group_distance);
//				} else {
					bean.setDistance("0.04km");
//				}
//				if (group_peopleCount != null) {
//					bean.setGroupPeopleCount(group_peopleCount);
//				} else {
					bean.setGroupPeopleCount("1");
//				}
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		} else {
			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				dbHelper.insertMainHistoryList(historygroupList);
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				bean.setGroupPeopleCount("1");
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		}
	}
}
