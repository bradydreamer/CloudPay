package cn.koolcloud.pos;

import java.math.BigInteger;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;

/**
 * ������ط���
 * @author ttlu
 *
 */
public class Utility
{
	private static final String AlogrithmKey = "DESede";
	private static final String Algorithm3DES = "DESede/ECB/PKCS5Padding";
//	private static final String CharsetUTF8 = "UTF-8";
	
	/**
	 * 16�����ַ�תBigInteger
	 * @param s
	 * @return
	 */
	public static BigInteger hex2decimal(String s)
	{
		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		BigInteger val = BigInteger.valueOf(0);
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = val.multiply(BigInteger.valueOf(16)).add(BigInteger.valueOf((long)d));
		}
		return val;
	}

	public static final String hexString(byte byte0)
	{
		// �ֽڵ�ʮ����Ƶ�ASCII��ת��
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char ac1[] = new char[2];
		ac1[0] = ac[byte0 >>> 4 & 0xf];
		ac1[1] = ac[byte0 & 0xf];
		String s = new String(ac1);
		return s;
	}
	
	/**
	 * byte����ת16�����ַ�
	 * @param bytes
	 * @return
	 */
	public static final String hexString(byte[] bytes)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			buffer.append(hexString(bytes[i]));
		}
		return buffer.toString();
	}
	
	private static int converCtoI(byte c)
	{
		if(c>=48 && c<58)
		{
			return c-48;
		}
		else if(c>=65 && c<71)
		{
			return c-65+10;
		}
		else if(c>=97 && c<103)
		{
			return c-97+10;
		}
		return 0;
	}
	
	public static byte[] hex2byte(String hex)
	{
		if(hex!=null)
		{
			byte[] source=hex.getBytes();
			byte[] resultdata=new byte[source.length/2];
			for (int i=0; i<source.length/2; i++)
			{
				int tempi=converCtoI(source[2*i]);
				resultdata[i]=(byte)((tempi<<4)&0xf0);
				resultdata[i]=(byte)(resultdata[i]+converCtoI(source[2*i+1]));
			}
			return resultdata;
		}
		return null;
	}
	
	private static Key toKey(byte[] key) throws Exception
	{  
        //ʵ��Des��Կ  
        DESedeKeySpec dks = new DESedeKeySpec(key);  
        //ʵ����Կ����  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(AlogrithmKey);  
        //�����Կ  
        SecretKey secretKey=keyFactory.generateSecret(dks);  
        return secretKey;  
    }
	
	/**
	 * RSA����
	 * @param mod
	 * @param pubExp
	 * @param data
	 * @return
	 */
	public static byte[] encodeRSA(BigInteger mod,BigInteger pubExp,byte[] data)
	{
		try
		{
			RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod, pubExp);
		AsymmetricBlockCipher eng = new RSAEngine();
			eng = new PKCS1Encoding(eng);
			eng.init(true, pubParameters);
			return eng.processBlock(data, 0, data.length);
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] encodeRSA(String mod,String pubExp,byte[] data)
	{
	
		return encodeRSA(new BigInteger(mod,16),new BigInteger(pubExp,16),data);
	}
	
	public static byte[] encodeRSAWithDecimalString(String mod,String pubExp,byte[] data)
	{
	
		return encodeRSA(new BigInteger(mod,10),new BigInteger(pubExp,10),data);
	}
	
	/**
	 * 3DES����
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] encode3DES(byte data[],byte key[])
	{
		try
		{
			//��ԭ��Կ
			Key k = toKey(key);
			//ʵ��
			Cipher cipher = Cipher.getInstance(Algorithm3DES);
			//��ʼ��������Ϊ����ģʽ
			cipher.init(Cipher.ENCRYPT_MODE, k);
			//ִ�в���
			return cipher.doFinal(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] encode3DES(String data,String key)
	{
		return encode3DES(data.getBytes(),key.getBytes());
	}
	
	/**
	 * 3DES����
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] decode3DES(byte data[],byte key[])
	{
		try
		{
			//��ԭ��Կ
			Key k = toKey(key);
			//ʵ��
			Cipher cipher = Cipher.getInstance(Algorithm3DES);
			//��ʼ��������Ϊ����ģʽ
			cipher.init(Cipher.DECRYPT_MODE, k);
			//ִ�в���
			return cipher.doFinal(data);
        }
		catch (Exception e)
		{  
			e.printStackTrace();
			return null;
        }  
	}
	
	public static byte[] decode3DES(String data,String key)
	{
		return decode3DES(data.getBytes(),key.getBytes());
	}
	
	public static String Base64(byte data[])
	{
		if(data == null){
			return null;
		}
		try
		{
//			 new String(com.sun.mail.util.BASE64EncoderStream.encode(data));
			return new String(Base64.encode(data));
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
