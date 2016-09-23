package com.yktx.service;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.yktx.group.CameraActivity;
import com.yktx.group.GroupApplication;
import com.yktx.group.conn.ServiceListener;
import com.yktx.util.Contanst;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver implements ServiceListener {
	private static final String TAG = "MyReceiver";
	private HashMap<String, Integer> hashmap;
	private int statusCode = 0;

	String getTopActivity(Activity context) {
		if (context != null) {
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Activity.ACTIVITY_SERVICE);
			List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

			if (runningTaskInfos != null)
				return (runningTaskInfos.get(0).topActivity).toString();
			else
				return null;
		} else
			return null;
	}

	// static boolean istalk;
	Context context;
	int type = 0;
	String productid;
	String applyid;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		hashmap = new HashMap<String, Integer>();
		String group_id = "0";
		Bundle bundle = intent.getExtras();
		Log.i(  TAG, "onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));
		String string = bundle.getString("cn.jpush.android.EXTRA");

		// extras: {"type":1,"senduser":"黄飞鸿","sendnum":1}

		int userid = 0;
		JSONObject result;
		String patid = null;
		try {

			// 01-12 11:39:27.553: I/aaa(10765): printBundle(bundle) ===
			// {"type":2,"group_id":"111516"}
			Log.i(  "aaa", "printBundle(bundle) === "
					+ printBundle(bundle));
			result = new JSONObject(printBundle(bundle));
			if (result.has("type"))
				type = result.getInt("type");
			if (type == 1) {
				patid = result.getString("patid");
				// ArrayList<PatListBean> list = new ArrayList<PatListBean>(4);
				// isHaveName(list, name, num);
				String PackageName = getAppPackageName(context);
				Log.i(  "aaa", "PackageName == " + PackageName);
				boolean IsRun = isServiceStarted(context, PackageName);
//				System.out.println("PackageName:IsRun -> " + PackageName
//						+ "  - >  " + IsRun);
				// if (IsRun) {
				// Intent i = new Intent(context, QhtMainActivity.class);
				// // // 删除其他的activity
				// // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// i.putExtra("bundle", "refresh");
				// context.startActivity(i);
				// // QhtMainActivity.activity.overridePendingTransition(
				// // R.anim.activity_come_in, android.R.anim.fade_out);
				//
				// Bundle bundle1 = intent.getExtras();
				// int notificationId = bundle1
				// .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				// JPushInterface.clearNotificationById(context,
				// notificationId);
				// }
			} else if (type == 2) {
				group_id = result.getString("group_id");
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// try {
		// JSONObject jsonObject = new JSONObject(string);
		// statusCode=jsonObject.getInt("statusCode");
		// hashmap.put("floorid",jsonObject.getInt("floorid"));
		// hashmap.put("budingid",jsonObject.getInt("budingid"));
		// Log.i(  TAG,jsonObject.toString());
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.i(  TAG, "接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.i(  TAG, "接收UnRegistration Id : " + regId);
			// send the UnRegistration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.i(  TAG,
					"接收到推送下来的自定义消息: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.i(  TAG, "接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.i(  TAG, "接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.i(  TAG, "用户点击打开了通知");

			SharedPreferences settings = GroupApplication.getInstance()
					.getSharedPreferences(Contanst.SystemStone, 0);
			if (settings.getBoolean("isAutoLogin", false)) {
				((GroupApplication)(context.getApplicationContext())).setCurSP(Contanst.UserStone);
			} else {
				((GroupApplication)(context.getApplicationContext())).setCurSP(Contanst.TouristStone);
			}
			if (type == 2) {
				opencamera(group_id);
			}
			// 判断当前软件是否正在运行
			// String PackageName = getAppPackageName(context);
			// Log.i(  "aaa", "PackageName == " + PackageName);
			// boolean IsRun = isServiceStarted(context, PackageName);
			// System.out.println("PackageName:IsRun -> " + PackageName
			// + "  - >  " + IsRun);

			String PackageName = getAppPackageName(context);
			Log.i(  "aaa", "PackageName == " + PackageName);
			boolean IsRun = isServiceStarted(context, PackageName);
//			System.out.println("PackageName:IsRun -> " + PackageName
//					+ "  - >  " + IsRun);
			// if (IsRun) {
			// Intent i = new Intent(context, PushLookActivity.class);
			// i.putExtra("bundle", "refresh");
			// i.putExtra("patid", patid);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(i);
			// // QhtMainActivity.activity.overridePendingTransition(
			// // R.anim.activity_come_in, android.R.anim.fade_out);
			//
			// Bundle bundle1 = intent.getExtras();
			// int notificationId = bundle1
			// .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			// JPushInterface.clearNotificationById(context, notificationId);
			// } else {
			// GroupMainActivity.isPush = true;
			// Intent i = new Intent(context, GroupMainActivity.class);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// i.putExtra("patid", patid);
			// context.startActivity(i);
			// }

			// Log.i(  "aaa", "istalk == " + istalk);
			// if (istalk) {
			// Intent in = new Intent(context, CallActivity.class);
			// in.putExtra("state", 0);
			// in.putExtra("userID", userid + "");
			// in.putExtra("productID", productid);
			// in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(in);
			// } else {
			// /

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.i(  TAG,
					"用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else {
			Log.i(  TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append(bundle.getInt(key));

			} else {
				sb.append(bundle.getString(key));
			}
		}

		String str = sb.toString();
		return str.substring(str.indexOf("{"), str.indexOf("}") + 1);
	}

	// private void isHaveName(ArrayList<PatListBean> list, String name, int
	// num) {
	// boolean flag = false;
	// for (int i = 0; i < list.size(); i++) {
	// PatListBean bean = list.get(i);
	// }
	// Log.i(  "aaa", "flag ===== " + flag);
	// }

	// android 检测一个android程序是否在运行.
	public static boolean isServiceStarted(Context context, String PackageName) {
		boolean isStarted = false;
		try {

			// ActivityManager mActivityManager = (ActivityManager)
			// context.getSystemService(Context.ACTIVITY_SERVICE);
			// List<RunningAppProcessInfo> run =
			// mActivityManager.getRunningAppProcesses();
			// for(ActivityManager.RunningAppProcessInfo pro: run){
			// if(pro.processName.equals(PackageName)){
			// isStarted = true;
			// break;
			// }
			// }

			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> run = mActivityManager.getRunningTasks(1);
			if (run.size() > 0) {
				if (PackageName.equals(run.get(0).topActivity.getPackageName())) {
					isStarted = true;
				}
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return isStarted;
	}

	// 获取程序自身包名
	public static String getAppPackageName(Context context) {
		try {
			String pkName = context.getPackageName();
			return pkName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Intent msgIntent = new Intent(
				"com.example.jpushdemo.MESSAGE_RECEIVED_ACTION");
		msgIntent.putExtra("message", message);
		if (extras != null) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (null != extraJson && extraJson.length() > 0) {
					msgIntent.putExtra("extras", extras);
				}
			} catch (JSONException e) {

			}
		}
		context.sendBroadcast(msgIntent);
	}

	/* 打开相机 */
	public void opencamera(String group_id) {
		SharedPreferences setting = GroupApplication.getInstance()
				.getSharedPreferences(Contanst.SystemStone, 0);
		String longitude = setting.getString("longitude", "-1");
		String latitude = setting.getString("latitude", "-1");

		Intent cameraIntent = new Intent(context, CameraActivity.class);
		cameraIntent.putExtra(CameraActivity.IsRegister, "0");
		cameraIntent.putExtra(CameraActivity.IsIntoGroup, true);
		cameraIntent.putExtra("longitude", longitude);
		cameraIntent.putExtra("latitude", latitude);
		cameraIntent.putExtra("group_id", group_id);
		cameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(cameraIntent);
	}

	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = bean;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataFail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataFail(String errcode, String errmsg, int connType) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_FAIL;
		msg.obj = errmsg;
		msg.arg1 = connType;
		mHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				// Intent i = new Intent(context, GroupMainActivity.class);
				// // 删除其他的activity
				// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// i.putExtra("push", true);
				// context.startActivity(i);
				// GroupMainActivity.mContext.overridePendingTransition(
				// R.anim.activity_come_in, android.R.anim.fade_out);

				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

}
