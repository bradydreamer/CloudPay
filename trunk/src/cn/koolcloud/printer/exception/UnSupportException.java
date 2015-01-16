package cn.koolcloud.printer.exception;

public class UnSupportException extends Exception {
	private static final long serialVersionUID = 4066743061223798737L;
	private int code;
	
	public static final int ARGFORMATERROR = -1;
	public static final int UNSUPPORT = -2;
	
	public UnSupportException(int noPendingOperation) {
		this.code = noPendingOperation;
	}

	public final int getCode() {
		return this.code;
	}
}