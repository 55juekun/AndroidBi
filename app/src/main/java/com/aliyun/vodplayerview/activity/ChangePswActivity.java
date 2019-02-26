package com.aliyun.vodplayerview.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bi.R;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePswActivity extends AppCompatActivity {
    private String useridStr="";
    private String oldPswStr="";
    private String newPswStr="";
    private Context mContext;
    private EditText userid;
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
        userid=findViewById(R.id.userid);
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
        useridStr=userid.getText().toString();
//        String pattern="[+]|[\\s]|[%]|[/]|[?]|[#]|[&]|[=]|[\\\\]";
//        Pattern r=Pattern.compile(pattern);
//        Matcher matcher=r.matcher(useridStr);
//        if(matcher.find()){
//            Toast.makeText(mContext,"用户名不得含有特殊字符",Toast.LENGTH_LONG).show();
//            return false;
//        }
//        if(userid.length()<3){
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
        Message msg=new Message();
        try {
            OkHttpClient httpClient=new OkHttpClient();
            Request request=new Request.Builder().url("http://cnrail2.cn/changePsw?userid="+useridStr+"&oldpsw="+MD5(oldPswStr)+"&newpsw="+MD5(newPswStr)).build();
            Response response= httpClient.newCall(request).execute();
            String responseMsg=response.body().string();
            if (responseMsg.equals("1")){
                msg.what=1;
            }else if (responseMsg.equals("0")){
                msg.what=2;
            }else {
                msg.what=3;
            }
            handler.sendMessage(msg);
        } catch (IOException e) {
            msg.what=3;
            handler.sendMessage(msg);
        }
    }
}