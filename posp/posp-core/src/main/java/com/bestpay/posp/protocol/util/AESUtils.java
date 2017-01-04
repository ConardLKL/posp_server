package com.bestpay.posp.protocol.util;

import com.bestpay.posp.utils.PropertiesUtil;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AESUtils {

    private static byte[] keyValue;// 用户密钥
    private static byte[] iv = new byte[16];// 算法参数
    private static SecretKey key; // 加密密钥
    private static AlgorithmParameterSpec paramSpec; // 算法参数
    private static Cipher ecipher; // 加密算法
    private static final String defaultEncoding = "GBK";
    private static final String UTF8Encoding = "UTF-8";

    static {
		PropertiesUtil propertiesUtil = new PropertiesUtil("sys.properties");
        String keyStr = propertiesUtil.getProperty("aes.key");
        String ivStr = propertiesUtil.getProperty("aes.iv");
        BASE64Decoder base64 = new BASE64Decoder();
        keyValue = Base64Utils.decode(keyStr);
        byte[] ivB = Base64Utils.decode(ivStr);
        for (int i = 0; i < 16; i++) {
            if (i > ivB.length) {
                iv[i] = (byte) i;
            }else{
                iv[i] = ivB[i];
            }
        }
        KeyGenerator kgen;
        try {
            // 为指定算法生成一个密钥生成器对象。
            kgen = KeyGenerator.getInstance("AES");
            // 使用用户提供的随机源初始化此密钥生成器，使其具有确定的密钥长度。
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(keyValue);
            kgen.init(128, random);
            // 使用KeyGenerator生成（对称）密钥。
            key = kgen.generateKey();
            // 使用iv中的字节作为IV来构造一个 算法参数。
            paramSpec = new IvParameterSpec(iv);
            // 生成一个实现指定转换的 Cipher 对象
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String content = "8ZOB0M91D7";
//        System.out.println("加密前："+content);
//        String encrypt = encrypt(content);
//        System.out.println("加密后："+encrypt);
//        String decrypt = decrypt(encrypt);
//        System.out.println("解密后："+decrypt);
//    }
    /**
     * 加密，使用指定数据源生成密钥，使用用户数据作为算法参数进行AES加密
     *
     * @param msg
     *            加密的数据
     * @return
     */
    public static String encrypt(String msg) {
        if(StringUtils.isBlank(msg)){
            return msg;
        }
        String str = "";
        try {
            // 用密钥和一组算法参数初始化此 cipher
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            // 加密并转换成16进制字符串
            str = asHex(ecipher.doFinal(msg.getBytes(defaultEncoding)));
        } catch (Exception e) {
            e.printStackTrace();
//            throw new BusinessException(e);
        }
        return str;
    }

    /**
     * 加密，使用指定数据源生成密钥，使用用户数据作为算法参数进行AES加密
     *
     * @param msg
     *            加密的数据
     * @return
     */
    public static String encrypt(String msg,String encode) {
        if(StringUtils.isBlank(msg)){
            return msg;
        }
        String str = "";
        try {
            // 用密钥和一组算法参数初始化此 cipher
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            // 加密并转换成16进制字符串
            str = asHex(ecipher.doFinal(msg.getBytes(encode)));
        } catch (Exception e) {
            e.printStackTrace();
//            throw new BusinessException(e);
        }
        return str;
    }

    /**
     * 解密，对生成的16进制的字符串进行解密
     *
     * @param value
     *            解密的数据
     * @return
     */
    public static String decrypt(String value) {
        String msg = null;
        if(StringUtils.isBlank(value)){
            return value;
        }
        byte[] bytes = null;
        try {
            ecipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            bytes = ecipher.doFinal(asBin(value));
        } catch (Exception e) {
            return value;
        }
        if(null==bytes){
            return value;
        }
        try {
            String utf8Msg = new String(bytes,UTF8Encoding);
            String gbkMsg = new String(bytes,defaultEncoding);
            if(!isMessyCode(utf8Msg)){
                if(isMessyCode(gbkMsg)||utf8Msg.length()<gbkMsg.length()){
                    msg = utf8Msg;
                }else{
                    msg = gbkMsg;
                }
            }else{
                msg = gbkMsg;
            }
        } catch (Exception e) {//原来就是明文
//			throw new BusinessException(e);
            try {
                msg = new String(bytes,"GBK");
            } catch (Exception e1) {
                msg =value;
            }
        }
        return msg;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param buf
     * @return
     */
    private static String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)// 小于十前面补零
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param src
     * @return
     */
    private static byte[] asBin(String src) {
        if (src.length() < 1)
            return null;
        byte[] encrypted = new byte[src.length() / 2];
        for (int i = 0; i < src.length() / 2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);// 取高位字节
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);// 取低位字节
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }

    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        if(strName.contains("�")){
            return true;
        }
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }
}
