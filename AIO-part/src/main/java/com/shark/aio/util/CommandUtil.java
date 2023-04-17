package com.shark.aio.util;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description: 调用命令 【windows&Linux】 @Author: cq @Date: 2022/12/20 @Version: V1.0
 * 用于启动ffmpeg 随之将rtsp 转化为rtmp 返回给Flv.js使用
 */

public class CommandUtil {

    /** 调用linux命令* */
    public int linuxExec(String cmd) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("执行命令[ " + cmd + "]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            int pid = ProcessUtil.getProcessIdInLinux(process);
            /*String line;
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
            process.destroy();*/
            return pid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /** 调用windwos命令* */
    public int winExec(String cmd) throws NoSuchFieldException, IllegalAccessException {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(cmd);
            int pid = ProcessUtil.getProcessIdInWindows(process);
            new InputStreamReader(process.getInputStream());
            return pid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
