package cn.koolcloud.printer.control;

import android.graphics.Bitmap;


public abstract class BasePrinterInterface {
	
	public static final int DOUBLE_NONE = 0x00;
	public static final int DOUBLE_WIDTH = 0x01;
	public static final int DOUBLE_HEIGHT = 0x02;
	public static final int DOUBLE_WIDTH_HEIGHT = 0x03;
	
	public static final int ALIGN_LEFT = 0x00;
	public static final int ALIGN_CENTER = 0x01;
	public static final int ALIGN_RIGHT = 0x02;
	
	public static final int MODE_NORMAL = 0x00;
	public static final int MODE_BOLD = 0x01;
	
	
	abstract String getType();
	
	
	
	abstract boolean isSupportTwoDBarCode();
	
	abstract boolean isSupportOneDBarCode();
	
	
	abstract byte[] setPrinterMode(int mode);
	
	abstract byte[] setFontSize(int size);
	
	abstract byte[] setDoubleSize(int flag);
	
	abstract byte[] setAlign(int align);
	
	abstract byte[] setLineSpacing(int linespace);
	
	abstract byte[] setWordSpacing(int wordspace);
	
	abstract void printBitmap(Bitmap mBitmap,int width,int height);
	
	abstract byte[] printTwoDBarCode(String content);
	
	abstract byte[] printOneDBarCode(String content);
	
}
