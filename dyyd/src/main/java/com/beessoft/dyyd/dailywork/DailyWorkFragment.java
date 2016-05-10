package com.beessoft.dyyd.dailywork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class DailyWorkFragment extends Fragment implements OnClickListener {

    private Button visitQueryBtn, myworkButton, mileageBtn, queryButton, noticeBtn, workQueryBtn,
            mymemoBtn, workLocationButton, arrangeBtn;
    private Button photoBtn;
    private Button checkQueryBtn;
    private Button noteBTn;
    private TextView textView;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dailywork = inflater.inflate(R.layout.fragment_dailywork, container, false);
        context = getActivity();
        initView(dailywork);
        return dailywork;
    }

    private void initView(View view) {

        textView = (TextView) view.findViewById(R.id.notice_num);

        visitQueryBtn = (Button) view.findViewById(R.id.visitquery_button);
        myworkButton = (Button) view.findViewById(R.id.work_button);
        mileageBtn = (Button) view.findViewById(R.id.mileage_button);
        queryButton = (Button) view.findViewById(R.id.query_button);
        noticeBtn = (Button) view.findViewById(R.id.notice_button);
        workQueryBtn = (Button) view.findViewById(R.id.workquery_button);
        mymemoBtn = (Button) view.findViewById(R.id.mymemo_button);
        workLocationButton = (Button) view.findViewById(R.id.worklocation_button);
        arrangeBtn = (Button) view.findViewById(R.id.arrange_button);

        photoBtn = (Button) view.findViewById(R.id.photo_button);
        checkQueryBtn = (Button) view.findViewById(R.id.checkquery_button);
        noteBTn = (Button) view.findViewById(R.id.btn_note);
        noteBTn = (Button) view.findViewById(R.id.btn_note);

        photoBtn.setOnClickListener(this);
        myworkButton.setOnClickListener(this);
        checkQueryBtn.setOnClickListener(this);
        workLocationButton.setOnClickListener(this);
        noteBTn.setOnClickListener(this);

        GetInfo.getButtonRole(context, photoBtn, "5","");
        GetInfo.getButtonRole(context, myworkButton, "6","");
        GetInfo.getButtonRole(context, checkQueryBtn, "7","");
        GetInfo.getButtonRole(context, workLocationButton, "8","");
        GetInfo.getButtonRole(context, noteBTn, "9","");

//        visitQueryBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if ("0".equals(role) || "1".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), VisitQueryListActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        mileageBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if ("0".equals(role) || "1".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), MyMileageActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        queryButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if ("0".equals(role) || "1".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(),
//                            ApproveQueryListActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        noticeBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (!"4".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), NoticeListActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        workQueryBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if ("0".equals(role) || "1".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), WorkQueryListActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        mymemoBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (!"4".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(getActivity(), MyMemoListActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//        });
//
//        arrangeBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if ("1".equals(role)) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, ArrangeActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//                            .show();
//                }
//
//            }
//        });
    }

    private void visitServer(final Context context) {
        String httpUrl = User.mainurl + "sf/notice";
        String mac = GetInfo.getIMEI(getActivity());
        String pass = GetInfo.getPass(context);
        String username = GetInfo.getUserName(context);
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("pass", pass);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String noticenum = dataJson.getString("icount");
                            PreferenceUtil.write(context, "noticenum", noticenum);
                            textView.setText(noticenum);
                            String online = dataJson.getString("pernum");
                            PreferenceUtil.write(context, "online", online);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.photo_button:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode5"))) {
                    intent.setClass(context, PhotoActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.work_button:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode6"))) {
                    intent.setClass(context, MyWorkActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.checkquery_button:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
                    intent.setClass(context, NewCheckQueryActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.worklocation_button:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode8"))) {
                    intent.setClass(context, WorkLocationActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.btn_note:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode9"))) {
                    if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode10"))) {
                        intent.setClass(context, NoteActivity.class);
                    }else{
                        intent.setClass(context, NoteQueryActivity.class);
                    }
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            default:
                break;
        }
    }
}