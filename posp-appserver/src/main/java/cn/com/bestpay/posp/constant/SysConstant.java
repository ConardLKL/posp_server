package cn.com.bestpay.posp.constant;


/**
 * 
 * 系统常量
 *
 */
public class SysConstant {
	
	/**
	 * 通道类型  CHANNEL_X001 = X001;
	 */
	public static final Long CL3001 = 3001L;
	public static final Long CL5001 = 5001L;
	public static final Long CL6001 = 6001L;
	public static final Long CL7001 = 7001L;
	public static final Long CL8001 = 8001L;
	public static final Long CL9001 = 9001L;
	public static final Long CL1 = 1L;
	public static final Long[] CHANNELS = {CL3001,CL5001,CL6001,CL7001,CL8001,CL9001,CL1};
	
	/**
	 * xml管理类交易服务代码名称
	 */
	public static final String MANAGEMENT_TYPES = "0100.0101.1001.1002.1003.1004.1005.1006.1007.1008";
	 /**
     * 终端交易类交易服务代码名称
     */
	public static final String TRANSACTION_TYPES = "8001.8002.8003.8004.2001.2002.2003.2004.2005.2006";
	/**
	 * 版本检查
	 */
	public static final String TRANS_TYPE_0100 = "0100";
	/**
	 * 获取短信验证码
	 */
	public static final String TRANS_TYPE_0101 = "0101";
	/**
	 * 用户注册
	 */
	public static final String TRANS_TYPE_1001 = "1001";
	/**
	 * 用户登录
	 */
	public static final String TRANS_TYPE_1002 = "1002";
	/**
	 * 用户退出
	 */
	public static final String TRANS_TYPE_1003 = "1003";
	/**
	 * 修改密码
	 */
	public static final String TRANS_TYPE_1004 = "1004";
	/**
	 * 重置密码
	 */
	public static final String TRANS_TYPE_1005 = "1005";
	/**
	 * 商户交易流水查询
	 */
	public static final String TRANS_TYPE_1006 = "1006";
	/**
	 * 交易汇总查询
	 */
	public static final String TRANS_TYPE_1007 = "1007";
	/**
	 * 获取公告
	 */
	public static final String TRANS_TYPE_1008 = "1008";
	/**
	 * 签到
	 */
	public static final String TRANS_TYPE_8001 = "8001";
	/**
	 * 签退
	 */
	public static final String TRANS_TYPE_8002 = "8002";
	/**
	 * IC卡参数下载
	 */
	public static final String TRANS_TYPE_8003 = "8003";
	/**
	 * IC卡公钥下载
	 */
	public static final String TRANS_TYPE_8004 = "8004";
	/**
	 * 查询余额
	 */
	public static final String TRANS_TYPE_2001 = "2001";
	/**
	 * 消费
	 */
	public static final String TRANS_TYPE_2002 = "2002";
	/**
	 * 消费撤销
	 */
	public static final String TRANS_TYPE_2003 = "2003";
	/**
	 * 消费退货
	 */
	public static final String TRANS_TYPE_2004 = "2004";
	/**
	 * 交易状态查询
	 */
	public static final String TRANS_TYPE_2005 = "2005";
	/**
	 * 电子凭证签名上送
	 */
	public static final String TRANS_TYPE_2006 = "2006";
	/**
	 * MPOS报文规范: 9001
	 */
	public static final String CAPITAL_POOL_9001 = "9001";
	/**
	 * POSP报文规范: 5001
	 */
	public static final String CAPITAL_POOL_5001 = "5001";
	/**
	 * 收单 平台流水号查询条件 /流水格式:不足16位前面补0
	 */
	public final static String SYSPARA_SERIAL_NO = "000010";
	
}
