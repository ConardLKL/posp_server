package cn.com.bestpay.posp.protocol;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.com.bestpay.posp.system.entity.XmlHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析xml格式报文
 * @author HR
 *
 */

public class XmlAnalysis {

	private static final Logger log = LoggerFactory.getLogger(XmlAnalysis.class);
	/**
	 * 解析xml
	 * @param buf
	 * @return
	 */
	public static Map<String,Object> parseXml(ByteBuf buf){
		Map<String,Object> map = new HashMap<String, Object>();
		byte[] buffer = new byte[buf.readableBytes()];
		buf.readBytes(buffer);
//		String message = "5044393462577767646D567963326C76626A30694D5334774969426C626D4E765A476C755A7A306952304A4C496A382B43676F38556B39505644344B49434138516B39455754344B494341670A49447853525645774D54417750676F674943416749434138566D5679506C59784C6A41384C315A6C636A344B4943416749447776556B56524D4445774D44344B494341384C304A5052466B2B0A43694167504568465155512B43694167494341385158427750744C74317165347471477154564250557A77765158427750676F674943416750456C4E52556B2B4F4459774D4463324D444D310A4E7A59314D54457A5043394A5455564A50676F674943416750456C4E55306B2B4E4459774D4463334D5441334E4455334D7A4D305043394A54564E4A50676F67494341675045317A5A306C450A506A417A4D4463784E6A49314D7A59384C30317A5A306C4550676F67494341675045317A5A314A6C5A6A34774D7A41334D5459794E544D324D446B7A5043394E633264535A57592B436941670A494341385433426C636B6C45506A77765433426C636B6C4550676F674943416750464A6C63325679646D552B504339535A584E6C636E5A6C50676F674943416750464A7A63454E765A4755760A50676F674943416750464A7A6345317A5A79382B43694167494341385532567A63326C766269382B436941674943413856484A446232526C506A41784D4441384C3152795132396B5A54344B0A49434167494478575A58492B4D5441774D447776566D567950676F674943416750466476636D74455958526C506A49774D5459774D7A41335043395862334A72524746305A54344B494341670A4944785862334A7256476C745A5434784E6A49314D7A59384C316476636D74556157316C50676F6749447776534556425244344B504339535430395550673D3D";
//		byte[] buffer = HexCodec.hexDecode(message);
		try {
        	String msg = new String(Base64.decodeBase64(buffer),"GBK");
        	log.info(String.format("[%s] xml Message:" + msg, "666666666"));
        	Document doc = DocumentHelper.parseText(msg); //将字符串转为XML
            Element root = doc.getRootElement(); // 获取根节点
            Element head = root.element("HEAD");
            Element body = root.element("BODY");
            XmlHead xmlHead = getHead(head);
            map.put("head", xmlHead);
            map.put("body", body);
            map.put("tranCode", xmlHead.getTrCode());
//            map.put("iso8583", HexCodec.hexEncode(iso8583));
            return map;
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	/**
	 * 解析xml报文头信息
	 * @param head
	 * @return
	 * @throws Exception
     */
	public static XmlHead getHead(Element head) throws Exception{
		XmlHead Xmlhead = new XmlHead();
		String app = head.elementText("App"); //应用名称
		if(StringUtils.isNotEmpty(app)){
			Xmlhead.setApp(app);
		}
        String ver = head.elementText("Ver"); //版本号
        if(StringUtils.isNotEmpty(ver)){
        	Xmlhead.setVer(ver);
		}
        String msgID = head.elementText("MsgID"); //报文标识号
        if(StringUtils.isNotEmpty(msgID)){
        	Xmlhead.setMsgID(msgID);
		}
        String msgRef = head.elementText("MsgRef"); //报文参考号
        if(StringUtils.isNotEmpty(msgRef)){
        	Xmlhead.setMsgRef(msgRef);
		}
        String trCode = head.elementText("TrCode"); //交易代码
        if(StringUtils.isNotEmpty(trCode)){
        	Xmlhead.setTrCode(trCode);
		}
        String workDate = head.elementText("WorkDate"); //工作日期
        if(StringUtils.isNotEmpty(workDate)){
        	Xmlhead.setWorkDate(workDate);
		}
        String workTime = head.elementText("WorkTime"); //工作时间
        if(StringUtils.isNotEmpty(workTime)){
        	Xmlhead.setWorkTime(workTime);
		}
        String IMSI = head.elementText("IMSI"); //手机卡唯一特征码
        if(StringUtils.isNotEmpty(IMSI)){
        	Xmlhead.setIMSI(IMSI);
        }
        String IMEI = head.elementText("IMEI"); //手机唯一特征码
        if(StringUtils.isNotEmpty(IMEI)){
        	Xmlhead.setIMEI(IMEI);
        }
        String operID = head.elementText("OperID"); //操作员
        if(StringUtils.isNotEmpty(operID)){
        	Xmlhead.setOperID(operID);
		}
        String session = head.elementText("Session"); //会话
        if(StringUtils.isNotEmpty(session)){
        	Xmlhead.setSession(session);
		}
        String rspCode = head.elementText("RspCode"); //应答代码
        if(StringUtils.isNotEmpty(rspCode)){
        	Xmlhead.setRspCode(rspCode);
		}
        String rspMsg = head.elementText("RspMsg"); //应答描述
        if(StringUtils.isNotEmpty(rspMsg)){
        	Xmlhead.setRspMsg(rspMsg);
		}
        String reserve = head.elementText("Reserve"); //预留字段
        if(StringUtils.isNotEmpty(reserve)){
        	Xmlhead.setReserve(reserve);
		}
        return Xmlhead;
	}
	/**
	 * 设置xml报文HEAD节点
	 * @param root
	 * @param xmlHead
	 */
	public static void setHead(Element root,XmlHead xmlHead){
		Element head = root.addElement("HEAD");
		if(StringUtils.isNotEmpty(xmlHead.getApp())){
			head.addElement("App").setText(xmlHead.getApp());//应用名称
		}
        if(StringUtils.isNotEmpty(xmlHead.getVer())){
			head.addElement("Ver").setText(xmlHead.getVer());//版本号
		}
        if(StringUtils.isNotEmpty(xmlHead.getMsgID())){
        	head.addElement("MsgID").setText(xmlHead.getMsgID());//报文标识号
		}
        if(StringUtils.isNotEmpty(xmlHead.getMsgRef())){
        	head.addElement("MsgRef").setText(xmlHead.getMsgRef());//报文参考号
		}
        if(StringUtils.isNotEmpty(xmlHead.getTrCode())){
        	head.addElement("TrCode").setText(xmlHead.getTrCode());//交易代码
		}
        if(StringUtils.isNotEmpty(xmlHead.getWorkDate())){
        	head.addElement("WorkDate").setText(xmlHead.getWorkDate());//工作日期
		}
        if(StringUtils.isNotEmpty(xmlHead.getWorkTime())){
        	head.addElement("WorkTime").setText(xmlHead.getWorkTime());//工作时间
		}
        if(StringUtils.isNotEmpty(xmlHead.getIMSI())){
        	head.addElement("IMSI").setText(xmlHead.getIMSI());//手机卡唯一特征码
        }
        if(StringUtils.isNotEmpty(xmlHead.getIMEI())){
        	head.addElement("IMEI").setText(xmlHead.getIMEI());//手机唯一特征码
        }
        if(StringUtils.isNotEmpty(xmlHead.getOperID())){
        	head.addElement("OperID").setText(xmlHead.getOperID());//操作员
		}
        if(StringUtils.isNotEmpty(xmlHead.getSession())){
        	head.addElement("Session").setText(xmlHead.getSession());//会话
		}
        if(StringUtils.isNotEmpty(xmlHead.getRspCode())){
        	head.addElement("RspCode").setText(xmlHead.getRspCode());//应答代码
		}
        if(StringUtils.isNotEmpty(xmlHead.getRspMsg())){
        	head.addElement("RspMsg").setText(xmlHead.getRspMsg());//应答描述
		}
        if(StringUtils.isNotEmpty(xmlHead.getReserve())){
        	head.addElement("Reserve").setText(xmlHead.getReserve());//预留字段
		}
	}
}
