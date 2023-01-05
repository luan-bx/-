package com.shark.aio.alarm.mapper;

import com.shark.aio.alarm.entity.AlarmEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmMapping {

    @Select("select * from alarm_settings")
    public List<AlarmEntity> getAllAlarmSettings();

    @Select("select * from alarm_settings " +
            "where monitor_class like #{feature} " +
            "or monitor_value like #{feature} " +
            "or lower_limit like #{feature} " +
            "or upper_limit like #{feature} " +
            "or message like #{feature}")
    public List<AlarmEntity> getAlarmSettingsByFeatures(String feature);
}
