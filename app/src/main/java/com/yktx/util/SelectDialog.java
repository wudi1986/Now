/**
 * 
 */
package com.yktx.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yktx.group.R;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年7月29日 下午4:11:18  
 * 类说明  */
/**
 * @author Administrator
 *
 */
public class SelectDialog extends AlertDialog{

	public LinearLayout weixin, pengyouquan;
	public ImageView back;
	
	
	public SelectDialog(Context context, int theme) {
	    super(context, theme);
	}
	
	public SelectDialog(Context context) {
	    super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.share_type);
	    setInverseBackgroundForced(true);
	    weixin = (LinearLayout)findViewById(R.id.weixin);
	    pengyouquan = (LinearLayout)findViewById(R.id.pengyouquan);
	    back = (ImageView)findViewById(R.id.share_back);
	    
	}
}
