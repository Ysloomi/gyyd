package com.beessoft.dyyd.dailywork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.NoteAddrAdapter;
import com.beessoft.dyyd.adapter.NoteQueryAdapter;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.bean.NoteAddr;
import com.beessoft.dyyd.bean.NoteQuery;
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
    private String rtCode="";

    private List<NoteAddr> waitNoteAddrs = new ArrayList<>();
    private List<NoteAddr> effectNoteAddrs = new ArrayList<>();
    private List<NoteAddr> visitNoteAddrs = new ArrayList<>();
    private List<NoteAddr> leaveNoteAddrs = new ArrayList<>();
    private NoteAddrAdapter noteAddrAdapter;

    private Note note;
    private boolean allWait = true;

    private  AlertDialog alertDialog;
    private  AlertDialog alertDialog1;

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

        Bundle b = getIntent().getExtras();
        note = b.getParcelable("note");
        from = b.getString("from");

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
        NoteQuery noteQueryReach = new NoteQuery();
        NoteQuery noteQueryLeave = new NoteQuery();
        NoteQuery noteQueryWait = new NoteQuery();
        String addrWait ="";
        String addrCodeWait= "";
        String addrReach ="";
        String addrCodeReach= "";
        String addrLeave ="";
        String addrCodeLeave = "";
        for (int i = 0;i<note.getNoteQueries().size();i++){
            NoteQuery noteQuery = note.getNoteQueries().get(i);
            if ("待走访".equals(noteQuery.getType())){
                addrWait += noteQuery.getAddr()+",";
                addrCodeWait += noteQuery.getAddrCode()+",";
            } else if("已拜访".equals(noteQuery.getType())){
                addrReach += noteQuery.getAddr()+",";
                addrCodeReach += noteQuery.getAddrCode()+",";
            } else if("已离开".equals(noteQuery.getType())){
                addrLeave += noteQuery.getAddr()+",";
                addrCodeLeave += noteQuery.getAddrCode()+",";
            } else {
                noteQueries.add(noteQuery);
            }
        }
        if (!Tools.isEmpty(addrReach)){
            noteQueryReach.setType("已拜访");
            noteQueryReach.setAddr(addrReach);
            noteQueryReach.setAddrCode(addrCodeReach);
            noteQueries.add(noteQueryReach);
        }
        if (!Tools.isEmpty(addrLeave)){
            noteQueryLeave.setType("已离开");
            noteQueryLeave.setAddr(addrLeave);
            noteQueryLeave.setAddrCode(addrCodeLeave);
            noteQueries.add(noteQueryLeave);
        }
        if (!Tools.isEmpty(addrWait)){
            noteQueryWait.setType("待走访");
            noteQueryWait.setAddr(addrWait);
            noteQueryWait.setAddrCode(addrCodeWait);
            noteQueries.add(noteQueryWait);
        }

        noteQueryAdapter = new NoteQueryAdapter(context,noteQueries);
        listView.setAdapter(noteQueryAdapter);
    }

    private void showSpinner(View view) {

        waitNoteAddrs.clear();
        effectNoteAddrs.clear();
        visitNoteAddrs.clear();
        leaveNoteAddrs.clear();
        addr ="";
        addrCode ="";
        allWait = true;
        for (int i=0 ; i<note.getNoteQueries().size();i++){
            NoteQuery noteQuery = note.getNoteQueries().get(i);
            if ("待走访".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setRtCode(noteQuery.getRtCode());
                noteAddr.setIscheck("0");
                waitNoteAddrs.add(noteAddr);
            }
            if ("已走访".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setRtCode(noteQuery.getRtCode());
                noteAddr.setIscheck("0");
                effectNoteAddrs.add(noteAddr);
                allWait = false;
            }
            if ("已拜访".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setRtCode(noteQuery.getRtCode());
                noteAddr.setIscheck("0");
                visitNoteAddrs.add(noteAddr);
                allWait = false;
            }
            if ("已离开".equals(noteQuery.getType())){
                NoteAddr noteAddr = new NoteAddr();
                noteAddr.setName(noteQuery.getAddr());
                noteAddr.setCode(noteQuery.getAddrCode());
                noteAddr.setRtCode(noteQuery.getRtCode());
                noteAddr.setIscheck("0");
                leaveNoteAddrs.add(noteAddr);
                allWait = false;
            }
            if ("未走访".equals(noteQuery.getType())){
                allWait = false;
            }
        }

        View v = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
        final PopupWindow pw = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setContentView(v);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);
        pw.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        pw.showAsDropDown(view);
        TextView visitTxt = (TextView) v.findViewById(R.id.action_reach);
        TextView leaveTxt = (TextView) v.findViewById(R.id.action_leave);
        TextView resultText = (TextView) v.findViewById(R.id.action_result);
        TextView effectText = (TextView) v.findViewById(R.id.action_effect);
        TextView changeText = (TextView) v.findViewById(R.id.action_change);

        if (waitNoteAddrs.size()>0){
            resultText.setVisibility(View.VISIBLE);
            visitTxt.setVisibility(View.VISIBLE);
        }else {
            if (leaveNoteAddrs.size() > 0){
                resultText.setVisibility(View.VISIBLE);
            }else{
                resultText.setVisibility(View.GONE);
            }
            visitTxt.setVisibility(View.GONE);
        }
        if (visitNoteAddrs.size()>0){
            leaveTxt.setVisibility(View.VISIBLE);
        }else {
            leaveTxt.setVisibility(View.GONE);
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

        visitTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                type = "reach";
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(context).setView(view);
                alertDialog = builder.create();
                alertDialog.show();

                ListView listView = (ListView) view.findViewById(R.id.list_view);
                noteAddrAdapter = new NoteAddrAdapter(context,waitNoteAddrs,false);
                listView.setAdapter(noteAddrAdapter);
                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                submitTxt.setVisibility(View.GONE);
            }
        });

        leaveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                type = "leave";
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr,null);

                AlertDialog.Builder builder=new AlertDialog.Builder(context).setView(view);
                alertDialog = builder.create();
                alertDialog.show();

                ListView listView = (ListView) view.findViewById(R.id.list_view);
                noteAddrAdapter = new NoteAddrAdapter(context,visitNoteAddrs,false);
                listView.setAdapter(noteAddrAdapter);
                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                submitTxt.setVisibility(View.GONE);
            }
        });

        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                type = "result";
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
                        .setTitle("选择状态")
                        .setPositiveButton("已走访", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                state = "已走访";
                                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view);
                                alertDialog = builder.create();
                                alertDialog.show();

                                ListView listView = (ListView) view.findViewById(R.id.list_view);
                                noteAddrAdapter = new NoteAddrAdapter(context, leaveNoteAddrs, true);
                                listView.setAdapter(noteAddrAdapter);
                                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                                submitTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (int j = 0; j < leaveNoteAddrs.size(); j++) {
                                            NoteAddr noteAddr = leaveNoteAddrs.get(j);
                                            if ("1".equals(noteAddr.getIscheck())) {
                                                addr += noteAddr.getName() + ",";
                                                addrCode += noteAddr.getCode() + ",";
                                                rtCode += noteAddr.getRtCode() + ",";
                                            }
                                        }
                                        if (!TextUtils.isEmpty(addr)){
                                            Intent intent = new Intent();
                                            intent.setClass(context, NoteDealActivity.class);
                                            Bundle b = new Bundle();
                                            b.putParcelable("note", note);
                                            b.putString("state", state);
                                            b.putString("addr", addr);
                                            b.putString("addrCode", addrCode);
                                            b.putString("from", type);
                                            b.putString("rtCode", rtCode);
                                            intent.putExtras(b);
                                            startActivity(intent);
                                            if (alertDialog != null) {
                                                alertDialog.dismiss();
                                            }
                                        }else{
                                            ToastUtil.toast(context,"请选择地点");
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("未走访", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                state = "未走访";
                                View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_addr, null);

                                AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view);
                                alertDialog = builder.create();
                                alertDialog.show();

                                ListView listView = (ListView) view.findViewById(R.id.list_view);
                                noteAddrAdapter = new NoteAddrAdapter(context, waitNoteAddrs, true);
                                listView.setAdapter(noteAddrAdapter);
                                listView.setOnItemClickListener(NoteQueryDetailActivity.this);

                                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                                submitTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (int j = 0; j < waitNoteAddrs.size(); j++) {
                                            NoteAddr noteAddr = waitNoteAddrs.get(j);
                                            if ("1".equals(noteAddr.getIscheck())) {
                                                addr += noteAddr.getName() + ",";
                                                addrCode += noteAddr.getCode() + ",";
                                            }
                                        }
                                        if (!TextUtils.isEmpty(addr)) {
                                            Intent intent = new Intent();
                                            intent.setClass(context, NoteDealActivity.class);
                                            Bundle b = new Bundle();
                                            b.putParcelable("note", note);
                                            b.putString("state", state);
                                            b.putString("addr", addr);
                                            b.putString("addrCode", addrCode);
                                            b.putString("from", type);
                                            intent.putExtras(b);
                                            startActivity(intent);
                                            if (alertDialog != null) {
                                                alertDialog.dismiss();
                                            }
                                        } else {
                                            ToastUtil.toast(context, "请选择地点");
                                        }
                                    }
                                });
                            }
                        });

                alertDialog1 = builder1.create();
                alertDialog1.show();
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

                TextView submitTxt = (TextView) view.findViewById(R.id.txt_submit);
                submitTxt.setVisibility(View.GONE);
            }
        });

        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
                if (allWait){
                    for (int j = 0; j< waitNoteAddrs.size(); j++){
                        NoteAddr noteAddr = waitNoteAddrs.get(j);
                        addr += noteAddr.getName()+",";
                        addrCode += noteAddr.getCode()+",";
                    }
                    Intent intent = new Intent();
                    intent.setClass(context, NoteAddActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("note", note);
                    b.putString("addr", addr);
                    b.putString("addrCode", addrCode);
                    b.putString("from", "change");
                    intent.putExtras(b);
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
                    if ("已走访".equals(state)) {
                        NoteAddr noteAddr = leaveNoteAddrs.get(position);
                        if ("0".equals(noteAddr.getIscheck())) {
                            noteAddr.setIscheck("1");
                        } else {
                            noteAddr.setIscheck("0");
                        }
                    } else {
                        NoteAddr noteAddr = waitNoteAddrs.get(position);
                        if ("0".equals(noteAddr.getIscheck())) {
                            noteAddr.setIscheck("1");
                        } else {
                            noteAddr.setIscheck("0");
                        }
                    }
                    noteAddrAdapter.notifyDataSetChanged();
                }else if ("effect".equals(type)){
                    //因为在最上面所以可以直接用notequery获取
                    NoteQuery noteQuery = note.getNoteQueries().get(position);
                    if (Tools.isEmpty(noteQuery.getEffect())){
                        Intent intent = new Intent();
                        intent.setClass(context, NoteDealActivity.class);
                        Bundle b = new Bundle();
                        b.putParcelable("note", note);
                        b.putString("rtCode", noteQuery.getRtCode());
                        b.putString("state", "");
                        b.putString("addr", noteQuery.getAddr());
                        b.putString("addrCode", noteQuery.getAddrCode());
                        b.putString("question", noteQuery.getQuestion());
                        b.putString("advise", noteQuery.getAdvise());
                        b.putString("from", type);
                        intent.putExtras(b);
                        startActivity(intent);
                        if (alertDialog != null){
                            alertDialog.dismiss();
                        }
                    } else {
                        ToastUtil.toast(context,"已填写成效跟踪");
                    }
                } else if ("reach".equals(type)){
                    NoteAddr noteAddr = waitNoteAddrs.get(position);
                    Intent intent = new Intent();
                    intent.setClass(context, NoteDealActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("note", note);
                    b.putString("addr", noteAddr.getName());
                    b.putString("addrCode", noteAddr.getCode());
                    b.putString("from", type);
                    intent.putExtras(b);
                    startActivity(intent);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                } else if ("leave".equals(type)){
                    NoteAddr noteAddr = visitNoteAddrs.get(position);
                    Intent intent = new Intent();
                    intent.setClass(context, NoteDealActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("note", note);
                    b.putString("addr", noteAddr.getName());
                    b.putString("addrCode", noteAddr.getCode());
                    b.putString("from", type);
                    b.putString("rtCode", noteAddr.getRtCode());
                    intent.putExtras(b);
                    startActivity(intent);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
                break;
        }
    }
}
