package com.bestpay.cupsf.service;

import com.bestpay.cupsf.entity.CupsfBuffer;
import com.bestpay.cupsf.entity.MessageBuffer;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * posp缓存监控
 * Created by HR on 2016/5/24.
 */
@Slf4j
public class PospMonitor implements Runnable{
    @Override
    public void run() {
        while(true) {
            try {
                if (CupsfBuffer.posp == null || CupsfBuffer.posp.size() < 1) {
//                    Thread.sleep(100);
                    continue;
                }
                for (String serialNo : CupsfBuffer.posp.keySet()) {
                    MessageBuffer buffer = CupsfBuffer.posp.get(serialNo);
                    if(CupsfBuffer.posp.containsKey(serialNo)){
                        Date end = new Date();
                        Date begin = buffer.getDate();
                        Long difference = (end.getTime() - begin.getTime()) / 1000;
                        //超过60秒删除
                        if (difference >= Long.valueOf(60)) {
                            log.info("超时删除！");
                            CupsfBuffer.posp.remove(serialNo);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void startup(){
        new Thread(this).start();
    }
}
