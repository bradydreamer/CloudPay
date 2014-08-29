package cn.koolcloud.pos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class CouponInfo implements Parcelable {
	private String action;
	private String result;
	private String couponCount;
	private String couponAmount;
	private String packagename;
	private String time;
	private String name;
	
	public CouponInfo(String action, String result, String couponCount, String couponAmount,
			String packagename, String time, String name) {
		this.action = action;
		this.result = result;
		this.couponCount = couponCount;
		this.couponAmount = couponAmount;
		this.packagename = packagename;
		this.time = time;
		this.name = name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(action);
		parcel.writeString(result);
		parcel.writeString(couponCount);
		parcel.writeString(couponAmount);
		parcel.writeString(packagename);
		parcel.writeString(time);
		parcel.writeString(name);
	}
	
	public CouponInfo(Parcel source) {
        super();
        this.readFromParcel(source);
    }
	
	public void readFromParcel(Parcel in) {
		action = in.readString();
		result = in.readString();
		couponCount = in.readString();
		couponAmount = in.readString();
		packagename = in.readString();
		time = in.readString();
		name = in.readString();
	}
	
	public static final Parcelable.Creator<CouponInfo> CREATOR = new Parcelable.Creator<CouponInfo>() {
		@Override
		public CouponInfo createFromParcel(Parcel source) {
			return new CouponInfo(source);
		}

		@Override
		public CouponInfo[] newArray(int arg0) {
			return new CouponInfo[arg0];
		}
	};

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(String couponCount) {
		this.couponCount = couponCount;
	}

	public String getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
