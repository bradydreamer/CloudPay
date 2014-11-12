package cn.koolcloud.jni;

import android.util.Log;

public class EmvL2Interface {
	static {
		System.loadLibrary("EMV_L2");
	}

	/*
	 * 加载EMV内核
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int loadKernel();

	/**
	 * 卸载EMV内核
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int unloadKernel();

	/**
	 * EMV内核参数初始化，在每次启动交易之前调用
	 * 
	 * @param pinpadInputMode
	 *            0:auto 1:manual, 默认应该选择手动输入PIN的方式
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int emvKernelInit(int pinpadInputMode,
			int pinpadkeyIndex);

	/**
	 * Open cardReader
	 * 
	 * @param[in] reader : 阅读器类型 : 0 打开所有读卡器 : 1 只打开接触式读卡器 : 2 只打开非接触式读卡器
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int openReader(int reader);

	/**
	 * 关闭读卡器，并释放资源
	 * 
	 * @param[in] reader： 阅读器类型 : 0 关闭所有读卡器 : 1 只关闭接触式读卡器 : 2 只关闭非接触式读卡器
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int closeReader(int reader);

	/**
	 * 查询EMV卡片是否在位
	 * 
	 * @param[in] reader： 阅读器类型 : 0 查询所有读卡器 : 1 只查询接触式读卡器 : 2 只查询非接触式读卡器
	 * @return - value >= 0 : 1存在 0 不存在 - value == -1 : error
	 */
	public native static int queryCardPresence(int reader);

	/**
	 * 接触式EMV卡槽上电
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int cardPowerOn();

	/**
	 * 接触式EMV卡槽下电
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int cardPowerOff();

	/**
	 * EMV内核选择银行卡片应用
	 * 
	 * @param kernelMode
	 *            0:PBOC kernel 1:QPBOC kernel
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int selectApp(int kernelMode);

	/**
	 * EMV 内核应用初始化
	 * 
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int appInit();

	/**
	 * EMV 内核读应用数据
	 * 
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int readAppData();

	/**
	 * EMV 内核脱机数据认证
	 * 
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int offLineDataAuth();

	/**
	 * EMV 内核交易处理限制
	 * 
	 * @param
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int processRestrict();

	/**
	 * EMV 内核持卡人认证方法
	 * 
	 * @return
	 * @param tag9F61Data
	 *            持卡人证件号码 tag9F61Len 持卡人证件号长度 printMethod，交易是否需要打印 1 打印 0 不打印 -
	 *            value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int getVerifyMethod(byte[] tag9F61Data,
			byte[] tag9F61Len, byte[] printMethod);

	/**
	 * EMV 内核风险管理控制
	 * 
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int terRiskManage();

	/**
	 * EMV 内核终端行为分析
	 * 
	 * @return - value >= 0 : success (suggest 0) - value < 0 : error
	 */
	public native static int terActionAnalyse();

	/**
	 * EMV IC卡L2内核 联机交易前处理 卡片拒绝不需要走完流程 超时要走完流程
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -12009 : 交易中止 -
	 *         value == -12010 : 不允许服务
	 */
	public native static int sendOnlineMessage();

	/**
	 * EMV IC卡L2内核 联机交易后处理 外部认证失败不要走完流程
	 * 
	 * @param g_pucEMVScript
	 *            发卡行脚本数据，最大255个字节 g_ucEMVScriptLen 发卡行脚本数据长度 s_aucEMVIssuerData
	 *            发卡行认证数据 s_ucEMVIssuerDataLen 发卡行认证数据长度，最大255个字节
	 *            aucEMVAuthorCode 授权码(没有用到) ucEMVAuthorCodeLen 授权码长度，最大40个字节
	 *            aucEMVRespCode 授权响应码 ucEMVRespCodeLen 授权响应码长度，最大40个字节
	 * @return - value >= 0 : success (suggest 0) - value == -12009 : 交易中止
	 */
	public native static int recvOnlineMessage(byte[] g_pucEMVScript,
			char g_ucEMVScriptLen, byte[] s_aucEMVIssuerData,
			char s_ucEMVIssuerDataLen, byte[] aucEMVAuthorCode,
			char ucEMVAuthorCodeLen, byte[] aucEMVRespCode,
			char ucEMVRespCodeLen);

	/**
	 * EMV 内核交易结束
	 * 
	 * @return - value >= 0 : success (suggest 0)，APK 发送确认报文 - value == -1 :
	 *         交易拒绝 - value == -12009 : 交易中止 - value == -12010 : 不允许服务 - value
	 *         == -12011 : 交易批准，冲正
	 */
	public native static int tradeEnd();

	/**
	 * EMV 内核下载内核交易参数，包括终端，公钥，应用参数 说明1. 前期内核参数下载请使用模拟后台工具进行下载 2.
	 * 后期内核参数下载请使用终端应用APK下载内核参数
	 * 
	 * @param emvParamType
	 *            Aid Param 应用参数 0 CAPK param 公钥参数 1 Terminal param 终端参数 2 Card
	 *            BlackList 卡黑名单 3 CAPK BlackList 公钥黑名单 4
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int downloadParam(byte[] emvParam, int dataLength,
			int emvParamType);

	/**
	 * EMV 内核保存参数
	 * 
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int saveParam();

	/**
	 * EMV 内核设置交易金额
	 * 
	 * @param tradeSum
	 *            长整型
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int setTradeSum(long tradeSum);

	/**
	 * EMV 内核设置交易类型
	 * 
	 * @param transType
	 *            0x4000 //商品 0x2000 //服务
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int setTradeType(short transType);

	/**
	 * EMV 内核读卡片交易日志
	 * 
	 * @param
	 * @return - value >= 0 : success (suggest 0) - value == -1 : error
	 */
	public native static int emvReadLog(byte[] tradeList);

	/**
	 * EMV 内核持卡人证件确认选择
	 * 
	 * @param pressKey
	 *            ----0xaa 确认键 0xbb 取消键
	 * @return
	 */
	public native static void cardholderConfirm(char pressKey);

	/**
	 * EMV 内核获取内核元素，进行组包
	 * 
	 * @param
	 * @return - value >= 0 : Tag元素的长度 - value == -1 : error
	 */
	public native static int getTagValue(short tag, byte[] tagValue);

	public static void cardEventOccure(int cardEventType) {
		// Log.i("jni.EMVKernelInterface",
		// "获取【卡事件回调】操作 - cardEventOccure");
		// int cardType = getCardType();
		// EMVKernel.cardEventCallBack(cardType, cardEventType);
		Log.i("CCCCC", "AAAAAA");
		EmvL2Event.setCardEvent(cardEventType);
		Log.i("DDDDD", "EEEEEE");
		// BaseInterface.led_on(BaseInterface.COLOR_YELLOW);
	}
}
