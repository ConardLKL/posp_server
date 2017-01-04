package com.bestpay.posp.utils.crypto;

import java.io.UnsupportedEncodingException;

import com.bestpay.posp.protocol.util.HexCodec;
import lombok.extern.slf4j.Slf4j;

/** 
 * PBOC3DES 加密算法 
 * @author Administrator 
 * 
 */  
@Slf4j
public class PBOC3DES {  
      
    private static int HEX = 0;  
    private static int ASC = 1;  
    private static int[][] subKey = new int[16][48];  
      
    /** ***************************压缩替换S-Box************************************************* */  
    private static final int[][] s1 = {  
            { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },  
            { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },  
            { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },  
            { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } };  
    private static final int[][] s2 = {  
            { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },  
            { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },  
            { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },  
            { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } };  
    private static final int[][] s3 = {  
            { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },  
            { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },  
            { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },  
            { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } };  
    private static final int[][] s4 = {  
            { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },  
            { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },// erorr  
            { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },  
            { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } };  
    private static final int[][] s5 = {  
            { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },  
            { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },  
            { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },  
            { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } };  
    private static final int[][] s6 = {  
            { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },  
            { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },  
            { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },  
            { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } };  
    private static final int[][] s7 = {  
            { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },  
            { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },  
            { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },  
            { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } };  
    private static final int[][] s8 = {  
            { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },  
            { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },  
            { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },  
            { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };  
      
    private static final int[] ip = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52,  
        44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48,  
        40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35,  
        27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31,  
        23, 15, 7 };  
    private static final int[] _ip = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7,  
        47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45,  
        13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11,  
        51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49,  
        17, 57, 25 };  
      
    // 每次密钥循环左移位数  
        private static final int[] LS = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2,  
                2, 1 };  
      
    /** 
     * 计算MAC(hex) 
     * PBOC_3DES_MAC(16的整数补8000000000000000) 
     * 前n-1组使用单长密钥DES 使用密钥是密钥的左8字节） 
     * 最后1组使用双长密钥3DES （使用全部16字节密钥） 
     * @param key 密钥(32byte) 
     * @param 初始向量0000000000000000 
     * @param data 数据 
     * @param type  HEX:16进制的数据    ASC：字符串 
     * @return mac 
     */  
    public static String PBOC_3DES_MAC(String data, String key,int type, String initVector){  
    	
        String vector = null;
        
        if(initVector == null)
        	vector = "0000000000000000";
        else
        	vector = initVector;
        
        if (key.length()!=32){  
            return null;  
        }  
        if (type == ASC) {  
            data = ASC_2_HEX(data);  
        }  
        int len = data.length();  
        int arrLen = len / 16 + 1;  
        String[] D = new String[arrLen];  
        if (len % 16 == 0) {  
            data += "8000000000000000";  
        } else {  
            data += "80";  
            for (int i = 0; i < 15 - len % 16; i++) {  
                data += "00";  
            }  
        }  
        for (int i = 0; i < arrLen; i++) {  
            D[i] = data.substring(i * 16, i * 16 + 16);  
        }
        // D0 Xor Vector  
        String I = xOr(D[0], vector);  
        String O = null;  
        String kl = key.substring(0, 16);  
        for (int i = 1; i < arrLen; i++) {  
            O = DES_1(I, kl, 0);  
            I = xOr(D[i], O);  
        }  
        I = DES_3(I, key, 0);  
        return I;  
    }  
      
    /** 
     *  
     * 三重DES算法(双长密32byte)) 
     * 密钥K1和K2 
     * 1、先用K1加密明文 
     * 2、接K2对上的结果进行解 
     * 3、然后用K1对上的结果进行加 
     * @param source 
     * @param key 
     * @param type 
     *            0:encrypt 1:discrypt 
     * @return 
     */  
    public static String DES_3(String source, String key, int type) {  
        if (key.length() != 32 || source.length() != 16)  
            return null;  
        String temp = null;  
        String K1 = key.substring(0, key.length() / 2);  
        String K2 = key.substring(key.length() / 2);  
//      log.debug("K1--->" + K1);  
//      log.debug("K2--->" + K2);  
        if (type == 0) {  
            temp = encryption(source, K1);  
//          log.debug("step1--->" + temp);  
            temp = discryption(temp, K2);  
//          log.debug("step2--->" + temp);  
            return encryption(temp, K1);  
        }  
        if (type == 1) {  
            temp = discryption(source, K1);  
            temp = encryption(temp, K2);  
            return discryption(temp, K1);  
        }  
        return null;  
    }  
      
    /** 
     * DES解密--->对称密钥 
     * 解密算法与加密算法基本相同，不同之处仅在于轮子密钥的使用顺序逆序，即解密的第1 
     * 轮子密钥为加密的6 轮子密钥，解密的 轮子密钥为加密的5 轮子密钥，…， 
     * 解密的第16 轮子密钥为加密的 轮子密钥 
     * @param source密文 
     * @param key密钥 
     * @return 
     *  
     */  
    public static String discryption(String source, String key) {  
        String str = "";  
        int[] data = string2Binary(source);// 64bit  
        // 第一步初始置  
        data = changeIP(data);  
        int[] left = new int[32];  
        int[] right = new int[32];  
        int[] tmp = new int[32];  
        for (int j = 0; j < 32; j++) {  
            left[j] = data[j];  
            right[j] = data[j + 32];  
        }  
        setKey(key);// sub key ok  
        for (int i = 16; i > 0; i--) {  
            // 获取(48bit)的轮子密  
            /** *******不同之处********* */  
            int[] sKey = subKey[i - 1];  
            tmp = left;  
            // R1 = L0  
            left = right;  
            // L1 = R0 ^ f(L0,K1)  
            int[] fTemp = f(right, sKey);// 32bit  
            right = diffOr(tmp, fTemp);  
        }  
        // 组合的时候，左右调换**************************************************  
        for (int i = 0; i < 32; i++) {  
            data[i] = right[i];  
            data[32 + i] = left[i];  
        }  
        data = changeInverseIP(data);  
        for (int i = 0; i < data.length; i++) {  
            str += data[i];  
        }  
        str = binary2ASC(str);  
        return str;  
    }  
      
    /** 
     * @param R(2bit) 
     * @param K(48bit的轮子密 
     * @return 32bit 
     */  
    public static int[] f(int[] R, int[] K) {  
        int[] dest = new int[32];  
        int[] temp = new int[48];  
        // 先将输入32bit扩展8bit  
        int[] expendR = expend(R);// 48bit  
        // 与轮子密钥进行异或运  
        temp = diffOr(expendR, K);  
        // 压缩2bit  
        dest = press(temp);  
        return dest;  
    }  
      
    /** 
     * 8bit压缩2bit 
     * @param source(48bit) 
     * @return R(32bit) B=E(R)⊕K，将48 位的B 分成8 个分组，B=B1B2B3B4B5B6B7B8 
     */  
    public static int[] press(int[] source) {  
        int[] ret = new int[32];  
        int[][] temp = new int[8][6];  
        int[][][] s = { s1, s2, s3, s4, s5, s6, s7, s8 };  
        StringBuffer str = new StringBuffer();  
        for (int i = 0; i < 8; i++) {  
            for (int j = 0; j < 6; j++) {  
                temp[i][j] = source[i * 6 + j];  
            }  
        }  
        for (int i = 0; i < 8; i++) {  
            // (16)  
            int x = temp[i][0] * 2 + temp[i][5];  
            // (2345)  
            int y = temp[i][1] * 8 + temp[i][2] * 4 + temp[i][3] * 2  
                    + temp[i][4];  
            int val = s[i][x][y];  
            String ch = int2Hex(val);  
            // log.debug("x=" + x + ",y=" + y + "-->" + ch);  
            // String ch = Integer.toBinaryString(val);  
            str.append(ch);  
        }  
        // log.debug(str.toString());  
        ret = string2Binary(str.toString());  
        // printArr(ret);  
        // 置换P  
        ret = dataP(ret);  
        return ret;  
    }  
      
    /** 
     * 置换P(32bit) 
     * @param source 
     * @return 
     */  
    public static int[] dataP(int[] source) {  
        int[] dest = new int[32];  
        int[] temp = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31,  
                10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };  
        int len = source.length;  
        for (int i = 0; i < len; i++) {  
            dest[i] = source[temp[i] - 1];  
        }  
        return dest;  
    }  
  
      
    /** 
     * 将int转换成Hex 
     * @param i 
     * @return 
     * @throws Exception 
     */  
    public static String int2Hex(int i) {  
        switch (i) {  
        case 0:  
        case 1:  
        case 2:  
        case 3:  
        case 4:  
        case 5:  
        case 6:  
        case 7:  
        case 8:  
        case 9:  
            return "" + i;  
        case 10:  
            return "A";  
        case 11:  
            return "B";  
        case 12:  
            return "C";  
        case 13:  
            return "D";  
        case 14:  
            return "E";  
        case 15:  
            return "F";  
        default:  
            return null;  
        }  
    }  
  
  
      
    /** 
     * 2bit扩展8bit 
     * @param source 
     * @return 
     */  
    public static int[] expend(int[] source) {  
        int[] ret = new int[48];  
        int[] temp = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12,  
                13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22,  
                23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };  
        for (int i = 0; i < 48; i++) {  
            ret[i] = source[temp[i] - 1];  
        }  
        return ret;  
    }  
      
    /** 
     * IP-1逆置 
     * @param source 
     * @return 
     */  
    public static int[] changeInverseIP(int[] source) {  
        int[] dest = new int[64];  
        for (int i = 0; i < 64; i++) {  
            dest[i] = source[_ip[i] - 1];  
        }  
        return dest;  
    }  
      
    /** 
     *  
     * 获取轮子密钥(48bit) 
     *  
     * @param source 
     *  
     * @return 
     *  
     */  
    public static void setKey(String source) {  
        if (subKey.length > 0)  
            subKey = new int[16][48];  
        // 装换4bit  
        int[] temp = string2Binary(source);  
        // 6bit均分成两部分  
        int[] left = new int[28];  
        int[] right = new int[28];  
        // 经过PC-14bit转换6bit  
        int[] temp1 = new int[56];  
        temp1 = keyPC_1(temp);  
        // printArr(temp1);  
        // 将经过转换的temp1均分成两部分  
        for (int i = 0; i < 28; i++) {  
            left[i] = temp1[i];  
            right[i] = temp1[i + 28];  
        }  
        // 经过16次循环左移，然后PC-2置换  
        for (int i = 0; i < 16; i++) {  
            left = keyLeftMove(left, LS[i]);  
            right = keyLeftMove(right, LS[i]);  
            for (int j = 0; j < 28; j++) {  
                temp1[j] = left[j];  
                temp1[j + 28] = right[j];  
            }  
            // printArr(temp1);  
            subKey[i] = keyPC_2(temp1);  
        }  
    }  
      
    /** 
     *  
     * 6bit的密钥转换成48bit 
     * @param source 
     * @return 
     *  
     */  
    public static int[] keyPC_2(int[] source) {  
        int[] dest = new int[48];  
        int[] temp = { 14, 17, 11, 24, 1, 5,  
        3, 28, 15, 6, 21, 10,  
        23, 19, 12, 4, 26, 8,  
        16, 7, 27, 20, 13, 2,  
        41, 52, 31, 37, 47, 55,  
        30, 40, 51, 45, 33, 48,  
        44, 49, 39, 56, 34, 53,  
        46, 42, 50, 36, 29, 32 };  
        for (int i = 0; i < 48; i++) {  
            dest[i] = source[temp[i] - 1];  
        }  
        return dest;  
    }  
      
    /** 
     *  
     * 将密钥循环左移i 
     * @param source 二进制密钥数 
     * @param i 循环左移位数 
     * @return 
     *  
     */  
    public static int[] keyLeftMove(int[] source, int i) {  
        int temp = 0;  
        int len = source.length;  
        int ls = LS[i];  
        // log.debug("len" + len + ",LS[" + i + "]=" + ls);  
        for (int k = 0; k < ls; k++) {  
            temp = source[0];  
            for (int j = 0; j < len - 1; j++) {  
                source[j] = source[j + 1];  
            }  
            source[len - 1] = temp;  
        }  
        return source;  
    }  
      
    /** 
     *  
     * 4bit的密钥转换成56bit 
     * @param source 
     * @return 
     *  
     */  
  
    public static int[] keyPC_1(int[] source) {  
        int[] dest = new int[56];  
        int[] temp = { 57, 49, 41, 33, 25, 17, 9,  
        1, 58, 50, 42, 34, 26, 18,  
        10, 2, 59, 51, 43, 35, 27,  
        19, 11, 3, 60, 52, 44, 36,  
        63, 55, 47, 39, 31, 23, 15,  
        7, 62, 54, 46, 38, 30, 22,  
        14, 6, 61, 53, 45, 37, 29,  
        21, 13, 5, 28, 20, 12, 4 };  
        for (int i = 0; i < 56; i++) {  
            dest[i] = source[temp[i] - 1];  
        }  
        return dest;  
    }  
  
      
    /** 
     *  
     * 单长密钥DES(16byte) 
     * @param source 
     * @param key 
     * @param type 0:encrypt 1:discrypt 
     * @return 
     *  
     */  
    public static String DES_1(String source, String key, int type) {  
        if (source.length() != 16 || key.length() != 16)  
            return null;  
        if (type == 0) {  
            return encryption(source, key);  
        }  
        if (type == 1) {  
            return discryption(source, key);  
        }  
        return null;  
    }  
      
    /** 
     * IP初始置换 
     * @param source 
     * @return 
     */  
    public static int[] changeIP(int[] source) {  
        int[] dest = new int[64];  
        for (int i = 0; i < 64; i++) {  
            dest[i] = source[ip[i] - 1];  
        }  
        return dest;  
    }  
      
    /** 
     *  
     * DES加密--->对称密钥 
     * D = Ln(32bit)+Rn(32bit) 
     * 经过16轮置 
     * @param D(16byte)明文 
     * @param K(16byte)轮子密钥 
     * @return (16byte)密文 
     */  
    public static String encryption(String D, String K) {  
        String str = "";  
        int[] temp = new int[64];  
        int[] data = string2Binary(D);  
        // 第一步初始置  
        data = changeIP(data);  
        int[][] left = new int[17][32];  
        int[][] right = new int[17][32];  
        for (int j = 0; j < 32; j++) {  
            left[0][j] = data[j];  
            right[0][j] = data[j + 32];  
        }  
        setKey(K);// sub key ok  
        for (int i = 1; i < 17; i++) {  
            // 获取(48bit)的轮子密  
            int[] key = subKey[i - 1];  
            // L1 = R0  
            left[i] = right[i - 1];  
            // R1 = L0 ^ f(R0,K1)  
            int[] fTemp = f(right[i - 1], key);// 32bit  
            right[i] = diffOr(left[i - 1], fTemp);  
        }  
        // 组合的时候，左右调换**************************************************  
        for (int i = 0; i < 32; i++) {  
            temp[i] = right[16][i];  
            temp[32 + i] = left[16][i];  
        }  
        temp = changeInverseIP(temp);  
        str = binary2ASC(intArr2Str(temp));  
        return str;  
    }  
      
    /** 
     * 将ASC字符串转16进制字符 
     * @param asc 
     * @return 
     */  
    public static String ASC_2_HEX(String asc) {  
        StringBuffer hex = new StringBuffer();  
        try {  
            byte[] bs = asc.toUpperCase().getBytes("UTF-8");  
            for (byte b : bs) {  
                hex.append(Integer.toHexString(new Byte(b).intValue()));  
            }  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return hex.toString();  
    }  
      
    /** 
     *  
     * 将s1和s2做异或，然后返回 
     * @param s1 
     * @param s2 
     * @return 
     *  
     */  
    public static String xOr(String s1, String s2) {  
        int[] iArr = diffOr(string2Binary(s1), string2Binary(s2));  
        return binary2ASC(intArr2Str(iArr));  
    }  
      
    /** 
     * 将二进制字符串转换成十六进制字符 
     * @param s 
     * @return 
     */  
    public static String binary2ASC(String s) {  
        String str = "";  
        int ii = 0;  
        int len = s.length();  
        // 不够4bit左补0  
        if (len % 4 != 0) {  
            while (ii < 4 - len % 4) {  
                s = "0" + s;  
            }  
        }  
        for (int i = 0; i < len / 4; i++) {  
            str += binary2Hex(s.substring(i * 4, i * 4 + 4));  
        }  
        return str;  
    }  
      
    /** 
     * s位长度的二进制字符串 
     * @param s 
     * @return 
     */  
    public static String binary2Hex(String s) {  
        int len = s.length();  
        int result = 0;  
        int k = 0;  
        if (len > 4)  
            return null;  
        for (int i = len; i > 0; i--) {  
            result += Integer.parseInt(s.substring(i - 1, i)) * getXY(2, k);  
            k++;  
        }  
        switch (result) {  
        case 0:  
        case 1:  
        case 2:  
        case 3:  
        case 4:  
        case 5:  
        case 6:  
        case 7:  
        case 8:  
        case 9:  
            return "" + result;  
        case 10:  
            return "A";  
        case 11:  
            return "B";  
        case 12:  
            return "C";  
        case 13:  
            return "D";  
        case 14:  
            return "E";  
        case 15:  
            return "F";  
        default:  
            return null;  
        }  
    }  
      
    /** 
     * 返回x的y次方 
     * @param x 
     * @param y 
     * @return 
     */  
    public static int getXY(int x, int y) {  
        int temp = x;  
        if (y == 0)  
            x = 1;  
        for (int i = 2; i <= y; i++) {  
            x *= temp;  
        }  
        return x;  
    }  
  
      
    /** 
     * 将字符串转换成二进制数组 
     * @param source :16字节 
     * @return 
     */  
    public static int[] string2Binary(String source) {  
        int len = source.length();  
        int[] dest = new int[len * 4];  
        char[] arr = source.toCharArray();  
        for (int i = 0; i < len; i++) {  
            int t = 0;  
            try {  
                t = getIntByChar(arr[i]);  
                // log.debug(arr[i]);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            String[] str = Integer.toBinaryString(t).split("");  
            int k = i * 4 + 3;  
            for (int j = str.length - 1; j > 0; j--) {  
                dest[k] = Integer.parseInt(str[j]);  
                k--;  
            }  
        }  
        return dest;  
    }  
      
    /** 
     * 将十六进制A--F转换成对应数 
     * @param ch 
     * @return 
     * @throws Exception 
     */  
    public static int getIntByChar(char ch) throws Exception {  
        char t = Character.toUpperCase(ch);  
        int i = 0;  
        switch (t) {  
        case '0':  
        case '1':  
        case '2':  
        case '3':  
        case '4':  
        case '5':  
        case '6':  
        case '7':  
        case '8':  
        case '9':  
            i = Integer.parseInt(Character.toString(t));  
            break;  
        case 'A':  
            i = 10;  
            break;  
        case 'B':  
            i = 11;  
            break;  
        case 'C':  
            i = 12;  
            break;  
        case 'D':  
            i = 13;  
            break;  
        case 'E':  
            i = 14;  
            break;  
        case 'F':  
            i = 15;  
            break;  
        default:  
            throw new Exception("getIntByChar was wrong");  
        }  
        return i;  
    }  
  
      
    /** 
     * 两个等长的数组做异或 
     * @param source1 
     * @param source2 
     * @return 
     */  
    public static int[] diffOr(int[] source1, int[] source2) {  
        int len = source1.length;  
        int[] dest = new int[len];  
        for (int i = 0; i < len; i++) {  
            dest[i] = source1[i] ^ source2[i];  
        }  
        return dest;  
    }  
      
    /** 
     * 将int类型数组拼接成字符串 
     * @param arr 
     * @return 
     */  
    public static String intArr2Str(int[] arr) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < arr.length; i++) {  
            sb.append(arr[i]);  
        }  
        return sb.toString();  
    }  
    
    public static String getTestTpkTak(String Random)
    {
    	String PrefixData;
    	 String MacDataPrefix;
    	String returnKey = "";
    	int len = 0;
    	int arrLen = 0;
    	String KEYDATA = "";
    	String KeyData = "";
    	String[] D;
    	String TMK="61BC627AA88A587C83DC2A134F450B4C";
//    	String TPK="11111111111111111111111111111111";
//    	String TAK="22222222222222222222222222222222";
    	String TPK="6894680BB5C425E0D35D263B687A3D4A";
    	String TAK="1346A7B5A840BA57386837AB3D989475";
//    	String TPK="C1524C2F343E4F0BDF451CF845673D20";
//    	String TAK="C1CE1C32FBA4CBAED3C1F7AD75EA16AB";
    	String TPK_KeyData;
    	String TPK_MAC;
    	String MACDATA;
    	String TAK_KeyData;
    	String TAK_MAC;
    	
    	//TPK固定前缀
    	MacDataPrefix = "84D408011C";
    	PrefixData = "170100080F010F88";
    	KEYDATA = PrefixData + TPK;
        len = KEYDATA.length();  
        arrLen = len / 16 ;  
        D = new String[arrLen];  
        if (len % 16 == 0) {  
        	KEYDATA += "8000000000000000";  
        } else {  
        	KEYDATA += "80";  
            for (int i = 0; i < 15 - len % 16; i++) {  
            	KEYDATA += "00";  
            }  
        }
        
        TPK_KeyData = "";
        for (int i = 0; i < arrLen; i++) {  
            D[i] = KEYDATA.substring(i * 16, i * 16 + 16);
            //log.debug("TPK："+D[i]); 
            TPK_KeyData += DES_3(D[i],TMK,0);
        }
        
        MACDATA = MacDataPrefix + TPK_KeyData;
        TPK_MAC = PBOC_3DES_MAC(MACDATA,TMK,HEX,Random);
        
        returnKey += TPK_KeyData;
        returnKey += TPK_MAC.substring(0,8);
////        
//        log.debug("TPK Random：= "+Random);
//    	log.debug("TPK ：= "+TPK_KeyData);
//    	log.debug("TPK_MACDATA：= "+MACDATA);
//    	log.debug("TPK_KEYDATA：= "+KEYDATA);
//    	log.debug("TPK_MAC：= "+TPK_MAC); 
//    	log.debug("\n\n");
    	
    	//TAK固定前缀
    	MacDataPrefix = "84D408021C";
    	PrefixData = "170200080F010F88";
       	KEYDATA = PrefixData + TAK;
        len = KEYDATA.length();  
        arrLen = len / 16 ;  
        D = new String[arrLen];  
        if (len % 16 == 0) {  
        	KEYDATA += "8000000000000000";  
        } else {  
        	KEYDATA += "80";  
            for (int i = 0; i < 15 - len % 16; i++) {  
            	KEYDATA += "00";  
            }  
        }
        
        TAK_KeyData = "";
        for (int i = 0; i < arrLen; i++) {  
            D[i] = KEYDATA.substring(i * 16, i * 16 + 16);
           // log.debug("TAK："+D[i]); 
            TAK_KeyData += DES_3(D[i],TMK,0);
        }
        
        MACDATA = MacDataPrefix + TAK_KeyData;
        TAK_MAC = PBOC_3DES_MAC(MACDATA,TMK,HEX,Random);
        
        returnKey += TAK_KeyData;
        returnKey += TAK_MAC.substring(0,8);
        
//        log.debug("TAK Random：= "+Random);
//        log.debug("TAK：= "+TAK_KeyData);
//    	log.debug("TAK_KEYDATA：= "+KEYDATA);
//    	log.debug("TAK_MACDATA：= "+MACDATA);
//    	log.debug("TAK_MAC：= "+TAK_MAC);
//    	
//    	log.debug("returnKey：= " + returnKey);
    	return returnKey;
    }
    
    public static String getMac(String mac_str)
    {
    	String MAC = "";
    	String TAK="C1CE1C32FBA4CBAED3C1F7AD75EA16AB";
    	String mac_hex_str = "";
		try {
			mac_hex_str = HexCodec.hexEncode(mac_str.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mac_hex_str = mac_str;
    	int len = mac_hex_str.length();  
        int arrLen = len / 16 + 1;  
        String[]  D = new String[arrLen];  
         if (len % 16 == 0) {  
        	 mac_hex_str += "8000000000000000";  
         } else {  
        	 mac_hex_str += "80";  
             for (int i = 0; i < 14 - len % 16; i++) {  
            	 mac_hex_str += "0";  
             }  
         }
        
         log.debug("mac_hex_str："+mac_hex_str);
         MAC = "";
         String Random = "0000000000000000";
         for (int i = 0; i < arrLen; i++) {  
//             D[i] = mac_hex_str.substring(i * 16, i * 16 + 16);
//             Random = xOr(D[i],Random);
//             log.debug("TAK："+D[i] + " Random：" + Random); 
//             Random = DES_3(Random,TAK,0);
        	 
        	 
             D[i] = mac_hex_str.substring(i * 16, i * 16 + 16);
             String A = xOr(D[i],Random);
            // log.debug("TAK："+D[i] + " Random：" + Random); 
             String B = DES_3(A,TAK,0);
             log.debug(String.format("[%s] NOR [%s]  [%s] DES = [%s]", Random,D[i],A,B)); 
             Random = B;
         }
         
    	return Random;
    }
    
    public static void main(String[] args) {  
        // shuju:2000000000000000000000510001000001504F533838383834343939313330323933393930303037  
        // MACforP :35812576  
        //结果：4632453534454631  
        //String result = PBOC_3DES_MAC("000000000000000003169CD5570CFE5F9EFA33EFC6B3D606C8EDC5D57ABD0EE3","438068AD4F6EFEAB4AEC3404AE26F8EF",HEX,"0000000000000000");  
        //log.debug("PBOC3DES加密结果为："+result);  
        //getTestTpkTak("26ABAA4D00000000");
    	String str = getTestTpkTak("86DB75BE00000000");
    	log.debug("PBOC3DES加密结果为："+str);
    	
    	//log.debug("MAC："+ getMac("0210 166225000000000030 300000 00 000000000272 58 11200001 837448010000000"));  
    	log.debug("MAC："+ getMac("000000000000000000"));
    }
}  
