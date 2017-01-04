package com.bestpay.posp.utils.crypto.essc.api;

public class NodeAPI {
	String hsmIP;
	int hsmPort;
	int timeOut;
	String gunionIDOfUnionAPI;
	
	public NodeAPI(String ip, int port, int timeOut, String gunionIDOfUnionAPI){
		hsmIP = ip;
		hsmPort = port;
		this.timeOut = timeOut;
		this.gunionIDOfUnionAPI = gunionIDOfUnionAPI;
	}
	
	/* 产生VISA卡校验值CVV
	功能：产生VISA卡校验值CVV 
	输入参数：
		nodeName,节点名称
		period,有效期
		serviceCode,服务码
		accNo,卡号
		返回值：
		cvv, 校验值
	*/
	public String NodeGenerateCVV(String nodeName,String period,String serviceCode,String accNo) throws Exception {
		NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "CVK") ;
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		return unionAPI.UnionGenerateCVV(fullKeyName, period, serviceCode, accNo);		
	}
	
	/*
	验证VISA卡校验值CVV 
	功能：验证VISA卡校验值CVV
	输入参数：
		nodeName,节点名
		period,有效期
		serviceCode,服务码
		accNo,卡号
		cvv, 校验值
	输出参数：
		无
	返回值
		<0，函数执行失败，值为失败的错误码
		=0，函数执行成功
	*/

	public int NodeVerifyCVV(String nodeName,String period,String serviceCode,String accNo,String cvv) throws Exception {
		NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "CVK");
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		if(unionAPI.UnionVerifyCVV(fullKeyName, period, serviceCode, accNo, cvv)== 0)
			return 0;
		else
			throw new Exception("in NodeVerifyCVV:: UnionVerifyCVV!");
	}

	/*
	读取库中的密钥
	功能： 读取库中的密钥，并使用同一属主的ZMK加密输出
	输入参数：
		nodeName，节点名
		type,     密钥类型
	返回值：
	    数组 results;
		    results[0] = keyValue, 密钥密文（ZMK加密）
		    results[1] = checkValue, 校验值（加密全0）
	*/
    public String[] NodeReadKey(String nodeName, String type) throws Exception{
    	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		return unionAPI.UnionReadKey(fullKeyName);
    }

    /*
    生成指定的密钥替换库中的密钥
    功能：生成指定的密钥替换库中的密钥，并使用同一属主的ZMK加密输出
    输入参数：
    	nodeName，节点名
    	type,     密钥类型
    返回值：
         数组: results;
    	results[0] = keyValue, 密钥密文（ZMK加密）
    	results[1] = checkValue, 校验值（加密全0）
     */
    public String[] NodeGenerateKey(String nodeName,String type) throws Exception{
    	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		return unionAPI.UnionGenerateKey(fullKeyName);
    }
    
    /*
    生成ZMK加密的密钥
   功能： 生成ZMK加密的密钥，这个函数与NodeGenerateKey的区别在
   		于，后者生成的密钥替换了库中的密钥，而本函数只生成一个密钥
   		值，并不替换库中的任何密钥。
   输入参数：
   	nodeName，ZMK节点名，
   	keyLenFlag 密钥长度标志：	'0'--64,'1'---128,'2'----192

   返回值：
       数组:results
   	        results[0] = keyValue, 密钥密文（ZMK加密）
   	        results[1] = checkValue,，校验值（加密全0）
   */
   public String[] NodeGenerateKeyByZmk(String nodeName,int keyLenFlag) throws Exception{
   	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zmk");
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateKeyByZMK(fullKeyName, keyLenFlag);
   }
   
   /*
   生成指定的密钥替换库中的密钥
   功能： 生成指定的密钥替换库中的密钥，与NodeGenerateKey的区别
   		在于：后者使用同一属主的ZMK加密输出密钥
   输入参数：
   	nodeName，节点名
   	type,     密钥类型
   输出参数：
   	无
   返回值：
   	<0，是错误码，
   	0，成功
   */
   public int NodeGenerateKeyMerely(String nodeName,String type) throws Exception {
	   	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		if(unionAPI.UnionGenerateKeyMerely(fullKeyName)==0)
			return 0;
		else
			throw new Exception("in NodeGenerateKeyMerely:: UnionGenerateKeyMerely!");
   }
   
   /*
   将ZMK加密的密钥存储到库中
   功能：将ZMK加密的密钥存储到库中
   输入参数：
   	nodeName, 节点名
   	type,     密钥类型
   	keyValue, 密钥密文（ZMK加密）
   	checkValue,校验值（加密全0）
   输出参数：
   	无
   返回值：
   	<0，是错误码，
   	0，成功
   */
   public int NodeStoreKey(String nodeName,String type,String keyValue,String checkValue) throws Exception {
	    NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		if (unionAPI.UnionStoreKey(fullKeyName, keyValue, checkValue)==0)
			return 0;
		else
			throw new Exception("in NodeStoreKey:: UnionStoreKey!");
   }
   
   /*
   生成MAC
   功能：使用指定密钥生成MAC,算法是ansi x9.9/x9.19
   输入参数：
   nodeName：节点名称
   lenOfMacData：MAC数据的长度,以十进制表示, <=500
   macData：MAC数据,长度变长, 字符串, 以'\0'结束
   返回值：mac
   mac：MAC值,长度8字节字符串(ASCII), 以'\0'结束
  */
   public String NodeGenerateMac(String nodeName,int lenOfMacData,String macData) throws Exception{
	    NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
		String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zak");
		UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
		return unionAPI.UnionGenerateMac(fullKeyName, lenOfMacData, macData);
   }
   
   /*
   验证报文MAC
  功能：使用指定密钥验证MAC,算法是x9.9/x9.19
  输入参数：fullKeyName,lenOfMacData,macData
  nodeName：节点名称
  lenOfMacData：MAC数据的长度,以十进制表示, <=500
  macData：MAC数据,长度变长, 字符串, 以'\0'结束
  mac：MAC值,(ASCII), 以'\0'结束
  输出参数：无
  返回值：<0,是错误码。0 成功。
  */
  public int NodeVerifyMac(String nodeName,int lenOfMacData,String macData,String mac) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zak");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  if(unionAPI.UnionVerifyMac(fullKeyName, lenOfMacData, macData, mac)==0)
		  return 0;
	  else
		  throw new Exception("in NodeVerifyMac:: UnionVerifyMac!");
  }

  /*
  按银联标准产生报文MAC
  功能：这个函数,使用指定密钥生成MAC,算法是中国银联标准
  输入参数：
  nodeName：节点名
  lenOfMacData：MAC数据的长度,以十进制表示, <=500
  macData：MAC数据,长度变长, 字符串, 以'\0'结束
  返回值：mac
  mac：MAC值,长度16字节字符串(ASCII), 以'\0'结束
  */
  public String NodeGenerateChinaPayMac(String nodeName,int lenOfMacData,String macData) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zak");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionGenerateChinaPayMac(fullKeyName, lenOfMacData, macData);
  }
  
  /*
  按银联标准验证报文MAC
  功能：这个函数,使用指定密钥验证MAC,算法是中国银联标准
  输入参数：
  nodeName：节点名
  lenOfMacData：MAC数据的长度,以十进制表示, <=500
  macData：MAC数据,长度变长, 字符串, 以'\0'结束
  mac：MAC值,(ASCII), 以'\0'结束
  输出参数：无
  返回值：
  	<0,是错误码。
  	0, 成功。
  */
  public int NodeVerifyChinaPayMac(String nodeName,int lenOfMacData,String macData,String mac) throws Exception{
      NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zak");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  if(unionAPI.UnionVerifyChinaPayMac(fullKeyName, lenOfMacData, macData, mac)==0)
  		  return 0;
  	  else
  		  throw new Exception("in NodeVerifyChinaPayMac:: UnionVerifyChinaPayMac!");
    }

  /*
  使用指定ZMK加密的密钥生成MAC
  功能：这个函数,使用指定ZMK加密的密钥生成MAC,算法是中国银联标准
  输入参数：
  nodeName：节点名
  keyByZmk: 是ZMK加密的密钥
  lenOfMacData：MAC数据的长度,以十进制表示, <=500
  macData：MAC数据,长度变长, 字符串, 以'\0'结束
  返回值：mac
  mac：MAC值,长度16字节字符串(ASCII), 以'\0'结束
  */
  public String NodeGenerateChinaPayMacUsingKeyByZmk(String nodeName,String keyByZmk,int lenOfMacData,String macData) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zmk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  return unionAPI.UnionGenerateChinaPayMacUsingKeyByZmk(fullKeyName, keyByZmk, lenOfMacData, macData);
  }
  
  /*
  将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文
  功能： 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文，
  		加密算法为Ansi x9.8/x9.18
  输入参数：
  nodeName1：节点名1
  nodeName2：节点名2
  pinBlock1：源密钥加密的PIN密文(PIN格式为ANSI9。8),长度16字节字符串(ASCII), 以'\0'结束。
  accNo：源账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
  返回值：pinBlock2,
  pinBlock2：目的成员行机构号加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
  */
  public String NodeTranslatePin(String nodeName1,String nodeName2,String pinBlock1,String accNo) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(nodeName1, "zpk");
  	  String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(nodeName2, "zpk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  return unionAPI.UnionTranslatePin(fullKeyName1, fullKeyName2, pinBlock1, accNo);
  }
  
  /*
  使用指定的密钥加密的PIN明文
  功能： 使用指定的密钥加密的PIN明文，算法为ansi x9.8/x9.18，这个
  		函数,将用户的PIN明文加密成PIN密文。
  输入参数：
  nodeName: 节点名称
  clearPin：PIN明文,长度6字节字符串(ASCII), 以'\0'结束。
  accNo：账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
  返回值：pinBlock
  pinBlock：上送中心加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
  */
  public String NodeEncryptPin(String nodeName,String clearPin,String accNo) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zpk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  return unionAPI.UnionEncryptPin(fullKeyName, clearPin, accNo);
  }
  
  /*
  由一个ZPK加密的PIN导出一个由PVK生成的PVV
  功能：由一个ZPK加密的PIN导出一个由PVK生成的PVV
  		从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
  		ZPK加密PIN采用的加密标准为ANSIX9.8。PVK生成PVV（PIN
  		Verification Value），采用的加密标准为Visa Method。
  输入参数：
  	zpkNodeName,ZPK节点名称
  	pvkNodeName,PVK节点名称
  	pinBlock，ZPK加密的PIN
  	accNo,账号
  返回值：
  	pvv
  */
  public String NodeDerivePVVFromPinByZpk(String zpkNodeName,String pvkNodeName,String pinBlock,String accNo) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
  	  String pvkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  return unionAPI.UnionDerivePVVFromPinByZpk(zpkFullKeyName, pvkFullKeyName, pinBlock, accNo);
  }
  
  /*
  根据一个ZPK加密的PIN密文和PVK生成的PVV验证PIN
  功能：根据一个ZPK加密的PIN密文和PVK生成的PVV验证PIN
  		从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
  		ZPK加密PIN采用的加密标准为ANSIX9.8。PVK生成PVV（PIN
  		Verification Value），采用的加密标准为Visa Method。
  输入参数：
  	zpkNodeName,ZPK节点名称
  	pvkNodeName,PVK节点名称
  	pinBlock，ZPK加密的PIN
  	accNo,账号
  	pvv,VisaPVV
  输出参数：
  	无
  返回值：
  	<0,是错误码。
  	0, 成功。
  */
  public int NodeVerifyPVVAndPinByZpk(String zpkNodeName,String pvkNodeName,String pinBlock,String accNo,String pvv) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
  	  String pvkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  if(unionAPI.UnionVerifyPVVAndPinByZpk(zpkFullKeyName, pvkFullKeyName, pinBlock, accNo, pvv) == 0)
  		  return 0;
  	  else
  		  throw new Exception("in NodeVerifyPVVAndPinByZpk:: UnionVerifyPVVAndPinByZpk!");
  }

  /*
  将一个ZPK加密的PIN转换为由LMK加密
  功能：将一个ZPK加密的PIN转换为由LMK加密
  		从一个ZPK加密的PIN，导出一个由PVK加密生成的PVV。
  		ZPK加密PIN采用的加密标准为ANSIX9.8。
  输入参数：
  	zpkNodeName,ZPK节点名称
  	pinBlock，ZPK加密的PIN
  	accNo,账号
  返回值：
  	pinByLmk,LMK0203加密的PIN
  */
  public String NodeDerivePinByLmkFromPinByZpk(String zpkNodeName,String pinBlock,String accNo) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
  	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
  	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
  	  return unionAPI.UnionDerivePinByLmkFromPinByZpk(zpkFullKeyName, pinBlock, accNo);
  }
  
  /*
  将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文
 功能： 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文
 		与NodeTranslatePin的区别在于，这个函数使用了两个账号，即
 		源目账号不同。 加密算法为Ansi x9.8/x9.18
 输入参数：
 nodeName1：节点名1
 nodeName2：节点名2
 pinBlock1：源密钥加密的PIN密文(PIN格式为ANSI9。8),长度16字节字符串(ASCII), 以'\0'结束。
 accNo1：源账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
 accNo2：目的账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
 返回值：pinBlock2,
 pinBlock2：目的成员行机构号加密的PIN密文(PIN格式为ANSI9.8), 长度16字节字符串(ASCII), 以'\0'结束
 */
 public String NodeTranslatePinWith2AccNo(String nodeName1,String nodeName2,String pinBlock1,String accNo1,String accNo2) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
 	  String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(nodeName1, "zpk");
 	  String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(nodeName2, "zpk");
 	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
 	  return unionAPI.UnionTranslatePinWith2AccNo(fullKeyName1, fullKeyName2, pinBlock1, accNo1, accNo2);
 }
 
 /*
 随机产生一个PIN
 功能： 随机产生一个PIN 401，获得LMK加密的密文
 输入参数：
 	pinLen,PIN长度
 	accNo：账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
 返回值：
 	pinByLmk,LMK0203加密的密文
 */
 public String NodeGeneratePinRandomly(int pinLen,String accNo) throws Exception{
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	 return unionAPI.UnionGeneratePinRandomly(pinLen, accNo);
 }
 
 /*
 由ZPK加密的PIN导出一个PIN的Offset
 功能：由ZPK加密的PIN导出一个PIN的Offset
 		从一个ZPK加密的PIN，导出一个由PVK加密生成的PinOffset。
 		ZPK加密PIN采用的加密标准为ANSIX9.8。用IBM方式产生一个
 		PIN的PIN Offset。
 输入参数：
 	zpkNodeName,ZPK密钥名称
 	pvkNodeName,PVK密钥名称
 	pinBlock，ZPK加密的PIN
 	accNo,账号
  返回值：
 	pinOffset
 */
 public String NodeDerivePinOffsetFromPinByZpk(String zpkNodeName,String pvkNodeName,String pinBlock,String accNo) throws Exception{
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
	  String pvkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDerivePinOffsetFromPinByZpk(zpkFullKeyName, pvkFullKeyName, pinBlock, accNo);
 }
  
 /*
 通过ZPK加密的PIN和PVK生成的IBMPINOFFSET验证密码
 功能：通过ZPK加密的PIN和PVK生成的IBMPINOFFSET验证密码
 		ZPK加密PIN采用的加密标准为ANSIX9.8。
 		PVK生成PinOffset（PIN Verification Value），采用的加密标准为
 		Visa Method。
 输入参数：
 	zpkNodeName,ZPK节点名称
 	pvkNodeName,PVK节点名称
 	pinBlock，ZPK加密的PIN
 	accNo,账号
 	pinOffset,IBM算法
 输出参数：
 	无
 返回值：
 	<0,是错误码。
 	0 成功。
 */
 public int NodeVerifyPinOffsetAndPinByZpk(String zpkNodeName,String pvkNodeName,String pinBlock,String accNo,String pinOffset) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
	  String pvkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  if(unionAPI.UnionVerifyPinOffsetAndPinByZpk(zpkFullKeyName, pvkFullKeyName, pinBlock, accNo, pinOffset)==0)
		  return 0;
	  else
		  throw new Exception("in NodeVerifyPinOffsetAndPinByZpk:: UnionVerifyPinOffsetAndPinByZpk!");
 }
 
 /*
 使用LMK0203加密的PIN明文
 功能： 使用LMK0203加密的PIN明文
 		这个函数,将用户的PIN明文加密成PIN密文。
 输入参数：
 	pinCyperLen,PIN密文长度
 	clearPin：PIN明文,长度6字节字符串(ASCII), 以'\0'结束。
 	accNo：账号/卡号,长度13-19字节字符串(ASCII), 以'\0'结束;卡号为实际卡号;账号和卡号填16个ASCII'0',表示无账号运算。
 返回值：
 	pinByLmk：LMK加密的PIN密文
 */
 public String NodeEncryptPinByLmk(int pinCyperLen,String clearPin,String accNo) throws Exception{
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	 return unionAPI.UnionEncryptPinByLmk(pinCyperLen, clearPin, accNo);
 }
 
 /*
 通过ZPK加密的PIN和LMK加密的PIN验证密码
功能：通过ZPK加密的PIN和LMK加密的PIN验证密码
		ZPK加密PIN采用的加密标准为ANSIX9.8。
输入参数：
	zpkNodeName,ZPK节点名称
	pinBlock，ZPK加密的PIN
	accNo,账号
	pinByLmk,IBM算法
输出参数：
	无
返回值：<0,是错误码。0 成功。
*/
public int NodeVerifyPinByLmkAndPinByZpk(String zpkNodeName,String pinBlock,String accNo,String pinByLmk) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionVerifyPinByLmkAndPinByZpk(zpkFullKeyName, pinBlock, accNo, pinByLmk);
}

