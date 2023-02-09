package com.shark.aio.conditionData.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
 *        {"dataPoints":
 * 				[
 *            		 {"variableName":"温度","dataPointId":8992316,"err":0,"slaveName":"高精度温湿度传感器IIOT-T20-BD","time":1665482403,"value":"24"},
 *           		 {"variableName":"温度","dataPointId":8992316,"err":0,"slaveName":"高精度温湿度传感器IIOT-T20-BD","time":1665482403,"value":"40"}
 * 				],
 * 			"deviceId":
 * 				"00500222052800054256",
 * 			"deviceName":
 * 				"Sens"
 *   	 },
 * "type":"dataPoint"}
 */
@Controller
@Slf4j
public class ConditionController {
	/**
	 * 验证码校验、接收数据
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/receiveConditionData")
	@ResponseBody
	protected String receiveConditionData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		log.info("进入接收方法");
		String strjson = request.getParameter("verify");
		if (strjson != null) {
			log.info(JSON.toJSONString(strjson));
			return strjson;
		} else {
			response.setCharacterEncoding("utf-8");
			JSONObject jsonInfo = getJsonInfo(request);
			response.getWriter().print("application/json 的 serlvet接收到的数据如下：");
			response.getWriter().print(jsonInfo);
		}
		return strjson;
	}

public static void main(String arg[]){
		ConditionController c = new ConditionController();
	HttpServletRequest  request = null;
	c.getJsonInfo(request);
}
	/**
	 * 数据转换json，更换时间格式，写入数据文件
	 * @param request
	 * @return
	 */
	@Test
	private JSONObject getJsonInfo(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			
//			InputStreamReader in = new InputStreamReader(request.getInputStream(), "utf-8");
//			BufferedReader br = new BufferedReader(in);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				sb.append(line);
//				log.info(line);
//			}
//			br.close();
			StringBuilder sb = new StringBuilder();
			FileReader fileReader = null;
			try {
				fileReader = new FileReader("D:/项目/AIO/shucaiyi.txt");
				byte[] bytes = new byte[4];//每一次读取四个字节
				String line = null;
				BufferedReader br = new BufferedReader(fileReader);
			while ((line = br.readLine()) != null) {
				sb.append(line);
				log.info(line);
			}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				fileReader.close();
			}

		log.info(String.valueOf(sb));
			json = JSONObject.fromObject(sb.toString());
			//换时间戳-->字符串
			//先把要换的数据从json找到，再调用换格式的方法
			//原方法为：找到json中时间戳的字段，修改后更新整个json
			JSONObject update1 = JSONObject.fromObject(json.getString("data"));
			JSONArray update2=JSONArray.fromObject(update1.getString("dataPoints"));
			JSONObject update3= update2.getJSONObject(0);
//			update3.put("time", TimeStamp2Date(JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("time")));//直接提交price的key，如果该key存在则替换value
//			update2.set(0, update3);//覆盖掉原来的值
//			update1.put("dataPoints", update2);  //再覆盖
//			json.put("data", update1);

			//现方法：找到该字段，修改后，取值直接写入文件
			String time0 = TimeStamp2Date(JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("time"));
			String time1 = TimeStamp2Date(JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(1)).getString("time"));

			String name0 = JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("variableName");
			String name1 = JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(1)).getString("variableName");

			String value0 = JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("value");
			String value1 = JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(1)).getString("value");

//			String pretty = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
//					SerializerFeature.WriteDateUseDateFormat);
			String path = "D:/项目/AIO/";
			File file = new File(path + "pollutionData.txt");
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

			String space = " ";
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true));
//				out.write(String.valueOf(json + "\r\n").getBytes());
				out.write((name0 + space + time0 + space + value0 + space + name1 + space + time1 + space + value1 + "\r\n").getBytes());
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


	/**
	 * 前端访问数据文件
	 * @param filePath
	 * @param req
	 * @throws IOException
	 */
	@RequestMapping("/returnConditionData")
	public void returnConditionData(String filePath, HttpServletResponse req) throws IOException {
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
}
