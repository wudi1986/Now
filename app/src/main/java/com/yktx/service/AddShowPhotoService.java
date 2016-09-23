package com.yktx.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.yktx.group.GroupApplication;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.util.Contanst;
import com.yktx.util.NotificationUtil;
import com.yktx.util.PictureUtil;

public class AddShowPhotoService extends Service implements ServiceListener {
	/** 上传照片 */
	public static final int AddShowPhotoRun = 0;
	/** 上传联系人 */
	public static final int SendPhoneRun = 1;
	/** 注册信息上传 */
	public static final int RegisterInfo = 2;
	/** 发布商品图片上传 */
	public static final int ProductImageInfo = 3;
	/** 刷新地理位置 */
	public static final int RefreshLocation = 4;
	/** 环信登录*/
	public static final int HuanXinLogin = 5;
	public int ADDSTATE = 1;
	// DBHelper db;
	String msg;
	// String filename;
	String communityid;
	int budingid;
	int floorid;
	String HXuserName;
	public static Bitmap bitmap_temp;
	
	public void setstate(int state) {
		ADDSTATE = state;
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			ADDSTATE = intent.getIntExtra("state", 0);
			msg = intent.getStringExtra("msg");

			if (msg == null) {
				msg = "";
			}

		}
		// filename = intent.getStringExtra("filename");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				SharedPreferences settings = AddShowPhotoService.this
						.getBaseContext().getSharedPreferences(((GroupApplication)(AddShowPhotoService.this.getApplicationContext())).getCurSP(), 0);
				number = settings.getString("phone", "-1");
				Log.i(  "aaa", "number === "+number);
				SharedPreferences setting = AddShowPhotoService.this.getBaseContext()
						.getSharedPreferences(Contanst.SystemStone, 0);
				String longitude = setting.getString("longitude", "-1");
				String latitude = setting.getString("latitude", "-1");
				switch (ADDSTATE) {
				case AddShowPhotoRun:
					NotificationUtil.calltop(AddShowPhotoService.this,
							"照片发送中....");
					// communityid = intent.getStringExtra("communityid");
					String string = PictureUtil.save(bitmap_temp);
					bitmap_temp.recycle();
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					try {
						params.add(new BasicNameValuePair("phone", number));
						params.add(new BasicNameValuePair("image", string));
						params.add(new BasicNameValuePair("context", "1"));
						params.add(new BasicNameValuePair("longitude", longitude));
						params.add(new BasicNameValuePair("latitude", latitude));
					} catch (Exception e) {

					}
//					com.yktx.group.service.Service
//							.getService(Contanst.HTTP_UPLOADPATS, null, null,
//									AddShowPhotoService.this).addList(params)
//							.request(UrlParams.POST);

					break;
				case SendPhoneRun:
					List<NameValuePair> params1 = new ArrayList<NameValuePair>();
//					String str = getContact();
					ArrayList<String>  numList = getContact();
					Log.i(  "aaa", "str = " + getContact());
//					if(str.length() == 0)getContan2
//						return;
//					str = str.substring(0, str.length()-1);
//					
					
					try {
						params1.add(new BasicNameValuePair("phone", number));
						for(int i = 0; i < numList.size(); i++){
							params1.add(new BasicNameValuePair("phones", numList.get(i)));
						}
					} catch (Exception e) {

					}
					com.yktx.group.service.Service
							.getService(Contanst.HTTP_UPLOADCONTACT, null,
									null, AddShowPhotoService.this)
							.addList(params1).request(UrlParams.POST);
					break;
				case RegisterInfo:
//					String photoStr = PictureUtil.save(bitmap_temp);
//					bitmap_temp.recycle();
//
//					List<NameValuePair> params11 = new ArrayList<NameValuePair>();
//					try {
//						params11.add(new BasicNameValuePair("name",intent.getStringExtra("name")));
//						params11.add(new BasicNameValuePair("photo", photoStr));
//						params11.add(new BasicNameValuePair("phone", number));
//						int pregnancy = intent.getIntExtra("pregnancy",2);
//						params11.add(new BasicNameValuePair("pregnancy",pregnancy+""));
//						if(pregnancy == RegisterInfoActivity.STATE_BAOBAO){
//							params11.add(new BasicNameValuePair("babysex", intent.getStringExtra("babysex")));
//							params11.add(new BasicNameValuePair("babybirthday", intent.getStringExtra("babybirthday")));
//		                } else if (pregnancy == RegisterInfoActivity.STATE_HUAIYUN){
//							params11.add(new BasicNameValuePair("babybirthday", intent.getStringExtra("babybirthday")));
//		                }
//						params11.add(new BasicNameValuePair("longitude",
//								longitude));
//						params11.add(new BasicNameValuePair("latitude", latitude));
//					} catch (Exception e) {
//
//					}
//					com.yktx.group.service.Service.getService(Contanst.HTTP_ACHIEVEUSER, null, null,
//							AddShowPhotoService.this).addList(params11)
//							.request(UrlParams.POST);
					break;
					
				case ProductImageInfo:
					
					productid =  intent.getIntExtra("productid", 0)+"";
					list = intent.getStringArrayListExtra("list");
					pageSum = list.size();
					
					sendImageList("0", list.get(1));
					break;
					
				case RefreshLocation:		//刷新用户地理位置
					List<NameValuePair> params111 = new ArrayList<NameValuePair>();
					try {
						params111.add(new BasicNameValuePair("phone", number));
						params111.add(new BasicNameValuePair("lon",
								longitude));
						params111.add(new BasicNameValuePair("lat", latitude));
					} catch (Exception e) {

					}
//					com.yktx.group.service.Service.getService(Contanst.HTTP_REFRESHLOCATION, null, null,
//							AddShowPhotoService.this).addList(params111)
//							.request(UrlParams.POST);
					break;
				case HuanXinLogin:
					
					HXuserName = settings.getString("userID", "");
					Log.i(  "bbb", "HXuserName ====== "+HXuserName);
//					if (HXuserName.length() > 0) {
						// 环信登录聊天
						EMChatManager.getInstance().login(HXuserName, "123",
//							EMChatManager.getInstance().login("user2", "123",
								new EMCallBack() {
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Log.i(  "bbb","onSuccess" );
									
								}

								@Override
								public void onProgress(int progress, String status) {
									// TODO Auto-generated method stub

									Log.i(  "bbb","onProgress" );
								}

								@Override
								public void onError(int code, String message) {
									// TODO Auto-generated method stub
									Log.i(  "bbb","onError ==================== "+message );
									Log.i(  "bbb","code ==================== "+code );
//									if(message){
									Message msg = new Message();
									msg.what = 100;
									mHandler.sendMessage(msg);
									
									}
									
//								}
							});

//					}
					break;
					