/*
使用指定应用的私钥进行签名
功能：使用指定应用的私钥进行签名
输入参数：
	nodeName,节点名
	lenOfData,签名数据的长度
	data，签名数据
	flag，数据填充方式: 0,如果数据不是密钥长度的整倍数,后面补0x00；1,PKCS填充方式
	hashID，HASH算法标志, 如果采用SJL06 38指令：‘0’：MD5，‘1’：SHA-1
返回值：
	sign，签名
*/
public String NodeGenerateSignature(String nodeName,String flag,String hashID,int lenOfData,String data) throws Exception{
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionNewGenerateSignature(nodeName, flag, hashID, lenOfData, data);
}
 
/*
使用指定公钥验证
功能：使用指定公钥验证
输入参数：
	nodeName,节点名称
	lenOfData,签名数据的长度
	data，签名数据
	lenOfSign，签名长度
	flag，数据填充方式: 0,如果数据不是密钥长度的整倍数,后面补0x00；1,PKCS填充方式
	hashID，HASH算法标志, 如果采用SJL06 38指令：‘0’：MD5，‘1’：SHA-1
	sign，签名
输出参数：
	无
返回值：
	<0,是错误码。
	0, 签名验证成功
*/
public int NodeVerifySignature(String nodeName,String flag,String hashID,int lenOfData,String data,String sign,int lenOfSign) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionNewVerifySignature(fullKeyName, flag, hashID, lenOfData, data, sign, lenOfSign);
}

