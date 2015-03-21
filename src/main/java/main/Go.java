package main;

import client.PixivClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Scienjus on 2015/3/16.
 */
public class Go {

    public static void main(String[] args) throws ParseException {
        //创建实例并选择图片保存位置
        PixivClient client = PixivClient.create("E:/誕生日");
        //设置用户名和密码
        client.setUsername("1498129534@qq.com");
        client.setPassword("a123456");
        if (client.login()) {
            //根据关键字搜索并过滤收藏数
            // param: 关键字 是否r18 最小收藏数
            client.searchAndDownload("誕生日", false, 1000);
            //搜索某一天的排行榜
//            client.downloadRankOn(new SimpleDateFormat("yyyyMMdd").parse("20150318"), RankingMode.all, false);
        }
        //关闭资源
        client.close();
    }

}
