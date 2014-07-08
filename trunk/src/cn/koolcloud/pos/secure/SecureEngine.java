package cn.koolcloud.pos.secure;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;
import cn.koolcloud.APDefine;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.RSAHelper;
import cn.koolcloud.pos.Utility;
import cn.koolcloud.pos.service.SecureInfo;
import cn.koolcloud.security.util.CodeUtil;

public class SecureEngine {
	private final String TAG = "SecureEngine";
	private static SecureEngine instance;
	private String cipherKey;
	private long workTime;
	private SimpleDateFormat dateFormat;
	private final long EXPIRE_TIME = 30 * 60 * 1000;
	private final int SEED_SIZE = 8;
	private final byte[] desKey = { 0x41, 0x50, 0x4D, 0x50, 0x59, 0x44, 0x5A,
			0x46, 0x59, 0x44, 0x5A, 0x46, 0x41, 0x50, 0x4D, 0x50, 0x41, 0x50,
			0x4D, 0x50, 0x59, 0x44, 0x5A, 0x46 };
//	private byte[] workKey;
	private String keyExchange;
//	private String sn;
//	public boolean isOriginSn = true;

	public SecureEngine() {
		workTime = -1;
		dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
	}

	public static SecureEngine engineInstance() {
		if (null == instance) {
			instance = new SecureEngine();
		}
		return instance;
	}

	public boolean isOriginSn() {
		return ClientEngine.engineInstance().getSecureInfo().getIsOriginSn()
				.equalsIgnoreCase("1");
	}

