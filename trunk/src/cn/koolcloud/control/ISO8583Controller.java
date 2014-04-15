package cn.koolcloud.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import cn.koolcloud.pos.ISO8583Engine;
import cn.koolcloud.pos.Utility;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.iso8583.ChongZheng;
import cn.koolcloud.iso8583.ISOField;
import cn.koolcloud.iso8583.ISOPackager;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.printer.PrinterException;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.ByteUtil;
import cn.koolcloud.util.StringUtil;

public class ISO8583Controller implements Constant {

	private static final String ZHIFUCHONGZHENG = "zhifuchongzheng"; // 普通冲正
	private static final String CHEXIAOCHONGZHENG = "chexiaochongzheng";// 撤销冲正
	private String mId = "";
	private String tId = "";
	private int transId = 0; // 流水号

	private byte[] mRequest;

	UtilFor8583 paramer = UtilFor8583.getInstance();

	public ISO8583Controller(String mID, String tID, int transID,
			int batchNumber) {
//		paramer.trans.init();
		paramer.oldTrans = null;
		
		this.mId = mID;
		this.tId = tID;
		this.transId = transID;
	
		paramer.terminalConfig.setTrace(transId);// 流水号
		paramer.trans.setTrace(transId);
		// 设置商户号 (41域）
		paramer.terminalConfig.setMID(mId);
		// 设置终端号 (42域）
		paramer.terminalConfig.setTID(tId);
		paramer.trans.setBatchNumber(batchNumber);

//		if (batchNumber != 0) {
//			
//		} else {
//			paramer.trans.setBatchNumber(Integer.parseInt("600001"));
//		}

	}

	/**
	 * 签到
	 * 
	 * @return
	 */
	public boolean login() {
		// 默认传递参数都是正确的，暂时为加入校验
		paramer.trans.setTransType(TRAN_LOGIN);
		// 设置POS终端交易流水 (11域）
		paramer.terminalConfig.setTrace(transId);// 流水号
		// 设置商户号 (41域）
		paramer.terminalConfig.setMID(mId);
		// 设置终端号 (42域）
		paramer.terminalConfig.setTID(tId);
		// 批次号 (60.2)
		// 600001暂时写死了。
		// 操作员代码01?02 (63域）
		boolean isSuccess = pack8583(paramer);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;

	}

	public String getBanlance() {
		// Log.d(APP_TAG, "balance = " + paramer.trans.getBalance());
		return "" + paramer.trans.getBalance();
	}

	/**
	 * 查询余额
	 * 
	 * @param account
	 * @param track2
	 * @param track3
	 * @param pinBlock
	 * @param open_brh
	 * @param payment_id
	 * @return
	 */
	public boolean purchaseChaXun(String account, String track2, String track3,
			byte[] pinBlock, String open_brh,String payment_id) {
		
		paramer.trans.setTransType(TRAN_BALANCE);
		paramer.trans.setPAN(account); // 设置主帐号
		paramer.trans.setTrack2Data(track2);
		paramer.trans.setTrack3Data(track3);
		paramer.trans.setPinBlock(pinBlock);
		paramer.trans.setEntryMode(HAVE_PIN);
		paramer.paymentId = payment_id;
		paramer.openBrh = open_brh;
		
		boolean isSuccess = pack8583(paramer);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;
	}

	public boolean purchase(JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_SALE);

		int[] bitMap = { 
				ISOField.F02_PAN, ISOField.F03_PROC,
				ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
				ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
				ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
				/*ISOField.F38_AUTH, */ISOField.F39_RSP, ISOField.F40,
				ISOField.F41_TID, ISOField.F42_ACCID, ISOField.F49_CURRENCY,
				ISOField.F52_PIN, ISOField.F53_SCI, ISOField.F55_ICC,
				ISOField.F60, ISOField.F64_MAC 
			};
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 冲正
	 * 
	 * @return
	 */
	public boolean chongZheng(byte[] iso8583, String oldTransDate, String name) {

		byte[] data = new byte[iso8583.length - 2];
		System.arraycopy(iso8583, 2, data, 0, data.length - 2);
		if (name.equals(CHEXIAOCHONGZHENG)) {
			paramer.trans.setTransType(TRAN_REVOCATION_REVERSAL);
		} else if (name.equals(ZHIFUCHONGZHENG)) {
			paramer.trans.setTransType(TRAN_SALE_REVERSAL);
		}

		OldTrans oldTrans = new OldTrans();
		ChongZheng.chongzhengUnpack(data, oldTrans);
		paramer.oldTrans = oldTrans;
		paramer.oldTrans.toString();
		paramer.oldTrans.setOldTransDate(oldTransDate);
		paramer.trans.setPAN(oldTrans.getOldPan());
		paramer.trans.setTransAmount(oldTrans.getOldTransAmount());
		paramer.trans.setEntryMode(oldTrans.getOldEntryMode());
		paramer.trans.setPinMode(oldTrans.getOldPinMode());
		// (40域)
		paramer.apOrderId = oldTrans.getOldApOrderId();
		paramer.payOrderBatch = oldTrans.getOldPayOrderBatch();
		paramer.openBrh = oldTrans.getOldOpenBrh();
		paramer.cardId = oldTrans.getOldCardId();
		
		boolean isSuccess = pack8583(paramer);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;
	}
	
