package com.yktx.group.conn;

/**
 * http网络连接监听器
 * 
 * @author wudi
 * @version 2011-02-25
 */
public interface HttpPostListener {

	/**
	 * 连接结果回调方法，reponse中的
	 * 
	 * <pre>
	 * retcode：C_S,数据返回成功，开始解析
	 * retcode:code,数据返回失败，查看code和errmsg(错误原因)
	 * 上述两种情况均代表已连接到服务器并成功下载数据
	 * 
	 * @param reponse 返回数据字符串
	 */
	public void connSuccess(String reponse);

	/**
	 * 连接失败回调方法，包括： 联网IO异常、联网超时以及其他不可预知的异常
	 * 
	 * @param erro
	 *            错误信息(IO/Timeout/Exception)
	 */
	public void connFail(String erro);

}
