package cn.koolcloud.jni;

/* Internal Interface */

/** STRONGLY RECOMMENDED: to implement resource control in Native Layer to avoid resource conflict between 
 *  two Java applications simultaneously invoke this device functionality. 
 */

/**
* Permission explicit declaration
* android.permission.KOOLCLOUD_SAFE_MODULE
* android.permission.KOOLCLOUD_SAFE_MODULE_READONLY
*/

public class SafeInterface
{

	static
	{
		/* Driver implementation, so file shall put under /system/lib */
		System.loadLibrary("koolcloudPos");
		System.loadLibrary("koolcloud_safemodule");		
	}
	
    public static final int FORMAT_PEM = 0;
    public static final int FORMAT_DER = 1;
    
    public static final int CERT_TYPE_OWNER = 1;
    public static final int CERT_TYPE_PUBLIC_KEY = 2;
    public static final int CERT_TYPE_APP_ROOT = 3;
    public static final int CERT_TYPE_COMMUNICATE = 4;
    
    public static final int ALGORITHM_RSA = 1;
	
    /**
     * Open the device.
     * This method requires SAFE_MODULE_READONLY or SAFE_MODULE permission.
     * return value  >= 0 : success (suggest 0)
     *	              < 0 : error code
     */
    public native static int open();
	
    /**
     * close the device
     * return value  >= 0 : success (suggest 0)
     *	              < 0 : error code
     */
     
    public native static int close();
	
    /**
     * Check the security module is tampered or not. If the security module is tampered, all data in the security module should not be trusted.
     * This method requires SAFE_MODULE_READONLY or SAFE_MODULE permission.
     * return value == 0 : Not tampered
     *              == 1 : Tampered
     */
    public native static int isTampered();

    /**
     * Get the real random buffer from safe module.
     * This method requires SAFE_MODULE_READONLY or SAFE_MODULE permission.
     * @param bufRandom     the buffer to store random bytes.
     * @param length        the length of the buffer.
     * return value >= 0 : success (suggest 0)
     *                <0 : error code
     */
    public native static int getRandom(byte[] bufRandom, int length);
	
    /**
     * Request security module to generate a key pair inside the module.
     * This method requires SAFE_MODULE permission.
     * @param alias         the alias of the private key.
     * @param algorithm     the algorithm of the key pair. Currently, only ALGORITHM_RSA is supported.
     * @param keySize       the bit size of the key. Currently, only 2048 is supported.
     * return value >= 0 : success (suggest 0)
     *               < 0 : error code
     */
    public native static int generateKeyPair(String alias, int algorithm, int keySize);
	
    /**
     * Inject the certificate of the existing key pair.
     * @param alias             the alias of the certificate.
     * @param aliasPrivateKey   the alias of the key pair, usually it's the private key's alias.
     * @param bufCert           the data of the certificate.
     * @param bufLength         the length of the data buffer.
     * @param dataFormat        the format of the buffer, Currently, only "PEM" is supported.
     * return value >= 0 : success (suggest 0)
     *               < 0 : error code
     */
    public native static int injectPublicKeyCertificate(String alias, String aliasPrivateKey, byte[] bufCert, int bufLength, int dataFormat);

    /**
     * Inject the root certificates to security module.
     * All the certificate must signed by the terminal's owner certificate.
     * The keyUsage flag must be set as define:
     * <li>CERT_TYPE_OWNER certificate's keyUsage flag must be set as critical, and the KeyEncipherment, CertificateSign and CRLSign must be set, other flags are cleared.
     * <li>CERT_TYPE_APP_ROOT certificate's keyUsage flag must be set as critical, and the DigitalSignature, CertificateSign must be set, other flags are cleared.
     * <li>CERT_TYPE_COMMUNICATE certificate's keyUsage flag must be set as non-critical and DigitalSignature, KeyEncipherment, DataEncipherment must be set, other flags are cleared.
     * This method required SAFE_MODULE permission.
     * @param certType      the certificate type, could be CERT_TYPE_OWNER, CERT_TYPE_APP_ROOT or CERT_TYPE_COMMUNICATE.
     * @param alias         the alias of the certificate.
     * @param bufCert       the data of the certificate.
     * @param bufLength     the length of the data buffer.
     * @param dataFormat    the format of the buffer, Currently, only FORMAT_PEM is supported.
     * return value >=0 : success (suggest 0)
     *               <0 : error code
     */
    public native static int injectRootCertificate(int certType, String alias, byte[] bufCert, int bufLength, int dataFormat);
    
    /**
     * Get the certificate data.
     * This method requires SAFE_MODULE_READONLY permission.
     * @param certType      the certificate type, could be CERT_TYPE_OWNER, CERT_TYPE_PUBLIC_KEY, CERT_TYPE_APP_ROOT or CERT_TYPE_COMMUNICATE.
     * @param alias         the alias of the certificate
     * @param dataFormat    the format of the buffer, Currently, only FORMAT_PEM is supported.
     * @param bufCert       the output buffer to store the certificate PEM data.
     * @param bufMaxLength  the max length of the result buffer.
     * return value >=0 : the length of the certificate PEM data.
     *               <0 : error code
     */
    public native static int getCertificate(int certType, String alias, int dataFormat, byte[] bufCert, int bufMaxLength);
    
    /**
     * Remove the certificate of the given alias.
     * The OWNER certificate can't be removed.
     * This method requires SAVE_MODULE permission.
     * @param certType      the certificate type, could be CERT_TYPE_PUBLIC_KEY, CERT_TYPE_APP_ROOT or CERT_TYPE_COMMUNICATE.
     * @param alias         the alias of the certificate
     * return value >=0 : success (suggest 0)
     *               <0 : error code
     */
    public native static int deleteCertificate(int certType, String alias);

    /**
     * Remove the key pair of the given alias.
     * This method requires SAVE_MODULE permission.
     * @param aliasPrivateKey         the alias of the private key.
     * return value >=0 : success (suggest 0)
     *               <0 : error code
     */
    public native static int deleteKeyPair(String aliasPrivateKey);
    
    /**
     * Generate the CSR for given private key.
     * This method requires SAVE_MODULE permission.
     * @param alias             the alias of the private key
     * @param aliasPrivateKey   the alias of the private key
     * @param commonName        the commonName of the CSR
     * @param bufResult         the buffer to store the CSR data.
     * @param resMaxLength      the max length of the result buffer.
     * return value >=0 : success, with the valid length of the bufResult.
     *               <0 : error code
     */
    public native static int generateCSR(String aliasPrivateKey, String commonName, byte[] bufResult, int resMaxLength);
    
    /**
     * Do encryption by the given private key. The result data is in PKCS#1 padding format.
     * This method requires SAFE_MODULE permission.
     * @param aliasPrivateKey   the alias of the given private key.
     * @param bufPlain          the buffer of the plain data. 
     * @param bufResult         the buffer for the output cipher data.
     * @param resMaxLength      the max length of the output buffer.
     * return value >=0 : encrypt success and return the length of the bufResult.
     *               <0 : error code.
     */
    public native static int doRSAEncrypt(String aliasPrivateKey, byte[] bufPlain, byte[] bufResult, int resMaxLength);
	
}
