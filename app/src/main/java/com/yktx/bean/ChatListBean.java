/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月14日 下午2:25:44  
 * 类说明 消息记录  */
/**
 * @author Administrator
 * 
 */
public class ChatListBean implements Serializable {

	// {"content":"烦烦烦","send_time":1415083097858,"status":0,"user_id":749,"type":0}
	private String chatId;
	/** 消息内容 */
	private String content;
	/** 消息时间 */
	private long sendTime;

	private String userId;

	private String groupId;

	/**
	 * "@"id 判断消息是否为空，如果不为空说明该条消息是@消息， 再判断是否包含自己的id，如果包含显示在右侧@样式，否则显示左侧（普通消息）
	 */
	private String[] atIDList;

	/**
	 * 消息状态 0--文本 1--图片 2--语音
	 */
	private String chatStatus;

	private String userHead;

	private String distance;

	private String userName;

	/**
	 * 消息发现哦那个状态 0--发送成功 1--发送中 2--发送失败
	 */
	private int chatSendStates = 0;

	/**
	 * 1 普通，2 @，3 第一次图片，4 赞
	 */
	private String flag;
	/**
	 * 第一次发图片的标题
	 */
	private String content_title;

	/**
	 * 点赞的对应的图片id
	 */
	private String photoChatID;

	private String sex = "0";
	private String level;
	private long birthday = 0;
	
	/** 自己的消息对应发送成功的map 如果发送失败，存储成功数据的路径 */
	HashMap< String , ChatPhotoBean> postSuccessMap = new HashMap<String, ChatPhotoBean>(3);

	public HashMap<String, ChatPhotoBean> getPostSuccessMap() {
		return postSuccessMap;
	}

	public void setPostSuccessMap(HashMap<String, ChatPhotoBean> postSuccessMap) {
		this.postSuccessMap = postSuccessMap;
	}

	private int brow;

	public int getBrow() {
		return brow;
	}

	public void setBrow(int brow) {
		this.brow = brow;
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

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getPhotoChatID() {
		return photoChatID;
	}

	public void setPhotoChatID(String photoChatID) {
		this.photoChatID = photoChatID;
	}

	/**
	 * 点赞list
	 */
	private ArrayList<ZanBean> zanList = new ArrayList<ZanBean>(2);

	public int getChatSendStates() {
		return chatSendStates;
	}

	public void setChatSendStates(int chatSendStates) {
		this.chatSendStates = chatSendStates;
	}

	/**
	 * 是否自己点过赞
	 */
	private boolean isCilckZan;

	public boolean isCilckZan() {
		return isCilckZan;
	}

	public void setCilckZan(boolean isCilckZan) {
		this.isCilckZan = isCilckZan;
	}

	/**
	 * @return the flag
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * @return the content_title
	 */
	public String getContent_title() {
		return content_title;
	}

	/**
	 * @param content_title
	 *            the content_title to set
	 */
	public void setContent_title(String content_title) {
		this.content_title = content_title;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the atIDList
	 */
	public String[] getAtIDList() {
		return atIDList;
	}

	/**
	 * @param atIDList
	 *            the atIDList to set
	 */
	public void setAtIDList(String[] atIDList) {
		this.atIDList = atIDList;
	}

	/**
	 * @return the zanList
	 */
	public ArrayList<ZanBean> getZanList() {
		return zanList;
	}

	/**
	 * @param zanList
	 *            the zanList to set
	 */
	public void setZanList(ArrayList<ZanBean> zanList) {
		this.zanList = zanList;
	}

	/**
	 * @return the chatId
	 */
	public String getChatId() {
		return chatId;
	}

	/**
	 * @param chatId
	 *            the chatId to set
	 */
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the sendTime
	 */
	public long getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime
	 *            the sendTime to set
	 */
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the chatStatus
	 */
	public String getChatStatus() {
		return chatStatus;
	}

	/**
	 * @param chatStatus
	 *            the chatStatus to set
	 */
	public void setChatStatus(String chatStatus) {
		this.chatStatus = chatStatus;
	}

	/**
	 * @return the userHead
	 */
	public String getUserHead() {
		return userHead;
	}

	/**
	 * @param userHead
	 *            the userHead to set
	 */
	public void setUserHead(String userHead) {
		this.userHead = userHead;
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

}
