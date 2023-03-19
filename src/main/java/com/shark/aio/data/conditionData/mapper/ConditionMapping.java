package com.shark.aio.data.conditionData.mapper;

import com.shark.aio.data.conditionData.entity.MnEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lbx
 * @date 2023/2/10 - 18:39
 **/
@Mapper
public interface ConditionMapping {

    @Select("select name from monitor")
    public List<String> getAllMonitor();

    /*
     *
     */
    @Select("SELECT * from `monitor_m_n` WHERE mn = #{mn};")
    MnEntity getMnEntityByMN(@Param("mn")String mn);


}
