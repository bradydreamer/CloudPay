package cn.koolcloud.parameter;

import cn.koolcloud.constant.Constant;
import cn.koolcloud.util.StringUtil;

public class OldTrans implements Constant{
	final boolean GUANGDONG_CUP = true;
	
	private String oldPan;			//主帐号
	
	private int transType;
	
	// 原始交易信息 
	private Integer oldTransAmount;//交易金额
	private Integer oldTrace;		//流水号
	private String oldTransYear; 	// YYYY
    private String oldTransDate; 	// MMDD
    private String oldTransTime;	// 
    private String oldExpiry;		// 卡有效期(Date Of Expired)
    private String oldAcquirerCode;// 受理方标识码(Acquiring Institution Identification Code)
    private String oldRrn;			// 检索参考号(Retrieval Reference Number)
    private String oldAuthCode = "";	// 授权标识应答码(Authorization Identification Response)
    private byte[] oldResponseCode = null;//应答码
    protected String oldAcquirerID;//收单机构标识码
    protected String oldIssuerID;	//接收机构标识码  
    private Integer oldBatch;		//批次号
    private String oldTID;			//
    private String oldMID;			//
    private String koolCloudTID;			//KoolCloud terminal id
    private String koolCloudMID;			//KoolCloud merchant id
    private String oldMertName;				//Merchant Name
    private String paymentName;				//payment Name
    private String paymentId;				//payment ID
    private String alipayPId;				//alipay PID
    private String alipayAccount;			//alipay account
    private String alipayTransactionID;		//alipay Transaction ID

	private byte oldEntryMode;
	private byte oldPinMode;

    private String oldPayOrderBatch = "";	//通联订单批次号。
    private String oldApOrderId = "";		//通联订单号
    private String oldSale_F40_Type;	//交易类型
    private String oldOpenBrh = "";		//机构号
    private String oldCardId = "";		//其他卡号

    private String oldCardOrganization; //国际卡组织: 
    
    private String oper;				//操作员
    

	public String getKoolCloudTID() {
		return koolCloudTID;
	}

	public void setKoolCloudTID(String koolCloudTID) {
		this.koolCloudTID = koolCloudTID;
	}

	public String getKoolCloudMID() {
		return koolCloudMID;
	}

	public void setKoolCloudMID(String koolCloudMID) {
		this.koolCloudMID = koolCloudMID;
	}

	public String getAlipayTransactionID() {
		return alipayTransactionID;
	}

	public void setAlipayTransactionID(String alipayTransactionID) {
		this.alipayTransactionID = alipayTransactionID;
	}

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public String getAlipayPId() {
		return alipayPId;
	}

	public void setAlipayPId(String alipayPId) {
		this.alipayPId = alipayPId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentName() {
		return paymentName;
	}

	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}

	public String getOldMertName() {
		return oldMertName;
	}

	public void setOldMertName(String oldMertName) {
		this.oldMertName = oldMertName;
	}

	public int getTransType() {
		return transType;
	}

