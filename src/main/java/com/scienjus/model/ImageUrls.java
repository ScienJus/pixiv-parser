package com.scienjus.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Scienjus
 * @date 2015/12/15.
 */
public class ImageUrls {

    @JSONField(name = "px_128x128")
    private String px128x128;

    @JSONField(name = "px_480mw")
    private String px480mw;

    private String large;

    private String small;

    private String medium;

//    get and set

    public String getPx128x128() {
        return px128x128;
    }

    public void setPx128x128(String px128x128) {
        this.px128x128 = px128x128;
    }

    public String getPx480mw() {
        return px480mw;
    }

    public void setPx480mw(String px480mw) {
        this.px480mw = px480mw;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    //    to string

    @Override
    public String toString() {
        return "ImageUrls{" +
                "px128x128='" + px128x128 + '\'' +
                ", px480mw='" + px480mw + '\'' +
                ", large='" + large + '\'' +
                ", small='" + small + '\'' +
                ", medium='" + medium + '\'' +
                '}';
    }
}
