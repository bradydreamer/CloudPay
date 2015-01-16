package cn.koolcloud.printer.util;

import cn.koolcloud.jni.PrinterInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class PrintBitmapSprt {

	private static final int VSIZE = 3;
	private static final int pixPerByte = 8;

	private static byte[] PRINTIMAGE = { 0x1B, 0x2A, 0x00, 0x00, 0x00 };
	private static byte[] BETWEEN1 = { 0x1B, 0x32 };
	private static byte[] BETWEEN2 = { 0x1B, 0x33, 0x00 };
	private static byte[] X0A = { 0x0A };

	private static int depth = 200;

	public static void printImage(byte[] pic, int length, int offset,
			boolean mode) {
		byte[] tmpArray = new byte[length + 5];
		for (int i = 0; i < length; i++) {
			tmpArray[i + 5] = pic[offset + i];
		}

		tmpArray[0] = PRINTIMAGE[0];
		tmpArray[1] = PRINTIMAGE[1];
		if (mode)
			tmpArray[2] = 0x21;
		else {
			tmpArray[2] = 0x20;
		}
		byte len = (byte) (length / 3);
		if (len > 255)
			len = (byte) 255;
		tmpArray[3] = len;
		tmpArray[4] = PRINTIMAGE[4];
		PrinterInterface.write(tmpArray, tmpArray.length);

	}

	public static void printBitMap(Bitmap mBitmap) {
		Bitmap mTmpBitmap = null;
		boolean singlemode = false;
		int bmpWidth = mBitmap.getWidth();
		if (bmpWidth > 0 && bmpWidth < 256) {
			singlemode = true;
			mTmpBitmap = mBitmap;// Bitmap.createScaledBitmap(mBitmap,
									// mBitmap.getWidth(), mBitmap.getHeight(),
									// false);
		} else if (bmpWidth >= 256 && bmpWidth <= 390) {
			mTmpBitmap = Bitmap.createScaledBitmap(mBitmap,
					mBitmap.getWidth() / 2, mBitmap.getHeight(), false);
		} else if (bmpWidth > 390) {
			mTmpBitmap = Bitmap.createScaledBitmap(mBitmap, 390 / 2,
					mBitmap.getHeight(), false);
		}

		boolean[][] boolArray = bitmapToArray(mTmpBitmap);
		mTmpBitmap.recycle();
		// dumpBoolArray(boolArray);
		int width = boolArray.length;
		if (width < 1)
			return;
		int height = boolArray[0].length;
		int batch = (height / (VSIZE * pixPerByte)) + 1;
		byte[] printArray = boolArrayToPrintArray(boolArray);
		PrinterInterface.write(BETWEEN2, BETWEEN2.length);
		for (int i = 0; i < batch; i++) {
			printImage(printArray, VSIZE * width, VSIZE * width * i, singlemode);
			PrinterInterface.write(X0A, X0A.length);
		}
		PrinterInterface.write(BETWEEN1, BETWEEN1.length);
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
			Log.d(" ", m.toString());
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
