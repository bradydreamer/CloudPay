package cn.koolcloud.pos;

import android.content.Context;
import android.content.Intent;
import cn.koolcloud.pos.service.MerchService;
import cn.koolcloud.pos.service.SecureService;

import com.baidu.frontia.FrontiaApplication;

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

}
