package cn.koolcloud.pos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ValueCallback;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.service.PaymentInfo;
import cn.koolcloud.pos.util.UtilForJSON;

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

	private BaseController currentController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		action = intent.getStringExtra(ACTION);
		if (ACTION_PAY.equalsIgnoreCase(action)) {
			String pMethod = intent.getStringExtra("pMethod");
			String transAmount = intent.getStringExtra("transAmount");
			String openBrh = intent.getStringExtra("openBrh");
			String paymentId = intent.getStringExtra("paymentId");
			String packageName = intent.getStringExtra("packageName");
			String orderNo = intent.getStringExtra("orderNo");
			String orderDesc = intent.getStringExtra("orderDesc");
			initPay(pMethod, transAmount, openBrh, paymentId, packageName,
					orderNo, orderDesc);
			((MyApplication) getApplication()).setPkgName(packageName);
		} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
			String txnId = intent.getStringExtra("txnId");
			String packageName = intent.getStringExtra("packageName");
			payInfo = new PayInfo();
			payInfo.txnId = txnId;
			payInfo.packageName = packageName;
			((MyApplication) getApplication()).setPkgName(packageName);
			
		} else if (ACTION_LOGIN.equalsIgnoreCase(action)) {

		} else if (ACTION_LOGOUT.equalsIgnoreCase(action)) {

		}
		super.onCreate(savedInstanceState);

		this.exitOnDestroy = false;
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
		} else if (ACTION_LOGIN.equalsIgnoreCase(action)) {
			startLogin();
		} else if (ACTION_LOGOUT.equalsIgnoreCase(action)) {
			startLogout();
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
						endReverse(result);
					} else if (ACTION_LOGIN.equalsIgnoreCase(action)) {
						endLogin(result);
					} else if (ACTION_LOGOUT.equalsIgnoreCase(action)) {
						endLogout(result);
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
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
	}

	public void initPay(String pMethod, String transAmount, String openBrh,
			String paymentId, String packageName, String orderNo, String orderDesc) {
		payInfo = new PayInfo();
		payInfo.pMethod = pMethod;
		payInfo.transAmount = transAmount;
		payInfo.openBrh = openBrh;
		payInfo.paymentId = paymentId;
		payInfo.packageName = packageName;
		payInfo.orderNo = orderNo;
		payInfo.orderDesc = orderDesc;
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
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.callJsHandler("External.onPay", msg);
	}

	private void endPay(JSONObject result) {
		Log.d(TAG, "endPay");

		Intent i = new Intent();
		String totalAmount = null;
		String paidAmount = null;
		String changeAmount = null;
		String resultValue = null;
		String detailList = null;

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
		JSONArray jsArray = null;
		try {
			detailList = result.getJSONArray("orderList").toString();
			jsArray = new JSONArray(detailList);
			String packageName = ((MyApplication) getApplication()).getPkgName();
			UtilForJSON.parseCardNumberByPackageName(packageName, jsArray, PayExScreen.this);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		i.putExtra(ACTION, ACTION_PAY);
		i.putExtra("totalAmount", totalAmount);
		i.putExtra("paidAmount", paidAmount);
		i.putExtra("changeAmount", changeAmount);
		i.putExtra("result", resultValue);
		i.putExtra("detailList", jsArray.toString());

		setResult(RESULT_CODE, i);
	}

	private void startReverse() {
		Log.d(TAG, "startReverse");
		
		JSONObject msg = new JSONObject();
//		CacheDB cacheDB = CacheDB.getInstance(PayExScreen.this);
//		PaymentInfo paymentInfo = cacheDB.getPaymentByPaymentId(payInfo.paymentId);
		try {
			/*if (payInfo.txnId != null && paymentInfo != null) {
				msg.put("txnId", payInfo.txnId);
				msg.put("txnId", paymentInfo.getBrhKeyIndex());
			}*/
			if (payInfo.txnId != null) {
				msg.put("txnId", payInfo.txnId);
			}
			if (!TextUtils.isEmpty(payInfo.packageName)) {
				msg.put("packageName", payInfo.packageName);
			}
		} catch (Exception e) {
			
		}
		
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(getString(R.string.controllerJSName_OrderDetail));
		js.callJsHandler("External.startReverse", msg);
	}

	private void endReverse(JSONObject result) {
		Log.d(TAG, "endReverse");
		Intent intent = new Intent();
		
		intent.putExtra(ACTION, ACTION_REVERSE);
		intent.putExtra("refNo", result.optString("refNo"));
		intent.putExtra("reverse_status", result.optString("reverse_status", "0"));
		intent.putExtra("orderStateDesc", result.optString("orderStateDesc"));
		intent.putExtra("transTime", result.optString("transTime"));
		intent.putExtra("operator", result.optString("operator"));
		intent.putExtra("paymentId", result.optString("paymentId"));
		intent.putExtra("paymentName", result.optString("paymentName"));
		setResult(RESULT_CODE, intent);
	}

	private void startLogin() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.callJsHandler("External.onLogin", null);
	}

	private void endLogin(JSONObject result) {
		Intent i = new Intent();
		setResult(RESULT_CODE, null);
	}

	private void startLogout() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.callJsHandler("External.onLogout", null,
				new ValueCallback<String>() {

					@Override
					public void onReceiveValue(String value) {
						try {
							endLogout(new JSONObject(value));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void endLogout(JSONObject result) {
		Intent i = new Intent();
		setResult(RESULT_CODE, null);
	}

	private class PayInfo {
		public String pMethod;
		public String transAmount;
		public String openBrh;
		public String paymentId;
		public String packageName;
		public String orderNo;
		public String orderDesc;
		public String txnId;
	}
}
