package com.aliyun.vodplayerview.utils.download;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.aliyun.vodplayerview.Util.GetTree;
import com.aliyun.vodplayerview.Util.MarkInfo;

import java.io.File;

public class DownloadMethod {
    //用于确定是哪个摄像头，并获取到对应的信息
    MarkInfo markInfo;
    String markinfo_path;
    public DownloadMethod(Context mContext, int id, String url) {
        MarkInfo[] markInfos=new GetTree().getMarkInfos();
        for (MarkInfo markInfo1 : markInfos) {
            if (markInfo1.getCameraId() == id) {
                markInfo=markInfo1;
                markinfo_path =markInfo.getLine()+"/"+markInfo.getGroup()+"/"+markInfo.getPoint()+"/"+markInfo.getUseId();
                break;
            }
        }
        if (url==null){
            Toast.makeText(mContext,"直播不允许下载",Toast.LENGTH_SHORT).show();
            return;
        }
        if (url.startsWith("/storage/emulated/")){
            Toast.makeText(mContext,"本地视频已经下载了",Toast.LENGTH_SHORT).show();
            return;
        }

        String filename = url.substring(url.lastIndexOf('/') + 1);

        String sd_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CNrail2/"+markinfo_path;
        File file=new File(sd_path);
        if (!file.exists()){
            file.mkdirs();
        }
        File[] files=file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(filename)){
                Toast.makeText(mContext,"该视频已经下载，请在本地视频中查找",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(mContext,"正在下载中",Toast.LENGTH_SHORT).show();
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));

        request.setMimeType(mimeString);
//在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI| DownloadManager.Request.NETWORK_MOBILE);
        request.setVisibleInDownloadsUi(true);

//sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/CNrail2/"+markinfo_path, filename);
        request.setTitle(filename);

        //将下载请求加入下载队列

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//加入下载队列后会给该任务返回一个long型的id，

        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法

        long mTaskId = downloadManager.enqueue(request);
//注册广播接收者，监听下载状态

    }

}
