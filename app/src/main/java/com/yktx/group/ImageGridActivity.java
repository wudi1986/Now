package com.yktx.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ImageGridActivity extends FragmentActivity {

	private static final String TAG = "ImageGridActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (BuildConfig.DEBUG) {
//            Utils.enableStrictMode();
//        }
        super.onCreate(savedInstanceState);

//        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
//            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(android.R.id.content, new ImageGridFragment(), TAG);
//            ft.commit();
//        }
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		
		
		
	}
	
	
}
