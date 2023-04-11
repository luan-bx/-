package com.shark.aio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan(basePackages = {"com.shark.aio.users.mapper", "com.shark.aio.project.mapper", "com.shark.aio.base.mapper"})
@EnableAsync
public class AIOPartApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIOPartApplication.class,args);
    }
}