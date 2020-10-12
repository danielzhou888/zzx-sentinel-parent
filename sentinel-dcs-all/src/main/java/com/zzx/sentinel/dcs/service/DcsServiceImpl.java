package com.zzx.sentinel.dcs.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zzx.sentinel.dcs.api.DcsApi;
import com.zzx.sentinel.dcs.exception.BusinessException;
import com.zzx.sentinel.dcs.po.CalcuteRule;
import com.zzx.sentinel.dfc.po.BaseRule;
import com.zzx.sentinel.dfc.api.DfcApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
//@DubboService(interfaceClass = DcsApi.class, dynamic = true)
@Service(interfaceClass = DcsApi.class, dynamic = true)
public class DcsServiceImpl implements DcsApi {

	private static final Logger logger = LoggerFactory.getLogger(DcsServiceImpl.class);

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

    @SentinelResource(value = "calcuteDeliveryRuleSentinel", fallback = "calcuteDeliveryRuleFallback")
    @Override
    public CalcuteRule calcuteDeliveryRule(String orderCode, Long userId) throws Exception {
        if (StringUtils.isEmpty(orderCode)) {
            throw new IllegalArgumentException("订单id = "+orderCode+" 非法");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户id = "+userId+" 非法");
        }
        CalcuteRule calcuteRule;
        try {
            BaseRule baseRule = this.dfcApi.calcuteBaseRule(orderCode, userId);

            if (baseRule == null || baseRule.isDfcDownGrade()) {
                logger.info("DFC calcuteBaseRule 降级了，采用默认规则计算");
                // 采用默认规则
                calcuteRule = calcuteDeliveryRuleFallback(orderCode, userId);
            } else {
                // 正常计算
                calcuteRule = CalcuteRule.builder()
                        .id(baseRule.getId())
                        .orderCode(orderCode)
                        .price(baseRule.getPrice())
                        .promotionPrice(baseRule.getPrice().subtract(new BigDecimal(1)))
                        .rules(baseRule.getRules())
                        .build();
            }
        } catch (Exception e) {
            logger.error("异常了，采用默认计算规则 e = {}", e);
            calcuteRule = calcuteDeliveryRuleFallback(orderCode, userId);
        }
        return calcuteRule;
    }

    public CalcuteRule calcuteDeliveryRuleFallback(String orderCode, Long userId) throws Exception {
        // 走默认计算逻辑
        logger.info("Dcs calcuteDeliveryRule 降级或异常了 执行calcuteDeliveryRuleFallback逻辑");
        return CalcuteRule.builder()
                .id(new Random(10000).nextLong())
                .orderCode(orderCode)
                .price(new BigDecimal(5))
                .promotionPrice(new BigDecimal(5))
                .rules("defaultDcsRule")
                .build();
    }
}
