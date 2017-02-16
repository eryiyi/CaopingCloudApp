package com.caoping.cloud;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.huanxin.DemoHelper;
import com.google.gson.Gson;
import com.hyphenate.easeui.controller.EaseUI;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhl on 2016/7/1.
 */
public class CaopingCloudApplication extends Application {
    // 运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    private ExecutorService lxThread;
    private Gson gson;
    private RequestQueue requestQueue;
    private SharedPreferences sp;

    private static CaopingCloudApplication application;

    public static String latStr;
    public static String lngStr;
    public static String locationAddress;

    public static String locationProvinceName;
    public static String locationCityName;
    public static String locationAreaName;


    // login user name
    public final String PREF_USERNAME = "username";

    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        requestQueue = Volley.newRequestQueue(this);
        gson = new Gson();
        lxThread = Executors.newFixedThreadPool(20);
        sp = getSharedPreferences("cpcloud_manage", Context.MODE_PRIVATE);
        imageLoader = new com.android.volley.toolbox.ImageLoader(requestQueue, new BitmapCache());
        initImageLoader(this);

        //init demo helper
        DemoHelper.getInstance().init(application);
        //red packet code : 初始化红包上下文，开启日志输出开关
//        RedPacket.getInstance().initContext(applicationContext);
//        RedPacket.getInstance().setDebugMode(true);
        //end of red packet code

        //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin(InternetURL.WEIXIN_APPID, InternetURL.WEIXIN_SECRET);

        PlatformConfig.setQQZone("1105644219", "G0cagmHEkzXdMs0e");

        EaseUI.getInstance().init(this, null);


        UMShareAPI.get(this);
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        Config.DEBUG = true;
        //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
//        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        //豆瓣RENREN平台目前只能在服务器端配置
        //新浪微博
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad");
//        Config.REDIRECT_URL="http://sns.whalecloud.com/sina2/callback";
        //易信
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
        PlatformConfig.setAlipay("2088421395960317");
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
        PlatformConfig.setPinterest("1439206");

    }


    /**
     * 获取自定义线程池
     *
     * @return
     */
    public ExecutorService getLxThread() {
        if (lxThread == null) {
            lxThread = Executors.newFixedThreadPool(20);
        }
        return lxThread;
    }

    /**
     * 获取Gson
     *
     * @return
     */
    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * 获取Volley请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);

        }
        return requestQueue;
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public SharedPreferences getSp() {
        if (sp == null) {
            sp = getSharedPreferences("cpcloud_manage", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static DisplayImageOptions options;
    public static DisplayImageOptions txOptions;//头像图片

    public CaopingCloudApplication() {

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_pic)
                .showImageForEmptyUri(R.drawable.no_pic)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.no_pic)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                           // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                             // 设置下载的图片是否缓存在内存卡中
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)          //图片的解码类型
//                .displayer(new RoundedBitmapDisplayer(5))
                .build();                                       // 创建配置过得DisplayImageOption对象

        txOptions = new DisplayImageOptions.Builder()//头像
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                           // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                             // 设置下载的图片是否缓存在内存卡中
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)          //图片的解码类型头像
                .build();
    }

    /**
     * 初始化图片加载组件ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private com.android.volley.toolbox.ImageLoader imageLoader;

    private class BitmapCache implements com.android.volley.toolbox.ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }

    public com.android.volley.toolbox.ImageLoader getImageLoader() {
        return imageLoader;
    }



    // 构造方法
    // 实例化一次
    public synchronized static CaopingCloudApplication getInstance() {
        if (null == application) {
            application = new CaopingCloudApplication();
        }
        return application;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}
