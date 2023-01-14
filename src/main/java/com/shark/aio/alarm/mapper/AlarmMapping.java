package com.shark.aio.alarm.mapper;

import com.shark.aio.alarm.entity.AlarmEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlarmMapping {

	@Select("select * from alarm_settings")
	public List<AlarmEntity> getAllAlarmSettings();

	@Select("select * from alarm_settings " + "where monitor_class like #{feature} "
			+ "or monitor_value like #{feature} " + "or lower_limit like #{feature} "
			+ "or upper_limit like #{feature} " + "or message like #{feature}")
	public List<AlarmEntity> getAlarmSettingsByFeatures(String feature);

	@Select("select monitor_value from alarm_settings where monitor_value=#{monitorValue}")
	public String getMonitorValue(String monitorValue);

	@Insert("insert into alarm_settings values (null,#{monitorClass},#{monitorValue},#{lowerLimit},#{upperLimit},#{message})")
	public int insertAlarmEntity(AlarmEntity alarmEntity);

	@Delete("delete from alarm_settings where id=#{id}")
	public int deleteAlarmSettingById(int id);

	@Update("UPDATE `alarm_settings` " + "SET " + "`monitor_class` = #{monitorClass}, "
			+ "`monitor_value` = #{monitorValue}, " + "`lower_limit` = #{lowerLimit}, "
			+ "`upper_limit` = #{upperLimit}, " + "`message` = #{message} " + "WHERE `id`=#{id}")
	public int updateAlarmSetting(AlarmEntity alarmEntity);

	@Select("select name from monitor_class")
	public List<String> getAllMonitorClass();

	@Insert("INSERT INTO `monitor_class` SET `name`=#{name}")
	public int insertMonitorClass(String name);

	@Select("select name from pollution")
	public List<String> getAllPollutionName();

	@Insert("INSERT INTO `pollution` SET `name`=#{name}")
	public int insertPollution(String name);
}
