package com.aliyun.vodplayerview.updateUtils;

import android.content.Context;
import android.graphics.Color;

import com.bi.R;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.xuexiang.xutil.net.JsonUtil;

public class UpdateUtils {
    Context context;
    public UpdateUtils(Context context) {
        this.context = context;
    }
    public void intstall() {
        XUpdate.newBuild(context)
                .themeColor(Color.rgb(255,89,46))
                .topResId(R.mipmap.bg_update_top)
                .updateUrl("http://113.54.154.103/getLatestVersion")
                .updateParser(new CustomUpdateParser())
//                .updatePrompter(new CustomUpdatePrompter(context))
                .update();
    }
    public void reIntstall() {
        XUpdate.newBuild(context)
                .themeColor(Color.rgb(255,89,46))
                .topResId(R.mipmap.bg_update_top)
                .updateUrl("http://113.54.154.103/getLatestVersion")
                .updateParser(new CustomUpdateParser2())
                .update();
    }
    public class CustomUpdateParser implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            UpdateApk result = JsonUtil.fromJson(json, UpdateApk.class);
            if (result != null) {
                //0代表不更新，1代表有版本更新，不需要强制升级，2代表有版本更新，需要强制升级
                boolean hasUpdate=false;
                boolean isforce=false;
                boolean ignorable=true;
                if (result.getUpdateStatus()==1){
                    hasUpdate=true;
                    isforce=false;
                }else if (result.getUpdateStatus()==2){
                    hasUpdate=true;
                    isforce=true;
                    ignorable=false;
                }
                return new UpdateEntity().setHasUpdate(hasUpdate).setForce(isforce).setIsIgnorable(ignorable).setVersionCode(result.getVersionCode()).setVersionName(result.getVersionName()).setUpdateContent(result.getUpdateInstruction()).setDownloadUrl(result.getUpdateUrl()).setSize(result.getApkSize());
            }
            return null;
        }
    }
    public class CustomUpdateParser2 implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            UpdateApk result = JsonUtil.fromJson(json, UpdateApk.class);
            if (result != null) {
                return new UpdateEntity().setHasUpdate(true).setIsIgnorable(false).setVersionCode(result.getVersionCode()).setVersionName(result.getVersionName()+" ").setUpdateContent(result.getUpdateInstruction()).setDownloadUrl(result.getUpdateUrl()).setSize(result.getApkSize());
            }
            return null;
        }
    }
}
