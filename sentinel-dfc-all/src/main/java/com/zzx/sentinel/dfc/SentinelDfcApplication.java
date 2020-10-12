package com.zzx.sentinel.dfc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zzx.sentinel.dfc")
public class SentinelDfcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelDfcApplication.class, args);
    }

}
