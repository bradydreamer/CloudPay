package cn.koolcloud.pos;

import cn.koolcloud.pos.service.MerchService;
import cn.koolcloud.pos.service.SecureService;
import cn.koolcloud.pos.util.NetUtil;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MyApplication extends Application {

	private static MyApplication instance;
	private Context context;

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
	}

}
