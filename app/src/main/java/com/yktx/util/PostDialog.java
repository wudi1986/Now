/**
 * 
 */
package com.yktx.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yktx.group.R;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年7月29日 下午4:11:18  
 * 类说明  */
/**
 * @author Administrator
 *
 */
public class PostDialog extends AlertDialog{

	public ImageView back;
	
	
	public PostDialog(Context context, int theme) {
	    super(context, theme);
	}
	
	public PostDialog(Context context) {
	    super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.post_loading);
	    back = (ImageView)findViewById(R.id.share_back);
	    back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostDialog.this.dismiss();
			}
		});
	}
}
