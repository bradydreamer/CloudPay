/* @(#)CodeUtil.java   
 *
 * Project: TLSecurity
 *
 * Modify Information:
 * =============================================================================
 *   Author       Date       Description
 *   ------------ ---------- ---------------------------------------------------
 *   zhouyijia    2010-07-15   create
 * Copyright Notice:
 * =============================================================================
 *    Copyright © 2008 allinpay. All. Reserver
 * Warning:
 * =============================================================================
 * 
 */
package cn.koolcloud.security.util;

import java.security.SecureRandom;
import java.util.Random;

public class CodeUtil {
	private CodeUtil() {
	}

	/**
	 * 
	 * @param aIndex
	 * @return
	 */
	public static String formatIndex(int aIndex) {
		String tString = null;
		if (aIndex >= 0 && aIndex < 10) {
			tString = "00" + new Integer(aIndex).toString();
		} else if (aIndex >= 10 && aIndex < 100) {
			tString = "0" + new Integer(aIndex).toString();
		} else if (aIndex >= 100 && aIndex <= 1000) {
			tString = new Integer(aIndex).toString();
		} else {
			return null;
		}
		return tString;
	}

	/**
	 * 
	 * @param aLen
	 * @return
	 */
	public static String formatLength(int aLen) {
		String tString = null;
		if (aLen >= 0 && aLen < 10) {
			tString = "000" + new Integer(aLen).toString();
		} else if (aLen >= 10 && aLen < 100) {
			tString = "00" + new Integer(aLen).toString();
		} else if (aLen >= 100 && aLen <= 1000) {
			tString = "0" + new Integer(aLen).toString();
		} else if (aLen >= 1000 && aLen <= 10000) {
			tString = new Integer(aLen).toString();
		} else {
			return null;
		}
		return tString;
	}

	public static int formatDataLen(byte[] aByte) {
		String tStr = new String(aByte);
		for (; tStr.charAt(0) != '0';) {
			tStr = tStr.substring(1, tStr.length());
		}
		return Integer.parseInt(tStr);
	}

	public static byte[] formatData(byte[] aByte) {
		byte[] tByte = new byte[128];
		System.arraycopy(aByte, 0, tByte, 0, aByte.length);
		return tByte;
	}

	/**
	 * 互联网密码十六进制密文转为byte[]数组
	 * 
	 * @param aByte
	 * @return
	 */
	public static byte[] byte2byte(byte[] aByte) {
		String tStr = "";
		byte[] tByte = new byte[24];
		int tIndex = 0;
		for (int i = 0; i < aByte.length; i++) {
			tStr = tStr + Character.toString((char) aByte[i]);
		}
		for (int m = 0; m < (tStr.length() - 1);) {
			String temp = tStr.substring(m, m + 2);
			tByte[tIndex] = (byte) Integer.parseInt(temp, 16);
			m = m + 2;
			tIndex = tIndex + 1;
		}
		return tByte;
	}

	/**
	 * 将十六进制表示的字符串转换为字符串代表的byte数组
	 * 
	 * @param aHex
	 *            十六进制字符串
	 * @return byte数组
	 */
	public static byte[] hex2Bytes(byte[] in) {
		byte[] out = new byte[in.length / 2];
		byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		byte[] temp = new byte[in.length];

		for (int i = 0; i < in.length; i++) {
			if (in[i] >= 0x30 && in[i] <= 0x39) {
				temp[i] = (byte) (in[i] - 0x30);
			} else if (in[i] >= 0x41 && in[i] <= 0x46) {
				temp[i] = asciiCode[in[i] - 0x41];
			} else if (in[i] >= 0x61 && in[i] <= 0x66) {
				temp[i] = asciiCode[in[i] - 0x61];
			}
		}

		for (int i = 0; i < in.length / 2; i++) {
			out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
		}

		return out;
	}

