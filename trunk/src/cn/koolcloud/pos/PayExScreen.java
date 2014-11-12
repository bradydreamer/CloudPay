package cn.koolcloud.pos;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.service.SmartIposRunBackground;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.wd.R;

public class PayExScreen extends WelcomeScreen {
	private PayInfo payInfo;
	private String action;

	public final static int SUCC = 0;
	public final static int FAIL = 1;
	public final static String ACTION = "ex_action";
	public final static int RESULT_CODE = 100010;

	public final static String ACTION_PAY = "pay";
	public final static String ACTION_LOGIN = "login";
	public final static String ACTION_LOGOUT = "logout";
	public final static String ACTION_REVERSE = "reverse";
	public final static String ACTION_INIT = "appInit";
	public final static String TAG = "PayExScreen";
	private SmartIposRunBackground sRunbackService;

	private BaseController currentController;

	String orderNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate----------------------");
		activityList.add(this);
		initData();
		super.onCreate(savedInstanceState);

		if (ACTION_INIT.equalsIgnoreCase(action)) {
			if (!hasInit) {
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
		this.exitOnDestroy = false;
	}

	private void initData() {
		Intent intent = getIntent();
		action = intent.getStringExtra(ACTION);
		orderNo = intent.getStringExtra("orderNo");

		if (ACTION_PAY.equalsIgnoreCase(action)) {
			// ActivityManager am = (ActivityManager) this
			// .getSystemService(ACTIVITY_SERVICE);
			// am.moveTaskToFront(this.getTaskId(),
			// ActivityManager.MOVE_TASK_NO_USER_ACTION); 将后台的运行的Task移到前台。
			String pMethod = intent.getStringExtra("pMethod");
			String transAmount = intent.getStringExtra("transAmount");
			String openBrh = intent.getStringExtra("acquId");
			String paymentId = intent.getStringExtra("paymentId");
			String packageName = intent.getStringExtra("packageName");
			String orderDesc = intent.getStringExtra("orderDesc");
			String userName = intent.getStringExtra("userName");
			String pwd = intent.getStringExtra("pwd");
			if (userName == null || pwd == null) {
				initPay(pMethod, transAmount, openBrh, paymentId, packageName,
						orderNo, orderDesc);
			} else {
				initPay(pMethod, transAmount, openBrh, paymentId, packageName,
						orderNo, orderDesc, userName, pwd);
			}
			((MyApplication) getApplication()).setPkgName(packageName);
		} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
			String txnId = intent.getStringExtra("txnId");
			String packageName = intent.getStringExtra("packageName");
			payInfo = new PayInfo();
			payInfo.txnId = txnId;
			payInfo.packageName = packageName;
			((MyApplication) getApplication()).setPkgName(packageName);
		} else if (ACTION_INIT.equalsIgnoreCase(action)) {
			moveTaskToBack(true);
			Intent it = new Intent();
			it.setAction(AppInitScreen.STARTSERVICE);
			this.bindService(it, mServiceConnection, Service.BIND_AUTO_CREATE);
		}

	}

	protected void setContentView() {
		if (ACTION_PAY.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_LOGIN.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_LOGOUT.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		}
	}

	protected void initApp() {
		if (ClientEngine.engineInstance().getCurrentController() == null) {
			super.initApp();
		}
	}

