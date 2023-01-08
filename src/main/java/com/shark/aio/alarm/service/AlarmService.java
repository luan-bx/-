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

    /**
     * 查询所有预警设置，分页且模糊查询
     * 分页通过AOP实现，详见com.shark.aio.util.pageAspect类
     * 方法参数pageNum和pageSize在方法体内从未使用，它们通过AOP在分页时使用
     * 模糊查询参数feature可以为空，代表不进行模糊查询
     * 若feature不为空，先进行模糊查询，再进行分页
     * @param pageNum 分页插件：当前页码
     * @param pageSize 分页插件：每页大小
     * @param feature 模糊查询：查询关键字
     * @return 查询后并分页的结果
     */
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

    /**
     * 新增预警设置或监测类型或污染物类型
     * 预警设置中，监测值不能重复
     * 监测类型和污染物类型不能和已有的重复
     * @param alarmEntity 新预警设置
     * @param newMonitorClass 新监测类型
     * @param existMonitorClass 已有的监测类型
     * @param newPollution 新污染物
     * @param existPollutionName 已有的污染物
     * @return 返回给页面的消息
     */
    public String addAlarmSetting(AlarmEntity alarmEntity,
                                  String newMonitorClass, String existMonitorClass,
                                  String newPollution, String existPollutionName){
        //新增预警设置，判断上下限阈值的合理性和监测值是否重复
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

        //新增监测值，判断是否和已有的监测值重复
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

        //新增污染物，判断是否和已有的污染物重复
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

    /**
     * 根据id删除预警设置
     * @param id id
     * @return 返回页面的消息
     */
    public String deleteAlarmSettingById(int id){
        try {
            alarmMapping.deleteAlarmSettingById(id);
            return "删除成功！";
        }catch (Exception e){
            e.printStackTrace();
            return "删除失败";
        }
    }

    /**
     * 编辑预警设置
     * @param alarmEntity 修改后的预警设置
     * @return 返回页面的消息
     */
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

    /**
     * 获取全部监测类型
     * @return 全部监测类型List
     */
    public List<String> getAllMonitorClass(){
        try {
            List<String> allMonitorClass = alarmMapping.getAllMonitorClass();
            return allMonitorClass;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取全部污染物名称
     * @return 全部污染物List
     */
    public List<String> getAllPollutionName(){
        try {
            List<String> allPollutionName = alarmMapping.getAllPollutionName();
            return allPollutionName;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 工具方法，供控制层调用，向request中存放所有监测类型和污染物
     * 查询监测类型或污染物名称失败后，返回false
     * @param request 要设置的request
     * @return 是否成功
     */
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
