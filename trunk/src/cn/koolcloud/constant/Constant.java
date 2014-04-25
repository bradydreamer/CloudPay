package cn.koolcloud.constant;

public interface Constant
{
	final boolean debug = true;
	final boolean GUANGDONG_CUP = true;
	final String APP_TAG = "CUP";
	final String APP_VERSION = "V000001";
	final String TSN = "WP15010000000001";
	
	final byte MAX_MASTER_KEY_NUMBER = 10;  // pinpad所支持的最大主密钥数
	final byte DEFAULT_KEY[] = {(byte)0x20, (byte)0x12, (byte)0x12, (byte)0x17, (byte)0x15, (byte)0x13, (byte)0x41, (byte)0x00,
			                    (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08
                               };
	
	/*-----  TRANSACTION TYPES  ---------------------------------*/
	final byte TRAN_BALANCE           =  0; //余额查询
	final byte TRAN_SALE              =  1; //消费
	/**
	 * 撤销的原交易必须在该终端能找到
	 * 退货则不需要在终端上找到原交易
	 * */
	final byte TRAN_VOID              =  2; //撤销
	final byte TRAN_REFUND            =  3; //退货
	final byte TRAN_AUTH              =  4; //预授权
	final byte TRAN_ADD_AUTH          =  5; //追加预授权 
	final byte TRAN_CANCEL            =  6; //预授权撤销
	final byte TRAN_AUTH_SETTLEMENT   =  7; //预授权完成离线（预授权完成通知）
	final byte TRAN_AUTH_COMPLETE     =  8; //预授权完成联机（预授权完成请求）
	final byte TRAN_VOID_COMPLETE     =  9; //预授权完成撤销
	final byte TRAN_OFFLINE           = 10; //离线结算
	final byte TRAN_ADJUST            = 11; //离线调整  
	final byte TRAN_LOGIN             = 12;	//签到
	final byte TRAN_LOGOUT            = 13; //签退
	final byte TRAN_VOID_SALE         = 14; //消费撤销
	final byte TRAN_VOID_OFFLINE      = 15; //离线撤销
	final byte TRAN_ADJUST_SALE       = 16; //小费
	final byte TRAN_ADJUST_OFFLINE    = 17; //离线调整
	/**做离线调整先要找到要调整的交易，如果原交易是消费的话，
	 * 那就是小费交易(TRAN_ADJUST_SALE)，向持卡人收取小费；
	 * 如果原交易是离线结算的话，那就是离线调整交易（TRAN_ADJUST_OFFLINE）
	 * 
	 * 做离线调整(TRAN_ADJUST)先要找到要调整的交易，
	 * 如果原交易是消费的话，那就是小费交易(TRAN_ADJUST_SALE)，向持卡人收取小费；
	 * 如果原交易是离线结算的话，那就是离线调整交易（TRAN_ADJUST_OFFLINE）
	 * */
	//分期 INSTALLMENT
	final byte TRAN_INSTALLMENT_SALE  = 18; 
	final byte TRAN_INSTALLMENT_VOID  = 19; 
	// 积分
	final byte TRAN_BONUS_SALE        = 20; 
	final byte TRAN_BONUS_VOID_SALE   = 21; 
	final byte TRAN_BONUS_QUERY       = 22; // 联盟积分查询
	final byte TRAN_BONUS_REFUND      = 23; // 联盟积分退货
	// 预约消费
	final byte TRAN_RESERV_SALE       = 24;
	final byte TRAN_RESERV_VOID_SALE  = 25;
	
	// 订购
	final byte TRAN_MOTO_SALE         = 26; // 订购消费
	final byte TRAN_MOTO_VOID_SALE    = 27; // 取消订购消费
	final byte TRAN_MOTO_REFUND       = 28;
	final byte TRAN_MOTO_AUTH         = 29;
	final byte TRAN_MOTO_CANCEL       = 30;
	final byte TRAN_MOTO_AUTH_COMP    = 31;
	final byte TRAN_MOTO_VOID_COMP    = 32;
	final byte TRAN_MOTO_AUTH_SETTLE  = 33;
	// 电子现金
	final byte TRAN_EC_SALE           = 34;
	final byte TRAN_EC_REFUND         = 35;
	final byte TRAN_EC_CASH_SAVING    = 36; // 现金充值
	final byte TRAN_EC_VOID_SAVING    = 37; // 现金充Constant值撤销
	final byte TRAN_EC_LOAD           = 38; // 指定账户圈存
	final byte TRAN_EC_LOAD_NOT_APPOINTED = 39; // 非指定账户圈存
	// 磁条卡充值
	final byte TRAN_MAG_LOAD_CASH_CHECK   = 40; // 磁条卡现金充值账户验证
	final byte TRAN_MAG_LOAD_CASH         = 41;
	final byte TRAN_MAG_LOAD_CASH_CON = 42;     // 磁条卡现金充值确认
	final byte TRAN_MAG_LOAD_ACCOUNT  = 43;
	// 积分签到
	final byte TRAN_LOGIN_BONUS       = 44;
	final byte TRAN_CHECK_CARDHOLDER  = 45;

	final byte TRAN_UPLOAD_MAG_OFFLINE             = 46;//TODO
	final byte TRAN_UPLOAD_PBOC_OFFLINE            = 47;
	final byte TRAN_UPLOAD_SCRIPT_RESULT           = 48;
	final byte TRAN_BATCH                          = 49;
	final byte TRAN_BATCH_UPLOAD_MAG_OFFLINE       = 50;
	final byte TRAN_BATCH_UPLOAD_PBOC_OFFLINE_SUCC = 51;
	final byte TRAN_BATCH_UPLOAD_MAG_ONLINE        = 52;
	final byte TRAN_BATCH_UPLOAD_MAG_ADVICE        = 53;
	final byte TRAN_BATCH_UPLOAD_PBOC_ADVICE       = 54;
	final byte TRAN_BATCH_UPLOAD_PBOC_ONLINE       = 55;
	final byte TRAN_BATCH_UPLOAD_PBOC_OFFLINE_FAIL = 56;
	final byte TRAN_BATCH_UPLOAD_PBOC_RISK         = 57;
	final byte TRAN_BATCH_END                      = 58;

	// 参数
	final byte TRAN_DOWN_PARAM        = 59;
	final byte TRAN_TESTING           = 60;
	final byte TRAN_UPSTATUS          = 61;
    final byte TRAN_DOWN_CAPK         = 62;
    final byte TRAN_DOWN_IC_PARAM     = 63;
    final byte TRAN_DOWN_BLACKLIST    = 64;
    
    /*
    final byte TRAN_SALE_9121    = 65;
    final byte TRAN_SALE_9100 = 66;
    final byte TRAN_SALE_9110 = 67;
    final byte TRAN_SALE_9130 = 68;
    final byte TRAN_SALE_9140 = 69;
    */
    final byte TRAN_SALE_REVERSAL = 70;
    final byte TRAN_REVOCATION_REVERSAL  = 71;
    

	//自定义交易
	
	/*-----  parameter set ---------------------------------*/
	// terminal param
//	final byte PARAM_TM_MID                = 1; // 商户编号
//	final byte PARAM_TM_TID                = 2; // 终端编号
//    final byte PARAM_TM_MERCHANT_NAME      = 3; // 商户名
//    final byte PARAM_TM_YEAR               = 4; // 当前年份
//	final byte PARAM_TM_TRACE              = 5; // 设置流水号(凭证号)  
//	final byte PARAM_TM_BATCH              = 6; // 设置批次号
//	final byte PARAM_TM_REFUND_LIMIT       = 7; // 最大退货金额
//	final byte PARAM_TM_PRINT_DETAIL       = 8; // 是否提示打印明细
//	final byte PARAM_TM_ENLISH_RECEIPT     = 9; // 是否打英文
//	final byte PARAM_TM_DEFAULT_TRANS      = 10; // 默认交易设置
//	final byte PARAM_TM_MANUAL_CARD        = 11; // 是否开放手输卡号
//	final byte PARAM_TM_CARD_ON_VOIDSALE   = 12; // 消费撤销是否用卡
//	final byte PARAM_TM_CARD_ON_VOIDCOMP   = 13; // 预授权完成撤销是否用卡
//	final byte PARAM_TM_PIN_ON_VOIDSALE    = 14; // 消费撤销是否输入密码
//	final byte PARAM_TM_PIN_ON_CANCEL      = 15; // 预授权撤销是否输入密码
//	final byte PARAM_TM_PIN_ON_VOIDCOMP    = 16; // 
//	final byte PARAM_TM_PIN_ON_COMP        = 17; // 
//	final byte PARAM_TM_AUTHCOMP_MODE      = 18; // 
//	final byte PARAM_TM_SMALL_AUTH         = 19; // 小额代授权
//	final byte PARAM_TM_AUTO_UPLOAD_TOTAL  = 20; // 
    // 终端密钥
    final byte PARAM_TM_KEY_INDEX          = 21;
    final byte PARAM_TM_MANUAL_KEY         = 22;
    
    // 交易功能
	// ProcessState
	final byte PROCESS_NORMAL     = 0;		
	final byte PROCESS_PREPROCESS = 1;	//预处理
	final byte PROCESS_REVERSAL   = 2;	//撤销
	final byte PROCESS_OFFLINE    = 3;	//脱机
	final byte PROCESS_BATCH      = 4;  //批处理
	
	// batch statusConstant
	final byte BATCH_UPLOAD_MAG_OFFLINE       = 0x01;  // 上送全部磁条卡离线类交易                                                          	       		帐不平（1）    9.3.1 和 9.3.2
	final byte BATCH_UPLOAD_PBOC_OFFLINE_SUCC = 0x02;  // 上送所有成功的IC卡借贷记脱机消费交易明细                  	                                帐不平（2）	9.3.4
	final byte BATCH_UPLOAD_MAG_ONLINE        = 0x03;  // 上送所有磁条卡的请求类联机成功交易明细                                        	           帐不平（3）    交易原格式
	final byte BATCH_UPLOAD_ADVICE            = 0x04;  // 上送退货和预授权完成(通知)交易                                                                         	帐不平（4）    磁条卡:9.4.5； IC卡:9.4.7 d)
	final byte BATCH_UPLOAD_PBOC_ONLINE       = 0x05;  // 上送所有成功的IC卡借贷记联机交易明细          	账平 （1）；	帐不平（5） 	9.4.7 a)
	final byte BATCH_UPLOAD_PBOC_OFFLINE_FAIL = 0x06;  // 上送所有失败的IC卡借贷记脱机消费交易明细			账平 （2）；   帐不平（6） 	9.4.7 d)
	final byte BATCH_UPLOAD_PBOC_RISK         = 0x07;  // 上送所有ARPC错但卡片仍然承兑的IC卡联机交易明细	账平 （3）				9.4.7 d)
	final byte BATCH_UPLOAD_END               = 0x08;
	final byte BATCH_DETAIL_REPORT            = 0x09;
	final byte BATCH_FAIL_REPORT              = 0x0A;
	
	//----------------------------------------------------------------------
	//  Transaction ENTRY mode
	//----------------------------------------------------------------------
	final byte SWIPE_ENTRY       =  0x02; 		//读取磁条卡号
    final byte INSERT_ENTRY      =  0x05; 		//读取IC卡
    // PIN Mode
    final byte HAVE_PIN      = 0x10;  //有密码
    final byte NO_PIN        = 0x20;  //无密码
    final String STR_NULL_PIN	= "0000000000000000";//null pin block string
    
	// Key Algorithm
	final byte SINGLE_KEY = 0;
	final byte DOUBLE_KEY = 1;

	// param type 用于上传状态
	final byte PARAM_MAG       = 0;
	final byte PARAM_CAPK      = 1;
	final byte PARAM_IC        = 2;
	final byte PARAM_BLACKLIST = 3;
	
	/**
	 * Printer Constants
	 */
	public static final String TAG_DTITAL = "e-Buy电子凭证";

	public static final String TAG_LINE = "******************************";
	
	public static final String TAG_LINE2 = "------------------------------";
	
	public static final String TAG_MERCHANT = "商户名  ：";
	
	public static final String TAG_TERMINAL = "终端号  ：";

	public static final String TAG_REF = "参考号  ：";
	
	public static final String TAG_DATE_TIME = "日期/时间：";

	public static final String TAG_TIME = "时  间  ：";
	
	public static final String TAG_PAYTYPE = "交易类型：";

	public static final String TAG_TRACE = "流水号  ：";
	
	public static final String TAG_MERCH_ORG_ORDER = "收单机构流水号  ：";
	
	public static final String TAG_AP_NAME = "收单机构名称  ：";
	
	public static final String TAG_ACCOUNT = "支付账户：";

	public static final String TAG_CHANNEL = "支付渠道：";
	
	public static final String TAG_AMOUNT = "支付金额：";
	
	public static final String TAG_TELLERNO = "操作员  ：";
	
	public static final String TRANSACTION_ID = "交易号  ：";
	
	public static final String TAG_SIGNATURE = "用户签名：";
	
	public static final String PRINT_TYPE_DEFAULT 	= "1";			//default print type
	public static final String PRINT_TYPE_ALIPAY 	= "2";			//Alipay print type
}
