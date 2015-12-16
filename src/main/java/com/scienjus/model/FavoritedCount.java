package com.scienjus.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Scienjus
 * @date 2015/12/15.
 */
public class FavoritedCount {

    @JSONField(name = "public")
    private int publicCount;

    @JSONField(name = "private")
    private int privateCount;

//    get and set

    public int getPublicCount() {
        return publicCount;
    }

    public void setPublicCount(int publicCount) {
        this.publicCount = publicCount;
    }

    public int getPrivateCount() {
        return privateCount;
    }

    public void setPrivateCount(int privateCount) {
        this.privateCount = privateCount;
    }

//    to string

    @Override
    public String toString() {
        return "FavoritedCount{" +
                "publicCount=" + publicCount +
                ", privateCount=" + privateCount +
                '}';
    }
}