	/**
	 * 刷卡消费撤销
	 * 
	 * @param iso8583
	 *            要撤销的返回报文记录
	 * @param jsonObject
	 *            参数集合
	 * @return
	 */
	public boolean cheXiao(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_VOID);
		int[] bitMap = { 
				ISOField.F02_PAN, ISOField.F03_PROC,ISOField.F04_AMOUNT, 
				ISOField.F11_STAN, ISOField.F14_EXP,
				ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC, ISOField.F26_CAPTURE, 
				ISOField.F35_TRACK2, ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,  
				ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID, ISOField.F49_CURRENCY,
				ISOField.F52_PIN, ISOField.F53_SCI, ISOField.F55_ICC,
				ISOField.F60, ISOField.F61, ISOField.F64_MAC 
			};
		
		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}
	
	/**
	 * 退货
	 * 
	 * @param iso8583
	 *            要退货的返回报文记录
	 * @param jsonObject
	 *            参数集合
	 * @return
	 */
	public boolean refund(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_REFUND);
	      /* 03 REFUND */
		int[] bitMap = { 
				ISOField.F02_PAN, ISOField.F03_PROC,
				ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
				ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
				ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
				ISOField.F37_RRN, ISOField.F38_AUTH,  ISOField.F40,
				ISOField.F41_TID, ISOField.F42_ACCID, ISOField.F49_CURRENCY,
				ISOField.F52_PIN, ISOField.F53_SCI, 
				ISOField.F60, ISOField.F61, ISOField.F63, ISOField.F64_MAC 
			};
		
		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}
	
	public JSONObject updateMapFromOldTrans(byte[] iso8583, JSONObject jsonObject){
		byte[] data = new byte[iso8583.length - 2];
		System.arraycopy(iso8583, 2, data, 0, data.length - 2);
		OldTrans oldTrans = new OldTrans();
		ChongZheng.chongzhengUnpack(data, oldTrans);
		paramer.oldTrans = oldTrans;
		
		try {
			jsonObject.put("F02", oldTrans.getOldPan());
			jsonObject.put("F04", oldTrans.getOldTransAmount());
			jsonObject.put("F11", oldTrans.getOldTrace());
			jsonObject.put("F37", oldTrans.getOldRrn());

			jsonObject.put("F40_6F10", oldTrans.getOldApOrderId());
			jsonObject.put("F40_6F08", oldTrans.getOldPayOrderBatch());
			jsonObject.put("F40_6F20", oldTrans.getOldOpenBrh());
			jsonObject.put("F40_6F21", oldTrans.getOldCardId());			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject;
	}
	
	public boolean mapAndPack(JSONObject jsonObject, int[] bitMap){
		List<Integer> purchaseMap = new ArrayList<Integer>();
		for (int i = 0; i < bitMap.length; i++) {
			boolean save = true;
			switch (bitMap[i]) {
			case ISOField.F02_PAN:
				//主帐号 F02  
				paramer.trans.setPAN(jsonObject.optString("F02"));
				break;
			case ISOField.F04_AMOUNT:
				//消费金额 F04
				paramer.trans.setTransAmount(Integer.parseInt(jsonObject.optString("F04")));
				break;	
			case ISOField.F11_STAN:
				//POS终端交易流水 F11
				String trace = jsonObject.optString("F11", null);
				if(trace != null){
					paramer.trans.setTrace(Integer.parseInt(trace));
				}
				break;
			case ISOField.F35_TRACK2:
				//磁道2 F35
				String track2 = jsonObject.optString("F35", null);
				if(track2 != null){
					paramer.trans.setTrack2Data(track2);
				}else{
					save = false;
				}
				break;
			case ISOField.F36_TRACK3:
				//磁道3 F36
				String track3 = jsonObject.optString("F36", null);
				if(track3 != null){
					paramer.trans.setTrack3Data(track3);
				}else{
					save = false;
				}
				break;
			case ISOField.F37_RRN:
				//参考号 F37
				String rrn = jsonObject.optString("F37", null);
				if(rrn != null){
					paramer.trans.setRRN(rrn);
				}
				break;
			case ISOField.F40:
				setF40(jsonObject);
				break;
			case ISOField.F49_CURRENCY:
				//消费币种 F49
				paramer.trans.setTransCurrency(jsonObject.optString("F49", "156"));
				break;
			case ISOField.F52_PIN:
				//PIN F52
				String pinblock = jsonObject.optString("F52");
				if(!pinblock.isEmpty()){
					paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
					paramer.trans.setPinMode(HAVE_PIN);
				}else{
					save = false;
				}
				break;
			case ISOField.F60:
				//支付活动号 F60.6
				paramer.paymentId = jsonObject.optString("F60.6");

			default:
				break;
			}
			if(save){
				purchaseMap.add(bitMap[i]);
			}		
		}
		
		int[] map = new int[purchaseMap.size()];
		int i = 0;
		for (Integer e : purchaseMap) {
			map[i++] = e.intValue();
		}

		boolean isSuccess = pack8583(paramer, map);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;
	}
	
	private void setF40(JSONObject jsonObject) {
		// 机构号 F40 6F20
		paramer.openBrh = jsonObject.optString("F40_6F20");
		// 其他类型卡号 F40 6F21
		paramer.cardId = jsonObject.optString("F40_6F21", null);

		// 签名 F40 6F12
		paramer.signature = jsonObject.optString("F40_6F12");

		// 密码键盘加密的支付密码 F40 6F02
		String payPwd = jsonObject.optString("F40_6F02");
		if (!payPwd.isEmpty()) {
			paramer.payPwd = Utility.hex2byte(payPwd);
		}

		// 是否短信交易 F40 6F14
		paramer.isSendCode = jsonObject.optString("F40_6F14");
		
		// 通联订单号 F40 6F10
		paramer.apOrderId = jsonObject.optString("F40_6F10");
		// 现金流水/批次号 F40 6F08
		paramer.payOrderBatch = jsonObject.optString("F40_6F08");
		
		// 短信验证码 F40 6F11
		String authCode = jsonObject.optString("F40_6F11");
		if (!authCode.isEmpty()) {
			paramer.msgPwd = Utility.hex2byte(authCode);
		}
	}

	public byte[] to8583Array() {
		return mRequest;
	}

	public String toString() {
		String temp = "";
		for (byte b : mRequest) {
			temp += String.format("%02X", b);
		}
		return temp;
	}

	public boolean load(byte[] data1) {
		if (data1 == null) {
			paramer.trans.setResponseCode("FF".getBytes());
			return false;
		}
		byte[] data;
		if (data1.length >= 2) {
			data = new byte[data1.length - 2];
			System.arraycopy(data1, 2, data, 0, data.length - 2);
		} else {
			data = data1;
		}

		boolean isSuccess = unpack8583(data, paramer);
		if (isSuccess) {
			switch (paramer.trans.getTransType()) {
			case TRAN_LOGIN:
				if (updateWorkingKey(paramer)) {
					ISO8583Engine.getInstance().updateLocalBatchNumber();
				} else {
					paramer.trans.setResponseCode("F0".getBytes());
				}
				break;
			// 7种交易类型
			case TRAN_SALE:
				break;
			case TRAN_BALANCE:
				break;
			case TRAN_VOID:
				break;
			}
		}
		return isSuccess;
	}

	private boolean pack8583(UtilFor8583 paramer) {
		return pack8583(paramer, null);
	}
	
	private boolean pack8583(UtilFor8583 paramer, int[] bitMap) {
		UtilFor8583 appState = paramer;

		Log.d(APP_TAG, "paramer.terminalConfig.getTrace()"
				+ paramer.terminalConfig.getTrace());
		int ret = 0;

		ISOPackager.initField();

		switch (appState.getProcessType()) {
		case PROCESS_REVERSAL:
			ret = ISOPackager.pack(false, appState);

			break;
		case PROCESS_OFFLINE:
			ret = ISOPackager.pack(false, appState);
			break;
		default:
			if (appState.trans.getTransType() != TRAN_UPLOAD_MAG_OFFLINE) {
				appState.trans.setTrace(appState.terminalConfig.getTrace());
			}

			if (appState.trans.getTransType() == TRAN_BATCH) {
				switch (appState.terminalConfig.getBatchStatus()) {
				case BATCH_UPLOAD_PBOC_ONLINE:
				case BATCH_UPLOAD_PBOC_OFFLINE_FAIL:
				case BATCH_UPLOAD_PBOC_RISK:
				case BATCH_UPLOAD_ADVICE:
					ret = ISOPackager.pack(true, appState);
					break;
				default:
					ret = ISOPackager.pack(false, appState);
					break;
				}
			} else {
				ret = ISOPackager.pack(false, appState, bitMap);
			}
			break;
		}

		if (ret <= 0) {
			return false;
		}

		if (appState.trans.getMacFlag() == true) {
			mRequest = new byte[ISOPackager.getSendDataLength() + 10];

			byte[] macOut = new byte[8];
//			if (calculateMAC(ISOPackager.getSendData(), 11, ISOPackager.getSendDataLength() - 11, macOut, appState) == false) {
//				return false;
//			}
			if (calculateMAC2(ISOPackager.getSendData() , macOut, appState) == false) {
				return false;
			}
			Log.d(APP_TAG, "calculateMac: macOut = " + StringUtil.toBestString(macOut));
			appState.trans.setMac(macOut);
		} else {
			mRequest = new byte[ISOPackager.getSendDataLength() + 2];
		}
		mRequest[0] = (byte) ((mRequest.length - 2) / 256);
		mRequest[1] = (byte) ((mRequest.length - 2) % 256);

		System.arraycopy(ISOPackager.getSendData(), 0, mRequest, 2,
				ISOPackager.getSendDataLength());
		if (appState.trans.getMacFlag() == true) {
			System.arraycopy(appState.trans.getMac(), 0, mRequest,
					mRequest.length - 8, 8);
		}
		return true;
	}

//	private boolean calculateMAC(final byte[] data, final int offset,
//			final int length, byte[] dataOut, UtilFor8583 appState) {
//		if (debug) {
//			String strDebug = StringUtil.toBestString(data);
//			Log.d(APP_TAG, "check 1 MAC Data: " + strDebug);
//			strDebug = "";
//			for (int i = 0; i < length; i++) {
//				strDebug += String.format("%02X ", data[offset + i]);
//			}
//			Log.d(APP_TAG, "check 2 MAC Data: " + strDebug);
//		}
//
//		
//		byte[] out = new byte[8];
//		int lp, thismove, ret;
//		byte[] encryptData = new byte[8];
//
//		for (int pos = offset; pos < (length + offset); pos += thismove) {
//			thismove = ((length + offset - pos) >= 8) ? 8
//					: (length + offset - pos);
//			for (lp = 0; lp < thismove; lp++)
//				out[lp] ^= data[lp + pos];
//		}
//		byte[] temp = StringUtil.toHexString(out, false).getBytes();
//		System.arraycopy(temp, 0, out, 0, 8);
//		
//		// encrypt
//		ret = PinPadInterface.open();
//		Log.d(APP_TAG, "open ret = " + ret);
//		
//		ret = PinPadInterface.updateUserKey(Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1, StringUtil.hexString2bytes(appState.terminalConfig.getMAK()), 8);
//		Log.d(APP_TAG, "updateUserKey ret = " + ret);
//		if (ret < 0) {
//			return false;
//		}
//		PinPadInterface.selectKey(2,
//				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
//				SINGLE_KEY);
//		
//		ret = PinPadInterface.encrypt(out, 8, encryptData);
//		Log.d(APP_TAG, "encrypt ret = " + ret);
//		if (ret < 0) {
//			return false;
//		}
//		for (int i = 0; i < 8; i++) {
//			encryptData[i] ^= temp[8 + i];
//		}
//		// Encrypt
//		ret = PinPadInterface.encrypt(encryptData, 8, out);
//		if (ret < 0) {
//			return false;
//		}
//		temp = StringUtil.toHexString(out, false).getBytes();
//		System.arraycopy(temp, 0, dataOut, 0, 8);
//		
//		PinPadInterface.close();	//关闭占用
//		return true;
//	}

	private boolean calculateMAC2(final byte[] data, byte[] dataOut, UtilFor8583 appState) {
		String strDebug = "";
		if (debug) {
			strDebug = StringUtil.toBestString(data);
			Log.d(APP_TAG, "check 1 MAC Data: " + strDebug);
			strDebug = "";
			for (int i = 0; i < data.length -11; i++) {
				strDebug += String.format("%02X ", data[11 + i]);
			}
			Log.d(APP_TAG, "check 2 MAC Data: " + strDebug);
		}
		
		byte [] encryptData = StringUtil.hexString2bytes(strDebug);
		PinPadInterface.open();

		int ret = PinPadInterface.selectKey(2, Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1, SINGLE_KEY);
		
		ret = PinPadInterface.calculateMac(encryptData, encryptData.length, 0x10, dataOut);
		PinPadInterface.close();	//关闭占用
		if (ret < 0) {
			return false;
		}
		return true;
	}

	private boolean unpack8583(byte[] mSocketResponse, UtilFor8583 appState) {
		return ISOPackager.unpack(mSocketResponse, appState);
	}

	private boolean updateWorkingKey(UtilFor8583 appState) {
		// validate key
		// PIK直接写入pinpad中
		// MAK, TDK暂时写入参数文件中，用时再写入pinpad中

		if (debug) {
			String pik = "";
			for (byte b : appState.PIK) {
				pik += String.format("%02X", b);
			}
			String mak = "";
			for (byte b : appState.MAK) {
				mak += String.format("%02X", b);
			}
			String tdk = "";
			for (byte b : appState.TDK) {
				tdk += String.format("%02X", b);
			}
			Log.d(APP_TAG, "PIK = " + pik);
			Log.d(APP_TAG, "MAK = " + mak);
			Log.d(APP_TAG, "TDK = " + tdk);
			String temp = "";
			for (byte b : appState.PIKCheck) {
				temp += String.format("%02X", b);
			}
			temp = "";
			for (byte b : appState.MAKCheck) {
				temp += String.format("%02X", b);
			}
			temp = "";
			for (byte b : appState.TDKCheck) {
				temp += String.format("%02X", b);
			}
		}

		if (appState.PIK == null || appState.MAK == null
				|| appState.TDK == null) {
//			appState.setErrorCode(R.string.error_key_check);
			return false;
		}
		byte[] checkResult = new byte[8];

		PinPadInterface.open();
		// check pinKey
		int nResult = PinPadInterface.updateUserKey(
				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 0,
				appState.PIK, appState.PIK.length);
		if (nResult < 0) {
			// appState.setErrorCode(R.string.error_pinpad);
			return false;
		}
		Log.d(APP_TAG, "1: updateUserKey = " + nResult);
		nResult = PinPadInterface.selectKey(2,
				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 0,
				DOUBLE_KEY);
		if (nResult < 0) {
			// appState.setErrorCode(R.string.error_pinpad);
			return false;
		}
		// nResult = PinPadInterface.encrypt(new byte[]{0x00, 0x00, 0x00, 0x00,
		// 0x00, 0x00, 0x00, 0x00}, 8, checkResult);
		nResult = PinPadInterface.calculateMac(new byte[] { 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00 }, 8, 0x01, checkResult);
		Log.d("APP", "check pinKey: encrypt convert calculateMac : nResult = "
				+ nResult);
		if (nResult < 0) {
			// appState.setErrorCode(R.string.error_pinpad);
			return false;
		}
		if (ByteUtil.compareByteArray(appState.PIKCheck, 0, checkResult, 0, 4) != 0) {
			if (debug) {
				String strDebug = "";
				for (int i = 0; i < 4; i++)
					strDebug += String.format("%02X ", appState.PIKCheck[i]);
				Log.d(APP_TAG, "pinKeyCheck = " + strDebug);

				strDebug = "";
				for (int i = 0; i < 8; i++)
					strDebug += String.format("%02X ", checkResult[i]);
				Log.d(APP_TAG, "pin checkResult = " + strDebug);
			}
			return false;
		}
		Log.d(APP_TAG, "pinKey check OK");
		// check macKey
		nResult = PinPadInterface.updateUserKey(
				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
				appState.MAK, appState.MAK.length);
		Log.d(APP_TAG, "2: updateUserKey = " + nResult);
		if (nResult < 0) {
			return false;
		}
		Log.d(APP_TAG, "invoke selectKey method!");
		nResult = PinPadInterface.selectKey(2,
				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
				SINGLE_KEY);
		// Encrypt
		Log.d(APP_TAG, "selectKey nResult = " + nResult);
		nResult = PinPadInterface.calculateMac(new byte[] { 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00 }, 8, 0x01, checkResult);
		Log.d("APP", "check macKey: encrypt convert calculateMac : nResult = "
				+ nResult);

		if (ByteUtil.compareByteArray(appState.MAKCheck, 0, checkResult, 0, 4) != 0) {
			if (debug) {
				String strDebug = "";
				for (int i = 0; i < 4; i++)
					strDebug += String.format("%02X ", appState.MAKCheck[i]);
				Log.d(APP_TAG, "macKeyCheck = " + strDebug);

				strDebug = "";
				for (int i = 0; i < 8; i++)
					strDebug += String.format("%02X ", checkResult[i]);
				Log.d(APP_TAG, "mac checkResult = " + strDebug);
			}
			return false;
		}
		Log.d(APP_TAG, "macKey check OK");

		// check TDK
//		nResult = PinPadInterface.updateUserKey(
//				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
//				appState.TDK, appState.TDK.length);
//		if (nResult < 0) {
//			return false;
//		}
//		nResult = PinPadInterface.selectKey(2,
//				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
//				DOUBLE_KEY);
//		if (nResult < 0) {
//			return false;
//		}
//		// nResult = PinPadInterface.encrypt(new byte[]{0x00, 0x00, 0x00, 0x00,
//		// 0x00, 0x00, 0x00, 0x00}, 8, checkResult);
//		nResult = PinPadInterface.calculateMac(new byte[] { 0x00, 0x00, 0x00,
//				0x00, 0x00, 0x00, 0x00, 0x00 }, 8, 0x10, checkResult);
//		Log.e("APP", "check TDKkey: encrypt convert calculateMac : nResult = "
//				+ nResult);
//		if (nResult < 0) {
//			return false;
//		}
//		if (ByteUtil.compareByteArray(appState.TDKCheck, 0, checkResult, 0, 4) != 0) {
//			if (debug) {
//				String strDebug = "";
//				for (int i = 0; i < 4; i++)
//					strDebug += String.format("%02X ", appState.TDKCheck[i]);
//				Log.d(APP_TAG, "TDKCheck = " + strDebug);
//
//				strDebug = "";
//				for (int i = 0; i < 8; i++)
//					strDebug += String.format("%02X ", checkResult[i]);
//				Log.d(APP_TAG, "TDK checkResult = " + strDebug);
//			}
//		}
		Log.d(APP_TAG, "TDK check OK");
		appState.terminalConfig.setMAK(StringUtil.toHexString(appState.MAK,
				false));
		appState.terminalConfig.setTDK(StringUtil.toHexString(appState.TDK,
				false));
		
		PinPadInterface.close();	//关闭占用
		return true;
	}

	public void printer(byte[] request, byte[] respons, String operator, Context context)
			throws PrinterException {

		byte[] data = new byte[request.length - 2];
		System.arraycopy(request, 2, data, 0, data.length - 2);

		OldTrans oldTrans = new OldTrans();
		ChongZheng.chongzhengUnpack(data, oldTrans);

		data = new byte[respons.length - 2];
		System.arraycopy(respons, 2, data, 0, data.length - 2);
		ChongZheng.chongzhengUnpack(data, oldTrans);

		if (null == operator) {
			operator = "";
		}
		oldTrans.setOper(operator);
		Log.d(APP_TAG, "oldTrans : " + oldTrans.toString());
//		PrinterHelper.getInstance(context).printReceipt(oldTrans);
		PrinterHelper.getInstance(context).printQRCodeReceipt(oldTrans);
	}

	public String getResCode() {
		String resCode = "FF";
		resCode = StringUtil.toString(paramer.trans.getResponseCode());
		return resCode;
	}
	
	public String getRRN(){
		String rrn = "";
		rrn = paramer.trans.getRRN();
		return rrn;
	}
	
	public String getApOrderId(){
		String apOrderId = "";
		apOrderId = paramer.apOrderId;
		return apOrderId;
	}
	
	public String getBatch(){
		String batch = "";
		batch = paramer.payOrderBatch;
		return batch;
	}
	
	public String getTransTime(){
		String transTime = paramer.trans.getTransYear();
		if(transTime == null || transTime.isEmpty()){
			transTime = "" + paramer.currentYear;
		}
		transTime += paramer.trans.getTransDate() + paramer.trans.getTransTime();
		return transTime;
	}
}
