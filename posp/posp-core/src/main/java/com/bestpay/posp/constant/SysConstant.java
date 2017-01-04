package com.bestpay.posp.constant;

import com.bestpay.posp.utils.CommondUtils;

/**
 * 
 * 系统常量
 *
 */
public class SysConstant {
	
	/**
	 * 获取本地IP地址
	 */
	public final static String SYS_IP = CommondUtils.localHostIP();
	
	public final static String RS_CONFIG = "config.properties";
	public final static String RS_JDBC = "jdbc.properties";
	
	public final static String[] CONFIG_RESOURCES = {RS_CONFIG,RS_JDBC};
	
	/**
	 * 通道类型  CHANNEL_X001 = X001;
	 */
	public static final Long CL3001 = 3001L;
	public static final Long CL4001 = 4001L;
	public static final Long CL5001 = 5001L;
	public static final Long CL6001 = 6001L;
	public static final Long CL7001 = 7001L;
	public static final Long CL8001 = 8001L;
	public static final Long CL9001 = 9001L;
	public static final Long CL1 = 1L;
	public static final Long[] CHANNELS = {CL3001,CL4001,CL5001,CL6001,CL7001,CL8001,CL9001,CL1};

	/**
	 * 加密机报文规范: 4001
	 */
	public static final String CAPITAL_POOL_4001 = "4001";
	/**
	 * POSP报文规范: 5001
	 */
	public static final String CAPITAL_POOL_5001 = "5001";
	/**
	 * 终端报文规范: 7001
	 */
	public static final String CAPITAL_POOL_7001 = "7001";

	/**
	 * 银联报文规范: 6001
	 */
	public static final String CAPITAL_POOL_6001 = "6001";
	/**
	 * 全渠道银联报文规范: 9001
	 */
	public static final String CAPITAL_POOL_9001 = "9001";
	
	/**
	 * 收单 平台流水号查询条件 /流水格式:不足16位前面补0
	 */
	public final static String SYSPARA_SERIAL_NO = "000010";
	
	/**
	 * 收单 平台 系统日期查询条件 /日期格式:yyyyMMdd
	 */
	public final static String SYSPARA_DATE = "000002";
	
	/**
	 * 入网机构代码前四位 收单机构码
	 */
	public final static String SYSPARA_BRANCHNO = "000020";
	
	/**
	 * 发送机构标示码
	 */
	public final static String SYSPARA_BRANCHFILAGCODE = "000021";
	
	/**
	 * 指定TPDU时，检查主叫号码
	 */
	public final static String SYS_TPDU = "6000020000";
	/**
	 * 贷记卡
	 */
	public final static String CARD_CDC = "CDC";
	/**
	 * 借记卡
	 */
	public final static String CARD_DBC = "DBC";
	/**
	 * 小额双免额度
	 */
	public final static int FREE_LIMIT = 30000;
	/**
	 * 小额双免范围
	 */
	public final static String NON_CONN = "07";

}
