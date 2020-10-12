package com.zzx.sentinel.orderweb.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.zzx.sentinel.distribute.response.ServiceResponse;
import com.zzx.sentinel.orderweb.utils.OrderUtils;
import com.zzx.sentinel.wdc.api.WdcApi;
import com.zzx.sentinel.wdc.exception.BusinessException;
import com.zzx.sentinel.wdc.po.Distribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private WdcApi wdcApi;

    /**
     * 模拟下订单调用配送系统限流
     * @return
     * @throws Exception
     */
    //@SentinelResource(value = "createOrderSentinel", fallback = "testAToBOrToCOrToDFallbackLevel1")
    @RequestMapping("/createOrder")
    @ResponseBody
    public String createOrder(@RequestParam("userId") Long userId) throws Exception {
        String result = "";
        try {
            String orderCode = OrderUtils.getOrderCode(userId);
            ServiceResponse<Distribute> response = wdcApi.distribute(orderCode, userId);
            if (response == null || response.isDownGrade()) {
                // 降级了
                // ...
                result = "wdc 降级了: " + JSONObject.toJSONString(response);
            } else {
                result = JSONObject.toJSONString(response);
            }
        } catch (Exception e) {
            log.error("createOrder error = {}", e);
            // ....
        }
        return result;
    }

    @SentinelResource(value = "aToBOrToCOrToDFallbackLevel1", fallback = "testAToBOrToCOrToDFallbackLevel2")
    @RequestMapping("/testAToBOrToCOrToDFallbackLevel1")
    @ResponseBody
    public String testAToBOrToCOrToDFallbackLevel1() throws Exception {
        String result = "";
        try {
            result = wdcApi.testToCThrowsException();
        } catch (Exception e) {
            log.error("testAToBOrToCOrToDFallbackLevel1 error {}", e);
            result = testAToBOrToCOrToDFallbackLevel2();
        }
        return result;
    }

    public String testAToBOrToCOrToDFallbackLevel2() {
        return "testAToBOrToCOrToDFallbackLevel2 我是兜底逻辑";
    }


}