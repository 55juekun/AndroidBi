package com.aliyun.vodplayerview.Util;


public class MarkInfo {
    private int cameraId;//硬件本身生成的id
    private String line;//线
    private String group;//组
    private String point;//点
    private String useId;//用户自定义的id eg:CZ-折多山隧道-1
    private String address;//经纬度定位出来的地址
    private String projectName;//项目名称
    /**0:green,1:yellow,2:red,3:grey*/
    private int status;//摄像头状态，绿，黄，红，灰分别表示网速好，中，差，无
    //纬度
    private double latitude;
    //经度
    private double longitude;
    private String url;//直播链接
    private String note;//后台管理用户可能会写的备注信息


    private String unknow;//不知道后面会不会加上的其他的东西
    private int year;//摄像头购买时间，可能会用到吧

    public MarkInfo() {
    }
    public MarkInfo(int cameraId, String line, String group, String point, String useId) {
        this.cameraId = cameraId;
        this.line = line;
        this.group = group;
        this.point = point;
        this.useId = useId;
    }
    public MarkInfo(int cameraId, String address, int status, double latitude, double longitude, String url) {
        this.cameraId = cameraId;
        this.address = address;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
    }

    public MarkInfo(int cameraId, String line, String group, String point, String useId, String projectName, String note) {
        this.cameraId = cameraId;
        this.line = line;
        this.group = group;
        this.point = point;
        this.useId = useId;
        this.projectName = projectName;
        this.note = note;
    }

    public MarkInfo(int cameraId, String line, String group, String point, String useId, String address, String projectName, int status, double latitude, double longitude, String url, String note) {
        this.cameraId = cameraId;
        this.line = line;
        this.group = group;
        this.point = point;
        this.useId = useId;
        this.address = address;
        this.projectName = projectName;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.note = note;
    }

    public int getCameraId() {
        return cameraId;
    }

    public String getAddress() {
        return address;
    }

    public int getStatus() {
        return status;
    }

    public String getProjectName() {
        return projectName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getUseId() {
        return useId;
    }

    public void setUseId(String useId) {
        this.useId = useId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUnknow() {
        return unknow;
    }

    public void setUnknow(String unknow) {
        this.unknow = unknow;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "MarkInfo{" +
                "cameraId=" + cameraId +
                ", line='" + line + '\'' +
                ", group='" + group + '\'' +
                ", point='" + point + '\'' +
                ", useId=" + useId +
                ", address='" + address + '\'' +
                ", projectName='" + projectName + '\'' +
                ", status=" + status +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", url='" + url + '\'' +
                ", unknow='" + unknow + '\'' +
                ", year=" + year +
                ", note='" + note + '\'' +
                '}';
    }

    public String getInfoWindowMsg(){
        String InfoWindowMsg="设备编号："+cameraId+"\n摄像头编号：" + useId + "\n地址："+address+"\n"+line+group+point+"\n项目名："+projectName+"\n备注信息："+note;
        return InfoWindowMsg;
    }
}
