package com.bestpay.cupsf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
/**
 * Created by HR on 2016/5/17.
 */
public class FilePathUtil {
	
	public static InputStream getInputStream(String propFile) throws Exception{
		File f = new File(propFile);
		InputStream is;
		if(f.exists()){
			is = new FileInputStream(f);
			return is;
		}else{
			return getInputStream(propFile,FilePathUtil.class);
		}
	}
	
	
	/**
	 * @param propFile
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static InputStream getInputStream(String propFile,Class<?> clz) throws Exception{
		File f = new File(propFile);
		InputStream is;
		if(f.exists()){
			is = new FileInputStream(f);
		}else{
			is = FilePathUtil.class.getResourceAsStream(propFile);
		}
		if(is==null){
			is = FilePathUtil.class.getClassLoader()
					.getResourceAsStream(propFile);
		}
		if(is==null){
			f = new File(clz.getClassLoader().getResource("").getPath()+"/"
					+clz.getPackage().getName().replaceAll("\\.", "/") +"/"+ propFile);
			is = new FileInputStream(f);
		}
		return is;
	}

}
