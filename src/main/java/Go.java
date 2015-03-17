
/**
 * Created by Scienjus on 2015/3/16.
 */
public class Go {

    public static void main(String[] args) {
        PixivClient client = PixivClient.createDefault();
        client.setUsername("931996776@qq.com");
        client.setPassword("xel0429");
        client.login();
        client.searchAndDownload("境界の彼方", false, 0); //搜索条件， 是否r18， 最小收藏数
    }

}
