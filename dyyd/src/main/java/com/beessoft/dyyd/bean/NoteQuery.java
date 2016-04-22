package com.beessoft.dyyd.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wongxl on 16/4/13.
 */
public class NoteQuery implements Parcelable {
    private String rtCode = "";//编码
    private String type = "";//已走访地点、未走访地点、待走访地点
    private String addr = "";//地点
    private String addrCode = "";//地点
    private String question = "";//问题
    private String advise = "";//意见
    private String reason = "";//未走访原因
    private String date = "";//走访或未走访时间
    private String effect = "";//成效
    private String dateEffect = "";//成效时间

    public NoteQuery() {

    }

    protected NoteQuery(Parcel in) {
        super();
        rtCode = in.readString();
        type = in.readString();
        addr = in.readString();
        addrCode = in.readString();
        question = in.readString();
        advise = in.readString();
        reason = in.readString();
        date = in.readString();
        effect = in.readString();
        dateEffect = in.readString();
    }

    public static final Creator<NoteQuery> CREATOR = new Creator<NoteQuery>() {
        @Override
        public NoteQuery createFromParcel(Parcel in) {
            return new NoteQuery(in);
        }

        @Override
        public NoteQuery[] newArray(int size) {
            return new NoteQuery[size];
        }
    };

    public String getRtCode() {
        return rtCode;
    }

    public void setRtCode(String rtCode) {
        this.rtCode = rtCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddrCode() {
        return addrCode;
    }

    public void setAddrCode(String addrCode) {
        this.addrCode = addrCode;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAdvise() {
        return advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateEffect() {
        return dateEffect;
    }

    public void setDateEffect(String dateEffect) {
        this.dateEffect = dateEffect;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rtCode);
        dest.writeString(type);
        dest.writeString(addr);
        dest.writeString(addrCode);
        dest.writeString(question);
        dest.writeString(advise);
        dest.writeString(reason);
        dest.writeString(date);
        dest.writeString(effect);
        dest.writeString(dateEffect);
    }
}
