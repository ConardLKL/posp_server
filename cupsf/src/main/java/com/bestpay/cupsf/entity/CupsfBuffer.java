package com.bestpay.cupsf.entity;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HR on 2016/5/24.
 */
public class CupsfBuffer {
    public static PKT_DEF[] pkt_def;
    public static SocketChannel channel;
    public static Map<String,MessageBuffer> posp = new ConcurrentHashMap<String,MessageBuffer>();
    public static Map<String,MessageBuffer> cup = new ConcurrentHashMap<String,MessageBuffer>();
}
