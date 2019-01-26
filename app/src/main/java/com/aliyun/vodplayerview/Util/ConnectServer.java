package com.aliyun.vodplayerview.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectServer {
    static String userid;
    static String psw;

    public void setUserMsg(String userid, String psw) {
        ConnectServer.userid = userid;
        ConnectServer.psw = psw;
    }
    public void login() throws IOException, JSONException {
        login(userid, psw);
    }

    public void login(String userid, String psw) throws IOException, JSONException {
        OkHttpClient httpClient=new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("userid", userid).add("psw",psw).build();
        Request request=new Request.Builder().url("http://113.54.152.9/login").post(formBody).build();
        Response response= httpClient.newCall(request).execute();
        String responseMsg=response.body().string();
        JSONArray jsonArray=new JSONArray(responseMsg);
        if (jsonArray.length()==0){
            throw new RuntimeException("账号密码错误或权限不足");
        }
        GetTree.markInfos.clear();
        for (int i = 0; i <jsonArray.length() ; i++) {
            JSONObject jsonObject=jsonArray.optJSONObject(i);
            MarkInfo markInfo=new MarkInfo();
            markInfo.setCameraId(jsonObject.getInt("cameraId"));
            markInfo.setLine(jsonObject.getString("line"));
            markInfo.setGroup(jsonObject.getString("group"));
            markInfo.setPoint(jsonObject.getString("point"));
            markInfo.setUseId(jsonObject.getString("useId"));
            markInfo.setAddress(jsonObject.getString("address"));
            markInfo.setProjectName(jsonObject.getString("projectName"));
            markInfo.setStatus(jsonObject.getInt("status"));
            markInfo.setLatitude(jsonObject.getDouble("latitude"));
            markInfo.setLongitude(jsonObject.getDouble("longitude"));
            markInfo.setUrl(jsonObject.getString("url"));
            markInfo.setNote(jsonObject.getString("notes"));
            GetTree.markInfos.add(markInfo);
        }
    }

    /**
     * @Description:
     * @Title: getUser
     * @Param: [username, psw]
     * @return: void
     * @author: 55juekun
     * @Date: 2019/1/25 16:00
     **/
    public User getUser(String userid,String psw){
        OkHttpClient httpClient=new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("userid", userid).add("psw",psw).build();
        Request request=new Request.Builder().url("http://113.54.152.9/getUser").post(formBody).build();
        User user=new User();
        try {
            Response response= httpClient.newCall(request).execute();
            String responseMsg=response.body().string();
            JSONObject jsonObject=new JSONObject(responseMsg);
            user.setUserName(jsonObject.getString("userName"));
            user.setPsw(jsonObject.getString("psw"));
            user.setUserId(jsonObject.getLong("userId"));
            user.setPrivilege(jsonObject.getInt("privilege"));
            user.setLines(jsonObject.getString("lines"));
            user.setGroups(jsonObject.getString("groups"));
            user.setPoints(jsonObject.getString("points"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public String changeMarkinfo(User user,MarkInfo markInfo){
        OkHttpClient httpClient=new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("userId", String.valueOf(user.getUserId()))
                .add("psw",user.getPsw())
                .add("cameraId", String.valueOf(markInfo.getCameraId()))
                .add("line",markInfo.getLine())
                .add("group",markInfo.getGroup())
                .add("point",markInfo.getPoint())
                .add("useId",markInfo.getUseId()).build();
        Request request=new Request.Builder().url("http://113.54.152.9/changeUseID").post(formBody).build();
        String responseMsg="";
        try {
            Response response= httpClient.newCall(request).execute();
            responseMsg=response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMsg;
    }
}
