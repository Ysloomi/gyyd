package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.SpecialApproveAdapter;
import com.beessoft.dyyd.utils.Escape;
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

public class SpecialApproveActivity extends BaseActivity
        implements View.OnClickListener {

    private ListView listView;
    private SpecialApproveAdapter specialApproveAdapter;
    private List<HashMap<String, String>> datas = new ArrayList<>();

    private String projectId;
    private String shopId;
    private String personId;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_approve);

        context = SpecialApproveActivity.this;

        projectId = getIntent().getStringExtra("projectId");
        shopId = getIntent().getStringExtra("shopId");
        personId = getIntent().getStringExtra("personId");

        initView();

        specialApproveAdapter = new SpecialApproveAdapter(context,datas);
        listView.setAdapter(specialApproveAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListView listView = (ListView) parent;
//                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
//                String projectId = map.get("projectId");
//                String shopId = map.get("shopId");
//            }
//        });
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list_view);

        findViewById(R.id.btn_agree).setOnClickListener(this);
        findViewById(R.id.btn_refuse).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ProgressDialogUtil.showProgressDialog(context);
        String httpUrl = User.mainurl;
        httpUrl += "call/examineChk";

        RequestParams parameters_userInfo = new RequestParams();
        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("clid", projectId);
        parameters_userInfo.put("ccuscode", shopId);
        parameters_userInfo.put("usercode", personId);
        parameters_userInfo.put("med", "project");

        visitServer(httpUrl, parameters_userInfo);
    }

    private void visitServer(String httpUrl, RequestParams parameters_userInfo) {

        AsyncHttpClient client_request = new AsyncHttpClient();

//        Logger.e(httpUrl + "?" + parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            datas.clear();
                            int code = dataJson.getInt("code");
                            if (code == 1) {
                                Toast.makeText(SpecialApproveActivity.this,
                                        "没有相关信息", Toast.LENGTH_SHORT).show();
                            } else if (code == 0) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject obj = array.getJSONObject(j);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("name", obj.getString("detailsname"));
                                    map.put("model", User.mainurl+obj.getString("model"));
                                    map.put("photo", User.mainurl+obj.getString("imageFile"));
                                    map.put("date", "要求时间:"+obj.getString("beginTime")+"至"+obj.getString("endTime"));
                                    map.put("remarks", obj.getString("remarks"));
                                    map.put("addr", obj.getString("addr"));
                                    datas.add(map);
                                }
                            }
                            specialApproveAdapter.notifyDataSetChanged();
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


    private void saveData(String btn, String reason) {
        String httpUrl = User.mainurl;
        httpUrl += "call/examineChk";

        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("clid", projectId);
        parameters_userInfo.put("ccuscode", shopId);
        parameters_userInfo.put("usercode", personId);
        parameters_userInfo.put("btn", btn);
        parameters_userInfo.put("med", "opn");
        parameters_userInfo.put("opinion", Escape.escape(reason));

//        Logger.e(httpUrl + "?" + parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
                            int code = dataJson.getInt("code");
                            if (code == 1) {
                                ToastUtil.toast(context,"请重试");
                            } else if (code == 0) {
                                ToastUtil.toast(context,"上传成功");
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
                        ProgressDialogUtil.closeProgressDialog();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_agree:
                saveData("0","");
                break;
            case R.id.btn_refuse:
                inputExamineDialog();
                break;
        }
    }

//	@Override
//	public void onBackPressed() {
//		if (isNotice) {
//			Intent[] intents = new Intent[2];
//			intents[0] = new Intent(context,MainActivity.class);
//			intents[1] = new Intent(context,MyWorkActivity.class);
////					intent.setClass(context,MyWorkActivity.class);
//			startActivities(intents);
////					startActivity(intent);
//		}else{
////			finish();
//			super.onBackPressed();
//		}
//
//	}


    @SuppressLint("InflateParams")
    private void inputExamineDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_unagree, null);

        final EditText editText1 = (EditText) view.findViewById(R.id.reason_text);

        final AlertDialog myDialog = new AlertDialog.Builder(context).setView(view)
                .setPositiveButton("确认", null).setNegativeButton("取消", null)
                .setCancelable(false).create();

        myDialog.setTitle("不同意的原因");
        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String unagree_reason = editText1.getText().toString();
                        if (TextUtils.isEmpty(unagree_reason.trim())) {
                            ToastUtil.toast(context, "请填写不同意原因");
                        } else {
                            ProgressDialogUtil.showProgressDialog(context);
                            saveData("1",unagree_reason);
                            myDialog.dismiss();
                        }
                    }
                });
                Button button1 = myDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {myDialog.dismiss();
                    }
                });
            }
        });
        myDialog.show();
    }
}
