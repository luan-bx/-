package com.shark.aio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.lbx.aio.users.mapper", "com.lbx.aio.project.mapper", "com.lbx.aio.base.mapper"})
public class AioApplication {

    public static void main(String[] args) {
        SpringApplication.run(AioApplication.class, args);
    }

}
