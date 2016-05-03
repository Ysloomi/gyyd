package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.check.MapActivity;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkQueryActivity extends BaseActivity {
    private TextView textView1, textView2, textView3, textView4, textView5,
            textView6, textView7;
    private String id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workquery_actions, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read:
                Intent intent = new Intent(this, ReadActivity.class);
                intent.putExtra("idTarget", id);
                startActivity(intent);
                return true;
            case R.id.action_mileage:
                Intent intent1 = new Intent(WorkQueryActivity.this,MapActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("department", "");
                intent1.putExtra("person", "");
                intent1.putExtra("itype", "run");
                intent1.putExtra("itime", "");
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workquery);

        context =WorkQueryActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        id = getIntent().getStringExtra("idTarget");

        initView();

        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void initView() {

        textView1 = (TextView) findViewById(R.id.query_person);
        textView2 = (TextView) findViewById(R.id.query_outtime);
        textView3 = (TextView) findViewById(R.id.query_yester);
        textView4 = (TextView) findViewById(R.id.query_summary);
        textView5 = (TextView) findViewById(R.id.query_plan);
        textView6 = (TextView) findViewById(R.id.query_advise);
        textView7 = (TextView) findViewById(R.id.query_time);

    }

    private void visitServer() {
        String httpUrl = User.mainurl + "sf/fragment_check";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("id", id);

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
                                    JSONObject obj = array.getJSONObject(0);
                                    textView1.setText(obj.getString("username"));
                                    textView2.setText(obj.getString("cmakertime"));
                                    textView3.setText(obj.getString("ytomplan"));
                                    textView4.setText(obj.getString("todsummary"));
                                    textView5.setText(obj.getString("tomplan"));
                                    textView6.setText(obj.getString("veropinion"));
                                    textView7.setText(obj.getString("checktime"));
                                }
                            } else {
                                Toast.makeText(WorkQueryActivity.this, "无工作数据",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
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
}
