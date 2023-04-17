package com.shark.aio.data.video.mapper;

import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
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
    @Insert("INSERT INTO `video` VALUES (null, #{monitorName}, #{rtsp}, #{description},#{stream})")
    public int insertIntoVideo(VideoEntity video);

    /**
     * 查询全部摄像头
     * @return
     */
    @Select("SELECT * FROM `video`")
    public List<VideoEntity> selectAllVideos();

    @Select("SELECT monitor_name FROM video")
    public List<String> selectAllVideoName();

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

    @Insert("INSERT INTO face_records (picture_url,result,score) VALUES(#{pictureUrl},#{result},#{score})")
    public void insertFaceRecord(FaceRecordsEntity faceRecords);

    @Select("SELECT * FROM face_records")
    public List<FaceRecordsEntity> selectAllFaceRecords();

    @Select("SELECT * FROM face_records ORDER BY time DESC LIMIT 4")
    public List<FaceRecordsEntity> selectFourFaceRecords();


    @Insert("INSERT INTO car_records (picture_url,result,score) VALUES(#{pictureUrl},#{result},#{score})")
    public void insertCarRecord(CarRecordsEntity carRecords);
    @Select("SELECT * FROM car_records")
    public List<CarRecordsEntity> selectAllCarRecords();

    @Select("SELECT * FROM car_records ORDER BY time DESC LIMIT 4")
    public List<CarRecordsEntity> selectFourCarRecords();
    @Select("SELECT stream FROM video WHERE monitor_name=#{videoName}")
    public String queryStreamByVideoName(String videoName);
}
