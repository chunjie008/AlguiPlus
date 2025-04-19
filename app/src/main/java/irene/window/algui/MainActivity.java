package irene.window.algui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.pm.ActivityInfo;

import xyz.eight.R;


public class MainActivity extends Activity {
    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 将屏幕方向设置为横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        //你可以在ALGUI打包安装后 反编译安装包 然后在Dex文件中找到这串代码编译后的Smali代码，
        //你可以复制编译后的Smali代码并粘贴进任意软件的Dex文件中MainActivity类中入口方法onCreate的Smali代码当中，
        //这样就相当于你成功将ALGUI嵌入进了其软件的内部，但是仅供娱乐最好不要破坏软件，后果自负
        //它的Smali代码：
        //invoke-static {p0}, Lcom/irene/algui/AlguiLoading;->start(Landroid/content/Context;)V
        AlguiLoading.start(this);
    }


  

}
