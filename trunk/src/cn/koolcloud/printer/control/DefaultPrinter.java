package cn.koolcloud.printer.control;

import android.graphics.Bitmap;

public class DefaultPrinter extends BasePrinterInterface {

	@Override
	boolean isSupportTwoDBarCode() {
		
		return false;
	}

	@Override
	boolean isSupportOneDBarCode() {
		
		return false;
	}

	@Override
	String getType() {
		
		return null;
	}

	@Override
	byte[] setFontSize(int size) {
		
		return null;
	}

	@Override
	byte[] setDoubleSize(int flag) {
		
		return null;
	}

	@Override
	byte[] setAlign(int align) {
		
		return null;
	}

	@Override
	byte[] setLineSpacing(int linespace) {
		
		return null;
	}

	@Override
	byte[] setWordSpacing(int wordspace) {
		
		return null;
	}

	@Override
	void printBitmap(Bitmap mBitmap, int width, int height) {
		

	}

	@Override
	byte[] printTwoDBarCode(String content) {
		
		return null;
	}

	@Override
	byte[] printOneDBarCode(String content) {
		
		return null;
	}

	@Override
	byte[] setPrinterMode(int mode) {
		
		return null;
	}
	
}
