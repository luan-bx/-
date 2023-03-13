package com.shark.aio.video.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shark.aio.util.Constants;
import com.shark.aio.util.DateUtil;
import com.shark.aio.util.ProcessUtil;
import com.shark.aio.video.entity.CarRecordsEntity;
import com.shark.aio.video.entity.FaceRecordsEntity;
import com.shark.aio.video.entity.VideoEntity;
import com.shark.aio.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
@CrossOrigin
public class FaceController {

	@Autowired
	private VideoService videoService;



	@RequestMapping("/callFaceAI")
	@ResponseBody
	public static FaceRecordsEntity callFaceAI(File file) {
//		filePath = "C:\\Users\\dell\\Desktop\\1.png";
		FaceRecordsEntity faceRecord = null;
		DataOutputStream out = null;
		final String newLine = "\r\n";
		final String prefix = "--";
		try {
			URL url = new URL("http://localhost:5000/ssd_predict");
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
			log.info("人脸识别算法："+line.substring(0,100));
			JSONObject result = JSON.parseObject(line);
			String msg = "";
			if (result.getString("class_name").contains("person")) {
				faceRecord = new FaceRecordsEntity();
				//判断有几个人以及最高概率
				String[] classes = result.getString("class_name").split(";");
				int count = 0;
				double score = 0;
				for (String s: classes){
					if (!s.contains("person")) continue;
					count ++;
					double thisScore = Double.parseDouble(s.substring(s.indexOf("person: ")+"person: ".length()));
					if (thisScore > score){
						score = thisScore;
					}
				}
				faceRecord.setResult(count + "个人");
				faceRecord.setScore(score);
				/*msg = line.substring(line.indexOf("\"img_str\":\"")+"\"img_str\":\"".length(), line.indexOf("\"}"));
				log.info(msg);*/
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
					String fileName = DateUtil.fileFormat.format(new Date())+".jpg";
					faceRecord.setPictureUrl(fileName);
					OutputStream out2 = new FileOutputStream(Constants.FACEIMGOUTPUTPATH + fileName);
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
		return faceRecord;
	}


	/**
	 * 跳转到摄像头列表页面
	 * @param request request
	 * @param feature 模糊查询特征字符串
	 * @return 页面
	 */
	@RequestMapping("/videoMonitor")
	public String toVideoPage(HttpServletRequest request,
							 String feature){
		List<VideoEntity> videos = videoService.selectAllVideos(feature);

		request.setAttribute("class","video");
		request.setAttribute("allVideos",videos);
		return "video";
	}

	@RequestMapping({"/videoMonitor/face","/videoMonitor/face/{pageSize}/{pageNum}"})
	public String toFaceRecordsOfVideoPage(HttpServletRequest request,
										   @PathVariable(required = false) Integer pageSize,
										   @PathVariable(required = false) Integer pageNum ){
		PageInfo<FaceRecordsEntity> faceRecords = videoService.selectFaceRecordsByPage(pageSize,pageNum);
		request.setAttribute("class","face");
		request.setAttribute("faceRecords",faceRecords);
		return "video";
	}

	@RequestMapping({"/videoMonitor/car","/videoMonitor/car/{pageSize}/{pageNum}"})
	public String toCarRecordsOfVideoPage(HttpServletRequest request,
										   @PathVariable(required = false) Integer pageSize,
										   @PathVariable(required = false) Integer pageNum){

		PageInfo<CarRecordsEntity> carRecords = videoService.selectCarRecordsByPage(pageSize,pageNum);
		request.setAttribute("class","car");
		request.setAttribute("carRecords",carRecords);
		return "video";
	}

	@GetMapping("/addVideo")
	public String toAddVideoPage(){
		return "addVideo";
	}

	@PostMapping("/addVideo")
	public String addVideo(@RequestParam String monitorName,
			               @RequestParam String username,
						   @RequestParam String password,
						   @RequestParam String ip,
						   @RequestParam String port,
						   String description){


		return null;
	}
	/**
	 * 跳转到视频播放页面
	 * @param stream rtmp流中的stream参数
	 * @return 视频播放页面
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@GetMapping("/videoPlay")
	public String toVideoPlayPage(String stream, HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException {
		request.setAttribute("stream",new String[]{stream});
//		videoService.addSession(stream);
		return "videoPlay";
	}

	/**
	 * 退出视频播放页面，用于推流进程的开启关闭管理
	 * @param stream rtmp流中的stream参数
	 * @return 啥也不返回
	 */
	/*@PostMapping("/quitVideoPlay")
	@ResponseBody
	public String quitVideoPlayPage(@RequestParam String stream){
		videoService.dropSession(stream);
		return null;
	}*/



}
