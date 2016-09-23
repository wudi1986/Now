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
public class UserInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3549592814250648844L;
	// {"content":"烦烦烦","send_time":1415083097858,"status":0,"user_id":749,"type":0}
	private String id;
	private String name;
	private String photo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

}
