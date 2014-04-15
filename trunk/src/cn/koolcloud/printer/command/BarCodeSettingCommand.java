package cn.koolcloud.printer.command;

public class BarCodeSettingCommand {
	public static byte[] getGSH(byte n) {
		return new byte[] { 29, 72, n };
	}

	public static byte[] getGSh(byte n) {
		return new byte[] { 29, 104, n };
	}

	public static byte[] getGSw(byte n) {
		return new byte[] { 29, 119, n };
	}

	public static byte[] getGSk(byte m, int n) {
		byte[] cmds = { 29, 107, m, (byte) n };
		return cmds;
	}
	
	public static byte[] getGSk(byte m) {
		byte[] cmds = { 29, 107, m};
		return cmds;
	}
	
	public static byte[] getGSx(byte n) {
		return new byte[] { 29, 120, n };
	}
}
