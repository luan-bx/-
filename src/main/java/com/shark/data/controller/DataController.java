package com.shark.data.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.internal.metadata.descriptor.ReturnValueDescriptorImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DataController {
	@RequestMapping("/receive")
	@ResponseBody
	protected String receive(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		log.info("进入接收方法");
		String strjson = request.getParameter("verify");
		if (strjson != null) {
			log.info(JSON.toJSONString(strjson));
			return strjson;
		} else {
			log.info("数据来了");
			response.setCharacterEncoding("utf-8");
			JSONObject jsonInfo = getJsonInfo(request);
			response.getWriter().print("application/json 的 serlvet接收到的数据如下：");
			response.getWriter().print(jsonInfo);
//			log.info(strjson);

		}
		return strjson;
	}

	private JSONObject getJsonInfo(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			
			InputStreamReader in = new InputStreamReader(request.getInputStream(), "utf-8");
			BufferedReader br = new BufferedReader(in);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				log.info(line);
			}
			br.close();
			json = JSONObject.fromObject(sb.toString());
			JSONObject update1 = JSONObject.fromObject(json.getString("data"));
			JSONArray update2=JSONArray.fromObject(update1.getString("dataPoints"));
			JSONObject update3= update2.getJSONObject(0);
			update3.put("time", TimeStamp2Date(JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("time")));//直接提交price的key，如果该key存在则替换value
			update2.set(0, update3);//覆盖掉原来的值
			update1.put("dataPoints", update2);  //再覆盖
			json.put("data", update1);
			 
//			String pretty = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
//					SerializerFeature.WriteDateUseDateFormat);
			String path = "/Users/lb/Downloads/shark/环保仪/data/";
			File file = new File(path + "shucaiyi.txt");
			if (file.exists()) {
//				System.out.println("文件存在");
			} else {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("文件创建成功");
			}

			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true));
				out.write(String.valueOf(json + "\r\n").getBytes());
				out.flush();
				try {
					out.close();
				} catch (IOException e) {
					log.info("FileService/saveData, 关闭文件失败", e);
				}
				System.out.println("json数据保存到成功！！！");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("从前端接收到的json数据：" + json);
//			log.info("从前端接收到的json数据：" + json);
//			log.info(
//					"温度值" + JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("value") + "湿度值"
//							+ JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(1)).getString("value"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return json;
	}

	@RequestMapping("/returnJson")
	public void returnJson(String filePath, HttpServletResponse req) throws IOException {
		filePath = "/Users/lb/Downloads/shark/环保仪/data/shucaiyi.txt";
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
			log.info(str);
			
		}
		buffReader.close();
		req.setContentType("text/html;charset=utf-8");
			req.getWriter().write(str);
	}
	//时间戳转字符串
	public static String TimeStamp2Date(String timestampString) {

		String formats = "yyyy-MM-dd HH:mm:ss";

		Long timestamp = Long.parseLong(timestampString) * 1000;

		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));

		return date;
		
		}
}
