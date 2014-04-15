package cn.koolcloud.printer.command;

public class CharacterSettingCommand {
	public static byte[] getESCExclamationN(byte n) {
		return new byte[] { 27, 33, n };
	}

	public static byte[] getGSExclamationN(byte n) {
		return new byte[] { 29, 33, n };
	}

	public static byte[] getGSBn(byte n) {
		return new byte[] { 29, 66, n };
	}

	public static byte[] getESCVn(byte n) {
		return new byte[] { 27, 86, n };
	}

	@Deprecated
	public static byte[] getESCvn(byte n) {
		return new byte[] { 27, 118, n };
	}

	public static byte[] getESCGn(byte n) {
		return new byte[] { 27, 71, n };
	}

	public static byte[] getESCEn(byte n) {
		return new byte[] { 27, 69, n };
	}

	public static byte[] getESCSPn(byte n) {
		return new byte[] { 27, 32, n };
	}

	public static byte[] getESCBraceN(byte n) {
		return new byte[] { 27, 123, n };
	}

	public static byte[] getESC_n(byte n) {
		return new byte[] { 27, 45, n };
	}

	public static byte[] getESCPercentN(byte n) {
		return new byte[] { 27, 37, n };
	}

	public static byte[] getFS_And() {
		return new byte[] { 28, 38 };
	}

	public static byte[] getFS() {
		return new byte[] { 28, 46 };
	}

	public static byte[] getFSExclamationN(byte n) {
		return new byte[] { 28, 33, n };
	}

	public static byte[] getESC_And(byte n) {
		return new byte[] { 27, 71, n };
	}

	public static byte[] getESCQuestionN(byte n) {
		return new byte[] { 27, 71, n };
	}

	public static byte[] getESCRn(byte n) {
		return new byte[] { 27, 82, n };
	}

	public static byte[] getESCtn(byte n) {
		return new byte[] { 27, 116, n };
	}
}
