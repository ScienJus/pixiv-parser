# Pixiv Parser

##Introduction

Batch download pictures from Pixiv.

It use the Pixiv iOS Api

The difference from [Pixiv Crawler][1] is i make it easier to expand.

So if you just want to download pictures to disk, you can use [Pixiv Crawler][1].

##API

###How to use

Import to your project.

Or write your work codes in `com/scienjus/main/Launch.java`.

###Create And Shutdown

Use the construction method of `PixivParserClient` to create a new instance

Set you Pixiv id and password by `setUsername` and `setPassword` method, then `login`, it will return `true` if login success.

After parser please shutdown the client by `close` method.

Example：
```java
//create a instance
PixivParserClient client = new PixivParserClient();
//set username and password
client.setUsername("username");
client.setPassword("password");
//login
if (client.login()) {
    //write your work codes
}
//close the client
client.close();
```

###Search by KeyWord

Use the `search` method from `PixivParserClient` to search illusts by key word, the params are：
 - `String keyWord`：the key word(the pixiv api can not be very good support in English, sometimes you need to change the key word from English to Japanese)
 - `IllustFilter filter`：the filter, will be introduced later;
 - `int limit`：the count you need
 
Example：
```java
//search all illusts of kancolle(舰队Collection)
List<IllustListItem> items = client.search("kancolle");
//search five illusts of ラブライブ!(love live!)
List<IllustListItem> items = client.search("ラブライブ!", 5);
```

###Rank

Use the `ranking` method from `PixivParserClient` to get illusts in rank, the params are：
 - `Date date`：get rank on this date(default today)
 - `IllustFilter filter`：the filter
 - `int limit`：the count you need
 
Example：
```java
//get all rank illusts on today
List<IllustListItem> items = client.ranking();
//get top five rank illusts on today
List<IllustListItem> items = client.ranking(5);
//get all rank illusts on 2015-04-29
List<IllustListItem> items = client.ranking(getDate("20150429"));
```

###Search By Author
Use the `byAuthor` method from `PixivParserClient` to get someone's illusts, the params are：
 - `String authorId`：the id of author(you need to find it in www.pixiv.net, if his(her) main page is `http://www.pixiv.net/member.php?id=numbers`, the numbers is his(her) id)
 - `IllustFilter filter`：the filter
 - `int limit`：the count you need

Example：
```java
//get illusts which author's id is 111111
List<IllustListItem> items = client.byAuthor("111111");
//get five illusts which author's id is 111111
List<IllustListItem> items = client.byAuthor("111111", 5);
```

###Custom Filter
create a class implements `IllustFilter` and overwrite `doFilter` method, you can get some attributes from the param `IllustListItem item`, return `true` if you like it. you also can add a `
java.util.Random` to this method make it  random

Example：
```java
//get illusts which is r18 and author's id is 111111
List<IllustListItem> items = client.byAuthor("111111",  new IllustFilter() {
		
	@Override
    public boolean doFilter(IllustListItem item) {
    	return item.getAgeLimit() == AgeLimit.r18;
    }

});
//get a random illust which is r18 and author's id is 111111
List<IllustListItem> items = client.byAuthor("111111",  new IllustFilter() {
		
    //non-uniform
	@Override
    public boolean doFilter(IllustListItem item) {
        return item.getAgeLimit() == AgeLimit.r18 && random.nextDouble() < 0.05;
    }

}, 1);
```

###Illust Details
After you get the `IllustListItem`, you can use the `getIllust` from `PixivParserClient` to get Illust Details(like title, author's nickname and image urls)

Example:
```
//get a random illust which is r18 and author's id is 111111
List<IllustListItem> items = client.byAuthor("111111", new IllustFilter() {

	@Override
    public boolean doFilter(IllustListItem item) {
        return item.getAgeLimit() == AgeLimit.r18 && random.nextDouble() < 0.05;
    }

}, 1);

//get illust details
Illust illust = client.getIllust(items.get(0).getId());
```

###Download Pictures
the `IllustImageDownloadTask` can download image by `IllustImage image`, there is a callback after download success named `DownloadCallback callback`, you can get the image and file in `onFinishied` method and do whatever you want.

Example:
```java
new Thread(new IllustImageDownloadTask(image, new DownloadCallback() {
    @Override
    public void onFinished(IllustImage illust, byte[] file) {
        //do something
    }
})).start();
```

##Support

If you have any questions or advice, please create a new issue, or call me by：
 - Blog：[My Blog][2]
 - email：xie_enlong@foxmail.com

My English is terrible, so if you find any grammar mistakes, please tell me! thanks!

[1]:https://github.com/ScienJus/pixiv-crawler/
[2]:http://www.scienjus.com