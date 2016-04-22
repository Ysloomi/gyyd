package com.beessoft.dyyd.dailywork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.NoteAddrAdapter;
import com.beessoft.dyyd.adapter.NoteQueryAdapter;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.bean.NoteAddr;
import com.beessoft.dyyd.bean.NoteQuery;
import com.beessoft.dyyd.utils.ArrayAdapter;
import com.beessoft.dyyd.utils.Constant;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class NoteQueryDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private TextView departText;
    private TextView nameText;
    private TextView addrText;
    private TextView startText;
    private TextView endText;
    private TextView planText;
    private TextView dateTxt;

    private ListView listView;
    private NoteQueryAdapter noteQueryAdapter;

    private String from;
    private String state="";
    private String type="";
    private String addr="";
    private String addrCode="";

    private List<NoteAddr> noteAddrs = new ArrayList<>();
    private List<NoteAddr> effectNoteAddrs = new ArrayList<>();
    private NoteAddrAdapter noteAddrAdapter;

    private Note note;
    private boolean allWait = true;

    private  AlertDialog alertDialog ;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //条用基类的方法，以便调出系统菜单（如果有的话）
        super.onCreateOptionsMenu(menu);
        menu.add(0, Constant.ACTION_DEAL, 0, "处理").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //返回值为“true”,表示菜单可见，即显示菜单
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ("query".equals(from)) {
            menu.setGroupVisible(0, false);
        }else {
            menu.setGroupVisible(0, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Constant.ACTION_DEAL:
                final View menuItemView = findViewById(item.getItemId());
                showSpinner(menuItemView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_query_detail);

        context = NoteQueryDetailActivity.this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);

        from = getIntent().getStringExtra("from");
        Bundle b = getIntent().getBundleExtra("bundle");
        note = b.getParcelable("note");

        initView();
        initData();

//      getData(note.getId());
    }

    private void initView() {

        listView = (ListView) findViewById(R.id.list_view);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout listViewHeader = (LinearLayout) inflater.inflate(R.layout.head_note_query_detail, listView, false);
        listView.addHeaderView(listViewHeader);

        departText = (TextView) listViewHeader.findViewById(R.id.txt_depart);
        nameText = (TextView) listViewHeader.findViewById(R.id.txt_name);
        addrText = (TextView) listViewHeader.findViewById(R.id.txt_addr);
        startText = (TextView) listViewHeader.findViewById(R.id.txt_start);
        endText = (TextView) listViewHeader.findViewById(R.id.txt_end);
        planText = (TextView) listViewHeader.findViewById(R.id.txt_plan);
        dateTxt = (TextView) listViewHeader.findViewById(R.id.txt_date);
    }

    private void initData() {

        departText.setText(note.getDepart());
        nameText.setText(note.getName());
        startText.setText(note.getStart());
        endText.setText(note.getEnd());
        addrText.setText(note.getAddr());
        planText.setText(note.getPlan());
        dateTxt.setText(note.getDate());

        List<NoteQuery> noteQueries = new ArrayList<>();
        NoteQuery noteQuery1 = new NoteQuery();
        String addr ="";
        String addrCode= "";
        for (int i = 0;i<note.getNoteQueries().size();i++){
            NoteQuery noteQuery = note.getNoteQueries().get(i);
            if ("待走访".equals(noteQuery.getType())){
                addr += noteQuery.getAddr()+",";
                addrCode += noteQuery.getAddrCode()+",";
            }else{
                noteQueries.add(noteQuery);
            }
        }
        if (!Tools.isEmpty(addr)){
            noteQuery1.setType("待走访");
            noteQuery1.setAddr(addr);
            noteQuery1.setAddrCode(addrCode);
            noteQueries.add(noteQuery1);
        }

        noteQueryAdapter = new NoteQueryAdapter(context,noteQueries);
        listView.setAdapter(noteQueryAdapter);
    }