	protected void startScene() {
		ClientEngine.engineInstance().mRequestCode = ClientEngine.REQUEST_EXTERNAL;
		if (ClientEngine.engineInstance().getCurrentController() != null) {
			currentController = ClientEngine.engineInstance()
					.getCurrentController();
			ClientEngine.engineInstance().setCurrentController(null);
		} else {
			currentController = null;
		}
		ClientEngine.engineInstance().setPayExController(this);
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(this.getString(R.string.controllerJSName_External));

		if (ACTION_PAY.equalsIgnoreCase(action)) {
			ClientEngine.engineInstance().showWaitingDialog(context, null,
					new Runnable() {

						@Override
						public void run() {
							startPay();
						}
					});
		} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
			ClientEngine.engineInstance().showWaitingDialog(context, null,
					new Runnable() {

						@Override
						public void run() {
							startReverse();
						}
					});
		} else if (ACTION_INIT.equalsIgnoreCase(action)) {
			AppInitManager aim = AppInitManager.getInstance();
			if (sRunbackService != null) {
				aim.Init(sRunbackService, this);
				aim.autoLogin();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "requestCode: " + requestCode + "; resultCode: "
				+ resultCode);

		if (resultCode == BaseController.RESULT_ORDER_END) {
			Log.d(TAG, "RESULT_ORDER_END");
			if (currentController != null) {
				ClientEngine.engineInstance().setCurrentController(
						currentController);
			}
			String strResultData = data.getStringExtra("result");

			if (strResultData != null) {
				try {
					JSONObject result = new JSONObject(strResultData);
					if (ACTION_PAY.equalsIgnoreCase(action)) {
						endPay(result);
					} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
						// endReverse(result);
						endPay(result);
					}
					ClientEngine.engineInstance().getMerchService()
							.endCallPayEx();

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if (resultCode == BaseController.RESULT_START_ACTIVITY) {
			Log.d(TAG, "startActivityForResult");

			startActivityForResult(data,
					ClientEngine.engineInstance().mRequestCode);
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart----------------------");
		if (ACTION_INIT.equalsIgnoreCase(action)) {
			moveTaskToBack(true);
		}
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart----------------------");
		initData();
		Log.i(TAG, "onRestart------action=" + action);
		if (ACTION_INIT.equalsIgnoreCase(action) && sRunbackService != null) {
			AppInitManager aim = AppInitManager.getInstance();
			aim.Init(sRunbackService, this);
			aim.autoLogin();
		}
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume----------------------");
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause----------------------");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy----------------");
		if (ACTION_INIT.equalsIgnoreCase(action)) {
			this.unbindService(mServiceConnection);
		}
		super.onDestroy();
	}

	public void initPay(String pMethod, String transAmount, String openBrh,
			String paymentId, String packageName, String orderNo,
			String orderDesc) {
		payInfo = new PayInfo();
		payInfo.pMethod = pMethod;
		payInfo.transAmount = transAmount;
		payInfo.openBrh = openBrh;
		payInfo.paymentId = paymentId;
		payInfo.packageName = packageName;
		payInfo.orderNo = orderNo;
		payInfo.orderDesc = orderDesc;
	}

	public void initPay(String pMethod, String transAmount, String openBrh,
			String paymentId, String packageName, String orderNo,
			String orderDesc, String userName, String pwd) {
		payInfo = new PayInfo();
		payInfo.pMethod = pMethod;
		payInfo.transAmount = transAmount;
		payInfo.openBrh = openBrh;
		payInfo.paymentId = paymentId;
		payInfo.packageName = packageName;
		payInfo.orderNo = orderNo;
		payInfo.orderDesc = orderDesc;
		payInfo.userName = userName;
		payInfo.pwd = pwd;

	}

	private void startPay() {
		Log.d(TAG, "startPay");
		JSONObject msg = new JSONObject();
		try {
			msg.put("payType", payInfo.pMethod);
			msg.put("transAmount", payInfo.transAmount);
			if (payInfo.openBrh != null) {
				msg.put("openBrh", payInfo.openBrh);
			}
			if (payInfo.paymentId != null) {
				msg.put("paymentId", payInfo.paymentId);
			}
			if (!TextUtils.isEmpty(payInfo.packageName)) {
				msg.put("packageName", payInfo.packageName);
			}
			if (!TextUtils.isEmpty(payInfo.orderNo)) {
				msg.put("orderNo", payInfo.orderNo);
			}
			if (!TextUtils.isEmpty(payInfo.orderDesc)) {
				msg.put("orderDesc", payInfo.orderDesc);
			}
		} catch (Exception e) {

		}
		initLoginData();
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.callJsHandler("External.onPay", msg);
	}

	private void initLoginData() {
		Map<String, Object> newMerchantMap = new HashMap<String, Object>();
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(MyApplication.getContext(),
						"merchant");
		String pwd = payInfo.pwd;
		String ssn = android.os.Build.SERIAL;
		String merchId = "999290053110041";
		String operator = payInfo.userName;
		if (pwd == null || pwd.equals("")) {
			pwd = "_TDS_" + stringToMD5("123456");
		} else {
			pwd = "_TDS_" + pwd;
		}
		if (map.get("merchId") == null) {
			merchId = "999290053110041";
		} else {
			merchId = map.get("merchId").toString();
		}
		if (operator == null || operator.equals("") || operator.equals("null")) {
			operator = "wan";
		}

		newMerchantMap.put("pwd", pwd);
		newMerchantMap.put("ssn", ssn);
		newMerchantMap.put("merchId", merchId);
		newMerchantMap.put("operator", operator);
		UtilForDataStorage.savePropertyBySharedPreferences(
				MyApplication.getContext(), "merchant", newMerchantMap);
	}

	private void endPay(JSONObject result) {
		Log.d(TAG, "endPay");

		Intent i = new Intent();
		String totalAmount = null;
		String paidAmount = null;
		String changeAmount = null;
		String couponAmount = null;
		String resultValue = null;
		String detailList = null;
		String operatorName = null;
		String orderNum = null;

		if (result == null) {
			setResult(RESULT_CANCELED, i);
		} else {

			try {
				totalAmount = result.getString("totalAmount");
			} catch (JSONException e1) {
				totalAmount = "0";
			}
			try {
				paidAmount = result.getString("paidAmount");
			} catch (JSONException e1) {
				paidAmount = "0";
			}
			try {
				changeAmount = result.getString("changeAmount");
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				changeAmount = "0";
			}
			try {
				resultValue = result.getString("result");
			} catch (JSONException e1) {
				resultValue = "0";
			}
			try {
				couponAmount = result.getString("couponAmount");
			} catch (JSONException e1) {
				couponAmount = "0";
			}
			try {
				orderNum = result.getString("orderNo");
			} catch (JSONException e1) {
				orderNum = "";
			}
			JSONArray jsArray = null;
			try {
				detailList = result.getJSONArray("orderList").toString();
				jsArray = new JSONArray(detailList);
				String packageName = ((MyApplication) getApplication())
						.getPkgName();
				UtilForJSON.parseCardNumberByPackageName(packageName, jsArray,
						PayExScreen.this);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Map<String, ?> map = UtilForDataStorage
					.readPropertyBySharedPreferences(
							MyApplication.getContext(), "merchant");
			operatorName = String.valueOf(map.get("operator"));

			// i.putExtra(ACTION, ACTION_PAY);
			i.putExtra(ACTION, action);
			i.putExtra("operatorName", operatorName);
			i.putExtra("totalAmount", totalAmount);
			i.putExtra("actualAmount", paidAmount);
			i.putExtra("result", resultValue);
			i.putExtra("detailList", jsArray.toString());

			if (Integer.parseInt(couponAmount) > 0) {
				i.putExtra("couponAmount", couponAmount);
			}
			if (!TextUtils.isEmpty(orderNum)) {
				i.putExtra("orderNo", orderNum);
			}

			setResult(RESULT_OK, i);
		}
	}

	private String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}

	private void startReverse() {
		Log.d(TAG, "startReverse");

		JSONObject msg = new JSONObject();
		// CacheDB cacheDB = CacheDB.getInstance(PayExScreen.this);
		// PaymentInfo paymentInfo =
		// cacheDB.getPaymentByPaymentId(payInfo.paymentId);
		try {
			/*
			 * if (payInfo.txnId != null && paymentInfo != null) {
			 * msg.put("txnId", payInfo.txnId); msg.put("txnId",
			 * paymentInfo.getBrhKeyIndex()); }
			 */
			if (payInfo.txnId != null) {
				msg.put("txnId", payInfo.txnId);
			}
			if (!TextUtils.isEmpty(payInfo.packageName)) {
				msg.put("packageName", payInfo.packageName);
			}

			msg.put("orderNo", orderNo);
		} catch (Exception e) {

		}
		initLoginData();
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(getString(R.string.controllerJSName_OrderDetail));
		js.callJsHandler("External.startReverse", msg);
	}

	private void endReverse(JSONObject result) {
		Log.d(TAG, "endReverse");
		Intent intent = new Intent();

		intent.putExtra(ACTION, ACTION_REVERSE);
		intent.putExtra("refNo", result.optString("refNo"));
		intent.putExtra("reverse_status",
				result.optString("reverse_status", "0"));
		intent.putExtra("orderStateDesc", result.optString("orderStateDesc"));
		intent.putExtra("transTime", result.optString("transTime"));
		intent.putExtra("operatorName", result.optString("operator"));
		intent.putExtra("paymentId", result.optString("paymentId"));
		intent.putExtra("paymentName", result.optString("paymentName"));
		setResult(RESULT_OK, intent);
	}

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			sRunbackService = SmartIposRunBackground.Stub.asInterface(service);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	};

	private class PayInfo {
		public String pMethod;
		public String transAmount;
		public String openBrh;
		public String paymentId;
		public String packageName;
		public String orderNo;
		public String orderDesc;
		public String userName;
		public String pwd;
		public String txnId;
	}
}
