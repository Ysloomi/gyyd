package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApproveQueryActivity extends BaseActivity {

    private TextView personTxt, outTimeTxt, yesterTxt, summaryTxt, planTxt, adviseTxt, approveTimeTxt;
    private String id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read:
                Intent intent = new Intent(context, ReadActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvequery);

        context = ApproveQueryActivity.this;

        id = getIntent().getStringExtra("id");

        personTxt = (TextView) findViewById(R.id.query_person);
        outTimeTxt = (TextView) findViewById(R.id.query_outtime);
        yesterTxt = (TextView) findViewById(R.id.query_yester);
        summaryTxt = (TextView) findViewById(R.id.query_summary);
        planTxt = (TextView) findViewById(R.id.query_plan);
        adviseTxt = (TextView) findViewById(R.id.query_advise);
        approveTimeTxt = (TextView) findViewById(R.id.query_time);

        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/check";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("id", id);

        client_request.post(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataJson = new JSONObject(response);
                    int code = dataJson.getInt("code");
                    if (code==0) {
                        JSONArray array = dataJson.getJSONArray("list");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(0);
                            personTxt.setText(obj.getString("username"));
                            outTimeTxt.setText(obj.getString("cmakertime"));
                            yesterTxt.setText(obj.getString("ytomplan"));
                            summaryTxt.setText(obj.getString("todsummary"));
                            planTxt.setText(obj.getString("tomplan"));
                            adviseTxt.setText(obj.getString("veropinion"));
                            approveTimeTxt.setText(obj.getString("checktime"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ProgressDialogUtil.closeProgressDialog();
                }
            }
        });
    }
}
