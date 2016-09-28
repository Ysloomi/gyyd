package com.beessoft.dyyd.check;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AskLeaveActivity extends BaseActivity {

    private EditText startEdit;
    private EditText overEdit;
    private EditText reasonEdit;
    private CheckBox amCheckBox;
    private CheckBox pmCheckBox;
    private Spinner typeSpinner;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.askleave, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_query:
                intent.setClass(context, AskLeaveQueryActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_askleave);

        context = AskLeaveActivity.this;

        initView();
        getType();
    }

    public void initView() {

        startEdit = (EditText) findViewById(R.id.start_et);
        startEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
        overEdit = (EditText) findViewById(R.id.over_et);
        overEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
        startEdit.setText(DateUtil.Date());
        overEdit.setText(DateUtil.Date());

        reasonEdit = (EditText) findViewById(R.id.reason_et);
        amCheckBox = (CheckBox) findViewById(R.id.am_cb);
        pmCheckBox = (CheckBox) findViewById(R.id.pm_cb);
        typeSpinner = (Spinner) findViewById(R.id.type_spinner);

        startEdit.setOnClickListener(onClickListener);
        overEdit.setOnClickListener(onClickListener);
        findViewById(R.id.askleave_confirm).setOnClickListener(onClickListener);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar c = Calendar.getInstance();
            switch (v.getId()) {
                case R.id.askleave_confirm:
                    String type = typeSpinner.getSelectedItem().toString();
                    String reason = reasonEdit.getText().toString();
                    if ("请选择".equals(type)) {
                        ToastUtil.toast(context, "请选择请假类型");
                    } else if (TextUtils.isEmpty(reason.trim())) {
                        ToastUtil.toast(context, "请填写请假事由");
                    } else {
                        String start = startEdit.getText().toString();
                        String over = overEdit.getText().toString();

                        String am = "";
                        if (amCheckBox.isChecked()) {
                            am = "true";
                        } else {
                            am = "false";
                        }
                        String pm = "";
                        if (pmCheckBox.isChecked()) {
                            pm = "true";
                        } else {
                            pm = "false";
                        }
                        ProgressDialogUtil.showProgressDialog(context);
                        saveData(start, over, am, pm, reason, type);
                    }
                    break;
                case R.id.start_et:
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
                case R.id.over_et:
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

                                    overEdit.setText(yearStr + "-" + month + "-" + day);
                                }
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                            .get(Calendar.DAY_OF_MONTH)).show();
                    break;
                default:
                    break;
            }

        }
    };


    private void getType() {
        String httpUrl = User.mainurl + "sf/LeaveType";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        client_request.get(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataJson = new JSONObject(response);
                    int code = dataJson.getInt("code");
                    if (code == 0) {
                        JSONArray array = dataJson.getJSONArray("list");
                        List<String> list = new ArrayList<String>();
                        list.add("请选择");
                        for (int j = 0; j < array.length(); j++) {
                            JSONObject obj = array.getJSONObject(j);
                            list.add(obj.getString("name"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                context,
                                R.layout.item_spinner,
                                list);
                        typeSpinner.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveData(String start, String over, String am, String pm, String reason, String type) {

        String httpUrl = User.mainurl + "sf/LeaveSave";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("idate", start);
        parameters_userInfo.put("idate2", over);
        parameters_userInfo.put("am", am);
        parameters_userInfo.put("pm", pm);
        parameters_userInfo.put("cmemo", Escape.escape(reason));
        parameters_userInfo.put("state", Escape.escape(type));
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                ToastUtil.toast(context, "提交成功，待领导审批");
                                finish();
                            } else {
                                ToastUtil.toast(context, "请重新提交");
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