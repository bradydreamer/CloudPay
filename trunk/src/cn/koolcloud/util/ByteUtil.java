package cn.koolcloud.util;

public class ByteUtil {
	public ByteUtil() {
	}

	/**
	 * 
	 * Method Concatenates the specified byte[].
	 * 
	 * @param number
	 *            The int value to be converted.
	 * 
	 */
	public static byte[] concatByteArray(byte[] a, byte[] b) {
		int aL = a.length;
		int bL = b.length;
		int len = aL + bL;
		byte[] c = new byte[len];

		System.arraycopy(a, 0, c, 0, aL);
		System.arraycopy(b, 0, c, aL, bL);

		return c;
	}

	public static void asciiToBCD(byte[] ascii_buf, int asc_offset,
			byte[] bcd_buf, int bcd_offset, int conv_len, int type) {
		int cnt;
		byte ch, ch1;
		int bcdOffset = bcd_offset;
		int asciiOffset = asc_offset;

		if (((conv_len & 0x01) > 0) && (type > 0)) {
			ch1 = 0;
		} else {
			ch1 = 0x55;
		}
		for (cnt = 0; cnt < conv_len; asciiOffset++, cnt++) {
			if (ascii_buf[asciiOffset] >= 97) // 97 = 'a'
			{
				ch = (byte) (ascii_buf[asciiOffset] - 97 + 10); // 97 = 'a'
			} else {
				if (ascii_buf[asciiOffset] >= 65) // 65 = 'A'
				{
					ch = (byte) ((ascii_buf[asciiOffset]) - 65 + 10); // 65 =
																		// 'A'
				} else {
					if (ascii_buf[asciiOffset] >= 48) // 48 = '0'
					{
						ch = (byte) ((ascii_buf[asciiOffset]) - 48); // 48 = '0'
					} else {
						ch = 0;
					}
				}
			}
			if (ch1 == 0x55) {
				ch1 = ch;
			} else {
				// *bcd_buf++=ch1<<4 | ch;
				bcd_buf[bcdOffset++] = (byte) ((ch1 << 4) | ch);
				ch1 = 0x55;
			}
		}
		if (ch1 != 0x55) {
			bcd_buf[bcdOffset] = (byte) (ch1 << 4);
		}
	}

	public static byte[] bcdToAscii(byte[] bcdByte) {
		byte[] returnByte = new byte[bcdByte.length * 2];
		byte value;
		for (int i = 0; i < bcdByte.length; i++) {
			value = (byte) (bcdByte[i] >> 4 & 0xF);
			if (value > 9) {
				returnByte[i * 2] = (byte) (value + (byte) 0x37);
			} else {
				returnByte[i * 2] = (byte) (value + (byte) 0x30);
			}
			value = (byte) (bcdByte[i] & 0xF);
			if (value > 9) {
				returnByte[i * 2 + 1] = (byte) (value + (byte) 0x37);
			} else {
				returnByte[i * 2 + 1] = (byte) (value + (byte) 0x30);
			}
		}
		return returnByte;
	}

	public static byte[] bcdToAscii(byte[] bcdByte, int offset, int length) {
		byte[] returnByte = new byte[length * 2];
		byte value;
		for (int i = offset; i < length; i++) {
			value = (byte) (bcdByte[i] >> 4 & 0xF);
			if (value > 9) {
				returnByte[i * 2] = (byte) (value + (byte) 0x37);
			} else {
				returnByte[i * 2] = (byte) (value + (byte) 0x30);
			}
			value = (byte) (bcdByte[i] & 0xF);
			if (value > 9) {
				returnByte[i * 2 + 1] = (byte) (value + (byte) 0x37);
			} else {
				returnByte[i * 2 + 1] = (byte) (value + (byte) 0x30);
			}
		}
		return returnByte;
	}

