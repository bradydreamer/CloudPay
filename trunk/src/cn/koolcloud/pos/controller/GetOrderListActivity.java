package cn.koolcloud.pos.controller;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import cn.koolcloud.pos.AppInit;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.wd.R;

public class GetOrderListActivity extends Activity {

	private String action;
	public final static String ACTION = "ex_action";
	public final static String ACTION_PRINT = "printInit";
	public final static String ACTION_GET_ORDER_LIST = "getOrderList";
	
	protected boolean hasInit;
	protected Handler mainHandler;
	private String startDate;
	private String endDate;
	private int pageNo;
	private int pageSize;
	
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
		startDate = intent.getStringExtra("startDate");
		endDate = intent.getStringExtra("endDate");
		pageNo = intent.getIntExtra("pageNo", 0);
		pageSize = intent.getIntExtra("pageSize", 0);
		mainHandler = new Handler();
		Log.w("GetOrderListActivity", "-----------------------" + action);
		if (ACTION_GET_ORDER_LIST.equalsIgnoreCase(action)) {
			
			if (!hasInit) {
				mainHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						initApp();
						getOrderList();
					}
				}, 1500);

				hasInit = true;
			}
		}
	}

	protected void initApp() {
		if (ClientEngine.engineInstance().getCurrentController() == null) {
//			super.initApp();
			AppInit appInit = new AppInit(GetOrderListActivity.this);
			appInit.init(mainHandler);
		}
		ClientEngine.engineInstance().setPayExController(this);
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(this.getString(R.string.controllerJSName_TransactionManageIndex));
	}
	
	private void getOrderList() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			try {
				JSONObject jsObj = new JSONObject();
				jsObj.put("startDate", startDate);
				jsObj.put("endDate", endDate);
				jsObj.put("pageNo", pageNo);
				jsObj.put("pageSize", pageSize);
				js.callJsHandler("window.TransactionManageIndex.getOrderList", jsObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		finish();
	}
	
}
