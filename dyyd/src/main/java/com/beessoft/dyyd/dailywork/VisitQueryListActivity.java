package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.VisitQueryAdapter;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitQueryListActivity extends BaseActivity {

    private String level;
    private List<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
    private AutoCompleteTextView autoCompleteTextView;
    private PullToRefreshListView mPullRefreshListView;
    private String currentPage = "1";
    private VisitQueryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitquerylist);

        context = VisitQueryListActivity.this;

//        level = "[全部人员]";
        level = "";

        initView();

        autoCompleteTextView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompleteTextView.showDropDown();// 显示下拉列表
                return false;
            }
        });

        findViewById(R.id.txt_search).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProgressDialogUtil.showProgressDialog(context);
//                level = autoCompleteTextView.getText().toString();
                visitRefresh("");
                Tools.closeInput(VisitQueryListActivity.this, autoCompleteTextView);
            }
        });

        mAdapter = new VisitQueryAdapter(context,datas);
        // mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        // 在刷新时允许继续滑动
        mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
        // mPullRefreshListView.getMode();//得到模式
        // 上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullRefreshListView.setAdapter(mAdapter);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                level = "[全部人员]";
                level = "";
                visitRefresh(level);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(
                        getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                visitLoad(level);
            }

        });

        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = datas.get(position - 1);
                String idate = map.get("idate");
                String name = map.get("name");
                Intent intent = new Intent(context, VisitQueryListDetailActivity.class);
                intent.putExtra("idate", idate);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

//        GetJSON.visitServer_GetInfo_NoSpecial(context, autoCompleteTextView, mac,username);
//        autoCompleteTextView.setHint("专业、姓名、分局");

        ProgressDialogUtil.showProgressDialog(context);
        visitRefresh(level);
    }

    private void initView() {
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.act_search);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
    }

    private void visitRefresh(String level) {

        String httpUrl = User.mainurl + "sf/visitlist";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        currentPage = "1";

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf",ifSf);
        parameters_userInfo.put("psn", Escape.escape(level));
        parameters_userInfo.put("page", currentPage);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            datas.clear();
                            List<HashMap<String, String>> mDatas = new ArrayList<HashMap<String, String>>();
                            if (code==1) {
                                Toast.makeText(VisitQueryListActivity.this,
                                        "没有相关信息", Toast.LENGTH_SHORT).show();
                            } else if (code==0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("idate", obj.getString("idate"));
                                    map.put("name", obj.getString("username"));
                                    mDatas.add(map);
                                }
                            }
                            datas = mDatas;
                            mAdapter.setDatas(mDatas);
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        error.printStackTrace(System.out);
                        ProgressDialogUtil.closeProgressDialog();
                        ToastUtil.toast(context, "网络连接错误，请检查网络");
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void visitLoad(String level) {

        String httpUrl = User.mainurl + "sf/visitlist";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        int page = Integer.valueOf(currentPage);
        page += 1;
        currentPage = String.valueOf(page);

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf",ifSf);
        parameters_userInfo.put("psn", Escape.escape(level));
        parameters_userInfo.put("page", currentPage);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code==1) {
                                Toast.makeText(VisitQueryListActivity.this,
                                        "没有相关信息", Toast.LENGTH_SHORT).show();
                            } else if (code==0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                List<HashMap<String, String>> mDatas = new ArrayList<HashMap<String, String>>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("idate", obj.getString("idate"));
                                    map.put("name", obj.getString("username"));
                                    mDatas.add(map);
                                }
                                datas.addAll(mDatas);
                                mAdapter.addAll(mDatas);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
                            mPullRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        error.printStackTrace(System.out);
                        ProgressDialogUtil.closeProgressDialog();
                        ToastUtil.toast(context, "网络连接错误，请检查网络");
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }
}
