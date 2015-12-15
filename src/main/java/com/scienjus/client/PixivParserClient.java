package com.scienjus.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scienjus.callback.WorkCallback;
import com.scienjus.config.PixivParserConfig;
import com.scienjus.filter.WorkFilter;
import com.scienjus.model.Rank;
import com.scienjus.model.Work;
import com.scienjus.param.ParserParam;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 使用Pixiv iOS APi的客户端
 * Pixiv Parser Client use Pixiv iOS Api
 */
public class PixivParserClient {

    /**'
     * 日志文件
     * the logger
     */
    private static final Logger LOGGER = Logger.getLogger(PixivParserClient.class);

    /**
     * 日期格式化
     * format date to url param
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
     * set your pixiv password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public PixivParserClient() {
        client = HttpClients.createDefault();
    }

    /**
     * 登陆表单的构成
     * params that need to be submitted when logged in
     * @return
     */
    private UrlEncodedFormEntity buildLoginForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", "bYGKuGVw91e0NMfPGp44euvGt59s"));    //感谢 pixiv-py 提供
        params.add(new BasicNameValuePair("client_secret", "HP3RmkgAmEGro0gn1x9ioawQE8WMfvLXDz3ZqxpK"));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("grant_type", "password"));
        return new UrlEncodedFormEntity(params, PixivParserConfig.CHARSET);
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
        HttpPost post = new HttpPost(PixivParserConfig.LOGIN_URL);
        post.setEntity(buildLoginForm());
        try (CloseableHttpResponse response = client.execute(post)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                LOGGER.info("登录成功！");
                this.accessToken = getAccessToken(response);
                return true;
            } else {
                LOGGER.error("登录失败！请检查用户名或密码是否正确");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    /**
     * 通过id获取作品
     * get illust by id
     * @param workId
     * @return
     */
    public Work getWork(int workId) {
        String url = buildDetailUrl(workId);
        HttpGet get = defaultHttpGet(url);
        try (CloseableHttpResponse response = client.execute(get)) {
            JSONObject json = getResponseContent(response);
            return JSON.parseObject(json.getJSONArray("response").getJSONObject(0).toJSONString(), Work.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * 从Response中获得AccessToken
     * get access token from response
     * @param response
     * @return
     */
    private static String getAccessToken(CloseableHttpResponse response) throws IOException {
        JSONObject json = getResponseContent(response);
        return json.getJSONObject("response").getString("access_token");
    }

    /**
     * 获得返回数据
     * get json content from response
     * @param response
     * @return
     */
    private static JSONObject getResponseContent(CloseableHttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), PixivParserConfig.CHARSET));
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return JSON.parseObject(buffer.toString());
    }

    /**
     * 创建默认的httpGet请求
     * create a defalut http get
     * @param url
     * @return
     */
    private HttpGet defaultHttpGet(String url) {
        url = url.replace(" ", "%20");
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", String.format("Bearer %s", this.accessToken));
        get.setHeader("Referer", "http://spapi.pixiv.net/");
        get.setHeader("User-Agent", "PixivIOSApp/5.6.0");
        return get;
    }

    /**
     * 获得当天的排行榜
     * get now ranking
     * @return
     */
    public Rank ranking() {
        return ranking((Date)null);
    }

    /**
     * 获得某天的排行榜（使用自定义过滤器筛选并指定作品数）
     * get ranking on one day with custom filter and limit
     * @param date
     * @return
     */
    public Rank ranking(Date date) {
        HttpGet get;
        JSONObject json;
        int page = PixivParserConfig.START_PAGE;
        Rank rank = null;
        while (true) {
            String url = buildRankUrl(date, page);
            get = defaultHttpGet(url);
            try (CloseableHttpResponse response = client.execute(get)) {
                json = getResponseContent(response);
                JSONArray body = JSON.parseObject(json.toJSONString()).getJSONArray("response");
                if (rank == null) {
                    rank = JSON.parseObject(body.getJSONObject(0).toJSONString(), Rank.class);
                } else {
                    rank.getWorks().addAll(JSON.parseObject(body.getJSONObject(0).toJSONString(), Rank.class).getWorks());
                }
                int nextPage = getNextPage(json);
                if (nextPage != PixivParserConfig.NO_NEXT_PAGE) {
                    page = nextPage;
                } else {
                    return rank;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 查询作品
     * search illusts by key word
     * @param keyWord
     * @return
     */
    public List<Work> search(String keyWord) {
        return search(keyWord, new ParserParam());
    }

    /**
     * 查询作品（使用自定义过滤器筛选并指定作品数）
     * search illusts by key word with custom filter and limit
     * @param keyWord
     * @return
     */
    public List<Work> search(String keyWord, ParserParam param) {
        HttpGet get;
        JSONObject json;
        int page = PixivParserConfig.START_PAGE;
        List<Work> works = new ArrayList<>();
        while (true) {
            String url = buildSearchUrl(keyWord, page);
            get = defaultHttpGet(url);
            try (CloseableHttpResponse response = client.execute(get)) {
                json = getResponseContent(response);
                JSONArray body = json.getJSONArray("response");
                for (int i = 0; i < body.size(); i++) {
                    Work work = JSON.parseObject(body.getJSONObject(i).toJSONString(), Work.class);
                    WorkFilter filter = param.getFilter();
                    if (filter == null || filter.doFilter(work)) {
                        WorkCallback callback = param.getCallback();
                        if (callback != null) {
                            callback.onFind(work);
                        }
                        works.add(work);
                        int limit = param.getLimit();
                        if (limit != PixivParserConfig.NO_LIMIT && works.size() >= limit) {
                            return works;
                        }
                    }
                }
                int nextPage = getNextPage(json);
                if (nextPage != PixivParserConfig.NO_NEXT_PAGE) {
                    page = nextPage;
                } else {
                    return works;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 获得指定作者的作品
     * get illusts by author
     * @param authorId
     * @return
     */
    public List<Work> byAuthor(int authorId) {
        return byAuthor(authorId, new ParserParam());
    }

    /**
     * 获得指定作者的作品（使用自定义过滤器筛选并指定作品数）
     * get illusts by author with custom filter and limit
     * @param authorId
     * @return
     */
    public List<Work> byAuthor(int authorId, ParserParam param) {
        HttpGet get;
        JSONObject json;
        int page = PixivParserConfig.START_PAGE;
        List<Work> works = new ArrayList<>();
        while (true) {
            String url = buildByAuthorUrl(authorId, page);
            get = defaultHttpGet(url);
            try (CloseableHttpResponse response = client.execute(get)) {
                json = getResponseContent(response);
                JSONArray body = json.getJSONArray("response");
                for (int i = 0; i < body.size(); i++) {
                    Work work = JSON.parseObject(body.getJSONObject(i).toJSONString(), Work.class);
                    WorkFilter filter = param.getFilter();
                    if (filter == null || filter.doFilter(work)) {
                        WorkCallback callback = param.getCallback();
                        if (callback != null) {
                            callback.onFind(work);
                        }
                        works.add(work);
                        int limit = param.getLimit();
                        if (limit != PixivParserConfig.NO_LIMIT && works.size() >= limit) {
                            return works;
                        }
                    }
                }
                int nextPage = getNextPage(json);
                if (nextPage != PixivParserConfig.NO_NEXT_PAGE) {
                    page = nextPage;
                } else {
                    return works;
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
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
    public static String buildByAuthorUrl(int authorId, int page) {
        Map<String, String> params = getCommonParams(page);
        params.put("mode", "exact_tag");
        params.put("per_page", "30");
        return buildGetUrl(PixivParserConfig.AUTHOR_DETAIL_URL.replace("{authorId}", String.valueOf(authorId)), params);
    }

    /**
     * 请求排行榜的url
     * the url in ranking api
     * @param date
     * @param page
     * @return
     */
    public static String buildRankUrl(Date date, int page) {
        Map<String, String> params = getCommonParams(page);
        params.put("mode", "daily");
        params.put("per_page", "50");
        if (date != null) {
            params.put("date", FORMAT.format(date));
        }
        return buildGetUrl(PixivParserConfig.RANK_URL, params);
    }

    /**
     * 请求搜索的url
     * the url in search api
     * @param keyWord
     * @param page
     * @return
     */
    public static String buildSearchUrl(String keyWord, int page) {
        Map<String, String> params = getCommonParams(page);
        params.put("q", keyWord);
        if (keyWord.split(" ").length == 1) {
            params.put("mode", "exact_tag");
        } else {
            params.put("mode", "text");
        }
        params.put("per_page", "30");
        return buildGetUrl(PixivParserConfig.SEARCH_URL, params);
    }

    /**
     * 公用参数
     * the common params
     * @param page
     * @return
     */
    private static Map<String, String> getCommonParams(int page) {
        Map<String, String> params = new HashMap<>();
        params.put("image_size", "profile_image_sizes");
        params.put("profile_image_sizes", "px_170x170");
        params.put("include_sanity_level", "true");
        params.put("include_stats", "true");
        params.put("period", "all");
        params.put("order", "desc");
        params.put("sort", "date");
        params.put("page", String.valueOf(page));
        return params;
    }

    /**
     * 作品详情url
     * thr url in getIllust api
     * @param workId
     * @return
     */
    private static String buildDetailUrl(int workId) {
        Map<String, String> params = new HashMap<>();
        params.put("image_sizes", "small,medium,large");
        params.put("include_stats", "true");
        return buildGetUrl(PixivParserConfig.ILLUST_DETAIL_URL.replace("{illustId}", String.valueOf(workId)), params);
    }

    /**
     * 将参数和url拼接起来
     * joint url and params
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
        if (params.isEmpty()) {
            return url;
        }
        StringBuilder buffer = new StringBuilder(url);
        if (!url.endsWith("?")) {
            buffer.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /**
     * 获得下一页
     * get next page number
     * @param json
     * @return
     */
    public static int getNextPage(JSONObject json) {
        Object nextPage = ((JSONObject) json.get("pagination")).get("next");
        if (nextPage != null) {
            return Integer.parseInt(nextPage.toString());
        }
        return PixivParserConfig.NO_NEXT_PAGE;
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
