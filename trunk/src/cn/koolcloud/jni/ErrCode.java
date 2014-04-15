package cn.koolcloud.jni;

public abstract interface ErrCode {
	public static final int NO_ERROR = 0;
	public static final int BAD_ARGUMENT = -1;
	public static final int BAD_MODE = -2;
	public static final int NO_PENDING_OPERATION = -3;
}
