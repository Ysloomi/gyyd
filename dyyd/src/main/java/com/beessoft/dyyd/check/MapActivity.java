package com.beessoft.dyyd.check;

import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.overlayutil.OverlayManager;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 */
public class MapActivity extends BaseActivity {
	// GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;
//	private Marker mMarker = null;
	private TextView textView;
	private String department, person, itype, itime,myId;
	private String flag;
	// 构建Marker图标
	BitmapDescriptor bitmap = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	// 构建Marker图标
	BitmapDescriptor bitmapGreen = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka_green);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		context= MapActivity.this;
		mac = GetInfo.getIMEI(context);
		username = GetInfo.getUserName(context);

		textView =(TextView) findViewById(R.id.mileage_text);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		department = getIntent().getStringExtra("department");
		person = getIntent().getStringExtra("person");
		itype = getIntent().getStringExtra("itype");
		itime = getIntent().getStringExtra("itime");
		myId= getIntent().getStringExtra("id");
		flag= getIntent().getStringExtra("flag");

		// 定义Maker坐标点
		LatLng point = new LatLng(31.133641, 104.397998);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

		if ("do".equals(itype)) {
			ProgressDialogUtil.showProgressDialog(context);
			visitServer_do();
		} else if ("run".equals(itype)) {
			ProgressDialogUtil.showProgressDialog(context);
			visitServer_run();
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		// mSearch.destroy();
		super.onDestroy();
		bitmap.recycle();
		bitmapGreen.recycle();
	}

	private void visitServer_do() {

		String httpUrl = User.mainurl + "sf/map_do";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("itype", itype);
		parameters_userInfo.put("flag", flag);
		parameters_userInfo.put("itime", itime);
		parameters_userInfo.put("id", myId);
		String strBase64 = new String(Base64.encode(person.getBytes(), Base64.DEFAULT));
		parameters_userInfo.put("usercode", strBase64);
		parameters_userInfo.put("cdepcode", department);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");

								List<OverlayOptions> optionsList = new ArrayList<OverlayOptions>();

								for (int j = 0; j < array.length(); j++) {

									JSONObject obj = array.getJSONObject(j);

									String name = "";
									if ("0".equals(obj.getString("state"))) {
										// 定义Maker坐标点
										LatLng point = new LatLng(Float
												.valueOf(obj.getString("lat")),
												Float.valueOf(obj
														.getString("lng")));

										// 构建MarkerOption，用于在地图上添加Marker
										OverlayOptions option = new MarkerOptions()
												.position(point).icon(bitmap);
										name = obj.getString("name");
										// 构建文字Option对象，用于在地图上添加文字
										OverlayOptions textOption = new TextOptions()
												.bgColor(0xAAFFFFFF)
												.fontSize(28)
												.fontColor(0xFF000000)
												.text(name)
												.position(point);
										// 在地图上添加该文字对象并显示
										mBaiduMap.addOverlay(textOption);
//										// 在地图上添加Marker，并显示
										mBaiduMap.addOverlay(option);
//										// 添加到
										optionsList.add(option);
									} else if ("1".equals(obj.getString("state"))) {
										// 定义Maker坐标点
										LatLng point = new LatLng(Float
												.valueOf(obj.getString("lat")),
												Float.valueOf(obj
														.getString("lng")));


										// 构建MarkerOption，用于在地图上添加Marker
										OverlayOptions option = new MarkerOptions()
												.position(point)
												.icon(bitmapGreen);

										name = obj.getString("name");
										// 构建文字Option对象，用于在地图上添加文字
										OverlayOptions textOption = new TextOptions()
												.bgColor(0xAAFFFFFF)
												.fontSize(28)
												.fontColor(0xFF000000)
												.text(obj.getString("name"))
												.position(point);
//										 在地图上添加该文字对象并显示
										mBaiduMap.addOverlay(textOption);
										// 在地图上添加Marker，并显示
										mBaiduMap.addOverlay(option);
										// 添加到
										optionsList.add(option);
									}
								}

								// 将所有的marker添加，并自动缩放
								LiOverlayManager overlayManager = new LiOverlayManager(
										mBaiduMap);
								overlayManager.setData(optionsList);
								mBaiduMap.setOnMarkerClickListener(overlayManager);
								overlayManager.addToMap();
								overlayManager.zoomToSpan();// 缩放
							} else if (-1==code) {
								ToastUtil.toast(context, "所选时间无位置信息");
								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
						ToastUtil.toast(context, "服务器连接超时");
						finish();
					}
				});
	}

	private void visitServer_run() {
		String httpUrl = User.mainurl + "sf/map_do";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("itype", itype);
		parameters_userInfo.put("flag", flag);
		parameters_userInfo.put("itime", itime);
		parameters_userInfo.put("id", myId);
		String strBase64 = new String(Base64.encode(person.getBytes(), Base64.DEFAULT));
		parameters_userInfo.put("usercode", strBase64);
		parameters_userInfo.put("cdepcode", department);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							int code = dataJson.getInt("code");
							if (code==0) {
								JSONArray array = dataJson.getJSONArray("list");
								List<LatLng> list = new ArrayList<LatLng>();
								LatLng point = null;
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									// 定义多边形的五个顶点
									point = new LatLng(Float.valueOf(obj
											.getString("lat")), Float
											.valueOf(obj.getString("lng")));
									list.add(point);
								}
								// /构建用户绘制多边形的Option对象
								OverlayOptions option = new PolylineOptions()
										.points(list).color(0xFF800080);
								// 在地图上添加多边形Option，用于显示
								mBaiduMap.addOverlay(option);
								mBaiduMap
										.animateMapStatus(MapStatusUpdateFactory
												.newLatLngZoom(point, 16));
								// mBaiduMap.setViewport(list);
							} else if (-1==code) {
								ToastUtil.toast(context, "所选时间无位置信息");
								finish();
							}
							if (!"".equals(myId)) {
								textView.setText(dataJson.getString("car"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
						ToastUtil.toast(context, "服务器连接超时");
						finish();
					}
				});

	}
}

class LiOverlayManager extends OverlayManager {

	private List<OverlayOptions> optionsList = new ArrayList<OverlayOptions>();

	public LiOverlayManager(BaiduMap baiduMap) {
		super(baiduMap);
	}

	@Override
	public List<OverlayOptions> getOverlayOptions() {
		return optionsList;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	public void setData(List<OverlayOptions> optionsList) {
		this.optionsList = optionsList;
	}

	@Override
	public boolean onPolylineClick(Polyline polyline) {
		return false;
	}
}
