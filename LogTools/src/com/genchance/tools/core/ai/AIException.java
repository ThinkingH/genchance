package com.genchance.tools.core.ai;

/**
 * AI相关的异常类
 * 
 *
 */
public class AIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5073326220450827682L;

	public AIException() {
	}

	public AIException(String msg) {
		super(msg);
	}

	public AIException(String message, Throwable cause) {
		super(message, cause);
	}

	public AIException(Throwable cause) {
		super(cause);
	}

}
