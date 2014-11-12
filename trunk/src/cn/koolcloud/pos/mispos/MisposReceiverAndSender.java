package cn.koolcloud.pos.mispos;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import cn.koolcloud.jni.MisPosInterface;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.pos.util.InputStreamUtils;
import cn.koolcloud.pos.util.Logger;

/**
 * <p>Title: MisposReceiverAndSender.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: KoolCloud</p>
 * @author 		Teddy
 * @date 		2014-6-25
 * @version 	
 */
public class MisposReceiverAndSender {
	
	private final static int DATA_8583_RECEIVED_HANDLER 		= 0;
	private final static int DATA_8583_FINISH_HANDLER 			= 1;
	
	public final static int SIGNIN_TYPE 						= 0;
	public final static int SIGNOUT_TYPE 						= 1;
	public final static int CONSUMPTION_TYPE 					= 2;
	public final static int CONSUMPTION_REVERSE_TYPE 			= 3;
	public final static int PRE_AUTHORIZATION_TYPE 				= 4;
	public final static int PRE_AUTHORIZATION_REVERSE_TYPE 		= 5;
	public final static int PRE_AUTHORIZATION_COMPLETE_TYPE		= 6;
	public final static int GET_BALANCE_TYPE					= 7;
	public final static int REFUND_TYPE							= 8;
	
	public final static String KEY_DATA_8583					= "data8583";
	public final static String KEY_AMOUNT						= "amount";
	public final static String KEY_MISPOS_ENTITY_DATA			= "entity_data";
	public final static String KEY_MISPOS_DATA					= "data";
	public final static String KEY_CALL_BACK_PARAMS				= "call_back_param";
	public final static String KEY_CALL_BACK_TRAN_TYPE_STR		= "tran_type_str";
	
	private static MisposReceiverAndSender instance;
	private I8583Processer i8583Processer;
	
	private String callBackMessage;
	private JSONObject callBackJson;
	
	//Looper for waiting data
	private Handler mHandler;
	private Looper waitDataLooper;
	private HandlerThread waitDataThread;
	
	private MisposReceiverAndSender() {
		if(waitDataLooper == null){
			waitDataThread = new HandlerThread("waitMisPosData", android.os.Process.THREAD_PRIORITY_BACKGROUND);
			waitDataThread.start();
			waitDataLooper = waitDataThread.getLooper();
			/*mHandler = new Handler(waitDataLooper, new Handler.Callback() {
					
				@Override
				public boolean handleMessage(Message msg) {
					JSONObject obj = (JSONObject) msg.obj;
					Logger.w("mHandler:" + msg.what);
					switch (msg.what) {
					case DATA_8583_RECEIVED_HANDLER:
						Logger.i("DATA_8583_RECEIVED_HANDLER");
						try {
							MisposData beanData = (MisposData) obj.get(KEY_MISPOS_DATA);
							if (!TextUtils.isEmpty(beanData.getTransType())) {
								i8583Processer.get8583CallBack(obj);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						break;
					case DATA_8583_FINISH_HANDLER:
						Logger.i("DATA_8583_FINISH_HANDLER");
						i8583Processer.send8583CallBack(obj);
						break;
					default:
						break;
					}
					return true;
				}
			});*/
			mHandler = new Handler(waitDataLooper);
			mHandler.post(mRunnable);
		}
	}
	
	public static MisposReceiverAndSender getInstance() {
		if (null == instance) {
			Logger.d("MisposReceiverAndSender null == instance");
			instance = new MisposReceiverAndSender();
		}
		
		return instance;
	}
	
