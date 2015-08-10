package client;

import bean.Illust;
import bean.RankingMode;
import config.PixivClientConfig;
import net.minidev.json.JSONArray;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 使用Pixiv iOS APi的客户端
 * Pixiv Parser Client use Pixiv iOS Api
 */
public class PixivApiClient {

    /**'
     * 日志文件
     */
    private static final Logger logger = Logger.getLogger(PixivApiClient.class);

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
     * 鉴权Token
     */
    private String accessToken;

    /**
     * 请求的上下文（存储登陆信息）
     */
    private HttpClientContext context;

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
     * 创建一个下载器
     * @return
     */
    public static PixivApiClient create() {
        return new PixivApiClient();
    }

    private PixivApiClient() {
        client = HttpClients.createDefault();
    }

    /**
     * 一个登陆表单的构成
     * params that need to be submitted when logged in
     * @return
     */
    private UrlEncodedFormEntity buildLoginForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", "bYGKuGVw91e0NMfPGp44euvGt59s"));
        params.add(new BasicNameValuePair("client_secret", "HP3RmkgAmEGro0gn1x9ioawQE8WMfvLXDz3ZqxpK"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("grant_type", "password"));
        return new UrlEncodedFormEntity(params, PixivClientConfig.ENCODING);
    }

    /**
     * 登陆
     * login and get access_token
     * @return access_token
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
            HttpPost post = new HttpPost("https://oauth.secure.pixiv.net/auth/token");
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(PixivClientConfig.SOCKET_TIMEOUT).
                    setConnectTimeout(PixivClientConfig.CONNECT_TIMEOUT).
                    build();
            post.setConfig(requestConfig);
            UrlEncodedFormEntity entity = buildLoginForm();
            post.setEntity(entity);
            response = client.execute(post, context);
            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("登陆成功！");
                this.accessToken = getAccessToken(response);
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
     * 从Response中获得AccessToken
     * @param response
     * @return
     */
    private static String getAccessToken(CloseableHttpResponse response) {
        JSONObject json = getResponseContent(response);
        return ((JSONObject)json.get("response")).getAsString("access_token");
    }

    /**
     * 获得返回数据
     * get content from response
     * @param response
     * @return
     */
    private static JSONObject getResponseContent(CloseableHttpResponse response) {
        try {
            return (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent(), PixivClientConfig.ENCODING));
        } catch (IOException e) {
            return null;
        }
    }

    private HttpGet createHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", String.format("Bearer %s", this.accessToken));
        return get;
    }

    public List<String> getRank(Date date) {
        HttpGet get;
        CloseableHttpResponse response;
        JSONObject json;
        int page = 1;
        List<String> ids = new ArrayList<>();
        while (true) {
            String url = buildRankUrl(date, page);
            get = createHttpGet(url);
            try {
                response = client.execute(get, context);
                json = getResponseContent(response);
                JSONArray works = (JSONArray) ((JSONObject)((JSONArray) json.get("response")).get(0)).get("works");
                for (int i = 0; i < works.size(); i++) {
                    JSONObject item = (JSONObject) works.get(i);
                    ids.add(((JSONObject)item.get("work")).getAsString("id"));
                }
                Object nextPage = ((JSONObject) json.get("pagination")).get("next");
                if (nextPage != null) {
                    page = Integer.parseInt(nextPage.toString());
                } else {
                    return ids;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> search(String keyWord) {
        HttpGet get;
        CloseableHttpResponse response;
        JSONObject json;
        int page = 1;
        List<String> ids = new ArrayList<>();
        while (true) {
            String url = buildSearchUrl(keyWord, page);
            logger.error(url);
            get = createHttpGet(url);
            try {
                response = client.execute(get, context);
                json = getResponseContent(response);
                logger.error(json);
                JSONArray works = (JSONArray)json.get("response");
                for (int i = 0; i < works.size(); i++) {
                    JSONObject item = (JSONObject) works.get(i);
                    ids.add(((JSONObject)item.get("work")).getAsString("id"));
                }
                Object nextPage = ((JSONObject) json.get("pagination")).get("next");
                if (nextPage != null) {
                    page = Integer.parseInt(nextPage.toString());
                } else {
                    return ids;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Illust getIllust(String id) {
        String url = "https://public-api.secure.pixiv.net/v1/works/" + id + ".json?image_sizes=small,medium,large&include_stats=true";
        HttpGet get = createHttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(get, context);
            JSONObject json = getResponseContent(response);
            logger.error(json);
            return new Illust(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String buildRankUrl(Date date, int page) {
        return "https://public-api.secure.pixiv.net/v1/ranking/all?" +
                "mode=daily&" +
                "page=" + page + "&" +
                "per_page=50&" +
                "image_size=px_128x128&" +
                "profile_image_sizes=px_170x170";
    }

    public static String buildSearchUrl(String keyWord, int page) {
        return "https://public-api.secure.pixiv.net/v1/search/works.json?" +
                "period=all&" +
                "include_stats=true&" +
                "q=" + keyWord + "&" +
                "page=" + page + "&" +
                "per_page=5&" +
                "order=desc&" +
                "sort=date&" +
                "mode=exact_tag&" +
                "include_sanity_level=true&" +
                "image_size=px_128x128&" +
                "profile_image_sizes=px_170x170";
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
//    public void downloadRankOn(Date aday, RankingMode mode, boolean isR18) {
//        downloadRankBetween(aday, aday, mode, isR18);
//    }

    /**
     * 下载从指定日期到当前的排行榜，不会重复下载
     * @param aday
     */
//    public void downloadRankAfter(Date aday, RankingMode mode, boolean isR18) {
//        downloadRankBetween(aday, new Date(), mode, isR18);
//    }

    /**
     * 关闭PixivClient端
     */
    public void close() {
        try {
            pool.shutdown();
            client.close();
        } catch (IOException e) {
            logger.error("关闭客户端失败：" + e.getMessage());
        }
    }
}
