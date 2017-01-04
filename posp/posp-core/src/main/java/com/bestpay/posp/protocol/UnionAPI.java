package com.bestpay.posp.protocol;

import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.cache.ConfigCache;
import com.tisson.sfip.util.StringUtils;
import com.union.api.TUnionTransInfo;
import com.union.api.UnionEsscAPI;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HR on 2016/10/18.
 */
@Slf4j
public class UnionAPI extends UnionEsscAPI {

    private static ConfigCache configCache;
    private static List<String> ipList = null;
    private static List<Integer> portList = null;
    private static Integer timeout = null;
    static {
        configCache = (ConfigCache) PospApplicationContext.getBean("ConfigCache");
        ipList = new ArrayList<String>();
        portList = new ArrayList<Integer>();
        ipList.add(0,configCache.getParaValues(8001L, "union.ip"));
        portList.add(0,Integer.parseInt(configCache.getParaValues(8001L, "union.port")));
        timeout = Integer.parseInt(configCache.getParaValues(8001L, "union.timeOut"));
    }

    public UnionAPI() throws Exception{
        super(ipList,portList,timeout,"HNNX","HNNX","0");
    }


    /**
     * zpk密钥下载
     */
    public String[] unionGenerateKeyZpk(String algorithmID,String name){
        String fomatZpk = configCache.getParaValues(8001L, "000054");//POS.%s.zpk
        String fullKeyName = String.format(fomatZpk, name);
        String fomatZmk = fomatZpk.replaceAll("zpk","zmk");
        String zmk = String.format(fomatZmk, name);
        TUnionTransInfo transInfo = unionAPIServiceE111(fullKeyName,1,algorithmID, 1, 0,zmk, -1, 128);
        String[] value = new String[2];
        if(transInfo.getIsSuccess() == 1) {
            value[0] = transInfo.getReturnBody().getKeyValue();
            value[1] = transInfo.getReturnBody().getCheckValue();
        }else{
            value[0] = "-"+transInfo.getResponseCode();
        }
        return value;
    }
    /**
     * POS.zak下载
     * @param name
     * @return
     */
    public String[] unionGenerateKeyZak(String algorithmID,String name){
        String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
        String fullKeyName = String.format(fomatZak, name);
        String fomatZmk = fomatZak.replaceAll("zak","zmk");
        String zmk = String.format(fomatZmk, name);
        TUnionTransInfo transInfo = unionAPIServiceE111(fullKeyName,1,algorithmID, 1, 0,zmk, -1, 128);
        String[] value = new String[2];
        if(transInfo.getIsSuccess() == 1) {
            value[0] = transInfo.getReturnBody().getKeyValue();
            value[1] = transInfo.getReturnBody().getCheckValue();
        }else{
            value[0] = "-"+transInfo.getResponseCode();
        }
        return value;
    }
    /**
     * POS.edk下载
     * @param name
     * @return
     */
    public String[] unionGenerateKeyEdk(String algorithmID,String name){
        String fomatEdk = configCache.getParaValues(8001L, "000051");//POS.%s.edk
        String fomatZmk = fomatEdk.replaceAll("edk","zmk");
        if(StringUtils.equals(algorithmID,"SM4")){
            fomatEdk = fomatEdk.replaceAll("edk","zek");
        }
        String fullKeyName = String.format(fomatEdk, name);
        String zmk = String.format(fomatZmk, name);
        TUnionTransInfo transInfo = unionAPIServiceE111(fullKeyName,1,algorithmID, 1, 0,zmk, -1, 128);
        String[] value = new String[2];
        if(transInfo.getIsSuccess() == 1) {
            value[0] = transInfo.getReturnBody().getKeyValue();
            value[1] = transInfo.getReturnBody().getCheckValue();
        }else{
            value[0] = "-"+transInfo.getResponseCode();
        }
        return value;
    }
    /**
     * 解密磁道密钥
     *
     */
    public String unionKeyDecryptDataBy531yizhifu(boolean sign,String name,String data){
        String fomatEdk = configCache.getParaValues(8001L, "000051");//POS.%s.edk
        TUnionTransInfo transInfo;
        if(sign) {
            fomatEdk = fomatEdk.replaceAll("edk", "zek");
            String fullName = String.format(fomatEdk, name);
            transInfo = unionAPIServiceE161(1,fullName, "", 0, 1, data, "31323334353637383930313233343536", 0);
        }else{
            String fullName = String.format(fomatEdk, name);
            transInfo = unionAPIServiceE161(1,fullName, "", 1, 1, data, "1234567890123456", 0);
        }
        String value;
        if(transInfo.getIsSuccess() == 1) {
            value = transInfo.getReturnBody().getData();
        }else{
            value = "-"+transInfo.getResponseCode();
        }
        return value;
    }

    /**
     * 将一个ZPK加密的PIN转换为另一个ZPK加密
     */
    public String unionTranslatePin(String name,String pinBlock1, String accNo){
        String fomatZpkp = configCache.getParaValues(8001L, "000054");//POS.%s.zpk
        String fullKeyName1 = String.format(fomatZpkp, name);
        String fullKeyName2 = configCache.getParaValues(8001L, "000055");//JG.%s.zpk"
        TUnionTransInfo transInfo = unionAPIServiceE142(pinBlock1,fullKeyName1, fullKeyName2, accNo,
                accNo, "01", "01",0,"");
        String value;
        if(transInfo.getIsSuccess() == 1) {
            value = transInfo.getReturnBody().getPinBlock();
        }else{
            value = "-"+transInfo.getResponseCode();
        }
        return value;
    }

    /**
     * 密钥验证MAC
     */
    public int unionVerifyMac(String name, String macData, String mac){
        String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
        String fullKeyName = String.format(fomatZak, name);
        TUnionTransInfo transInfo = unionAPIServiceE151(1, fullKeyName,"", 1,0,2,macData, mac);
        return transInfo.getResponseCode();
    }

    /**
     * 密钥生成中国银联标准MAC
     * fullKeyName为定值
     */
    public String unionGenerateChinaPayMac(String macData){
        String fullKeyName = configCache.getParaValues(8001L, "000053");//JG.%s.zak
        TUnionTransInfo transInfo = unionAPIServiceE150(1,fullKeyName,"",2,1,1,macData.toUpperCase());
        String value;
        if(transInfo.getIsSuccess() == 1) {
            value = transInfo.getReturnBody().getMac();
        }else{
            value = "-"+transInfo.getResponseCode();
        }
        return value;
    }

    /**
     * 密钥生成MAC
     */
    public String unionGenerateMac(String name, String macData){
        String fomatZak = configCache.getParaValues(8001L, "000052");//POS.%s.zak
        String fullKeyName = String.format(fomatZak, name);
        TUnionTransInfo transInfo = unionAPIServiceE150(1,fullKeyName,"",1,0,1,macData);
        String value;
        if(transInfo.getIsSuccess() == 1) {
            value = transInfo.getReturnBody().getMac();
        }else{
            value = "-"+transInfo.getResponseCode();
        }
        return value;
    }

    /**
     * 机密数据进行解密
     * @param encryptKey
     * @param encryptedData
     * @return
     */
    public String unionKeyDecryptDataBy531(String encryptKey,String encryptedData){
        TUnionTransInfo transInfo = unionAPIServiceE161(1,encryptKey, "", 1, 0, encryptedData, "1234567890123456", 0);
        String value;
        if(transInfo.getIsSuccess() == 1) {
            value = transInfo.getReturnBody().getData();
        }else{
            value = "-"+transInfo.getResponseCode();
        }
        return value;
    }
}
