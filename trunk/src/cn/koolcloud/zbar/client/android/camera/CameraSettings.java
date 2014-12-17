package cn.koolcloud.zbar.client.android.camera;

public class CameraSettings {
	
	//set true
	static  boolean DISABLE_CONTINUOUS_FOCUS = false;
	//set true
	static  boolean AUTO_FOCUS = true;
	//set false
	static  boolean INVERT_SCAN = false;
	
	static public final int FACING_BACK = 0;
	static public final int FACING_FRONT = 1;
	static public final int FACING_AUTO = 2;
	
	static int CAMERA_FACING = FACING_AUTO;
	
	static boolean BULKMODE = true;

	static boolean BEEP = true;
	
	static boolean ONE_D_FORMATS = true;
	static boolean QR_CODE_FORMATS = true;
	static boolean DATA_MATRIX_FORMATS = true;
	
	
	
	
	public static boolean isONE_D_FORMATS() {
		return ONE_D_FORMATS;
	}

	public static void setONE_D_FORMATS(boolean oNE_D_FORMATS) {
		ONE_D_FORMATS = oNE_D_FORMATS;
	}

	public static boolean isQR_CODE_FORMATS() {
		return QR_CODE_FORMATS;
	}

	public static void setQR_CODE_FORMATS(boolean qR_CODE_FORMATS) {
		QR_CODE_FORMATS = qR_CODE_FORMATS;
	}

	public static boolean isDATA_MATRIX_FORMATS() {
		return DATA_MATRIX_FORMATS;
	}

	public static void setDATA_MATRIX_FORMATS(boolean dATA_MATRIX_FORMATS) {
		DATA_MATRIX_FORMATS = dATA_MATRIX_FORMATS;
	}

	public static boolean isBEEP() {
		return BEEP;
	}

	public static void setBEEP(boolean bEEP) {
		BEEP = bEEP;
	}

	public static boolean isBULKMODE() {
		return BULKMODE;
	}

	public static void setBULKMODE(boolean bULKMODE) {
		BULKMODE = bULKMODE;
	}

	public static void setDISABLE_CONTINUOUS_FOCUS(boolean dISABLE_CONTINUOUS_FOCUS) {
		DISABLE_CONTINUOUS_FOCUS = dISABLE_CONTINUOUS_FOCUS;
	}

	public static boolean isAUTO_FOCUS() {
		return AUTO_FOCUS;
	}
	
	public static void setAUTO_FOCUS(boolean aUTO_FOCUS) {
		AUTO_FOCUS = aUTO_FOCUS;
	}

	public static void setINVERT_SCAN(boolean iNVERT_SCAN) {
		INVERT_SCAN = iNVERT_SCAN;
	}

	public static void setCAMERA_FACING(int cAMERA_FACING) {
		CAMERA_FACING = cAMERA_FACING;
	}
	
	
	
	
}
