package cn.koolcloud.pos;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import cn.koolcloud.pos.controller.HomeController;
import cn.koolcloud.pos.util.PushUtils;
import cn.koolcloud.pos.wd.R;

public class WelcomeScreen extends Activity {
	protected Handler mainHandler;
	protected Context context;
	protected boolean hasInit;
	protected boolean exitOnDestroy = true;
	protected final String TAG = "WelcomeScreen";
	public static List<Activity> activityList = new ArrayList<Activity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        
        Log.d(TAG, this.toString() + "onCreate");
        context = this;
        mainHandler = new Handler();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
        PushUtils.logStringCache = PushUtils.getLogText(getApplicationContext());
        PushUtils.loginBaiduCloud(getApplicationContext());
        
        this.setContentView();
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
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
	
	public static void exit() {
		if (activityList != null && activityList.size() > 0) {
			for (int i = 0; i < activityList.size(); i++) {
				Activity activity = activityList.get(i);
				activity.finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this.toString() + " onDestroy");
		super.onDestroy();
		if(exitOnDestroy){
			System.exit(0);
		}
	}
}
