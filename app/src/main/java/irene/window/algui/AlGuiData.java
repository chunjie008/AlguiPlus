package irene.window.algui;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import android.view.WindowManager;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/23 16:31
 * @Describe Algui全局数据
 */
public class AlGuiData {

    public static final String TAG = "AlGuiData";
    private static boolean isLiveStream=false;//是否为直播模式
    //获取直播模式标识
    public static boolean getIsLiveStream() {
        return isLiveStream;
    }
    //设置直播模式标识
    public static void setIsLiveStream(Context c, boolean b) {
        isLiveStream = b;
        if (b) {
            //悬浮球窗口
            AlGui.GUI(c).getBallWindowParams().flags = getLiveStreamFlags() |//直播模式flag
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            AlGui.GUI(c).updateBall();

            //悬浮菜单窗口
            AlGui.GUI(c).getMenuWindowParams().flags = 
                getLiveStreamFlags() |//直播模式flag
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | 
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                AlGui.GUI(c).updateMenu();

            //气泡通知窗口
            AlGuiBubbleNotification.Inform(c).getWindowManageLayoutParams().flags =
                AlGuiData.getLiveStreamFlags() |//直播模式flag
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
                ;
            AlGuiBubbleNotification.Inform(c).updateW();
        } else {
            //悬浮球窗口
            AlGui.GUI(c).getBallWindowParams().flags = 
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            AlGui.GUI(c).updateBall();

            //悬浮菜单窗口
            AlGui.GUI(c).getMenuWindowParams().flags = 
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | 
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            AlGui.GUI(c).updateMenu();

            //气泡通知窗口
            AlGuiBubbleNotification.Inform(c).getWindowManageLayoutParams().flags =
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |//硬件加速
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
                ;
            AlGuiBubbleNotification.Inform(c).updateW();
        }
    }
    //直播模式flag
    public static int liveStreamFlags;
    //设置直播模式标识
    public static int getLiveStreamFlags() {
        if (getIsLiveStream()) {
            liveStreamFlags =
                //  WindowManager.LayoutParams.FLAG_SECURE |//防截屏
                WindowManager.LayoutParams.FLAG_DITHER //抖动(防录屏)
                |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;//硬件加速
                
        } else {
            liveStreamFlags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;//硬件加速
        }

        return liveStreamFlags;
    }



    //protected static boolean isALUIcreate=false;//是否创建了ALGUI悬浮窗 防止横竖屏切换时二次创建
    //protected static boolean isBallHide=false;//悬浮球是否被隐藏

    //存储数据 使用教程：
    //下面使用保存颜色数据演示 (保存其他数据同理)

    //0. 获取应用程序自身目录名为 MenuColorData 的xml配置信息文件 (后续使用时如果不存在此文件则自动创建)
    //SharedPreferences menuColorSP = context.getSharedPreferences("MenuColorData", context.MODE_PRIVATE);
    //创建一个配置文件编辑器 menuColorSPED 用于编辑MenuColorData文件的编辑器
    //SharedPreferences.Editor menuColorSPED = menuColorSP.edit();

    //1. 键值对形式存储数据
    //在 MenuColorData 文件中写入
    //键名为 "背景颜色"，值为 0xFF9575CD 的数据
    //menuColorSPED.putInt("背景颜色", 0xFF9575CD); 
    //提交保存编辑操作，使修改生效 注意，这里使用 apply() 方法而不是 commit() 方法
    //因为 apply() 方法会异步执行，而不会阻塞主线程，从而提高了应用程序的响应速度。
    //colorEditor.apply();

    //2. 键值对形式获取存储的数据
    //从存储颜色数据的 MenuColorData 文件中读取键名为 "背景颜色" 的颜色数据
    //如果没有找到对应的值，则返回默认值 0
    //int color = preferences.getInt("背景颜色", 0);

