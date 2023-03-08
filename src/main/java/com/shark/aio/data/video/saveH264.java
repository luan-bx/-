//package com.shark.aio.video;
//
//import lombok.extern.slf4j.Slf4j;
//import org.bytedeco.ffmpeg.global.avcodec;
//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.FFmpegFrameRecorder;
//import org.bytedeco.javacv.Frame;
//import org.bytedeco.javacv.FrameRecorder;
//
//import java.io.IOException;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//
///**
// * @author lbx
// * @date 2023/2/21 - 17:51
// **/
//@Slf4j
//public class saveH264 {
//    private final PipedInputStream pis = new PipedInputStream();
//    private final PipedOutputStream pos = new PipedOutputStream();
//    private String deviceId;
//    private String channel;
//    private volatile boolean running = true;
//    private boolean flag = false;
//    private String rtspBaseAddress = "rtsp://127.0.0.1:554/";
//    private String rtmpBaseAddress = "rtmp://127.0.0.1:1935/hls/";
//
//    public void RtmpPusher(String deviceId, String channel) {
//        this.deviceId = deviceId;
//        this.channel = channel;
//        try {
//            // 使用管道流实现线程间通信
//            pos.connect(pis);
//        } catch (IOException e) {
//            log.info("pos connect pis error.{}", e.getMessage());
//        }
//    }
//
//    /**
//     * 将视频流写入管道
//     */
//    public void onMediaStream(byte[] data) {
//        try {
//            if (!flag) {
//                log.info("receive data...");
//                flag = true;
//            }
//            pos.write(data);
//        } catch (IOException e) {
//            log.error("write video data error.{}", e.getMessage());
//            try {
//                pos.close();
//            } catch (IOException ex) {
//                log.error("pos close error.{}", ex.getMessage());
//            }
//        }
//    }
//    /**
//     * 转流器, 指定format
//     */
//    public void recordPushWithFormat() {
//        long startTime = 0;
//        long videoTS;
//
//        try {
//            log.info("grabber start ... {} > {}", deviceId, channel);
//            // 从管道流中读取视频流
//            // maximumSize 设置为0，不设置会阻塞
//            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(pis, 0);
//
//            //此配置以减少启动时间！若不设置，启动最起码半分钟；
//            //类似一个缓冲区，用来检测流的正确性，值越小，启动越快
//            grabber.setOption("probesize", "1024*20");
//            grabber.setFrameRate(30);
//            grabber.setVideoBitrate(2000000);
//            //阻塞式，直到通道有数据
//            grabber.start();
//
//            log.info("grabber start suc. and start recorder ... {} > {}", deviceId, channel);
//
//            // 获取推流地址对应的recorder，用于录制视频
//            // rtmp对应format是flv
//            // rtsp对应format是rtsp
//            FrameRecorder rtspRecorder = getRecorder(rtspBaseAddress + deviceId + Constant.SEPARATOR + channel, "rtsp");
//            FrameRecorder rtmpRecorder = getRecorder(rtmpBaseAddress + deviceId + Constant.SEPARATOR + channel, "flv");
//
//            rtspRecorder.start();
//            rtmpRecorder.start();
//
//            Frame grabframe;
//
//            // 从视频流中捕获帧以录制视频
//            while (running && (grabframe = grabber.grab()) != null) {
//
//                if (startTime == 0) {
//                    startTime = System.currentTimeMillis();
//                }
//
//                videoTS = 1000 * (System.currentTimeMillis() - startTime);
//
//                // 推流到rtsp server
//                if (videoTS > rtspRecorder.getTimestamp()) {
//                    rtspRecorder.setTimestamp(videoTS);
//                }
//                rtspRecorder.record(grabframe);
//                // 推流到rtmp server
//                if (videoTS > rtmpRecorder.getTimestamp()) {
//                    rtmpRecorder.setTimestamp(videoTS);
//                }
//                rtmpRecorder.record(grabframe);
//
//            }
//        } catch (Exception e) {
//            log.error("record push error.", e);
//        } finally {
//            try {
//                pis.close();
//            } catch (IOException e) {
//                log.error("pis close error.{}", e.getMessage());
//            }
//        }
//
//    }
//
//    private FrameRecorder getRecorder(String address, String format) {
//        FrameRecorder recorder = new FFmpegFrameRecorder(address, 720, 480);
//        recorder.setInterleaved(true);
//        recorder.setVideoOption("tune", "zerolatency");
//        recorder.setVideoOption("preset", "ultrafast");
//        recorder.setVideoOption("crf", "28");
//        recorder.setVideoBitrate(2000000);//码率 越大越清晰
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        recorder.setFormat(format);//flv rtsp
//        recorder.setFrameRate(30);//帧率 30fps 每秒包含的帧数 24-30越大越流畅
//        recorder.setGopSize(60);//30*2 每60帧存在一个关键帧
//        return recorder;
//    }
//
//    public void stopPush() {
//        this.running = false;
//        this.interrupt();
//    }
//
//    public boolean isRunning() {
//        return this.running;
//    }
//
//    @Override
//    public void run() {
//        recordPushWithFormat();
//    }
//
//}
