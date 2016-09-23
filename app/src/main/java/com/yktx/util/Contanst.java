package com.yktx.util;

import com.yktx.group.R;



/**
 * @Title: Const.java
 * @Package com.mosition.giftizone.other
 * @Description: TODO(��һ�仰�������ļ���ʲô)
 * @author WUDI   
 * @date 2011-10-11 ����05:50:26
 * @version V1.0   

 */
public class Contanst {
	
	
	//============ 环信  ==========
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	//============ end ==========
	
	/** 显示调试信息 */
	public static final boolean isDebug = false;
	
	/** 判断登录时间 */
	public static final int LOGIN_TIME = 30;
	
	/**==================best list ����=========================*/
	public static final int BEST_INFO_OK = 0;
	public static final int BEST_INFO_FAIL = 1;
	public static final int MINA_CONN_FAIL = 2;
	public static final int MINA_MESSAGE_RECEIVED = 3;
	
	public static final int[] CHAT_FACIAL_SMALL = {R.drawable.chat_facial_small1, R.drawable.chat_facial_small2,
		R.drawable.chat_facial_small3, R.drawable.chat_facial_small4, R.drawable.chat_facial_small5,
		R.drawable.chat_facial_small6, R.drawable.chat_facial_small7, R.drawable.chat_facial_small8, R.drawable.chat_facial_small9};
	
	/**
	 * 游客记录
	 */
	public static final String TouristStone = "Tourist";

	/**
	 * 用户记录
	 */
	public static final String UserStone = "UserStone";
	
	/**
	 * 系统记录 记录是否自动登录，记住密码，经纬度
	 */
	public static final String SystemStone = "SystemStone";
	
	/**
	 * 是否登录状态
	 */
	public static boolean isLogin = false;
	
	/** 初始化mina以后需要发送消息 */
	public static final int INIT_MINA_SEND_MESSAGE = 4;
	public static final int MINA_ERROR = 5;
	
	/** 检测mina是否在连接 */
	public static final int MINA_TIMER_TASK = 6;
	/**==================发送聊天状态=========================*/
	/** 发送成功 */
	public static final int CHAT_SEND_SUCCESS = 0;
	/** 发送中 */
	public static final int CHAT_SEND_ING = 1;
	/** 发送失败 */
	public static final int CHAT_SEND_FAIL = 2;
	
	public static final String AUTO_LOGIN = "auto_login";
	
	public static final String SETTING_PREFER = "SETTING_PREFER"; // this is for saving c2dm registration key ...
	public static final String REG_ID = "REG_ID";
	public static final String TWITTER_REG = "TWITTER_REG";
	public static final String FACEBOOK_REG = "FACEBOOK_REG";

	public static String firstInGroupStr = "大家好，我来了。";
	public static String firstTouristInGroupStr = "我来了,各位亲。";
	
	public static final String HTTP_SUCCESS = "200";
	/**================= 联网获取list ========================*/
	/** 首页联网获取图片list */
//	public static ArrayList<ImageBean> mainList = new ArrayList<ImageBean>(10);
	
	
	/**================= 联网地址 ========================*/
	public static final String HTTP_SENDVERIFY = "sendVerify";
	public static final String HTTP_VERIFYCODE = "registeruser";
	public static final String HTTP_HOMEPAGE = "homePage";
	/** 注册验证 */
	public static final String HTTP_REGISTER_VERIFCATION = "registerVerification";
//	搜索：searchGroup
//	参数：Pager pager,String longitude,String latitude,String keywords
	public static final String HTTP_SEARCHGROUP = "searchGroup";
	/** 注册/修改用户 */
	public static final String HTTP_UPDATEUSERINFO = "updateUserInfo";
	public static final String HTTP_GETGROUPMEMBERS = "getGroupMembers";
	public static final String HTTP_GETHISTORYMESSAGE = "getHistoryMessage";
//	nearOrhotGroup
//	参数：Pager pager,String status（0附件，2热）,String longitude,String latitude
	public static final String HTTP_NEARORHOTGROUP = "nearOrhotGroup";
//	getGroupMembers
	public static final String HTTP_UPLOADCONTACT = "uploadContact";
	
	public static final String HTTP_INTOGROUP = "intoGroup";
	public static final String HTTP_UPLOADMESSAGEIMG = "uploadMessageImg";
	public static final String HTTP_GETHISTORYGROUPS = "getHistoryGroups";
	public static final String HTTP_GETGATLIST = "getGatList";
	public static final String HTTP_GETUSERINFO = "getUserInfo";
	public static final String HTTP_GETATTENTIONLIST = "getAttentionList";
	public static final String HTTP_ATTENTION = "attention";
	public static final String HTTP_JPUSHGROUP = "jpushGroup";
	public static final String HTTP_UPLOADMESSAGEARM = "uploadMessageArm";
	public static final String HTTP_BAIDU_LOCATION = "http://api.map.baidu.com/geocoder?output=json&location=";
	public static final String HTTP_REGISTER = "register";
	public static final String HTTP_APPLOGIN = "appLogin";
	public static final String HTTP_UPDATEPASSWORD = "updatePassword";
	public static final String HTTP_FORGETPASSWORD = "forgetPassword";
	public static final String HTTP_VALIDATEUSERNAME = "validateUserName";
	public static final String HTTP_ADDERRORLOG = "addErrorlog";
	public static final String HTTP_GETUSERINFOLIST = "getUserInfoList";
	public static final String HTTP_HOTSEARCHGROUP = "hotSearchGroup";
	
	
	
	
//	/** 清楚消息列表 */
//	public static final String HTTP_INDENTLIST = "indentList";
	
	/**================= 联网区分 ========================*/
	public static final int GETGROUPMEMBERS = 0; 
	public static final int REGISTER_VERIFCATION = 1;
	public static final int UPDATEUSERINFO = 3;
	public static final int SENDVERIFY = 4;
	public static final int VERIFYCODE = 5;
	public static final int UPLOADCONTACT = 6;
	public static final int HOMEPAGE = 7;
	public static final int SEARCHGROUP = 8;
	public static final int NEARORHOTGROUP = 9;
	public static final int GETHISTORYMESSAGE = 10;
	public static final int INTOGROUP = 11;
	public static final int UPLOADMESSAGEIMG = 12;
	public static final int GETHISTORYGROUPS = 13;
	public static final int GETGATLIST = 14;
	public static final int GETUSERINFO = 15;
	public static final int GETATTENTIONLIST = 16;
	public static final int ATTENTION = 17;
	public static final int JPUSHGROUP = 18;
	public static final int UPLOADMESSAGEARM = 19;
	public static final int BAIDU_LOCATION = 20;
	public static final int REGISTER = 21;
	public static final int APPLOGIN = 22;
	public static final int UPDATEPASSWORD = 23;
	public static final int FORGETPASSWORD = 24;
	public static final int VALIDATEUSERNAME = 25;
	public static final int ADDERRORLOG = 26;
	public static final int GETUSERINFOLIST = 27;
	public static final int HOTSEARCHGROUP = 28;
	
	
	
	
	
	// public static final int NEARORHOTGROUP = 10;

}
