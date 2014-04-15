package cn.koolcloud.printer.control;

import android.graphics.Bitmap;
import cn.koolcloud.jni.TimeConstants;
import cn.koolcloud.printer.exception.AccessException;

public abstract interface PrinterControl extends DeviceControl, TimeConstants {
	public static final int BARCODE_UPC_A = 65;
	public static final int BARCODE_UPC_E = 66;
	public static final int BARCODE_JAN13 = 67;
	public static final int BARCODE_JAN8 = 68;
	public static final int BARCODE_CODE39 = 69;
	public static final int BARCODE_ITF = 70;
	public static final int BARCODE_CODABAR = 71;
	public static final int BARCODE_CODE93 = 72;
	public static final int BARCODE_CODE128 = 73;
	
	public abstract void open() throws AccessException;

	public abstract void printText(String paramString) throws AccessException;
	
	public abstract void printText(String paramString,byte fonttype) throws AccessException;
	
	public abstract void printText(String paramString,byte fonttype,byte align) throws AccessException;
	
	public abstract void printText(String paramString,byte fonttype,byte align,byte depth) throws AccessException;

	public abstract void printImage(Bitmap paramBitmap) throws AccessException;

	public abstract void printBarcode(int paramInt, String paramString)
			throws AccessException;

	public abstract int sendESC(byte[] paramArrayOfByte) throws AccessException;
}
