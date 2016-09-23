/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *     @author 作者 :  wudi  @version 创建时间：2014年10月29日16:32:34  类说明  返回群组列表信息  
 */
public class GroupListBean implements Serializable {

	/** 群ID */
	private String groupID = "";
	/** 群名字 */
	private String groupName = "";
	/** 距离 */
	private String distance = "";
	/** 群人数 */
	private String groupPeopleCount = "";
	/** 群头像 */
	private String groupPhoto = "";
	/** 进群预览时间 */
	private long groupTime;
	/** @ 未读消息 */
	private String atNum = "";

	/** 在线人名字 */
	private String user_name = "";

	private ArrayList<HistoryMemberBean> members = new ArrayList<HistoryMemberBean>();

	public ArrayList<HistoryMemberBean> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<HistoryMemberBean> members) {
		this.members = members;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAtNum() {
		return atNum;
	}

	public void setAtNum(String atNum) {
		this.atNum = atNum;
	}

	/**
	 * @return the groupTime
	 */
	public long getGroupTime() {
		return groupTime;
	}

	/**
	 * @param groupTime
	 *            the groupTime to set
	 */
	public void setGroupTime(long groupTime) {
		this.groupTime = groupTime;
	}

	/**
	 * @return the groupID
	 */
	public String getGroupID() {
		return groupID;
	}

	/**
	 * @param groupID
	 *            the groupID to set
	 */
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName
	 *            the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the groupPeopleCount
	 */
	public String getGroupPeopleCount() {
		return groupPeopleCount;
	}

	/**
	 * @param groupPeopleCount
	 *            the groupPeopleCount to set
	 */
	public void setGroupPeopleCount(String groupPeopleCount) {
		this.groupPeopleCount = groupPeopleCount;
	}

	/**
	 * @return the groupPhoto
	 */
	public String getGroupPhoto() {
		return groupPhoto;
	}

	/**
	 * @param groupPhoto
	 *            the groupPhoto to set
	 */
	public void setGroupPhoto(String groupPhoto) {
		this.groupPhoto = groupPhoto;
	}

}
