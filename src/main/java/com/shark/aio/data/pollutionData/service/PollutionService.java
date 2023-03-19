package com.shark.aio.data.pollutionData.service;

import com.shark.aio.alarm.mapper.AlarmMapping;
import com.shark.aio.data.pollutionData.mapper.PollutionMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lbx
 * @date 2023/2/10 - 18:35
 **/
@Service
@Slf4j
public class PollutionService {
    @Autowired
    private PollutionMapping pollutionMapping;
    @Autowired
    private AlarmMapping alarmMapping;









}
