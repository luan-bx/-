package com.shark.aio.base.controller;

import com.shark.aio.data.video.entity.CarRecordsEntity;
import com.shark.aio.data.video.entity.FaceRecordsEntity;
import com.shark.aio.data.video.entity.VideoEntity;
import com.shark.aio.data.video.face.controller.FaceController;
import com.shark.aio.data.video.license.controller.LicenseController;
import com.shark.aio.data.video.mapper.VideoMapping;
import com.shark.aio.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

//@Component
@Slf4j
public class InitFFmpeg implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private VideoMapping videoMapping;

    @Resource(name="threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        List<VideoEntity> videos = videoMapping.selectAllVideos();
        for (VideoEntity video : videos){
            //这里是jar包启动就会自动推流
            try {
                ProcessUtil.videoPreview(video.getRtsp(),video.getStream());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            //这里是调用算法的部分
            /*try {
                ImageRecorderService imageRecorderService =new ImageRecorderService(video.getRtsp(),1280,720,"0",video.getMonitorName());
                runExample(imageRecorderService.getPARENT_DIR());
                threadPoolTaskExecutor.execute(imageRecorderService);
//                new Thread(imageRecorderService).start();
//                new VideoRecorderService().startRecordVideo();
            } catch (Exception e) {
                log.error("打开监控"+video.getMonitorName()+"异常！"+e);
            }*/
        }
    }

    public void runExample(String PARENT_DIR) throws java.lang.Exception {

        File parentDir = FileUtils.getFile(PARENT_DIR);

        FileAlterationObserver observer = new FileAlterationObserver(parentDir);
        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                FaceRecordsEntity faceResult = FaceController.callFaceAI(file);
                if (faceResult!=null){
                    try {
                        videoMapping.insertFaceRecord(faceResult);
                    }catch (java.lang.Exception e){
                        e.printStackTrace();
                    }
                }
                CarRecordsEntity carResult = LicenseController.callLicenseAI(file);
                if (carResult!=null){
                    try {
                        videoMapping.insertCarRecord(carResult);
                    }catch (java.lang.Exception e){
                        e.printStackTrace();
                    }
                }
//                System.out.println("算法识别" + file.getName());
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println("File deleted: " + file.getName());
            }

            @Override
            public void onDirectoryCreate(File dir) {
                System.out.println("Directory created: " + dir.getName());
            }

            @Override
            public void onDirectoryDelete(File dir) {
                System.out.println("Directory deleted: " + dir.getName());
            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(500, observer);

        monitor.start();
    }
}
