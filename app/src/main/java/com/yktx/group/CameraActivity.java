package com.yktx.group;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.yanzi.util.CamParaUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gallety.PhotoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.yktx.bean.GroupListBean;
import com.yktx.group.conn.ServiceListener;
import com.yktx.group.conn.UrlParams;
import com.yktx.group.service.Service;
import com.yktx.sqlite.DBHelper;
import com.yktx.util.Contanst;
import com.yktx.util.FileURl;
import com.yktx.util.FlashLightManager;
import com.yktx.util.ImageTool;

/**
 * 发布商品 Created by Administrator on 2014/4/9.
 */
@SuppressLint("NewApi")
public class CameraActivity extends BaseActivity implements
		TextureView.SurfaceTextureListener, ServiceListener {

	UUID uuid;
	private Intent getedintent;
	String filename = "";// 拍照获得的图片路径名字
	private ImageView paizhao;
	private Camera mCamera;
	private Camera.Parameters mParams;
	private float mPreviwRate = 1.33f;
	TextureView mTextureView;
	/** 相机取景器 */
	FrameLayout cameraFramlayout;
	RelativeLayout cameraBg;
	Bitmap photoBitmap;
	FrameLayout cameraFramlayoutResult;
	/** 拍照結果 */
	ImageView cameraResult;
	TextView cameraPhotos; // 相册
	public static String filepath = "";// 照片保存路径

	/** 拍照确认 */
	TextView camera_confrim;

	boolean isCanPai = true;
	ImageView cameraTurn;
	TextView camera_back;
	TextView camera_finish;
	TextView camera_return;

	int cameraPosition = 0;

	boolean flashLightState;
	/** 闪光灯状态描述 */
	TextView flashInfo;
	LinearLayout flashLightButton;
	boolean setProduct;
	public static String curClipPhoto;
	DBHelper dbHelper;

	/**
	 * 是否跳过
	 */
	boolean isJump = false;
	boolean isFirst = false;
	/** 取景器距屏幕上方距离 */
	public int offX;

	TextView camera_now_what;
	public static final String IsRegister = "IsRegister";
	public static final String IsIntoGroup = "IsIntoGroup";
	public static final String IsFile = "IsFile";
	private String isRegister;
	boolean isIntoGroupConn = false;
	/** 是否进群拍照 */
	private boolean isIntoGroup;
	
	/** 返回链接 */
	private boolean isFile;

	ArrayList<GroupListBean> historyList = new ArrayList<GroupListBean>(10);
	String longitude, latitude, group_id, group_name, group_distance,
			group_peopleCount;
	/** 搜索进来的 */
	GroupListBean searchGroupBean;
	String atMyNum;
	String userStatus, userID;

	// boolean isSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = CameraActivity.this.getSharedPreferences(
				(GroupApplication.getInstance()).getCurSP(), 0);
		userStatus = settings.getString("userStatus", "0");
		userID = settings.getString("userID", userID);
		isIntoGroupConn = false;

		SharedPreferences setting = CameraActivity.this.getSharedPreferences(
				Contanst.SystemStone, 0);
		longitude = setting.getString("longitude", "-1");
		latitude = setting.getString("latitude", "-1");
		isFirst = false;
		isJump = false;
		setContentView(R.layout.camera_layout);
		dbHelper = new DBHelper(this);
		historyList = dbHelper.getMainHistoryList(historyList);
		offX = (int) (84 * BaseActivity.DENSITY);
		setProduct = getIntent().getBooleanExtra("setProduct", false);
		if (setProduct) {

		} else {
			uuid = UUID.randomUUID();
		}
		isRegister = getIntent().getStringExtra(IsRegister);
		Log.i(  "aaa",
				"isRegisterisRegisterisRegister===========" + isRegister);
		isIntoGroup = getIntent().getBooleanExtra(IsIntoGroup, false);
		isFile = getIntent().getBooleanExtra(IsFile, false);
		
		if (isIntoGroup) {
			group_id = getIntent().getStringExtra("group_id");
			group_name = getIntent().getStringExtra("group_name");
			group_distance = getIntent().getStringExtra("group_distance");
			group_peopleCount = getIntent().getStringExtra("group_peopleCount");
			searchGroupBean = (GroupListBean) getIntent().getCharSequenceExtra(
					"insertGroupBean");
			atMyNum = getIntent().getStringExtra("atMyNum");
		}
		initview();
		initdata();
		initCameraLayout();

	}

	/** 初始化相机布局 */
	private void initCameraLayout() {
		cameraFramlayout = (FrameLayout) findViewById(R.id.cameraFrameLayout);
		cameraBg = (RelativeLayout) findViewById(R.id.cameraBg);
		final RelativeLayout.LayoutParams cameraParams = new RelativeLayout.LayoutParams(
				ScreenWidth, (int) (ScreenWidth * mPreviwRate));
		cameraParams.setMargins(0, offX, 0, 0);
		cameraFramlayout.setLayoutParams(cameraParams);

		cameraFramlayoutResult = (FrameLayout) findViewById(R.id.cameraResult);
		final RelativeLayout.LayoutParams resultParams = new RelativeLayout.LayoutParams(
				ScreenWidth, ScreenWidth);
		resultParams.setMargins(0, offX, 0, 0);
		cameraFramlayoutResult.setLayoutParams(resultParams);

		// 取景器长方形，要变成正方形把多余的地方遮挡
		final RelativeLayout.LayoutParams bgParams = new RelativeLayout.LayoutParams(
				ScreenWidth, (int) (ScreenWidth * mPreviwRate));
		bgParams.setMargins(0, offX + ScreenWidth, 0, 0);
		cameraBg.setLayoutParams(bgParams);

		mTextureView = new TextureView(this);
		mTextureView.setSurfaceTextureListener(this);
		mTextureView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCamera.autoFocus(new AutoFocusCallback() {

					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						// TODO Auto-generated method stub
						// success为true表示对焦成功

					}
				});
			}
		});

		initCamera(mPreviwRate);
		cameraFramlayout.addView(mTextureView);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	private void initCamera(float previewRate) {
		if (mCamera != null) {

			mParams = mCamera.getParameters();

			mParams.setPictureFormat(PixelFormat.JPEG);// �������պ�洢��ͼƬ��ʽ
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(), previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);
			Log.i(  "aaa", "previewSize.width == "
					+ previewSize.width);
			Log.i(  "aaa", "previewSize.height == "
					+ previewSize.height);
			mCamera.setDisplayOrientation(90);

			Log.i(  "aaa", "base ScreenWidth ======== "
					+ BaseActivity.ScreenWidth);
			Log.i(  "aaa", "base ScreenHeight ======== "
					+ BaseActivity.ScreenHeight);
			Log.i(  "aaa", "base DENSITY ======== "
					+ BaseActivity.DENSITY);
			if (BaseActivity.ScreenHeight > 2000) {

			} else {
				try {
					mCamera.setParameters(mParams);
				} catch (Exception e) {

				}
			}
			mCamera.startPreview();// ����Ԥ��
			mParams = mCamera.getParameters(); // ����getһ��
			Log.i(  "aaa",
					"��������:PreviewSize--With = "
							+ mParams.getPreviewSize().width + "Height = "
							+ mParams.getPreviewSize().height);
			Log.i(  "aaa",
					"��������:PictureSize--With = "
							+ mParams.getPictureSize().width + "Height = "
							+ mParams.getPictureSize().height);
		}
	}

	public static boolean isFinish;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isFinish) {
			CameraActivity.this.finish();
			isFinish = false;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			Intent intentdata = new Intent();
			setResult(222, intentdata);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		uuid = null;
		getedintent = null;
		filename = null;// 拍照获得的图片路径名字
		paizhao = null;
		mCamera = null;
		mParams = null;
		mTextureView = null;
		if (photoBitmap != null) {
			photoBitmap.recycle();
			photoBitmap = null;
		}
		cameraResult = null;
		cameraPhotos = null;
		cameraTurn = null;
		flashLightButton = null;
		curClipPhoto = null;
		System.gc();
	}

	private void initview() {
		cameraTurn = (ImageView) findViewById(R.id.cameraTurn);
		camera_back = (TextView) findViewById(R.id.camera_back);
		camera_finish = (TextView) findViewById(R.id.camera_finish);
		camera_return = (TextView) findViewById(R.id.camera_return);
		flashLightButton = (LinearLayout) findViewById(R.id.flashLightButton);
		flashInfo = (TextView) findViewById(R.id.flashInfo);
		paizhao = (ImageView) findViewById(R.id.camera_paizhao);
		cameraPhotos = (TextView) findViewById(R.id.camera_photos);
		cameraResult = (ImageView) findViewById(R.id.cameraPhoto);
		camera_confrim = (TextView) findViewById(R.id.camera_confrim);
		camera_now_what = (TextView) findViewById(R.id.camera_now_what);
		/**
		 * 根据是否从注册页面跳转过来的标示判断
		 */
		if ("0".equals(isRegister)) {
			camera_now_what.setVisibility(View.VISIBLE);
			cameraPhotos.setVisibility(View.GONE);
//			camera_return.setVisibility(View.VISIBLE);
			camera_return.setVisibility(View.GONE);
		} else if ("2".equals(isRegister)) {
			camera_now_what.setVisibility(View.VISIBLE);
			cameraPhotos.setVisibility(View.GONE);
			camera_return.setVisibility(View.GONE);
		}
	}

	private void initdata() {
		camera_confrim.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isIntoGroupConn)
					return;
				if (isIntoGroup) {
					intoGroup(group_id, group_name);
					isFirst = true;
					isJump = false;
					
				}else if(isFile){
					Intent intent = new Intent();
					intent.putExtra("filePath", filepath);
					setResult(222, intent);
					CameraActivity.this.finish();
				} else {
					Intent intent = new Intent();
					setResult(111, intent);
					CameraActivity.this.finish();
				}
			}
		});

		camera_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// MobclickAgent.onEvent(CameraActivity.this,
				// "twobitcode_success");
				// TODO Auto-generated method stub
				cameraResult.setImageResource(R.drawable.toumingimg);
				cameraTurn.setVisibility(View.VISIBLE);
				flashLightButton.setVisibility(View.VISIBLE);
				cameraPhotos.setVisibility(View.VISIBLE);
				paizhao.setVisibility(View.VISIBLE);
				Log.i(  "aaa", "isRegister ============ "
						+ isRegister);
				if ("0".equals(isRegister)) {
					camera_now_what.setVisibility(View.VISIBLE);
					cameraPhotos.setVisibility(View.GONE);
					if (userStatus.equals("1")) {

//						camera_return.setVisibility(View.VISIBLE);
						camera_return.setVisibility(View.GONE);
					}
				} else if ("2".equals(isRegister)) {
					camera_now_what.setVisibility(View.VISIBLE);
					cameraPhotos.setVisibility(View.GONE);
					camera_return.setVisibility(View.GONE);
				}
				camera_confrim.setVisibility(View.GONE);
				camera_back.setVisibility(View.GONE);
				camera_finish.setVisibility(View.VISIBLE);
			}
		});
		camera_finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent();
				setResult(222, in);
				finish();
			}
		});

		camera_return.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isIntoGroupConn) {
					return;
				}
				intoGroup(group_id, group_name);
				isJump = true;
			}
		});

		flashLightButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// showdialogexit();
				if (flashLightState) {
					// 关闭
					FlashLightManager.turnLightOff(mCamera);
					flashLightState = false;
					flashInfo.setText("关闭");
				} else {

					// 小米3切换闪光灯开启时会黑屏，重启相机即可
					if (mCamera != null) {
						mCamera.stopPreview();// 停掉原来摄像头的预览
						mCamera.release();// 释放资源
						mCamera = null;// 取消原来摄像头
					}
					cameraFramlayout.removeAllViews();
					mTextureView = null;
					mTextureView = new TextureView(CameraActivity.this);
					mTextureView.setSurfaceTextureListener(CameraActivity.this);
					cameraFramlayout.addView(mTextureView);
					mTextureView.setVisibility(View.VISIBLE);
					// 开启
					FlashLightManager.turnLightOn(mCamera);
					flashLightState = true;
					flashInfo.setText("");
				}

			}
		});

		cameraTurn.setOnClickListener(new View.OnClickListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(  "aaa",
						"cameraTurncameraTurncameraTurncameraTurncameraTurncameraTurn");
				frontOrBack();
			}
		});

		getedintent = getIntent();
		paizhao.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 快门
				mCamera.autoFocus(new AutoFocusCallback() {// 自动对焦
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						// TODO Auto-generated method stub
						// if (success) {
						// 设置参数，并拍照
						// Parameters params = camera.getParameters();
						// params.setPictureFormat(PixelFormat.JPEG);//
						// 图片格式
						// params.setPreviewSize(photoWidth,
						// photoHeight);//
						// 图片大小
						// camera.setParameters(params);//
						// 将参数设置到我的camera
						camera.takePicture(null, null, jpeg);// 将拍摄到的照片给自定义的对象
						ImageLoader.getInstance().clearMemoryCache();
						ImageLoader.getInstance().clearDiscCache();
						// // 拍照时，开启闪光灯
						// Camera.Parameters parameters =
						// mCamera.getParameters();
						// parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
						//
						// mCamera.setParameters(parameters);
						cameraTurn.setVisibility(View.GONE);
						flashLightButton.setVisibility(View.GONE);
						cameraPhotos.setVisibility(View.GONE);
						camera_return.setVisibility(View.GONE);
						paizhao.setVisibility(View.GONE);
						camera_confrim.setVisibility(View.VISIBLE);
						camera_now_what.setVisibility(View.GONE);
						camera_back.setVisibility(View.VISIBLE);
						camera_finish.setVisibility(View.GONE);

						// upImage.
						// }

					}
				});
			}
		});

		cameraPhotos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent_gralley = new Intent(PublishNewActivity.this,
				// PhotoActivity.class);
				// startActivityForResult(intent_gralley, GRALLEY);
				Intent intent_gralley = new Intent(CameraActivity.this,
						PhotoActivity.class);
				intent_gralley.putExtra(IsRegister, isRegister);
				startActivityForResult(intent_gralley, GRALLEY);

			}
		});

		// -------------------------------------------------------------------------------------------------相机添加图片
		// filename = getedintent.getStringExtra("filename");
		// if (filename != null && !filename.equals("")) {
		// releaseadapter.additem(filename);
		// }
		// //
		// -------------------------------------------------------------------------------------------------相册添加图片
		// PublishImageBean[] temp = (PublishImageBean[]) getedintent
		// .getSerializableExtra("filename");
		// if (temp != null) {
		// filenames = temp;
		// }
		// if (filenames != null && filenames.length > 0)
		// releaseadapter.addlist(filenames);

		// setlisten();
		// if (filename != null) {
		// filenames.add(filename);
		// }
	}

	// 创建jpeg图片回调数据对象
	PictureCallback jpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {
				// if (data.length < MAX_PIC_LENGTH * SAMPLE_SIZE) {
				// Log.i(  "aaa",
				// "data.length < MAX_PIC_LENGTH * SAMPLE_SIZE");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				// options.inSampleSize = SAMPLE_SIZE;

				photoBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length, options);

				photoBitmap = ImageTool.rotateBitMap(photoBitmap, 1);

				int width = photoBitmap.getWidth();
				if (cameraPosition == 0) {
					photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, width,
							width);
				} else {
					photoBitmap = ImageTool.convert(photoBitmap);
					photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, width,
							width);
				}
				Log.i(  "aaa", "isRegister === " + isRegister);
				// 自定义文件保存路径 以拍摄时间区分命名
				if ("1".equals(isRegister)) {
					filepath = FileURl.GoodsIamgeURL + FileURl.IMAGE_NAME;
					filename = filepath;
				// } else if ("0".equals(isRegister)) {
				} else {
					filepath = FileURl.GoodsIamgeURL
							+ (System.currentTimeMillis() / 1000)
							+ FileURl.IMAGE_NAME;
					filename = filepath;
					Log.i(  "aaa",
							"filename filepathfilepathfilepath========== "
									+ filename);
				}
				Log.i(  "aaa", "filename ========== " + filename);
				File file = new File(filepath);
				if (file.exists()) {
					file.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));

				photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩的流里面
				// photoBitmap = bitmap;

				ImageLoader.getInstance().displayImage(
						FileURl.LOAD_FILE + filename, cameraResult);
				bos.flush();// 刷新此缓冲区的输出流
				bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
				camera.stopPreview();// 关闭预览 处理数据
				camera.startPreview();// 数据处理完后继续开始预览

				Log.i(  "aaa", "curIndex ===== " + curPosition);

				// if(curIndex == 6){
				// releaseadapter.setItemIndex(curIndex-1);
				// releaseadapter.notifyDataSetChanged();
				// } else {
				// releaseadapter.setItemIndex(curIndex);
				// releaseadapter.notifyDataSetChanged();
				// }
				// bitmap.recycle();// 回收bitmap空间
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	private void frontOrBack() {
		// TODO Auto-generated method stub
		// 切换前后摄像头
		if (mCamera != null) {
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();// 释放资源
			mCamera = null;// 取消原来摄像头
		}
		cameraFramlayout.removeAllViews();

		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
			if (cameraPosition == 1) {
				// 现在是后置，变更为前置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					Log.i(  "aaa", "现在是后置，变更为前置"); // CAMERA_FACING_BACK后置
					cameraPosition = 0;
					mTextureView = null;
					mTextureView = new TextureView(CameraActivity.this);
					mTextureView.setSurfaceTextureListener(CameraActivity.this);
					cameraFramlayout.addView(mTextureView);
					mTextureView.setVisibility(View.VISIBLE);
					// initCamera(mPreviwRate);

					break;
				}
			} else {
				// 现在是前置， 变更为后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					Log.i(  "aaa", " 现在是前置， 变更为后置");

					cameraPosition = 1;
					mTextureView = null;
					mTextureView = new TextureView(CameraActivity.this);
					mTextureView.setSurfaceTextureListener(CameraActivity.this);
					cameraFramlayout.addView(mTextureView); // CAMERA_FACING_BACK后置
					mTextureView.setVisibility(View.VISIBLE);
					break;
				}
			}
		}

	}

	String imagename;
	int curPosition;

	public static final int GRALLEY = 100;
	public static final int PHOTO = 101;

	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);
	// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
	String appID = "wx29ade8f18412f2fa";

	// 封装请求Gallery的intent
	public Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GRALLEY && resultCode == 444) {
			Log.i(  "aaa", "GRALLEY");
			Intent intent = new Intent();
			setResult(444, intent);
			CameraActivity.this.finish();
			return;
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		
	

		try {
			mCamera = Camera.open(cameraPosition);
			if (mCamera == null) {
				// Seeing this on Nexus 7 2012 -- I guess it wants a rear-facing
				// camera, but
				// there isn't one. TODO: fix
				throw new RuntimeException("Default camera not available");
			}
			mCamera.setPreviewTexture(surface);
			mCamera.setDisplayOrientation(getPreviewDegree(CameraActivity.this));
			mCamera.startPreview();
			initCamera(mPreviwRate);
		} catch (Exception e) {
			// Something bad happened
			Toast.makeText(CameraActivity.this, "请在设置里面打开相机权限", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		// Ignored, Camera does all the work for us
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// Invoked every time there's a new Camera preview frame
		// Log.i(  TAG, "updated, ts=" + surface.getTimestamp());

	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	/**
	 * 进群接口
	 */
	private void intoGroup(String group_id, String group_name) {
		
		if (userID == null || userID.length() == 0) {
			SharedPreferences setting = GroupApplication.getInstance()
					.getSharedPreferences(
							(GroupApplication.getInstance()).getCurSP(), 0);
			userID = setting.getString("userID", null);
			
		}
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("longitude", longitude));
			params.add(new BasicNameValuePair("latitude", latitude));
			params.add(new BasicNameValuePair("user_id", userID));
			if (group_id != null && group_id.length() > 0) {

				params.add(new BasicNameValuePair("group_id", group_id));
			}
			if (group_name != null && group_name.length() > 0) {
				params.add(new BasicNameValuePair("group_name", group_name));
				isNewGroup = true;
			}
			// params.add(new BasicNameValuePair("token", Imei));
			
			Log.i(  "aaa", "params ============ " + params);

		} catch (Exception e) {

		}
		Service.getService(Contanst.HTTP_INTOGROUP, null, null,
				CameraActivity.this).addList(params).request(UrlParams.POST);
		isIntoGroupConn = true;
	}

	private void insertSearchHistory(String group_id, String group_Name) {
		// 添加历史
		ArrayList<GroupListBean> historygroupList = new ArrayList<GroupListBean>(
				10);
		// if (isMain) {
		historygroupList = dbHelper.getMainHistoryList(historygroupList);
		// } else {
		// historygroupList = dbHelper.getNameList(historygroupList);
		// }

		if (historygroupList.size() > 0 && historygroupList != null) {
			for (int i = 0; i < historygroupList.size(); i++) {
				GroupListBean historyListBean = historygroupList.get(i);
				if (group_id.equals(historyListBean.getGroupID())) {
					Log.i(  "aaa", "重复数据" + group_Name);
					// 如果重复不添加
					historygroupList.get(i).setGroupTime(
							System.currentTimeMillis());
					// dbHelper.insertSearchList(historygroupList);

					// if (isMain) {
					dbHelper.insertMainHistoryList(historygroupList);
					// } else {
					// dbHelper.insertSearchList(historygroupList);
					// }
					return;
				}
			}

			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				if (group_distance != null) {
					bean.setDistance(group_distance);
				} else {
					bean.setDistance("0.04km");
				}
				if (group_peopleCount != null) {
					bean.setGroupPeopleCount(group_peopleCount);
				} else {
					bean.setGroupPeopleCount("1");
				}
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		} else {
			if (searchGroupBean != null && searchGroupBean.getGroupID() != null
					&& searchGroupBean.getGroupID().length() > 0) {
				historygroupList.add(searchGroupBean);
				// dbHelper.insertSearchList(historygroupList);
				dbHelper.insertMainHistoryList(historygroupList);
			} else {
				GroupListBean bean = new GroupListBean();
				bean.setGroupID(group_id);
				bean.setDistance("0.04km");
				bean.setGroupPeopleCount("1");
				bean.setGroupName(group_Name);
				bean.setGroupTime(System.currentTimeMillis());
				historygroupList.add(bean);
				// dbHelper.insertSearchList(historygroupList);
				// if (isMain) {
				dbHelper.insertMainHistoryList(historygroupList);
				// } else {
				// dbHelper.insertSearchList(historygroupList);
				// }
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yktx.group.conn.ServiceListener#getJOSNdataSuccess(java.lang.Object,
	 * java.lang.String, int)
	 */
	@Override
	public void getJOSNdataSuccess(Object bean, String sccmsg, int connType) {
		// TODO Auto-generated method stub
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

	boolean isNewGroup;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();
			switch (msg.what) {
			case Contanst.BEST_INFO_OK:
				if (msg.arg1 == Contanst.INTOGROUP) {
					HashMap<String, String> map = (HashMap<String, String>) msg.obj;
					group_id = map.get("group_id");

					Intent in = new Intent(CameraActivity.this,
							ChatActivity.class);
					if (group_id != null && group_id.length() > 0) {
						in.putExtra(ChatActivity.GroupID, group_id);
						in.putExtra(ChatActivity.GroupName, group_name);
						in.putExtra(ChatActivity.IsNewGroup, isNewGroup);
						in.putExtra(ChatActivity.IsFirst, isFirst);
						in.putExtra(ChatActivity.IsJump, isJump);
						in.putExtra(ChatActivity.AtMyNum, atMyNum);
					}

					// if (isSearch) {
					// // 添加搜索界面历史
					//
					// insertSearchHistory(group_id, group_name);
					// }
					// 添加本地历史记录
					insertSearchHistory(group_id, group_name);
					CameraActivity.this.startActivity(in);
					CameraActivity.this.finish();

				}
				break;
			case Contanst.BEST_INFO_FAIL:
				String message = (String) msg.obj;
				isIntoGroupConn = false;
				Log.i(  "aaa", "message = " + message);
				Toast.makeText(CameraActivity.this, message, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}

	};

}