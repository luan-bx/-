import org.bytedeco.javacv.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import com.shark.aio.util.ClientDemo;

public class testCV {

    @Test
    public void testzc() throws FrameGrabber.Exception {
        //ffmpeg -rtsp_transport tcp -i rtsp://admin:Shark666@nju@192.168.0.2:554 -vcodec libx264 -r 25 -video_size 1280*720 -preset ultrafast -tune zerolatency -f flv -an rtmp://localhost:1935/myapp/room
        //ffmpeg -rtsp_transport tcp -i rtsp://admin:lbx123456@192.168.0.3:554 -vcodec libx264 -r 25 -video_size 1280*720 -preset ultrafast -tune zerolatency -f flv -an rtmp://localhost:1935/myapp/room
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

<<<<<<< HEAD
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        int nodeCnt = scanner.nextInt();
        String node = scanner.nextLine();
        Node[] nodes = new Node[nodeCnt];
        for (int i=0;i<nodeCnt;i++){
            nodes[i]=new Node(node.charAt(i) );
        }
        String[] parents = scanner.nextLine().split(" ");
        for (int i=0;i<nodeCnt-1;i++){
=======
    @Test
    public void testRtspIsAvailable() throws InterruptedException {
>>>>>>> 2413be3ef9dc74d3ac2c65e3c822720f02f154e3


    }
}

