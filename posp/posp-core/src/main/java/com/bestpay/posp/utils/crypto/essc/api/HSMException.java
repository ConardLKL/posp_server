package com.bestpay.posp.utils.crypto.essc.api;

public class HSMException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errCode = null;
	private Throwable rootCause = null;

	HSMException() {
		super();
	}

	HSMException(String errCode) {
		super(errCode);
		this.errCode = errCode;
	}

	public String getErrorCode() {
		return errCode;
	}

	public Throwable getRootCause() {
		return rootCause;
	}

}