    //menuColorSP用于保存悬浮菜单外观颜色配置数据至本地文件
    private static SharedPreferences menuColorSP;
    public static SharedPreferences getMenuColorSP(Context context) {
        if (context == null) {
            return null;
        }
        if (menuColorSP == null) {
            /*
             参数1：文件名
             参数2：访问模式
             MODE_PRIVATE：表示只允许当前应用程序访问该 SharedPreferences 文件，其他应用程序无法访问。这是最常见和推荐的访问模式，确保储存在其中的数据对于其他应用程序来说是私有的。
             MODE_WORLD_READABLE：已被废弃，不推荐使用。曾经允许其他应用程序读取该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_WORLD_WRITEABLE：已被废弃，不推荐使用。曾经允许其他应用程序写入该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_MULTI_PROCESS：已被废弃，不推荐使用。曾经允许多个进程同时访问 SharedPreferences 文件，但在 Android 7.0（API 级别 24）之后，SharedPreferences 已经自动支持多进程同步，不再需要显式地设置这个访问模式。
             通常情况下，我们会使用 MODE_PRIVATE 来确保 SharedPreferences 文件中的数据对于其他应用程序来说是私有的，从而保护用户数据的安全性。
             需要注意的是，这些访问模式只适用于 getSharedPreferences() 方法创建的 SharedPreferences 对象。如果使用其他方法（如 PreferenceManager.getDefaultSharedPreferences()）来获取 SharedPreferences 对象，则默认使用 MODE_PRIVATE 访问模式。
             */
            menuColorSP = context.getSharedPreferences("MenuColorData", context.MODE_PRIVATE);
        }
        return menuColorSP;
    }
    //menuColorSPED用于编辑悬浮菜单外观颜色配置数据文件的编辑器
    private static SharedPreferences.Editor menuColorSPED;
    public static SharedPreferences.Editor getMenuColorSPED(Context context) {
        if (context == null) {
            return null;
        }
        if (menuColorSPED == null) {
            menuColorSPED = getMenuColorSP(context).edit();
        }
        return menuColorSPED;
    }
    //菜单视图外观颜色的映射本地文件键名和默认数据 用于统一管理本地键名和默认颜色数据
    private static HashMap<String, Object> MenuColorData;
    //默认颜色配置
    public static int rootLayoutBackColor = 0xFF303030;//根布局背景颜色默认数据
    public static int rootLayoutStrokeColor = 0xFF424242;//根布局描边颜色默认数据
    public static int menuTopLineColor = 0xFFBDBDBD;//菜单顶部线条颜色默认数据
    public static int menuMainTitleTextColor = 0xFFFFFFFF;//菜单主标题文本颜色默认数据
    public static int menuSubTitleTextColor = 0x60FFFFFF;//菜单副标题文本颜色默认数据
    public static int menuLiveStreamIconColor = 0xFFBDBDBD;//菜单直播模式图标颜色默认数据
    public static int menuExplanationBackColor = 0x60FFFFFF;//菜单说明背景颜色默认数据
    public static int menuExplanationTextColor = 0xFFFFFFFF;//菜单说明文本颜色默认数据
    public static int menuScrollBackColor = 0xFF212121;//菜单滚动列表背景颜色默认数据
    public static int menuBottLeftButtonTextColor = 0xFFFFFFFF;//菜单左下角按钮文本颜色默认数据
    public static int menuBottRightButtonTextColor = 0xFFFFFFFF;//菜单右下角按钮文本颜色默认数据
    public static int menuBottRightTriangleColor = 0xFF424242;//菜单右下角三角形颜色默认数据

    public static HashMap getMenuColorData() {

        MenuColorData = new HashMap<String, Object>() {{
                //第一个参数是键 不要改！第二个参数是值对应键
                //虽然值是Object 但是 键名的值：必须String类型 默认数据的值必须是int类型颜色值
                //如果不听上面我说的那么必将引发灾难异常
                put("根布局背景颜色键名", "rootLayoutBackColor");//键名：rootLayoutBackColor
                put("根布局背景颜色默认数据", rootLayoutBackColor);//默认颜色数据：0xFF303030

                put("根布局描边颜色键名", "rootLayoutStrokeColor");
                put("根布局描边颜色默认数据", rootLayoutStrokeColor);

                put("菜单顶部线条颜色键名", "menuTopLineColor");
                put("菜单顶部线条颜色默认数据", menuTopLineColor);

                put("菜单主标题文本颜色键名", "menuMainTitleTextColor");
                put("菜单主标题文本颜色默认数据", menuMainTitleTextColor);

                put("菜单副标题文本颜色键名", "menuSubTitleTextColor");
                put("菜单副标题文本颜色默认数据", menuSubTitleTextColor);
                
                put("菜单直播模式图标颜色键名", "menuLiveStreamIconColor");
                put("菜单直播模式图标颜色默认数据", menuLiveStreamIconColor);

                put("菜单说明背景颜色键名", "menuExplanationBackColor");
                put("菜单说明背景颜色默认数据", menuExplanationBackColor);

                put("菜单说明文本颜色键名", "menuExplanationTextColor");
                put("菜单说明文本颜色默认数据", menuExplanationTextColor);

                put("菜单滚动列表背景颜色键名", "menuScrollBackColor");
                put("菜单滚动列表背景颜色默认数据", menuScrollBackColor);

                put("菜单左下角按钮文本颜色键名", "menuBottLeftButtonTextColor");
                put("菜单左下角按钮文本颜色默认数据", menuBottLeftButtonTextColor);

                put("菜单右下角按钮文本颜色键名", "menuBottRightButtonTextColor");
                put("菜单右下角按钮文本颜色默认数据", menuBottRightButtonTextColor);

                put("菜单右下角三角形颜色键名", "menuBottRightTriangleColor");
                put("菜单右下角三角形颜色默认数据", menuBottRightTriangleColor);

            }};


        return MenuColorData;
    }


