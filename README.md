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

# PixivParser

Batch download pictures from Pixiv

###PixivClient
parse www.pixiv.net to download pictures, it's simple to use.

示例：
```
//create a new instance and set the path where pictures stored
PixivClient client = PixivClient.create("path");
//set username(your pixiv id) and password
client.setUsername("username");
client.setPassword("password");
//login to pixiv
if (client.login()) {
	//search by key word and set a base star(collectrion) number
	//param: key word, isR18(if you just need r18 set true), base star(collectrion) number
	client.searchByKeyword("key", false, num);
	
	//download the Pixiv ranking on a day
	client.downloadRankOn("20140505", RankingMode.all, false);
	
	//search all pictures by author
	client.searchByAuthor("id");
}
//please close the client after used
client.close();
```

###PixivApiClient
used the Pixiv iOS api, it will be more faster and consume less traffic, it's easier to use than PixivClient when you extended development it,

示例
```
//create a new instance, the PixivApiClient can't download the picture, so you don't need to set a path
PixivApiClient client = PixivApiClient.create();
//set username(your pixiv id) and password
client.setUsername("username");
client.setPassword("password");
//login to pixiv
if (client.login()) {
	//search by key word
	List<IllustListItem> items = client.search("keyWord");
	
	//get ranking on a day
	List<IllustListItem> items = client.ranking("20140505");
	
	//search all illust by author
	List<IllustListItem> items = client.byAuthor("id");
	
	//you can add a custom filter to discard the illust you don't need
	//search kancolle and not r18
	List<IllustListItem> items = client.search("kancolle", new IllustFilter() {
		
		@Override
	    public boolean doFilter(IllustListItem item) {
        	return item.getAgeLimit() != AgeLimit.r18;
	    }
	
	})
	
	//also you can give a limit
	//search kancolle and not r18, only five
	List<IllustListItem> items = client.search("kancolle", new IllustFilter() {
		
		@Override
	    public boolean doFilter(IllustListItem item) {
        	return item.getAgeLimit() != AgeLimit.r18;
	    }
	
	}， 5)
	
	//the api's default sort is order by create time, so you will get the same result if you don't change the param, you can set a random in the filter if you want random result.
	
	//get the illust's details
	for (IllustListItem item : items) {
		Illust illust = client.getIllust(item.getId());
		//illust's id and title
		System.out.println("Illust: " + illust.getTitle() + "[" + illust.getId() + "]");
		//author's id and nickname
		System.out.println("Author: " + illust.getAuthorName() + "[" + illust.getAuthorId() + "]");
		//the pictures in this illust
		List<IllustImage> images = illust.getImages();
	}
	
}
//please close the client after used
client.close();
```