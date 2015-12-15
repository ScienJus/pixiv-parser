package com.scienjus.filter.impl;

import com.scienjus.bean.IllustListItem;
import com.scienjus.filter.IllustFilter;

import java.util.Random;

/**
 * @author Scienjus
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
