package com.shark.aio.data.video.service;

import com.shark.aio.data.video.face.controller.FaceController;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder.Exception;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lbx
 * @date 2023/2/27 - 15:04
 **/

/**
 * 连续截图，覆盖截图
 * @author eguid
 */
public class ImageRecorderService implements Runnable{

    private String input;
    private String output;
    private Integer width;
    private Integer height;
    private String mode;

    public static void main(String[] args) throws java.lang.Exception {
        SimpleDateFormat DataFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String url = "D:\\项目\\AIO\\image\\" +(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(new Date()) ;
        File localPath1 = new File(url + "\\lbx");
        if (!localPath1.exists()) {  // 获得文件目录，判断目录是否存在，不存在就新建一个
            localPath1.mkdirs();
        }
        File localPath2 = new File(url + "\\thg");
        if (!localPath2.exists()) {  // 获得文件目录，判断目录是否存在，不存在就新建一个
            localPath2.mkdirs();
        }

//        record("rtsp://admin:lbx123456@192.168.0.3:554", url + "\\%Y-%m-%d_%H-%M-%S.png", 1280, 720,"0");
        new Thread(new ImageRecorderService("rtsp://admin:lbx123456@192.168.0.3:554", url + "\\lbx\\%Y-%m-%d_%H-%M-%S.png", 1280, 720,"0")).start();
        new Thread(new ImageRecorderService("rtsp://admin:Shark666@nju@192.168.0.2:554", url + "\\thg\\%Y-%m-%d_%H-%M-%S.png", 1280, 720,"0")).start();
        runExample();
    }


    /**
     * 视频快照，连续截图，覆盖截图
     * @author eguid
     * @param input 可以是动态图片(apng,gif等等)，视频文件（mp4,flv,avi等等）,流媒体地址（http-flv,rtmp，rtsp等等）
     * @param output 图片
     * @param width 图像宽度
     * @param height 图像高度
     * @param mode 模式（1-覆盖模式，0-连续截图，根据文件名称模板顺序生成）
     */
//    public static void record(String input,String output,Integer width,Integer height,String mode) throws Exception, org.bytedeco.javacv.FrameGrabber.Exception{
//
//    }



    public ImageRecorderService(String input,String output,Integer width,Integer height,String mode){

        this.input = input;
        this.output = output;
        this.width = width;
        this.height = height;
        this.mode = mode;
    }
    @SneakyThrows
    @Override
    public void run() {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
        //虽然rtsp本身是协议，但是对于ffmpeg来说，rtsp只是个多路复用器/解复用器。可以支持普通的rtp传输，也可以支持RDT传输的Real-RTSP协议
        grabber.setFormat("rtsp");
        //设置要从服务器接受的媒体类型，为空默认支持所有媒体类型，支持的媒体类型：[video，audio，data]
        grabber.setOption("allowed_media_types", "video");
        //设置RTSP传输协议为tcp传输模式
        grabber.setOption("rtsp_transport", "tcp");
        /*
         * rtsp_flags:[filter_src,prefer_tcp,listen]
         * filter_src:仅接受来自协商对等地址和端口的数据包。
         * prefer_tcp:如果TCP可用作RTSP RTP传输，请首先尝试使用TCP进行RTP传输。
         * listen:充当rtsp服务器，监听rtsp连接
         * rtp传输首选使用tcp传输模式
         */
        grabber.setOption("rtsp_flags", "prefer_tcp");
        //socket网络超时时间
        grabber.setOption("stimeout","3000000");
        //设置要缓冲以处理重新排序的数据包的数据包数量
//			grabber.setOption("reorder_queue_size","");
        //设置本地最小的UDP端口，默认为5000端口。
//			grabber.setOption("min_port","5000");
        //设置本地最大的UDP端口，默认为65000端口。
//			grabber.setOption("max_port","65000");

            grabber.start();


        if(width==null||height==null) {
            width=grabber.getImageWidth();
            height=grabber.getImageHeight();
        }

        FFmpegFrameRecorder recorder =new FFmpegFrameRecorder(output,width,height,0);

        //过滤掉日志
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);

        recorder.setFormat("image2");
        if(mode==null) {
            mode="0";//默认连续截图
        }
        recorder.setOption("update", mode);
        recorder.setOption("strftime", "1"); //根据日期生成文件名
        try {
            recorder.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        CanvasFrame canvas = new CanvasFrame("图像预览");// 新建一个窗口
//        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame frame = null;

        // 只抓取图像画面
        for (;(frame = grabber.grabImage()) != null;) {
            try {

//                显示画面
//                canvas.showImage(frame);
                //录制/推流
                recorder.record(frame);
//                Thread.sleep(1000);

            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }

        recorder.close();//close包含stop和release方法。录制文件必须保证最后执行stop()方法，才能保证文件头写入完整，否则文件损坏。
        grabber.close();//close包含stop和release方法

    }







    private static final String PARENT_DIR =
            "D:\\项目\\AIO\\image\\2023-02-28\\lbx";



    public static void runExample() throws java.lang.Exception {

        File parentDir = FileUtils.getFile(PARENT_DIR);

        FileAlterationObserver observer = new FileAlterationObserver(parentDir);
        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                FaceController.callFaceAI(file.getPath());
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
