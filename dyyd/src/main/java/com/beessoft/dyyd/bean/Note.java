package com.beessoft.dyyd.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by wongxl on 16/4/12.
 */
public class Note implements Parcelable {
    private String id; //编号
    private String rdCode; //   编号
    private String depart;//部门
    private String name;//姓名
    private String start;//开始时间
    private String end;//结束时间
    private String addr;//地址
    private String plan;//计划
    private String date;//创建时间
    private ArrayList<NoteQuery> noteQueries = new ArrayList<>();//查询
//    private String state;//状态

    public Note() {

    }

    protected Note(Parcel in) {
        super();
        id = in.readString();
        rdCode = in.readString();
        depart = in.readString();
        name = in.readString();
        start = in.readString();
        end = in.readString();
        addr = in.readString();
        plan = in.readString();
        date = in.readString();
        in.readTypedList(noteQueries,NoteQuery.CREATOR);
//        noteQueries = in.createTypedArrayList(NoteQuery.CREATOR);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRdCode() {
        return rdCode;
    }

    public void setRdCode(String rdCode) {
        this.rdCode = rdCode;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<NoteQuery> getNoteQueries() {
        return noteQueries;
    }

    public void setNoteQueries(ArrayList<NoteQuery> noteQueries) {
        this.noteQueries = noteQueries;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(rdCode);
        dest.writeString(depart);
        dest.writeString(name);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(addr);
        dest.writeString(plan);
        dest.writeString(date);
        dest.writeTypedList(noteQueries);
    }
}
