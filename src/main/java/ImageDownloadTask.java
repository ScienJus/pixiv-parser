import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 负责下载图片的线程
 */
public class ImageDownloadTask implements Runnable {
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(ImageDownloadTask.class);

    /**
     * 最大重试次数
     */
    private static final int max_failure_time = 5;

    /**
     * 失败等待时间
     */
    private static final int sleep_time = 200;

    /**
     * 重试次数
     */
    private int failure;

    /**
     * 请求客户端
     * @param client
     * @param id
     * @param url
     * @param referer
     */
    private CloseableHttpClient client;

    /**
     * 图片对象
     */
    private Image image;


    public ImageDownloadTask(CloseableHttpClient client, Image image) {
        this.client = client;
        this.image = image;
        this.failure = 0;
    }

    @Override
    public void run() {
        logger.info("开始下载图片[" + image.getId() + "-" + image.getChildId() + "]...");

        File file = new File(image.getPath());
        if (file.exists()) {
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        CloseableHttpResponse response = null;
        OutputStream out = null;
        HttpGet get = null;
        try {
            get = new HttpGet(image.getUrl());
            get.setHeader("Referer", image.getReferer());
            response = client.execute(get);
            out = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while((bytesRead = response.getEntity().getContent().read(buffer)) != -1){
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            if (failure++ < max_failure_time) {
                try {
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e1) {}
                logger.error("图片[" + image.getId() + "-" + image.getChildId() + "]下载失败...正在重试第" + failure +"次...");
                if (file.exists()) {
                    file.delete();
                }
                run();
            } else {
                logger.error("图片[" + image.getId() + "-" + image.getChildId() + "]无法下载...");
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}