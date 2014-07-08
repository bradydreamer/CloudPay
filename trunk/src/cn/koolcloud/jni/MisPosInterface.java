package cn.koolcloud.jni;

public class MisPosInterface {
	static {
		System.loadLibrary("koolcloud_mispos");
	}
	
	public native static int communicationOpen();
	public native static int communicationClose();
	public native static int communicationTest();
	public native static int serialPoll(int nTimeout);
	public native static int serialCancelPoll();
	public native static int registration(int transType);
	public native static int unregistration(int transType);
	public native static int consume(int transType, String amount);
	public native static int consumeRevoke(int transType, String amount, String voucher);
	public native static int returnGoods(int transType, String amount);
	public native static int preAuthorization(int transType, String amount);
	public native static int preAuthorizationRevoke(int transType, String amount, String voucher);
	public native static int preAuthorizationComplete(int transType, String amount);
	public native static int getBalance(int transType);
	public native static int sendMessage(byte[] sendData, int sendDataLen);
	public native static int recvMessage(byte[] recvData);
	public native static int getTagValue(int tag, byte[] value);
}
