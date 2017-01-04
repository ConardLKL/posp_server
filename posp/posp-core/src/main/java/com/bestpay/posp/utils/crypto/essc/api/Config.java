package com.bestpay.posp.utils.crypto.essc.api;

public class Config {

	// 缓冲区大小
	public static int BUF_LEN = 4 * 1024 * 1024;

	// 整个报文头部的长度字节数
	// public static int ALL_LEN_LEN=8;
	public static int ALL_LEN_LEN = 2;

	// 报文中域数目的长度
	public static int AREA_COUNT_LEN = 3;

	// 报文中每个域名称的长度
	public static int AREA_NAME_LEN = 3;

	// 报文中每个域长度的长度
	// public static int AREA_LEN_LEN = 8;
	public static int AREA_LEN_LEN = 4;

	// 响应报文中应用id的位置
	// public static int API_ID_GEGIN = 0;
	public static int API_ID_GEGIN = 2;
	// 响应报文中应用id的长度
	public static int API_ID_LEN = 2;

	// 响应报文中服务代码的位置
	// public static int SVR_CODE_BEGIN = 2;
	public static int SVR_CODE_BEGIN = 4;
	// 响应报文中服务代码的长度
	public static int SVR_CODE_LEN = 3;

	// 响应报文中响应标识位置
	// public static int RESPFLAG_BEGIN = 5;
	public static int RESPFLAG_BEGIN = 7;
	// 响应报文中响应标识的长度
	public static int RESPFLAG_LEN = 1;

	// 响应报文中错误代码的位置
	public static int ERRCODE_GEGIN = 8;
	// 响应报文中错误代码的长度
	public static int ERRCODE_LEN = 6;

	// 如果出错再次发送报文次数
	public static int TRY_COUNT = 0;
}
