package com.yktx.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.yktx.group.R;

public class NotificationUtil {
	public static void calltop(final Context context,String msg) {
		String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
        CharSequence tickerText = "Hello";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.icon,msg, when);
        notification.flags=Notification.FLAG_AUTO_CANCEL;
//        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context,NewMainActivity.class), 0);
        notification.setLatestEventInfo(context, "Pat发送中", msg, null);
        nm.notify(1, notification);
        
        new  Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
			    notificationManager.cancel(1); 
			}
		}).start();

	}
}
