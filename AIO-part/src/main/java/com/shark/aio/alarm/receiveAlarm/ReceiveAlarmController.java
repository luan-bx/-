package com.shark.aio.alarm.receiveAlarm;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lbx
 * @date 2023/4/19 - 14:43
 **/
@Controller
@Slf4j
@Order(2)
@MapperScan("com.shark.aio.alarm.receiveAlarm")
public class ReceiveAlarmController  implements ApplicationRunner {

    @Autowired
    protected ReceiveAlarmMapping receiveAlarmMapping;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        receiveAlarm();
    }
    public void receiveAlarm() {
        try {
            ServerSocket serverSocket = new ServerSocket(9997);
            int count = 0;//记录客户端的数量
            System.out.println("服务器启动，等待客户端的连接。。。");
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                ++count;
                Thread serverHandleThread = new Thread(new ServerHandleThread(socket, receiveAlarmMapping));
                serverHandleThread.setPriority(4);
                serverHandleThread.start();
//                System.out.println("上线的客户端有" + count + "个！");
//                InetAddress inetAddress = socket.getInetAddress();
//                System.out.println("当前客户端的IP地址是：" + inetAddress.getHostAddress());
            }
        } catch (IOException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}



