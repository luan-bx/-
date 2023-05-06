package com.shark.aio.base.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
import com.shark.aio.data.video.mapper.VideoMapping;
import com.shark.aio.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class IndexScheduled {

    @Autowired
    private VideoMapping videoMapping;



    /**
     * 每3秒执行一次
     */
    @Scheduled(cron = "0/3 * * * * ? ") //我这里暂时不需要运行这条定时任务，所以将注解注释了，朋友们运行时记得放开注释啊
    public void nowOnline() {

        CopyOnWriteArraySet<WebSocket> webSocketSet = WebSocket.getWebSocketSet();

        List<FaceRecordsEntity> faceRecordsEntities = videoMapping.selectFourFaceRecords();
        List<CarRecordsEntity> carRecordsEntities = videoMapping.selectFourCarRecords();
//        log.info("faceRecords:"+faceRecordsEntities.toString());
        JSONArray faceArray = JSON.parseArray(JSON.toJSONString(faceRecordsEntities));
        for (int i=0;i<faceArray.size();i++){
            JSONObject jsonObject = faceArray.getJSONObject(i);
            Date date = new Date(Long.parseLong(jsonObject.get("time").toString()));
            jsonObject.put("time",DateUtil.pageFormat.format(date));
        }

        JSONArray carArray = JSON.parseArray(JSON.toJSONString(carRecordsEntities));
        for (int i=0;i<carArray.size();i++){
            JSONObject jsonObject = carArray.getJSONObject(i);
            Date date = new Date(Long.parseLong(jsonObject.get("time").toString()));
            jsonObject.put("time",DateUtil.pageFormat.format(date));
        }
        HashMap<String,String> map = new HashMap<>();
        map.put("faceData",faceArray.toJSONString());
        map.put("carData",carArray.toJSONString());

        webSocketSet.forEach(c -> {
            try {
                c.sendMessage(JSON.toJSONString(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}