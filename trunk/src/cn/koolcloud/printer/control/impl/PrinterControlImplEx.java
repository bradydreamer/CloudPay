package cn.koolcloud.printer.control.impl;

import cn.koolcloud.printer.command.CharacterSettingCommand;
import cn.koolcloud.printer.command.FormatSettingCommand;
import cn.koolcloud.printer.control.Align;
import cn.koolcloud.printer.control.Depth;
import cn.koolcloud.printer.control.FontType;
import cn.koolcloud.printer.control.PrinterSetting;
import cn.koolcloud.printer.exception.AccessException;


public class PrinterControlImplEx extends PrinterControlImpl {

	protected static final String TAG = "PrinterControlImplEx";

	public void printText(String paramString, byte fonttype)
			throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		printText(paramString, fonttype, Align.LEFT, Depth.NORMAL);
	}

	public void printText(String paramString, byte fonttype, byte align)
			throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		printText(paramString, fonttype, align, Depth.NORMAL);
	}

	public void printText(String paramString, byte fonttype, byte align,
			byte depth) throws AccessException {
		if (!this.isOpened) {
			throw new AccessException(2);
		}
		if (PrinterSetting.checkFontType(fonttype)) {
			sendESC(CharacterSettingCommand.getGSExclamationN(fonttype));
		}
		if (PrinterSetting.checkAlignMode(align)) {
			sendESC(FormatSettingCommand.getESCan(align));
		}
		if (PrinterSetting.checkFontDepth(depth)) {
			sendESC(CharacterSettingCommand.getESCEn(depth));
		}

		printText(paramString);

		if (PrinterSetting.checkFontType(FontType.NORMAL)) {
			sendESC(CharacterSettingCommand.getGSExclamationN(FontType.NORMAL));
		}
		if (PrinterSetting.checkAlignMode(Align.LEFT)) {
			sendESC(FormatSettingCommand.getESCan(Align.LEFT));
		}
		if (PrinterSetting.checkFontDepth(Depth.NORMAL)) {
			sendESC(CharacterSettingCommand.getESCEn(Depth.NORMAL));
		}
	}
}
