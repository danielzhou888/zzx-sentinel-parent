package com.zzx.sentinel.wdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zzx.sentinel.wdc")
public class SentinelWdcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelWdcApplication.class, args);
    }

}
