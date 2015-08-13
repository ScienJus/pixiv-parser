# Pixiv Parser

##介绍

批量抓取和下载Pixiv上的图片。

通过请求Pixiv iOS Api完成图片的爬取和下载。

与[Pixiv Crawler][1]不同的是，我将这个项目组件化，以支持二次开发而不仅仅是使用。

所以如果你只需要简单的将图片下载到本地磁盘，而不需要拓展自定义功能，可以使用[Pixiv Crawler][1]。

##操作说明

###如何运行

在`com/scienjus/main/Launch.java`中编写你的任务。

###创建和关闭

使用`PixivParserClient`的构造方法创建一个新的客户端实例。

通过`setUsername`和`setPassword`方法设置你的用户名和密码，然后调用`login`方法进行登录，如果登录成功会返回`true`（所有下载任务都需要在登陆后进行）。

当图片抓取全部完成后需要通过`PixivParserClient`的`close`方法关闭客户端。

示例：
```java
//创建一个客户端实例
PixivParserClient client = new PixivParserClient();
//设置用户名和对应的密码
client.setUsername("username");
client.setPassword("password");
//登陆
if (client.login()) {
    //进行获取图片任务..
}
//关闭客户端
client.close();
```

###关键词搜索

使用`PixivParserClient`中的`search`方法进行关键词搜索，可选参数有：
 - `String keyWord`：指定搜索的关键词（请尽量使用准确的日文关键词，否则只能依靠Pixiv的联想匹配）
 - `IllustFilter filter`：过滤器，在之后会介绍
 - `int limit`：限制获取作品的数量
 
示例：
```java
//查询kancolle(舰队Collection)的所有作品
List<IllustListItem> items = client.search("kancolle");
//查询ラブライブ!（love live!）的5个作品
List<IllustListItem> items = client.search("ラブライブ!", 5);
```

###排行榜

使用`PixivParserClient`中`ranking`方法可以获取排行榜作品，可选参数有：
 - `Date date`：获取该日期的排行榜（不选默认为当天）
 - `IllustFilter filter`：过滤器
 - `int limit`：限制获取作品的数量
 
示例：
```java
//查询当天排行榜
List<IllustListItem> items = client.ranking();
//查询当天排行榜前五名
List<IllustListItem> items = client.ranking(5);
//查询2015年4月29日的排行榜
List<IllustListItem> items = client.ranking(getDate("20150429"));
```

###作者索引
使用`PixivParserClient`的`byAuthor`方法可以下载某位作者的全部作品，，可选参数有：
 - `String authorId`：作者的id（需要自行去作者主页查找，方法为进入作者个人资料页面后，如果链接为`http://www.pixiv.net/member.php?id=一串数字`的格式，一串数字就是作者的id）
 - `IllustFilter filter`：过滤器，在之后会介绍
 - `int limit`：限制获取作品的数量

示例：
```java
//查询作者id为111111的全部作品
List<IllustListItem> items = client.byAuthor("111111");
//查询作者id为111111的前5个作品
List<IllustListItem> items = client.byAuthor("111111", 5);
```

###自定义过滤器
使用`IllustFilter`的实现类并重写`doFilter`方法可以在获取作品时进行条件筛选，该方法的默认参数为`IllustListItem`对象，里面封装了作品的一些属性用于筛选。需要注意的是`search`和`byAuthor`默认按照时间倒序排序，`ranking`默认按照排名排序，所以在短时间内多次调用相同参数的方法返回内容一般也是相同的，如果需要每次获得不同的结果就需要在自定义过滤器中增加随机因子。

示例：
```java
//查询作者id为111111的r18作品
List<IllustListItem> items = client.byAuthor("111111",  new IllustFilter() {
		
	@Override
    public boolean doFilter(IllustListItem item) {
    	return item.getAgeLimit() == AgeLimit.r18;
    }

});
//查询作者id为111111的随机1个r18作品
List<IllustListItem> items = client.byAuthor("111111",  new IllustFilter() {
		
    //这个随机不会分布均匀，需要自己实现
	@Override
    public boolean doFilter(IllustListItem item) {
        return item.getAgeLimit() == AgeLimit.r18 && random.nextDouble() < 0.05;
    }

}， 1);
```

###获取作品详情
在筛选出合适的作品后，通过`PixivParserClient`的`getIllust`方法可以通过作品的id获取到作品的详细信息，其中包括作品的标题、作者名称、图片地址等。

示例：
```
//下载作者id为111111的随机1个r18作品
List<IllustListItem> items = client.byAuthor("111111",  new IllustFilter() {
		
    //这个随机不会分布均匀，需要自己实现
	@Override
    public boolean doFilter(IllustListItem item) {
        return item.getAgeLimit() == AgeLimit.r18 && random.nextDouble() < 0.05;
    }

}， 1);

Illust illust = client.getIllust(items.get(0).getId());
```

###下载图片
默认提供了`IllustImageDownloadTask`用于下载作品的每张图片，同时它在下载完成后会调用传入`DownloadCallback`对象的`onFinished`方法，你可以在这个方法中拿到图片信息和文件对象，然后将它持久化在硬盘中或渲染到页面中或上传到远程服务器，随你喜欢。

示例：
```java
new Thread(new IllustImageDownloadTask(image, new DownloadCallback() {
    @Override
    public void onFinished(IllustImage illust, byte[] file) {
        //do something
    }
})).start();
```

##帮助

如果您在使用中遇到了问题或是有新的想法，请给我提Issues。或是通过以下方式联系我：
 - 博客：[我的博客][2]
 - 邮箱：xie_enlong@foxmail.com

[1]:https://github.com/ScienJus/pixiv-crawler/
[2]:http://www.scienjus.com