package com.bestpay.posp.protocol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.system.cache.ConfigCache;
import lombok.extern.slf4j.Slf4j;

import oracle.sql.BLOB;

import org.apache.commons.lang.StringUtils;

import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.spring.PospApplicationContext;

@Slf4j
public class Utils {
	
	/**
	 * 对象是否为空
	 * @param object
	 * @return true/false
	 */
	public static boolean isNull(final Object object){
		if(object == null){
			return true;
		}
		return false;
	}
	/**
	 * 判断获取信息是否为空
	 * 若为空，则返回相应错误信息
	 * @param object
	 * @param errorCode
	 * @param iso
	 * @return true/false
	 */
	public static boolean isNullAndReturn(Object object, String errorCode, IsoMessage iso){
		if(object == null){
			RespCodeInformation.getAndReturnRespCodeInfo(errorCode,iso);
			return true;
		}
		return false;
	}
	/**
	 * 判断获取信息是否为空
	 * 若为空，则返回相应错误信息
	 * @param object
	 * @param errorCode
	 * @param iso
	 * @return true/false
	 */
	public static boolean isNotNullAndReturn(Object object, String errorCode, IsoMessage iso){
		if(object != null){
			RespCodeInformation.getAndReturnRespCodeInfo(errorCode,iso);
			return true;
		}
		return false;
	}
	/**
	 * 是否电话线接入
	 * @param tpdu
	 * @return true/false
	 */
	public static boolean isTelephoneAccess(byte[] tpdu){
		ConfigCache cache = (ConfigCache)PospApplicationContext.getBean("ConfigCache");
		String TPDU = cache.getTelephoneAccessTpdu();
		if(StringUtils.equals(HexCodec.hexEncode(tpdu).substring(0, 6), TPDU.substring(0, 6))){
			return true;
		}
		return false;
	}
	/**
	 * 字符转换
	 * @param data
	 * @return 
	 */
	public static String getString(byte[] data){
		if(data == null){
			return null;
		}
		try {
			return new String(data,"GBK").trim();
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			log.error("FORMAT_EXCEPTION: 字符转换异常。");
		}
		return null;
	}
	/**
	 * Double类型转换成 int类型
	 * @param d
	 * @return
	 */
	public static int DoubleToInt(Double d){
		String s = d.toString();
		return Integer.parseInt(s.substring(0,s.indexOf("."))+s.substring(s.indexOf(".")+1));
	}
	/**
	 * blob类型转化成byte[]类型
	 * @param blob
	 * @return
	 */
	public static byte[] blobToBytes(BLOB blob) {  
        InputStream is = null;  
        byte[] b = null;  
        try {  
            is = blob.getBinaryStream();  
            b = new byte[(int) blob.length()];  
            is.read(b);  
            return b;  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                is.close();  
                is = null;  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return b;  
    }
	/**
	 * 获取原交易码
	 * @param iso
	 * @return
	 */
	public static String getOrgTranCode(IsoMessage iso){
		//消费冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400000022,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200000022;
		}
		//消费撤销冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400200023,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200200023;
		}
		//预授权冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400030610,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0100030610;
		}
		//预授权撤销冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400200611,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0100200611;
		}
		//预授权完成冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400000620,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200000620;
		}
		//预授权完成撤销冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400200621,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200200621;
		}
		//指定账户圈存冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400609145,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200609145;
		}
		//非指定账户圈存冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400629147,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200629147;
		}
		//现金充值冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400639146,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200639146;
		}
		//现金充值撤销冲正
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0400179151,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200179151;
		}
		//消费撤销
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0200200023,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200000022;
		}
		//预授权撤销
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0100200611,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0100030610;
		}
		//预授权完成撤销
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0200200621,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200000620;
		}
		//现金充值撤销
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0200179151,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0200639146;
		}
		//预授权完成通知，预授权完成请求
		if(StringUtils.equals(PosConstant.TRANS_TYPE_0220000624,iso.getTranCode()) 
				|| StringUtils.equals(PosConstant.TRANS_TYPE_0200000620,iso.getTranCode())){
			return PosConstant.TRANS_TYPE_0100030610;
		}
		return null;
	}
}
