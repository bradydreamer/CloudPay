package cn.koolcloud.parameter;

import java.util.Calendar;
import java.util.Locale;

import cn.koolcloud.constant.Constant;

public class UtilFor8583 implements Constant {
	private static UtilFor8583 instance = null;

	public static UtilFor8583 getInstance() {
		if (instance == null)
			instance = new UtilFor8583();

		instance.getCurrentDateTime();
		return instance;

	}

	private UtilFor8583() {

	}

	public String apOrderId = ""; // 通联订单号 F40 6F10
	public String payOrderBatch = ""; // 批次号 F40 6F08
	public String sale_F40_Type = ""; // 业务类型 F40 6F13
	public byte[] payPwd; // 支付密码 F40 6F02
	public byte[] msgPwd; // 短信验证码 F40 6F11
	public String signature; // 签名 F40 6F12
	public String paymentId = ""; // 支付活动号 F60.6
	public String isSendCode = ""; // 是否短信交易 F40 6F14
	public String openBrh = ""; // 卡券机构号 F40 6F20
	public String cardId; // 其他类型卡号 F40 6F21
	public String alipayPID = ""; // 支付宝 PID F40 6F22
	public String alipayAccount = ""; // 支付宝账户 F40 6F26
	public String alipayTransactionID = "";// 支付宝交易号 F40 6F27

	public TransDetailInfo trans = new TransDetailInfo();

	public OldTrans oldTrans;

	private Calendar mCalendar;
	private String currentOperatorID = "01";// 当前操作员
	public TerminalConfig terminalConfig = new TerminalConfig();

	public int currentYear;
	public int currentMonth;
	public int currentDay;
	public int currentHour;
	public int currentMinute;
	public int currentSecond;

	// 重要参数
	public byte[] PIK = null;
	public byte[] PIKCheck = null;
	public byte[] MAK = null;
	public byte[] MAKCheck = null;
	public byte[] TDK = null;
	public byte[] TDKCheck = null;

	private byte processType = 0; // 处理阶段

	// processType
	public byte getProcessType() {
		return processType;
	}

	// currentOperatorID
	public String getCurrentOperatorID() {
		return currentOperatorID;
	}

	public void getCurrentDateTime() {
		long time = System.currentTimeMillis();
		/* 透过Calendar对象来取得小时与分钟 */
		mCalendar = Calendar.getInstance(Locale.CHINA);
		mCalendar.setTimeInMillis(time);
		currentYear = mCalendar.get(Calendar.YEAR);
		currentMonth = mCalendar.get(Calendar.MONTH) + 1;
		currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		currentHour = mCalendar.get(Calendar.HOUR);
		if (mCalendar.get(Calendar.AM_PM) == Calendar.PM) {
			currentHour += 12;
		}
		currentMinute = mCalendar.get(Calendar.MINUTE);
		currentSecond = mCalendar.get(Calendar.SECOND);
	}

	public void setYear(int year) {
		mCalendar.set(Calendar.YEAR, year);
		long when = mCalendar.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			// SystemClock.setCurrentTimeMillis(when);TODO
		}
		getCurrentDateTime();
	}

	public void setDateTime(int month, int day, int hour, int minute, int second) {
		mCalendar.set(Calendar.MONTH, month - 1);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		mCalendar.set(Calendar.HOUR, hour);
		mCalendar.set(Calendar.MINUTE, minute);
		mCalendar.set(Calendar.SECOND, second);
		long when = mCalendar.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE) {
			// SystemClock.setCurrentTimeMillis(when);TODO
		}
		getCurrentDateTime();
	}

}
