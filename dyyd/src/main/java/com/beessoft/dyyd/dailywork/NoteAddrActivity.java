package com.beessoft.dyyd.dailywork;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.NoteAddrAdapter;
import com.beessoft.dyyd.bean.NoteAddr;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoteAddrActivity extends BaseActivity
        implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

    private Spinner typeSp;
    private EditText keywordEdt;

//    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private List<NoteAddr> noteAddrs = new ArrayList<>();
    private NoteAddrAdapter noteAddrAdapter;

    private int currentPage = 1;
    private String keyword = "";
    private String type = "0";
    private String addr = "";
    private String addrCode = "";
    private boolean isSame = false;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_addr_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_submit:
//                String all = NoteAddrDBManager.getInstance().getCheckName("1");
                String addr = "";
                String addrCode = "";
                for (int j = 0;j<noteAddrs.size();j++){
                    NoteAddr noteAddr = noteAddrs.get(j);
                    if ("1".equals(noteAddr.getIscheck())){
                        addr += noteAddr.getName()+",";
                        addrCode += noteAddr.getCode()+",";
                    }
                }
                if (!Tools.isEmpty(addr)&&!Tools.isEmpty(addrCode)){
//                    String[] a = all.split(",");
                    Intent intent = new Intent();
                    intent.putExtra("addr", addr);
                    intent.putExtra("addrCode", addrCode);
//                    Bundle b = new Bundle();
//                    b.putParcelableArrayList("note", noteAddrs);
//                    intent.putExtra("bundle", b);
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    ToastUtil.toast(context,"请至少选择一个地点");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_addr);

        context = NoteAddrActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        addr = getIntent().getStringExtra("addr");
        addrCode = getIntent().getStringExtra("addrCode");

        if (!Tools.isEmpty(addr)) {
            String[] a = addr.split(",");
            String[] b = addrCode.split(",");
            for (int i = 0; i < a.length; i++) {
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(a[i]);
                noteAddr.setCode(b[i]);
                noteAddr.setIscheck("1");
                noteAddrs.add(noteAddr);
            }
        }
        initView();
        initData();

//        // mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
//        // 在刷新时允许继续滑动
//        mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
//        // mPullRefreshListView.getMode();//得到模式
//        // 上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
//        mPullRefreshListView.setMode(PullToRefreshBase.Mode.);
//        mPullRefreshListView.setAdapter(noteAddrAdapter);
//
//        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(
//                        context,
//                        System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME
//                                | DateUtils.FORMAT_SHOW_DATE
//                                | DateUtils.FORMAT_ABBREV_ALL);
//
//                // Update the LastUpdatedLabel
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//                type="0";
//                keyword = "";
//                visitRefresh();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                String label = DateUtils.formatDateTime(
//                        context,
//                        System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME
//                                | DateUtils.FORMAT_SHOW_DATE
//                                | DateUtils.FORMAT_ABBREV_ALL);
//
//                // Update the LastUpdatedLabel
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//
//                visitLoad();
//            }
//        });

//        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Find mFind = finds.get(position - 1);
//                Intent intent = new Intent();
//                if ("中规".equals(mFind.getFlag())) {
//                    intent.setClass(context, FindChinaActivity.class);
//                } else {
//                    intent.setClass(context, FindOtherActivity.class);
//                }
//                intent.putExtra("id", mFind.getmId());
//                startActivity(intent);
//            }
//        });
    }



    private void initView() {
//        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mListView = (ListView) findViewById(R.id.list_view);
        typeSp = (Spinner) findViewById(R.id.spn_type);
        keywordEdt = (EditText) findViewById(R.id.edt_search);

        mListView.setOnItemClickListener(this);
        typeSp.setOnItemSelectedListener(this);
        findViewById(R.id.txt_search).setOnClickListener(this);
    }

    private void initData() {

        noteAddrAdapter = new NoteAddrAdapter(context, noteAddrs,true);
        mListView.setAdapter(noteAddrAdapter);

        List<String> lists = new ArrayList<>();
        lists.add("政企单位");
        lists.add("渠道商家");
        lists.add("公司部门");
        Tools.reloadSpinner(context,typeSp,lists);
    }

    private void visitRefresh() {

        currentPage = 1;

        String httpUrl = User.mainurl + "notePad/TreeServlet";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("page", currentPage+"");
        parameters_userInfo.put("type", type);
//        parameters_userInfo.put("pid", Escape.escape(keyword));
//        try {
//            keyword = URLEncoder.encode(keyword,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace(System.out);
//        }
        parameters_userInfo.put("pid", keyword);

//        Logger.e(httpUrl+"?"+parameters_userInfo);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);
//							Logger.e(response.toString());
                            Integer code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
//                            NoteAddrDBManager.getInstance().delete("0");
//                            noteAddrs.clear();
//                            noteAddrs.addAll(NoteAddrDBManager.getInstance().getCheck("1"));
                            List<NoteAddr> mDatas = new ArrayList<>();
                            for (int j = 0;j<noteAddrs.size();j++){
                                NoteAddr noteAddr = noteAddrs.get(j);
                                if ("1".equals(noteAddr.getIscheck())){
                                    mDatas.add(noteAddr);
                                }
                            }
                            noteAddrs.clear();
//                            noteAddrs.addAll(mDatas);
                            if (code == 0) {
//                                List<NoteAddr> mDatas = getFinds(dataJson);
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    NoteAddr noteAddr = new NoteAddr();
                                    String codeInside = obj.getString("id");
                                    String name = obj.getString("name");
                                    isSame =false;
                                    for (int j = 0; j < mDatas.size(); j++) {
                                        NoteAddr noteAddrInside = mDatas.get(j);
                                        if (codeInside.equals(noteAddrInside.getCode()) &&
                                                name.equals(noteAddrInside.getName())) {
                                            isSame =true;
                                            break;
                                        }
                                    }
                                    if (!isSame){
                                        noteAddr.setCode(codeInside);
                                        noteAddr.setName(name);
                                        noteAddr.setIscheck("0");
                                        mDatas.add(noteAddr);
                                    }
//                                    NoteAddrDBManager.getInstance().insert(noteAddr);
                                }

                                noteAddrs.addAll(mDatas);
                                noteAddrAdapter.setDatas(mDatas);
                                noteAddrAdapter.notifyDataSetChanged();
                            }
