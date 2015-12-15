package com.scienjus.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/12/15.
 */
public class Work {
    private int id;

    private String title;

    private String caption;

    private List<String> tags;

    private List<String> tools;

    @JSONField(name = "image_urls")
    private ImageUrls imageUrls;

    private int width;

    private int height;

    private Stats stats;

    private int publicity;

    @JSONField(name = "age_limit")
    private String ageLimit;

    @JSONField(name = "created_time")
    private String createdTime;

    @JSONField(name = "reuploaded_time")
    private String reuploadedTime;

    private User user;

    @JSONField(name = "is_manga")
    private boolean manga;

    @JSONField(name = "is_liked")
    private boolean liked;

    @JSONField(name = "favorite_id")
    private String favoriteId;

    @JSONField(name = "page_count")
    private int pageCount;

    @JSONField(name = "book_style")
    private String bookStyle;

    private String type;

    private Metadata metadata;

    @JSONField(name = "content_type")
    private String contentType;

    @JSONField(name = "sanity_level")
    private String sanityLevel;

    //detail



    //    get and set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public ImageUrls getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ImageUrls imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public int getPublicity() {
        return publicity;
    }

    public void setPublicity(int publicity) {
        this.publicity = publicity;
    }

    public String getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(String ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getReuploadedTime() {
        return reuploadedTime;
    }

    public void setReuploadedTime(String reuploadedTime) {
        this.reuploadedTime = reuploadedTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isManga() {
        return manga;
    }

    public void setManga(boolean manga) {
        this.manga = manga;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getBookStyle() {
        return bookStyle;
    }

    public void setBookStyle(String bookStyle) {
        this.bookStyle = bookStyle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSanityLevel() {
        return sanityLevel;
    }

    public void setSanityLevel(String sanityLevel) {
        this.sanityLevel = sanityLevel;
    }

//    to string

    @Override
    public String toString() {
        return "Work{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", tags=" + tags +
                ", tools='" + tools + '\'' +
                ", imageUrls=" + imageUrls +
                ", width=" + width +
                ", height=" + height +
                ", stats=" + stats +
                ", publicity=" + publicity +
                ", ageLimit='" + ageLimit + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", reuploadedTime='" + reuploadedTime + '\'' +
                ", user=" + user +
                ", manga=" + manga +
                ", liked=" + liked +
                ", favoriteId='" + favoriteId + '\'' +
                ", pageCount=" + pageCount +
                ", bookStyle='" + bookStyle + '\'' +
                ", type='" + type + '\'' +
                ", metadata='" + metadata + '\'' +
                ", contentType='" + contentType + '\'' +
                ", sanityLevel='" + sanityLevel + '\'' +
                '}';
    }
}
