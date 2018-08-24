package cn.evun.sweet.common.util.encode;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * MD5工具
 */
@SuppressWarnings("restriction")
public class MD5 {

    /**
     * 定义 加密算法,可用DES,DESede,Blowfish
     */
    private static String Algorithm = "DES";

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    /**
     * 生成密钥, 注意此步骤时间比较长
     */
    public static byte[] getKey() throws Exception {
        return KeyGenerator.getInstance(Algorithm).generateKey().getEncoded();
    }

    /**
     * 加密
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encode(byte[] input, byte[] key) throws Exception {
        SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, Algorithm);
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        return c1.doFinal(input);
    }

    /**
     * 解密
     */
    public static byte[] decode(byte[] input, byte[] key) throws Exception {
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, Algorithm));
        return c1.doFinal(input);
    }

    /**
     * 二进制转16进制字符
     *
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        return hexDigits[(b & 0xf0) >> 4] + hexDigits[b & 0x0f];
    }

    /**
     * 转换字节数16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buf.append(byteToHexString(b[i]));
        }
        return buf.toString();
    }

    public static String MD5Encode(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            origin = byteArrayToHexString(md.digest(origin.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return origin;
    }

    /**
     * md5()信息摘要, 不可移
     */
    public static byte[] md5(byte[] input) throws Exception {
        MessageDigest alg = MessageDigest
                .getInstance("MD5"); // or "SHA-1"
        alg.update(input);
        return alg.digest();
    }

    /**
     * 字节码转换成16进制字符串
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }

}