/*
将指定ZMK加密的密钥存储到库中
功能： 将指定ZMK加密的密钥存储到库中
输入参数：
	nodeName,节点名
	type,     密钥类型
	zmkNodeName,是ZMK节点的名称
	keyValue, 密钥密文（ZMK加密）
	checkValue,校验值（加密全0）
输出参数：
	无
返回值：
	<0，是错误码，
	0, 成功
*/
public int NodeStoreKeyBySpecZmk(String nodeName,String type,String zmkNodeName,String keyValue,String checkValue) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  String zmkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zmkNodeName, "zmk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  if(unionAPI.UnionStoreKeyBySpecZmk(fullKeyName, zmkFullKeyName, keyValue, checkValue)==0)
		  return 0;
	  else
		  throw new Exception("in NodeStoreKeyBySpecZmk:: UnionStoreKeyBySpecZmk");
}

//============================== 数据加解密服务 ==============================
/*
函数名称UnionEncryptData
功能使用指定的密钥加密数据
输入参数fullKeyName：加密密钥的名称，密钥用于加密数据
                clearDataLen：明文数据的长度
                clearData：明文数据
                initIV：初始化向量，可选；若不需要，填“NULL”
                arithmeticFlag：算法标志，可选，“0”，ECB，“1”，CBC；
                若不需要，填“NULL”。
返回值：        cryptograph：加密得到的密文数据，密文数据对应的明文是：
                4字节明文长度+明文+补位‘0’

密钥            1．fullKeyName：加密密钥，加密明文数据生成密文
服务代码        703
*/
public String NodeEncryptData(String nodeName,int clearDataLen,String clearData,String initIV,String arithmeticFlag) throws Exception
{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "edk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionEncryptData(fullKeyName, clearDataLen, clearData, initIV,arithmeticFlag);

}

