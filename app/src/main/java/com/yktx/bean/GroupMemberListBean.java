/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;

/**  
 * @author 作者 :  cyx
 * @version 创建时间：2014年10月29日16:33:21
 * 类说明 
 *		返回群组成员列表信息
 * */
public class GroupMemberListBean implements Serializable {

	/** 用户ID */
	private String id;
	/** 用户名字 */
	private String name;
	/** 距离 */
	private String distance;
	/** 用户头像 */
	private String photo;
	
	private long birthday;
	private String sex = "0";
	private String integral;
	private String is_att;

	
	public String getIs_att() {
		return is_att;
	}
	public void setIs_att(String is_att) {
		this.is_att = is_att;
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
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @param userID
	 */
	public void setId(String memberID) {
		this.id = memberID;
	}
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param userName
	 */
	public void setName(String memberName) {
		this.name = memberName;
	}
	/**
	 * 
	 * @return
	 */
	public String getDistance() {
		return distance;
	}
	/**
	 * 
	 * @param distance
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}
	/**
	 * 
	 * @return
	 */
	public String getPhoto() {
		return photo;
	}
	/**
	 * 
	 * @param photo
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
