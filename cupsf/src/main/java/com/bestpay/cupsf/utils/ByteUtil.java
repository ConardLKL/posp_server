package com.bestpay.cupsf.utils;

/**
 * Created by HR on 2016/8/12.
 */
public class ByteUtil {
    /**
     * 从b指定位置s开始获取长度len赋值到a
     * @param a 目标参数
     * @param b 源参数
     * @param len 长度
     * @param s b指定位置
     */
    public static void getByteContext(byte[] a, byte[] b, int len, int s) {
        for(int i=0; i < len; i++){
            a[i] = b[s+i];
        }
    }

    /**
     * 从b初始位置开始获取长度len赋值到a的指定位置
     * @param a 目标参数
     * @param b 源参数
     * @param len 长度
     * @param s a指定位置
     */
    public static void setByteContext(byte[] a, byte[] b, int len, int s) {
        for(int i=0; i < len; i++){
            a[s+i] = b[i];
        }
    }
    public static void setByteContext(byte[] a, byte b, int i) {
        a[i] = b;
    }
}
