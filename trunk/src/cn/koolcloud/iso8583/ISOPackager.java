package cn.koolcloud.iso8583;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

import android.util.Log;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.EmvL2Interface;
import cn.koolcloud.parameter.EMVICData;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.external.EMVICManager;
import cn.koolcloud.util.AppUtil;
import cn.koolcloud.util.ByteUtil;
import cn.koolcloud.util.NumberUtil;
import cn.koolcloud.util.StringUtil;

public class ISOPackager implements Constant {
	private static final byte FFIX = 0x00;
	private static final byte FLLVAR = 0x01;
	private static final byte FLLLVAR = 0x02;

	private static final byte ATTBIN = 0x00;
	private static final byte ATTN = 0x04;
	private static final byte ATTAN = 0x08;
	private static final byte ATTANS = 0x0C;

	public static byte[] F_MessageType = new byte[2];
	private static byte[] F3_ProcessingCode;// 第三域的processCode

	private static ISOData iso = new ISOData();
	private static byte[] sendData = null;
	private static int sendDataLength = 0;
	/*
	 * 对55域 的解析
	 */
	static byte[] t9f36 = { (byte) 0x9F, (byte) 0x36 };
	static byte[] t91 = { (byte) 0x91 };
	static byte[] t71 = { (byte) 0x71 };
	static byte[] t72 = { (byte) 0x72 };
	static byte[] c9f36 = null;
	static byte[] c91 = null;
	static byte[] c71 = null;
	static byte[] c72 = null;

	public static byte[] getSendData() {
		return sendData;
	}

	public static int getSendDataLength() {
		return sendDataLength;
	}

	/**
	 * Initializtion ISO8583 Field value = null
	 */
	public static void initField() {
		iso.offset = 0;
	}

