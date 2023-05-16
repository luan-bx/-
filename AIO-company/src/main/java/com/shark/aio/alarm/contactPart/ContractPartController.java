package com.shark.aio.alarm.contactPart;

import com.shark.aio.alarm.entity.AlarmRecordEntity;
import com.shark.aio.data.monitorDeviceHj212.MonitorDeviceEntity;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;

/**
 * @author lbx
 * @date 2023/4/17 - 18:17
 **/
@MapperScan(value = "com.shark.aio.alarm.contactPart")
@Controller
@Slf4j
public class ContractPartController {
    @Autowired
    ContractPartMapping contractPartMapping;

    public void alarmToPart(AlarmRecordEntity alarmRecordEntity, String company)  {
        try {
            PartHostEntity partHostEntity = contractPartMapping.getPartHost();
            Socket socket = new Socket(partHostEntity.getIp(), Integer.parseInt(partHostEntity.getPort()));
            //2.获取该Socket的输出流，用来向服务器发送信息
            OutputStream os = socket.getOutputStream();


            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(alarmRecordEntity);
            oos.writeObject(company);
            socket.shutdownOutput();
            String infoString = null;
            //3.获取输入流，取得服务器的信息
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("服务器端的信息：" + info);
            }
            socket.shutdownInput();
            oos.close();
            os.close();
            is.close();
            br.close();
            socket.close();
        } catch (UnknownHostException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void alarmToPart2(String object, String key, MonitorDeviceEntity monitorDeviceEntity, AlarmRecordEntity alarmRecordEntity, String company, Timestamp alarmTime)  {
        try {
            PartHostEntity partHostEntity = contractPartMapping.getPartHost();
            Socket socket = new Socket(partHostEntity.getIp(), Integer.parseInt(partHostEntity.getPort()));
            //2.获取该Socket的输出流，用来向服务器发送信息
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(object);
            switch(object){
                case "lose3h":
                    oos.writeObject(key);
                    oos.writeObject(monitorDeviceEntity.getMonitorName());
                    oos.writeObject(monitorDeviceEntity.getMonitorClass());
                case "more3":
                    oos.writeObject(alarmRecordEntity);
                case "invariable8h":
                    oos.writeObject(key);
            }
            oos.writeObject(company);
            oos.writeObject(alarmTime);
            socket.shutdownOutput();
            String infoString = null;
            //3.获取输入流，取得服务器的信息
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("服务器端的信息：" + info);
            }
            socket.shutdownInput();
            oos.close();
            os.close();
            is.close();
            br.close();
            socket.close();
        } catch (UnknownHostException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
