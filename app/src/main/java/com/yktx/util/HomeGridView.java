/**
 * 
 */
package com.yktx.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**  
 * @author 作者 :  wudi
 * @version 创建时间：2014年10月9日 上午10:49:33  
 * 类说明  */
/**
 * @author Administrator
 *
 */
public class HomeGridView extends GridView  {
	
	
/**
	 * @param context
	 * @param attrs
	 */
	public HomeGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

  
    /** 
     * 设置不滚动 
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                MeasureSpec.AT_MOST);  
        super.onMeasure(widthMeasureSpec, expandSpec);  
  
    }  
  
}  
