package com.shark.aio.alarm.service;

import com.github.pagehelper.PageInfo;
import com.shark.aio.alarm.entity.AlarmEntity;
import com.shark.aio.alarm.mapper.AlarmMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlarmService {

    @Autowired
    private AlarmMapping alarmMapping;

    public PageInfo<AlarmEntity> getAlarmSettingsByPage(Integer pageNum, Integer pageSize, String feature){
        List<AlarmEntity> alarmSettings;
        System.out.println(feature);
        if(feature!=null&&!feature.equals("")){
            feature = "%"+feature+"%";
            alarmSettings = alarmMapping.getAlarmSettingsByFeatures(feature);
        }else{
            alarmSettings = alarmMapping.getAllAlarmSettings();
        }
        PageInfo<AlarmEntity> pageInfo = new PageInfo<>(alarmSettings, 5);
        return pageInfo;
    }
}
