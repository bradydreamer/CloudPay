package cn.koolcloud.jni;

public class HsmObject {

	public static final int HSM_OBJECT_DATA_TYPE_pem = 0;
	public static final int HSM_OBJECT_DATA_TYPE_der = 1;
	public static final int HSM_OBJECT_DATA_TYPE_p7b = 2;
	public static final int HSM_OBJECT_DATA_TYPE_pfx = 3;
	
	public static final int HSM_OBJECT_TYPE_private_key = 0;
	public static final int HSM_OBJECT_TYPE_public_key = 1;
	public static final int HSM_OBJECT_TYPE_cert = 2;
	
	public String mId;
	public String mLabel;
	public String mPassword;
	public int mType;
	
	public HsmObject() {

	}

	public HsmObject(String mId, String mLabel, String mPassword, int mType) {
		super();
		this.mId = mId;
		this.mLabel = mLabel;
		this.mPassword = mPassword;
		this.mType = mType;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmLabel() {
		return mLabel;
	}

	public void setmLabel(String mLabel) {
		this.mLabel = mLabel;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public int getmType() {
		return mType;
	}

	public void setmType(int mType) {
		this.mType = mType;
	}
	
}
