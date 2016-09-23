package com.yktx.listener;

/**
 * 联网服务反馈监听器
 * 基本联网流程：ui设置此监听器到Service，service设置监听器到HttpConnectinWrapper
 * 				最后数据连续回调到ui层
 * 
 * @author wudi
 */
public interface IntoUserCenterListener {

	public void getIntoUserCenter(String userID);
}
