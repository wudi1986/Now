package com.yktx.facial;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class ExpressionUtil {
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */
	public static SpannableString dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start,
			HashMap<String, Object> chatFacial) throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			Log.i(  "aaa", "key" + key);
			if (matcher.start() < start) {
				continue;
			}
			// Field field = R.drawable.class.getDeclaredField(key);
			// int resId = Integer.parseInt(field.get(null).toString());
			// if (resId != 0) {
			Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), (Integer) chatFacial.get(key));
			@SuppressWarnings("deprecation")
			ImageSpan imageSpan = new ImageSpan(context,bitmap);
			
			int end = matcher.start() + key.length();
			spannableString.setSpan(imageSpan, matcher.start(), end,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			if (end < spannableString.length()) {
				dealExpression(context, spannableString, patten, end,
						chatFacial);
			}
			return spannableString;
			// }
		}
		return spannableString;
	}

	public static SpannableString getExpressionString(Context context,
			String str, String zhengze, HashMap<String, Object> chatFacial) {
		Log.i(  "aaa", "进来的内容 = " + str);
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			return dealExpression(context, spannableString, sinaPatten, 0,
					chatFacial);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}
	
	public static boolean isHave(Context context,
		String str, String zhengze, HashMap<String, Object> chatFacial){
		Log.i(  "aaa", "进来的内容 = " + str);
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			return isHave(context, spannableString, sinaPatten, 0,
					chatFacial);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("dealExpression", e.getMessage());
		}
		return false;
		
	}
	
	public static boolean isHave(Context context,
			SpannableString spannableString, Pattern patten, int start,
			HashMap<String, Object> chatFacial) throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			Log.i(  "aaa", "key" + key);
			if (matcher.start() < start) {
				continue;
			}
			// Field field = R.drawable.class.getDeclaredField(key);
			// int resId = Integer.parseInt(field.get(null).toString());
			// if (resId != 0) {
			Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), (Integer) chatFacial.get(key));
			@SuppressWarnings("deprecation")
			ImageSpan imageSpan = new ImageSpan(context,bitmap);
			
			int end = matcher.start() + key.length();
			spannableString.setSpan(imageSpan, matcher.start(), end,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			return true;
			// }
		}
		return false;
	}

	
}