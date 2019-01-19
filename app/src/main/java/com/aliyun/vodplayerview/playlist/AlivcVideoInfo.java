package com.aliyun.vodplayerview.playlist;

import com.aliyun.vodplayerview.constants.PlayParameter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author Mulberry create on 2018/5/17.
 */

public class AlivcVideoInfo {
    @SerializedName("VideoList")
    private VideoList videoList;

    public VideoList getVideoList() {
        return videoList;
    }

    public void setVideoList(VideoList videoList) {
        this.videoList = videoList;
    }


    public static class VideoList {
        @SerializedName("Video")
        private ArrayList<Video> video;

        public ArrayList<Video> getVideo() {
            return video;
        }

        public void setVideo(ArrayList<Video> video) {
            this.video = video;
        }

        @Override
        public String toString() {
            return "ClassPojo [Video = " + video + "]";
        }
    }

    public static class Video {
        @SerializedName("CoverURL")
        private String coverURL;
        @SerializedName("GroupId")
        private String groupId;
        @SerializedName("VideoId")
        private String videoId;
        @SerializedName("Duration")
        private String duration;
        @SerializedName("CreateTime")
        private String createTime;
        @SerializedName("Title")
        private String title;
        @SerializedName("Size")
        private String size;
        @SerializedName("Description")
        private String description;
        @SerializedName("Url")
        private String url;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreationTime() {
            return createTime;
        }

        public void setCreationTime(String creationTime) {
            this.createTime = creationTime;
        }

        public String getCoverURL() {
            return coverURL;
        }

        public void setCoverURL(String coverURL) {
            this.coverURL = coverURL;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public void setVideoUrl(){
            this.url=PlayParameter.VIDEO_HOME_URL +groupId+"/"+videoId;
        }

        public void setPhotoUrl(){
            this.url=PlayParameter.PHOTO_HOME_URL +groupId+"/"+videoId;
        }
        public void setUrl(String url){
            this.url=url;
        }
        public String getUrl(){
            return url;
        }
        @Override
        public String toString() {
            return "ClassPojo [CoverURL = " + coverURL + ", VideoId = " + videoId + ", Duration = " + duration + ", CreateTime = " + createTime
                + ", ModifyTime = , Title = " + title + ", Size = "
                + size + "]";
        }
    }
}
