package com.zzx.sentinel.dms.api;

import com.zzx.sentinel.dms.exception.BusinessException;
import com.zzx.sentinel.dms.po.DeliveryRoute;

import java.math.BigDecimal;

public interface DmsApi {

	String getProductNameById(Long id) throws Exception;

	BigDecimal getProductPriceById(Long id) throws Exception;

	String getThrowNullPointerException() throws Exception;

	String getNameWhileExpireTime(Long id) throws Exception;

	String testThrowsBusinessException() throws BusinessException;

	String testTryThrowsBusinessException() throws BusinessException;

	String testToBThrowsException() throws Exception;

	String testToCThrowsException() throws Exception;


	DeliveryRoute calcuteDeliveryRoute(String orderCode, Long userId) throws Exception;
}
