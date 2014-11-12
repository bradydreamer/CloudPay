package cn.koolcloud.pos;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.control.ISO8583Controller;
import cn.koolcloud.jni.EmvL2Interface;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.controller.HomeController;
import cn.koolcloud.pos.controller.PinPadController;
import cn.koolcloud.pos.controller.delivery_voucher.DelVoucherIdController;
import cn.koolcloud.pos.controller.delivery_voucher.DelVoucherInfoController;
import cn.koolcloud.pos.controller.delivery_voucher.InputDelVoucherNumController;
import cn.koolcloud.pos.controller.dialogs.AlertCommonDialog;
import cn.koolcloud.pos.controller.mispos.MisposController;
import cn.koolcloud.pos.controller.multipay.MultiPayIndex;
import cn.koolcloud.pos.controller.multipay.MultiPayRecord;
import cn.koolcloud.pos.controller.others.BalanceResultController;
import cn.koolcloud.pos.controller.others.OthersIndexController;
import cn.koolcloud.pos.controller.others.settings.CreateUserController;
import cn.koolcloud.pos.controller.others.settings.ListUserInfoController;
import cn.koolcloud.pos.controller.others.settings.LoginController;
import cn.koolcloud.pos.controller.others.settings.LoginVerifyController;
import cn.koolcloud.pos.controller.others.settings.MerchantInfoController;
import cn.koolcloud.pos.controller.others.settings.ModifyPWDController;
import cn.koolcloud.pos.controller.others.settings.PaymentMechanismController;
import cn.koolcloud.pos.controller.others.settings.SetMachineIdController;
import cn.koolcloud.pos.controller.others.settings.SetMerchIdController;
import cn.koolcloud.pos.controller.others.settings.SetTransIdController;
import cn.koolcloud.pos.controller.others.settings.SettingsDownloadController;
import cn.koolcloud.pos.controller.others.settings.SigninController;
import cn.koolcloud.pos.controller.others.settings.TransBatchController;
import cn.koolcloud.pos.controller.pay.PayAccountController;
import cn.koolcloud.pos.controller.pay.PayMethodController;
import cn.koolcloud.pos.controller.pay.TransAmountController;
import cn.koolcloud.pos.controller.prepaid_card.PrepaidCardQRCodeController;
import cn.koolcloud.pos.controller.prepaid_card.PrepaidCardSearchResultController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.ConsumptionRecordController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.ConsumptionRecordSearchController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.OrderDetailController;
import cn.koolcloud.pos.controller.transaction_manage.consumption_record.SingleRecordSearchController;
import cn.koolcloud.pos.controller.transaction_manage.del_voucher.DelVoucherRecordController;
import cn.koolcloud.pos.controller.transaction_manage.del_voucher.DelVoucherRecordSearchController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.external.EMVICManager;
import cn.koolcloud.pos.net.NetEngine;
import cn.koolcloud.pos.secure.SecureEngine;
import cn.koolcloud.pos.service.CouponInfo;
import cn.koolcloud.pos.service.ICouponService;
import cn.koolcloud.pos.service.IMerchService;
import cn.koolcloud.pos.service.ISecureService;
import cn.koolcloud.pos.service.MerchInfo;
import cn.koolcloud.pos.service.SecureInfo;
import cn.koolcloud.pos.service.SmartIposRunBackground;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForGraghic;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.pos.util.UtilForThread;
import cn.koolcloud.pos.wd.R;
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
	private static final long SESSION_TIMEOUT = 8 * 60;// 分钟
	private Thread sessionThread = null;

	// private ISecureService getSummarySecureService;

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

	/*
	 * public void setBackgroundService(ISecureService mService) {
	 * this.getSummarySecureService = mService; }
	 */

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

	public ISecureService getSecureService() {
		return mSecureService;
	}

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

	private ICouponService mCouponService;

	private ServiceConnection couponConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mCouponService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mCouponService = ICouponService.Stub.asInterface(service);
		}
	};

	public void initEngine() {
		Context context = MyApplication.getContext();
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

		Intent couponIntent = new Intent(ICouponService.class.getName());
		couponIntent.setAction("com.koolyun.coupon.service.permission.COUPON");
		boolean result_coupon = context.bindService(couponIntent,
				couponConnection, Context.BIND_AUTO_CREATE);

		if (!result_coupon) {
			// Toast.makeText(context, "coupon 服务绑定失败。",
			// Toast.LENGTH_SHORT).show();
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
		jsEngine.loadJs("Others/Settings/SignIn");
		jsEngine.loadJs("Others/Settings/TransBatch");
		jsEngine.loadJs("Others/Settings/Login/index");
		jsEngine.loadJs("Others/Settings/index");
		jsEngine.loadJs("Others/Settings/settingsDownload");
		jsEngine.loadJs("Common/home");

	}

	public JavaScriptEngine javaScriptEngine() {
		return jsEngine;
	}

	public SecureEngine secureEngine() {
		return secureEngine;
	}

	public void serviceMerchantInfo(JSONObject data, String identifier) {
		String action = data.optString("action");
		boolean isSetAction = action.equalsIgnoreCase("set");

		JSONObject result = null;
		if (isSetAction) {

			String valueStr = data.optString("value");
			if (!TextUtils.isEmpty(valueStr)) {
				try {
					JSONObject valueObj = new JSONObject(valueStr);
					String merchName = valueObj.optString("merchName");
					String merchId = valueObj.optString("merchId");
					String terminalId = valueObj.optString("machineId");
					MerchInfo merchInfo = new MerchInfo(merchName, merchId,
							terminalId);
					setMerchInfo(merchInfo);
					callBack(identifier, null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} else {
			// FIXME: get merchant info from service
			result = getServiceSecureInfo();
		}

		if (!identifier.isEmpty()) {
			callBack(identifier, result);
		}
	}

	public void serviceSecureInfo(JSONObject data, String identifier) {
		String action = data.optString("action");
		boolean isSetAction = action.equalsIgnoreCase("set");

		JSONObject result = null;
		if (isSetAction) {
			setServiceSecureInfo(data.optJSONObject("value"));
			callBack(identifier, null);
		} else {
			result = getServiceSecureInfo();
		}

		if (!identifier.isEmpty()) {
			callBack(identifier, result);
		}
	}

	private void setServiceSecureInfo(JSONObject value) {
		if (mSecureService != null && value != null) {
			try {
				mSecureService.setUserInfo(value.toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private JSONObject getServiceSecureInfo() {
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

	public void startSessionTest() {
		if (sessionThread == null) {

			sessionThread = new Thread(new Runnable() {

				@Override
				public void run() {
					JavaScriptEngine js = null;
					while (true) {
						Date now = new Date();
						long curTime = now.getTime();
						Map<String, ?> map = UtilForDataStorage
								.readPropertyBySharedPreferences(
										MyApplication.getContext(), "merchant");
						Long lastTouchTime = (Long) map.get("curTouchTime");
						Long period_min = (curTime - lastTouchTime) / 1000 / 60;
						Log.i(TAG,
								"Session Test---------------Through the time:"
										+ period_min);
						if (SESSION_TIMEOUT - period_min < 30) {
							if (js == null) {
								js = ClientEngine.engineInstance()
										.javaScriptEngine();
							}
							js.callJsHandler("Home.checkSessionByEchoTest",
									null);
						}
						try {
							Thread.sleep(1000 * 60 * 2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							sessionThread = null;
							break;
						}
					}
				}
			});
			sessionThread.start();
		}
	}

	public void stopSessionTest() {
		if (sessionThread != null) {
			sessionThread.interrupt();
		}
		Log.i(TAG, "stopSessionTest success--------------");
	}

	public void deleteParamsFiles() {
		File file1 = new File("/mnt/sdcard/aid_param.ini");
		if (file1.exists()) {
			file1.delete();
		}
		File file2 = new File("/mnt/sdcard/ca_param.ini");
		if (file2.exists()) {
			file2.delete();
		}
	}

	public Boolean paramsFilesIsExists() {
		Boolean existsTag = false;
		File file1 = new File("/mnt/sdcard/aid_param.ini");
		File file2 = new File("/mnt/sdcard/ca_param.ini");
		if (!file1.exists() || !file2.exists()) {
			file1.delete();
			file2.delete();
			existsTag = false;
		} else {
			existsTag = true;
		}
		Log.i(TAG, "===,paramsFilesIsExists,return:" + existsTag);
		return existsTag;
	}

	public void showAlert(final JSONObject data, final String identifier) {
		String msg = data.optString("msg");
		if (msg.startsWith("JSLOG")) {
			Log.i(TAG, msg);
			// Logger.i(msg);
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

		// use styled alert dialog start on 16th June
		Intent mIntent = new Intent(context, AlertCommonDialog.class);
		mIntent.putExtra(ConstantUtils.POSITIVE_BTN_KEY, positiveText);
		mIntent.putExtra(ConstantUtils.NEGATIVE_BTN_KEY, negativeText);
		mIntent.putExtra(ConstantUtils.MSG_KEY, msg);
		mIntent.putExtra(ConstantUtils.IDENTIFIER_KEY, identifier);
		context.startActivity(mIntent);
		// use styled alert dialog end on 16th June

		/*
		 * if (null == negativeText) { new AlertDialog.Builder(context)
		 * .setMessage(msg) .setPositiveButton(
		 * context.getString(R.string.alert_btn_positive), new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * onAlertClicked(identifier, true); } }) .setOnCancelListener( new
		 * DialogInterface.OnCancelListener() {
		 * 
		 * @Override public void onCancel(DialogInterface dialog) {
		 * onAlertClicked(identifier, true); }
		 * }).show().setCanceledOnTouchOutside(false); } else { if (0 ==
		 * negativeText.length()) { negativeText =
		 * context.getString(R.string.alert_btn_negative); } AlertDialog
		 * alertWith2Buttons = new AlertDialog.Builder(context) .setMessage(msg)
		 * .setPositiveButton(positiveText, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * onAlertClicked(identifier, true); } })
		 * .setNegativeButton(negativeText, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * onAlertClicked(identifier, false); } }).show();
		 * alertWith2Buttons.setCanceledOnTouchOutside(false);
		 * alertWith2Buttons.setCancelable(false); }
		 */
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

	public void saveLocal(JSONObject data, String identifier, Context context) {
		if (null == data) {
			return;
		}
		String keyStr = data.optString("key");
		if (!TextUtils.isEmpty(keyStr) && keyStr.equals("batchCache")) {
			// String txnId = data.optString("txnId");
			// BatchTaskBean batchTask = new BatchTaskBean();
			// batchTask.setAuthCode(data.optString("authNo"));
			// batchTask.setExpDate(data.optString("dateExpr"));
			// batchTask.setIssuerId(data.optString("issuerId"));
			// batchTask.setRefrenceRetrievalNumber(data.optString("refNo"));
			// batchTask.setResponseCode(data.optString("resCode"));
			// batchTask.setResponseMsg(data.optString("resMsg"));
			// batchTask.setSettlementDate(data.optString("stlmDate"));
			// batchTask.setTxnId(data.optString("txnId"));
			JSONObject batchObj = null;
			try {
				String batchStr = data.getString("value");
				if (!TextUtils.isEmpty(batchStr)) {
					batchObj = new JSONObject(batchStr);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (batchObj != null) {

				CacheDB cacheDB = CacheDB.getInstance(context);
				cacheDB.insertBatchTask(batchObj);
			}

		} else {

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

	public void readLocalBatch(String identifier, Context context) {
		CacheDB cacheDB = CacheDB.getInstance(context);
		JSONArray jsArray = cacheDB.selectAllBatchStack();
		callBack(identifier, jsArray);
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

	public void rmBachCache(JSONObject data, Context context) {
		if (null == data) {
			return;
		}
		String pkId = data.optString("pk_id");
		CacheDB cacheDB = CacheDB.getInstance(context);
		// remove local cached batch task
		cacheDB.deleteBatchTaskByPKId(pkId);
	}

	/**
	 * save merchSetting info to database
	 * 
	 * @Title: saveToDataBase
	 * @Description: TODO
	 * @param data
	 * @param context
	 * @return: void
	 */
	public void saveMerchSettingToDataBase(String identifier, Context context) {
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context, "merchSettings");
		JSONArray jsonArray = null;
		if (map.containsKey("settingString")) {
			String jsArrayStr = String.valueOf(map.get("settingString"));
			CacheDB cacheDB = CacheDB.getInstance(context);
			try {
				jsonArray = new JSONArray(jsArrayStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// clear acquire institute table
			cacheDB.clearAcquireInstituteTableData();

			List<AcquireInstituteBean> acquireList = UtilForJSON
					.parseJsonArray2AcquireInstitute(jsonArray);
			if (acquireList != null && acquireList.size() > 0) {
				cacheDB.insertAcquireInstitute(acquireList);
			}

			// clear cached payment table
			cacheDB.clearPaymentActivityTableData();
			List<AcquireInstituteBean> acquireJsonList = UtilForJSON
					.parseJsonArray2AcquireInstituteWithJson(jsonArray);
			if (acquireJsonList != null && acquireJsonList.size() > 0) {
				cacheDB.insertPayment(acquireJsonList);
			}
		}

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

		// fix update search record status while refund or reverse operating
		// --start Teddy on 20th May
		/*
		 * String startDate =
		 * params.optJSONArray("body").optJSONObject(0).optString("startDate");
		 * String endDate =
		 * params.optJSONArray("body").optJSONObject(0).optString("endDate");
		 * try {
		 * response.optJSONArray("body").optJSONObject(0).put("start_date",
		 * startDate);
		 * response.optJSONArray("body").optJSONObject(0).put("end_date",
		 * endDate); } catch (JSONException e) { e.printStackTrace(); }
		 */

		// fix update search record status while refund or reverse operating
		// --end Teddy on 20th May

		// try {
		// JSONArray mbody = response.getJSONArray("body");
		// JSONObject mrp = mbody.getJSONObject(0);
		// JSONArray arr = mrp.getJSONArray("recordList");
		// String txnId;
		// for (int i = 0; i < arr.length(); i++) {
		// JSONObject jo = arr.getJSONObject(i);
		// txnId = jo.optString("txnId");
		// jo.put("txnId", txnId);
		// }
		// } catch (JSONException e) { // TODO Auto-generated catch block
		// // e.printStackTrace();
		// callBack(identifier, response);
		// }

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
				if ("msc/pay/reverse".equals(action)
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
				} else if ("Coupon".equals(name)) {
					if (Env.checkApkExist(context,
							ConstantUtils.COUPON_APP_PACKAGE_NAME)) {
						if (mCouponService != null) {
							JSONObject couponData = data.optJSONObject("data");
							String couponDataType = couponData
									.optString("coupon_type");
							String transAmount = couponData
									.optString("transAmount");
							String couponType = "";
							if (!TextUtils.isEmpty(couponDataType)
									&& couponDataType.equals("rm_coupon")) {
								couponType = "1";
							} else {
								couponType = "0";
							}

							CouponInfo payResult;
							try {
								if (!TextUtils.isEmpty(transAmount)) {

									// mCouponService.startCoupon parameters：
									// transAmount – not null, total pay
									// packagename – invoker app packagename,
									// could be null
									// orderNo – order number , could be null
									// orderDesc – order short description,
									// could be null
									// typeAction – give coupon or check coupon
									// (eg. 0:give coupon, 1: check coupon)
									payResult = mCouponService.startCoupon(
											transAmount, couponType, null,
											null, null);
									if (payResult != null
											&& !TextUtils
													.isEmpty(couponDataType)) {
										Log.e("count",
												"count:"
														+ payResult
																.getCouponAmount());
										Log.e("payResult", "payResult:"
												+ payResult.getResult());

										// TODO:write back to APMP, and organize
										// amount data.
										writeBackAPMPCouponData(couponData,
												payResult.getCouponAmount(),
												payResult.getResult());
									} else {
										Log.e("payResult", "return null value!");
									}
								} else {
									Toast.makeText(
											context,
											context.getResources()
													.getString(
															R.string.str_coupon_amount_null),
											Toast.LENGTH_LONG).show();
								}
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(
									context,
									context.getResources()
											.getString(
													R.string.str_coupon_service_binding),
									Toast.LENGTH_LONG).show();
						}
					} else {
						try {
							JSONObject jsObj = new JSONObject();
							jsObj.put(
									"msg",
									context.getResources()
											.getString(
													R.string.str_coupon_app_not_installed));
							showAlert(jsObj, "");
						} catch (NotFoundException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
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

	private void writeBackAPMPCouponData(JSONObject couponData,
			String couponAmount, String couponNum) {
		String transTime = null;
		String transType = null;
		String batchNo = null;
		String traceNo = null;
		int traceId = 0;
		String resCode = "00";
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		System.setProperty("user.timezone", "GMT+8");
		transTime = dateFormat.format(now);
		transType = "1021";
		DecimalFormat dataFormat = new DecimalFormat("000000");

		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(MyApplication.getContext(),
						"merchant");
		if (null == map.get("transId")) {
			traceNo = "0";
		} else {
			traceId = ((Integer) map.get("transId")).intValue();
			traceNo = dataFormat.format(traceId);
		}
		if (null == map.get("batchId")) {
			batchNo = "0";
		} else {
			batchNo = dataFormat.format(((Integer) map.get("batchId"))
					.intValue());
		}

		Map<String, Object> newMerchantMap = new HashMap<String, Object>();
		newMerchantMap.put("transId", Integer.valueOf(traceId + 1));
		UtilForDataStorage.savePropertyBySharedPreferences(
				MyApplication.getContext(), "merchant", newMerchantMap);

		JSONObject msg = new JSONObject();
		try {
			msg.put("transAmount",
					UtilForMoney.yuan2fen(couponData.optString("transAmount")));
			msg.put("couponPaidAmount", UtilForMoney.yuan2fen(couponAmount));
			msg.put("couponNum", couponNum);
			msg.put("transTime", transTime);
			msg.put("transType", transType);
			msg.put("batchNo", batchNo);
			msg.put("traceNo", traceNo);
			msg.put("resCode", resCode);
			msg.put("keyIndex", couponData.optString("payKeyIndex"));
			msg.put("paymentId", couponData.optString("paymentId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (jsEngine != null) {
			jsEngine.callJsHandler("window.Pay.writeBackAPMPCouponData", msg);
		}
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
		if (className.equals("LoginVerify")) {
			controllerClass = LoginVerifyController.class;
		} else if (className.equals("Signin")) {
			controllerClass = SigninController.class;
		} else if (className.equals("Home")) {
			controllerClass = HomeController.class;
		} else if (className.equals("SetMerchId")) {
			controllerClass = SetMerchIdController.class;
		} else if (className.equals("SetTransId")) {
			controllerClass = SetTransIdController.class;
		} else if (className.equals("ModifyPwd")) {
			controllerClass = ModifyPWDController.class;
		} else if (className.equals("CreateUser")) {
			controllerClass = CreateUserController.class;
		} else if (className.equals("TransBatch")) {
			controllerClass = TransBatchController.class;
		} else if (className.equals("MerchantInfo")) {
			controllerClass = MerchantInfoController.class;
		} else if (className.equals("Login")) {
			controllerClass = LoginController.class;
		} else if (className.equals("UsersList")) {
			controllerClass = ListUserInfoController.class;
		} else if (className.equals("ConsumptionRecord")) {
			controllerClass = ConsumptionRecordController.class;
		} else if (className.equals("ConsumptionRecordSearch")) {
			controllerClass = ConsumptionRecordSearchController.class;
		} else if (className.equals("PrepaidCardQRCodeController")) {
			controllerClass = PrepaidCardQRCodeController.class;
		} else if (className.equals("PrepaidCardSearchResultController")) {
			controllerClass = PrepaidCardSearchResultController.class;
		} else if (className.equals("PaymentMechanism")) {
			controllerClass = PaymentMechanismController.class;
		} else if (className.equals("InputDelVoucherNum")) {
			controllerClass = InputDelVoucherNumController.class;
		} else if (className.equals("OrderDetail")) {
			controllerClass = OrderDetailController.class;
		} else if (className.equals("SetMachineId")) {
			controllerClass = SetMachineIdController.class;
		} else if (className.equals("SettingsDownload")) {
			controllerClass = SettingsDownloadController.class;
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
		} else if (className.equals("MisposController")) {
			controllerClass = MisposController.class;
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
		String data8583;
		String transType;
		String batchNo;
		String traceNo;
		String transTime;
		String cardNo;
		Long transAmount;
		String oriBatchNo;
		String oriTraceNo;
		String oriTransTime;
		JSONObject businessJsonObject = new JSONObject();
		Log.d(TAG, "get8583 jsonObject : " + jsonObject);
		ISO8583Controller iso8583Controller = ISO8583Engine.getInstance()
				.generateISO8583Controller();
		String typeOf8583 = jsonObject.optString("typeOf8583");
		String oriTxnId = jsonObject.optString("oriTxnId");
		if (oriTxnId.equals("")) {
			oriTxnId = null;
		}
		String paymentId = jsonObject.optString("paymentId");
		if (paymentId.equals("")) {
			paymentId = null;
		}

		try {
			if (typeOf8583.equals("pay")) {
				iso8583Controller.purchase(jsonObject);
			} else if (typeOf8583.equals("preAuth")) {
				iso8583Controller.preAuth(jsonObject);
			} else if (typeOf8583.equals("signin")) {
				iso8583Controller.signin();
			} else if (typeOf8583.equals("signout")) {
				iso8583Controller.signout();
			} else if (typeOf8583.equals("transBatch")) {
				iso8583Controller.transBatch();
			} else if (typeOf8583.equals("chongZheng")) {
				data8583 = jsonObject.optString("data8583");
				String transDate = jsonObject.optString("transDate");
				String subType = jsonObject.optString("subType");
				iso8583Controller.chongZheng(Utility.hex2byte(data8583),
						transDate, subType);
			} else if (typeOf8583.equals("cheXiao")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.cheXiao(Utility.hex2byte(data8583),
						jsonObject);
			} else if (typeOf8583.equals("refund")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller
						.refund(Utility.hex2byte(data8583), jsonObject);
			} else if (typeOf8583.equals("chaxunyue")) {
				String cardID = jsonObject.optString("cardID");
				String track2 = jsonObject.optString("track2");
				String track3 = jsonObject.optString("track3");
				String balancePwd = jsonObject.optString("balancePwd");

				String openBrh = jsonObject.optString("openBrh");
				paymentId = jsonObject.optString("paymentId");

				iso8583Controller.purchaseChaXun(cardID, track2, track3,
						balancePwd, openBrh, paymentId);
			} else if (typeOf8583.equals("preAuthComplete")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.preAuthComplete(Utility.hex2byte(data8583),
						jsonObject);
			} else if (typeOf8583.equals("preAuthSettlement")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.preAuthSettlement(Utility.hex2byte(data8583),
						jsonObject);
			} else if (typeOf8583.equals("preAuthCancel")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.preAuthCancel(Utility.hex2byte(data8583),
						jsonObject);
			} else if (typeOf8583.equals("preAuthCompleteCancel")) {
				data8583 = jsonObject.optString("transData8583");
				jsonObject.remove("transData8583");
				iso8583Controller.preAuthCompleteCancel(
						Utility.hex2byte(data8583), jsonObject);
			} else if (typeOf8583.equals("posUpStatus")) {
				iso8583Controller.posUpStatus(jsonObject);
			} else if (typeOf8583.equals("downloadParams")) {
				iso8583Controller.downloadParams(jsonObject);
			} else if (typeOf8583.equals("downloadEnd")) {
				iso8583Controller.endDownloadParams(jsonObject);
			}
			data8583 = iso8583Controller.toString();
			transType = iso8583Controller.getApmpTransType();
			batchNo = iso8583Controller.getBatchNum();
			traceNo = iso8583Controller.getTraceNum();
			transTime = iso8583Controller.getCurrentTime();
			cardNo = iso8583Controller.getBankCardNum();
			transAmount = iso8583Controller.getTransAmount();
			oriBatchNo = iso8583Controller.getOriBatchNum();
			oriTraceNo = iso8583Controller.getOriTraceNum();
			oriTransTime = iso8583Controller.getOriTransTime();
			try {
				businessJsonObject.put("data8583", data8583);
				businessJsonObject.put("paymentId", paymentId);
				businessJsonObject.put("transType", transType);
				businessJsonObject.put("batchNo", batchNo);
				businessJsonObject.put("traceNo", traceNo);
				businessJsonObject.put("transTime", transTime);
				businessJsonObject.put("cardNo", cardNo);
				businessJsonObject.put("transAmount", transAmount);
				businessJsonObject.put("oriTxnId", oriTxnId);
				businessJsonObject.put("oriBatchNo", oriBatchNo);
				businessJsonObject.put("oriTraceNo", oriTraceNo);
				businessJsonObject.put("oriTransTime", oriTransTime);
			} catch (JSONException e) {
				businessJsonObject.put("error", "ERROR");
				callBack(callBackId, businessJsonObject);
				e.printStackTrace();
			}
			Log.d(TAG, "get8583 : " + data8583);
			callBack(callBackId, businessJsonObject);
		} catch (Exception e1) {
			try {
				businessJsonObject.put("error", "ERROR");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			callBack(callBackId, businessJsonObject);
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
			/*-------------------for ic trans--------------------*/
			UtilFor8583 uf8 = UtilFor8583.getInstance();
			EMVICManager emvICManager = EMVICManager.getEMVICManagerInstance();
			String resCodeStr = "";
			int res = -1;
			int cAuthCode_len = 0;
			byte[] cAuthCode = null;
			int trade_ret = -1;
			if (uf8.trans.getEntryMode() == ConstantUtils.ENTRY_IC_MODE) {
				// TODO:后续可能再加上类型。
				if (uf8.trans.getApmpTransType() == iso8583Controller.APMP_TRAN_CONSUMECANCE
						|| uf8.trans.getApmpTransType() == iso8583Controller.APMP_TRAN_PRAUTHCANCEL
						|| uf8.trans.getApmpTransType() == iso8583Controller.APMP_TRAN_PRAUTHCOMPLETE
						|| uf8.trans.getApmpTransType() == iso8583Controller.APMP_TRAN_PRAUTHSETTLEMENT
						|| uf8.trans.getApmpTransType() == iso8583Controller.APMP_TRAN_PREAUTHCOMPLETECANCEL) {
					if (uf8.trans.getAuthCode() == null
							|| uf8.trans.getAuthCode().equals("")) {
						cAuthCode = null;
						cAuthCode_len = 0;
					}
					res = EmvL2Interface.recvOnlineMessage(null, (char) (0),
							null, (char) (0), cAuthCode,
							(char) (cAuthCode_len),
							uf8.trans.getResponseCode(),
							(char) (uf8.trans.getResponseCode().length));
					Log.i("convert8583", "===,recvOnlineMessage,ker_ret:" + res);
				} else {
					res = iso8583Controller.getICTranserMsgResult();
				}
				if (res >= 0) {
					trade_ret = emvICManager.tradeEnd();
					Log.i("convert8583", "===,tradeEnd,trade_ret:" + trade_ret);
					if (trade_ret < 0) {
						if (Arrays.equals(uf8.trans.getResponseCode(),
								"98".getBytes())
								|| Arrays.equals(uf8.trans.getResponseCode(),
										"00".getBytes())) {
							if (trade_ret == -12009) {
								uf8.trans.setResponseCode("C2".getBytes());
							} else if (trade_ret == -12010) {
								uf8.trans.setResponseCode("C3".getBytes());
							} else if (trade_ret == -12011) {
								uf8.trans.setResponseCode("C4".getBytes());
							} else if (trade_ret == -1) {
								uf8.trans.setResponseCode("C1".getBytes());
							}
						} else if (Arrays.equals(uf8.trans.getResponseCode(),
								"22".getBytes())
								|| Arrays.equals(uf8.trans.getResponseCode(),
										"55".getBytes())) {
							// 返回原响应码
						} else if (Arrays.equals(uf8.trans.getResponseCode(),
								"12".getBytes())
								|| Arrays.equals(uf8.trans.getResponseCode(),
										"40".getBytes())) {
							if (trade_ret == -12009) {
								uf8.trans.setResponseCode("B2".getBytes());
							} else if (trade_ret == -12010) {
								uf8.trans.setResponseCode("B3".getBytes());
							} else if (trade_ret == -12011) {
								uf8.trans.setResponseCode("B4".getBytes());
							} else if (trade_ret == -1) {
								uf8.trans.setResponseCode("B1".getBytes());
							}
						} else {
							if (trade_ret == -12009) {
								uf8.trans.setResponseCode("D2".getBytes());
							} else if (trade_ret == -12010) {
								uf8.trans.setResponseCode("D3".getBytes());
							} else if (trade_ret == -12011) {
								uf8.trans.setResponseCode("C4".getBytes());
							} else if (trade_ret == -1) {
								uf8.trans.setResponseCode("D1".getBytes());
							}
						}
					} else {
						// 交易成功，返回原响应码
					}
					// for chongzheng test:
					// uf8.trans.setResponseCode("C1".getBytes());
					resCodeStr += String.format("%02X ",
							uf8.trans.getResponseCode()[0]);
					resCodeStr += String.format("%02X ",
							uf8.trans.getResponseCode()[1]);
					Log.i("convert8583", "===,after tradeEnd,responseCode:"
							+ resCodeStr);
				} else if (res == -12009) {
					if (Arrays.equals(uf8.trans.getResponseCode(),
							"00".getBytes())
							|| Arrays.equals(uf8.trans.getResponseCode(),
									"98".getBytes())) {
						uf8.trans.setResponseCode("C2".getBytes());
					} else if (Arrays.equals(uf8.trans.getResponseCode(),
							"22".getBytes())
							|| Arrays.equals(uf8.trans.getResponseCode(),
									"55".getBytes())) {
						// 返回原响应码
					} else {
						uf8.trans.setResponseCode("D2".getBytes());
					}
					// for chongzheng test:
					// uf8.trans.setResponseCode("C1".getBytes());
					resCodeStr += String.format("%02X ",
							uf8.trans.getResponseCode()[0]);
					resCodeStr += String.format("%02X ",
							uf8.trans.getResponseCode()[1]);
					Log.i("convert8583", "===,after tradeEnd,responseCode:"
							+ resCodeStr);
				}
				uf8.trans.setEntryMode((byte) 0);
			}
			String resCode = iso8583Controller.getResCode();
			Log.d(TAG, "load8583 resCode : " + resCode);
			String resMessage = HostMessage.getMessage(resCode);
			Log.d(TAG, "load8583 resMessage : " + resMessage);
			data8583JsonObject.put("resCode", resCode);
			data8583JsonObject.put("resMessage", resMessage);
			data8583JsonObject.put("rrn", iso8583Controller.getApOrderId());// 使用机构的参考号
			data8583JsonObject.put("paramDownloadFlag",
					iso8583Controller.getParamDownloadFlag());
			data8583JsonObject.put("paramsCapkDownloadNeed",
					iso8583Controller.getParamsCapkDownloadNeed());
			data8583JsonObject.put("paramsCapkCheckNeed",
					iso8583Controller.getIcParamsCapkCheckNeed());
			data8583JsonObject.put("apOrderId",
					iso8583Controller.getApOrderId());// 机构参考号
			data8583JsonObject.put("payOrderBatch",
					iso8583Controller.getBatch());
			data8583JsonObject.put("transAmount",
					iso8583Controller.getTransAmount());
			data8583JsonObject.put("transTime",
					iso8583Controller.getTransTime());
			data8583JsonObject.put("paymentId",
					iso8583Controller.getPaymentId());
			data8583JsonObject.put("cardNum",
					iso8583Controller.getBankCardNum());// 卡号
			data8583JsonObject.put("iusserName",
					iso8583Controller.getIssuerName());
			data8583JsonObject.put("alipayAccount",
					iso8583Controller.getAlipayAccount());
			data8583JsonObject.put("alipayPID",
					iso8583Controller.getAlipayPID());
			data8583JsonObject.put("alipayTransactionID",
					iso8583Controller.getAlipayTransactionID());
			data8583JsonObject.put("issuerId", iso8583Controller.getIssuerId());
			data8583JsonObject.put("dateExpr",
					iso8583Controller.getDateExpiry());
			data8583JsonObject.put("stlmDate",
					iso8583Controller.getSettlementTime());
			data8583JsonObject.put("authNo", iso8583Controller.getAuthCode());
			iso8583Controller.setAuthCode("");

		} catch (Exception e) {
			try {
				data8583JsonObject.put("error", "ERROR");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			callBack(callBackId, data8583JsonObject);
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
					String txnId = jsonObjData.optString("txnId");
					String paymentId = jsonObjData.optString("paymentId");
					String paymentName = jsonObjData.optString("paymentName");
					Log.d(TAG, "print req8583 : " + req8583);
					Log.d(TAG, "print res8583 : " + res8583);
					if (!"".equals(req8583) && !"".equals(res8583)) {
						iso8583Controller.printer(Utility.hex2byte(req8583),
								Utility.hex2byte(res8583), userName, paymentId,
								paymentName, txnId, context);
					} else {
						String printType = jsonObjData.optString("printType");
						if (!TextUtils.isEmpty(printType)
								&& printType
										.equals(ConstantUtils.FOR_PRINT_PRINT_SUMMARY)) {
							iso8583Controller
									.printSummary(jsonObjData, context);
						}
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
		String apOrderId = jsonObjData.optString("txnId");
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

	public void summaryCallBack(final JSONObject jsonObjData,
			final String callBackId) {
		JSONObject jsObj = new JSONObject();

		try {
			JSONArray summaryItems = jsonObjData.optJSONArray("statistic");
			int responseCode = jsonObjData.optInt("responseCode");
			String responseMessage = "";
			String merchName = jsonObjData.optString("merchName");
			String merchId = jsonObjData.optString("merchId");
			String terminalId = jsonObjData.optString("machineId");

			if (responseCode == 0) {
				responseMessage = context.getResources().getString(
						R.string.msg_summary_success);
				if (summaryItems != null && summaryItems.length() == 1) {// there
																			// is
																			// only
																			// consumption
																			// data
					JSONObject revokeObj = new JSONObject();
					revokeObj.put("totalSize", "0");
					revokeObj.put("totalAmount", "0");
					revokeObj.put("transType", "3021");
					summaryItems.put(revokeObj);
				}
			} else {// there is no items data
				responseMessage = jsonObjData.optString("errorMsg");
				summaryItems = new JSONArray();

				JSONObject consumptionObj = new JSONObject();
				consumptionObj.put("totalSize", "0");
				consumptionObj.put("totalAmount", "0");
				consumptionObj.put("transType", "1021");
				summaryItems.put(consumptionObj);

				JSONObject revokeObj = new JSONObject();
				revokeObj.put("totalSize", "0");
				revokeObj.put("totalAmount", "0");
				revokeObj.put("transType", "3021");
				summaryItems.put(revokeObj);
			}

			jsObj.put("items", summaryItems);
			jsObj.put("responseCode", responseCode);
			jsObj.put("responseMessage", responseMessage);
			jsObj.put("merchName", merchName);
			jsObj.put("merchId", merchId);
			jsObj.put("terminalId", terminalId);

			if (mSecureService != null) {
				mSecureService.getSummaryCallBack(jsObj.toString());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateTransData8583(final JSONObject jsonObjData,
			final String callBackId) {
		String apOrderId = jsonObjData.optString("txnId");
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
		String apOrderId = jsonObjData.optString("txnId");
		if (TextUtils.isEmpty(apOrderId)) {
			return;
		}
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
