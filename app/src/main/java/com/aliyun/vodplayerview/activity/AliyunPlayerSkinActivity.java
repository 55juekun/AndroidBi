package com.aliyun.vodplayerview.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.Util.ConnectServer;
import com.aliyun.vodplayerview.Util.Constant;
import com.aliyun.vodplayerview.Util.GetLiveUrl;
import com.aliyun.vodplayerview.Util.GetTree;
import com.aliyun.vodplayerview.Util.LoadingDialog;
import com.aliyun.vodplayerview.Util.MarkInfo;
import com.aliyun.vodplayerview.Util.ScreenRecordService;
import com.aliyun.vodplayerview.Util.User;
import com.aliyun.vodplayerview.constants.PlayParameter;
import com.aliyun.vodplayerview.playlist.AlivcPlayListAdapter;
import com.aliyun.vodplayerview.playlist.AlivcVideoInfo;
import com.aliyun.vodplayerview.utils.GetRecordVideo;
import com.aliyun.vodplayerview.utils.ScreenUtils;
import com.aliyun.vodplayerview.utils.download.DownloadMethod;
import com.aliyun.vodplayerview.view.choice.AlivcShowMoreDialog;
import com.aliyun.vodplayerview.view.control.ControlView;
import com.aliyun.vodplayerview.view.more.AliyunShowMoreValue;
import com.aliyun.vodplayerview.view.more.ShowMoreView;
import com.aliyun.vodplayerview.view.more.SpeedValue;
import com.aliyun.vodplayerview.view.tipsview.ErrorInfo;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 播放器和播放列表界面 Created by Mulberry on 2018/4/9.
 */
public class AliyunPlayerSkinActivity extends AppCompatActivity {

