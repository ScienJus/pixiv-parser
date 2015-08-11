package client;

import bean.Illust;
import bean.IllustListItem;
import config.PixivApiClientConfig;
import config.PixivClientConfig;
import filter.IllustFilter;
import filter.impl.AlwaysTrueFilter;
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
import thread.IllustDownloadTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 使用Pixiv iOS APi的客户端
 * Pixiv Parser Client use Pixiv iOS Api
 */
public class PixivApiClient {

    /**'
     * 日志文件
     * the logger
     */
    private static final Logger LOGGER = Logger.getLogger(PixivApiClient.class);

    /**
     * 日期格式化
     * format date to url param
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 用户名
     * the username to login
     */
    private String username;

    /**
     * 密码
     * the password to login
     */
    private String password;

    /**
     * 鉴权Token
     * the access token to use pixiv api
     */
    private String accessToken;

    /**
     * 请求的上下文（存储登陆信息）
     * store cookie and session id
     */
    private HttpClientContext context;

    /**
     * http请求发送端
     * send to pixiv
     */
    private CloseableHttpClient client;

    /**
     * 设置用户名
     * set your username (pixiv id)
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 设置密码
     * set your pixiv id's password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 创建一个下载器
     * create a new instance
     * @return
     */
    public static PixivApiClient create() {
        return new PixivApiClient();
    }

    private PixivApiClient() {
        client = HttpClients.createDefault();
    }

