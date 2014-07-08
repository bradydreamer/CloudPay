package cn.koolcloud.util;

public class NumberUtil {
	public NumberUtil() {
	}

	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };
	final static char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
			'2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
			'3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
			'4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
			'6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
			'7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
			'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
			'9', };
	final static char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', };

	public final static byte[] BCD_Digit = { (byte) 0x00, (byte) 0x01,
			(byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
			(byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11,
			(byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16,
			(byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x20, (byte) 0x21,
			(byte) 0x22, (byte) 0x23, (byte) 0x24, (byte) 0x25, (byte) 0x26,
			(byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x30, (byte) 0x31,
			(byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36,
			(byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x40, (byte) 0x41,
			(byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
			(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x50, (byte) 0x51,
			(byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56,
			(byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x60, (byte) 0x61,
			(byte) 0x62, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66,
			(byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x70, (byte) 0x71,
			(byte) 0x72, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76,
			(byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x80, (byte) 0x81,
			(byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86,
			(byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x90, (byte) 0x91,
			(byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
			(byte) 0x97, (byte) 0x98, (byte) 0x99, };

	public static int getIntegerLength(int i) {
		return IntegerToBytes(i, null, 0, true, true, true);
	}

	public static byte[] intToBcd(int number, int length) {
		byte[] buf = new byte[length];
		buf = StringUtil.hexString2bytes(StringUtil.fillString("" + number,
				length * 2, '0', true));

		return buf;

	}

	public static int IntegerToBytes(int i, byte[] dst, int offset) {
		return IntegerToBytes(i, dst, offset, true, true, false);
	}

	/*
	 * Converts unicodes to encoded &#92;uxxxx and writes out any of the
	 * characters in specialSaveChars with a preceding slash
	 * 
	 * @param i the int needing convert.
	 * 
	 * @param dst Save of the result
	 * 
	 * @param offset the offset of result
	 * 
	 * @param minusFlag if <code>false</code>, skip the minus
	 * 
	 * @param lengthFlag Whether add one byte of length in the result.
	 * <code>true</code> add one byte of length in the result
	 * 
	 * @param getLengthFlag Calculate the length of result, if
	 * <code>true</code>, theString length that return.
	 * 
	 * @return if getLengthFlag = false, return offset of the result. if
	 * getLengthFlag = true, the length of the sequence of characters
	 * represented by this object.
	 */
	public static int IntegerToBytes(int i, byte[] dst, int offset,
			boolean minusFlag, boolean lengthFlag, boolean getLengthFlag) {
		if (!getLengthFlag
				&& (dst == null || dst.length < offset || dst.length < 1 || offset < 0))
			return -1;

		int length;
		byte[] buf;
		int charPos = 12;

		if (i == Integer.MIN_VALUE) {
			if (minusFlag)
				buf = "-2147483648".getBytes();
			else
				buf = "2147483648".getBytes();

			length = buf.length;
			charPos = 0;
		} else {
			int q, r;
			buf = new byte[12];
			char sign = 0;

			if (i < 0) {
				sign = '-';
				i = -i;
			}

			// Generate two digits per iteration
			while (i >= 65536) {
				q = i / 100;
				// really: r = i - (q * 100);
				r = i - ((q << 6) + (q << 5) + (q << 2));
				i = q;
				buf[--charPos] = (byte) DigitOnes[r];
				buf[--charPos] = (byte) DigitTens[r];
			}

			// Fall thru to fast mode for smaller numbers
			// assert(i <= 65536, i);
			for (;;) {
				q = (i * 52429) >>> (16 + 3);
				r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
				buf[--charPos] = (byte) digits[r];
				i = q;
				if (i == 0)
					break;
			}
			if (sign != 0 && minusFlag) {
				buf[--charPos] = (byte) sign;
			}
			length = 12 - charPos;
		}

		if (!getLengthFlag) {
			if (dst != null
					&& dst.length >= offset + length + (lengthFlag ? 1 : 0)) {
				if (lengthFlag) {
					dst[offset] = (byte) length;
					offset++;
				}
				System.arraycopy(buf, charPos, dst, offset, length);
				length = offset + length;
			} else {
				length = -1;
			}
		} else {
			if (lengthFlag)
				length = length + 1;
		}

		buf = null;
		return length;
	}

	/**
	 * Parses the string argument as a signed integer in the radix specified by
	 * the second argument. The characters in the string must all be digits of
	 * the specified radix (as determined by whether
	 * {@link java.lang.Character#digit(char, int)} returns a nonnegative
	 * value), except that the first character may be an ASCII minus sign
	 * <code>'-'</code> (<code>'&#92;u002D'</code>) to indicate a negative
	 * value. The resulting integer value is returned.
	 * <p>
	 * An exception of type <code>NumberFormatException</code> is thrown if any
	 * of the following situations occurs:
	 * <ul>
	 * <li>The first argument is <code>null</code> or is a string of length
	 * zero.
	 * <li>The radix is either smaller than
	 * {@link java.lang.Character#MIN_RADIX} or larger than
	 * {@link java.lang.Character#MAX_RADIX}.
	 * <li>Any character of the string is not a digit of the specified radix,
	 * except that the first character may be a minus sign <code>'-'</code> (
	 * <code>'&#92;u002D'</code>) provided that the string is longer than length
	 * 1.
	 * <li>The value represented by the string is not a value of type
	 * <code>int</code>.
	 * </ul>
	 * <p>
	 * Examples: <blockquote>
	 * 
	 * <pre>
	 * parseInt("0", 10) returns 0
	 * parseInt("473", 10) returns 473
	 * parseInt("-0", 10) returns 0
	 * parseInt("-FF", 16) returns -255
	 * parseInt("1100110", 2) returns 102
	 * parseInt("2147483647", 10) returns 2147483647
	 * parseInt("-2147483648", 10) returns -2147483648
	 * parseInt("2147483648", 10) throws a NumberFormatException
	 * parseInt("99", 8) throws a NumberFormatException
	 * parseInt("Kona", 10) throws a NumberFormatException
	 * parseInt("Kona", 27) returns 411787
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param src
	 *            the <code>String</code> containing the integer representation
	 *            to be parsed
	 * @param radix
	 *            the radix to be used while parsing <code>s</code>.
	 * @return the integer represented by the string argument in the specified
	 *         radix.
	 * @exception NumberFormatException
	 *                if the <code>String</code> does not contain a parsable
	 *                <code>int</code>.
	 */
	public static int parseInt(byte[] s, int offset, int radix,
			boolean lengthFlag) throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException("null");
		}

		if (radix < Character.MIN_RADIX) {
			throw new NumberFormatException("radix " + radix
					+ " less than Character.MIN_RADIX");
		}

		if (radix > Character.MAX_RADIX) {
			throw new NumberFormatException("radix " + radix
					+ " greater than Character.MAX_RADIX");
		}

		int result = 0;
		boolean negative = false;
		int i = 0, max = (s.length - offset);
		int limit;
		int multmin;
		int digit;

		if (lengthFlag) {
			max = s[offset];
			offset++;
		}

		if (max > 0) {
			if (s[offset + 0] == (byte) '-') {
				negative = true;
				limit = Integer.MIN_VALUE;
				i++;
			} else {
				limit = -Integer.MAX_VALUE;
			}
			multmin = limit / radix;
			if (i < max) {
				digit = Character.digit((char) s[offset + (i++)], radix);
				if (digit < 0) {
					throw new NumberFormatException(
							"Byte array contains non-digit");
				} else {
					result = -digit;
				}
			}
			while (i < max) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = Character.digit((char) s[offset + (i++)], radix);
				if (digit < 0) {
					throw new NumberFormatException(
							"Byte array contains non-digit");
				}
				if (result < multmin) {
					throw new NumberFormatException(
							"Byte array contains non-digit");
				}
				result *= radix;
				if (result < limit + digit) {
					throw new NumberFormatException(
							"Byte array contains non-digit");
				}
				result -= digit;
			}
		} else {
			throw new NumberFormatException("Byte array contains non-digit");
		}
		if (negative) {
			if (i > 1) {
				return result;
			} else { /* Only got "-" */
				throw new NumberFormatException("Byte array contains non-digit");
			}
		} else {
			return -result;
		}
	}

	public static int parseInt(byte[] s, int offset)
			throws NumberFormatException {
		return parseInt(s, offset, 10, true);
	}

	public static byte[] shortToByte2(short number) {
		int tmp_num = number;
		byte[] byte2 = new byte[2];

		for (int i = byte2.length - 1; i > -1; i--) {
			byte2[i] = new Integer(tmp_num & 0xff).byteValue();
			tmp_num = tmp_num >> 8;
		}

		return byte2;
	}

	/**
	 * 
	 * Method converting short into byte array.
	 * 
	 * @param number
	 *            The int value to be converted.
	 * 
	 */
	public static byte[] shortToByte2(String num) {
		return shortToByte2((short) (Integer.parseInt(num, 10)));
	}

	public static short byte2ToShort(byte[] src) {
		return byte2ToShort(src, 0);
	}

	public static short byte2ToShort(byte[] src, int offset) {
		if (null == src || offset < 0 || offset > src.length)
			throw new NullPointerException("invalid byte array ");
		if ((src.length - offset) < 2)
			throw new IndexOutOfBoundsException("invalid len: " + src.length);
		short number = (short) ((src[offset + 0] & 0xff) << 8 | (src[offset + 1] & 0xff));
		return number;
	}

	/**
	 * Method converting binary String into int.
	 * 
	 * @param The
	 *            binary String to be converted.
	 * 
	 */
	public static int BinStringToInt(String binaryString) {
		return (Integer.valueOf(binaryString, 2).intValue());
	}

	/**
	 * Method converting byte array into int.
	 * 
	 * @param The
	 *            byte array to be converted.
	 * 
	 */
	public static int byte4ToInt(byte[] src) {
		if (src == null)
			throw new NullPointerException("invalid byte array ");
		if (src.length < 4)
			throw new IndexOutOfBoundsException("invalid len: " + src.length);

		int number = ((((src[0] & 0xff) << 8 | (src[1] & 0xff)) << 8) | (src[2] & 0xff)) << 8
				| (src[3] & 0xff);

		return number;
	}

	/**
	 * Method converting byte array into long.
	 * 
	 * @param The
	 *            byte array to be converted.
	 * 
	 */
	public static int byte8ToInt(byte[] byte8) {
		int number;
		number = (byte8[0] & 0xff);
		for (int i = 1; i < byte8.length; i++) {
			number = number << 8;
			number = number | (byte8[i] & 0xff);
		}

		return number;
	}

	public static long byte8ToLong(byte[] byte8) {
		long number;
		number = (byte8[0] & 0xff);
		for (int i = 1; i < byte8.length; i++) {
			number = number << 8;
			number = number | (byte8[i] & 0xff);
		}

		return number;
	}

	/**
	 * 
	 * Method converting long into byte array.
	 * 
	 * @param number
	 *            The int value to be converted.
	 * 
	 */
	public static byte[] longToByte8(long value) {
		long tmp_num = value;
		byte[] byte8 = new byte[8];

		for (int i = byte8.length - 1; i > -1; i--) {
			byte8[i] = (byte) (tmp_num & 0xff);
			tmp_num = tmp_num >> 8;
		}
		return byte8;
	}

	public static byte[] intToByte8(int value) {
		int tmp_num = value;
		byte[] byte8 = new byte[8];

		for (int i = byte8.length - 1; i > -1; i--) {
			byte8[i] = (byte) (tmp_num & 0xff);
			tmp_num = tmp_num >> 8;
		}
		return byte8;
	}

	public static int longToString(long number, byte[] buf, int offset,
			int length, boolean bcdFlag) {
		if (buf == null || offset < 0 || length < 1
				|| buf.length < (offset + length)) {
			return -1;
		}
		length = offset + length;
		int charPos = length - 1;
		int numberLength = charPos;
		boolean numberLengthFlag = true;
		int index = 0;
		int radix = (bcdFlag ? 100 : 10);

		if (number > 0) {
			number = -number;
		}
		for (; charPos >= offset; charPos--) {
			if (0 != number) {
				index = (int) (-(number % radix));
				number = number / radix;
			} else {
				index = 0;
			}
			if (numberLengthFlag && 0 == number) {
				numberLengthFlag = !numberLengthFlag;
				numberLength = length - charPos;
			}
			if (bcdFlag) {
				buf[charPos] = BCD_Digit[index];
			} else {
				buf[charPos] = (byte) digits[index];
			}
		}
		return numberLength;
	}

	public static byte[] longToString(long number, boolean bcdFlag,
			int bufferSize) {
		int size = (bufferSize > 0 ? bufferSize : 20);
		byte[] buffer = new byte[size];
		int length = longToString(number, buffer, 0, size, bcdFlag);
		if (bufferSize > 0) {
			return buffer;
		}
		byte[] buf = new byte[length];
		System.arraycopy(buffer, size - length, buf, 0, length);
		buffer = null;
		return buf;
	}

	public static int longToAscii(long number, byte[] buf, int offset,
			int length) {
		return longToString(number, buf, offset, length, false);
	}

	public static byte[] longToAscii(long number, int bufferSize) {
		return longToString(number, false, bufferSize);
	}

	public static byte[] longToAscii(long number) {
		return longToString(number, false, -1);
	}

	public static int longToBCD(long number, byte[] buf, int offset, int length) {
		return longToString(number, buf, offset, length, true);
	}

	public static int longToBCD(long number, byte[] buf) {
		if (buf == null) {
			return -1;
		}
		return longToString(number, buf, 0, buf.length, true);
	}

	public static byte[] longToBCD(long number) {
		return longToString(number, true, -1);
	}

	/**
	 * 
	 * Method converting int into byte array.
	 * 
	 * @param number
	 *            The int value to be converted.
	 * 
	 */
	public static byte[] intToByte4(int number) {
		int tmp_num = number;
		byte[] byte4 = new byte[4];

		for (int i = byte4.length - 1; i > -1; i--) {
			byte4[i] = (byte) (tmp_num & 0xff);
			tmp_num = tmp_num >> 8;
		}
		return byte4;
	}

	/**
	 * 
	 * Method converting int into byte array.
	 * 
	 * @param number
	 *            The int value to be converted. 低位在前
	 * 
	 */
	public static byte[] intToByte4LowFirst(int number) {
		int tmp_num = number;
		byte[] byte4 = new byte[4];

		for (int i = 0; i < byte4.length; i++) {
			byte4[i] = (byte) (tmp_num & 0xff);
			tmp_num = tmp_num >> 8;
		}
		return byte4;
	}
}
