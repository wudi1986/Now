/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月14日 下午3:30:34  
 * 类说明  点赞消息 */
/**
 * @author Administrator
 * 
 */
public class ZanBean implements Serializable {

	private String zanUserID;

	private String zanUserHead;
	
	private String zanUserName;
	
	private long birthday;
	
	private String sex;
	
	private String level;
	
	private int brow;

	public int getBrow() {
		return brow;
	}

	public void setBrow(int brow) {
		this.brow = brow;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the zanUserName
	 */
	public String getZanUserName() {
		return zanUserName;
	}

	/**
	 * @param zanUserName
	 *            the zanUserName to set
	 */
	public void setZanUserName(String zanUserName) {
		this.zanUserName = zanUserName;
	}

	/**
	 * @return the zanUserID
	 */
	public String getZanUserID() {
		return zanUserID;
	}

	/**
	 * @param zanUserID
	 *            the zanUserID to set
	 */
	public void setZanUserID(String zanUserID) {
		this.zanUserID = zanUserID;
	}

	/**
	 * @return the zanUserHead
	 */
	public String getZanUserHead() {
		return zanUserHead;
	}

	/**
	 * @param zanUserHead
	 *            the zanUserHead to set
	 */
	public void setZanUserHead(String zanUserHead) {
		this.zanUserHead = zanUserHead;
	}

}
