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
public class AttentionMainBean implements Serializable {

	private String user_id;
	private String user_name;
	private String sex;
	private String photo;
	private long birthday = 0;
	private String integral;
	private String is_line;
	private String linenum;
	private String group_id;
	private String group_name;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthday) {
		this.birthday = birthday;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getIs_line() {
		return is_line;
	}

	public void setIs_line(String is_line) {
		this.is_line = is_line;
	}

	public String getLinenum() {
		return linenum;
	}

	public void setLinenum(String linenum) {
		this.linenum = linenum;
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