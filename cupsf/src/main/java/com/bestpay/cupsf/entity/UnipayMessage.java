package com.bestpay.cupsf.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by HR on 2016/5/17.
 */
@Setter
@Getter
public class UnipayMessage {
    private String header;
    private String body;
}
