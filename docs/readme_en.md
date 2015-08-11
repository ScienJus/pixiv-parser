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
		//you can download images like
		for (IllustImage image : images) {
			new Thread(new IllustImageDownloadTask(image, new DownloadCallback() {
            @Override
            public void onFinished(IllustImage illust, byte[] file) {
                //do something..
            }
        }))
		}
	}
	
}
//please close the client after used
client.close();
```
