package com.bestpay.posp.constant;

/**
 * 
 * @author 
 *
 */
public class PosConstant {
	
	/**
	 * 消费类
	 */
	public static final String MSG_TYPE_0200_00 = "0200";
	
	/**
	 * 预授权撤销
	 */
	public static final String MSG_TYPE_0200_20 = "0100";
	
	/**
	 * 冲正类
	 */
	public static final String MSG_TYPE_0400_00 = "0400";
	/**
	 * 第三方管理类
	 */
	public static final String MSG_TYPE_0800_00 = "0800";
	/**
	 * posp平台支持交易
	 */
	public static final String MANAGEMENT_TYPES = "0800004.0800006.0030800004.0820002.0500201.0320000.0320201.0320202.0320203.0320204.0320205.0320206.0320207.0800380.0030800380.0800370.0030800370.0800371.0820372.0820373.0800373.0820382.0800381.0820362.0800394.0800395";
	/**
	 * xml管理类交易服务代码名称
	 */
	public static final String XML_TRANS_TYPES = "REQ0100.REQ0101.REQ1001.REQ1002.REQ1003.REQ1004.REQ1005.REQ1006.REQ1007.REQ1008";
	 /**
     * 终端交易类交易服务代码名称
     */
	public static final String TRANSACTION_TYPES = "0200310001.0200000022.0400000022.0200200023.0400200023.0220200025.0100030610.0400030610.0100200611.0400200611.0200000620.0220000624.0400000620.0200200621.0400200621.0200609145.0400609145.0200629147.0400629147.0200639146.0400639146.0200179151.0400179151.0200000036.0620951";
	/**
	 * 电子化凭条服务代码名称
	 */
	public static final String TRANS_TYPE_SLIP = "0820800.0820801.0820802.0820803.0820804.0820805.0820806.0820807.0820808.0820809";
	 /**
     * 需要流水检查服务代码名称
     */
	public static final String FLOW_CHECK_TYPE = "0200000022.0200200023.0220200025.0100030610.0200000620.0220000624.0200609145.0200639146.0200179151.0200010055";
	/**
	 * 余额查询
	 */
	public static final String TRANS_TYPE_0200310001 = "0200310001";
	/**
	 * 消费
	 */
	public static final String TRANS_TYPE_0200000022 = "0200000022";
	/**
	 * 消费冲正
	 */
	public static final String TRANS_TYPE_0400000022 = "0400000022";
	/**
	 * 消费撤销
	 */
	public static final String TRANS_TYPE_0200200023 = "0200200023";
	/**
	 * 消费撤销冲正
	 */
	public static final String TRANS_TYPE_0400200023 = "0400200023";
	/**
	 * 退货
	 */
	public static final String TRANS_TYPE_0220200025 = "0220200025";
	/**
	 * 预授权
	 */
	public static final String TRANS_TYPE_0100030610 = "0100030610";
	/**
	 * 预授权冲正
	 */
	public static final String TRANS_TYPE_0400030610 = "0400030610";
	/**
	 * 预授权撤销
	 */
	public static final String TRANS_TYPE_0100200611 = "0100200611";
	/**
	 * 预授权撤销冲正
	 */
	public static final String TRANS_TYPE_0400200611 = "0400200611";
	/**
	 * 预授权完成请求
	 */
	public static final String TRANS_TYPE_0200000620 = "0200000620";
	/**
	 * 预授权完成通知
	 */
	public static final String TRANS_TYPE_0220000624 = "0220000624";
	/**
	 * 预授权完成冲正
	 */
	public static final String TRANS_TYPE_0400000620 = "0400000620";
	/**
	 * 预授权完成撤销
	 */
	public static final String TRANS_TYPE_0200200621 = "0200200621";
	/**
	 * 预授权完成撤销冲正
	 */
	public static final String TRANS_TYPE_0400200621 = "0400200621";
	/**
	 * 指定账户圈存
	 */
	public static final String TRANS_TYPE_0200609145 = "0200609145";
	/**
	 * 指定账户圈存冲正
	 */
	public static final String TRANS_TYPE_0400609145 = "0400609145";
	/**
	 * 非指定账户圈存
	 */
	public static final String TRANS_TYPE_0200629147 = "0200629147";
	/**
	 * 非指定账户圈存冲正
	 */
	public static final String TRANS_TYPE_0400629147 = "0400629147";
	/**
	 * 现金充值
	 */
	public static final String TRANS_TYPE_0200639146 = "0200639146";
	/**
	 * 现金充值冲正
	 */
	public static final String TRANS_TYPE_0400639146 = "0400639146";
	/**
	 * 现金充值撤销
	 */
	public static final String TRANS_TYPE_0200179151 = "0200179151";
	/**
	 * 现金充值撤销冲正
	 */
	public static final String TRANS_TYPE_0400179151 = "0400179151";
	/**
	 * 脱机消费上传
	 */
	public static final String TRANS_TYPE_0200000036 = "0200000036";
	/**
	 * 签到
	 */
	public static final String TRANS_TYPE_0800004 = "0800004";
	/**
	 * 国密签到
	 */
	public static final String TRANS_TYPE_0800006 = "0800006";
	/**
	 * 签退
	 */
	public static final String TRANS_TYPE_0820002 = "0820002";
	/**
	 * 批结算
	 */
	public static final String TRANS_TYPE_0500201 = "0500201";
	/**
	 * （对账平）IC卡联机交易明细
	 */
	public static final String TRANS_TYPE_0320203 = "0320203";
	/**
	 * （对账不平）IC卡联机交易明细
	 */
	public static final String TRANS_TYPE_0320205 = "0320205";
	/**
	 * 脚本通知
	 */
	public static final String TRANS_TYPE_0620951 = "0620951";
	/**
	 * 电子化凭条
	 */
	public static final String TRANS_TYPE_0820800 = "0820800";
	/**
	 * 国密公钥查询
	 */
	public static final String TRANS_TYPE_0820373 = "0820373";
	/**
	 * 国密公钥下载
	 */
	public static final String TRANS_TYPE_0800373 = "0800373";
}
