package com.scienjus.filter.impl;

import com.scienjus.bean.IllustListItem;
import com.scienjus.filter.IllustFilter;

/**
 * @author Scienjus
 * @date 2015/8/11.
 */
public class FavoriteCountFilter implements IllustFilter {

    private int baseFavoriteCount;

    public FavoriteCountFilter(int baseFavoriteCount) {
        this.baseFavoriteCount = baseFavoriteCount;
    }

    @Override
    public boolean doFilter(IllustListItem item) {
        if (item.getFavoriteCount() > baseFavoriteCount) {
            return true;
        }
        return false;
    }
}
