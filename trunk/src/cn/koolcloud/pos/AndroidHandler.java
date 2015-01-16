package cn.koolcloud.pos;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

public class AndroidHandler {

	public static Handler getMainHandler() {
		Handler mainHandler = ClientEngine.engineInstance().getMainHandler();
		return mainHandler;
	}

	public static void handle(final String androidHandlerName,
			final Object data, final String callBackId, final Context context) {
		final ClientEngine clientEngine = ClientEngine.engineInstance();
		Thread androidHandlerThread = new Thread(new Runnable() {

			@Override
			public void run() {

				JSONObject jsonObjData = null;
				String strData = "";
				if (data instanceof JSONObject) {
					jsonObjData = (JSONObject) data;
					strData = jsonObjData.toString();
				} else if (data instanceof String) {
					strData = (String) data;
				}
				if ("alert".equals(androidHandlerName)) {
					clientEngine.showAlert(jsonObjData, callBackId);
				} else if ("showScene".equals(androidHandlerName)) {
					clientEngine.showController(jsonObjData, callBackId);
				} else if ("checkSessionTime".equals(androidHandlerName)) {
					clientEngine.startSessionTest();
				} else if ("stopCheckSessionTime".equals(androidHandlerName)) {
					clientEngine.stopSessionTest();
				} else if("deleteParamsFiles".equals(androidHandlerName)){
					clientEngine.deleteParamsFiles();
				}else if ("goBack".equals(androidHandlerName)) {
					clientEngine.showController(jsonObjData, callBackId);
				} else if ("setProperty".equals(androidHandlerName)) {
					clientEngine.setControllerProperty(jsonObjData, callBackId);
				} else if ("netAsynConnect".equals(androidHandlerName)) {
					clientEngine.netConnect(jsonObjData, callBackId);
				} else if ("netConnect".equals(androidHandlerName)) {
					clientEngine
							.netConnectInMainThread(jsonObjData, callBackId);
				} else if ("saveLocal".equals(androidHandlerName)) {
					clientEngine.saveLocal(jsonObjData, callBackId, context);
				} else if ("clearLocal".equals(androidHandlerName)) {
					clientEngine.clearLocal(jsonObjData, callBackId);
				} else if ("rmBatchTask".equals(androidHandlerName)) {
					clientEngine.rmBachCache(jsonObjData, context);
				} else if ("readLocal".equals(androidHandlerName)) {
					clientEngine.readLocal(jsonObjData, callBackId);
				} else if ("readLocalBatch".equals(androidHandlerName)) {
					clientEngine.readLocalBatch(callBackId, context);
				} else if ("getSystemInfo".equals(androidHandlerName)) {
					if (null == clientEngine.getSystemInfo()) {
						clientEngine.initSystemInfo();
					}
					clientEngine.callBack(callBackId,
							clientEngine.getSystemInfo());
				} else if ("convert8583".equals(androidHandlerName)) {
					final JSONObject jsData = jsonObjData;
					clientEngine.showWaitingDialogWhenRun(new Runnable() {

						@Override
						public void run() {
							clientEngine.convert8583(jsData, callBackId);
						}
					}, context.getResources().getString(R.string.msg_trading));

				} else if ("get8583".equals(androidHandlerName)) {
					final JSONObject jsData = jsonObjData;
					clientEngine.showWaitingDialogWhenRun(new Runnable() {

						@Override
						public void run() {
							clientEngine.get8583(jsData, callBackId);
						}
					}, context.getResources().getString(R.string.msg_trading));
				} else if ("printTrans".equals(androidHandlerName)) {
					clientEngine.print(jsonObjData, context);
				} else if ("insertTransData8583".equals(androidHandlerName)) {
					clientEngine.insertTransData8583(jsonObjData, callBackId);
				} else if ("updateTransData8583".equals(androidHandlerName)) {
					clientEngine.updateTransData8583(jsonObjData, callBackId);
				} else if ("getTransData8583".equals(androidHandlerName)) {
					clientEngine.getTransData8583(jsonObjData, callBackId);
				} else if ("getBalance".equals(androidHandlerName)) {
					clientEngine.getBalance(callBackId);
				} else if ("ServiceSecureInfo".equals(androidHandlerName)) {
					clientEngine.serviceSecureInfo(jsonObjData, callBackId);
				} else if ("ServiceMerchInfo".equals(androidHandlerName)) {
					clientEngine.serviceMerchantInfo(jsonObjData, callBackId);
				} else if ("PrintRecord".equals(androidHandlerName)) {
                    clientEngine.printRecord(jsonObjData, context);
                } else if ("saveBankData".equals(androidHandlerName)) {
                    clientEngine.saveBankData(jsonObjData, context);
                }
			}
		});
		androidHandlerThread.setName("androidHandlerThread");
		androidHandlerThread.start();
	}
}
