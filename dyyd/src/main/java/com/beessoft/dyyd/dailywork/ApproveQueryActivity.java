package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApproveQueryActivity extends BaseActivity {
    private TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7;
    private String mac, id;

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
                Intent intent = new Intent(this, ReadActivity.class);
                intent.putExtra("idTarget", id);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approvequery);

        context = ApproveQueryActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        id = getIntent().getStringExtra("id");

        textView1 = (TextView) findViewById(R.id.query_person);
        textView2 = (TextView) findViewById(R.id.query_outtime);
        textView3 = (TextView) findViewById(R.id.query_yester);
        textView4 = (TextView) findViewById(R.id.query_summary);
        textView5 = (TextView) findViewById(R.id.query_plan);
        textView6 = (TextView) findViewById(R.id.query_advise);
        textView7 = (TextView) findViewById(R.id.query_time);

//        textView2.setInputType(InputType.TYPE_NULL);
//        textView3.setInputType(InputType.TYPE_NULL);
//        textView4.setInputType(InputType.TYPE_NULL);
        ProgressDialogUtil.showProgressDialog(context);
        visitServer();
    }

    private void visitServer() {

        String httpUrl = User.mainurl + "sf/fragment_check";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("id", id);

        client_request.post(httpUrl, parameters_userInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject dataJson = new JSONObject(Escape.unescape(response));

                    if (dataJson.getString("code").equals("0")) {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    ProgressDialogUtil.closeProgressDialog();
                }
            }
        });
    }
}
