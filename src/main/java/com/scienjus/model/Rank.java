package com.scienjus.model;

import java.util.Date;
import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/12/15.
 */
public class Rank {

    private String content;

    private String mode;

    private Date date;

    private List<RankWork> works;

//    get and set


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<RankWork> getWorks() {
        return works;
    }

    public void setWorks(List<RankWork> works) {
        this.works = works;
    }

    //    to string

    @Override
    public String toString() {
        return "Rank{" +
                "content='" + content + '\'' +
                ", mode='" + mode + '\'' +
                ", date=" + date +
                ", works=" + works +
                '}';
    }
}