	public boolean isValid() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		long currentTime = calendar.getTimeInMillis();
		long timeInterval = currentTime - workTime;
		Log.d(TAG, "work key time : " + timeInterval);
		if (-1 == workTime || timeInterval >= EXPIRE_TIME) {
			workTime = currentTime;
			return false;
		}
		return true;
	}

	public void resetWorkKey() {
		String r1 = randomSeed();
		String r2 = randomSeed();
		String r3 = randomSeed();
		String r4 = randomSeed();
		Log.d(TAG, "r1 : " + r1);
		Log.d(TAG, "r2 : " + r2);
		Log.d(TAG, "r3 : " + r3);
		Log.d(TAG, "r4 : " + r4);
		String wkStr = String.format("%s%s", xorWithHexString(r2, r4),
				xorWithHexString(r1, r3));
		Log.d(TAG, "wkStr : " + wkStr);
		byte[] workKey = Utility.encode3DES(Utility.hex2byte(wkStr), desKey);
		Log.d(TAG, "workKey : " + Utility.hexString(workKey));
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		workTime = calendar.getTimeInMillis();
		String timeStr = dateFormat.format(calendar.getTime());
		
		SecureInfo si = ClientEngine.engineInstance().getSecureInfo();
		String sn = si.getSn();
		si.setWorkKey(workKey);
		ClientEngine.engineInstance().setSecureInfo(si);
		
		String key = String.format("%s|%s|%s|%s|%s|%s", sn, r1, r2, r3, r4,
				timeStr);
		Log.d(TAG, "key : " + key);

		String modulus = APDefine.MODULUS;
		String exponent = "65537";
		byte[] outdata;
		try {
			// outdata = key.getBytes("UTF-8");
			// outdata = Utility.encodeRSAWithDecimalString(modulus, exponent,
			// outdata);

			PublicKey publicKey = RSAHelper.getPublicKey(modulus, exponent);
			outdata = RSAHelper.encryptByPublicKey(key.getBytes("UTF-8"),
					publicKey);
			keyExchange = CodeUtil.byte2HexString(outdata);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "keyExchange : " + keyExchange);
	}

	public String getKeyExchange() {
		return keyExchange;
	}

	public String fieldDecryptFromByte(byte[] src, byte[] key) {
		byte[] result = Utility.decode3DES(src, format3DesKey(key));
		if (null == result || 0 == result.length) {
			return "";
		}
		return new String(result);
	}

	public String fieldDecrypt(String src, byte[] key) {
		return fieldDecryptFromByte(Utility.hex2byte(src), key);
	}

	public String fieldDecrypt(String src) {
		byte[] workKey = ClientEngine.engineInstance().getSecureInfo()
				.getWorkKey();
		return fieldDecrypt(src, workKey);
	}

	private byte[] format3DesKey(byte[] key) {
		if (key.length != 24) {
			byte[] fullkey = new byte[24];
			System.arraycopy(key, 0, fullkey, 0, key.length);
			if (key.length == 16) {
				System.arraycopy(key, 0, fullkey, key.length, 8);
			} else {
				System.arraycopy(desKey, 0, fullkey, key.length, fullkey.length
						- key.length);
			}
			key = fullkey;
		}
		return key;
	}

	public String fieldEncryptFromByte(byte[] src, byte[] key) {
		byte[] result = Utility.encode3DES(src, format3DesKey(key));
		return Utility.hexString(result);
	}

	public String fieldEncrypt(String src, byte[] key) {
		String result = null;
		try {
			result = fieldEncryptFromByte(src.getBytes("UTF-8"), key);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String fieldEncrypt(String src) {
		byte[] workKey = ClientEngine.engineInstance().getSecureInfo()
				.getWorkKey();
		return fieldEncrypt(src, workKey);
	}

	public void setSn(String encodeSn) {
		String decodeSn = fieldDecrypt(encodeSn);
		if (decodeSn != "") {
			SecureInfo si = ClientEngine.engineInstance().getSecureInfo();
			si.setSn(decodeSn);
			si.setIsOriginSn("0");
			ClientEngine.engineInstance().setSecureInfo(si);
		}
	}

	public boolean keyCheckValue(String checkStr) {
		// byte[] workKey =
		// ClientEngine.engineInstance().getSecureInfo().getWorkKey();
		// return keyCheckValue(checkStr, workKey);
		byte[] zero = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		String kcvStr = signature(zero);
		return checkStr.equalsIgnoreCase(kcvStr);
	}

	public boolean keyCheckValue(String checkStr, byte[] key) {
		boolean kcvResult = false;
		if (null == checkStr) {
			return kcvResult;
		}
		String kcvStr = getKeyCheckValue(key);
		kcvResult = checkStr.toUpperCase().equals(kcvStr.toUpperCase());
		return kcvResult;
	}

	public String getKeyCheckValue(byte[] key) {
		final byte[] kcvSrc = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		String kcvStr = fieldEncryptFromByte(kcvSrc, key);
		return kcvStr;
	}

	private String xorWithHexString(String str1, String str2) {
		byte[] arr1 = Utility.hex2byte(str1);
		byte[] arr2 = Utility.hex2byte(str2);
		byte[] result = new byte[arr1.length];
		for (int i = 0; i < arr1.length; i++) {
			result[i] = (byte) (arr1[i] ^ arr2[i]);
		}
		return Utility.hexString(result);
	}

	private String randomSeed() {
		StringBuilder hex = new StringBuilder(SEED_SIZE * 2);
		for (int i = 0; i < SEED_SIZE; i++) {
			int aNumber = (int) (Math.random() * 256);
			if (aNumber < 16) {
				hex.append("0");
			}
			hex.append(Integer.toHexString(aNumber));
		}
		return hex.toString();
	}

	public String getCipherKey() {
		return cipherKey;
	}

	public void resetCipherKey() {
		StringBuilder random = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			int aNumber = (int) (Math.random() * 3);
			switch (aNumber) {
			case 1:
				aNumber = (int) (Math.random() * 10);
				break;

			default:
				aNumber = (int) (Math.random() * 26) + 'a';
				break;
			}
			if (aNumber > 10) {
				random.append((char) aNumber);
			} else {
				random.append(aNumber);
			}
		}
		cipherKey = random.toString();
	}

	public String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return Utility.hexString(hash);
	}

	public byte[] sha256(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("SHA-256").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, SHA-256 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		return hash;
	}

	public String getSha256String(String string) {
		byte[] hash = sha256(string);
		return Utility.hexString(hash);
	}

	public String signature(String string) {
		Log.d(TAG, "res: " + string);
		try {
			return signature(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String signature(byte[] res) {
		Mac sha256_HMAC;
		try {
			byte[] workKey = ClientEngine.engineInstance().getSecureInfo()
					.getWorkKey();

			SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(
					workKey, "HmacSHA256");
			sha256_HMAC = Mac.getInstance(secret_key.getAlgorithm());
			sha256_HMAC.init(secret_key);
			byte[] mac_data = sha256_HMAC.doFinal(res);
			String macStr = Utility.hexString(mac_data);
			Log.d(TAG, "workKey : " + Utility.hexString(workKey));

			Log.d(TAG, "res bytes: " + Utility.hexString(res));
			Log.d(TAG, "mac : " + macStr);
			return macStr;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, HmacSHA256 should be supported?",
					e);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;

	}
}