    //menuAttributeSP用于保存悬浮菜单属性配置数据至本地文件
    private static SharedPreferences menuAttributeSP;
    public static SharedPreferences getMenuAttributeSP(Context context) {
        if (context == null) {
            return null;
        }
        if (menuAttributeSP == null) {
            /*
             参数1：文件名
             参数2：访问模式
             MODE_PRIVATE：表示只允许当前应用程序访问该 SharedPreferences 文件，其他应用程序无法访问。这是最常见和推荐的访问模式，确保储存在其中的数据对于其他应用程序来说是私有的。
             MODE_WORLD_READABLE：已被废弃，不推荐使用。曾经允许其他应用程序读取该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_WORLD_WRITEABLE：已被废弃，不推荐使用。曾经允许其他应用程序写入该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_MULTI_PROCESS：已被废弃，不推荐使用。曾经允许多个进程同时访问 SharedPreferences 文件，但在 Android 7.0（API 级别 24）之后，SharedPreferences 已经自动支持多进程同步，不再需要显式地设置这个访问模式。
             通常情况下，我们会使用 MODE_PRIVATE 来确保 SharedPreferences 文件中的数据对于其他应用程序来说是私有的，从而保护用户数据的安全性。
             需要注意的是，这些访问模式只适用于 getSharedPreferences() 方法创建的 SharedPreferences 对象。如果使用其他方法（如 PreferenceManager.getDefaultSharedPreferences()）来获取 SharedPreferences 对象，则默认使用 MODE_PRIVATE 访问模式。
             */
            menuAttributeSP = context.getSharedPreferences("MenuAttributeData", context.MODE_PRIVATE);
        }
        return menuAttributeSP;
    }
    //menuAttributeSPED用于编辑悬浮菜单属性配置数据文件的编辑器
    private static SharedPreferences.Editor menuAttributeSPED;
    public static SharedPreferences.Editor getMenuAttributeSPED(Context context) {
        if (context == null) {
            return null;
        }
        if (menuAttributeSPED == null) {
            menuAttributeSPED = getMenuAttributeSP(context).edit();
        }
        return menuAttributeSPED;
    }
    //悬浮菜单属性的映射本地文件键名和默认数据 用于统一管理本地键名和默认属性数据
    private static HashMap<String, Object> MenuAttributeData;
    //默认属性配置
    public static float rootLayoutFilletRadius = 10.9f;//根布局圆角半径默认数据
    public static float rootLayoutStrokeWidth = 0.4f;//根布局描边宽度默认数据
    public static float menuTopLineFilletRadius = 20f;//菜单顶部线条圆角半径默认数据
    public static float menuTransparency = 1f;//菜单透明度默认数据
    public static float menuScrollWidth = 809f;//菜单滚动列表宽度默认数据
    public static float menuScrollHeight = 554f;//菜单滚动列表高度默认数据
    public static HashMap getMenuAttributeData() {

        MenuAttributeData = new HashMap<String, Object>() {{
                //第一个参数是键 不要改！第二个参数是值对应键
                //虽然值是Object 但是 键名的值：必须String类型 默认数据的值必须是float类型 (除非可以隐式转换)
                //如果不听上面我说的那么必将引发灾难异常
                put("根布局圆角半径键名", "rootLayoutFilletRadius");
                put("根布局圆角半径默认数据", rootLayoutFilletRadius);

                put("根布局描边宽度键名", "rootLayoutStrokeWidth");
                put("根布局描边宽度默认数据", rootLayoutStrokeWidth);

                put("菜单顶部线条圆角半径键名", "menuTopLineFilletRadius");
                put("菜单顶部线条圆角半径默认数据", menuTopLineFilletRadius);

                put("菜单透明度键名", "menuTransparency");
                put("菜单透明度默认数据", menuTransparency);

                put("菜单滚动列表宽度键名", "menuScrollWidth");
                put("菜单滚动列表宽度默认数据", menuScrollWidth);

                put("菜单滚动列表高度键名", "menuScrollHeight");
                put("菜单滚动列表高度默认数据", menuScrollHeight);         

            }};


        return MenuAttributeData;
    }

