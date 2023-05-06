package com.shark.aio.alarm.receiveAlarm;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lbx
 * @date 2023/4/19 - 14:47
 **/
@Mapper
public interface ReceiveAlarmMapping {
    @Insert("INSERT INTO `alarm_records_company`(`alarm_time`, `monitor`, `monitor_class`, `monitor_value`, `monitor_data` , `message`, `company`) " +
            " VALUES( #{alarmTime}, #{monitor}, #{monitorClass}, #{monitorValue}, #{monitorData}, #{message}, #{company});")
    void insertAlarmRecordCompany(AlarmRecordCompanyEntity alarmRecordCompanyEntity);

}
