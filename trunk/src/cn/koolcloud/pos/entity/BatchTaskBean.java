package cn.koolcloud.pos.entity;

public class BatchTaskBean {
	
	/*write back data*/
	private String txnId;
	private String refrenceRetrievalNumber;
	private String authCode;
	private String responseCode;
	private String responseMsg;
	private String issuerId;
	private String expDate;				//format YYMM
	private String settlementDate;		//settlement date format MMDD
	
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getRefrenceRetrievalNumber() {
		return refrenceRetrievalNumber;
	}
	public void setRefrenceRetrievalNumber(String refrenceRetrievalNumber) {
		this.refrenceRetrievalNumber = refrenceRetrievalNumber;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public String getIssuerId() {
		return issuerId;
	}
	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}
	public String getExpDate() {
		return expDate;
	}
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	public String getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}
}
