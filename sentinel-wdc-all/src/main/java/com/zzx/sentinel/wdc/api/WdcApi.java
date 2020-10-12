package com.zzx.sentinel.wdc.api;

import com.zzx.sentinel.distribute.response.ServiceResponse;
import com.zzx.sentinel.wdc.exception.BusinessException;
import com.zzx.sentinel.wdc.po.Distribute;

import java.math.BigDecimal;

public interface WdcApi {

	String getProductNameById(Long id) throws Exception;

	BigDecimal getProductPriceById(Long id) throws Exception;

	String getThrowNullPointerException() throws Exception;

	String getNameWhileExpireTime(Long id) throws Exception;

	String testThrowsBusinessException() throws BusinessException;

	String testTryThrowsBusinessException() throws BusinessException;

	String testToBThrowsException() throws Exception;

	String testToCThrowsException() throws Exception;

    ServiceResponse<Distribute> distribute(String orderCode, Long userId) throws Exception, BusinessException;
}
