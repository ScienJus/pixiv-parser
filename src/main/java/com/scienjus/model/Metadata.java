package com.scienjus.model;

import java.util.List;

/**
 * @author Scienjus
 * @date 2015/12/15.
 */
public class Metadata {

    List<Page> pages;

//    get and set

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

//    to string

    @Override
    public String toString() {
        return "Metadata{" +
                "pages=" + pages +
                '}';
    }
}
