package com.beessoft.dyyd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.baidu.location.LocationClient;
import com.beessoft.dyyd.adapter.MainAdapter;
import com.beessoft.dyyd.check.CheckInActivity;
import com.beessoft.dyyd.check.CheckOutActivity;
import com.beessoft.dyyd.check.VisitLeaveActivity;
import com.beessoft.dyyd.check.VisitReachActivity;
import com.beessoft.dyyd.dailywork.NewCheckQueryActivity;
import com.beessoft.dyyd.dailywork.VisitQueryListActivity;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.AlarmUtils;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DialogActivity extends BaseActivity {

    private static String INTENT_KEY = "typeList";

    private String itype;
    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private LocationClient mLocationClient;
    private MainAdapter mainAdapter;
    private List<String> typeList = new ArrayList<>();

    public static void navToDialog(Context context, ArrayList<String> typeList) {
        Intent intent = new Intent(context, DialogActivity.class);
        intent.putStringArrayListExtra(INTENT_KEY, typeList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_recyclerview);


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.x = -20;
        params.height = (height) / 3;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.y = -10;

        this.getWindow().setAttributes(params);

        context = DialogActivity.this;
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;


        typeList = getIntent().getStringArrayListExtra(INTENT_KEY);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        mainAdapter = new MainAdapter(context, typeList);
        recyclerView.setAdapter(mainAdapter);
        mainAdapter.setOnItemClickLitener(new MainAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                String a = typeList.get(position);
                switch (a) {
                    case MainActivity.Type.CHECKIN:
                        itype = MainActivity.Type.CHECKIN;
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))) {
                            visitServer();
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.CHECKIN_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                    case MainActivity.Type.CHECKOUT:
                        itype = MainActivity.Type.CHECKOUT;
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))) {
                            visitServer();
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.CHECKOUT_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                    case MainActivity.Type.CHECKQUERY:
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
                            intent.setClass(context, NewCheckQueryActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.CHECKQUERY_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                    case MainActivity.Type.REACH:
                        itype = MainActivity.Type.REACH;
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))) {
                            visitServer();
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.REACH_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                    case MainActivity.Type.LEAVE:
                        itype = MainActivity.Type.LEAVE;
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))) {
                            visitServer();
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.LEAVE_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                    case MainActivity.Type.VISITQUERY:
                        if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
                            intent.setClass(context, VisitQueryListActivity.class);
                            startActivity(intent);
                        } else {
                            ToastUtil.toast(context, "无权限");
                        }
                        break;
                    case MainActivity.Type.VISITQUERY_UNTAP:
                        ToastUtil.toast(context, "无权限");
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    private void visitServer() {

        String httpUrl = User.mainurl + "sf/startwork_do";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String code = dataJson.getString("icount");
                            Intent intent = new Intent();
                            if (MainActivity.Type.CHECKIN.equals(itype) || MainActivity.Type.CHECKOUT.equals(itype)) {
                                if ("0".equals(code)) {
                                    if (MainActivity.Type.CHECKIN.equals(itype)) {
                                        intent.setClass(context, CheckInActivity.class);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.toast(context, "请签到");
                                    }
                                } else if ("1".equals(code)) {
                                    switch (itype) {
                                        case MainActivity.Type.CHECKIN:
                                            ToastUtil.toast(context, "已签到");
                                            break;
                                        case MainActivity.Type.CHECKOUT:
                                            if (GetInfo.getIfSf(context)) {
                                                if ("1".equals(dataJson.getString("notice"))) {
                                                    ToastUtil.toast(context, "通知中有未阅读的文件请处理");
                                                } else {
                                                    if ("1".equals(dataJson.getString("bin"))) {
                                                        ToastUtil.toast(context, "签到待审批不能签退");
                                                    } else {
                                                        startActivity(new Intent().setClass(context, CheckOutActivity.class));
                                                    }
                                                }
                                            } else {
                                                intent.setClass(context, CheckOutActivity.class);
                                                startActivity(intent);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                } else if ("2".equals(code)) {
                                    ToastUtil.toast(context, "已签退");
                                }
                            }
                            if (MainActivity.Type.REACH.equals(itype) || MainActivity.Type.LEAVE.equals(itype)) {
                                switch (itype) {
                                    case MainActivity.Type.REACH:
                                        if ("0".equals(dataJson.getString("visit"))) {
                                            intent.setClass(context, VisitReachActivity.class);
                                            startActivity(intent);
                                        } else {
                                            ToastUtil.toast(context, "尚有到达现场，请先离开");
                                        }
                                        break;
                                    case MainActivity.Type.LEAVE:
                                        if ("1".equals(dataJson.getString("visit"))) {
                                            intent.setClass(context, VisitLeaveActivity.class);
                                            startActivity(intent);
                                        } else {
                                            ToastUtil.toast(context, "请先拜访客户");
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
