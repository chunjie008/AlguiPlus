package com.irene.algui;


import static com.irene.algui.AlguiService.CHANNEL_ID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import irene.window.algui.Tools.AppPermissionTool;
import xyz.eight.R;

import android.os.Process;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //你可以在ALGUI打包安装后 反编译安装包 然后在Dex文件中找到这串代码编译后的Smali代码，
        //你可以复制编译后的Smali代码并粘贴进任意软件的Dex文件中的Smali代码当中，
        //这样就相当于你成功将ALGUI嵌入进了其软件的内部，但是仅供娱乐最好不要破坏软件，后果自负
        //它的Smali代码：
        //invoke-static {p0}, Lcom/irene/algui/AlguiLoading;->start(Landroid/content/Context;)V
        // 获取按钮并设置点击事件

        Button btnWindowPermission = findViewById(R.id.btn_window_permission);
        Button btnNotificationPermission = findViewById(R.id.btn_notification_permission);
        Button btnBackgroundPermission = findViewById(R.id.btn_background_permission);
        Button btnOpenWindow = findViewById(R.id.btn_open_window);
        Button btnCloseWindow = findViewById(R.id.btn_close_window);
        btnWindowPermission.setOnClickListener(v -> {
            // 申请悬浮窗权限
            AppPermissionTool.floatingWindowPermission(this);
        });

        btnNotificationPermission.setOnClickListener(v -> {
            // 创建通知渠道(Android 8.0+需要)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "权限通知",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            // 构建通知
            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("通知权限申请")
                        .setContentText("这是测试通知 可以关闭")
                        .setSmallIcon(R.drawable.ic_launcher) // 请替换为你的应用图标
                        .setAutoCancel(true) // 点击后自动关闭
                        .build();
            }
            // 发送通知
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1001, notification);
        });


        btnBackgroundPermission.setOnClickListener(v -> {
            //关闭省电模式权限申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isIgnoringBatteryOptimizations(getPackageName())) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                    intent.setData(getPackageURI());
                    startActivity(intent);
                }
            }
        });

        btnOpenWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击按钮时执行悬浮窗启动
                AlguiLoading.start(MainActivity.this); //原神，启动！
            }
        });

        btnCloseWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlguiService.class);
                //关闭服务
                context.stopService(intent);
                // 关闭当前Activity
                finish();
                // 杀死进程
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
        });
        IntentFilter filter = new IntentFilter("com.irene.algui.ACTION_CLOSE");
        registerReceiver(closeReceiver, filter);
    }

    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish(); // 关闭当前 Activity
        }
    };

    // 添加检查电池优化的方法
    private boolean isIgnoringBatteryOptimizations(String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return powerManager.isIgnoringBatteryOptimizations(packageName);
        }
        return true;
    }

    // 添加获取包URI的方法
    private android.net.Uri getPackageURI() {
        return android.net.Uri.parse("package:" + getPackageName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeReceiver);
    }

}
