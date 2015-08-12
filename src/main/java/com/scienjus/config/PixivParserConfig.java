package com.scienjus.config;

import java.nio.charset.Charset;

/**
 * @author XieEnlong
 * @date 2015/8/11.
 */
public class PixivParserConfig {

    /**
     * 不指定数量
     * the number means doesn't use limit
     */
    public static final int NO_LIMIT = -1;

    /**
     * 字符编码
     * charset
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * 登陆地址
     * login url
     */
    public static final String LOGIN_URL = "https://oauth.secure.pixiv.net/auth/token";

    /**
     * 作品详情地址
     * illust detail url
     */
    public static final String ILLUST_DETAIL_URL = "https://public-api.secure.pixiv.net/v1/works/{illustId}.json";

    /**
     * 作者详情地址
     * author detail url
     */
    public static final String AUTHOR_DETAIL_URL = "https://public-api.secure.pixiv.net/v1/users/{authorId}/works.json";

    /**
     * 排行榜地址
     * rank url
     */
    public static final String RANK_URL = "https://public-api.secure.pixiv.net/v1/ranking/all";

    /**
     * 搜索地址
     * search url
     */
    public static final String SEARCH_URL = "https://public-api.secure.pixiv.net/v1/search/works.json";

    /**
     * 默认分页大小
     * default paging size
     */
    public static final int PAGING_SIZE = 20;

    /**
     * 起始页的页码
     * start page number
     */
    public static final int START_PAGE = 1;

    /**
     * 无下一页的页码
     * the page number when do not have next page
     */
    public static final int NO_NEXT_PAGE = -1;
}