	public void setTransType(int transType) {
		this.transType = transType;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getOldCardOrganization() {
		return oldCardOrganization;
	}

	public void setOldCardOrganization(String oldCardOrganization) {
		this.oldCardOrganization = oldCardOrganization;
	}

	public boolean isGUANGDONG_CUP() {
		return GUANGDONG_CUP;
	}

	public String getOldPayOrderBatch() {
		return oldPayOrderBatch;
	}

	public void setOldPayOrderBatch(String oldPayOrderBatch) {
		this.oldPayOrderBatch = oldPayOrderBatch;
	}

	public String getOldApOrderId() {
		return oldApOrderId;
	}

	public void setOldApOrderId(String oldApOrderId) {
		this.oldApOrderId = oldApOrderId;
	}

	public String getOldSale_F40_Type() {
		return oldSale_F40_Type;
	}

	public void setOldSale_F40_Type(String oldSale_F40_Type) {
		this.oldSale_F40_Type = oldSale_F40_Type;
	}

	public String getOldOpenBrh() {
		return oldOpenBrh;
	}

	public void setOldOpenBrh(String oldOpenBrh) {
		this.oldOpenBrh = oldOpenBrh;
	}

	public String getOldCardId() {
		return oldCardId;
	}

	public void setOldCardId(String oldCardId) {
		this.oldCardId = oldCardId;
	}

	public String getOldMID() {
		return oldMID;
	}

	public void setOldMID(String oldMID) {
		this.oldMID = oldMID;
	}

	private String oldProcesscode;
    
	public String getOldProcesscode() {
		return oldProcesscode;
	}

	public void setOldProcesscode(String oldProcesscode) {
		this.oldProcesscode = oldProcesscode;
	}

	public String getOldPan() {
		return oldPan;
	}

	public void setOldPan(String oldPan) {
		this.oldPan = oldPan;
	}


	public String getOldTID() {
		return oldTID;
	}

	public void setOldTID(String oldTID) {
		this.oldTID = oldTID;
	}

	// For Reversal 
    private byte reversalTimes;
    public Integer getOldBatch() {
		return oldBatch;
	}

	public void setOldBatch(Integer oldBatch) {
		this.oldBatch = oldBatch;
	}

	private byte reversalReason;
    
	public byte getReversalTimes() {
		return reversalTimes;
	}

	public void setReversalTimes(byte reversalTimes) {
		this.reversalTimes = reversalTimes;
	}

	public byte getReversalReason() {
		return reversalReason;
	}

	public void setReversalReason(byte reversalReason) {
		this.reversalReason = reversalReason;
	}

	// responseCode
    public byte[] getResponseCode()
    {
    	return oldResponseCode;
    }
    
    public Integer getOldTransAmount() {
		return oldTransAmount;
	}

	public void setOldTransAmount(Integer oldTransAmount) {
		this.oldTransAmount = oldTransAmount;
	}

	public Integer getOldTrace() {
		return oldTrace;
	}

	public void setOldTrace(Integer oldTrace) {
		this.oldTrace = oldTrace;
	}

	public String getOldTransYear() {
		if(oldTransYear == null){
			oldTransYear = "" + UtilFor8583.getInstance().currentYear;
		}
		
		return oldTransYear;
	}

	public void setOldTransYear(String oldTransYear) {
		this.oldTransYear = oldTransYear;
	}

	public String getOldTransDate() {
		
		return oldTransDate;
	}

	public void setOldTransDate(String oldTransDate) {
		this.oldTransDate = oldTransDate;
	}

	public String getOldTransTime() {
		return oldTransTime;
	}

	public void setOldTransTime(String oldTransTime) {
		this.oldTransTime = oldTransTime;
	}

	public String getOldExpiry() {
		return oldExpiry;
	}

	public void setOldExpiry(String oldExpiry) {
		this.oldExpiry = oldExpiry;
	}

	public String getOldAcquirerCode() {
		return oldAcquirerCode;
	}

	public void setOldAcquirerCode(String oldAcquirerCode) {
		this.oldAcquirerCode = oldAcquirerCode;
	}

	public String getOldRrn() {
		return oldRrn;
	}

	public void setOldRrn(String oldRrn) {
		this.oldRrn = oldRrn;
	}

	public String getOldAuthCode() {
		return oldAuthCode;
	}

	public void setOldAuthCode(String oldAuthCode) {
		this.oldAuthCode = oldAuthCode;
	}

	public byte[] getOldResponseCode() {
		return oldResponseCode;
	}

	public void setOldResponseCode(byte[] oldResponseCode) {
		this.oldResponseCode = oldResponseCode;
	}

	public String getOldAcquirerID() {
		return oldAcquirerID;
	}

	public void setOldAcquirerID(String oldAcquirerID) {
		this.oldAcquirerID = oldAcquirerID;
	}

	public String getOldIssuerID() {
		return oldIssuerID;
	}

	public void setOldIssuerID(String oldIssuerID) {
		this.oldIssuerID = oldIssuerID;
	}

	public byte getOldEntryMode() {
		return oldEntryMode;
	}

	public void setOldEntryMode(byte oldEntryMode) {
		this.oldEntryMode = oldEntryMode;
	}

	public byte getOldPinMode() {
		return oldPinMode;
	}

	public void setOldPinMode(byte oldPinMode) {
		this.oldPinMode = oldPinMode;
	}

	public void setResponseCode(byte[] responseCode)
    {
    	if(responseCode != null && responseCode.length == 2)
    	{
    		this.oldResponseCode = new byte[2];
    		System.arraycopy(responseCode, 0, this.oldResponseCode, 0, 2);
    	}
    }
	
	// issuerName
    public String getIssuerName(String issuerID)
    {
    	String issuerName = getBankNameFromID(issuerID);
    	return issuerName;
    }
    public String getAcquirerName(String acquirerID){
    	String oldAcquirerName = getBankNameFromID(acquirerID);
    	return oldAcquirerName;
    }
	
	private String getBankNameFromID(String id)
    {
    	if(id.length()  > 0)
    	{
            String headerID = id.substring(0, 4);
    		if(headerID.equals("0100"))
            {
            	return "邮储银行"; 
            }
            else if(headerID.equals("0102"))
	        {
            	return "工商银行"; 
	        }
            else if(headerID.equals("0103"))
	        {
            	return "农业银行"; 
	        }
            else if(   headerID.equals("0104")
            		|| headerID.equals("6104")
            	   )
	        {
            	return "中国银行"; 
	        }
            else if(headerID.equals("0105"))
	        {
            	return "建设银行"; 
	        }
            else if(headerID.equals("0301"))
	        {
            	return "交通银行"; 
	        }
            else if(headerID.equals("0302"))
	        {
            	return "中信银行"; 
	        }
            else if(headerID.equals("0303"))
	        {
            	return "光大银行"; 
	        }
            else if(headerID.equals("0304"))
	        {
            	return "华夏银行"; 
	        }
            else if(headerID.equals("0305"))
	        {
            	return "民生银行"; 
	        }
            else if(headerID.equals("0306"))
	        {
            	return "广发银行"; 
	        }
            else if(   headerID.equals("0307")
            		|| headerID.equals("0410")
            	   )
	        {
            	return "平安银行"; 
	        }
            else if(   headerID.equals("0308")
            		|| headerID.equals("6308")
            	   )
	        {
            	return "招商银行"; 
	        }
            else if(headerID.equals("0309"))
	        {
            	return "兴业银行"; 
	        }
            else if(headerID.equals("0310"))
	        {
            	return "浦发银行"; 
	        }
            else if(headerID.equals("0320"))
	        {
            	return "东亚银行"; 
	        }
            else{
	        	if(GUANGDONG_CUP)
	        	{
	        		if(   headerID.equals("0413")
	        		   || headerID.equals("6413")
	        		  )
	        		{
	        			return "广州银行";
	        		}
	        		else if(   headerID.equals("0425")
	        				|| headerID.equals("0505")
	        			   )
	        		{
	        			return "东莞银行";
	        		}
	        		else if(headerID.equals("0437"))
	        		{
	        			return "华润银行";
	        		}
	        		else if(  headerID.equals("0489")
	        				||headerID.equals("6489")
	        			   )
	        		{
	        			return "南粤银行";
	        		}
	        		else if(headerID.equals("0495"))
	        		{
	        			return "柳州商行";
	        		}
	        		else if(headerID.equals("0578"))
	        		{
	        			return "华兴银行";
	        		}
	        		else if(  headerID.equals("1405")
	        				||headerID.equals("6505")
	        			   )
	        		{
	        			return "广州农商";
	        		}
	        		else if(headerID.equals("1407"))
	        		{
	        			return "南海农信";
	        		}
	        		else if(  headerID.equals("1408")
	        				||headerID.equals("6508")
	        			   )
	        		{
	        			return "顺德农商";
	        		}
	        		else if(headerID.equals("1415"))
	        		{
	        			return "东莞农商";
	        		}
	        		else if(headerID.equals("1421"))
	        		{
	        			return "三水农信";
	        		}
	        		else if(headerID.equals("1425"))
	        		{
	        			return "新会农商";
	        		}
	        		else if(headerID.equals("1426"))
	        		{
	        			return "肇庆农信";
	        		}
	        		else if(headerID.equals("1427"))
	        		{
	        			return "佛山农信";
	        		}
	        		else if(headerID.equals("1431"))
	        		{
	        			return "珠海农信";
	        		}
	        		else if(headerID.equals("1432"))
	        		{
	        			return "中山农信";
	        		}
	        		else if(headerID.equals("1450"))
	        		{
	        			return "广东农信";
	        		}
	        	}
	        }
    	}
    	return id;
    }
	
	@Override
	public String toString(){
		String msg = "oldPan = " + oldPan +"\n" +
				"oldTransAmount = " + oldTransAmount  +"\n" +
				"oldTrace = " + oldTrace  +"\n" +
				"oldTransYear = " + oldTransYear  +"\n" +
				"oldTransDate = " + oldTransDate  +"\n" +
				"oldTransTime = " + oldTransTime  +"\n" +
				"oldExpiry = " + oldExpiry  +"\n" +
				"oldAcquirerCode = " + oldAcquirerCode  +"\n" +
				"oldRrn = " + oldRrn  +"\n" +
				"oldAuthCode = " + oldAuthCode  +"\n" +
				"oldResponseCode = " + StringUtil.toBestString(oldResponseCode)  +"\n" +
				"oldAcquirerID = " + oldAcquirerID  +"\n" +
				"oldIssuerID = " + oldIssuerID  +"\n" +
				"oldBatch = " + oldBatch  +"\n" +
				"oldTID = " + oldTID  +"\n" +
				"oldMID = " + oldMID;
//		System.out.println(msg);
		return msg;
	}
}
