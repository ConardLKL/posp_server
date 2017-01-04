package com.bestpay.posp.service.checker;

import java.nio.ByteBuffer;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.system.entity.SalesSlip;
import com.bestpay.posp.system.entity.TLogSignePuchaseInfo;
import com.bestpay.posp.system.service.TLogSignePuchaseInfoService;
import com.bestpay.posp.utils.recombin.TLVDecoder;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.BLOB;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.utils.recombin.HexBinary;
import com.bestpay.posp.utils.recombin.TLVList;

@Slf4j
@Component
public class ElectronicSignature {

    @Autowired
    private TLogSignePuchaseInfoService tLogSignePuchaseInfoService;
    @Autowired
    private TLVDecoder decoder;
    @Autowired
    private ObtainObjectInformation obtainObjectInformation;

    public IsoMessage electronicSignature(IsoMessage iso) {
        try {
            if (saveElectronicSignature(iso).isState()
                    && updateElectronicSignature(iso).isState()) {
                iso.setState(true);
                return iso;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.error(e.getMessage());
            RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_AA96, iso);
            log.warn(String.format("[%s] error：电子化凭条信息操作失败! ", iso.getSeq()));
        }
        iso.setState(false);
        return iso;
    }

    /**
     * 保存电子化凭条信息
     *
     * @param iso
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public IsoMessage saveElectronicSignature(IsoMessage iso) throws Exception {
        if (!StringUtils.equals(iso.getTranCode(), "0820800")
                && !StringUtils.equals(iso.getTranCode(), "0820801")) {
            iso.setState(true);
            return iso;
        }
        boolean selSign = false;
        getSalesSlip(iso);//获取签购单域信息
        TLogSignePuchaseInfo tLogSignePuchaseInfo = new TLogSignePuchaseInfo();
        tLogSignePuchaseInfo.setMerchCode(iso.getField(42));//商户编码
        tLogSignePuchaseInfo.setTermCode(iso.getField(41));//终端编码
        tLogSignePuchaseInfo.setBatchNo(iso.getField(60).substring(2, 8));//批次号
        tLogSignePuchaseInfo.setTermSerialNo(iso.getField(11));//终端流水号
        tLogSignePuchaseInfo.setCardNo(obtainObjectInformation.encryptionAccountNumber(iso.getField(2)));//卡号
        tLogSignePuchaseInfo.setTranDate(iso.getSalesSlip().getFF06());//交易时间
        tLogSignePuchaseInfo.setReferNo(iso.getField(37));//系统参考号
        tLogSignePuchaseInfo.setSettleDate(iso.getField(15));//清算日期
        tLogSignePuchaseInfo.setTranCode(iso.getTranCode());//交易处理码
        if (StringUtils.isNotEmpty(iso.getField(4))) {
            tLogSignePuchaseInfo.setTranAmount(Double.valueOf(iso.getField(4)) / 100);//交易金额
        }
        tLogSignePuchaseInfo.setMerchName(iso.getSalesSlip().getFF00());//商户名称
        tLogSignePuchaseInfo.setTranType(iso.getSalesSlip().getFF01());//交易类型
        tLogSignePuchaseInfo.setOperatorNum(iso.getSalesSlip().getFF02());//操作员号
        tLogSignePuchaseInfo.setRcvBranchCode(iso.getSalesSlip().getFF03());//收单机构
        tLogSignePuchaseInfo.setRcvBankCode(iso.getSalesSlip().getFF04());//发卡机构
        tLogSignePuchaseInfo.setAuthNo(iso.getSalesSlip().getFF07());//授权码
        if (StringUtils.isNotEmpty(iso.getSalesSlip().getFF08())) {
            tLogSignePuchaseInfo.setTipAmount(Double.valueOf(iso.getSalesSlip().getFF08()) / 100);//小费金额
        }
        tLogSignePuchaseInfo.setCardOrganization(iso.getSalesSlip().getFF09());//卡组织
        tLogSignePuchaseInfo.setTranCcy(iso.getSalesSlip().getFF0A());//交易币种
        tLogSignePuchaseInfo.setPhoneNum(iso.getSalesSlip().getFF0B());//手机号码
        tLogSignePuchaseInfo.setAppLabelTerm(iso.getSalesSlip().getFF20());//应用标签(存量终端)
        tLogSignePuchaseInfo.setAppNameTerm(iso.getSalesSlip().getFF21());//应用名称(存量终端)
        tLogSignePuchaseInfo.setAppLabel(iso.getSalesSlip().getFF30());//应用标签
        tLogSignePuchaseInfo.setAppName(iso.getSalesSlip().getFF31());//应用名称
        tLogSignePuchaseInfo.setAppIdent(iso.getSalesSlip().getFF22());//应用标识
        tLogSignePuchaseInfo.setAppCrypt(iso.getSalesSlip().getFF23());//应用密文
        tLogSignePuchaseInfo.setCardBalance(iso.getSalesSlip().getFF24());//卡片余额
        tLogSignePuchaseInfo.setAddCardNo(obtainObjectInformation.encryptionAccountNumber(iso.getSalesSlip().getFF25()));//转入卡号
        tLogSignePuchaseInfo.setUnpdtNum(iso.getSalesSlip().getFF26());//不可预知数
        tLogSignePuchaseInfo.setAip(iso.getSalesSlip().getFF27());//应用交互特征
        tLogSignePuchaseInfo.setTermVerResults(iso.getSalesSlip().getFF28());//终端验证结果
        tLogSignePuchaseInfo.setTranStatus(iso.getSalesSlip().getFF29());//交易状态信息
        tLogSignePuchaseInfo.setAtc(iso.getSalesSlip().getFF2A());//应用交易计数器
        tLogSignePuchaseInfo.setIssueAppdata(iso.getSalesSlip().getFF2B());//发卡应用数据
        tLogSignePuchaseInfo.setOrigDocumtNo(iso.getSalesSlip().getFF60());//原凭证码
        tLogSignePuchaseInfo.setOrigBatchNo(iso.getSalesSlip().getFF61());//原批次号
        tLogSignePuchaseInfo.setOrigReferNo(iso.getSalesSlip().getFF62());//原参考号
        tLogSignePuchaseInfo.setOrigTranDate(iso.getSalesSlip().getFF63());//原交易日期
        tLogSignePuchaseInfo.setOrigAuthNo(iso.getSalesSlip().getFF64());//原授权码
        tLogSignePuchaseInfo.setOrigTermCode(iso.getSalesSlip().getFF65());//原终端编码
        if (StringUtils.isNotEmpty(iso.getSalesSlip().getFF70())) {
            tLogSignePuchaseInfo.setPrintNo(iso.getSalesSlip().getFF70().substring(1));//打印张数
        }
        tLogSignePuchaseInfo.setEleSignatInfo(iso.getField(62));//电子签字信息
        selSign = tLogSignePuchaseInfoService.insertSignePuchaseInfo(tLogSignePuchaseInfo);
        if (!selSign) {
            iso.setState(false);
            RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_AA96, iso);
            log.warn(String.format("[%s] error：插入电子化凭条信息操作失败! ", iso.getSeq()));
            return iso;
        }
        iso.setState(true);
        return iso;
    }

    /**
     * 更新电子化凭条信息（部分电子签字报文）
     *
     * @param iso
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public IsoMessage updateElectronicSignature(IsoMessage iso) throws Exception {
        if (StringUtils.equals(iso.getTranCode(), "0820800")
                || StringUtils.equals(iso.getTranCode(), "0820801")) {
            iso.setState(true);
            return iso;
        }
        boolean selSign = false;
        getSalesSlip(iso);
        TLogSignePuchaseInfo tLogSignePuchaseInfo = new TLogSignePuchaseInfo();
        tLogSignePuchaseInfo.setMerchCode(iso.getField(42));//商户编码
        tLogSignePuchaseInfo.setTermCode(iso.getField(41));//终端编码
        tLogSignePuchaseInfo.setBatchNo(iso.getField(60).substring(2, 8));//批次号
        tLogSignePuchaseInfo.setTermSerialNo(iso.getField(11));//终端流水号
        tLogSignePuchaseInfo.setTranDate(iso.getSalesSlip().getFF06());//交易时间
        TLogSignePuchaseInfo logSignePuchaseInfo = tLogSignePuchaseInfoService.getSignePuchaseInfo(tLogSignePuchaseInfo);
        tLogSignePuchaseInfo.setEleSignatInfo(HexCodec.hexEncode(Utils.blobToBytes((BLOB) logSignePuchaseInfo.getEleSignatInfo())) + iso.getField(62));
        selSign = tLogSignePuchaseInfoService.updateSignePuchaseInfo(tLogSignePuchaseInfo);
        if (!selSign) {
            iso.setState(false);
            RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_AA96, iso);
            log.warn(String.format("[%s] error：更新电子化凭条信息操作失败! ", iso.getSeq()));
            return iso;
        }
        iso.setState(true);
        return iso;
    }

    /**
     * 获取签购单域信息
     *
     * @param iso
     */
    public void getSalesSlip(IsoMessage iso) {
        TLVList tlvList = decoder.decode(ByteBuffer.wrap(HexBinary.decode(iso.getField(55))));
        SalesSlip salesSlip = new SalesSlip();
        if (tlvList.get("FF00") != null && !tlvList.get("FF00").equals("")) {
            salesSlip.setFF00(HexBinary.encode(tlvList.get("FF00").getValue()));
        }
        if (tlvList.get("FF01") != null && !tlvList.get("FF01").equals("")) {
            salesSlip.setFF01(HexBinary.encode(tlvList.get("FF01").getValue()));
        }
        if (tlvList.get("FF02") != null && !tlvList.get("FF02").equals("")) {
            salesSlip.setFF02(HexBinary.encode(tlvList.get("FF02").getValue()));
        }
        if (tlvList.get("FF03") != null && !tlvList.get("FF03").equals("")) {
            salesSlip.setFF03(Utils.getString(tlvList.get("FF03").getValue()));
        }
        if (tlvList.get("FF04") != null && !tlvList.get("FF04").equals("")) {
            salesSlip.setFF04(Utils.getString(tlvList.get("FF04").getValue()));
        }
        if (tlvList.get("FF05") != null && !tlvList.get("FF05").equals("")) {
            salesSlip.setFF05(HexBinary.encode(tlvList.get("FF05").getValue()));
        }
        if (tlvList.get("FF06") != null && !tlvList.get("FF06").equals("")) {
            salesSlip.setFF06(HexBinary.encode(tlvList.get("FF06").getValue()));
        }
        if (tlvList.get("FF07") != null && !tlvList.get("FF07").equals("")) {
            salesSlip.setFF07(Utils.getString(tlvList.get("FF07").getValue()));
        }
        if (tlvList.get("FF08") != null && !tlvList.get("FF08").equals("")) {
            salesSlip.setFF08(HexBinary.encode(tlvList.get("FF08").getValue()));
        }
        if (tlvList.get("FF09") != null && !tlvList.get("FF09").equals("")) {
            salesSlip.setFF09(HexBinary.encode(tlvList.get("FF09").getValue()));
        }
        if (tlvList.get("FF0A") != null && !tlvList.get("FF0A").equals("")) {
            salesSlip.setFF0A(Utils.getString(tlvList.get("FF0A").getValue()));
        }
        if (tlvList.get("FF0B") != null && !tlvList.get("FF0B").equals("")) {
            salesSlip.setFF0B(HexBinary.encode(tlvList.get("FF0B").getValue()));
        }
        if (tlvList.get("FF20") != null && !tlvList.get("FF20").equals("")) {
            salesSlip.setFF20(HexBinary.encode(tlvList.get("FF20").getValue()));
        }
        if (tlvList.get("FF21") != null && !tlvList.get("FF21").equals("")) {
            salesSlip.setFF21(HexBinary.encode(tlvList.get("FF21").getValue()));
        }
        if (tlvList.get("FF30") != null && !tlvList.get("FF30").equals("")) {
            salesSlip.setFF30(HexBinary.encode(tlvList.get("FF30").getValue()));
        }
        if (tlvList.get("FF31") != null && !tlvList.get("FF31").equals("")) {
            salesSlip.setFF31(HexBinary.encode(tlvList.get("FF31").getValue()));
        }
        if (tlvList.get("FF22") != null && !tlvList.get("FF22").equals("")) {
            salesSlip.setFF22(HexBinary.encode(tlvList.get("FF22").getValue()));
        }
        if (tlvList.get("FF23") != null && !tlvList.get("FF23").equals("")) {
            salesSlip.setFF23(HexBinary.encode(tlvList.get("FF23").getValue()));
        }
        if (tlvList.get("FF24") != null && !tlvList.get("FF24").equals("")) {
            salesSlip.setFF24(HexBinary.encode(tlvList.get("FF24").getValue()));
        }
        if (tlvList.get("FF25") != null && !tlvList.get("FF25").equals("")) {
            salesSlip.setFF25(HexBinary.encode(tlvList.get("FF25").getValue()));
        }
        if (tlvList.get("FF26") != null && !tlvList.get("FF26").equals("")) {
            salesSlip.setFF26(HexBinary.encode(tlvList.get("FF26").getValue()));
        }
        if (tlvList.get("FF27") != null && !tlvList.get("FF27").equals("")) {
            salesSlip.setFF27(HexBinary.encode(tlvList.get("FF27").getValue()));
        }
        if (tlvList.get("FF28") != null && !tlvList.get("FF28").equals("")) {
            salesSlip.setFF28(HexBinary.encode(tlvList.get("FF28").getValue()));
        }
        if (tlvList.get("FF29") != null && !tlvList.get("FF29").equals("")) {
            salesSlip.setFF29(HexBinary.encode(tlvList.get("FF29").getValue()));
        }
        if (tlvList.get("FF2A") != null && !tlvList.get("FF2A").equals("")) {
            salesSlip.setFF2A(HexBinary.encode(tlvList.get("FF2A").getValue()));
        }
        if (tlvList.get("FF2B") != null && !tlvList.get("FF2B").equals("")) {
            salesSlip.setFF2B(HexBinary.encode(tlvList.get("FF2B").getValue()));
        }
        if (tlvList.get("FF40") != null && !tlvList.get("FF40").equals("")) {
            salesSlip.setFF40(HexBinary.encode(tlvList.get("FF40").getValue()));
        }
        if (tlvList.get("FF41") != null && !tlvList.get("FF41").equals("")) {
            salesSlip.setFF41(HexBinary.encode(tlvList.get("FF41").getValue()));
        }
        if (tlvList.get("FF42") != null && !tlvList.get("FF42").equals("")) {
            salesSlip.setFF42(HexBinary.encode(tlvList.get("FF42").getValue()));
        }
        if (tlvList.get("FF43") != null && !tlvList.get("FF43").equals("")) {
            salesSlip.setFF43(HexBinary.encode(tlvList.get("FF43").getValue()));
        }
        if (tlvList.get("FF44") != null && !tlvList.get("FF44").equals("")) {
            salesSlip.setFF44(HexBinary.encode(tlvList.get("FF44").getValue()));
        }
        if (tlvList.get("FF45") != null && !tlvList.get("FF45").equals("")) {
            salesSlip.setFF45(HexBinary.encode(tlvList.get("FF45").getValue()));
        }
        if (tlvList.get("FF46") != null && !tlvList.get("FF46").equals("")) {
            salesSlip.setFF46(HexBinary.encode(tlvList.get("FF46").getValue()));
        }
        if (tlvList.get("FF47") != null && !tlvList.get("FF47").equals("")) {
            salesSlip.setFF47(HexBinary.encode(tlvList.get("FF47").getValue()));
        }
        if (tlvList.get("FF57") != null && !tlvList.get("FF57").equals("")) {
            salesSlip.setFF57(HexBinary.encode(tlvList.get("FF57").getValue()));
        }
        if (tlvList.get("FF48") != null && !tlvList.get("FF48").equals("")) {
            salesSlip.setFF48(HexBinary.encode(tlvList.get("FF48").getValue()));
        }
        if (tlvList.get("FF49") != null && !tlvList.get("FF49").equals("")) {
            salesSlip.setFF49(HexBinary.encode(tlvList.get("FF49").getValue()));
        }
        if (tlvList.get("FF4A") != null && !tlvList.get("FF4A").equals("")) {
            salesSlip.setFF4A(HexBinary.encode(tlvList.get("FF4A").getValue()));
        }
        if (tlvList.get("FF4B") != null && !tlvList.get("FF4B").equals("")) {
            salesSlip.setFF4B(HexBinary.encode(tlvList.get("FF4B").getValue()));
        }
        if (tlvList.get("FF60") != null && !tlvList.get("FF60").equals("")) {
            salesSlip.setFF60(HexBinary.encode(tlvList.get("FF60").getValue()));
        }
        if (tlvList.get("FF61") != null && !tlvList.get("FF61").equals("")) {
            salesSlip.setFF61(HexBinary.encode(tlvList.get("FF61").getValue()));
        }
        if (tlvList.get("FF62") != null && !tlvList.get("FF62").equals("")) {
            salesSlip.setFF62(HexBinary.encode(tlvList.get("FF62").getValue()));
        }
        if (tlvList.get("FF63") != null && !tlvList.get("FF63").equals("")) {
            salesSlip.setFF63(HexBinary.encode(tlvList.get("FF63").getValue()));
        }
        if (tlvList.get("FF64") != null && !tlvList.get("FF64").equals("")) {
            salesSlip.setFF64(Utils.getString(tlvList.get("FF64").getValue()));
        }
        if (tlvList.get("FF65") != null && !tlvList.get("FF65").equals("")) {
            salesSlip.setFF65(HexBinary.encode(tlvList.get("FF65").getValue()));
        }
        if (tlvList.get("FF70") != null && !tlvList.get("FF70").equals("")) {
            salesSlip.setFF70(HexBinary.encode(tlvList.get("FF70").getValue()));
        }
        if (tlvList.get("FF71") != null && !tlvList.get("FF71").equals("")) {
            salesSlip.setFF71(HexBinary.encode(tlvList.get("FF71").getValue()));
        }
        if (tlvList.get("FF72") != null && !tlvList.get("FF72").equals("")) {
            salesSlip.setFF72(HexBinary.encode(tlvList.get("FF72").getValue()));
        }
        if (tlvList.get("FF73") != null && !tlvList.get("FF73").equals("")) {
            salesSlip.setFF73(HexBinary.encode(tlvList.get("FF73").getValue()));
        }
        iso.setSalesSlip(salesSlip);
    }
}
