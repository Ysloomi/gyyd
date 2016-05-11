package com.beessoft.dyyd;

import android.app.Application;
import android.database.Cursor;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONObject;

public class LocationApplication extends Application {

	public LocationClient mLocationClient;
	// LocationClient mLocationClient = null;
	// private MyLocationListenner myListener = new MyLocationListenner();
	public MyLocationListener mMyLocationListener;
	// private NotifyLister mNotifyer=null;
	private DistanceDatabaseHelper distanceHelper; // 数据库帮助类

	private String jd, wd, addr;

	private String distance, totalDistance;
	private String type = "基站";

	private String iflag = "0";
	private int i = 0;

	private String jdLocation = "";
	private String wdLocation = "";
	private String addrLocation = "";
	private String timeLocation = "";
	private String typeLocation = "";
	private String MacAddr;

	private String idFlag;

	// 经
	public String getjd() {
		return jd;
	}

	public void setjd(String s) {
		jd = s;
	}

	// 纬
	public void setwd(String s) {
		wd = s;
	}

	public String getwd() {
		return wd;
	}

	// 中文地址
	public void setaddr(String s) {
		addr = s;
	}

	public String getaddr() {
		return addr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public static LocationApplication mInstance;

	public static LocationApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		// mLocationClient.registerLocationListener( myListener );
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		MacAddr = GetInfo.getIMEI(LocationApplication.this);
		CrashReport.initCrashReport(LocationApplication.this, "900011168", false);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
//		Stetho.initializeWithDefaults(this);

		mInstance =this;
	}

	public void logMsg(String str, double Latitude, double Longitude,
			String addr, String type, double speed) {
		if (Latitude > 1 && Longitude > 1) {
			setjd(Longitude + "");
			setwd(Latitude + "");
			setaddr(addr);
			setType(type);

			if (Gps.exist(this, "distance.db")) {
				distanceHelper = new DistanceDatabaseHelper(
						getApplicationContext(), "distance.db", 1);

				jd = Double.toString(Longitude);
				wd = Double.toString(Latitude);

				distance = "0";
				totalDistance = "0";

				String time = DateUtil.getDateLoca();

				distanceHelper.getReadableDatabase()
						.execSQL(
								"insert into distance_table values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
								new String[] { time, jd, wd, addr, distance,
										totalDistance, type, iflag, speed + "" });

				distanceHelper.close();

				try {
					if (i > 23) {
						distanceHelper = new DistanceDatabaseHelper(
								getApplicationContext(), "distance.db", 1);
						Cursor cursor3 = distanceHelper
								.getReadableDatabase()
								.rawQuery(
										"select * from distance_table where iflag_text = ?",
										new String[] { "0" });

						try {
							while (cursor3.moveToNext()) {
								idFlag = cursor3.getString(0);
								String time1 = cursor3.getString(1);
								jd = cursor3.getString(2);
								wd = cursor3.getString(3);
								String typeGet = cursor3.getString(7);
								if (!"".equals(timeLocation)) {
									timeLocation += ",";
								}
								if (!"".equals(jdLocation)) {
									jdLocation += ",";
								}
								if (!"".equals(wdLocation)) {
									wdLocation += ",";
								}
								if (!"".equals(typeLocation)) {
									typeLocation += ",";
								}
								if (!"".equals(addrLocation)) {
									addrLocation += ",";
								}
								jdLocation += jd;
								wdLocation += wd;
								addrLocation +="无";
								timeLocation += time1;
								typeLocation += typeGet;
							}
//							Log.d("location", typeLocation);
							visitServer(MacAddr, jdLocation,wdLocation, addrLocation, typeLocation,timeLocation);
							i = 0;
							jdLocation = "";
							wdLocation = "";
							addrLocation = "";
							typeLocation = "";
							timeLocation = "";
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							cursor3.close();
						}
						distanceHelper.close();
					}
					i++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			StringBuffer sb = new StringBuffer(256);
			double Latitude = location.getLatitude();
			double Longitude = location.getLongitude();
			String addr = location.getAddrStr();
			double speed = 0;
			String type = "基站";
			String describe = "";
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());

			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				speed = location.getSpeed();
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");
				describe = "gps定位成功";
				type = "Gps";
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				if (location.getNetworkLocationType().equals("wf")) {
					type = "Wifi";
					sb.append("\ndescribe : ");
					sb.append("Wifi网络定位成功");
					describe = "Wifi网络定位成功";
				} else if (location.getNetworkLocationType().equals("cl")) {
					type = "基站";
					sb.append("\ndescribe : ");
					sb.append("基站网络定位成功");
					describe = "基站网络定位成功";
				}
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
				describe = "离线定位成功，离线定位结果也是有效的";
				//				请求离线定位：
//				离线定位功能：用户请求过得基站定位结果会缓存在本地文件。
//
//				离线定位结果只缓存基站定位结果，没有Wi-Fi定位结果，所以定位精度较差。
//
//				离线定位结果没有地址信息。
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				describe = "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因";
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
				describe = "\"网络不同导致定位失败，请检查网络是否通畅";
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				describe = "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机";
			}
			logMsg(sb.toString(), Latitude, Longitude, addr, type, speed);
		}

	}

	// 访问服务器http post
	private void visitServer(String MacAddr, String jd,String wd, String addr, String type,String time) {

		String inum = "0";

		String httpUrl = User.mainurl + "app/save_app_jwd";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		parameters_userInfo.put("jd", jd);
		parameters_userInfo.put("wd", wd);
		parameters_userInfo.put("time", time);
		parameters_userInfo.put("addr", Escape.escape(addr));
		parameters_userInfo.put("iauto", "0"); // 0表示自动定位,1手动按了定位按钮
		parameters_userInfo.put("up2down", inum); // 0表示上班定位,1表示下班定位
		parameters_userInfo.put("cmaker", MacAddr);
		parameters_userInfo.put("gpstype", Escape.escape(type));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						distanceHelper = new DistanceDatabaseHelper(getApplicationContext(), "distance.db", 1);
						// 更新用户参数
						try {
							JSONObject dataJson = new JSONObject(response);
							if (dataJson.getString("code").equals("0")) {
								distanceHelper
										.getReadableDatabase()
										.execSQL("update distance_table set iflag_text = ? where _id <= ?",
												new Object[] { "1", idFlag });
							}else if("2".equals(dataJson.getString("code"))){
								deleteDatabase("distance.db");
								mLocationClient.stop();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							distanceHelper.close();
						}
					}
				});
	}
}
