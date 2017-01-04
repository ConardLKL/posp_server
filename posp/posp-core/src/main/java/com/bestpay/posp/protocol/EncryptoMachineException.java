package com.bestpay.posp.protocol;

public class EncryptoMachineException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3147495645363752213L;

	/**
     * Creates a new exception.
     */
    public EncryptoMachineException() {
    }

    /**
     * Creates a new exception.
     */
    public EncryptoMachineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public EncryptoMachineException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public EncryptoMachineException(Throwable cause) {
        super(cause);
    }
}
