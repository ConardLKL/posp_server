package com.bestpay.posp.remoting.transport.client;

import com.bestpay.posp.constant.SysConstant;
import com.bestpay.posp.protocol.IsoMessage;
import com.bestpay.posp.protocol.util.Utils;
import com.bestpay.posp.service.platform.online.BaseService;
import com.bestpay.posp.service.platform.online.MapUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import com.bestpay.posp.spring.PospApplicationContext;
import com.bestpay.posp.system.entity.TranDatas;

import com.unionpay.acp.sdk.SDKUtil;
@Slf4j
public class FullChannelClientCall implements ClientCall{
	
	private IsoMessage in;
//	private String HOST = "192.168.83.9";
//	private String HOST = "192.168.91.11";
	private String HOST = "172.25.132.26";
	private int PORT = 9880;
	
	public FullChannelClientCall(IsoMessage in){
		this.in = in;
	}
	@Override
	public IsoMessage call(IsoMessage in, int timeout) throws Exception {
		BaseService baseService = (BaseService) PospApplicationContext.getBean(in.getTranCode());
		Map<String, String> resmap = baseService.service(in);
		TranDatas tranDatas = new TranDatas();
		tranDatas.setData(resmap);
		IsoMessage out = assemblyMessage(in,connect(tranDatas));
		return out;
	}
	/**
	 * 发送并做业务处理
	 * @param in
	 * @return
	 * 
	 * @throws Exception
	 */
	public TranDatas connect(TranDatas in) throws Exception{
		final FullChannelClienthandler handler = new FullChannelClienthandler();
		TranDatas out = null;
        //配置客户端线程组
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            //配置客户端启动辅助类
            Bootstrap b=new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //添加POJO对象解码器 禁止缓存类加载器
                 	socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024,ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                    //设置发送消息编码器
                 	socketChannel.pipeline().addLast(new ObjectEncoder());
                    //设置网络IO处理器
                 	socketChannel.pipeline().addLast(handler);
                }
            });
            //发起异步服务器连接请求 同步等待成功
            ChannelFuture f=b.connect(this.HOST,this.PORT).sync();
            f.channel().writeAndFlush(in);
            
            log.info(String.format("[%s] 全渠道报文发往银联成功" ,this.in.getSeq()));
            if(log.isDebugEnabled()){
            	log.debug(String.format("[%s] Request Message :"+in.getData().toString(), this.in.getSeq()));
            }
            out = handler.getReturnMessage(5000);
            //余额查询只有同步返回，并且超时不发起查询
            //表示成功的应答码包括00、A6(有缺陷成功)。
            //表示未知，后续需要再发查询应答码包括03、04、05。
            String successfulCode = "00.A6";
            String queryCode = "03.04.05";
            if(!StringUtils.equals(this.in.getTranCode(), "0200310001")){
            	//对于失败的前台/后台资金类交易，全渠道平台均不会发送后台通知；
	            if(out == null || queryCode.contains(out.getData().get("respCode"))){
	        		out = assemblyAndReturnQuery(f,handler);
	        	}else if(successfulCode.contains(out.getData().get("respCode"))){
	        		out = handler.getReturnMessage(3000);
	        		if(out == null){
	        			out = assemblyAndReturnQuery(f,handler);
	        		}
	        	}
            }
            f.channel().close();
	        //等到客户端链路关闭
	        f.channel().closeFuture().sync();
        }catch(Exception e){
        	log.info(e.getMessage());
        }finally{
            //优雅释放线程资源
            group.shutdownGracefully();
        }
        return out;
    }
	/**
	 * 组装返回报文
	 * @param out
	 * @param resmap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private IsoMessage assemblyMessage(IsoMessage out,TranDatas resmap)throws Exception{
		if(Utils.isNull(resmap)){
			return null;
		}
		if(log.isDebugEnabled()){
			log.debug(String.format("[%s] Response Message :"+resmap.getData().toString(),this.in.getSeq()));
		}
		String respCode = resmap.getData().get("respCode");
		//如果返回交易成功，则验证签名
		//签名验证不成功，则返回“11（验证签名失败）”
		if((StringUtils.equals(respCode, "00") 
				|| StringUtils.equals(respCode, "A6"))
				&& !SDKUtil.validate(resmap.getData(), "UTF-8")){
			respCode = "11";
    	}
		//应答报文中，“应答码”即respCode字段，表示的是查询交易本身的应答，即查询这个动作是否成功，不代表被查询交易的状态；
		//若查询动作成功，即应答码为“00“，则根据“原交易应答码”即origRespCode来判断被查询交易是否成功。此时若origRespCode为00，则表示被查询交易成功。
		if(StringUtils.equals(respCode, "00") 
				&& StringUtils.isNotEmpty(resmap.getData().get("origRespCode"))){
			respCode = resmap.getData().get("origRespCode");
		}
		out.setChannelCode(SysConstant.CAPITAL_POOL_9001);
		//消费交易的流水号， 供后续查询用
		out.getFlow().setCupsSerialNo(resmap.getData().get("queryId"));
		//余额查询时，组装余额域
		if(StringUtils.isNotEmpty(resmap.getData().get("balance"))){
			Map<String, String> balanceMap = (Map<String, String>) MapUtils.toMap(resmap.getData().get("balance"));
			String balance = balanceMap.get("accType")+balanceMap.get("balanceType")+balanceMap.get("currencyCode")+balanceMap.get("balanceSign")+String.format("%012d",Integer.valueOf(balanceMap.get("balance")));
			out.setField(54, String.format("%03d",balance.length())+balance);
		}
		out.setField(0, String.format("%04d",Integer.parseInt(out.getField(0)) + 10));
		out.setField(39, respCode);
		return out;
	}
	/**
	 * 组装并发送查询报文
	 * @param f
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	private TranDatas assemblyAndReturnQuery(ChannelFuture channelFuture,FullChannelClienthandler handler) throws Exception{
		TranDatas out = null;
		int count = 1;
		BaseService baseService = (BaseService) PospApplicationContext.getBean("0200");
		Map<String, String> resmap = baseService.service(this.in);
		TranDatas tranDatas = new TranDatas();
		tranDatas.setData(resmap);
		while(out == null && count <= 5){
			channelFuture.channel().writeAndFlush(tranDatas);
			if(log.isDebugEnabled()){
				log.debug("AssemblyQueryMessage ["+count+"] :" + tranDatas.getData().toString());
			}
			out = handler.getReturnMessage(3000);
			count ++;
		}
		return out;
	}
}
