package com.shark.aio.video.mapper;

import com.shark.aio.video.entity.VideoEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapping {
    @Insert("INSERT INTO `video` VALUES (null, #{monitorName}, #{url}, #{description})")
    public void insertIntoVideo(VideoEntity video);

    @Select("SELECT * FROM `video`")
    public List<VideoEntity> selectAllVideos();

    @Select("SELECT * FROM `video` where monitor_name like #{feature} or description like #{feature}")
    public List<VideoEntity> selectVideosByFeature(String feature);

}
