package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.FieldConstant;
import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.constant.PosConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.DataManipulations;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.*;
import com.bestpay.posp.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 金额控制
 * Created by HR on 2016/5/6.
 */
@Slf4j
@Component
public class Pub_AmountControl {

    @Autowired
    private TLogTranFlowService tLogTranFlowService;
    @Autowired
    private DataManipulations dataManipulations;
    @Autowired
    private ObtainObjectInformation objectInformation;
    /**
     * 金额控制
     * @param iso
     * @return
     */
    public IsoMessage amountControl(IsoMessage iso) {
        iso.setState(false);
        if(!iso.getFlow().getTranCode().equals(PosConstant.TRANS_TYPE_0200000022)
                && !iso.getFlow().getTranCode().equals(PosConstant.TRANS_TYPE_0100030610)
                && !iso.getFlow().getTranCode().equals(PosConstant.TRANS_TYPE_0220200025)){
            iso.setState(true);
            return iso;
        }
        // 退货交易，如果交易额大于原始金额或者大于剩余金额返回码64
        if (iso.getFlow().getTranCode().equals(PosConstant.TRANS_TYPE_0220200025)) {
            String serialNo = iso.getFlow().getTranDate().substring(2,4)
                    +iso.getField(FieldConstant.FIELD64_61).substring(12, 16)
                    +iso.getField(FieldConstant.FIELD64_37);
            TLogTranFlow ttLogTranFlow1 = objectInformation.getTLogTranFlow(serialNo);
            TLogTranFlowH tLogTranFlowH1 = objectInformation.getTLogTranFlowH(serialNo);
            if(ttLogTranFlow1 == null && tLogTranFlowH1 == null){
                RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A025,iso);
                log.info(String.format("[%s] amountControl：原交易不存在！ ", iso.getSeq()));
                return iso;
            }
            if ((ttLogTranFlow1 != null
                        && ttLogTranFlow1.getTranAmount() != null
                        && ttLogTranFlow1.getRemainAmount() != null
                        && Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                            <= yuanToCent(ttLogTranFlow1.getTranAmount().toString())
                        && Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                            <= yuanToCent(ttLogTranFlow1.getRemainAmount().toString()))
                    || (tLogTranFlowH1 != null
                        && tLogTranFlowH1.getTranAmount() != null
                        && tLogTranFlowH1.getRemainAmount() != null
                        && Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                            <= yuanToCent(tLogTranFlowH1.getTranAmount().toString())
                        && Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                            <= yuanToCent(tLogTranFlowH1.getRemainAmount().toString()))) {
                iso.setState(true);
                return iso;
            }else{
                RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A064,iso);
                log.info(String.format("[%s] amountControl：退货金额不正确! ", iso.getSeq()));
                return iso;
            }
        }
        // 不是退货交易，如果超过日累计金额上限返回码61，写入数据库；
        // 交易金额大于最大限制金额返回码61，写入数据库；
        // 交易金额大于单笔预警金额限制，只写入数据库
        int sumTerm = 0, sumMerch = 0, sumCard = 0;
        TMcmMerchant mcmMerchant = objectInformation.getTMcmMerchant(iso);
        TMcmPosinfo mcmPosinfo = objectInformation.getTMcmPosinfo(iso);
        TStlRiskTransCard stlRiskTransCard = objectInformation.getTStlRiskTransCard(iso);
        if(mcmMerchant != null && mcmPosinfo != null && stlRiskTransCard != null){
            if (stlRiskTransCard.getAlarmAmountTrans() != null
                    && stlRiskTransCard.getAlarmAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(stlRiskTransCard.getAlarmAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A000,iso);
                    log.info(String.format("[%s] amountControl：超过此卡单笔预警金额限制! ",iso.getSeq()));
                    dataManipulations.insertDubious(iso);
                }
            }
            if (stlRiskTransCard.getMaxAmountTrans() != null
                    && stlRiskTransCard.getMaxAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(stlRiskTransCard.getMaxAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A061,iso);
                    log.info(String.format("[%s] amountControl：超过此卡单笔最大金额限制! ",iso.getSeq()));
                    return iso;
                }
            }
            if (mcmPosinfo.getPeakAmountPerday() != null
                    && mcmPosinfo.getAlarmAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(mcmPosinfo.getAlarmAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A100,iso);
                    log.info(String.format("[%s] amountControl：超过终端单笔预警金额限制! ",iso.getSeq()));
                    dataManipulations.insertDubious(iso);
                }
            }
            if (mcmPosinfo.getMaxAmountTrans() != null
                    && mcmPosinfo.getMaxAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(mcmPosinfo.getMaxAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A161,iso);
                    log.info(String.format("[%s] amountControl：超过终端单笔最大金额限制! ",iso.getSeq()));
                    return iso;
                }
            }
            if (mcmMerchant.getAlarmAmountTrans() != null
                    && mcmMerchant.getAlarmAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(mcmMerchant.getAlarmAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A200,iso);
                    log.info(String.format("[%s] amountControl：超过商户单笔预警金额限制! ",iso.getSeq()));
                    dataManipulations.insertDubious(iso);
                }
            }
            if (mcmMerchant.getMaxAmountTrans() != null
                    && mcmMerchant.getMaxAmountTrans() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4))
                        > yuanToCent(mcmMerchant.getMaxAmountTrans().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A261,iso);
                    log.info(String.format("[%s] amountControl：超过商户单笔最大金额限制! ",iso.getSeq()));
                    return iso;
                }
            }

            //计算卡号日累计金额
            TLogTranFlow cardLogTranFlow = new TLogTranFlow();
            cardLogTranFlow.setCardNo(objectInformation.encryptionAccountNumber(iso.getField(FieldConstant.FIELD64_2)));
            cardLogTranFlow.setTranDate(iso.getFlow().getTranDate());
            TLogTranFlow cardTLogTranFlow = tLogTranFlowService.sumTranAmount(cardLogTranFlow);
            sumCard = yuanToCent(cardTLogTranFlow.getTranAmount().toString());
            if ( stlRiskTransCard.getPeakAmountPerday() != null
                    && stlRiskTransCard.getPeakAmountPerday() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4)) + sumCard
                        > yuanToCent(stlRiskTransCard.getPeakAmountPerday().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A461,iso);
                    iso.setRspMsg("超过卡日累计金额设置（现累计金额："+Double.valueOf(Integer.parseInt(iso.getField(4))+sumCard)/100+")");
                    log.info(String.format("[%s] amountControl：超过卡日累计金额上限! ",iso.getSeq()));
                    return iso;
                }
            }
            //计算终端日累计金额
            TLogTranFlow logTranFlow = new TLogTranFlow();
            logTranFlow.setTermCode(iso.getField(FieldConstant.FIELD64_41));
            logTranFlow.setMerchCode(iso.getField(FieldConstant.FIELD64_42));
            logTranFlow.setTranDate(iso.getFlow().getTranDate());
            TLogTranFlow logTranFlow1 = tLogTranFlowService.sumTranAmount(logTranFlow);
            sumTerm = yuanToCent(logTranFlow1.getTranAmount().toString());
            if (mcmPosinfo.getPeakAmountPerday() != null && mcmPosinfo.getPeakAmountPerday() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4)) + sumTerm
                        > yuanToCent(mcmPosinfo.getPeakAmountPerday().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A561,iso);
                    iso.setRspMsg("超过终端日累计金额设置（现累计金额："+Double.valueOf(Integer.parseInt(iso.getField(4))+sumTerm)/100+")");
                    log.info(String.format("[%s] amountControl：超过终端日累计金额上限! ",iso.getSeq()));
                    return iso;
                }
            }
            //计算商户日累计金额
            TLogTranFlow tLogTranFlow = new TLogTranFlow();
            tLogTranFlow.setMerchCode(iso.getField(FieldConstant.FIELD64_42));
            tLogTranFlow.setTranDate(iso.getFlow().getTranDate());
            TLogTranFlow tLogTranFlow1 = tLogTranFlowService.sumTranAmount(tLogTranFlow);
            sumMerch = yuanToCent(tLogTranFlow1.getTranAmount().toString());
            if (mcmMerchant.getPeakAmountPerday() != null && mcmMerchant.getPeakAmountPerday() != 0) {
                if (Integer.parseInt(iso.getField(FieldConstant.FIELD64_4)) + sumMerch
                        > yuanToCent(mcmMerchant.getPeakAmountPerday().toString())) {
                    RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A661,iso);
                    iso.setRspMsg("超过商户日累计金额设置（现累计金额："+Double.valueOf(Integer.parseInt(iso.getField(4))+sumMerch)/100+")");
                    log.info(String.format("[%s] amountControl：超过商户日累计金额上限! ",iso.getSeq()));
                    return iso;
                }
            }
        }
        iso.setState(true);
        return iso;
    }

    /**
     * （人民币）元转化成分
     * @param cent
     * @return
     */
    private int yuanToCent(String cent){
        return new BigDecimal(cent).movePointRight(2).intValue();
    }
}
