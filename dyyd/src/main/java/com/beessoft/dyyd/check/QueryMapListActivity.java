package com.beessoft.dyyd.check;

import android.os.Bundle;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;

import java.util.HashMap;
import java.util.List;

/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class QueryMapListActivity extends BaseActivity {

    // GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    BaiduMap mBaiduMap = null;
    MapView mMapView = null;

    private String jd, wd, username;
    //构建Marker图标
    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
    BitmapDescriptor bitmapGreen = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka_green);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        List<HashMap<String, String>> list = (List<HashMap<String, String>>) getIntent().getSerializableExtra("pin");
        jd = getIntent().getStringExtra("jd");
        wd = getIntent().getStringExtra("wd");
//		username = getIntent().getStringExtra("username");
        initView();

        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> map = list.get(i);
            //定义Maker坐标点
            LatLng point = new LatLng(Float.valueOf(map.get("latitude")), Float.valueOf(map.get("longitude")));

            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);

            LatLng p2 = new LatLng(Double.valueOf(wd), Double.valueOf(jd));
            double distance = DistanceUtil.getDistance(point, p2);
            distance = Math.ceil(distance);
            //构建文字Option对象，用于在地图上添加文字
            OverlayOptions textOption = new TextOptions()
                    .bgColor(0xAAFFFFFF)
                    .fontSize(20)
                    .fontColor(0xFF000000)
                    .text("距离" + distance + "米应小于" + map.get("fw") + "米")
                    .position(point);
            //在地图上添加该文字对象并显示
            mBaiduMap.addOverlay(textOption);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }
        LatLng point = new LatLng(Float.valueOf(wd), Float.valueOf(jd));
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmapGreen);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 15));
    }

    private void initView() {
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        TextView infoTxt = (TextView) findViewById(R.id.mileage_text);
        infoTxt.setText("红色的点显示的是签到点以及显示了当前位置的距离，绿色的点显示当前位置。");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            bitmap.recycle();
            bitmapGreen.recycle();
            mMapView .onDestroy();
        }
    }
}

