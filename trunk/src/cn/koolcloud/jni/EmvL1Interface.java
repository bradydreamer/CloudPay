package cn.koolcloud.jni;

public class EmvL1Interface {
	static {
		System.loadLibrary("EMV_L1");
	}
	
	/**
	 * EMV L1 test open
	 * @return
	 */
	public native static int emvl1Test_Open();
	
	/**
	 * EMV L1 test close
	 * @return
	 */
	public native static int emvl1Test_Close();
	
	/**
	 * EMV L1 Test Case
	 * @return
	 */
	public native static int emvl1Test_Case(int mode);
}
