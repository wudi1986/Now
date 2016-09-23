/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月8日 下午2:57:06  
 * 类说明 
 *		返回群组列表信息
 * */
/**
 * @author Administrator
 * 
 */
public class HomeBean implements Serializable {

	private ArrayList<GroupListBean> hotList = new ArrayList<GroupListBean>(6);
	
	private ArrayList<GroupListBean> nearList = new ArrayList<GroupListBean>(4);

	/**
	 * @return the hotList
	 */
	public ArrayList<GroupListBean> getHotList() {
		return hotList;
	}

	/**
	 * @param hotList the hotList to set
	 */
	public void setHotList(ArrayList<GroupListBean> hotList) {
		this.hotList = hotList;
	}

	/**
	 * @return the nearList
	 */
	public ArrayList<GroupListBean> getNearList() {
		return nearList;
	}

	/**
	 * @param nearList the nearList to set
	 */
	public void setNearList(ArrayList<GroupListBean> nearList) {
		this.nearList = nearList;
	}
	
	

}
