package com.yktx.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * @Title: Tools.java
 * @Package com.coupon.other
 * @author WUDI
 * @date 2011-8-26 ����08:31:48
 * @version V1.0
 */
public class Tools {
	
	
	/** 
     * 得到amr的时长 
    *  
     * @param file 
     * @return 
     * @throws IOException 
     */  
	public static long getAmrDuration(File file) throws IOException {  
        long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长度  
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            /////////////////////////////////////////////////////  
            byte[] datas = new byte[1];//初始数据值  
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            /////////////////////////////////////////////////////  
            duration += frameCount * 20;//帧数*20  
        } finally {  
            if (randomAccessFile != null) {  
                randomAccessFile.close();  
            }  
        }  
        return duration;  
    }   
	/**
	 * @param imgUrl1
	 *            �������ȡurl ���ͼƬ
	 * @return
	 */
	// public static Bitmap downloadFile(String fileUrl) {
	// URL myFileUrl = null;
	// Bitmap bmImg = null;
	// try {
	// myFileUrl = new URL(fileUrl);
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// try {
	// HttpURLConnection conn = (HttpURLConnection) myFileUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// int length = conn.getContentLength();
	//
	// InputStream is = conn.getInputStream();
	// if (bmImg == null)
	// bmImg = BitmapFactory.decodeStream(is);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	//
	// } finally {
	//
	// }
	// // Log.i(  "aaa", "bmImg = "+bmImg.getWidth());
	// // Log.i(  "aaa", "bmImg = "+bmImg.getHeight());
	// return bmImg;
	// }
	
	
	/**
	 * 限制edittext输入特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str)throws PatternSyntaxException{

        String regEx = "[-,.，。/\\:*?<>|\"\n\t]";

        Pattern p = Pattern.compile(regEx);

        Matcher m = p.matcher(str);

        return m.replaceAll("");

    }
	
	
	public static  int getLineSize(String s){
//		String s="中文absdfwe中sfwef哈";
        int n=0;
        
        for (int i=0;i<s.length();i++)
        {
        	if (s.charAt(i) >= 0x4e00 && s.charAt(i) <= 0x9fa5){
        		n+=2;
        	} else {
            	n++;
        	}
        }
        Log.i(  "aaa", "n === "+n);
        return n;
	}

	
	// ��ȡcookei httpHeader
	public static String spit(String fileValue, String str) {

		if (fileValue.indexOf(str) != -1) {
			return fileValue.substring(fileValue.indexOf(str), fileValue
					.indexOf(";"));
		}
		return null;
	}

	// �����ȡweb url ����
	public static String[] keyAndVaalue(String url) {
		String[] array = url.split("&");
		return array;
	}

	public static String keyToValue(String url, String key) {
		String[] array = url.split("&");
		for (int i = 0; i < array.length; i++) {
			if (array[i].substring(0, array[i].indexOf("=")).equals(key)) {
				return array[i].substring(array[i].indexOf("=") + 1);
			}
		}
		return "";
	}


	/**
	 * 获取系统时间
	 */
   public static String getData(){
	   SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
	   Date curDate = new Date(System.currentTimeMillis());//获取当前时间
	   String str = formatter.format(curDate);
	return str;
	   
   }

	// ������ͷ
	public static Bitmap tackpicture(Activity activity) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				"/sdcard/test.jpg")));
		activity.startActivityForResult(intent, 2);
		return null;

	}

	// ��ʾ��Ƭ
	public static void displayPicture(ImageView img) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile("/sdcard/test.jpg", options);
		Bitmap bmm = zoomImage(bm, 200, 200);
		Bitmap bmmm = rotate(bmm, 90);
		img.setImageBitmap(bmmm);
	}

	// ��������ͼ
	public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

		int width = bgimage.getWidth();

		int height = bgimage.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,

		matrix, true);

		return bitmap;
	}

	// ����Bitmap����ת
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) 80, (float) 80);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b
						.getHeight(), m, true);
				if (b != b2) {
					b.recycle(); // Android�������ٴ���ʾBitmap������Ӧ����ʾ���ͷ�
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123��������γ������ڴ治���쳣�����return ԭʼ��bitmap����.
			}
		}
		return b;
	}

	public static String ReturnPicture() {
		return "/sdcard/test.jpg";
	}

	// �Ӷ�ý����ѡ��ͼƬ
	public static void changePicture(ArrayList list, String rootPath) {
		list.clear();
		// File sd = Environment.getExternalStorageDirectory();
		// String path = sd.getPath() + "/picshow";// ��ȡ��Ŀ¼
		// File file = new File(path);
		// if (!file.exists())
		// file.mkdir(); // ����picshow�ļ��У����ڴ������ͼ
		getFileDir(rootPath, list); // ��ʼ����SD������ͼƬ
	}
	
	// ����SD������ͼƬ
	public static void getFileDir(String filePath, ArrayList list) {
		try {
			File f = new File(filePath);
			File[] files = f.listFiles();
			if (files != null) {
				int count = files.length;
				Log.i(  "aaa", "count = " + count);
				for (int i = 0; i < count; i++) {
					File filess = files[i];
					File file = filess;
					String filepath = file.getAbsolutePath();
					String path = file.getPath();
					// ��������ͼ��picshow�ļ��У���ϵͳ�Զ���ɵ�dcim�ļ�����
					// if (filepath.endsWith("picshow")
					// || filepath.endsWith("DCIM"))
					// continue;

					if (filepath.endsWith("jpg") || filepath.endsWith("gif")
							|| filepath.endsWith("bmp")
							|| filepath.endsWith("png")) {

						// j++; // ÿ��ȡһ��ͼƬ��j��1
						//
						// Log.i(  "kkk", "number:" + j);
						// saveFile(filepath,list); // ���ͼƬ
						Log.i(  "aaa", "filepath = " + filepath);
						list.add(filepath);

					}
					// ��ͼƬ�ļ�
					else { // Ŀ��Ϊ�ļ��У�������ļ��м������

						if (file.isDirectory()) {
							getFileDir(path, list);
						}
					}
					continue;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// // ��������ͼ
	// public static Bitmap saveFile( String filepath,ArrayList list) {
	//
	// BitmapFactory.Options opts = new BitmapFactory.Options();
	// opts.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(filepath, opts);
	// opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
	// opts.inJustDecodeBounds = false;
	// Bitmap bmp = BitmapFactory.decodeFile(filepath, opts);
	//
	// // File filex = new File("/sdcard/picshow/" + "pic_" + file.getName());
	// // try {
	// // FileOutputStream out = new FileOutputStream(filex);
	// // if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
	// // out.flush();
	// // out.close();
	// // }
	// // } catch (FileNotFoundException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // } catch (IOException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// list.add(filepath);
	//
	// return bmp;
	// }

	// ��ȡ·���е�ͼƬ
	public static Bitmap FileToBitmapSmall(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(path, opts);
		return bmp;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
				.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	
	 public static Bitmap decodeBitmap(Bitmap groundBitmap)  
	    {  
		 
	         BitmapFactory.Options options = new BitmapFactory.Options();  
	         options.inJustDecodeBounds = true;  
	       // 通过这个bitmap获取图片的宽和高         
	         Bitmap bitmap = Bitmap.createBitmap(groundBitmap.getWidth(),
						groundBitmap.getHeight(), Config.ARGB_8888);
	         if (bitmap == null)  
	         {  
	             System.out.println("bitmap为空");  
	         }  
	         float realWidth = options.outWidth;  
	         float realHeight = options.outHeight;  
//	         System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);  
	        // 计算缩放比         
	         int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);  
	        if (scale <= 0)  
	         {  
	             scale = 1;  
	         }  
	         options.inSampleSize = scale;  
	         options.inJustDecodeBounds = false;  
	         // 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。        
	         bitmap = BitmapFactory.decodeFile("/sdcard/MTXX/3.jpg", options); 
	         
	         int w = bitmap.getWidth();  
	         int h = bitmap.getHeight();  
//	         System.out.println("缩略图高度：" + h + "宽度:" + w);  
	         return bitmap;  
	     }  

	

	public static String addPrice(String price) {
		if (price == null || price.length() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();

		String priceResule = price;
		int length = priceResule.length();
		int off = length % 3;
		if (off != 0) {
			sb.append(priceResule.substring(0, off));
			sb.append(",");
		}
		for (int i = 0; i < length / 3; i++) {
			sb.append(priceResule.substring(off + i * 3, off + i * 3 + 3));
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String addJianGe(String expireDate) {
		if (expireDate == null || expireDate.length() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		String dateResule = expireDate;
		for (int i = 0; i < expireDate.length(); i++) {
			if (i == 3) {
				sb.append(dateResule.subSequence(0, 4));
				sb.append(".");
			}
			if (i == 5) {
				sb.append(dateResule.substring(3, 5));
				sb.append(".");
				sb.append(dateResule.substring(6));
			}
		}
		return sb.toString();
	}

	// ����ʱ���ʽ

	public static String timeFormat(int time) {
		String t;
		if (time < 10) {
			t = "00:0" + time;
		} else if (time <= 60) {
			t = "00:" + time;
		} else {
			t = time / 60 > 10 ? time / 60 + ":" + time % 60 : "0" + time / 60
					+ ":" + time % 60;
		}
		return t;
	}

	// ����ϵͳ¼���ܲ�������ָ��·��
	public static void videoAndSave(int requestCode, int resultCode,
			Intent data, Activity activity) {
		File fileDst = new File("/sdcard/videotest.3gp");
		try {

			Log.i(  "kkk", "path-----" + data.getData().toString());
			Log.i(  "kkk", "file-----" + data.getData());

			AssetFileDescriptor videoAsset = activity.getContentResolver()
					.openAssetFileDescriptor(data.getData(), "r");
			InputStream in = videoAsset.createInputStream();
			OutputStream out = new FileOutputStream(fileDst);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(activity, "faile to save video file",
					Toast.LENGTH_SHORT).show();
		}
	}

	// �ϴ�ͼƬ

	// public static String getImageString(String imgFilePath) {
	// Bitmap mBitmap = BitmapFactory.decodeFile(imgFilePath);
	// Matrix matrix = new Matrix();
	// matrix.postScale(0.5f, 0.5f);
	// Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap
	// .getWidth(), mBitmap.getHeight(), matrix, true);
	//
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// newBitmap.compress(CompressFormat.JPEG, 100, out);
	// byte[] bytes = out.toByteArray();
	// String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
	// return imageString;
	// }

	public static void uploadFile(String imageFilePath) {
		String actionUrl = "http://14.63.215.38:8080/api//web/ImageUpload.aspx";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			con.setRequestMethod("POST");

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			File file = new File(imageFilePath);

			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;

			while ((length = fStream.read(buffer)) != -1) {

				ds.write(buffer, 0, length);
			}

			fStream.close();
			ds.flush();

			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// �ֻ�����¼�
	public static void browserPhotoAction(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		Uri mUri = Uri.fromFile(new File("kkkk"));
//		Log.i(  "aaa", "mUri = "+mUri);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		activity.startActivityForResult(intent, 1);
		// Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
		// getImage.addCategory(Intent.CATEGORY_OPENABLE);
		// getImage.setType("image/jpeg");
		// startActivityForResult(getImage, PICK_PIC);
	}

	/**
	 * ��ͼƬ���ݽ�����ֽ�����
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	/**
	 * ���ֽ�����ת��ΪImageView�ɵ��õ�Bitmap����
	 * 
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return zoomImage(BitmapFactory.decodeByteArray(bytes, 0,
						bytes.length, opts), 200, 200);
			else
				return zoomImage(BitmapFactory.decodeByteArray(bytes, 0,
						bytes.length), 200, 200);
		return null;
	}
	
	/**获取版本号*/
	public static String getAppVersionName(Context context) {

		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
//			Log.e(TAG, "Exception", e);
		}

		return versionName;

	}
	
	/**�ж��Ƿ�Ϊ������ ���ҳ��Ȳ�С��6*/
	public static Boolean isOnlyMath(String password){
		
		if(password.length() < 6){
			return true;
		}
		for (int i = 0; i < password.length(); i++){
			String str = null;
			if(i == password.length()-1){
				str = password.substring(i);
			} else 
				str= password.substring(i,i+1);
			Log.i(  "aaa", "str = "+str);
			Log.i(  "aaa", "str hashCode = "+str.hashCode());
			if(str.hashCode() < 48){
				Log.i(  "aaa", "str = "+str);
				Log.i(  "aaa", "Integer.parseInt(str) < 48 hashCode = "+str.hashCode());
				return false;
			}
			if(str.hashCode() > 57){
				Log.i(  "aaa", "str = "+str);
				Log.i(  "aaa", "Integer.parseInt(str) > 57 hashCode = "+str.hashCode());
				return false;
			}
		}
		
		return true;
	}

	public static final int i = 0;		//显示一般信息
	public static final int w = 1;		//显示警告信息
	public static final int e = 2;		//显示错误
	public static final int v = 3;		//显示全部信息
	public static final int d = 4;		//显示调试信息
	
	public static void getLog(int type, String tag, String msg){
		if(Contanst.isDebug){
			switch(type){
			case i:
				Log.i(  tag, msg);
				break;
			case w:
				Log.i(  tag, msg);
				break;
			case e:
				Log.i(  tag, msg);
				break;
			case v:
				Log.i(  tag, msg);
				break;
			case d:
				Log.i(  tag, msg);
				break;
			}
		}
	}
	
	
	
}
