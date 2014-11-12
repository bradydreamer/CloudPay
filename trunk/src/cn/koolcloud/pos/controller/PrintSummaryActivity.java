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
import cn.koolcloud.pos.service.SmartIposRunBackground;
import cn.koolcloud.pos.wd.R;

public class PrintSummaryActivity extends Activity {

	private String action;
	public final static String ACTION = "ex_action";
	public final static String ACTION_PRINT = "printInit";
	public final static String ACTION_GET_SUMMARY = "getSummary";
	
	protected boolean hasInit;
	protected Handler mainHandler;
	
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
		Log.w("PrintSummaryActivity", "-----------------------" + action);
		if (ACTION_PRINT.equalsIgnoreCase(action)) {
			if (!hasInit) {
				mainHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						initApp();
						startPrint();
					}
				}, 1500);

				hasInit = true;
			}
		}
	}

	protected void initApp() {
		if (ClientEngine.engineInstance().getCurrentController() == null) {
//			super.initApp();
			AppInit appInit = new AppInit(PrintSummaryActivity.this);
			appInit.init(mainHandler);
		}
		ClientEngine.engineInstance().setPayExController(this);
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(this.getString(R.string.controllerJSName_TransactionManageIndex));
	}
	
	private void startPrint() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler("window.TransactionManageIndex.startPrintSummary", null);
		}
		finish();
	}
	
}
