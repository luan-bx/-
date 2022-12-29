package com.lbx.aio.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Skip {
	@RequestMapping("/index")
	public String indexWeb() {
		return "index";
	}
	
	@RequestMapping("/wastedWater")
	public String wastedWaterWeb() {
		return "wastedWater";
	}
	
	@RequestMapping("/carId")
	public String carIdWeb() {
		return "carId";
	}
	
	@RequestMapping("/wastedGas")
	public String wastedGasWeb() {
		return "wastedGas";
	}
	
	@RequestMapping("/conditionMonitor")
	public String conditionMonitorWeb() {
		return "conditionMonitor";
	}
	
	@RequestMapping("/face")
	public String faceWeb() {
		return "face";
	}
	
	@RequestMapping("/eleMonitor")
	public String eleMonitorWeb() {
		return "eleMonitor";
	}
	
	@RequestMapping("/alarm")
	public String alarmWeb() {
		return "alarm";
	}
	
	@RequestMapping("/map")
	public String mapWeb() {
		return "map";
	}
	
	@RequestMapping("/information")
	public String informationWeb() {
		return "information";
	}
	
	@RequestMapping("/upload")
	public String uploadWeb() {
		return "upload";
	}
	@RequestMapping("/trace")
	public String traceWeb() {
		return "trace";
	}
	
	@RequestMapping("/file")
	public String fileWeb() {
		return "file";
	}
	
	@RequestMapping("/disk")
	public String diskWeb() {
		return "disk";
	}
	
	@RequestMapping("/user")
	public String userWeb() {
		return "user";
	}
	
	@RequestMapping("/authority")
	public String authorityWeb() {
		return "authority";
	}
	
	@RequestMapping("/log")
	public String logWeb() {
		return "log";
	}
	
	@RequestMapping("/device")
	public String deviceWeb() {
		return "device";
	}

	@RequestMapping("/heatMap")
	public String heatMapWeb() {
		return "heatMap";
	}

	@RequestMapping("/huiliu")
	public String huiliuWeb() {
		return "huiliu";
	}

	@RequestMapping("/traceHeatMap")
	public String traceHeatMapWeb() {
		return "traceHeatMap";
	}
}