    //diaLogFlagSP用于保存弹窗的标识至本地文件 (比如某个弹窗不再提示)
    private static SharedPreferences diaLogFlagSP;
    public static SharedPreferences getDiaLogFlagSP(Context context) {
        if (context == null) {
            return null;
        }
        if (diaLogFlagSP == null) {
            /*
             参数1：文件名
             参数2：访问模式
             MODE_PRIVATE：表示只允许当前应用程序访问该 SharedPreferences 文件，其他应用程序无法访问。这是最常见和推荐的访问模式，确保储存在其中的数据对于其他应用程序来说是私有的。
             MODE_WORLD_READABLE：已被废弃，不推荐使用。曾经允许其他应用程序读取该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_WORLD_WRITEABLE：已被废弃，不推荐使用。曾经允许其他应用程序写入该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_MULTI_PROCESS：已被废弃，不推荐使用。曾经允许多个进程同时访问 SharedPreferences 文件，但在 Android 7.0（API 级别 24）之后，SharedPreferences 已经自动支持多进程同步，不再需要显式地设置这个访问模式。
             通常情况下，我们会使用 MODE_PRIVATE 来确保 SharedPreferences 文件中的数据对于其他应用程序来说是私有的，从而保护用户数据的安全性。
             需要注意的是，这些访问模式只适用于 getSharedPreferences() 方法创建的 SharedPreferences 对象。如果使用其他方法（如 PreferenceManager.getDefaultSharedPreferences()）来获取 SharedPreferences 对象，则默认使用 MODE_PRIVATE 访问模式。
             */
            diaLogFlagSP = context.getSharedPreferences("DiaLogFlagData", context.MODE_PRIVATE);
        }
        return diaLogFlagSP;
    }
    //diaLogFlagSPED用于编辑弹窗标识数据文件的编辑器
    private static SharedPreferences.Editor diaLogFlagSPED;
    public static SharedPreferences.Editor getDiaLogFlagSPED(Context context) {
        if (context == null) {
            return null;
        }
        if (diaLogFlagSPED == null) {
            diaLogFlagSPED = getDiaLogFlagSP(context).edit();
        }
        return diaLogFlagSPED;
    }
    //弹窗标识的映射本地文件键名和默认数据 用于统一管理本地键名和默认属性数据
    private static HashMap<String, Object> DiaLogFlagData = new HashMap<String, Object>() {{
            //第一个参数是键 不要改！第二个参数是值对应键
            //虽然值是Object 但是 键名的值：必须String类型 默认数据的值必须是boolean类型
            //如果不听上面我说的那么必将引发灾难异常
            put("悬浮窗隐藏弹窗不再提示键名", "hideMenuDiaLogBZTS");
            put("悬浮窗隐藏弹窗不再提示默认数据", false);

            put("悬浮窗退出弹窗不再提示键名", "exitMenuDiaLogBZTS");
            put("悬浮窗退出弹窗不再提示默认数据", false);

        }};
    public static HashMap getDiaLogFlagData() {
        return DiaLogFlagData;
    }







