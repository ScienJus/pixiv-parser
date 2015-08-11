package filter.impl;

import bean.IllustListItem;
import filter.IllustFilter;

import java.util.Random;

/**
 * @author XieEnlong
 * @date 2015/8/11.
 */
public class RandomFilter implements IllustFilter {

    private Random random = new Random();

    @Override
    public boolean doFilter(IllustListItem item) {
        if (random.nextDouble() < 0.05) {
            return true;
        }
        return false;
    }
}
