package cn.koolcloud.printer.command;

public class FormatSettingCommand {
	public static byte[] getESC2() {
		return new byte[] { 27, 50 };
	}

	public static byte[] getESC3n(byte n) {
		return new byte[] { 27, 51, n };
	}

	public static byte[] getESCan(byte n) {
		return new byte[] { 27, 97, n };
	}

	public static byte[] getESCSO(byte n) {
		return new byte[] { 27, 14, n };
	}

	public static byte[] getESCDC4(byte n) {
		return new byte[] { 27, 20, n };
	}

	public static byte[] getGSLnLnH(byte nL, byte nH) {
		return new byte[] { 29, 60, nL, nH };
	}

	public static byte[] getESC$(byte nL, byte nH) {
		return new byte[] { 27, 36, nL, nH };
	}

	public static byte[] getESBn(byte n) {
		return new byte[] { 27, 66, n };
	}
}
