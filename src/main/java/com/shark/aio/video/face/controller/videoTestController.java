package com.shark.aio.video.face.controller;

import com.shark.aio.util.CommandUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Description: 视频输出 @Author: cq @Date: 2022/11/30 @Version: V1.0
 */

@RestController
@RequestMapping("/video/videos")
@AllArgsConstructor
@Slf4j
public class videoTestController {
    /** Windows系统* */
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    /** Linux系统* */
    private static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("Linux");


    /** 视频播放指令执行 rtsp地址【源播放地址】通过ffmpeg程序进行转化成rtmp，将地址传给flv.js进行播放 */
    @GetMapping("/videoStart")
    public void videoPreview(@RequestParam(name = "videoPath", required = false) String videoPath) {
        CommandUtil commandUtil = new CommandUtil();
        /*如果是winodws系统**/
        if (IS_WINDOWS) {
            String cmd ="cmd /k start ffmpeg -rtsp_transport tcp -i"
                    + " "
                    + videoPath
                    + " "
                    + "-c:v libx264 -c:a aac -f flv -an rtmp://xxx.xxx.xxx.xxx:1935/myapp/room";
            commandUtil.winExec(cmd);
        }
        /*如果是Linux系统**/
        if (IS_LINUX){
            System.out.println("linux");
            String cmd = "ffmpeg -f rtsp -rtsp_transport tcp -i '"
                    + ""
                    + videoPath
                    + "'"
                    + ""
                    + " -codec copy -f flv -an 'rtmp://xxx.xxx.xxx.xxx:1935/myapp/room'";
            commandUtil.linuxExec(cmd);
        }
    }

    /** 关闭ffmpeg.exe程序 */
    @GetMapping("/videoClose")
    public String close() {
        closeHistoryProgram("ffmpeg.exe");
        return "已成功停止";
    }

    public void closeHistoryProgram(String processName) {
        String cmd = "taskkill /f /t /im " + processName;
        try {
            // exec执行cmd命令
            Process process = Runtime.getRuntime().exec(cmd);
            // 获取CMD命令结果的输出流
            InputStream fis = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(fis, "GBK");
            // 使用缓冲器读取
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            // 全部读取完成为止，一行一行读取
            while ((line = br.readLine()) != null) {
                // 输出读取的内容
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
