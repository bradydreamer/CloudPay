package cn.koolcloud.pos;

import android.content.Context;
import android.os.Handler;
import cn.koolcloud.pos.util.UtilForThread;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class AppInit {
	private Context context;

	public AppInit(Context context) {
		this.context = context;
	}

	public void init(final Handler mainHandler) {
		UtilForThread.setMainThreadId(mainHandler.getLooper().getThread()
				.getId());

		ClientEngine clientEngine = ClientEngine.engineInstance();
		clientEngine.setMainHandler(mainHandler);
		clientEngine.setContext(context);
		clientEngine.initEngine();
		// initImageLoader(context);
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		// ImageLoader.getInstance().clearDiscCache();
	}

}
