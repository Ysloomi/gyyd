package com.beessoft.dyyd.bean;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by little_yellow on 17/2/24.
 */

public class Nearby {
    private String med;
    private String name;
    private String jd;
    private String wd;
    private String photo;
    private String operator;
    private LatLng latLng;

    @Override
    public String toString() {
        return "Nearby{" +
                "med='" + med + '\'' +
                ", name='" + name + '\'' +
                ", jd='" + jd + '\'' +
                ", wd='" + wd + '\'' +
                ", photo='" + photo + '\'' +
                ", operator='" + operator + '\'' +
                ", latLng=" + latLng +
                '}';
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }



    public String getMed() {
        return med;
    }

    public void setMed(String med) {
        this.med = med;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
