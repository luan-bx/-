package com.shark.aio.data.pollutionData.controller;

import com.alibaba.fastjson.JSON;
import com.shark.aio.alarm.entity.AlarmRecordEntity;
import com.shark.aio.alarm.entity.AlarmSettingsEntity;
import com.shark.aio.alarm.mapper.AlarmMapping;
import com.shark.aio.data.pollutionData.service.PollutionService;
import com.shark.aio.util.Constants;
import com.shark.aio.util.DateUtil;
import com.shark.aio.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 接受数采仪数据
 * 将数据存储数据库（何种方式分类存储）
 * 将数据传到前端（传多少条，太多会卡）
 */
@Controller
@Slf4j
public class PollutionController {

	@Autowired
	private AlarmMapping alarmMapping;
	@Autowired
	private PollutionService pollutionService;
	/**
	 * 验证码校验、接收数据
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/receivePollutionData")
	@ResponseBody
	protected String receivePollutionData(HttpServletRequest request, HttpServletResponse response)
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
		PollutionController c = new PollutionController();
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
			BufferedReader br = null;
			try {
				String line = null;
				br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
				while ((line = br.readLine()) != null) {
					sb.append(line);
					log.info(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				br.close();
			}

			log.info(String.valueOf(sb));
			json = JSONObject.fromObject(sb.toString());
			//换时间戳-->字符串
			//先把要换的数据从json找到，再调用换格式的方法
			//原方法为：找到json中时间戳的字段，修改后更新整个json
			//JSONObject为逐级找{}，JSONArray为逐级找[]
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

			String deviceID = JSONObject.fromObject((json.getString("data"))).getString("deviceId");

			Double value0Double = Double.parseDouble(value0);
			AlarmSettingsEntity alarmSettingsEntity = null;
			AlarmRecordEntity alarmRecordEntity= null ;
			alarmSettingsEntity =alarmMapping.getAlarmSettingsEntity(name0);
			if(alarmSettingsEntity!= null){
				if(alarmSettingsEntity.getLowerLimit() > value0Double){
					alarmRecordEntity.setAlarmTime(time0);
					alarmRecordEntity.setMonitor("监测点1");
					alarmRecordEntity.setMonitorClass("污染源监控");
					alarmRecordEntity.setMonitorValue(name0);
					alarmRecordEntity.setMonitorData(value0);
					alarmRecordEntity.setMessage("低于阈值");
					alarmMapping.insertalarmRecords(alarmRecordEntity);
				}
				if(alarmSettingsEntity.getUpperLimit() < value0Double){
					alarmRecordEntity.setAlarmTime(time0);
					alarmRecordEntity.setMonitor("监测点1");
					alarmRecordEntity.setMonitorClass("污染源监控");
					alarmRecordEntity.setMonitorValue(name0);
					alarmRecordEntity.setMonitorData(value0);
					alarmRecordEntity.setMessage("高于阈值");
					alarmMapping.insertalarmRecords(alarmRecordEntity);
				}
			}


			//			String pretty = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
//					SerializerFeature.WriteDateUseDateFormat);
			String documenPath = Constants.POLLUTIONPATH + DateUtil.Data;
			String filePath = documenPath + Constants.POLLUTIONDATA;
			File document = new File(documenPath);
			if (!document.exists()) {
				document.mkdirs();
			}
			File file = new File(filePath);
			String space = " ";
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true));
//				out.write(String.valueOf(json + "\r\n").getBytes());
				out.write(("{\"data\":[" +
						"{\"名称" + "\":" + "\"" + name0 + "\"," +
						"\"时间\":" + "\"" + time0 + "\","+
						"\"幅值\":" + "\"" + value0 + "\"}," +
						"{\"名称" + "\":" + "\"" + name1 + "\"," +
						"\"时间\":" + "\"" + time1 + "\"," +
						"\"幅值\":" + "\"" + value1 + "\"}]," +
						"\"设备ID\":" + "\"" + deviceID + "\"}" + "\r\n").getBytes());
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
	@RequestMapping("/returnPollutionData")
	public void returnPollutionData(String monitorName,String dir ,HttpServletResponse req) throws IOException {

		FileInputStream fin = new FileInputStream(Constants.POLLUTIONPATH + monitorName + (ProcessUtil.IS_WINDOWS?"\\":"/") + dir + (ProcessUtil.IS_WINDOWS?"\\":"/") + Constants.POLLUTIONDATA);
		InputStreamReader reader = new InputStreamReader(fin);
		BufferedReader buffReader = new BufferedReader(reader);
		String strTmp = "";
		ArrayList<com.alibaba.fastjson.JSONObject> data = new ArrayList<>();
		ArrayList<String> keyset = new ArrayList<>();
		keyset.add("DataTime");
		com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
		while ((strTmp = buffReader.readLine()) != null) {
			com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(strTmp);
			com.alibaba.fastjson.JSONObject dataObj = jsonObject.getJSONObject("CP");
			dataObj.put("DataTime",jsonObject.getString("DataTime"));
			data.add(dataObj);
			for (String key : dataObj.keySet()){
				if (!keyset.contains(key)){
					keyset.add(key);
				}
			}
		}
		result.put("keySet",keyset);
		result.put("data",data.toArray());
		String response = result.toJSONString();
		buffReader.close();
		req.setContentType("text/html;charset=utf-8");
		req.getWriter().write(response);
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

	@RequestMapping("/pollutionMonitor")
	public String pollutionMonitorWeb(HttpServletRequest request) {
		File file = new File(Constants.POLLUTIONPATH);
		File[] files = file.listFiles();
		String[] fileList = new String[files.length];
		for(int i=0;i<files.length;i++){
			fileList[i]=files[i].getName();
		}
		request.setAttribute("allMonitors",fileList);
		return "pollutionMonitor";
	}

	@RequestMapping("returnPollutionFileList")
	@ResponseBody
	public String[] returnConditionFileList(String monitorName){
		File conditionDir = new File(Constants.POLLUTIONPATH+monitorName);
		File[] files = conditionDir.listFiles();
		String[] fileList = new String[files.length];
		for(int i=0;i<files.length;i++){
			fileList[i]=files[i].getName();
		}
		return fileList;
	}
}
