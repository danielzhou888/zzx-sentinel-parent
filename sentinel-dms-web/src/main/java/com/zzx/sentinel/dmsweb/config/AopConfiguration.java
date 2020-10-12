package com.zzx.sentinel.dmsweb.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Eric Zhao
 */
@Configuration
@DubboComponentScan
public class AopConfiguration {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}