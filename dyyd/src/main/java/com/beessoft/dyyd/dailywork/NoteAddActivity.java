package com.beessoft.dyyd.dailywork;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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
import com.beessoft.dyyd.view.DateDialogFragment;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class NoteAddActivity extends BaseActivity
        implements View.OnClickListener
//        ,DateDialogFragment.DateDialogListener
{

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

    private TimePickerView pvTime;
    private int dateType = 0;

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


        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
//        Calendar calendar = Calendar.getInstance();
//        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                if (dateType==0){
                    startEdit.setText(DateUtil.queryDate(date,"yyyy-MM-dd"));
                }else{
                    String start = startEdit.getText().toString();
                    //选择日期早于now
                    if (date.getTime() >= DateUtil.String2Date(start).getTime())
                        endEdit.setText(DateUtil.queryDate(date,"yyyy-MM-dd"));
                    else ToastUtil.toast(context,"结束日期不到早于开始日期");
                }
            }
        });

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
        // Initialize a new date picker dialog fragment
        DialogFragment dFragment = new DateDialogFragment();
        Bundle data = new Bundle();
        switch (v.getId()) {
            case R.id.edt_start:
//                new DatePickerDialog(context,
//                        DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                String yearStr = String.valueOf(year);
//                                String month = String.valueOf(monthOfYear + 1);
//                                String day = String.valueOf(dayOfMonth);
//                                if ((monthOfYear + 1) < 10) {
//                                    month = "0" + month;
//                                }
//                                if (dayOfMonth < 10) {
//                                    day = "0" + day;
//                                }
//                                startEdit.setText(yearStr + "-" + month + "-" + day);
//                            }
//                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
//                data.putString("minDate", );
//                dFragment.setArguments(data);//通过Bundle向Activity中传递值
                // Show the date picker dialog fragment
//                dFragment.show(getFragmentManager(), "Date Picker");
                dateType = 0;
                pvTime.show();
                break;
            case R.id.edt_end:
//                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
//                        DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//                                String yearStr = String.valueOf(year);
//                                String month = String.valueOf(monthOfYear + 1);
//                                String day = String.valueOf(dayOfMonth);
//                                if ((monthOfYear + 1) < 10) {
//                                    month = "0" + month;
//                                }
//                                if (dayOfMonth < 10) {
//                                    day = "0" + day;
//                                }
//                                endEdit.setText(yearStr + "-" + month + "-" + day);
//                            }
//                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//                        .get(Calendar.DAY_OF_MONTH));
//                String start1 = startEdit.getText().toString();
//                if (!TextUtils.isEmpty(start1)){
////                    long timeInMillisSinceEpoch = DateUtil.getTimeInMillisSinceEpoch(start1);
////                    datePickerDialog.getDatePicker().setMinDate(timeInMillisSinceEpoch);
////                    datePickerDialog.show();
//                    data.putString("minDate", start1);
//                    dFragment.setArguments(data);//通过Bundle向Activity中传递值
//                    // Show the date picker dialog fragment
//                    dFragment.show(getFragmentManager(), "Date Picker");
//                }else{
//                    ToastUtil.toast(context,"请先选择开始日期");
//                }
                dateType = 1;
                pvTime.show();
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

//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog, String date) {
//        startEdit.setText(date);
//    }
//
//    @Override
//    public void onDialogNegativeClick(DialogFragment dialog, String date) {
//
//    }
}
