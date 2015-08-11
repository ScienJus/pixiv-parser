package filter.impl;

import bean.IllustListItem;
import filter.IllustFilter;

/**
 * @author XieEnlong
 * @date 2015/8/11.
 */
public class AlwaysTrueFilter implements IllustFilter {
    @Override
    public boolean doFilter(IllustListItem item) {
        return true;
    }
}
