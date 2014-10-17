package cn.koolcloud.jni;

public class MisPosInterface {
	public static final int TRANS_TYPE_REGISTATION = 0x01;
	public static final int TRANS_TYPE_UNREGISTATION = 0x0E;
	public static final int TRANS_TYPE_CONSUME = 0x02;
	public static final int TRANS_TYPE_CONSUME_REVOKE = 0x03;
	public static final int TRANS_TYPE_RETURN_GOODS = 0x04;
	public static final int TRANS_TYPE_PRE_AUTH = 0x06;
	public static final int TRANS_TYPE_PRE_AUTH_REVOKE = 0x07;
	public static final int TRANS_TYPE_PRE_AUTH_COMPLETE = 0x08;
	public static final int TRANS_TYPE_GET_BALANCE = 0x12;
	public static final int TRANS_TYPE_GET_PAN = 0x62;
	public static final int TRANS_TYPE_DOWNLOAD_CAPK = 0x81;
	public static final int TRANS_TYPE_DOWNLOAD_AID = 0x82;
	
	static {
		System.loadLibrary("koolcloud_mispos");
		System.loadLibrary("MisPos");
		//System.loadLibrary("data/data/com.koolpos.demo/lib/libMisPos.so");
	}
	
	/**
	 * Serialport Communication Open
	 * @return >0 : succ
	 * 		  <=0 : fail
	 */
	public native static int communicationOpen();
	
	/**
	 * Serialport Communication Close
	 * @return >=0 : succ
	 * 			<0 : fail
	 */
	public native static int communicationClose();
	
	/**
	 * Serialport Communication Test
	 * @return >=0 : succ
	 * 			<0 : fail
	 */
	public native static int communicationTest();
	
	/**
	 * Poll message from the serialport
	 * @param nTimeout : wait timeout, usually we set -1
	 * @return >=0 : succ
	 * 			<0 : fail
	 */
	public native static int serialPoll(int nTimeout);
	
	/**
	 * Cancel Poll
	 * @return >=0 : succ
	 * 			<0 : fail
	 */
	public native static int serialCancelPoll();
	
	/**
	 * Mispos Registration
	 * @param transType : transaction type, set "0x01"
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int registration(int transType);
	
	/**
	 * Mispos Unregistration
	 * @param transType : transaction type, set "0x0E"
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int unregistration(int transType);
	
	/**
	 * Mispos Consume
	 * @param transType : transaction type, set "0x02"
	 * @param amount : transaction amount
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int consume(int transType, String amount);
	
	/**
	 * Mispos Consume Revoke
	 * @param transType : transaction type, set "0x03"
	 * @param amount : transaction amount
	 * @param voucher : voucher
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int consumeRevoke(int transType, String amount, String voucher);
	
	/**
	 * Mispos Return Goods
	 * @param transType : transaction type, set "0x04"
	 * @param amount : transaction amount
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int returnGoods(int transType, String amount);
	
	/**
	 * Mispos Prepare Authorization
	 * @param transType : transaction type, set "0x06"
	 * @param amount : transaction amount
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int preAuthorization(int transType, String amount);
	
	/**
	 * Mispos Prepare Authorization Revoke
	 * @param transType : transaction type, set "0x07"
	 * @param amount : transaction amount
	 * @param voucher : voucher
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int preAuthorizationRevoke(int transType, String amount, String voucher);
	
	/**
	 * Mispos Prepare Authorization Complete
	 * @param transType : transaction type, set "0x08"
	 * @param amount : transaction amount
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int preAuthorizationComplete(int transType, String amount);
	
	/**
	 * Mispos Get Balance
	 * @param transType : transaction type, set "0x12"
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int getBalance(int transType);
	
	/**
	 * Mispos Get PAN
	 * @param transType : transaction type, set "0x62"
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int getPAN(int transType);
	
	/**
	 * Mispos Download CAPK
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int downloadCAPK();
	
	/**
	 * Mispos Download AID
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int downloadAID();
	
	/**
	 * Mispos Send Data
	 * @param sendData : send data content
	 * @param sendDataLen : send data length
	 * @return >= 0 : succ
	 * 			< 0 : fail
	 */
	public native static int sendMessage(byte[] sendData, int sendDataLen);
	
	/**
	 * Mispos Recv Data
	 * @param recvData : recv data content
	 * @return >= 0 : succ, and return the recv data length
	 * 			< 0 : fail
	 */
	public native static int recvMessage(byte[] recvData);
	
	/**
	 * Mispos Get TAG Value
	 * @param tag : such as "0x9F00, 0x9F01..."
	 * @param value : tag value
	 * @return >= 0 : succ, and return the tag value length
	 * 			< 0 : fail
	 */
	public native static int getTagValue(int tag, byte[] value);
	
	/**
	 * Mispos Get ISO8583 field value
	 * @param tag : range is 0-127
	 * @param value : ISO8583 field value
	 * @return >= 0 : succ, and return the field value length
	 * 			< 0 : fail
	 */
	public native static int getIso8583FieldValue(int tag, byte[] value);
	
	/**
	 * Mispos Event Occure
	 * @param misposEventType
	 */
	public static void misposEventOccure(int misposEventType) {
		// get mispos callback event - misposEventOccure
		MisPosEvent.setMisposEvent(misposEventType);
	}
}
