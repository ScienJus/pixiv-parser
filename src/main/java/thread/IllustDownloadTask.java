package thread;

import bean.Illust;
import bean.IllustImage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/8/11.
 */
public class IllustDownloadTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(IllustDownloadTask.class);

    private String path;

    private Illust illust;

    public IllustDownloadTask(String path, Illust illust) {
        this.path = path.endsWith(File.separator) ? path : path + File.separator;
        this.illust = illust;
    }

    public static final HttpGet defaultHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Referer", "http://www.pixiv.net");
        return get;
    }

    @Override
    public void run() {
        try (CloseableHttpClient client = HttpClients.createDefault();) {
            List<IllustImage> images = illust.getImages();
            if (images.size() == 1) {
                File dir = new File(this.path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                IllustImage image = images.get(0);
                String fileName = illust.getId() + "." + image.getExtension();
                downloadImage(image.getLargeUrl(), this.path + fileName, client);
            } else {
                String dirPath = this.path + illust.getId() + File.separator;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (IllustImage image : images) {
                    String fileName = image.getSerial() + "." + image.getExtension();
                    downloadImage(image.getLargeUrl(), dirPath + fileName, client);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void downloadImage(String url, String filePath, CloseableHttpClient client) {
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        HttpGet get = defaultHttpGet(url);
        try (OutputStream out = new FileOutputStream(file);
             CloseableHttpResponse response = client.execute(get);
             InputStream in = response.getEntity().getContent();){
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while((len = in.read(buffer)) != -1){
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
