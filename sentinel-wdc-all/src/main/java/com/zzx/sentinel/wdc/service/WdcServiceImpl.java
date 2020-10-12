package com.zzx.sentinel.wdc.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zzx.sentinel.dcs.api.DcsApi;
import com.zzx.sentinel.dcs.po.CalcuteRule;
import com.zzx.sentinel.distribute.response.ServiceResponse;
import com.zzx.sentinel.dms.api.DmsApi;
import com.zzx.sentinel.dms.po.DeliveryRoute;
import com.zzx.sentinel.wdc.api.WdcApi;
import com.zzx.sentinel.wdc.exception.BusinessException;
import com.zzx.sentinel.wdc.po.Distribute;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
//@DubboService(interfaceClass = WdcApi.class, dynamic = true)
@Service(interfaceClass = WdcApi.class, dynamic = true)
public class WdcServiceImpl implements WdcApi {

	private static final Logger logger = LoggerFactory.getLogger(WdcServiceImpl.class);

	@Reference
    private DcsApi dcsApi;

	@Reference
    private DmsApi dmsApi;

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

    @SentinelResource(value = "distributeSentinel", fallback = "distributeFallback")
    @Override
    public ServiceResponse<Distribute> distribute(String orderCode, Long userId) throws Exception, BusinessException {
        if (StringUtils.isEmpty(orderCode)) {
            throw new IllegalArgumentException("订单id = "+orderCode+" 非法");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户id = "+userId+" 非法");
        }
        DeliveryRoute deliveryRoute;
        try {
            deliveryRoute = this.dmsApi.calcuteDeliveryRoute(orderCode, userId);
            if (deliveryRoute == null || deliveryRoute.isDmsDownGrade()) {
                logger.info("Dms calcuteDeliveryRoute 降级了 执行本地localCalcuteDeliveryRoute逻辑");
                deliveryRoute = this.localCalcuteDeliveryRoute(orderCode, userId);
            }
        } catch (Exception e) {
            logger.error("distribute error = {}", e);
            logger.info("calcuteDeliveryRoute 异常了 执行本地localCalcuteDeliveryRoute逻辑");
            deliveryRoute = this.localCalcuteDeliveryRoute(orderCode, userId);
        }

        CalcuteRule calcuteRule;
        try {
            calcuteRule = this.dcsApi.calcuteDeliveryRule(orderCode, userId);
            if (calcuteRule == null || calcuteRule.isDcsDownGrade()) {
                // 降级了
                // 走本地默认计算规则逻辑
                logger.info("Dcs calcuteDeliveryRule 降级了 执行本地localCalcuteDeliveryRule逻辑");
                calcuteRule = this.localCalcuteDeliveryRule(orderCode, userId);
            }
        } catch (Exception e) {
            logger.error("distribute error = {}", e.getMessage());
            logger.info("Dcs calcuteDeliveryRule 异常了 执行本地localCalcuteDeliveryRule逻辑");
            calcuteRule = this.localCalcuteDeliveryRule(orderCode, userId);
        }

        Distribute distribute = Distribute.builder()
                .courierName("张三")
                .courierMobile("18949239399")
                .receiverMobile("18949239399")
                .receiverName("李四")
                .sendTime(new Date())
                .calcuteRule(calcuteRule)
                .deliveryRoute(deliveryRoute)
                .build();
        return new ServiceResponse<>().success(distribute);
    }

    private CalcuteRule localCalcuteDeliveryRule(String orderCode, Long userId) {
        logger.info("Wdc calcuteDeliveryRule 降级或异常了 执行 localCalcuteDeliveryRule 逻辑");
        return CalcuteRule.builder()
                .id(new Random(10000).nextLong())
                .orderCode(orderCode)
                .rules("localWdcRules")
                .price(new BigDecimal(3))
                .promotionPrice(new BigDecimal(3))
                .build();
    }

    private DeliveryRoute localCalcuteDeliveryRoute(String orderCode, Long userId) {
        logger.info("Wdc calcuteDeliveryRoute 降级或异常了 执行 localCalcuteDeliveryRule 逻辑");
        return DeliveryRoute.builder()
                .routeId(new Random(10000).nextLong())
                .orderCode(orderCode)
                .userId(userId)
                .type("localWdcType")
                .routes("localWdcRoutes")
                .build();
    }

    public ServiceResponse<Distribute> distributeFallback(String orderCode, Long userId) throws Exception, BusinessException {
        Distribute distribute = Distribute.builder()
                .courierMobile("wdc降级了")
                .courierName("wdc降级了")
                .calcuteRule(localCalcuteDeliveryRule(orderCode, userId))
                .deliveryRoute(localCalcuteDeliveryRoute(orderCode, userId))
                .sendTime(new Date())
                .receiverMobile("wdc降级了")
                .receiverName("wdc降级了")
                .id(new Random(1000).nextLong())
                .build();
        return new ServiceResponse<>().downGrade(distribute);
    }
}
