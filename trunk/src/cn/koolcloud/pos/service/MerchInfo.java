package cn.koolcloud.pos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class MerchInfo implements Parcelable{
	private String merchId;
	private String terminalId;
	private String userTypeName;
	private String userType;
	
	public MerchInfo(String merchId, String terminalId) {
		this.merchId = merchId;
		this.terminalId = terminalId;
	}

	public MerchInfo(Parcel source) {
        super();
        this.readFromParcel(source);
    }
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(merchId);
		parcel.writeString(terminalId);
		parcel.writeString(userTypeName);
		parcel.writeString(userType);
	}
	
	public void readFromParcel(Parcel in) {
		merchId = in.readString();
		terminalId = in.readString();
		userTypeName = in.readString();
		userType = in.readString();
	}
	
	public String getMerchId() {
		return merchId;
	}

	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public static final Parcelable.Creator<MerchInfo> CREATOR = new Parcelable.Creator<MerchInfo>() {  
        public MerchInfo createFromParcel(Parcel source) {  
            return new MerchInfo(source);  
        }  
 
        public MerchInfo[] newArray(int size) {  
            return new MerchInfo[size];  
        }  
    };
}
