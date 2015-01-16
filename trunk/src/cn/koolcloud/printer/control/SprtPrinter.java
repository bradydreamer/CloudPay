package cn.koolcloud.printer.control;

import android.graphics.Bitmap;

import cn.koolcloud.printer.command.CharacterSettingCommand;
import cn.koolcloud.printer.command.FormatSettingCommand;
import cn.koolcloud.printer.command.PrinterCommand;
import cn.koolcloud.printer.util.PrintBitmapSprt;

public class SprtPrinter extends DefaultPrinter {

	@Override
	String getType() {
		return "SP-POS58IVU";
	}

	@Override
	byte[] setPrinterMode(int mode) {
		/**
		 * CharacterSetting_Command.getESCEn(byte n) { 27, 69, n }
		 */
		byte cmdflag;
		final byte NORMAL = 0x00;
		final byte BOLD = 0x01;

		if ((mode & 0x01) == 0) {
			cmdflag = NORMAL;
		} else {
			cmdflag = BOLD;
		}
		return CharacterSettingCommand.getESCEn(cmdflag);
	}

	@Override
	byte[] setDoubleSize(int flag) {

		/**
		 * CharacterSetting_Command.getGSExclamationN(byte n) { 29, 33, n }
		 */
		byte cmdflag;
		final byte NORMAL = 0x00;
		final byte DOUBLE_W = 0x10;
		final byte DOUBLE_H = 0x01;
		final byte DOUBLE_WH = 0x11;
		switch (flag) {
		case DOUBLE_NONE:
			cmdflag = NORMAL;
			break;
		case DOUBLE_WIDTH:
			cmdflag = DOUBLE_W;
			break;
		case DOUBLE_HEIGHT:
			cmdflag = DOUBLE_H;
			break;
		case DOUBLE_WIDTH_HEIGHT:
			cmdflag = DOUBLE_WH;
			break;
		default:
			cmdflag = NORMAL;
			break;
		}

		return CharacterSettingCommand.getGSExclamationN(cmdflag);
	}

	@Override
	byte[] setAlign(int align) {

		/**
		 * CharacterSetting_Command.getESCEn(byte n) { 27, 69, n }
		 */
		byte cmdflag;
		final byte LEFT = 0x30;
		final byte CENTER = 0x31;
		final byte RIGHT = 0x32;

		switch (align) {
		case ALIGN_LEFT:
			cmdflag = LEFT;
			break;
		case ALIGN_CENTER:
			cmdflag = CENTER;
			break;
		case ALIGN_RIGHT:
			cmdflag = RIGHT;
			break;
		default:
			cmdflag = LEFT;
			break;
		}
		return FormatSettingCommand.getESCan(cmdflag);
	}

	@Override
	byte[] setLineSpacing(int linespace) {
		return FormatSettingCommand.getESC3n((byte) linespace);
	}

	@Override
	void printBitmap(Bitmap mBitmap, int width, int height) {
		PrintBitmapSprt.printBitMap(mBitmap);
	}

	@Override
	byte[] printOneDBarCode(String content) {

		return null;
	}

    @Override
    byte[] setFontSize(int size) {
        return PrinterCommand.setFontEnlarge(size);
    }
}
