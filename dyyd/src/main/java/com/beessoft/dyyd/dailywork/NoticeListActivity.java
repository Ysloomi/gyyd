package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
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
    private ListView listView;
    private SimpleAdapter simAdapter;
    List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notice_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_unread:
                state = "0";// 未读
                visitServer();
                return true;
            case R.id.action_read:
                state = "1";// 已读
                visitServer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticelist);

        context = NoticeListActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        listView = (ListView) findViewById(R.id.notice_list);

        state = "0";// 未读
        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/noticelist";
        String pass = GetInfo.getPass(context);
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("pass", pass);
        parameters_userInfo.put("state", state);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            dataList.clear();
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("id", obj.getString("id"));
                                    map.put("itype", obj.getString("itype"));
                                    map.put("context", obj.getString("context"));
                                    map.put("url", obj.getString("url"));
                                    map.put("bookclass",
                                            obj.getString("bookclass"));
                                    map.put("state", obj.getString("state"));
                                    map.put("date", obj.getString("ddate"));
                                    map.put("fbperson",
                                            obj.getString("fbperson"));
                                    dataList.add(map);
                                }
                            } else if (1==code) {
                                Toast.makeText(NoticeListActivity.this, "暂无通知",
                                        Toast.LENGTH_SHORT).show();
                            } else if (-2==code) {
                                Toast.makeText(NoticeListActivity.this, "无权限",
                                        Toast.LENGTH_SHORT).show();
                            }
                            simAdapter = new SimpleAdapter(
                                    NoticeListActivity.this,
                                    dataList,// 数据源
                                    R.layout.item_noticelist,// 显示布局
                                    new String[]{"date", "fbperson",
                                            "state", "context"},
                                    new int[]{R.id.date, R.id.person,
                                            R.id.state, R.id.context});
                            listView.setAdapter(simAdapter);
                            // 添加点击
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @SuppressWarnings({"unchecked"})
                                @Override
                                public void onItemClick(
                                        AdapterView<?> parent, View view,
                                        int position, long id) {

                                    ListView listView = (ListView) parent;
                                    HashMap<String, String> map = (HashMap<String, String>) listView
                                            .getItemAtPosition(position);

                                    String itype = map.get("itype");
                                    String idTarget = map.get("id");
                                    String person = map.get("fbperson");
                                    String date = map.get("date");
                                    String myContext = map.get("context");

                                    if ("0".equals(itype)) {
                                        visitServer_save(idTarget);
                                        inputTitleDialog(date, person, myContext);
                                    } else if ("1".equals(itype)) {
//                                        visitServer_save(idTarget);
//                                        String bookclass = map.get("bookclass");
//                                        Intent intent = new Intent();
//                                        if ("1".equals(bookclass)) {
//                                            intent.setClass(context, WebViewActivity.class);
//                                        } else if ("2".equals(bookclass)) {
//                                            intent.setClass(context,
//                                                    SalesWorkBookActivity.class);
//                                        } else if ("3".equals(bookclass)) {
//                                            intent.setClass(context,
//                                                    BossWorkBookActivity.class);
//                                        } else if ("4".equals(bookclass)) {
//                                            intent.setClass(context,
//                                                    CompanyTargetActivity.class);
//                                        } else if ("5".equals(bookclass)) {
//                                            intent.setClass(context,
//                                                    BranchTargetActivity.class);
//                                        }
//                                        intent.putExtra("from", "notice");
//                                        intent.putExtra("url", map.get("url") + "&mac=" + mac + "&usercode=" + username);
//                                        startActivity(intent);
                                    }
                                }
                            });
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

    /**
     * 打开dialog显示
     */
    @SuppressLint("InflateParams")
    private void inputTitleDialog(String date, String person, String myContext) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_noticeinfo, null);
        // EditText editText = (EditText)findViewById(R.id.content);// error
        TextView textView1 = (TextView) view.findViewById(R.id.info_date);
        TextView textView2 = (TextView) view.findViewById(R.id.info_person);
        TextView textView3 = (TextView) view.findViewById(R.id.info_context);
        // System.out.println("")
        textView1.setText(date);
        textView2.setText(person);
        textView3.setText(myContext);
        textView3.setMovementMethod(ScrollingMovementMethod.getInstance());// 可滚动

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("通知详情").setView(view).setPositiveButton("确认", null)
                .show();
    }

    private void visitServer_save(String idTarget) {

        String httpUrl = User.mainurl + "sf/notice_save";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("pass", "");
        parameters_userInfo.put("id", idTarget);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 0) {
                                visitServer();
                                Toast.makeText(NoticeListActivity.this,
                                        "标记已阅读成功", Toast.LENGTH_SHORT).show();
                            } else if (code==1) {
                                Toast.makeText(NoticeListActivity.this,
                                        "当日已签到", Toast.LENGTH_SHORT).show();
                            } else if (code==-2) {
                                Toast.makeText(NoticeListActivity.this, "无权限",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
