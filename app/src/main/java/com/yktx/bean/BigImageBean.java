/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月14日 下午2:25:44  
 * 类说明 用户头像姓名列表  */
/**
 * @author Administrator
 * 
 */
public class BigImageBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3549592814250648844L;
	// {"content":"烦烦烦","send_time":1415083097858,"status":0,"user_id":749,"type":0}
	private String chatID;

	private int index;

	private String imageUrl;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getChatID() {
		return chatID;
	}

	public void setChatID(String chatID) {
		this.chatID = chatID;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
