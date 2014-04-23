package cn.koolcloud.pos.controller;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.TextView;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.Utility;

public class PinPadController extends BaseController {
	private Looper looper;
	private HandlerThread handlerThread;
	private JSONObject transData;
	private TextView tv_notice;
	private int TIME_INPUT = 300000;
	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		setLeftButtonHidden();
		tv_notice = (TextView) findViewById(R.id.pin_pad_id_tv_notice);
		transData = formData
				.optJSONObject(getString(R.string.formData_key_data));

		handlerThread = new HandlerThread("PinPadInterface");
		handlerThread.start();
		looper = handlerThread.getLooper();
	}

	@Override
	protected void willShow() {
		super.willShow();
		Handler handler = new Handler(looper);
		handler.post(new Runnable() {

			@Override
			public void run() {
				boolean isCancelled = false;

				int openResult = PinPadInterface.open();
				if (openResult < 0) {
					PinPadInterface.close();
					PinPadInterface.open();
				}

				String cardId = transData.optString("cardID");
				if (cardId.isEmpty()) {
					cardId = "0000000000000000000";
				}
				byte[] bytes_pan = cardId.getBytes();
				byte[] pinBlock = new byte[8];
				PinPadInterface.selectKey(2, 0, 0, 1);
				String actionPurpose = transData.optString("actionPurpose");
				if (!actionPurpose.equals("Balance")) {
					String amount = transData.optString("transAmount");
					if (!amount.isEmpty()) {
						String text = amount;
						byte[] btyes_text = text.getBytes();
						PinPadInterface.showText(0, btyes_text,
								btyes_text.length, 1);
					}
				}
				PinPadInterface.setPinLength(6, 1);
				int pwdInputResult = PinPadInterface.calculatePinBlock(
						bytes_pan, bytes_pan.length, pinBlock, TIME_INPUT, 0);
				if (pwdInputResult < 0) {
					isCancelled = true;
				} else {
					String pwd = Utility.hexString(pinBlock);
					try {
						transData.put("pwd", pwd);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Boolean needAuthCode = transData.optBoolean("needAuthCode");
					if (needAuthCode) {
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								setTitle(getString(R.string.pin_pad_tv_java_input_authCode));
								tv_notice
										.setText(getString(R.string.pin_pad_tv_java_input_authCode));
							}
						});
						byte[] auth = new byte[8];
						int authInputResult = PinPadInterface
								.calculatePinBlock(bytes_pan, bytes_pan.length,
										auth, TIME_INPUT, 0);
						if (authInputResult < 0) {
							isCancelled = true;
						} else {
							String authCode = Utility.hexString(auth);
							try {
								transData.put("authCode", authCode);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
				PinPadInterface.close();// 关闭占用
				try {
					transData.put("isCancelled", isCancelled);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onCall("PinPad.CompleteInput", transData);
			}
		});
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_pin_pad_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_pin_pad_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_PinPad);
	}

	@Override
	protected void onDestroy() {
		looper.quit();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}
}
