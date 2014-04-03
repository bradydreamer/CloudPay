package cn.koolcloud.jni;

/** STRONGLY RECOMMENDED: to implement resource control in Native Layer to avoid resource conflict between 
 *  two Java applications simultaneously invoke this device functionality. 
 */

/**
* Permission explicit declaration
* android.permission.KOOLCLOUD_MSR
*/

public class MsrInterface
{

	static
	{
		/* Driver implementation, so file shall put under /system/lib */
		System.loadLibrary("koolcloudPos");
		System.loadLibrary("koolcloud_msr");
	}

	/* native methods as following */		

	/**
	 * open the device
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code, -1 : device doesn't exist, -10 : unknown error
	 */
    public native static int open();

	/**
     * close the device
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code, -102 : I/O error, -100 : unknown error
	 */
    public native static int close();
	
    /**
	 * poll the msr information within period time
     * @param nTimeout_MS : time out in milliseconds.
	 * 					    if nTimeout_MS is less then zero, the searching process is infinite.
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code
	 */
    public native static int poll(int nTimout);
	
    /**
	 * cancel the polling msr information
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code
	 */	
    public native static int cancelPoll();
	
	/**
     * get track error
     * @param nTrackIndex : track index[0.1,2] 0 : 1st track, 1 : 2nd track, 2 : 3rd track
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code, -101 : input error, -102 : I/O error, -100 : unknown error
	 */
    public native static int getTrackError(int nTrackIndex);
	
	/**
     * get length of track data
     * @param nTrackIndex : track index[0.1,2] 0 : 1st track, 1 : 2nd track, 2 : 3rd track
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code, -101 : input error, -102 : I/O error, -100 : unknown error
	 */
    public native static int getTrackDataLength(int nTrackIndex);
	
	/**
     * get track data.
     * @param nTrackIndex : track index[0.1,2] 0 : 1st track, 1 : 2nd track, 2 : 3rd track
     * @param byteArry : track data
     * @param nLength : length of track data
	 * return value >= 0 : success (suggest 0)
	 *               < 0 : error code, -101 : input error, -102 : I/O error, -100 : unknown error
	 */
    public native static int getTrackData(int nTrackIndex,byte[] byteArry,int nLength);

}
