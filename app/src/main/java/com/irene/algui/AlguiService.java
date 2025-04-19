package com.irene.algui;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import xyz.eight.R;


/**
 * @Author 𝘽𝙮·艾琳 - 手游逆向交流群101640882
 * @Date 2023/010/11 15:38
 * @Describe ALGUI服务
 */
public class AlguiService extends Service {

    public static final String CHANNEL_ID = "com.irene.algui.channel_id";

    private Context mContext;

    //这个方法用于绑定Service组件和其他组件之间的交互，在这里返回null表示不支持绑定
    @Override
    public IBinder onBind(Intent Intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Algui.start(mContext);//启动ALGUI

        // 创建前台通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Algui Service")
                    .setContentText("为了保证ALGUI正常运行，请不要关闭此通知")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
        }

        startForeground(1, notification);

        if (mContext != null) {
            Intent intent = new Intent("com.irene.algui.ACTION_CLOSE");
            mContext.sendBroadcast(intent);
        }
    }

    //在Service销毁时调用此方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static final String TAG = "AlguiService";



}
