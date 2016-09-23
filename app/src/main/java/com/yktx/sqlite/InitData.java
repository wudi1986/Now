package com.yktx.sqlite;
//package com.orionrsclient.sqlite;
//
////package com.sqlite.sqlite;
//
//import android.content.Context;
//
//import com.beotech.logicomponent.AndroidUtils;
//import com.other.other.Define;
//
//public class InitData {
//
//	public static void resetData(Context context) {
//
//		DBHelper mDb = new DBHelper(context);
//
//		AndroidUtils.setSharedPreferences(context, Define.STORE, Define.JSON,
//				"");
//
//		AndroidUtils.setSharedPreferences(context, Define.DIVISION,
//				Define.JSON, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_MAIN, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_CO, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_CO_WIND, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_MULTI, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_MULTI_WIND, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.DISP_STD,
//				Define.JSON_DISP_ADD, "");
//
//		AndroidUtils.setSharedPreferences(context, Define.REPORT_STORE,
//				Define.JSON, "");
//
//		mDb.deleteSyncInfo();
//	}
//
//	public static void initLogin(Context context) {
//
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO, "PRE_ID",
//				"");
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO, "ID", "");
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO, "NAME", "");
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO, "DEPT_CD",
//				"");
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO, "DEPT_NM",
//				"");
//		AndroidUtils.setSharedPreferences(context, Define.USERINFO,
//				"SESSION_ID", "");
//
//		AndroidUtils.setSharedPreferences(context, Define.LOGIN,
//				Define.LOGIN_DATE, "");
//		AndroidUtils.setSharedPreferences(context, Define.LOGIN,
//				Define.LOGIN_ID, "");
//		AndroidUtils.setSharedPreferences(context, Define.LOGIN,
//				Define.LOGIN_PW, "");
//	}
//}
