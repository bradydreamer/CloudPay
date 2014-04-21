package cn.koolcloud.printer.control.impl;

import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.util.Logger;
import cn.koolcloud.printer.command.BarCodeSettingCommand;
import cn.koolcloud.printer.control.PrinterControl;
import cn.koolcloud.printer.exception.AccessException;
import cn.koolcloud.printer.util.ParamChecker;
import cn.koolcloud.printer.util.PrintBitmapNew;

public class PrinterControlImpl implements PrinterControl {
	protected static final String TAG = "PrinterControlImpl";
	boolean isOpened = false;
	
	public void close() throws AccessException {
		Logger.d("printer is closing!");
		if (this.isOpened) {
			int endResult = PrinterInterface.end();
			if (endResult < 0)
				throw new AccessException(2);
			
			int result = PrinterInterface.close();
			if (result < 0)
				throw new AccessException(-1);
		} else {
			throw new AccessException(2);
		}
	}

	@Deprecated
	public void cancelRequest() throws AccessException {
		throw new AccessException(4);
	}

	public void open() throws AccessException {
		Logger.d("isOpened = " + this.isOpened);
		if (!this.isOpened) {
			//open operation
			int result = PrinterInterface.open();
			Logger.d("invoke print_open : result = " + result);
			if (result >= 0) {
				this.isOpened = true;
			} else {
				throw new AccessException(-1);
			}
			
			//set operation
			int setResult = PrinterInterface.set(1);
			/*if (setResult < 0) {
				Logger.d(TAG, "printer set failed");
				Logger.d(TAG, "Master Printer Set failed");
				throw new AccessException(2);
			}*/
			
			//begin operation
			int beginResult = PrinterInterface.begin();
			Logger.d("invoke begin : result = " + beginResult);
			if (beginResult < 0) {
				throw new AccessException(2);
			}
			
		} else {
			throw new AccessException(2);
		}
	}

	public void printText(String message) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] content = null;
		try {
			content = message.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		PrinterInterface.write(content, content.length);
	}

	public void printImage(Bitmap bitmap) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}

		//fix recalling begin bug on wizzar pos on 21 April start
		/*int result = PrinterInterface.begin();
		if (result < 0) {
			throw new AccessException(2);
		}*/
		//fix recalling begin bug on wizzar pos on 21 April end
		//PrinterBitmapUtil.printBitmap(bitmap, 0, 0);
		
		PrintBitmapNew.printBitMap(bitmap);

	}

	public void printBarcode(int format, String barcode) throws AccessException {
		int width = 2;
		int height = 80;
		int marginLeft = 0;
		int position = 48;
		printBarcode(format, barcode, width, height, marginLeft, position);
	}

	public int printBarcode(int format, String barcode, int width, int height,
			int marginLeft, int position) throws AccessException {

		byte[] content = barcode.getBytes();
		int length = content.length;

		boolean isLegal = ParamChecker.checkParamForBarcode(format, content);
		if (!isLegal) {
			throw new IllegalArgumentException(
					"set Param error, please check !");
		}
		//fix recalling begin bug on wizzar pos on 21 April start
		/*int result = PrinterInterface.begin();
		if (result < 0) {
			throw new AccessException(2);
		}*/
		//fix recalling begin bug on wizzar pos on 21 April end
		
		// Setting bar code width 2-6
		byte[] cmds = new byte[0];
		cmds = BarCodeSettingCommand.getGSw((byte) width);
		PrinterInterface.write(cmds, cmds.length);

		// Setting bar code height
		cmds = BarCodeSettingCommand.getGSh((byte) height);
		PrinterInterface.write(cmds, cmds.length);

//		cmds = BarCodeSetting_Command.getGSx((byte) marginLeft);
//		PrinterInterface.write(cmds, cmds.length);
//
//		cmds = BarCodeSetting_Command.getGSH((byte) position);
//		PrinterInterface.write(cmds, cmds.length);

		byte[] header_cmds = BarCodeSettingCommand.getGSk((byte) format, length);
		cmds = new byte[header_cmds.length + content.length];

		System.arraycopy(header_cmds, 0,cmds , 0, header_cmds.length);
		System.arraycopy(content, 0, cmds, header_cmds.length, content.length);
		PrinterInterface.write(cmds, cmds.length);

		return 0;
	}

	public void printQRcode(byte[] content) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		//fix recalling begin bug on wizzar pos on 21 April start
		/*int result = PrinterInterface.begin();
		if (result < 0) {
			throw new AccessException(2);
		}

		result = PrinterInterface.end();
		if (result < 0)
			throw new AccessException(2);*/
		//fix recalling begin bug on wizzar pos on 21 April end
	}

	public int sendESC(byte[] cmds) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}

		PrinterInterface.write(cmds, cmds.length);
		return 0;
	}

	@Override
	public void printText(String paramString, byte fonttype)
			throws AccessException {
		throw new AccessException(-1);
		
	}

	@Override
	public void printText(String paramString, byte fonttype, byte align)
			throws AccessException {
		throw new AccessException(-1);
		
	}

	@Override
	public void printText(String paramString, byte fonttype, byte align,
			byte depth) throws AccessException {
		throw new AccessException(-1);
		
	}
}