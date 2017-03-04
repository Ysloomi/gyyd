package com.beessoft.dyyd.nearby;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Nearby;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyActivity extends BaseActivity {
    private MapView mapView;
    private BaiduMap baiduMap;
    private final Context context = NearbyActivity.this;

    private LatLng lastLatLng;

    private LocationClient client;
    private OverlayOptions options;
    private OverlayOptions ooCircle;
    private BitmapDescriptor bitmap;
    private RelativeLayout layout;

    private ArrayList<Nearby> nearbyList = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private Nearby near;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_nearby);
        layout = (RelativeLayout) findViewById(R.id.activity_nearby);

        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setTrafficEnabled(true);
        layout.addView(LayoutInflater.from(context).
                inflate(R.layout.local_position_layout,mapView,false));

        initialise();

        initListener();


        //绑定地图上的标注物事件的点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener(){

            //此方法中弹出窗口
            @Override
            public boolean onMarkerClick(Marker marker) {

                Nearby nearby;
                String med="",operator="",name="",picture="";
                for (int i = 0; i < markerList.size(); i++) {
                    if (marker == markerList.get(i)){
                        nearby = nearbyList.get(i);
                        med = nearby.getMed();
                        operator = nearby.getOperator();
                        name = nearby.getName();
                        picture = nearby.getPhoto();
                    }
                }

                SelfDialog dialog = new SelfDialog(context,picture,med,name,operator);

                dialog.show();

                return false;
            }
        });



    }



    //初始化地图工作,并且一切工作都在初始化以后操作
    private void initialise() {


        client = new LocationClient(getApplicationContext());
        initLocation();

        //注册定位监听结果
        client.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(bdLocation.getLocType() == 161){

                    double longitude = bdLocation.getLongitude();
                    double latitude = bdLocation.getLatitude();


                    LatLng position =  new LatLng(latitude,longitude);
                    lastLatLng = position;

                    //设置当前位置为试图中心点
                    baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(position));
                    markerCenter(position);

                    client.stop();

                    //获取到当前的定位以后开始获取数据
                    getData(longitude,latitude);


                }else {
                    ToastUtil.toast(context,"定位失败,请检查网络");
                }
            }
        });
        client.start();




        /*//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(position)
                .zoom(12)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);*/
    }

    private void markerCenter(LatLng position){



        //添加标注图标
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_itis_position);
        options = new MarkerOptions()
                .position(position)//通过定位获取到的位置
                .icon(descriptor)
                .draggable(true);

       /* //添加圆
        ooCircle = new CircleOptions().fillColor(0xC8D9DE)
                .center(position).stroke(new Stroke(5, 0xAA000000))
                .radius(2000);
        baiduMap.addOverlay(ooCircle);*/

        /*baiduMap.addOverlay(options);//把标注添加到百度地图中*/

        //把当前位置作为试图的中心点,并且设置为16
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(position,15));
        //baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(position));
    }

    /**
     * 标记获取到的点
     * @param i
     */
    private void marker(int i) {
        double jd = Double.parseDouble(nearbyList.get(i).getJd());
        double wd = Double.parseDouble(nearbyList.get(i).getWd());
        String title = nearbyList.get(i).getMed()+"\n"+nearbyList.get(i).getName();
        final LatLng point = new LatLng(wd,jd);

        near = nearbyList.get(i);
        switch (near.getOperator()){
            case "中国电信":
                if (near.getMed().equals("gjx")){
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dxgj);
                }else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dxgp);
                }
                break;
            case "中国联通":
                if (near.getMed().equals("gjx")){
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ltgj);
                }else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ltgp);
                }
                break;
            case "中国移动":
                if (near.getMed().equals("gjx")){
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ydgj);
                }else {
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ydgp);
                }
                break;
            default:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dxgp);
                break;
        }

        //Log.e("=====",jd+"wd: "+ wd +"title: "+ title);
        //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dxgj);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .title(title);


        //把标注物添加到百度地图上
        //baiduMap.addOverlay(option);
        markerList.add((Marker)baiduMap.addOverlay(option));



    }


    /**
     * 地图状态改变
     */
    private void initListener() {
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus status) {
                // updateMapState(status);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus status) {

                updateMapState(status);
            }

            @Override
            public void onMapStatusChange(MapStatus status) {
                // updateMapState(status);
            }
        });
    }
    //更新后的地图中心点
    private void updateMapState(MapStatus status) {
        LatLng mCenterLatLng = status.target;
        if (DistanceUtil.getDistance(lastLatLng,mCenterLatLng)>500){

            /**获取经纬度*/
            double lat = mCenterLatLng.latitude;
            double lng = mCenterLatLng.longitude;

            baiduMap.clear();
            getData(lng,lat);

            //更新中心试图,并且做标记。
            //markerCenter(mCenterLatLng);
            lastLatLng = mCenterLatLng;
        }

    }

    /**
     * 获取数据
     */
    private void getData(final double lat, final double lng){
        nearbyList.clear();
        markerList.clear();

        String httpUrl = User.mainurl + "sf/GetBoxs";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams request_params = new RequestParams();

        request_params.put("mac",mac);
        request_params.put("jd",String.valueOf(lat));
        request_params.put("wd",String.valueOf(lng));

        request_params.put("usercode",username);


        //Log.e("====","jd:"+String.valueOf(lat)+",wd:"+String.valueOf(lng));
        httpClient.post(httpUrl,request_params,
                new AsyncHttpResponseHandler(){
                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        ToastUtil.toast(context,"请检查网络");
                    }

                    @Override
            public void onSuccess(String s) {
                try {
                    JSONObject data_json = new JSONObject(s);
                    if (data_json.optString("code").equals("0")){
                        JSONArray arrayType = data_json.optJSONArray("list");
                        Nearby nearby;
                        for (int i = 0; i < arrayType.length(); i++) {
                            JSONObject obj = arrayType.optJSONObject(i);
                            nearby= new Nearby();
                            nearby.setJd(obj.optString("jd"));
                            nearby.setMed(obj.optString("med"));
                            nearby.setName(obj.optString("name"));
                            nearby.setWd(obj.optString("wd"));
                            nearby.setPhoto(obj.optString("photo"));
                            nearby.setOperator(obj.optString("Operator"));
                            nearby.setLatLng(new LatLng(lat,lng));
                            nearbyList.add(nearby);
                        }

                        //开始做标记
                        for (int i = 0; i < nearbyList.size(); i++) {
                            marker(i);
                        }

                    }else if (data_json.optString("code").equals("1")){
                        ToastUtil.toast(context,"没有数据");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=3000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        //设置好的定位信息给定位类对象
        client.setLocOption(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        finish();
        //停止定位服务
        client.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

}
