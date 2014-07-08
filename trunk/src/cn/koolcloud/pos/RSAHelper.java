package cn.koolcloud.pos;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import cn.koolcloud.security.util.CodeUtil;

public class RSAHelper {
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 53;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 64;

	/**
	 * <p>
	 * 公钥加密
	 * </p>
	 * 
	 * @param data
	 *            源数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * <P>
	 * 私钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData,
			PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher
						.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher
						.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 公钥加密.
	 */
	public static byte[] encrypt(byte[] data, PublicKey publicKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * 私钥解密.
	 */
	public static byte[] decrypt(byte[] data, PrivateKey privateKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 得到公钥
	 * 
	 * @param key
	 *            密钥字符串（HEX格式）
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = CodeUtil.hexString2Byte(key);

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（HEX格式）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes = CodeUtil.hexString2Byte(key);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 得到密钥字符串（HEX格式）
	 * 
	 * @return
	 */
	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		return CodeUtil.byte2HexString(keyBytes);
	}

	public static PublicKey getPublicKey(String modulus, String publicExponent)
			throws Exception {
		BigInteger m = new BigInteger(modulus);
		BigInteger e = new BigInteger(publicExponent);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;

	}

	public static PrivateKey getPrivateKey(String modulus,
			String privateExponent) throws Exception {
		BigInteger m = new BigInteger(modulus);
		BigInteger e = new BigInteger(privateExponent);
		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;

	}

	public static void main(String[] args) throws Exception {
		boolean isTrue = true;
		if (isTrue) {
			/*************************************************
			 * 根据MODULES EXPONENT生成RSA密钥对，进行加解密
			 *************************************************/

			String modulus = "10103166745709600780215616551837697832816413714471062522342538060943596036859967333870827790358555455232243383580565187280643159050869924436081447583051139";
			String publicExponent = "65537";
			String privateExponet = "367979294475011322800474185715497882523349856362702385535371444397399388741997039894583483410120364529325888461124714276674612930833020362278754665756193";

			PublicKey publicKey = RSAHelper.getPublicKey(modulus,
					publicExponent);
			PrivateKey privateKey = RSAHelper.getPrivateKey(modulus,
					privateExponet);

			// 加解密类
			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");

			// 明文
			byte[] plainText = "我们都很好！邮件：@sina.com".getBytes();

			// 加密
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] enBytes = cipher.doFinal(plainText);

			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] deBytes = cipher.doFinal(enBytes);

			String s = new String(deBytes);
			System.out.println(s);

		} else {
			/*************************************************
			 * 用KeyPairGenerator产生RSA密钥对，进行加解密
			 *************************************************/
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			// 密钥位数
			keyPairGen.initialize(1024);
			// 密钥对
			KeyPair keyPair = keyPairGen.generateKeyPair();

			// 公钥
			PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

			// 私钥
			PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

			String publicKeyString = getKeyString(publicKey);
			System.out.println("public:\n" + publicKeyString);

			String privateKeyString = getKeyString(privateKey);
			System.out.println("private:\n" + privateKeyString);

			// 加解密类
			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");// Cipher.getInstance("RSA/None/PKCS1Padding");

			// 明文
			byte[] plainText = "我们都很好！邮件：@sina.com".getBytes();

			// 加密
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] enBytes = cipher.doFinal(plainText);

			// 通过密钥字符串得到密钥
			publicKey = getPublicKey(publicKeyString);
			privateKey = getPrivateKey(privateKeyString);

			// 解密
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] deBytes = cipher.doFinal(enBytes);

			publicKeyString = getKeyString(publicKey);
			System.out.println("public:\n" + publicKeyString);

			privateKeyString = getKeyString(privateKey);
			System.out.println("private:\n" + privateKeyString);

			String s = new String(deBytes);
			System.out.println(s);
		}
	}

}