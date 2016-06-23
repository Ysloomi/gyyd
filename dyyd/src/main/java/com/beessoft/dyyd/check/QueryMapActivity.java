package com.beessoft.dyyd.check;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;

/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class QueryMapActivity extends BaseActivity {
	// GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;

	private String jd,wd,username;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Intent intent = getIntent();
		jd = intent.getStringExtra("jd");
		wd = intent.getStringExtra("wd");
		username = intent.getStringExtra("username");

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		//定义Maker坐标点  
		LatLng point = new LatLng(Float.valueOf(wd),Float.valueOf(jd));
//		LatLng point = new LatLng(31.133648, 104.173363);
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()
				.position(point)
				.icon(bitmap);

		//构建文字Option对象，用于在地图上添加文字  
		OverlayOptions textOption = new TextOptions()
				.bgColor(0xAAFFFFFF).fontSize(28)
				.fontColor(0xFF000000)
				.text(username)
				.position(point);
		//在地图上添加该文字对象并显示  
		mBaiduMap.addOverlay(textOption);
		//在地图上添加Marker，并显示  
		mBaiduMap.addOverlay(option);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 18));
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
		mMapView.onDestroy();
	}
}

