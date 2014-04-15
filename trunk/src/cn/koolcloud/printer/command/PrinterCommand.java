package cn.koolcloud.printer.command;

public class PrinterCommand {
	public static byte[] getHt() {
		return new byte[] { 9 };
	}

	public static byte[] getLinefeed() {
		return new byte[] { 10 };
	}

	public static byte[] getCR() {
		return new byte[] { 9 };
	}

	public static byte[] getESCDn(byte nk) {
		byte[] cmds = { 27, 68, nk };
		return cmds;
	}

	public static byte[] getESCJn(byte n) {
		return new byte[] { 27, 74, n };
	}

	public static byte[] getESCdn(byte n) {
		return new byte[] { 27, 100, n };
	}

	public static byte[] getESCEqualsN(byte n) {
		return new byte[] { 27, 61, n };
	}
	
	public static byte[] getESCAN(byte n) {
		return new byte[] { 27, 97, n };
	}
}
