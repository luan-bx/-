package com.shark.aio;

import com.shark.aio.util.ProcessUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan(basePackages = {"com.shark.aio.users.mapper", "com.shark.aio.project.mapper", "com.shark.aio.base.mapper"})
@EnableAsync
public class AioApplication {


    public static void main(String[] args){

        SpringApplication.run(AioApplication.class, args);

//        File file = new File("C:\\Users\\dell\\Desktop\\yao.jpg");
//        for (int i=0;i<10;i++){
//            Thread thread = new Thread(){
//                @Override
//                public void run() {
//                    while (true){
//                        FaceController.callFaceAI(file);
//                        LicenseController.callLicenseAI(file);
//                        try {
//                            sleep(500);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            };
//            thread.start();
//        }




    }

}
