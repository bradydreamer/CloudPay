package cn.koolcloud.pos;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import cn.koolcloud.pos.service.SmartIposRunBackground;
import cn.koolcloud.pos.util.UtilForDataStorage;

public class AppInitManager {

	private static final String TAG = "AppInitManager";
	public static AppInitManager aimInstance;
	private SmartIposRunBackground sRunbackService;
	private Context context;

	private AppInitManager() {
		super();
	}

	public static AppInitManager getInstance() {
		if (aimInstance == null) {
			aimInstance = new AppInitManager();
		}
		return aimInstance;
	}

	public void Init(SmartIposRunBackground sRunbackService, Context context) {
		this.sRunbackService = sRunbackService;
		this.context = context;
	}

	public void autoLogin() {
		Log.i(TAG, "autoLogin  ----------------------");
		JSONObject msg = initAutoLoginData();
		onCall("LoginIndex.autoLogin", msg);
	}

	public void precessResult(JSONObject data) {
		Log.i(TAG, "precessResult----------------------");
		int result = data.optInt("value", 1);
		try {
			sRunbackService.invokCallBack(result);
			// ((Activity) context).finish();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onCall(String jsHandler, JSONObject msg) {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler(jsHandler, msg);
		} else {
			Log.d(TAG,
					"ClientEngine.engineInstance(): "
							+ ClientEngine.engineInstance());
			Log.d(TAG, "javaScriptEngine(): "
					+ ClientEngine.engineInstance().javaScriptEngine());
		}
	}

	private JSONObject initAutoLoginData() {
		Map<String, Object> newMerchantMap = new HashMap<String, Object>();
		JSONObject msg = new JSONObject();
		newMerchantMap.put("pwd", "_TDS_" + stringToMD5("123456"));
		newMerchantMap.put("ssn", android.os.Build.SERIAL);
		newMerchantMap.put("merchId", "999290053110041");
		newMerchantMap.put("operator", "wan");
		try {
			msg.put("pwd", "_TDS_" + stringToMD5("123456"));
			msg.put("ssn", android.os.Build.SERIAL);
			msg.put("merchId", "999290053110041");
			msg.put("userName", "wan");
			msg.put("INIT", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UtilForDataStorage.savePropertyBySharedPreferences(
				MyApplication.getContext(), "merchant", newMerchantMap);
		return msg;
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
}