	/**
	 * @Title: get8583Protocol
	 * @Description: get 8583 protocol from mispos
	 * @param protocolType: listed static type on this class
	 * @param callBackParams: need to pass params (including received 8583 data) on call back
	 * @param iProcesser: call back class which must implement I8583Processer interface
	 * @return: void
	 */
	public void get8583Protocol(I8583Processer iProcesser, int protocolType, JSONObject callBackParams) {
		this.i8583Processer = iProcesser;
		
		this.callBackMessage = callBackParams.optString(KEY_CALL_BACK_PARAMS);
		this.callBackJson = callBackParams;
		Logger.i("get8583Protocol");
		if (waitDataThread == null) {
			
			waitDataThread = new HandlerThread("waitMisPosData", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		} 
		if (waitDataLooper != null) {
			waitDataLooper = waitDataThread.getLooper();
			mHandler = new Handler(waitDataLooper);
		}
		if (!waitDataThread.isAlive()) {
			waitDataThread.start();
		}
		mHandler.post(mRunnable);
		
		requestMispos(protocolType, callBackParams);
	}
	
	public void send8583Protocol(I8583Processer iProcesser, JSONObject callBackParams) {
		this.i8583Processer = iProcesser;
		Logger.d("send8583Protocol");
		if (waitDataThread == null) {
			
			waitDataThread = new HandlerThread("waitMisPosData", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		} 
		if (waitDataLooper != null) {
			waitDataLooper = waitDataThread.getLooper();
			mHandler = new Handler(waitDataLooper);
		}
		if (!waitDataThread.isAlive()) {
			waitDataThread.start();
		}
		mHandler.post(mRunnable);
		
		String data8583 = callBackParams.optString(KEY_DATA_8583);
		Logger.d("send8583Protocol 8583:" + data8583);
		this.callBackMessage = callBackParams.optString(KEY_CALL_BACK_PARAMS);
		try {
			InputStream inputStream = InputStreamUtils.StringTOInputStream(data8583);
			byte ibuf[] = new byte[1024];
			int len = inputStream.read(ibuf);
			
			MisPosInterface.sendMessage(ibuf, len);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void requestMispos(int protocolType, JSONObject jsObj) {
		Logger.i("request:" + protocolType);
		switch (protocolType) {
		case SIGNIN_TYPE:
			MisPosInterface.registration(0x01);
			break;
		case SIGNOUT_TYPE:
			MisPosInterface.unregistration(0x0E);
			break;
		case CONSUMPTION_TYPE:
			String amountConsumption = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.consume(0x02, amountConsumption);
			break;
		case CONSUMPTION_REVERSE_TYPE:
			String amountConsumptionReverse = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.consumeRevoke(0x03, amountConsumptionReverse, "");
			break;
		case PRE_AUTHORIZATION_TYPE:
			String preAuthorizeAmount = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.preAuthorization(0x06, preAuthorizeAmount);
			break;
		case PRE_AUTHORIZATION_REVERSE_TYPE:
			String preAuthorizeReverseAmount = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.preAuthorizationRevoke(0x07, preAuthorizeReverseAmount, "");
			break;
		case PRE_AUTHORIZATION_COMPLETE_TYPE:
			String preAuthorizeCompleteAmount = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.preAuthorizationComplete(0x08, preAuthorizeCompleteAmount);
			break;
		case GET_BALANCE_TYPE:
			MisPosInterface.getBalance(0x12);
			break;
		case REFUND_TYPE:
			String refundAmount = jsObj.optString(KEY_AMOUNT);
			MisPosInterface.returnGoods(0x04, refundAmount);
			break;

		default:
			break;
		}
	}
	
	private Runnable mRunnable = new Runnable() {    
        
        public void run() {    
			Logger.i("MisposDataReceiverThread");
			while (true) {
				int ret = MisPosInterface.serialPoll(-1);
				Logger.i("serialPoll ret:" + ret);
				if (ret >= 0) {
					Logger.i("serialPoll succ");
					messageProcess();
					/*int recvDataLen = 0;
					byte[] recvData = new byte[1024];

					try {
						recvDataLen = MisPosInterface.recvMessage(recvData);
						Logger.i("recvDataLen: " + recvDataLen);
//						Message msg = mHandler.obtainMessage();
						Message msg = Message.obtain(mHandler);
						
						//FIXME: parse returned bean from mispos
						MisposData data = parseMisposData();
						JSONObject params = new JSONObject();
						params.put(KEY_MISPOS_DATA, data);
						params.put(KEY_CALL_BACK_PARAMS, callBackMessage);
						//there is 8583 data response from mispos
						if (recvDataLen != 0) {
							params.put(KEY_8583, recvData);
							msg.what = DATA_8583_RECEIVED_HANDLER;
						} else {//handle data after send 8583 data
							msg.what = DATA_8583_FINISH_HANDLER;
						}
						msg.obj = params;
//						msg.sendToTarget();
						mHandler.sendMessage(msg);
						
					} catch (Exception e) {
						e.printStackTrace();
					}*/
				}
			}
        }    
            
    }; 
	
//	private class MisposDataReceiverThread extends Thread {
//
//		@Override
//		public void run() {
//			int num = 0;
//			Logger.i("MisposDataReceiverThread");
//			while (true) {
//				int ret = MisPosInterface.serialPoll(-1);
//				Logger.i("serialPoll ret:" + ret);
//				if (ret >= 0) {
//					Logger.i("serialPoll succ" + ++num);
//					messageProcess();
//					/*int recvDataLen = 0;
//					byte[] recvData = new byte[1024];
//
//					try {
//						recvDataLen = MisPosInterface.recvMessage(recvData);
//						Logger.i("recvDataLen: " + recvDataLen);
//						Message msg = mHandler.obtainMessage();
//						
//						//FIXME: parse returned bean from mispos
//						MisposData data = parseMisposData();
//						JSONObject params = new JSONObject();
//						params.put(KEY_MISPOS_DATA, data);
//						params.put(KEY_CALL_BACK_PARAMS, callBackMessage);
//						//there is 8583 data response from mispos
//						if (recvDataLen != 0) {
//							params.put(KEY_8583, recvData);
//							msg.what = DATA_8583_RECEIVED_HANDLER;
//						} else {//handle data after send 8583 data
//							msg.what = DATA_8583_FINISH_HANDLER;
//						}
//						msg.obj = params;
//						mHandler.sendMessage(msg);
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}*/
//				}
//			}
//		}
//		
//	}
	
	private void messageProcess() {
		int recvDataLen = 0;
		byte[] recvData = new byte[1024];

		try {
			recvDataLen = MisPosInterface.recvMessage(recvData);
			Logger.i("recvDataLen: " + recvDataLen);
			//FIXME: parse returned bean from mispos
			MisposData data = parseMisposData();
			JSONObject params = new JSONObject();
			params.put(KEY_MISPOS_DATA, data);
			params.put(KEY_CALL_BACK_PARAMS, callBackMessage);
			//there is 8583 data response from mispos
			if (recvDataLen != 0) {
				params.put(KEY_MISPOS_DATA, InputStreamUtils.byteTOString(recvData));
				Logger.i("i8583:" + InputStreamUtils.byteTOString(recvData));
				i8583Processer.get8583CallBack(params);
			} else {//handle data after send 8583 data
				i8583Processer.send8583CallBack(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private MisposData parseMisposData() {
		MisposData data = new MisposData();
		
		byte[] bTransType = new byte[1];
		MisPosInterface.getTagValue(0x9F01, bTransType);
		if (bTransType[0] == 0x02 || bTransType[0] == 0x03) {
			Logger.i("TransType: " + bTransType[0]);
			data.setTransType(String.valueOf((char) bTransType[0]));
			
			byte[] bMerchantId = new byte[100];
			int iMerchantIdLen = MisPosInterface.getTagValue(0x9F04, bMerchantId);
			String strMerchantId = new String(bMerchantId, 0, iMerchantIdLen);
			Logger.i("MerchantId: " + strMerchantId);
			data.setMerchantId(InputStreamUtils.byteTOString(bMerchantId));
			
			byte[] bMerchantName = new byte[100];
			int iMerchantNameLen = MisPosInterface.getTagValue(0x9F03, bMerchantName);
			String strMerchantName = new String(bMerchantName, 0, iMerchantNameLen);
			Logger.i("MerchantName: " + strMerchantName);
			data.setMerchantName(InputStreamUtils.byteTOString(bMerchantName));
			
			byte[] bAmount = new byte[100];
			int iAmountLen = MisPosInterface.getTagValue(0x9F02, bAmount);
			String strAmount = new String(bAmount, 0, iAmountLen);
			Logger.i("Amount: " + strAmount);
			data.setAmount(InputStreamUtils.byteTOString(bAmount));
			
			byte[] bTerminalId = new byte[100];
			int iTerminalIdLen = MisPosInterface.getTagValue(0x9F05, bTerminalId);
			String strTerminalId = new String(bTerminalId, 0, iTerminalIdLen);
			Logger.i("TerminalId: " + strTerminalId);
			data.setTerminalId(InputStreamUtils.byteTOString(bTerminalId));
			
			byte[] bOperatorId = new byte[100];
			int iOperatorIdLen = MisPosInterface.getTagValue(0x9F06, bOperatorId);
			String strOperatorId = new String(bOperatorId, 0, iOperatorIdLen);
			Logger.i("OperatorId: " + strOperatorId);
			data.setOperatorId(InputStreamUtils.byteTOString(bOperatorId));
			
			byte[] bAcquirerId = new byte[100];
			int iAcquirerIdLen = MisPosInterface.getTagValue(0x9F07, bAcquirerId);
			String strAcquirerId = new String(bAcquirerId, 0, iAcquirerIdLen);
			Logger.i("AcquirerId: " + strAcquirerId);
			data.setAcquirerId(InputStreamUtils.byteTOString(bAcquirerId));
			
			byte[] bIssuerId = new byte[100];
			int iIssuerIdLen = MisPosInterface.getTagValue(0x9F08, bIssuerId);
			String strIssuerId = new String(bIssuerId, 0, iIssuerIdLen);
			Logger.i("IssuerId: " + strIssuerId);
			data.setIssuerId(InputStreamUtils.byteTOString(bIssuerId));
			
			byte[] bIssuerName = new byte[100];
			int iIssuerNameLen = MisPosInterface.getTagValue(0x9F09, bIssuerName);
			String strIssuerName = new String(bIssuerName, 0, iIssuerNameLen);
			Logger.i("IssuerName: " + strIssuerName);
			data.setIssuerName(InputStreamUtils.byteTOString(bIssuerName));
			
			byte[] bCardNo = new byte[100];
			int iCardNoLen = MisPosInterface.getTagValue(0x9F0B, bCardNo);
			String strCardNo = new String(bCardNo, 0, iCardNoLen);
			Logger.i("CardNo: " + strCardNo);
			data.setCardNo(InputStreamUtils.byteTOString(bCardNo));
			
			byte[] bBatchNo = new byte[100];
			int iBatchNoLen = MisPosInterface.getTagValue(0x9F0D, bBatchNo);
			String strBatchNo = new String(bBatchNo, 0, iBatchNoLen);
			Logger.i("BatchNo: " + strBatchNo);
			data.setBatchNo(InputStreamUtils.byteTOString(bBatchNo));
			
			byte[] bVoucherNo = new byte[100];
			int iVoucherNoLen = MisPosInterface.getTagValue(0x9F0E, bVoucherNo);
			String strVoucherNo = new String(bVoucherNo, 0, iVoucherNoLen);
			Logger.i("VoucherNo: " + strVoucherNo);
			data.setVoucherNo(InputStreamUtils.byteTOString(bVoucherNo));
			
			byte[] bAuthNo = new byte[100];
			int iAuthNoLen = MisPosInterface.getTagValue(0x9F0F, bAuthNo);
			String strAuthNo = new String(bAuthNo, 0, iAuthNoLen);
			Logger.i("AuthNo: " + strAuthNo);
			data.setAuthNo(InputStreamUtils.byteTOString(bAuthNo));
			
			byte[] bRefNo = new byte[100];
			int iRefNoLen = MisPosInterface.getTagValue(0x9F10, bRefNo);
			String strRefNo = new String(bRefNo, 0, iRefNoLen);
			Logger.i("RefNo: " + strRefNo);
			data.setRefNo(InputStreamUtils.byteTOString(bRefNo));
			
			byte[] bTime = new byte[100];
			int iTimeLen = MisPosInterface.getTagValue(0x9F11, bTime);
			String strTime = new String(bTime, 0, iTimeLen);
			Logger.i("Time: " + strTime);
			data.setTranTime(InputStreamUtils.byteTOString(bTime));
			
			byte[] bDate = new byte[100];
			int iDateLen = MisPosInterface.getTagValue(0x9F12, bDate);
			String strDate = new String(bDate, 0, iDateLen);
			Logger.i("Date: " + strDate);
			data.setTranDate(InputStreamUtils.byteTOString(bDate));
		}
		return data;
	}
	
	/**
	 * @Title: openMispos
	 * @Description: invoke mispos open on application start (onCreate of Application)
	 * @return: void
	 */
	public static void openMispos() {
		// communication open
		MisPosInterface.communicationOpen();
	}
	
	/**
	 * @Title: closeMispos
	 * @Description: invoke mispos close on application exit (onDesdroy of BaseController)
	 * @return: void
	 */
	public static void closeMispos() {
		MisPosInterface.communicationClose();
	}
	
	public interface I8583Processer {
		
		/**
		 * 
		 * @Title: get8583CallBack
		 * @Description: get 8583 protocol call back from mispos 
		 * @param jsonObj
		 * @return: void
		 */
		public void get8583CallBack(JSONObject jsonObj);
		
		/**
		 * 
		 * @Title: send8583CallBack
		 * @Description: send 8583 protocol call back to mispos
		 * @param jsonObj
		 * @return: void
		 */
		public void send8583CallBack(JSONObject jsonObj);
	}

}
