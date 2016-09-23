package com.clien;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.util.Log;

 



public class ClientMessageHandlerAdapter extends IoHandlerAdapter {

	public interface Callback {
		
		void connected();

		void loggedIn();

		void loggedOut();

		void disconnected();

		void messageReceived(String message);

		void error(String message);
		
		/**
		 * @param 发送报告
		 */
		void messageSentReport(String message);
		
	}
	private final Callback callback;
//    private final static Logger log = LoggerFactory.getLogger(ClientMessageHandlerAdapter.class);

    public ClientMessageHandlerAdapter(Callback callback) {
		this.callback = callback;
	}


    public void messageReceived(IoSession session, Object message) throws Exception {
    	 Log.i(  "aaa","messageSent 服务器发送消息：" + message);
        String content = message.toString();
        callback.messageReceived(content);
        
    }

    

    public void messageSent(IoSession session , Object message) throws Exception{

//    	log.info("aaa","messageSent 客户端发送消息：" + message);
        Log.i(  "aaa","messageSent 客户端发送消息：" + message);
        callback.loggedIn();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
//    	log.info("aaa","服务器发生异常： {}", cause.getMessage());
        Log.i(  "aaa","服务器发生异常： {} ====== "+ cause.getMessage());
        callback.error("聊天断开");
        
    }

}