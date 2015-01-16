package cn.koolcloud.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.iso8583.ChongZheng;
import cn.koolcloud.iso8583.ISOField;
import cn.koolcloud.iso8583.ISOPackager;
import cn.koolcloud.jni.EmvL2Interface;
import cn.koolcloud.jni.PinPadInterface;
import cn.koolcloud.parameter.EMVICData;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.ISO8583Engine;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.Utility;
import cn.koolcloud.pos.entity.ConsumptionRecordBean;
import cn.koolcloud.pos.external.CardSwiper;
import cn.koolcloud.pos.external.EMVICManager;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.printer.exception.PrinterException;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.ByteUtil;
import cn.koolcloud.util.DateUtil;
import cn.koolcloud.util.StringUtil;

public class ISO8583Controller implements Constant {

	private static final String ZHIFUCHONGZHENG = "zhifuchongzheng"; // 普通冲正
	private static final String CHEXIAOCHONGZHENG = "chexiaochongzheng";// 撤销冲正
	private static final String PREAUTHREVERSE = "preauthreverse";// 预授冲正
	private static final String PREAUTHCOMPLETEREVERSE = "preauthcompletereverse";// 预授冲正
	private static final String PREAUTHCANCELREVERSE = "preauthcancelreverse";// 预授冲正
	private static final String PREAUTHCOMPLETECANCELREVERSE = "preauthcompletecancelreverse";// 预授冲正
	private String errorType = null;
	private String mId = "";
	private String tId = "";
	private String oriCardID = "";
	private int transId = 0; // 流水号

	private byte[] mRequest;

	UtilFor8583 paramer = UtilFor8583.getInstance();

	public ISO8583Controller(String mID, String tID, int transID,
			int batchNumber) {
		// paramer.trans.init();
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

		// if (batchNumber != 0) {
		//
		// } else {
		// paramer.trans.setBatchNumber(Integer.parseInt("600001"));
		// }

	}

