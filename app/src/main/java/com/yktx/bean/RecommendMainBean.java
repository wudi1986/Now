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
public class RecommendMainBean implements Serializable {

	private String groupDesc;
	private String photo;
	private String groupManCount;
	private String group_id;
	private String group_name;
	private float offx;
	private float offy;
	private String color = "4285690225";
	

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public float getOffx() {
		return offx;
	}

	public void setOffx(float offx) {
		this.offx = offx;
	}

	public float getOffy() {
		return offy;
	}

	public void setOffy(float offy) {
		this.offy = offy;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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