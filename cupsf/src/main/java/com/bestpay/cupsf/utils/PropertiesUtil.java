package com.bestpay.cupsf.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * Created by HR on 2016/5/17.
 */
public class PropertiesUtil {

	private static Properties prop = new Properties();
	private static String file = "cupsf.properties";
	static {
		try {
			prop.load(FilePathUtil.getInputStream(file));
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getProperty(String key){
		return prop.getProperty(key);
	}
	public static void setProper(String key,String value){
		/**
		* 将文件加载到内存中，在内存中修改key对应的value值，再将文件保存
		*/
		try {
			prop.setProperty(key, value);
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, null);
			fos.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
