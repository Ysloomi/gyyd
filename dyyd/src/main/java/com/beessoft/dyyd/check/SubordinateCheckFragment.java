package com.beessoft.dyyd.check;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.dailywork.CheckApproveActivity;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.timessquare.CalendarView;
import com.squareup.timessquare.CalendarView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarView.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubordinateCheckFragment extends Fragment {

    @BindView(R.id.subcheck_act)
    AutoCompleteTextView subcheckAct;
    @BindView(R.id.calendar)
    CalendarView calendar;
    @BindView(R.id.hoilyday_txt)
    TextView hoilydayTxt;

    private String mac, year, month, btn = "1", psn = "", idGet, idate, state,
            flag = "";
    private String username;
    private String ifSf;
    private Context context;
    private Date dateMonth;

    List<Calendar> feeds = new ArrayList<Calendar>();
    List<Calendar> calendars = new ArrayList<Calendar>();
    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View check = inflater.inflate(R.layout.fragment_subcheck, container, false);
        ButterKnife.bind(this, check);
        context = getActivity();
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);
        ifSf = GetInfo.getIfSf(context) ? "0" : "1";
        return check;
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Calendar cal = Calendar.getInstance();
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        String role = GetInfo.getRole(getActivity());

        if ("1".equals(role)) {
            initEvent();
            GetJSON.visitServer_GetInfo(context, subcheckAct, mac, username);
        } else {
            calendar.setVisibility(View.GONE);
            hoilydayTxt.setBackground(null);// 去除背景
            hoilydayTxt.setText("没有权限");
            hoilydayTxt.setTextColor(0xff000000);
        }

        subcheckAct.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                subcheckAct.showDropDown();// 显示下拉列表
                return false;
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void initEvent() {
        // 设置日期点击监听器
        calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

            @Override
            public void onDateUnselected(Date date) {

            }

            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String dateSelect = format.format(date);
                String idTarget = map.get(dateSelect);

                if (null != idTarget) {
                    Intent intent = new Intent(getActivity(),
                            CheckApproveActivity.class);
                    intent.putExtra("idTarget", idTarget);
                    intent.putExtra("query", "query");
                    startActivity(intent);
                }
            }
        });
        // 设置月份改变监听器
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onChangedToPreMonth(Date dateOfMonth) {
                // new GetCalendarsOfMonthTask(dateOfMonth).execute();
                getVerser(dateOfMonth);
            }

            @Override
            public void onChangedToNextMonth(Date dateOfMonth) {
                // new GetCalendarsOfMonthTask(dateOfMonth).execute();
                getVerser(dateOfMonth);
            }

            public void getVerser(Date dateOfMonth) {
                SimpleDateFormat format = new SimpleDateFormat("MM");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                year = yearFormat.format(dateOfMonth);
                month = format.format(dateOfMonth);
                flag = "monthChange";
                dateMonth = dateOfMonth;
                if ("[全部人员]".equals(psn)) {
                    GetJSON.visitServer_GetInfo(context, subcheckAct, mac, username);
                } else {
                    ProgressDialogUtil.showProgressDialog(context);
                    visitServer();
                }
            }
        });
        // new
        // GetCalendarsOfMonthTask(Calendar.getInstance().getTime()).execute();
    }

    @OnClick(R.id.query_txt)
    public void onClick() {
        // 清空列表
        cleanlist();

        psn = subcheckAct.getText().toString();

        visitServer();

        Tools.closeInput(getActivity(), subcheckAct);//关闭键盘
    }


    class GetCalendarsOfMonthTask extends AsyncTask<Object, Object, String> {
        Date dateOfMonth;
        List<List<Calendar>> calsList;

        public GetCalendarsOfMonthTask(Date dateOfMonth) {
            this.dateOfMonth = dateOfMonth;
        }

        @Override
        protected String doInBackground(Object... params) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateOfMonth);
            calsList = getCalendarsOfMonth(cal.get(Calendar.YEAR) + "",
                    (cal.get(Calendar.MONTH) + 1) + "");
            // calsList = data;
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (calsList != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateOfMonth);
                // 在某些天上标记红点，或者字体颜色的代码
                calendar.markDatesOfMonth(cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), false, true, calsList.get(0));
                if (calsList.size() > 1) {
                    calendar.markDatesOfMonth(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH), true, false,
                            calsList.get(1));
                }
            }
        }
    }

    private List<List<Calendar>> getCalendarsOfMonth(String year, String month) {

        List<List<Calendar>> data = new ArrayList<List<Calendar>>();
        data.add(feeds);
        data.add(calendars);

        return data;
    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/kqlist";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("year", year);
        parameters_userInfo.put("month", month);
        parameters_userInfo.put("btn", btn);//0为个人考勤，1为下属考勤
        parameters_userInfo.put("psn", Escape.escape(psn));
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    idGet = obj.getString("id");
                                    idate = obj.getString("idate");
                                    state = obj.getString("state");

                                    map.put(idate, idGet);

                                    String[] str = idate.split("\\-");
                                    int yearGet = Integer.valueOf(str[0]);
                                    int monthGet = Integer.valueOf(str[1]) - 1;
                                    int dayGet = Integer.valueOf(str[2]);

                                    Calendar cal = Calendar.getInstance();
                                    cal.set(yearGet, monthGet, dayGet, 0, 0, 0);
                                    feeds.add(cal);

                                    if ("1".equals(state)) {

                                        String idateState = obj
                                                .getString("idate");
                                        String[] strState = idateState
                                                .split("\\-");

                                        int yearState = Integer.valueOf(strState[0]);
                                        int monthState = Integer.valueOf(strState[1]) - 1;
                                        int dayState = Integer.valueOf(strState[2]);

                                        Calendar calState = Calendar.getInstance();
                                        calState.set(yearState, monthState, dayState, 0, 0, 0);
                                        calendars.add(calState);
                                    }
                                }
                            } else if (1 == code) {
                                Toast.makeText(getActivity(), "没有数据",
                                        Toast.LENGTH_SHORT).show();
                            } else if (-2 == code) {
                                Toast.makeText(getActivity(), "无权限，请与管理员联系",
                                        Toast.LENGTH_SHORT).show();
                            }

                            hoilydayTxt.setText(dataJson.getString("kqset"));

                            if ("monthChange".equals(flag)) {
                                new GetCalendarsOfMonthTask(dateMonth)
                                        .execute();
                            } else {
                                new GetCalendarsOfMonthTask(Calendar
                                        .getInstance().getTime()).execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }

    // 清除处理
    private void cleanlist() {
        int size = feeds.size();
        if (size > 0) {
            feeds.removeAll(feeds);
            new GetCalendarsOfMonthTask(Calendar.getInstance().getTime())
                    .execute();
        }
        int size1 = calendars.size();
        if (size1 > 0) {
            calendars.removeAll(calendars);
            new GetCalendarsOfMonthTask(Calendar.getInstance().getTime())
                    .execute();
        }
        int size2 = map.size();
        if (size2 > 0) {
            map.clear();
        }
    }
}