	/**
	 * 签到
	 * 
	 * @return
	 */
	public boolean signin() {
		// 默认传递参数都是正确的，暂时为加入校验
		paramer.trans.setTransType(TRAN_LOGIN);
		paramer.trans.setApmpTransType(this.APMP_TRAN_SIGNIN);
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

	public boolean signout() {

		// 默认传递参数都是正确的，暂时为加入校验
		paramer.trans.setTransType(this.TRAN_LOGOUT);
		paramer.trans.setApmpTransType(this.APMP_TRAN_SIGNOUT);
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

	/**
	 * 批结
	 * 
	 * @return
	 */
	public boolean transBatch() {

		paramer.trans.setTransType(TRAN_BATCH);
		paramer.trans.setApmpTransType(this.APMP_TRAN_BATCHSETTLE);
		// 设置POS终端交易流水 (11域）
		paramer.terminalConfig.setTrace(transId);// 流水号
		// 设置商户号 (41域）
		paramer.terminalConfig.setMID(mId);
		// 设置终端号 (42域）
		paramer.terminalConfig.setTID(tId);

		boolean isSuccess = pack8583(paramer);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;
	}

	/**
	 * 参数、公钥查询
	 * 
	 * @return
	 */
	public boolean posUpStatus(JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_UPSTATUS);
		if (jsonObject.optString("paramType").equals("CAPK")) {
			paramer.trans.setParamType(PARAM_CAPK);
			// open kernel
			EMVICManager emvICm = EMVICManager.getEMVICManagerInstance();
			emvICm.downloadParamsInit();
		} else if (jsonObject.optString("paramType").equals("PARAM")) {
			paramer.trans.setParamType(PARAM_IC);
		}
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

	/**
	 * 下载参数、公钥
	 * 
	 * @return
	 */
	public boolean downloadParams(JSONObject jsonObject) {

		paramer.trans.setTransType(TRAN_DOWN_PARAM);
		if (jsonObject.optString("paramType").equals("CAPK")) {
			paramer.trans.setParamType(PARAM_CAPK);
		} else if (jsonObject.optString("paramType").equals("PARAM")) {
			paramer.trans.setParamType(PARAM_IC);
		}
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

	/**
	 * 下载参数、公钥结束
	 * 
	 * @return
	 */
	public boolean endDownloadParams(JSONObject jsonObject) {

		paramer.trans.setTransType(TRAN_DWON_CAPK_PARAM_END);
		if (jsonObject.optString("paramType").equals("CAPK")) {
			paramer.trans.setParamType(PARAM_CAPK);
		} else if (jsonObject.optString("paramType").equals("PARAM")) {
			paramer.trans.setParamType(PARAM_IC);
			EmvL2Interface.saveParam();
			EMVICManager emvICm = EMVICManager.getEMVICManagerInstance();
			emvICm.downloadParamsFinish();
		}
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
			String pinBlock, String open_brh, String payment_id) {

		paramer.trans.setTransType(TRAN_BALANCE);
		paramer.trans.setApmpTransType(APMP_TRAN_BALANCE);
		paramer.trans.setPAN(account); // 设置主帐号
		paramer.trans.setTrack2Data(track2);
		paramer.trans.setTrack3Data(track3);
		paramer.trans.setExpiry((new CardSwiper()).getCardValidTime(track2));

		// paramer.trans.setEntryMode(ConstantUtils.SEARCH_ENTRY_MODE);
		paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
		paramer.paymentId = payment_id;
		paramer.openBrh = open_brh;
		EMVICData mEMVICData = EMVICData.getEMVICInstance();
		int f55Len = mEMVICData.getF55Length();
		byte[] f55 = mEMVICData.getF55();
		if (f55Len != 0 && f55 != null) {
			paramer.trans.setICCRevData(f55, 0, f55Len);
		}

		// fix no pin block start
		int[] bitMap = null;
		if (!pinBlock.isEmpty()) {
			if (pinBlock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] {
						ISOField.F02_PAN,
						ISOField.F03_PROC,
						ISOField.F11_STAN,
						ISOField.F14_EXP,
						ISOField.F22_POSE,
						ISOField.F23,
						ISOField.F25_POCC,
						ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2,
						ISOField.F36_TRACK3,
						ISOField.F39_RSP,
						ISOField.F40,
						ISOField.F41_TID,
						ISOField.F42_ACCID,
						ISOField.F49_CURRENCY,
						/* ISOField.F52_PIN, ISOField.F53_SCI, */ISOField.F55_ICC,
						ISOField.F60, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinBlock(Utility.hex2byte(pinBlock));
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F11_STAN, ISOField.F14_EXP, ISOField.F22_POSE,
						ISOField.F23, ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
						ISOField.F42_ACCID, ISOField.F49_CURRENCY,
						ISOField.F52_PIN, ISOField.F53_SCI, ISOField.F55_ICC,
						ISOField.F60, ISOField.F64_MAC };
			}
		} else {
			paramer.trans.setPinBlock(Utility.hex2byte(pinBlock));
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F11_STAN, ISOField.F14_EXP, ISOField.F22_POSE,
					ISOField.F23, ISOField.F25_POCC, ISOField.F26_CAPTURE,
					ISOField.F35_TRACK2, ISOField.F36_TRACK3, ISOField.F39_RSP,
					ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F49_CURRENCY, ISOField.F52_PIN, ISOField.F53_SCI,
					ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
		}
		// fix no pin block end

		boolean isSuccess = pack8583(paramer, bitMap);
		if (isSuccess) {
			Log.d(APP_TAG, "pack 8583 ok!");
		} else {
			Log.e(APP_TAG, "pack 8583 failed!");
		}
		return isSuccess;
	}

	public boolean purchase(JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_SALE);
		paramer.trans.setApmpTransType(this.APMP_TRAN_CONSUME);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		// fix no pin block original start
		/*
		 * int[] bitMap = { ISOField.F02_PAN, ISOField.F03_PROC,
		 * ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
		 * ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
		 * ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
		 * ISOField.F38_AUTH, ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
		 * ISOField.F42_ACCID, ISOField.F49_CURRENCY, ISOField.F52_PIN,
		 * ISOField.F53_SCI, ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
		 */
		// fix no pin block original end

		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] {
						ISOField.F02_PAN,
						ISOField.F03_PROC,
						ISOField.F04_AMOUNT,
						ISOField.F11_STAN,
						ISOField.F14_EXP,
						ISOField.F22_POSE,
						ISOField.F23,
						ISOField.F25_POCC,
						ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2,
						ISOField.F36_TRACK3,
						/* ISOField.F38_AUTH, */ISOField.F39_RSP,
						ISOField.F40,
						ISOField.F41_TID,
						ISOField.F42_ACCID,
						ISOField.F49_CURRENCY,
						/* ISOField.F52_PIN, ISOField.F53_SCI, */ISOField.F55_ICC,
						ISOField.F60, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						/* ISOField.F38_AUTH, */ISOField.F39_RSP, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, ISOField.F55_ICC, ISOField.F60,
						ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3,
					/* ISOField.F38_AUTH, */ISOField.F39_RSP, ISOField.F40,
					ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F49_CURRENCY, ISOField.F52_PIN, ISOField.F53_SCI,
					ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
		}
		// fix no pin block end
		return mapAndPack(jsonObject, bitMap);
	}

