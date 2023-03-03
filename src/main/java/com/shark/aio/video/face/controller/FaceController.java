package com.shark.aio.video.face.controller;

import com.github.pagehelper.PageInfo;
import com.shark.aio.util.ProcessUtil;
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

@Controller
@Slf4j
@CrossOrigin
public class FaceController {

	@Autowired
	private VideoService videoService;


	@RequestMapping("/callFaceAI")
	@ResponseBody
	public static void callFaceAI(String filePath) {
//		filePath = "C:\\Users\\thg\\Desktop\\1.jpg";
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
			File file = new File(filePath);
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
			log.info(line.substring(0,100));
			String msg = "";
			if (line.contains("person")) {
				msg = line.substring(line.indexOf("\"img_str\":\"")+"\"img_str\":\"".length(), line.indexOf("\"}"));
				log.info(msg);
				BASE64Decoder decoder = new BASE64Decoder();

				try {
					// 解密
					byte[] b = decoder.decodeBuffer(msg);
					// 处理数据
					for(int i = 0; i < b.length; ++i) {
						if (b[i] < 0) {
							b[i] += 256;
						}
					}
					OutputStream out2 = new FileOutputStream(ProcessUtil.IS_WINDOWS?"C:\\Users\\thg\\Desktop\\face.jpg":"/home/user/AIO/image/AIResult/face.jpg");
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
	}


	/**
	 * 跳转到摄像头列表页面
	 * @param request request
	 * @param pageSize 分页每页大小
	 * @param pageNum 分页页码
	 * @param feature 模糊查询特征字符串
	 * @return 页面
	 */
	@RequestMapping({"/videoMonitor","/videoMonitor/{pageSize}/{pageNum}"})
	public String toFacePage(HttpServletRequest request,
							 @PathVariable(required = false) Integer pageSize,
							 @PathVariable(required = false) Integer pageNum ,
							 String feature){
		PageInfo<VideoEntity> videos = videoService.selectVideosByPage(pageSize, pageNum, feature);
		/*for(int i=0;i<10;i++){
			videos.add(new VideoEntity(i+1,"监测点"+(i+1),"rtmp://localhost:1935/myapp/room","测试监测点"));
		}*/

		request.setAttribute("allVideos",videos);
		return "video";
	}

	/**
	 * 跳转到视频播放页面
	 * @param stream rtmp流中的stream参数
	 * @return 视频播放页面
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@GetMapping("/videoPlay")
	public String toVideoPlayPage(@ModelAttribute("stream")String stream) throws NoSuchFieldException, IllegalAccessException {
		videoService.addSession(stream);
		return "videoPlay";
	}

	/**
	 * 退出视频播放页面，用于推流进程的开启关闭管理
	 * @param stream rtmp流中的stream参数
	 * @return 啥也不返回
	 */
	@PostMapping("/quitVideoPlay")
	@ResponseBody
	public String quitVideoPlayPage(@RequestParam String stream){
		videoService.dropSession(stream);
		return null;
	}



}
