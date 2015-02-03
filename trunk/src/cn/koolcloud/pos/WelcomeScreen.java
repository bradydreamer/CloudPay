package cn.koolcloud.pos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import cn.koolcloud.BuildingConfig;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.controller.HomeController;
import cn.koolcloud.pos.controller.dialogs.PushMessageController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.service.local.LocalService;
import cn.koolcloud.pos.util.PushUtils;

public class WelcomeScreen extends Activity {
	protected Handler mainHandler;
	protected Context context;
	protected boolean hasInit;
	protected boolean exitOnDestroy = true;
	protected final String TAG = "WelcomeScreen";
	protected boolean isExternalOrder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobclickAgent.setDebugMode(BuildingConfig.DEBUG);
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.updateOnlineConfig(this);

        Log.d(TAG, this.toString() + "onCreate");
        context = this;
        mainHandler = new Handler();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
        PushUtils.logStringCache = PushUtils.getLogText(getApplicationContext());
        PushUtils.loginBaiduCloud(getApplicationContext());
        
		//start Local Service but don't execute checking
        if (!isExternalOrder) {
        	Intent bindIntent = new Intent(this, LocalService.class);
        	//indicate start a local service from external
        	bindIntent.putExtra(ConstantUtils.START_SERVICE_EXTERNAL_TAG, true);
        	bindIntent.putExtra(ConstantUtils.LOCAl_SERVICE_TAG, false);
        	startService(bindIntent);
        }
        
        this.setContentView();
        
        //make CacheDB go upgrade
        CacheDB.getInstance(WelcomeScreen.this).getPaymentsCount();
    }
    
    protected void setContentView() {
        setContentView(R.layout.activity_welcome_screen);
	}
	@Override
	protected void onStart() {
		Log.d(TAG, this.toString() + "onStart");
		super.onStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && !hasInit) {
			mainHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					initApp();
					startScene();
				}
			}, 1500);
			
			hasInit = true;
		}
	}
	
	protected void initApp(){
		AppInit appInit = new AppInit(context);
		appInit.init(mainHandler);
	}
	protected void startScene(){
		ClientEngine.engineInstance().mRequestCode = ClientEngine.REQUEST_INTER;
		Intent intent = new Intent();
		intent.setClass(context, HomeController.class);
//		intent.setClass(context, PayExScreen.class);
//		intent.putExtra(PayExScreen.ACTION, PayExScreen.ACTION_MERCH_INFO);
		//fix appstore can't open smartpay (SMTPS-113) --start fixed by Teddy on 26th September
		intent.setFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TOP|*/Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//fix appstore can't open smartpay (SMTPS-113) --end fixed by Teddy on 26th September
		startActivityForResult(intent, ClientEngine.engineInstance().mRequestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		Log.d(TAG, this.toString() + " onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this.toString() + " onDestroy");
		super.onDestroy();
		if(exitOnDestroy){
			System.exit(0);
		}
		//stop Local service
		if (!isExternalOrder) {
			
			Intent bindIntent = new Intent(this, LocalService.class);  
			stopService(bindIntent);  
		}
	}
}
