package cn.koolcloud.pos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentInfo implements Parcelable {
	private String paymentId;
	private String paymentName;
	private String brhKeyIndex;
	private String prdtNo;
	private String prdtTitle;
	private String prdtDesc;
	private String openBrh;
	private String openBrhName;
	
	public PaymentInfo(String paymentId, String paymentName, String brhKeyIndex,
			String prodtNo, String prdtTitle, String prdtDesc, String openBrh, String openBrhName) {
		this.paymentId = paymentId;
		this.brhKeyIndex = brhKeyIndex;
		this.paymentName = paymentName;
		this.prdtNo = prodtNo;
		this.prdtTitle = prdtTitle;
		this.prdtDesc = prdtDesc;
		this.openBrh = openBrh;
		this.openBrhName = openBrhName;
	}

	public PaymentInfo(Parcel source) {
        super();
        this.readFromParcel(source);
    }
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(paymentId);
		parcel.writeString(brhKeyIndex);
		parcel.writeString(paymentName);
		parcel.writeString(prdtNo);
		parcel.writeString(prdtTitle);
		parcel.writeString(prdtDesc);
		parcel.writeString(openBrh);
		parcel.writeString(openBrhName);
	}
	
	public void readFromParcel(Parcel in) {
		paymentId = in.readString();
		brhKeyIndex = in.readString();
		paymentName = in.readString();
		prdtNo = in.readString();
		prdtTitle = in.readString();
		prdtDesc = in.readString();
		openBrh = in.readString();
		openBrhName = in.readString();
	}
	
	public String getOpenBrh() {
		return openBrh;
	}

	public void setOpenBrh(String openBrh) {
		this.openBrh = openBrh;
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

	public String getBrhKeyIndex() {
		return brhKeyIndex;
	}

	public void setBrhKeyIndex(String brhKeyIndex) {
		this.brhKeyIndex = brhKeyIndex;
	}

	public String getPrdtNo() {
		return prdtNo;
	}

	public void setPrdtNo(String prdtNo) {
		this.prdtNo = prdtNo;
	}

	public String getPrdtTitle() {
		return prdtTitle;
	}

	public void setPrdtTitle(String prdtTitle) {
		this.prdtTitle = prdtTitle;
	}

	public String getPrdtDesc() {
		return prdtDesc;
	}

	public void setPrdtDesc(String prdtDesc) {
		this.prdtDesc = prdtDesc;
	}

	public String getOpenBrhName() {
		return openBrhName;
	}

	public void setOpenBrhName(String openBrhName) {
		this.openBrhName = openBrhName;
	}

	public static final Parcelable.Creator<PaymentInfo> CREATOR = new Parcelable.Creator<PaymentInfo>() {  
        public PaymentInfo createFromParcel(Parcel source) {  
            return new PaymentInfo(source);  
        }  
 
        public PaymentInfo[] newArray(int size) {  
            return new PaymentInfo[size];  
        }  
    };
}