	/**
	 * 支付密码： 放在40域6F02标签，加密和转换同52域PIN 短信验证码： 放在40域6F11标签，加密和转换同52域PIN
	 * 通联商户订单号：放在40域6F10标签 批次号： 放在40域6F08标签 签名： 放在40域6F12标签 是否短信交易： 放到40域6F14标签
	 * 机构号： 放到40域6F20标签 其他类型卡号： 放到40域6F21标签
	 * */
	private static void setF40_CUP(UtilFor8583 appState) {
		int F40_Length = 0;
		byte[] tmpBuf = new byte[512];
		byte[] tag_1 = "6F02".getBytes();
		byte[] tag_2 = "6F11".getBytes();
		byte[] tag_3 = "6F08".getBytes();
		byte[] tag_4 = "6F12".getBytes();
		byte[] tag_5 = "6F13".getBytes();

		// byte[] tag_6 = "6F10".getBytes();

		byte[] tag_7 = "6F14".getBytes();
		byte[] tag_8 = "6F20".getBytes();
		byte[] tag_9 = "6F21".getBytes();

		byte[] F40_TYPE = "11".getBytes();

		// 支付密码
		byte[] pinBlock_1 = appState.payPwd/* appState.trans.getPinBlock() */; // 6F02

		if (pinBlock_1 != null) {
			System.arraycopy(tag_1, 0, tmpBuf, F40_Length, tag_1.length);
			F40_Length = F40_Length + tag_1.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;
			byte[] pinBlock_1_length = NumberUtil.longToAscii(
					pinBlock_1.length, 4);
			System.arraycopy(pinBlock_1_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(pinBlock_1, 0, tmpBuf, F40_Length, 8);
			F40_Length = F40_Length + 8;
		} else {
			pinBlock_1 = new byte[] { 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
					0x20 };
			System.arraycopy(tag_1, 0, tmpBuf, F40_Length, tag_1.length);
			F40_Length = F40_Length + tag_1.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;
			byte[] pinBlock_1_length = NumberUtil.longToAscii(
					pinBlock_1.length, 4);
			System.arraycopy(pinBlock_1_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(pinBlock_1, 0, tmpBuf, F40_Length, 8);
			F40_Length = F40_Length + 8;
		}

		// 短信验证码
		byte[] pinBlock_2 = appState.msgPwd; // 6F11

		if (pinBlock_2 != null) {
			System.arraycopy(tag_2, 0, tmpBuf, F40_Length, tag_2.length);
			F40_Length = F40_Length + tag_1.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;
			byte[] pinBlock_2_length = NumberUtil.longToAscii(
					pinBlock_2.length, 4);
			System.arraycopy(pinBlock_2_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(pinBlock_2, 0, tmpBuf, F40_Length, 8);
			F40_Length = F40_Length + 8;
		} else {
			pinBlock_2 = new byte[] { 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
					0x20 };
			System.arraycopy(tag_2, 0, tmpBuf, F40_Length, tag_2.length);
			F40_Length = F40_Length + tag_1.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;
			byte[] pinBlock_2_length = NumberUtil.longToAscii(
					pinBlock_2.length, 4);
			System.arraycopy(pinBlock_2_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(pinBlock_2, 0, tmpBuf, F40_Length, 8);
			F40_Length = F40_Length + 8;
		}

		// 批次号
		System.arraycopy(tag_3, 0, tmpBuf, F40_Length, tag_1.length);
		F40_Length = F40_Length + tag_1.length;
		System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
		F40_Length = F40_Length + F40_TYPE.length;
		byte[] url = appState.payOrderBatch.getBytes(); // 6F08
		byte[] url_length = NumberUtil.longToAscii(url.length, 4);
		System.arraycopy(url_length, 0, tmpBuf, F40_Length, 4);
		F40_Length = F40_Length + 4;
		System.arraycopy(url, 0, tmpBuf, F40_Length, url.length);
		F40_Length = F40_Length + url.length;

		// 通联商户订单号
		/*
		 * System.arraycopy(tag_6, 0, tmpBuf, F40_Length, tag_6.length);
		 * F40_Length = F40_Length + tag_6.length; System.arraycopy(F40_TYPE, 0,
		 * tmpBuf, F40_Length, F40_TYPE.length); F40_Length = F40_Length +
		 * F40_TYPE.length; byte[] order_ID = appState.apOrderId.getBytes(); //
		 * 6F10 byte[] order_ID_length = NumberUtil.longToAscii(order_ID.length,
		 * 4); System.arraycopy(order_ID_length, 0, tmpBuf, F40_Length, 4);
		 * F40_Length = F40_Length + 4; System.arraycopy(order_ID, 0, tmpBuf,
		 * F40_Length, order_ID.length); F40_Length = F40_Length +
		 * order_ID.length;
		 */

		// 签名

		if (appState.signature != null) {
			byte[] signature = appState.signature.getBytes(); // 6F12
			System.arraycopy(tag_4, 0, tmpBuf, F40_Length, tag_4.length);
			F40_Length = F40_Length + tag_4.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;

			byte[] signature_length = NumberUtil.longToAscii(signature.length,
					4);
			System.arraycopy(signature_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(signature, 0, tmpBuf, F40_Length, signature.length);
			F40_Length = F40_Length + signature.length;
		}

		// 业务码
		// System.arraycopy(tag_5, 0, tmpBuf, F40_Length, tag_5.length);
		// F40_Length = F40_Length + tag_5.length;
		// System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
		// F40_Length = F40_Length + F40_TYPE.length;
		// byte[] productCode = appState.sale_F40_Type.getBytes(); // 6F13
		// byte[] productCode_length =
		// NumberUtil.longToAscii(productCode.length,
		// 4);
		// System.arraycopy(productCode_length, 0, tmpBuf, F40_Length, 4);
		// F40_Length = F40_Length + 4;
		// System.arraycopy(productCode, 0, tmpBuf, F40_Length,
		// productCode.length);
		// F40_Length = F40_Length + productCode.length;

		// 短信交易

		if (appState.isSendCode != null) {
			byte[] sendCode = appState.isSendCode.getBytes(); // 6F14
			System.arraycopy(tag_7, 0, tmpBuf, F40_Length, tag_7.length);
			F40_Length = F40_Length + tag_7.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;

			byte[] sendCode_length = NumberUtil.longToAscii(sendCode.length, 4);
			System.arraycopy(sendCode_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(sendCode, 0, tmpBuf, F40_Length, sendCode.length);
			F40_Length = F40_Length + sendCode.length;
		}

		// 卡券机构号： 放在40域6F20标签

		byte[] openBrh = appState.openBrh.getBytes(); // 6F20
		System.arraycopy(tag_8, 0, tmpBuf, F40_Length, tag_8.length);
		F40_Length = F40_Length + tag_8.length;
		System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
		F40_Length = F40_Length + F40_TYPE.length;

		byte[] openBrh_length = NumberUtil.longToAscii(openBrh.length, 4);
		System.arraycopy(openBrh_length, 0, tmpBuf, F40_Length, 4);
		F40_Length = F40_Length + 4;
		System.arraycopy(openBrh, 0, tmpBuf, F40_Length, openBrh.length);
		F40_Length = F40_Length + openBrh.length;

		if (appState.cardId != null) {
			byte[] cardId = appState.cardId.getBytes(); // 6F21
			System.arraycopy(tag_9, 0, tmpBuf, F40_Length, tag_9.length);
			F40_Length = F40_Length + tag_9.length;
			System.arraycopy(F40_TYPE, 0, tmpBuf, F40_Length, F40_TYPE.length);
			F40_Length = F40_Length + F40_TYPE.length;

			byte[] cardId_length = NumberUtil.longToAscii(cardId.length, 4);
			System.arraycopy(cardId_length, 0, tmpBuf, F40_Length, 4);
			F40_Length = F40_Length + 4;
			System.arraycopy(cardId, 0, tmpBuf, F40_Length, cardId.length);
			F40_Length = F40_Length + cardId.length;
		}

		movGen(ISOField.F40, tmpBuf, F40_Length);

		// 初始化临时属性
		appState.cardId = null;
		appState.payPwd = null;
		appState.msgPwd = null;
		appState.signature = null;
		appState.isSendCode = null;
		appState.openBrh = null;
	}

	private static void setF48_CUP(UtilFor8583 appState, boolean uploadFlag) {
		byte index;
		byte[] tmpAddData = new byte[4];
		byte[] tmpBuf = new byte[62];
		byte[] F48_Additional;

		if (uploadFlag
				&& appState.trans.getTransType() == TRAN_BATCH_UPLOAD_MAG_ADVICE) {
			movGen(ISOField.F48, appState.trans.uploadRecord,
					(2 + 40 * appState.trans.uploadRecord[0]));
			return;
		} else {
			switch (appState.trans.getTransType()) {
			case TRAN_BATCH:
				/* Build the totals in TmpBuf then copy to the send buffer. */
				index = 0;

				/* CUP debit amount & count. */
				System.arraycopy(AppUtil.toCurrency(/*
													 * appState.batchInfo.
													 * getCupDebitAmount()
													 */999, false), 0, tmpBuf,
						index, 12);
				index += 12;
				System.arraycopy(StringUtil.fillZero(Integer.toString(/*
																	 * appState.
																	 * batchInfo
																	 * .
																	 * getCupDebitCount
																	 * ()
																	 */999), 3)
						.getBytes(), 0, tmpBuf, index, 3);
				index += 3;
				/* CUP credit amount & count. */
				System.arraycopy(AppUtil.toCurrency(/*
													 * appState.batchInfo.
													 * getCupCreditAmount()
													 */888, false), 0, tmpBuf,
						index, 12);
				index += 12;
				System.arraycopy(StringUtil.fillZero(Integer.toString(/*
																	 * appState.
																	 * batchInfo
																	 * .
																	 * getCupCreditCount
																	 * ()
																	 */888), 3)
						.getBytes(), 0, tmpBuf, index, 3);
				index += 3;
				index += 1;

				/* abroad debit amount & count. */
				System.arraycopy(AppUtil.toCurrency(/*
													 * appState.batchInfo.
													 * getAbrDebitAmount()
													 */777, false), 0, tmpBuf,
						index, 12);
				index += 12;
				System.arraycopy(StringUtil.fillZero(Integer.toString(/*
																	 * appState.
																	 * batchInfo
																	 * .
																	 * getAbrDebitCount
																	 * ()
																	 */777), 3)
						.getBytes(), 0, tmpBuf, index, 3);
				index += 3;
				/* abroad credit amount & count. */
				System.arraycopy(AppUtil.toCurrency(/*
													 * appState.batchInfo.
													 * getAbrCreditAmount()
													 */666, false), 0, tmpBuf,
						index, 12);
				index += 12;
				System.arraycopy(StringUtil.fillZero(Integer.toString(/*
																	 * appState.
																	 * batchInfo
																	 * .
																	 * getAbrCreditCount
																	 * ()
																	 */666), 3)
						.getBytes(), 0, tmpBuf, index, 3);
				index += 3;
				index += 1;

				F48_Additional = new byte[31];
				ByteUtil.asciiToBCD(tmpBuf, 0, F48_Additional, 0, 62, 0);

				movGen(ISOField.F48, F48_Additional, 62);
				break;
			case TRAN_BATCH_END:
				System.arraycopy(
						StringUtil.fillZero(
								Integer.toString(appState.terminalConfig
										.getUploadTotal()), 4).getBytes(), 0,
						tmpAddData, 0, 4);
				F48_Additional = new byte[2];
				ByteUtil.asciiToBCD(tmpAddData, 0, F48_Additional, 0, 4, 0);
				movGen(ISOField.F48, F48_Additional, 4);
				break;
			case TRAN_ADJUST_SALE:
			case TRAN_OFFLINE:
				if (appState.trans.getTipAmount() > 0) {
					System.arraycopy(AppUtil.toCurrency(
							appState.trans.getTipAmount(), false), 0, tmpBuf,
							0, 12);
					F48_Additional = new byte[6];
					ByteUtil.asciiToBCD(tmpBuf, 0, F48_Additional, 0, 12, 0);
					movGen(ISOField.F48, F48_Additional, 12);
				}
				break;
			case TRAN_MAG_LOAD_CASH_CHECK:
				F48_Additional = new byte[1];
				F48_Additional[0] = 0x11;
				movGen(ISOField.F48, F48_Additional, 2);
				break;
			// 持卡人验证
			case TRAN_CHECK_CARDHOLDER:
				F48_Additional = new byte[1];
				F48_Additional[0] = 0x00;
				movGen(ISOField.F48, F48_Additional, 2);
				break;
			}
		}
		return;
	}

	private static void setF60_CUP(UtilFor8583 appState) {
		int F60_Length = 0;
		// byte[] tmpBuf = new byte[9];
		byte[] tmpBuf = new byte[10];
		switch (appState.trans.getTransType()) {
		case TRAN_BALANCE:
		case TRAN_MAG_LOAD_CASH:
		case TRAN_CHECK_CARDHOLDER:
			tmpBuf[F60_Length / 2] = 0x01;
			break;
		case TRAN_BONUS_QUERY:
			tmpBuf[F60_Length / 2] = 0x03;
			break;
		case TRAN_AUTH:
		case TRAN_ADD_AUTH:
		case TRAN_AUTH_REVERSAL:
			tmpBuf[F60_Length / 2] = 0x10;
			break;
		case TRAN_AUTH_CANCEL:
		case TRAN_AUTH_CANCEL_REVERSAL:
			tmpBuf[F60_Length / 2] = 0x11;
			break;
		case TRAN_AUTH_COMPLETE:
		case TRAN_AUTH_COMPLETE_REVERSAL:
			tmpBuf[F60_Length / 2] = 0x20;
			break;
		case TRAN_AUTH_COMPLETE_CANCEL:
		case TRAN_AUTH_COMPLETE_CANCEL_REVERSAL:
			tmpBuf[F60_Length / 2] = 0x21;
			break;
		case TRAN_SALE:
		case TRAN_SALE_REVERSAL: // 冲正
		case TRAN_REVOCATION_REVERSAL: // 撤销冲正
		case TRAN_INSTALLMENT_SALE:
		case TRAN_BONUS_SALE:
		case TRAN_MOTO_SALE:
			tmpBuf[F60_Length / 2] = 0x22;
			break;
		case TRAN_VOID:
		case TRAN_VOID_SALE:
		case TRAN_INSTALLMENT_VOID:
		case TRAN_BONUS_VOID_SALE:
		case TRAN_MOTO_VOID_SALE:
			tmpBuf[F60_Length / 2] = 0x23;
			break;
		case TRAN_AUTH_SETTLEMENT:
			tmpBuf[F60_Length / 2] = 0x24;
			break;
		case TRAN_REFUND:
		case TRAN_MOTO_REFUND:
		case TRAN_BONUS_REFUND:
			tmpBuf[F60_Length / 2] = 0x25;
			break;
		case TRAN_EC_REFUND:
			tmpBuf[F60_Length / 2] = 0x27;
			break;
		case TRAN_OFFLINE:
			tmpBuf[F60_Length / 2] = 0x30;
			break;
		case TRAN_ADJUST_OFFLINE:
			tmpBuf[F60_Length / 2] = 0x32;
			break;
		case TRAN_ADJUST_SALE:
			tmpBuf[F60_Length / 2] = 0x34;
			break;
		case TRAN_EC_SALE:
			tmpBuf[F60_Length / 2] = 0x36;
			break;
		case TRAN_EC_LOAD:
			tmpBuf[F60_Length / 2] = 0x45;
			break;
		case TRAN_EC_CASH_SAVING:
			tmpBuf[F60_Length / 2] = 0x46;
			break;
		case TRAN_EC_LOAD_NOT_APPOINTED:
			tmpBuf[F60_Length / 2] = 0x47;
			break;
		case TRAN_MAG_LOAD_CASH_CON:
			tmpBuf[F60_Length / 2] = 0x48;
			break;
		case TRAN_MAG_LOAD_ACCOUNT:
			tmpBuf[F60_Length / 2] = 0x49;
			break;
		case TRAN_EC_VOID_SAVING:
			tmpBuf[F60_Length / 2] = 0x51;
			break;
		case TRAN_RESERV_VOID_SALE:
			tmpBuf[F60_Length / 2] = 0x53;
			break;
		case TRAN_RESERV_SALE:
			tmpBuf[F60_Length / 2] = 0x54;
			break;
		default:
			tmpBuf[F60_Length / 2] = 0x00;
			break;
		}
		F60_Length += 2;

		System.arraycopy(
				NumberUtil.intToBcd(appState.trans.getBatchNumber(), 3), 0,
				tmpBuf, F60_Length / 2, 3);
		F60_Length += 6;

		switch (appState.trans.getTransType()) {
		case TRAN_LOGIN:
			// 网络管理信息码
			tmpBuf[F60_Length / 2] = 0x00;
			tmpBuf[F60_Length / 2 + 1] = 0x30;
			// 批次号,默认6个0，每次签到，都会从pos中心获取最新的批次号并保存
			byte[] space6 = { '0', '0', '0', '0', '0', '0' };
			byte[] batchNumber = new byte[(space6.length + 1) / 2];
			ByteUtil.asciiToBCD(space6, 0, batchNumber, 0, space6.length, 0);
			System.arraycopy(batchNumber, 0, tmpBuf, (F60_Length / 4) / 2, 3);

			F60_Length += 3;

			break;
		case TRAN_LOGIN_BONUS:
			tmpBuf[F60_Length / 2] = 0x40;
			tmpBuf[F60_Length / 2 + 1] = 0x10;
			F60_Length += 3;
			break;
		case TRAN_LOGOUT:
			tmpBuf[F60_Length / 2] = 0x00;
			tmpBuf[F60_Length / 2 + 1] = 0x20;
			F60_Length += 3;
			break;
		case TRAN_TESTING:
			tmpBuf[F60_Length / 2] = 0x30;
			tmpBuf[F60_Length / 2 + 1] = 0x10;
			F60_Length += 3;
			break;
		case TRAN_UPSTATUS:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				tmpBuf[F60_Length / 2] = 0x37;
			} else if (appState.trans.getParamType() == PARAM_IC) {
				tmpBuf[F60_Length / 2] = 0x38;
			} else {
				tmpBuf[F60_Length / 2] = 0x36;
			}
			tmpBuf[F60_Length / 2 + 1] = 0x20;
			F60_Length += 3;
			break;
		case TRAN_DOWN_PARAM:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				tmpBuf[F60_Length / 2] = 0x37;
				if (!appState.trans.getIcParamsCapkDownloadNeed()) {
					tmpBuf[F60_Length / 2 + 1] = 0x10;
				} else {
					tmpBuf[F60_Length / 2 + 1] = 0x00;
				}
			} else if (appState.trans.getParamType() == PARAM_IC) {
				tmpBuf[F60_Length / 2] = 0x38;
				if (!appState.trans.getIcParamsCapkDownloadNeed()) {
					tmpBuf[F60_Length / 2 + 1] = 0x10;
				} else {
					tmpBuf[F60_Length / 2 + 1] = 0x00;
				}
			} else if (appState.trans.getParamType() == PARAM_BLACKLIST) {
				tmpBuf[F60_Length / 2] = 0x39;
				if (appState.trans.getParamNextFlag() == '0') {
					tmpBuf[F60_Length / 2 + 1] = 0x10;
				} else {
					tmpBuf[F60_Length / 2 + 1] = 0x00;
				}
			} else {
				tmpBuf[F60_Length / 2] = 0x36;
				tmpBuf[F60_Length / 2 + 1] = 0x00;
			}

			F60_Length += 3;
			break;
		case TRAN_DWON_CAPK_PARAM_END:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				tmpBuf[F60_Length / 2] = 0x37;
			} else if (appState.trans.getParamType() == PARAM_IC) {
				tmpBuf[F60_Length / 2] = 0x38;
			}
			tmpBuf[F60_Length / 2 + 1] = 0x10;
			F60_Length += 3;
			break;
		case TRAN_DOWN_CAPK:
			tmpBuf[F60_Length / 2] = 0x37;
			tmpBuf[F60_Length / 2 + 1] = 0x00;
			F60_Length += 3;
			break;
		case TRAN_DOWN_IC_PARAM:
			tmpBuf[F60_Length / 2] = 0x38;
			tmpBuf[F60_Length / 2 + 1] = 0x00;
			F60_Length += 3;
			break;
		case TRAN_DOWN_BLACKLIST:
			tmpBuf[F60_Length / 2] = 0x39;
			tmpBuf[F60_Length / 2 + 1] = 0x10;
			F60_Length += 3;
			break;
		case TRAN_BATCH:
		case TRAN_BATCH_UPLOAD_MAG_ADVICE:
			tmpBuf[F60_Length / 2] = 0x20;
			tmpBuf[F60_Length / 2 + 1] = 0x10;
			F60_Length += 3;
			break;
		case TRAN_BATCH_UPLOAD_PBOC_ADVICE:
		case TRAN_BATCH_UPLOAD_PBOC_OFFLINE_FAIL:
		case TRAN_BATCH_UPLOAD_PBOC_ONLINE:
		case TRAN_BATCH_UPLOAD_PBOC_RISK:
			tmpBuf[F60_Length / 2] = 0x20;
			tmpBuf[F60_Length / 2 + 1] = 0x30;
			F60_Length += 3;
			break;
		case TRAN_BATCH_END:
			if ((appState.terminalConfig.getSettleFlag() & 0x10) != 0
					&& (appState.terminalConfig.getSettleFlag() & 0x01) != 0) {
				tmpBuf[F60_Length / 2] = 0x20;
				tmpBuf[F60_Length / 2 + 1] = 0x70;
			} else {
				tmpBuf[F60_Length / 2] = 0x20;
				tmpBuf[F60_Length / 2 + 1] = 0x20;
			}
			F60_Length += 3;
			break;
		default:
			tmpBuf[F60_Length / 2] = 0x00;
			tmpBuf[F60_Length / 2 + 1] = 0x05;// TODO 终端读取能力 6.4
			F60_Length += 4;
			// 60.5 60.6

			String F60_last = "";
			int F60_last_index = F60_Length;

			tmpBuf[F60_Length / 2] = 0x00; // 60.5
			F60_Length += 1;

			if (appState.trans.getTransType() == TRAN_SALE
					|| appState.trans.getTransType() == TRAN_VOID
					|| appState.trans.getTransType() == TRAN_VOID_SALE
					|| appState.trans.getTransType() == TRAN_INSTALLMENT_SALE
					|| appState.trans.getTransType() == TRAN_INSTALLMENT_VOID
					|| appState.trans.getTransType() == TRAN_RESERV_SALE
					|| appState.trans.getTransType() == TRAN_RESERV_VOID_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_VOID_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_REFUND
					|| appState.trans.getTransType() == TRAN_BONUS_QUERY
					|| appState.trans.getTransType() == TRAN_MOTO_SALE
					|| appState.trans.getTransType() == TRAN_MOTO_VOID_SALE
					|| appState.trans.getTransType() == TRAN_MOTO_REFUND
					|| appState.trans.getTransType() == TRAN_REFUND
					|| appState.trans.getTransType() == TRAN_CHECK_CARDHOLDER
					|| appState.trans.getTransType() == TRAN_SALE_REVERSAL
					|| appState.trans.getTransType() == TRAN_REVOCATION_REVERSAL
					|| appState.trans.getTransType() == TRAN_BALANCE
					|| appState.trans.getTransType() == TRAN_AUTH
					|| appState.trans.getTransType() == TRAN_AUTH_CANCEL
					|| appState.trans.getTransType() == TRAN_AUTH_SETTLEMENT
					|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE
					|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
					|| appState.trans.getTransType() == TRAN_AUTH_REVERSAL
					|| appState.trans.getTransType() == TRAN_AUTH_CANCEL_REVERSAL
					|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_REVERSAL
					|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL_REVERSAL) {

				F60_last += "0"; // 60.5

				// 60.6
				if (!appState.paymentId.isEmpty()) {
					F60_last += appState.paymentId.substring(appState.paymentId
							.length() - 4);

				}
				F60_Length += 4;
			} else {

			}

			// 60.7
			if (appState.trans.getTransType() == TRAN_SALE
					|| appState.trans.getTransType() == TRAN_SALE_REVERSAL
					|| appState.trans.getTransType() == TRAN_REVOCATION_REVERSAL
					|| appState.trans.getTransType() == TRAN_VOID
					|| appState.trans.getTransType() == TRAN_REFUND
					|| appState.trans.getTransType() == TRAN_BALANCE) {

				F60_last += "99";
				F60_Length += 2;

				byte[] F60LastBytes = F60_last.getBytes();
				ByteUtil.asciiToBCD(F60LastBytes, 0, tmpBuf,
						F60_last_index / 2, F60LastBytes.length, 0);
			}

			if (appState.trans.getTransType() == TRAN_BONUS_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_VOID_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_REFUND
					|| appState.trans.getTransType() == TRAN_BONUS_QUERY) {
				if ((appState.trans.getTransType() == TRAN_BONUS_SALE || appState.trans
						.getTransType() == TRAN_BONUS_VOID_SALE)
						&& appState.trans.getBonusType() == 1) {
					tmpBuf[F60_Length / 2] = 0x04;
					tmpBuf[F60_Length / 2 + 1] = (byte) 0x80;
				} else {
					tmpBuf[F60_Length / 2] = 0x06;
					tmpBuf[F60_Length / 2 + 1] = (byte) 0x50;
				}

				F60_Length += 3;
			}
			break;
		}
		movGen(ISOField.F60, tmpBuf, F60_Length);
		return;
	}

	private static void setF61_CUP(UtilFor8583 appState) {
		int F61_Length = 0;
		byte[] tmpBuf = new byte[20];
		// 61.1
		/* Check if Original Batch no. has data */
		if (appState.oldTrans.getOldBatch() >= 0) {
			/* Yes; data in field exists */
			/* Moves the original batch number into SENDBUF */
			System.arraycopy(
					NumberUtil.intToBcd(appState.oldTrans.getOldBatch(), 3), 0,
					tmpBuf, F61_Length / 2, 3);
		}
		F61_Length += 6;

		// 61.2
		// 撤销
		if (appState.oldTrans.getOldTrace() >= 0) {
			/* Yes; data in field exists */
			/* Moves the original batch number into SENDBUF */
			System.arraycopy(
					NumberUtil.intToBcd(appState.oldTrans.getOldTrace(), 3), 0,
					tmpBuf, F61_Length / 2, 3);
		}
		F61_Length += 6;

		// 61.3
		if (appState.trans.getTransType() == TRAN_REFUND
				|| appState.trans.getTransType() == TRAN_BONUS_REFUND
				|| appState.trans.getTransType() == TRAN_VOID
				|| appState.trans.getTransType() == TRAN_MOTO_REFUND
				|| appState.trans.getTransType() == TRAN_ADD_AUTH
				|| appState.trans.getTransType() == TRAN_AUTH_CANCEL
				|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE
				|| appState.trans.getTransType() == TRAN_AUTH_SETTLEMENT
				|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
				|| appState.trans.getTransType() == TRAN_OFFLINE
				|| appState.trans.getTransType() == TRAN_ADJUST_OFFLINE
				|| appState.trans.getTransType() == TRAN_SALE_REVERSAL // 冲正
				|| appState.trans.getTransType() == TRAN_REVOCATION_REVERSAL // 撤销冲正
				|| appState.trans.getTransType() == TRAN_ADJUST_SALE
				|| appState.trans.getTransType() == TRAN_AUTH_REVERSAL
				|| appState.trans.getTransType() == TRAN_AUTH_CANCEL_REVERSAL
				|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_REVERSAL
				|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL_REVERSAL) {
			if (appState.oldTrans.getOldTransDate().length() > 0) {
				/* Yes; data in field exists */
				/* Moves the original batch number into SENDBUF */
				System.arraycopy(StringUtil.hexString2bytes(appState.oldTrans
						.getOldTransDate()), 0, tmpBuf, F61_Length / 2, 2);
			}
			F61_Length += 4;
		}

		if (appState.trans.getTransType() == TRAN_OFFLINE
				|| appState.trans.getTransType() == TRAN_ADJUST_OFFLINE
				|| appState.trans.getTransType() == TRAN_ADJUST_SALE) {
			// 61.4
			if (appState.trans.getTransType() == TRAN_ADJUST_SALE) {
				tmpBuf[F61_Length / 2] = 0x00;
			} else {
				if (appState.trans.getAuthType() > 0) {
					tmpBuf[F61_Length / 2] = (byte) (appState.trans
							.getAuthType() - 1);
				}
			}
			F61_Length += 2;

			// 61.5
			if (appState.trans.getAuthBankCode().length() == 11) {
				System.arraycopy(
						StringUtil.hexString2bytes(appState.trans
								.getAuthBankCode() + "0"), 0, tmpBuf,
						F61_Length / 2, 6);
				F61_Length += 11;
			}
		}
		movGen(ISOField.F61, tmpBuf, F61_Length);
		return;
	}

	private static void setF62_CUP(UtilFor8583 appState) {
		byte[] tmpBuf = null;
		String tempString = "";
		switch (appState.trans.getTransType()) {
		case TRAN_INSTALLMENT_SALE:
			tmpBuf = new byte[62];
			byte[] installment = NumberUtil.intToBcd(
					appState.trans.getInstallment(), 1);
			tmpBuf[0] = installment[0];
			System.arraycopy(
					StringUtil.fillZero(
							Integer.toString(appState.trans.getInstallment()),
							2).getBytes(), 0, tmpBuf, 0, 2);
			System.arraycopy(
					StringUtil.fillSpace(appState.trans.getGoodsCode(), 30)
							.getBytes(), 0, tmpBuf, 2, 30);
			tmpBuf[32] = '1';
			tmpBuf[33] = appState.trans.getFeePayType();
			System.arraycopy("                            ".getBytes(), 0,
					tmpBuf, 34, 28);
			movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			break;
		case TRAN_BONUS_SALE:
			tmpBuf = new byte[30];
			System.arraycopy(
					StringUtil.fillSpace(appState.trans.getGoodsCode(), 30)
							.getBytes(), 0, tmpBuf, 0, 30);
			movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			break;
		case TRAN_RESERV_SALE:
			tempString = "90"
					+ StringUtil.fillSpace(
							StringUtil.fillSpace(
									appState.trans.getMobileNumber(), 11)
									+ appState.trans.getRSVNumber(), 20);
			movGen(ISOField.F62, tempString.getBytes(), tempString.length());
			break;
		case TRAN_MOTO_SALE:
		case TRAN_MOTO_AUTH_COMP:
		case TRAN_MOTO_AUTH:
			tempString = "92CV003"
					+ appState.trans.getCVN()
					+ "SF006"
					+ appState.trans.getIDNumber()
					+ "TX"
					+ StringUtil.fillZero(Integer.toString(appState.trans
							.getMobileNumber().length()), 3)
					+ appState.trans.getMobileNumber()
					+ "NM"
					+ StringUtil.fillZero(Integer.toString(StringUtil
							.length(appState.trans.getCardholderName())), 3)
					+ appState.trans.getCardholderName();
			try {
				tmpBuf = tempString.getBytes("GB2312");
			} catch (UnsupportedEncodingException e) {
			}
			movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			break;
		case TRAN_CHECK_CARDHOLDER:
			tempString = "92SF006"
					+ appState.trans.getIDNumber()
					+ "TX"
					+ StringUtil.fillZero(Integer.toString(appState.trans
							.getMobileNumber().length()), 3)
					+ appState.trans.getMobileNumber()
					+ "NM"
					+ StringUtil.fillZero(Integer.toString(StringUtil
							.length(appState.trans.getCardholderName())), 3)
					+ appState.trans.getCardholderName();
			try {
				tmpBuf = tempString.getBytes("GB2312");
			} catch (UnsupportedEncodingException e) {
			}
			movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			break;
		case TRAN_LOGIN:
			tempString = "Sequence No" + 20 + "1201" + TSN;
			movGen(ISOField.F62, tempString.getBytes(), tempString.length());
			break;
		case TRAN_UPSTATUS:
			if (appState.trans.getParamType() == PARAM_CAPK
					|| appState.trans.getParamType() == PARAM_IC) {
				tmpBuf = new byte[3];
				tmpBuf[0] = 0x31;
				System.arraycopy(
						StringUtil
								.fillZero(
										Integer.toString(appState.trans
												.getParamCount()), 2)
								.getBytes(), 0, tmpBuf, 1, 2);
				movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			} else {
				tmpBuf = new byte[126];
				// 1-5
				System.arraycopy("011021031041051116012".getBytes(), 0, tmpBuf,
						0, 21);
				// 11- 12
				tmpBuf[21] = (byte) ((byte) (appState.terminalConfig
						.getCommTimeout() / 10) + 0x30);
				tmpBuf[22] = (byte) ((byte) (appState.terminalConfig
						.getCommTimeout() % 10) + 0x30);
				// 13
				System.arraycopy("13".getBytes(), 0, tmpBuf, 23, 2);
				tmpBuf[25] = 0x33;
				// 14
				System.arraycopy("14".getBytes(), 0, tmpBuf, 26, 2);
				System.arraycopy("              ".getBytes(), 0, tmpBuf, 28, 14);
				// 15
				System.arraycopy("15".getBytes(), 0, tmpBuf, 42, 2);
				System.arraycopy("              ".getBytes(), 0, tmpBuf, 44, 14);
				// 16
				System.arraycopy("16".getBytes(), 0, tmpBuf, 58, 2);
				System.arraycopy("              ".getBytes(), 0, tmpBuf, 60, 14);
				// 17
				System.arraycopy("17".getBytes(), 0, tmpBuf, 74, 2);
				System.arraycopy("              ".getBytes(), 0, tmpBuf, 76, 14);
				// 18
				System.arraycopy("18".getBytes(), 0, tmpBuf, 90, 2);
				tmpBuf[99] = (byte) (appState.terminalConfig.getTipSwitch() + 0x30);
				// 19
				System.arraycopy("19".getBytes(), 0, tmpBuf, 93, 2);
				tmpBuf[95] = (byte) ((byte) (appState.terminalConfig
						.getTipPercent() / 10) + 0x30);
				tmpBuf[96] = (byte) ((byte) (appState.terminalConfig
						.getTipPercent() % 10) + 0x30);
				// 20
				System.arraycopy("20".getBytes(), 0, tmpBuf, 97, 2);
				// tmpBuf[99] = (byte)(appState.terminalConfig.getManualSwitch()
				// + 0x30);
				// 21
				System.arraycopy("21".getBytes(), 0, tmpBuf, 100, 2);
				tmpBuf[102] = (byte) (appState.terminalConfig
						.getAutoLogoffSwitch() + 0x30);
				// 23
				System.arraycopy("23".getBytes(), 0, tmpBuf, 103, 2);
				tmpBuf[105] = 0x33;
				// 24
				System.arraycopy("24".getBytes(), 0, tmpBuf, 106, 2);
				// tmpBuf[108] = (byte)(appState.terminalConfig.getUploadType()
				// + 0x30);
				// 25
				System.arraycopy("25".getBytes(), 0, tmpBuf, 109, 2);
				tmpBuf[111] = (byte) (Byte.parseByte(appState.terminalConfig
						.getKeyIndex()) + 0x30);
				// 51
				System.arraycopy("51".getBytes(), 0, tmpBuf, 112, 2);
				System.arraycopy("0011".getBytes(), 0, tmpBuf, 114, 4);
				System.arraycopy("00111".getBytes(), 0, tmpBuf, 118, 5);
				System.arraycopy("098".getBytes(), 0, tmpBuf, 123, 3);
				movGen(ISOField.F62, tmpBuf, 126);
			}
			break;
		case TRAN_DOWN_PARAM:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				if (appState.trans.getParamOffset() < appState.trans
						.getParamDataLength()) {
					tmpBuf = new byte[12];
					System.arraycopy(appState.trans.getParamData(),
							appState.trans.getParamOffset(), tmpBuf, 0, 12);
					appState.trans.setParamOffset(appState.trans
							.getParamOffset() + 23);
					movGen(ISOField.F62, tmpBuf, tmpBuf.length);
					if (appState.trans.getParamOffset() >= appState.trans
							.getParamDataLength()) {
						appState.trans.setIcParamsCapkDownloadNeed(false);
						appState.trans.setParamCount(0);
					} else {
						appState.trans.setIcParamsCapkDownloadNeed(true);
					}
				}
			} else if (appState.trans.getParamType() == PARAM_IC) {
				if (appState.trans.getParamOffset() < appState.trans
						.getParamDataLength()) {
					int dataLength = appState.trans.getParamData()[appState.trans
							.getParamOffset() + 2];
					tmpBuf = new byte[3 + dataLength];
					System.arraycopy(appState.trans.getParamData(),
							appState.trans.getParamOffset(), tmpBuf, 0,
							tmpBuf.length);
					appState.trans.setParamOffset(appState.trans
							.getParamOffset() + tmpBuf.length);
					movGen(ISOField.F62, tmpBuf, tmpBuf.length);
					if (appState.trans.getParamOffset() >= appState.trans
							.getParamDataLength()) {
						appState.trans.setIcParamsCapkDownloadNeed(false);
						appState.trans.setParamCount(0);
					} else {
						appState.trans.setIcParamsCapkDownloadNeed(true);
					}
				}
			} else if (appState.trans.getParamType() == PARAM_BLACKLIST) {
				tmpBuf = StringUtil.fillZero(
						Integer.toString(appState.trans.getParamCount()), 3)
						.getBytes();
				movGen(ISOField.F62, tmpBuf, tmpBuf.length);
			}
			break;
		case TRAN_EC_REFUND:
			movGen(ISOField.F62, appState.oldTrans.getOldTID().getBytes(), 8);
			break;
		}

	}

	private static void setF63_CUP(UtilFor8583 appState) {
		byte[] F63_Field;
		switch (appState.trans.getTransType()) {
		case TRAN_LOGIN:
		case TRAN_BATCH:
			F63_Field = new byte[3];
			System.arraycopy(appState.getCurrentOperatorID().getBytes(), 0,
					F63_Field, 0, 2);
			F63_Field[2] = ' ';
			break;
		case TRAN_OFFLINE:
		case TRAN_ADJUST:
		case TRAN_ADJUST_SALE:
		case TRAN_ADJUST_OFFLINE:
			F63_Field = appState.trans.getCardOrganization().getBytes();
			break;
		case TRAN_REFUND:
		case TRAN_BONUS_REFUND:
		case TRAN_MOTO_REFUND:
			F63_Field = "000".getBytes();
			break;
		default:
			return;
		}
		if (F63_Field != null && F63_Field.length > 0) {
			movGen(ISOField.F63, F63_Field, F63_Field.length);
		}
		return;
	}

	private static void procB48_CUP(byte[] F48_Additional, UtilFor8583 appState) {
		byte[] tmpBuf = new byte[F48_Additional.length * 2];
		/* Any Bit 48 Data to process */
		if (TRAN_BATCH != appState.trans.getTransType()) {
			return;
		}

		ByteUtil.bcdToAscii(F48_Additional, 0, tmpBuf, 0, tmpBuf.length, 0);

		appState.terminalConfig.setSettleFlag((byte) 0);

		// 由于批上送有问题，强制设成结算平
		tmpBuf[30] = 0x31;
		if (tmpBuf[30] == 0x31) {
			appState.trans.setResponseCode(new byte[] { '0', '0' });
			appState.terminalConfig
					.setSettleFlag((byte) (appState.terminalConfig
							.getSettleFlag() | 0x10));
		} else {
			if (tmpBuf[30] == 0x32 || tmpBuf[30] == 0x33) {
				appState.trans.setResponseCode(new byte[] { 'U', 'P' });
				if (tmpBuf[30] == 0x32) {
					appState.terminalConfig
							.setSettleFlag((byte) (appState.terminalConfig
									.getSettleFlag() | 0x20));
				} else {
					appState.terminalConfig
							.setSettleFlag((byte) (appState.terminalConfig
									.getSettleFlag() | 0x40));
				}
			} else {
				appState.trans.setResponseCode(new byte[] { 'U', 'E' });
				appState.terminalConfig
						.setSettleFlag((byte) (appState.terminalConfig
								.getSettleFlag() | 0x80));
			}
		}
		// 由于批上送有问题，强制设成结算平
		tmpBuf[61] = 0x31;
		if (tmpBuf[61] == 0x31) {
			appState.terminalConfig
					.setSettleFlag((byte) (appState.terminalConfig
							.getSettleFlag() | 0x01));
			return;
		} else {
			if (tmpBuf[61] == 0x32 || tmpBuf[61] == 0x33) {
				appState.trans.setResponseCode(new byte[] { 'U', 'P' });
				if (tmpBuf[61] == 0x32) {
					appState.terminalConfig
							.setSettleFlag((byte) (appState.terminalConfig
									.getSettleFlag() | 0x02));
				} else {
					appState.terminalConfig
							.setSettleFlag((byte) (appState.terminalConfig
									.getSettleFlag() | 0x04));
				}
			} else {
				appState.trans.setResponseCode(new byte[] { 'U', 'E' });
				appState.terminalConfig
						.setSettleFlag((byte) (appState.terminalConfig
								.getSettleFlag() | 0x08));
			}
		}
	}

	private static void procB55_CUP(byte[] F55_Field, UtilFor8583 appState) {
		appState.trans.setIssuerAuthData(F55_Field, 0, F55_Field.length);
		int ker_ret = -1;
		int c72_len = 0;
		int c91_len = 0;
		int cAuthCode_len = 0;
		byte[] cAuthCode = null;
		String resCode = "";
		EMVICManager emvICManager = EMVICManager.getEMVICManagerInstance();
		int trade_ret = -1;

		if (UtilFor8583.getInstance().trans.getEntryMode() == ConstantUtils.ENTRY_IC_MODE) {
			if (Arrays
					.equals(appState.trans.getResponseCode(), "98".getBytes())
					|| Arrays.equals(appState.trans.getResponseCode(),
							"68".getBytes())) {

			} else {
				c9f36 = null;
				c91 = null;
				c71 = null;
				c72 = null;
				convertF55(F55_Field);
				if (c72 != null) {
					c72_len = c72.length;
				}
				if (c91 != null) {
					c91_len = c91.length;
				}
				if (appState.trans.getAuthCode() == null
						|| appState.trans.getAuthCode().equals("")) {
					cAuthCode = null;
					cAuthCode_len = 0;
				}
				Log.i("ISOPackager",
						"===,Response Code:"
								+ appState.trans.getResponseCode()[0] + " "
								+ appState.trans.getResponseCode()[1]);
				if (c72 == null) {
					Log.i("ISOPackager", "===,c72:null,c72_len:0");
				} else {
					String str = "";
					for (int i = 0; i < c72_len; i++) {
						str += String.format("%02X ", c72[i]);
					}
					Log.i("ISOPackager", "===,c72:" + str);
				}
				if (c91 == null) {
					Log.i("ISOPackager", "===,c91:null,c91_len:0");
				} else {
					String str = "";
					for (int i = 0; i < c91_len; i++) {
						str += String.format("%02X ", c91[i]);
					}
					Log.i("ISOPackager", "===,c91:" + str + ",c91_len:"
							+ c91_len);
				}
				ker_ret = EmvL2Interface.recvOnlineMessage(c72,
						(char) (c72_len), c91, (char) (c91_len), cAuthCode,
						(char) (cAuthCode_len),
						appState.trans.getResponseCode(),
						(char) (appState.trans.getResponseCode().length));
				Log.i("ISOPackager", "===,recvOnlineMessage,ker_ret:" + ker_ret);
				appState.trans.setIcTransferMsgResult(ker_ret);
			}
		}
	}

	private static void convertF55(byte[] F55_Field) {
		int offset = 0;

		if (F55_Field[0] == t9f36[0]) {
			if (F55_Field[1] == t9f36[1]) {
				offset += 2 + 1;
				c9f36 = new byte[F55_Field[2]];
				System.arraycopy(F55_Field, offset, c9f36, 0, c9f36.length);
				offset += F55_Field[2];
				if (offset < F55_Field.length) {
					byte[] temp = new byte[F55_Field.length - offset];
					System.arraycopy(F55_Field, offset, temp, 0, temp.length);
					convertF55(temp);
				}
			}
		} else if (F55_Field[0] == t91[0]) {
			offset += 1 + 1;
			c91 = new byte[F55_Field[1]];
			System.arraycopy(F55_Field, offset, c91, 0, c91.length);
			offset += F55_Field[1];
			if (offset < F55_Field.length) {
				byte[] temp = new byte[F55_Field.length - offset];
				System.arraycopy(F55_Field, offset, temp, 0, temp.length);
				convertF55(temp);
			}
		} else if (F55_Field[0] == t71[0]) {
			offset += 1 + 1;
			offset += F55_Field[1];
			c71 = new byte[offset];
			System.arraycopy(F55_Field, 0, c71, 0, c71.length);
			if (offset < F55_Field.length) {
				byte[] temp = new byte[F55_Field.length - offset];
				System.arraycopy(F55_Field, offset, temp, 0, temp.length);
				convertF55(temp);
			}
		} else if (F55_Field[0] == t72[0]) {
			offset += 1 + 1;
			offset += F55_Field[1];
			c72 = new byte[offset];
			System.arraycopy(F55_Field, 0, c72, 0, c72.length);
			if (offset < F55_Field.length) {
				byte[] temp = new byte[F55_Field.length - offset];
				System.arraycopy(F55_Field, offset, temp, 0, temp.length);
				convertF55(temp);
			}
		}
	}

	private static void procB60_CUP(byte[] F60_Field, UtilFor8583 appState) {
		if (TRAN_LOGIN == appState.trans.getTransType()) {
			String str = "";
			for (int i = 0; i < 3; i++) {
				str += String.format("%02x", F60_Field[i]);
			}
			Log.i("ISOPackager", "===,procB60_CUP,Str=" + str);
			byte[] batchNumber = new byte[3];
			System.arraycopy(F60_Field, 1, batchNumber, 0, 3);
			str = "";
			for (int i = 0; i < 3; i++) {
				str += String.format("%02x", batchNumber[i]);
			}
			Log.i("ISOPackager", "===,procB60_CUP,batchNumber=" + str);
			Log.i("ISOPackager",
					"===,procB60_CUP,batchNumber="
							+ ByteUtil.bcdToInt(batchNumber));
			if (batchNumber != null) {
				appState.trans.setBatchNumber(ByteUtil.bcdToInt(batchNumber));
			}
			Log.d("DEBUG", "60.2 批次号 = " + appState.trans.getBatchNumber());
		}
	}

	private static void getCAPKParamInfo(UtilFor8583 appState, byte[] data) {
		byte[] capk = new byte[data.length - 1];
		System.arraycopy(data, 1, capk, 0, capk.length);

		EmvL2Interface.downloadParam(capk, capk.length, 1);
	}

	private static void getBlackList(UtilFor8583 appState, byte[] data) {
		appState.trans.setParamNextFlag(data[0]);
		byte[] length = new byte[3];
		System.arraycopy(data, 1, length, 0, 3);
		appState.trans
				.setParamCount(NumberUtil.parseInt(length, 0, 10, false) + 1);
		// insert blacklist
	}

	private static void getEMVParamInfo(UtilFor8583 appState, byte[] data) {
		byte[] aParams = new byte[data.length - 1];
		System.arraycopy(data, 1, aParams, 0, aParams.length);

		EmvL2Interface.downloadParam(aParams, aParams.length, 0);

	}

	private static void procB62_CUP(byte[] F62_Field, UtilFor8583 appState) {
		// System.arraycopy(F62_Field, 0, F62_Field, 1, F62_Field.length);
		// removeArrayItem(F62_Field);
		// byte [] temp_F62 = java.util.Arrays.copyOf(F62_Field,
		// F62_Field.length - 1);
		// byte [] temp_F62 = new byte[F62_Field.length -1];
		// System.arraycopy(F62_Field, 1 , temp_F62 , 0 , temp_F62.length);
		// F62_Field = temp_F62 ;

		byte[] tmp = null;
		switch (appState.trans.getTransType()) {
		case TRAN_BONUS_SALE:
			tmp = new byte[10];
			System.arraycopy(F62_Field, 0, tmp, 0, 10);
			appState.trans.setBonus(NumberUtil.parseInt(tmp, 0, 10, false));
			tmp = new byte[12];
			System.arraycopy(F62_Field, 10, tmp, 0, 12);
			appState.trans.setSelfPayAmount(NumberUtil.parseInt(tmp, 0, 10,
					false));
			break;
		case TRAN_INSTALLMENT_SALE:
			tmp = new byte[12];
			System.arraycopy(F62_Field, 0, tmp, 0, 12);
			appState.trans.setFirstTermAmount(NumberUtil.parseInt(tmp, 0, 10,
					false));
			if (appState.trans.getFeePayType() == '0') {
				tmp = new byte[12];
				System.arraycopy(F62_Field, 15, tmp, 0, 12);
				appState.trans.setFee(NumberUtil.parseInt(tmp, 0, 10, false));
			} else {
				tmp = new byte[12];
				System.arraycopy(F62_Field, 40, tmp, 0, 12);
				appState.trans.setFirstTermFee(NumberUtil.parseInt(tmp, 0, 10,
						false));

				tmp = new byte[12];
				System.arraycopy(F62_Field, 52, tmp, 0, 12);
				appState.trans.setPerTermFee(NumberUtil.parseInt(tmp, 0, 10,
						false));
			}
		case TRAN_LOGIN:
			appState.PIK = new byte[16];
			appState.PIKCheck = new byte[4];
			appState.MAK = new byte[8];
			appState.MAKCheck = new byte[4];
			appState.TDK = new byte[16];
			appState.TDKCheck = new byte[4];

			System.arraycopy(F62_Field, 44, appState.PIK, 0, 16);
			System.arraycopy(F62_Field, 60, appState.PIKCheck, 0, 4);
			System.arraycopy(F62_Field, 64, appState.MAK, 0, 8);
			System.arraycopy(F62_Field, 80, appState.MAKCheck, 0, 4);
			System.arraycopy(F62_Field, 84, appState.TDK, 0, 16);
			System.arraycopy(F62_Field, 100, appState.TDKCheck, 0, 4);

			// if(debug){
			// temp = "";
			// for(byte b : appState.PIK){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.PIK = " + temp);
			//
			// temp = "";
			// for(byte b : appState.PIKCheck){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.PIKCheck = " + temp);
			//
			// temp = "";
			// for(byte b : appState.MAK){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.MAK = " + temp);
			//
			// temp = "";
			// for(byte b : appState.MAKCheck){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.MAKCheck = " + temp);
			//
			// temp = "";
			// for(byte b : appState.TDK){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.TDK = " + temp);
			//
			// temp = "";
			// for(byte b : appState.TDKCheck){
			// temp += String.format("%02X ", b);
			// }
			// Log.d(APP_TAG,"appState.TDKCheck = " + temp);
			// }
			break;
		case TRAN_UPSTATUS:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				appState.trans.setParamNextFlag(F62_Field[0]);
				if (F62_Field[0] != '0' && F62_Field.length > 1) {
					appState.trans.addParamData(F62_Field, 1,
							F62_Field.length - 1);
				}
				appState.trans.setParamCount(appState.trans
						.getParamDataLength() / 23);
				if (F62_Field[0] != '3') {
					appState.trans.setIcParamsCapkCheckNeed(true);
				} else {
					appState.trans.setIcParamsCapkCheckNeed(false);
					appState.trans.setIcParamsCapkDownloadNeed(true);
					appState.trans.setParamCount(0);
				}
			} else if (appState.trans.getParamType() == PARAM_IC) {
				appState.trans.setParamNextFlag(F62_Field[0]);
				if (F62_Field[0] != '0' && F62_Field.length > 1) {
					int offset = 1;
					while (offset < F62_Field.length) {
						appState.trans.setParamCount(appState.trans
								.getParamCount() + 1);
						offset += (3 + F62_Field[offset + 2]);
					}
					appState.trans.addParamData(F62_Field, 1,
							F62_Field.length - 1);
				}
				if (F62_Field[0] != '3') {
					appState.trans.setIcParamsCapkCheckNeed(true);
				} else {
					appState.trans.setIcParamsCapkCheckNeed(false);
					appState.trans.setIcParamsCapkDownloadNeed(true);
					appState.trans.setParamCount(0);
				}
			}
			break;
		case TRAN_DOWN_PARAM:
			if (appState.trans.getParamType() == PARAM_CAPK) {
				getCAPKParamInfo(appState, F62_Field);
				break;
			} else if (appState.trans.getParamType() == PARAM_IC) {
				getEMVParamInfo(appState, F62_Field);
				break;
			} else if (appState.trans.getParamType() == PARAM_BLACKLIST) {
				getBlackList(appState, F62_Field);
			} else {
				int tempFldLen = F62_Field.length;
				int offset = 0;
				int paramNum;
				byte[] tmpCode = new byte[2];
				byte[] tmpByte2 = new byte[2];
				while (tempFldLen > 0) {
					System.arraycopy(F62_Field, offset, tmpCode, 0, 2);
					/* Convert number entered */
					paramNum = Integer.parseInt(StringUtil.toString(tmpCode));
					offset += 2;
					tempFldLen -= 2;
					switch (paramNum) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						offset++;
						tempFldLen -= 1;
						break;
					case 11:
						offset += 2;
						tempFldLen -= 2;
						break;
					case 12:
						offset += 2;
						tempFldLen -= 2;
						break;
					case 13:
						offset++;
						tempFldLen -= 1;
						break;
					case 14:
					case 15:
					case 16:
						offset += 14;
						tempFldLen -= 14;
						break;
					case 17:
						// for( i = 0; i < 14; i++ )
						// {
						// if( F62_Field[offset + i] == 0x20
						// || F62_Field[offset + i] == 0x00
						// )
						// {
						// break;
						// }
						// }
						// if(i > 0)
						// {
						// byte[] tmpTel = new byte[i];
						// System.arraycopy(F62_Field,offset,tmpTel,0,i);
						// appState.terminalConfig.setManagePhone(StringUtil.toString(tmpTel));
						// }
						offset += 14;
						tempFldLen -= 14;
						break;
					case 18:
						appState.terminalConfig
								.setTipSwitch((byte) (F62_Field[offset] - 0x30));
						offset++;
						tempFldLen -= 1;
						break;
					case 19:
						System.arraycopy(F62_Field, offset, tmpByte2, 0, 2);
						appState.terminalConfig.setTipPercent(Integer
								.parseInt(StringUtil.trimSpace(new String(
										tmpByte2), 0)));
						offset += 2;
						tempFldLen -= 2;
						break;
					case 20:
						// appState.terminalConfig.setManualSwitch((byte)(F62_Field[offset]
						// - 0x30));
						offset++;
						tempFldLen -= 1;
						break;
					case 21:
						appState.terminalConfig
								.setAutoLogoffSwitch((byte) (F62_Field[offset] - 0x30));
						offset++;
						tempFldLen -= 1;
						break;
					case 22:
						byte[] merchantName = new byte[40];
						System.arraycopy(F62_Field, offset, merchantName, 0, 40);
						try {
							appState.terminalConfig.setMerchantName(StringUtil
									.trimSpace(new String(merchantName,
											"GB2312")));
						} catch (UnsupportedEncodingException e) {

						}
						offset += 40;
						tempFldLen -= 40;
						break;
					case 23:
						offset++;
						tempFldLen -= 1;
						break;
					case 24:
						// appState.terminalConfig.setUploadType((byte)(F62_Field[offset]
						// - 0x30));
						offset++;
						tempFldLen -= 1;
						break;
					case 25:
						// appState.terminalConfig.setKeyIndex(Byte.toString((byte)(F62_Field[offset]
						// - 0x30)));
						offset++;
						tempFldLen -= 1;
						break;
					case 26:
						byte[] tranSwitch = new byte[4];
						System.arraycopy(F62_Field, offset, tranSwitch, 0, 4);
						if ((tranSwitch[0] & 0x80) != 0) {
							appState.terminalConfig.setBalanceSwitch((byte) 1);
						} else {
							appState.terminalConfig.setBalanceSwitch((byte) 0);
						}
						if ((tranSwitch[0] & 0x40) != 0) {
							appState.terminalConfig.setAuthSwitch((byte) 1);
						} else {
							appState.terminalConfig.setAuthSwitch((byte) 0);
						}
						if ((tranSwitch[0] & 0x20) != 0) {
							appState.terminalConfig.setCancelSwitch((byte) 1);
						} else {
							appState.terminalConfig.setCancelSwitch((byte) 0);
						}
						if ((tranSwitch[0] & 0x10) != 0) {
							appState.terminalConfig.setAuthCompSwitch((byte) 1);
						} else {
							appState.terminalConfig.setAuthCompSwitch((byte) 0);
						}
						if ((tranSwitch[0] & 0x08) != 0) {
							appState.terminalConfig.setVoidCompSwitch((byte) 1);
						} else {
							appState.terminalConfig.setVoidCompSwitch((byte) 0);
						}

						if ((tranSwitch[0] & 0x02) != 0) {
							appState.terminalConfig.setVoidSaleSwitch((byte) 1);
						} else {
							appState.terminalConfig.setVoidSaleSwitch((byte) 0);
						}
						if ((tranSwitch[0] & 0x01) != 0) {
							appState.terminalConfig.setRefundSwitch((byte) 1);
						} else {
							appState.terminalConfig.setRefundSwitch((byte) 0);
						}
						if ((tranSwitch[1] & 0x80) != 0) {
							appState.terminalConfig.setOfflineSwitch((byte) 1);
						} else {
							appState.terminalConfig.setOfflineSwitch((byte) 0);
						}
						// if((tranSwitch[1] & 0x40) != 0)
						// {
						// appState.terminalConfig.setAdjustSwitch(true);
						// }
						// else{
						// appState.terminalConfig.setAdjustSwitch(false);
						// }
						if ((tranSwitch[1] & 0x20) != 0) {
							appState.terminalConfig
									.setAuthSettleSwitch((byte) 1);
						} else {
							appState.terminalConfig
									.setAuthSettleSwitch((byte) 0);
						}
						if ((tranSwitch[1] & 0x10) != 0) {
							// IC card
						} else {
							// IC card
						}
						if ((tranSwitch[1] & 0x08) != 0) {
							// offline sale
						} else {
							// offline sale
						}

						offset += 4;
						tempFldLen -= 4;
						break;
					case 51:
						offset += 12;
						tempFldLen -= 12;
						break;
					default:
						tempFldLen = 0;
						break;
					} // switch*/
				} // while
			}
		default:
			break;
		}
	}

