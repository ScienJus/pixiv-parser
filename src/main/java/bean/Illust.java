package bean;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieEnlong
 * @date 2015/8/10.
 */
public class Illust {

    private String id;
    private String authorId;
    private String authorName;
    private boolean manga;
    private List<IllustImage> images;

    public Illust() {
    }

    public Illust(JSONObject json) {
        JSONObject root = (JSONObject) ((JSONArray)json.get("response")).get(0);
        this.manga = Boolean.parseBoolean(root.getAsString("is_manga"));
        this.id = root.getAsString("id");
        JSONObject author = (JSONObject) root.get("user");
        this.authorId = author.getAsString("id");
        this.authorName = author.getAsString("name");
        images = new ArrayList<>();
        if (manga) {
            JSONArray pages = (JSONArray) ((JSONObject) root.get("metadata")).get("pages");
            for (int i = 0; i < pages.size(); i++) {
                JSONObject urls = (JSONObject) ((JSONObject)pages.get(i)).get("image_urls");
                IllustImage image = new IllustImage();
                image.setIllustId(id);
                image.setLargeUrl(urls.getAsString("large"));
                image.setMediumUrl(urls.getAsString("medium"));
                image.setSmallUrl(urls.getAsString("small"));
                image.setSerial(i + 1);
                images.add(image);
            }
        } else {
            JSONObject urls = (JSONObject) root.get("image_urls");
            IllustImage image = new IllustImage();
            image.setIllustId(id);
            image.setLargeUrl(urls.getAsString("large"));
            image.setMediumUrl(urls.getAsString("medium"));
            image.setSmallUrl(urls.getAsString("small"));
            image.setSerial(1);
            images.add(image);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isManga() {
        return manga;
    }

    public void setManga(boolean manga) {
        this.manga = manga;
    }

    public List<IllustImage> getImages() {
        return images;
    }

    public void setImages(List<IllustImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Illust{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", manga=" + manga +
                ", images=" + images +
                '}';
    }
}
