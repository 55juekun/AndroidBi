package com.aliyun.vodplayerview.updateUtils;

/**
 * @ProjectName: cnrail2spring
 * @Package: uestc.cnrail2spring.entity
 * @ClassName: UpdateApk
 * @Description: 一个apk应该包含的信息，用于app更新
 * @Author: 55珏坤
 * @CreateDate: 2019/4/15 8:51
 * @Version: 1.0
 */
public class UpdateApk {
    private int versionCode;
    private String versionName;
    private int updateStatus;//0代表不更新，1代表有版本更新，不需要强制升级，2代表有版本更新，需要强制升级
    private String updateInstruction;
    private String updateUrl;
    private int apkSize;
    private String apkMd5;
    private String apkName;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getUpdateInstruction() {
        return updateInstruction;
    }

    public void setUpdateInstruction(String updateInstruction) {
        this.updateInstruction = updateInstruction;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public int getApkSize() {
        return apkSize;
    }

    public void setApkSize(int apkSize) {
        this.apkSize = apkSize;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    @Override
    public String toString() {
        return "UpdateApk{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", updateStatus=" + updateStatus +
                ", updateInstruction='" + updateInstruction + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", apkSize=" + apkSize +
                ", apkMd5='" + apkMd5 + '\'' +
                ", apkName='" + apkName + '\'' +
                '}';
    }
}
