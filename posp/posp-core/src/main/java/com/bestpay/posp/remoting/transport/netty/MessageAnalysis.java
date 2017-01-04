package com.bestpay.posp.remoting.transport.netty;

import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.PKT_DEF;
import com.bestpay.posp.protocol.PacketDefineInitializer;
import com.bestpay.posp.protocol.util.HexCodec;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.entity.PospMessage;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
/**
 * 报文解析
 * @author HR
 *
 */
@Slf4j
@Component("MessageAnalysis")
public class MessageAnalysis {
	
	private IsoMessage iso = null;
	private PospMessage pospMessage = null;
	/**
	 * tcp报文解析
	 * @param msg
	 * @return
	 */
	public IsoMessage decode(ByteBuf msg){
		if(isRefresh(msg)){
			return null;
		}
		this.iso = new IsoMessage();
		this.pospMessage = new PospMessage();
//		setMposSign(msg);//mpos入口
		setTpdu(msg);
		setTelephone(msg);
		setBody(msg);
		setMessageDefine();
		setMessage();
		return iso;
	}
	/**
	 * http报文解析
	 * @param msg
	 * @return
	 */
	public IsoMessage HttpDecode(ByteBuf msg){
		this.iso = new IsoMessage();
		this.pospMessage = new PospMessage();
		setMessageLength(msg);
		setTpdu(msg);
		setBody(msg);
		setMessageDefine();
		setMessage();
		return iso;
	}

	/**
	 * 是否刷新交易
	 * @param msg
	 * @return
     */
	private boolean isRefresh(ByteBuf msg){
		ByteBuf mposMsg = msg.copy();
		ByteBuf mpos = mposMsg.readBytes(7);
		byte[] m_Pos = new byte[7];
		mpos.getBytes(0, m_Pos);
		if(StringUtils.equals(new String(m_Pos), "REFRESH")) {
			return true;
		}
		return false;
	}
	/**
	 * 解析报文长度
	 * @param msg
	 */
	private void setMposSign(ByteBuf msg){
		ByteBuf mposMsg = msg.copy();
		ByteBuf mpos = mposMsg.readBytes(4);
		byte[] m_Pos = new byte[4];
		mpos.getBytes(0, m_Pos);
		if(StringUtils.equals(new String(m_Pos), "MPOS")){
			msg.readBytes(4);
			ByteBuf mobilPhone = msg.readBytes(6);
			byte[] mobil_Phone = new byte[6];
			mobilPhone.getBytes(0, mobil_Phone);
			ByteBuf serialNo = msg.readBytes(9);
			byte[] serial_No = new byte[9];
			serialNo.getBytes(0, serial_No);
			iso.setSeq(HexCodec.hexEncode(serial_No));
			pospMessage.setMpos(new String(m_Pos));
			pospMessage.setMobilPhone(HexCodec.hexEncode(mobil_Phone).substring(1));
			log.info("RECV_MPOS:[" + pospMessage.getMpos() + "]");
			log.info("RECV_MOBILPHONE:[" + pospMessage.getMobilPhone() + "]");
			log.info("RECV_SERIALNO:[" + iso.getSeq() + "]");
		}
	}
	/**
	 * 解析报文长度
	 * @param msg
	 */
	private void setMessageLength(ByteBuf msg){
		ByteBuf msgLength = msg.readBytes(2);
		byte[] msg_Length = new byte[2];
		msgLength.getBytes(0, msg_Length);
		log.info("RECV_LENGTH:[" + HexCodec.hexEncode(msg_Length) + "]");
	}
	/**
	 * 解析tpdu
	 * @param msg
	 */
	private void setTpdu(ByteBuf msg){
		ByteBuf tpduBuf = msg.readBytes(5);
		byte[] tpdu_buffer = new byte[5];
		tpduBuf.getBytes(0, tpdu_buffer);
		pospMessage.setTpdu(tpdu_buffer);
		log.info("RECV_TPDU:[" + HexCodec.hexEncode(tpdu_buffer) + "]");
	}
	/**
	 * 解析电话号码
	 * @param msg
	 */
	private void setTelephone(ByteBuf msg){
		if(Utils.isTelephoneAccess(pospMessage.getTpdu())){
			ByteBuf lriHeadBuf = msg.readBytes(5);
			byte[] lriHead_buffer = new byte[5];
			lriHeadBuf.getBytes(0, lriHead_buffer);
			log.info("RECV_LRIHEAD:[" + HexCodec.hexEncode(lriHead_buffer) + "]");
			ByteBuf aniBuf = msg.readBytes(8);
			byte[] ani_buffer = new byte[8];
			aniBuf.getBytes(0, ani_buffer);
			pospMessage.setAni(HexCodec.hexEncode(ani_buffer));
			log.info("RECV_ANI:[" + HexCodec.hexEncode(ani_buffer) + "]");
			ByteBuf dnisBuf = msg.readBytes(8);
			byte[] dnis_buffer = new byte[8];
			dnisBuf.getBytes(0, dnis_buffer);
			pospMessage.setDnis(HexCodec.hexEncode(dnis_buffer));
			log.info("RECV_TDNIS:[" + HexCodec.hexEncode(dnis_buffer) + "]");
			ByteBuf lriEndBuf = msg.readBytes(12);
			byte[] lriEnd_buffer = new byte[12];
			lriEndBuf.getBytes(0, lriEnd_buffer);
			log.info("RECV_LRIEND:[" + HexCodec.hexEncode(lriEnd_buffer) + "]");
		}
	}
	/**
	 * 解析报文正文
	 * @param msg
	 */
	private void setBody(ByteBuf msg){
		ByteBuf headerBuf = msg.readBytes(6);
		byte[] header_buffer = new byte[6];
		headerBuf.getBytes(0, header_buffer);
		pospMessage.setHeader(header_buffer);
		log.info("RECV_HEADER:[" + HexCodec.hexEncode(header_buffer) + "]");
		byte[] buffer = new byte[msg.readableBytes()];
		msg.readBytes(buffer);
		pospMessage.setBody(buffer);
		if(log.isDebugEnabled()) {
			log.debug("RECV_BODY:[" + HexCodec.hexEncode(buffer) + "]");
		}
		log.info("RECV_BODY_LENGTH:[" + HexCodec.hexEncode(buffer).length() + "]");
		byte[] msg_type = new byte[2];
		msg_type[0] = buffer[0];
		msg_type[1] = buffer[1];
		pospMessage.setType(msg_type);
	}
	/**
	 * 设置报文格式
	 */
	private void setMessageDefine(){
		try{
			PKT_DEF[] pkt_def = ((PacketDefineInitializer)PospApplicationContext.getCtx()
					.getBean("PacketDefineInitializer")).findPacketDefine(HexCodec.hexEncode(pospMessage.getType()));
			iso.setLengthValueCompress(true);
			iso.setMessageDefine(pkt_def); 
		}catch(NullPointerException e){
			log.error(e.getMessage());
			log.error("FORMAT_EXCEPTION: ISO8583数据定义配置不能为null。");
		}catch(Exception e){
			log.error(e.getMessage());
			log.error("FORMAT_EXCEPTION: ISO8583数据定义异常。PKT_DEF数组长度必须为(64 + 1)或(128 + 1)。");
		}
	}
	/**
	 * 设置报文域
	 */
	private void setMessage(){
		try{
			iso.setPospMessage(pospMessage);
			iso.setMessage(pospMessage.getBody());
		}catch(Exception e){
			log.error(e.getMessage());
			log.error("FORMAT_EXCEPTION: 位图消息类型定义错误！");
		}
	}
}
