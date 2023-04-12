package com.shark.aio.data.video.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shark.aio.base.annotation.Description;
import com.shark.aio.data.video.configuration.VideoConfiguration;
import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
import com.shark.aio.data.video.entity.VideoEntity;
import com.shark.aio.data.video.service.VideoRecorderService;
import com.shark.aio.data.video.service.VideoService;
import com.shark.aio.util.Constants;
import com.shark.aio.util.DateUtil;
import com.shark.aio.util.ObjectUtil;
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
		String ip = videoService.queryIdByCompanyId(companyId);
		request.setAttribute("ip",ip);
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