    //游戏准星
    //用于保存参数至本地文件 (比如某个弹窗不再提示)
    private static SharedPreferences GameFrontSightSP;
    public static SharedPreferences getGameFrontSightSP(Context context) {
        if (context == null) {
            return null;
        }
        if (GameFrontSightSP == null) {
            /*
             参数1：文件名
             参数2：访问模式
             MODE_PRIVATE：表示只允许当前应用程序访问该 SharedPreferences 文件，其他应用程序无法访问。这是最常见和推荐的访问模式，确保储存在其中的数据对于其他应用程序来说是私有的。
             MODE_WORLD_READABLE：已被废弃，不推荐使用。曾经允许其他应用程序读取该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_WORLD_WRITEABLE：已被废弃，不推荐使用。曾经允许其他应用程序写入该文件中的数据，但由于安全风险，Android 4.2（API 级别 17）之后不再支持。
             MODE_MULTI_PROCESS：已被废弃，不推荐使用。曾经允许多个进程同时访问 SharedPreferences 文件，但在 Android 7.0（API 级别 24）之后，SharedPreferences 已经自动支持多进程同步，不再需要显式地设置这个访问模式。
             通常情况下，我们会使用 MODE_PRIVATE 来确保 SharedPreferences 文件中的数据对于其他应用程序来说是私有的，从而保护用户数据的安全性。
             需要注意的是，这些访问模式只适用于 getSharedPreferences() 方法创建的 SharedPreferences 对象。如果使用其他方法（如 PreferenceManager.getDefaultSharedPreferences()）来获取 SharedPreferences 对象，则默认使用 MODE_PRIVATE 访问模式。
             */
            GameFrontSightSP = context.getSharedPreferences("GameFrontSightData", context.MODE_PRIVATE);
        }
        return GameFrontSightSP;
    }
    //用于编辑参数数据文件的编辑器
    private static SharedPreferences.Editor GameFrontSightSPED;
    public static SharedPreferences.Editor getGameFrontSightSPED(Context context) {
        if (context == null) {
            return null;
        }
        if (GameFrontSightSPED == null) {
            GameFrontSightSPED = getGameFrontSightSP(context).edit();
        }
        return GameFrontSightSPED;
    }
    //映射本地文件键名和默认参数数据 用于统一管理本地文件键名和默认参数数据
    private static HashMap<String, Object> GameFrontSightData = new HashMap<String, Object>() {{
            //第一个参数是键 不要改！第二个参数是值对应键
            //虽然值是Object 但是 键名的值：必须String类型 默认数据的值必须是boolean类型
            //如果不听上面我说的那么必将引发灾难异常
            // put("游戏准星开启状态键名", "OnStatus");
            // put("游戏准星开启状态默认数据", false);

            put("游戏准星样式键名", "Style");
            put("游戏准星样式默认数据", "╋");

            put("游戏准星颜色键名", "Color");
            put("游戏准星颜色默认数据", 0xFF4CAF50);

            put("游戏准星大小键名", "Size");
            put("游戏准星大小默认数据", 15f);

            put("游戏准星透明度键名", "Transparency");
            put("游戏准星透明度默认数据", 1f);


            put("游戏准星X偏移键名", "X_Offset");
            put("游戏准星X偏移默认数据", 0);

            put("游戏准星Y偏移键名", "Y_Offset");
            put("游戏准星Y偏移默认数据", 0);


        }};
    public static HashMap getGameFrontSightData() {
        return GameFrontSightData;
    }





    //艾琳GUI 所有视图ID
    public enum AlguiView {
        Collapse(99),//折叠菜单
        Switch(100),//开关按钮
        SmallButton(101),//小按钮
        Button(102),//普通按钮
        EditText(103),//输入框
        SeekBarInt(104),//整数拖动条(拖动int值)
        SeekBarFloat(105),//小数拖动条(拖动float值)
        CheckBox(106),//复选框
        WebView(107),//web视图(完全支持html)
        TextView(108),//文本视图
        MarqueeTextView(109),//字幕文本视图(可滚动)
        Line(110),//线
        WebSite(111),//web网站视图
        ;

        private final int id;

        private AlguiView(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }


    //艾琳GUI 所有形式通知的ID
    public enum AlguiNotification {
        MessageNotification(1000),//消息通知
        ButtonNotification(1001);//可交互的带按钮的通知

        private final int id;

        private AlguiNotification(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }





    //下面是 Base64形式存储的图像资源
    /*
     注意：
     由于base64编码太大 为了防止使用AIDE打开此文件直接看见一大串编码 导致AIDE崩溃
     所以 我将图像的base64编码 放在了最下面 你需要往下滑才能看见它们！
     请使用MT管理器查看下面base64编码！否则您的AIDE将崩溃，就算重启AIDE你也会卡在启动界面!
     */
























//视频图标
    //视频_直播_开始
    private static String video_Icon_LiveStart = "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";
    public static String getVideo_Icon_LiveStart() {
        return video_Icon_LiveStart;
    }

    //视频_直播_结束
    private static String video_Icon_LiveEnd = "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";
    public static String getVideo_Icon_LiveEnd() {
        return video_Icon_LiveEnd;
    }


   











//精美通知的图标素材

