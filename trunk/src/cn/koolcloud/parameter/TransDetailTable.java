package cn.koolcloud.parameter;

import cn.koolcloud.util.StringUtil;

public class TransDetailTable {
	private Integer _id;
	private Integer trace;
	private String pan;
	private byte entryMode;
	private byte pinMode;
	private String expiry;
	private byte transType;
	private String apmpTransType;// for APMP
	private String transYear; // YYYY
	private String transDate; // MMDD
	private String transTime;
	private String settlementTime;
	private String authCode;
	private String rrn;
	private String oper;
	private String cardOrganization;
	private String acquirerCode;
	protected String acquirerID;
	protected String issuerID;
	private byte authType;
	private String authBankCode;
	private Long transAmount = null;
	private String transCurrency;
	private Integer tipAmount;
	private String hostText1;
	private String hostText2;
	private String hostText3;
	private String IDNumber; // 订购交易增加 身份证号
	private String cvn; // CVN号
	private String mobileNumber;// 手机号码
	private String rsvNumber; // 预约号
	private String track2Data;
	private String track3Data;
	// 分期
	private Integer installment;
	private String goodsCode;
	private byte feePayType;
	private Integer firstTermAmount;
	private Integer fee;
	private Integer firstTermFee;
	private Integer perTermFee;

	// 积分
	private byte bonusType;
	private Integer bonus = 0; // 兑换积分分数
	private Integer selfPayAmount = 0; // 自付金金额
	private long bonusBalance = 0; // 积分余额
	// EMV Data
	private byte csn;
	private String unpredictableNumber;
	private String tc;
	private String arqc;
	private String tvr;
	private String aid;
	private String tsi;
	private String appLabel;
	private String appName;
	private String aip;
	private String iad;
	private Integer ecBalance;
	private String iccData;
	private String scriptResult;

	public TransDetailTable() {
		init();
	}

	public void init() {
		_id = -1;
		trace = 0;
		pan = "";
		entryMode = 0;
		pinMode = 0x20;
		expiry = "";
		transType = (byte) 12;
		transYear = "";
		transDate = "";
		transTime = "";
		settlementTime = "";
		authCode = "";
		rrn = "";
		oper = "";
		cardOrganization = "";
		acquirerCode = "";
		acquirerID = "";
		issuerID = "";
		authType = -1;
		authBankCode = "";
		transAmount = null;
		tipAmount = 0;
		transCurrency = "156";
		hostText1 = "";
		hostText2 = "";
		hostText3 = "";
		IDNumber = "";
		cvn = "";
		mobileNumber = "";
		rsvNumber = "";
		track2Data = "";
		track3Data = "";
		installment = 0;
		goodsCode = "";
		feePayType = 0;
		firstTermAmount = 0;
		fee = 0;
		firstTermFee = 0;
		perTermFee = 0;

		bonusType = 0;
		bonus = 0; // 兑换积分分数
		selfPayAmount = 0; // 自付金金额
		bonusBalance = 0; // 积分余额
		// EMV Data
		csn = 0;
		unpredictableNumber = "";
		tc = "";
		arqc = "";
		tvr = "";
		aid = "";
		tsi = "";
		appLabel = "";
		appName = "";
		aip = "";
		iad = "";
		ecBalance = -1;
		scriptResult = "";
		iccData = "";
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		this._id = id;
	}

	public Integer getTrace() {
		return trace;
	}

	public void setTrace(Integer trace) {
		this.trace = trace;
	}

	// pan
	public String getPAN() {
		if (pan == null) {
			pan = "";
		}
		return pan;
	}

	public void setPAN(String pan) {
		this.pan = pan;
	}

	// entryMode
	public byte getEntryMode() {
		return entryMode;
	}

	public void setEntryMode(byte entryMode) {
		this.entryMode = entryMode;
	}

	// pinMode
	public byte getPinMode() {
		return pinMode;
	}

	public void setPinMode(byte pinMode) {
		this.pinMode = pinMode;
	}