//    private void getData(String id) {
//
//        String httpUrl = User.mainurl + "sf/lxmx";
//        AsyncHttpClient client_request = new AsyncHttpClient();
//        RequestParams parameters_userInfo = new RequestParams();
//
//        parameters_userInfo.put("mac", mac);
//        parameters_userInfo.put("usercode", username);
//        parameters_userInfo.put("id", id);
//
//        client_request.post(httpUrl, parameters_userInfo,
//                new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(String response) {
//                        try {
//                            JSONObject dataJson = new JSONObject(response);
//                            int code = dataJson.getInt("code");
//                            String msg = dataJson.getString("msg");
//                            noteQueries.clear();
//                            if (code == 0) {
//                                departText.setText(dataJson.getString(""));
//                                nameText.setText(dataJson.getString(""));
//                                startText.setText(dataJson.getString(""));
//                                endText.setText(dataJson.getString(""));
//                                addr = dataJson.getString("addr");
//                                addrText.setText(dataJson.getString(""));
//                                planText.setText(dataJson.getString(""));
//                                noteQueries = getData(dataJson);
//                            }
//                            noteQueryAdapter.notifyDataSetChanged();
//                            ToastUtil.toast(context, msg);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }finally {
//                            ProgressDialogUtil.closeProgressDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable error, String data) {
//                        ToastUtil.toast(context, "网络错误，请重试");
//                        ProgressDialogUtil.closeProgressDialog();
//                    }
//                });
//    }
//
//
//    @NonNull
//    private List<NoteQuery> getData(JSONObject jsonObject) throws JSONException {
//        JSONArray array = jsonObject.getJSONArray("list");
//        List<NoteQuery> mDatas = new ArrayList<>();
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject obj = array.getJSONObject(i);
//            NoteQuery noteQuery = new NoteQuery();
//            noteQuery.setType(obj.getString("id"));
//            noteQuery.setAddr(obj.getString("id"));
//            noteQuery.setQuestion(obj.getString("id"));
//            noteQuery.setAdvise(obj.getString("beginTime"));
//            noteQuery.setEffect(obj.getString("customer"));
//            mDatas.add(noteQuery);
//        }
//        return mDatas;
//    }


    private void showSpinner(View view) {

        noteAddrs.clear();
        effectNoteAddrs.clear();
        addr ="";
        addrCode ="";
        allWait = true;
        for (int i=0 ; i<note.getNoteQueries().size();i++){
            NoteQuery noteQuery = note.getNoteQueries().get(i);
            if ("待走访".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setIscheck("0");
                noteAddrs.add(noteAddr);
            }
            if ("已走访".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setIscheck("0");
                effectNoteAddrs.add(noteAddr);
                allWait = false;
            }
            if ("未走访".equals(noteQuery.getType())){
                allWait = false;
            }
        }

        View v = LayoutInflater.from(context).inflate(R.layout.menu_item, null);
        final PopupWindow pw = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        pw.showAsDropDown(view);
        TextView resultText = (TextView) v.findViewById(R.id.action_result);
        TextView effectText = (TextView) v.findViewById(R.id.action_effect);
        TextView changeText = (TextView) v.findViewById(R.id.action_change);

        if (noteAddrs.size()>0){
            resultText.setVisibility(View.VISIBLE);
        }else {
            resultText.setVisibility(View.GONE);
        }
        if (effectNoteAddrs.size()>0){
            effectText.setVisibility(View.VISIBLE);
        }else {
            effectText.setVisibility(View.GONE);
        }
        if (allWait){
            changeText.setVisibility(View.VISIBLE);
        }else {
            changeText.setVisibility(View.GONE);
        }

        pw.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU
                        && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (pw != null && pw.isShowing()) {
                        pw.dismiss();
                    }
                    return true;
                }
                return false;
            }

        });

        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                type = "result";
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setView(view);
                alertDialog = builder.create();
                alertDialog.show();

                ListView listView = (ListView) view.findViewById(R.id.list_view);
                noteAddrAdapter = new NoteAddrAdapter(context,noteAddrs,true);
                listView.setAdapter(noteAddrAdapter);
                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                Spinner stateSpinner = (Spinner) view.findViewById(R.id.spn_state);
                final List<String> states = new ArrayList<>();
                states.add("已走访");
                states.add("未走访");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,R.layout.spinner_item,states);
                stateSpinner.setAdapter(arrayAdapter);
                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        state = states.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                submitTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int j = 0;j<noteAddrs.size();j++){
                            NoteAddr noteAddr = noteAddrs.get(j);
                            if ("1".equals(noteAddr.getIscheck())){
                                addr += noteAddr.getName()+",";
                                addrCode += noteAddr.getCode()+",";
                            }
                        }
                        Intent intent = new Intent();
                        intent.setClass(context, NoteDealActivity.class);
                        Bundle b = new Bundle();
                        b.putParcelable("note", note);
                        intent.putExtra("bundle", b);
                        intent.putExtra("state", state);
                        intent.putExtra("addr", addr);
                        intent.putExtra("addrCode", addrCode);
                        intent.putExtra("from", "plan");
                        startActivity(intent);
                        if (alertDialog != null){
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        effectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                type = "effect";
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setView(view);
                alertDialog = builder.create();
                alertDialog.show();

                ListView listView = (ListView) view.findViewById(R.id.list_view);
                noteAddrAdapter = new NoteAddrAdapter(context,effectNoteAddrs,false);
                listView.setAdapter(noteAddrAdapter);
                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                LinearLayout stateLl = (LinearLayout) view.findViewById(R.id.ll_state);
                stateLl.setVisibility(View.GONE);

                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                submitTxt.setVisibility(View.GONE);
            }
        });

        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                if (allWait){
                    for (int j = 0;j<noteAddrs.size();j++){
                        NoteAddr noteAddr = noteAddrs.get(j);
                        addr += noteAddr.getName()+",";
                        addrCode += noteAddr.getCode()+",";
                    }
                    Intent intent = new Intent();
                    intent.setClass(context, NoteAddActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("note", note);
                    intent.putExtra("bundle", b);
                    intent.putExtra("addr", addr);
                    intent.putExtra("addrCode", addrCode);
                    intent.putExtra("from", "change");
                    startActivity(intent);
                }else {
                    ToastUtil.toast(context,"已走访或未走访，不能修改");
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.list_view:
                if ("result".equals(type)){
                    NoteAddr noteAddr = noteAddrs.get(position);
                    if ("0".equals(noteAddr.getIscheck())) {
                        noteAddr.setIscheck("1");
                    } else {
                        noteAddr.setIscheck("0");
                    }
                    noteAddrAdapter.notifyDataSetChanged();
                }else {
                    NoteQuery noteQuery = note.getNoteQueries().get(position);
//                    Logger.e("noteQuery.getEffect()>>>"+noteQuery.getEffect());
                    if (Tools.isEmpty(noteQuery.getEffect())){
                        Intent intent = new Intent();
                        intent.setClass(context, NoteDealActivity.class);
                        Bundle b = new Bundle();
                        b.putParcelable("note", note);
                        intent.putExtra("bundle", b);
                        intent.putExtra("rtCode", noteQuery.getRtCode());
                        intent.putExtra("state", "");
                        intent.putExtra("addr", noteQuery.getAddr());
                        intent.putExtra("addrCode", noteQuery.getAddrCode());
                        intent.putExtra("question", noteQuery.getQuestion());
                        intent.putExtra("advise", noteQuery.getAdvise());
                        intent.putExtra("from", type);
                        startActivity(intent);
                        if (alertDialog != null){
                            alertDialog.dismiss();
                        }
                    } else {
                        ToastUtil.toast(context,"已填写成效跟踪");
                    }
                }
                break;
        }
    }

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

}
