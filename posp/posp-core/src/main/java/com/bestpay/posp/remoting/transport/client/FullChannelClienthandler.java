package com.bestpay.posp.remoting.transport.client;

import com.bestpay.posp.system.entity.TranDatas;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import com.regaltec.nma.collector.common.socket.NmaEvent;


@Slf4j
public class FullChannelClienthandler extends ChannelInboundHandlerAdapter{

	private TranDatas in = null;
	private NmaEvent evt = new NmaEvent();
	private boolean alreadyResponsed = false;

	public TranDatas getReturnMessage(int timeout) {
		if (!alreadyResponsed) {
			evt.waitEvent(timeout);
		}
		TranDatas out = null;
		out = in;
		setDefault();
		return out;
	}

	public void setDefault(){
		this.in = null;
		this.alreadyResponsed = false;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if(msg instanceof TranDatas){
			in = (TranDatas) msg;
			if(log.isDebugEnabled()){
				log.debug(String.format("Received Message From Unipay >>>>>>"+in.getClient()+"<<<<<<:"+in.getData().toString()));
			}
			alreadyResponsed = true;
			evt.setEvent();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error(String.format("[%s] ", cause.getMessage()));
		ctx.close();
		alreadyResponsed = true;
		evt.setEvent();
	}
}
