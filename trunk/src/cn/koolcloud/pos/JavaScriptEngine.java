package cn.koolcloud.pos;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public class JavaScriptEngine {

	private static JavaScriptEngine instance;
	private WebView webView;

	private String jsResponse;
	private ValueCallback<String> jsCallBack;
	private Handler mHandler;

	private boolean hasLoadedGlobal;
	private static final String TAG = "JavaScriptEngine";

	public JavaScriptEngine() {
		HandlerThread thread = new HandlerThread("JavaScriptHandlerThread");
		thread.start();
		mHandler = new Handler(thread.getLooper());
	}

	public static JavaScriptEngine engineInstance() {
		if (null == instance) {
			instance = new JavaScriptEngine();
			Log.d(TAG, "JavaScriptEngine null == instance");
		}
		return instance;
	}

	public void initEngine(Context context) {
		webView = new WebView(context.getApplicationContext());
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.setw
		webView.addJavascriptInterface(new JSResponser(context), "JSResponser");
		loadJSWaitUntilDone(
				"file:///android_asset/JavaScript/platform/android.html", null);
	}

	public void loadJs(String fileName) {
		if (!hasLoadedGlobal && fileName.contains("global")) {
			hasLoadedGlobal = true;
		}
		if (hasLoadedGlobal && !fileName.contains("global")) {
			callJsHandler("Global.clearGlobal", null);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("file:///android_asset/JavaScript/").append(fileName)
				.append(".js");
		String filePath = builder.toString();
		String funcStr = String.format("javascript:%s('%s','%s');",
				"loadScript", filePath, fileName);
		loadJSWaitUntilDone(funcStr, null);
	}

	public void removeJs(String fileName) {
		String funcStr = String.format("javascript:%s('%s');",
				"removeScriptById", fileName);
		Log.i(TAG, "Warning:--------RemoveJS:" + funcStr);
		loadJSWaitUntilDone(funcStr, null);
	}

	public void callJsHandler(String JsHandlerName, JSONObject message) {
		callJsHandler(JsHandlerName, message, null);
	}

	public void callJsHandler(String JsHandlerName, JSONObject message,
			ValueCallback<String> callBack) {
		String messageJSON = null;
		JSONObject jsMessage = new JSONObject();
		try {
			jsMessage.put("handler", JsHandlerName);
			if (null != message) {
				jsMessage.put("params", message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		messageJSON = jsMessage.toString();

		if (null != message) {
			messageJSON = messageJSON.replace("\\", "\\\\");
			messageJSON = messageJSON.replace("\"", "\\\"");
			messageJSON = messageJSON.replace("\'", "\\\'");
			messageJSON = messageJSON.replace("\n", "\\n");
			messageJSON = messageJSON.replace("\r", "\\r");
			messageJSON = messageJSON.replace("\f", "\\f");
		}

		String funcStr = String.format("javascript:%s('%s');", "Global.callJS",
				messageJSON);
		loadJSWaitUntilDone(funcStr, callBack);
	}

	private void loadJSWaitUntilDone(final String message,
			final ValueCallback<String> callBack) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				jsResponse = null;
				jsCallBack = callBack;
				AndroidHandler.getMainHandler().post(new Runnable() {

					@Override
					public void run() {
						webView.loadUrl(message);
					}
				});
				while (jsResponse == null) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.d(TAG, jsResponse);
			}
		});
	}

	private class JSResponser {
		private String messageJSON;
		private Context ctx;

		public JSResponser(Context context) {
			this.ctx = context;
		}

		@SuppressWarnings("unused")
		public void flushMessage(String messageJSONFromJs) {
			messageJSON = messageJSONFromJs;
			JSONObject message;
			try {
				message = new JSONObject(messageJSON);
				String callBackHandler = null;
				if (null != message.optString("callbackId")) {
					callBackHandler = message.optString("callbackId");
				}

				if (null != message.optString("handler")) {
					String androidHandlerName = message.optString("handler");
					AndroidHandler.handle(androidHandlerName,
							message.opt("params"), callBackHandler, ctx);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unused")
		public void sendJsResponse(String messageJSONFromJs) {
			jsResponse = messageJSONFromJs;
			if (jsResponse == null) {
				jsResponse = "";
			}
			if (jsCallBack != null) {
				jsCallBack.onReceiveValue(jsResponse);
			}
		}
	}

	public void responseCallback(String callBackHandler, Object data) {
		final JSONObject callBackMessage = new JSONObject();
		try {
			callBackMessage.put("responseId", callBackHandler);
			callBackMessage.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		AndroidHandler.getMainHandler().post(new Runnable() {

			@Override
			public void run() {
				callJsHandler("Global.objcResponse", callBackMessage);
			}
		});

	}

}
