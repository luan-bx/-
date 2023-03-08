package com.shark.aio.data.video;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * 转流
 *
 * @author eguid
 */
public class JavaCVTest4 {
    public static void main(String arg[]) throws Exception {
        System.out.println("开始");
        runExample();
    }

    private static final String PARENT_DIR =
            "D:\\项目\\AIO\\image\\2023-02-28\\lbx";



    public static void runExample() throws Exception {

        File parentDir = FileUtils.getFile(PARENT_DIR);

        FileAlterationObserver observer = new FileAlterationObserver(parentDir);
        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onFileCreate(File file) {
                System.out.println("File created: " + file.getName());
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