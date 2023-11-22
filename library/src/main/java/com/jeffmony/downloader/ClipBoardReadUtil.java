package com.jeffmony.downloader;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.utils.ext.intent.IParseIntent;
import com.hss01248.utils.ext.intent.SysIntentShareDispatcherActivity;
import com.hss01248.utils.ext.lifecycle.AppFirstActivityOnCreateListener;
import com.hss01248.utils.ext.lifecycle.FirstActivityCreatedCallback;

/**
 * @Despciption todo
 * @Author hss
 * @Date 22/11/2023 10:53
 * @Version 1.0
 */
public class ClipBoardReadUtil {

    public static  void parseIntent(){
        SysIntentShareDispatcherActivity.addParser(new IParseIntent() {
            @Override
            public boolean parseIntent(Intent intent, AppCompatActivity activity) {
                return false;
            }
        });
    }

    public static void regist() {
        FirstActivityCreatedCallback.addAppFirstActivityOnCreateListener(new AppFirstActivityOnCreateListener() {
            @Override
            public void onForegroundBackgroundChanged(Activity activity, boolean changeToBackground) {
                AppFirstActivityOnCreateListener.super.onForegroundBackgroundChanged(activity, changeToBackground);
                if(!changeToBackground){
                    ThreadUtils.getMainHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //应用获取焦点后才能读取,否则无法读取
                            try{
                                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData primaryClip = clipboardManager.getPrimaryClip();
                                if(primaryClip!=null){
                                    String url = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                                    LogUtils.dTag("ClipboardMonitor", "Clipboard text changed: " + url);
                                    if(TextUtils.isEmpty(url)){
                                        return;
                                    }
                                    if(!url.contains(".m3u8") && !url.contains(".mp4")){
                                        return;

                                    }

                                    /*String finalUrl = url.substring(url.indexOf("https://v.douyin.com/"));
                                    String title = url.substring(0,url.indexOf("https://v.douyin.com/"));
                                    if(finalUrl.equals(SPStaticUtils.getString("video_cli"))){
                                        return;
                                    }*/

                                    AlertDialog dialog =   new AlertDialog.Builder(ActivityUtils.getTopActivity())
                                            .setTitle("视频自动下载")
                                            .setMessage("检测到有m3u8/mp4链接,是否下载?\n\n"+url)
                                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                       DownloadUtil.startDownload("",url);
                                                    }catch (Throwable throwable){
                                                        LogUtils.w(throwable);
                                                    }
                                                }
                                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).create();
                                    dialog.show();
                                }

                            }catch (Throwable e){
                                LogUtils.w(e);
                            }
                        }
                    },500);

                }
            }
        });
    }
}