/*
函数名称	NodeDecryptData
功能		使用指定的密钥解密数据
输入参数	nodeName：解密密钥的节点名称，密钥用于解密数据
		cryptographLen：密文数据的长度
		cryptograph：待解密的密文数据，密文数据对应的明文是：
		4字节明文长度+明文+补位‘0’
		initIV：初始化向量，可选；若不需要，填“NULL”
		arithmeticFlag：算法标志，可选，
		“0”，ECB，“1”，CBC；若不需要，填“NULL”。
输出参数	clearData：解密出来的明文数据
返回值		<0：函数执行失败，值为失败的错误码
		>0：函数执行成功，返回明文clearData的长度
密钥		1．XX.nodeName.edk：解密密钥，解密密文数据得到明文
服务代码	704
*/
public String NodeDecryptData(String nodeName,int cryptographLen,String cryptograph,String initIV,String arithmeticFlag) throws Exception
{
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "edk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDecryptData(fullKeyName, cryptographLen, cryptograph, initIV,arithmeticFlag);
}

//对字符串Hash计算摘要 520
/*
输入参数：
	method,hashdata,datalen,分别为hash算法（01,02,03,04)，数据，数据长度
	
输出参数：
	hashresult
返回值：<0，是错误码，否则成功
*/
public String NodeHash(String method,String hashdata,int datalen) 
{
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionHash(method,hashdata,datalen);
}

