/**
 * 
 */
package com.bestpay.posp.protocol;



/**
 * IMacCallback 。
 * 
 * @throws InvalidMacException
 *             if an error occurred
 */
public interface IMacCallback {	
	String calculate(IMessage msg, PKT_DEF[] pktdef, byte[] buffer) throws Exception;
}
