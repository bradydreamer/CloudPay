package cn.koolcloud.pos.external;

import java.util.Arrays;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import cn.koolcloud.jni.EmvL2Event;
import cn.koolcloud.jni.EmvL2Interface;
import cn.koolcloud.parameter.EMVICData;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.util.ByteUtil;
import cn.koolcloud.util.StringUtil;

public class EMVICManager {
	private Handler mHandler;
	public static final int GET_SUCCESS = 0x10;
	public static final int GET_FAILED = 0x11;
	public static final String ICCARDSTATUS = "card_status";
	public static final int STATUS_VALUE_0 = 0x20;
	public static final int STATUS_VALUE_1 = 0x21;
	public static final int STATUS_VALUE_2 = 0x22;
	public static final int STATUS_VALUE_3 = 0x23;
	public static final int STATUS_VALUE_4 = 0x24;
	public static final int TRADE_STATUS_0 = 0x30;
	public static final int TRADE_STATUS_1 = 0x31;
	public static final int TRADE_STATUS_2 = 0x32;
	public static final int TRADE_STATUS_3 = 0x33;
	public static final int TRADE_STATUS_4 = 0x34;
	public static final int TRADE_STATUS_5 = 0x35;
	public static final int TRADE_STATUS_6 = 0x36;
	public static final int TRADE_STATUS_7 = 0x37;
	public static final int TRADE_STATUS_8 = 0x38;
	public static final int TRADE_STATUS_9 = 0x39;
	public static final int TRADE_STATUS_10 = 0x50;
	public static final int TRADE_STATUS_BAN = 0x40;
	public static final int TRADE_STATUS_ABORT = 0x41;
	public static final int TRADE_STATUS_APPROVED = 0x42;
	public static final int TRADE_STATUS_DISABLESERVICE = 0x43;
	public static final int TRADE_STATUS_ONLINE = 0x44;
	public static final int TRADE_STATUS_AFGETICDATA = 0x45;
	public static final int TRADE_STATUS_READICCARDID = 0x46;
	public static final int SMART_CARD_EVENT_INSERT_CARD = 0x00;
	public static final int SMART_CARD_EVENT_REMOVE_CARD = 0x01;
	public static final int SMART_CARD_EVENT_INPUTPASSWORD = 0x04;
	private Boolean tradeAbortFlag = true;

	private final String TAG = "EMVICManager";

	private Looper waitDataLooper;
	private Thread checkCardStatusThread = null;
	private String price = null;

	public byte[] tag9F61Data = new byte[255];
	public byte[] tag9F61DataLen = new byte[1];
	public int cardType = 0;
	public byte[] bPrintMethod = new byte[1];
	private Boolean tradeFlag = false;
	private Boolean readFlag = false;
	private Boolean readRes = false;
	private EMVICData mEMVICData = EMVICData.getEMVICInstance();
	private UtilFor8583 util8583 = UtilFor8583.getInstance();

	private static EMVICManager emvICManger = null;

	private EMVICManager() {
		super();
	}

	public static EMVICManager getEMVICManagerInstance() {
		if (emvICManger == null) {
			emvICManger = new EMVICManager();
		}
		return emvICManger;
	}

	public void onCreate(Context c, Handler handler) {
		this.mHandler = handler;
		// EMVL2初始化
		init();
	}

	public void init() {
		int keyIndex = Integer.parseInt(util8583.terminalConfig.getKeyIndex());
		EmvL2Interface.loadKernel();
		EmvL2Interface.emvKernelInit(1, keyIndex);
		EmvL2Interface.openReader(1);
	}

	public void initForRead(){
		readFlag = true;
		readRes = false;
	}

	public void finishRead(){
		readFlag = false;
		readRes = false;
		onDestroy();
	}

	public void finish() {
		EmvL2Interface.cardPowerOff();
		EmvL2Interface.closeReader(1);
		EmvL2Interface.unloadKernel();
	}

	public void downloadParamsInit() {
		EmvL2Interface.loadKernel();
	}

	public void downloadParamsFinish() {
		EmvL2Interface.unloadKernel();
	}

