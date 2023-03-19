package com.shark.aio.data.pollutionData.service;

import com.shark.aio.alarm.mapper.AlarmMapping;
import com.shark.aio.data.conditionData.entity.MnEntity;
import com.shark.aio.data.pollutionData.mapper.PollutionMapping;
import com.shark.aio.util.Constants;
import com.shark.aio.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lbx
 * @date 2023/2/10 - 18:35
 **/
@Service
@Slf4j
public class PollutionService {
    @Autowired
    private PollutionMapping pollutionMapping;
    @Autowired
    private AlarmMapping alarmMapping;

    /**
     * 获取全部监测点
     * @return 全部监测类型List
     */
    public List<String> getAllMonitor(){
        try {
            List<String> allMonitor = pollutionMapping.getAllMonitor();
            return allMonitor;
        }catch (Exception e){
            log.error("PollutionService/getAllMonitor:获取全部监测点失败！",e);
            return null;
        }
    }

    /**
     * 获取全部污染物名称
     * @return 全部污染物List
     */
    public List<String> getAllPollutionName(){
        try {
            List<String> allPollutionName = pollutionMapping.getAllPollutionName();
            return allPollutionName;
        }catch (Exception e){
            log.error("AlarmService/getAllMonitorClass:获取全部污染物名称失败！",e);
            return null;
        }
    }

    /**
     * 工具方法，供控制层调用，向request中存放所有监测类型和污染物
     * 查询监测类型或污染物名称失败后，返回false
     * @param request 要设置的request
     * @return 是否成功
     */
    public boolean searchMonitor(HttpServletRequest request){
        List<String> allMonitor = getAllMonitor();
        List<String> allPollutionName = getAllPollutionName();
        if (allMonitor == null || allPollutionName == null){
            return false;
        }
        request.setAttribute(Constants.ALLMONITOR, allMonitor);
        request.setAttribute(Constants.ALLPOLLUTIONNAME, allPollutionName);
        return true;
    }

    /**
     * 新增预警设置或监测类型或污染物类型
     * 预警设置中，监测值不能重复
     * 监测类型和污染物类型不能和已有的重复
     * @param pollutionMonitorEntity 新关联
     * @param newMonitorName 新监测名称
     * @param existMonitorName 已有的监测名称
     * @return 返回给页面的消息
     */
    public String addPollutionMonitor(MnEntity mnEntity,
                                      String newMonitorName, String existMonitorName){
        //新增预警设置，判断上下限阈值的合理性和监测值是否重复
        if (!ObjectUtil.isEmpty(mnEntity)){
           try {
               pollutionMapping.insert(mnEntity);
            }catch (DataIntegrityViolationException e){
                log.error("pollutionMonitor:新增关联失败！",e);
                return "设备不能和已有的重复！";
            }
            return "新增设备成功！";
        }

        //新增监测值，判断是否和已有的监测值重复
        if (newMonitorName!=null){
            //插入新的监测类型
            if (!ObjectUtil.isEmptyString(existMonitorName)){
                List<String> allMonitor = new ArrayList<>();
                Collections.addAll(allMonitor, existMonitorName.split(","));
                if (allMonitor.contains(newMonitorName)){
                    return "和已有监测类型重复！";
                }
            }
            try {
                alarmMapping.insertMonitor(newMonitorName);
                return "新增监测类型成功！";
            }catch (Exception e){
                log.error("AlarmService/addAlarmSetting:新增监测类型失败！",e);
                return "新增监测类型失败！";
            }
        }
        return "未接收到数据！";
    }

}
