package com.aliyun.vodplayerview.utils;


import android.util.Log;

import com.aliyun.vodplayerview.playlist.AlivcVideoInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetRecordVideo {
    public static ArrayList<AlivcVideoInfo.Video> getvideos(int id,String[] urls){
        ArrayList<AlivcVideoInfo.Video> videos =new ArrayList<>();
        for (int i = urls.length-1; i >=0 ; i--) {
            if (urls[i].length()<30)
                return videos;
            AlivcVideoInfo.Video video=new AlivcVideoInfo.Video();
            String name=urls[i].split("/")[3].replace("\"","");
            String[] titleAndTime=setTitleAndTime(name);
            video.setDuration(titleAndTime[1]);
            video.setTitle(titleAndTime[0]);
            video.setGroupId(id+"");
            video.setVideoId(name);
            video.setVideoUrl();
            video.setCoverURL(video.getUrl()+"?x-oss-process=video/snapshot,t_1000,m_fast");
            videos.add(video);
        }
        return videos;
    }
    public static ArrayList<AlivcVideoInfo.Video> getphotos(int id,String[] urls){
        ArrayList<AlivcVideoInfo.Video> videos =new ArrayList<>();
        for (int i = urls.length-1; i >=0 ; i--) {
            if (urls[i].length()<20)
                return videos;
            AlivcVideoInfo.Video video=new AlivcVideoInfo.Video();
            String name=urls[i].split("/")[2].replace("\"","");
            video.setTitle(name);
            video.setGroupId(id+"");
            video.setVideoId(name);
            video.setPhotoUrl();
            video.setCoverURL(video.getUrl() +"?x-oss-process=style/small");
            videos.add(video);
        }
        return videos;
    }

    public static String[] setTitleAndTime(String name){
        String startTime=name.split("_")[0];
        String[] startTimes=startTime.split("-");
        String endTime=name.split("_")[1];
        String[] endTimes=endTime.split("-");
        StringBuffer sb=new StringBuffer();

        for (int j = 3; j <6 ; j++) {
            if (j!=5){
                sb.append(startTimes[j]+":");
            }else {
                sb.append(startTimes[j]+"-");
            }
        }
        for (int j = 3; j <6 ; j++) {
            if (j!=5){
                sb.append(endTimes[j]+":");
            }else {
                sb.append(endTimes[j]);
            }
        }
        sb.append("\n"+startTimes[0]+".");
        sb.append(startTimes[1]+".");
        sb.append(startTimes[2]+"  ");

        int time=0;
        int hour=0,minute=0,second=0;
        hour += Integer.parseInt(endTimes[3])-Integer.parseInt(startTimes[3]);
        if (hour<0) {
            hour += 24;
        }
        minute += Integer.parseInt(endTimes[4])-Integer.parseInt(startTimes[4]);
        if (minute<0){
            minute += 60;
            hour -= 1;
        }
        second += Integer.parseInt(endTimes[5].substring(0,2))-Integer.parseInt(startTimes[5]);
        if (second<0){
            second += 60;
            minute -= 1;
        }
        time=hour*3600+minute*60+second;

        String titleAndTime[]=new String[2];
        titleAndTime[0]=sb.toString();
        titleAndTime[1]=time+"";
        return titleAndTime;
    }

    public static String[] getVideosUrl(int id) throws IOException {
        String httpUrl = "http://cnrail2.cn/getRecord/"+id;
        return getUrl(httpUrl);
    }
    public static String[] getPhotosUrl(int id) throws IOException {
        String httpUrl = "http://cnrail2.cn/getPicture/"+id;
        return getUrl(httpUrl);
    }
    public static String[] getUrl(String  httpUrl) throws IOException {
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            // 打开连接，此处只是创建一个实例，并没有真正的连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(2000);
            // 连接
            urlConn.connect();
            InputStream input = urlConn.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(inputReader);
            String inputLine = null;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine).append("\n");
            }
            reader.close();
            inputReader.close();
            input.close();
            urlConn.disconnect();
            String videoUrls=sb.toString();
            videoUrls=videoUrls.substring(1,videoUrls.length()-2);
            return videoUrls.split(",");
        }else{
            Log.i("TAG", "url is null");
        }
        return new String[]{};
    }

    /**
     * 发送拍照指令至服务器
     */
    public static void takeRemotePhoto(int id) throws IOException {
        String httpUrl = "http://cnrail2.cn/captureNow/"+id;
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            // 打开连接，此处只是创建一个实例，并没有真正的连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(2000);
            // 连接
            urlConn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
        }
    }

}
