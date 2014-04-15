package cn.koolcloud.pos;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import cn.koolcloud.control.ISO8583Controller;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.controller.HomeController;
import cn.koolcloud.pos.controller.PinPadController;
import cn.koolcloud.pos.controller.delivery_voucher.DelVoucherIdController;
import cn.koolcloud.pos.controller.delivery_voucher.DelVoucherInfoController;
import cn.koolcloud.pos.controller.delivery_voucher.InputDelVoucherNumController;
import cn.koolcloud.pos.controller.multipay.MultiPayIndex;
import cn.koolcloud.pos.controller.multipay.MultiPayRecord;
import cn.koolcloud.pos.controller.others.BalanceResultController;
import cn.koolcloud.pos.controller.others.OthersIndexController;
import cn.koolcloud.pos.controller.others.settings.LoginController;
import cn.koolcloud.pos.controller.others.settings.MerchantInfoController;
import cn.koolcloud.pos.controller.others.settings.SetMachineIdController;
import cn.koolcloud.pos.controller.others.settings.SetMerchIdController;
import cn.koolcloud.pos.controller.others.settings.SetTransIdController;
import cn.koolcloud.pos.controller.others.settings.SettingsDownloadController;
import cn.koolcloud.pos.controller.others.settings.SettingsIndexController;
import cn.koolcloud.pos.controller.pay.PayAccountController;
import cn.koolcloud.pos.controller.pay.PayMethodController;
import cn.koolcloud.pos.controller.pay.TransAmountController;
import cn.koolcloud.pos.controller.transaction_manage.TransactionManageIndexController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.ConsumptionRecordController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.ConsumptionRecordSearchController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.OrderDetailController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.SingleRecordSearchController;
import cn.koolcloud.pos.controller.transaction_manage.del_voucher.DelVoucherRecordController;
import cn.koolcloud.pos.controller.transaction_manage.del_voucher.DelVoucherRecordSearchController;
import cn.koolcloud.pos.net.NetEngine;
import cn.koolcloud.pos.secure.SecureEngine;
import cn.koolcloud.pos.service.IMerchService;
import cn.koolcloud.pos.service.ISecureService;
import cn.koolcloud.pos.service.MerchInfo;
import cn.koolcloud.pos.service.SecureInfo;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForGraghic;
import cn.koolcloud.pos.util.UtilForThread;
import cn.koolcloud.pos.widget.CustomAnimDialog;

public class ClientEngine {

	private static ClientEngine instance;
	private JavaScriptEngine jsEngine;
	private SecureEngine secureEngine;

	private Context context;
	private BaseController currentController;
	private Activity payExController;

	private Stack<Map<String, BaseController>> controllerStack;
	private static final String TAG = "ClientEngine";
	private Handler mainHandler;

	private Drawable dw_dialogProgress;
	private CustomAnimDialog waitingDialog;
	private JSONObject systemInfo;

	public int mRequestCode;
	public static final int REQUEST_INTER = 1;
	public static final int REQUEST_EXTERNAL = 2;

	private ClientEngine() {
		controllerStack = new Stack<Map<String, BaseController>>();
	}

	public static ClientEngine engineInstance() {
		if (null == instance) {
			Log.d(TAG, "ClientEngine null == instance");
			instance = new ClientEngine();

		}
		return instance;
	}

	public boolean isContextNull() {
		if (null == context) {
			return true;
		} else {
			return false;
		}
	}

	public Handler getMainHandler() {
		return mainHandler;
	}

	public void setMainHandler(Handler mainHandler) {
		this.mainHandler = mainHandler;
	}

	public void addController(String controllerName,
			BaseController currentController) {
		Map<String, BaseController> map = new HashMap<String, BaseController>();
		map.put(controllerName, currentController);
		controllerStack.add(map);
	}

	public void removeController(String controllerName) {
		for (int i = controllerStack.size() - 1; i >= 0; i--) {
			if (controllerStack.get(i).containsKey(controllerName)) {
				controllerStack.remove(i);
				return;
			}
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setCurrentController(BaseController currentController) {
		this.currentController = currentController;
		this.context = currentController;
	}

	public BaseController getCurrentController() {
		return this.currentController;
	}

	public void setPayExController(Activity fController) {
		this.payExController = fController;
		this.context = payExController;
	}

	private IMerchService mMerchService;

	public IMerchService getMerchService() {
		return mMerchService;
	}

	private ServiceConnection merchConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMerchService = IMerchService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mMerchService = null;
		}
	};

