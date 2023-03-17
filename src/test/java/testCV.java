import org.bytedeco.javacv.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.Scanner;

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

        }

    }

    class Tank{
        public int x;
        public int y;
        public char direction;

        public Tank(int x,int y,char direction){
            this.x=x;
            this.y=y;
            this.direction=direction;
        }

    }
    public void tankGame(){
        Scanner scanner = new Scanner(System.in);
        String D_CMD = scanner.nextLine();
        String W_CMD = scanner.nextLine();
        int[][] map = new int[16][16];//地图，0代表未被占领，1代表D占领，2代表W占领
        map[0][0]=1;
        map[15][15]=2;
        Tank D = new Tank(0,0,'R');
        for (int i=0;i<256;i++){
            char d = D_CMD.charAt(i);
            char w = W_CMD.charAt(i);

        }
    }

    public void nums(){
        Scanner scanner = new Scanner(System.in);
        String number = scanner.nextLine();
        int[] dp = new int[number.length()+1];
        dp[0]=0;
        dp[1]=0;
        boolean flag = false;
        for (int i=2;i<dp.length;i++){
            if (number.charAt(i-1)==number.charAt(i-2)&&!flag){
                dp[i]=dp[i-1]+1;
                flag = true;
            }else{
                dp[i]=dp[i-1];
                flag=false;
            }
        }
        System.out.println(dp[dp.length-1]);
    }

    public void star(){
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        int[] s = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        Arrays.sort(s);
        int[] t = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        Arrays.sort(t);
        int begin = Arrays.stream(s).min().getAsInt();
        int end = Arrays.stream(t).max().getAsInt();
        int[] time = new int[end+1];
        int x = 0;
        int y = 0;
        /*for (int time = begin;time<=end;time++){
            int count=0;
            for (int i=0;i<n;i++){
                if (time>=s[i]&&time<=t[i]){
                    count ++;
                }
            }
            if (count>x){
                x=count;
                y=1;
            }else if(count==x){
                y++;
            }
        }*/
        for (int i=0;i<s.length;i++){
            time[s[i]]++;
            time[t[i]]--;
        }
        for (int i=0;i<time.length;i++){
            int count = 0;
            if (time[i]>0){
                count += time[i];
                x=count;
            }
        }

        System.out.println(x+" "+y);

    }
}

class Node{
    public char color;
    public Node[] children;

    public int testChildren(){
        int count = 0;
        for (int i=0;i<children.length;i++){
            if (children[i]!=null){
                count = children[i].color=='R'?count+1:count-1;
            }
        }
        return count;
    }

    public Node(char color) {
        this.color = color;
    }
}