	public boolean superTransfer(JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_SUPER_TRANSFER);
		paramer.trans.setApmpTransType(APMP_TRAN_SUPER_TRANSFER);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		// fix no pin block original start
		/*
		 * int[] bitMap = { ISOField.F02_PAN, ISOField.F03_PROC,
		 * ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
		 * ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
		 * ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
		 * ISOField.F38_AUTH, ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
		 * ISOField.F42_ACCID, ISOField.F49_CURRENCY, ISOField.F52_PIN,
		 * ISOField.F53_SCI, ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
		 */
		// fix no pin block original end

		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
						ISOField.F42_ACCID, ISOField.F44_ADDITIONAL,
						ISOField.F49_CURRENCY,
						/* ISOField.F52_PIN, ISOField.F53_SCI, */
						ISOField.F60, ISOField.F61, ISOField.F62,
						ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
						ISOField.F42_ACCID, ISOField.F44_ADDITIONAL,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, ISOField.F60, ISOField.F61,
						ISOField.F62, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F39_RSP, ISOField.F40,
					ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F44_ADDITIONAL, ISOField.F49_CURRENCY,
					ISOField.F52_PIN, ISOField.F53_SCI, ISOField.F60,
					ISOField.F61, ISOField.F62, ISOField.F64_MAC };
		}
		// fix no pin block end
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 冲正
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean chongZheng(byte[] iso8583, String oldTransDate, String name)
			throws Exception {

		byte[] data = new byte[iso8583.length - 2];
		System.arraycopy(iso8583, 2, data, 0, data.length - 2);
		if (name.equals(CHEXIAOCHONGZHENG)) {
			paramer.trans.setTransType(TRAN_REVOCATION_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		} else if (name.equals(ZHIFUCHONGZHENG)) {
			paramer.trans.setTransType(TRAN_SALE_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		} else if (name.equals(PREAUTHREVERSE)) {
			paramer.trans.setTransType(TRAN_AUTH_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		} else if (name.equals(PREAUTHCOMPLETEREVERSE)) {
			paramer.trans.setTransType(TRAN_AUTH_COMPLETE_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		} else if (name.equals(PREAUTHCANCELREVERSE)) {
			paramer.trans.setTransType(TRAN_AUTH_CANCEL_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		} else if (name.equals(PREAUTHCOMPLETECANCELREVERSE)) {
			paramer.trans.setTransType(TRAN_AUTH_COMPLETE_CANCEL_REVERSAL);
			paramer.trans.setApmpTransType(this.APMP_TRAN_OFFSET);
		}

		OldTrans oldTrans = new OldTrans();
		try {
			ChongZheng.chongzhengUnpack(data, oldTrans);
		} catch (Exception e) {
			setErrorType(ConstantUtils.ERROR_TYPE_0);
			e.printStackTrace();
			throw e;
		}
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
		paramer.trans.setApmpTransType(this.APMP_TRAN_CONSUMECANCE);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		// fix no pin block original start
		/*
		 * int[] bitMap = { ISOField.F02_PAN,
		 * ISOField.F03_PROC,ISOField.F04_AMOUNT, ISOField.F11_STAN,
		 * ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
		 * ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
		 * ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40, ISOField.F41_TID,
		 * ISOField.F42_ACCID, ISOField.F49_CURRENCY, ISOField.F52_PIN,
		 * ISOField.F53_SCI, ISOField.F55_ICC, ISOField.F60, ISOField.F61,
		 * ISOField.F64_MAC };
		 */
		// fix no pin block original end

		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY,
						/* ISOField.F52_PIN, ISOField.F53_SCI, */
						/* ISOField.F55_ICC, */
						ISOField.F60, ISOField.F61, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, /* ISOField.F55_ICC, */ISOField.F60,
						ISOField.F61, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F49_CURRENCY, ISOField.F52_PIN, ISOField.F53_SCI,
					ISOField.F55_ICC, ISOField.F60, ISOField.F61,
					ISOField.F64_MAC };
		}
		// fix no pin block end

		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 交易状态查询
	 *
	 * @param iso8583
	 *            原交易报文记录
	 * @param jsonObject
	 *            参数集合
	 * @return
	 */
	public boolean statusQuery(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_STATUS_QUERY);
		paramer.trans.setApmpTransType(this.APMP_TRANS_STATUS_QUERY);
//		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		// fix no pin block original start
		/*
		 * int[] bitMap = { ISOField.F02_PAN,
		 * ISOField.F03_PROC,ISOField.F04_AMOUNT, ISOField.F11_STAN,
		 * ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
		 * ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
		 * ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40, ISOField.F41_TID,
		 * ISOField.F42_ACCID, ISOField.F49_CURRENCY, ISOField.F52_PIN,
		 * ISOField.F53_SCI, ISOField.F55_ICC, ISOField.F60, ISOField.F61,
		 * ISOField.F64_MAC };
		 */
		// fix no pin block original end

		// fix no pin block start
		int[] bitMap = null;
		paramer.trans.setPinMode(ConstantUtils.NO_PIN);
		bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
				ISOField.F04_AMOUNT, ISOField.F11_STAN,
				ISOField.F22_POSE, ISOField.F25_POCC,
				ISOField.F35_TRACK2, ISOField.F36_TRACK3,
				ISOField.F37_RRN, ISOField.F40,
				ISOField.F41_TID, ISOField.F42_ACCID,
				ISOField.F49_CURRENCY,
				ISOField.F60, ISOField.F61, ISOField.F64_MAC };

		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 预授权
	 * 
	 * @param jsonObject
	 * @return
	 */
	public boolean preAuth(JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_AUTH);
		paramer.trans.setApmpTransType(this.APMP_TRAN_PREAUTH);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		/*
		 * F02_PAN, F03_PROC, F04_AMOUNT, F11_STAN, F14_EXP, F22_POSE, F23,
		 * F25_POCC, F26_CAPTURE, F35_TRACK2, F36_TRACK3, F38_AUTH, F39_RSP,
		 * F41_TID, F42_ACCID, F49_CURRENCY, F52_PIN, F53_SCI, F55_ICC, F60,
		 * F64_MAC
		 */
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
						ISOField.F42_ACCID, ISOField.F49_CURRENCY,
						/*
						 * ISOField.F52_PIN, ISOField.F53_SCI,
						 */
						ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
			} else {
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
						ISOField.F42_ACCID, ISOField.F49_CURRENCY,
						ISOField.F52_PIN, ISOField.F53_SCI, ISOField.F55_ICC,
						ISOField.F60, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F39_RSP, ISOField.F40,
					ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F49_CURRENCY, ISOField.F52_PIN, ISOField.F53_SCI,
					ISOField.F55_ICC, ISOField.F60, ISOField.F64_MAC };
		}
		// fix no pin block end
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 预授权完成联机
	 * 
	 * @param iso8583
	 * @param jsonObject
	 * @return
	 */
	public boolean preAuthComplete(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_AUTH_COMPLETE);
		paramer.trans.setApmpTransType(this.APMP_TRAN_PRAUTHCOMPLETE);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		/*
		 * FOR preAuthComplete field. F02_PAN, F03_PROC, F04_AMOUNT, F11_STAN,
		 * F14_EXP, F22_POSE, F23, F25_POCC, F26_CAPTURE, F35_TRACK2,
		 * F36_TRACK3, F38_AUTH, F39_RSP, F41_TID, F42_ACCID, F49_CURRENCY,
		 * F52_PIN, F53_SCI, F55_ICC, F60, F61, F64_MAC
		 */
		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F38_AUTH, ISOField.F39_RSP, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY,/* ISOField.F55_ICC, */
						ISOField.F60, ISOField.F61, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, /* ISOField.F55_ICC, */ISOField.F60,
						ISOField.F61, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
					ISOField.F42_ACCID, ISOField.F49_CURRENCY,
					ISOField.F52_PIN, ISOField.F53_SCI, /* ISOField.F55_ICC, */
					ISOField.F60, ISOField.F61, ISOField.F64_MAC };
		}
		// fix no pin block end

		jsonObject = updateMapAndOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 预授权完成离线
	 * 
	 * @param iso8583
	 * @param jsonObject
	 * @return
	 */
	public boolean preAuthSettlement(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(TRAN_AUTH_SETTLEMENT);
		paramer.trans.setApmpTransType(this.APMP_TRAN_PRAUTHSETTLEMENT);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		/*
		 * F02_PAN, F03_PROC, F04_AMOUNT, F11_STAN, F14_EXP, F22_POSE, F23,
		 * F25_POCC, F26_CAPTURE, F35_TRACK2, F36_TRACK3, F38_AUTH, F39_RSP,
		 * F41_TID, F42_ACCID, F49_CURRENCY, F52_PIN, F53_SCI, F55_ICC, F60,
		 * F61, F64_MAC
		 */
		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F38_AUTH, ISOField.F39_RSP, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, /* ISOField.F55_ICC, */
						ISOField.F60, ISOField.F61, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, /* ISOField.F55_ICC, */ISOField.F60,
						ISOField.F61, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
					ISOField.F42_ACCID, ISOField.F49_CURRENCY,
					/* ISOField.F55_ICC, */ISOField.F60, ISOField.F61,
					ISOField.F64_MAC };
		}
		// fix no pin block end

		jsonObject = updateMapAndOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 预授权撤销
	 * 
	 * @param iso8583
	 * @param jsonObject
	 * @return
	 */
	public boolean preAuthCancel(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(this.TRAN_AUTH_CANCEL);
		paramer.trans.setApmpTransType(this.APMP_TRAN_PRAUTHCANCEL);
		/*
		 * F02_PAN, F03_PROC, F04_AMOUNT, F11_STAN, F14_EXP, F22_POSE, F23,
		 * F25_POCC, F26_CAPTURE, F35_TRACK2, F36_TRACK3, F38_AUTH, F39_RSP,
		 * F41_TID, F42_ACCID, F49_CURRENCY, F52_PIN, F53_SCI, F55_ICC, F60,
		 * F61, F64_MAC
		 */
		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, /* ISOField.F55_ICC, */
						ISOField.F60, ISOField.F61, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, /* ISOField.F55_ICC, */ISOField.F60,
						ISOField.F61, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
					ISOField.F42_ACCID, ISOField.F49_CURRENCY,
					/* ISOField.F55_ICC, */ISOField.F60, ISOField.F61,
					ISOField.F64_MAC };
		}
		// fix no pin block end

		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	/**
	 * 预授权完成撤销
	 * 
	 * @param iso8583
	 * @param jsonObject
	 * @return
	 */
	public boolean preAuthCompleteCancel(byte[] iso8583, JSONObject jsonObject) {
		paramer.trans.setTransType(this.TRAN_AUTH_COMPLETE_CANCEL);
		paramer.trans.setApmpTransType(this.APMP_TRAN_PREAUTHCOMPLETECANCEL);
		/*
		 * F02_PAN, F03_PROC, F04_AMOUNT, F11_STAN, F14_EXP, F22_POSE, F23,
		 * F25_POCC, F26_CAPTURE, F35_TRACK2, F36_TRACK3, F37_RRN, F38_AUTH,
		 * F39_RSP, F41_TID, F42_ACCID, F49_CURRENCY, F52_PIN, F53_SCI, F55_ICC,
		 * F60, F61, F64_MAC
		 */
		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, /* ISOField.F55_ICC, */
						ISOField.F60, ISOField.F61, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F39_RSP,
						ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, /* ISOField.F55_ICC, */ISOField.F60,
						ISOField.F61, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F39_RSP, ISOField.F40, ISOField.F41_TID,
					ISOField.F42_ACCID, ISOField.F49_CURRENCY,
					/* ISOField.F55_ICC, */ISOField.F60, ISOField.F61,
					ISOField.F64_MAC };
		}
		// fix no pin block end

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
		paramer.trans.setApmpTransType(this.APMP_TRAN_REFUND);
		paramer.trans.setExpiry(jsonObject.optString("validTime"));
		/* 03 REFUND */
		// fix no pin block original start on 4th June
		/*
		 * int[] bitMap = { ISOField.F02_PAN, ISOField.F03_PROC,
		 * ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
		 * ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
		 * ISOField.F26_CAPTURE, ISOField.F35_TRACK2, ISOField.F36_TRACK3,
		 * ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40, ISOField.F41_TID,
		 * ISOField.F42_ACCID, ISOField.F49_CURRENCY, ISOField.F52_PIN,
		 * ISOField.F53_SCI, ISOField.F60, ISOField.F61, ISOField.F63,
		 * ISOField.F64_MAC };
		 */
		// fix no pin block original end on 4th June

		// fix no pin block start
		int[] bitMap = null;
		String pinblock = jsonObject.optString("F52");
		if (!pinblock.isEmpty()) {
			if (pinblock.equals(ConstantUtils.STR_NULL_PIN)) {
				paramer.trans.setPinMode(ConstantUtils.NO_PIN);
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY,
						/* ISOField.F52_PIN, ISOField.F53_SCI, */ISOField.F60,
						ISOField.F61, ISOField.F63, ISOField.F64_MAC };
			} else {
				paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
				bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
						ISOField.F04_AMOUNT, ISOField.F11_STAN,
						ISOField.F14_EXP, ISOField.F22_POSE, ISOField.F23,
						ISOField.F25_POCC, ISOField.F26_CAPTURE,
						ISOField.F35_TRACK2, ISOField.F36_TRACK3,
						ISOField.F37_RRN, ISOField.F38_AUTH, ISOField.F40,
						ISOField.F41_TID, ISOField.F42_ACCID,
						ISOField.F49_CURRENCY, ISOField.F52_PIN,
						ISOField.F53_SCI, ISOField.F60, ISOField.F61,
						ISOField.F63, ISOField.F64_MAC };
			}
		} else {
			bitMap = new int[] { ISOField.F02_PAN, ISOField.F03_PROC,
					ISOField.F04_AMOUNT, ISOField.F11_STAN, ISOField.F14_EXP,
					ISOField.F22_POSE, ISOField.F23, ISOField.F25_POCC,
					ISOField.F26_CAPTURE, ISOField.F35_TRACK2,
					ISOField.F36_TRACK3, ISOField.F37_RRN, ISOField.F38_AUTH,
					ISOField.F40, ISOField.F41_TID, ISOField.F42_ACCID,
					ISOField.F49_CURRENCY, ISOField.F52_PIN, ISOField.F53_SCI,
					ISOField.F60, ISOField.F61, ISOField.F63, ISOField.F64_MAC };
		}
		// fix no pin block end
		jsonObject = updateMapFromOldTrans(iso8583, jsonObject);
		return mapAndPack(jsonObject, bitMap);
	}

	public JSONObject updateMapFromOldTrans(byte[] iso8583,
			JSONObject jsonObject) {
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

			String paymentId = jsonObject.optString("paymentId");
			if (!paymentId.isEmpty()) {
				jsonObject.put("F60.6", paymentId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public JSONObject updateMapAndOldTrans(byte[] iso8583, JSONObject jsonObject) {
		byte[] data = new byte[iso8583.length - 2];
		System.arraycopy(iso8583, 2, data, 0, data.length - 2);
		OldTrans oldTrans = new OldTrans();
		ChongZheng.chongzhengUnpack(data, oldTrans);
		paramer.oldTrans = oldTrans;
		try {
			jsonObject.put("F02", oldTrans.getOldPan());
			jsonObject.put("F40_6F10", oldTrans.getOldApOrderId());
			jsonObject.put("F40_6F08", oldTrans.getOldPayOrderBatch());
			jsonObject.put("F40_6F20", oldTrans.getOldOpenBrh());
			jsonObject.put("F40_6F21", oldTrans.getOldCardId());

			String paymentId = jsonObject.optString("paymentId");
			if (!paymentId.isEmpty()) {
				jsonObject.put("F60.6", paymentId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public boolean mapAndPack(JSONObject jsonObject, int[] bitMap) {
		List<Integer> purchaseMap = new ArrayList<Integer>();
		for (int i = 0; i < bitMap.length; i++) {
			boolean save = true;
			switch (bitMap[i]) {
			case ISOField.F02_PAN:
				// 主帐号 F02  
				paramer.trans.setPAN(jsonObject.optString("F02"));
				break;
			case ISOField.F04_AMOUNT:
				// 消费金额 F04
				paramer.trans.setTransAmount(Long.parseLong(jsonObject
						.optString("F04")));
				break;
			case ISOField.F11_STAN:
				// POS终端交易流水 F11
				String trace = jsonObject.optString("F11", null);
				if (trace != null) {
					paramer.trans.setTrace(Integer.parseInt(trace));
				}
				break;
			case ISOField.F35_TRACK2:
				// 磁道2 F35
				String track2 = jsonObject.optString("F35", null);
				if (track2 != null) {
					paramer.trans.setTrack2Data(track2);
				} else {
					save = false;
				}
				break;
			case ISOField.F36_TRACK3:
				// 磁道3 F36
				String track3 = jsonObject.optString("F36", null);
				if (track3 != null) {
					paramer.trans.setTrack3Data(track3);
				} else {
					save = false;
				}
				break;
			case ISOField.F37_RRN:
				// 参考号 F37
				String rrn = jsonObject.optString("F37", null);
				if (rrn != null) {
					paramer.trans.setRRN(rrn);
				}
				break;
			case ISOField.F40:
				setF40(jsonObject);
				break;
			case ISOField.F49_CURRENCY:
				// 消费币种 F49
                //upload currency
                String currency = "";

                Map<String, ?> merchMap = UtilForDataStorage.readPropertyBySharedPreferences(MyApplication.getContext(), "merchant");
                if (merchMap.containsKey("currency")) {
                    currency = String.valueOf(merchMap.get("currency"));
                } else {
                    currency = "156";
                }
				paramer.trans.setTransCurrency(jsonObject.optString("F49", currency));
				break;
			case ISOField.F52_PIN:
				// PIN F52
				String pinblock = jsonObject.optString("F52");
				if (!pinblock.isEmpty()) {
					paramer.trans.setPinBlock(Utility.hex2byte(pinblock));
					paramer.trans.setPinMode(ConstantUtils.HAVE_PIN);
				} else {
					save = false;
				}
				break;
			case ISOField.F55_ICC:
				EMVICData mEMVICData = EMVICData.getEMVICInstance();
				int f55Len = mEMVICData.getF55Length();
				byte[] f55 = mEMVICData.getF55();
				if (f55Len != 0 && f55 != null) {
					paramer.trans.setICCRevData(f55, 0, f55Len);
				}
				break;
			case ISOField.F60:
				// 支付活动号 F60.6
				paramer.paymentId = jsonObject.optString("F60.6");
				paramer.paymentId = paramer.paymentId
						.substring(paramer.paymentId.length() - 4);
			case ISOField.F61:
				paramer.trans.setIdCardNo(jsonObject.optString("F61"));
				break;
			case ISOField.F62:
				paramer.trans.setToAccountCardNo(jsonObject.optString("F62"));
				break;
			default:
				break;
			}
			if (save) {
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
			System.arraycopy(data1, 2, data, 0, data.length);
		} else {
			data = data1;
		}

		boolean isSuccess = unpack8583(data, paramer);
		if (isSuccess) {
			switch (paramer.trans.getTransType()) {
			case TRAN_LOGIN:
				if (updateWorkingKey(paramer)) {
					Map<String, ?> map = UtilForDataStorage
							.readPropertyBySharedPreferences(
									MyApplication.getContext(), "merchant");
					int oldBatchId = ((Integer) map.get("batchId")).intValue();
					int newBatchId = paramer.trans.getBatchNumber();
					if (newBatchId > oldBatchId) {
						ISO8583Engine.getInstance().updateLocalBatchNumber(
								paramer);
					}
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
			// if (calculateMAC(ISOPackager.getSendData(), 11,
			// ISOPackager.getSendDataLength() - 11, macOut, appState) == false)
			// {
			// return false;
			// }
			if (calculateMAC2(ISOPackager.getSendData(), macOut, appState) == false) {
				return false;
			}
			Log.d(APP_TAG,
					"calculateMac: macOut = " + StringUtil.toBestString(macOut));
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

	// private boolean calculateMAC(final byte[] data, final int offset,
	// final int length, byte[] dataOut, UtilFor8583 appState) {
	// if (debug) {
	// String strDebug = StringUtil.toBestString(data);
	// Log.d(APP_TAG, "check 1 MAC Data: " + strDebug);
	// strDebug = "";
	// for (int i = 0; i < length; i++) {
	// strDebug += String.format("%02X ", data[offset + i]);
	// }
	// Log.d(APP_TAG, "check 2 MAC Data: " + strDebug);
	// }
	//
	//
	// byte[] out = new byte[8];
	// int lp, thismove, ret;
	// byte[] encryptData = new byte[8];
	//
	// for (int pos = offset; pos < (length + offset); pos += thismove) {
	// thismove = ((length + offset - pos) >= 8) ? 8
	// : (length + offset - pos);
	// for (lp = 0; lp < thismove; lp++)
	// out[lp] ^= data[lp + pos];
	// }
	// byte[] temp = StringUtil.toHexString(out, false).getBytes();
	// System.arraycopy(temp, 0, out, 0, 8);
	//
	// // encrypt
	// ret = PinPadInterface.open();
	// Log.d(APP_TAG, "open ret = " + ret);
	//
	// ret =
	// PinPadInterface.updateUserKey(Integer.parseInt(appState.terminalConfig.getKeyIndex()),
	// 1, StringUtil.hexString2bytes(appState.terminalConfig.getMAK()), 8);
	// Log.d(APP_TAG, "updateUserKey ret = " + ret);
	// if (ret < 0) {
	// return false;
	// }
	// PinPadInterface.selectKey(2,
	// Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
	// SINGLE_KEY);
	//
	// ret = PinPadInterface.encrypt(out, 8, encryptData);
	// Log.d(APP_TAG, "encrypt ret = " + ret);
	// if (ret < 0) {
	// return false;
	// }
	// for (int i = 0; i < 8; i++) {
	// encryptData[i] ^= temp[8 + i];
	// }
	// // Encrypt
	// ret = PinPadInterface.encrypt(encryptData, 8, out);
	// if (ret < 0) {
	// return false;
	// }
	// temp = StringUtil.toHexString(out, false).getBytes();
	// System.arraycopy(temp, 0, dataOut, 0, 8);
	//
	// PinPadInterface.close(); //关闭占用
	// return true;
	// }

	private boolean calculateMAC2(final byte[] data, byte[] dataOut,
			UtilFor8583 appState) {
		String strDebug = "";
		if (debug) {
			strDebug = StringUtil.toBestString(data);
			Log.d(APP_TAG, "check 1 MAC Data: " + strDebug);
			strDebug = "";
			for (int i = 0; i < data.length - 11; i++) {
				strDebug += String.format("%02X ", data[11 + i]);
			}
			Log.d(APP_TAG, "check 2 MAC Data: " + strDebug);
		}

		byte[] encryptData = StringUtil.hexString2bytes(strDebug);
		PinPadInterface.open();

		int ret = PinPadInterface.selectKey(2,
				Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
				SINGLE_KEY);

		ret = PinPadInterface.calculateMac(encryptData, encryptData.length,
				0x10, dataOut);
		PinPadInterface.close(); // 关闭占用
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
			if (appState.PIK == null || appState.MAK == null
					|| appState.TDK == null) {
				return false;
			}
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
			// appState.setErrorCode(R.string.error_key_check);
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
		// nResult = PinPadInterface.updateUserKey(
		// Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
		// appState.TDK, appState.TDK.length);
		// if (nResult < 0) {
		// return false;
		// }
		// nResult = PinPadInterface.selectKey(2,
		// Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1,
		// DOUBLE_KEY);
		// if (nResult < 0) {
		// return false;
		// }
		// // nResult = PinPadInterface.encrypt(new byte[]{0x00, 0x00, 0x00,
		// 0x00,
		// // 0x00, 0x00, 0x00, 0x00}, 8, checkResult);
		// nResult = PinPadInterface.calculateMac(new byte[] { 0x00, 0x00, 0x00,
		// 0x00, 0x00, 0x00, 0x00, 0x00 }, 8, 0x10, checkResult);
		// Log.e("APP",
		// "check TDKkey: encrypt convert calculateMac : nResult = "
		// + nResult);
		// if (nResult < 0) {
		// return false;
		// }
		// if (ByteUtil.compareByteArray(appState.TDKCheck, 0, checkResult, 0,
		// 4) != 0) {
		// if (debug) {
		// String strDebug = "";
		// for (int i = 0; i < 4; i++)
		// strDebug += String.format("%02X ", appState.TDKCheck[i]);
		// Log.d(APP_TAG, "TDKCheck = " + strDebug);
		//
		// strDebug = "";
		// for (int i = 0; i < 8; i++)
		// strDebug += String.format("%02X ", checkResult[i]);
		// Log.d(APP_TAG, "TDK checkResult = " + strDebug);
		// }
		// }
		Log.d(APP_TAG, "TDK check OK");
		appState.terminalConfig.setMAK(StringUtil.toHexString(appState.MAK,
				false));
		appState.terminalConfig.setTDK(StringUtil.toHexString(appState.TDK,
				false));

		PinPadInterface.close(); // 关闭占用
		return true;
	}

	public void printer(byte[] request, byte[] respons, JSONObject jsonObject, Context context)
			throws PrinterException {
		byte[] data;
        String operator = jsonObject.optString("userName");
		String paymentId = jsonObject.optString("paymentId");
		String paymentName = jsonObject.optString("paymentName");
        String txnId = jsonObject.optString("txnId");

		OldTrans oldTrans = new OldTrans();
		if(request != null) {
			data = new byte[request.length - 2];
			System.arraycopy(request, 2, data, 0, data.length - 2);
			ChongZheng.chongzhengUnpack(data, oldTrans);
		}
		data = new byte[respons.length - 2];
		System.arraycopy(respons, 2, data, 0, data.length - 2);
		ChongZheng.chongzhengUnpack(data, oldTrans);

		if (null == operator) {
			operator = "";
		}
		oldTrans.setOper(operator);
		Log.d(APP_TAG, "oldTrans : " + oldTrans.toString());

		// get print type
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(context, "paymentInfo");
		String paymentStr = (String) map.get(paymentId);

		String prdtNo = "";
        String printType = "";
		try {
			JSONObject jsonObj = new JSONObject(paymentStr);
			prdtNo = jsonObj.getString("prdtNo");
            printType = jsonObj.getString("printType");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// get merchant name
		Map<String, ?> merchMap = UtilForDataStorage
				.readPropertyBySharedPreferences(context, "merchant");
		String merchName = (String) merchMap.get("merchName");
		oldTrans.setOldMertName(merchName);

		// set payment name
		oldTrans.setPaymentName(paymentName);
		oldTrans.setPaymentId(paymentId);
        oldTrans.setTxnId(txnId);

        //set from tag
        String fromTag = jsonObject.optString("from");
        if (!TextUtils.isEmpty(fromTag) && fromTag.equals("recordList")) {
            oldTrans.setPrintFromTag(fromTag);
        }

		if (!TextUtils.isEmpty(printType)
				&& printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY)) {
			PrinterHelper.getInstance(context).printQRCodeReceipt(oldTrans);
		} else if (!TextUtils.isEmpty(printType)
                && printType.equals(ConstantUtils.PRINT_TYPE_WECHAT)) {
            PrinterHelper.getInstance(context).printWeChatReceipt(oldTrans);
        } else if (!TextUtils.isEmpty(printType)
                && printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY_OVER_SEA)) {
            PrinterHelper.getInstance(context).printQRCodeOverSeaReceipt(oldTrans);
        } else if (!TextUtils.isEmpty(printType) && printType.equals(ConstantUtils.PRINT_TYPE_TRANSFER)) {
            PrinterHelper.getInstance(context).printTransferReceipt(oldTrans);
        } else {
			PrinterHelper.getInstance(context).printReceipt(oldTrans);
		}
	}

    public void printRecord(JSONObject jsonObject, Context context) throws PrinterException {

        // get merchant name
        Map<String, ?> merchMap = UtilForDataStorage.readPropertyBySharedPreferences(context, "merchant");
        ConsumptionRecordBean record = new ConsumptionRecordBean();
        if (null != merchMap) {
            record.setMerchName(String.valueOf(merchMap.get("merchName")));
            record.setMerchNo(String.valueOf(merchMap.get("merchId")));
            record.setTerId(String.valueOf(merchMap.get("tID")));
            record.setBatchNo(String.valueOf(merchMap.get("batchId")));
            record.setOperator(jsonObject.optString("ioperator"));
            record.setPrintTime(DateUtil.formatDate(new Date()));
        }
        int totalSize = jsonObject.optInt("totalSize");
        if (totalSize > 0) {
            JSONArray itemArray = jsonObject.optJSONArray("recordList");
            record.setSummaryMap(UtilForJSON.parseRecordSummary(itemArray));
        }

        PrinterHelper.getInstance(context).printRecord(record, jsonObject);
    }

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getResCode() {
		String resCode = "FF";
		resCode = StringUtil.toString(paramer.trans.getResponseCode());
		return resCode;
	}

	public int getICTranserMsgResult() {
		return paramer.trans.getIcTransferMsgResult();
	}

	public String getRRN() {
		String rrn = "";
		rrn = paramer.trans.getRRN();
		return rrn;
	}

	public Boolean getParamDownloadFlag() {
		Boolean rs = paramer.trans.getParamDownloadFlag();
		return rs;
	}

	public Boolean getParamsCapkDownloadNeed() {
		Boolean rs = paramer.trans.getIcParamsCapkDownloadNeed();
		return rs;
	}

	public Boolean getIcParamsCapkCheckNeed() {
		Boolean rs = paramer.trans.getIcParamsCapkCheckNeed();
		return rs;
	}

	public String getApOrderId() {
		String apOrderId = "";
		apOrderId = paramer.apOrderId;
		return apOrderId;
	}

	public String getBatch() {
		String batch = "";
		batch = paramer.payOrderBatch;
		return batch;
	}

	public String getPaymentId() {
		String paymentId = paramer.paymentId;
		if (paymentId.equals("")) {
			return null;
		} else {
			return paymentId;
		}
	}

	public String getOldRrn(){
		String rrn = null;
		if(paramer.oldTrans != null) {
			rrn = paramer.oldTrans.getOldApOrderId();
			if (TextUtils.isEmpty(rrn)) {
				rrn = null;
				return rrn;
			} else {
				return rrn;
			}
		}else{
			return rrn;
		}
	}

	public String getStatusResCode(){
		String resCode = paramer.statusQueryRes;
		if(resCode.equals("")){
			return null;
		}else{
			return resCode;
		}
	}

	public String getTransTime() {
		String transTime = paramer.trans.getTransYear();
		paramer.getCurrentDateTime();
		if (transTime == null || transTime.isEmpty()) {
			transTime = "" + paramer.currentYear;
		}
		if (paramer.trans.getTransDate().equals("0000")
				|| paramer.trans.getTransTime().equals("000000")) {
			Date now = new Date();
			//这里之所以传8时区，因为这里应该传服务器所在时区的时间。
			transTime = DateUtil.formatDate(now,"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00"));
		} else {
			transTime += paramer.trans.getTransDate();
			transTime += paramer.trans.getTransTime();
		}
		return transTime;
	}

	public String getCurrentTime() {
		String transTime;
		Date now = new Date();
		//这个时间是当前终端的时间，所以获取机器默认时区。
		transTime = DateUtil.formatDate(now,"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00"));
		return transTime;
	}

	public String getBankCardNum() {
		if (paramer.trans.getPAN().equals("")) {
			return null;
		} else {
			return paramer.trans.getPAN();
		}
	}

	public String getIssuerName() {
		String issuerName = paramer.trans.getIssuerName();
		return issuerName;
	}

	public String getIssuerId() {
		if (paramer.trans.getIssuerID().equals("")) {
			return null;
		} else {
			return paramer.trans.getIssuerID();
		}
	}

	public String getAlipayAccount() {
		return paramer.alipayAccount;
	}

	public String getAlipayPID() {
		return paramer.alipayPID;
	}

	public String getAlipayTransactionID() {
		return paramer.alipayTransactionID;
	}

	public String getApmpTransType() {
		return paramer.trans.getApmpTransType();
	}

	public String getBatchNum() {

		return IntegerOne2StringSix(paramer.trans.getBatchNumber());
	}

	public String getTraceNum() {
		return IntegerOne2StringSix(paramer.trans.getTrace());
	}

	public Long getTransAmount() {
		return paramer.trans.getTransAmount();
	}

	public String getOriBatchNum() {
		if (paramer.oldTrans == null) {
			return null;
		} else {
			return IntegerOne2StringSix(paramer.oldTrans.getOldBatch());
		}
	}

	public String getOriTraceNum() {
		if (paramer.oldTrans == null) {
			return null;
		} else {
			return IntegerOne2StringSix(paramer.oldTrans.getOldTrace());
		}
	}

	public String getOriTransTime() {
		if (paramer.oldTrans == null) {
			return null;
		} else {
			String transTime = paramer.oldTrans.getOldTransYear();
			if (transTime == null || transTime.isEmpty()) {
				transTime = "" + paramer.currentYear;
			}

			if (paramer.oldTrans.getOldTransDate() == null
					|| paramer.oldTrans.getOldTransTime() == null) {
				Date now = new Date();
				transTime = DateUtil.formatDate(now,"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00"));
			} else {
				transTime += paramer.oldTrans.getOldTransDate()
						+ paramer.oldTrans.getOldTransTime();
			}
			return transTime;
		}
	}

	public String getDateExpiry() {
		if (paramer.trans.getExpiry().equals("")) {
			return null;
		} else {
			return paramer.trans.getExpiry();
		}
	}

	public String getSettlementTime() {
		if (paramer.trans.getSettlementTime().equals("")) {
			return null;
		} else {
			return paramer.trans.getSettlementTime();
		}
	}

	public String getAuthCode() {
		if (paramer.trans.getAuthCode().equals("")) {
			return null;
		} else {
			return paramer.trans.getAuthCode();
		}
	}

	public void setAuthCode(String authCode) {
		paramer.trans.setAuthCode(authCode);
	}

	public String IntegerOne2StringSix(int num) {
		int covertNum = num;
		String str = "000000" + String.valueOf(covertNum);
		String convertNumStr = str.substring(str.length() - 6);
		return convertNumStr;
	}
}
