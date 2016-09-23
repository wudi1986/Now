package com.yktx.group;

import java.lang.reflect.Field;
import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.EasyUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.yktx.chatuidemo.utils.CommonUtils;
import com.yktx.util.Contanst;

/**
 * ����activit���ṩ�˻����ļ�����ʾ��
 * 
 * @author wudi
 * 
 */
public class BaseActivity extends InstrumentedActivity {

	public interface LocationListenner {
		public void getLocatione(String longitude, String latitude,
				String address);

		public void fail();
	}

	public static float DENSITY;
	// public ImageLoader imageLoader = ImageLoader.getInstance();
	// public DisplayImageOptions options;
	// public static ImageLoader imageLoader = ImageLoader.getInstance();

	public DisplayImageOptions options;

	/** 进入编辑界面的缓存图片 */
	// public static Bitmap mbitmap;
	public Context mContext;
	private LocationClient locationClient = null;
	LocationListenner getLocation;
	public static int ScreenHeight;
	public static int ScreenWidth;
	public DisplayMetrics mDisplayMetrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mContext = this;
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		GroupApplication.getInstance().addActivity(this);
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

		DENSITY = mDisplayMetrics.density;
		Log.i(  "aaa", "DENSITY === " + DENSITY);
		ScreenHeight = mDisplayMetrics.heightPixels;
		ScreenWidth = mDisplayMetrics.widthPixels;

		// imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder().showImageOnLoading(null)
				.showImageForEmptyUri(null).showImageOnFail(null)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		SharedPreferences settings = BaseActivity.this.getBaseContext()
				.getSharedPreferences(
						(GroupApplication.getInstance()).getCurSP(), 0);

		String alias = settings.getString("alias", "");
		Log.i(  "aaa", "BaseActivity alias == " + alias);

