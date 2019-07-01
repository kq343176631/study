package com.style.test;

import com.style.study.thread.download.BigFileDownloader;
import com.style.study.thread.util.Debug;
import org.junit.Test;

public class DownloadTest {

    private static final String FILE_DIR = "D:\\My Folder\\Downloads\\";

    @Test
    public void download() throws Exception {
        String[] args = new String[]{"http://yourserver.com/bigfile", "2", "3"};
        final int argc = args.length;
        BigFileDownloader downloader = new BigFileDownloader(args[0]);

        // 下载线程数
        int workerThreadsCount = argc >= 2 ? Integer.valueOf(args[1]) : 2;
        long reportInterval = argc >= 3 ? Integer.valueOf(args[2]) : 2;

        Debug.info("downloading %s%nConfig:worker threads:%s,reportInterval:%s s.",
                args[0], workerThreadsCount, reportInterval);

        downloader.download(workerThreadsCount, reportInterval * 1000);
    }
}
