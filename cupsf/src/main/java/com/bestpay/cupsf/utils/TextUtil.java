package com.bestpay.cupsf.utils;

import com.bestpay.cupsf.entity.TCfgPktDef;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by HR on 2016/5/17.
 */
@Slf4j
public class TextUtil {
	private static String filePath = "unipay.define";
	private static InputStreamReader read  = null;
	private static String encoding="UTF-8";
	static {
		try {
			log.info("加载银联域定义配置文件……");
			InputStream in = FilePathUtil.getInputStream(filePath);
			read = new InputStreamReader(in,encoding);
		}catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	public static List<TCfgPktDef> readTxtFile(){
        try {
			BufferedReader bufferedReader = new BufferedReader(read);
			List<TCfgPktDef> pktDef = new ArrayList<TCfgPktDef>();
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				String[] str = lineTxt.split("\\s+");
				TCfgPktDef tCfgPktDef = new TCfgPktDef();
				tCfgPktDef.setBit(Long.valueOf(str[0]));
				tCfgPktDef.setType(str[1]);
				tCfgPktDef.setFormat(str[2]);
				if(!str[3].equals("NULL")){
					tCfgPktDef.setLength(Long.valueOf(str[3]));
				}
				tCfgPktDef.setMsgType("6001");
				pktDef.add(tCfgPktDef);
			}
			log.info("银联域定义配置文件加载成功！");
			read.close();
			bufferedReader.close();
			return pktDef;
        } catch (Exception e) {
			log.info("银联域定义配置文件加载失败！");
            e.printStackTrace();
        }
        return null;
	}
}
