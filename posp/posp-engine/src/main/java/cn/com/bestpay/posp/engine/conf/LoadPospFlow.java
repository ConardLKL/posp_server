/**
 * 
 */
package cn.com.bestpay.posp.engine.conf;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.regaltec.nma.collector.common.ExcelUtil;
import com.tisson.sfip.module.reboot.SfipProperties;
import com.tisson.sfip.esb.cfg.LoadFlowCfg;
import com.tisson.sfip.module.util.DBMapping;
import com.tisson.sfip.module.util.DateUtil;
import com.tisson.sfip.module.util.SystemUtil;
import com.tisson.sfip.util.FileUtils;

/**
 * @author yihaijun
 * 
 */
@Slf4j
public class LoadPospFlow implements LoadFlowCfg {
	private DBMapping dbOpt;
	private String cmdFileName = SystemUtil.getSfipHome()
			+ "/bin/LoadPospFlow.cmd";
	private String cfgFileSimpleName = "t_tisson_esb_forwarding.xls";
	private String cfgFileName = SystemUtil.getSfipHome() + "/config/"
			+ cfgFileSimpleName;

	private static long lastLoadTime = 0;

	/**
	 * @return the dbOpt
	 */
	public DBMapping getDbOpt() {
		return dbOpt;
	}

	/**
	 * @param dbOpt
	 *            the dbOpt to set
	 */
	public void setDbOpt(DBMapping dbOpt) {
		this.dbOpt = dbOpt;
	}

	public int loadFlow() {
		File f = new File(cmdFileName);
		if (!f.exists()) {
			return 0;
		}
		if (f.lastModified() <= lastLoadTime) {
			return 0;
		}
		lastLoadTime = f.lastModified();
		backupOldCfg();
		loadCfg();
		return 0;
	}

	private int backupOldCfg() {
		if(log.isDebugEnabled()){
			log.debug("backupOldCfg begin...");
		}
		final File appDir = new File(SystemUtil.getSfipHome() + "/config",
				cfgFileSimpleName);
		final File destFileDir = new File(SystemUtil.getSfipHome(),
				SfipProperties.SFIP_CONFIG_BAK_FILENAME);
		final File destFile = new File(destFileDir, cfgFileSimpleName + "."
				+ DateUtil.toStrFromDate(new Date(), "yyyyMMddHHmmss"));
		try {
			FileUtils.moveFile(appDir, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log.debug("backupOldCfg return.");
		}
		return 0;
	}

	private int loadCfg() {
		if(log.isDebugEnabled()){
			log.debug("loadCfg begin...");
		}
		String querySql = PospFlowCfgRecord.getCfgQuerySelectSql();
		if(log.isDebugEnabled()){
			log.debug("querySql="+querySql);
		}
		List<Map<String, String>> resultSet = dbOpt.getRowSet(querySql);
		if(log.isInfoEnabled()){
			log.info("resultSet.size()="+resultSet.size());
		}
		for (int i = 0; i < resultSet.size(); i++) {
			Map<String, String> map = resultSet.get(i);
			loadCfgRow(map);
		}
		if(log.isDebugEnabled()){
			log.debug("loadCfg return.");
		}
		return 0;
	}

	private int loadCfgRow(Map<String, String> map) {
		PospFlowCfgRecord cfgRecord = new PospFlowCfgRecord();
		cfgRecord.setRecord(map);
		String transCode = cfgRecord.getTransCode();
		try {
			int wRet = ExcelUtil.write(cfgFileName, transCode,
					PospFlowCfgRecord.toStringArryTitle());

			wRet = ExcelUtil.append(cfgFileName, transCode,
					cfgRecord.getRecord());
			// ExcelUtil.edit(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7,
			// arg8);这个edit是以后装载一个流程会用到
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		return 0;
	}
}
