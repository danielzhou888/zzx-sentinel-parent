package com.zzx.sentinel.dms.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zzx.sentinel.dfc.api.DfcApi;
import com.zzx.sentinel.dfc.po.BaseRoute;
import com.zzx.sentinel.dms.api.DmsApi;
import com.zzx.sentinel.dms.exception.BusinessException;
import com.zzx.sentinel.dms.po.DeliveryRoute;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
//@DubboService(interfaceClass = DmsApi.class, dynamic = true)
@Service(interfaceClass = DmsApi.class, dynamic = true)
public class DmsServiceImpl implements DmsApi {

	private static final Logger logger = LoggerFactory.getLogger(DmsServiceImpl.class);

	@Reference
    private DfcApi dfcApi;

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

    @SentinelResource(value = "calcuteDeliveryRouteSentinel", fallback = "calcuteDeliveryRouteFallback")
    @Override
    public DeliveryRoute calcuteDeliveryRoute(String orderCode, Long userId) throws Exception {
        DeliveryRoute deliveryRoute;
        try {
            BaseRoute baseRoute = this.dfcApi.calcuteBaseRoute(orderCode, userId);
            if (baseRoute == null || baseRoute.isDfcDownGrade()) {
                // 降级了
                // 走默认计算路线逻辑
                logger.info("Dfc calcuteBaseRoute 降级了 执行本地 calcuteDeliveryRouteFallback 逻辑");
                deliveryRoute = calcuteDeliveryRouteFallback(orderCode, userId);
            } else {
                deliveryRoute = DeliveryRoute.builder()
                        .routeId(baseRoute.getRouteId())
                        .orderCode(orderCode)
                        .userId(userId)
                        .routes(baseRoute.getRoutes())
                        .type(baseRoute.getType())
                        .build();
            }
        } catch (Exception e) {
            logger.error("calcuteDeliveryRoute error = {}", e.getMessage());
            logger.info("Dfc calcuteBaseRoute 异常了 执行本地 calcuteDeliveryRouteFallback 逻辑");
            deliveryRoute = calcuteDeliveryRouteFallback(orderCode, userId);
        }
        return deliveryRoute;
    }

    public DeliveryRoute calcuteDeliveryRouteFallback(String orderCode, Long userId) throws Exception {
        logger.info("calcuteDeliveryRouteFallback 降级了，走逻辑计算路线逻辑");
        return DeliveryRoute.builder()
                .orderCode(orderCode)
                .userId(userId)
                .routeId(new Random(10000).nextLong())
                .routes("defaultDmsDeliveryRoutes")
                .type("defaultDmsDeliveryType")
                .build();
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
