package com.shark.aio.data.video.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shark.aio.base.annotation.Description;
import com.shark.aio.data.video.configuration.VideoConfiguration;
import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
import com.shark.aio.data.video.entity.VideoEntity;
import com.shark.aio.data.video.mapper.VideoMapping;
import com.shark.aio.data.video.service.VideoRecorderService;
import com.shark.aio.data.video.service.VideoService;
import com.shark.aio.util.Constants;
import com.shark.aio.util.DateUtil;
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
	public FaceRecordsEntity callFaceAI(File file , VideoEntity video) {
//		filePath = "C:\\Users\\dell\\Desktop\\1.png";
		FaceRecordsEntity faceRecord = null;
		CarRecordsEntity carRecord = null;
		DataOutputStream out = null;
		VideoRecorderService videoRecorderService = VideoConfiguration.getBean(VideoRecorderService.class, video.getMonitorName());
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
			log.info("ssd算法："+line.substring(0,100));
			JSONObject result = JSON.parseObject(line);
			String msg = "";
			boolean isPerson = result.getString("class_name").contains("person");
			boolean isCar = result.getString("class_name").contains("car");
			if (isPerson||isCar) {
				//录制视频
				videoRecorderService.setStatus(true);
				if (!videoRecorderService.isRecording()){
					log.info("开始录制...");
					videoRecorderService.startRecordVideo(video);
				}
				if (isCar)carRecord = new CarRecordsEntity();
				//判断有几个人和车以及最高概率
				String[] classes = result.getString("class_name").split(";");
				int personCount = 0;
				int carCount = 0;
				double personScore = 0;
				double carScore = 0;
				for (String s: classes){
					//person
					if (!s.contains("person")) continue;
					personCount ++;
					double thisPersonScore = Double.parseDouble(s.substring(s.indexOf("person: ")+"person: ".length()));
					if (thisPersonScore > personScore){
						personScore = thisPersonScore;
					}
					//car
					if (!s.contains("person")) continue;
					carCount ++;
					double thisCarScore = Double.parseDouble(s.substring(s.indexOf("car: ")+"car: ".length()));
					if (thisCarScore > carScore){
						carScore = thisCarScore;
					}
				}

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
					OutputStream out2 = new FileOutputStream(Constants.FACEIMGOUTPUTPATH + fileName);
					out2.write(b);
					out2.flush();
					out2.close();
					log.info("有人，图片已存");

					if (isPerson){
						faceRecord = new FaceRecordsEntity();
						faceRecord.setResult(personCount + "个人");
						faceRecord.setScore(personScore);
						faceRecord.setPictureUrl(fileName);
						videoService.insertFaceRecord(faceRecord);
					}
					if (isCar){
						carRecord = new CarRecordsEntity();
						carRecord.setResult(carCount + "辆车");
						carRecord.setScore(carScore);
						carRecord.setPictureUrl(fileName);
						videoService.insertCarRecord(carRecord);
					}




				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else {
				videoRecorderService.setStatus(false);
			}



		} catch (Exception e) {
			log.error("发送POST请求出现异常！",e);
		}
		//删除照片文件
		return faceRecord;
	}

	/**
	 * 跳转到摄像头列表页面
	 * @param request request
	 * @param feature 模糊查询特征字符串
	 * @return 页面
	 */
	@RequestMapping("/videoMonitor")
	@Description("跳转到摄像头列表页面")
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
		log.info("进入人员识别结果页面成功！");
		return "video";
	}

	@RequestMapping({"/videoMonitor/car","/videoMonitor/car/{pageSize}/{pageNum}"})
	public String toCarRecordsOfVideoPage(HttpServletRequest request,
										   @PathVariable(required = false) Integer pageSize,
										   @PathVariable(required = false) Integer pageNum){

		PageInfo<CarRecordsEntity> carRecords = videoService.selectCarRecordsByPage(pageSize,pageNum);
		request.setAttribute("class","car");
		request.setAttribute("carRecords",carRecords);
		log.info("进入车牌识别结果页面成功！");
		return "video";
	}

	@GetMapping("/addVideo")
	public String toAddVideoPage(){
		log.info("进入添加摄像头页面成功！");
		return "addVideo";
	}


	/**
	 * 跳转到视频播放页面
	 * @param stream rtmp流中的stream参数
	 * @return 视频播放页面
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@GetMapping("/videoPlay")
	public String toVideoPlayPage(String stream,String companyId ,HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException {
		request.setAttribute("stream",new String[]{stream});
		log.info("进入摄像头实时监测页面成功！");
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
