package cn.koolcloud.pos.util;

import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.jni.MisPosInterface;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.util.ByteUtil;

public class MisposOperationUtil {
	private static final String TAG = "MisposOperationUtil";
	
	public static final String RESPONSE_CODE_SUCCESS = "00";							//success
	public static final String TRAN_TYPE_SIGN_IN = "8011";								//sign in
	public static final String TRAN_TYPE_SIGN_OUT = "8081";								//sign out
	public static final String TRAN_TYPE_CONSUMPTION = "1021";							//consumption
	public static final String TRAN_TYPE_CONSUMPTION_TRANSFER = "8872";					//consumption transfer
	public static final String TRAN_TYPE_CONSUMPTION_REVERSE = "3021";					//consumption reverse
	public static final String TRAN_TYPE_PRE_AUTHORIZATION = "1011";					//pre authorization	
	public static final String TRAN_TYPE_PRE_AUTHORIZATION_REVERSE = "1011";			//pre authorization	reverse
	public static final String TRAN_TYPE_BALANCE = "9009";								//balance
	public static final String TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE = "1031";			//pre authorization complete
	public static final String TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE_REVERSE = "3031";	//pre authorization complete reverse

	/**
	 * communication test
	 */
	public static void communicationTest() {
		MisPosInterface.communicationTest();
	}

	/**
	 * sign in
	 */
	public static void registration() {
		MisPosInterface.registration(0x01);
	}

	/**
	 * sign out
	 */
	public static void unregistration() {
		MisPosInterface.unregistration(0x0E);
	}

	/**
	 * consumption
	 */
	public static void consume(String amountStr) {
//		String temp = "0.01";
//		String temp_amount = amountStr.replace(".", "");
		String temp_amount = amountStr;
		String amount = "";
		if (!TextUtils.isEmpty(amountStr) && amountStr.length() > 0) {
			for (int i = 0; i < 12 - temp_amount.length(); i++) {
				amount += "0";
			}
			amount += temp_amount;
		} else {
			return;
		}
		Log.i("AAAAAAAAAAAA", amount);
		MisPosInterface.consume(0x02, amount);
	}

    /**
     * consumption transfer
     */
    public static void consumeTransfer(String amountStr) {
        String temp_amount = amountStr;
        String amount = "";
        if (!TextUtils.isEmpty(amountStr) && amountStr.length() > 0) {
            for (int i = 0; i < 12 - temp_amount.length(); i++) {
                amount += "0";
            }
            amount += temp_amount;
        } else {
            return;
        }
        Log.i("AAAAAAAAAAAA", amount);
        MisPosInterface.consume(0x72, amount);
    }

	/**
	 * consumption reverse
	 */
	public static void consumeRevoke(String amountStr, String voucher) {
//		String temp = "0.01";
		String temp_amount = amountStr.replace(".", "");
		String amount = "";
		if ((temp_amount.length() % 2) != 0) {
			for (int i = 0; i < 12 - temp_amount.length(); i++) {
				amount += "0";
			}
			amount += temp_amount;
		}

		MisPosInterface.consumeRevoke(0x03, amount, voucher);
	}

	/**
	 * refund
	 */
	public static void returnGoods() {
		String temp = "0.01";
		String temp_amount = temp.replace(".", "");
		String amount = "";
		if ((temp_amount.length() % 2) != 0) {
			for (int i = 0; i < 12 - temp_amount.length(); i++) {
				amount += "0";
			}
			amount += temp_amount;
		}
		MisPosInterface.returnGoods(0x04, amount);
	}

	/**
	 * preAuthorization
	 */
	public static void preAuthorization() {
		String amount = "0.01";
		MisPosInterface.preAuthorization(0x06, amount);
	}

	/**
	 * preAuthorization reverse
	 */
	public static void preAuthorizationRevoke(String amount, String voucher) {
//		String amount = "0.01";
		MisPosInterface.preAuthorizationRevoke(0x07, amount, voucher);
	}

	/**
	 * preAuthorization complete
	 */
	public static void preAuthorizationComplete() {
		String amount = "0.01";
		MisPosInterface.preAuthorizationComplete(0x08, amount);
	}

	/**
	 * Balance
	 */
	public static void getBalance() {
		MisPosInterface.getBalance(0x12);
	}
	
