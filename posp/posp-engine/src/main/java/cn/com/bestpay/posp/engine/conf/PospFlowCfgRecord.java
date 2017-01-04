package cn.com.bestpay.posp.engine.conf;

import java.io.Serializable;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 概述该类作用，请详细描述。
 * </p>
 * <p>
 * 创建日期：2013-1-4 下午03:22:02
 * </p>
 * 
 * @author yihaijun
 */
@Slf4j
public class PospFlowCfgRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5958584847453974016L;

	private String[] record;
	private String transCode;

	/**
	 * @return the record
	 */
	public String[] getRecord() {
		return record;
	}

	/**
	 * @param record
	 *            the record to set
	 */
	public void setRecord(String[] record) {
		this.record = record;
	}

	/**
	 * @return the transCode
	 */
	public String getTransCode() {
		return transCode;
	}

	/**
	 * @param record
	 *            the record to set
	 */
	public void setRecord(Map<String, String> map) {
		log.info("map="+map);
		transCode = map.get("rootconsumerlocusid");
		String[] tmpRecord = { map.get("routeid"), map.get("routename"),
				map.get("currentconsumer"), map.get("currentservicecode"),
				map.get("currentrequesttype"), map.get("and1"),
				map.get("and2"), map.get("or1"), map.get("or2"),
				map.get("forwardingclass"), map.get("forwardingmethod"), 
				map.get("forwardingrequest"), map.get("forwardingresponse"), 
				map.get("transformerrequest"), map.get("transformerresponse")
				};
		for(int i = 0;i<tmpRecord.length;i++){
			if(tmpRecord[i] == null){
				tmpRecord[i] = "";
			}
		}
		this.record = tmpRecord;
	}

	public static String[] toStringArryTitle() {
		String[] excelTitle = { "节点ID", "节点名称", "上一服务代码", "本服务代码", "请求类型",
				"AND条件1", "AND条件2", "OR条件1", "OR条件2", "处理组件", "处理方法", "请求类",
				"响应类", "请求转换器", "响应转换器" };
		return excelTitle;
	}

	public static String getCfgQuerySelectSql() {
		StringBuffer out = new StringBuffer();
		//out.append("\r\n	select n.ROOTCONSUMERLOCUS,");
		out.append("\r\n	select m.ROOTCONSUMERLOCUSID,");
		out.append("\r\n    M.ROUTEID,");
		out.append("\r\n    M.ROUTENAME,");
		out.append("\r\n    M.CURRENTCONSUMER,");
		out.append("\r\n    M.CURRENTSERVICECODE,");
		out.append("\r\n    M.CURRENTREQUESTTYPE,");
		out.append("\r\n    M.AND1,");
		out.append("\r\n    M.AND2,");
		out.append("\r\n    M.OR1,");
		out.append("\r\n    M.OR2,");
		out.append("\r\n    M.FORWARDINGCLASS,");
		out.append("\r\n    M.FORWARDINGMETHOD,");
		out.append("\r\n    M.FORWARDINGREQUEST,");
		out.append("\r\n    M.FORWARDINGRESPONSE,");
		out.append("\r\n    M.TRANSFORMERREQUEST,");
		out.append("\r\n    M.TRANSFORMERRESPONSE");

//		out.append("\r\nfrom T_INFO_SFIPS_FLOW m, T_INFO_SFIPS_LIST n");
//		out.append("\r\nwhere m.rootconsumerlocusid = n.rootconsumerlocusid");
//		out.append("\r\norder by n.rootconsumerlocus, m.routeid");
		out.append("\r\nfrom T_INFO_SFIPS_FLOW m");
		out.append("\r\norder by m.ROOTCONSUMERLOCUSID, m.routeid");
		
		return out.toString();
	}
}
