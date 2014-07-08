package cn.koolcloud.iso8583;

import android.util.Log;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.util.StringUtil;

public class MessageType implements Constant {
	public static MessageTypeTable[] messageTypeTable = {
			new MessageTypeTable(TRAN_BALANCE, "0200", "310000"), // 0
			new MessageTypeTable(TRAN_SALE, "0200", "000000"), // 1
			new MessageTypeTable(TRAN_VOID, "0200", "200000"), // 2
			new MessageTypeTable(TRAN_REFUND, "0220", "200000"), // 3
			new MessageTypeTable(TRAN_AUTH, "0100", "030000"), // 4
			new MessageTypeTable(TRAN_ADD_AUTH, "0100", "030000"), // 5
			new MessageTypeTable(TRAN_AUTH_CANCEL, "0100", "200000"), // 6
			new MessageTypeTable(TRAN_AUTH_SETTLEMENT, "0220", "000000"), // 7
			new MessageTypeTable(TRAN_AUTH_COMPLETE, "0200", "000000"), // 8
			new MessageTypeTable(TRAN_AUTH_COMPLETE_CANCEL, "0200", "200000"), // 9
			new MessageTypeTable(TRAN_OFFLINE, "0220", "000000"), // 10
			new MessageTypeTable(TRAN_ADJUST, "0220", "090000"), // 11
			new MessageTypeTable(TRAN_LOGIN, "0800", "      "), // 12
			new MessageTypeTable(TRAN_LOGOUT, "0820", "      "), // 13
			new MessageTypeTable(TRAN_VOID_SALE, "0200", "200000"), // 14
			new MessageTypeTable(TRAN_VOID_OFFLINE, "0200", "200000"), // 15
			new MessageTypeTable(TRAN_ADJUST_SALE, "0220", "000000"), // 16
			new MessageTypeTable(TRAN_ADJUST_OFFLINE, "0220", "000000"), // 17
			// 分期
			new MessageTypeTable(TRAN_INSTALLMENT_SALE, "0200", "000000"), // 18
			new MessageTypeTable(TRAN_INSTALLMENT_VOID, "0200", "200000"), // 19
			// 积分
			new MessageTypeTable(TRAN_BONUS_SALE, "0200", "000000"), // 20
			new MessageTypeTable(TRAN_BONUS_VOID_SALE, "0200", "200000"), // 21
			new MessageTypeTable(TRAN_BONUS_QUERY, "0200", "310000"), // 22
			new MessageTypeTable(TRAN_BONUS_REFUND, "0220", "200000"), // 23
			// 预约消费
			new MessageTypeTable(TRAN_RESERV_SALE, "0200", "000000"), // 24
			new MessageTypeTable(TRAN_RESERV_VOID_SALE, "0200", "200000"), // 25
			// 订购
			new MessageTypeTable(TRAN_MOTO_SALE, "0200", "000000"), // 26
			new MessageTypeTable(TRAN_MOTO_VOID_SALE, "0200", "200000"), // 27
			new MessageTypeTable(TRAN_MOTO_REFUND, "0220", "200000"), // 28
			new MessageTypeTable(TRAN_MOTO_AUTH, "0100", "030000"), // 29
			new MessageTypeTable(TRAN_MOTO_CANCEL, "0100", "200000"), // 30
			new MessageTypeTable(TRAN_MOTO_AUTH_COMP, "0200", "000000"), // 31
			new MessageTypeTable(TRAN_MOTO_VOID_COMP, "0200", "200000"), // 32
			new MessageTypeTable(TRAN_MOTO_AUTH_SETTLE, "0220", "000000"), // 33
			// 电子现金
			new MessageTypeTable(TRAN_EC_SALE, "0200", "000000"), // 34
			new MessageTypeTable(TRAN_EC_REFUND, "0220", "200000"), // 35
			new MessageTypeTable(TRAN_EC_CASH_SAVING, "0200", "630000"), // 36
																			// 现金充值
			new MessageTypeTable(TRAN_EC_VOID_SAVING, "0200", "170000"), // 37
																			// 现金充值撤销
			new MessageTypeTable(TRAN_EC_LOAD, "0200", "600000"), // 38 指定账户圈存
			new MessageTypeTable(TRAN_EC_LOAD_NOT_APPOINTED, "0200", "620000"), // 39
																				// 非指定账户圈存
			// 磁条卡充值
			new MessageTypeTable(TRAN_MAG_LOAD_CASH_CHECK, "0100", "330000"), // 40
			new MessageTypeTable(TRAN_MAG_LOAD_CASH, "0200", "630000"), // 41
			new MessageTypeTable(TRAN_MAG_LOAD_CASH_CON, "0220", "630000"), // 42
			new MessageTypeTable(TRAN_MAG_LOAD_ACCOUNT, "0200", "400000"), // 43
			// 积分签到
			new MessageTypeTable(TRAN_LOGIN_BONUS, "0820", "      "), // 44
			new MessageTypeTable(TRAN_CHECK_CARDHOLDER, "0100", "330000"), // 45

			new MessageTypeTable(TRAN_UPLOAD_MAG_OFFLINE, "0320", "      "), // 46
			new MessageTypeTable(TRAN_UPLOAD_PBOC_OFFLINE, "0320", "      "), // 47
			new MessageTypeTable(TRAN_UPLOAD_SCRIPT_RESULT, "0320", "      "), // 48
			new MessageTypeTable(TRAN_BATCH, "0500", "      "), // 49
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_OFFLINE, "0320",
					"      "), // 50
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_OFFLINE_SUCC, "0320",
					"      "), // 51
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_ONLINE, "0320", "      "), // 52
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_ADVICE, "0320", "      "), // 53
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_ADVICE, "0320",
					"      "), // 54
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_ONLINE, "0320",
					"      "), // 55
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_OFFLINE_FAIL, "0320",
					"      "), // 56
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_RISK, "0320", "      "), // 57
			new MessageTypeTable(TRAN_BATCH_END, "0320", "      "), // 58
			// 参数
			new MessageTypeTable(TRAN_DOWN_PARAM, "0800", "      "), // 59
			new MessageTypeTable(TRAN_TESTING, "0820", "      "), // 60
			new MessageTypeTable(TRAN_UPSTATUS, "0820", "      "), // 61
			new MessageTypeTable(TRAN_DOWN_CAPK, "0800", "      "), // 62
			new MessageTypeTable(TRAN_DOWN_IC_PARAM, "0800", "      "), // 63
			new MessageTypeTable(TRAN_DOWN_BLACKLIST, "0800", "      "), // 64
			/*
			 * //自定义 快捷支付 new MessageTypeTable(TRAN_SALE_9121, "0200",
			 * "000000"), // 65 new MessageTypeTable(TRAN_SALE_9100, "0200",
			 * "000000"), // 66 new MessageTypeTable(TRAN_SALE_9110, "0200",
			 * "000000"), // 67 new MessageTypeTable(TRAN_SALE_9130, "0200",
			 * "000000"), // 68 new MessageTypeTable(TRAN_SALE_9140, "0200",
			 * "000000"), // 69
			 */
			new MessageTypeTable(TRAN_SALE_REVERSAL, "0400", "000000"), // 70
			new MessageTypeTable(TRAN_REVOCATION_REVERSAL, "0400", "200000"),// 71
			new MessageTypeTable(TRAN_AUTH_REVERSAL, "0400", "030000"), // 72
			new MessageTypeTable(TRAN_AUTH_CANCEL_REVERSAL, "0400", "200000"), // 73
			new MessageTypeTable(TRAN_AUTH_COMPLETE_REVERSAL, "0400", "000000"), // 74
			new MessageTypeTable(TRAN_AUTH_COMPLETE_CANCEL_REVERSAL, "0400",
					"200000") // 75
	};

	private static int i;

	public MessageType() {
	}

	public static byte[] getReqMsgType(int transType) {
		byte[] reqMsg = null;

		for (i = 0; i < messageTypeTable.length; i++) {
			if (transType == messageTypeTable[i].transType) {
				reqMsg = messageTypeTable[i].reqMsgType;
				break;
			}
		}

		return reqMsg;
	}

	public static byte[] getProcessingCode(int transType) {
		byte[] procCode = null;

		for (i = 0; i < messageTypeTable.length; i++) {
			if (transType == messageTypeTable[i].transType) {
				procCode = messageTypeTable[i].processingCode;
				break;
			}
		}

		return procCode;
	}

	public static int getTransType(String procCode, byte[] reqMsg) {

		int transType = -1;
		for (i = 0; i < messageTypeTable.length; i++) {
			String reqMsg_s = StringUtil.toBestString(reqMsg).replace(" ", "");
			String reqMsgType_s = StringUtil
					.toString(messageTypeTable[i].reqMsgType);
			if (reqMsg_s.equals(reqMsgType_s)) {
				String processingCode_s = StringUtil
						.toBestString(messageTypeTable[i].processingCode);
				System.err.println("procCode = " + procCode
						+ ", processingCode_s = " + processingCode_s);
				if (procCode.equals(processingCode_s)) {
					transType = messageTypeTable[i].transType;
					Log.d(APP_TAG, "unpack transtype is " + transType);
					break;
				}
			}
		}
		return transType;
	}
}