	private static void procB63_CUP(byte[] F63_Field, UtilFor8583 appState) {
		if (TRAN_LOGIN != appState.trans.getTransType()) {
			int offset = 0;
			byte[] cardOrg = new byte[3];
			System.arraycopy(F63_Field, 0, cardOrg, 0, 3);
			appState.trans.setCardOrganization(StringUtil.toString(cardOrg));

			offset += 3;
			int textLength = F63_Field.length - offset;
			if (textLength > 0) {
				if (textLength > 20) {
					byte[] hostText = new byte[20];
					System.arraycopy(F63_Field, offset, hostText, 0, 20);
					try {
						appState.trans.setHostText1(new String(hostText,
								"GB2312"));
					} catch (UnsupportedEncodingException e) {
					}
					offset += 20;
					textLength -= 20;
					if (textLength > 0) {
						if (textLength > 20) {
							System.arraycopy(F63_Field, offset, hostText, 0, 20);
							try {
								appState.trans.setHostText2(new String(
										hostText, "GB2312"));
							} catch (UnsupportedEncodingException e) {
							}
							offset += 20;
							textLength -= 20;
							if (textLength > 0) {
								if (textLength >= 20) {
									System.arraycopy(F63_Field, offset,
											hostText, 0, 20);
									try {
										appState.trans.setHostText3(new String(
												hostText, "GB2312"));
									} catch (UnsupportedEncodingException e) {
									}
								} else {
									byte[] hostText3 = new byte[textLength];
									System.arraycopy(F63_Field, offset,
											hostText3, 0, textLength);
									try {
										appState.trans.setHostText3(new String(
												hostText3, "GB2312"));
									} catch (UnsupportedEncodingException e) {
									}
								}
							}
						} else {
							byte[] hostText2 = new byte[textLength];
							System.arraycopy(F63_Field, offset, hostText2, 0,
									textLength);
							try {
								appState.trans.setHostText2(new String(
										hostText2, "GB2312"));
							} catch (UnsupportedEncodingException e) {
							}
						}
					}
				} else {
					byte[] hostText1 = new byte[textLength];
					System.arraycopy(F63_Field, offset, hostText1, 0,
							textLength);
					try {
						appState.trans.setHostText1(new String(hostText1,
								"GB2312"));
					} catch (UnsupportedEncodingException e) {
					}
				}
			}
		}
	}

