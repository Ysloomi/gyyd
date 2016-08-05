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
import com.beessoft.dyyd.check.SpecialActivity;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class DailyWorkFragment extends Fragment implements OnClickListener {

    private Button myworkButton, mileageBtn, queryButton, noticeBtn, workQueryBtn,
            mymemoBtn, workLocationButton, arrangeBtn;
    private Button photoBtn;
    private Button checkQueryBtn;
    private Button noteBTn;
    private Button visitQueryBtn;
    private TextView noticeNumTxt;
    private TextView todoTxt;
    private Button specialBtn;
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

        noticeNumTxt = (TextView) view.findViewById(R.id.notice_num);
        todoTxt = (TextView) view.findViewById(R.id.todo_num);

        visitQueryBtn = (Button) view.findViewById(R.id.btn_visitquery);
        myworkButton = (Button) view.findViewById(R.id.btn_work);
        mileageBtn = (Button) view.findViewById(R.id.btn_mileage);
        queryButton = (Button) view.findViewById(R.id.btn_approve_query);
        noticeBtn = (Button) view.findViewById(R.id.btn_notice);
        workQueryBtn = (Button) view.findViewById(R.id.btn_work_query);
        mymemoBtn = (Button) view.findViewById(R.id.btn_memo);
        workLocationButton = (Button) view.findViewById(R.id.btn_location);
        arrangeBtn = (Button) view.findViewById(R.id.btn_arrange);
        specialBtn = (Button) view.findViewById(R.id.btn_special);

        photoBtn = (Button) view.findViewById(R.id.photo_button);
        checkQueryBtn = (Button) view.findViewById(R.id.checkquery_button);
        noteBTn = (Button) view.findViewById(R.id.btn_note);

        photoBtn.setOnClickListener(DailyWorkFragment.this);
        checkQueryBtn.setOnClickListener(DailyWorkFragment.this);
        noteBTn.setOnClickListener(DailyWorkFragment.this);

        visitQueryBtn.setOnClickListener(DailyWorkFragment.this);
        myworkButton.setOnClickListener(DailyWorkFragment.this);
        mileageBtn.setOnClickListener(DailyWorkFragment.this);
        queryButton.setOnClickListener(DailyWorkFragment.this);
        noticeBtn.setOnClickListener(DailyWorkFragment.this);
        workQueryBtn.setOnClickListener(DailyWorkFragment.this);
        mymemoBtn.setOnClickListener(DailyWorkFragment.this);
        workLocationButton.setOnClickListener(DailyWorkFragment.this);
        arrangeBtn.setOnClickListener(DailyWorkFragment.this);
        specialBtn.setOnClickListener(DailyWorkFragment.this);

        noticeNumTxt.setVisibility(View.GONE);
        todoTxt.setVisibility(View.GONE);
        queryButton.setVisibility(View.GONE);
        noticeBtn.setVisibility(View.GONE);
        workQueryBtn.setVisibility(View.GONE);
        mymemoBtn.setVisibility(View.GONE);
        arrangeBtn.setVisibility(View.GONE);
        specialBtn.setVisibility(View.GONE);

        GetInfo.getButtonRole(context, photoBtn, "5", "");
        GetInfo.getButtonRole(context, myworkButton, "6", "");
        GetInfo.getButtonRole(context, checkQueryBtn, "7", "");
        GetInfo.getButtonRole(context, workLocationButton, "8", "");
        GetInfo.getButtonRole(context, noteBTn, "9", "");
        GetInfo.getButtonRole(context, visitQueryBtn, "11", "");
        GetInfo.getButtonRole(context, mileageBtn, "12", "");
    }


    @Override
    public void onResume() {
        super.onResume();
        if (GetInfo.getIfSf(context)) {
            visitServer();
        }
    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/notice";

        String mac = GetInfo.getIMEI(getActivity());
        String username = GetInfo.getUserName(context);
        String ifSf = GetInfo.getIfSf(context) ? "0" : "1";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String noticenum = dataJson.getString("icount");
                            String mesnum = dataJson.getString("mesnum");
                            PreferenceUtil.write(context, "noticenum", noticenum);

                            noticeNumTxt.setText(noticenum);
                            todoTxt.setText(mesnum);

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
            case R.id.btn_work:
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
            case R.id.btn_location:
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
                    } else {
                        intent.setClass(context, NoteQueryActivity.class);
                    }
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.btn_visitquery:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
                    intent.setClass(context, VisitQueryListActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;

            case R.id.btn_mileage:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                    intent.setClass(context, MyMileageActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.btn_approve_query:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(context, ApproveQueryListActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            case R.id.btn_notice:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(getActivity(), NoticeListActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            case R.id.btn_work_query:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(getActivity(), WorkQueryListActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            case R.id.btn_memo:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(getActivity(), MyMemoListActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            case R.id.btn_arrange:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(getActivity(), ArrangeActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            case R.id.btn_special:
//                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                intent.setClass(getActivity(), SpecialActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
                break;
            default:
                break;
        }
    }


}