// add by liangqf 2014-01-23
/**
 * 
 * @param nodeName
 * @return
 * @throws Exception
 */
public String NodeReadPK(String nodeName) throws Exception{
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionReadPK(fullKeyName);
}
/**
 * 将指定的密钥使用指定的ZMK加密输出 288
 * @param nodeName 节点名称
 * @param type 密钥的类型
 * @param zmkNodeName ZMK节点的名称
 * @return
 * @throws Exception
 */
public String[] NodeReadKeyBySpecZmk(String nodeName,String type,String zmkNodeName) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  String zmkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zmkNodeName, "zmk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionReadKeyBySpecZmk(fullKeyName, zmkFullKeyName);
}
/**
 * 从远程申请下载指定密钥的当前密钥值  291
 * @param nodeName 要下载的密钥的节点名称
 * @param type 密钥的类型
 * @return
 * @throws Exception
 */
public int NodeApplyCurrentKey(String nodeName,String type) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionApplyCurrentKey(fullKeyName);
}
/**
 * 从远程申请下载指定密钥的新密钥值 292
 * @param nodeName 要下载的密钥的节点名称
 * @param type 密钥的类型
 * @return
 * @throws Exception
 */
public int NodeApplyNewKey(String nodeName,String type) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionApplyNewKey(fullKeyName);
}
/**
 * 将本地平台的指定密钥的当前值分发到远程平台（293）
 * @param nodeName 节点名称
 * @param type 密钥的类型
 * @return
 * @throws Exception
 */
public int NodeDeployCurrentKey(String nodeName,String type) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDeployCurrentKey(fullKeyName);
}
/**
 * 在本地平台生成指定密钥的新值并替换库中的密钥，然后将新值分发到远程平台（294）
 * @param nodeName 要分发的密钥的节点名称
 * @param type 密钥的类型
 * @return
 * @throws Exception
 */
public int NodeDeployNewKey(String nodeName,String type) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, type);
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDeployNewKey(fullKeyName);
}
/**
 * 使用指定的密码机组生成一对RSA密钥对（211）
 * @param vkIndex 私钥索引号，即私钥在密码机中的存储索引，范围在‘00’－‘19’之间。
 * @param lenOfRsaPair RSA密钥对的长度，其值可以为‘512’、‘1024’、‘2048’、‘4096’和‘8192’
 * @return
 */
public String NodeGenerateRsaPairAtHsmGrp(String vkIndex, int lenOfRsaPair) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateRsaPairAtHsmGrp(vkIndex,lenOfRsaPair);
}
/**
 * 为指定的应用生成一对RSA密钥对（212）
 * @param idOfApp 应用编号
 * @return 输出的裸的公钥
 */
public String NodeGenerateRsaPairOfApp(String idOfApp) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateRSA(idOfApp);
}
/**
 * 生成PKCS10应用的RSA密钥对（512）
 * @param idOfApp 应用编号
 * @param keyLenFlag 密钥长度标识，其值可以为‘512’、‘1024’、‘2048’和‘0’。长度标识为‘0’表示不产生密钥，而是取存在文件里的公钥输出
 * @return 输出的公钥
 */
public String NodeGenerateRsaPairForPKCS10(String idOfApp, int keyLenFlag) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateRsaPairForPKCS10(idOfApp,keyLenFlag);
}
/**
 * 将一个ZPK加密的PIN密文转换为另一个ZPK加密的PIN密文（含两个账号，两种算法）（510）
 * @param nodeName1 源节点名称
 * @param nodeName2 目的节点名称
 * @param pinBlock1 源ZPK密钥加密的PIN密文，长度16字节字符串(ASCII)，以'\0'结束。
 * @param accNo1 源账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param accNo2 目的账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param arithmeticMode1 源加密算法，取值为01、02、03、04、05，算法具体说明参见密码机指令集。
 * @param arithmeticMode2 目的加密算法，取值为01、02、03、04、05，算法具体说明参见密码机指令集。
 * @return
 * @throws Exception
 */
