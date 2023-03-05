package com.shark.aio;

import com.shark.aio.video.MediaUtils;
import com.shark.aio.video.entity.VideoEntity;
import com.shark.aio.video.mapper.VideoMapping;
import org.mybatis.spring.annotation.MapperScan;
import org.opencv.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
@MapperScan(basePackages = {"com.shark.aio.users.mapper", "com.shark.aio.project.mapper", "com.shark.aio.base.mapper"})
public class AioApplication {


    public static void main(String[] args) {

        SpringApplication.run(AioApplication.class, args);

    }

}