	// expiry
	public String getExpiry() {
		return expiry;
	}

	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}

	// transType
	public byte getTransType() {
		return transType;
	}

	public void setTransType(byte transType) {
		this.transType = transType;
	}

	// apmpTransType
	public String getApmpTransType() {
		return apmpTransType;
	}

	public void setApmpTransType(String apmpTransType) {
		this.apmpTransType = apmpTransType;
	}

	// transYear
	public String getTransYear() {
		return transYear;
	}

	public void setTransYear(String transYear) {
		this.transYear = transYear;
	}

	// transDate
	public String getTransDate() {
		if (transDate == null || transDate == "") {
			return ("0000");
		} else {
			return transDate;
		}
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	// transTime
	public String getTransTime() {
		if (transTime == null || transTime == "") {
			return "000000";
		} else {
			return transTime;
		}
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(String settlementTime) {
		this.settlementTime = settlementTime;
	}

	// authCode
	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	// rrn
	public String getRRN() {
		return rrn;
	}

	public void setRRN(String rrn) {
		this.rrn = rrn;
	}

	// oper
	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	// cardOrganization
	public String getCardOrganization() {
		return cardOrganization;
	}

	public void setCardOrganization(String cardOrganization) {
		this.cardOrganization = cardOrganization;
	}

	// acquirerCode
	public String getAcquirerCode() {
		return acquirerCode;
	}

	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}

	// acquirerID
	public String getAcquirerID() {
		return acquirerID;
	}

	public void setAcquirerID(String acquirerID) {
		this.acquirerID = acquirerID;
	}

	// issuerID
	public String getIssuerID() {
		return issuerID;
	}

	public void setIssuerID(String issuerID) {
		this.issuerID = issuerID;
	}

	// authType
	public byte getAuthType() {
		return authType;
	}

	public void setAuthType(byte authType) {
		this.authType = authType;
	}

	// authBankCode
	public String getAuthBankCode() {
		return authBankCode;
	}

	public void setAuthBankCode(String authBankCode) {
		this.authBankCode = authBankCode;
	}

	// transAmount
	public Long getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(Long transAmount) {
		this.transAmount = transAmount;
	}

	public String getTransCurrency() {
		return transCurrency;
	}

	public void setTransCurrency(String transCurrency) {
		this.transCurrency = transCurrency;
	}

	// tipAmount
	public Integer getTipAmount() {
		return tipAmount;
	}

	public void setTipAmount(Integer tipAmount) {
		this.tipAmount = tipAmount;
	}

	// hostText
	public String getHostText1() {
		return hostText1;
	}

	public void setHostText1(String hostText) {
		this.hostText1 = hostText;
	}

	public String getHostText2() {
		return hostText2;
	}

	public void setHostText2(String hostText) {
		this.hostText2 = hostText;
	}

	public String getHostText3() {
		return hostText3;
	}

	public void setHostText3(String hostText) {
		this.hostText3 = hostText;
	}

	// IDNumber 订购交易增加 身份证号
	public String getIDNumber() {
		return IDNumber;
	}

	public void setIDNumber(String id) {
		this.IDNumber = id;
	}

	// cvn CVN号
	public String getCVN() {
		return cvn;
	}

	public void setCVN(String cvn) {
		this.cvn = cvn;
	}

	// mobileNumber 手机号码
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	// rsvNumber 预约号
	public String getRSVNumber() {
		return rsvNumber;
	}

	public void setRSVNumber(String rsvNumber) {
		this.rsvNumber = rsvNumber;
	}

	// track2Data
	public String getTrack2Data() {
		return track2Data;
	}

	public void setTrack2Data(String track2Data) {
		if (track2Data != null && track2Data.length() > 0) {
			this.track2Data = track2Data;
		}
	}

	public void setTrack2Data(byte[] track2Data, int offset, int length) {
		if (track2Data != null && track2Data.length > 0
				&& (offset + length) < track2Data.length) {
			byte[] tmpData = new byte[length];
			System.arraycopy(track2Data, offset, tmpData, 0, length);
			this.track2Data = StringUtil.toString(tmpData);
		}
	}

	// track3Data
	public String getTrack3Data() {
		return track3Data;
	}

	public void setTrack3Data(String track3Data) {
		if (track3Data != null && track3Data.length() > 0) {
			this.track3Data = track3Data;
		}
	}

	public void setTrack3Data(byte[] track3Data, int offset, int length) {
		if (track3Data != null && track3Data.length > 0
				&& (offset + length) < track3Data.length) {
			byte[] tmpData = new byte[length];
			System.arraycopy(track3Data, offset, tmpData, 0, length);
			this.track3Data = StringUtil.toString(tmpData);
		}
	}

	// installment
	public Integer getInstallment() {
		return installment;
	}

	public void setInstallment(Integer installment) {
		this.installment = installment;
	}

	// goodsCode
	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	// feepayType
	public byte getFeePayType() {
		return feePayType;
	}

	public void setFeePayType(byte payType) {
		this.feePayType = payType;
	}

	// firstTermAmount
	public Integer getFirstTermAmount() {
		return firstTermAmount;
	}

	public void setFirstTermAmount(Integer amount) {
		this.firstTermAmount = amount;
	}

	// fee
	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

	// firstTermFee
	public Integer getFirstTermFee() {
		return firstTermFee;
	}

	public void setFirstTermFee(Integer fee) {
		this.firstTermFee = fee;
	}

	// perTermFee
	public Integer getPerTermFee() {
		return perTermFee;
	}

	public void setPerTermFee(Integer fee) {
		this.perTermFee = fee;
	}

	// bonusType
	public byte getBonusType() {
		return bonusType;
	}

	public void setBonusType(byte bonusType) {
		this.bonusType = bonusType;
	}

	// bonus 兑换积分分数
	public Integer getBonus() {
		return bonus;
	}

	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}

	// selfPayAmount 自付金金额
	public Integer getSelfPayAmount() {
		return selfPayAmount;
	}

	public void setSelfPayAmount(Integer amount) {
		this.selfPayAmount = amount;
	}

	// bonusBalance 积分余额
	public long getBonusBalance() {
		return bonusBalance;
	}

	public void setBonusBalance(long balance) {
		this.bonusBalance = balance;
	}

	// EMV Data
	// csn
	public byte getCSN() {
		return csn;
	}

	public void setCSN(byte csn) {
		this.csn = csn;
	}

	// unpredictableNumber
	public String getUnpredictableNumber() {
		return unpredictableNumber;
	}

	public void setUnpredictableNumber(String unpredictableNumber) {
		this.unpredictableNumber = unpredictableNumber;
	}

	// tc
	public String getTC() {
		return tc;
	}

	public void setTC(String tc) {
		this.tc = tc;
	}

	// arqc
	public String getARQC() {
		return arqc;
	}

	public void setARQC(String arqc) {
		this.arqc = arqc;
	}

	// tvr
	public String getTVR() {
		return tvr;
	}

	public void setTVR(String tvr) {
		this.tvr = tvr;
	}

	// aid
	public String getAID() {
		return aid;
	}

	public void setAID(String aid) {
		this.aid = aid;
	}

	// tsi
	public String getTSI() {
		return tsi;
	}

	public void setTSI(String tsi) {
		this.tsi = tsi;
	}

	// appLabel
	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appLabel) {
		this.appLabel = appLabel;
	}

	// appName
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	// aip
	public String getAIP() {
		return aip;
	}

	public void setAIP(String aip) {
		this.aip = aip;
	}

	// iad
	public String getIAD() {
		return iad;
	}

	public void setIAD(String iad) {
		this.iad = iad;
	}

	// availableOfflineAmount
	public Integer getECBalance() {
		return ecBalance;
	}

	public void setECBalance(Integer ecBalance) {
		this.ecBalance = ecBalance;
	}

	// scriptResult
	public String getScriptResult() {
		return scriptResult;
	}

	public void setScriptResult(String data) {
		this.scriptResult = data;
	}

	// iccData
	public String getICCData() {
		return iccData;
	}

	public void setICCData(String data) {
		this.iccData = data;
	}

	public void setICCData(byte[] data, int offset, int length) {
		if (data != null && (offset + length) <= data.length) {
			StringUtil.toHexString(data, offset, length, false);
		}
	}

	@Override
	public String toString() {
		/*
		 * return DatabaseOpenHelper.TABLE_TRANS_DETAIL + " [trace=" + trace +
		 * ", pan=" + pan + ", entryMode=" + entryMode + ", pinMode=" + pinMode
		 * + ", expiry=" + expiry + ", transType=" + transType + ", transDate="
		 * + transDate + ", transTime=" + transTime + ", authCode=" + authCode +
		 * ", rrn=" + rrn + ", oper=" + oper + ", cardOrganization=" +
		 * cardOrganization + ", acquirerCode=" + acquirerCode + ", acquirerID="
		 * + acquirerID + ", issuerID=" + issuerID + ", authType=" + authType +
		 * ", authBankCode=" + authBankCode + ", transAmount=" + transAmount +
		 * ", tipAmount=" + tipAmount + ", oldTrace=" + oldTrace + ", oldBatch="
		 * + oldBatch + ", oldTransDate=" + oldTransDate + ", oldRrn=" + oldRrn
		 * + ", oldAuthCode=" + oldAuthCode + ", voidFlag=" + voidFlag +
		 * ", adjustFlag=" + adjustFlag + ", uploadFlag=" + uploadFlag + "]";
		 */
		return "test";
	}
}
