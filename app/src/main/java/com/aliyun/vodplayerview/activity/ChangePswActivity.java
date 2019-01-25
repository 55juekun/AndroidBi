package com.aliyun.vodplayerview.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bi.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePswActivity extends AppCompatActivity {
    private String usernameStr="";
    private String oldPswStr="";
    private String newPswStr="";
    private Context mContext;
    private EditText username;
    private EditText oldpsw;
    private EditText newpsw;
    private Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_psw);
        mContext=ChangePswActivity.this;
        initView();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(mContext,"修改成功！请输入新密码重新登录",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2:
                    Toast.makeText(mContext,"用户名或密码错误！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext,"检查网络连接或者其他错误！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private void initView() {
        username=findViewById(R.id.username);
        oldpsw=findViewById(R.id.old_password);
        newpsw=findViewById(R.id.new_password);
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePsw();
            }
        });
    }
    public boolean check(){
        usernameStr=username.getText().toString();
//        String pattern="[+]|[\\s]|[%]|[/]|[?]|[#]|[&]|[=]|[\\\\]";
//        Pattern r=Pattern.compile(pattern);
//        Matcher matcher=r.matcher(usernameStr);
//        if(matcher.find()){
//            Toast.makeText(mContext,"用户名不得含有特殊字符",Toast.LENGTH_LONG).show();
//            return false;
//        }
//        if(username.length()<3){
//            Toast.makeText(mContext,"用户名长度需要大于2",Toast.LENGTH_LONG).show();
//            return false;
//        }
        oldPswStr=oldpsw.getText().toString();
        newPswStr=newpsw.getText().toString();
        if (oldPswStr.equals(newPswStr)){
            Toast.makeText(mContext,"新旧密码不能设置成一致的",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public String MD5(String originStr){
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] cipherData = md5.digest(originStr.getBytes());
            StringBuilder builder = new StringBuilder();
            for(byte cipher : cipherData) {
                String toHexStr = Integer.toHexString(cipher & 0xff);
                builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void changePsw(){
        if (check()){
            new Thread(() -> postMsg()).start();
        }
    }

    public void postMsg() {
        URL url;
        Message msg=new Message();
        try {
            url = new URL("http://cnrail2.cn/changePsw?username="+usernameStr+"&oldpsw="+MD5(oldPswStr)+"&newpsw="+MD5(newPswStr));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时时间。
            conn.setConnectTimeout(2000);
            /*注释掉的部分为POST方法的一些相关写法，且传入的参数为 JSONObject jsonObject */
//            conn.setRequestMethod("POST");// 大写
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
//            conn.setRequestProperty("Content-Type", "application/json");//请求的类型 表单数据
//            conn.setDoOutput(true);//设置向服务器写数据。
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.getOutputStream().write(String.valueOf(jsonObject).getBytes());//把数据以流的方式写给服务器。

            conn.connect();
            InputStream input = conn.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(inputReader);
            String inputLine = null;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            inputReader.close();
            input.close();
            conn.disconnect();
            String responseMsg=sb.toString();
            Log.d("55juekun", "postMsg: " + responseMsg);
            if (responseMsg.equals("ok")){
                msg.what=1;
            }else if (responseMsg.equals("error")){
                msg.what=2;
            }else {
                msg.what=3;
            }
            handler.sendMessage(msg);
        } catch (IOException e) {
            Log.d("55juekun", "postMsg: eeeeeeee" );
            msg.what=3;
            handler.sendMessage(msg);
        }
    }
}