package cn.koolcloud.pos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class InputStreamUtils {

	final static int BUFFER_SIZE = 4096;

	/**
	 * InputStream to String
	 * 
	 * @param in InputStream
	 * @return String
	 * @throws Exception
	 * 
	 */
	public static String InputStreamTOString(InputStream in) {

		ByteArrayOutputStream outStream;
		String result = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] data = new byte[BUFFER_SIZE];
			int count = -1;
			while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
				outStream.write(data, 0, count);
			}

			data = null;
			result = new String(outStream.toByteArray(), "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.trim();
	}

	/**
	 * InputStream to String with encoding
	 * 
	 * @param in
	 * @param encoding
	 * @return string
	 * @throws Exception
	 */
	public static String InputStreamTOString(InputStream in, String encoding)
			throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
			outStream.write(data, 0, count);
		}

		data = null;
		// return new String(outStream.toByteArray(), "ISO-8859-1");
		return new String(outStream.toByteArray(), encoding);
	}

	/**
	 * String to InputStream
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static InputStream StringTOInputStream(String str) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(
				str.getBytes("ISO-8859-1"));
		return is;
	}

	/**
	 * InputStream to byte array
	 * 
	 * @param in InputStream
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] InputStreamTOByte(InputStream in) throws IOException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
			outStream.write(data, 0, count);
		}

		data = null;
		return outStream.toByteArray();
	}

	/**
	 * byte array to InputStream
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static InputStream byteTOInputStream(byte[] in) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(in);
		return is;
	}

	/**
	 * byte array to String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String byteTOString(byte[] in) {
		InputStream is = null;
		try {
			is = byteTOInputStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return InputStreamTOString(is);
	}

	/**
	 * get image from assert
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getImageFromAssetFile(Context context, String fileName) {
		Bitmap image = null;
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(fileName);
			
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		    opt.inPurgeable = true;  
		    opt.inInputShareable = true;
		    
			image = BitmapFactory.decodeStream(is, null, opt);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static byte[] getByteFromFile(String fileName) throws Exception {
		Logger.d("filename="+fileName);
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] buffer = new byte[length];
		fis.read(buffer);
		fis.close();
		return buffer;
	}

	public static BitmapFactory.Options getImageOptions(String filePath) throws Exception {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		return opts;
	}
}