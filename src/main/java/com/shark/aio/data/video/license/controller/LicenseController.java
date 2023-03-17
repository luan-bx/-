package com.shark.aio.data.video.license.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.util.Constants;
import com.shark.aio.util.DateUtil;
import com.shark.aio.util.ObjectUtil;
import com.shark.aio.util.UnicodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Controller
@Slf4j
public class LicenseController {
	@RequestMapping("/callLicenseAI")
	public static CarRecordsEntity callLicenseAI(File file) {

		CarRecordsEntity carRecord = null;
		DataOutputStream out = null;
		final String newLine = "\r\n";
		final String prefix = "--";
		try {
			URL url = new URL("http://114.212.128.235:8050/carID_image_predict");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			String BOUNDARY = "-------7da2e536604c8";
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			out = new DataOutputStream(conn.getOutputStream());
			StringBuilder sb1 = new StringBuilder();
			sb1.append(prefix);
			sb1.append(BOUNDARY);
			sb1.append(newLine);
			sb1.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + newLine);
			sb1.append("Content-Type:application/octet-stream");
			sb1.append(newLine);
			sb1.append(newLine);
			out.write(sb1.toString().getBytes());
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			byte[] bufferOut = new byte[1024];
			int bytes = 0;
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write(newLine.getBytes());
			in.close();

			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(end_data);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			line = reader.readLine();
			log.info("车牌识别算法："+line.substring(0,100));
			JSONObject result = JSON.parseObject(line);
			String msg = "";
			if (!ObjectUtil.isEmptyString(result.getString("class_name"))) {
				/*msg = line.substring(line.indexOf("\"img_str\":\"")+"\"img_str\":\"".length(), line.indexOf("\"}"));
				log.info(msg);*/
				carRecord = new CarRecordsEntity();
				String[] results = result.getString("class_name").split("/n");
				StringBuffer IDs = new StringBuffer();
				double score = 0;
				for(String s : results){
					String[] oneResult = s.split(" ");
					IDs.append(UnicodeUtil.UnicodeToString(oneResult[0])+" ");
					double thisScore = Double.parseDouble(oneResult[1]);
					if (thisScore > score){
						score = thisScore;
					}
				}
				carRecord.setResult(IDs.toString());
				carRecord.setScore(score);

				BASE64Decoder decoder = new BASE64Decoder();
				try {
					// 解密
					byte[] b = decoder.decodeBuffer(result.getString("img_str"));
					// 处理数据
					for(int i = 0; i < b.length; ++i) {
						if (b[i] < 0) {
							b[i] += 256;
						}
					}
					String fileName =  DateUtil.fileFormat.format(new Date())+".jpg";
					carRecord.setPictureUrl(fileName);
					OutputStream out2 = new FileOutputStream(Constants.CARIMGOUTPUTPATH + fileName);
					out2.write(b);
					out2.flush();
					out2.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
		return carRecord;
	}
}
