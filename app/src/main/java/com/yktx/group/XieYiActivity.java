/**
 * 
 */
package com.yktx.group;




import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年5月21日 下午6:56:13  
 * 类说明  */
/**
 * @author Administrator
 * 
 */
public class XieYiActivity extends BaseActivity {

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xieyi_activity);
		// RelativeLayout rl = (RelativeLayout) findViewById(R.id.main);
		TextView back = (TextView)findViewById(R.id.xieyi_back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				XieYiActivity.this.finish();
			}
		});

	}


}