	public static void bcdToAscii(byte[] bcd_buf, int offset, byte[] ascii_buf,
			int asc_offset, int conv_len, int type) {
		int cnt;
		int bcdOffset = offset;
		int asciiOffset = asc_offset;
		if (conv_len > (bcd_buf.length * 2)) {
			conv_len = (bcd_buf.length * 2);
		}
		if (((conv_len & 0x01) > 0) && (type > 0)) {
			cnt = 1;
			conv_len++;
		} else {
			cnt = 0;
		}
		for (; cnt < conv_len; cnt++, asciiOffset++) {
			ascii_buf[asciiOffset] = (byte) (((cnt & 0x01) > 0) ? (bcd_buf[bcdOffset++] & 0x0f)
					: ((bcd_buf[bcdOffset] & 0xFF) >>> 4));
			ascii_buf[asciiOffset] += (byte) ((ascii_buf[asciiOffset] > 9) ? (65 - 10)
					: 48); // 65 = 'A' 48 = '0'
		}
	}

	public static int bcdToInt(byte[] bcdByte) {
		return NumberUtil.parseInt(bcdToAscii(bcdByte), 0, 10, false);
	}

	public static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	public static String bcd_2_Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	public static String asc_to_str(byte[] bytes) {
		String result = "";
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		result = bcd_2_Str(bcd);
		return result;
	}
	/**
	 * Compares this byte arrary to the specified object. The result is
	 * <code>true</code> if and only if the argument is not <code>null</code>
	 * and is a <code>String</code> object that represents the same sequence of
	 * characters as this object.
	 * 
	 * @param src
	 *            the first byte array
	 * @param tag
	 *            the second byte array against.
	 * @return <code>true</code> if the <code>String </code>are equal;
	 *         <code>false</code> otherwise.
	 */
	public static boolean equalByteArray(byte[] src, byte[] dst) {
		return equalByteArray(src, 0, src.length, dst, 0, dst.length);
	}

	public static boolean equalByteArray(byte[] src, int srcOffset, int srcLen,
			byte[] dst, int dstOffset, int dstLen) {
		if (compareByteArray(src, srcOffset, srcLen, dst, dstOffset, dstLen) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static int compareByteArray(byte[] src, byte[] dst) {
		return compareByteArray(src, 0, src.length, dst, 0, dst.length);
	}

	public static int compareByteArray(byte[] src, int srcOffset, byte[] dst,
			int dstOffset, int length) {
		return compareByteArray(src, srcOffset, length, dst, dstOffset, length);
	}

	/**
	 * Compares two byte array lexicographically..
	 * 
	 * @param src
	 *            the byte array
	 * @param srcOffset
	 *            the start position of the first byte array
	 * @param dst
	 *            the byte array
	 * @param dstOffset
	 *            the start position of the second byte array
	 * @return the value <code>0</code> if the argument string is equal to this
	 *         string; a value less than <code>0</code> if this string is
	 *         lexicographically less than the string argument; and a value
	 *         greater than <code>0</code> if this string is lexicographically
	 *         greater than the string argument.
	 */
	public static int compareByteArray(byte[] src, int srcOffset, int srcLen,
			byte[] dst, int dstOffset, int dstLen) {
		char c1, c2;
		if (src == null || srcOffset < 0 || srcLen < 0) {
			return Integer.MIN_VALUE;
		}
		if (dst == null || dstOffset < 0 || dstLen < 0) {
			return Integer.MIN_VALUE;
		}
		int n = Math.min(srcLen, dstLen);
		if ((srcOffset + n) > src.length || (dstOffset + n) > dst.length) {
			return Integer.MIN_VALUE;
		}
		for (int i = 0; i < n; i++) {
			// compare the byte
			c1 = (char) (src[srcOffset + i] & 0xFF);
			c2 = (char) (dst[dstOffset + i] & 0xFF);
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return srcLen - dstLen;
	}

	public static void arraycopy(String src, int srcOffset, byte[] dst,
			int dstOffset, int length) {
		if (src == null || dst == null) {
			throw new NullPointerException("invalid byte array ");
		}
		if ((src.length() < (srcOffset + length))
				|| (dst.length < (dstOffset + length))) {
			throw new IndexOutOfBoundsException("invalid length: " + length);
		}
		for (int i = 0; i < length; i++) {
			dst[dstOffset + i] = (byte) src.charAt(srcOffset + i);
		}
	}
}
