package com.shark.aio.alarm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlarmEntity {
    /*
    create table AIO.alarm_settings
    (
        id            int auto_increment comment '主键',
        monitor_class varchar(255) not null comment '监测类型',
        monitor_value varchar(255) not null comment '监测值',
        lower_limit   double       not null comment '下限阈值',
        upper_limit   double       not null comment '上限阈值',
        message       varchar(255) not null comment '告警信息',
        constraint id
            unique (id)
    )
        comment '预警设置';
    */
    private Integer id;
    private String monitorClass;
    private String monitorValue;
    private Double lowerLimit;
    private Double upperLimit;
    private String message;
}
