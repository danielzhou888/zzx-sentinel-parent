package com.zzx.sentinel.dcs.api;

import com.zzx.sentinel.dcs.exception.BusinessException;
import com.zzx.sentinel.dcs.po.CalcuteRule;

import java.math.BigDecimal;

public interface DcsApi {

	String getProductNameById(Long id) throws Exception;

	BigDecimal getProductPriceById(Long id) throws Exception;

	String getThrowNullPointerException() throws Exception;

	String getNameWhileExpireTime(Long id) throws Exception;

	String testThrowsBusinessException() throws BusinessException;

	String testTryThrowsBusinessException() throws BusinessException;

	String testToBThrowsException() throws Exception;

	String testToCThrowsException() throws Exception;

    CalcuteRule calcuteDeliveryRule(String orderCode, Long userId) throws Exception;
}
