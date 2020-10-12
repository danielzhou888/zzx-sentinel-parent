package com.zzx.sentinel.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zzx.sentinel.dms")
public class SentinelDmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelDmsApplication.class, args);
    }

}
