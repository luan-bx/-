package com.shark.aio.data.video.mapper;

import com.shark.aio.data.video.entity.VideoEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapping {
    /**
     * 插入一个摄像头
     * @param video
     */
    @Insert("INSERT INTO `video` VALUES (null, #{monitorName}, #{url}, #{description})")
    public void insertIntoVideo(VideoEntity video);

    /**
     * 查询全部摄像头
     * @return
     */
    @Select("SELECT * FROM `video`")
    public List<VideoEntity> selectAllVideos();

    /**
     * 根据特征feature模糊查询摄像头信息
     * @param feature
     * @return
     */
    @Select("SELECT * FROM `video` where monitor_name like #{feature} or description like #{feature}")
    public List<VideoEntity> selectVideosByFeature(String feature);

    /**
     * 根据stream查询rtsp
     * @param stream
     * @return
     */
    @Select("SELECT `rtsp` FROM `video` WHERE `stream`=#{stream}")
    public String selectRtspByStream(String stream);

}
