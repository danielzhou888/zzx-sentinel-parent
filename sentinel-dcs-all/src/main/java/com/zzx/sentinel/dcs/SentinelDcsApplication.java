package com.zzx.sentinel.dcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zzx.sentinel.dcs")
public class SentinelDcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelDcsApplication.class, args);
    }

}