    private AlivcShowMoreDialog showMoreDialog;
    private boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
            || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
            || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
            || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
            || "hermes".equalsIgnoreCase(Build.DEVICE)
            || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
            || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;
    }

    private AliyunScreenMode currentScreenMode = AliyunScreenMode.Small;
    //在线视频项
    private TextView tvVideoList;
    private ImageView ivVideoList;
    private RecyclerView recyclerView;
    private LinearLayout llVideoList;
    //拍照列表项
    private TextView tvOnlinePhoto;
    private ImageView ivOnlinePhoto;
    //离线视频项
    private TextView tvDownloadVideo;
    private ImageView ivDownloadVideo;
    //列表下方复选框相关组件
    private RelativeLayout relativeEnd;
    private CheckBox cb_all;
    private TextView tvOK;
    private TextView tvDelete;
    private TextView tvDownloadEnd;
    private Button tvReturnLiveOrPhoto;//返回现场监控或者拍照
    private ImageButton record_button;//用于录制视频时显示，从而让用户了解到当前正在录制
    private AliyunVodPlayerView mAliyunVodPlayerView = null;

    private AlivcPlayListAdapter alivcPlayListAdapter;

    private ArrayList<AlivcVideoInfo.Video> alivcVideoInfos;
    private ArrayList<AlivcVideoInfo.Video> ossPhotoInfos=new ArrayList<>();//oss照片列表
    private ArrayList<AlivcVideoInfo.Video> ossVideoInfos=new ArrayList<>();//oss视频列表
    private ArrayList<AlivcVideoInfo.Video> localVideoInfos=new ArrayList<>();// 本地视频列表
    private ErrorInfo currentError = ErrorInfo.Normal;
    private static final String DEFAULT_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    /**
     * get StsToken stats
     */
    private HashSet checkedItemUrl=new HashSet();

    private static String[] PERMISSIONS_STORAGE = {
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE   = 103;

    /**
     * 当前tab
     */
    private int currentTab = TAB_VIDEO_LIST;
    private static final int TAB_ONLINE_PHOTO_LIST = 1;
    private static final int TAB_VIDEO_LIST = 2;
    private static final int TAB_DOWNLOAD_LIST = 3;
    public static int id=0;
    public static String currentPlayUrl;

    boolean isRecord=false;
    int mScreenWidth;
    int mScreenHeight;
    int mScreenDensity;
    //用于确定是哪个摄像头，并获取到对应的信息
    MarkInfo markInfo;
    String markinfo_path;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (isStrangePhone()) {
            //            setTheme(R.style.ActTheme);
        } else {
            setTheme(R.style.NoActionTheme);
        }

        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        id=intent.getExtras().getInt("id");
        MarkInfo[] markInfos=new GetTree().getMarkInfos();
        for (MarkInfo markInfo1 : markInfos) {
            if (markInfo1.getCameraId() == id) {
                markInfo=markInfo1;
                markinfo_path =markInfo.getLine()+"/"+markInfo.getGroup()+"/"+markInfo.getPoint()+"/"+markInfo.getUseId();
                break;
            }
        }
        setContentView(R.layout.alivc_player_layout_skin);

        initAliyunPlayerView();
        initCheckALLView();
        initVideoListView();
        addRecordVideo(id);
        initOnlinePhotoView();
        initDownloadView();
        PlayLive();

//        if (ContextCompat.checkSelfPermission(AliyunPlayerSkinActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
//        }
//
//        if (ContextCompat.checkSelfPermission(AliyunPlayerSkinActivity.this, Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
//        }

        startScreenRecord();
        getScreenBaseInfo();
    }

    private void initCheckALLView() {
        relativeEnd=findViewById(R.id.rl_end);
        cb_all=findViewById(R.id.cb_check);
        tvOK =findViewById(R.id.tv_ok);
        tvDelete =findViewById(R.id.tv_delete);
        tvDownloadEnd =findViewById(R.id.tv_download_end);

        cb_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_all.isChecked()){
                    checkedItemUrl.clear();
                    for (AlivcVideoInfo.Video video:alivcVideoInfos) {
                        checkedItemUrl.add(video.getUrl());
                    }
                    cb_all.setText("全不选");
                    alivcPlayListAdapter.setCHECKALL();
                }else {
                    checkedItemUrl.clear();
                    cb_all.setText("全选");
                    alivcPlayListAdapter.setCHECKNONE();
                }
            }
        });
        tvOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relativeEnd.getVisibility()==View.VISIBLE){
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                }
            }
        });
        tvDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedItemUrl!=null) {
                    Iterator it=checkedItemUrl.iterator();
                    while (it.hasNext()){
                        String url=(String) it.next();
                        File file =new File(url);
                        file.delete();
                    }
                    checkedItemUrl.clear();
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                    tvDownloadVideo.callOnClick();
                }
            }
        });
        tvDownloadEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedItemUrl!=null) {
                    Iterator it=checkedItemUrl.iterator();
                    while (it.hasNext()){
                        String url=(String) it.next();
                        new DownloadMethod(getApplicationContext(), id, url);
                    }
                    checkedItemUrl.clear();
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                }
            }
        });
    }

    private void startRecord(){
        Message msg=new Message();
        msg.what=2;
        msg.obj=null;
        handler1.sendMessage(msg);
    }

    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setPlaySource();
        if(requestCode == 1000){
            if(resultCode == RESULT_OK){                //获得录屏权限，启动Service进行录制
                Intent intent=new Intent(AliyunPlayerSkinActivity.this,ScreenRecordService.class);
                intent.putExtra("resultCode",RESULT_OK);
                intent.putExtra("resultData",data);
                intent.putExtra("mScreenWidth",mScreenWidth);
                intent.putExtra("mScreenHeight",mScreenHeight);
                intent.putExtra("mScreenDensity",mScreenDensity);
                intent.putExtra("mCameraPath",markinfo_path);
                Message msg=new Message();
                msg.what=1;
                msg.obj=intent;
                handler1.sendMessage(msg);
            }else{
                Toast.makeText(this,"取消该项会导致无法录屏，请重启应用",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void getScreenBaseInfo() {        //A structure describing general information about a display, such as its size, density, and font scaling.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;
    }

    private void startScreenRecord() {
        MediaProjectionManager mediaProjectionManager=(MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE); //Returns an Intent that must passed to startActivityForResult() in order to start screen capture.
        Intent permissionIntent=mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent,1000);

    }
    @SuppressLint("HandlerLeak")
    Handler handler1=new Handler(){
        Intent intent;
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                intent = (Intent) msg.obj;
            }else if (msg.what==2&&intent!=null){
                startService(intent);
                record_button.setBackgroundResource(R.mipmap.record_end);
                Toast.makeText(getApplicationContext(),"录屏开始",Toast.LENGTH_SHORT).show();
                isRecord=true;
            }else {
                Toast.makeText(getApplicationContext(),"录屏失败",Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void stopScreenRecord() {
        Intent service = new Intent(this, ScreenRecordService.class);
        stopService(service);
        isRecord=false;
        Toast.makeText(this,"录屏成功",Toast.LENGTH_SHORT).show();
        record_button.setBackgroundResource(R.mipmap.record);
    }
    public void Capture(){
        Bitmap bitmap =mAliyunVodPlayerView.snapShot();
        try{
            String sdCardPath = Environment.getExternalStorageDirectory().getPath()+ "/CNrail2/"+markinfo_path+"/";
            File folder=new File(sdCardPath);
            if (!folder.exists()){
                folder.mkdirs();
            }
            // 图片文件路径
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
            String filePath = sdCardPath +simpleDateFormat.format(new Date())+markInfo.getLine()+markInfo.getGroup()+markInfo.getPoint()+markInfo.getUseId()+".jpg";
            File file = new File(filePath);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            Toast.makeText(getApplicationContext(),"截图成功",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"截图失败",Toast.LENGTH_SHORT).show();
        }
    }
    
    public void PlayLive(){
        //返回直播功能
        String oriUrl = "rtmp://live.cnrail2.cn/as/"+id;
        String url= new GetLiveUrl().getUrl(oriUrl);
        currentPlayUrl=url;
        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource(url);
        AliyunLocalSource localSource = alsb.build();
        mAliyunVodPlayerView.setLocalSource(localSource);
        mAliyunVodPlayerView.disableNativeLog();
        mAliyunVodPlayerView.setAutoPlay(true);
        tvReturnLiveOrPhoto.setText("点击拍照");
    }

    public void takeOnlinePhoto(){
        //远程拍照功能
        tvReturnLiveOrPhoto.setText("拍照中...");
        LoadingDialog.showDialogForLoading(AliyunPlayerSkinActivity.this,"拍照中...",true);
        new Thread(() -> {
            try {//通过反复向服务器查询当前照片数量来判断是否成功
                GetRecordVideo.takeRemotePhoto(id);
                int oriLength=ossPhotoInfos.size();
                String [] photoUrls=GetRecordVideo.getPhotosUrl(id);
                while (photoUrls.length==oriLength){
                    Thread.sleep(10000);
                    photoUrls=GetRecordVideo.getPhotosUrl(id);
                }
                ossPhotoInfos.clear();
                ossPhotoInfos.addAll(GetRecordVideo.getphotos(id,photoUrls));
                handler.sendEmptyMessage(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void Back(View view){
        //返回按鈕功能
        Back();
    }
    public void Back(){
        if (mAliyunVodPlayerView.getScreenMode()==AliyunScreenMode.Full){
            mAliyunVodPlayerView.changeScreenMode(AliyunScreenMode.Small);
        }else if (relativeEnd.getVisibility()==View.GONE){
            super.onBackPressed();
        }else if (relativeEnd.getVisibility()==View.VISIBLE){
            alivcPlayListAdapter.removeCheck();
            checkedItemUrl.clear();
            relativeEnd.setVisibility(View.GONE);
            cb_all.setChecked(false);
        }
    }

    //info按鈕功能
    public void Getinfo(View view){
        View info_view = View.inflate(AliyunPlayerSkinActivity.this, R.layout.info_dialog, null);
        TextView tvCameraId=info_view.findViewById(R.id.tv_camera_id);
        TextView tvCameraAddress=info_view.findViewById(R.id.tv_camera_address);
        TextView tvCameraProjectName=info_view.findViewById(R.id.tv_camera_project_name);
        TextView tvCameraNote=info_view.findViewById(R.id.tv_camera_note);
        EditText etCameraLine=info_view.findViewById(R.id.et_camera_line);
        EditText etCameraGroup=info_view.findViewById(R.id.et_camera_group);
        EditText etCameraPoint=info_view.findViewById(R.id.et_camera_point);
        EditText etCameraUseId=info_view.findViewById(R.id.et_camera_useId);
        User user=((MyApp)getApplication()).getUser();

        tvCameraId.setText("设备编号："+markInfo.getCameraId());
        tvCameraAddress.setText("地址："+markInfo.getAddress());
        tvCameraProjectName.setText("项目名称："+markInfo.getProjectName());
        tvCameraNote.setText("备注："+markInfo.getNote());
        etCameraLine.setText(markInfo.getLine());
        etCameraGroup.setText(markInfo.getGroup());
        etCameraPoint.setText(markInfo.getPoint());
        etCameraUseId.setText(markInfo.getUseId());
        switch (user.getPrivilege()){
            case Constant.IDCHANGE:
                etCameraLine.setEnabled(false);
                etCameraGroup.setEnabled(false);
                etCameraPoint.setEnabled(false);
                break;
            default:
                etCameraLine.setEnabled(false);
                etCameraGroup.setEnabled(false);
                etCameraPoint.setEnabled(false);
                etCameraUseId.setEnabled(false);
                break;
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(AliyunPlayerSkinActivity.this);
        AlertDialog alert=builder.setView(info_view).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (user.getPrivilege()<5){
                    return;
                }
                markInfo.setLine(etCameraLine.getText().toString());
                markInfo.setGroup(etCameraGroup.getText().toString());
                markInfo.setPoint(etCameraPoint.getText().toString());
                markInfo.setUseId(etCameraUseId.getText().toString());
                markinfo_path =markInfo.getLine()+"/"+markInfo.getGroup()+"/"+markInfo.getPoint()+"/"+markInfo.getUseId();
                LoadingDialog.showDialogForLoading(AliyunPlayerSkinActivity.this,"修改中...",true);
                new Thread(()->{
                    String responserStr=new ConnectServer().changeMarkinfo(user,markInfo);
                Message toastMessage=new Message();
                    if (responserStr.equals("ok")){
                        toastMessage.obj="修改成功";
                    }else {
                        toastMessage.obj="修改失败";
                    }
                    toastMessage.what=3;
                    handler.sendMessage(toastMessage);
                } ).start();
            }
        }).setNegativeButton("取消",null).create();
        alert.show();
    }



    @SuppressLint("HandlerLeak")
            //主要是刷新ui
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llVideoList.setActivated(true);
            llVideoList.setVisibility(View.VISIBLE);
            alivcPlayListAdapter.notifyDataSetChanged();
            if (msg.what==2){
                Toast.makeText(getApplicationContext(),"拍照完成，请刷新拍照列表查看",Toast.LENGTH_SHORT).show();
                LoadingDialog.cancelDialogForLoading();
            }
            if (msg.what==3){
                Toast.makeText(getApplicationContext(), (String) msg.obj,Toast.LENGTH_SHORT).show();
                LoadingDialog.cancelDialogForLoading();
            }
        }
    };
    private void addRecordVideo(final int id) {
        //主要是将oss录制的视频链接获取到，并添加对应video，添加完毕之后刷新ui
        new Thread(() -> {
            try {
                String [] videoUrls = GetRecordVideo.getVideosUrl(id);
                ossVideoInfos.addAll(GetRecordVideo.getvideos(id,videoUrls));
                alivcVideoInfos.addAll(ossVideoInfos);
                String [] photoUrls = GetRecordVideo.getPhotosUrl(id);
                ossPhotoInfos.addAll(GetRecordVideo.getphotos(id,photoUrls));
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void initAliyunPlayerView() {
        mAliyunVodPlayerView = (AliyunVodPlayerView)findViewById(R.id.video_view);
        //保持屏幕常亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CNrail2/test_save_cache/";

        mAliyunVodPlayerView.setPlayingCache(true, sdDir, 60 * 60 /*单个文件的最大时长, s */, 300 /*缓存目录文件的总大小，MB*/);

        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        //mAliyunVodPlayerView.setCirclePlay(true);
        mAliyunVodPlayerView.setAutoPlay(true);

        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        mAliyunVodPlayerView.setNetConnectedListener(new MyNetConnectedListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new MyFrameInfoListener(this));
        mAliyunVodPlayerView.setOnChangeQualityListener(new MyChangeQualityListener(this));
        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));
        mAliyunVodPlayerView.setOnErrorListener(new MyOnOnErrorListener(this));
//        mAliyunVodPlayerView.setOrientationChangeListener(new MyOrientationChangeListener(this));//去掉自动旋转
        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new MyOnUrlTimeExpiredListener(this));
        mAliyunVodPlayerView.setOnShowMoreClickListener(new MyShowMoreClickLisener(this));
        mAliyunVodPlayerView.setOnCutClickListener(new MyCutClickListener());
        mAliyunVodPlayerView.setOnRecordClickListener(new MyRecordClikListener());
//        mAliyunVodPlayerView.enableNativeLog();

        record_button=findViewById(R.id.record);

    }
    /**
     * init拍照列表tab
     */
    private void initOnlinePhotoView() {
        tvOnlinePhoto = (TextView)findViewById(R.id.tv_tab_online_photo);
        ivOnlinePhoto = (ImageView)findViewById(R.id.iv_online_photo);
        tvReturnLiveOrPhoto=(Button)findViewById(R.id.bt_return_live); 
        
        //拍照列表tab默认不选择
        ivOnlinePhoto.setActivated(false);
        tvOnlinePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTab != TAB_ONLINE_PHOTO_LIST) {
                    currentTab=TAB_ONLINE_PHOTO_LIST;
                    ivOnlinePhoto.setActivated(true);
                    ivVideoList.setActivated(false);
                    ivDownloadVideo.setActivated(false);
                    tvDownloadEnd.setVisibility(View.VISIBLE);
                    tvDelete.setVisibility(View.GONE);
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                    checkedItemUrl.clear();
                    alivcVideoInfos.clear();
                    alivcVideoInfos.addAll(ossPhotoInfos);
                    handler.sendEmptyMessage(1);
                }
            }
        });
        
        tvReturnLiveOrPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAliyunVodPlayerView.isPlaying()&&currentPlayUrl.startsWith("rtmp")){
                    takeOnlinePhoto();
                }
                else{
                    if (currentPlayUrl.startsWith("rtmp")){
                        Toast.makeText(getApplicationContext(),"设备暂未上线，拍照失败",Toast.LENGTH_SHORT).show();
                    }
                    PlayLive();
                }
            }
        });
    }
    /**
     * init视频列表tab
     */
    private void initVideoListView() {
        tvVideoList = findViewById(R.id.tv_tab_video_list);
        ivVideoList = findViewById(R.id.iv_video_list);
        recyclerView = findViewById(R.id.video_list);
        llVideoList = findViewById(R.id.ll_video_list);
        alivcVideoInfos = new ArrayList<AlivcVideoInfo.Video>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alivcPlayListAdapter = new AlivcPlayListAdapter(this, alivcVideoInfos);

        ivVideoList.setActivated(true);
        llVideoList.setVisibility(View.VISIBLE);

        tvVideoList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTab!=TAB_VIDEO_LIST){
                    currentTab = TAB_VIDEO_LIST;
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    tvDownloadEnd.setVisibility(View.VISIBLE);
                    tvDelete.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                    checkedItemUrl.clear();
                    alivcVideoInfos.clear();
                    alivcVideoInfos.addAll(ossVideoInfos);
                    ivVideoList.setActivated(true);
                    llVideoList.setVisibility(View.VISIBLE);
                    ivOnlinePhoto.setActivated(false);
                    ivDownloadVideo.setActivated(false);
                    alivcPlayListAdapter.notifyDataSetChanged();
                }
            }
        });

        recyclerView.setAdapter(alivcPlayListAdapter);

        alivcPlayListAdapter.setOnVideoListItemClick(new AlivcPlayListAdapter.OnVideoListItemClick() {
            @Override
            public void onItemClick(View view,int position) {
                if (relativeEnd.getVisibility()==View.GONE) {
                    PlayParameter.PLAY_PARAM_TYPE = "url";
                    // 点击视频列表, 切换播放的视频
                    changePlaySource(position);
                }else if (relativeEnd.getVisibility()==View.VISIBLE){
                    boolean check=alivcPlayListAdapter.getChecked(position);
                    if (check){
                        checkedItemUrl.remove(alivcVideoInfos.get(position).getUrl());
                    }else {
                        checkedItemUrl.add(alivcVideoInfos.get(position).getUrl());
                    }
                    alivcPlayListAdapter.setIsChecked(position,!check);
                    alivcPlayListAdapter.setCHECKNORMAL();
                }
            }
        });

        alivcPlayListAdapter.setOnVideoListItemLongClick(new AlivcPlayListAdapter.OnVideoListItemLongClick() {
            @Override
            public void onItemLongClick(View view,int position) {

                relativeEnd.setVisibility(View.VISIBLE);
                alivcPlayListAdapter.setIsChecked(position,true);
                checkedItemUrl.add(alivcVideoInfos.get(position).getUrl());
            }
        });
    }
    /**
     * init离线视频tab
     */
    private void initDownloadView() {
        tvDownloadVideo = (TextView)findViewById(R.id.tv_tab_download_video);
        ivDownloadVideo = (ImageView)findViewById(R.id.iv_download_video);
        //离线下载Tab默认不选择
        ivDownloadVideo.setActivated(false);
        tvDownloadVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTab != TAB_DOWNLOAD_LIST) {
                    currentTab=TAB_DOWNLOAD_LIST;
                    ivDownloadVideo.setActivated(true);
                    ivVideoList.setActivated(false);
                    ivOnlinePhoto.setActivated(false);
                    tvDownloadEnd.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.VISIBLE);
                    alivcPlayListAdapter.removeCheck();
                    relativeEnd.setVisibility(View.GONE);
                    cb_all.setChecked(false);
                    checkedItemUrl.clear();
                }

                String sd_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CNrail2/" + markinfo_path;
                File file = new File(sd_path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File[] files = file.listFiles();
                localVideoInfos.clear();
                for (int i =files.length-1; i >=0; i--) {
                    AlivcVideoInfo.Video video = new AlivcVideoInfo.Video();
                    video.setUrl(files[i].getAbsolutePath());
                    video.setGroupId(id + "");
                    try{
                        video.setTitle(GetRecordVideo.setTitleAndTime(files[i].getName())[0]);
                    }catch (Exception e){
                        video.setTitle(files[i].getName());
                    }
                    video.setGroupId(id + "");
                    if (files[i].getName().contains(".mp4")){
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(files[i].getPath());
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        video.setDuration(mediaPlayer.getDuration() / 1000+"");
                        mediaPlayer.release();
                    }
                    localVideoInfos.add(video);
                }
                alivcVideoInfos.clear();
                alivcVideoInfos.addAll(localVideoInfos);
                handler.sendEmptyMessage(1);
            }
        });
    }

    private int currentVideoPosition;

    /**
     * 播放资源(包含本地以及在线资源)
     *
     * @param position 需要播放的数据在集合中的下标
     */
    private void changePlaySource(int position) {
        mAliyunVodPlayerView.onStop();
        currentVideoPosition = position;
        AlivcVideoInfo.Video video = alivcVideoInfos.get(position);
        String url = video.getUrl();
        if (url.contains(".jpg")){
            if (currentTab==TAB_ONLINE_PHOTO_LIST){
                new DownloadMethod(getApplicationContext(), id, url);
            }else {
                File file = new File(url);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoOutputUri = FileProvider.getUriForFile(
                            this,
                            this.getPackageName() + ".fileprovider",
                            file);
                    intent.setDataAndType(photoOutputUri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//注意加上这句话
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                }
                startActivity(intent);
            }
        }else {
            currentPlayUrl = url;
            tvReturnLiveOrPhoto.setText("返回现场监控");
            LoadingDialog.showDialogForLoading(AliyunPlayerSkinActivity.this,"加载中...",true);
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(url);
            alsb.setTitle(video.getTitle());
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);
            mAliyunVodPlayerView.disableNativeLog();
            mAliyunVodPlayerView.setAutoPlay(true);
        }
    }

    private static class MyPrepareListener implements IAliyunVodPlayer.OnPreparedListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyPrepareListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onPrepared() {
        }
    }


    private static class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyCompletionListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {

            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private void onCompletion() {
        // 当前视频播放结束, 播放下一个视频
        onNext();
    }

    private void onNext() {
        if (currentError == ErrorInfo.UnConnectInternet) {
            // 此处需要判断网络和播放类型
            // 网络资源, 播放完自动波下一个, 无网状态提示ErrorTipsView
            // 本地资源, 播放完需要重播, 显示Replay, 此处不需要处理
            if ("url".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                mAliyunVodPlayerView.showErrorTipView(4014, -1, "当前网络不可用");
            }
            return;
        }

        currentVideoPosition++;
        if (currentVideoPosition >= alivcVideoInfos.size()) {
            //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
            currentVideoPosition = 0;
        }

        AlivcVideoInfo.Video video = alivcVideoInfos.get(currentVideoPosition);
        if (video != null) {
            changePlaySource(currentVideoPosition);
        }

    }

    private static class MyFrameInfoListener implements IAliyunVodPlayer.OnFirstFrameStartListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyFrameInfoListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onFirstFrameStart() {
            //当成功播放第一帧，则说明缓冲结束，取消掉loading动画
            LoadingDialog.cancelDialogForLoading();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                Toast.makeText(AliyunPlayerSkinActivity.this, "没有sd卡读写权限, 无法下载", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static class MyChangeQualityListener implements IAliyunVodPlayer.OnChangeQualityListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyChangeQualityListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onChangeQualitySuccess(String finalQuality) {
        }

        @Override
        public void onChangeQualityFail(int code, String msg) {
        }
    }

    private static class MyOnOnErrorListener implements IAliyunVodPlayer.OnErrorListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyOnOnErrorListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }
        @Override
        public void onError(int i, int i1, String s) {
//            activityWeakReference.get().tvReturnLiveOrPhoto.setText("返回现场监控");
//            Log.d("55juekun", "onError: " + s+"  i"+i+"   i1"+i1);
        }
    }

    private static class MyStoppedListener implements IAliyunVodPlayer.OnStoppedListener {

        private WeakReference<AliyunPlayerSkinActivity> activityWeakReference;

        public MyStoppedListener(AliyunPlayerSkinActivity skinActivity) {
            activityWeakReference = new WeakReference<AliyunPlayerSkinActivity>(skinActivity);
        }

        @Override
        public void onStopped() {

            AliyunPlayerSkinActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }
    private void onStopped() {
        Toast.makeText(AliyunPlayerSkinActivity.this.getApplicationContext(), R.string.log_play_stopped,
            Toast.LENGTH_SHORT).show();
    }

    private void setPlaySource() {
        if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(PlayParameter.PLAY_PARAM_URL);
            Uri uri = Uri.parse(PlayParameter.PLAY_PARAM_URL);
            if ("rtmp".equals(uri.getScheme())) {
                alsb.setTitle("");
            }
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                llVideoList.setActivated(true);
                llVideoList.setVisibility(View.VISIBLE);//自己修改的

                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams)mAliyunVodPlayerView
                    .getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int)(ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
                //                }

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }else {

                }

                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams)mAliyunVodPlayerView
                    .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = 0;
                //                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        updatePlayerViewMode();
    }

    /**
     * 打算取消掉自动旋转的功能，因此去掉该方法
     * */
