package cn.koolcloud.printer.control;

import cn.koolcloud.jni.ErrCode;
import cn.koolcloud.printer.exception.AccessException;


public abstract interface DeviceControl extends ErrCode {
	public static final String APP_TAG = "Devices";

	public abstract void close() throws AccessException;

	public abstract void cancelRequest() throws AccessException;
}
