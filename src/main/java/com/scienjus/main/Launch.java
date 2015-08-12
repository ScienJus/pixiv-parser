package com.scienjus.main;

import com.scienjus.client.PixivParserClient;

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
            System.out.println(client.search("utaha"));
        }
        //关闭资源
        client.close();
    }

}