	public void onStart() {
		if (checkCardStatusThread == null) {
			tradeFlag = true;
			checkCardStatusThread = new Thread(new Runnable() {

				@Override
				public void run() {
					Boolean queryStatus = false;
					while (true) {
						if (!queryStatus) {
							if (EmvL2Interface.queryCardPresence(0) == 1) {
								EmvL2Interface.cardPowerOn();
								EmvL2Event
										.setCardEvent(SMART_CARD_EVENT_INSERT_CARD);
							}
							queryStatus = true;
						}
						checkCardStatus();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							checkCardStatusThread = null;
							break;
						}
					}
				}
			});
			checkCardStatusThread.start();
		}
	}

	public void onPause() {
		onDestroy();
	}

	public void onDestroy() {
		finish();
		tradeAbortFlag = true;
		if (checkCardStatusThread != null) {
			Log.i(TAG, "EMVICManage onDestroy------------Stop thread");
			checkCardStatusThread.interrupt();
		}
	}

	public void setTransAmount(String price) {
		this.price = price;
	}

	private void checkCardStatus() {
		switch (EmvL2Event.getCardEvent()) {
		case SMART_CARD_EVENT_INSERT_CARD: {
			if (!tradeAbortFlag) {
				tradeStatusSend(TRADE_STATUS_9);
			}
			if(readRes){
				tradeStatusSend(TRADE_STATUS_10);
				return;
			}
			if (tradeFlag && tradeAbortFlag) {
				tradeFlag = false;
				tradeAbortFlag = false;
				Message message = new Message();
				message.what = STATUS_VALUE_0;
				mHandler.sendMessage(message);
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						tradeProcess();
					}
				});
				th.start();
				EmvL2Event.setCardEvent(-1);
			}
			break;
		}
		case SMART_CARD_EVENT_REMOVE_CARD: {
			tradeFlag = true;
			Message message = new Message();
			message.what = STATUS_VALUE_1;
			mHandler.sendMessage(message);
			EmvL2Event.setCardEvent(-1);
			break;
		}
		case SMART_CARD_EVENT_INPUTPASSWORD: {
			Message message = new Message();
			message.what = STATUS_VALUE_4;
			mHandler.sendMessage(message);
			EmvL2Event.setCardEvent(-1);
			break;
		}
		}

	}

	private void tradeProcess() {

		// 交易金额的设置
		Log.i(TAG, "strAmount: " + price);
		if (price == null || price.equals("")) {
			price = "0";
		}
		long transAmount = Long.parseLong(UtilForMoney.yuan2fen(price));
		EmvL2Interface.setTradeSum(transAmount);
		tradeStatusSend(TRADE_STATUS_0);
		// 应用选择
		int ret = EmvL2Interface.selectApp(0);
		if (ret < 0) {
			tradeStatusSend(TRADE_STATUS_ABORT);
			return;
		}
		tradeStatusSend(TRADE_STATUS_1);
		// 应用初始化
		ret = EmvL2Interface.appInit();
		if (ret < 0) {
			tradeStatusSend(TRADE_STATUS_ABORT);
			return;
		}
		tradeStatusSend(TRADE_STATUS_2);
		// 读应用数据
		ret = EmvL2Interface.readAppData();
		if (ret < 0) {
			tradeStatusSend(TRADE_STATUS_ABORT);
			return;
		}
		if(readFlag){ //读取数据，只需要进行到此步骤。
			tradeAbortFlag = true;
			readRes = true;
			tradeStatusSend(TRADE_STATUS_READICCARDID);
			return;
		}
		tradeStatusSend(TRADE_STATUS_3);
		// 脱机数据认证
		ret = EmvL2Interface.offLineDataAuth();
		if (ret < 0) {
			tradeStatusSend(TRADE_STATUS_ABORT);
			return;
		}
		tradeStatusSend(TRADE_STATUS_4);
		// 处理限制
		ret = EmvL2Interface.processRestrict();
		if (ret < 0) {
			tradeStatusSend(TRADE_STATUS_ABORT);
			return;
		}
		tradeStatusSend(TRADE_STATUS_5);
		// 持卡人认证
		cardType = EmvL2Interface.getVerifyMethod(tag9F61Data, tag9F61DataLen,
				bPrintMethod);
		if (cardType < 0) {
			if (cardType == -12009) {
				tradeStatusSend(TRADE_STATUS_ABORT);
				return;
			}
		} else if (cardType > 0) {
			// todo 持卡人出示证件，进行确认！
		} else if (cardType == 0) {
			tradeStatusSend(TRADE_STATUS_6);
			// 终端风险管理
			ret = EmvL2Interface.terRiskManage();
			if (ret < 0) {
				tradeStatusSend(TRADE_STATUS_ABORT);
				return;
			}
			tradeStatusSend(TRADE_STATUS_7);
			// 终端行为分析
			ret = EmvL2Interface.terActionAnalyse();
			if (ret < 0) {
				tradeStatusSend(TRADE_STATUS_ABORT);
				return;
			}
			tradeStatusSend(TRADE_STATUS_8);
			ret = EmvL2Interface.sendOnlineMessage();
			if (ret < 0) {
				if (ret == -12009) {
					tradeStatusSend(TRADE_STATUS_ABORT);
					return;
				} else if (ret == -12010) {
					tradeStatusSend(TRADE_STATUS_DISABLESERVICE);
					return;
				}
			}
			tradeStatusSend(TRADE_STATUS_ONLINE);
			getICDatas();
			tradeStatusSend(TRADE_STATUS_AFGETICDATA);
		}
	}

	/**
	 * IC卡交易结束
	 * 
	 * @return
	 */
	public int tradeEnd() {
		int ret = EmvL2Interface.tradeEnd();
		Log.i(TAG, "tradeEnd ret: " + ret);
		finish();
		return ret;
	}

	private void getICDatas() {
		getPinBlock();
		getICF55();
		getPAN();
		getDataOfExpired();
		getCardSeqNo();
		getTrack2();
	}

	private void tradeStatusSend(int status) {
		if (status == TRADE_STATUS_ABORT
				|| status == TRADE_STATUS_DISABLESERVICE
				|| status == TRADE_STATUS_BAN
				|| status == TRADE_STATUS_APPROVED) {
			tradeAbortFlag = true;
		}
		Message message = new Message();
		message.what = status;
		mHandler.sendMessage(message);
	}

	/**
	 * 获取pinblock.
	 */
	public void getPinBlock() {
		byte[] pinBlock = new byte[8];
		byte[] pinTemp = new byte[128];
		int pLen = 0;
		// 00E4:pinblock
		pLen = EmvL2Interface.getTagValue((short) 0x00E4, pinTemp);
		Log.i(TAG, "00E4--" + StringUtil.toHexString(pinTemp, 0, pLen));
		System.arraycopy(pinTemp, 3, pinBlock, 0, pinBlock.length);
		if (!Arrays.equals(pinBlock, "00000000".getBytes())) {
			mEMVICData.setPinBlock(pinBlock);
		}
	}

	/**
	 * @return ICDataClass 用于脚本通知获取IC卡TAG信息
	 */
	public void getICF55() {
		int f55Length = 0;
		byte[] f55 = new byte[255];
		byte ba[] = new byte[255];
		int pLen = 0;

		// 5F2A：交易货币代码
		pLen = EmvL2Interface.getTagValue((short) 0x5F2A, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "5F2A===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F02：交易金额
		pLen = EmvL2Interface.getTagValue((short) 0x9F02, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F02===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F03：其它金额
		pLen = EmvL2Interface.getTagValue((short) 0x9F03, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F03===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F09：应用版本号
		pLen = EmvL2Interface.getTagValue((short) 0x9F09, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F09===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F10：发卡行应用数据
		pLen = EmvL2Interface.getTagValue((short) 0x9F10, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F10===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F1A：终端国家代码
		pLen = EmvL2Interface.getTagValue((short) 0x9F1A, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F1A===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F1E：接口设备序列号
		pLen = EmvL2Interface.getTagValue((short) 0x9F1E, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F1E===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F26：应用密文
		pLen = EmvL2Interface.getTagValue((short) 0x9F26, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F26===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F27：应用信息数据
		pLen = EmvL2Interface.getTagValue((short) 0x9F27, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F27===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F33：终端性能
		pLen = EmvL2Interface.getTagValue((short) 0x9F33, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F33===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;

		// 9F34：持卡人验证结果
		pLen = EmvL2Interface.getTagValue((short) 0x9F34, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F34===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F35：终端类型
		pLen = EmvL2Interface.getTagValue((short) 0x9F35, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F35===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F36：应用交易计算器
		pLen = EmvL2Interface.getTagValue((short) 0x9F36, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F36===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9F37：不可预知数
		pLen = EmvL2Interface.getTagValue((short) 0x9F37, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F37===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;

		// 9F41：交易序列计数器------目前无法获取，需与无锡沟通
		pLen = EmvL2Interface.getTagValue((short) 0x9F41, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9F41===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;

		// 82：应用交互特征
		pLen = EmvL2Interface.getTagValue((short) 0x82, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "82===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 84：专用文件名称
		pLen = EmvL2Interface.getTagValue((short) 0x84, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "84===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 95：终端验证结果
		pLen = EmvL2Interface.getTagValue((short) 0x95, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "95===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9A：交易日期
		pLen = EmvL2Interface.getTagValue((short) 0x9A, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9A===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;
		// 9C：交易类型
		pLen = EmvL2Interface.getTagValue((short) 0x9C, ba);
		System.arraycopy(ba, 0, f55, f55Length, pLen);
		Log.i(TAG, "9C===" + StringUtil.toHexString(ba, 0, pLen));
		f55Length += pLen;

		// // DF32：芯片序列号
		// pLen = EmvL2Interface.getTagValue((short) 0xDF32, ba);
		// System.arraycopy(ba, 0, f55, f55Length, pLen);
		// Log.i(TAG, "DF32===" + StringUtil.toHexString(ba, 0, pLen));
		// f55Length += pLen;
		// // DF33：过程密钥数据
		// pLen = EmvL2Interface.getTagValue((short) 0xDF33, ba);
		// System.arraycopy(ba, 0, f55, f55Length, pLen);
		// Log.i(TAG, "DF33===" + StringUtil.toHexString(ba, 0, pLen));
		// f55Length += pLen;
		// // DF34：终端读取时间
		// pLen = EmvL2Interface.getTagValue((short) 0xDF34, ba);
		// System.arraycopy(ba, 0, f55, f55Length, pLen);
		// Log.i(TAG, "DF34===" + StringUtil.toHexString(ba, 0, pLen));
		// f55Length += pLen;
		mEMVICData.setF55Length(f55Length);
		mEMVICData.setF55(f55);
	}

	public void getPAN() {
		byte ba[] = new byte[255];
		int pLen = EmvL2Interface.getTagValue((short) 0x5A, ba);
		String pan = ByteUtil.bcd_2_Str(ba, pLen).substring(4);
		Log.i("icData.PAN", pan);
		if ((pan.endsWith("f") || pan.endsWith("F"))) {
			pan = pan.substring(0, pan.length() - 1);
		}
		mEMVICData.setICPan(pan);
	}

	private void getDataOfExpired() {
		byte ba[] = new byte[255];
		int pLen = EmvL2Interface.getTagValue((short) 0x5F24, ba);
		String dateOfExpired = ByteUtil.bcd_2_Str(ba, pLen).substring(6);
		dateOfExpired = dateOfExpired.substring(0, dateOfExpired.length() - 2);
		Log.i("icData.dateOfExpired", dateOfExpired);
		mEMVICData.setDataOfExpired(dateOfExpired);
	}

	private void getCardSeqNo() {
		byte ba[] = new byte[255];
		int pLen = EmvL2Interface.getTagValue((short) 0x5F34, ba);
		String cardSeqNo = "0" + ByteUtil.bcd_2_Str(ba, pLen).substring(6);
		Log.i("icData.cardSeqNo", cardSeqNo);
		mEMVICData.setCardSeqNo(cardSeqNo);
	}

	public String getTrack2() {
		byte ba[] = new byte[255];
		int pLen = EmvL2Interface.getTagValue((short) 0x57, ba);
		Log.i("tag len", String.valueOf(pLen));

		String track2 = ByteUtil.bcd_2_Str(ba, pLen).substring(4);
		if ((track2.endsWith("f") || track2.endsWith("F"))) {
			track2 = track2.substring(0, track2.length() - 1);
		}
		mEMVICData.setTrack2(track2);
		return track2;
	}

	public interface ICManagerListener {
		public void onRecvICData(JSONObject ICData);
	}
}
