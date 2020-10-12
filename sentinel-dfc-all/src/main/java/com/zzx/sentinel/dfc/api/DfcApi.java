package com.zzx.sentinel.dfc.api;

import com.zzx.sentinel.dfc.po.BaseRoute;
import com.zzx.sentinel.dfc.po.BaseRule;
import com.zzx.sentinel.dfc.exception.BusinessException;

import java.math.BigDecimal;

public interface DfcApi {

	String getProductNameById(Long id) throws Exception;

	BigDecimal getProductPriceById(Long id) throws Exception;

	String getThrowNullPointerException() throws Exception;

	String getNameWhileExpireTime(Long id) throws Exception;

	String testThrowsBusinessException() throws BusinessException;

	String testTryThrowsBusinessException() throws BusinessException;

	String testToBThrowsException() throws Exception;

	String testToCThrowsException() throws Exception;

	BaseRule calcuteBaseRule(String orderCode, Long userId) throws Exception;

	BaseRoute calcuteBaseRoute(String orderCode, Long userId) throws Exception;
}
