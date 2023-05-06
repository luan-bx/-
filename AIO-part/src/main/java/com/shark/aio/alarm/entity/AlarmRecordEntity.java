package com.shark.aio.alarm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlarmRecordEntity implements Serializable {

    private Integer id;
    private Timestamp alarmTime;
    private String monitor;
    private String monitorClass;
    private String monitorValue;
    private String monitorData;
    private String message;


}
