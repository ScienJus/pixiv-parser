package com.scienjus.bean;

/**
 * 作品的年龄限制
 * the age limit of illust
 * @author ScienJus
 * @date 2015/8/11.
 */
public enum AgeLimit {
    r18,
    all;

    public static AgeLimit customValueOf(String name) {
        if ("all-age".equals(name)) {
            return all;
        }
        if ("r18".equals(name)) {
            return r18;
        }
        return null;
    }
}
