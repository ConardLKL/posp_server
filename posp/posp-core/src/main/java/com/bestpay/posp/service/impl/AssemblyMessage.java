package com.bestpay.posp.service.impl;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.service.unipay.MessageTranslationInterface;
import com.bestpay.posp.system.cache.ConfigCache;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.spring.PospApplicationContext;

/**
 * 发往银联报文组装
 * 返回终端报文组装
 * @author TISSON
 *
 */
@Component
public class AssemblyMessage implements MessageTranslationInterface {

	@Override
	public IsoMessage posToUnion(IsoMessage iso) throws Exception{

		String field60 = createField60(iso,"");
		//对于电子现金应用的非指定账户圈存交易，在受理方发出的脚本结果通知中，带3域、22域同原转入圈存请求报文中的一致
		if(iso.getTranCode().equals(PosConstant.TRANS_TYPE_0620951)){
			if(iso.getOrgFlow().getTranCode().equals(PosConstant.TRANS_TYPE_0200629147)){
				iso.setField(3, "650000");
				iso.setField(22, iso.getOrgFlow().getInputMode());
			}
			field60=field60.substring(0, 22)+"0";
		}
		iso.setField(60, field60);
		//53域为安全控制信息，长度为16，后14为置全0
		String field53 = iso.getField(53);
		if(StringUtils.isNotEmpty(field53)){
			field53 = field53.substring(0,1)+"40"+field53.substring(3);
			if(field53.substring(0,1).equals("0")){
				field53 = "1"+field53.substring(1);
			}
			iso.setField(53,field53);
		}
		return iso;
	}

	@Override
	public IsoMessage unionToPos(IsoMessage iso) throws Exception{
		ConfigCache cache = (ConfigCache)PospApplicationContext.getBean("ConfigCache");
		String field33 = cache.getParaValues(SysConstant.CL1, "000021");
		if(StringUtils.isNotEmpty(iso.getField(54))){
			iso.setField(54, iso.getField(54).substring(iso.getField(54).length()-20, iso.getField(54).length()));
		}
		//银联返回44域需要特殊处理
		//AN11接收机构标识码+AN11收单机构标识码
		if(iso.getField(100) != null && !iso.getField(100).equals("")){
			iso.setField(44, String.format("%-11s", iso.getField(100))+String.format("%-11s", field33));
		}else{
			iso.setField(44, String.format("%-11s", "00000000")+String.format("%-11s", field33));
		}
        //应答码细化
        if(StringUtils.isNotEmpty(iso.getField(57))){
            String filed57 = iso.getField(57);
            if(StringUtils.equals("ASAR",filed57.substring(0,4))){
                iso.getFlow().setDetailRespCode(filed57.substring(filed57.length()-3));
            }
        }
		return iso;
	}

