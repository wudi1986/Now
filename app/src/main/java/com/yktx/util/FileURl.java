package com.yktx.util;

import java.io.File;
import java.io.FileInputStream;

import android.os.Environment;
import android.util.Base64;

/**
 * Created by Administrator on 2014/4/14.
 */
public class FileURl {
	/* 商品图片 */
	public static final String GoodsIamgeURL = Environment
			.getExternalStorageDirectory() + "/groupimage/";
	public static final String LOAD_FILE = "file://"; // from SD
	public static final String LOAD_HTTP = "http://";// from Web
	public static final String LOAD_CONTENT = "content://";// from content
															// provider
	public static final String LOAD_ASSETS = "assets://image.png"; // from
																	// assets
	public static final String LOAD_DRAWABLES = "drawable://"; // from drawables
																// (only images,
																// non-9patch)
	public static final String IMAGE_NAME = "group.jpg"; // from drawables (only
															// images,
															// non-9patch)

	public static final String VOICE_PATH = "/groupvoice/";
	public static final String GoodsVoiceURL = Environment
			.getExternalStorageDirectory() + VOICE_PATH;

	public static File ImageFilePath = new File(
			(Environment.getExternalStorageDirectory()).getPath()
					+ "/groupimage/");

	public static String fileToString(String path) {
		File file = new File(path);
		FileInputStream inputFile;
		try {
			inputFile = new FileInputStream(file);

			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// new BASE64Encoder() .encode(buffer);
	}
}