	// public static byte[] encryptTrackData(UtilFor8583 appState, byte[] data)
	// {
	// byte[] out = new byte[8];
	// if(appState.pinpadOpened == false)
	// {
	// return null;
	// }
	// int nResult =
	// PinPadInterface.updateUserKey(Integer.parseInt(appState.terminalConfig.getKeyIndex()),
	// 1,
	// StringUtil.hexString2bytes(appState.terminalConfig.getTDK()),
	// 16);
	// if(nResult < 0)
	// {
	// return null;
	// }
	// nResult = PinPadInterface.setKey(2,
	// Integer.parseInt(appState.terminalConfig.getKeyIndex()), 1, DOUBLE_KEY);
	// if(nResult < 0)
	// {
	// return null;
	// }
	// nResult = PinPadInterface.encrypt(data, 8, out);
	// if(nResult < 0)
	// {
	// return null;
	// }
	// return out;
	// }

	public static int pack(boolean uploadFlag, UtilFor8583 appState) {
		return pack(uploadFlag, appState, null);
	}

	public static int pack(boolean uploadFlag, UtilFor8583 appState,
			int[] defaultBitMap) {

		iso.clearBitFlag();

		if (uploadFlag) {
			F_MessageType[0] = 0x03;
			F_MessageType[1] = 0x20;
		} else {
			// 获取msg type
			Log.d(APP_TAG,
					"appState.trans.getTransType() = "
							+ appState.trans.getTransType());
			ByteUtil.asciiToBCD(
					MessageType.getReqMsgType(appState.trans.getTransType()),
					0, F_MessageType, 0, 4, 0);
		}
		/*
		 * 设置消息类型
		 */
		movGen(ISOField.F00_MSGID, F_MessageType, 0);

		byte[] F_Bitmap = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		/*
		 * 设置位图
		 */
		movGen(ISOField.F01_BITMAP, F_Bitmap, 0);

		appState.trans.setMacFlag(false);

		int[] bitMap = defaultBitMap != null ? defaultBitMap
				: ISOField.bitMap[appState.trans.getTransType()];

		for (int i = 0; i < bitMap.length; i++) {
			if (debug) {
				Log.d(APP_TAG, "pack field " + bitMap[i]);
			}

			switch (bitMap[i]) {
			case ISOField.F02_PAN:
				if (appState.trans.getPAN().length() > 0
				// && appState.trans.getEntryMode() == SWIPE_ENTRY // 读取磁条卡号
				) {
					byte[] F2_AccountNumber = new byte[(appState.trans.getPAN()
							.length() + 1) / 2];
					ByteUtil.asciiToBCD(appState.trans.getPAN().getBytes(), 0,
							F2_AccountNumber, 0, appState.trans.getPAN()
									.length(), 0);
					movGen(ISOField.F02_PAN, F2_AccountNumber, appState.trans
							.getPAN().length());
				}
				break;
			case ISOField.F03_PROC:
				F3_ProcessingCode = new byte[3];
				if (null != appState.oldTrans
						&& appState.oldTrans.getOldProcesscode().equals(
								"200000")
						&& TRAN_SALE_REVERSAL == appState.trans.getTransType()) {
					ByteUtil.asciiToBCD(
							MessageType.getProcessingCode(Constant.TRAN_VOID),
							0, F3_ProcessingCode, 0, 6, 0);
				} else if (null != appState.oldTrans
						&& (TRAN_AUTH_REVERSAL == appState.trans.getTransType()
								|| TRAN_AUTH_CANCEL_REVERSAL == appState.trans
										.getTransType()
								|| TRAN_AUTH_COMPLETE_REVERSAL == appState.trans
										.getTransType() || TRAN_AUTH_COMPLETE_CANCEL_REVERSAL == appState.trans
								.getTransType())) {
					ByteUtil.asciiToBCD(
							MessageType.getProcessingCode(appState.oldTrans
									.getTransType()), 0, F3_ProcessingCode, 0,
							6, 0);
				} else {

					ByteUtil.asciiToBCD(MessageType
							.getProcessingCode(appState.trans.getTransType()),
							0, F3_ProcessingCode, 0, 6, 0);
				}
				// ByteUtil.asciiToBCD(MessageType.getProcessingCode(appState.trans.getTransType()),
				// 0,F3_ProcessingCode, 0, 6, 0);
				movGen(ISOField.F03_PROC, F3_ProcessingCode, 0);
				break;
			case ISOField.F04_AMOUNT:
				byte[] F4_Amount = new byte[6];
				ByteUtil.asciiToBCD((AppUtil.toCurrency(
						appState.trans.getTransAmount(), false)), 0, F4_Amount,
						0, 12, 0);
				movGen(ISOField.F04_AMOUNT, F4_Amount, 0);
				break;
			case ISOField.F11_STAN:
				if (appState.trans.getTransType() == TRAN_SALE_REVERSAL
						|| appState.trans.getTransType() == TRAN_REVOCATION_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_CANCEL_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL_REVERSAL) {
					movGen(ISOField.F11_STAN, NumberUtil.intToBcd(
							appState.oldTrans.getOldTrace(), 3), 0);
				} else {
					movGen(ISOField.F11_STAN,
							NumberUtil.intToBcd(appState.trans.getTrace(), 3),
							0);
				}

				break;
			case ISOField.F12_TIME:
				if (appState.trans.getTransTime().length() == 6) {
					byte[] F12_Time = new byte[3];
					ByteUtil.asciiToBCD(appState.trans.getTransTime()
							.getBytes(), 0, F12_Time, 0, 6, 0);
					movGen(ISOField.F12_TIME, F12_Time, 0);
				}
				break;
			case ISOField.F13_DATE:
				if (appState.trans.getTransDate().length() == 8) {
					byte[] F13_Date = new byte[2];
					ByteUtil.asciiToBCD(appState.trans.getTransDate()
							.getBytes(), 0, F13_Date, 0, 4, 0);
					movGen(ISOField.F13_DATE, F13_Date, 0);
				}
				break;
			case ISOField.F14_EXP:
				if (// appState.trans.getEntryMode() != SWIPE_ENTRY&&
				appState.trans.getExpiry().length() == 4) {
					byte[] F14_Expiry = new byte[2];
					ByteUtil.asciiToBCD(appState.trans.getExpiry().getBytes(),
							0, F14_Expiry, 0, 4, 0);
					movGen(ISOField.F14_EXP, F14_Expiry, 0);
				}
				break;
			case ISOField.F22_POSE:
				byte[] F22_Pose = new byte[2];

				if (appState.trans.getTransType() == TRAN_RESERV_SALE
						|| appState.trans.getTransType() == TRAN_RESERV_VOID_SALE) {
					F22_Pose[0] = (byte) 0x92;
				} else {
					F22_Pose[0] = appState.trans.getEntryMode();
				}
				F22_Pose[1] = appState.trans.getPinMode();
				movGen(ISOField.F22_POSE, F22_Pose, 0);
				break;
			case ISOField.F23:
				// if (appState.trans.getCSN() > 0) {
				// byte[] F23_CSN = new byte[2];
				// F23_CSN[1] = appState.trans.getCSN();
				// movGen(ISOField.F23, F23_CSN, 0);
				// }
				EMVICData mEMVICData = EMVICData.getEMVICInstance();
				String csn = mEMVICData.getCardSeqNo();
				// byte[] b_csn = csn.getBytes();
				if (!csn.equals("") && csn != null) {
					byte[] F23_CSN = new byte[2];
					// ByteUtil.ASCII_To_BCD(b_csn, b_csn.length);
					ByteUtil.asciiToBCD(csn.getBytes(), 0, F23_CSN, 0, 3, 1);
					movGen(ISOField.F23, F23_CSN, 0);
				}
				break;
			case ISOField.F25_POCC:
				byte[] F25_POCC = new byte[1];
				switch (appState.trans.getTransType()) {
				case TRAN_AUTH:
				case TRAN_AUTH_CANCEL:
				case TRAN_AUTH_COMPLETE:
				case TRAN_AUTH_COMPLETE_CANCEL:
				case TRAN_AUTH_SETTLEMENT:
					F25_POCC[0] = 0x06;
					break;
				case TRAN_ADD_AUTH:
					F25_POCC[0] = 0x60;
					break;
				case TRAN_INSTALLMENT_SALE:
				case TRAN_INSTALLMENT_VOID:
					F25_POCC[0] = 0x64;
					break;
				case TRAN_BONUS_QUERY:
				case TRAN_BONUS_SALE:
				case TRAN_BONUS_VOID_SALE:
					F25_POCC[0] = 0x65;
					break;
				case TRAN_RESERV_SALE:
				case TRAN_RESERV_VOID_SALE:
					F25_POCC[0] = 0x67;
					break;
				case TRAN_MOTO_AUTH:
				case TRAN_MOTO_AUTH_COMP:
				case TRAN_MOTO_AUTH_SETTLE:
				case TRAN_MOTO_CANCEL:
				case TRAN_MOTO_VOID_COMP:
					F25_POCC[0] = 0x18;
					break;
				case TRAN_MOTO_SALE:
				case TRAN_MOTO_VOID_SALE:
				case TRAN_MOTO_REFUND:
					F25_POCC[0] = 0x08;
					break;
				case TRAN_EC_CASH_SAVING:
				case TRAN_EC_LOAD:
				case TRAN_EC_LOAD_NOT_APPOINTED:
					F25_POCC[0] = (byte) 0x91;
					break;
				case TRAN_MAG_LOAD_ACCOUNT:
					F25_POCC[0] = (byte) 0x66;
					break;
				default:
					if (null != appState.oldTrans) {
						if (TRAN_AUTH_REVERSAL == appState.trans.getTransType()
								|| TRAN_AUTH_CANCEL_REVERSAL == appState.trans
										.getTransType()
								|| TRAN_AUTH_COMPLETE_REVERSAL == appState.trans
										.getTransType()
								|| TRAN_AUTH_COMPLETE_CANCEL_REVERSAL == appState.trans
										.getTransType()) {
							F25_POCC[0] = 0x06;
							break;
						} else if (TRAN_SALE_REVERSAL == appState.trans
								.getTransType()) {
							F25_POCC[0] = 0x00;
							break;
						}

					} else {
						F25_POCC[0] = 0x00;
						break;
					}
				}
				movGen(ISOField.F25_POCC, F25_POCC, 0);
				break;
			case ISOField.F26_CAPTURE:
				if (appState.trans.getPinBlock() != null
				/* && appState.getProcessType() != PROCESS_REVERSAL */
				) {
					byte[] F26_Capture = new byte[1];
					F26_Capture[0] = 0x12;
					movGen(ISOField.F26_CAPTURE, F26_Capture, 0);
				}
				break;
			case ISOField.F35_TRACK2:
				if (appState.trans.getTrack2Data() != null
						&& appState.trans.getTrack2Data().length() > 0
				/*
				 * && ( appState.getProcessType() == PROCESS_NORMAL || (
				 * appState.getProcessType() == PROCESS_BATCH && (
				 * appState.trans.getTransType() == TRAN_REFUND ||
				 * appState.trans.getTransType() == TRAN_AUTH_SETTLEMENT ) ) )
				 */
				) {
					byte[] track2Data = new byte[(appState.trans
							.getTrack2Data().length() + 1) / 2];
					ByteUtil.asciiToBCD(appState.trans.getTrack2Data()
							.getBytes(), 0, track2Data, 0, appState.trans
							.getTrack2Data().length(), 0);
					// TODO 对数据加密
					// byte[] tdb = new byte[8];
					// System.arraycopy(track2Data, track2Data.length - 9, tdb,
					// 0, 8);
					// byte[] encryptData = encryptTrackData(appState, tdb);
					// 加密
					// if(encryptData != null)
					// {
					// System.arraycopy(encryptData, 0, track2Data,
					// track2Data.length - 9, 8);
					// }
					movGen(ISOField.F35_TRACK2, track2Data, appState.trans
							.getTrack2Data().length());
				}
				break;
			case ISOField.F36_TRACK3:
				if (appState.trans.getTrack3Data() != null
						&& appState.trans.getTrack3Data().length() > 0
				//
				// && (
				// ( /*
				// * appState.getProcessType() == PROCESS_NORMAL &&
				// */appState.trans.getEntryMode() == SWIPE_ENTRY
				// ) ||
				// ( /*
				// * appState
				// * .
				// * getProcessType
				// * (
				// * )
				// * ==
				// * PROCESS_BATCH
				// * &&
				// */
				// (appState.trans.getTransType() == TRAN_REFUND ||
				// appState.trans
				// .getTransType() == TRAN_AUTH_SETTLEMENT)
				// )
				// )
				) {
					byte[] track3Data = new byte[(appState.trans
							.getTrack3Data().length() + 1) / 2];
					ByteUtil.asciiToBCD(appState.trans.getTrack3Data()
							.getBytes(), 0, track3Data, 0, appState.trans
							.getTrack3Data().length(), 0);
					// byte[] tdb = new byte[8];
					// System.arraycopy(track3Data, track3Data.length - 9, tdb,
					// 0, 8);
					// byte[] encryptData = encryptTrackData(appState, tdb);
					// if(encryptData != null)
					// {
					// System.arraycopy(encryptData, 0, track3Data,
					// track3Data.length - 9, 8);
					// }
					movGen(ISOField.F36_TRACK3, track3Data, appState.trans
							.getTrack3Data().length());
				}
				break;
			case ISOField.F37_RRN:
				if (appState.trans.getRRN().length() > 0) {
					movGen(ISOField.F37_RRN,
							appState.trans.getRRN().getBytes(), 0);
				}
				break;
			case ISOField.F38_AUTH:
				if ((appState.oldTrans.getReversalReason() & 0xf0) != 0) {
					if (appState.trans.getTransType() == TRAN_AUTH_COMPLETE
							|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
							|| appState.trans.getTransType() == TRAN_AUTH_SETTLEMENT
							|| appState.trans.getTransType() == TRAN_AUTH_CANCEL) {
						movGen(ISOField.F38_AUTH, appState.oldTrans
								.getOldAuthCode().getBytes(), 0);
					} else {
						if (appState.trans.getAuthCode().length() > 0) {
							movGen(ISOField.F38_AUTH, appState.trans
									.getAuthCode().getBytes(), 0);
						} else if (appState.oldTrans.getOldAuthCode().length() > 0) {
							movGen(ISOField.F38_AUTH, appState.oldTrans
									.getOldAuthCode().getBytes(), 0);
						}
					}
				} else {
					if (appState.trans.getTransType() == TRAN_OFFLINE
							&& appState.trans.getAuthType() == 3) {
						break;
					}
					if (appState.trans.getTransType() == TRAN_AUTH_COMPLETE
							|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
							|| appState.trans.getTransType() == TRAN_AUTH_SETTLEMENT
							|| appState.trans.getTransType() == TRAN_AUTH_CANCEL
							|| appState.trans.getTransType() == TRAN_AUTH_REVERSAL
							|| appState.trans.getTransType() == TRAN_AUTH_CANCEL_REVERSAL
							|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_REVERSAL
							|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL_REVERSAL) {
						if (appState.oldTrans.getOldAuthCode().length() > 0) {
							movGen(ISOField.F38_AUTH, appState.oldTrans
									.getOldAuthCode().getBytes(), 0);
						}
					} else if (appState.trans.getAuthCode().length() > 0) {
						movGen(ISOField.F38_AUTH, appState.trans.getAuthCode()
								.getBytes(), 0);
					}
				}
				break;
			case ISOField.F39_RSP:
				if (appState.trans.getTransType() == TRAN_SALE_REVERSAL
						|| appState.trans.getTransType() == TRAN_REVOCATION_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_CANCEL_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_REVERSAL
						|| appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL_REVERSAL) {
					appState.trans.setResponseCode(new byte[] { '9', '8' });
					appState.oldTrans
							.setOldResponseCode(new byte[] { '9', '8' });
					// 暂时不用
					// if ((appState.oldTrans.getReversalReason() & 0x20) ==
					// 0x20) {
					// appState.trans.setResponseCode(new byte[] { 'A', '0' });
					// } else if ((appState.oldTrans.getReversalReason() & 0x10)
					// == 0x10) {
					// appState.trans.setResponseCode(new byte[] { '0', '6' });
					// } else {
					// appState.trans.setResponseCode(new byte[] { '9', '8' });
					// }
					movGen(ISOField.F39_RSP, appState.trans.getResponseCode(),
							0);
				}
				break;
			case ISOField.F40:
				setF40_CUP(appState);
				break;
			case ISOField.F41_TID:
				if (appState.terminalConfig.getTID().length() > 0) {
					movGen(ISOField.F41_TID, appState.terminalConfig.getTID()
							.getBytes(), 0);
				}
				break;
			case ISOField.F42_ACCID:
				if (appState.terminalConfig.getMID().length() > 0) {
					movGen(ISOField.F42_ACCID, appState.terminalConfig.getMID()
							.getBytes(), 0);
				}
				break;
			case ISOField.F48:
				setF48_CUP(appState, uploadFlag);
				break;
			case ISOField.F49_CURRENCY:
				movGen(ISOField.F49_CURRENCY, appState.trans.getTransCurrency()
						.getBytes(), 0);
				/*
				 * if (appState.trans.getTransType() == TRAN_SALE_9140) {
				 * movGen(ISOField.F49_CURRENCY, new byte[] { '8', '1', '0'
				 * },0); } else { movGen(ISOField.F49_CURRENCY, new byte[] {
				 * '1', '5', '6' },0); }
				 */
				break;
			case ISOField.F52_PIN:
				if (appState.trans.getPinBlock() != null
						&& appState.trans.getPinBlock().length == 8
				/* && appState.getProcessType() != PROCESS_REVERSAL */
				) {
					movGen(ISOField.F52_PIN, appState.trans.getPinBlock(), 0);
				}
				break;
			case ISOField.F53_SCI:
				if (((appState.trans.getPinBlock() != null && appState.trans
						.getPinBlock().length == 8) || appState.trans
						.getTrack2Data() != null)
				/* && appState.getProcessType() != PROCESS_REVERSAL */
				) {
					byte[] F53_SCI = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
							0x00, 0x00 };
					if (appState.trans.getPinBlock() == null) {
						F53_SCI[0] = 0x26; // 不符合规范
					} else {
						if (appState.trans.getPAN().length() > 0) {
							F53_SCI[0] = 0x26;
						} else {
							F53_SCI[0] = 0x16;
						}
					}
					if (appState.trans.getTrack2Data() == null) {
						F53_SCI[1] = 0x00;
					} else {
						F53_SCI[1] = 0x00;

					}
					movGen(ISOField.F53_SCI, F53_SCI, 0);
				}
				break;
			case ISOField.F54_TIP:
				byte[] F54_TIP = AppUtil.toCurrency(
						appState.trans.getTipAmount(), false);
				movGen(ISOField.F54_TIP, F54_TIP, F54_TIP.length);
				break;
			case ISOField.F55_ICC:
				if (appState.trans.getICCRevData() != null) {
					movGen(ISOField.F55_ICC, appState.trans.getICCRevData(),
							appState.trans.getICCRevData().length);
				} else if (appState.trans.getICCData() != null
						&& appState.trans.getICCData().length() > 0) {
					movGen(ISOField.F55_ICC,
							StringUtil.hexString2bytes(appState.trans
									.getICCData()), appState.trans.getICCData()
									.length() / 2);
				}
				break;
			case ISOField.F60:
				setF60_CUP(appState);
				break;
			case ISOField.F61:
				setF61_CUP(appState);
				break;
			case ISOField.F62:
				setF62_CUP(appState);
				break;
			case ISOField.F63:
				setF63_CUP(appState);
				break;
			case ISOField.F64_MAC:
				if (!uploadFlag) {
					appState.trans.setMacFlag(true);
				}
				break;
			}
			Log.d(APP_TAG, "++++++++++++++++++++++++");// TODO

		}
		iso.SetBitmap();
		int iso8583Len = ISO8583.GetDataLength(iso);
		sendData = new byte[11 + iso8583Len];
		// TPDU
		System.arraycopy(
				StringUtil.hexString2bytes(appState.terminalConfig.getTPDU()),
				0, sendData, 0, 5);
		// APDU
		// 报文头信息
		if (appState.trans.getEntryMode() == ConstantUtils.ENTRY_IC_MODE) {
			sendData[5] = 0x61;// 应用类别定义
		} else {
			sendData[5] = 0x60;// 应用类别定义
		}
		sendData[6] = 0x31;// 终端遵循的规范
		sendData[7] = 0x00;// 终端状态/处理要求
		sendData[8] = 0x31;// 业务编码
		System.arraycopy(StringUtil.hexString2bytes(APP_VERSION.substring(3)),
				0, sendData, 9, 2);
		// Data
		ISO8583.GetDataBuffer(sendData, (short) 11, iso);
		if (appState.trans.getMacFlag() == true) {
			sendData[20] |= 0x01;
		}
		sendDataLength = 11 + iso8583Len;