    //精美通知-消息的图标🔔
    private static String exquisiteNotice_Icon_Message="iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAAAXNSR0IArs4c6QAABoZJREFUeF7tnV2SGzcMhMcnW+dkm5zMzsmSQlZMsWRZg8YPCYCYF+8DZ4ZEf2iCFGV9u868vl/X9fkYOv3987quv6/r+vO0cHw7bcAPkYf4z8P/6zQITgOAMvx34g8YjoLgNAD+YTreMXE5ZqAP4bkA/PGoC5i85G12EgBU7P1gSnXMNNAAvCaiAWBmSqZm7QAv1GoHaAfIlMSqvrYDtAN0EfjMwElTAGcTaMSHtoapEKR/S19VASC7p2ve75cKOT4noPvp71JQVAJgfMAzxJcKzrmP3IGu9B8eVQCARPi4rmuF8K/gSL1nkBWAldnOcQRqk9IVsgEQUfhnQEbxmKJWyAQAUsVzs9azXYqpIQsA9CHOrjleA0l4CKIDgOzeaYTyvjcsCJEByGb5dxCFhCAqAFkt/w4CKgzpsEmYKyIAHuKPipxO/s7V+fz3qDHmWsNjfyEUBNEAsBTfcj9/7PjdHSjlZnYYCCIBYDHnW4r+OzGtYAgBQRQAtOKvEP4ZCJoq5i+YcLN/brcdgggAaMXfXV1r+78Vgt0AaIK3I+vfZblmLNsg3gmAZpNnW8BufF4DwZbvIuwEQFrxRxV/sCGFYMtUsAsAaZCiiz8gkLrb8vHtAEAanC0WKSntH/ekGOcOACTWn018rRMs02XZi6YzdOhu2nJbVGT9q1slTrCsHlgNAPfbuSOQ2cXXFIZLtFnyEkX2r+yfceL/8jh06lsC/8oAn5r9mnrAXR/3Fwizfwn93in/4vno8te9+I0KwKp+bWDgv+8ncs83uheDKwKNVsFVs186Fbi6wAoAUNtb0acdmT+/M4wLrAg2UvxVz/5wLuANAGr/3v3Znfnz+5HEcJsGvAOO2P8p2S/ZHHIrBr0BQOa60wBA3DEtAIjNecMYyf5HX5AEcZkGPIPe9n+PXAPwiNFp9i9ZDbhMA54OgNDt2Y/7PNzbYus06Rn4rQPbqyn0diRRzOuACACcav+S5WAaAJAlzukAILFqACBzzdG4JADIEtCc6hy6/99LBABzt/SqARoAjEJuwVwSAC8IMQn2tuYCYL4X4BV8xAG8+rBXUuzt5QBA1rYNwNdvGXGOiaVxAO6AKE8agAYA88uCrbkJ0w5QUHwaUgNQVFjusBoAbqSKtmsAigrLHVYDwI1U0XYNQFFhucNqALiRKtquASgqLHdYDQA3UkXbNQBFheUOqwHgRqpouwagqLDcYTUA3EgVbdcAFBWWO6wGgBupou24ANDwTc9PmD5sEmfbEaekgDQASYWz6vaxAJgfc7ZSZPFzkEO0pt+j2D0FNABfpCFfDgkPAEJzA9AAXON3+Ba7bqjXIQ5gejDUYwrYVtCEkhTvDHflZLoU9ABgy0DweIe7A0kcszrAGoCe/+VcNQDy2JW4c0sdYO0Abf86FpH4mUwDlgAg9m9ayOhiHupuZBowWQ1YAoDQ2+v/19wh0wA9Qe0CVgB09tsZCZJIahewAAAVv7P/PSzINKB2AQsAEGKpww3AewDQaUBVT2kBQLNfbVl2Thv6SagLiOOqAQAVv7Ofz5zEBUTOKgVA0kGVVfFjV6Yl6gKiekAKgKRzIkLLyIkPRJpk0NJQAkBbPy6m9A5JrKF6AAVAQiXUIWmkCt8ncVu2C6AASIhkd6awiJqhuSYdCkCv+TVSyu+VJB5LW1ajR78lJFLh15dNBD7Bx7Cc1xsAsM/d3DACrFVXA2AY8WCPagcIJsjq7pgDQAOQLElWD7zf9xUBlruzGk0RbQBy4MWa/9mUTGOWrARyhKxWL90AoDBJ1qS1wht7NGzxJQ4wht4QxIQA3nZHa4B52A1BLAigzB9d1wAwu8EH8zdvYoUsf28o4+ki8cff0KgsAIBeqGyMrEJgO7zpG+p4KWKbopOKVYjIFl+AgIpv9V5lvtzfXh2AYY+a/4MAFZ/eydqFu5fHv0U2ACgiyDQwIkjTATpP0p4HfQLH+T2/WSnrqceVgowAaDajBgTvCiZ6/hBfEvw09q/ZB5AExvIeiS0/v//5rAKtZOhCM35+birxMwMgnQosIXz1rHSOmq7DihWBt/jpsj+7Awy7pqJw95VS/AoA0Bgs6gENQGnFrwLATghSi18JgB3TQXrxqwEwbNx7SpBsKmmmGNd7M68C3gVmbP2iZ+nfPbOU8GOgVQEY49Pu6tFzSgp/CgBzRg9XuDu7MH/GPgBwteGdD6/uAJzYjq1f0YEKzgsit2kAIquzoG//Aj3ghpBZuiE3AAAAAElFTkSuQmCC";
    public static String getExquisiteNotice_Icon_Message() {
        return exquisiteNotice_Icon_Message;
    }

