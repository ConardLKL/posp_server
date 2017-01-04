package com.bestpay.posp.constant;

public class POSPConstant {
	
	/**
	 * 交易成功
	 */
	public static final String POSP_00 = "00";
	/**
	 * MAC校验失败
	 */
	public static final String POSP_A0A0 = "A0A0";
	/**
	 * 超过此卡单笔预警金额限制
	 */
	public static final String POSP_A000 = "A000";
	/**
	 * 超过终端单笔预警金额限制
	 */
	public static final String POSP_A100 = "A100";
	/**
	 * 超过商户单笔预警金额限制
	 */
	public static final String POSP_A200 = "A200";
	/**
	 * 原交易失败，请联系发卡行
	 */
	public static final String POSP_A001 = "A001";
	/**
	 * 商户不存在
	 */
	public static final String POSP_A003 = "A003";
	/**
	 * 无效商户
	 */
	public static final String POSP_A103 = "A103";
	/**
	 * 无效第三方机构
	 */
	public static final String POSP_A203 = "A203";
	
	/**
	 * 不支持此交易
	 */
	public static final String POSP_A012 = "A012";
	/**
	 * 分期付款，不允许退货
	 */
	public static final String POSP_A112 = "A112";
	/**
	 * 第三方平台接入只支持公网交易
	 */
	public static final String POSP_A212 = "A212";
	/**
	 * 撤销卡号与原交易不匹配
	 */
	public static final String POSP_A014 = "A014";
	/**
	 * 无此发卡行
	 */
	public static final String POSP_A015 = "A015";
	/**
	 * 超过交易允许时间
	 */
	public static final String POSP_A022 = "A022";
	/**
	 * 原交易不存在
	 */
	public static final String POSP_A025 = "A025";
	/**
	 * 原交易已经冲正
	 */
	public static final String POSP_A125 = "A125";
	/**
	 * 原交易非正常状态
	 */
	public static final String POSP_A225 = "A225";
	/**
	 * 不支持隔日冲正
	 */
	public static final String POSP_A325 = "A325";
	/**
	 * 写卡通知-无原始交易
	 */
	public static final String POSP_A425 = "A425";
	/**
	 * 原交易日期不正确
	 */
	public static final String POSP_A525 = "A525";
	/**
	 * 批上送通知：查找原始通知类交易失败
	 */
	public static final String POSP_A625 = "A625";
	/**
	 * 原交易类型不正确
	 */
	public static final String POSP_A725 = "A725";
	/**
	 * 不支持隔日撤销
	 */
	public static final String POSP_A825 = "A825";
	/**
	 * 报文格式错误
	 */
	public static final String POSP_A030 = "A030";
	/**
	 * 磁道解密失败
	 */
	public static final String POSP_A130 = "A130";
	/**
	 * 不允许机构做此交易
	 */
	public static final String POSP_A040 = "A040";
	/**
	 * 请求的功能尚不支持
	 */
	public static final String POSP_A140 = "A140";
	/**
	 * 无效终端
	 */
	public static final String POSP_A058 = "A058";
	/**
	 * 主叫号码与系统登记号码不一致
	 */
	public static final String POSP_A158 = "A158";
	/**
	 * 该商户终端已注销
	 */
	public static final String POSP_A258 = "A258";
	/**
	 * 商户不支持该类型卡
	 */
	public static final String POSP_A358 = "A358";
	/**
	 * 不允许商户做此交易
	 */
	public static final String POSP_A458 = "A458";
	/**
	 * 不允许终端做此交易
	 */
	public static final String POSP_A558 = "A558";
	/**
	 * 该商户在黑名单中
	 */
	public static final String POSP_A658 = "A658";
	/**
	 * 该终端在黑名单中
	 */
	public static final String POSP_A758 = "A758";
	/**
	 * 超过此卡单笔最大金额设置
	 */
	public static final String POSP_A061 = "A061";
	/**
	 * 超过终端单笔最大金额设置
	 */
	public static final String POSP_A161 = "A161";
	/**
	 * 超过商户单笔最大金额设置
	 */
	public static final String POSP_A261 = "A261";
	/**
	 * 退货金额大于原始消费金额或剩余金额
	 */
	public static final String POSP_A361 = "A361";
	/**
	 * 超过卡日累计金额设置
	 */
	public static final String POSP_A461 = "A461";
	/**
	 * 超过终端日累计金额设置
	 */
	public static final String POSP_A561 = "A561";
	/**
	 * 超过商户日累计金额设置
	 */
	public static final String POSP_A661 = "A661";
	/**
	 * 该卡号在黑名单中
	 */
	public static final String POSP_A062 = "A062";
	/**
	 * 退货金额不正确
	 */
	public static final String POSP_A064 = "A064";
	/**
	 * 冲正交易与原交易金额不匹配
	 */
	public static final String POSP_A164 = "A164";
	/**
	 * 撤销金额不等于原交易金额
	 */
	public static final String POSP_A264 = "A264";
	/**
	 * 预授权完成金额错误
	 */
	public static final String POSP_A364 = "A364";
	/**
	 * POS批次与网络中心不一致，请重新签到
	 */
	public static final String POSP_A077 = "A077";
	/**
	 * 终端未签到，请签到后再签退
	 */
	public static final String POSP_A177 = "A177";
	/**
	 * 终端未签到
	 */
	public static final String POSP_A277 = "A277";
	/**
	 * 交易流水重复，请重新操作
	 */
	public static final String POSP_A094 = "A094";
	/**
	 * 系统异常
	 */
	public static final String POSP_AA96 = "AA96";
	/**
	 * 系统流程引擎异常
	 */
	public static final String POSP_A096 = "A096";
	/**
	 * 银联域转化异常
	 */
	public static final String POSP_A196 = "A196";
	/**
	 * 银联域返回转化异常
	 */
	public static final String POSP_A296 = "A296";
	/**
	 * 调用银联过程异常
	 */
	public static final String POSP_A396 = "A396";
	/**
	 * 交易控制出现故障
	 */
	public static final String POSP_A496 = "A496";
	/**
	 * 机构交易控制出现故障
	 */
	public static final String POSP_A596 = "A596";
	/**
	 * 商户黑名单控制出现故障
	 */
	public static final String POSP_A696 = "A696";
	/**
	 * 终端黑名单控制出现故障
	 */
	public static final String POSP_A796 = "A796";
	/**
	 * 卡号黑名单控制出现故障
	 */
	public static final String POSP_A896 = "A896";
	/**
	 * 计算原交易时间间隔异常
	 */
	public static final String POSP_A996 = "A996";
	/**
	 * PIN转加密异常
	 */
	public static final String POSP_B096 = "B096";
	/**
	 * 预计流水操作失败
	 */
	public static final String POSP_B196 = "B196";
	/**
	 * 更新当前流水操作失败
	 */
	public static final String POSP_B296 = "B296";
	/**
	 * 更新银联超时流水失败
	 */
	public static final String POSP_B396 = "B396";
	/**
	 * 更新原流水失败
	 */
	public static final String POSP_B496 = "B496";
	/**
	 * 更新管理类交易或者公共检查失败流水失败
	 */
	public static final String POSP_B596 = "B596";
	/**
	 * 失败时插入预计流水操作失败
	 */
	public static final String POSP_B696 = "B696";
	/**
	 * 签到获取TPK,TAK时加密机出现异常
	 */
	public static final String POSP_B796 = "B796";
	/**
	 * 加密机没有录入终端商户信息
	 */
	public static final String POSP_B896 = "B896";
	/**
	 * 签到60域数据组合失败
	 */
	public static final String POSP_B996 = "B996";
	/**
	 * 签到时更新终端或签到表失败
	 */
	public static final String POSP_C096 = "C096";
	/**
	 * 签退时更新终端或签到表失败
	 */
	public static final String POSP_C196 = "C196";
	/**
	 * IC卡参数下载异常
	 */
	public static final String POSP_C296 = "C296";
	/**
	 * IC卡参数查询异常
	 */
	public static final String POSP_C396 = "C396";
	/**
	 * CA公钥查询异常
	 */
	public static final String POSP_C496 = "C496";
	/**
	 * CA公钥下载异常
	 */
	public static final String POSP_C596 = "C596";
	/**
	 * 批上送,更新交易流水批结算状态失败
	 */
	public static final String POSP_C696 = "C696";
	/**
	 * 批上送脱机消费交易异常
	 */
	public static final String POSP_C796 = "C796";
	/**
	 * 批结算交易检查异常
	 */
	public static final String POSP_C896 = "C896";
	/**
	 * 批上送插入批上送表失败
	 */
	public static final String POSP_C996 = "C996";
	/**
	 * 终端未在中心或银行登记
	 */
	public static final String POSP_A097 = "A097";
	/**
	 * 银联返回超时
	 */
	public static final String POSP_A098 = "A098";
	/**
	 * PIN转加密失败
	 */
	public static final String POSP_A099 = "A099";

}
