package com.shark.aio.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description: 调用命令 【windows&Linux】 @Author: cq @Date: 2022/12/20 @Version: V1.0
 * 用于启动ffmpeg 随之将rtsp 转化为rtmp 返回给Flv.js使用
 */

public class CommandUtil {

    /** 调用linux命令* */
    public String linuxExec(String cmd) {
        System.out.println("执行命令[ " + cmd + "]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null) {
                out.append(line);
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /** 调用windwos命令* */
    public String winExec(String cmd) {
        Runtime runtime = Runtime.getRuntime();
        String command =cmd;
        try {
            Process process = runtime.exec(command);
            new InputStreamReader(process.getInputStream());
            return "成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "请检查摄像头地址";
        }
    }
}
