package cn.koolcloud.iso8583;

import android.util.Log;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.util.StringUtil;

public class MessageType implements Constant {
	public static MessageTypeTable[] messageTypeTable = {
			new MessageTypeTable(TRAN_BALANCE, "0200", "310000", "00", "01"), // 0
			new MessageTypeTable(TRAN_SALE, "0200", "000000", "00", "22"), // 1
			new MessageTypeTable(TRAN_VOID, "0200", "200000", "00", "23"), // 2
			new MessageTypeTable(TRAN_REFUND, "0220", "200000", "00", "25"), // 3
			new MessageTypeTable(TRAN_AUTH, "0100", "030000", "06", "10"), // 4
			new MessageTypeTable(TRAN_ADD_AUTH, "0100", "030000", "60", "10"), // 5
			new MessageTypeTable(TRAN_AUTH_CANCEL, "0100", "200000", "06", "11"), // 6
			new MessageTypeTable(TRAN_AUTH_SETTLEMENT, "0220", "000000", "06",
					"24"), // 7
			new MessageTypeTable(TRAN_AUTH_COMPLETE, "0200", "000000", "06",
					"20"), // 8
			new MessageTypeTable(TRAN_AUTH_COMPLETE_CANCEL, "0200", "200000",
					"06", "21"), // 9
			new MessageTypeTable(TRAN_OFFLINE, "0220", "000000", "00", "30"), // 10
			new MessageTypeTable(TRAN_ADJUST, "0220", "090000", "00", "00"), // 11
			new MessageTypeTable(TRAN_LOGIN, "0800", "      ", "00", "00"), // 12
			new MessageTypeTable(TRAN_LOGOUT, "0820", "      ", "00", "00"), // 13
			new MessageTypeTable(TRAN_VOID_SALE, "0200", "200000", "00", "23"), // 14
			new MessageTypeTable(TRAN_VOID_OFFLINE, "0200", "200000", "00",
					"00"), // 15
			new MessageTypeTable(TRAN_ADJUST_SALE, "0220", "000000", "00", "34"), // 16
			new MessageTypeTable(TRAN_ADJUST_OFFLINE, "0220", "000000", "00",
					"32"), // 17
			// 分期
			new MessageTypeTable(TRAN_INSTALLMENT_SALE, "0200", "000000", "64",
					"22"), // 18
			new MessageTypeTable(TRAN_INSTALLMENT_VOID, "0200", "200000", "64",
					"23"), // 19
			// 积分
			new MessageTypeTable(TRAN_BONUS_SALE, "0200", "000000", "65", "22"), // 20
			new MessageTypeTable(TRAN_BONUS_VOID_SALE, "0200", "200000", "65",
					"23"), // 21
			new MessageTypeTable(TRAN_BONUS_QUERY, "0200", "310000", "65", "03"), // 22
			new MessageTypeTable(TRAN_BONUS_REFUND, "0220", "200000", "00",
					"25"), // 23
			// 预约消费
			new MessageTypeTable(TRAN_RESERV_SALE, "0200", "000000", "67", "54"), // 24
			new MessageTypeTable(TRAN_RESERV_VOID_SALE, "0200", "200000", "67",
					"53"), // 25
			// 订购
			new MessageTypeTable(TRAN_MOTO_SALE, "0200", "000000", "08", "22"), // 26
			new MessageTypeTable(TRAN_MOTO_VOID_SALE, "0200", "200000", "08",
					"23"), // 27
			new MessageTypeTable(TRAN_MOTO_REFUND, "0220", "200000", "08", "25"), // 28
			new MessageTypeTable(TRAN_MOTO_AUTH, "0100", "030000", "18", "00"), // 29
			new MessageTypeTable(TRAN_MOTO_CANCEL, "0100", "200000", "18", "00"), // 30
			new MessageTypeTable(TRAN_MOTO_AUTH_COMP, "0200", "000000", "18",
					"00"), // 31
			new MessageTypeTable(TRAN_MOTO_VOID_COMP, "0200", "200000", "18",
					"00"), // 32
			new MessageTypeTable(TRAN_MOTO_AUTH_SETTLE, "0220", "000000", "18",
					"00"), // 33
			// 电子现金
			new MessageTypeTable(TRAN_EC_SALE, "0200", "000000", "00", "36"), // 34
			new MessageTypeTable(TRAN_EC_REFUND, "0220", "200000", "00", "27"), // 35
			new MessageTypeTable(TRAN_EC_CASH_SAVING, "0200", "630000", "91",
					"46"), // 36
			// 现金充值
			new MessageTypeTable(TRAN_EC_VOID_SAVING, "0200", "170000", "00",
					"51"), // 37
			// 现金充值撤销
			new MessageTypeTable(TRAN_EC_LOAD, "0200", "600000", "91", "45"), // 38
			// 指定账户圈存
			new MessageTypeTable(TRAN_EC_LOAD_NOT_APPOINTED, "0200", "620000",
					"91", "47"), // 39
			// 非指定账户圈存
			// 磁条卡充值
			new MessageTypeTable(TRAN_MAG_LOAD_CASH_CHECK, "0100", "330000",
					"00", "00"), // 40
			new MessageTypeTable(TRAN_MAG_LOAD_CASH, "0200", "630000", "00",
					"01"), // 41
			new MessageTypeTable(TRAN_MAG_LOAD_CASH_CON, "0220", "630000",
					"00", "48"), // 42
			new MessageTypeTable(TRAN_MAG_LOAD_ACCOUNT, "0200", "400000", "66",
					"49"), // 43
			// 积分签到
			new MessageTypeTable(TRAN_LOGIN_BONUS, "0820", "      ", "00", "00"), // 44
			new MessageTypeTable(TRAN_CHECK_CARDHOLDER, "0100", "330000", "00",
					"01"), // 45

			new MessageTypeTable(TRAN_UPLOAD_MAG_OFFLINE, "0320", "      ",
					"00", "00"), // 46
			new MessageTypeTable(TRAN_UPLOAD_PBOC_OFFLINE, "0320", "      ",
					"00", "00"), // 47
			new MessageTypeTable(TRAN_UPLOAD_SCRIPT_RESULT, "0320", "      ",
					"00", "00"), // 48
			new MessageTypeTable(TRAN_BATCH, "0500", "      ", "00", "00"), // 49
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_OFFLINE, "0320",
					"      ", "00", "00"), // 50
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_OFFLINE_SUCC, "0320",
					"      ", "00", "00"), // 51
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_ONLINE, "0320",
					"      ", "00", "00"), // 52
			new MessageTypeTable(TRAN_BATCH_UPLOAD_MAG_ADVICE, "0320",
					"      ", "00", "00"), // 53
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_ADVICE, "0320",
					"      ", "00", "00"), // 54
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_ONLINE, "0320",
					"      ", "00", "00"), // 55
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_OFFLINE_FAIL, "0320",
					"      ", "00", "00"), // 56
			new MessageTypeTable(TRAN_BATCH_UPLOAD_PBOC_RISK, "0320", "      ",
					"00", "00"), // 57
			new MessageTypeTable(TRAN_BATCH_END, "0320", "      ", "00", "00"), // 58
			// 参数
			new MessageTypeTable(TRAN_DOWN_PARAM, "0800", "      ", "00", "00"), // 59
			new MessageTypeTable(TRAN_TESTING, "0820", "      ", "00", "00"), // 60
			new MessageTypeTable(TRAN_UPSTATUS, "0820", "      ", "00", "00"), // 61
			new MessageTypeTable(TRAN_DOWN_CAPK, "0800", "      ", "00", "00"), // 62
			new MessageTypeTable(TRAN_DOWN_IC_PARAM, "0800", "      ", "00",
					"00"), // 63
			new MessageTypeTable(TRAN_DOWN_BLACKLIST, "0800", "      ", "00",
					"00"), // 64
			new MessageTypeTable(TRAN_DWON_CAPK_PARAM_END, "0800", "      ",
					"00", "00"), // 65
			/*
			 * //自定义 快捷支付 new MessageTypeTable(TRAN_SALE_9121, "0200",
			 * "000000"), // 65 new MessageTypeTable(TRAN_SALE_9100, "0200",
			 * "000000"), // 66 new MessageTypeTable(TRAN_SALE_9110, "0200",
			 * "000000"), // 67 new MessageTypeTable(TRAN_SALE_9130, "0200",
			 * "000000"), // 68 new MessageTypeTable(TRAN_SALE_9140, "0200",
			 * "000000"), // 69
			 */
			new MessageTypeTable(TRAN_SALE_REVERSAL, "0400", "000000", "00",
					"22"), // 70
			new MessageTypeTable(TRAN_REVOCATION_REVERSAL, "0400", "200000",
					"00", "22"),// 71
			new MessageTypeTable(TRAN_AUTH_REVERSAL, "0400", "030000", "06",
					"10"), // 72
			new MessageTypeTable(TRAN_AUTH_CANCEL_REVERSAL, "0400", "200000",
					"06", "11"), // 73
			new MessageTypeTable(TRAN_AUTH_COMPLETE_REVERSAL, "0400", "000000",
					"06", "20"), // 74
			new MessageTypeTable(TRAN_AUTH_COMPLETE_CANCEL_REVERSAL, "0400",
					"200000", "06", "21"), // 75
			
			new MessageTypeTable(TRAN_SUPER_TRANSFER, "0200", "383003", "82", "00") // 76
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

	public static int getTransType(String procCode, byte[] reqMsg, String pocc,
			String transTypeCode) {
		int transType = -1;
		for (i = 0; i < messageTypeTable.length; i++) {
			String reqMsg_s = StringUtil.toBestString(reqMsg).replace(" ", "");
			if (reqMsg_s.equals(StringUtil
					.toString(messageTypeTable[i].reqMsgType))
					&& procCode.equals(StringUtil
							.toBestString(messageTypeTable[i].processingCode))
					&& pocc.equals(StringUtil
							.toBestString(messageTypeTable[i].pocc))
					&& transTypeCode.equals(StringUtil
							.toBestString(messageTypeTable[i].tranTypeCode))) {
				transType = messageTypeTable[i].transType;
				break;
			}
		}
		return transType;
	}
}
