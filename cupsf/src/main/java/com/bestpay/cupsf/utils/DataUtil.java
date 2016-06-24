package com.bestpay.cupsf.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by HR on 2016/5/24.
 */
public class DataUtil {
    public static Date getDate(){
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = dfs.format(new Date());
        Date date = null;
        try {
            date = dfs.parse(dfs.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    @Test
    public void test(){
        Date date1 = new Date();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date date2 = new Date();
        long interval = (date2.getTime()-date1.getTime())/1000;
        System.out.println(interval);
    }
}
