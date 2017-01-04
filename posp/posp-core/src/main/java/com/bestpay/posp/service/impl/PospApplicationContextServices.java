/**
 *
 */
package com.bestpay.posp.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.bestpay.posp.spring.PospApplicationContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.tisson.sfip.module.reboot.SfipContainerBootstrapUtils;

/**
 * @author yihaijun
 */
@Slf4j
public class PospApplicationContextServices implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        try {
            log.info("PospApplicationContext.setCtx() begin...");
            this.applicationContext = applicationContext;
            // org.springframework.web.context.support.XmlWebApplicationContext
            // cannot be cast to
            // org.springframework.context.support.ClassPathXmlApplicationContext
            PospApplicationContext.setCtx(applicationContext);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("PospApplicationContext.setCtx() Exception.", e);
        }
        init();
        log.info("PospApplicationContext.setCtx() return.");
    }

    public void init() {
        String cfgFileSimpleName = "t_tisson_esb_forwarding.xls";
        String cfgFileName = getPospCfgHome() + cfgFileSimpleName;
        boolean needCopyCfgFile = false;
        if(applicationContext instanceof org.springframework.web.context.support.XmlWebApplicationContext){
        	needCopyCfgFile = true;
        }
        File file = new File(cfgFileName);
        if (!file.exists()) {
        	needCopyCfgFile = true;
        }
    	if(needCopyCfgFile){
        	log.info("Now copy config file to system path from jvm resource file." );
    		copyResourceFile2SysPath(cfgFileSimpleName, cfgFileName);
    	}
    	log.info("Now config file is [" + cfgFileName + "]" );
    }

    public static String getPospCfgHome() {
    	String sfipHome = SfipContainerBootstrapUtils.locateSfipHome();
        String pospCfgHome = sfipHome + "/config/";
        File file = new File(pospCfgHome);
        if (!file.exists()) {
            file.mkdirs();
        }
        return pospCfgHome;
    }

    public static int copyResourceFile2SfipPath(String src, String dest) {
        return copyResourceFile2SysPath(src, getPospCfgHome() + dest);
    }

    public static int copyResourceFile2SysPath(String src, String dest) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(dest));
        } catch (Throwable e1) {
            e1.printStackTrace();
            return -1;
        }// 被复制到此文件中
        Resource resource = new ClassPathResource(src); // 文件源 :找类文件
        try {
            FileCopyUtils.copy(resource.getInputStream(), os);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        // InputStream is = PospApplicationContextServices.class
        // .getResourceAsStream(src);
        // BufferedReader br = new BufferedReader(new InputStreamReader(is));
        // FileUtil opt = new FileUtil();
        // File fd = opt.createFile(dest);
        // BufferedWriter bw = null;// new BufferedWrite
        return 0;
    }

    public class FileUtil {
        private ByteBuffer mbread = null, mbwrite = null;

        public File createFile(String fileUrl) {
            return createFile(fileUrl, true);
        }

        public File createFile(String fileUrl, boolean fileflag) {
            File newFile = new File(fileUrl);
            try {
                if (fileflag) {
                    String parentStr = newFile.getParent();
                    // System.out.println(parentStr);
                    String fileName = newFile.getName();
                    File newDir = parentStr != null ? createFile(parentStr,
                            false) : new File("");
                    File theFile = new File(newDir, fileName);
                    theFile.createNewFile();
                    /*
                     * if(!theFile.createNewFile())
					 * System.out.println(fileUrl+" already existed!");
					 */
                } else {
                    if (!newFile.exists())
                        newFile.mkdirs();
                }
            } catch (Exception e) {
                // System.out.println(e);
                log.info("[FileAdapter]", "[createFile]", e);
            }
            return newFile;
        }

        public int copyTo(String toFilePath, long every) {
            int c = 0;
            // FileAdapter fa = new FileAdapter(toFilePath);
            // fa.createFile();
            // byte[] bts = null;
            // long begin=0;
            // while((bts=this.getReader(begin, every).readAll())!=null){
            // c+=fa.getWriter().write(bts);
            // begin+=bts.length;
            // }
            // fa.close();
            // //System.out.println("copy length:"+c);
            return c;
        }

        public byte[] read(int totalnum) {
            int readnum = Math.min(mbread.remaining(), totalnum);
            byte[] bt = null;
            try {
                if (readnum > 0) {
                    bt = new byte[readnum];
                    mbread.get(bt);
                }
            } catch (Exception e) {
                System.out.println(e);
                // LogUtil.info("[ReadAdapter]", "[read]", e.getMessage());
            }
            return bt;
        }
    }
}