public String  NodeTranslatePinWith2AccNo2Arith(String nodeName1,String nodeName2,String pinBlock1,
		String accNo1,String accNo2,String arithmeticMode1,String arithmeticMode2) throws Exception{
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(nodeName1, "zpk");
	String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(nodeName2, "zpk");
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionTranslatePinWith2AccNo2Arith(fullKeyName1, fullKeyName2, pinBlock1,
		 accNo1, accNo2, arithmeticMode1, arithmeticMode2);
	
}
/**
 * 使用ZPK解密PIN密文（434）
 * @param nodeName 节点名称
 * @param pinBlock 待解密的PIN密文，长度16字节字符串(ASCII)，以'\0'结束
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @return 解密后的PIN明文，ASCII字符串，以'\0'结束
 * @throws Exception
 */
public String NodeDecryptPin(String nodeName,String pinBlock,String accNo) throws Exception{
	  NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zpk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDecryptPin(fullKeyName, pinBlock, accNo);
}

/**
 * 由IBMPinOffset导出一个LMK加密的PIN （402）
 * @param pvkNodeName PVK节点名称
 * @param pinOffset PVK生成的PIN Offset，左对齐，右补‘F’
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param pinLength 最小PIN长度，通常为4
 * @return LMK0203对加密的PIN密文
 * @throws Exception
 */
public String NodeDerivePinByLmkFromPinOffset(String pvkNodeName,
		String pinOffset, String accNo, String pinLength) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDerivePinByLmkFromPinOffset(fullKeyName, pinOffset, accNo,pinLength);
}
/**
 * 由LMK加密的PIN密文导出PVK加密的PIN Offset （513）
 * @param pvkNodeName PVK节点名称
 * @param pinByLmk LMK0203对加密的PIN密文
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param pinLength 最小PIN长度，通常为4
 * @return PVK生成的PIN Offset，左对齐，右补‘F’
 * @throws Exception
 */
public String NodeDerivePinOffsetFromPinByLmk(String pvkNodeName,
		String pinByLmk, String accNo, int pinLength) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDerivePinOffsetFromPinByLmk(fullKeyName, pinByLmk, accNo,pinLength);
}
/**
 * 将一PVK加密的PIN Offset转换为另一PVK加密（514）
 * @param nodeName1 源PVK节点名称
 * @param nodeName2 目的PVK节点名称
 * @param pinOffset1 待转换的PINOffset，源PVK加密
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param pinLength 最小PIN长度，通常为4
 * @return 转换后的PIN Offset，目的PVK加密，左对齐，右补‘F’
 * @throws Exception
 */
public String NodeTranslatePinOffset(String nodeName1,String nodeName2,String pinOffset1,String accNo,int pinLength) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	 String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(nodeName1, "pvk");
	 String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(nodeName2, "pvk");
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionTranslatePinOffset(fullKeyName1, fullKeyName2,pinOffset1, accNo,pinLength);
}
/**
 * 将一PVK加密的PIN Offset转换为另一PVK加密（含两个帐号）（515）
 * @param nodeName1 源PVK节点名称
 * @param nodeName2 目的PVK节点名称
 * @param pinOffset1 待转换的PINOffset，源PVK加密
 * @param accNo1 源账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param accNo2 目的账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @param pinLength 最小PIN长度，通常为4
 * @return pinOffset2 转换后的PIN Offset，使用目的帐号，由目的PVK加密，左对齐，右补‘F’
 * @throws Exception
 */
public String NodeTranslatePinOffsetWith2AccNo(String nodeName1,String nodeName2,String pinOffset1,String accNo1,String accNo2,int pinLength) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	 String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(nodeName1, "pvk");
	 String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(nodeName2, "pvk");
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionTranslatePinOffsetWith2AccNo(fullKeyName1, fullKeyName2,pinOffset1, accNo1,accNo2,pinLength);
}
/**
 * 使用本地主密钥LMK0203对解密一个PIN密文（432）
 * @param pinByLmk LMK0203对加密的PIN密文
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @return
 * @throws Exception
 */
public String NodeDecryptPinByLmk(String pinByLmk,String accNo) throws Exception{
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	 return unionAPI.UnionDecryptPinByLmk(pinByLmk, accNo);
}
/**
 * 将一个LMK加密的PIN转换为由ZPK加密（308）
 * @param zpkNodeName ZPK节点名称
 * @param pinByLmk LMK0203对加密的PIN
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @return ZPK加密的PIN
 * @throws Exception
 */