		JPushInterface.setAliasAndTags(getApplicationContext(), alias, null,
				mAliasCallback);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
		option.setPriority(LocationClientOption.GpsFirst); // 设置定位优先级
		option.setScanSpan(10 * 60 * 1000);// 设置发起定位请求的间隔时间为5000ms
		option.setAddrType("all");
		option.setProdName("group"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		locationClient = new LocationClient(this);
		locationClient.setLocOption(option);

		locationClient.registerLocationListener(new BDLocationListener() {

			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub

				if (location == null) {
					getLocation.fail();
					return;
				}
				String latitude = location.getLatitude() + "";
				String longitude = location.getLongitude() + "";

				// mMKSearch.reverseGeocode(new GeoPoint((int) (location
				// .getLatitude() * 1e6),
				// (int) (location.getLongitude() * 1e6)));
				// http://api.map.baidu.com/geocoder?output=json&location=39.945541,116.440678&key=dpEPfKSsGavWFH7jpkzGmdau
				StringBuffer sb = new StringBuffer(256);
				// sb.append("Time : ");
				// sb.append(location.getTime());
				// sb.append("\nError code : ");
				// sb.append(location.getLocType());
				// sb.append("\nLatitude : ");
				// sb.append(location.getLatitude());
				// sb.append("\nLontitude : ");
				// sb.append(location.getLongitude());
				// sb.append("\nRadius : ");
				// sb.append(location.getRadius());
				// if (location.getLocType() == BDLocation.TypeGpsLocation) {
				// sb.append("\nSpeed : ");
				// sb.append(location.getSpeed());
				// sb.append("\nSatellite : ");
				// sb.append(location.getSatelliteNumber());
				// } else if (location.getLocType() ==
				// BDLocation.TypeNetWorkLocation) {
				// sb.append("\nAddress : ");
				// sb.append(location.getAddrStr());
				// sb.append(location.getProvince());
				sb.append(location.getCity());
				sb.append(".");
				sb.append(location.getDistrict());
				// }
				// // LOCATION_COUTNS ++;
				// sb.append("\n检查位置更新次数：");
				// // sb.append(String.valueOf(LOCATION_COUTNS));
				// // locationInfoTextView.setText(sb.toString());
				Log.i(  "aaa", "sb =====" + sb.toString());
				getLocation.getLocatione(longitude, latitude, sb.toString());
			}

			public void onReceivePoi(BDLocation location) {
			}
		});
		BaseActivity.this.getLocationClient(new LocationListenner() {
			@Override
			public void getLocatione(String longitude, String latitude,
					String address) {
				// TODO Auto-generated method stub
				Log.i(  "aaa", "longitude " + longitude);
				Log.i(  "aaa", "latitude " + latitude);
				SharedPreferences settings = BaseActivity.this.getBaseContext()
						.getSharedPreferences(Contanst.SystemStone, 0);
				Editor edit = settings.edit();
				if (longitude.indexOf("E") == -1) {
					edit.putString("longitude", longitude);
					edit.putString("latitude", latitude);
				} else {
					edit.putString("longitude", "-1");
					edit.putString("latitude", "-1");
				}
				edit.putString("address", address);
				edit.commit();

				// Intent intent_baonew = new Intent(BaseActivity.this,
				// AddShowPhotoService.class);
				// intent_baonew.putExtra("state",
				// AddShowPhotoService.RefreshLocation);
				// startService(intent_baonew);

			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub
				// Toast.makeText(BaseActivity.this, "获取位置失败",
				// Toast.LENGTH_SHORT).show();
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/** 获取通知栏高度 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = this.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;

	}

	/** 获取定位信息先启动locationClient */
	public void getLocationClient(LocationListenner getLocation) {
		this.getLocation = getLocation;
		if (locationClient == null) {
			return;
		}
		if (locationClient.isStarted()) {
			locationClient.stop();
		} else {
			locationClient.start();
			/*
			 * 当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。 调用requestLocation(
			 * )后，每隔设定的时间，定位SDK就会进行一次定位。 如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
			 * 返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
			 * 定时定位时，调用一次requestLocation，会定时监听到定位结果。
			 */
			locationClient.requestLocation();
		}

	}

	public final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {

			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(  "aaa", logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(  "aaa", logs);
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e("aaa", logs);
			}

		}

	};

//	private void registerEasemob(final String userName, Activity mContext) {
//		new Thread(new Runnable() {
//			public void run() {
//				try {
//					// 调用sdk注册方法
//					EMChatManager.getInstance().createAccountOnServer(userName,
//							"123");
//					runOnUiThread(new Runnable() {
//						public void run() {
//							// 保存用户名
//							// GroupApplication.getInstance().setUserName(userName);
//							// Toast.makeText(getApplicationContext(), st6,
//							// 0).show();
//							// finish();
//
//						}
//					});
//				} catch (final EaseMobException e) {
//					runOnUiThread(new Runnable() {
//						public void run() {
//							// int errorCode=e.getErrorCode();
//							// if(errorCode==EMError.NONETWORK_ERROR){
//							// Toast.makeText(getApplicationContext(), st7,
//							// Toast.LENGTH_SHORT).show();
//							// }else if(errorCode==EMError.USER_ALREADY_EXISTS){
//							// Toast.makeText(getApplicationContext(), st8,
//							// Toast.LENGTH_SHORT).show();
//							// }else if(errorCode==EMError.UNAUTHORIZED){
//							// Toast.makeText(getApplicationContext(), st9,
//							// Toast.LENGTH_SHORT).show();
//							// }else{
//							// Toast.makeText(getApplicationContext(), st10 +
//							// e.getMessage(), Toast.LENGTH_SHORT).show();
//							// }
//						}
//					});
//				}
//			}
//		}).start();
//	}

//	public void loginEasemob(String userName) {
//		// 调用sdk登陆方法登陆聊天服务器
//		EMChatManager.getInstance().login(userName, "123", new EMCallBack() {
//
//			@Override
//			public void onSuccess() {
//
//			}
//
//			@Override
//			public void onProgress(int progress, String status) {
//			}
//
//			@Override
//			public void onError(final int code, final String message) {
//
//				Log.i(  "aaa", "code ======== " + code
//						+ "       message ======== " + message + "    ");
//			}
//		});
//	}

	private static final int notifiId = 11;
	protected NotificationManager notificationManager;

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if (!EasyUtils.isAppRunningForeground(this)) {
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(getApplicationInfo().icon)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = CommonUtils.getMessageDigest(message, this);
		String st = getResources().getString(R.string.expression);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
		// 设置状态栏提示
		mBuilder.setTicker(message.getFrom() + ": " + ticker);

		// 必须设置pendingintent，否则在2.3的机器上会有bug
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId,
				intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notificationManager.notify(notifiId, notification);
		notificationManager.cancel(notifiId);
	}

	 /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }
	
	
	/** ������ʾ�Ի��� */
	public ProgressDialog mDialog;

	/**
	 * ��ʾ������ʾ��
	 * 
	 * @param message
	 *            Ҫ��ʾ����ʾ��������
	 */
	public void showProgressDialog(String message) {

		// mDialog = CustomProgressDialog.createDialog(this);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(message);
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
		mDialog.show();
	}

}
