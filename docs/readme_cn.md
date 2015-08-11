# PixivParser

批量抓取和下载Pixiv上的图片

###PixivClient
通过解析www.pixiv.net完成图片的爬取和下载，适合直接下载使用。

如果你想了解如何解析并模拟请求，可以参考我博客的文章：[Pixiv爬图教程][1]

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
	List<IllustListItem> items = client.search("keyWord");
	
	//搜索某一天的排行榜
	List<IllustListItem> items = client.ranking("20140505");
	
	//搜索某个作者的所有作品
	List<IllustListItem> items = client.byAuthor("id");
	
	//以上三个方法均可以指定自定义的过滤器筛选内容，例如：
	//搜索kancolle非r18内容
	List<IllustListItem> items = client.search("kancolle", new IllustFilter() {
		
		@Override
	    public boolean doFilter(IllustListItem item) {
        	return item.getAgeLimit() != AgeLimit.r18;
	    }
	
	})
	
	//也可以指定返回图片数量
	//搜索kancolle非r18内容，只返回5张
	List<IllustListItem> items = client.search("kancolle", new IllustFilter() {
		
		@Override
	    public boolean doFilter(IllustListItem item) {
        	return item.getAgeLimit() != AgeLimit.r18;
	    }
	
	}， 5)
	
	//由于api默认为时间排序，多次调用同一方法返回结果是相同的，如果需要每次返回不同的结果请在过滤器中加入随机因子
	
	//如果你想获得作品的详细内容
	for (IllustListItem item : items) {
		Illust illust = client.getIllust(item.getId());
		//作品id和作品名
		System.out.println("Illust: " + illust.getTitle() + "[" + illust.getId() + "]");
		//作者和作者id
		System.out.println("Author: " + illust.getAuthorName() + "[" + illust.getAuthorId() + "]");
		//图片
		List<IllustImage> images = illust.getImages();
	}
	
}
//关闭资源
client.close();
```

[1]:http://www.scienjus.com/pixiv-parser/