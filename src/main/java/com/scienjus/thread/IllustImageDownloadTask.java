package com.scienjus.thread;

import com.scienjus.bean.IllustImage;
import com.scienjus.callback.DownloadCallback;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by xieenlong on 15/8/11.
 */
public class IllustImageDownloadTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(IllustImageDownloadTask.class);

    private IllustImage image;

    private DownloadCallback callback;

    public IllustImageDownloadTask(IllustImage image, DownloadCallback callback) {
        this.image = image;
        this.callback = callback;
    }

    public static final HttpGet defaultHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader("Referer", "http://www.pixiv.net");
        return get;
    }

    @Override
    public void run() {
        HttpGet get = defaultHttpGet(image.getLargeUrl());
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get);
             InputStream in = response.getEntity().getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            if (callback != null) {
                callback.onFinished(image, out.toByteArray());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
