package com.bestpay.posp.utils.crypto.essc.api;

public class EsscFldTagDef {
	public  int conEsscFldKeyName = 1; // 密钥的名称
	public  int conEsscFldMacData = 21; // MAC数据
	public  int conEsscFldMac = 22; // MAC
	public  int conEsscFldDirectHsmCmdReq = 100; // 直接的加密机指令
	public  int conEsscFldDirectHsmCmdRes = 101; // 直接的加密机指令响应
	public  int conEsscFldKeyValue = 61; // 密钥值
	public  int conEsscFldSecondKeyCheckValue = conEsscFldKeyValue+1;// 第二个密钥校验值
	public  int conEsscFldKeyCheckValue = 51; // 密钥校验值
	public  int conEsscFldZMKName = 11; // ZMK密钥的名称
	public  int conEsscFldKeyLenFlag = 203; // 密钥长度标识
	public  int conEsscFldFirstWKName = conEsscFldKeyName; // 第一个工作密钥的名称
	public  int conEsscFldSecondWKName = conEsscFldKeyName + 1; // 第二个工作密钥的名称
	public  int conEsscFldEncryptedPinByZPK = 33; // ZPK加密的PIN密文
	public  int conEsscFldEncryptedPinByZPK1 = conEsscFldEncryptedPinByZPK; // ZPK1加密的PIN密文
	public  int conEsscFldEncryptedPinByZPK2 = conEsscFldEncryptedPinByZPK + 1; // ZPK2加密的PIN密文
	public  int conEsscFldAccNo = 41; // 账号
	public  int conEsscFldAccNo1 = conEsscFldAccNo; // 账号1
	public  int conEsscFldAccNo2 = conEsscFldAccNo + 1; // 账号2

	public  int conEsscFldCardPeriod = 72; // 卡有效期
	public  int conEsscFldServiceID = 73; // 服务代码
	public  int conEsscFldVisaCVV = 71; // VisaCVV

	public  int conEsscFldPlainPin = 31; // PIN明文
	public  int conEsscFldVisaPVV = 36; // Visa PVV
	public  int conEsscFldEncryptedPinByLMK0203 = 35; // LMK0203加密的PIN密文
	public  int conEsscFldForPinLength = 205; // PIN的长度
	public  int conEsscFldIBMPinOffset = 37; // IBM PinOffset
	public  int conEsscFldIDOfApp = 207; // 应用编号
	public  int conEsscFldSignData = 91; // 签名数据
	public  int conEsscFldSignDataPadFlag = 93; // 签名数据的填充方式
	public  int conEsscFldSign = 92; // 签名
	public  int conEsscFldAlgorithmMode = 214; // 算法标志
	public  int conEsscFldAlgorithm02Mode = 215; // 第二种算法标志
	public  int conEsscFldPlainData = 81; // 明文数据
	public  int conEsscFldIV = 213; // 初始向量
	public  int conEsscFldCiperData = 82; // 密文数据
	public  int conEsscFldHashData = 83; // HASH数据，2008/4/10，added
	public  int conEsscFldHashDegist = 84; // Hash摘要，2008/4/10，added
	public  int conEsscFldLengthOfKey = 208; // 密钥长度
	public  int conEsscFldRandNum = 303; // 随机数
	public  int conEsscFldRandNum2 = 304; // 随机数
	public  int conEsscDisperseData = 426; // 离散数据
	public  int conEsscFldKeyVersion = 410; // 密钥版本号
	public  int conEsscFldSecondKeyVersion = 409; // 密钥版本号 add by zhaorb
														// 20121029

	public  int conEsscFldHsmGrpID = 201; // 离散密钥方案
	public  int conEsscFldKeyTypeFlag = 221; // 脱机PIN模式
	public  int conEsscFldAlgorithm01Mode = 214;
	public  int conEsscFldData = 81;

	public  int conEsscFldARQC = 313; // ARQC
	public  int conEsscFldARPC = 314; // ARPC
	public  int conEsscFldARC = 315; // ARC

	// V-STK
	public  int vstkSignData = 801;//
	public  int vstkApp = 001;//
	public  int vstkSigntoFileData = 802;//
	public  int vstkFldApp = 002;
	public  int vstkFldenvelope = 803;
	public  int conEsscFldKeyIndex = 210;// 密钥索引

	public int conEsscFldPinByRsaPK = 38;// RSAPK加密的PIN
	public int conEsscFldRemark = 998;             // 备注信息
	public int conEsscFldResID = 997;             // 资源标识
	public int conEsscFldPinOffset = 32;//PINOffset
	public int conEsscFldHsmIPAddrList = 211;             // 密码机IP地址列表
}