    /**
     * 登陆表单的构成
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
            LOGGER.error("用户名或密码为空！");
            return false;
        }
        LOGGER.info("当前登录的用户为：" + username);
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
                LOGGER.info("登陆成功！");
                this.accessToken = getAccessToken(response);
                return true;
            } else {
                LOGGER.error("登陆失败！请检查用户名或密码是否正确");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 通过id获取作品
     * get illust by id
     * @param id
     * @return
     */
    public Illust getIllust(String id) {
        String url = "https://public-api.secure.pixiv.net/v1/works/" + id + ".json?image_sizes=small,medium,large&include_stats=true";
        HttpGet get = defaultHttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(get, context);
            JSONObject json = getResponseContent(response);
            return new Illust(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从Response中获得AccessToken
     * get access token from response
     * @param response
     * @return
     */
    private static String getAccessToken(CloseableHttpResponse response) {
        JSONObject json = getResponseContent(response);
        return ((JSONObject)json.get("response")).getAsString("access_token");
    }

    /**
     * 获得返回数据
     * get json content from response
     * @param response
     * @return
     */
    private static JSONObject getResponseContent(CloseableHttpResponse response) {
        try {
            JSONObject content = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent(), PixivClientConfig.ENCODING));
            response.close();
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 创建默认的httpGet请求
     * create a defalut http get
     * @param url
     * @return
     */
    private HttpGet defaultHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", String.format("Bearer %s", this.accessToken));
        get.setHeader("Referer", "http://spapi.pixiv.net/");
        get.setHeader("User-Agent", "PixivIOSApp/5.6.0");
        return get;
    }

    /**
     * 获得某天的排行榜
     * get ranking on one day
     * @param date
     * @return
     */
    public List<IllustListItem> ranking(Date date) {
        return ranking(date, new AlwaysTrueFilter(), PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 获得某天的排行榜（使用自定义过滤器筛选）
     * get ranking on one day with custom filter
     * @param date
     * @return
     */
    public List<IllustListItem> ranking(Date date, IllustFilter filter) {
        return ranking(date, filter, PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 获得某天的排行榜（指定作品数）
     * get ranking on one day with limit
     * @param date
     * @return
     */
    public List<IllustListItem> ranking(Date date, int limit) {
        return ranking(date, new AlwaysTrueFilter(), limit);
    }

    /**
     * 获得某天的排行榜（使用自定义过滤器筛选并指定作品数）
     * get ranking on one day with custom filter and limit
     * @param date
     * @return
     */
    public List<IllustListItem> ranking(Date date, IllustFilter filter, int limit) {
        HttpGet get;
        CloseableHttpResponse response;
        JSONObject json;
        int page = 1;
        List<IllustListItem> items = new ArrayList<>();
        while (true) {
            String url = buildRankingUrl(date, page);
            get = defaultHttpGet(url);
            try {
                response = client.execute(get, context);
                json = getResponseContent(response);
                JSONArray works = (JSONArray) ((JSONObject)((JSONArray) json.get("response")).get(0)).get("works");
                for (int i = 0; i < works.size(); i++) {
                    IllustListItem item = new IllustListItem((JSONObject) ((JSONObject) works.get(i)).get("work"));
                    if (filter.doFilter(item)) {
                        items.add(item);
                        if (limit != PixivApiClientConfig.NO_LIMIT && items.size() >= limit) {
                            return items;
                        }
                    }
                }
                Integer nextPage = getNextPage(json);
                if (nextPage != null) {
                    page = nextPage;
                } else {
                    return items;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询作品
     * search illusts by key word
     * @param keyWord
     * @return
     */
    public List<IllustListItem> search(String keyWord) {
        return search(keyWord, new AlwaysTrueFilter(), PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 查询作品（使用自定义过滤器筛选）
     * search illusts by key word with custom filter
     * @param keyWord
     * @return
     */
    public List<IllustListItem> search(String keyWord, IllustFilter filter) {
        return search(keyWord, filter, PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 查询作品（指定作品数）
     * search illusts by key word with limit
     * @param keyWord
     * @return
     */
    public List<IllustListItem> search(String keyWord, int limit) {
        return search(keyWord, new AlwaysTrueFilter(), limit);
    }

    /**
     * 查询作品（使用自定义过滤器筛选并指定作品数）
     * search illusts by key word with custom filter and limit
     * @param keyWord
     * @return
     */
    public List<IllustListItem> search(String keyWord, IllustFilter filter, int limit) {
        HttpGet get;
        CloseableHttpResponse response;
        JSONObject json;
        int page = 3;
        List<IllustListItem> items = new ArrayList<>();
        while (true) {
            String url = buildSearchUrl(keyWord, page);
            get = defaultHttpGet(url);
            try {
                response = client.execute(get, context);
                json = getResponseContent(response);
                JSONArray works = (JSONArray)json.get("response");
                for (int i = 0; i < works.size(); i++) {
                    IllustListItem item = new IllustListItem((JSONObject) works.get(i));
                    if (filter.doFilter(item)) {
                        items.add(item);
                        if (limit != PixivApiClientConfig.NO_LIMIT && items.size() >= limit) {
                            return items;
                        }
                    }
                }
                Integer nextPage = getNextPage(json);
                if (nextPage != null) {
                    page = nextPage;
                } else {
                    return items;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得指定作者的作品
     * get illusts by author
     * @param authorId
     * @return
     */
    public List<IllustListItem> byAuthor(String authorId) {
        return byAuthor(authorId, new AlwaysTrueFilter(), PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 获得指定作者的作品（使用自定义过滤器筛选）
     * get illusts by author with custom filter
     * @param authorId
     * @return
     */
    public List<IllustListItem> byAuthor(String authorId, IllustFilter filter) {
        return byAuthor(authorId, filter, PixivApiClientConfig.NO_LIMIT);
    }

    /**
     * 获得指定作者的作品（指定作品数）
     * get illusts by author with limit
     * @param authorId
     * @return
     */
    public List<IllustListItem> byAuthor(String authorId, int limit) {
        return byAuthor(authorId, new AlwaysTrueFilter(), limit);
    }

    /**
     * 获得指定作者的作品（使用自定义过滤器筛选并指定作品数）
     * get illusts by author with custom filter and limit
     * @param authorId
     * @return
     */
    public List<IllustListItem> byAuthor(String authorId, IllustFilter filter, int limit) {
        HttpGet get;
        CloseableHttpResponse response;
        JSONObject json;
        int page = 3;
        List<IllustListItem> items = new ArrayList<>();
        while (true) {
            String url = buildByAuthorUrl(authorId, page);
            get = defaultHttpGet(url);
            try {
                response = client.execute(get, context);
                json = getResponseContent(response);
                JSONArray works = (JSONArray)json.get("response");
                for (int i = 0; i < works.size(); i++) {
                    IllustListItem item = new IllustListItem((JSONObject) works.get(i));
                    if (filter.doFilter(item)) {
                        items.add(item);
                        if (limit != PixivApiClientConfig.NO_LIMIT && items.size() >= limit) {
                            return items;
                        }
                    }
                }
                Integer nextPage = getNextPage(json);
                if (nextPage != null) {
                    page = nextPage;
                } else {
                    return items;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * byAuthor请求的url
     * the url in byAuthor api
     * @param authorId
     * @param page
     * @return
     */
    private static String buildByAuthorUrl(String authorId, int page) {
        return "https://public-api.secure.pixiv.net/v1/users/" + authorId + "/works.json?" +
                "period=all&" +
                "include_stats=true&" +
                "page=" + page + "&" +
                "per_page=5&" +
                "order=desc&" +
                "sort=date&" +
                "mode=exact_tag&" +
                "include_sanity_level=true&" +
                "image_size=px_128x128,small,large&" +
                "profile_image_sizes=px_170x170";
    }

    /**
     * 请求排行榜的url
     * the url in ranking api
     * @param date
     * @param page
     * @return
     */
    public static String buildRankingUrl(Date date, int page) {
        return "https://public-api.secure.pixiv.net/v1/ranking/all?" +
                "mode=daily&" +
                "page=" + page + "&" +
                "date=" + sdf.format(date) + "&" +
                "per_page=50&" +
                "image_size=px_128x128&" +
                "profile_image_sizes=px_170x170";
    }

    /**
     * 请求搜索的url
     * the url in search api
     * @param keyWord
     * @param page
     * @return
     */
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
                "image_size=px_128x128,small,large&" +
                "profile_image_sizes=px_170x170";
    }

    /**
     * 获得下一页
     * get next page number
     * @param json
     * @return
     */
    public static Integer getNextPage(JSONObject json) {
        Object nextPage = ((JSONObject) json.get("pagination")).get("next");
        if (nextPage != null) {
            return Integer.parseInt(nextPage.toString());
        }
        return null;
    }

    public static void main(String[] args) {
        PixivApiClient api = PixivApiClient.create();
        api.setUsername("1498129534@qq.com");
        api.setPassword("a123456");
        if (api.login()) {
            List<IllustListItem> items = api.search("kancolle", 1);
            for (IllustListItem item : items) {
                new Thread(new IllustDownloadTask("E:/kancolle", api.getIllust(item.getId()))).start();
            }
        }
    }

    /**
     * 关闭PixivClient端
     * close the client
     */
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.error("关闭客户端失败：" + e.getMessage());
        }
    }
}
