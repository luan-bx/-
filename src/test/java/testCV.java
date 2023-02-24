import org.bytedeco.javacv.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class testCV {

    @Test
    public void testzc() throws FrameGrabber.Exception {
        //ffmpeg -rtsp_transport tcp -i rtsp://admin:Shark666@nju@192.168.0.2:554 -vcodec libx264 -r 25 -video_size 1280*720 -preset ultrafast -tune zerolatency -f flv -an rtmp://localhost:1935/myapp/room
        //ffmpeg -rtsp_transport tcp -i rtsp://admin:Shark666@nju@192.168.0.2:554/Streaming/tracks/101?starttime=20230221t110000z -allowed_media_types video -vcodec libx264 -r 25 -ar 22050 -preset ultrafast -tune zerolatency -f flv rtmp://localhost:1935/myapp/mystream
        //ffmpeg -re -i D:\aierhaisen1.mp4 -i D:\aierhaisen2.mp4 -vcodec libx264 -acodec aac -f flv rtmp://localhost:1935/myapp/mystream
        //String file = "rtsp://192.168.2.38:5554/2";
        String file =  "rtsp://admin:Shark666@nju@192.168.0.2:554";
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
        grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
        // 一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
        grabber.setImageWidth(2560);
        grabber.setImageHeight(1440);
        System.out.println("grabber start");
        grabber.start();
        CanvasFrame canvasFrame = new CanvasFrame("sdsaf");
        canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvasFrame.setAlwaysOnTop(true);
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        // OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        Frame frame;
        while (true){
            frame = grabber.grabImage();
            System.out.println(frame.image);
//            opencv_core.Mat mat = converter.convertToMat(frame);
            canvasFrame.showImage(frame);
        }
    }
}
