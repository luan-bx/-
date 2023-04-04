package com.shark.aio.data.video.service;

import com.github.pagehelper.PageInfo;
import com.shark.aio.base.controller.InitFFmpeg;
import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
import com.shark.aio.data.video.entity.FfmpegProcess;
import com.shark.aio.data.video.entity.VideoEntity;
import com.shark.aio.data.video.mapper.VideoMapping;
import com.shark.aio.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@MapperScan(value = "com.shark.aio.data.video.mapper")
public class VideoService {

    @Autowired
    private VideoMapping videoMapping;

    /**
     * 存放rtsp地址和推拉流进程的映射关系
     */
    private static HashMap<String, FfmpegProcess> map = new HashMap<>();

    /**
     * 查询全部摄像头信息
     * @return 摄像头list
     */
    public List<VideoEntity> selectAllVideos(){
        return videoMapping.selectAllVideos();
    }

    /**
     * 查询摄像头信息
     * @param feature   模糊查询特征字符串
     * @return 分页查询结果
     */
    public List<VideoEntity> selectAllVideos(String feature){
        List<VideoEntity> videos;
        if(feature!=null&&!feature.equals("")){
            feature = "%"+feature+"%";
            videos = videoMapping.selectVideosByFeature(feature);
        }else{
            videos = videoMapping.selectAllVideos();
        }
        return videos;
    }

    public PageInfo<FaceRecordsEntity> selectFaceRecordsByPage(Integer pageSize, Integer pageNum){
        List<FaceRecordsEntity> faceRecords = videoMapping.selectAllFaceRecords();

        return new PageInfo<>(faceRecords,5);
    }

    public PageInfo<CarRecordsEntity> selectCarRecordsByPage(Integer pageSize, Integer pageNum){
        List<CarRecordsEntity> carRecords = videoMapping.selectAllCarRecords();

        return new PageInfo<>(carRecords,5);
    }


    /**
     * 使map中，stream参数对应的rtsp地址下的页面数量加一
     * 如果在数量增加前rtsp地址下没有页面，则创建推流进程
     * @param stream rtmp的stream参数
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
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

    /**
     * 使map中，stream参数对应的rtsp地址下的页面数量加一
     * 如果在数量减少后rtsp地址下没有页面，则销毁推流进程
     * @param stream
     */
    public void dropSession(String stream){
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

    public int insertVideo(String monitorName, String username, String password, String ip, String port, String description){
        String rtsp = "rtsp://"+username+":"+password+"@"+ip+":"+port;
        String stream = "stream"+ UUID.randomUUID();
        int count = videoMapping.insertIntoVideo(new VideoEntity(null,monitorName,rtsp,description,stream));
        if (count==1){
            InitFFmpeg.map.put(monitorName,new VideoRecorderService());
        }
        return count;

    }

}
