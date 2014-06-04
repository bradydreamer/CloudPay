package cn.koolcloud.util;

public class AppUtil {
	public AppUtil() {
	}

	public static boolean checkDateYYMM(String expireDate) {
		if (expireDate != null && expireDate.length() == 4) {
			int month = Integer.parseInt(expireDate.substring(2));
			if (month >= 1 && month <= 12) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	public static boolean checkDateMMDD(String expireDate) {
		if (expireDate != null && expireDate.length() == 4) {
			int month = Integer.parseInt(expireDate.substring(0, 2));
			int day = Integer.parseInt(expireDate.substring(2));
			if (month >= 1 && month <= 12) {
				if (day >= 1 && day <= 31) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else
			return false;
	}

	/**
	 * 
	 * Method Check whether IP address is legal.
	 * 
	 * @param IPString
	 *            The value to be checked.
	 * 
	 */
	public static boolean checkIPWithDot(String IPString) {
		if (IPString != null && IPString.length() >= 7) {
			int ip[] = { 0, 0, 0, 0 };
			byte[] arrayIP = IPString.getBytes();
			int dotNumber = 0;
			if (arrayIP[0] == '.' || arrayIP[arrayIP.length - 1] == '.') {
				return false;
			}
			for (int i = 0; i < arrayIP.length; i++) {
				if (arrayIP[i] == '.' && arrayIP[i + 1] == '.')
					return false;
				if (arrayIP[i] == '.') {
					dotNumber++;
				} else if (arrayIP[i] >= 0x30 && arrayIP[i] <= 0x39) {
					ip[dotNumber] = ip[dotNumber] * 10 + (arrayIP[i] - 0x30);
				} else {
					return false;
				}
			}
			if (dotNumber != 3)
				return false;
			if (ip[0] < 1 || ip[0] >= 224 || ip[0] == 127)
				return false;
			if (ip[1] < 0 || ip[1] > 255)
				return false;
			if (ip[2] < 0 || ip[2] > 255)
				return false;
			if (ip[3] < 1 || ip[3] > 254)
				return false;
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * Method Check whether IP address is legal.
	 * 
	 * @param IPString
	 *            The value to be checked.
	 * 
	 */
	public static boolean checkIP(String IPString) {
		if (IPString != null && IPString.length() == 12) {
			int ip = Integer.parseInt(IPString.substring(0, 3));
			if (ip < 1 || ip >= 224 || ip == 127)
				return false;
			ip = Integer.parseInt(IPString.substring(3, 6));
			if (ip < 0 || ip > 255)
				return false;
			ip = Integer.parseInt(IPString.substring(6, 9));
			if (ip < 0 || ip > 255)
				return false;
			ip = Integer.parseInt(IPString.substring(9, 12));
			if (ip < 1 || ip > 254)
				return false;

			return true;
		} else
			return false;
	}

	/**
	 * 
	 * Method Check whether PhoneNumber is legal.
	 * 
	 * @param PhoneString
	 *            The value to be checked.
	 * 
	 */
	public static boolean checkPhoneNumber(String PhoneString) {
		// TODO
		return true;
	}

	/**
	 * 
	 * Method Check whether time is legal.
	 * 
	 * @param timeData
	 *            The int value to be checked.
	 * 
	 */
	public static boolean checkTime(String timeData) {
		if (timeData != null && timeData.length() == 4) {
			int hh = Integer.parseInt(timeData.substring(0, 2));
			int mm = Integer.parseInt(timeData.substring(2, 4));

			if ((hh >= 0 && hh <= 23) && (mm >= 0 && mm <= 59))
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * Protects PAN, Track2, CVC (suitable for logs).
	 * 
	 * <pre>
	 * "40000101010001" is converted to "400001____0001"
	 * "40000101010001=020128375" is converted to "400001____0001=0201_____"
	 * "123" is converted to "___"
	 * </pre>
	 * 
	 * @param s
	 *            string to be protected
	 * @return 'protected' String
	 */
	public static String cardProtect(String s) {
		StringBuffer sb = new StringBuffer();
		int len = s.length();
		int clear = len > 6 ? 6 : 0;
		int lastFourIndex = -1;
		if (clear > 0) {
			lastFourIndex = s.indexOf('=') - 4;
			if (lastFourIndex < 0) {
				lastFourIndex = s.indexOf('^') - 4;
				if (lastFourIndex < 0) {
					lastFourIndex = len - 4;
				}
			}
		}
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) == '=') {
				clear = 5;
			} else if (s.charAt(i) == '^') {
				lastFourIndex = 0;
				clear = len - i;
			} else if (i == lastFourIndex) {
				clear = 4;
			}
			sb.append(clear-- > 0 ? s.charAt(i) : '*');
		}
		return sb.toString();
	}

	public static String ipProtect(String s) {
		StringBuffer sb = new StringBuffer();

		int indexFirstDot = s.indexOf(".");
		int indexLastDot = s.lastIndexOf(".");

		for (int i = 0; i < s.length(); i++) {
			if (i <= indexFirstDot || i >= indexLastDot) {
				sb.append(s.charAt(i));
			} else if (i > indexFirstDot && i < indexLastDot) {
				if (s.charAt(i) == '.') {
					sb.append('.');
				} else
					sb.append('*');
			}
		}
		return sb.toString();
	}

	public static String formatAmount(String amount, boolean separator) {
		if (amount == null) {
			amount = "";
		}
		if (amount.length() < 3) {
			amount = StringUtil.fillZero(amount, 3);
		}
		StringBuffer s = new StringBuffer();
		int strLen = amount.length();
		for (int i = 1; i <= strLen; i++) {
			s.insert(0, amount.charAt(strLen - i));
			if (i == 2)
				s.insert(0, '.');
			if (i > 3 && ((i % 3) == 0)) {
				if (separator) {
					s.insert(1, ',');
				}
			}
		}
		return s.toString();
	}

	/**
	 * prepare long value used as amount for display (implicit 2 decimals)
	 * 
	 * @param amount
	 *            value
	 * @return formated field
	 * @exception RuntimeException
	 */
	public static String formatAmount(long amount) {
		return formatAmount("" + amount, false);
	}

	public static int toAmount(String strAmount) {
		int amount = -1;
		byte[] temp = new byte[strAmount.length()];
		byte[] tempPoint = { '0', '0' };
		int index = 0, indexPoint = 0;
		boolean pointFlag = false;

		if (strAmount.getBytes()[0] == '.') {
			return -1;
		}
		for (int i = 0; i < temp.length; i++) {
			if (strAmount.getBytes()[i] != '.'
					&& (strAmount.getBytes()[i] < '0' || strAmount.getBytes()[i] > '9')) {
				return -1;
			}
			if (false == pointFlag) {
				if (strAmount.getBytes()[i] != '.') {
					temp[index++] = strAmount.getBytes()[i];
				} else {
					pointFlag = true;
				}
			} else {
				if (strAmount.getBytes()[i] != '.') {
					tempPoint[indexPoint++] = strAmount.getBytes()[i];
					if (indexPoint >= 2)
						break;
				} else {
					return -1;
				}
			}
		}
		if (indexPoint == 1 || (pointFlag == true && indexPoint == 0)) {
			return -1;
		}
		byte[] temp2 = new byte[index + 2];
		System.arraycopy(temp, 0, temp2, 0, index);
		System.arraycopy(tempPoint, 0, temp2, index, 2);
		amount = NumberUtil.parseInt(temp2, 0, 10, false);

		return amount;
	}

	public static Long toAmount(byte[] byte6) {
		Long amount = (long) -1;

		try {
			amount = Long.parseLong(StringUtil.toHexString(byte6, false));
		} catch (NumberFormatException e) {
			amount = (long) -1; // the number too big
		}

		return amount;
	}

	public static byte[] toCurrency(long number, boolean bcdFlag) {
		byte[] currency = null;

		if (bcdFlag) {
			currency = new byte[6];
			currency = StringUtil.hexString2bytes(StringUtil.fillString(""
					+ number, 12, '0', true));
		} else {
			currency = new byte[12];
			currency = (StringUtil.fillString("" + number, 12, '0', true))
					.getBytes();
		}
		return currency;
	}

	public static String toIPAddress(String ip) {
		return "" + Integer.parseInt(ip.substring(0, 3)) + "."
				+ Integer.parseInt(ip.substring(3, 6)) + "."
				+ Integer.parseInt(ip.substring(6, 9)) + "."
				+ Integer.parseInt(ip.substring(9, 12));
	}

	/**
	 * get card nember or expire date from track2
	 * 
	 * @param track2
	 *            value
	 * @param expire
	 *            flag, if expire==true ,will return expire date
	 */
	public static String getCardfromTrack2(byte[] track2, boolean expire) {
		if (track2 == null) {
			return "";
		} else {
			int panStart = -1;
			int panEnd = -1;
			String s = null;
			for (int i = 0; i < track2.length; i++) {
				if ((track2[i] >= (byte) '0' && track2[i] <= (byte) '9')
						&& panStart == -1) {
					panStart = i;
				}
				if (track2[i] == (byte) '=') {
					/* Field separator */
					panEnd = i;
					break;
				}
			}
			if (panEnd == -1 || panStart == -1) {
				s = "";
			} else {
				if (expire)
					s = new String(track2, panEnd + 1, 4);
				else
					s = new String(track2, panStart, panEnd - panStart);
			}
			return s;
		}
	}

	public static byte[] getLengthArray(int srcLength) {
		byte[] destArray = null;

		if (srcLength <= 0x7F) {
			destArray = new byte[1];
			destArray[0] = (byte) (srcLength & (byte) 0x7F);
		} else {
			int nCount;
			int midLength;
			midLength = srcLength;
			for (nCount = 1;; nCount++) {
				midLength = midLength / 256;
				if (midLength < 256) {
					break;
				}
				if (nCount >= 20) {
					break;
				}
			}
			destArray = new byte[nCount + 1];
			destArray[0] = (byte) (nCount | 0x80);
			midLength = srcLength;
			for (int idx = nCount; idx >= 1; idx--) {
				destArray[idx] = (byte) (midLength % 256);
				midLength = midLength / 256;
			}
		}

		return destArray;
	}

	public static byte[] removeTailF(byte[] buffer) {
		int length = buffer.length;
		for (; length > 0; length--) {
			if (buffer[length - 1] != 'F')
				break;
		}
		if (length == buffer.length) {
			return buffer;
		} else {
			byte[] destBuffer = new byte[length];
			System.arraycopy(buffer, 0, destBuffer, 0, length);
			return destBuffer;
		}
	}
}
