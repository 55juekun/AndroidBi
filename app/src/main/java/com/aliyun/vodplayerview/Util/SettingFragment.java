package com.aliyun.vodplayerview.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliyun.vodplayerview.activity.LoginActivity;
import com.aliyun.vodplayerview.activity.MyApp;
import com.baidu.mapapi.map.BaiduMap;
import com.bi.R;

import java.util.ArrayList;


public class SettingFragment extends Fragment implements View.OnClickListener {
    private DrawerLayout drawer_layout;
    private FragmentManager fManager;

    private ArrayList<TreeElement> mPdfOutlinesCount = new GetTree().getExample();
    private TreeAdapter treeAdapter=null;
    private Context context=null;
    private ListView listView;
    private BaiduMap mBaiduMap;
    private TextView tvUserName;
    private TextView tvUserMsg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, container, false);
        tvUserName=view.findViewById(R.id.tv_username);
        tvUserMsg=view.findViewById(R.id.tv_user_msg);
        User user=((MyApp)getActivity().getApplication()).getUser();
        tvUserName.setText(user.getUserName());
        tvUserMsg.setText(user.getUserMsgProfile());
        view.findViewById(R.id.btn_map_normal).setOnClickListener(this);
        view.findViewById(R.id.btn_map_traffic).setOnClickListener(this);
        view.findViewById(R.id.btn_map_satellite).setOnClickListener(this);
        view.findViewById(R.id.exit).setOnClickListener(this);
        fManager = getActivity().getSupportFragmentManager();
        context = getActivity();
        treeAdapter = new TreeAdapter( context,mPdfOutlinesCount);
        listView=view.findViewById(R.id.list_one);
        listView.setAdapter(treeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                treeAdapter.onItemClick(parent,view,position,id,drawer_layout);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_map_normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mBaiduMap.setTrafficEnabled(false);
                drawer_layout.closeDrawer(Gravity.START);
                break;
            case R.id.btn_map_traffic:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                mBaiduMap.setTrafficEnabled(true);
                drawer_layout.closeDrawer(Gravity.START);
                break;
            case R.id.btn_map_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                mBaiduMap.setTrafficEnabled(false);
                drawer_layout.closeDrawer(Gravity.START);
                break;
//            case R.id.btn_refresh:
//                Log.e(TAG, "onClick: three" );
//                try {
//                    new Connect_sql().login();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                drawer_layout.closeDrawer(Gravity.START);
//                break;
            case R.id.exit:
                Intent intent1=new Intent(getContext(),LoginActivity.class);
                startActivity(intent1);
                getActivity().finish();
                break;
        }
    }
    public void setDrawerLayout(DrawerLayout drawer_layout){
        this.drawer_layout = drawer_layout;
    }

    public void setBaiduMap(BaiduMap mBaiduMap) {
        this.mBaiduMap = mBaiduMap;
    }
}
