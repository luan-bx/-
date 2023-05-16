package com.shark.aio.alarm.GradedAlarm.neww;

import com.shark.aio.alarm.contactPart.ContractPartController;
import com.shark.aio.alarm.entity.AlarmRecordCompanyMediumEntity;
import com.shark.aio.base.information.InformationEntity;
import com.shark.aio.base.information.InformationMapping;
import com.shark.aio.data.monitorDeviceHj212.MonitorDeviceEntity;
import com.shark.aio.data.monitorDeviceHj212.MonitorDeviceMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Controller;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {




    JedisPoolConfig config = new JedisPoolConfig();
    JedisPool pool = new JedisPool(config, "127.0.0.1", 6379, 2000, "password");



    @Autowired
    MonitorDeviceMapping monitorDeviceMapping;
    @Autowired
    ContractPartController contractPartController;
    @Autowired
    InformationMapping informationMapping;
    Jedis jedis = new Jedis("127.0.0.1", 6379);
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    protected void doHandleMessage(Message message) {
        // 这个就是过期的key ，过期后，也就是事件触发后对应的value是拿不到的。
        // 这里实现业务逻辑，如果是服务器集群的话需要使用分布式锁进行抢占执行。
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp alarmTime = Timestamp.valueOf(dataFormat.format(new Date()));
        String key = message.toString();
        System.out.println("key = " + key);
        System.out.println("end = " + alarmTime);
        InformationEntity informationEntity = informationMapping.getInformation();

        if(key.contains("invariable_")){
            //存本地高级预警
            monitorDeviceMapping.insertAlarmRecordCompanyHigh(alarmTime, informationEntity.getCompany(), key.substring(11), "超过8小时参数未变化 !" );
            //告诉园区8h恒定值
            contractPartController.alarmToPart2("invariable8h", key.substring(11), null, null, informationEntity.getCompany(), alarmTime);
            jedis.setex("invariable_" + key, 8*60*60, "1");
        }else{
            MonitorDeviceEntity monitorDeviceEntity = monitorDeviceMapping.getMonitorDeviceEntityByDeviceId(key);
            //存本地中级预警
            AlarmRecordCompanyMediumEntity alarmRecordCompanyMediumEntity = new AlarmRecordCompanyMediumEntity();
            alarmRecordCompanyMediumEntity.setAlarmTime(alarmTime);
            alarmRecordCompanyMediumEntity.setCompany(informationEntity.getCompany());
            alarmRecordCompanyMediumEntity.setMessage("超过3小时未收到数据 ！");
            alarmRecordCompanyMediumEntity.setDeviceId(key);
            alarmRecordCompanyMediumEntity.setMonitorName(monitorDeviceEntity.getMonitorName());
            alarmRecordCompanyMediumEntity.setMonitorClass(monitorDeviceEntity.getMonitorClass());
            monitorDeviceMapping.insertAlarmRecordCompanyMedium(alarmRecordCompanyMediumEntity);
            //告诉园区3h未收到数据
            contractPartController.alarmToPart2("lose3h", key, monitorDeviceEntity, null, informationEntity.getCompany(), alarmTime);
            //3小时过期
            jedis.setex(key, 3*60*60, "true");
        }

    }
}
