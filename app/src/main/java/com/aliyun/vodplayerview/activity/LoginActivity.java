package com.aliyun.vodplayerview.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aliyun.vodplayerview.Util.Connect_sql;
import com.aliyun.vodplayerview.Util.LoadingDialog;
import com.bi.PermissionUtils;
import com.bi.R;

import org.json.JSONException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText username;
    private EditText psw;
    private Button login;
    private TextView register;
    private ToggleButton toggleButton;
    private CheckBox remember;

    private String usernameStr;
    private String pswStr;

    private Context mContext;
    private SharedPreferences sp=null;

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            LoadingDialog.cancelDialogForLoading();
            switch (msg.what){
                case 1:
                    Toast.makeText(mContext,"账号密码错误或权限不足！",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Intent intent1=new Intent(LoginActivity.this,MainActivity.class);
                    intent1.putExtra("groups",(String[]) msg.obj);
                    startActivity(intent1);
                    finish();
                    break;
                case 3:
                    Toast.makeText(mContext,"数据格式错误！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext,"检查网络连接或者其他错误！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext=LoginActivity.this;
        sp=this.getSharedPreferences("userinfo",MODE_PRIVATE);
        initView();
        boolean checkResult = PermissionUtils.checkPermissionsGroup(this, permission);
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }
    }

    private void initView() {
        username=findViewById(R.id.username);
        psw=findViewById(R.id.password);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        toggleButton=findViewById(R.id.psw_on_off);
        remember=findViewById(R.id.remember);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    toggleButton.setBackgroundResource(R.drawable.psw);
                } else {
                    //否则隐藏密码
                    psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    toggleButton.setBackgroundResource(R.drawable.psw2);
                }
            }
        });

        if (sp.getBoolean("checkboxBoolean", false))
        {
            username.setText(sp.getString("uname", null));
            psw.setText(sp.getString("upswd", null));
            remember.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                usernameStr=username.getText().toString();
                pswStr=psw.getText().toString();
                boolean CheckBoxLogin = remember.isChecked();
                if (CheckBoxLogin)
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("uname", usernameStr);
                    editor.putString("upswd", pswStr);
                    editor.putBoolean("checkboxBoolean", true);
                    editor.commit();
                }
                else
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("uname", null);
                    editor.putString("upswd", null);
                    editor.putBoolean("checkboxBoolean", false);
                    editor.commit();
                }
                LoadingDialog.showDialogForLoading(LoginActivity.this,"正在登陆...",false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        try {
//                            new Connect_sql().login(usernameStr,MD5(pswStr));
                            new Connect_sql().setUser(usernameStr,MD5(pswStr));
                            new Connect_sql().login();
                            message.what=2;
                        } catch (IOException e) {
                            message.what=4;
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            message.what=1;
                            message.obj=e;
                            e.printStackTrace();
                        } catch (JSONException e) {
                            message.what=3;
                            e.printStackTrace();
                            message.obj=e;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.register:
                Intent intent2=new Intent(LoginActivity.this,ChangePswActivity.class);
                startActivity(intent2);
                break;
        }
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
//            Log.e( "MD5: ",builder.toString() );
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int PERMISSION_REQUEST_CODE = 1001;


    @Override//申请权限
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                Toast.makeText(this, "权限获取完成", Toast.LENGTH_SHORT).show();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }
    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_name) + "需要访问 \"外部存储器读写权限\",否则会影响视频下载的功能使用, 请到 \"应用信息 -> 权限\" 中设置！");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("暂不设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }

    }
}

