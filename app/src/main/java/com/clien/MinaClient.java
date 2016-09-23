package com.clien;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.clien.ClientMessageHandlerAdapter.Callback;
import com.google.gson.Gson;

/**
 * 
 * <b>function:</b> mina客户端
 * 
 * @author hoojo
 * 
 * @createDate 2012-6-29 下午07:28:45
 * 
 * @file MinaClient.java
 * 
 * @package com.hoo.mina.client.message
 * 
 * @project ApacheMiNa
 * 
 * @blog http://blog.csdn.net/IBM_hoojo
 * 
 * @email hoojo_@126.com
 * 
 * @version 1.0
 */

public class MinaClient {

	private SocketConnector connector;

	private ConnectFuture future;

	public IoSession session;
	
	public static MinaClient minaClient;
	
	public static synchronized MinaClient getMinaClient(){
		if(minaClient == null){
			minaClient = new MinaClient();
		}
		return minaClient;
	}
	
	public boolean isClient() {
		if(minaClient != null && minaClient.getSession() != null
				&& !minaClient.getSession().isClosing()
				&& minaClient.getSession().isConnected())
			return true;
		
		return false;
				
	}
	

	public boolean connect(Callback callback, String groupID, String userID) {

		// 创建一个socket连接

		connector = new NioSocketConnector();
		// 设置链接超时时间

		connector.setConnectTimeoutMillis(3000);
		
		// 获取过滤器链
		
		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
		
		// 添加编码过滤器 处理乱码、编码问题
		
		filterChain.addLast("codec", new ProtocolCodecFilter(
				new CharsetCodecFactory()));
		
		// 消息核心处理器
		
		connector.setHandler(new ClientMessageHandlerAdapter(callback));
		
		// 连接服务器，知道端口、地址

//		future = connector
//				.connect(new InetSocketAddress("223.203.216.190", 3456));
//		future = connector
//				.connect(new InetSocketAddress("192.168.199.2", 3456));
//		future = connector
//		.connect(new InetSocketAddress("192.168.3.11", 3456));
//		future = connector
//		.connect(new InetSocketAddress("now.yt521.net", 3456));
		future = connector
		.connect(new InetSocketAddress("182.92.172.174", 3456));
		
		
		
		// 等待连接创建完成
		
		future.awaitUninterruptibly();
		// 获取当前session
		try {
			session = future.getSession();
			HashMap<String,String> map=new HashMap<String,String>();
        	map.put("flag", "0");
        	map.put("group_id", groupID);
        	map.put("user_id", userID);
        	
        	send(map);
//        	Gson gson = new Gson();
//    		String json = gson.toJson(map);
//    		session.write(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
		// session.setAttribute("group_id", "111111");
		return true;

	}

	public void setAttribute(Object key, Object value) {

		session.setAttribute(key, value);

	}

	int i = 0;

	public void send(Map<String, String> map) {
		Gson gson = new Gson();
		String json = gson.toJson(map);
//		if (i == 0) {
//			i++;
//			session.write(message);
//		} else {
			session.write(json);
//		}
	}

	public boolean close() {

		CloseFuture future = session.getCloseFuture();

		future.awaitUninterruptibly(1000);

		connector.dispose();

		return true;

	}

	public SocketConnector getConnector() {

		return connector;

	}

	public IoSession getSession() {

		return session;

	}

}
