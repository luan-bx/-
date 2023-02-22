package com.shark.aio.video.service;

import com.github.pagehelper.PageInfo;
import com.shark.aio.util.ProcessUtil;
import com.shark.aio.video.WebSocket;
import com.shark.aio.video.entity.FfmpegProcess;
import com.shark.aio.video.entity.VideoEntity;
import com.shark.aio.video.mapper.VideoMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class VideoService {

    @Autowired
    private VideoMapping videoMapping;

    private static HashMap<String, FfmpegProcess> map = new HashMap<>();

    public List<VideoEntity> selectAllVideos(){
        return videoMapping.selectAllVideos();
    }

    public PageInfo<VideoEntity> selectVideosByPage(Integer pageSize, Integer pageNum, String feature){
        List<VideoEntity> videos;
        if(feature!=null&&!feature.equals("")){
            feature = "%"+feature+"%";
            videos = videoMapping.selectVideosByFeature(feature);
        }else{
            videos = videoMapping.selectAllVideos();
        }
        return new PageInfo<>(videos,5);
    }


    public void addSession(String stream) throws NoSuchFieldException, IllegalAccessException {
        String rtsp = videoMapping.selectRtspByStream(stream);
        synchronized (map){
            if(!map.containsKey(rtsp)){
                //开始推流进程
                int pid = ProcessUtil.videoPreview(rtsp, stream);
                //把新ffmpegProcess对象放到map中
                FfmpegProcess ffmpegProcess = new FfmpegProcess(pid, 1);
                    map.put(rtsp, ffmpegProcess);
            }else{
                    map.get(rtsp).increase();
            }
        }
        log.info("目前流"+rtsp+"下的页面有："+map.get(rtsp).getCount()+"个");
    }

    public void dropSession(String stream, HttpSession session){
        String rtsp = videoMapping.selectRtspByStream(stream);
        synchronized (map){
            FfmpegProcess ffmpegProcess = map.get(rtsp);
            ffmpegProcess.decrease();
            log.info("目前流"+rtsp+"下的页面还有："+ffmpegProcess.getCount()+"个");
            //如果流已无人使用，关闭推流进程
            if (ffmpegProcess.getCount()<=0){
                System.out.println(ProcessUtil.close(map.get(rtsp).getPid()));
                map.remove(rtsp);
            }
        }

    }

}
