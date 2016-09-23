package com.yktx.group;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chatuidemo.domain.User;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yktx.util.CrashHandler;
import com.yktx.util.FileURl;

/**
 * Created by Administrator on 2014/4/11.
 */
public class GroupApplication extends Application {
	public String TAG = "TAG";
	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;
	public Vibrator mVibrator01;
	private static GroupApplication qhtApplication;
	public static Context applicationContext;

	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	public static GroupApplication getInstance() {
		return qhtApplication;
	}

	/**
	 * 如果没有登录使用游客记录，如果记录使用用户记录
	 */
	public String curSP;

	public String getCurSP() {
		return curSP;
	}

	public void setCurSP(String curSP) {
		this.curSP = curSP;
	}

	@Override
	public void onCreate() {
		// 获取异常信息
		 CrashHandler crashHandler = CrashHandler.getInstance();
		 crashHandler.init(getApplicationContext());
		qhtApplication = this;
		applicationContext = this;
		aboutimageloder();
		aboutjpush();
		aboutbaidu();
		
		/**
		 * debugMode == true 时为打开，sdk 会在log里输入调试信息
		 * 
		 * @param debugMode
		 *            在做代码混淆的时候需要设置成false
		 */
//		EMChat.getInstance().setDebugMode(false);// 在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无
		EMChat.getInstance().setAutoLogin(false);
		hxSDKHelper.onInit(applicationContext);
		super.onCreate();
	}

	private void aboutimageloder() {

		File goodsiamge = new File(FileURl.GoodsIamgeURL);
		if (!goodsiamge.exists()) {
			goodsiamge.mkdirs();
		}
		File cacheDir = StorageUtils.getOwnCacheDirectory(this,
				"yingtao/imgcache");
		// DisplayImageOptions options = new DisplayImageOptions.Builder()
		// .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				// // .memoryCacheExtraOptions(480, 800)
				// // // default = device screen dimensions
				// .threadPoolSize(3)
				// // default
				// .threadPriority(Thread.NORM_PRIORITY - 2)
				// // default
				// .tasksProcessingOrder(QueueProcessingType.LIFO)
				// // default
				// .denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new WeakMemoryCache())
				// .memoryCacheSize(7 * 1024 *
				// 1024).memoryCacheSizePercentage(13)
				// // default
				// // default
				// // .imageDownloader(new BaseImageDownloader(context)) //
				// default
				// // .imageDecoder(new BaseImageDecoder()) // default
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// // default
				// // .writeDebugLogs()
				// .build();
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	private void aboutjpush() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

	private void aboutbaidu() {
		mLocationClient = new LocationClient(this);

		/**
		 * ——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */

		// mLocationClient.setAK("QgD8qt5wmp5XzGLWb6b4DXUm");
		mLocationClient.registerLocationListener(myListener);
		mGeofenceClient = new GeofenceClient(this);
	}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			Log.i(  "BAIDULOCA", String.valueOf(location.getLatitude()) + ":"
					+ String.valueOf(location.getLongitude()));
			// rqusetloca();
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				// sb.append("\nspeed : ");
				// sb.append(location.getSpeed());
				// sb.append("\nsatellite : ");
				// sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				/**
				 * 格式化显示地址信息
				 */
				if (location.getProvince().equals(location.getCity())) {

				} else {
					// PwdDao.getInstance().setlocation(
					// location.getProvince() + ":" + location.getCity());
				}
				// PwdDao.getInstance().setmorelocation(location.getAddrStr());//
				// 详细地址
				Log.i(  TAG, "省:" + location.getProvince() + "" + " 市："
						+ location.getCity() + " 详细地址：" + location.getAddrStr());

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}

	/**
	 * Activity列表
	 */
	public List<Activity> activityList = new LinkedList<Activity>();

	/**
	 * 保存Activity到现有列表中
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 关闭保存的Activity
	 */
	public void exit() {
		if (activityList != null) {
			for (Activity activity : activityList) {
				activity.finish();
			}
			System.exit(0);
		}

	}

	public void backHomeActivity() {
		if (activityList != null) {
			for (int i = 1; i < activityList.size(); i++) {
				activityList.get(i).finish();
			}
		}
	}

	/** 软键盘的打开 */
	public static void openKeybord(EditText mEditText, Context mContext) {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/** 软键盘的关闭 */
	public static void closeKeybord(EditText mEditText, Context mContext) {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
	
	///====================环信===============
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(emCallBack);
	}
}