	public static MisposData parseMisposData() {
		MisposData beanData = new MisposData();
		
		byte[] resCode = new byte[2];
		
		MisPosInterface.getTagValue(0x9F14, resCode);
		Log.i(TAG, "response code:" + resCode[0]);
		beanData.setResponseCode(ByteUtil.asc_to_str(resCode));
		
		byte[] bResponseMsg = new byte[100];
		int ibResponseMsgLen = MisPosInterface.getTagValue(0x9F19, bResponseMsg);
		Log.i(TAG, "response msg:" + InputStreamUtils.byteTOString(bResponseMsg));
		beanData.setResponseMsg(InputStreamUtils.byteTOString(bResponseMsg));
		
		byte[] bTransType = new byte[1];
		MisPosInterface.getTagValue(0x9F01, bTransType);
		
		Log.i(TAG, "TransType: " + bTransType[0]);
		beanData.setTransType(parseTranTypeStr(bTransType[0]));
		beanData.setTranTypeName(parseTranTypeName(bTransType[0]));
		
		//sign in:0x01, consumption:0x02, consumption reverse:0x03
		if (bTransType[0] == 0x01 || bTransType[0] == 0x02 
				|| bTransType[0] == 0x03 || bTransType[0] == 0x12) {
			getTagValue(beanData);
			
		}
		return beanData;
	}
	
	private static void getTagValue(MisposData beanData) {
		byte[] bMerchantId = new byte[100];
		int iMerchantIdLen = MisPosInterface.getTagValue(0x9F04, bMerchantId);
		String strMerchantId = new String(bMerchantId, 0, iMerchantIdLen);
		Log.i(TAG, "MerchantId: " + strMerchantId);
		beanData.setMerchantId(InputStreamUtils.byteTOString(bMerchantId));
		
		byte[] bMerchantName = new byte[100];
		int iMerchantNameLen = MisPosInterface.getTagValue(0x9F03, bMerchantName);
		String strMerchantName = new String(bMerchantName, 0, iMerchantNameLen);
		Log.i(TAG, "MerchantName: " + InputStreamUtils.byteTOString(bMerchantName));
		beanData.setMerchantName(InputStreamUtils.byteTOString(bMerchantName));
		
		byte[] bAmount = new byte[100];
		int iAmountLen = MisPosInterface.getTagValue(0x9F02, bAmount);
		String strAmount = new String(bAmount, 0, iAmountLen);
		Log.i(TAG, "Amount: " + strAmount);
		beanData.setAmount(InputStreamUtils.byteTOString(bAmount).replaceFirst("^0+", ""));
		
		byte[] bTerminalId = new byte[100];
		int iTerminalIdLen = MisPosInterface.getTagValue(0x9F05, bTerminalId);
		String strTerminalId = new String(bTerminalId, 0, iTerminalIdLen);
		Log.i(TAG, "TerminalId: " + strTerminalId);
		beanData.setTerminalId(InputStreamUtils.byteTOString(bTerminalId));
		
		byte[] bOperatorId = new byte[100];
		int iOperatorIdLen = MisPosInterface.getTagValue(0x9F06, bOperatorId);
		String strOperatorId = new String(bOperatorId, 0, iOperatorIdLen);
		Log.i(TAG, "OperatorId: " + strOperatorId);
		beanData.setOperatorId(InputStreamUtils.byteTOString(bOperatorId));
		
		byte[] bAcquirerId = new byte[100];
		int iAcquirerIdLen = MisPosInterface.getTagValue(0x9F07, bAcquirerId);
		String strAcquirerId = new String(bAcquirerId, 0, iAcquirerIdLen);
		Log.i(TAG, "AcquirerId: " + strAcquirerId);
		beanData.setAcquirerId(InputStreamUtils.byteTOString(bAcquirerId));
		
		byte[] bIssuerId = new byte[100];
		int iIssuerIdLen = MisPosInterface.getTagValue(0x9F08, bIssuerId);
		String strIssuerId = new String(bIssuerId, 0, iIssuerIdLen);
		Log.i(TAG, "IssuerId: " + strIssuerId);
		if (!TextUtils.isEmpty(InputStreamUtils.byteTOString(bIssuerId))) {
			beanData.setIssuerId(InputStreamUtils.byteTOString(bIssuerId).substring(2));
		} else {
			beanData.setIssuerId(InputStreamUtils.byteTOString(bIssuerId));
		}
		
		byte[] bIssuerName = new byte[100];
		int iIssuerNameLen = MisPosInterface.getTagValue(0x9F09, bIssuerName);
		String strIssuerName = new String(bIssuerName, 0, iIssuerNameLen);
		Log.i(TAG, "IssuerName: " + strIssuerName);
		beanData.setIssuerName(InputStreamUtils.byteTOString(bIssuerName));
		
		byte[] bCardNo = new byte[100];
		int iCardNoLen = MisPosInterface.getTagValue(0x9F0B, bCardNo);
		String strCardNo = new String(bCardNo, 0, iCardNoLen);
		Log.i(TAG, "CardNo: " + strCardNo);
		beanData.setCardNo(InputStreamUtils.byteTOString(bCardNo));
		
		byte[] bBatchNo = new byte[100];
		int iBatchNoLen = MisPosInterface.getTagValue(0x9F0D, bBatchNo);
		String strBatchNo = new String(bBatchNo, 0, iBatchNoLen);
		Log.i(TAG, "BatchNo: " + strBatchNo);
		beanData.setBatchNo(InputStreamUtils.byteTOString(bBatchNo));
		
		byte[] bVoucherNo = new byte[100];
		int iVoucherNoLen = MisPosInterface.getTagValue(0x9F0E, bVoucherNo);
		String strVoucherNo = new String(bVoucherNo, 0, iVoucherNoLen);
		Log.i(TAG, "VoucherNo: " + strVoucherNo);
		beanData.setVoucherNo(InputStreamUtils.byteTOString(bVoucherNo));
		
		byte[] bAuthNo = new byte[100];
		int iAuthNoLen = MisPosInterface.getTagValue(0x9F0F, bAuthNo);
		String strAuthNo = new String(bAuthNo, 0, iAuthNoLen);
		Log.i(TAG, "AuthNo: " + strAuthNo);
		beanData.setAuthNo(InputStreamUtils.byteTOString(bAuthNo));
		
		byte[] bRefNo = new byte[100];
		int iRefNoLen = MisPosInterface.getTagValue(0x9F10, bRefNo);
		String strRefNo = new String(bRefNo, 0, iRefNoLen);
		Log.i(TAG, "RefNo: " + strRefNo);
		beanData.setRefNo(InputStreamUtils.byteTOString(bRefNo));
		
		byte[] bDate = new byte[100];
		int iDateLen = MisPosInterface.getTagValue(0x9F11, bDate);
		String strDate = new String(bDate, 0, iDateLen);
		Log.i(TAG, "Date: " + strDate);
		beanData.setTranDate(InputStreamUtils.byteTOString(bDate));
		
		byte[] bTime = new byte[100];
		int iTimeLen = MisPosInterface.getTagValue(0x9F12, bTime);
		String strTime = new String(bTime, 0, iTimeLen);
		Log.i(TAG, "Time: " + strTime);
		beanData.setTranTime(InputStreamUtils.byteTOString(bTime));
	}
	
