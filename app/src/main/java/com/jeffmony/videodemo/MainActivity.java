package com.jeffmony.videodemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.github.florent37.runtimepermission.RuntimePermission;
import com.jeffmony.downloader.DownloadUtil;
import com.jeffmony.videodemo.download.DownloadSettingsActivity;
import com.jeffmony.videodemo.download.VideoDownloadListActivity;
import com.jeffmony.videodemo.merge.VideoMergeActivity;

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
        parseIntent2();
    }

    private void parseIntent2() {

       String title =  getIntent().getStringExtra("title");
       String url = getIntent().getStringExtra("url");
        LogUtils.d(title,url);
       if(TextUtils.isEmpty(url)){
           return;
       }
       DownloadUtil.startDownload(title,url);
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




        //80M
        DownloadUtil.startDownload("一集动漫84M","https://vip.lz-cdn3.com/20230811/20991_834743a8/index.m3u8");
    }


    public void startDownload404(View view) {
        DownloadUtil.startDownload("404","https://47.242.4.90/前任小叔他又宠又撩（89集）/第2集.mp4");
    }

    public void startDownload2g(View view) {
        //2G. 下载oom,//todo待解决
        DownloadUtil.startDownload("黄金大劫案", "https://ydd.yqk88.com/m3u82/share/585157/628185/20231104/110452/1080/index.m3u8?sign=af286d2564ab3a4f23f16bf436d20b2c&t=1700561643");

    }

    public void startDownload4m(View view) {
        //4M
        DownloadUtil.startDownload("游戏测试视频4","http://videoconverter.vivo.com.cn/201706/655_1498479540118.mp4.main.m3u8");

        //2.8M
        //DownloadUtil.startDownload("2.8","https://cf-st.sc-cdn.net/d/1awUye1IRuft6Nr1iIy1o.85.m3u8");
    }
}
