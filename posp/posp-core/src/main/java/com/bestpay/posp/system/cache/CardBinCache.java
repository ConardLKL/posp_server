package com.bestpay.posp.system.cache;

import com.bestpay.posp.system.dao.TStlBankCardBinDao;
import com.bestpay.posp.system.entity.TStlBankCardBin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

/**
 * 卡bin缓存
 * Created by HR on 2016/8/4.
 */
public class CardBinCache  extends HashMap<String, HashMap<String, TStlBankCardBin>> {

    @Autowired
    private TStlBankCardBinDao tStlBankCardBinDao;


    private CardBinCache(){

    }

    public void cache(){
        List<String> lengths = tStlBankCardBinDao.findAccountLen();
        for (String accountLen : lengths) {
            List<TStlBankCardBin> list = tStlBankCardBinDao.findByAccountLen(accountLen);
            if (list == null || list.size() == 0) {
                continue;
            }
            HashMap<String, TStlBankCardBin> cache = new HashMap<String, TStlBankCardBin>();
            for (TStlBankCardBin tStlBankCardBin : list) {
                cache.put(tStlBankCardBin.getCardBin(), tStlBankCardBin);
            }
            this.put(accountLen, cache);
        }
    }

    public TStlBankCardBin getCardBin(String cardNo){
        String accountLen = String.valueOf(cardNo.trim().length());
        if(this.get(accountLen) == null) return null;
        HashMap<String, TStlBankCardBin> cache = this.get(accountLen);
        for(TStlBankCardBin tStlBankCardBin : cache.values()){
            String cardBin = cardNo.trim().substring(0,Integer.valueOf(tStlBankCardBin.getBankidLen()));
            if(cache.get(cardBin) != null){
                return cache.get(cardBin);
            }
        }
        return null;
    }
}