public String NodeDerivePinOffsetFromPinByZpk(String zpkNodeName,String pinByLmk,String accNo) throws Exception{
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String zpkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(zpkNodeName, "zpk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDerivePinBlockFromPinByLmk(zpkFullKeyName, pinByLmk, accNo);
}

/**
 * 先验证一个MAC，再生成一个新的MAC （333）
 * @param zakNodeName1 ZAK密钥节点名称，用于验证MAC
 * @param zakNodeName2 ZAK密钥节点名称，用于验证MAC
 * @param macData MAC数据，长度变长，字符串，以‘\0’结束
 * @param macBeVerified 待验证的MAC值，长度16字节字符串(ASCII)
 * @return
 * @throws Exception
 */
public String NodeTranslatePinOffsetWith2AccNo(String zakNodeName1,String zakNodeName2,String macData,String macBeVerified) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	 String fullKeyName1 = nodeKeyMng.UnionGetNodeKeyName(zakNodeName1, "zak");
	 String fullKeyName2 = nodeKeyMng.UnionGetNodeKeyName(zakNodeName2, "zak");
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionTranslateMac(fullKeyName1, fullKeyName2,macData, macBeVerified);
}
/**
 * 使用指定ZMK加密的密钥生成MAC（334）
 * @param nodeName 节点名称
 * @param keyByZmk ZMK加密的密钥密文
 * @param macData MAC数据，长度变长，字符串，以'\0'结束
 * @return MAC值，长度16字节字符串(ASCII) 
 * @throws Exception
 */
public String NodeGenerateMacUsingKeyByZmk(String nodeName,
		String keyByZmk, String macData) throws Exception {
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	 String fullKeyName = nodeKeyMng.UnionGetNodeKeyName(nodeName, "zmk");
	 UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionGenerateMacUsingKeyByZmk(fullKeyName, keyByZmk,macData);
}

/**
 * 由一个LMK对加密的PIN导出一个由PVK生成的PVV（403）
 * @param pvkNodeName PVK节点名称
 * @param pinByLmk LMK0203对加密的PIN
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @return PVK生成的PVV
 * @throws Exception
 */
public String NodeDerivePVVFromPinByLmk(String pvkNodeName,String pinByLmk,String accNo) throws Exception{
	 NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	  String pvkFullKeyName = nodeKeyMng.UnionGetNodeKeyName(pvkNodeName, "pvk");
	  UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	  return unionAPI.UnionDerivePVVFromPinByLmk(pvkFullKeyName, pinByLmk, accNo);
}
/**
 * 使用指定应用的私钥签名（134）
 * @param idOfApp
 * @param flag
 * @param lenOfData
 * @param data
 * @return
 * @throws Exception 
 */
/*public String UnionGenerateSignature(String idOfApp, String flag,
		int lenOfData, String data) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	 return unionAPI.UnionGenerateSignature(idOfApp, flag, lenOfData,data);
	
}*/
/**
 * 使用指定应用的公钥验证签名（133）
 * @param nodeName 节点名称
 * @param flag 数据填充方式：0，如果数据不是密钥长度的整倍数，后面补0x00；1，PKCS填充方式。一般使用PKCS填充方式。
 * @param lenOfData  签名数据的长度
 * @param data 签名数据
 * @param sign 待验证的签名
 * @param lenOfSign 待验证签名的长度
 * @return
 * @throws Exception
 */
public int NodeVerifyRsaSignature(String nodeName, String flag, int lenOfData,
		String data, String sign, int lenOfSign) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionVerifySignature(fullKeyName, flag, lenOfData,data,sign,lenOfSign);
}

/**
 * 使用输入的公钥验证签名（135）
 * @param pkValue 公开密钥值
 * @param flag 数据填充方式：0，如果数据不是密钥长度的整倍数，后面补0x00；1，PKCS填充方式。一般使用PKCS填充方式。
 * @param hashID 数据填充方式：0，如果数据不是密钥长度的整倍数，后面补0x00；1，PKCS填充方式。一般使用PKCS填充方式。
 * @param data 签名数据
 * @param sign 待验证的签名
 * @return
 */
public int NodeVerifySignatureUsingInputPK(String pkValue, String flag,
		String hashID, String data, String sign) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionVerifySignatureUsingInputPK(pkValue, flag, hashID,data,sign);
}
/**
 *  将指定应用的PK加密的PIN转换为ZPK加密（170）
 * @param idOfApp 应用编号
 * @param pinByPK PK加密的PIN
 * @param zpkNodeName ZPK节点名称
 * @param accNo 账号/卡号，长度13-19字节字符串(ASCII)，以'\0'结束；卡号为实际卡号；账号/卡号填16个ASCII‘0’，表示无账号运算
 * @return
 * @throws Exception
 */
public String NodeDerivePinBlockFromPinByPK(String idOfApp,
		String pinByPK, String zpkNodeName, String accNo) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(zpkNodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionDerivePinBlockFromPinByPK(idOfApp, pinByPK, fullKeyName,accNo);
}

/**
 * 私钥解密（535）
  * @param pkIndex
	 *            私钥索引
	 * @param pkPadFlag
	 *            公钥填充方式
	 * @param cipherDataLen
	 *            加密数据长度
	 * @param cipherData
	 *            加密数据
	 * @return 解密数据
 */
public String NodeDecDataByPK(int pkIndex, int pkPadFlag,
		int cipherDataLen, String cipherData) {
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionDecDataByPK(pkIndex, pkPadFlag, cipherDataLen,cipherData);
}
/**
 * 使用指定的密钥加密数据（516）
 * @param nodeName 加密密钥的名节点称，密钥用于加密数据
 * @param data 明文数据
 * @param dataLen 明文数据的长度
 * @param encFlag 算法标志，可选，“0”，ECB，“1”，CBC；若不需要，填“NULL”。
 * @param iv 初始化向量，可选；若不需要，填“NULL”
 * @return
 * @throws Exception
 */
