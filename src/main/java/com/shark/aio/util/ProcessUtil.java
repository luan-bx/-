package com.shark.aio.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

@Slf4j
public class ProcessUtil {

    /** Windows系统* */
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    /** Linux系统* */
    public static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");

    public static int getProcessIdInWindows(Process p) throws NoSuchFieldException, IllegalAccessException {
        Field f = p.getClass().getDeclaredField("handle");
        f.setAccessible(true);
        long handle = f.getLong(p);
        Kernel32 kernel = Kernel32.INSTANCE;
        WinNT.HANDLE winntHandle = new WinNT.HANDLE();
        winntHandle.setPointer(Pointer.createConstant(handle));
        int pid = kernel.GetProcessId(winntHandle);
        return pid;
    }

    public static int getProcessIdInLinux(Process p) throws NoSuchFieldException, IllegalAccessException {
        Field pid = p.getClass().getDeclaredField("pid");
        pid.setAccessible(true);
        return pid.getInt(p);
    }

    /** 视频播放指令执行 rtsp地址【源播放地址】通过ffmpeg程序进行转化成rtmp，将地址传给flv.js进行播放 */
    public static int videoPreview(String videoPath, String stream) throws NoSuchFieldException, IllegalAccessException {
        CommandUtil commandUtil = new CommandUtil();
        int pid = -1;
        /*如果是winodws系统**/
        if (IS_WINDOWS) {
            String cmd ="cmd /k start ffmpeg -rtsp_transport tcp -i"
                    + " "
                    + videoPath
                    + " -vcodec libx264 -r 25 -preset ultrafast -tune zerolatency -f flv -an rtmp://localhost:1935/myapp/"
                    + stream;
            log.info(cmd);
            pid = commandUtil.winExec(cmd);
        }
        /*如果是Linux系统**/
        if (IS_LINUX){
            System.out.println("linux");
            String cmd = "ffmpeg -f rtsp -rtsp_transport tcp -i '"
                    + ""
                    + videoPath
                    + "'"
                    + " "
                    + "-vcodec libx264 -r 25 -preset ultrafast -tune zerolatency -f flv -an rtmp://localhost:1935/myapp/"
                    + stream;
            pid = commandUtil.linuxExec(cmd);
        }
        if(pid>0) {
            log.info("启动推流进程成功，进程：" + pid);
        }else{
            log.warn("启动推流进程error");
        }
        return pid;
    }

    /** 关闭ffmpeg进程*/
    public static String close(int pid) {
        //这里需要停止特定进程
        log.info("停止推流进程，进程"+pid);
        String cmd = "taskkill /f /t /pid " + pid;
        try {
            log.info("执行命令:"+cmd);
            Runtime.getRuntime().exec(cmd);
            log.info("成功停止进程："+pid);
            return "已成功停止";
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.warn("停止进程"+pid+"失败！");
        return "杀死进程失败";
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
