package com.shark.aio.video.service;

import com.github.pagehelper.PageInfo;
import com.shark.aio.video.entity.VideoEntity;
import com.shark.aio.video.mapper.VideoMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VideoService {

    @Autowired
    private VideoMapping videoMapping;

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
}
