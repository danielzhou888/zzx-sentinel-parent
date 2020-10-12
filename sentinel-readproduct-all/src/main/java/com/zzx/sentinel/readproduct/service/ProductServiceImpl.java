package com.zzx.sentinel.readproduct.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zzx.sentinel.readproduct.api.ProductApi;
import com.zzx.sentinel.readproduct.exception.BusinessException;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = ProductApi.class, dynamic = true)
public class ProductServiceImpl implements ProductApi {

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    @Override
    public String getProductNameById(Long id) throws Exception {
        return "我是商品1";
    }

    @Override
    public BigDecimal getProductPriceById(Long id) throws Exception {
        return new BigDecimal(99.9);
    }

    @SentinelResource(value = "throwNullPotinterException", blockHandler = "getThrowNullPointerExceptionBlockHandler")
    @Override
    public String getThrowNullPointerException() throws Exception {
        Integer a = null;
        double v = a.doubleValue();
        return "success";
    }

    @SentinelResource(value = "nameWhileExpireTime", blockHandler = "getNameWhileExpireTimeBlockHandler")
    @Override
    public String getNameWhileExpireTime(Long id) throws Exception {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "sucess";
    }

    @Override
    public String testThrowsBusinessException() throws BusinessException {
        throw new BusinessException("testThrowsBusinessException 抛了个业务异常");
    }

    @Override
    public String testTryThrowsBusinessException() throws BusinessException {
        try {
            throw new BusinessException("testTryThrowsBusinessException 我是一个业务异常");
        } catch (BusinessException e) {
            logger.error("BusinessException runs");
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception runs");
        }
        return null;
    }

    @Override
    public String testToBThrowsException() throws Exception {
        throw new RuntimeException("我是testToBThrowsException异常");
    }

    @SentinelResource(value = "toCThrowsExceptionApi", fallback = "testToCThrowsExceptionApi")
    @Override
    public String testToCThrowsException() throws Exception {
        throw new RuntimeException("我是testToCThrowsException异常");
    }

    public String testToCThrowsExceptionApi() {
        return "我是testToCThrowsExceptionApi 快速响应";
    }

    public String getThrowNullPointerExceptionBlockHandler(Long id, BlockException e) {
        logger.warn("getThrowNullPointerExceptionBlockHandler run...");
        return "我是ReadPrduct服务异常之后触发执行的getThrowNullPointerExceptionBlockHandler";
    }

    public String getNameWhileExpireTimeBlockHandler(Long id, BlockException e) {
        logger.warn("getNameWhileExpireTimeBlockHandler run...");
        return "我是ReadPrduct服务异常之后触发执行的getNameWhileExpireTimeBlockHandler";
    }
}
