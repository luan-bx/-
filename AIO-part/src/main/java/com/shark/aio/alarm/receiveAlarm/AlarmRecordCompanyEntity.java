package com.shark.aio.alarm.receiveAlarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author lbx
 * @date 2023/4/19 - 16:49
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRecordCompanyEntity {

    private Integer id;
    private Timestamp alarmTime;
    private String monitor;
    private String monitorClass;
    private String monitorValue;
    private String monitorData;
    private String message;
    private String company;
}
