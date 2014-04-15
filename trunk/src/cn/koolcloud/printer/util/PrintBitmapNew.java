package cn.koolcloud.printer.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.util.Logger;

public class PrintBitmapNew {

	private static final int VSIZE = 3;
	private static final int pixPerByte = 8;

	private static byte[] PRINTIMAGE = { 0x1B, 0x2A, 0x21, 0x00, 0x00 };
	private static byte[] PRINTFEED = { 0x1B, 0x4A, 0x18 };
	private static byte[] BETWEEN1 = { 0x1B, 0x32 };
	private static byte[] BETWEEN2 = { 0x1B, 0x33, 0x00 };
	private static byte[] X0A = { 0x0A };

	private static int depth = 200;

	public static void printImage(byte[] pic, int length, int offset) {
		byte[] tmpArray = new byte[length + 5];
		for (int i = 0; i < length; i++) {
			tmpArray[i + 5] = pic[offset + i];
		}

		tmpArray[0] = PRINTIMAGE[0];
		tmpArray[1] = PRINTIMAGE[1];
		tmpArray[2] = PRINTIMAGE[2];

		byte len = (byte) (length / 3);
		if (len > 255)
			len = (byte) 255;
		tmpArray[3] = len;
		tmpArray[4] = PRINTIMAGE[4];

		Logger.d(length / 3 + "     " + (byte) (length / 3));

		PrinterInterface.write(tmpArray, tmpArray.length);
		PrinterInterface.write(BETWEEN2, BETWEEN2.length);
		PrinterInterface.write(X0A, X0A.length);
		PrinterInterface.write(BETWEEN1, BETWEEN1.length);
	}

	public static void printBitMap(Bitmap mBitmap) {
		boolean[][] boolArray = bitmapToArray(mBitmap);
		// dumpBoolArray(boolArray);
		int width = boolArray.length;
		if (width < 1)
			return;
		int height = boolArray[0].length;
		int batch = (height / (VSIZE * pixPerByte)) + 1;
		byte[] printArray = boolArrayToPrintArray(boolArray);

		for (int i = 0; i < batch; i++) {
			printImage(printArray, VSIZE * width, VSIZE * width * i);
		}

	}

	private static boolean[][] bitmapToArray(Bitmap mBitmap) {
		return bitmapToArray(mBitmap, depth);
	}

	private static boolean[][] bitmapToArray(Bitmap mBitmap, int thresholdvalue) {
		int width = mBitmap.getWidth();
		int height = mBitmap.getHeight();
		boolean[][] imageArray = new boolean[width][height];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int color = mBitmap.getPixel(j, i);

				int r = Color.red(color);
				int g = Color.green(color);
				int b = Color.blue(color);
				int a = Color.alpha(color);
				if (a < 170) {
					imageArray[j][i] = false;
				} else {
					int ret = ((int) (0.7 * r) + (int) (0.2 * g) + (int) (0.1 * b));
					boolean tmp = (thresholdvalue >= ret);
					imageArray[j][i] = tmp;
				}
			}
		}
		return imageArray;
	}

	@SuppressWarnings("unused")
	private static void dumpBoolArray(boolean[][] array) {
		int width = array.length;
		if (width < 1)
			return;
		int height = array[0].length;

		for (int i = 0; i < height; i++) {
			StringBuilder m = new StringBuilder();
			for (int j = 0; j < width; j++) {
				if (array[j][i]) {
					m.append("+");
				} else {
					m.append("-");
				}
			}
			Logger.d(m.toString());
		}
	}

	private static byte[] boolArrayToPrintArray(boolean[][] boolArray) {

		if (boolArray.length < 1)
			return null;
		int width = boolArray.length;
		int height = boolArray[0].length;
		int batch = (height / (VSIZE * pixPerByte)) + 1;
		if (height % (VSIZE * pixPerByte) == 0)
			batch--;
		byte[] printArray = new byte[batch * width * VSIZE];

		for (int i = 0; i < printArray.length; i++) {
			printArray[i] = 0;
		}

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int index = VSIZE * i + (j / pixPerByte) % VSIZE
						+ (j / VSIZE / pixPerByte) * VSIZE * width;
				if (i == 0)
					Log.d("", "" + (j / VSIZE / pixPerByte) + "  "
							+ (j / VSIZE / pixPerByte) * VSIZE * width);
				if (boolArray[i][j]) {
					printArray[index] <<= 1;
					printArray[index] |= 0x01;
				} else {
					printArray[index] <<= 1;
					printArray[index] &= 0xfe;
				}
			}
		}

		return printArray;
	}

}
