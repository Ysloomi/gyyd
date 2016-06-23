package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.WebViewActivity;
import com.beessoft.dyyd.adapter.NoticeAdapter;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
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

public class NoticeListActivity extends BaseActivity {

    private String state;

    private List<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

    private PullToRefreshListView mPullRefreshListView;
    private String currentPage = "1";
    private NoticeAdapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notice_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_unread:
                cleanlist();
                state = "0";// 未读
                visitRefresh();
                return true;
            case R.id.action_read:
                cleanlist();
                state = "1";// 已读
                visitRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticelist);

        context = NoticeListActivity.this;

        initView();

        mAdapter = new NoticeAdapter(context,datas);
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
                visitRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//				String label = DateUtils.formatDateTime(
//						getApplicationContext(),
//						System.currentTimeMillis(),
//						DateUtils.FORMAT_SHOW_TIME
//								| DateUtils.FORMAT_SHOW_DATE
//								| DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
//				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                visitLoad();
            }
        });

        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = datas.get(position - 1);

                String itype = map.get("itype");
                String idTarget = map.get("idTarget");
                String person = map.get("fbperson");
                String date = map.get("date");
                String myContext = map.get("context");

                if ("0".equals(itype)) {
                    cleanlist();
                    visitServer_save(idTarget);
                    visitRefresh();
                    inputTitleDialog(date, person, myContext);
                } else if ("1".equals(itype)) {
                    visitServer_save(idTarget);
                    String bookclass = map.get("bookclass");
                    Intent intent = new Intent();
                    intent.setClass(context, WebViewActivity.class);
                    if ("1".equals(bookclass)) {
                        intent.putExtra("title", "员工手册");
                    } else if ("2".equals(bookclass)) {
                        intent.putExtra("title", "店员手册");
                    } else if ("3".equals(bookclass)) {
                        intent.putExtra("title", "老板手册");
                    } else if ("4".equals(bookclass)) {
                        intent.putExtra("title", "公司日报");
                    } else if ("5".equals(bookclass)) {
                        intent.putExtra("title", "分局日报");
                    }
                    intent.putExtra("from", "notice");
                    intent.putExtra("url", map.get("url") + "&mac=" + mac + "&usercode=" + username);
                    startActivity(intent);
                }
            }
        });
        state = "0";// 未读
        ProgressDialogUtil.showProgressDialog(context);
        visitRefresh();
    }

    public void initView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
    }

    private void visitRefresh() {

        String httpUrl = User.mainurl + "sf/noticelist";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();
        currentPage= "1";
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("state", state);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("page", currentPage);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(Escape
                                    .unescape(response));
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                List<HashMap<String, String>> mDatas = new ArrayList<HashMap<String, String>>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("idTarget", obj.getString("id"));
                                    map.put("itype", obj.getString("itype"));
                                    map.put("context", obj.getString("context"));
                                    map.put("url", obj.getString("url"));
                                    map.put("bookclass", obj.getString("bookclass"));
                                    map.put("state", obj.getString("state"));
                                    map.put("date", obj.getString("ddate"));
                                    map.put("fbperson", obj.getString("fbperson"));
                                    mDatas.add(map);
                                }
                                datas = mDatas;
                                mAdapter.setDatas(mDatas);
                                mAdapter.notifyDataSetChanged();
                            } else if ("1".equals(code)) {
                                ToastUtil.toast(context, "暂无通知");
                            } else if ("-2".equals(code)) {
                                ToastUtil.toast(context, "无权限");
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
                        ToastUtil.toast(context, "网络连接错误，请检查网络");
                        ProgressDialogUtil.closeProgressDialog();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

    private void visitLoad() {
        String httpUrl = User.mainurl + "sf/noticelist";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        int page = Integer.valueOf(currentPage);
        page += 1;
        currentPage = String.valueOf(page);

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("state", state);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("page", currentPage);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                List<HashMap<String, String>> mDatas = new ArrayList<HashMap<String, String>>();
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("idTarget", obj.getString("id"));
                                    map.put("itype", obj.getString("itype"));
                                    map.put("context", obj.getString("context"));
                                    map.put("url", obj.getString("url"));
                                    map.put("bookclass", obj.getString("bookclass"));
                                    map.put("state", obj.getString("state"));
                                    map.put("date", obj.getString("ddate"));
                                    map.put("fbperson", obj.getString("fbperson"));
                                    mDatas.add(map);
                                }
                                datas.addAll(mDatas);
                                mAdapter.addAll(mDatas);
                                mAdapter.notifyDataSetChanged();

                            } else if ("1".equals(code)) {
                                ToastUtil.toast(context, "暂无通知");
                            } else if ("-2".equals(code)) {
                                ToastUtil.toast(context, "无权限");
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
                        ToastUtil.toast(context, "网络连接错误，请检查网络");
                        ProgressDialogUtil.closeProgressDialog();
                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

    @SuppressLint("InflateParams")
    private void inputTitleDialog(String date, String person, String myContext) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_noticeinfo, null);


        TextView textView1 = (TextView) view.findViewById(R.id.info_date);
        TextView textView2 = (TextView) view.findViewById(R.id.info_person);
        TextView textView3 = (TextView) view.findViewById(R.id.info_context);

        textView1.setText(date);
        textView2.setText(person);
        textView3.setText(myContext);
        textView3.setMovementMethod(ScrollingMovementMethod.getInstance());// 可滚动

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("通知详情").setView(view).setPositiveButton("确认", null)
                .show();
    }

    private void visitServer_save(String idTarget) {

        String httpUrl = User.mainurl + "sf/notice_save";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("id", idTarget);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(Escape
                                    .unescape(response));

                            String code = dataJson.getString("code");
                            if (code.equals("0")) {
                                ToastUtil.toast(context, "标记已阅读成功");
                            } else if (code.equals("1")) {
                                ToastUtil.toast(context, "当日已签到");
                            } else if (code.equals("-2")) {
                                ToastUtil.toast(context, "无权限");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private  void cleanlist() {
        int size = datas.size();
        if (size > 0) {
            datas.removeAll(datas);
            mAdapter.notifyDataSetChanged();
            mPullRefreshListView.setAdapter(mAdapter);
        }
    }
}
