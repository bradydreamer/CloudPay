package cn.koolcloud.jni;

public class MisPosEvent {
	public static int iMisposEvent = -1;
	
	/**
	 * Set Mispos Event
	 * @param misposEvent
	 */
	public static void setMisposEvent(int misposEvent) {
		iMisposEvent = misposEvent;
	}
	
	/**
	 * Get Mispos Event
	 * @return
	 */
	public static int getMisposEvent() {
		return iMisposEvent;
	}
}
