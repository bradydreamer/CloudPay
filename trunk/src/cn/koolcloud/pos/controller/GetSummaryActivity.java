package cn.koolcloud.pos.controller;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cn.koolcloud.pos.AppInit;
import cn.koolcloud.pos.AppInitScreen;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.service.ISecureService;
import cn.koolcloud.pos.wd.R;

public class GetSummaryActivity extends Activity {

	private String action;
	public final static String ACTION = "ex_action";
	public final static String ACTION_PRINT = "printInit";
	public final static String ACTION_GET_SUMMARY = "getSummary";
	
	protected boolean hasInit;
	protected Handler mainHandler;
	
	/*private ISecureService mService;
	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = ISecureService.Stub.asInterface(service);
			ClientEngine.engineInstance().setBackgroundService(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

	};*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		moveTaskToBack(true);
		Intent intent = getIntent();
		action = intent.getStringExtra(ACTION);
		mainHandler = new Handler();
		Log.w("GetSummaryActivity", "-----------------------" + action);
		if (ACTION_GET_SUMMARY.equalsIgnoreCase(action)) {
//			Intent mIntent = new Intent();
//			mIntent.setAction(AppInitScreen.STARTSERVICE);
//			this.bindService(mIntent, mServiceConnection, Service.BIND_AUTO_CREATE);
			
			if (!hasInit) {
				mainHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						initApp();
						getSummary();
					}
				}, 1500);

				hasInit = true;
			}
		}
	}

	protected void initApp() {
		if (ClientEngine.engineInstance().getCurrentController() == null) {
//			super.initApp();
			AppInit appInit = new AppInit(GetSummaryActivity.this);
			appInit.init(mainHandler);
		}
		ClientEngine.engineInstance().setPayExController(this);
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(this.getString(R.string.controllerJSName_TransactionManageIndex));
	}
	
	private void getSummary() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler("window.TransactionManageIndex.getSummary", null);
		}
		
		finish();
	}

	/*@Override
	protected void onDestroy() {
		if (ACTION_GET_SUMMARY.equalsIgnoreCase(action)) {
			this.unbindService(mServiceConnection);
		}
		mService = null;
		super.onDestroy();
	}*/
	
}
