package com.beessoft.dyyd.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.LocationClient;
import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.db.DistanceDatabaseHelper;
import com.beessoft.dyyd.utils.AlarmUtils;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.Gps;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class CheckFragment extends Fragment implements View.OnClickListener {

    private Button checkInButton, checkOutButton, visitReachBtn, visitLeaveBtn;
    private Button collectBtn;
    private Button askLeaveBtn;

    private String itype;
    private Context context;

    private DistanceDatabaseHelper distanceHelper; // 数据库帮助类
    private LocationClient mLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View check = inflater.inflate(R.layout.fragment_check, container, false);
        context = getActivity();
        initView(check);
        mLocationClient = ((LocationApplication) getActivity().getApplication()).mLocationClient;
        return check;
    }

    private void initView(View view) {

        checkInButton = (Button) view.findViewById(R.id.checkin_button);
        checkOutButton = (Button) view.findViewById(R.id.checkout_button);
        visitReachBtn = (Button) view.findViewById(R.id.visit_button);
        visitLeaveBtn = (Button) view.findViewById(R.id.leave_button);
        collectBtn = (Button) view.findViewById(R.id.collect_button);
        askLeaveBtn = (Button) view.findViewById(R.id.askleave_button);

        checkInButton.setOnClickListener(CheckFragment.this);
        checkOutButton.setOnClickListener(CheckFragment.this);
        visitReachBtn.setOnClickListener(CheckFragment.this);
        visitLeaveBtn.setOnClickListener(CheckFragment.this);
        collectBtn.setOnClickListener(CheckFragment.this);
        askLeaveBtn.setOnClickListener(CheckFragment.this);

        GetInfo.getButtonRole(context, checkInButton, "1", "checkin");
        GetInfo.getButtonRole(context, checkOutButton, "1", "checkout");
        GetInfo.getButtonRole(context, visitReachBtn, "2", "reach");
        GetInfo.getButtonRole(context, visitLeaveBtn, "2", "leave");
        GetInfo.getButtonRole(context, collectBtn, "3", "");
        GetInfo.getButtonRole(context, askLeaveBtn, "4", "");

    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/startwork_do";

        String mac = GetInfo.getIMEI(context);
        String username = GetInfo.getUserName(context);

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
                            if ("checkin".equals(itype) || "checkout".equals(itype)) {
                                if ("0".equals(code)) {
                                    if ("checkin".equals(itype)) {
                                        intent.setClass(context, CheckInActivity.class);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.toast(context, "请签到");
                                    }
                                } else if ("1".equals(code)) {

                                    if (!Gps.exist(context, "distance.db")) {
                                        distanceHelper = new DistanceDatabaseHelper(context.getApplicationContext(), "distance.db", 1);

                                        String time = DateUtil.getDateLoca();

                                        distanceHelper
                                                .getReadableDatabase()
                                                .execSQL(
                                                        "insert into distance_table values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                                        new String[]{time, "0",
                                                                "0", "0", "0",
                                                                "0", "", "0", "0"});
                                        distanceHelper.close();
                                        Gps.GPS_do(mLocationClient, 8000);// 启动百度定位的8秒轮询

                                        AlarmUtils.doalarm(context);
                                    }
                                    switch (itype) {
                                        case "checkin":
                                            ToastUtil.toast(context, "已签到");
                                            break;
                                        case "checkout":

                                            if (GetInfo.getIfSf(context)) {
                                                if ("1".equals(dataJson.getString("notice"))) {
                                                    ToastUtil.toast(context, "通知中有未阅读的文件请处理");
                                                } else {
                                                    if ("1".equals(dataJson.getString("bin"))) {
                                                        ToastUtil.toast(context, "签到待审批不能签退");
                                                    } else {
                                                        startActivity(new Intent()
                                                                .setClass(
                                                                        getActivity(),
                                                                        CheckOutActivity.class));
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
                                    ToastUtil.toast(getActivity(), "已签退");
                                }
                            }
                            if ("reach".equals(itype) || "leave".equals(itype)) {
                                switch (itype) {
                                    case "reach":
                                        if ("0".equals(dataJson.getString("visit"))) {
                                            intent.setClass(context, VisitReachActivity.class);
                                            startActivity(intent);
                                        } else {
                                            ToastUtil.toast(context, "尚有到达现场，请先离开");
                                        }
                                        break;
                                    case "leave":
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkin_button:
                itype = "checkin";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))) {
                    visitServer();
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.checkout_button:
                itype = "checkout";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode1"))) {
                    visitServer();
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.visit_button:
                itype = "reach";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))) {
                    visitServer();
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.leave_button:
                itype = "leave";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode2"))) {
                    visitServer();
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.collect_button:
                itype = "collect";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode3"))) {
                    Intent intent = new Intent(context, CollectActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.askleave_button:
                itype = "askleave";
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode4"))) {
                    Intent intent1 = new Intent(context, AskLeaveActivity.class);
                    startActivity(intent1);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            default:
                break;
        }
    }
}