package com.shark.aio.alarm.service;

import com.github.pagehelper.PageInfo;
import com.shark.aio.alarm.entity.AlarmEntity;
import com.shark.aio.alarm.mapper.AlarmMapping;
import com.shark.aio.util.Constants;
import com.shark.aio.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlarmService {

    @Autowired
    private AlarmMapping alarmMapping;

    public PageInfo<AlarmEntity> getAlarmSettingsByPage(Integer pageNum, Integer pageSize, String feature){
        List<AlarmEntity> alarmSettings;
        if(feature!=null&&!feature.equals("")){
            feature = "%"+feature+"%";
            alarmSettings = alarmMapping.getAlarmSettingsByFeatures(feature);
        }else{
            alarmSettings = alarmMapping.getAllAlarmSettings();
        }
        PageInfo<AlarmEntity> pageInfo = new PageInfo<>(alarmSettings, 5);
        return pageInfo;
    }

    public String addAlarmSetting(AlarmEntity alarmEntity,
                                  String newMonitorClass, String existMonitorClass,
                                  String newPollution, String existPollutionName){
        if (!ObjectUtil.isEmpty(alarmEntity)){
            if (alarmEntity.getUpperLimit()<=alarmEntity.getLowerLimit()) return "上限阈值须大于下限阈值！";
           try {
               alarmMapping.insertAlarmEntity(alarmEntity);
           }catch (DataIntegrityViolationException e){
                e.printStackTrace();
                return "监测值不能和已有的重复！";
           }
           return "新增预警设置成功！";
        }
        if (newMonitorClass!=null){
            //插入新的监测类型
            List<String> allMonitorClass = new ArrayList<>();
            Collections.addAll(allMonitorClass, existMonitorClass.split(","));
            if (allMonitorClass.contains(newMonitorClass)){
                return "和已有监测类型重复！";
            }
            try {
                alarmMapping.insertMonitorClass(newMonitorClass);
                return "新增监测类型成功！";
            }catch (Exception e){
                e.printStackTrace();
                return "新增监测类型失败！";
            }
        }
        if (newPollution!=null){
            //插入新的污染物
            List<String> pollutionNameList = Arrays.stream(existPollutionName.split(",")).collect(Collectors.toList());
            if (pollutionNameList.contains(newPollution)){
                return "和已有污染物重复！";
            }
            try {
                alarmMapping.insertPollution(newPollution);
                return "新增污染物成功！";
            }catch (Exception e){
                e.printStackTrace();
                return "新增污染物失败！";
            }
        }
        return "未接收到数据！";
    }

    public String deleteAlarmSettingById(int id){
        try {
            alarmMapping.deleteAlarmSettingById(id);
            return "删除成功！";
        }catch (Exception e){
            e.printStackTrace();
            return "删除失败";
        }
    }

    public String editAlarmSetting(AlarmEntity alarmEntity){
        if (alarmEntity.getUpperLimit()<=alarmEntity.getLowerLimit()) return "上限阈值须大于下限阈值！";
        if (alarmMapping.getMonitorValue(alarmEntity.getMonitorValue())!=null) return "监测值和已有监测值重复！";
        try {
            alarmMapping.updateAlarmSetting(alarmEntity);
            return "修改成功！";

        }catch (Exception e){
            e.printStackTrace();
            return "修改失败！";
        }
    }

    public List<String> getAllMonitorClass(){
        try {
            List<String> allMonitorClass = alarmMapping.getAllMonitorClass();
            return allMonitorClass;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllPollutionName(){
        try {
            List<String> allPollutionName = alarmMapping.getAllPollutionName();
            return allPollutionName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public boolean setAttributeBYMonitorAndPollution(HttpServletRequest request){
        List<String> allMonitorClass = getAllMonitorClass();
        List<String> allPollutionName = getAllPollutionName();
        if (allPollutionName==null || allMonitorClass==null){
            return false;
        }
        request.setAttribute(Constants.ALLMONITORCLASS, allMonitorClass);
        request.setAttribute(Constants.ALLPOLLUTIONNAME, allPollutionName);
        return true;
    }

}
