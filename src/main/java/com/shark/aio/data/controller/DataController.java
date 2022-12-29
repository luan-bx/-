package com.lbx.aio.data.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
 * 接受数采仪数据
 * 将数据存储数据库（何种方式分类存储）
 * 将数据传到前端（传多少条，太多会卡）
 */
@Controller
@Slf4j
public class DataController {
	/**
	 * 验证码校验、接收数据
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
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
			response.setCharacterEncoding("utf-8");
			JSONObject jsonInfo = getJsonInfo(request);
			response.getWriter().print("application/json 的 serlvet接收到的数据如下：");
			response.getWriter().print(jsonInfo);
		}
		return strjson;
	}


	/**
	 * 数据转换json，更换时间格式，写入数据文件
	 * @param request
	 * @return
	 */
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
			//换时间戳-->字符串
			//先把要换的数据从json找到，再调用换格式的方法
			JSONObject update1 = JSONObject.fromObject(json.getString("data"));
			JSONArray update2=JSONArray.fromObject(update1.getString("dataPoints"));
			JSONObject update3= update2.getJSONObject(0);
			update3.put("time", TimeStamp2Date(JSONObject.fromObject(JSONArray.fromObject(JSONObject.fromObject(json.getString("data")).getString("dataPoints")).get(0)).getString("time")));//直接提交price的key，如果该key存在则替换value
			update2.set(0, update3);//覆盖掉原来的值
			update1.put("dataPoints", update2);  //再覆盖
			json.put("data", update1);
			 
//			String pretty = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
//					SerializerFeature.WriteDateUseDateFormat);
			String path = "D:/项目/AIO/";
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


	/**
	 * 前端访问数据文件
	 * @param filePath
	 * @param req
	 * @throws IOException
	 */
	@RequestMapping("/returnData")
	public void returnData(String filePath, HttpServletResponse req) throws IOException {
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
