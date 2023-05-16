package com.shark.aio.alarm.receiveAlarm;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;

/**
 * @author lbx
 * @date 2023/4/19 - 14:47
 **/
@Mapper
public interface ReceiveAlarmMapping {
    @Insert("INSERT INTO `alarm_records_company`(`alarm_time`, `monitor`, `monitor_class`, `monitor_value`, `monitor_data` , `message`, `company`) " +
            " VALUES( #{alarmTime}, #{monitor}, #{monitorClass}, #{monitorValue}, #{monitorData}, #{message}, #{company});")
    void insertAlarmRecordCompany(AlarmRecordCompanyEntity alarmRecordCompanyEntity);

    @Insert("INSERT INTO `alarm_records_company_medium`(`alarm_time`, `company`, `monitor_name`, `monitor_class`, `device_id`, `message`) " +
            " VALUES( #{alarmTime}, #{company}, #{monitorName}, #{monitorClass}, #{deviceId}, #{message});")
    void insertAlarmRecordCompanyMedium(AlarmRecordCompanyMediumEntity alarmRecordCompanyMediumEntity);

    @Insert("INSERT INTO `alarm_records_company_high`(`alarm_time`, `company`, `monitor_value`,  `message`) " +
            " VALUES( #{alarmTime}, #{company}, #{key}, #{message});")
    void insertAlarmRecordCompanyHigh(Timestamp alarmTime, String company, String key, String message);
}
