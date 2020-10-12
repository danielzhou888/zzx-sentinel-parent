package com.zzx.sentinel.dmsweb.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zzx.sentinel.readproduct.api.ProductApi;
import com.zzx.sentinel.readproduct.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/sentinel")
public class SentinelController {

    @DubboReference
    private ProductApi productApi;

    @RequestMapping("/sentinel")
    @ResponseBody
    public String sentinel(){
        return "sentinel ....";
    }

    @RequestMapping("/testRT10ms")
    @ResponseBody
    public String testRT10ms() throws InterruptedException {
        Thread.sleep(20);
        return "testRT10ms ....";
    }

    @RequestMapping("/test10ErrorPercent")
    @ResponseBody
    public String ErrorPercent() throws InterruptedException {
        if (0 == 0)
            throw new InterruptedException("异常");
        return "ErrorPercent ....";
    }

    @RequestMapping("/getProductNameById")
    @ResponseBody
    public String getProductNameById() throws Exception {
        return this.productApi.getProductNameById(1L);
    }

    @RequestMapping("/getProductPriceById")
    @ResponseBody
    public BigDecimal getProductPriceById() throws Exception {
        return this.productApi.getProductPriceById(1L);
    }

    @RequestMapping("/getThrowNullPointerException")
    @ResponseBody
    public String getThrowNullPointerException() throws Exception {
        return this.productApi.getThrowNullPointerException();
    }

    @RequestMapping("/getNameWhileExpireTime")
    @ResponseBody
    public String getNameWhileExpireTime() throws Exception {
        return this.productApi.getNameWhileExpireTime(1L);
    }

    /**
     * 调用Service接口超时，然后web接口做兜底，走web默认处理逻辑返回
     * @return
     * @throws Exception
     */
    @SentinelResource(value = "nameWhileExpireTimeWebDubble", fallback = "getNameWhileExpireTimeWebDubbleFallback")
    @RequestMapping("/getNameWhileExpireTimeWebDubble")
    @ResponseBody
    public String getNameWhileExpireTimeWebDubble() throws Exception {
        return this.productApi.getNameWhileExpireTime(1L);
    }

    public String getNameWhileExpireTimeWebDubbleFallback() {
        return "getNameWhileExpireTimeWebDubble调用Service接口超时，我是默认返回值";
    }

    /**
     * 测试异常时，第一个调用fallback返回，限流时，调用blockHander返回
     * @return
     * @throws Exception
     */
    @SentinelResource(value = "nameTestFallbackErrorAndBlockHander", blockHandler = "getBlockHanderA", fallback = "getFallbackA")
    //@SentinelResource(value = "nameTestFallbackErrorAndBlockHander", fallbackClass = SentinelController.class, fallback = "getFallbackA", blockHandlerClass = SentinelController.class, blockHandler = "getBlockHanderA")
    @RequestMapping("/getNameTestFallbackErrorAndBlockHander")
    @ResponseBody
    public String getNameTestFallbackErrorAndBlockHander() throws Exception {
        throw new RuntimeException("error");
        //return "fail";
    }

    public String getFallbackA() throws Exception {
        return "getNameTestFallbackErrorAndBlockHanderFallback调用Service接口超时，我是默认返回值";
    }

    public String getBlockHanderA(BlockException e) throws Exception{
        return "getNameTestFallbackErrorAndBlockHanderHander调用Service接口超时，我是默认返回值";
    }

    /**
     * 测试超时时，第一个调用fallback返回，限流时，调用blockHander返回
     * @return
     * @throws Exception
     */
    @SentinelResource(value = "nameTestFallbackErrorAndBlockHanderExpireTime", fallback = "getNameTestFallbackErrorAndBlockHanderExpireTimeFallback", blockHandler = "getNameTestFallbackErrorAndBlockHanderExpireTimeBlockHandler")
    @RequestMapping("/getNameTestFallbackErrorAndBlockHanderExpireTime")
    @ResponseBody
    public String getNameTestFallbackErrorAndBlockHanderExpireTime() throws Exception {
        return productApi.getNameWhileExpireTime(2L);
    }

    public String getNameTestFallbackErrorAndBlockHanderExpireTimeFallback() {
        return "我是fallback默认逻辑";
    }

    public String getNameTestFallbackErrorAndBlockHanderExpireTimeBlockHandler(BlockException e) {
        return "我是blockHandler默认逻辑";
    }

    @SentinelResource(value = "webRequestEnterBlockHandler", blockHandler = "testWebRequestEnterBlockHandlerBlockMethod")
    @RequestMapping("/testWebRequestEnterBlockHandler")
    @ResponseBody
    public String testWebRequestEnterBlockHandler() throws Exception {
        return "success";
    }

    public String testWebRequestEnterBlockHandlerBlockMethod(BlockException e) {
        return "testWebRequestEnterBlockHandlerBlockMethod 我是默认值";
    }

    /**
     * 测试A -> B, B不通，然后A -> C，如果C通则返回数据给前端，如果C不通，兜底：A -> D
     * @return
     * @throws Exception
     */
    @SentinelResource(value = "aToBOrToCOrToD", fallback = "testAToBOrToCOrToDFallbackLevel1")
    @RequestMapping("/testAToBOrToCOrToD")
    @ResponseBody
    public String testAToBOrToCOrToD() throws Exception {
        return productApi.testToBThrowsException();
    }

    @SentinelResource(value = "aToBOrToCOrToDFallbackLevel1", fallback = "testAToBOrToCOrToDFallbackLevel2")
    @RequestMapping("/testAToBOrToCOrToDFallbackLevel1")
    @ResponseBody
    public String testAToBOrToCOrToDFallbackLevel1() throws Exception {
        String result = "";
        try {
            result = productApi.testToCThrowsException();
        } catch (Exception e) {
            log.error("testAToBOrToCOrToDFallbackLevel1 error {}", e);
            result = testAToBOrToCOrToDFallbackLevel2();
        }
        return result;
    }

    public String testAToBOrToCOrToDFallbackLevel2() {
        return "testAToBOrToCOrToDFallbackLevel2 我是兜底逻辑";
    }

    @RequestMapping("/testThrowsBusinessException")
    @ResponseBody
    public String testThrowsBusinessException() throws Exception {
        try {
            this.productApi.testThrowsBusinessException();
        } catch (BusinessException e) {
            log.error("BusinessException runs -> {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception runs -> {}", e.getMessage());
        }
        return "";
    }

    @RequestMapping("/testTryThrowsBusinessException")
    @ResponseBody
    public String testTryThrowsBusinessException() throws Exception {
        try {
            this.productApi.testTryThrowsBusinessException();
        } catch (BusinessException e) {
            log.error("BusinessException runs -> {}", e.getMessage());
        } catch (Exception e) {
            log.error("Exception runs -> {}", e.getMessage());
        }
        return "";
    }


}