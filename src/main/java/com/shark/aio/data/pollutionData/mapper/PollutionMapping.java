package com.shark.aio.data.pollutionData.mapper;

import com.shark.aio.data.pollutionData.entity.PollutionMonitorEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lbx
 * @date 2023/2/10 - 18:39
 **/
@Mapper
public interface PollutionMapping {

    @Select("select name from monitor")
    public List<String> getAllMonitor();
    /**
     * 从污染物类型pollution表中查询所有污染物
     * @return 所有污染物List
     */
    @Select("select name from pollution")
    public List<String> getAllPollutionName();


    /*
     * 插入一个设备id与监测点关联记录
     */
    @Insert("INSERT INTO `pollution_monitor` (`monitor_name`, `device_id`) VALUES (#{monitorName}, #{deviceId});")
    void insert(PollutionMonitorEntity pollutionMonitorEntity);
}
