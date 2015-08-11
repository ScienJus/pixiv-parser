# PixivParser

批量抓取和下载Pixiv上的图片

###PixivClient
通过解析www.pixiv.net完成图片的爬取和下载，适合直接下载使用。

示例：
```
//创建实例并选择图片保存位置
PixivClient client = PixivClient.create("path");
//设置用户名和密码
client.setUsername("username");
client.setPassword("password");
//登陆
if (client.login()) {
	//根据关键字搜索并过滤收藏数
	//param: 关键字 是否r18 最小收藏数
	client.searchByKeyword("key", false, num);
	
	//搜索某一天的排行榜
	client.downloadRankOn("20140505", RankingMode.all, false);
	
	//搜索某个作者的所有作品
	client.searchByAuthor("id");
}
//关闭资源
client.close();
```

###PixivApiClient
使用Pixiv iOS客户端协议抓取图片，将整个Api拆分成获取图片和处理图片两部分，便于二次开发。

示例
```
//创建实例，由于PixivApiClient不负责下载图片，所以不需要指定路径
PixivApiClient client = PixivApiClient.create();
//设置用户名和密码
client.setUsername("username");
client.setPassword("password");
//登陆
if (client.login()) {
	//根据关键字搜索
	client.searchByKeyword("key", false, num);
	
	//搜索某一天的排行榜
	client.downloadRankOn("20140505", RankingMode.all, false);
	
	//搜索某个作者的所有作品
	client.searchByAuthor("id");
}
//关闭资源
client.close();
```