	/**
     * 组织银联60域 到60.3.5域截至
     */
    private String createField60(IsoMessage iso, String cupsType) throws Exception{
        String posField60 = iso.getField(60);
        String request22 = null;
        if(iso.getOrgFlow() != null
                && iso.getOrgFlow().getInputMode() != null){
            request22 = iso.getOrgFlow().getInputMode();
        }else{
            request22 = iso.getField(22);
        }
        StringBuilder str = new StringBuilder();
        // 60.1报文原因码
        // 本域在冲正、存款确认、转入确认等报文中由报文发送方填写，
        // 用于描述引发该报文的原因。其它的0100，0200，0220报文中本域以“0000”填充。
        str.append("0000");
        // 60.2 服务点附加信息码 长度11 共9个子域
        // F60.2.1：账户所有人类型
        str.append("0");
        // F60.2.2：终端读取能力 从POS报文60.4取得 没有的情况下为0
        if(
//                request22.substring(0, 2).equals("05")
//                ||
                request22.substring(0, 2).equals("95")
                || request22.substring(0, 2).equals("02")
                || request22.substring(0, 2).equals("90")){
        	str.append("5");
        }else if(request22.substring(0, 2).equals("05")){
            str.append("6");
        }else{
        	if (posField60.length()>11) {
	            str.append(posField60.substring(11,12));
	        } else {
	            str.append("0");
	        }
        }
        // F60.2.3：IC卡条件代码 从POS报文60.5取得 没有的情况下为0
        if (posField60.length()>12) {
            str.append(posField60.substring(12,13));
        } else {
            str.append("0");
        }
        // F60.2.4：保留使用
        str.append("0");
        // F60.2.5：终端类型
        //  03-有线pos
        str.append("03");
        // F60.2.6：境外受理免验密码网络标志
        str.append(iso.getFreePasswordSign());
        // F60.2.7：IC卡验证可靠性标志
        str.append("0");
        // F60.2.8：电子商务标志
        str.append("00");
        // F60.2.9：交互方式标志
        str.append("0");
        // 60.3 交易发生附加信息
        // F60.3.1：特殊计费类型
        if (cupsType==null || cupsType.equals("")) {
            str.append("000");
        } else {
            str.append(cupsType);
        }
        // F60.3.3：保留使用，取值000
//        str.append("000");
        str.append("004");
        // F60.3.4：支持部分承兑和返回余额标志 0-不支持
        str.append("0");
        // F60.3.5：交易发起方式
        str.append("1");
        return str.toString();
    }
    /**
	 * 银联90域
	 * @param request
	 * @return
	 */
	public IsoMessage createField90(IsoMessage request){
        String field90 = "";
        // 判断是否为0620 如果是0620则根据后续2为长度判断原始交易
            // 处理金额
          
            String tranType = request.getOrgFlow().getTranType();
         // 90.1 原始交易类型 原交易messType 定长4位
            if (tranType.equals("01")) {
                // 查询类交易
                field90 = "0200";
                // 3域不送原始 送30开头+原始后四位
                // POS规范和银联规范不一样
               // cupDataForm.setField3("30" + check.getIso().getField(3).substring(2));
            } else if (tranType.equals("10")) {
                // 预授权
                field90 = "0100";
            } else if (tranType.equals("11")) {
                // 预授权撤消
                field90 = "0100";
            } else if (tranType.equals("20")) {
                // 预授权完成 请求
                field90 = "0200";
            } else if (tranType.equals("21")) {
                // 预授权完成撤消
                field90 = "0200";
            } else if (tranType.equals("22")) {
                // 消费
            	 field90 = "0200";
            } else if (tranType.equals("23")) {
                // 消费撤消
                field90 = "0200";
            } else if (tranType.equals("24")) {
                // 预授权完成通知
                field90 = "0220";
            } else if (tranType.equals("25")) {
                // 退货
                field90 = "0220";
            } else if (tranType.equals("45")) {
                // 电子现金指定账户圈存
                field90 = "0200";
            } else if (tranType.equals("46")) {
                // 电子现金现金充值
                field90 = "0200";
            } else if (tranType.equals("47")) {
                // 电子现金非指定账户转账圈存
                field90 = "0200";
            } else if (tranType.equals("51")) {
                // 电子现金现金充值撤销
                field90 = "0200";
            }else {
                // 其他交易暂时不支持
                field90 = "0000";
            }
        
        // 90.2 原始系统跟踪号 原始交易流水后6位
        field90 = field90 + request.getOrgFlow().getTermSerialNo();
        // 90.3 原始交易传输时间 MMDDHHmmss
        field90 = field90 + request.getOrgFlow().getTranDate().substring(request.getOrgFlow().getTranDate().length() - 4) + request.getOrgFlow().getTranTime();
        // 90.4 原始受理机构标识码 机构码现在8位 右对齐 前补0
//          field90 = field90 + "000" +GDNamedParamQuery.getAcquiringInstitution();
        field90 = field90 + "000" + request.getOrgFlow().getRcvBankCode();//对应商户的成员机构号+城市代码
        // 90.5 原始发送机构标识码 机构码现在8位 右对齐 前补0
        field90 = field90 + "000" + request.getOrgFlow().getRcvBranchCode();
        request.getFlow().setField90(field90);
		return request;
	}
	
}
