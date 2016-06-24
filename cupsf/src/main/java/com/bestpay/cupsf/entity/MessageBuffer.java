package com.bestpay.cupsf.entity;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by HR on 2016/5/24.
 */
@Setter
@Getter
public class MessageBuffer {
    private int id;
    private ChannelHandlerContext ctx;
    private Date date;
    private String serialNo;
    private byte[] message;
}
