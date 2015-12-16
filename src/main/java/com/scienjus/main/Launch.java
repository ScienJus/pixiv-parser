package com.scienjus.main;

import com.scienjus.callback.DownloadCallback;
import com.scienjus.callback.WorkCallback;
import com.scienjus.client.PixivParserClient;
import com.scienjus.filter.WorkFilter;
import com.scienjus.model.Work;
import com.scienjus.param.ParserParam;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Scienjus on 2015/3/16.
 */
public class Launch {

    //一个简单的示例
    public static void main(String[] args) throws ParseException {

        //创建实例
        final PixivParserClient client = new PixivParserClient();

        //设置用户名和密码
        client.setUsername("your username");
        client.setPassword("your password");

        //登录
        if (client.login()) {

            //构造自定义参数
            ParserParam param = new ParserParam()
                    //只取100张
                    .withLimit(100)
                    //获得公开收藏数大于1000的非r18作品
                    .withFilter(new WorkFilter() {
                        @Override
                        public boolean doFilter(Work work) {
                            return work.getStats().getFavoritedCount().getPublicCount() > 1000 && !work.getAgeLimit().contains("r18");
                        }
                    })
                    //在回调中下载图片
                    .withCallback(new WorkCallback() {
                        @Override
                        public void onFound(Work work) {
                            //获取作品详情
                            Work detail = client.getWork(work.getId());
                            //将图片保存在本地磁盘
                            PixivParserClient.download(detail, new DownloadCallback() {
                                @Override
                                public void onIllustFinished(Work work, byte[] file) {
                                    try (FileOutputStream out = new FileOutputStream("E:/Pixiv/" + work.getId() + ".jpg")) {
                                        out.write(file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onMangaFinished(Work work, List<byte[]> files) {
                                    int i = 1;
                                    for (byte[] file : files) {
                                        try (FileOutputStream out = new FileOutputStream("E:/Pixiv/" + work.getId() + "_" + i++ + ".jpg")) {
                                            out.write(file);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
            //搜索
            client.search("咲-Saki-", param);
        }
        //关闭实例
        client.close();
    }

}
