package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.NoteAdapter;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.pulltorefresh.RefreshTime;
import com.beessoft.dyyd.swipemenulistview.SwipeMenu;
import com.beessoft.dyyd.swipemenulistview.SwipeMenuCreator;
import com.beessoft.dyyd.swipemenulistview.SwipeMenuHelper;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.beessoft.dyyd.view.PullToRefreshSwipeMenuListView;
import com.bigkoo.pickerview.TimePickerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends BaseActivity
        implements PullToRefreshSwipeMenuListView.IXListViewListener, View.OnClickListener {

    private EditText startEdit;
    private EditText endEdit;

    private PullToRefreshSwipeMenuListView pullToRefreshListView;
    private List<Note> notes =new ArrayList<>();
    private NoteAdapter listAdapter;

    private int currentPage = 1;
    private String start = "";
    private String end = "";



    private TimePickerView pvTime;
    private int dateType = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.mn_search:
                intent.setClass(context, NoteQueryActivity.class);
                startActivity(intent);
                return true;
            case R.id.mn_add:
                intent.setClass(context, NoteAddActivity.class);
                Note note = new Note();
                Bundle b = new Bundle();
                b.putParcelable("note", note);
                b.putString("from", "add");
                intent.putExtras(b);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        context = NoteActivity.this;

        initView();
        initData();

        listAdapter = new NoteAdapter(context, notes);

        pullToRefreshListView.setPullRefreshEnable(true);
        pullToRefreshListView.setPullLoadEnable(true);
        pullToRefreshListView.setXListViewListener(this);
        pullToRefreshListView.setAdapter(listAdapter);
        SwipeMenuHelper helper = new SwipeMenuHelper(context);
        SwipeMenuCreator creator = helper.getSwipeMenuDelectCreator();

        // set creator
        pullToRefreshListView.setMenuCreator(creator);
        // step 2. listener item click event
        pullToRefreshListView.setOnMenuItemClickListener(new PullToRefreshSwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Note note = notes.get(position);
                switch (index) {
                    case 0:
                        ProgressDialogUtil.showProgressDialog(context);
                        deleteById(note.getRdCode());
//                        Logger.e("note.getRdCode()>>>"+note.getRdCode());
                        break;
                }
            }
        });

        // set SwipeListener
        pullToRefreshListView.setOnSwipeListener(new PullToRefreshSwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notes.get(position - 1);
                Intent intent = new Intent();
                intent.setClass(context, NoteQueryDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Bundle b = new Bundle();
                b.putParcelable("note", note);
                b.putString("from", "note");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        start = startEdit.getText().toString();
        end = endEdit.getText().toString();
        ProgressDialogUtil.showProgressDialog(context);
        visitRefresh();
    }

    private void initView() {
        startEdit = (EditText) findViewById(R.id.edt_start);
        startEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘

        endEdit = (EditText) findViewById(R.id.edt_end);
        endEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘

        pullToRefreshListView = (PullToRefreshSwipeMenuListView) findViewById(R.id.pull_swipe_list);


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
        findViewById(R.id.txt_search).setOnClickListener(this);
    }

    private void initData() {
        startEdit.setText(DateUtil.monthFirstDay());
        endEdit.setText(DateUtil.monthLastDay());
        start = startEdit.getText().toString();
        end = endEdit.getText().toString();
    }

    private void visitRefresh() {

        currentPage = 1;

        String httpUrl = User.mainurl + "app/app_notes";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("date1", start);
        parameters_userInfo.put("date2", end);
        parameters_userInfo.put("currentPage", currentPage + "");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            notes.clear();
                            List<Note> mDatas = new ArrayList<>();
                            if (code == 0) {
                                mDatas = GetInfo.getNotes(dataJson);
                            }
                            notes = mDatas;
                            listAdapter.setDatas(mDatas);
                            listAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                            pullToRefreshListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
                            pullToRefreshListView.stopRefresh();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                        pullToRefreshListView.stopRefresh();
                    }
                });
    }

    private void visitLoad() {
        currentPage += 1;
        String httpUrl = User.mainurl + "app/app_notes";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("date1", start);
        parameters_userInfo.put("date2", end);
        parameters_userInfo.put("currentPage", currentPage + "");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                List<Note> mDatas = GetInfo.getNotes(dataJson);
                                notes.addAll(mDatas);
                                listAdapter.addAll(mDatas);
                                listAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                            pullToRefreshListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
                            pullToRefreshListView.stopLoadMore();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                        pullToRefreshListView.stopLoadMore();
                    }
                });
    }



    private void deleteById(String id) {

        String httpUrl = User.mainurl + "notePad/MyNoteServlet";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("rdcode", id);
        parameters_userInfo.put("type", "del");

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code == 0) {
                                initData();
                                visitRefresh();
                                ToastUtil.toast(context, "删除成功");
                            }else {
                                ToastUtil.toast(context, "已走访不能删除");
                            }

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
    public void onRefresh() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getApplicationContext(), df.format(new Date()));
        initData();
        visitRefresh();
    }

    @Override
    public void onLoadMore() {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        RefreshTime.setRefreshTime(getApplicationContext(), df.format(new Date()));
        visitLoad();
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.edt_start:
                dateType = 0;
                pvTime.show();
                break;
            case R.id.edt_end:
                dateType = 1;
                pvTime.show();
                break;
            case R.id.txt_search:
                start = startEdit.getText().toString();
                end = endEdit.getText().toString();
                ProgressDialogUtil.showProgressDialog(context);
                visitRefresh();
                break;
        }
    }
}