		return sendDataLength;
	}

	public static boolean unpack(byte[] databuf, UtilFor8583 appState) {

		Log.d(APP_TAG,
				"appState.trans.getTransType() = "
						+ appState.trans.getTransType());
		ByteUtil.asciiToBCD(
				MessageType.getReqMsgType(appState.trans.getTransType()), 0,
				F_MessageType, 0, 4, 0);
		F3_ProcessingCode = new byte[3];
		ByteUtil.asciiToBCD(
				MessageType.getProcessingCode(appState.trans.getTransType()),
				0, F3_ProcessingCode, 0, 6, 0);
		// 233 251 ;248
		int i, j, n;
		byte bitmask;
		int offset = 0;
		boolean ret = true;

		appState.trans.setResponseCode(new byte[] { 'F', 'F' });

		appState.trans.setMacFlag(false);

		// TPDU
		offset += 5;
		// APDU
		byte[] temp = new byte[1];
		System.arraycopy(databuf, offset + 2, temp, 0, 1);
		byte[] pos_Status_Require = ByteUtil.bcdToAscii(temp, 0, 1);
		if (pos_Status_Require[1] == 9 + 48) {
			appState.trans.setParamDownloadFlag(true);
			ClientEngine.engineInstance().deleteParamsFiles();
		} else {
			appState.trans.setParamDownloadFlag(false);
		}
		if (appState.trans.getTransType() == TRAN_LOGIN) {
			if (!ClientEngine.engineInstance().paramsFilesIsExists()) {
				appState.trans.setParamDownloadFlag(true);
			}
		}
		// for test
		// appState.trans.setParamDownloadFlag(true);
		offset += 6;

		iso.setOffset((short) 0);
		iso.setDataBuffer(databuf, offset, databuf.length - offset);

		if (databuf[offset] != F_MessageType[0]
				|| databuf[offset + 1] != (F_MessageType[1] + 0x10)) {

			if (debug) {
				Log.d(APP_TAG, "MessageType Error");
				Log.d(APP_TAG,
						"F_MessageType[0] = "
								+ String.format("%02X ", F_MessageType[0]));
				Log.d(APP_TAG,
						"F_MessageType[1] = "
								+ String.format("%02X ", F_MessageType[1]));
				Log.d(APP_TAG,
						"databuf[offset] = "
								+ String.format("%02X ", databuf[offset]));
				Log.d(APP_TAG,
						"databuf[offset+1] = "
								+ String.format("%02X ", databuf[offset + 1]));
			}
			return false;
		}
		offset += 2;

		byte[] F_Bitmap = new byte[8];
		System.arraycopy(databuf, offset, F_Bitmap, 0, 8);
		offset += 8;

		/* copy dataBuffer elements to iso */
		iso.setOffset((short) 10); // MsgID + Bitmap
		for (i = 0; i < 8; i++) {
			bitmask = (byte) 0x80;
			for (j = 0; j < 8; j++, bitmask = (byte) ((bitmask & 0xFF) >>> 1)) {
				if (i == 0 && j == 0) {
					continue; // Jumped over the expansion sign of bitmap
				}
				/* Check bitmap flag */
				if ((F_Bitmap[i] & bitmask) == 0) {
					continue;
				}
				/* Count isotable[] index */
				n = (i << 3) + j + 1;
				try {
					Log.d(APP_TAG, "Count isotable[] index n = " + n);
					ret = saveData(n, appState);
					if (ret == false) {
						Log.d("8583", "saveData[" + n + "] fail");
						return ret;
					}
				} catch (Exception e) {
					Log.d("8583", "saveData[" + n + "]Exception");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public static int movGen(int bit, byte[] pData, int length) {

		ISOTable[] isotable;
		byte[] llvar = new byte[1];
		byte[] lllvar = new byte[2];

		if (iso.isotable != null) {
			isotable = iso.isotable;
		} else {
			isotable = CUPPack.isotable;
		}

		if (bit < 0 || bit > 65) {
			return -1;
		}
		if (bit >= 2) {
			iso.bitFlag[bit - 2] = 1; // 不太明白为什么要减2.
		}

		// Switch on the message format
		switch (isotable[bit].fieldType) {
		case FFIX + ATTN: // Fixed Numeric
			// Make length even and divide by 2
			length = (isotable[bit].fieldLen + 1) / 2;
			break;
		case FFIX + ATTBIN: // Fixed Binary
			// Divide by 8
			length = isotable[bit].fieldLen / 8;// TODO
			if (bit == 52) {
				length = isotable[bit].fieldLen;
			}
			break;
		case FFIX + ATTAN: // Fixed Alpha Numeric
		case FFIX + ATTANS: // Fixed Alpha Numeric Special
			length = isotable[bit].fieldLen;
			break;
		case FLLVAR + ATTANS: // LL Variable Alpha Numeric Special
			llvar[0] = (byte) ((length % 10) + (length / 10) * 16);
			iso.setDataBuffer(llvar, 0, 1);
			break;

		case FLLLVAR + ATTAN: // LLL Variable Alpha Numeric
		case FLLLVAR + ATTANS: // LLL Variable Alpha Numeric Special
			// Convert length to BCD (2 bytes)
			lllvar[0] = (byte) (length / 100);
			lllvar[1] = (byte) (((length % 100) / 10) * 16 + (length % 100) % 10);
			iso.setDataBuffer(lllvar, 0, 2);
			break;
		case FLLVAR + ATTN: // LL Variable Numeric
			// Move BCD length (1 byte)
			llvar[0] = (byte) ((length % 10) + (length / 10) * 16);
			iso.setDataBuffer(llvar, 0, 1);
			length = (length + 1) / 2;
			break;
		case FLLLVAR + ATTN: /* LLL Variable Numeric */
			// Convert length to BCD (2 bytes)
			lllvar[0] = (byte) (length / 100);
			lllvar[1] = (byte) (((length % 100) / 10) * 16 + (length % 100) % 10);
			iso.setDataBuffer(lllvar, 0, 2);
			length = (length + 1) / 2;
			break;
		default: // Unknown format; no data to move
			length = 0;
			break;
		}
		// Safety validation on length
		if (length != 0) {
			// Move the Data to the Send Buffer
			if (debug) {
				String temp = "";
				for (byte b : pData) {
					temp += String.format("%02X ", b);
				}
				Log.d(APP_TAG, temp + ", length = " + length);
			}
			iso.setDataBuffer(pData, 0, length);
		}
		return 0;
	}

	private static boolean saveData(int bit, UtilFor8583 appState) {
		int length;
		boolean ret = true;
		ISOTable[] isotable;
		byte[] llvar = new byte[1];
		byte[] lllvar = new byte[2];

		if (iso.isotable != null) {
			isotable = iso.isotable;
		} else {
			isotable = CUPPack.isotable;
		}

		switch (isotable[bit].fieldType) {
		case FFIX + ATTN: // Fixed Numeric
			// Make length even and divide by 2
			length = (isotable[bit].fieldLen + 1) / 2;
			break;
		case FFIX + ATTBIN: // Fixed Binary
			// Divide by 8
			length = isotable[bit].fieldLen;
			break;
		case FFIX + ATTAN: // Fixed Alpha Numeric
		case FFIX + ATTANS: // Fixed Alpha Numeric Special
			length = isotable[bit].fieldLen;
			break;
		case FLLVAR + ATTANS: // LL Variable Alpha Numeric Special
			iso.fetchDataBuffer(llvar, 0, 1);
			length = ((llvar[0] >> 4) & 0x0F) * 10 + (llvar[0] & 0x0F);
			break;
		case FLLLVAR + ATTAN: // LLL Variable Alpha Numeric
		case FLLLVAR + ATTANS: // LLL Variable Alpha Numeric Special
			// Convert length to BCD (2 bytes)
			iso.fetchDataBuffer(lllvar, 0, 2);
			length = lllvar[0] & 0x0F;
			length = length * 100 + ((lllvar[1] >> 4) & 0x0F) * 10
					+ (lllvar[1] & 0x0F);
			break;
		case FLLVAR + ATTN: // LL Variable Numeric
			iso.fetchDataBuffer(llvar, 0, 1);
			length = ((llvar[0] >> 4) & 0x0F) * 10 + (llvar[0] & 0x0F);
			length = (length + 1) / 2;
			break;
		case FLLLVAR + ATTN: /* LLL Variable Numeric */
			iso.fetchDataBuffer(lllvar, 0, 2);
			length = lllvar[0] & 0x0F;
			length = length * 100 + ((lllvar[1] >> 4) & 0x0F) * 10
					+ (lllvar[1] & 0x0F);
			length = (length + 1) / 2;
			break;
		default: // Unknown format; no data to move
			length = 0;
			break;
		}
		byte[] tempBuffer = new byte[length];
		iso.fetchDataBuffer(tempBuffer, 0, tempBuffer.length);
		switch (bit) {
		case ISOField.F02_PAN:
			byte[] F2_AccountNumber = new byte[tempBuffer.length * 2];
			ByteUtil.bcdToAscii(tempBuffer, 0, F2_AccountNumber, 0, 20, 0);
			if (appState.trans.getTransType() == TRAN_RESERV_SALE) {
				int panLength = ((llvar[0] >> 4) & 0x0F) * 10
						+ (llvar[0] & 0x0F);
				// appState.trans.setPAN(StringUtil.toString(F2_AccountNumber)
				// .substring(0, panLength));
				appState.trans.setPAN(StringUtil.toString(F2_AccountNumber));
			} else {
				// if( appState.getProcessType() != PROCESS_REVERSAL
				// && appState.trans.getTransType() != TRAN_VOID_SALE
				// && appState.trans.getTransType() != TRAN_VOID_COMPLETE
				// && appState.trans.getTransType() != TRAN_RESERV_VOID_SALE
				// && appState.trans.getTransType() != TRAN_BONUS_VOID_SALE
				// && appState.trans.getTransType() != TRAN_MOTO_VOID_SALE
				// && appState.trans.getTransType() != TRAN_MOTO_VOID_COMP
				// && appState.trans.getTransType() != TRAN_INSTALLMENT_VOID
				// )
				// {
				// int cardLength = appState.trans.getPAN().length();
				// if(ByteUtil.compareByteArray(F2_AccountNumber,0,cardLength,
				// appState.trans.getPAN().getBytes(),0,cardLength) != 0)
				// {
				// Log.d("8583","Card number is not same");
				// return false;
				// }
				// }
			}
			break;
		case ISOField.F03_PROC:
			if (ByteUtil.compareByteArray(F3_ProcessingCode, 0,
					F3_ProcessingCode.length, tempBuffer, 0, tempBuffer.length) != 0) {
				Log.d("8583", "Processcode is not same");
				return false;
			}
			break;
		case ISOField.F04_AMOUNT:
			byte[] F4_Amount = new byte[6];
			ByteUtil.asciiToBCD(
					(AppUtil.toCurrency(appState.trans.getTransAmount(), false)),
					0, F4_Amount, 0, 12, 0);
			if (ByteUtil.compareByteArray(F4_Amount, 0, F4_Amount.length,
					tempBuffer, 0, tempBuffer.length) != 0) {
				Log.d("8583", "Amount is not same");
				return false;
			}
			break;
		case ISOField.F11_STAN:
			if (ByteUtil.bcdToInt(tempBuffer) != appState.trans.getTrace()) {
				Log.d("8583", "F11_STAN = " + ByteUtil.bcdToInt(tempBuffer));
				Log.d("8583",
						"appState.trans.getTrace() ="
								+ appState.trans.getTrace());
				Log.d("8583", "Stan is not same");
				// return false;
			}
			break;
		case ISOField.F12_TIME:
			appState.trans.setTransTime(StringUtil.toString(ByteUtil
					.bcdToAscii(tempBuffer)));
			break;
		case ISOField.F13_DATE:
			appState.trans.setTransDate(StringUtil.toString(ByteUtil
					.bcdToAscii(tempBuffer)));
			break;
		case ISOField.F14_EXP:
			appState.trans.setExpiry(StringUtil.toString(ByteUtil
					.bcdToAscii(tempBuffer)));
			break;
		case ISOField.F15_SETTLE_DATE:
			break;
		case ISOField.F22_POSE:
			break;
		case ISOField.F24_NII:
			break;
		case ISOField.F25_POCC:
			break;
		case ISOField.F26_CAPTURE:
			break;
		case ISOField.F32_ACQUIRER:
			appState.trans.setAcquirerCode(StringUtil.toString(tempBuffer));
			break;
		case ISOField.F37_RRN:
			appState.trans.setRRN(StringUtil.toString(tempBuffer));
			break;
		case ISOField.F38_AUTH:
			/*
			 * if (appState.trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL) {
			 * appState.oldTrans.setOldAuthCode(appState.trans.getAuthCode());
			 * appState.trans.setAuthCode(StringUtil.toString(tempBuffer)); }
			 * else { if (appState.trans.getAuthCode().length() > 0) {
			 * appState.oldTrans.setOldAuthCode(StringUtil
			 * .toString(tempBuffer)); } else {
			 * appState.trans.setAuthCode(StringUtil.toString(tempBuffer)); } }
			 */

			appState.trans.setAuthCode(StringUtil.toString(tempBuffer));
			break;
		case ISOField.F39_RSP:
			appState.trans.setResponseCode(tempBuffer);
			break;
		case ISOField.F40:
			getF40_CUP(tempBuffer, appState);
			break;
		case ISOField.F41_TID:
			byte[] F41_TID = appState.terminalConfig.getTID().getBytes();
			if (ByteUtil.compareByteArray(F41_TID, 0, F41_TID.length,
					tempBuffer, 0, tempBuffer.length) != 0) {
				Log.d("8583", "TID is : " + appState.terminalConfig.getTID());
				Log.d("8583",
						"TID is not same:" + StringUtil.toString(tempBuffer));
				return false;
			}
			break;
		case ISOField.F42_ACCID:
			byte[] F42_ACCID = appState.terminalConfig.getMID().getBytes();
			if (ByteUtil.compareByteArray(F42_ACCID, 0, F42_ACCID.length,
					tempBuffer, 0, tempBuffer.length) != 0) {
				Log.d("8583",
						"MID is not same" + appState.terminalConfig.getMID());
				return false;
			}
			break;
		case ISOField.F44_ADDITIONAL:
			byte[] issID = new byte[8];
			byte[] acqID = new byte[8];
			if (tempBuffer.length <= 11) {
				System.arraycopy(tempBuffer, 0, issID, 0, 8);
				appState.trans.setIssuerID(StringUtil.toString(issID));
				appState.trans.setIssuerName();
			} else if (tempBuffer.length >= 22) {
				System.arraycopy(tempBuffer, 11, acqID, 0, 8);
				System.arraycopy(tempBuffer, 0, issID, 0, 8);
				appState.trans.setIssuerID(StringUtil.toString(issID));
				appState.trans.setAcquirerID(StringUtil.toString(acqID));
				appState.trans.setIssuerName();
			}
			break;
		case ISOField.F48:
			procB48_CUP(tempBuffer, appState);
			break;
		case ISOField.F49_CURRENCY:
			break;
		case ISOField.F53_SCI:
			break;
		case ISOField.F54_TIP:
			byte[] bal = new byte[12];
			System.arraycopy(tempBuffer, 8, bal, 0, 12);
			Log.d(APP_TAG, StringUtil.toString(bal));
			long lbal = Long.parseLong(StringUtil.toString(bal));
			if (appState.trans.getTransType() == TRAN_BALANCE) {
				if (tempBuffer[7] == 'D') {
					appState.trans.setBalance((-1) * lbal);
				} else {
					appState.trans.setBalance(lbal);
				}
			} else if (appState.trans.getTransType() == TRAN_BONUS_SALE
					|| appState.trans.getTransType() == TRAN_BONUS_QUERY) {
				appState.trans.setBonusBalance(lbal);
			}
			break;
		case ISOField.F55_ICC:
			procB55_CUP(tempBuffer, appState);
			break;
		case ISOField.F60:
			procB60_CUP(tempBuffer, appState);
			break;
		case ISOField.F61:
			break;
		case ISOField.F62:
			procB62_CUP(tempBuffer, appState);
			break;
		case ISOField.F63:
			procB63_CUP(tempBuffer, appState);
			break;
		case ISOField.F64_MAC:
			appState.trans.setMacFlag(true);
			appState.trans.setMac(tempBuffer);
			break;
		}
		return ret;
	}

	// 通联订单号 F40 6F10
	// 批次号 F40 6F08
	// F40_CUP is :
	// 6f101100182013121711280264886f021100006f08110020201312171128020704016f111100006f1

	private static void getF40_CUP(byte[] F40_Field, UtilFor8583 appState) {

		String temp_F40 = StringUtil.toString(F40_Field).toUpperCase(
				Locale.getDefault());
		Log.d("8583", "F40_CUP is : " + temp_F40);
		String[] strs = temp_F40.split("6F");
		String payOrderBatch = "";
		String apOrderId = "";
		String sale_F40_Type = "";
		String openBrh = "";
		String cardId = "";
		String alipayPId = "";
		String alipayAccount = "";
		String alipayTransactionID = "";

		for (String s : strs) {
			if (s.length() < 2) {
				continue;
			}
			String flag = s.substring(0, 2);
			if (flag.equals("08")) {
				payOrderBatch = s.substring(8);
				continue;
			} else if (flag.equals("10")) {
				apOrderId = s.substring(8);
				continue;
			} else if (flag.equals("13")) {
				sale_F40_Type = s.substring(8);
				continue;
			} else if (flag.equals("20")) {
				openBrh = s.substring(8);
				continue;
			} else if (flag.equals("21")) {
				cardId = s.substring(8);
				continue;
			} else if (flag.equals("22")) {
				alipayPId = s.substring(8);
				continue;
			} else if (flag.equals("26")) {
				alipayAccount = s.substring(8);
				continue;
			} else if (flag.equals("27")) {
				alipayTransactionID = s.substring(8);
				continue;
			}
		}
		appState.payOrderBatch = payOrderBatch;
		appState.apOrderId = apOrderId;
		appState.sale_F40_Type = sale_F40_Type;
		appState.openBrh = openBrh;
		appState.cardId = cardId;
		appState.alipayPID = alipayPId;
		appState.alipayAccount = alipayAccount;
		appState.alipayTransactionID = alipayTransactionID;
	}
}