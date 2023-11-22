package com.jeffmony.videodemo;

import android.app.Application;
import android.content.Context;

import com.hss01248.dokit.IDokitConfig;
import com.hss01248.dokit.MyDokit;
import com.jeffmony.downloader.common.DownloadConstants;
import com.jeffmony.downloader.VideoDownloadConfig;
import com.jeffmony.downloader.VideoDownloadManager;
import com.jeffmony.downloader.utils.VideoStorageUtils;

import java.io.File;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File file = VideoStorageUtils.getVideoCacheDir(this);
        if (!file.exists()) {
            file.mkdir();
        }
        VideoDownloadConfig config = new VideoDownloadManager.Build(this)
                .setCacheRoot(file.getAbsolutePath())
                .setTimeOut(DownloadConstants.READ_TIMEOUT, DownloadConstants.CONN_TIMEOUT)
                .setConcurrentCount(1)
                .setIgnoreCertErrors(true)
                .setShouldM3U8Merged(false)
                .buildConfig();
        VideoDownloadManager.getInstance().initConfig(config);

        MyDokit.setConfig(new IDokitConfig() {
            @Override
            public void loadUrl(Context context, String url) {
                if(url.contains(".m3u8") || url.contains(".mp4")){
                    DownloadUtil.startDownload("",url);
                }
            }

            @Override
            public void report(Object o) {

            }
        });
    }
}
