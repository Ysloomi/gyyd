package com.beessoft.dyyd.dailywork;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class NoteDealActivity extends BaseActivity implements View.OnClickListener {

    private TextView departText;
    private TextView nameText;
    private TextView addrText;
    private TextView startText;
    private TextView endText;
    private TextView planText;

    private LinearLayout questionLl;
    private LinearLayout adviseLl;
    private LinearLayout reasonLl;
    private LinearLayout effectLl;

    private EditText questionEdt;
    private EditText adviseEdt;
    private EditText reasonEdt;
    private EditText effectEdt;

    private String from;
    private String state;
    private String addr;
    private String addrCode;
    private String rtCode;
    private String question;
    private String advise;

    private Note note = new Note();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_deal);

        context = NoteDealActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        from = getIntent().getStringExtra("from");
        Bundle b = getIntent().getBundleExtra("bundle");
        note = b.getParcelable("note");
        state = getIntent().getStringExtra("state");
        addr = getIntent().getStringExtra("addr");
        addrCode = getIntent().getStringExtra("addrCode");
        rtCode = getIntent().getStringExtra("rtCode");


        initView();
        initData();
    }

    private void initView() {

        departText = (TextView) findViewById(R.id.txt_depart);
        nameText = (TextView) findViewById(R.id.txt_name);
        addrText = (TextView) findViewById(R.id.txt_addr);
        startText = (TextView) findViewById(R.id.edt_start);
        endText = (TextView) findViewById(R.id.edt_end);
        planText = (TextView) findViewById(R.id.txt_plan);

        questionLl = (LinearLayout) findViewById(R.id.ll_question);
        adviseLl = (LinearLayout) findViewById(R.id.ll_advise);
        reasonLl = (LinearLayout) findViewById(R.id.ll_reason);
        effectLl = (LinearLayout) findViewById(R.id.ll_effect);

        questionEdt = (EditText) findViewById(R.id.edt_question);
        adviseEdt = (EditText) findViewById(R.id.edt_advise);
        reasonEdt = (EditText) findViewById(R.id.edt_reason);
        effectEdt = (EditText) findViewById(R.id.edt_effect);

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }


    private void initData() {

        departText.setText(note.getDepart());
        nameText.setText(note.getName());
        startText.setText(note.getStart());
        endText.setText(note.getEnd());
        planText.setText(note.getPlan());

        if ("plan".equals(from)){
            if ("未走访".equals(state)){
                questionLl.setVisibility(View.GONE);
                adviseLl.setVisibility(View.GONE);
                reasonLl.setVisibility(View.VISIBLE);
                effectLl.setVisibility(View.GONE);
            }else{
                questionLl.setVisibility(View.VISIBLE);
                adviseLl.setVisibility(View.VISIBLE);
                reasonLl.setVisibility(View.GONE);
                effectLl.setVisibility(View.GONE);
            }

        }else {
            question = getIntent().getStringExtra("question");
            advise = getIntent().getStringExtra("advise");
            questionLl.setVisibility(View.VISIBLE);
            adviseLl.setVisibility(View.VISIBLE);
            questionEdt.setText(question);
            questionEdt.setKeyListener(null);
            questionEdt.setBackground(getResources().getDrawable(R.drawable.textshape_grey));
            adviseEdt.setText(advise);
            adviseEdt.setKeyListener(null);
            adviseEdt.setBackground(getResources().getDrawable(R.drawable.textshape_grey));
            reasonLl.setVisibility(View.GONE);
            effectLl.setVisibility(View.VISIBLE);
        }
        addrText.setText(addr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                String quesioton = questionEdt.getText().toString();
                String advise = adviseEdt.getText().toString();
                String reason = reasonEdt.getText().toString();
                String effect = effectEdt.getText().toString();
                if ("plan".equals(from)){
                    if ("未走访".equals(state)){
                        if (!Tools.isEmpty(reason)){
                            saveData(quesioton,advise,reason,effect);
                        }else {
                            ToastUtil.toast(context,"请填写原因");
                        }
                    }else{
                        if (!Tools.isEmpty(quesioton)&&!Tools.isEmpty(advise)){
                            saveData(quesioton,advise,reason,effect);
                        }else {
                            ToastUtil.toast(context,"请填写问题以及措施");
                        }
                    }
                }else{
                    if (!Tools.isEmpty(effect)){
                        saveData(quesioton,advise,reason,effect);
                    }else {
                        ToastUtil.toast(context,"请填写成效跟踪");
                    }
                }

                break;
        }
    }


    private void saveData(String quesioton, String advise,
                          String reason, String effect) {

        String httpUrl = User.mainurl+"notePad/MyNoteServlet";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);


//        try {
//            addr = URLEncoder.encode(addr,"UTF-8");
//            plan = URLEncoder.encode(plan,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace(System.out);
//        }
        parameters_userInfo.put("Inter", "app");
        if ("plan".equals(from)){
            if ("未走访".equals(state)){
                parameters_userInfo.put("tmp", "unfinish");
                parameters_userInfo.put("problem",reason );
            }else{
                parameters_userInfo.put("tmp", "finish");
                parameters_userInfo.put("problem",quesioton );
            }
            parameters_userInfo.put("type", "result");
            parameters_userInfo.put("id", note.getId());
            parameters_userInfo.put("rdcode", note.getRdCode());
            parameters_userInfo.put("opinion", advise);
            parameters_userInfo.put("ccuscode", addrCode);
        }else{
            parameters_userInfo.put("type", "effect");
            parameters_userInfo.put("rtcode", rtCode);
            parameters_userInfo.put("effect", effect);
        }

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
                            if (code==0) {
                                finish();
                                ToastUtil.toast(context, "上传成功");
                            } else {
                                ToastUtil.toast(context, "请重试");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });
    }
}
