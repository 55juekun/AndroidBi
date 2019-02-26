package com.aliyun.vodplayerview.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.aliyun.vodplayerview.Util.ConnectServer;
import com.aliyun.vodplayerview.Util.DrawMark;
import com.aliyun.vodplayerview.Util.GetTree;
import com.aliyun.vodplayerview.Util.MarkInfo;
import com.aliyun.vodplayerview.Util.SettingFragment;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.bi.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnGetDistricSearchResultListener {

    private Button left_drawer_open;
    private MapView mMapView;
    private Spinner lineSpinner;
    private Spinner groupSpinner;
    private DrawerLayout drawer_layout;
    private SettingFragment fg_left_menu;

    private FragmentManager fManager;
    private BaiduMap mBaiduMap;
    private DistrictSearch mDistrictSearch;
    private ArrayAdapter<String> lineAdapter;
    private ArrayAdapter<String> groupAdapter;
    private Context mainContext;
//    private Button mSearchButton;
    private boolean markerClicked=false;
//    private boolean left_drawer_opened=false;
    String TAG="MainActivity";
    private ArrayList<OverlayOptions> markerOptions=new ArrayList<>();//记录所有标记点，用于刷新地图时标记点不变

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String[]a=getIntent().getExtras().getStringArray("groups");
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mainContext=getApplicationContext();
        fManager = getSupportFragmentManager();
        initView();
    }

    public void initView(){
        mMapView =  findViewById(R.id.map);
        left_drawer_open =findViewById(R.id.btn_setting);
        fg_left_menu =(SettingFragment) fManager.findFragmentById(R.id.fg_left_menu);
        drawer_layout=findViewById(R.id.drawer_layout);
        drawer_layout.setScrimColor(Color.TRANSPARENT);
        fg_left_menu.setDrawerLayout(drawer_layout);
        mDistrictSearch = DistrictSearch.newInstance();
        mDistrictSearch.setOnDistrictSearchListener(this);
        mBaiduMap = mMapView.getMap();
        fg_left_menu.setBaiduMap(mBaiduMap);
        //之前是无地图模式，总感觉不爽，默认还是普通地图吧
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapStatusUpdate msu= MapStatusUpdateFactory.newLatLngZoom(new LatLng(30.771268,103.974595),5.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        mMapView.showScaleControl(true);

        left_drawer_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (!markerClicked) {
                    LatLng position = marker.getPosition();
                    String msg = marker.getExtraInfo().getString("msg");
                    marker.setIcon(marker.getIcon());
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextSize(7);
                    textView.setText(msg);
                    textView.setTextColor(Color.BLUE);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(MainActivity.this,AliyunPlayerSkinActivity.class);
                            intent.putExtra("id",marker.getExtraInfo().getInt("id"));
                            startActivity(intent);
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    InfoWindow infoWindow = new InfoWindow(textView, position, -47);
                    mBaiduMap.showInfoWindow(infoWindow);
                    markerClicked=!markerClicked;
                    return true;
                }else {
                    mBaiduMap.hideInfoWindow();
                    markerClicked=!markerClicked;
                    return false;
                }
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markerClicked){
                    mBaiduMap.hideInfoWindow();
                    markerClicked=!markerClicked;
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }
    @Override
    public void onGetDistrictResult(DistrictResult districtResult) {
        mBaiduMap.clear();
        if (districtResult == null) {
            return;
        }
        if (districtResult.error == SearchResult.ERRORNO.NO_ERROR) {
            List<List<LatLng>> polyLines = districtResult.getPolylines();
            if (polyLines == null) {
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (List<LatLng> polyline : polyLines) {
                OverlayOptions ooPolyline11 = new PolylineOptions().width(7)
                        .points(polyline).dottedLine(true).color(Color.YELLOW);
                mBaiduMap.addOverlay(ooPolyline11);
                //作用是圈定的范围添加区块蒙版，去掉了这个功能
//                OverlayOptions ooPolygon = new PolygonOptions().points(polyline)
//                        .stroke(new Stroke(5, 0xAA00FF88)).fillColor(0xAAFFFF00);
//                mBaiduMap.addOverlay(ooPolygon);
                for (LatLng latLng : polyline) {
                    builder.include(latLng);
                }
            }
            mBaiduMap.addOverlays(markerOptions);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            try {
                new ConnectServer().login();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Bitmap oriBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.icon_gcoding);
            MarkInfo[] markInfos=new GetTree().getMarkInfos();
            DrawMark drawMark=new DrawMark();
            for (int i = 0; i <markInfos.length ; i++) {
                CoordinateConverter converter  = new CoordinateConverter();
                converter.from(CoordinateConverter.CoordType.GPS);
                // sourceLatLng GPS硬件获取到的原始坐标
                converter.coord(new LatLng(markInfos[i].getLatitude(),markInfos[i].getLongitude()));
                //pointLat 经过转换后的百度地图坐标
                LatLng pointLat = converter.convert();
                /*因为用户自定义的id不是简单的数字，因此需要将其_-作为分隔符，将之后的最后一个值作为数字标记*/
                String fullStringId=markInfos[i].getUseId();
                fullStringId.replaceAll("_","-");
                String[] fullStringIdArray=fullStringId.split("-");
                String nO=String.valueOf(fullStringIdArray[fullStringIdArray.length-1]);
                BitmapDescriptor bitmap= BitmapDescriptorFactory.fromBitmap(drawMark.setBitmap(oriBitmap,markInfos[i].getStatus(),nO ));
                Bundle bundle=new Bundle();
                String msg=markInfos[i].getInfoWindowMsg();
                bundle.putString("msg",msg);
                int cameraId=markInfos[i].getCameraId();
                bundle.putInt("id",cameraId);
                OverlayOptions options=new MarkerOptions().position(pointLat).icon(bitmap).draggable(false).perspective(true).extraInfo(bundle);
                mBaiduMap.addOverlay(options);
                markerOptions.add(options);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mDistrictSearch.destroy();
        super.onDestroy();
    }

}
