package com.scienjus.main;

import com.scienjus.client.PixivParserClient;
import com.scienjus.model.Work;

import java.text.ParseException;

/**
 * Created by Scienjus on 2015/3/16.
 */
public class Launch {

    public static void main(String[] args) throws ParseException {
        //创建实例并选择图片保存位置
        PixivParserClient client = new PixivParserClient();
        //设置用户名和密码
        client.setUsername("1498129534@qq.com");
        client.setPassword("a123456");
        if (client.login()) {
//            List<IllustListItem> items = client.search("咲-Saki-", new IllustFilter() {
//                @Override
//                public boolean doFilter(IllustListItem item) {
//                    return item.getFavoriteCount() > 2000 && !item.isManga() && item.getAgeLimit() == AgeLimit.all;
//                }
//            });
//            for (IllustListItem item : items) {
//                new Thread(new IllustImageDownloadTask(client.getIllust(item.getId()).getImages().get(0), new DownloadCallback() {
//                    @Override
//                    public void onFinished(IllustImage illust, byte[] file) {
//                        try (FileOutputStream out = new FileOutputStream("E:/saki/" + illust.getIllustId() + "." + illust.getExtension())) {
//                            out.write(file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                })).start();
//            }
//            client.ranking();
//            client.search("咲-Saki-");
            Work work = client.getIllust("53587891");
            System.out.println(work);
        }
        //关闭资源
        client.close();
    }

}
