package cn.koolcloud.printer.util;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRcodeBitmap {

	public static Bitmap create(String content, int width, int height) {
		return create(content, width, height, ErrorCorrectionLevel.H);
	}

	/**
	 * Create QR Code bitmap
	 */
	public static Bitmap create(String content, int width, int height,
			ErrorCorrectionLevel level) {

		// Setting QR Code params
		Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
		// Setting correction level, select the highest level H
		qrParam.put(EncodeHintType.ERROR_CORRECTION, level);
		// Setting encode type
		qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		// Generate QR Code data--this is a Boolean array
		// Arguments encoded content, encode type, pic width, pic height, setting params
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.QR_CODE, width, height, qrParam);

			// start to create bitmap with QR code data by black and white
			int w = bitMatrix.getWidth();
			int h = bitMatrix.getHeight();
			int[] data = new int[w * h];

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (bitMatrix.get(x, y))
						data[y * w + x] = 0xff000000;// black
					else
						data[y * w + x] = -1;// -1 equals 0xffffffff white
				}
			}

			// create bitmap with ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			// input color array and generate bitmap color
			bitmap.setPixels(data, 0, w, 0, 0, w, h);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
