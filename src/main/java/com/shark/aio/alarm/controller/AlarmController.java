package com.shark.aio.alarm.controller;

import com.github.pagehelper.PageInfo;
import com.shark.aio.alarm.entity.AlarmEntity;
import com.shark.aio.alarm.service.AlarmService;
import com.shark.aio.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    @RequestMapping(value = {"/settings","/settings/{pageNum}/{pageSize}"})
    public String toAlarmSettingsPage(HttpServletRequest request,
                                      @PathVariable(required = false) Integer pageNum,
                                      @PathVariable(required = false) Integer pageSize,
                                      String feature){
        PageInfo<AlarmEntity> alarmSettings = alarmService.getAlarmSettingsByPage(pageNum, pageSize, feature);

        request.setAttribute("allSettings",alarmSettings);
        return Constants.ALARMSETTINGS;
    }
}
