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

	@RequestMapping(value = { "/settings", "/settings/{pageNum}/{pageSize}" })
	public String toAlarmSettingsPage(HttpServletRequest request, @PathVariable(required = false) Integer pageNum,
			@PathVariable(required = false) Integer pageSize, String feature) {
		PageInfo<AlarmEntity> alarmSettings = alarmService.getAlarmSettingsByPage(pageNum, pageSize, feature);

		request.setAttribute("allSettings", alarmSettings);
		return Constants.ALARMSETTINGS;
	}

	@GetMapping("/settings/add")
	public String toAddAlarmSettingPage(HttpServletRequest request) {
		if (!alarmService.setAttributeBYMonitorAndPollution(request)) {
			return "500";
		}
		return Constants.ADDALARMSETTING;
	}

	@PostMapping("/settings/add")
	public String addAlarmSetting(HttpServletRequest request, AlarmEntity alarmEntity, String newMonitorClass,
			String newPollution, String existPollutionName, String existMonitorClass) {
		String msg = alarmService.addAlarmSetting(alarmEntity, newMonitorClass, existMonitorClass, newPollution,
				existPollutionName);
		request.setAttribute(Constants.MSG, msg);
		if (msg.contains("成功"))
			return "forward:/alarm/settings";
		else
			return toAddAlarmSettingPage(request);
	}

	@GetMapping("/settings/delete/{id}")
	public String deleteAlarmSetting(HttpServletRequest request, @PathVariable int id) {
		String msg = alarmService.deleteAlarmSettingById(id);
		request.setAttribute(Constants.MSG, msg);
		return "forward:/alarm/settings";
	}

	@GetMapping("/settings/edit")
	public String toEditAlarmSettingPage(HttpServletRequest request, AlarmEntity alarmEntity) {
		request.setAttribute("alarmEntity", alarmEntity);
		if (!alarmService.setAttributeBYMonitorAndPollution(request)) {
			return "500";
		}
		return Constants.EDITALARMSETTING;
	}

	@PostMapping("/settings/edit")
	public String editAlarmSetting(HttpServletRequest request, AlarmEntity alarmEntity) {
		String msg = alarmService.editAlarmSetting(alarmEntity);
		request.setAttribute(Constants.MSG, msg);
		if (msg.contains("成功"))
			return "forward:/alarm/settings";
		else
			return toEditAlarmSettingPage(request, alarmEntity);

	}

}
