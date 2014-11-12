package cn.koolcloud.parameter;

public class EMVICData {

	private String ICPan = "";
	private String dataOfExpired = "";
	private String cardSeqNo = "";
	private String track2 = "";
	private byte[] f55 = new byte[255];
	private byte[] pinBlock = new byte[8];
	private int f55Length = 0;

	private static EMVICData emvICData = null;

	private EMVICData() {
	}

	public byte[] getPinBlock() {
		return pinBlock;
	}

	public void setPinBlock(byte[] pinBlock) {
		this.pinBlock = pinBlock;
	}

	public static EMVICData getEMVICInstance() {
		if (emvICData == null) {
			emvICData = new EMVICData();
		}
		return emvICData;
	}

	public String getICPan() {
		return ICPan;
	}

	public void setICPan(String iCPan) {
		ICPan = iCPan;
	}

	public String getDataOfExpired() {
		return dataOfExpired;
	}

	public void setDataOfExpired(String dataOfExpired) {
		this.dataOfExpired = dataOfExpired;
	}

	public String getCardSeqNo() {
		return cardSeqNo;
	}

	public void setCardSeqNo(String cardSeqNo) {
		this.cardSeqNo = cardSeqNo;
	}

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	public byte[] getF55() {
		return f55;
	}

	public void setF55(byte[] f55) {
		System.arraycopy(f55, 0, this.f55, 0, f55Length);
	}

	public int getF55Length() {
		return f55Length;
	}

	public void setF55Length(int f55Length) {
		this.f55Length = f55Length;
	}

}
