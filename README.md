# PixivParser

起因是Pixiv有个按照收藏数排名的功能，但是只有付费才能用，于是决定自己写个爬虫玩。

准备实现的功能：
  
1.登陆【废话  
2.指定关键词的搜索及根据收藏数过滤（已经实现）  
3.每日排行榜的爬取（已经实现）  
4.指定关键词的搜索及根据收藏数排序（实现比较简单，但是返回格式比较麻烦，故放弃）  
5.指定作者的所有作品爬取（已经实现）    
  
  
本来是创建了一个Web工程的，但是Web工程的资源共享和流量都是大问题（因为Pixiv的图片需要验证Referer，无法直接外链）。    
桌面项目的话我又懒得写界面，再说Java的桌面项目美观度实在是…【好吧是我无能，被Swing虐的太深。    
总之只是一个自己用来爬小黄兔用的命令行小程序罢了_(:з」∠)_ 【P站的各位画师请不要打我        
     
例子：      
```
    
//创建实例并选择图片保存位置
PixivClient client = PixivClient.create("path");
//设置用户名和密码
client.setUsername("username");
client.setPassword("password");
if (client.login()) {
	//根据关键字搜索并过滤收藏数
	// param: 关键字 是否r18 最小收藏数
	client.searchByKeyword("key", false, num);
	//搜索某一天的排行榜
	client.downloadRankOn(new SimpleDateFormat("yyyyMMdd").parse("aday"), RankingMode.all, false);
	//搜索某个作者的所有作品
	client.searchByAuthor("id");
}
//关闭资源
client.close();
```