//                            ToastUtil.toast(context, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            ProgressDialogUtil.closeProgressDialog();
//                            mPullRefreshListView.onRefreshComplete();
                        }
                    }
                    @Override
                    public void onFailure(Throwable error, String data) {
                        ToastUtil.toast(context, "网络错误，请重试");
                        ProgressDialogUtil.closeProgressDialog();
//                        mPullRefreshListView.onRefreshComplete();
                    }
                });
    }

//    private void visitLoad() {
//        currentPage += 1;
//        String httpUrl = User.mainurl + "notePad/TreeServlet";
//        AsyncHttpClient client_request = new AsyncHttpClient();
//        RequestParams parameters_userInfo = new RequestParams();
//
//        parameters_userInfo.put("mac", mac);
//        parameters_userInfo.put("usercode", username);
//        parameters_userInfo.put("page", currentPage+"");
//        parameters_userInfo.put("type", type);
//        parameters_userInfo.put("plan", Escape.escape(keyword));
//
//        client_request.post(httpUrl, parameters_userInfo,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(String response) {
//                        try {
//                            JSONObject dataJson = new JSONObject(response);
//                            Integer code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
//                            if (code == 0) {
//                                List<NoteAddr> mDatas = getFinds(dataJson);
//                                noteAddrs.addAll(mDatas);
//                                noteAddrAdapter.addAll(mDatas);
//                                noteAddrAdapter.notifyDataSetChanged();
//                            }
//                            ToastUtil.toast(context, msg);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } finally {
//                            ProgressDialogUtil.closeProgressDialog();
//
//                        }
//                    }
//                    @Override
//                    public void onFailure(Throwable error, String data) {
//                        ToastUtil.toast(context, "网络错误，请重试");
//                        ProgressDialogUtil.closeProgressDialog();
//                        mPullRefreshListView.onRefreshComplete();
//                    }
//                });
//    }

//    @NonNull
//    private List<NoteAddr> getFinds(JSONObject response) throws JSONException {
//        JSONArray array = response.getJSONArray("list");
//        List<NoteAddr> mDatas = new ArrayList<>();
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject obj = array.getJSONObject(i);
//            NoteAddr noteAddr = new NoteAddr();
//            noteAddr.setName(obj.getString("id"));
//            noteAddr.setIscheck("0");
//            mDatas.add(noteAddr);
//        }
//        return mDatas;
//    }


//    @Override
//    public void changeCheck(int position, boolean isChecked) {
//        NoteAddr noteAddr = noteAddrs.get(position);
//        if (isChecked) {
//            noteAddr.setIscheck("1");
//        } else {
//            noteAddr.setIscheck("0");
//        }
//        noteAddrAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_search:
                keyword = keywordEdt.getText().toString();
                visitRefresh();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spn_type:
                type = position+"";
                keyword = "";
                keywordEdt.setText("");
                visitRefresh();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Logger.e("parent.getId>>>>"+parent.getId());
        switch (parent.getId()){
            case R.id.list_view:
                NoteAddr noteAddr = noteAddrs.get(position);
                if ("0".equals(noteAddr.getIscheck())) {
                    noteAddr.setIscheck("1");
                } else {
                    noteAddr.setIscheck("0");
                }
//                NoteAddrDBManager.getInstance().update(noteAddr);
                noteAddrAdapter.notifyDataSetChanged();
                break;
        }
    }
}
