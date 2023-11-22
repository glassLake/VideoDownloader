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



    public void startDownload(View view) {

        //2G. 下载oom,//todo待解决
        //startDownload("黄金大劫案", "https://ydd.yqk88.com/m3u82/share/585157/628185/20231104/110452/1080/index.m3u8?sign=af286d2564ab3a4f23f16bf436d20b2c&t=1700561643");

        //4M
        //DownloadUtil.startDownload("游戏测试视频4","http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.main.m3u8");

        //2.8M
        //DownloadUtil.startDownload("2.8","https://cf-st.sc-cdn.net/d/1awUye1IRuft6Nr1iIy1o.85.m3u8");

        //80M
        DownloadUtil.startDownload("一集动漫84M","https://vip.lz-cdn3.com/20230811/20991_834743a8/index.m3u8");
    }


}
