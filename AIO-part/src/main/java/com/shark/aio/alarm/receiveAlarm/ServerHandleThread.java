package com.shark.aio.alarm.receiveAlarm;

import com.shark.aio.alarm.entity.AlarmRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;

/**
 * @author lbx
 * @date 2023/4/19 - 18:12
 **/
@Slf4j
@MapperScan("com.shark.aio.alarm.receiveAlarm")

public class ServerHandleThread implements Runnable{


    Socket socket = null;
    ReceiveAlarmMapping receiveAlarmMapping = null;
    public ServerHandleThread(Socket socket, ReceiveAlarmMapping receiveAlarmMapping) {
        super();
        this.socket = socket;
        this.receiveAlarmMapping = receiveAlarmMapping;
    }
    @Override
    public void run() {//TODO Auto-generated method stub
        OutputStream os = null;
        PrintWriter pw = null;
        String company = null;
        String key = null;
        Timestamp alarmTime = null;
        try {
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            //readObject()方法必须保证服务端和客户端的User包名一致，要不然会出现找不到类的错误

            switch ((String)ois.readObject()){
                case "lose3h":
                    key = (String) ois.readObject();
                    String monitorName = (String) ois.readObject();
                    String monitorClass = (String) ois.readObject();
                    company = (String) ois.readObject();
                    alarmTime = (Timestamp) ois.readObject();
                    //插入数据库
                    AlarmRecordCompanyMediumEntity alarmRecordCompanyMediumEntity = new AlarmRecordCompanyMediumEntity();
                    alarmRecordCompanyMediumEntity.setAlarmTime(alarmTime);
                    alarmRecordCompanyMediumEntity.setCompany(company);
                    alarmRecordCompanyMediumEntity.setMessage("超过3小时未收到数据 !");
                    alarmRecordCompanyMediumEntity.setDeviceId(key);
                    alarmRecordCompanyMediumEntity.setMonitorName(monitorName);
                    alarmRecordCompanyMediumEntity.setMonitorClass(monitorClass);
                    receiveAlarmMapping.insertAlarmRecordCompanyMedium(alarmRecordCompanyMediumEntity);
                case "more3":
                    AlarmRecordEntity alarmRecordEntity= (AlarmRecordEntity)ois.readObject();
                    company = (String) ois.readObject();
                    alarmTime = (Timestamp) ois.readObject();
                    //插入数据库
                    AlarmRecordCompanyEntity alarmRecordCompanyEntity = new AlarmRecordCompanyEntity();
                    alarmRecordCompanyEntity.setAlarmTime(alarmRecordEntity.getAlarmTime());
                    alarmRecordCompanyEntity.setCompany(company);
                    alarmRecordCompanyEntity.setMessage(alarmRecordEntity.getMessage());
                    alarmRecordCompanyEntity.setMonitor(alarmRecordEntity.getMonitor());
                    alarmRecordCompanyEntity.setMonitorValue(alarmRecordEntity.getMonitorValue());
                    alarmRecordCompanyEntity.setMonitorData(alarmRecordEntity.getMonitorData());
                    alarmRecordCompanyEntity.setMonitorClass(alarmRecordEntity.getMonitorClass());
                    receiveAlarmMapping.insertAlarmRecordCompany(alarmRecordCompanyEntity);
                case "invariable8h":
                    key = (String) ois.readObject();
                    company = (String) ois.readObject();
                    alarmTime = (Timestamp) ois.readObject();
                    receiveAlarmMapping.insertAlarmRecordCompanyHigh(alarmTime, company,  key, "超过8小时参数未变化 !" );
            }


            socket.shutdownInput();
            //禁用套接字的输入流
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            pw.println("欢迎登录！");
            pw.flush();
            socket.shutdownOutput();



        } catch (IOException | ClassNotFoundException e) {//TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (os != null) {
                    os.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {//TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
