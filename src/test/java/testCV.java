import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class testCV {

    @Test
    public void testzc() throws FrameGrabber.Exception {
        //ffmpeg -rtsp_transport tcp -i rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4 -c:v libx264 -c:a aac -f flv -an rtmp://localhost:1935/myapp/room
        //String file = "rtsp://192.168.2.38:5554/2";
        String file =  "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4";
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
        grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
        // 一直报错的原因！！！就是因为是 2560 * 1440的太大了。。
        grabber.setImageWidth(960);
        grabber.setImageHeight(540);
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
            opencv_core.Mat mat = converter.convertToMat(frame);
            canvasFrame.showImage(frame);
        }
    }
}
