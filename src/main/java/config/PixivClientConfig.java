package config;

import java.nio.charset.Charset;

/**
 * @author jinxing
 * @date 2015/3/21.
 */
public class PixivClientConfig {

    /**
     * 字符编码
     */
    public static final Charset encoding = Charset.forName("UTF-8");

    /**
     * 默认的图片存放位置
     */
    public static final String default_path = "E:/pixiv/";

    /**
     * 登陆请求地址
     */
    public static final String login_url = "https://www.secure.pixiv.net/login.php";

    /**
     * 搜索请求地址
     */
    public static final String search_url = "http://www.pixiv.net/search.php";

    /**
     * 排行榜请求地址
     */
    public static final String rank_url = "http://www.pixiv.net/ranking.php";

    /**
     * 图片详情请求地址
     */
    public static final String detail_url = "http://www.pixiv.net/member_illust.php";

    /**
     * 下载图片间隔时间
     */
    public static final int socket_timeout = 2000;

    /**
     * 下载图片间隔时间
     */
    public static final int connect_timeout = 2000;

    /**
     * 下载图片间隔时间
     */
    public static final long sleep_time = 200;

    /**
     * 最大重试次数
     */
    public static final int max_failure_time = 5;

}
