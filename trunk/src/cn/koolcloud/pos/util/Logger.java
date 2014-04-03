package cn.koolcloud.pos.util;

import android.util.Log;
import cn.koolcloud.BuildingConfig;

/**
 * <p>Title: Logger.java </p>
 * <p>Description: Simple Logger class it funds on Android native logger but permits also some extra features .
 * Two level of filters and systematically print class name when this is not static</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2014-3-27
 * @version 	
 */
public class Logger {
	
	static String className;
	static String methodName;
	static int lineNumber;

	private Logger() {
		/* Protect from instantiations */
	}

	public static boolean isDebuggable() {
		return BuildingConfig.DEBUG;
	}

	private static String createLog(String log) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}

	private static void getMethodNames(StackTraceElement[] sElements) {
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message) {
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void i(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}

	public static void d(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}

	public static void v(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}

	public static void w(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}

	public static void wtf(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}
	
}
