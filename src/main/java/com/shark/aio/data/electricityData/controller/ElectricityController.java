package com.shark.aio.data.electricityData.controller;

import com.shark.aio.data.electricityData.service.ElectricityService;
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
 * 接受用电数据
 * 将数据存储数据库（何种方式分类存储）
 * 将数据传到前端（传多少条，太多会卡）
 */

@Controller
@Slf4j
public class ElectricityController {

	@Autowired
	private ElectricityService electricityService;


	/**
	 * 前端访问数据文件
	 * @param filePath
	 * @param req
	 * @throws IOException
	 */
	@RequestMapping("/returnElectricityData")
	public void returnElectricityData(String filePath, HttpServletResponse req) throws IOException {
		filePath = "D:/项目/AIO/data/electricityData.txt";
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
	@GetMapping("/electricity/add")
	public String toAddElectricityPage(HttpServletRequest request){
		if (!electricityService.searchMonitor(request)){
			return "500";
		}
		return Constants.ADDPOLLUTION;
	}
}
