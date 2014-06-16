package cn.koolcloud.pos;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.HomeController;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

public class WelcomeScreen extends Activity {
	protected Handler mainHandler;
	protected Context context;
	protected boolean hasInit;
	protected boolean exitOnDestroy = true;
	protected final String TAG = "WelcomeScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, this.toString() + "onCreate");
        context = this;
        mainHandler = new Handler();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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

	@Override
	protected void onDestroy() {
		Log.d(TAG, this.toString() + " onDestroy");
		super.onDestroy();
		if(exitOnDestroy){
			System.exit(0);
		}
	}
}
