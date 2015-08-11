package filter.impl;

import bean.IllustListItem;
import filter.IllustFilter;

/**
 * @author XieEnlong
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
