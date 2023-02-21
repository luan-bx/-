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

    private HashMap<String, FfmpegProcess> map = new HashMap<>();

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


    public void addSession(String stream, HttpSession session) throws NoSuchFieldException, IllegalAccessException {
        String rtsp = videoMapping.selectRtspByStream(stream);
        if(!map.containsKey(rtsp)){
            //开始推流进程
            int pid = ProcessUtil.videoPreview(rtsp, stream);
            //把新ffmpegProcess对象放到map中
            HashSet<HttpSession> sessions = new HashSet<>();
            session.setMaxInactiveInterval(0);
            sessions.add(session);
            FfmpegProcess ffmpegProcess = new FfmpegProcess(pid, sessions);
            map.put(rtsp, ffmpegProcess);
        }else{
            map.get(rtsp).getSessions().add(session);
        }
        log.info("添加session对象："+session.toString());
        log.info("目前流"+rtsp+"下的session有："+map.get(rtsp).getSessions().size()+"个");
    }

    public void dropSession(String stream, HttpSession session){
        String rtsp = videoMapping.selectRtspByStream(stream);
        //删除一个session对象
        Set set = map.get(rtsp).getSessions();
        log.info("删除session对象："+session.toString());
        set.remove(session);
        session.setMaxInactiveInterval(30*60);
        log.info("目前流"+rtsp+"下的session还有："+map.get(rtsp).getSessions().size()+"个");
        //如果删除完session，进程下的session集合已经为空，代表该流已无人使用，关闭推流进程
        if (set.isEmpty()){

            System.out.println(ProcessUtil.close(map.get(rtsp).getPid()));
            map.remove(rtsp);
        }

    }

}
