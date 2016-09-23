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
public class HotMainBean implements Serializable {

	private String msgnum;
	private String groupManCount;
	private String group_id;
	private String group_name;

	public String getMsgnum() {
		return msgnum;
	}

	public void setMsgnum(String msgnum) {
		this.msgnum = msgnum;
	}

	public String getGroupManCount() {
		return groupManCount;
	}

	public void setGroupManCount(String groupManCount) {
		this.groupManCount = groupManCount;
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

}