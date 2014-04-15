package cn.koolcloud.printer.control;

public class PrinterSetting {

	static private byte fontType = FontType.NORMAL;
	static private byte fontDepth = Depth.NORMAL;
	static private byte alignMode = Align.LEFT;

	public static byte getFontType() {
		return fontType;
	}

	public static void setFontType(byte fontType) {
		PrinterSetting.fontType = fontType;
	}

	public static byte getFontDepth() {
		return fontDepth;
	}

	public static void setFontDepth(byte fontDepth) {
		PrinterSetting.fontDepth = fontDepth;
	}

	public static byte getAlignMode() {
		return alignMode;
	}

	public static void setAlignMode(byte alignMode) {
		PrinterSetting.alignMode = alignMode;
	}

	public static Boolean checkFontType(byte mfontType) {
		if (mfontType == fontType)
			return false;
		fontType = mfontType;
		return true;
	}

	public static Boolean checkFontDepth(byte mfontDepth) {
		if (mfontDepth == fontDepth)
			return false;
		fontDepth = mfontDepth;
		return true;
	}

	public static Boolean checkAlignMode(byte malignMode) {
		if (malignMode == alignMode)
			return false;
		alignMode = malignMode;
		return true;
	}

}
