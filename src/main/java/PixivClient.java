import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
     * 字符编码
     */
    private static final Charset encoding = Charset.forName("UTF-8");

    /**
     * 登陆请求地址
     */
    private static final String login_url = "https://www.secure.pixiv.net/login.php";

    /**
     * 搜索请求地址
     */
    private static final String search_url = "http://www.pixiv.net/search.php";

    /**
     * 排行榜请求地址
     */
    private static final String rank_url = "http://www.pixiv.net/ranking.php";

    /**
     * 图片详情请求地址
     */
    private static final String detail_url = "http://www.pixiv.net/member_illust.php";

    /**
     * 默认的图片存放位置
     */
    private static final String default_path = "E:/pixiv/";

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
     * 日期格式化
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

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
        return create(default_path);
    }

    /**
     * 创建一个指定存放位置的下载器
     * @param path
     * @return
     */
    public static PixivClient create(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            logger.error("选择的路径并不是一个文件夹！");
            return null;
        }
        if (pool == null) {
            pool = Executors.newCachedThreadPool();
        }
        return new PixivClient(path);
    }

    private PixivClient(String path) {
        this.path = path;
        parser = new PageParser();
    }

    /**
     * 一个登陆表单的构成
     * @return
     */
    private UrlEncodedFormEntity buildLoginForm() {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("mode", "login"));
        formparams.add(new BasicNameValuePair("pixiv_id", username));
        formparams.add(new BasicNameValuePair("pass", password));
        formparams.add(new BasicNameValuePair("return_to", "/"));
        formparams.add(new BasicNameValuePair("skip", "1"));
        return new UrlEncodedFormEntity(formparams, encoding);
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
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(login_url);
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
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索并下载
     * @param word  关键词
     * @param isR18 是否只需要r18
     * @param praise    收藏数过滤条件
     */
    public void searchAndDownload(String word, boolean isR18, int praise) {
        logger.info("开始下载收藏数大于" + praise + "的\"" + word + "\"图片");
        searchAndDownload(bulidSearchUrl(word, isR18), praise);
    }

    private String getPage(String url) {
        url = decodeUrl(url);

        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet get = new HttpGet(url);
            response = client.execute(get, context);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), encoding));
            StringBuilder pageHTML = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                pageHTML.append(line);
                pageHTML.append("\r\n");
            }
            return pageHTML.toString();
        } catch (IOException e) {
            logger.error("获取网页失败：" + url);
            return null;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
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
            String pageHtml = getPage(url);
            List<String> items = parser.parseList(pageHtml, praise);
            if (items != null) {
                for (String imgUrl : items) {
                    downloadImage(imgUrl);
                }
            }
            String next = parser.parseNextPage(pageHtml);
            if (next != null) {
                searchAndDownload(search_url + next, praise);
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
     * @param url
     */
    private void downloadImage(String url) {
        url = decodeUrl(url);
        String pageHtml = getPage(url);
        if (parser.isManga(pageHtml)) {
            url = url.replace("medium", "manga");
            pageHtml = getPage(url);
            List<String> images = parser.parseManga(pageHtml);
            if (images != null) {
                int i = 0;
                for (String image : images) {
                    String id = url.substring(url.lastIndexOf("id=") + 3);
                    if (id.indexOf("&") > -1) {
                        id = id.substring(0, id.indexOf("&"));
                    }
                    id = id + "_" + i++;
                    pool.execute(new ImageDownloadTask(id, image, url));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        } else {
            String image = parser.parseMedium(pageHtml);
            if (image != null) {
                String id = url.substring(url.lastIndexOf("id=") + 3);
                if (id.indexOf("&") > -1) {
                    id = id.substring(0, id.indexOf("&"));
                }
                pool.execute(new ImageDownloadTask(id, image, url));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 下载20070913-当天的排行榜图片，不会重复下载
     */
    public void downloadAllRank() {
        try {
            downloadRankAfter(sdf.parse("20070913"));
        } catch (ParseException e) {
            logger.error("这代码写的有问题：" + e.getMessage());
        }
    }

    /**
     * 合成一个作品详情页的链接
     * @param id 作品id
     * @return
     */
    private String buildDetailUrl(String id) {
        return detail_url + "?mode=medium&illust_id=" + id;
    }

    /**
     * 合成一个排行榜的链接
     * @param today 哪一天
     * @param page 第几页
     * @return
     */
    private String buildRankUrl(String today, int page) {
        return rank_url + "?format=json&date=" + today + "&p=" + page;
    }

    /**
     * 合成一个搜索的链接
     * @param word
     * @param isR18
     * @return
     */
    private String bulidSearchUrl(String word, boolean isR18) {
        return search_url + "?word=" + word + "&r18=" + (isR18 ? "1" : "0");
    }

    /**
     * 下载某天的排行榜图片
     * @param aday
     */
    public void downloadRankOn(Date aday) {
        String date = sdf.format(aday);
        int page = 0;
        logger.info("开始下载[" + date + "]的排行榜");
        while (true) {
            String pageJson = getPage(buildRankUrl(date, page));
            JSONObject json = (JSONObject)JSONValue.parse(pageJson);
            List<String> ids = parser.praseRank(json, false);
            for (String id : ids) {
                downloadImage(buildDetailUrl(id));
            }
            if (json.get("next") != null) {
                page = Integer.parseInt(String.valueOf(json.get("next")));
                continue;
            }
            return;
        }
    }

    /**
     * 下载从指定日期到当前的排行榜，不会重复下载
     * @param aday
     */
    public void downloadRankAfter(Date aday) {
        String date = sdf.format(new Date());
        int page = 0;
        String endDate = sdf.format(aday);
        while (true) {
            if (page == 0) {
                logger.info("开始下载[" + date + "]的排行榜");
            }
            String pageJson = getPage(buildRankUrl(date, page));
            JSONObject json = (JSONObject)JSONValue.parse(pageJson);
            List<String> ids = parser.praseRank(json, !date.equals(endDate));
            for (String id : ids) {
                downloadImage(buildDetailUrl(id));
            }
            if (json.get("next") != null) {
                page = Integer.parseInt(String.valueOf(json.get("next")));
                continue;
            }
            if (json.get("prev_date") != null) {
                if (date.equals(endDate)) {
                    return;
                }
                page = 0;
                date = String.valueOf(json.get("prev_date"));
                continue;
            }
            return;
        }
    }

    /**
     * 负责下载图片的线程
     */
    private class ImageDownloadTask implements Runnable {

        /**
         * 图片id
         */
        private String id;
        /**
         * 图片的地址
         */
        private String url;
        /**
         * 请求地址（验证用）
         */
        private String referer;

        public ImageDownloadTask(String id, String url, String referer) {
            this.id = id;
            this.url = url;
            this.referer = referer;
        }

        @Override
        public void run() {
            logger.info("开始下载图片[" + id + "]...");
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            OutputStream out = null;

            try {
                HttpGet get = new HttpGet(url);
                get.setHeader("Referer", referer);
                response = client.execute(get, context);
                String ext = url.substring(url.lastIndexOf("."));
                if (ext.indexOf("?") > -1) {
                    ext = ext.substring(0, ext.indexOf("?"));
                }
                out = new FileOutputStream(path + id + ext);
                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                while((bytesRead = response.getEntity().getContent().read(buffer)) != -1){
                    out.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (response != null) {
                        response.close();
                    }
                    client.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
