package com.tisson.bestpay.posp;

import cn.com.bestpay.posp.protocol.RespCodeInfo;
import cn.com.bestpay.posp.protocol.util.HexCodec;
import cn.com.bestpay.posp.system.entity.XmlMessage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    public static void main(String [] srgs){
        String code = null;
        System.out.println(code.length());
    }
}
