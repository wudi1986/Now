package com.yktx.view;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yktx.bean.GroupListBean;
import com.yktx.group.R;
/**
 * @author xushilin
 * 
 */
public class VerticalScrollTextView extends TextView {
	private Paint mPaint;
	private float mX;
	private Paint mPathPaint;	
	public int index = 0;
	private ArrayList<GroupListBean> list;
	public float mTouchHistoryY;
	private int mY;	
	private float middleY;// y轴中间
	private static final int DY = 100; // 每一行的间隔
	public VerticalScrollTextView(Context context) {
		super(context);
		init();
	}
	public VerticalScrollTextView(Context context, AttributeSet attr) {
		super(context, attr);
		init();
	}
	public VerticalScrollTextView(Context context, AttributeSet attr, int i) {
		super(context, attr, i);
		init();
	}
	private void init() {
		setFocusable(true);
		if(list == null){
			list=new ArrayList<GroupListBean>();
			GroupListBean sen = new GroupListBean();
			list.add(0, sen);
		}		
	
		// 非高亮部分
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(getResources().getDimension(R.dimen.font5));
		mPaint.setColor(getResources().getColor(R.color.meibao_color_6));
		mPaint.setTypeface(Typeface.SERIF);
		
		// 高亮部分 当前歌词
		mPathPaint = new Paint();
		mPathPaint.setAntiAlias(true);
		mPathPaint.setColor(getResources().getColor(R.color.meibao_color_3));
		mPathPaint.setTextSize(getResources().getDimension(R.dimen.font3));
		mPathPaint.setTypeface(Typeface.SANS_SERIF);
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(0xEFeffff);
		Paint p = mPaint;
		Paint p2 = mPathPaint;
		p.setTextAlign(Paint.Align.CENTER);
		if (index == -1)
			return;
		p2.setTextAlign(Paint.Align.CENTER);
		// 先画当前行，之后再画他的前面和后面，这样就保持当前行在中间的位置
		canvas.drawText(list.get(index).getGroupName(), mX, middleY, p2);
		float tempY = middleY;
		// 画出本句之前的句子
		for (int i = index - 1; i >= 0; i--) {			
			tempY = tempY - DY;
			if (tempY < 0) {
				break;
			}
			canvas.drawText(list.get(i).getGroupName(), mX, tempY, p);			
		}
		tempY = middleY;
		// 画出本句之后的句子
		for (int i = index + 1; i < list.size(); i++) {
			// 往下推移
			tempY = tempY + DY;
			if (tempY > mY) {
				break;
			}
			canvas.drawText(list.get(i).getGroupName(), mX, tempY, p);			
		}
	}
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		mX = w * 0.5f; 
		mY = h;
		middleY = h * 0.5f;
	}

	public long updateIndex(int index) {
		if (index == -1)
			return -1;
		this.index=index;		
		return index;
	}
	
	public List<GroupListBean> getList() {
		return list;
	}
	
	public void setList(ArrayList<GroupListBean> list) {
		this.list = list;
	}
	public void updateUI(){
		new Thread(new updateThread()).start();
	}
	class updateThread implements Runnable {
		long time = 2000; // 开始 的时间，不能为零，否则前面几句歌词没有显示出来
		int i=0;
		public void run() {
			while (true) {
				long sleeptime = updateIndex(i);
				time += sleeptime;
				mHandler.post(mUpdateResults);
				if (sleeptime == -1)
					return;
				try {
					Thread.sleep(time);
					i++;
					if(i==getList().size())
						i=0;
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
	}
	Handler mHandler = new Handler();
	Runnable mUpdateResults = new Runnable() {
		public void run() {
			invalidate(); // 更新视图
		}
	};
}