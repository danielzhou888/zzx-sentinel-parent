package com.zzx.sentinel.dms.exception;


/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 4620872602332366142L;

	public BusinessException(String message) {
		super(message);
	}

}
