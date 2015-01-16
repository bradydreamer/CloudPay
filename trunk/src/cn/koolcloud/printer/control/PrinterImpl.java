package cn.koolcloud.printer.control;

import java.io.UnsupportedEncodingException;

import cn.koolcloud.jni.PrinterInterface;

import cn.koolcloud.printer.exception.AccessException;
import cn.koolcloud.printer.exception.UnSupportException;
import cn.koolcloud.printer.util.CodeBitmap;

import android.graphics.Bitmap;

public class PrinterImpl {

	private BasePrinterInterface mBasePrinterInterface = null;

	private boolean isOpened = false;

	/**
	 * @param type
	 */
	public PrinterImpl(String type) {

		if (PrinterList.mPrinterMap.containsKey(type)) {
			mBasePrinterInterface = PrinterList.mPrinterMap.get(type);
		} else {
			mBasePrinterInterface = new SprtPrinter();
		}
	}

	public PrinterImpl() {
		this("");
	}

	public void open() throws AccessException {
		if (!this.isOpened) {
			int result = PrinterInterface.open();
			if (result >= 0) {
				this.isOpened = true;
			} else {
				throw new AccessException(-1);
			}
			// int setResult = PrinterInterface.set(1);
			int beginResult = PrinterInterface.begin();
			if (beginResult < 0) {
				throw new AccessException(2);
			}
		} else {
			throw new AccessException(2);
		}
	}

	public void close() throws AccessException {
		if (this.isOpened) {
			int endResult = PrinterInterface.end();
			if (endResult < 0)
				throw new AccessException(2);

			int result = PrinterInterface.close();
			this.isOpened = false;
			if (result < 0)
				throw new AccessException(-1);
		} else {
			throw new AccessException(2);
		}
	}

	public void set(int flag) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		PrinterInterface.set(flag);
	}

	/**
	 * @param linespace
	 *            设置行间距
	 */
	public void setLineSpacing(int linespace) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setLineSpacing(linespace);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param wordspace
	 *            设置字间距
	 */
	public void setWordSpacing(int wordspace) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setWordSpacing(wordspace);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param format
	 *            设置字体 普通，倍宽，倍高，倍宽+倍高
	 */
	public void setDoubleFormat(int format) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setDoubleSize(format);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param align
	 *            设置对齐，居左，剧中，居右
	 */
	public void setAlign(int align) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setAlign(align);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param mode
	 *            设置额外字体 黑体，斜体，下划线，反白。。。
	 */
	public void setPrinterMode(int mode) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setPrinterMode(mode);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param size
	 *            设置字体大小
	 */
	public void setFontSize(int size) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = mBasePrinterInterface.setFontSize(size);
		if (cmd != null)
			PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param content
	 *            打印字符串，gb2312 行缓冲，\n打印并换行
	 */
	public void printText(String content) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		byte[] cmd = null;
		try {
			cmd = content.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		PrinterInterface.write(cmd, cmd.length);
	}

	/**
	 * @param mBitmap
	 *            打印位图,图片的宽度，高度最好小于390 如果有出现乱码，可以换一个长度试一下
	 */
	public void printerBitmap(Bitmap mBitmap) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		mBasePrinterInterface.printBitmap(mBitmap, mBitmap.getWidth(),
				mBitmap.getHeight());

	}

	/**
	 * @param content
	 *            打印二维码,图片的宽度，高度最好小于390 如果有出现乱码，可以换一个长度试一下
	 */
	public void printTwoDBarCode(String content, int width, int height)
			throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}

		if (mBasePrinterInterface.isSupportTwoDBarCode()) {
			byte[] cmd = mBasePrinterInterface.printTwoDBarCode(content);
			if (cmd != null)
				PrinterInterface.write(cmd, cmd.length);
		} else {
			Bitmap mBitmap = CodeBitmap.createQrBitmap(content, width, height);
			mBasePrinterInterface.printBitmap(mBitmap, width, height);
		}
	}

	/**
	 * @param content
	 *            打印一维吗,图片的宽度，高度最好小于390 如果有出现乱码，可以换一个长度试一下
	 * @throws cn.koolcloud.printer.exception.UnSupportException
	 */
	public void printOneDBarCode(String content, int width, int height)
			throws AccessException, UnSupportException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}

		if (mBasePrinterInterface.isSupportOneDBarCode()) {
			byte[] cmd = mBasePrinterInterface.printOneDBarCode(content);
			if (cmd != null)
				PrinterInterface.write(cmd, cmd.length);
		} else {
			if (content.length() > 18)
				throw new UnSupportException(-2);
			Bitmap mBitmap = CodeBitmap.create128Bitmap(content, width, height);
			mBasePrinterInterface.printBitmap(mBitmap, width, height);
		}
	}

}
