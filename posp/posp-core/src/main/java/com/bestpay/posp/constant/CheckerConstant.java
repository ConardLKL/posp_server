package com.bestpay.posp.constant;

public class CheckerConstant {
	

	/**
	 * 承兑或交易成功
	 */
	public static final String RESP_CODE_00 = "00";
	/**
	 * 查发卡行
	 */
	public static final String RESP_CODE_01 = "01";
	/**
	 * 商户需要在银行或中心登记
	 */
	public static final String RESP_CODE_03 = "03";
	/**
	 * 发卡行不支持的交易
	 */
	public static final String RESP_CODE_12 = "12";
	/**
	 * 无效金额
	 */
	public static final String RESP_CODE_13 = "13";
	/**
	 *无效卡号（无此账号） 
	 */
	public static final String RESP_CODE_14 = "14";
	/**
	 *无此发卡方 
	 */
	public static final String RESP_CODE_15 = "15";
	
	/**
	 * POS状态与中心不符，可重新签到
	 */
	public static final String RESP_CODE_22 = "22";
	/**
	 * 找不到原始交易
	 */
	public static final String RESP_CODE_25 = "25";
	/**
	 * 格式错误（不符合磁道预校验规则）
	 */
	public static final String RESP_CODE_30 = "30";
	/**
	 * 受限制的卡
	 */
	public static final String RESP_CODE_35 = "35";
	/**
	 * 请求的功能尚不支持
	 */
	public static final String RESP_CODE_40 = "40";
	/**
	 * 不正确的PIN
	 */
	public static final String RESP_CODE_55 = "55";
	/**
	 * 不允许持卡人进行的交易
	 */
	public static final String RESP_CODE_57 = "57";
	/**
	 * 不允许终端进行的交易
	 */
	public static final String RESP_CODE_58 = "58";
	
	/**
	 * 超出金额限制
	 */
	public static final String RESP_CODE_61 = "61";
	/**
	 * 受限制的卡
	 */
	public static final String RESP_CODE_62 = "62";
	/**
	 * 原始金额不正确
	 */
	public static final String RESP_CODE_64 = "64";
	/**
	 * 发卡行响应超时
	 */
	public static final String RESP_CODE_68 = "68";
	
	/**
	 * POS批次与网络中心不一致
	 */
	public static final String RESP_CODE_77 = "77";
	/**
	 * 拒绝，重复交易，请稍后重试    
	 */
	public static final String RESP_CODE_94 = "94";
	/**
	 * 发卡方或网络中心出现故障
	 */
	public static final String RESP_CODE_96 = "96";
	/**
	 * 终端未在中心或银行登记
	 */
	public static final String RESP_CODE_97 = "97";
	/**
	 * 银联收不到发卡行应答
	 */
	public static final String RESP_CODE_98 = "98";
	/**
	 * PIN格式错
	 */
	public static final String RESP_CODE_99 = "99";
	/**
	 * MAC校验错
	 */
	public static final String RESP_CODE_A0 = "A0";
	/**
	 * 终端对应字段sign_state值为"0"初始状态
	 */
	public static final String SIGN_STATE_0 = "0";
	/**
	 * 终端对应字段sign_state值为"1"签到状态
	 */
	public static final String SIGN_STATE_1 = "1";
	/**
	 * 终端对应字段sign_state值为"2"签退状态
	 */
	public static final String SIGN_STATE_2 = "2";
	/**
	 * 终端对应字段sign_state值为"3"维修状态
	 */
	public static final String SIGN_STATE_3 = "3";
	/**
	 * 终端对应字段sign_state值为"4"其他
	 */
	public static final String SIGN_STATE_4 = "4";
	/**
	 * 商户对应字段POS_STATE为"S0A"
	 */
	public static final String POS_STATE_S0A = "S0A";
	
	
	
	/**
	 * 获取系统状态的key
	 */
	public final static String TSYSPARA_STATE_KEY = "000008";
	/**
	 * 系统正常状态 
	 */
	public final static String TSYSPARA_STATE_0 = "0";
	/**
	 * 系统正在做日终切换 
	 */
	public final static String TSYSPARA_STATE_1 = "1";
	/**
	 * 系统处于暂停营业中
	 */
	public final static String TSYSPARA_STATE_2 = "2";
	/**
	 * 系统处于系统维护中
	 */
	public final static String TSYSPARA_STATE_3 = "3";
	/**
	 * 签到表状态为签到
	 */
	public final static String TREM_SIGN_1 = "1";
}
