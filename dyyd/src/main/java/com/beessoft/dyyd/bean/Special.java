package com.beessoft.dyyd.bean;

/**
 * Created by wongxl on 16/4/8.
 */
public class Special {

    private int id;
    private String subjectId;
    private String shopId;
    private String projectId;
    private String name;
    private String remarks;
    private String begin;
    private String end;
    private String modelPhoto;
    private String jd;
    private String wd;
    private String addr;
    private String photo;
    private String result;

    public Special(){

    }

    public Special(int id, String projectId, String shopId, String subjectId, String name, String remarks, String begin, String end,
                   String modelPhoto, String jd, String wd, String addr, String photo,
                   String result){
        super();
        this.id = id;
        this.projectId = projectId;
        this.shopId = shopId;
        this.subjectId = subjectId;
        this.name = name;
        this.remarks = remarks;
        this.begin = begin;
        this.end = end;
        this.modelPhoto = modelPhoto;
        this.jd = jd;
        this.wd = wd;
        this.addr = addr;
        this.photo = photo;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getModelPhoto() {
        return modelPhoto;
    }

    public void setModelPhoto(String modelPhoto) {
        this.modelPhoto = modelPhoto;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
