package cn.koolcloud.printer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.util.Logger;

public class PrinterBitmapUtil {
	public static final int BIT_WIDTH = 384;
	private static final int WIDTH = 48;
	private static final int DOT_LINE_LIMIT = 200;
	private static final int DC2V_HEAD = 4;
	private static final int GSV_HEAD = 8;

	private static String getSystemProperty(String name) {
		try {
			Process du = Runtime.getRuntime().exec("getprop " + name);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					du.getInputStream()));
			String value = in.readLine();
			in.close();
			return value;
		} catch (Exception e) {
		}
		return null;
	}

	public static void printBitmap(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		String printerBaud = getSystemProperty("wp.printer.baud");
		if ("115200".equals(printerBaud)) {
			Logger.d("PrintUI GSV " + printerBaud);
			printBitmapGSVMSB(bm, bitMarginLeft, bitMarginTop);
		} else if ("9600".equals(printerBaud)) {
			Logger.d("PrintUI DC2V" + printerBaud);
			printBitmapDV2VMSB(bm, bitMarginLeft, bitMarginTop);
		} else {
			Logger.d("PrintUI DC2V slow" + printerBaud);
			printBitmapDV2VMSBslow(bm, bitMarginLeft, bitMarginTop);
		}
	}

	private static void printBitmapDV2VMSB(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		byte[] result = generateBitmapArrayDC2V_MSB(bm, bitMarginLeft,
				bitMarginTop);

		int lines = (result.length - 4) / 48;
		System.arraycopy(new byte[] { 18, 86, (byte) (lines & 0xFF),
				(byte) (lines >> 8 & 0xFF) }, 0, result, 0, 4);
		PrinterInterface.write(result, result.length);

		PrinterInterface.write(new byte[] { 27, 55, 7, -128, 2 }, 5);

		PrinterInterface.write(new byte[] { 27, 64 }, 2);
	}

	private static void printBitmapDV2VMSBslow(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		byte[] result = generateBitmapArrayDC2V_MSB(bm, bitMarginLeft,
				bitMarginTop);

		PrinterInterface.write(new byte[] { 27, 55, 7, -16, 2 }, 5);

		Vector vData = checkBufferLength(result);
		for (int i = 0; i < vData.size(); i++) {
			byte[] temp = (byte[]) vData.elementAt(i);
			int lines = temp.length / 48;
			PrinterInterface.write(new byte[] { 18, 86,
					(byte) (lines & 0xFF), (byte) (lines >> 8 & 0xFF) }, 4);
			PrinterInterface.write(temp, temp.length);
		}

		PrinterInterface.write(new byte[] { 27, 55, 7, -128, 2 }, 5);
	}

	private static void printBitmapGSVMSB(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		byte[] result = generateBitmapArrayGSV_MSB(bm, bitMarginLeft,
				bitMarginTop);

		int lines = (result.length - 8) / 48;
		System.arraycopy(new byte[] { 29, 118, 48, 0, 48, 0,
				(byte) (lines & 0xFF), (byte) (lines >> 8 & 0xFF) }, 0, result,
				0, 8);
		PrinterInterface.write(result, result.length);
	}

	private static byte[] generateBitmapArrayDC2V_MSB(Bitmap bm,
			int bitMarginLeft, int bitMarginTop) {
		byte[] result = null;
		int n = bm.getHeight() + bitMarginTop;
		result = new byte[n * 48 + 4];
		for (int y = 0; y < bm.getHeight(); y++) {
			for (int x = 0; x < bm.getWidth(); x++) {
				if (x + bitMarginLeft >= 384)
					break;
				int color = bm.getPixel(x, y);
				int alpha = Color.alpha(color);
				int red = Color.red(color);
				int green = Color.green(color);
				int blue = Color.blue(color);
				if (red < 128) {
					int bitX = bitMarginLeft + x;
					int byteX = bitX / 8;
					int byteY = y + bitMarginTop;
					int tmp118_117 = (4 + byteY * 48 + byteX);
					byte[] tmp118_107 = result;
					tmp118_107[tmp118_117] = (byte) (tmp118_107[tmp118_117] | 128 >> bitX
							- byteX * 8);
				}

			}

		}

		return result;
	}

	private static byte[] generateBitmapArrayGSV_MSB(Bitmap bm,
			int bitMarginLeft, int bitMarginTop) {
		byte[] result = null;
		int n = bm.getHeight() + bitMarginTop;
		int offset = 8;
		result = new byte[n * 48 + offset];
		for (int y = 0; y < bm.getHeight(); y++) {
			for (int x = 0; x < bm.getWidth(); x++) {
				if (x + bitMarginLeft >= 384)
					break;
				int color = bm.getPixel(x, y);
				int alpha = Color.alpha(color);
				int red = Color.red(color);
				int green = Color.green(color);
				int blue = Color.blue(color);
				if (red < 128) {
					int bitX = bitMarginLeft + x;
					int byteX = bitX / 8;
					int byteY = y + bitMarginTop;
					int tmp124_123 = (offset + byteY * 48 + byteX);
					byte[] tmp124_112 = result;
					tmp124_112[tmp124_123] = (byte) (tmp124_112[tmp124_123] | 128 >> bitX
							- byteX * 8);
				}

			}

		}

		return result;
	}

	public static void printBitmapESCStar(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		Vector vData = generateCmdBitmapArrayESCStar(bm, bitMarginLeft,
				bitMarginTop);

		for (int i = 0; i < vData.size(); i++) {
			byte[] temp = (byte[]) vData.elementAt(i);
			if (i > 0) {
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			PrinterInterface.write(temp, temp.length);
		}

		String text = "This is a text tset...";
		byte[] bufText = text.getBytes();
		PrinterInterface.write(bufText, bufText.length);
		PrinterInterface.write("\n".getBytes(), 1);
	}

	private static Vector<byte[]> generateCmdBitmapArrayESCStar(Bitmap bm,
			int bitMarginLeft, int bitMarginTop) {
		Vector v = new Vector();
		int line = 0;
		byte[] block = null;
		int n = bm.getHeight() + bitMarginTop;
		int pxHeight = bm.getHeight();
		int pxWidth = bm.getWidth();
		int blockWidth = 384;

		while (line < pxHeight) {
			int blockHeight = 0;

			blockHeight = 24;
			v.add(new byte[] { 27, 42, 33, (byte) (blockWidth & 0xFF),
					(byte) (blockWidth >> 8 & 0xFF) });

			block = new byte[3 * blockWidth];
			for (int y = 0; (y + line < pxHeight) && (y < blockHeight); y++) {
				for (int x = 0; x < pxWidth; x++) {
					int posBit = x * blockHeight + y;
					int posByte = posBit / 8;

					int posBitInByteLeft = posBit % 8;
					if (x >= 384)
						break;
					int color = bm.getPixel(x, y + line);
					int alpha = Color.alpha(color);
					int red = Color.red(color);
					int green = Color.green(color);
					int blue = Color.blue(color);
					if (red < 128) {
						int tmp200_198 = posByte;
						byte[] tmp200_196 = block;
						tmp200_196[tmp200_198] = (byte) (tmp200_196[tmp200_198] | 128 >> posBitInByteLeft);
					}

				}

			}

			v.add(block);
			v.add(new byte[] { 10 });

			line += blockHeight;
		}
		return v;
	}

	private static void tracelogCmdBitmapArrayESCStar(Vector<byte[]> vData) {
		StringBuffer[] arysbBlock = null;
		int blockWidth = 0;
		int m = 0;
		for (int v = 0; v < vData.size(); v++) {
			byte[] buffer = (byte[]) vData.elementAt(v);
			if (buffer.length >= 5) {
				if (buffer.length == 5) {
					if (arysbBlock != null) {
						for (int i = 0; i < arysbBlock.length; i++) {
							Log.d("PrintPNG", arysbBlock[i].toString());
						}
					}
					m = buffer[2];
					blockWidth = (buffer[3] & 0xFF) + (buffer[4] & 0xFF) * 256;
					if (m == 1)
						arysbBlock = new StringBuffer[8];
					else if (m == 33) {
						arysbBlock = new StringBuffer[24];
					}

					for (int i = 0; i < arysbBlock.length; i++)
						arysbBlock[i] = new StringBuffer();
				} else {
					int i = 0;
					while (i < buffer.length) {
						byte b = buffer[i];
						for (int pos = 0; pos < 8; pos++) {
							if ((b & 128 >> pos) != 0)
								arysbBlock[pos].append('*');
							else {
								arysbBlock[pos].append(' ');
							}
						}
						i++;
						if (m == 33) {
							b = buffer[i];
							for (int pos = 0; pos < 8; pos++) {
								if ((b & 128 >> pos) != 0)
									arysbBlock[(pos + 8)].append('*');
								else {
									arysbBlock[(pos + 8)].append(' ');
								}
							}
							i++;
							b = buffer[i];
							for (int pos = 0; pos < 8; pos++) {
								if ((b & 128 >> pos) != 0)
									arysbBlock[(pos + 16)].append('*');
								else {
									arysbBlock[(pos + 16)].append(' ');
								}
							}
							i++;
						}
					}
				}
			}
		}
		if (arysbBlock != null) {
			for (int i = 0; i < arysbBlock.length; i++)
				Logger.d(arysbBlock[i].toString());
		}
	}

	private static byte[] generateBitmapArrayDot8(Bitmap bm, int bitMarginLeft,
			int bitMarginTop) {
		byte[] result = null;

		return result;
	}

	private static void tracelogMSB(byte[] bufMSB, int widthBytes) {
		StringBuffer sbline = new StringBuffer();
		for (int i = 0; i < bufMSB.length; i++) {
			if (i % widthBytes == 0) {
				Log.d("PrintPNG", sbline.toString());
				sbline = new StringBuffer();
			}
			byte b = bufMSB[i];
			for (int pos = 0; pos < 8; pos++)
				if ((b & 128 >> pos) != 0)
					sbline.append('*');
				else
					sbline.append(' ');
		}
	}

	private static Vector<byte[]> checkBufferLength(byte[] buffer) {
		Vector v = new Vector();
		int byteLimit = 9600;
		if (buffer.length <= byteLimit) {
			v.add(buffer);
			return v;
		}
		int offset = 4;
		while (offset < buffer.length) {
			byte[] buftemp = new byte[offset + byteLimit < buffer.length ? byteLimit
					: buffer.length - offset];
			System.arraycopy(buffer, offset, buftemp, 0, buftemp.length);
			v.add(buftemp);
			offset += buftemp.length;
		}
		return v;
	}
}
