package cn.koolcloud.pos.util;

public class UtilForThread {
	
	private static long mainThreadId;
	
	public static boolean isCurrentInMainThread(Thread currentThread) {
		if (currentThread.getId() == mainThreadId) {
			return true;
		}
		return false;
	}
	
	public static void setMainThreadId(long id) {
		mainThreadId = id;
	}
	
}