	/**
	 * Byte数组转十六进制字符串，字节间用空格分隔
	 * 
	 * @param aBytes
	 * @return
	 */
	public static String byte2hex(byte[] aBytes) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < aBytes.length; n++) {
			stmp = (java.lang.Integer.toHexString(aBytes[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
			if (n < aBytes.length - 1) {
				hs = hs + " ";
			}
		}

		return hs.toUpperCase();
	}

	/**
	 * Byte数组转十六进制字符串字节间不用空格分隔
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] hexString2Byte(String src) {
		if (src.length() % 2 != 0) {
			src = src + "0";
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < (src.length() / 2); i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * hex转byte.
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] hex2byte(byte[] b, int offset, int len) {
		byte[] d = new byte[len];
		for (int i = 0; i < len * 2; i++) {
			int shift = i % 2 == 1 ? 0 : 4;
			d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
		}
		return d;
	}

	public static int byte2Int(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = bRefArr.length - 1, x = 0; i >= 0; i--, x++) {
			bLoop = bRefArr[x];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	/**
	 * 生成随机的HEX字符串.
	 * 
	 * @param HEX字符串的字节数
	 * @return
	 */
	public static String randomHexStr(int len) {
		Random ran = new Random();
		int num = 0;
		String stmp = "";
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			while (true) {
				num = ran.nextInt(256);
				stmp = java.lang.Integer.toHexString(num & 0xFF);
				if (stmp.length() == 1) {
					stmp = "0" + stmp;
				}
				strb.append(stmp);
				break;
			}
		}
		return strb.toString();
	}

	/**
	 * 两字节异或
	 * 
	 * @param byte1
	 * @param byte2
	 * @return
	 */
	public static byte byteXOR(byte byte1, byte byte2) {
		return (byte) (byte1 ^ byte2);
	}

	/**
	 * 两字节数组异或
	 * 
	 * @param byte1
	 * @param byte2
	 * @return
	 */
	public static byte[] btyeArrayXOR(byte[] byte1, byte[] byte2) {
		byte[] reBype = new byte[byte1.length];
		for (int i = 0, j = byte1.length; i < j; i++) {
			reBype[i] = byteXOR(byte1[i], byte2[i]);
		}
		return reBype;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 数字字符串转换为bcd,右靠，左补0
	 * 
	 * @param value
	 * @param buf
	 */
	public static void toBcd_right(String value, byte[] buf) {

		int charpos = 0; // char where we start
		int bufpos = 0;
		int tmp = buf.length - (value.length() / 2 + value.length() % 2);
		for (int i = 0; i < tmp; i++) {
			value = "00" + value;
		}

		if (value.length() % 2 == 1) {
			// for odd lengths we encode just the first digit in the first byte
			buf[0] = (byte) (value.charAt(0) - 48);
			charpos = 1;
			bufpos = 1;
		}
		// encode the rest of the string
		while (charpos < value.length()) {
			buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) | (value
					.charAt(charpos + 1) - 48));
			charpos += 2;
			bufpos++;
		}

	}

	/**
	 * 数字字符串转换为bcd,左靠，右补0
	 * 
	 * @param value
	 * @param buf
	 */
	public static void toBcd_left(String value, byte[] buf) {

		if (value.length() % 2 != 0) {
			value = value + "0";
		}

		byte[] tmp = value.getBytes();
		for (int i = 0; i < (value.length() / 2); i++) {
			buf[i] = CodeUtil.uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return;
	}

	/**
	 * 对传入的Byte数组进行PKCS#1填充
	 * 
	 * @param aBytesText
	 *            欲进行PKCS#1填充的Byte数组
	 * @param aBlockSize
	 *            区块大小
	 * @return 经过PKCS#1填充后的Byte数组，大小等于传入的区块大小。<br>
	 *         若传入的Byte数组长度超过(填充区块大小-3)时无法进行填充作业，将回传null。
	 */
	public static byte[] addPKCS1Padding(byte[] aBytesText, int aBlockSize) {
		if (aBytesText.length > (aBlockSize - 3)) {
			// 传入的Byte数组长度超过(填充区块大小-3)
			return null;
		}
		SecureRandom tRandom = new SecureRandom();
		byte[] tAfterPaddingBytes = new byte[aBlockSize];
		tRandom.nextBytes(tAfterPaddingBytes);
		tAfterPaddingBytes[0] = 0x00;
		tAfterPaddingBytes[1] = 0x02;
		int i = 2;
		for (; i < aBlockSize - 1 - aBytesText.length; i++) {
			if (tAfterPaddingBytes[i] == 0x00) {
				tAfterPaddingBytes[i] = (byte) tRandom.nextInt();
			}
		}
		tAfterPaddingBytes[i] = 0x00;
		System.arraycopy(aBytesText, 0, tAfterPaddingBytes, (i + 1),
				aBytesText.length);

		return tAfterPaddingBytes;
	}

	/**
	 * pin--->pinBlock，不加主帐号
	 * 
	 * @param aPin
	 * @return
	 * @throws KcSecurityException
	 */

	/**
	 * 
	 * @param aPan
	 * @return
	 * @throws KcSecurityException
	 */

	/**
	 * 返回CUPS标准的固定长度数字格式（右对齐前补零）
	 * 
	 * @param aLen
	 *            长度
	 * @param aString
	 * @return
	 */
	public static byte[] getNNumberString(int aLen, int aValue) {
		String tString = String.valueOf(aValue);
		int tStringLen = tString.length();
		StringBuffer tStringBuffer = new StringBuffer();

		for (int i = 0; i < aLen - tStringLen; i++)
			tStringBuffer.append("0");

		tStringBuffer.append(tString);

		return tStringBuffer.toString().getBytes();
	}
}
