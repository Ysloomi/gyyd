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

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DailyWorkFragment extends Fragment {

    @BindView(R.id.photo_btn)
    Button photoBtn;
    @BindView(R.id.mywork_btn)
    Button myworkBtn;
    @BindView(R.id.checkquery_btn)
    Button checkqueryBtn;
    @BindView(R.id.mileage_btn)
    Button mileageBtn;
    @BindView(R.id.location_btn)
    Button locationBtn;
    @BindView(R.id.note_btn)
    Button noteBtn;
    @BindView(R.id.visitquery_btn)
    Button visitqueryBtn;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dailywork = inflater.inflate(R.layout.fragment_dailywork, container, false);
        context = getActivity();
        initView(dailywork);
        ButterKnife.bind(this, dailywork);
        return dailywork;
    }

    private void initView(View view) {

        GetInfo.getButtonRole(context, photoBtn, "5", "");
        GetInfo.getButtonRole(context, myworkBtn, "6", "");
        GetInfo.getButtonRole(context, checkqueryBtn, "7", "");
        GetInfo.getButtonRole(context, locationBtn, "8", "");
        GetInfo.getButtonRole(context, noteBtn, "9", "");
        GetInfo.getButtonRole(context, visitqueryBtn, "11", "");
        GetInfo.getButtonRole(context, mileageBtn, "12", "");
    }

    @OnClick({R.id.photo_btn, R.id.mywork_btn, R.id.checkquery_btn, R.id.mileage_btn, R.id.location_btn, R.id.note_btn, R.id.visitquery_btn})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.photo_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode5"))) {
                    intent.setClass(context, PhotoActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.mywork_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode6"))) {
                    intent.setClass(context, MyWorkActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.checkquery_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode7"))) {
                    intent.setClass(context, NewCheckQueryActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.mileage_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode12"))) {
                    intent.setClass(context, MyMileageActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.location_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode8"))) {
                    intent.setClass(context, WorkLocationActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
            case R.id.note_btn:
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
            case R.id.visitquery_btn:
                if ("0".equals(PreferenceUtil.readString(context, "rolebuttoncode11"))) {
                    intent.setClass(context, VisitQueryListActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
                break;
        }
    }
}