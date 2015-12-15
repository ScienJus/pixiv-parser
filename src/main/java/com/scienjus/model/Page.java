package com.scienjus.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author Scienjus
 * @date 2015/12/15.
 */
public class Page {

    @JSONField(name = "image_urls")
    ImageUrls imageUrls;

//    get and set

    public ImageUrls getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ImageUrls imageUrls) {
        this.imageUrls = imageUrls;
    }

//    to string

    @Override
    public String toString() {
        return "Page{" +
                "imageUrls=" + imageUrls +
                '}';
    }
}
