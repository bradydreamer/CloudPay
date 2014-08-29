package cn.koolcloud.constant;

public class ConstantUtils {

	public static final String PRINT_TYPE_DEFAULT = "1"; 			// default print type
	public static final String PRINT_TYPE_ALIPAY = "0080"; 			// Alipay print type
	public static final String PRINT_TYPE_MISPOS = "03"; 			// mispos print type
	public static final String STR_NULL_PIN = "0000000000000000"; 	// null pin
																	// block
																	// string
	// PIN Mode
	public static final byte HAVE_PIN = 0x10; // have pin
	public static final byte NO_PIN = 0x20; // no pin

	public static final byte ENTRY_QRCODE_MODE = 0X03;
	public static final byte ENTRY_SWIPER_MODE = 0X02;
	public static final byte ENTRY_KEYBOARD_MODE = 0X01;
	public static final byte ENTRY_UNKNOW_MODE = 0X00;
	
	//appstore package name
	public static final String APP_STORE_PACKAGE_NAME = "cn.koolcloud.ipos.appstore";
	public static final String COUPON_APP_PACKAGE_NAME = "com.koolyun.coupon";
//	public static final String COUPON_APP_PACKAGE_NAME = "cn.koolcloud.ipos.appstore";
	
	public static final String ALREADY_REVERSED = "已撤销";
	
	//keys for alert common dialog
	public static final String POSITIVE_BTN_KEY = "positive_btn_key";
	public static final String NEGATIVE_BTN_KEY = "negative_btn_key";
	public static final String MSG_KEY = "alert_common_dialog_msg";
	public static final String IDENTIFIER_KEY = "alert_common_dialog_identifier";
	
	//mispos index
	public static final String IP = "116.228.223.216";
	public static final int PORT = 10021;
	public static final String MISPOS_INDEX = "90";
	//Mispos All in Pay test
	//public static final String IP = "116.236.252.102";
	//public static final int PORT = 8880;
	
}
