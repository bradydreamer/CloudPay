package cn.koolcloud.printer.util;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class CodeBitmap {

	public static Bitmap createQrBitmap(String content, int width, int height) {
		return create(content, width, height, BarcodeFormat.QR_CODE);
	}

	public static Bitmap create128Bitmap(String content, int width, int height) {
		return create(content, width, height, BarcodeFormat.CODE_128);
	}

	public static Bitmap create39Bitmap(String content, int width, int height) {
		return create(content, width, height, BarcodeFormat.CODE_39);
	}

	public static Bitmap createEAN13Bitmap(String content, int width, int height) {
		return create(content, width, height, BarcodeFormat.EAN_13);
	}

	/**
	 * 创建一维二维码图片
	 */
	public static Bitmap create(String content, int width, int height,
			BarcodeFormat marcodeFormat) {

		// 用于设置QR二维码参数
		Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别——这里选择最高H级别
		// qrParam.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H );
		// 设置编码方式
		// qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		// 生成QR二维码数据——这里只是得到一个由true和false组成的数组
		// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					marcodeFormat, width, height, qrParam);

			// 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
			int w = bitMatrix.getWidth();
			int h = bitMatrix.getHeight();
			int[] data = new int[w * h];

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (bitMatrix.get(x, y))
						data[y * w + x] = 0xff000000;// 黑色
					else
						data[y * w + x] = -1;// -1 相当于0xffffffff 白色
				}
			}

			// 创建一张bitmap图片，采用最高的图片效果ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			// 将上面的二维码颜色数组传入，生成图片颜色
			bitmap.setPixels(data, 0, w, 0, 0, w, h);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
