package com.shark.aio.alarm.GradedAlarm;

import com.shark.aio.alarm.mapper.AlarmMapping;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Controller;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author lbx
 * @date 2023/5/8 - 14:05
 **/

/**
 * 如果接收到信息，如果id存在redis，就重新设置过期时间
 *                如果id不存在redis，报警，新建字段和过期时间
 * 3h还没接到，改id的字段失效
 */
@Controller
@Slf4j
@Order(3)
public class GradedAlarmController implements ApplicationRunner {

    @Autowired
    protected AlarmMapping alarmMapping;

    Jedis jedis = new Jedis("127.0.0.1", 6379);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //redis存对象，需要序列化
//        AlarmRecordEntity alarmRecordEntity = new AlarmRecordEntity();
//        jedis.set("user".getBytes(), SerializeUtil.serialize(alarmRecordEntity));
//        System.out.println("--------------------------------------------------------------------");
//        AlarmRecordEntity userResult = (AlarmRecordEntity) SerializeUtil.deserialize(jedis.get("user".getBytes()));
//        System.out.println(userResult.toString());


        List<String> allDevice = getDeviceId();
        for(String id :allDevice){
            jedis.setex(id, 3*60*60, "true");
//            jedis.setex(id, 10, "true");
            System.out.println(id + "已加入了redis");
            //防止一次性加入太多炸了
            Thread.currentThread().sleep(500);
        }

        for(int i = 0; i < 100; i++){
            jedis.setex(String.valueOf(i), 3*60*60, "true");
//            jedis.setex(id, 10, "true");
            System.out.println(i + "已加入了redis");
            //防止一次性加入太多炸了
        }

    }


    public List<String> getDeviceId() {
        try {
            List<String> allDeviceId = alarmMapping.getAllDeviceId();
            return allDeviceId;
        } catch (Exception e) {
            // TODO: handle exception
            log.error("GradedAlarmController/getDeviceId, 获取全部设备id失败, ", e);
            return null;
        }
    }


}