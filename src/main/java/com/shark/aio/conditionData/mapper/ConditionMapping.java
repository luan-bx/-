package com.shark.aio.conditionData.mapper;

import org.apache.ibatis.annotations.Mapper;
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
}
