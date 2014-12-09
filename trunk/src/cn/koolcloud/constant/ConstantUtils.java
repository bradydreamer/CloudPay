package cn.koolcloud.constant;

public class ConstantUtils {

	public static final String PRINT_TYPE_DEFAULT = "1"; // default print type
	public static final String PRINT_TYPE_ALIPAY = "02"; // Alipay print type
	public static final String PRINT_TYPE_ALIPAY_OVER_SEA = "12"; // Alipay over sea print type
	public static final String PRINT_TYPE_TRANSFER = "11"; // transfer print type
	public static final String PRINT_TYPE_MISPOS = "03"; // mispos print type
	public static final String STR_NULL_PIN = "0000000000000000"; // null pin
																	// block
																	// string
	// PIN Mode
	public static final byte HAVE_PIN = 0x10; // have pin
	public static final byte NO_PIN = 0x20; // no pin

	public static final byte ENTRY_PREPAID_CARD_QRCODE_MODE = (byte) 0x81;
	public static final byte ENTRY_PREPAID_COUPON_QRCODE_MODE = (byte) 0x82;
	public static final byte ENTRY_QRCODE_MODE = 0x03;
	public static final byte ENTRY_SWIPER_MODE = 0x02;
	public static final byte ENTRY_KEYBOARD_MODE = 0x01;
	public static final byte ENTRY_UNKNOW_MODE = 0x00;
	public static final byte ENTRY_IC_MODE = 0x05;

	// appstore package name
	public static final String APP_STORE_PACKAGE_NAME = "cn.koolcloud.ipos.appstore";
	public static final String COUPON_APP_PACKAGE_NAME = "com.koolyun.coupon";
	// public static final String COUPON_APP_PACKAGE_NAME =
	// "cn.koolcloud.ipos.appstore";

	public static final String ALREADY_REVERSED = "已撤销";

	// keys for alert common dialog
	public static final String POSITIVE_BTN_KEY = "positive_btn_key";
	public static final String NEGATIVE_BTN_KEY = "negative_btn_key";
	public static final String MSG_KEY = "alert_common_dialog_msg";
	public static final String IDENTIFIER_KEY = "alert_common_dialog_identifier";
	public static final String SER_KEY = "cn.koolcloud.pos.ser";
	public static final String UPDATE_INFO_KEY = "update_info_key";
	public static final String LOCAl_SERVICE_TAG = "local_service_tag";
	public static final String START_SERVICE_EXTERNAL_TAG = "start_service_external";

	public static final String DEVICE_PINPAD_KEY = "pinpad";
	public static final String DEVICE_NETWORK_KEY = "network";
	public static final String DEVICE_PRINTER_KEY = "printer";
	public static final String DEVICE_MISPOS_KEY = "mispos";
	public static final String DEVICE_ALL_KEY = "all_devices";

	// mispos index
	public static final String IP = "116.228.223.216";
	public static final int PORT = 10021;
	public static final String MISPOS_INDEX = "90";
	// Mispos All in Pay test
	// public static final String IP = "116.236.252.102";
	// public static final int PORT = 8880;
	// Exception/Error tag
	public static final String ERROR_TYPE_0 = "0x00"; // 冲正解包出错。
	/*
	 *  组织报文失败：1.可能是Mac计算失败（键盘没有插好，或密钥没有灌成功）
	 *             2.可以是组文过程中返回的报文长度为0.
	 */
	public static final String ERROR_TYPE_1 = "0x01";
	public static final String ERROR_TYPE_2 = "0x02"; // 预留

	public static final String APMP_TRAN_TYPE_CONSUME = "1021";
	public static final String APMP_TRAN_TYPE_CONSUMECANCE = "3021";
	public static final String APMP_TRAN_TYPE_PREAUTH = "1011";
	public static final String APMP_TRAN_TYPE_PRAUTHCOMPLETE = "1031";
	public static final String APMP_TRAN_TYPE_PRAUTHSETTLEMENT = "1091";
	public static final String APMP_TRAN_TYPE_PRAUTHCANCEL = "3011";
	public static final String APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL = "3031";
	public static final String FOR_PRINT_MERCHANT_NAME = "merchantName";
	public static final String FOR_PRINT_MERCHANT_ID = "merchantID";
	public static final String FOR_PRINT_MECHINE_ID = "mechineID";
	public static final String TAB_TYPE_COUPON = "coupon";

	public static final String ORDER_STATE_SUCCESS = "0";
	public static final String ORDER_STATE_FAILURE = "1";
	public static final String ORDER_STATE_CHONGZHENG = "2";
	public static final String ORDER_STATE_REVOKE = "3";
	public static final String ORDER_STATE_AUTH_COMPLETE = "4";
	public static final String ORDER_STATE_INTERRUPT = "5";
	public static final String ORDER_STATE_TIMEOUT = "9";

	public static final String ALIIPAY_OPEN_BRH = "0229000228";

	public static final String LANGUAGE_CHINESE = "zh";

}
