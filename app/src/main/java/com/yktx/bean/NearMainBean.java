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
public class NearMainBean implements Serializable {

	private String group_id;
	private String group_name;
	private String groupManCount;
	private String distance = "-1";
	private String msgnum;
	private ArrayList<MsgListBean> msgList = new ArrayList<MsgListBean>(10);

	public ArrayList<MsgListBean> getMsgList() {
		return msgList;
	}

	public void setMsgList(ArrayList<MsgListBean> msgList) {
		this.msgList = msgList;
	}

	public String getMsgnum() {
		return msgnum;
	}

	public void setMsgnum(String msgnum) {
		this.msgnum = msgnum;
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

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public static class MsgListBean {

		private String content;
		private String user_name;
		private long send_time;
		private int flag;
		private int status;
		private String message_id;
		private String content_title;

		public String getContent_title() {
			return content_title;
		}

		public void setContent_title(String content_title) {
			this.content_title = content_title;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getUser_name() {
			return user_name;
		}

		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}

		public long getSend_time() {
			return send_time;
		}

		public void setSend_time(long send_time) {
			this.send_time = send_time;
		}

		public String getMessage_id() {
			return message_id;
		}

		public void setMessage_id(String message_id) {
			this.message_id = message_id;
		}

	}

}