package com.shark.aio.conditionData.controller;

import com.shark.aio.conditionData.service.ConditionService;
import com.shark.aio.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 接受数工况数据
 * 将数据存储数据库（何种方式分类存储）
 * 将数据传到前端（传多少条，太多会卡）
 */

/**
 * 数采仪数据格式
 * {"data":
 * 		{"dataPoints":
 * 			[
 *            	{"variableName":"温度","dataPointId":8992316,"err":0,"slaveName":"高精度温湿度传感器IIOT-T20-BD","time":1665482403,"value":"24"},
 *           	{"variableName":"温度","dataPointId":8992316,"err":0,"slaveName":"高精度温湿度传感器IIOT-T20-BD","time":1665482403,"value":"40"}
 * 			],
 * 		"deviceId":
 * 			"00500222052800054256",
 * 		"deviceName":
 * 			"Sens"
 *    },
 * "type":"dataPoint"}
 */
@Controller
@Slf4j
public class ConditionController {

	@Autowired
	private ConditionService conditionService;


	/**
	 * 前端访问数据文件
	 * @param filePath
	 * @param req
	 * @throws IOException
	 */
	@RequestMapping("/returnConditionData")
	public void returnConditionData(String filePath, HttpServletResponse req) throws IOException {
		filePath = "D:/项目/AIO/conditionData.txt";
		FileInputStream fin = new FileInputStream(filePath);
		InputStreamReader reader = new InputStreamReader(fin);
		BufferedReader buffReader = new BufferedReader(reader);
		String strTmp = "";
		String str = "";
		int i = 0;
		while ((strTmp = buffReader.readLine()) != null) {
			if (i == 0) {
				str = str.concat("  ");
			}
			str = str.concat(strTmp);

		}
		buffReader.close();
		req.setContentType("text/html;charset=utf-8");
			req.getWriter().write(str);
	}


	/**
	 * 时间戳转字符串
	 * @param timestampString
	 * @return
	 */
	public static String TimeStamp2Date(String timestampString) {

		String formats = "yyyy-MM-dd HH:mm:ss";

		Long timestamp = Long.parseLong(timestampString) * 1000;

		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));

		return date;
		
		}

	/**
	 * 跳转到新增xxx页面
	 * @param request request
	 * @return 新增xxx页面
	 */
	@GetMapping("/condition/add")
	public String toAddConditionPage(HttpServletRequest request){
		if (!conditionService.searchMonitor(request)){
			return "500";
		}
		return Constants.ADDPOLLUTION;
	}
}
