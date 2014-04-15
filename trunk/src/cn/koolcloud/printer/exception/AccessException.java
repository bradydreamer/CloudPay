package cn.koolcloud.printer.exception;

public class AccessException extends Exception {
	private static final long serialVersionUID = 4066743061223798736L;
	private int code;
	public static final int DEVICE_OPEN_ERROR = -1;
	public static final int BAD_CONTROL_MODE = 2;
	public static final int REQUEST_PENDING = 3;
	public static final int NO_REQUEST_PENDING = 4;
	public static final int LISTENER_ERROR = 5;
	public static final int ACCESS_DENIED = 6;

	public AccessException(int noPendingOperation) {
		this.code = noPendingOperation;
	}

	public final int getCode() {
		return this.code;
	}
}