    //精美通知-成功的图标✓
    private static String exquisiteNotice_Icon_Success="iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAAAXNSR0IArs4c6QAABFpJREFUeF7tnWF22yAQBsXNmpO1OVlzM/rkWi+ua2tZWAnQN/4ZI8TujFZAIiUtfKQzkKSjJ/gFAcQlQAAEEM+AePhUAAQQz4B4+FQABBDPgHj4VAAEEM+AePhUAAQQz4B4+FQABBDPgHj4VAAEEM+AePhUAAQQz4B4+FQABBDPgHj4VAAEEM+AePhUAAQQz4B4+FQABBDPwMTh55x/rMNPKX3VhkEFqM1cx+Nyzr+WZfn5NITPlNL6c9cHAVzp6t845/x7WZbblf/i85VS+vCMEgE82erc1oC/jc5VCRCgM9TS0xfCv3WXUirmWtywdKC0i8+AB/797B+lE0MEiOcV2mMF/PX8CBBKoVNnlfC5BXTiFXraWvjLsrhWAtwCQrHFdNYA33X13yaMMUOml6gMtMD33Pu38SJAFLmAfs6GTwUIgBbRxX1Pf93hq/0Uz/qfT0AFqE150HE94VMBgiDWdtMbPgLUkgs4bgT4CBAAsqaLUeAjQA29xmNGgo8AjTC9h48GHwG8BBvajwgfARqAeg4dFT4CeChWth0ZPgJUQi09bHT4CFBKsqLdDPARoAJsySGzwEeAEprONjPBRwAnXKv5bPARwCLq+H5G+AjgALzXdFb4CBAgwMzwEaBRgNnhI0CDAFeA3yzA9nz6raOGZ9QbOHQ59CrwqwV483y664GELuQCTnol+FUC5Jyzkcfqv1AN4HNoF1eD7xbA8Xfrl5PgivBrBLCu/scr8DISXBW+S4DKJEwvQWXc01wIxQ+GNCRiWgkaYt4EGD72YgHWiAomgO8mYcMn4nngCvBdt4C7AK9eT1Y6855GAhX4bgHuEuy9psySYXgJlOBXCXBlCdTgVwtwRQkU4TcJcCUJVOE3C3AFCZThhwgwswTq8MMEmFEC4P9dsLk2gqw1nuOXRa+6Om2JCPzv9IcKMEMlAP6/1164ACNLAPz/C+8hAowoAfBf38APE2AkCYD/fvZ2qAAjSAD8/an74QL0lAD41roteBm4d7qzl4jAt+GH7wNYpzxLAuBbJA7cB7BOfbQEwLcInLAPYA3hKAmAb2X+xH0AayjREgDfyniHfQBrSFESAN/KdMd9AGtorRLc++/yzxas2Gb4/pR9ACsRjRJY3e99f9pvIFsGeeSxQwgQsFlUkyN5+KfvA1iUTqwEwL/DGKYCbHKcIAHwH67E4QQ4+HYA/KcyPKQAB0kA/Bf34GEFCJYA+G8mYEMLECQB8Hdm38ML0CgB8I2l1xQCVEoAfGvdHf1cQMH5mpo4lojAL8z0NBXAsU8A/EL4w+0Elo77zYsqP1NK6xtM+DgyMF0FeIxte1Wt0mtqHWyLmk4tQFGENNrNAAKIC4IACCCeAfHwqQAIIJ4B8fCpAAggngHx8KkACCCeAfHwqQAIIJ4B8fCpAAggngHx8KkACCCeAfHwqQAIIJ4B8fCpAAggngHx8KkACCCeAfHwqQAIIJ4B8fCpAAggngHx8P8A2akIn8YbW/UAAAAASUVORK5CYII=";
    public static String getExquisiteNotice_Icon_Success() {
        return exquisiteNotice_Icon_Success;
    }

