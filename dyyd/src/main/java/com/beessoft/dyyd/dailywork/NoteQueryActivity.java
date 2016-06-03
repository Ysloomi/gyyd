package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.NoteAdapter;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.utils.ArrayAdapter;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.bigkoo.pickerview.TimePickerView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteQueryActivity extends BaseActivity implements View.OnClickListener,OnItemSelectedListener{

    private Spinner departSpn;
    private Spinner nameSpn;

    private List<String> departs = new ArrayList<>();
    private List<String> departCodes = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<String> nameCodes = new ArrayList<>();

    private EditText startEdit;
    private EditText endEdit;

    private PullToRefreshListView mPullRefreshListView;
    private List<Note> notes = new ArrayList<>();
    private NoteAdapter listAdapter;

    private int currentPage = 1;

    private String depart;
    private String departCode;
    private String name;
    private String nameCode;
    private String start;
    private String end;

    private String from;
    private String type;


    private TimePickerView pvTime;
    private int dateType = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_query_actions, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_search:
                if (!Tools.isEmpty(nameCode)){
                    ProgressDialogUtil.showProgressDialog(context);
                    start = startEdit.getText().toString();
                    end = endEdit.getText().toString();
                    visitRefresh();
                }else{
                    ToastUtil.toast(context,"请选择到人员");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_query);

        context = NoteQueryActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        initView();
        initData();

        listAdapter = new NoteAdapter(context, notes);

        // mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        // 在刷新时允许继续滑动
        mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
        // mPullRefreshListView.getMode();//得到模式
        // 上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setAdapter(listAdapter);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        context,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                start = "";
                end = "";
                visitRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        context,
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                visitLoad();
            }
        });

        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notes.get(position - 1);
                Intent intent = new Intent();
                intent.setClass(context, NoteQueryDetailActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("note", note);
                b.putString("from", "query");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

//        visitRefresh();

        type = "dep";
        getData();
    }


    private void initView() {

        mPullRefreshListView = (PullToRefreshListView)  findViewById(R.id.pull_refresh_list);

        departSpn = (Spinner) findViewById(R.id.spn_depart);
        nameSpn = (Spinner) findViewById(R.id.spn_name);

        startEdit = (EditText) findViewById(R.id.edt_start);
        startEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘

        endEdit = (EditText) findViewById(R.id.edt_end);
        endEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘


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

        departSpn.setOnItemSelectedListener(this);
        nameSpn.setOnItemSelectedListener(this);
        startEdit.setOnClickListener(this);
        endEdit.setOnClickListener(this);
    }


    private void initData() {
        startEdit.setText(DateUtil.forwardWeekDate());
        endEdit.setText(DateUtil.Date());
    }


    private void visitRefresh() {

        currentPage = 1;

        String httpUrl = User.mainurl + "app/app_notes";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", nameCode);
        parameters_userInfo.put("date1", start);
        parameters_userInfo.put("date2", end);
        parameters_userInfo.put("currentPage", currentPage + "");

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//							Logger.e(response.toString());
                            Integer code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            notes.clear();
                            List<Note> mDatas = new ArrayList<Note>();
                            if (code == 0) {
                               mDatas = GetInfo.getNotes(dataJson);
                            }
                            notes = mDatas;
                            listAdapter.setDatas(mDatas);
                            listAdapter.notifyDataSetChanged();
//                            ToastUtil.toast(context, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }
                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void visitLoad() {
        currentPage += 1;
        String httpUrl = User.mainurl + "app/app_notes";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", nameCode);
        parameters_userInfo.put("date1", start);
        parameters_userInfo.put("date2", end);
        parameters_userInfo.put("currentPage", currentPage + "");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            Integer code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code == 0) {
                                List<Note> mDatas = GetInfo.getNotes(dataJson);
                                notes.addAll(mDatas);
                                listAdapter.addAll(mDatas);
                                listAdapter.notifyDataSetChanged();
                            }
//                            ToastUtil.toast(context, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();

                        }
                    }
                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

        @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.edt_start:
//                new DatePickerDialog(context,
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
//                                startEdit.setText(yearStr + "-" + month + "-" + day);
//                            }
//                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
//                        .get(Calendar.DAY_OF_MONTH)).show();
                dateType = 0;
                pvTime.show();
                break;
            case R.id.edt_end:
//                DatePickerDialog datePickerDialog =new DatePickerDialog(context,
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
//                start = startEdit.getText().toString();
//                if (!TextUtils.isEmpty(start)){
//                    long timeInMillisSinceEpoch = DateUtil.getTimeInMillisSinceEpoch(start);
//                    datePickerDialog.getDatePicker().setMinDate(timeInMillisSinceEpoch);
//                    datePickerDialog.show();
//                }else{
//                    ToastUtil.toast(context,"请先选择开始日期");
//                }
                dateType = 1;
                pvTime.show();
                break;
        }
    }

    private void getData() {

        String httpUrl = User.mainurl + "app/GetInfo";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("med", type);
        if ("user".equals(type)){
            parameters_userInfo.put("cdepcode", departCode);
        }

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//                            Logger.e("dataJson"+dataJson);
                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code == 0) {
                                names.clear();
                                nameCodes.clear();
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    if ("dep".equals(type)) {
                                        departs.add(obj.getString("cdepname"));
                                        departCodes.add(obj.getString("cdepcode"));
                                        reloadSpinner(departSpn, departs);
                                    }else {
                                        names.add(obj.getString("username"));
                                        nameCodes.add(obj.getString("usercode"));
                                        reloadSpinner(nameSpn,names);
                                    }
                                }
                            }
//                            ToastUtil.toast(context, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            ProgressDialogUtil.closeProgressDialog();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spn_depart:
                depart = departs.get(position);
                departCode = departCodes.get(position);
                type = "user";
                ProgressDialogUtil.showProgressDialog(context);
                getData();
                break;
            case R.id.spn_name:
                name = names.get(position);
                nameCode = nameCodes.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void reloadSpinner(Spinner spinner,List<String> lists){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                R.layout.item_spinner,
                lists);
        spinner.setAdapter(arrayAdapter);
    }
}
