package client;

import bean.Image;
import bean.RankingMode;
import config.PixivClientConfig;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import parser.PageParser;
import thread.ImageDownloadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 登陆/搜索并下载符合要求的图片/排行榜（暂未实现）
 */
public class PixivClient {

    /**'
     * 日志文件
     */
    private static final Logger logger = Logger.getLogger(PixivClient.class);

    /**
     * 下载图片的线程池
     */
    public static ExecutorService pool;

    /**
     * 日期格式化
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 图片存放位置
     */
    private String path;

    /**
     * 请求的上下文（存储登陆信息）
     */
    private HttpClientContext context;

    /**
     * html文本解析器
     */
    private PageParser parser;

    /**
     * 缓存已经下载的图片id
     */
    private Set<String> cache;

    /**
     * http请求发送端
     */
    private CloseableHttpClient client;

    /**
     * 设置用户名
     * @param username  你的pixiv账号
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 设置密码
     * @param password  对应的pixiv密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 创建一个默认的下载器，它会把图片存放在默认位置
     * @return
     */
    public static PixivClient createDefault() {
        return create(PixivClientConfig.DEFAULT_PATH);
    }

    /**
     * 创建一个指定存放位置的下载器
     * @param path
     * @return
     */
    public static PixivClient create(String path) {
        if (path == null || "".equals(path)) {
            logger.error("请输入地址！");
            return null;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            logger.error("选择的路径并不是一个文件夹！");
            return null;
        }
        return new PixivClient(path);
    }

    private PixivClient(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if (pool == null) {
            pool = Executors.newFixedThreadPool(PixivClientConfig.POOL_SIZE);
        }
        this.path = path;
        parser = new PageParser();
        cache = new HashSet<>();
        client = HttpClients.createDefault();
    }

