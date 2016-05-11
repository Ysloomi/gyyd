package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;

public class AskLeaveApproveActivity extends BaseActivity {

    private Button agreeButton;
    private Button refuseButton;
    private TextView nameText, startText, overText, daysText, typeText, reasonText;

    private LinearLayout approveManLl;
    private LinearLayout approveTimeLl;
    private LinearLayout approveResultLl;

    private String usercode;
    private String intodate;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.askapprove);

        context = AskLeaveApproveActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        initView();

        HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("hashmap");
        nameText.setText(map.get("username"));
        startText.setText(map.get("start"));
        overText.setText(map.get("over"));
        usercode = map.get("usercode");
        String days = map.get("days");

        if ("0.5天".equals(days)) {
            if (map.get("am").equals("0")) {
                days += "  上午";
            } else {
                days += "  下午";
            }
        }

        daysText.setText(days);
        typeText.setText(map.get("type"));
        reasonText.setText(map.get("cmemo"));
        intodate = map.get("intodate");

        if ("query".equals(getIntent().getStringExtra("query"))) {
            setTitle("请假查询");
            agreeButton.setVisibility(View.GONE);
            refuseButton.setVisibility(View.GONE);
//            approveManLl.setVisibility(View.VISIBLE);
//            approveTimeLl.setVisibility(View.VISIBLE);
//            approveResultLl.setVisibility(View.VISIBLE);
        } else {
            agreeButton.setVisibility(View.VISIBLE);
            refuseButton.setVisibility(View.VISIBLE);
//            approveManLl.setVisibility(View.GONE);
//            approveTimeLl.setVisibility(View.GONE);
//            approveResultLl.setVisibility(View.GONE);
        }
    }

    public void initView() {

        approveManLl = (LinearLayout) findViewById(R.id.ll_approveman) ;
        approveTimeLl = (LinearLayout) findViewById(R.id.ll_approve_time) ;
        approveResultLl = (LinearLayout) findViewById(R.id.ll_approve_result) ;

        nameText = (TextView) findViewById(R.id.person_text);
        startText = (TextView) findViewById(R.id.start_text);
        overText = (TextView) findViewById(R.id.over_text);
        daysText = (TextView) findViewById(R.id.days_text);
        typeText = (TextView) findViewById(R.id.type_text);
        reasonText = (TextView) findViewById(R.id.explain_text);

//        approvenameText = (TextView) findViewById(R.id.approveman_text);
//        approvetimeText = (TextView) findViewById(R.id.approvetime_text);
//        approveresultText = (TextView) findViewById(R.id.approveresult_text);

        agreeButton = (Button) findViewById(R.id.agree_button);
        refuseButton = (Button) findViewById(R.id.refuse_button);
        agreeButton.setOnClickListener(onClickListener);
        refuseButton.setOnClickListener(onClickListener);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.agree_button:
                    ProgressDialogUtil.showProgressDialog(context);
                    visitServer_comfirm("yes", "");
                    break;
                case R.id.refuse_button:
                    inputExamineDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("InflateParams")
    private void inputExamineDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.unagree, null);

        final EditText editText1 = (EditText) view.findViewById(R.id.reason_text);

        final AlertDialog myDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setCancelable(false)
                .create();

        myDialog.setTitle("请输入不同意原因");
        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String unagree_reason = editText1.getText().toString();
                        if (TextUtils.isEmpty(unagree_reason.trim())) {
                            ToastUtil.toast(AskLeaveApproveActivity.this, "请填写不同意原因");
                        } else {
                            ProgressDialogUtil.showProgressDialog(context);
                            visitServer_comfirm("no", unagree_reason);
                            myDialog.dismiss();
                        }
                    }
                });
                Button button1 = myDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                    }
                });
            }
        });
        myDialog.show();
    }

    private void visitServer_comfirm(String btn, String unagree_reason) {

        String httpUrl = User.mainurl + "sf/LeaveCheckSave";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", usercode);
        parameters_userInfo.put("intodate", intodate);
        parameters_userInfo.put("btn", btn);
        parameters_userInfo.put("reason", Escape.escape(unagree_reason));

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code==0) {
                                ToastUtil.toast(context, "审批成功");
                                finish();
                            } else {
                                ToastUtil.toast(context,"请重新上传");
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
                    }
                });
    }
}