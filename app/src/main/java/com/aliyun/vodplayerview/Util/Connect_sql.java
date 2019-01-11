package com.aliyun.vodplayerview.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Connect_sql {
    static String username;
    static String psw;

    public void setUser(String username, String psw) {
        Connect_sql.username = username;
        Connect_sql.psw = psw;
    }
    public void login() throws IOException, JSONException {
        login(username, psw);
    }

    public void login(String username, String psw) throws IOException, JSONException {
        String httpUrl = "http://cnrail2.cn/login?username="+username+"&psw="+psw;
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
            JSONArray jsonArray=new JSONArray(sb.toString());
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
                markInfo.setNote(jsonObject.getString("note"));
                GetTree.markInfos.add(markInfo);
            }
//                Log.d("55juekun", "postMsg: " + sb.toString());
        }else{
            Log.i("TAG", "url is null");
        }
    }
}
