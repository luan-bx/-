package com.shark.aio.alarm.contactPart;

import com.shark.aio.alarm.entity.AlarmRecordEntity;
import com.shark.aio.base.information.InformationEntity;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

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



}