	private ISecureService mSecureService;
	private ServiceConnection secureConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSecureService = ISecureService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSecureService = null;
		}
	};

	public void initEngine() {
		Intent merchIntent = new Intent(IMerchService.class.getName());
		context.startService(merchIntent);
		boolean result_merch = context.bindService(merchIntent,
				merchConnection, Context.BIND_AUTO_CREATE);
		if (!result_merch) {
			Toast.makeText(context, "merch服务绑定失败。", Toast.LENGTH_SHORT).show();
		}

		Intent secureIntent = new Intent(ISecureService.class.getName());
		context.startService(secureIntent);
		boolean result_secure = context.bindService(secureIntent,
				secureConnection, Context.BIND_AUTO_CREATE);
		if (!result_secure) {
			Toast.makeText(context, "secure服务绑定失败。", Toast.LENGTH_SHORT).show();
		}
		initJSengine();
		secureEngine = new SecureEngine();
	}

	public MerchInfo getMerchInfo() {
		if (mMerchService != null) {
			try {
				return mMerchService.getMerchInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setMerchInfo(MerchInfo mi) {
		if (mMerchService != null) {
			try {
				mMerchService.setMerchInfo(mi);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private SecureInfo mSecureInfo;

	public SecureInfo getSecureInfo() {
		SecureInfo si = null;
		if (mSecureService != null) {
			try {
				si = mSecureService.getSecureInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			if (mSecureInfo == null) {
				mSecureInfo = new SecureInfo("00000000", "1", null, "");
			}
			return mSecureInfo;
		}
		return si;
	}

	public void setSecureInfo(SecureInfo si) {
		if (mSecureService != null) {
			try {
				mSecureService.setSecureInfo(si);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void initJSengine() {
		jsEngine = new JavaScriptEngine();
		jsEngine.initEngine(context);

		jsEngine.loadJs("Common/global");
		jsEngine.loadJs("platform/android");
		jsEngine.loadJs("platform/ipos");
		jsEngine.loadJs("Common/ConsumptionData");
		jsEngine.loadJs("Common/net");
		jsEngine.loadJs("Common/util");
		jsEngine.loadJs("Common/user");
		jsEngine.loadJs("pay/pay");
		jsEngine.loadJs("pay/payFlow");
		jsEngine.loadJs("pay/payReverse");
		jsEngine.loadJs("pay/payMethod");
	}

	public JavaScriptEngine javaScriptEngine() {
		return jsEngine;
	}

	public SecureEngine secureEngine() {
		return secureEngine;
	}

	public void serviceMerchInfo(JSONObject data, String identifier) {
		String action = data.optString("action");
		boolean isSetAction = action.equalsIgnoreCase("set");

		JSONObject result = null;
		if (isSetAction) {
			setServiceMerchInfo(data.optJSONObject("value"));
			callBack(identifier, null);
		} else {
			result = getServiceMerchInfo();
		}

		if (!identifier.isEmpty()) {
			callBack(identifier, result);
		}
	}

	private void setServiceMerchInfo(JSONObject value) {
		if (mSecureService != null && value != null) {
			try {
				mSecureService.setUserInfo(value.toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private JSONObject getServiceMerchInfo() {
		JSONObject msgObj = null;
		if (mSecureService != null) {
			try {
				String ui = mSecureService.getUserInfo();
				if (ui != null && !ui.isEmpty()) {
					msgObj = new JSONObject(ui);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return msgObj;
	}

	void initSystemInfo() {
		JSONObject infos = new JSONObject();
		try {
			infos.put("platform", "Android");
			infos.put("os", "" + Build.VERSION.RELEASE);
			infos.put("model", Build.BRAND + " " + Build.MODEL);
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point point = UtilForGraghic.getDisplayPoint(display);
			infos.put("resolution", String.format("%d*%d", point.x, point.y));
			try {
				infos.put(
						"version",
						context.getPackageManager().getPackageInfo(
								context.getPackageName(), 0).versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		systemInfo = infos;
	}

	public JSONObject getSystemInfo() {
		return systemInfo;
	}

	void showAlert(final JSONObject data, final String identifier) {
		String msg = data.optString("msg");
		if (msg.startsWith("JSLOG")) {
			Log.i(TAG, msg);
		} else {
			if (UtilForThread.isCurrentInMainThread(Thread.currentThread())) {
				showAlertInMainThread(data, identifier);
			} else {
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						showAlertInMainThread(data, identifier);
					}
				});
			}
		}
	}

	private void showAlertInMainThread(final JSONObject data,
			final String identifier) {
		String msg = data.optString("msg");
		String positiveText = data.optString("positiveButtonText", null);
		if (null == positiveText || 0 == positiveText.length()) {
			positiveText = context.getString(R.string.alert_btn_positive);
		}
		String negativeText = data.optString("negativeButtonText", null);
		if (null == negativeText) {
			new AlertDialog.Builder(context)
					.setMessage(msg)
					.setPositiveButton(
							context.getString(R.string.alert_btn_positive),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onAlertClicked(identifier, true);
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									onAlertClicked(identifier, true);
								}
							}).show().setCanceledOnTouchOutside(false);
		} else {
			if (0 == negativeText.length()) {
				negativeText = context.getString(R.string.alert_btn_negative);
			}
			AlertDialog alertWith2Buttons = new AlertDialog.Builder(context)
					.setMessage(msg)
					.setPositiveButton(positiveText,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onAlertClicked(identifier, true);
								}
							})
					.setNegativeButton(negativeText,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									onAlertClicked(identifier, false);
								}
							}).show();
			alertWith2Buttons.setCanceledOnTouchOutside(false);
			alertWith2Buttons.setCancelable(false);
		}
	}

	private void onAlertClicked(String identifier, boolean isPositiveClicked) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("isPositiveClicked", isPositiveClicked);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callBack(identifier, msg);
	}

	void setControllerProperty(final JSONObject data, final String identifier) {
		if (UtilForThread.isCurrentInMainThread(Thread.currentThread())) {
			setControllerPropertyInMainThread(data, identifier);
		} else {
			mainHandler.post(new Runnable() {

				@Override
				public void run() {
					setControllerPropertyInMainThread(data, identifier);
				}
			});
		}

	}

	private void setControllerPropertyInMainThread(JSONObject data,
			String identifier) {
		if (null == data || null == data.optJSONArray("params")) {
			return;
		}
		BaseController controller = null;
		String controllerName = data.optString("controller");
		if (null == controllerName || "".equals(controllerName)) {
			controller = currentController;
		} else {
			for (int i = controllerStack.size() - 1; i >= 0; i--) {
				Map<String, BaseController> map = controllerStack.get(i);
				if (map.containsKey(controllerName)) {
					controller = map.get(controllerName);
					break;
				}
			}
		}
		if (null != controller) {
			controller.setProperty(data.optJSONArray("params"));
		}
	}

	public void callBack(String callBackHandler, Object data) {
		jsEngine.responseCallback(callBackHandler, data);
	}

	public void saveLocal(JSONObject data, String identifier) {
		if (null == data) {
			return;
		}
		String preferencesName = data.optString("key", null);
		JSONObject value = data.optJSONObject("value");
		Iterator<String> iterator = value.keys();
		Map<String, Object> map = new HashMap<String, Object>();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			map.put(key, value.opt(key));
		}
		UtilForDataStorage.savePropertyBySharedPreferences(context,
				preferencesName, map);
	}

	public void readLocal(JSONObject data, String identifier) {
		if (null == data) {
			return;
		}
		String preferencesName = data.optString("key", null);
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context, preferencesName);

		callBack(identifier, new JSONObject(map));
	}

	public void clearLocal(JSONObject data, String identifier) {
		if (null == data) {
			return;
		}
		String preferencesName = data.optString("key");
		UtilForDataStorage.clearPropertyBySharedPreferences(context,
				preferencesName);
		callBack(identifier, null);
	}

	void netConnect(JSONObject params, String identifier) {
		Map<String, String> headerMap = new HashMap<String, String>();
		JSONObject headerJsonObject = params.optJSONObject("header");

		if (null != headerJsonObject) {
			Iterator<String> iterator = headerJsonObject.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				headerMap.put(key, headerJsonObject.optString(key));
			}
		}

		String body = null;
		try {
			body = new String(params.optJSONArray("body").toString()
					.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject response = NetEngine.post(context, body, headerMap);
		callBack(identifier, response);
	}

	public void netConnectInMainThread(final JSONObject params,
			final String identifier) {
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				String action = "";
				if (null != params) {
					action = params.optJSONArray("body").optJSONObject(0)
							.optString("action");
				}
				String message = null;
				if ("merchant/reverse".equals(action)
						|| "merchant/orderCloseRefund".equals(action)) {
					message = context
							.getString(R.string.waiting_dialog_msg_reverse);
				}
				showWaitingDialog(context, message, new Runnable() {

					@Override
					public void run() {
						netConnect(params, identifier);
					}
				});
			}
		});
	}

	public void showWaitingDialog(Context context, String message,
			Runnable runnable) {
		// dw_dialogProgress =
		// context.getResources().getDrawable(R.drawable.animation_dialog_progress);
		dw_dialogProgress = null;
		waitingDialog = new CustomAnimDialog(context);
		waitingDialog.setAnimDrawable(dw_dialogProgress, 60, 60);
		waitingDialog.setMessage(message);
		waitingDialog.showWhileExecuting(runnable);
	}

	void showController(final JSONObject data, final String identifier) {
		if (UtilForThread.isCurrentInMainThread(Thread.currentThread())) {
			showControllerInMainThread(data, identifier);
		} else {
			mainHandler.post(new Runnable() {

				@Override
				public void run() {
					showControllerInMainThread(data, identifier);
				}
			});
		}
	}

	private void showControllerInMainThread(final JSONObject data,
			final String identifier) {
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (null == data) {
					return;
				}
				String name = data.optString("name");
				if ("".equals(name)) {
					if (null != data) {
						Intent intent = new Intent();
						intent.putExtra("result", data.toString());
						currentController.setResult(Activity.RESULT_OK, intent);
					}
					currentController.finish();
				} else if ("first".equals(name)) {
					Intent intent = new Intent();
					JSONObject result = data.optJSONObject("data");
					if (result != null) {
						intent.putExtra("result", result.toString());
					}

					if (currentController != null) {
						currentController.setResult(
								BaseController.RESULT_ORDER_END, intent);
						currentController.finish();
					}
				} else {
					int requestCode = mRequestCode;
					Class<?> controllerClass = classForName(name);
					startController(controllerClass, data, requestCode);
					if (currentController != null
							&& currentController.getClass().equals(
									controllerClass)) {
						currentController.notifyWillshow();
					}
				}
			}
		});
	}

	private void startController(Class<?> cls, JSONObject formData,
			int requestCode) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Boolean shouldRemoveCurCtrl = false;
		if (null != formData) {
			JSONObject data = formData.optJSONObject("data");
			if (null != data) {
				shouldRemoveCurCtrl = data.optBoolean("shouldRemoveCurCtrl");
				if (shouldRemoveCurCtrl) {
					data.remove("shouldRemoveCurCtrl");
					try {
						formData.put("data", data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			intent.putExtra(
					context.getString(R.string.intent_extra_name_formData),
					formData.toString());
		}

		Activity current = currentController != null ? currentController
				: payExController;

		if (shouldRemoveCurCtrl) {
			current.setResult(BaseController.RESULT_START_ACTIVITY, intent);
			current.finish();
		} else {
			current.startActivityForResult(intent, requestCode);
		}
	}

	private Class<?> classForName(String className) {
		if (null == className) {
			return null;
		}

		Class<?> controllerClass = null;
		if (className.equals("Login")) {
			controllerClass = LoginController.class;
		} else if (className.equals("Home")) {
			controllerClass = HomeController.class;
		} else if (className.equals("SetMerchId")) {
			controllerClass = SetMerchIdController.class;
		} else if (className.equals("SetTransId")) {
			controllerClass = SetTransIdController.class;
		} else if (className.equals("MerchantInfo")) {
			controllerClass = MerchantInfoController.class;
		} else if (className.equals("TransactionManageIndex")) {
			controllerClass = TransactionManageIndexController.class;
		} else if (className.equals("ConsumptionRecord")) {
			controllerClass = ConsumptionRecordController.class;
		} else if (className.equals("ConsumptionRecordSearch")) {
			controllerClass = ConsumptionRecordSearchController.class;
		} else if (className.equals("InputDelVoucherNum")) {
			controllerClass = InputDelVoucherNumController.class;
		} else if (className.equals("OrderDetail")) {
			controllerClass = OrderDetailController.class;
		} else if (className.equals("SetMachineId")) {
			controllerClass = SetMachineIdController.class;
		} else if (className.equals("SettingsDownload")) {
			controllerClass = SettingsDownloadController.class;
		} else if (className.equals("SettingsIndex")) {
			controllerClass = SettingsIndexController.class;
		} else if (className.equals("DelVoucherRecordSearch")) {
			controllerClass = DelVoucherRecordSearchController.class;
		} else if (className.equals("DelVoucherInfo")) {
			controllerClass = DelVoucherInfoController.class;
		} else if (className.equals("DelVoucherRecord")) {
			controllerClass = DelVoucherRecordController.class;
		} else if (className.equals("PinPad")) {
			controllerClass = PinPadController.class;
		} else if (className.equals("BalanceResult")) {
			controllerClass = BalanceResultController.class;
		} else if (className.equals("OthersIndex")) {
			controllerClass = OthersIndexController.class;
		} else if (className.equals("InputAmount")) {
			controllerClass = TransAmountController.class;
		} else if (className.equals("PayMethod")) {
			controllerClass = PayMethodController.class;
		} else if (className.equals("PayAccount")) {
			controllerClass = PayAccountController.class;
		} else if (className.equals("DelVoucherId")) {
			controllerClass = DelVoucherIdController.class;
		} else if (className.equals("SingleRecordSearch")) {
			controllerClass = SingleRecordSearchController.class;
		} else if (className.equals("MultiPayIndex")) {
			controllerClass = MultiPayIndex.class;
		} else if (className.equals("MultiPayRecord")) {
			controllerClass = MultiPayRecord.class;
		}

		return controllerClass;
	}

	public void showController(final Class<?> cls) {
		showController(cls, null);
	}

	public void showController(final Class<?> cls, final JSONObject formData) {
		showController(cls, formData, 0);
	}

	public void showController(final Class<?> cls, final JSONObject formData,
			final int requestCode) {
		if (UtilForThread.isCurrentInMainThread(Thread.currentThread())) {
			startController(cls, formData, requestCode);
		} else {
			mainHandler.post(new Runnable() {

				@Override
				public void run() {
					startController(cls, formData, requestCode);
				}
			});
		}
	}

	public void showWaitingDialogWhenRun(final Runnable runnable,
			final String message) {
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				showWaitingDialog(context, message, runnable);
			}
		});
	}

	void get8583(final JSONObject jsonObject, final String callBackId) {
		Log.d(TAG, "get8583 jsonObject : " + jsonObject);
		ISO8583Controller iso8583Controller = ISO8583Engine.getInstance()
				.generateISO8583Controller();
		String typeOf8583 = jsonObject.optString("typeOf8583");

		try {
			if (typeOf8583.equals("pay")) {
				iso8583Controller.purchase(jsonObject);
			} else if (typeOf8583.equals("login")) {
				iso8583Controller.login();
			} else if (typeOf8583.equals("chongZheng")) {
				String data8583 = jsonObject.optString("data8583");
				String transDate = jsonObject.optString("transDate");
				String subType = jsonObject.optString("subType");
				iso8583Controller.chongZheng(Utility.hex2byte(data8583),
						transDate, subType);
			} else if (typeOf8583.equals("cheXiao")) {
				String data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.cheXiao(Utility.hex2byte(data8583),
						jsonObject);
			} else if (typeOf8583.equals("refund")) {
				String data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller
						.refund(Utility.hex2byte(data8583), jsonObject);
			} else if (typeOf8583.equals("chaxunyue")) {
				String cardID = jsonObject.optString("cardID");
				String track2 = jsonObject.optString("track2");
				String track3 = jsonObject.optString("track3");
				String balancePwd = jsonObject.optString("balancePwd");

				String openBrh = jsonObject.optString("openBrh");
				String paymentId = jsonObject.optString("paymentId");

				iso8583Controller.purchaseChaXun(cardID, track2, track3,
						Utility.hex2byte(balancePwd), openBrh, paymentId);
			}

			String data8583 = iso8583Controller.toString();
			JSONObject businessJsonObject = new JSONObject();
			try {
				businessJsonObject.put("data8583", data8583);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "get8583 : " + data8583);
			callBack(callBackId, businessJsonObject);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	void convert8583(final JSONObject jsonObjData, final String callBackId) {
		JSONObject data8583JsonObject = new JSONObject();
		try {
			String data8583 = jsonObjData.optString("data8583");

			ISO8583Controller iso8583Controller = ISO8583Engine.getInstance()
					.generateISO8583Controller();
			Log.d(TAG, "load8583 data8583 : " + data8583);
			boolean load8583Result = iso8583Controller.load(Utility
					.hex2byte(data8583));
			Log.d(TAG, "load8583 load8583Result : " + load8583Result);
			String resCode = iso8583Controller.getResCode();
			Log.d(TAG, "load8583 resCode : " + resCode);
			String resMessage = HostMessage.getMessage(resCode);
			Log.d(TAG, "load8583 resMessage : " + resMessage);
			data8583JsonObject.put("resCode", resCode);
			data8583JsonObject.put("resMessage", resMessage);
			data8583JsonObject.put("rrn", iso8583Controller.getRRN());
			data8583JsonObject.put("apOrderId",
					iso8583Controller.getApOrderId());
			data8583JsonObject.put("payOrderBatch",
					iso8583Controller.getBatch());
			data8583JsonObject.put("transTime",
					iso8583Controller.getTransTime());

		} catch (Exception e) {
			e.printStackTrace();
		}
		callBack(callBackId, data8583JsonObject);
	}

	void getBalance(final String callBackId) {
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				showWaitingDialog(context, null, new Runnable() {

					@Override
					public void run() {
						JSONObject data8583JsonObject = new JSONObject();
						try {
							ISO8583Controller iso8583Controller = ISO8583Engine
									.getInstance().generateISO8583Controller();
							String balance = iso8583Controller.getBanlance();
							Log.d(TAG, "load8583 balance" + balance);
							data8583JsonObject.put("balance", balance);
						} catch (Exception e) {
							e.printStackTrace();
						}
						callBack(callBackId, data8583JsonObject);
					}
				});
			}
		});
	}

	public void print(final JSONObject jsonObjData, final Context context) {
		Thread printThread = new Thread(new Runnable() {

			@Override
			public void run() {
				ISO8583Controller iso8583Controller = ISO8583Engine
						.getInstance().generateISO8583Controller();
				try {
					String req8583 = jsonObjData.optString("req8583");
					String res8583 = jsonObjData.optString("res8583");
					String userName = jsonObjData.optString("userName");
					Log.d(TAG, "print req8583 : " + req8583);
					Log.d(TAG, "print res8583 : " + res8583);
					if (!"".equals(req8583) && !"".equals(res8583)) {
						iso8583Controller.printer(Utility.hex2byte(req8583),
								Utility.hex2byte(res8583), userName, context);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		printThread.start();
	}

	public void insertTransData8583(final JSONObject jsonObjData,
			final String callBackId) {
		String apOrderId = jsonObjData.optString("ref");
		String req8583 = jsonObjData.optString("req8583");
		String res8583 = jsonObjData.optString("res8583");
		String databaseName = "TransData8583";
		String tableName = "transData";
		SQLiteDatabase db = context.openOrCreateDatabase(databaseName,
				Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
				+ " (apOrderId TEXT primary key,req8583 TEXT,res8583 TEXT)");
		ContentValues cv = new ContentValues();
		cv.put("apOrderId", apOrderId);
		cv.put("req8583", req8583);
		cv.put("res8583", res8583);
		db.insert(tableName, null, cv);
		db.close();
		callBack(callBackId, jsonObjData);
	}

	public void updateTransData8583(final JSONObject jsonObjData,
			final String callBackId) {
		String apOrderId = jsonObjData.optString("ref");
		String req8583 = jsonObjData.optString("req8583");
		String res8583 = jsonObjData.optString("res8583");
		String databaseName = "TransData8583";
		String tableName = "transData";
		SQLiteDatabase db = context.openOrCreateDatabase(databaseName,
				Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
				+ " (apOrderId TEXT primary key,req8583 TEXT,res8583 TEXT)");
		String sql = "Update " + tableName + " set req8583 = " + "'" + req8583
				+ "'" + ",res8583 = " + "'" + res8583 + "'"
				+ " where apOrderId = " + "'" + apOrderId + "'";
		db.execSQL(sql);
		db.close();
		callBack(callBackId, jsonObjData);
	}

	public void getTransData8583(final JSONObject jsonObjData,
			final String callBackId) {
		String apOrderId = jsonObjData.optString("ref");
		String databaseName = "TransData8583";
		SQLiteDatabase db = context.openOrCreateDatabase(databaseName,
				Context.MODE_PRIVATE, null);
		String tableName = "transData";
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
				+ " (apOrderId TEXT primary key,req8583 TEXT,res8583 TEXT)");
		Cursor c = db.rawQuery("SELECT req8583,res8583 FROM " + tableName
				+ " WHERE apOrderId=" + apOrderId, null);
		String req8583 = "";
		String res8583 = "";
		if (c.moveToFirst()) {
			req8583 = c.getString(c.getColumnIndex("req8583"));
			res8583 = c.getString(c.getColumnIndex("res8583"));
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("req8583", req8583);
			msg.put("res8583", res8583);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		db.close();
		callBack(callBackId, msg);
	}

}
