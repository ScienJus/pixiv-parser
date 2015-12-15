package com.scienjus.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author XieEnlong
 * @date 2015/12/15.
 */
public class RankWork {

    private int rank;

    @JSONField(name = "previous_rank")
    private int previousRank;

    private Work work;

//    get and set

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPreviousRank() {
        return previousRank;
    }

    public void setPreviousRank(int previousRank) {
        this.previousRank = previousRank;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

//    to string

    @Override
    public String toString() {
        return "Rank{" +
                "rank=" + rank +
                ", previousRank=" + previousRank +
                ", work=" + work +
                '}';
    }
}
