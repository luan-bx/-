package com.shark.aio.operation.controller;

import com.shark.aio.operation.entity.AIOFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    private static final String videoDir = "F:\\code\\soft\\AIO\\video";
    private static final String dataDir = "F:\\code\\soft\\AIO\\data";
    private static final String logsDir = "F:\\code\\soft\\AIO\\logs";
    private static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    /*public static void main(final String[] args) {
        final long start = System.nanoTime();

        final double total = getTotalSizeOfFilesInDir(new File(fileName))/(1024.0*1024);
        final long end = System.nanoTime();
        System.out.println("Total Size: " + total + "MB");
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }*/

    private long getAllFileInformationInDir(final File file, List<AIOFile> fileList) throws IOException {
        if (file.isFile()){
            fileList.add(getFileInformation(file));

            return file.length();
        }
        final File[] children = file.listFiles();
        long total = 0;
        if(children != null){
            for (final File child : children){
                total +=  getAllFileInformationInDir(child, fileList);
            }
        }
        return total;
    }

    private AIOFile getFileInformation(final File file) throws IOException {
        if (!file.isFile()){
            throw new RuntimeException("不是文件");
        }
        return new AIOFile(file.getName(),file.getAbsolutePath(),file);
    }


    @RequestMapping("/documentManagement")
    public String toFileManagementPage(HttpServletRequest request) throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        List<AIOFile> videoFileList = new ArrayList<>();
        double videoTotalSize = getAllFileInformationInDir(new File(videoDir), videoFileList)/(1024.0*1024.0);
        List<AIOFile> dataFileList = new ArrayList<>();
        double dataTotalSize = getAllFileInformationInDir(new File(dataDir), dataFileList)/(1024.0*1024.0);
        List<AIOFile> logsFileList = new ArrayList<>();
        double logsTotalSize = getAllFileInformationInDir(new File(logsDir), logsFileList)/(1024.0*1024.0);
        request.setAttribute("videoTotalSize",decimalFormat.format(videoTotalSize));
        request.setAttribute("dataTotalSize",decimalFormat.format(dataTotalSize));
        request.setAttribute("logsTotalSize",decimalFormat.format(logsTotalSize));
        request.setAttribute("videoFileList",videoFileList);
        request.setAttribute("dataFileList",dataFileList);
        request.setAttribute("logsFileList",logsFileList);
        return "FileManagement";
    }
}
