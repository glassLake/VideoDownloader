package com.jeffmony.videodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.github.florent37.runtimepermission.RuntimePermission;
import com.hss.utils.enhance.foregroundservice.CommonProgressService;
import com.jeffmony.downloader.VideoDownloadManager;
import com.jeffmony.downloader.listener.DownloadListener;
import com.jeffmony.downloader.model.VideoTaskItem;
import com.jeffmony.downloader.utils.LogUtils;
import com.jeffmony.m3u8library.VideoProcessManager;
import com.jeffmony.m3u8library.listener.IVideoTransformListener;
import com.jeffmony.videodemo.download.DownloadSettingsActivity;
import com.jeffmony.videodemo.download.VideoDownloadListActivity;
import com.jeffmony.videodemo.merge.VideoMergeActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mDownloadSettingBtn;
    private Button mDownloadListBtn;
    private Button mVideoMergeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RuntimePermission
                .askPermission(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET)
                .ask();
        initViews();
    }

    private void initViews() {
        mDownloadSettingBtn = findViewById(R.id.download_settings_btn);
        mDownloadListBtn = findViewById(R.id.download_list_btn);
        mVideoMergeBtn = findViewById(R.id.video_merge_btn);

        mDownloadSettingBtn.setOnClickListener(this);
        mDownloadListBtn.setOnClickListener(this);
        mVideoMergeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mDownloadSettingBtn) {
            Intent intent = new Intent(this, DownloadSettingsActivity.class);
            startActivity(intent);
        } else if (v == mDownloadListBtn) {
            Intent intent = new Intent(this, VideoDownloadListActivity.class);
            startActivity(intent);
        } else if (v == mVideoMergeBtn) {
            Intent intent = new Intent(this, VideoMergeActivity.class);
            startActivity(intent);
        }
    }

    public static void startDownload(String extTitle,String url){
        // 莫斯科大劫案: https://ydd.yqk88.com/m3u82/share/585157/628185/20231104/110452/1080/index.m3u8?sign=af286d2564ab3a4f23f16bf436d20b2c&t=1700561643
        // 游戏开头视频,4M: http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.main.m3u8
        VideoTaskItem item1 = new VideoTaskItem(url,
                "https://i.loli.net/2021/04/18/WuAUZc85meB6D2Q.jpg",
                "test1", "group-1");
        VideoDownloadManager.getInstance().setShouldM3U8Merged(true);
        VideoDownloadManager.getInstance().setIgnoreAllCertErrors(true);
        ProgressDialog dialog = new ProgressDialog(ActivityUtils.getTopActivity());
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("下载文件: "+extTitle);
        dialog.show();
        VideoDownloadManager.getInstance().setGlobalDownloadListener(new DownloadListener(){
            @Override
            public void onDownloadStart(VideoTaskItem item) {
                super.onDownloadStart(item);
                CommonProgressService.startS("文件下载: "+extTitle, "下载进度:", 98, new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onDownloadProgress(VideoTaskItem item) {
                super.onDownloadProgress(item);
                Log.d("down","onDownloadProgress: "+item.getPercentString());
                CommonProgressService.updateProgress((int) (item.getPercent()*item.getTotalSize()), (int) item.getTotalSize(),"文件下载: "+extTitle,
                        "进度: "+item.getPercentString()+", "+item.getDownloadSizeString()+", "+item.getSpeedString(),98);
                ThreadUtils.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = item.getPercentString()+", "+item1.getDownloadSizeString()+", "+item.getSpeedString();
                        //dialog.setMessage(msg);
                        dialog.setTitle("下载文件: "+" "+extTitle);
                        dialog.setMessage(msg);
                        //title最多两行
                        //dialog.setMax((int) item.getTotalSize());
                       // dialog.setProgress((int) (item.getPercent()*item.getTotalSize()));
                    }
                });
            }

            @Override
            public void onDownloadSuccess(VideoTaskItem item) {
                super.onDownloadSuccess(item);
                Log.i("down","onDownloadSuccess: "+item.getUrl()+"\n"+item.getFilePath());
                if(!new File(item.getFilePath()).exists()){
                    com.blankj.utilcode.util.LogUtils.w("转换失败: "+item.getFilePath());
                    //ideoMerge onTransformFailed err=-12
                    onDownloadError(item);
                    return;
                    ///Download/37d6e8f30b6a302f92652c83176b824a/37d6e8f30b6a302f92652c83176b824a_local.m3u8
                }


                //拷贝到mediastore
                ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Object>() {
                    @Override
                    public Object doInBackground() throws Throwable {
                        copyFileToDownloadsDir(new File(item.getFilePath()),extTitle);
                        return null;
                    }

                    @Override
                    public void onSuccess(Object result) {
                        ThreadUtils.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        CommonProgressService.updateProgress(100, 100,"文件下载: "+extTitle,
                                "进度: "+item.getPercentString(),98);
                        ToastUtils.showLong("文件下载到: Downloads/"+AppUtils.getAppName()+"/"+new File(item.getFilePath()).getName());
                    }

                    @Override
                    public void onFail(Throwable t) {
                        super.onFail(t);
                        ThreadUtils.getMainHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                        ToastUtils.showLong("文件下载失败:"+t.getMessage()+"\n"+item.getUrl());
                    }
                });


            }

            @Override
            public void onDownloadError(VideoTaskItem item) {
                super.onDownloadError(item);
                Log.w("down","onDownloadError: "+item.getErrorCode());
                ThreadUtils.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
                ToastUtils.showLong("文件下载失败:"+item.getErrorCode()+"\n"+item.getUrl());
            }
        });
        VideoDownloadManager.getInstance().startDownload(item1);
    }

    public void startDownload(View view) {
        startDownload("黄金大劫案", "https://ydd.yqk88.com/m3u82/share/585157/628185/20231104/110452/1080/index.m3u8?sign=af286d2564ab3a4f23f16bf436d20b2c&t=1700561643");

        //startDownload("游戏测试视频","http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.main.m3u8");
    }

    private static void copyFileToDownloadsDir(File file,String exTitle) {
        Uri uri = MediaStore.Files.getContentUri("external");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/"+AppUtils.getAppName());
        // 设置文件名称
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, exTitle+"-"+file.getName());
        // 设置文件标题, 一般是删除后缀, 可以不设置
        //contentValues.put(MediaStore.Downloads.TITLE, "hello");
        // uri 表示操作哪个数据库 , contentValues 表示要插入的数据内容
        Uri insert = Utils.getApp().getContentResolver().insert(uri, contentValues);
        // 向 Download/hello/hello.txt 文件中插入数据

        try {
            int sBufferSize = 524288;
            InputStream is = new FileInputStream(file);
            OutputStream os = Utils.getApp().getContentResolver().openOutputStream(insert);
            try {
                os = new BufferedOutputStream(os, sBufferSize);

                double totalSize = is.available();
                int curSize = 0;

                byte[] data = new byte[sBufferSize];
                for (int len; (len = is.read(data)) != -1; ) {
                    os.write(data, 0, len);
                    curSize += len;
                }
                os.flush();
                LogUtils.i("down","拷贝文件到download文件夹: download/"+ AppUtils.getAppName()+"/" + file.getName());
                ToastUtils.showLong("文件下载到download文件夹: download/"+AppUtils.getAppName()+"/" + file.getName());
                file.delete();

            } catch (IOException e) {
                LogUtils.w("down",e.getMessage());

            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.w("down",e.getMessage());
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    LogUtils.w("down",e.getMessage());
                }
            }
        } catch (Exception e) {
            LogUtils.w("down",e.getMessage());
        }
    }
}
