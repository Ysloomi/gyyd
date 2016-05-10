package com.beessoft.dyyd.check;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AskLeaveQueryActivity extends BaseActivity {

    private ListView listView;
    private SimpleAdapter simAdapter;
    private List<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);

        context = AskLeaveQueryActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        listView = (ListView) findViewById(R.id.list_view);

        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/LeaveQuery";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);

                            if (dataJson.getString("code").equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    String start = obj.getString("mindate");
                                    String over = obj.getString("maxdate");
                                    String am = obj.getString("am");
                                    String pm = obj.getString("pm");
                                    if (start.equals(over)) {
                                        String a = "";
                                        if ("1".equals(am)) {
                                            a = " 上午";
                                        } else if ("1".equals(pm)) {
                                            a = " 下午";
                                        }
                                        if ("1".equals(am) && "1".equals(pm)) {
                                            a = "";
                                        }
                                        over += a;
                                    }
                                    map.put("start", start);
                                    map.put("over", over);
                                    String ifflag = obj.getString("ifflag").trim();

                                    if ("1".equals(ifflag)) {
                                        ifflag = "同意";
                                    } else if ("0".equals(ifflag)) {
                                        ifflag = "不同意";
                                    } else {
                                        ifflag = "待审批";
                                    }
                                    map.put("isagree", ifflag);
                                    map.put("type", obj.getString("state"));
                                    map.put("reason", obj.getString("reason"));
                                    map.put("username", "审批人:" + obj.getString("username"));
                                    datas.add(map);
                                }
                                simAdapter = new SimpleAdapter(
                                        AskLeaveQueryActivity.this,
                                        datas,// 数据源
                                        R.layout.item_askleavequery,// 显示布局
                                        new String[]{"start", "over",
                                                "type", "isagree", "reason", "username"},
                                        new int[]{R.id.start, R.id.over,
                                                R.id.state, R.id.isagree, R.id.reason, R.id.person});
                                listView.setAdapter(simAdapter);
                            } else {
                                ToastUtil.toast(context, "暂无通知");
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
