
/**
 * Created by Scienjus on 2015/3/16.
 */
public class Go {

    public static void main(String[] args) {
        PixivClient client = PixivClient.createDefault();
        client.setUsername("这里填帐户名");
        client.setPassword("这里填密码");
        client.login();
        //根据关键字搜索并过滤收藏数 param: 关键字 是否r18 最小收藏数
        client.searchAndDownload("境界の彼方", false, 0); 
    }

}
