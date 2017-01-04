package com.bestpay.posp.protocol;

/*
 * @author   PengGuoYi  
 * @version  1.0  2014/06/13 
 */
public class InvalidMacException extends RuntimeException {

    private static final long serialVersionUID = 2908318315971079034L;

    /**
     * Creates a new exception.
     */
    public InvalidMacException() {
    }

    /**
     * Creates a new exception.
     */
    public InvalidMacException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public InvalidMacException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public InvalidMacException(Throwable cause) {
        super(cause);
    }
}