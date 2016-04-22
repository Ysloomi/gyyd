package com.beessoft.dyyd.bean;

/**
 * Created by wongxl on 16/4/12.
 */
public class NoteAddr{

    private String name;//地点名字
    private String code;//地点编码
    private String ischeck;//是否选中 0未选 1选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
