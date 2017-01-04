package com.bestpay.posp.protocol;


import lombok.Getter;
import lombok.Setter;

/**
 * @DengPengHai
 */
@Setter
@Getter
public class FlowMessage {

	private String serialNo;
	private String YYYYMMDD;
	private String MMDD;
	private String hhmmss;
	private String MMDDhhmmss;
	//YYYYMMDD
	private String tranDate;
	//hhmmss
	private String tranTime;
	private String tranCode;
	private String tranType;
	private String tranFlag;
	private Long channelCode;
    private String origSerialNo;
    private String origSerialDate;
    private String origSerialTime;
    private String cardNo;
    /**
     * 转入卡信息
     */
    private String intoCard;
    private String cardType;
    private String cardBin;
    private String merchCode;
    private String mctName;
    private String mcc;
    private String termCode;
    private String batchNo;
	private String termSerialNo;
	private String cupsSerialNo;
	private String cupsDate;
    private String referNo;
	private String rcvBankCode;
    private String rcvBranchCode;
	private Double tranAmount;
	private String authNo;
    private String authAmount;
    private String conditionMode;
    private Double remainAmount;
    private String ip;
    private String unipay52;
    /**
     * 银联返回码
     */
    private String extsysRespCode;
    /**
     * 服务点输入方式吗
     */
    private String InputMode;
    
    /**
     * 电话号码
     */
    private String phoneNumber;
    
    /**
     * 被叫号码
     */
    private String calledNumber;
    /**
     * TPDU
     */
    private String tpdu;
    /**
	 * 63域打印广告
	 * @return
	 */
	private String field63;
	/**
	 * 基站信息类型
	 */
	private String baseStationType;
	/**
	 * 基站信息值
	 */
	private String baseStationValues;
	
	private String field90;
	/**
	 * 第三方商户终端号
	 */
	private String field32;
	/**
	 * 与绑定是否一致
	 */
	private String isMatch;
	/**
	 * 卡属性
	 */
    private String cardProperties;
	private String servAgent;
	private String detailRespCode;
	private String freePasswordSign;
}
