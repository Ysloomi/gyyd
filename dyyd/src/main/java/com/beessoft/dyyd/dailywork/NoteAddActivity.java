package com.beessoft.dyyd.dailywork;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class NoteAddActivity extends BaseActivity
        implements View.OnClickListener{

    private TextView departText;
    private TextView nameText;
    private TextView addrText;

    private EditText startEdit;
    private EditText endEdit;
    private EditText planEdit;

    private String addr;
    private String addrCode;
    private String from;

    private Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        context = NoteAddActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        Bundle b = getIntent().getExtras();
        note = b.getParcelable("note");
        from = b.getString("from");

        initView();
        initData();

    }

    private void initView() {

        departText = (TextView) findViewById(R.id.txt_depart);
        nameText = (TextView) findViewById(R.id.txt_name);
        addrText = (TextView) findViewById(R.id.txt_addr);

        startEdit = (EditText) findViewById(R.id.edt_start);
        startEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
        endEdit = (EditText) findViewById(R.id.edt_end);
        endEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘

        planEdit = (EditText) findViewById(R.id.edt_plan);

        startEdit.setOnClickListener(this);
        endEdit.setOnClickListener(this);
        findViewById(R.id.txt_addr).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }


    private void initData() {
        if ("change".equals(from)){
            Bundle b = getIntent().getExtras();
            addr = b.getString("addr");
            addrCode = b.getString("addrCode");
            departText.setText(note.getDepart());
            nameText.setText(note.getName());
            startEdit.setText(note.getStart());
            endEdit.setText(note.getEnd());
            addrText.setText(note.getAddr());
            planEdit.setText(note.getPlan());
        }else{
            addr = "";
            addrCode = "";
            departText.setText(GetInfo.getDepart(context));
            nameText.setText(GetInfo.getName(context));
            startEdit.setText(DateUtil.Date());
            endEdit.setText(DateUtil.behindWeekDate());
        }
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.edt_start:
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String yearStr = String.valueOf(year);
                                String month = String.valueOf(monthOfYear + 1);
                                String day = String.valueOf(dayOfMonth);
                                if ((monthOfYear + 1) < 10) {
                                    month = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    day = "0" + day;
                                }
                                startEdit.setText(yearStr + "-" + month + "-" + day);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.edt_end:
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String yearStr = String.valueOf(year);
                                String month = String.valueOf(monthOfYear + 1);
                                String day = String.valueOf(dayOfMonth);
                                if ((monthOfYear + 1) < 10) {
                                    month = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    day = "0" + day;
                                }
                                endEdit.setText(yearStr + "-" + month + "-" + day);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                        .get(Calendar.DAY_OF_MONTH));
                String start1 = startEdit.getText().toString();
                if (!TextUtils.isEmpty(start1)){
                    long timeInMillisSinceEpoch = DateUtil.getTimeInMillisSinceEpoch(start1);
                    datePickerDialog.getDatePicker().setMinDate(timeInMillisSinceEpoch);
                    datePickerDialog.show();
                }else{
                    ToastUtil.toast(context,"请先选择开始日期");
                }
                break;
            case R.id.txt_addr:
                Intent intent = new Intent();
                intent.setClass(context, NoteAddrActivity.class);
                intent.putExtra("addr",addr);
                intent.putExtra("addrCode",addrCode);
                startActivityForResult(intent,0);
                break;
            case R.id.btn_submit:
                String start = startEdit.getText().toString();
                String end = endEdit.getText().toString();
                String plan = planEdit.getText().toString();
                if (!Tools.isEmpty(plan)){
                    saveData(start,end,plan);
                }else {
                    ToastUtil.toast(context,"请填写计划");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            addr = data.getStringExtra("addr");
            addrCode = data.getStringExtra("addrCode");
            addrText.setText(addr);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveData(String start, String end, String plan) {

        String httpUrl = User.mainurl+"notePad/MyNoteServlet";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        parameters_userInfo.put("type", "plan");

        parameters_userInfo.put("beginTime", start);
        parameters_userInfo.put("endTime", end);
//        try {
//            addr = URLEncoder.encode(addr,"UTF-8");
//            plan = URLEncoder.encode(plan,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace(System.out);
//        }
        parameters_userInfo.put("ccusname", addr);
        parameters_userInfo.put("ccuscode", addrCode);
        parameters_userInfo.put("Inter", "app");
        parameters_userInfo.put("plan", plan);
        if ("change".equals(from)){
            parameters_userInfo.put("med", "edit");
            parameters_userInfo.put("id", note.getId());
            parameters_userInfo.put("rdcode", note.getRdCode());
        }else{
            parameters_userInfo.put("med", "add");
        }

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code==0) {
                                finish();
                                ToastUtil.toast(context, "上传成功");
                            } else {
                                ToastUtil.toast(context, "请重试");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }
}