public String NodeEncryptDataWithE0(String nodeName, String data,
		int dataLen, String encFlag, String iv) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionEncryptPlainData(fullKeyName, data, dataLen,encFlag,iv);
}
/**
 * 使用指定的密钥解密数据（517）
 * @param nodeName 解密密钥的节点名称，密钥用于解密数据
 * @param encData 密文数据
 * @param encFlag 算法标志，可选，“0”，ECB，“1”，CBC；若不需要，填“NULL”。
 * @param iv 初始化向量，可选；若不需要，填“NULL”
 * @return 
 * @throws Exception
 */
public String NodeDecryptDataWithE0(String nodeName, String encData,
		String encFlag, String iv) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionDecryptEncData(fullKeyName, encData, encFlag,iv);
}
/**
 * 使用指定keyID的交易主密钥，校验ARQC。(525)
 * @param nodeName 节点名称。
 * @param keyVersion 密钥版本号.
 * @param pan 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param processGene 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param data 计算ARQC使用的数据。
 * @param ARQC  待验证的ARQC，16字节。
 * @param iccType  IC卡类型。取值如下：0，PBOC2.0规范IC卡。1，VISA cvn17（cvn10）规范IC卡
 * @return
 * @throws Exception
 */
public boolean NodeVerifyARQCUsingDerivedKey(String nodeName,
		int keyVersion, String pan, String processGene, String data,
		String ARQC, int iccType) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionVerifyARQCUsingDerivedKey(fullKeyName, keyVersion, pan,processGene,data,ARQC,iccType);
}
/**
 * 生成APRC（526）
 * @param nodeName 节点名称.
 * @param keyVersion 密钥版本
 * @param pan 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param processGene 过程因子，参与生成过程密钥，如果是应用交易计数器ATC，则为4字节。
 * @param data 计算ARQC使用的数据
 * @param ARQC 授权请求密文，参与生成ARPC，16字节。
 * @param ARC 授权响应码，参与生成ARPC，4字节。
 * @param iccType IC卡类型。取值如下：0，PBOC2.0规范IC卡。1，VISA cvn17（cvn10）规范IC卡。
 * @return 生成的授权响应密文，16字节。
 * @throws Exception 
 */
public String NodeGenerateARPCUsingDerivedKey(String nodeName,
		int keyVersion, String pan, String processGene, String data,
		String ARQC, String ARC, int iccType) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateARPCUsingDerivedKey(fullKeyName, keyVersion, pan,processGene,data,ARQC,ARC,iccType);
}
/**
 * 加密数据 （527）
 * @param encryptMode 加密模式，0：CBC（MASTERCARD）；1：ECB（VISA/PBOC）
 * @param nodeName 节点名称。
 * @param keyVersion 密钥版本号
 * @param pan 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param processGene 过程因子，参与生成过程密钥，如果是应用交易计数器ATC，则为4字节。
 * @param data 明文数据
 * @return 密文数据。
 * @throws Exception
 */
public String NodeEncryptDataUsingDerivedKey(int encryptMode,
		String nodeName, int keyVersion, String pan, String processGene,
		String data) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionEncryptDataUsingDerivedKey(encryptMode,fullKeyName, keyVersion, pan,processGene,data);
}
/**
 * 脚本数据计MAC  （528）
 * @param nodeName 节点名称。
 * @param keyVersion 密钥版本
 * @param pan 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param processGene 过程因子，参与生成过程密钥，如果是应用交易计数器ATC，则为4字节。
 * @param data 明文数据
 * @return 明文数据的mac值
 * @throws Exception
 */
public String NodeGenerateMacUsingDerivedKey(String nodeName,
		int keyVersion, String pan, String processGene, String data) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateMacUsingDerivedKey(fullKeyName, keyVersion, pan,processGene,data);
}
/**
 * 生成ARPC，不校验ARQC（529）
 * @param nodeName 节点名称。
 * @param keyVersion 密钥版本
 * @param pan 卡号，使用交易主密钥对卡号进行分散，得到IC卡的交易密钥
 * @param processGene 过程因子，参与生成过程密钥，如果是应用交易计数器ATC，则为4字节。
 * @param ARQC 授权请求密文，参与生成ARPC，16字节。
 * @param ARC 授权响应码，参与生成ARPC，4字节。
 * @param iccType IC卡类型。取值如下：0，PBOC2.0规范IC卡。1，VISA cvn17（cvn10）规范IC卡。
 * @return 生成的授权响应密文，16字节。
 * @throws Exception
 */
public String NodeGenerateARPCUsingDerivedKey2(String nodeName,
		int keyVersion, String pan, String processGene, String ARQC,
		String ARC, int iccType) throws Exception {
	NodeKeyMng nodeKeyMng = new NodeKeyMng(gunionIDOfUnionAPI);
	String fullKeyName = nodeKeyMng.UnionGetNodePKName(nodeName);
	UnionAPI unionAPI = new UnionAPI(hsmIP,hsmPort,timeOut,0,gunionIDOfUnionAPI);
	return unionAPI.UnionGenerateARPCUsingDerivedKey2(fullKeyName, keyVersion, pan,processGene,ARQC,ARC,iccType);
}


}