/*    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOrientationChangeListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            AliyunPlayerSkinActivity activity = weakReference.get();
            activity.hideShowMoreDialog(from, currentMode);
        }
    }*/

    private void hideShowMoreDialog(boolean from, AliyunScreenMode currentMode) {
        if (showMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                showMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    /**
     * 判断是否有网络的监听
     */
    private class MyNetConnectedListener implements AliyunVodPlayerView.NetConnectedListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyNetConnectedListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            AliyunPlayerSkinActivity activity = weakReference.get();
        }

        @Override
        public void onNetUnConnected() {
            AliyunPlayerSkinActivity activity = weakReference.get();
        }
    }

    private static class MyOnUrlTimeExpiredListener implements IAliyunVodPlayer.OnUrlTimeExpiredListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        public MyOnUrlTimeExpiredListener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<AliyunPlayerSkinActivity>(activity);
        }

        @Override
        public void onUrlTimeExpired(String s, String s1) {
            AliyunPlayerSkinActivity activity = weakReference.get();
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Toast.makeText(context, "下载完成==>"+"id : " + downId,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static class MyShowMoreClickLisener implements ControlView.OnShowMoreClickListener {
        WeakReference<AliyunPlayerSkinActivity> weakReference;

        MyShowMoreClickLisener(AliyunPlayerSkinActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void showMore() {
            AliyunPlayerSkinActivity activity = weakReference.get();
            activity.showMore(activity);
        }
    }

    private class MyCutClickListener implements ControlView.OnCutClickListener {
        @Override
        public void onClick() {
            Capture();

        }
    }

    private class MyRecordClikListener implements ControlView.OnRecordClickListener{
        @Override
        public void onClick() {
            if(isRecord){
                stopScreenRecord();
            }else{
                startRecord();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    private void showMore(final AliyunPlayerSkinActivity activity) {
        showMoreDialog = new AlivcShowMoreDialog(activity);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume(mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScreenBrightness(mAliyunVodPlayerView.getCurrentScreenBrigtness());

        ShowMoreView showMoreView = new ShowMoreView(activity, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();

        showMoreView.setOnSpeedCheckedChangedListener(new ShowMoreView.OnSpeedCheckedChangedListener() {
            @Override
            public void onSpeedChanged(RadioGroup group, int checkedId) {
                // 点击速度切换
                if (checkedId == R.id.rb_speed_normal) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
                } else if (checkedId == R.id.rb_speed_half) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.Half);
                } else if (checkedId == R.id.rb_speed_twice) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
                } else if (checkedId == R.id.rb_speed_oneHalf) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.oneHalf);
                }
            }
        });

        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentScreenBrigtness(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentVolume(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });


    }
}
