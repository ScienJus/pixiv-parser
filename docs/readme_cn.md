# Pixiv Parser

##介绍

批量抓取和下载 Pixiv 上的图片。

通过请求 Pixiv iOS Api 完成图片的爬取和下载。

与 [Pixiv Crawler][1] 不同的是：这个项目更接近 Api，尽量支持二次开发而不仅仅是下载图片。

所以如果你只需要简单的将图片下载到本地磁盘，而不需要拓展自定义功能，可以使用[Pixiv Crawler][1]。

##快速开始

###如何运行

下载项目，在`com/scienjus/main/Launch.java`中编写你的任务。

或是添加Maven依赖：

仓库：

```
<repository>
    <id>scienjus-mvn-repo</id>
    <url>https://raw.github.com/ScienJus/maven/mvn-repo/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

依赖：

```
<dependency>
    <groupId>com.scienjus</groupId>
    <artifactId>pixiv-parser</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

###创建和关闭

使用`PixivParserClient`的构造方法创建一个新的客户端实例。

通过`setUsername`和`setPassword`方法设置你的用户名和密码，然后调用`login`方法进行登录，如果登录成功会返回`true`（所有下载任务都需要在登录后进行）。

当所有操作全部完成后需要通过`PixivParserClient`的`close`方法关闭客户端。

示例：

```
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
 - `String keyWord`：指定搜索的关键词（iOS Api 没有关键词联想功能，所以请尽量输入日文关键词）
 - `ParserParam param`：Api 的增强功能，包含 Limit（个数）、Filter（过滤器）和 Callback（回调）

该方法的返回值为`List<Work>`对象，其中每个`Work`对象对应一件作品。

 
示例：

```
//查询kancolle(舰队Collection)的所有作品
List<Work> works = client.search("kancolle");
```

###排行榜

使用`PixivParserClient`中`ranking`方法可以获取排行榜作品，通过`Date date`参数可以指定日期，也可以调用无参方法获得当天的排行榜。

示例：

```
//查询当天排行榜
Rank rank = client.ranking();
//查询2015年4月29日的排行榜
Rank rank = client.ranking(getDate("2015-04-29"));
```
该方法的返回值为`Rank`对象，对象的works属性为排行榜的作品。

###作者索引

使用`PixivParserClient`的`byAuthor`方法可以查找某个作者的作品，，可选参数有：
 - `String authorId`：作者的id（需要自行去作者主页查找，方法为进入作者个人资料页面后，如果链接为`http://www.pixiv.net/member.php?id=一串数字`的格式，一串数字就是作者的id）
 - `ParserParam param`：Api 的增强功能，包含 Limit（个数）、Filter（过滤器）和 Callback（回调）

示例：

```
//查询作者id为111111的全部作品
List<Work> works = client.byAuthor(111111);
```

###自定义参数

使用`ParserParam`对象可以增强`search`和`byAuthor`方法，该对象有三个属性：

- `int limit`：指定获取的作品数量
- `WorkFilter filter`：筛选出特定的作品
- `WorkCallback callback`：当查找到符合条件的作品后触发的回调

示例：

```
//查找关键词为"咲-Saki-"，公开收藏数大于500的5件作品，找到后立刻打印它们的标题
ParserParam param = new ParserParam()
        .withLimit(5)
        .withFilter(new WorkFilter() {
            @Override
            public boolean doFilter(Work work) {
                return work.getStats().getFavoritedCount().getPublicCount() > 500;
            }
        })
        .withCallback(new WorkCallback() {
            @Override
            public void onFound(Work work) {
                System.out.println(work.getTitle());
            }
        });
client.search("咲-Saki-", param);
```

###获取作品详情

在查找出合适的作品后，通过`PixivParserClient`的`getWork`方法可以通过作品的id获取到作品的详细信息，主要是为了获取作品的大图地址。

示例：

```
//获得作品id为111111的作品
Work work = client.getIllust(111111);
```

###下载图片

使用`PixivParserClient`的`download`方法可以下载某个作品，它的参数有：

- `Work work`：需要下载的作品（必须是调用`getWork`方法获取的对象，否则无法获得图片信息）
- `DownloadCallback callback`：下载后的回调对象

该方法会启动一个新线程去下载图片，所以推荐在`search`或`byAuthor`时使用`ParserParam`的`callback`进行下载，可以节约大量时间。

`DownloadCallback`有两个方法：`onIllustFinished`和`onMangaFinished`，如果下载的作品是插画（单张图片）会调用前者，如果是漫画（多张图片）会调用后者。


##帮助

如果您在使用中遇到了问题或是有新的想法，请给我提Issues。或是通过以下方式联系我：
 - 博客：[我的博客][2]
 - 邮箱：`i@scienjus.com`

[1]:https://github.com/ScienJus/pixiv-crawler/
[2]:http://www.scienjus.com
