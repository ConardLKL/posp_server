package com.bestpay.cupsf.entity;


import com.bestpay.cupsf.protocol.PacketDefineFormat;
import com.bestpay.cupsf.protocol.PacketDefineSpec;
import com.bestpay.cupsf.protocol.PacketDefineType;
import lombok.Getter;
import lombok.Setter;
/**
 * Created by HR on 2016/5/17.
 */
@Setter
@Getter
public class PKT_DEF {
	private PacketDefineType type;
	private PacketDefineFormat format;
	private PacketDefineSpec spec = PacketDefineSpec.NULL_VALUE;
	public int length = -1;
	private String memo;
}
