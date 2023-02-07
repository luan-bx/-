package com.shark.aio.video;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame.Type;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.util.EnumSet;

/**
 * 转流
 *
 * @author eguid
 */
public class JavaCVTest4 {

    public static void main(String[] args)
            throws Exception {

        String inputFile = "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0";

        String outputFile="rtmp://192.168.30.21/live/pushFlow";

        recordPush(inputFile, outputFile,25);
    }
    /**
     * 转流器
     *
     * @param inputFile
     * @param outputFile
     * @throws Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     * @throws InterruptedException
     */
    public static void recordPush(String inputFile, String outputFile, int v_rs) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, InterruptedException {

        FrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        try {
            grabber.start();
        } catch (Exception e) {
            throw e;
        }
        //一个opencv视频帧转换器
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        Frame grabFrame = grabber.grab();
        IplImage grabbedImage = null;
        if (grabFrame != null) {
            System.out.println("取到第一帧");
            grabbedImage = converter.convert(grabFrame);
        } else {
            System.out.println("没有取到第一帧");
        }
        //如果想要保存图片,可以使用 opencv_imgcodecs.cvSaveImage("hello.jpg", grabbedImage);来保存图片

        FrameRecorder recorder;
        try {
            recorder = FrameRecorder.createDefault(outputFile, 1280, 720);
            //如果包含音频，使用这个创建，多出的参数“2”表示音频声道：FrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720,2);
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            throw e;
        }

        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264
        recorder.setFormat("flv");
        recorder.setFrameRate(v_rs);
        recorder.setGopSize(v_rs);

        try {
            recorder.start();
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            System.out.println("录制器启动失败");
            throw e;

        }

        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);

        while (frame.isVisible() && (grabFrame = grabber.grab()) != null) {
            EnumSet<Type> videoOrAudio = grabFrame.getTypes();
            if (videoOrAudio.contains(Type.VIDEO)) {
                frame.showImage(grabFrame);
            }
            if (grabFrame != null) {
                recorder.record(grabFrame);
            }
        }
        recorder.close();
        grabber.close();
    }

}