				default:
					break;
				}
			}
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	
	
	String number;
	String productid;
	int curPage = 1;
	int pageSum ;
	ArrayList<String> list = new ArrayList<String>(7);

	private void sendImageList(final String isindex, String image){
		NotificationUtil.calltop(AddShowPhotoService.this,
				"照片发送中....");
//		ImageLoader imageLoader = ImageLoader.getInstance();
		Log.i(  "bbb", "image ========= "+image);
		
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.snake.conn.ServiceListener#getJOSNdataSuccess(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
		Log.i(  "aaa", "getJOSNdataSuccessgetJOSNdataSuccess");
		Message msg = new Message();
		msg.what = Contanst.BEST_INFO_OK;
		msg.obj = sccmsg;
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
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				if (msg.arg1 == Contanst.SENDVERIFY) {
					Toast.makeText(AddShowPhotoService.this, "发送成功!",
							Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == Contanst.VERIFYCODE) {
					Toast.makeText(AddShowPhotoService.this, "发送成功!",
							Toast.LENGTH_LONG).show();
					
				}
				break;
			case Contanst.BEST_INFO_FAIL:
				if (msg.arg1 == Contanst.SENDVERIFY) {
					Toast.makeText(AddShowPhotoService.this, "断网了",
							Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == Contanst.VERIFYCODE) {
					
				}
				break;
				
			case 100:
				Log.i(  "bbb", "注册");
				final String appkey = EMChatConfig.getInstance().APPKEY;
				Log.i(  "bbb", "appkey ===== "+appkey);
				new Thread(new Runnable() {
					public void run() {
						try {
							//调用sdk注册方法
							EMChatManager.getInstance().createAccountOnServer(appkey + "_" + HXuserName, "123");
							
						} catch (final Exception e) {
							if ( e != null && e.getMessage() != null)
							{
								String errorMsg=e.getMessage();
								if (errorMsg.indexOf("EMNetworkUnconnectedException")!=-1)
								{
									Log.i(  "bbb", "网络异常，请检查网络！");
								} else if (errorMsg.indexOf("conflict")!=-1)
								{
//									Toast.makeText(getApplicationContext(), "用户已存在！", 0).show();
									Log.i(  "bbb", "用户已存在！");
								} else {
//									Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), 1).show();
									Log.i(  "bbb", "注册失败: "+ e.getMessage());
								}
								
							} else {
								Log.i(  "bbb", "注册失败: 未知异常");
							}
						}
					}
				}).start();
				break;
				
			}
		}

	};

	public ArrayList<String> getContact() {

		ArrayList<String> list = new ArrayList<String>();
		// 取得ContentResolver
		ContentResolver content = getContentResolver();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;// 联系人的URI
//		Cursor cursor = content.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		Cursor cursor = content.query(uri, null, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		// int contactCount = cursor.getCount(); // 获得联系人数目
		if (cursor.moveToFirst()) {
			// 循环遍历
			for (; !cursor.isAfterLast(); cursor.moveToNext()) {
				int idColumn = cursor
						.getColumnIndex(ContactsContract.Contacts._ID);
				int displayNameColumn = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				int phoneColumn = cursor
						.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

				// 获得联系人的ID号
				String contactId = cursor.getString(idColumn);
				// 获得联系人姓名
				String disPlayName = cursor.getString(displayNameColumn);

				// 电话号码的个数
				// String phoneString = cursor.getString(phoneColumn);
				int phoneNum = cursor.getInt(phoneColumn);
				if (phoneNum > 0) {

					// 获得联系人的电话号码的cursor;
					Cursor phones = content.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					
					
					
					
					int phoneCount = phones.getCount();
//					Log.i(  "bbb", "phoneCount === "+phoneCount);
					if (phones.moveToFirst()) {
						// 遍历所有的电话号码
						for (; !phones.isAfterLast(); phones.moveToNext()) {
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

//							Log.i(  "bbb", "phoneNumber === "+phoneNumber);
							phoneNumber = phoneNumber.replace("+86", "");
							phoneNumber = phoneNumber.replace("-", "");
							phoneNumber = phoneNumber.replace(" ", "");
//							if (phoneNumber.length() != 11)
//								continue;
							StringBuffer sb = new StringBuffer();
							sb.append(phoneNumber + "---" + disPlayName);
							list.add(sb.toString());
						}
						if (!phones.isClosed()) {
							phones.close();
						}
					}
				}
			}
			if (!cursor.isClosed()) {
				cursor.close();
			}
		}
		return list;
	}

	private ArrayList<String> getContan2(){
        //得到ContentResolver对象
		ArrayList<String> list = new ArrayList<String>();
        ContentResolver cr = getContentResolver();

        //取得电话本中开始一项的光标

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        //向下移动光标

        while(cursor.moveToNext())

        {

            //取得联系人名字

            int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);

            String contact = cursor.getString(nameFieldColumnIndex);

           //取得电话号码

            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));   
            
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

            while(phone.moveToNext())

            {

                String Number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
                Number = Number.replace("+86", "");
                Number = Number.replace("-", "");
//				if (Number.length() != 11)
//					continue;
				StringBuffer sb = new StringBuffer();
				sb.append(Number + "---" + Number);
				list.add(sb.toString());

            }

        }

        cursor.close();
        
        return list;
	}
	/** �ֻ����Ϣ�� */
	public class PhoneCallInfo {
		public Integer _id;
		public String number;
		public Long date;
		public Integer duration;
		public Integer type;
		public String name;
	}

	
	public List<PhoneCallInfo> getPhoneCallInfos() {
		List<PhoneCallInfo> phoneCallInfos = new ArrayList<PhoneCallInfo>();
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,null,null,null,null);
			if(cursor != null){
				while (cursor.moveToNext()) {
					PhoneCallInfo pci = new PhoneCallInfo();
					pci._id = cursor.getInt(cursor.getColumnIndex("_id"));
					pci.number = cursor.getString(cursor.getColumnIndex("number"));
					pci.date = cursor.getLong(cursor.getColumnIndex("date"));
					pci.duration = cursor.getInt(cursor.getColumnIndex("duration"));
					pci.type = cursor.getInt(cursor.getColumnIndex("type"));
					pci.name = cursor.getString(cursor.getColumnIndex("name"));
					phoneCallInfos.add(pci);
					Log.i(  "aaa", "pci.number === "+pci.number);
				}
				return phoneCallInfos;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	
	// @Override
	// public void onHttpResult(int id, Object obj) {
	// // TODO Auto-generated method stub
	//
	// if (id == 0) {
	// AddShowPhotoParser parser = (AddShowPhotoParser) obj;
	// if (parser == null) {
	// NotificationUtil.calltop(this, "抱歉!网络断开");
	// // failedaddShowPhoto();
	// return;
	// }
	// if (parser.getStatusCode() == 1) {
	// MobclickAgent.onEvent(this, "showsucceed");
	// NotificationUtil.calltop(this, "发送成功!");
	// MobclickAgent.onEvent(this, "sendimageok");
	// if(db==null){
	// db=new DBHelper(this);
	// }
	// // db.delfailed(FailedMessge.TYPE_AddShowPhoto, filename);
	//
	//
	// } else {
	// NotificationUtil.calltop(this, "发送失败!");
	// // failedaddShowPhoto();
	// }
	// } else if (id == 1) {
	// AddPhotoFloorParser parser = (AddPhotoFloorParser) obj;
	// if (parser == null) {
	// NotificationUtil.calltop(this, "抱歉!网络断开");
	// // failedaddPhootoFloor();
	// return;
	// }
	// if (parser.getStatusCode() == 1) {
	// MobclickAgent.onEvent(this, "backshowsucceed");
	// NotificationUtil.calltop(this, "发送成功!");
	// PwdDao.getInstance().setisupdate(true);
	// MobclickAgent.onEvent(this, "sendimageok");
	// if(db==null){
	// db=new DBHelper(this);
	// }
	// // db.delfailed(FailedMessge.TYPE_AddPhotoFloor, filename);
	// } else {
	// NotificationUtil.calltop(this, "发送失败!");
	// // failedaddPhootoFloor();
	// }
	// } else if (id == 2) {
	// AddSecretParser parser = (AddSecretParser) obj;
	// if (parser == null) {
	// NotificationUtil.calltop(this, "抱歉!网络断开");
	// failedaddsecret();
	// return;
	// }
	// if (parser.getStatusCode() == 1) {
	// NotificationUtil.calltop(this, "发送成功!");
	// MobclickAgent.onEvent(this, "sendimageok");
	// if(db==null){
	// db=new DBHelper(this);
	// }
	// // db.delfailed(FailedMessge.TYPE_AddSecretRun, filename);
	//
	// } else {
	// NotificationUtil.calltop(this, "发送失败!");
	// failedaddsecret();
	// }
	// }
	// if(bitmap_temp!=null)
	// stopSelf();
	// }
	/**
	 * AddShowPhoto失败
	 */
	// public void failedaddShowPhoto() {
	//
	// ContentValues values=new ContentValues();
	// values.put(FailedMessge.TYPE, FailedMessge.TYPE_AddShowPhoto);
	// values.put(FailedMessge.MSG,msg);
	// values.put(FailedMessge.COMMNNITYID,communityid);
	// // values.put(FailedMessge.FILENAME, filename);
	// if (db == null) {
	// db = new DBHelper(this);
	// }
	// db.addfailed(values);
	// }
	/**
	 * AddPhotoFloor失败
	 */
	// public void failedaddPhootoFloor() {
	// if (db == null) {
	// db = new DBHelper(this);
	// }
	// ContentValues values=new ContentValues();
	// values.put(FailedMessge.TYPE, FailedMessge.TYPE_AddPhotoFloor);
	// values.put(FailedMessge.MSG,msg);
	// // values.put(FailedMessge.FILENAME, filename);
	// values.put(FailedMessge.FLOORID, floorid);
	// values.put(FailedMessge.BUDINGID, budingid);
	// values.put(FailedMessge.USERID, userid);
	// db.addfailed(values);
	// }

	/**
	 * AddSecretRun失败
	 */
	// public void failedaddsecret() {
	// if (db == null) {
	// db = new DBHelper(this);
	// }
	// ContentValues values=new ContentValues();
	// values.put(FailedMessge.TYPE, FailedMessge.TYPE_AddSecretRun);
	// values.put(FailedMessge.MSG,msg);
	// // values.put(FailedMessge.FILENAME, filename);
	// db.addfailed(values);
	// }

}
