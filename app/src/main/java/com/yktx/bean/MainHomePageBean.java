/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月14日 下午3:30:34  
 * 类说明  点赞消息 */
/**
 * @author Administrator
 * 
 */
public class MainHomePageBean<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2703084279117080267L;
	private int currentPage;
	private int totalCount;
	private int totalPage;
	private int pageLimit;
	private ArrayList<T> listData = new ArrayList<T>(10);
	private String group_id;
	private String group_name	;
	private String groupManCount;	
	private String msgnum;	
	public String getMsgnum() {
		return msgnum;
	}

	public void setMsgnum(String msgnum) {
		this.msgnum = msgnum;
	}

	private boolean is_have;

	
	
	public boolean isIs_have() {
		return is_have;
	}

	public void setIs_have(boolean is_have) {
		this.is_have = is_have;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getGroupManCount() {
		return groupManCount;
	}

	public void setGroupManCount(String groupManCount) {
		this.groupManCount = groupManCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public ArrayList<T> getListData() {
		return listData;
	}

	public void setListData(ArrayList<T> listData) {
		this.listData = listData;
	}
}
