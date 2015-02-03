package cn.koolcloud.pos.external;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Locale;

import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.MsrInterface;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.Utility;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.controller.pay.PayAccountController;

public class PinPadManager {
	private Context context;
	private JSONObject transData;
	private Handler mHandler;
	private HandlerThread handlerThread;
	private Looper looper;
	private String brhKeyIndex = "";
	private final String TAG = "PinPadManager";
	private static int TIME_INPUT = 300000;
	public static PinPadManager ppM_instance = null;
	private UtilFor8583 util8583 = UtilFor8583.getInstance();

	public static final int MODE_INPUT_AUTH_CODE = 0xF0;
	public static final int MODE_PAY_NEXT = 0xE0;
	private PinPadManager() {
	}

	public static PinPadManager getInstance(){
		if(ppM_instance == null){
			ppM_instance = new PinPadManager();
		}
		return ppM_instance;
	}

	public void setParams(Context ctx,JSONObject transData, Handler mHandler){
		this.context = ctx;
		this.transData = transData;
		this.mHandler = mHandler;
	}

	public void start(){
		brhKeyIndex = transData.optString("brhKeyIndex","00");
		if (brhKeyIndex != null && !brhKeyIndex.equals("")) {
			util8583.terminalConfig.setKeyIndex(brhKeyIndex);
		}
		handlerThread = new HandlerThread("PinPadInterface");
		handlerThread.start();
		looper = handlerThread.getLooper();
		Handler handler = new Handler(looper);
		handler.post(new Runnable() {

			@Override
			public void run() {
				boolean isCancelled = false;
				String pwd= null;
				String authCode = null;

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
				int keyIndex = Integer.parseInt(util8583.terminalConfig
						.getKeyIndex());
				PinPadInterface.selectKey(2, keyIndex, 0, 1);
				String actionPurpose = transData.optString("actionPurpose");
				if (!actionPurpose.equals("Balance")) {
					String amount = transData.optString("transAmount");
					if (!amount.isEmpty()) {
						String text = amount;
						String language = Locale.getDefault().getLanguage();
						if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) { //中文的排版
							byte[] bytes_text_1 = new byte[]{(byte) 0x8B, (byte) 0x86};
							byte[] btyes_text_2 = (":" + text).getBytes();
							byte[] bytes_text_line_1 = new byte[bytes_text_1.length + btyes_text_2.length];
							System.arraycopy(bytes_text_1, 0, bytes_text_line_1, 0, bytes_text_1.length);
							System.arraycopy(btyes_text_2, 0, bytes_text_line_1, bytes_text_1.length, btyes_text_2.length);
							byte[] bytes_text_line_2 = new byte[]{(byte) 0x80,
									(byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84};
							// clean line
							PinPadInterface.showText(0, null, 0, 1);
							PinPadInterface.showText(0, bytes_text_line_1,
									bytes_text_line_1.length, 1);
							PinPadInterface.showText(1, bytes_text_line_2,
									bytes_text_line_2.length, 1);
						}else{
							byte[] bytes_text_line_1 = ("AMT:" + text).getBytes();
							byte[] bytes_text_line_2 = "PLS INPUT PWD".getBytes();
							// clean line
							PinPadInterface.showText(0, null, 0, 1);
							PinPadInterface.showText(0, bytes_text_line_1,
									bytes_text_line_1.length, 1);
							PinPadInterface.showText(1, bytes_text_line_2,
									bytes_text_line_2.length, 1);
						}

					}
				} else {// input pwd character on pinpad line 1
					String language = Locale.getDefault().getLanguage();
					if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) { //中文的排版
						byte[] btyes_text_1 = new byte[]{(byte) 0x80,
								(byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84};
						// clean line
						PinPadInterface.showText(1, null, 0, 1);
						PinPadInterface.showText(1, btyes_text_1,
								btyes_text_1.length, 1);
					}else{
						byte[] btyes_text_1 = "PLS INPUT PWD".getBytes();
						// clean line
						PinPadInterface.showText(1, null, 0, 1);
						PinPadInterface.showText(1, btyes_text_1,
								btyes_text_1.length, 1);
					}
				}
				PinPadInterface.setPinLength(6, 1);
				int pwdInputResult = PinPadInterface.calculatePinBlock(
						bytes_pan, bytes_pan.length, pinBlock, TIME_INPUT, 0);
				if (pwdInputResult < 0) {
					isCancelled = true;
				} else {
					pwd = Utility.hexString(pinBlock);
					Boolean needAuthCode = transData.optBoolean("needAuthCode");
					if (needAuthCode) {
						/*Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								setTitle(getString(R.string.pin_pad_tv_java_input_authCode));
								tv_notice
										.setText(getString(R.string.pin_pad_tv_java_input_authCode));
							}
						});*/
						//发送消息，显示输入授权码！
						Message msg = new Message();
						msg.what = MODE_INPUT_AUTH_CODE;
						mHandler.sendMessage(msg);

						byte[] auth = new byte[8];
						int authInputResult = PinPadInterface
								.calculatePinBlock(bytes_pan, bytes_pan.length,
										auth, TIME_INPUT, 0);
						if (authInputResult < 0) {
							isCancelled = true;
						} else {
							authCode = Utility.hexString(auth);
						}
					}
				}
				PinPadInterface.close();// 关闭占用
				try {
					transData.put("isCancelled", isCancelled);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				Bundle bd = new Bundle();
				bd.putString("pwd",pwd);
				bd.putBoolean("isCancelled",isCancelled);
				bd.putString("authCode",authCode);
				msg.setData(bd);
				msg.what = MODE_PAY_NEXT;
				mHandler.sendMessage(msg);
			}
		});
	}
}