	private static String parseTranTypeStr(byte tranId) {
		String tranType = "";
		switch (tranId) {
		case 0x01:
			tranType = TRAN_TYPE_SIGN_IN;//sign in
			break;
		case 0x02:
			tranType = TRAN_TYPE_CONSUMPTION;//consumption
			break;
		case 0x03:
			tranType = TRAN_TYPE_CONSUMPTION_REVERSE;//consumption reverse			
			break;
		case 0x06:
			tranType = TRAN_TYPE_PRE_AUTHORIZATION;//pre authorization
			break;
		case 0x07:
			tranType = TRAN_TYPE_PRE_AUTHORIZATION_REVERSE;
			break;
		case 0x08:
			tranType = TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE;
			break;
		case 0x0B:
			tranType = TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE_REVERSE;
			break;
		case 0x0E:
			tranType = TRAN_TYPE_SIGN_OUT;
			break;
		case 0x12:
			tranType = TRAN_TYPE_BALANCE;
			break;
        case 0x72:
            tranType = TRAN_TYPE_CONSUMPTION_TRANSFER;
            break;

		default:
			break;
		}
		
		return tranType;
	}
	
	public static String parseTranTypeName(byte tranId) {
		String tranType = "";
		switch (tranId) {
		case 0x01:
			tranType = "签到";//sign in
			break;
		case 0x02:
			tranType = "消费";//consumption
			break;
		case 0x03:
			tranType = "消费撤消";//consumption reverse			
			break;
		case 0x06:
			tranType = "预授权";//pre authorization
			break;
		case 0x07:
			tranType = "预授权撤消";
			break;
		case 0x08:
			tranType = "预授权完成";
			break;
		case 0x0B:
			tranType = "预授权完成撤消";
			break;
		case 0x0E:
			tranType = "签退";
			break;
		case 0x12:
			tranType = "查询余额";
			break;

		default:
			break;
		}
		
		return tranType;
	}
	
	/**
	 * 
	 * @Title: parseDateTimeString
	 * @Description: TODO
	 * @param date	YYYYMMDD
	 * @param time	HHmmss
	 * @return
	 * @return: String
	 */
	public static String parseDateTimeString(String date, String time) {
		String tmpDateTime = "";
		if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
			return tmpDateTime;
		}
		
		tmpDateTime = date.substring(0, 4) 
			+ "-" + date.substring(4, 6)
			+ "-" + date.substring(6, 8)
			+ " " + time.substring(0, 2)
			+ ":" + time.substring(2, 4)
			+ ":" + time.substring(4, 6);
		return tmpDateTime;
	}
	
	
}
