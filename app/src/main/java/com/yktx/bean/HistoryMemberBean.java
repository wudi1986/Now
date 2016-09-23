/**
 * 
 */
package com.yktx.bean;

import java.io.Serializable;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月14日 下午2:25:44  
 * 类说明 消息记录  */
/**
 * @author Administrator
 * 
 */
public class HistoryMemberBean implements Serializable {

	// {"content":"烦烦烦","send_time":1415083097858,"status":0,"user_id":749,"type":0}
	private String id;

	private String photo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
