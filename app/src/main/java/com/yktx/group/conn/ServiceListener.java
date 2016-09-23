package com.yktx.group.conn;

/**
 * 联网服务反馈监听器
 * 基本联网流程：ui设置此监听器到Service，service设置监听器到HttpConnectinWrapper
 * 				最后数据连续回调到ui层
 * 
 * @author wudi
 */
public interface ServiceListener {

	/**
	 * 成功返回数据封装类
	 * @param bean 数据解析后封装数据类
	 */
	public void getJOSNdataSuccess(Object bean,String sccmsg, int connType );
	
	/**
	 * 请求失败
	 * @param errcode 错误码
	 * @param errmsg 错误信息
	 */
	public void getJOSNdataFail(String errcode,String errmsg, int connType);
	
}
