package com.bestpay.posp.service.checker;

import com.bestpay.posp.constant.POSPConstant;
import com.bestpay.posp.protocol.FlowMessage;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.service.impl.BaseStationInfo;
import com.bestpay.posp.service.impl.DataManipulations;
import com.bestpay.posp.service.impl.ObtainObjectInformation;
import com.bestpay.posp.service.impl.RespCodeInformation;
import com.bestpay.posp.system.entity.TCfgRiskWhiteList;
import com.bestpay.posp.system.entity.TMcmPosinfo;
import com.tisson.sfip.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 位置控制
 * Created by HR on 2016/5/9.
 */
@Slf4j
@Component
public class Pub_PositionControl {
    @Autowired
    private ObtainObjectInformation objectInformation;
    @Autowired
    private DataManipulations dataManipulations;

    public boolean positionControl(IsoMessage iso){
        if(!StringUtils.equals(iso.getFlow().getBaseStationType(),"CDMA")){
            return true;
        }
        if(StringUtils.isEmpty(iso.getFlow().getBaseStationValues())){
            generateAlarmInfo(iso);//生成告警信息
            return true;
        }
        TMcmPosinfo mcmPosinfo = objectInformation.getTMcmPosinfo(iso);
        if(posPositionControl(iso,mcmPosinfo)){
            return true;
        }
        RespCodeInformation.getAndReturnRespCodeInfo(POSPConstant.POSP_A058,iso);
        return false;
    }

    /**
     * 终端合法位置控制
     * @return
     */
    private boolean posPositionControl(IsoMessage iso,TMcmPosinfo mcmPosinfo){
        if(StringUtils.isEmpty(mcmPosinfo.getStationVal1())
                && StringUtils.isEmpty(mcmPosinfo.getStationVal2())
                && StringUtils.isEmpty(mcmPosinfo.getStationVal3())){
            merchantPositionControl(iso);
            return true;
        }
        String[] stationVals = iso.getFlow().getBaseStationValues().split(";");
        StringBuffer stations = new StringBuffer();
        StringBuffer sids = new StringBuffer();
        int count = 0;
        if(mcmPosinfo.getStationVal1() != null){
            stations.append(mcmPosinfo.getStationVal1());
            sids.append(mcmPosinfo.getStationVal1().split(",")[0]);
        }
        if(mcmPosinfo.getStationVal2() != null){
            stations.append(";");
            sids.append(";");
            stations.append(mcmPosinfo.getStationVal2());
            sids.append(mcmPosinfo.getStationVal2().split(",")[0]);
        }
        if(mcmPosinfo.getStationVal3() != null){
            stations.append(";");
            sids.append(";");
            stations.append(mcmPosinfo.getStationVal3());
            sids.append(mcmPosinfo.getStationVal3().split(",")[0]);
        }
        for(String stationVal:stationVals){
            if(stations.toString().contains(stationVal)){
                return true;
            }
            if(sids.toString().contains(stationVal.split(",")[0])){
                count ++;
            }
        }
        merchantPositionControl(iso);
//        generateAlarmInfo(iso);//生成告警信息
        if(count == stationVals.length){
            log.info(String.format("[%s] SID与终端合法基站信息相同",iso.getSeq()));
            return true;
        }
        log.info(String.format("[%s] 与终端合法基站信息不相同",iso.getSeq()));
        return false;
    }

    /**
     * 商户合法位置控制
     * @param iso
     * @return
     */
    private void merchantPositionControl(IsoMessage iso){
        List<TCfgRiskWhiteList> tCfgRiskWhiteLists
                = objectInformation.getTCfgRiskWhiteLists(iso.getFlow().getServAgent());
        if(tCfgRiskWhiteLists == null
                || tCfgRiskWhiteLists.isEmpty()){
            generateAlarmInfo(iso);//生成告警信息
            return ;
        }
        for(TCfgRiskWhiteList tCfgRiskWhiteList:tCfgRiskWhiteLists){
            if(StringUtils.isEmpty(tCfgRiskWhiteList.getStationVal())){
                break;
            }
            if(iso.getFlow().getBaseStationValues().contains(tCfgRiskWhiteList.getStationVal())){
                return ;
            }
        }
        generateAlarmInfo(iso);//生成告警信息
    }
    /**
     * 生成告警信息
     * @param iso
     * @return
     */
    private IsoMessage generateAlarmInfo(IsoMessage iso){
        String[] stationVals = iso.getFlow().getBaseStationValues().split(";");
        for(String stationVal:stationVals){
            dataManipulations.insertMoveInfo(iso,stationVal);
        }
        return iso;
    }
}
