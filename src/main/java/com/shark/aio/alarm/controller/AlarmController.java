package com.shark.aio.alarm.controller;

import com.github.pagehelper.PageInfo;
import com.shark.aio.alarm.entity.AlarmEntity;
import com.shark.aio.alarm.service.AlarmService;
import com.shark.aio.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    /**
     * 跳转到预警设置页面
     * 查询现有所有预警设置，返回前端
     * @param request http请求
     * @param pageNum 分页插件：当前页码数
     * @param pageSize 分页插件：每页大小
     * @param feature 模糊查询，查询关键字
     * @return 预警设置页面
     */
    @RequestMapping(value = {"/settings","/settings/{pageNum}/{pageSize}"})
    public String toAlarmSettingsPage(HttpServletRequest request,
                                      @PathVariable(required = false) Integer pageNum,
                                      @PathVariable(required = false) Integer pageSize,
                                      String feature){
        PageInfo<AlarmEntity> alarmSettings = alarmService.getAlarmSettingsByPage(pageNum, pageSize, feature);

        request.setAttribute("allSettings",alarmSettings);
        return Constants.ALARMSETTINGS;
    }

    /**
     * 跳转到新增预警设置页面
     * @param request request
     * @return 新增预警页面
     */
    @GetMapping("/settings/add")
    public String toAddAlarmSettingPage(HttpServletRequest request){
        if (!alarmService.setAttributeBYMonitorAndPollution(request)){
            return "500";
        }
        return Constants.ADDALARMSETTING;
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
     * @return 操作成功返回预警设置页，否则返回新增页
     */
    @PostMapping("/settings/add")
    public String addAlarmSetting(HttpServletRequest request, AlarmEntity alarmEntity,
                                  String newMonitorClass, String newPollution,
                                  String existPollutionName, String existMonitorClass){
        String msg = alarmService.addAlarmSetting(alarmEntity,newMonitorClass,existMonitorClass,newPollution,existPollutionName);
        request.setAttribute(Constants.MSG, msg);
        if (msg.contains("成功")) return "forward:/alarm/settings";
        else return toAddAlarmSettingPage(request);
    }

    /**
     * 删除预警设置
     * @param request request
     * @param id id
     * @return 预警设置页
     */
    @GetMapping("/settings/delete/{id}")
    public String deleteAlarmSetting(HttpServletRequest request, @PathVariable int id){
        String msg = alarmService.deleteAlarmSettingById(id);
        request.setAttribute(Constants.MSG, msg);
        return "forward:/alarm/settings";
    }

    /**
     * 跳转到预警设置编辑页面
     * @param request request
     * @param alarmEntity 修改前的预警设置
     * @return 预警设置编辑页
     */
    @GetMapping("/settings/edit")
    public String toEditAlarmSettingPage(HttpServletRequest request ,AlarmEntity alarmEntity){
        request.setAttribute("alarmEntity", alarmEntity);
        if (!alarmService.setAttributeBYMonitorAndPollution(request)){
            return "500";
        }
        return Constants.EDITALARMSETTING;
    }

    /**
     * 编辑预警设置
     * @param request request
     * @param alarmEntity 修改后的预警设置
     * @return 成功返回预警设置页面，否则返回编辑页面
     */
    @PostMapping("/settings/edit")
    public String editAlarmSetting(HttpServletRequest request, AlarmEntity alarmEntity){
        String msg = alarmService.editAlarmSetting(alarmEntity);
        request.setAttribute(Constants.MSG, msg);
        if (msg.contains("成功")) return "forward:/alarm/settings";
        else return toEditAlarmSettingPage(request, alarmEntity);

    }

}