    //精美通知-错误的图标x
    private static String exquisiteNotice_Icon_Mistake="iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAAAXNSR0IArs4c6QAAA+pJREFUeF7t3VtSwkAQBdDMznBl6spkZ7FiJVUUJTAkk+6+D370IyTTfQ/NoKBt8k26A026ehc/GYA4AgMwAPEOiJfvCWAA4h0QL98TwADEOyBevieAAYh3QLx8TwADEO+AePmeAAYg3gHx8j0BDEC8A+LlewIYgHgHxMv3BDAA8Q6Il+8JYADiHRAv3xPAAMQ7IF6+J4ABiHdAvPz0CTDP89c0TdfW2lUpi3meL9M0XVprS/1pt1QAa/ifa/UfKgjW8H/Wur8zEaQBuAt/ewTQI7gLf6s7DUEKgAfh0yN4EH4qgnAAL8KnRfAi/DQEoQA6w6dD0Bl+CoJoAMvOd9v89Ox84fcEb4a/9CS05lAAS3XVG9KjsvcYhFrDAaggQAh/ySIFADsClPBTAbAiQAo/HQAbArTwSwBgQYAYfhkA6AhQwy8FABUBcvjlAKAhQA+/JAAUBAzhlwVQHQFL+KUBVEXAFH55ANUQsIUPAaAKAsbwYQBkI2ANHwpAFgLm8OEARCNgDx8SQBQChfBhAZyNQCV8aABnIVAKHx7AaARq4VMAGIVAMXwaAEcRqIZPBWAvguV+ap9VWGv++5L2ruDbRYz8fsej+Z3Lh35o452F7T2WDsDOSdDTP7rwKSfAluTgSUAZPjWAgZOANnx6AAMQUIcvAeAAAvrwDeD51s8AerbG1Y85uBmkR0D5MnDwKwFqBLQADj7y7wcbLQJKAIPD3zBQIqADcFL4tAioAOwI/8O/DKq+je9c357wtz9Ne+S+ncsrexjFBBgR4IhzlE35ycLgAYwMbuS5UDBAAzgjsDPOWRkDLIAzgzrz3NUwQAKICCjiGhUwwAGIDCbyWlkYoABkBJJxzUgMMAAyg8i89tkYIABUCKDCGs7AUB5ApcZXWssoDKUBVGx4xTUdwVAWQOVGV17buxhKAkBoMMIaezCUA4DUWKS1PsJQCgBiQxHXfIuhDADkRiKvvQQA5AYeeAdyifcYpgNgCB8ZQSoApvBREaQBYAwfEUEKAObw0RCEA1AIHwlBKACl8FEQRAP4mqbps+dHlNH/RbtzTbsOexP+d2tt6VPILRTAUtE8zz0ISrxGHplAJ4LQ8Jf6wgF0IKALv/PpIDz8NABPENCG/wJBSvipAP5BQB/+AwRp4acDuEFw3T6oOfJ5t/K51j3BJXLD918/UvYAlYNRW5sBqCV+V68BGIB4B8TL9wQwAPEOiJfvCWAA4h0QL98TwADEOyBevieAAYh3QLx8TwADEO+AePmeAAYg3gHx8j0BDEC8A+LlewIYgHgHxMv3BDAA8Q6Il+8JYADiHRAv3xPAAMQ7IF6+J4A4gF8nwp6fTforwgAAAABJRU5ErkJggg==";
    public static String getExquisiteNotice_Icon_Mistake() {
        return exquisiteNotice_Icon_Mistake;
    }

    //精美通知-警告的图标⚠️
    private static String exquisiteNotice_Icon_Alert="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";
    public static String getExquisiteNotice_Icon_Alert() {
        return exquisiteNotice_Icon_Alert;
    }











    //简约通知的图标素材

    //简约通知-消息的图标i
    private static String simplicityNotice_Icon_Message="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";
    public static String getSimplicityNotice_Icon_Message() {
        return simplicityNotice_Icon_Message;
    }

    //简约通知-成功的图标✔️
    private static String simplicityNotice_Icon_Success="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";
    public static String getSimplicityNotice_Icon_Success() {
        return simplicityNotice_Icon_Success;
    }

    //简约通知-错误的图标！
    private static String simplicityNotice_Icon_Mistake="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";
    public static String getSimplicityNotice_Icon_Mistake() {
        return simplicityNotice_Icon_Mistake;
    }

    //简约通知-警告的图标⚠️
    private static String simplicityNotice_Icon_Alert="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";
    public static String getSimplicityNotice_Icon_Alert() {
        return simplicityNotice_Icon_Alert;
    }



//其他图标素材

    //黑客图标1
    private static String other_Icon_Hacker1="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";
    public static String getOther_Icon_Hacker1() {
        return other_Icon_Hacker1;
    }
    //黑客图标2
    private static String other_Icon_Hacker2="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";
    public static String getOther_Icon_Hacker2() {
        return other_Icon_Hacker2;
    }

}