    /**
     * 一个登陆表单的构成
     * @return
     */
    private UrlEncodedFormEntity buildLoginForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("mode", "login"));
        params.add(new BasicNameValuePair("pixiv_id", username));
        params.add(new BasicNameValuePair("pass", password));
        params.add(new BasicNameValuePair("return_to", "/"));
        params.add(new BasicNameValuePair("skip", "1"));
        return new UrlEncodedFormEntity(params, PixivClientConfig.ENCODING);
    }

    /**
     * 登陆
     * @return 登陆结果
     */
    public boolean login() {
        if (username == null || password == null) {
            logger.error("用户名或密码为空！");
            return false;
        }
        logger.info("当前登录的用户为：" + username);
        context = HttpClientContext.create();
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(PixivClientConfig.LOGIN_URL);
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(PixivClientConfig.SOCKET_TIMEOUT).
                    setConnectTimeout(PixivClientConfig.CONNECT_TIMEOUT).
                    build();
            post.setConfig(requestConfig);
            UrlEncodedFormEntity entity = buildLoginForm();
            post.setEntity(entity);
            response = client.execute(post, context);
            if (response.getStatusLine().getStatusCode() == 302) {
                logger.info("登陆成功！");
                return true;
            } else {
                logger.error("登陆失败！请检查用户名或密码是否正确");
                return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 通过关键词搜索
     * @param word  关键词
     * @param isR18 是否只需要r18
     * @param praise    收藏数过滤条件
     */
    public void searchByKeyword(String word, boolean isR18, int praise) {
        if (word == null || "".equals(word.trim())) {
            logger.error("请输入关键词！");
            return;
        }
        logger.info("开始下载收藏数大于" + praise + "的\"" + word + "\"所有图片");
        searchAndDownload(bulidSearchUrl(word, isR18), praise);
    }

    /**
     * 通过作者搜索
     * @param id
     */
    public void searchByAuthor(String id) {
        if (id == null || "".equals(id)) {
            logger.error("请输入作者id！");
            return;
        }
        logger.info("开始下载作者id[" + id + "]的所有图片");
        searchAndDownload(buildAuthorUrl(id), -1);
    }

    /**
     * 重连机制获取页面
     * @param url
     * @return
     */
    private String getPageWithReconnection(String url) {
        int fail = 0;
        String pageHtml = null;
        while (pageHtml == null) {
            pageHtml = getPage(url);
            if (fail++ > PixivClientConfig.MAX_FAILURE_TIME) {
                return null;
            }
            try {
                Thread.sleep(PixivClientConfig.SLEEP_TIME);
            } catch (InterruptedException e) {}
        }
        return pageHtml;
    }

    /**
     * 请求并获得页面
     * @param url
     * @return
     */
    private String getPage(String url) {
        url = decodeUrl(url);
        CloseableHttpResponse response = null;
        HttpGet get = null;
        client = HttpClients.createDefault();
        try {
            get = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(PixivClientConfig.SOCKET_TIMEOUT).setConnectTimeout(PixivClientConfig.CONNECT_TIMEOUT).build();
            get.setConfig(requestConfig);
            response = client.execute(get, context);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), PixivClientConfig.ENCODING));
            StringBuilder pageHTML = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                pageHTML.append(line);
                pageHTML.append("\r\n");
            }
            return pageHTML.toString();
        } catch (IOException e) {
            logger.error("获取网页失败：" + url);
            logger.error(e.getMessage());
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 请求地址并下载
     * @param url   请求的地址
     * @param praise    收藏数过滤条件
     */
    private void searchAndDownload(String url, int praise) {
        try {
            String pageHtml = getPageWithReconnection(url);
            if (pageHtml == null) {
                return;
            }
            List<String> ids = parser.parseList(pageHtml, praise);
            if (ids != null) {
                for (String id : ids) {
                    downloadImage(id);
                }
            }
            String next = parser.parseNextPage(pageHtml);
            if (next != null) {
                if (url.startsWith(PixivClientConfig.SEARCH_URL)) {
                    searchAndDownload(PixivClientConfig.SEARCH_URL + next, praise);
                } else if (url.startsWith(PixivClientConfig.DETAIL_URL)) {
                    searchAndDownload(PixivClientConfig.DETAIL_URL + next, praise);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * url转译
     * @param url
     * @return
     */
    private String decodeUrl(String url) {
        return URLDecoder.decode(url).replace("&amp;", "&").replace(" ", "%20");
    }

    /**
     * 下载该页面的图片（可能有多张）
     * @param id    作品id
     */
    private void downloadImage(String id) {
        if (cache.contains(id)) {
            return;
        }
        String url = buildDetailUrl(id);
        String pageHtml = getPageWithReconnection(url);
        if (pageHtml == null) {
            return;
        }
        if (parser.isManga(pageHtml)) {
            url = url.replace("medium", "manga");
            pageHtml = getPageWithReconnection(url);
            if (pageHtml == null) {
                return;
            }
            List<String> images = parser.parseManga(pageHtml);
            if (images != null) {
                int i = 1;
                for (String imgUrl : images) {
                    Image image = new Image();
                    image.setDir(path);
                    image.setId(id);
                    image.setReferer(url);
                    image.setChildId("" + i++);
                    image.setUrl(imgUrl);
                    pool.execute(new ImageDownloadTask(client, image));
                    try {
                        Thread.sleep(PixivClientConfig.SLEEP_TIME);
                    } catch (InterruptedException e) {}
                }
            }
        } else {
            String imgUrl = parser.parseMedium(pageHtml);
            if (imgUrl != null) {
                Image image = new Image();
                image.setDir(path);
                image.setId(id);
                image.setReferer(url);
                image.setUrl(imgUrl);
                pool.execute(new ImageDownloadTask(client, image));
                try {
                    Thread.sleep(PixivClientConfig.SLEEP_TIME);
                } catch (InterruptedException e) {}
            }
        }
        cache.add(id);
    }

    /**
     * 下载所有排行榜图片，不会重复下载
     * @param mode  排名方式
     * @param isR18 是否R18
     */
    public void downloadAllRank(RankingMode mode, boolean isR18) {
        try {
            downloadRankAfter(sdf.parse("20070913"), mode,  isR18);
        } catch (ParseException e) {
            logger.error("这代码写的有问题：" + e.getMessage());
        }
    }

    /**
     * 下载某个时间段的排行榜图片，不会重复下载
     * @param start 开始时间
     * @param end   结束时间
     * @param mode  排名方式
     * @param isR18 是否R18
     */
    public void downloadRankBetween(Date start, Date end, RankingMode mode, boolean isR18) {
        if (start == null || end == null || end.getTime() < start.getTime()) {
            logger.error("输入的时间不正确！");
            return;
        }
        if (mode == null) {
            logger.error("请选择排行榜类型！");
            return;
        }
        String date = sdf.format(end);
        int page = 1;
        String endDate = sdf.format(start);
        while (true) {
            if (page == 1) {
                logger.info("开始下载[" + date + "]的排行榜");
            }
            logger.info(buildRankUrl(date, page, mode, isR18));
            String pageJson = getPageWithReconnection(buildRankUrl(date, page, mode, isR18));
            if (pageJson == null) {
                return;
            }
            JSONObject json = (JSONObject)JSONValue.parse(pageJson);
            List<String> ids = parser.praseRank(json);
            for (String id : ids) {
                downloadImage(id);
            }
            if (json.get("next") != null) {
                int newPage = Integer.parseInt(String.valueOf(json.get("next")));
                if (page != newPage) {
                    page = newPage;
                    continue;
                }
            }
            if (json.get("prev_date") != null) {
                if (date.equals(endDate)) {
                    logger.info("排行榜已全部下载完成！");
                    return;
                }
                page = 1;
                date = String.valueOf(json.get("prev_date"));
                continue;
            }
            return;
        }
    }

    /**
     * 合成一个作者主页的连接
     * @return
     */
    private String buildAuthorUrl(String id) {
        return PixivClientConfig.DETAIL_URL + "?id=" + id;
    }

    /**
     * 合成一个作品详情页的链接
     * @param id 作品id
     * @return
     */
    private String buildDetailUrl(String id) {
        return PixivClientConfig.DETAIL_URL + "?mode=medium&illust_id=" + id;
    }

    /**
     * 合成一个排行榜的链接
     * @param aday 哪一天
     * @param page 第几页
     * @param mode  排名方式
     * @param isR18 是否R18
     * @return
     */
    private String buildRankUrl(String aday, int page, RankingMode mode, boolean isR18) {
        String param;
        switch (mode) {
            case all:
                param = "daily";
                break;
            case rookie:
                param = "rookie";
                break;
            case original:
                param = "original";
                break;
            case male:
                param = "male";
                break;
            case female:
                param = "female";
                break;
            default:
                logger.error("枚举定义的有问题！");
                return null;
        }
        if (isR18) {
            param += "_r18";
        }
        return PixivClientConfig.RANK_URL + "?format=json&content=illust&date=" + aday + "&p=" + page + "&mode=" + param;
    }

    /**
     * 合成一个搜索的链接
     * @param word  关键词
     * @param isR18 是否R18
     * @return
     */
    private String bulidSearchUrl(String word, boolean isR18) {
        return PixivClientConfig.SEARCH_URL + "?word=" + word + "&r18=" + (isR18 ? "1" : "0");
    }

    /**
     * 下载某天的排行榜图片
     * @param aday
     */
    public void downloadRankOn(Date aday, RankingMode mode, boolean isR18) {
        downloadRankBetween(aday, aday, mode, isR18);
    }

    /**
     * 下载从指定日期到当前的排行榜，不会重复下载
     * @param aday
     */
    public void downloadRankAfter(Date aday, RankingMode mode, boolean isR18) {
        downloadRankBetween(aday, new Date(), mode, isR18);
    }

    /**
     * 关闭PixivClient端
     */
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            logger.error("关闭客户端失败：" + e.getMessage());
        }
    }
}
