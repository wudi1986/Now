package com.yktx.bean;

/**
 * 聊天listview的每个item的数据对象
 *
 * @author len
 *
 */
public class ChatPhotoBean{
    /**
     * 图片路径
     */
    private String imagePath;
    /**
     * 是否选中
     */
    private boolean isSelect;
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
