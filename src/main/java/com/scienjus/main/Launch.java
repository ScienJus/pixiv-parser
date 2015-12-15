package com.scienjus.main;

import com.scienjus.callback.WorkCallback;
import com.scienjus.client.PixivParserClient;
import com.scienjus.filter.WorkFilter;
import com.scienjus.model.Work;
import com.scienjus.param.ParserParam;

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
            ParserParam param = new ParserParam()
                    .withLimit(1)
                    .withFilter(new WorkFilter() {
                        @Override
                        public boolean doFilter(Work work) {
                            return work.getStats().getFavoritedCount().getPublicCount() > 500;
                        }
                    })
                    .withCallback(new WorkCallback() {
                        @Override
                        public void onFind(Work work) {
                            System.out.println(work.getTitle());
                        }
                    });
            Work work = client.search("咲-Saki-", param).get(0);
            System.out.println(work.getMetadata());

            work = client.getWork(work.getId());
            System.out.println(work.getMetadata());
        }
        //关闭资源
        client.close();
    }

}
