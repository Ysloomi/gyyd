package com.beessoft.dyyd.check;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
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

public class CustomerActivity extends BaseActivity {

    private ListView listView;
    private List<String> datas;
    private List<HashMap<String,String>> latlngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        context = CustomerActivity.this;

        datas = new ArrayList<>();
        latlngs = new ArrayList<>();
        String name = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        initView();

        ProgressDialogUtil.showProgressDialog(context);
        visitServer(name,type);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list_view);
    }

    private void visitServer(String name,String type) {

        String httpUrl = User.mainurl + "sf/GetCustomer";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("name", Escape.escape(name));
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("type", type);
        parameters_userInfo.put("sf", ifSf);

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code==0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    HashMap<String,String> map = new HashMap<String, String>();
                                    String name = obj.getString("ccusname");
                                    String ccuscode = obj.getString("ccuscode");
                                    datas.add(ccuscode+"_"+name);
                                    map.put("name",name);
                                    map.put("lat",obj.getString("lat"));
                                    map.put("lng",obj.getString("lng"));
                                    map.put("scope",obj.getString("fw"));
                                    map.put("ccuscode",ccuscode);
                                    latlngs.add(map);
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                        R.layout.item_baselist,
                                        datas);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent();
                                        HashMap<String,String> map = latlngs.get(position);
                                        intent.putExtra("name", map.get("name"));
                                        intent.putExtra("lat", map.get("lat"));
                                        intent.putExtra("lng", map.get("lng"));
                                        intent.putExtra("scope", map.get("scope"));
                                        intent.putExtra("ccuscode", map.get("ccuscode"));
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }
                                });
                            }else{
                                ToastUtil.toast(context,"无相应信息");
                                finish();
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
                        ToastUtil.toast(context,"网络连接错误");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }
}
