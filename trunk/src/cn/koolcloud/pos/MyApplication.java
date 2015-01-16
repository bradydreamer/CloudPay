package cn.koolcloud.pos;

import android.content.Context;
import android.content.Intent;
import cn.koolcloud.pos.service.MerchService;
import cn.koolcloud.pos.service.SecureService;
import cn.koolcloud.pos.util.CustomImageDownloader;

import com.baidu.frontia.FrontiaApplication;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MyApplication extends FrontiaApplication {

	private static MyApplication instance;
	private Context context;
	private String pkgName;
	
	private boolean isFirstStart = false;

    public MyApplication() {
    	instance = this;
    }

    public static Context getContext() {
    	return instance;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		Intent merchService = new Intent(context, MerchService.class);
        context.startService(merchService);
        
        Intent secureService = new Intent(context, SecureService.class);
        context.startService(secureService);

        //init universal image loader
        initImageLoader(context);
	}
	
	public boolean isFirstStart() {
		return this.isFirstStart;
	}
	
	public void setFirstStart(boolean firstStarted) {
		this.isFirstStart = firstStarted;
	}
	
	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String packageName) {
		this.pkgName = packageName;
	}

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        //cache image to sdcard if it is exists
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "SmartPos/Cache/Images");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new CustomImageDownloader(context, 10000, 10000))
//                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

}
