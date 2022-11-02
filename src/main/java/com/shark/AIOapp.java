package com.shark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = { "com.shark.user.mapper" })

public class AIOapp {

	public static void main(String[] args) {
		SpringApplication.run(AIOapp.class, args);
	}

}
