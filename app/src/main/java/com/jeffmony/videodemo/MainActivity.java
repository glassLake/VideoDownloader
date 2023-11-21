package com.jeffmony.videodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.florent37.runtimepermission.RuntimePermission;
import com.jeffmony.downloader.VideoDownloadManager;
import com.jeffmony.downloader.listener.DownloadListener;
import com.jeffmony.downloader.model.VideoTaskItem;
import com.jeffmony.downloader.utils.LogUtils;
import com.jeffmony.m3u8library.VideoProcessManager;
import com.jeffmony.m3u8library.listener.IVideoTransformListener;
import com.jeffmony.videodemo.download.DownloadSettingsActivity;
import com.jeffmony.videodemo.download.VideoDownloadListActivity;
import com.jeffmony.videodemo.merge.VideoMergeActivity;

import java.io.File;
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
        VideoTaskItem item1 = new VideoTaskItem("https://v3.cdtlas.com/20221119/zimPf0a2/1100kb/hls/index.m3u8",
                "https://i.loli.net/2021/04/18/WuAUZc85meB6D2Q.jpg",
                "test1", "group-1");
        VideoDownloadManager.getInstance().setShouldM3U8Merged(true);
        VideoDownloadManager.getInstance().setIgnoreAllCertErrors(true);
        VideoDownloadManager.getInstance().setGlobalDownloadListener(new DownloadListener(){
                    @Override
                    public void onDownloadProgress(VideoTaskItem item) {
                        super.onDownloadProgress(item);
                        Log.d("down","onDownloadProgress: "+item.getPercentString());
                    }

            @Override
            public void onDownloadSuccess(VideoTaskItem item) {
                super.onDownloadSuccess(item);
                Log.i("down","onDownloadSuccess: "+item.getUrl()+"\n"+item.getFilePath());
                /*String outputPath = new File(item.getFilePath())

                LogUtils.i("onDownloadSuccess", "inputPath="+item.getFilePath()+", outputPath="+outputPath);
                VideoProcessManager.getInstance().transformM3U8ToMp4(item.getFilePath(), outputPath, new IVideoTransformListener() {

                    @SuppressLint("StringFormatInvalid")
                    @Override
                    public void onTransformProgress(float progress) {
                        LogUtils.i("onTransformProgress", "onTransformProgress progress="+progress);
                        DecimalFormat format = new DecimalFormat(".00");
                       // mTransformProgressTxt.setText(String.format(getResources().getString(R.string.convert_progress), format.format(progress)));
                    }

                    @Override
                    public void onTransformFinished() {
                        LogUtils.i("onTransformFinished", "onTransformFinished");
                    }

                    @Override
                    public void onTransformFailed(int err) {
                        LogUtils.i("TAG", "onTransformFailed, err="+err);
                    }
                });*/
            }

            @Override
            public void onDownloadError(VideoTaskItem item) {
                super.onDownloadError(item);
                Log.w("down","onDownloadError: "+item.getErrorCode());
            }
        });
        VideoDownloadManager.getInstance().startDownload(item1);
    }
}
