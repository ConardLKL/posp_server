package com.bestpay.posp.service.checker;

import java.nio.ByteBuffer;
import java.util.List;

import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.TInfoPublickeyManage;
import com.bestpay.posp.system.service.TInfoPublickeyManageService;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLV;
import com.tisson.sfip.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bestpay.posp.utils.recombin.TLVDecoder;
import com.bestpay.posp.utils.recombin.TLVEncoder;
import com.bestpay.posp.utils.recombin.TLVList;

@Slf4j
@Component
public class M_DownCAPkInfoChecker {
	
	@Autowired
	TInfoPublickeyManageService tInfoPublickeyManageService;

	/**
	 * CA公钥查询
	 */
	public IsoMessage queryCAPkInfo(IsoMessage requestIso){
    	IsoMessage output = requestIso.clone();
    	try{
	        // 拼接60域，62域
	        //60域
	        //60.1 交易类型码 00
	        StringBuffer strfield60 = new StringBuffer();
	        strfield60.append("00");
	        // 60.2 批次号
	        strfield60.append(requestIso.getField(60).substring(2,8));
	        // 60.3 网络管理信息码 ?返回372国密返回373
			if(!StringUtils.equals(requestIso.getTranCode(), PosConstant.TRANS_TYPE_0820373)) {
				strfield60.append("372");
			}else{
				strfield60.append("373");
			}
			output.setField(60, strfield60.toString());
	        // 62域 请求时为TLV格式的RID+索引
	        byte [] field_62 = HexBinary.decode(requestIso.getField(62));
	        String request_62 = "";
			request_62 = new String(field_62,"GBK");
	        if("1".equals(request_62.subSequence(0, 1))){//查询请求
	        	// 62域返回报文拼写
	            StringBuffer field62 = new StringBuffer();
				TInfoPublickeyManage publickeyManage = new TInfoPublickeyManage();
				//区分国际版公钥和国密版公钥   0800373-国密公钥获取
				if(!StringUtils.equals(requestIso.getTranCode(), PosConstant.TRANS_TYPE_0820373)) {
					publickeyManage.setPkIdentify("01");
				}else{
					publickeyManage.setPkIdentify("04");
				}
	            List<TInfoPublickeyManage> tInfoPublickeyManage=tInfoPublickeyManageService.getInfoPublickeyManage(publickeyManage);
		        if(tInfoPublickeyManage!=null){
		            // 后续有参数信息
		        	int complete = Integer.parseInt(request_62.substring(1));//已经下载的条数
		        	if(tInfoPublickeyManage.size()-complete > 10){//剩余不止10条aid
		        		field62.append("32");
		        	}else{
		        		field62.append("31");
		        	}
		            //编辑TLV
		            TLVList returnList = new TLVList();
	            	for(int i=0;i<tInfoPublickeyManage.size();i++){
	            		if(i>=complete){
		            		TInfoPublickeyManage ttInfoPublickeyManage = tInfoPublickeyManage.get(i);
			                TLV tlv9f06 = TLVEncoder.genTLV("9F06",ttInfoPublickeyManage.getProvideIdentify(),true);
			                TLV tlv9F22 = TLVEncoder.genTLV("9F22",ttInfoPublickeyManage.getPkIdx(),true);
			                TLV tlvDF05 = TLVEncoder.genTLV("DF05",ttInfoPublickeyManage.getValidDate(),false);
			                returnList.add(tlv9f06);
			                returnList.add(tlv9F22);
			                returnList.add(tlvDF05);
	            		}
	            	}
	                field62.append(HexBinary.encode(returnList.toBinary()));
		        }else{
		        	field62.append("30");
		        }
				output.setField(62, field62.toString());
	        }
	        output.setState(true);
			return output;
    	}catch(Exception e){
			log.error(e.getMessage());
    		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C496,output);
    		log.info(String.format("[%s] CA公钥查询异常! ", output.getSeq()));
    		output.setState(false);
    		return output;
    	}
    }

	
	
	
	 /**
     * CA公钥下载
     */
    public IsoMessage downCAPkInfo(IsoMessage requestIso){
    	IsoMessage output = requestIso.clone();
    	try{
	        // 拼接60域，62域
	        //60域
	        //60.1 交易类型码 00
	        StringBuffer strfield60 = new StringBuffer();
	        strfield60.append("00");
	        // 60.2 批次号
	        strfield60.append(requestIso.getField(60).substring(2,8));
	        // 60.3 网络管理信息码 ?返回370国密返回373
			if(!StringUtils.equals(requestIso.getTranCode(), PosConstant.TRANS_TYPE_0800373)) {
				strfield60.append("370");
			}else{
				strfield60.append("373");
			}
			output.setField(60, strfield60.toString());
	        // 62域 请求时为TLV格式的RID+索引
	        TLVDecoder decoder = new TLVDecoder();
//	        byte[] s = HexBinary.decode(requestIso.getField(62));
	        ByteBuffer byteBuff = ByteBuffer.wrap(HexBinary.decode(requestIso.getField(62)));
	        TLVList tlvList = decoder.decode(byteBuff);
	        String rid = HexBinary.encode(tlvList.get("9F06").getValue());
//	        s = tlvList.get("9F22").getValue();
	        String pkIdx = HexBinary.encode(tlvList.get("9F22").getValue());    
	        log.debug("公钥rid：" + rid);
	        log.debug("公钥index：" + pkIdx);
	        
	        // 62域返回报文拼写
	        StringBuffer field62 = new StringBuffer();
	        TInfoPublickeyManage infoPublickeyManage=new TInfoPublickeyManage();
	        infoPublickeyManage.setProvideIdentify(rid);
	        infoPublickeyManage.setPkIdx(pkIdx);
	        TInfoPublickeyManage tInfoPublickeyManage=tInfoPublickeyManageService.findInfoPublickeyManage(infoPublickeyManage);
	        if (tInfoPublickeyManage == null) {
	            // 没有该公钥信息
	            field62.append("30");
	        } else {
	            // 后续有公钥信息
	            field62.append("31");
	            //编辑TLV
	            TLVList returnList = new TLVList();
                // 信息列表只返回 RID 索引 有效期
                TLV tlv9f06 = TLVEncoder.genTLV("9F06",tInfoPublickeyManage.getProvideIdentify(),true);
                TLV tlv9f22 = TLVEncoder.genTLV("9F22",tInfoPublickeyManage.getPkIdx(),true);
                TLV tlvdf05 = TLVEncoder.genTLV("DF05",tInfoPublickeyManage.getValidDate(),false);
                TLV tlvdf06 = TLVEncoder.genTLV("DF06",tInfoPublickeyManage.getHashIdentify(),true);
                TLV tlvdf07 = TLVEncoder.genTLV("DF07",tInfoPublickeyManage.getPkIdentify(),true);
                TLV tlvdf02 = TLVEncoder.genTLV("DF02",tInfoPublickeyManage.getPkMode(),true);
                TLV tlvdf04 = TLVEncoder.genTLV("DF04",tInfoPublickeyManage.getPkIndex(),true);
                TLV tlvdf03 = TLVEncoder.genTLV("DF03",tInfoPublickeyManage.getPkChv(),true);
                returnList.add(tlv9f06);
                returnList.add(tlv9f22);
                returnList.add(tlvdf05);
                returnList.add(tlvdf06);
                returnList.add(tlvdf07);
                returnList.add(tlvdf02);
                returnList.add(tlvdf04);
                returnList.add(tlvdf03);
                field62.append(HexBinary.encode(returnList.toBinary()));
	        }
	        // 由于62域在签到中使用16进制bcd编码，因此使用此域的其他交易，如果使用asc作为返回值的话，
	        // 将string转为asc编码的string HexBinary
	        //output.setField62ReservedPrivate(field62.toString());
	        //output.setField62ReservedPrivate(HexBinary.encode(field62.toString().getBytes()));
			output.setField(62,field62.toString());
			output.setState(true);
			return output;
    	}catch(Exception e){
    		e.printStackTrace();
    		RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_C596,output);
    		log.info(String.format("[%s] CA公钥下载异常! ", output.getSeq()));
    		output.setState(false);
    		return output;
    	}
    }
}
