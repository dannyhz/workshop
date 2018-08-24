package cn.evun.sweet.common.util.encode;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA加解密方法。
 * 
 * @author yangw
 * @since 1.0.0
 */
public class RSACode {
	
	/** 加解密算法关键字 */
	public static final String KEY_ALGORITHM = "RSA";
    
	/** 公钥关键字 */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    
    /** 私钥关键字 */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** 签名算法 */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    
    /** 
     * 私钥解密。
     * @param data 对应公钥加密后的密文。
     * @param keyBytes 私钥。
     * @return 明文。    
     */      
    public static byte[] decryptByPrivateKey(byte[] data, byte[] keyBytes) throws Exception {      
        // 取得私钥      
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);      
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);      
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);      
     
        // 对数据解密      
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());      
        cipher.init(Cipher.DECRYPT_MODE, privateKey);      
     
        return cipher.doFinal(data);      
    }       
      
    /** 
     * 公钥解密。
     * @param data 对应私钥加密后的密文。
     * @param keyBytes 公钥。
     * @return 明文。    
     */      
    public static byte[] decryptByPublicKey(byte[] data, byte[] keyBytes) throws Exception {
        // 取得公钥      
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);      
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);      
        Key publicKey = keyFactory.generatePublic(x509KeySpec);      
     
        // 对数据解密      
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());      
        cipher.init(Cipher.DECRYPT_MODE, publicKey);      
     
        return cipher.doFinal(data);      
    }
      
    /** 
     * 公钥加密。
     * @param data 明文。
     * @param keyBytes 公钥。
     * @return 密文。     
     */      
    public static byte[] encryptByPublicKey(byte[] data, byte[] keyBytes) throws Exception {      
        // 取得公钥      
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);      
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);      
        Key publicKey = keyFactory.generatePublic(x509KeySpec);      
     
        // 对数据加密      
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());      
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);      
     
        return cipher.doFinal(data);      
    }       
      
    /**   
     * 私钥加密。
     * @param data 明文。
     * @param keyBytes 私钥。
     * @return 密文。      
     */      
    public static byte[] encryptByPrivateKey(byte[] data, byte[] keyBytes) throws Exception {          
        // 取得私钥      
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);      
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);      
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);      
     
        // 对数据加密      
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());      
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);      
     
        return cipher.doFinal(data);      
    }       
      
    /** 
     * 取得私钥。     
     */      
    public static byte[] getPrivateKey(Map<String, Object> keyMap) throws Exception {      
        Key key = (Key) keyMap.get(PRIVATE_KEY);      
        return key.getEncoded();      
    }       
      
    /** 
     * 取得公钥。      
     */      
    public static byte[] getPublicKey(Map<String, Object> keyMap)       
            throws Exception {      
        Key key = (Key) keyMap.get(PUBLIC_KEY);      
        return key.getEncoded();      
    }       
      
    /**
     * 初始化密钥。    
     */      
    public static Map<String, Object> initKey() throws NoSuchAlgorithmException {      
        KeyPairGenerator keyPairGen = KeyPairGenerator .getInstance(KEY_ALGORITHM);      
        keyPairGen.initialize(1024);      
        KeyPair keyPair = keyPairGen.generateKeyPair();      
      
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();    // 公钥                
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  // 私钥         
        Map<String, Object> keyMap = new HashMap<String, Object>(2);      
     
        keyMap.put(PUBLIC_KEY, publicKey);      
        keyMap.put(PRIVATE_KEY, privateKey);      
        return keyMap;      
    }
    
    /**
     * 将密钥用BASE64加密成字符形式。
     */
    public static String encryptBASE64(byte[] encoded){
    	if(encoded==null){
    		return null;
    	}
    	return Base64Code.encodeToString(encoded);
    }
    
    /**
     * 将以BASE64加密的密钥还原为字节数组。
     */
    public static byte[] decryptBASE64(String key) throws IOException{
    	if(key==null){
    		return null;
    	}
    	return Base64Code.decodeFromString(key);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = decryptBASE64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 签名后的数据
     */
    public static boolean verify(byte[] data, String publicKey, String sign)  throws Exception {
        byte[] keyBytes = decryptBASE64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(decryptBASE64(sign));
    }

}
