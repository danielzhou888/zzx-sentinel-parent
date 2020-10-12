package com.zzx.sentinel.readproduct.api;

import com.zzx.sentinel.readproduct.exception.BusinessException;

import java.math.BigDecimal;

public interface ProductApi {

	String getProductNameById(Long id) throws Exception;

	BigDecimal getProductPriceById(Long id) throws Exception;

	String getThrowNullPointerException() throws Exception;

	String getNameWhileExpireTime(Long id) throws Exception;

	String testThrowsBusinessException() throws BusinessException;

	String testTryThrowsBusinessException() throws BusinessException;

	String testToBThrowsException() throws Exception;

	String testToCThrowsException() throws Exception;
}
